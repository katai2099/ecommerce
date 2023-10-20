package com.web.ecommerce.repository;

import com.web.ecommerce.model.product.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findCartByUserId(Long id);
    @Query("select c from Cart c where c.deviceId=?1")
    Optional<Cart> findCartByDeviceId(String deviceId);


}
