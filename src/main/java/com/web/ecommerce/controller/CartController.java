package com.web.ecommerce.controller;

import com.web.ecommerce.dto.cart.NewCartDTO;
import com.web.ecommerce.dto.cart.UpdateCartDTO;
import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Long> addToCart(@RequestBody NewCartDTO cartItem){
        Long cartItemId = cartService.addToCart(cartItem);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCart(@RequestBody UpdateCartDTO cartItem, @PathVariable Long id){
        if(!Objects.equals(cartItem.getCartItemId(), id)){
            throw new InvalidContentException("Cart ID mismatch");
        }
        cartService.updateCart(cartItem);
        return new ResponseEntity<>("Successfully updated",HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CartItemDTO>> getCartItems(){
        List<CartItemDTO> items = cartService.getCartItems();
        return new ResponseEntity<>(items,HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(){
        cartService.checkout();
        return ResponseEntity.ok("Done checkout");
    }

}
