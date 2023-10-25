package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findAllByIsTopIsTrueAndPublishIsTrue();
    @Query("SELECT COUNT(c) FROM Category c WHERE c.isTop = true")
    Long countAllByIsTopIsTrue();
}
