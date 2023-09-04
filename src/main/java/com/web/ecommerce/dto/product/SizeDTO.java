package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SizeDTO {
    private final Long id;
    private final String name;
    public static SizeDTO toSizeDTO(Size size) {
        return SizeDTO.builder()
                .id(size.getId())
                .name(size.getName())
                .build();
    }
}
