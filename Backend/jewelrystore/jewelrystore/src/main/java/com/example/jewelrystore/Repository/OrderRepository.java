package com.example.jewelrystore.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUsername(String username);

    // Tìm kiếm đơn hàng theo username và trạng thái
    List<Order> findByUserUsernameAndStatus(String username, String status);

}
