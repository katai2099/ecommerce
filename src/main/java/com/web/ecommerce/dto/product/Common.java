package com.web.ecommerce.dto.product;

import lombok.Builder;
import lombok.Getter;


public class Common {

    @Getter
    @Builder
    public static class Category{
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class ProductImage {
        private Long id;
        private String imageUrl;
    }

    @Getter
    @Builder
    public static class ProductSize{
        private Long id;
        private int stockCount;
        private Size size;
    }

    @Getter
    @Builder
    public static class Size{
        private Long id;
        private String name;
    }

    public static Category toCategory(com.web.ecommerce.model.product.Category category){
        return Category.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static ProductImage toProductImage(com.web.ecommerce.model.product.ProductImage productImage){
        return ProductImage.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }

    public static ProductSize toProductSize(com.web.ecommerce.model.product.ProductSize productSize){
        return ProductSize.builder()
                .id(productSize.getId())
                .stockCount(productSize.getStockCount())
                .size(toSize(productSize.getSize()))
                .build();
    }

    public static Size toSize(com.web.ecommerce.model.product.Size size){
        return Size.builder()
                .id(size.getId())
                .name(size.getName())
                .build();
    }
}
