package com.web.ecommerce.model;

import com.web.ecommerce.model.user.Address;
import lombok.Getter;

@Getter
public class PlaceOrderRequest {
    private Address deliveryAddress;
    private Address billingAddress;
    private String stripePaymentIntentId;
}
