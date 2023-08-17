package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.ProductImage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private List<String> sizeLabels = new ArrayList<>();
    private List<String> picUrls = new ArrayList<>();
    public static ProductDetailDTO productDetailDTO(Product product){
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory().getName());
        dto.setSizeLabels(product.getProductSizes().stream().map(x->x.getSize().getName()).collect(Collectors.toList()));
        dto.setPicUrls(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        return dto;
    }

}
