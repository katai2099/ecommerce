package com.web.ecommerce.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateProductRequest {
    private Long id;
    private String name;
    private String description;
    private double price;
    private CategoryDTO category;
    private String gender;
    private List<ProductSizeDTO> productSizes = new ArrayList<>();
    private List<ProductImageDTO> images = new ArrayList<>();
}
