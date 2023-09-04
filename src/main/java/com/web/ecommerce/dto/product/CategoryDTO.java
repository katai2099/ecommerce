package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {
    private final Long id;
    private final String name;
    public static CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
