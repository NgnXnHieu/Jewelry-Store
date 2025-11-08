package com.example.jewelrystore.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.DTO.OrderSumaryDTO;
import com.example.jewelrystore.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUsername(String username);

    // Tìm kiếm đơn hàng theo username và trạng thái
    List<Order> findByUserUsernameAndStatus(String username, String status);

    @Query("SELECT new com.example.jewelrystore.DTO.OrderSumaryDTO(o.id, o.orderDate, o.totalAmount, o.status, o.user.id, o.address, o.phone, o.quantity) FROM Order o")
    Page<OrderSumaryDTO> findAllOrderSummary(Pageable pageable);

}
