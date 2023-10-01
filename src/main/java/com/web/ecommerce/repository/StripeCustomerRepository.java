package com.web.ecommerce.repository;

import com.web.ecommerce.model.user.StripeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StripeCustomerRepository extends JpaRepository<StripeCustomer,Long> {
    Optional<StripeCustomer> findByUserId(Long userId);
}
