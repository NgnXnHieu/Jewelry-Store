package com.example.jewelrystore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
