package com.web.ecommerce.repository;

import com.web.ecommerce.model.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByUserId(Long userID, Pageable pageable);
    @Query("select o from Order o where o.id= ?1")
    Optional<Order> findByOrderUuid(UUID uuid);
}
