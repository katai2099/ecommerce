package com.web.ecommerce.mapper;

import com.web.ecommerce.dto.cart.CartItemDTO;
import com.web.ecommerce.dto.product.ProductDTO;
import com.web.ecommerce.model.product.CartItem;
import com.web.ecommerce.model.product.ProductImage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartItemMapper {
    public static CartItemDTO toCartItemDTO(CartItem cartItem){
        CartItemDTO dto = new CartItemDTO();
        ProductDTO productDTO = new ProductDTO();
        dto.setProduct(productDTO);
        if(cartItem!=null && cartItem.getProductSize()!=null){
            dto.getProduct().setId(cartItem.getProductSize().getProduct().getId());
            dto.getProduct().setName(cartItem.getProductSize().getProduct().getName());
            dto.getProduct().getSizeLabels().add(cartItem.getProductSize().getSize().getName());
            dto.getProduct().setPrice(cartItem.getProductSize().getProduct().getPrice());
            dto.getProduct().setPicUrls(cartItem.getProductSize().getProduct().getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
            dto.setQuantity(cartItem.getQuantity());
        }
        return dto;
    }
    public static List<CartItemDTO> toCartItemDTOs(Set<CartItem> cartItems){
        return cartItems.stream()
                .map(CartItemMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }
}
