package com.web.ecommerce.dto.cart;

import lombok.Data;

@Data
public class UpdateCartDTO {
    private Long cartItemId;
    private int quantity;
}
