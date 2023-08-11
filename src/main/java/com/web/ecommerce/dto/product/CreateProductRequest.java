package com.web.ecommerce.dto.product;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private double price;
    private Long categoryId;
    private Long productSizeId;
    private int stockCount;
}
