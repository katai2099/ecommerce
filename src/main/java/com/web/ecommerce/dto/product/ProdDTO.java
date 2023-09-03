package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProdDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private boolean publish;
    private LocalDateTime createdAt;
    private Common.Category category;
    private List<Common.ProductSize> productSizes;
    private List<Common.ProductImage> images;

    public static ProdDTO toProdDTO(Product product){
        ProdDTO dto = new ProdDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setPublish(product.isPublish());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCategory(Common.toCategory(product.getCategory()));
        dto.setProductSizes(product.getProductSizes().stream().map(Common::toProductSize).collect(Collectors.toList()));
        dto.setImages(product.getImages().stream().map(Common::toProductImage).collect(Collectors.toList()));
        return dto;
    }



    public static List<ProdDTO> toProdDTOS(List<Product> products){
        return products.stream()
                .map(ProdDTO::toProdDTO)
                .collect(Collectors.toList());
    }


}
