package com.web.ecommerce.repository;

import com.web.ecommerce.model.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    @Query("select t from PasswordResetToken t where t.token = ?1")

    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUserId(Long id);

}
