package com.web.ecommerce.service;

import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.dto.cart.NewCartDTO;
import com.web.ecommerce.dto.cart.UpdateCartDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.mapper.CartItemMapper;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.order.OrderDetail;
import com.web.ecommerce.model.order.OrderStatus;
import com.web.ecommerce.model.product.Cart;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.model.product.ProductSize;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.web.ecommerce.enumeration.OrderStatusEnum ;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSizeRepository productSizeRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductSizeRepository productSizeRepository,
                       UserRepository userRepository,
                       OrderStatusRepository orderStatusRepository,
                       OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productSizeRepository = productSizeRepository;
        this.userRepository = userRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderRepository = orderRepository;
    }

    public List<CartItemDTO> getCartItems() {
        Optional<Cart> dbCart = cartRepository.findCartByUserId(1L);
        if (dbCart.isEmpty()) {
            return new LinkedList<>();
        } else {
            Cart cart = dbCart.get();
            return CartItemMapper.toCartItemDTOs(cart.getCartItems());
        }
    }

    @Transactional
    public void addToCart(NewCartDTO newCartItem) {
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(1L);
        ProductSize productSize = productSizeRepository.
                findProductSizeByProductIdAndSizeName(newCartItem.getProductId(), newCartItem.getSize())
                .orElseThrow(() -> new InvalidContentException("Product with provided size does not exist"));
        Cart currentCart;
        if (optionalCart.isPresent()) {
            currentCart = optionalCart.get();
        } else {
            currentCart = new Cart();
            User user = new User();
            user.setId(1L);
            currentCart.setUser(user);
        }
        Optional<CartItem> existingCartItem = currentCart
                .getCartItems()
                .stream()
                .filter(item -> item.getProductSize().getId().equals(productSize.getId()))
                .findFirst();
        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            if (item.getQuantity() >= productSize.getStockCount()) {
                throw new InvalidContentException("Product out of stock");
            }
            item.setQuantity(item.getQuantity() + 1);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(1);
            cartItem.setProductSize(productSize);
            cartItem.setCart(currentCart);
            currentCart.getCartItems().add(cartItem);
        }
        cartRepository.save(currentCart);
    }

    @Transactional
    public void updateCart(UpdateCartDTO item) {
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
                    throw new InvalidContentException("Product out of stock");
                }
            }
            dbCartItem.setQuantity(item.getQuantity());
            cartItemRepository.save(dbCartItem);
        }
    }

    @Transactional
    public void checkout() {
        User user = userRepository.findById(1L).orElseThrow(()->new InvalidContentException("User not found"));
        Cart cart = cartRepository.findCartByUserId(1L)
                .orElseThrow(() -> new InvalidContentException("No items in the cart"));
        Set<CartItem> cartItems = cart.getCartItems();
        Order order = new Order();
        double total = 0;
        for(CartItem item: cartItems){
            int quantity = item.getQuantity();
            double price = item.getProductSize().getProduct().getPrice();
            int productStockCount = item.getProductSize().getStockCount();
            if(productStockCount == 0){
                throw new InvalidContentException("Product with product Size id " + item.getProductSize().getId() + " out of stock");
            }
            if(productStockCount < quantity){
                throw new InvalidContentException("Product with product Size id " + item.getProductSize().getId() + " has only " + productStockCount + " left");
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(quantity);
            orderDetail.setProductSize(item.getProductSize());
            orderDetail.setPriceAtPurchase(price);
            //calculate total
            total += quantity * price;
            //decrease stock count
            item.getProductSize().setStockCount(productStockCount- quantity);
            order.addOrderDetail(orderDetail);
        }
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(total);
        order.setUser(user);
        OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.PURCHASED.toString())
                .orElseThrow(()->new RuntimeException("Internal Server Error"));
        orderStatus.addOrder(order);
        order.setOrderStatus(orderStatus);
        user.addOrder(order);
        cartRepository.delete(user.getCart());
        orderRepository.save(order);
    }
}
