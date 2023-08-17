package com.web.ecommerce.dto.cart;

import com.web.ecommerce.dto.product.ProductDTO;
import lombok.Data;

@Data
public class CartItemDTO {
    private ProductDTO product;
    private int quantity;

}
