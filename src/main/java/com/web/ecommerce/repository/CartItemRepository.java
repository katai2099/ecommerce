package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findCartItemByCartIdAndProductSizeId(Long cartId,Long ProductSizeId);
}
