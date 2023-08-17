package com.web.ecommerce.dto.product;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateProductRequest {
    private Long id;
    private String name;
    private String description;
    private double price;
    private Category category;
    private String gender;
    private List<ProductSize> sizes = new ArrayList<>();

    @Getter
    public static class Category{
        private Long id;
    }

    @Getter
    public static class ProductSize {
        private Long id;
        private int stockCount;
        private Size size;
    }

    @Getter
    public static class Size{
        private String name;
    }

}
