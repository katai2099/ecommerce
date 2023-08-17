package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize,Long> {
    Optional<ProductSize> findProductSizeByProductIdAndSizeName(Long productId, String sizeName);
}
