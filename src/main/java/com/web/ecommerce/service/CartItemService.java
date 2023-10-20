package com.web.ecommerce.service;

import com.web.ecommerce.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

}
