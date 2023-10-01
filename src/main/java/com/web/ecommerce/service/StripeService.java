package com.web.ecommerce.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.ConfirmPaymentRequest;
import com.web.ecommerce.model.NextActionResponse;
import com.web.ecommerce.model.product.Cart;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.model.user.StripeCustomer;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.CartRepository;
import com.web.ecommerce.repository.StripeCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

@Service
public class StripeService {
    @Value("${stripe.apiKey}")
    private String stripeApiKey;
    private final CartRepository cartRepository;
    private final StripeCustomerRepository stripeCustomerRepository;


    @Autowired
    public StripeService(CartRepository cartRepository, StripeCustomerRepository stripeCustomerRepository) {
        this.cartRepository = cartRepository;
        this.stripeCustomerRepository = stripeCustomerRepository;
    }

    @Transactional
    public NextActionResponse confirmPayment(ConfirmPaymentRequest request) {
        Long userId = getUserIdFromSecurityContext();
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new InvalidContentException("No items in the cart"));
        Set<CartItem> cartItems = cart.getCartItems();
        StripeCustomer stripeCustomer = stripeCustomerRepository.findByUserId(userId).orElseThrow(()->new InvalidContentException("stripe customer does not exist"));
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
                .setCustomer(stripeCustomer.getCustomerId())
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods
                        .builder()
                        .setEnabled(true)
                        .build())
                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.ON_SESSION)
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

    @Transactional
    public void createNewStripeCustomer(User user){
        Optional<StripeCustomer> optionalStripeCustomer = stripeCustomerRepository.findByUserId(user.getId());
        if(optionalStripeCustomer.isPresent()){
            return;
        }
        CustomerCreateParams customerParams = CustomerCreateParams
                .builder()
                .setEmail(user.getEmail())
                .setName(user.getFirstname() + " " + user.getLastname())
                .build();
        Customer customer;
        Stripe.apiKey = stripeApiKey;
        try {
            customer = Customer.create(customerParams);
        } catch (StripeException e) {
            //TODO handle stripe exception
            throw new InvalidContentException(e.getMessage());
        }
        StripeCustomer stripeCustomer = StripeCustomer.builder()
                .customerId(customer.getId())
                .user(user)
                .build();
        stripeCustomerRepository.save(stripeCustomer);
    }
}
