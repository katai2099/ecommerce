package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.ProductImage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDTO {
    private final Long id;
    private final String imageUrl;

    public static ProductImageDTO toProductImageDTO(ProductImage productImage){
        return ProductImageDTO.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }

}
