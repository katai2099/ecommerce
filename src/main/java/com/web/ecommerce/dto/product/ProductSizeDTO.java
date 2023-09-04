package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.ProductSize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSizeDTO {
    private final Long id;
    private final int stockCount;
    private final SizeDTO size;

    public static ProductSizeDTO toProductSizeDTO(ProductSize productSize){
        return ProductSizeDTO.builder()
                .id(productSize.getId())
                .stockCount(productSize.getStockCount())
                .size(SizeDTO.toSizeDTO(productSize.getSize()))
                .build();
    }

}
