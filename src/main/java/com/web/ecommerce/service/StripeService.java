package com.web.ecommerce.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.ConfirmPaymentRequest;
import com.web.ecommerce.model.NextActionResponse;
import com.web.ecommerce.model.product.Cart;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

@Service
public class StripeService {
    @Value("${stripe.apiKey}")
    private String stripeApiKey;
    private final CartRepository cartRepository;


    @Autowired
    public StripeService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public NextActionResponse confirmPayment(ConfirmPaymentRequest request) {
        Long userId = getUserIdFromSecurityContext();
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new InvalidContentException("No items in the cart"));
        Set<CartItem> cartItems = cart.getCartItems();
        double total = 0;
        for (CartItem item : cartItems) {
            int quantity = item.getQuantity();
            double price = item.getProductSize().getProduct().getPrice();
            //calculate total
            total += quantity * price;
        }
        Stripe.apiKey = stripeApiKey;
        long totalAmount = (long) total;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount * 100)
                .setCurrency("eur")
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods
                        .builder()
                        .setEnabled(true)
                        .build())
                .setPaymentMethod(request.getPaymentMethodId())
                .setUseStripeSdk(true)
                .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return NextActionResponse.builder()
                    .status(paymentIntent.getStatus())
                    .clientSecret(paymentIntent.getClientSecret())
                    .build();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
