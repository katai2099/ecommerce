package com.web.ecommerce.controller;

import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.dto.cart.NewCartDTO;
import com.web.ecommerce.dto.cart.UpdateCartDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.*;
import com.web.ecommerce.service.CartService;
import com.web.ecommerce.service.StripeService;
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
    private final StripeService stripeService;

    @Autowired
    public CartController(CartService cartService, StripeService stripeService) {
        this.cartService = cartService;
        this.stripeService = stripeService;
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Long> addToCart(@RequestBody NewCartDTO cartItem,
                                          @RequestParam String deviceId) {
        Long cartItemId = cartService.addToCart(cartItem, deviceId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCart(@RequestBody UpdateCartDTO cartItem,
                                             @PathVariable Long id) {
        if (!Objects.equals(cartItem.getCartItemId(), id)) {
            throw new InvalidContentException("Cart ID mismatch");
        }
        cartService.updateCart(cartItem);
        return new ResponseEntity<>("Successfully updated", HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CartItemDTO>> getCartItems(@RequestParam String deviceId) {
        List<CartItemDTO> items = cartService.getCartItems(deviceId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping("/stock-check")
    public ResponseEntity<List<StockCountCheckResponse>> performStockCheck() {
        List<StockCountCheckResponse> response = cartService.performStockCountCheck();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createPaymentIntent() {
        CheckoutResponse response = cartService.checkout();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-intent")
    public ResponseEntity<NextActionResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        NextActionResponse response = stripeService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(@RequestBody PlaceOrderRequest request) {
        String orderId = cartService.placeOrder(request);
        return ResponseEntity.ok(orderId);
    }


}
