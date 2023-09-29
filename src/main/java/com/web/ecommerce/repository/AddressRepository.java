package com.web.ecommerce.repository;

import com.web.ecommerce.model.user.Address;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
//    @Query("select a from address a left join fetch users u on u.id = a.id where u.id = ?1")
    List<Address> findAllByUserId(Long userId, Sort sort);
    Optional<Address> findAddressByUserIdAndIsDefaultIsTrue(Long userId);
//    @Query("select a from address a where a.user.id = ?1 order by a.id asc limit 1")
//    Optional<Address> findLowestIdAddressForUser(Long userId);
     Optional<Address> findFirstByUserIdOrderByIdAsc(Long userid);
}
