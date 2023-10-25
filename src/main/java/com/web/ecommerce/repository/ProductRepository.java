package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>,
        JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.publish = true")
    List<Product> findAllByIsFeaturedIsTrueAndPublishIsTrue();
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isFeatured = true")
    Long countAllByIsFeaturedIsTrue();
}
