package com.web.ecommerce.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPaymentRequest {
    private String paymentMethodId;
}
