package com.web.ecommerce.service;

import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.dto.cart.NewCartDTO;
import com.web.ecommerce.dto.cart.UpdateCartDTO;
import com.web.ecommerce.mapper.CartItemMapper;
import com.web.ecommerce.model.product.Cart;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.model.product.ProductSize;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.CartItemRepository;
import com.web.ecommerce.repository.CartRepository;
import com.web.ecommerce.repository.ProductSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSizeRepository productSizeRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductSizeRepository productSizeRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productSizeRepository = productSizeRepository;
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
        Optional<ProductSize> optionalProductSize = productSizeRepository.
                findProductSizeByProductIdAndSizeName(newCartItem.getProductId(), newCartItem.getSize());
        if (optionalProductSize.isEmpty()) {
            throw new RuntimeException("No size associated with the product");
        }
        ProductSize productSize = optionalProductSize.get();
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
            if(item.getQuantity()>=productSize.getStockCount()){
                throw new RuntimeException("Product out of stock");
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
        Optional<CartItem> dbCartItem = cartItemRepository.findById(item.getCartItemId());
        if (dbCartItem.isEmpty()) {
            throw new RuntimeException("No cart item");
        }
        if (item.getQuantity() == 0) {
            dbCartItem.get().removeFromCart();
            cartItemRepository.deleteById(item.getCartItemId());
        } else {
            Optional<ProductSize> optionalProductSize = productSizeRepository.
                    findProductSizeByCartItemsId(item.getCartItemId());
            if(optionalProductSize.isPresent()){
                ProductSize productSize = optionalProductSize.get();
                if(item.getQuantity() >productSize.getStockCount()){
                    throw new RuntimeException("Product out of stock");
                }
            }
            CartItem cartItem = dbCartItem.get();
            cartItem.setQuantity(item.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }
}
