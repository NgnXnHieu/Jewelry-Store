package com.example.jewelrystore.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.jewelrystore.Entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAddressByUserId(Integer userId);

    List<Address> findAllByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.is_defaut = false WHERE a.user.id = :user_id")
    void setAllDefaultFalseForUser(@Param("user_id") Integer userId);

    // Lấy ra địa chỉ mặc định
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.is_defaut = true")
    Optional<Address> findDefaultAddressByUserId(@Param("userId") Integer userId);

}
