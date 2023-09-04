package com.web.ecommerce.dto.cart;

import com.web.ecommerce.dto.product.ProductDTO;
import com.web.ecommerce.dto.product.ProductImageDTO;
import com.web.ecommerce.dto.product.ProductSizeDTO;
import com.web.ecommerce.model.product.CartItem;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CartItemDTO {
    private ProductDTO product;
    private int quantity;

    public static CartItemDTO toCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        ProductDTO productDTO = new ProductDTO();
        dto.setProduct(productDTO);
        if (cartItem != null && cartItem.getProductSize() != null) {
            dto.getProduct().setId(cartItem.getProductSize().getProduct().getId());
            dto.getProduct().setName(cartItem.getProductSize().getProduct().getName());
            dto.getProduct().getProductSizes().add(ProductSizeDTO.toProductSizeDTO(cartItem.getProductSize()));
            dto.getProduct().setPrice(cartItem.getProductSize().getProduct().getPrice());
            dto.getProduct().setImages(cartItem.getProductSize().getProduct().getImages().stream().map(ProductImageDTO::toProductImageDTO).collect(Collectors.toList()));
            dto.setQuantity(cartItem.getQuantity());
        }
        return dto;
    }

    public static List<CartItemDTO> toCartItemDTOs(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItemDTO::toCartItemDTO)
                .collect(Collectors.toList());
    }

}
