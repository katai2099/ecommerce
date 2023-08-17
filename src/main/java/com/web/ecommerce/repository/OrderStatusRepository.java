package com.web.ecommerce.repository;


import com.web.ecommerce.model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus,Long> {
    Optional<OrderStatus> findByName(String name);
}
