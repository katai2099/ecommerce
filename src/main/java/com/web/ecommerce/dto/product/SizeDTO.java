package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class SizeDTO {
    private final Long id;
    private final String name;
    private final Boolean publish;
    private final LocalDateTime lastModified;

    public static SizeDTO toSizeDTO(Size size) {
        return SizeDTO.builder()
                .id(size.getId())
                .name(size.getName())
                .publish(size.getPublish())
                .lastModified(size.getLastModified())
                .build();
    }

    public static List<SizeDTO> toSizeDTOS(List<Size> sizes) {
        return sizes.stream()
                .map(SizeDTO::toSizeDTO)
                .collect(Collectors.toList());
    }
}
