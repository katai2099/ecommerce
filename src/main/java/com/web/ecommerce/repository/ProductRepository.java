package com.web.ecommerce.repository;

import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.model.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>,
        JpaSpecificationExecutor<Product> {
    List<Product> findAllByGenderAndCategory(Gender gender, Category category, Pageable pageable);
    List<Product> findAllByGender(Gender gender, Pageable pageable);
    List<Product> findAllByNameContainingIgnoreCaseAndGender(String name,Gender gender,Pageable pageable);
    List<Product> findAllByIsFeaturedIsTrue();
}
