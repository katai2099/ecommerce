package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.ProductImage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private List<String> sizeLabels = new ArrayList<>();
    private List<String> picUrls = new ArrayList<>();

    public static ProductDTO toProductDTO(Product product){
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setSizeLabels(product.getProductSizes().stream().map(x->x.getSize().getName()).collect(Collectors.toList()));
        dto.setPicUrls(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        return dto;
    }

    public static List<ProductDTO> toProductDTOS(List<Product> products){
        return products.stream()
                .map(ProductDTO::toProductDTO)
                .collect(Collectors.toList());
    }


}
