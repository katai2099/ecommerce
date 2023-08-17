package com.web.ecommerce.repository;

import com.web.ecommerce.model.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByUserId(Long userID, Pageable pageable);
}
