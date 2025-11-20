package com.example.jewelrystore.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Entity.Checkout;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Integer> {
    Optional<Checkout> findByOrderId(Integer id);
}
