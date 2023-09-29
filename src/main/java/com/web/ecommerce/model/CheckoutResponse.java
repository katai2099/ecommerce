package com.web.ecommerce.model;

import com.web.ecommerce.dto.cart.CartItemDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CheckoutResponse {
    List<CartItemDTO> carts;
    double total;
}
