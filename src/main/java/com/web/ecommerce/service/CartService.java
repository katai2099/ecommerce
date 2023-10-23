package com.web.ecommerce.service;

import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.dto.cart.NewCartDTO;
import com.web.ecommerce.dto.cart.UpdateCartDTO;
import com.web.ecommerce.enumeration.OrderStatusEnum;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.CheckoutResponse;
import com.web.ecommerce.model.PlaceOrderRequest;
import com.web.ecommerce.model.StockCountCheckResponse;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.order.OrderDetail;
import com.web.ecommerce.model.order.OrderHistory;
import com.web.ecommerce.model.order.OrderStatus;
import com.web.ecommerce.model.product.Cart;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.model.product.ProductSize;
import com.web.ecommerce.model.user.Address;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSizeRepository productSizeRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final StripeService stripeService;


    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductSizeRepository productSizeRepository,
                       UserRepository userRepository,
                       OrderStatusRepository orderStatusRepository,
                       OrderRepository orderRepository,
                       AddressRepository addressRepository,
                       StripeService stripeService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productSizeRepository = productSizeRepository;
        this.userRepository = userRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.stripeService = stripeService;
    }

    public List<CartItemDTO> getCartItems(String deviceId) {
        Long userId = getUserIdFromSecurityContext();
        Optional<Cart> dbCart;
        if (userId != -1) {
            dbCart = cartRepository.findCartByUserId(userId);
        } else {
            dbCart = cartRepository.findCartByDeviceId(deviceId);
        }
        if (dbCart.isEmpty()) {
            return new LinkedList<>();
        } else {
            Cart cart = dbCart.get();
            return CartItemDTO.toCartItemDTOs(cart.getCartItems());
        }
    }

    @Transactional
    public Long addToCart(NewCartDTO newCartItem, String deviceId) {
        Long userId = getUserIdFromSecurityContext();
        ProductSize productSize = productSizeRepository.
                findProductSizeByProductIdAndSizeName(newCartItem.getProductId(), newCartItem.getSize())
                .orElseThrow(() -> new InvalidContentException("Product with provided size does not exist"));
        if (productSize.getStockCount() == 0) {
            throw new InvalidContentException("Product out of stock");
        }
        Cart currentCart;
        if (userId != -1) {
            currentCart = cartRepository.findCartByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        User user = new User();
                        user.setId(userId);
                        newCart.setUser(user);
                        return cartRepository.save(newCart);
                    });
        } else {
            currentCart = cartRepository.findCartByDeviceId(deviceId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setDeviceId(deviceId);
                        return cartRepository.save(newCart);
                    });
        }
        CartItem existingCartItem = currentCart
                .getCartItems()
                .stream()
                .filter(item -> item.getProductSize().getId().equals(productSize.getId()))
                .findFirst()
                .orElse(null);
        if (existingCartItem != null) {
            if (existingCartItem.getQuantity() >= productSize.getStockCount()) {
                throw new InvalidContentException("Requested quantity is not available");
            }
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            cartItemRepository.save(existingCartItem);
            return existingCartItem.getId();
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(1);
            cartItem.setProductSize(productSize);
            cartItem.setCart(currentCart);
            currentCart.addToCart(cartItem);
            CartItem savedCartItem = cartItemRepository.save(cartItem);
            return savedCartItem.getId();
        }
    }

    @Transactional
    public void updateCart(UpdateCartDTO item) {
        //TODO: check if caller is actually the owner of cart item
        CartItem dbCartItem = cartItemRepository.findById(item.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item associate with cart does not exist"));
        if (item.getQuantity() == 0) {
            dbCartItem.removeFromCart();
            cartItemRepository.deleteById(item.getCartItemId());
        } else {
            Optional<ProductSize> optionalProductSize = productSizeRepository.
                    findProductSizeByCartItemsId(item.getCartItemId());
            if (optionalProductSize.isPresent()) {
                ProductSize productSize = optionalProductSize.get();
                if (item.getQuantity() > productSize.getStockCount()) {
                    throw new InvalidContentException("Request quantity is not available");
                }
            }
            dbCartItem.setQuantity(item.getQuantity());
            cartItemRepository.save(dbCartItem);
        }
    }

    public List<StockCountCheckResponse> performStockCountCheck() {
        Long userId = getUserIdFromSecurityContext();
        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(() -> new InvalidContentException("Cart is empty"));
        List<StockCountCheckResponse> responseList = new ArrayList<>();
        Set<CartItem> cartItems = cart.getCartItems();
        for (CartItem item : cartItems) {
            int quantity = item.getQuantity();
            int productStockCount = item.getProductSize().getStockCount();
            if (productStockCount == 0 || productStockCount < quantity) {
                StockCountCheckResponse tmp = StockCountCheckResponse.builder()
                        .cartItemId(item.getId())
                        .stockCount(productStockCount)
                        .build();
                responseList.add(tmp);
            }
        }
        return responseList;
    }

    @Transactional
    public CheckoutResponse checkout() {
        Long userId = getUserIdFromSecurityContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));
        Optional<Cart> dbCart = cartRepository.findCartByUserId(userId);
        if (dbCart.isEmpty()) {
            return CheckoutResponse.builder()
                    .carts(new ArrayList<>())
                    .total(0).build();
        }
        Cart cart = dbCart.get();
        Set<CartItem> cartItems = cart.getCartItems();
        double total = 0;
        for (CartItem item : cartItems) {
            int quantity = item.getQuantity();
            double price = item.getProductSize().getProduct().getPrice();
            int productStockCount = item.getProductSize().getStockCount();
            if (productStockCount == 0) {
                throw new InvalidContentException("Product with product Size id " + item.getProductSize().getId() + " out of stock");
            }
            if (productStockCount < quantity) {
                throw new InvalidContentException("Product with product Size id " + item.getProductSize().getId() + " has only " + productStockCount + " left");
            }
            //calculate total
            total += quantity * price;
        }
        List<CartItemDTO> carts = CartItemDTO.toCartItemDTOs(cart.getCartItems());
        stripeService.createNewStripeCustomer(user);
        return CheckoutResponse.builder()
                .carts(carts)
                .total(total)
                .build();

    }

    @Transactional
    public String placeOrder(PlaceOrderRequest request) {
        Long userId = getUserIdFromSecurityContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidContentException("User not found"));
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new InvalidContentException("No items in the cart"));
        Set<CartItem> cartItems = cart.getCartItems();
        Order order = new Order();

        Address deliveryAddress = request.getDeliveryAddress();
        Address billingAddress = request.getBillingAddress();
        Address dbDeliveryAddress = addressRepository.findById(deliveryAddress.getId())
                .orElseGet(() -> {
                    deliveryAddress.setUser(user);
                    return addressRepository.save(deliveryAddress);
                });

        Address dbBillingAddress = null;
        if (!isAddressTheSame(deliveryAddress, billingAddress)) {
            dbBillingAddress =
                    addressRepository.findById(billingAddress.getId())
                            .orElseGet(() -> {
                                billingAddress.setUser(user);
                                return addressRepository.save(billingAddress);
                            });
        }
        double total = 0;
        for (CartItem item : cartItems) {
            int quantity = item.getQuantity();
            double price = item.getProductSize().getProduct().getPrice();
            int productStockCount = item.getProductSize().getStockCount();
            total += quantity * price;
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(quantity);
            orderDetail.setProductSize(item.getProductSize());
            orderDetail.setPriceAtPurchase(price);
            //decrease stock count
            item.getProductSize().setStockCount(productStockCount - quantity);
            order.addOrderDetail(orderDetail);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        order.setDeliveryAddress(dbDeliveryAddress);
        order.setBillingAddress(isAddressTheSame(deliveryAddress, billingAddress) ? dbDeliveryAddress : dbBillingAddress);
        order.setStripePaymentIntentId(request.getStripePaymentIntentId());
        order.setOrderDate(currentTime);
        order.setTotalPrice(total);
        order.setUser(user);
        OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.ORDER_PLACED.toString())
                .orElseThrow(() -> new RuntimeException("Internal Server Error"));
        orderStatus.addOrder(order);
        order.setOrderStatus(orderStatus);
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setStatus(orderStatus);
        orderHistory.setActionTime(currentTime);
        order.addOrderHistory(orderHistory);
        user.addOrder(order);
        Order savedOrder = orderRepository.save(order);
        cartRepository.delete(user.getCart());
        return savedOrder.getId().toString();
    }

    private boolean isAddressTheSame(Address firstAddress, Address secondAddress) {
        if (!firstAddress.getFirstname().trim().equals(secondAddress.getFirstname().trim())) {
            return false;
        }
        if (!firstAddress.getLastname().trim().equals(secondAddress.getLastname().trim())) {
            return false;
        }
        if (!firstAddress.getPhoneNumber().trim().equals(secondAddress.getPhoneNumber().trim())) {
            return false;
        }
        if (!firstAddress.getStreet().trim().equals(secondAddress.getStreet().trim())) {
            return false;
        }
        if (!firstAddress.getHouseNumber().trim().equals(secondAddress.getHouseNumber().trim())) {
            return false;
        }
        if (!firstAddress.getZipCode().trim().equals(secondAddress.getZipCode().trim())) {
            return false;
        }
        if (!firstAddress.getCity().trim().equals(secondAddress.getCity().trim())) {
            return false;
        }
        if (!firstAddress.getCountry().trim().equals(secondAddress.getCountry().trim())) {
            return false;
        }
        return true;
    }

    public void mergeCart(Long userId, String deviceId) {
        Optional<Cart> optionalCart = cartRepository.findCartByDeviceId(deviceId);
        if (optionalCart.isEmpty()) {
            return;
        }
        Cart deviceCart = optionalCart.get();
        if (deviceCart.getCartItems().isEmpty()) {
            return;
        }
        Cart userCart = cartRepository.findCartByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    User user = new User();
                    user.setId(userId);
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        userCart.clearCart();
        cartRepository.save(userCart);
        deviceCart.getCartItems().forEach(userCart::addToCart);
        cartRepository.save(userCart);
    }
}
