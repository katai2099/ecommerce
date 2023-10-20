package com.web.ecommerce.repository;

import com.web.ecommerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional <User> findByEmail(String email);
    @Query("select u from users u join PasswordResetToken p on u.id=p.user.id where p.token=?1")
    Optional <User> findUserByPasswordResetToken(String token);

}
