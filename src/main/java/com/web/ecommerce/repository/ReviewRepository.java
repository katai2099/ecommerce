package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    Optional<Review> findByProductIdAndUserId(Long productId,Long userId);
    Page<Review> findByProductIdAndUserIdNot(Long productId, Long userId, Pageable pageable);

}
