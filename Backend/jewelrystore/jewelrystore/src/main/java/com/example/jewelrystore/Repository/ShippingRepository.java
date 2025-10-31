package com.example.jewelrystore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {

}
