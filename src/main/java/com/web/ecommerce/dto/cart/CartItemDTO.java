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
    private long id;
    private int quantity;
    private ProductDTO product;

    public static CartItemDTO toCartItemDTO(CartItem cartItem) {
        CartItemDTO cartDto = new CartItemDTO();
        ProductDTO productDTO = new ProductDTO();
        cartDto.setProduct(productDTO);
        if (cartItem != null && cartItem.getProductSize() != null) {
            cartDto.getProduct().setId(cartItem.getProductSize().getProduct().getId());
            cartDto.getProduct().setName(cartItem.getProductSize().getProduct().getName());
            ProductSizeDTO test = ProductSizeDTO.toProductSizeDTO(cartItem.getProductSize());
            cartDto.getProduct().getProductSizes().add(test);
            cartDto.getProduct().setPrice(cartItem.getProductSize().getProduct().getPrice());
            cartDto.getProduct().setImages(cartItem.getProductSize().getProduct().getImages().stream().map(ProductImageDTO::toProductImageDTO).collect(Collectors.toList()));
            cartDto.setQuantity(cartItem.getQuantity());
            cartDto.setId(cartItem.getId());
        }
        return cartDto;
    }

    public static List<CartItemDTO> toCartItemDTOs(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItemDTO::toCartItemDTO)
                .collect(Collectors.toList());
    }

}
