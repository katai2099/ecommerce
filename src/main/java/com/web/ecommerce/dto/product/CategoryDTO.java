package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CategoryDTO {
    private final Long id;
    private final String name;
    private final boolean isTop;
    private final boolean publish;
    private final String categoryImage;

    public static List<CategoryDTO> toCategoryDTOS(List<Category> categories){
        return categories.stream()
                .map(CategoryDTO::toCategoryDTO)
                .collect(Collectors.toList());
    }
    public static CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .isTop(category.isTop())
                .publish(category.isPublish())
                .categoryImage(category.getCategoryImage())
                .build();
    }
}
