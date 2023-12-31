package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private boolean publish;
    private boolean isFeatured;
    private String gender;
    private LocalDateTime createdAt;
    private CategoryDTO category;
    private List<ProductSizeDTO> productSizes = new ArrayList<>();
    private List<ProductImageDTO> images = new ArrayList<>();
    private double rating;
    private int totalReview;

    public static ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setGender(product.getGender().toString());
        dto.setPrice(product.getPrice());
        dto.setPublish(product.isPublish());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCategory(CategoryDTO.toCategoryDTO(product.getCategory()));
        dto.setProductSizes(product.getProductSizes().stream().map(ProductSizeDTO::toProductSizeDTO).collect(Collectors.toList()));
        dto.setImages(product.getImages().stream().map(ProductImageDTO::toProductImageDTO).collect(Collectors.toList()));
        dto.setRating(product.getRating());
        dto.setFeatured(product.isFeatured());
        dto.setTotalReview(product.getReviews()!=null ? product.getReviews().size() : 0);
        return dto;
    }


    public static List<ProductDTO> toProductDTOs(List<Product> products) {
        return products.stream()
                .map(ProductDTO::toProductDTO)
                .collect(Collectors.toList());
    }


}
