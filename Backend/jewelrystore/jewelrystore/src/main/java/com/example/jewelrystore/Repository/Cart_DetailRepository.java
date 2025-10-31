package com.example.jewelrystore.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Cart_Detail;

@Repository
public interface Cart_DetailRepository extends JpaRepository<Cart_Detail, Integer> {
    Page<Cart_Detail> findByCartId(Integer cartId, Pageable pageable);
}
