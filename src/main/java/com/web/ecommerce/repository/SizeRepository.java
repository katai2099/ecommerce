package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.Size;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size,Long> {
    Optional<Size> findByName(String name);
    @Query("select s from sizes s where s.publish = true")
    List<Size> findAllByPublishIsTrue(Sort sort);
}
