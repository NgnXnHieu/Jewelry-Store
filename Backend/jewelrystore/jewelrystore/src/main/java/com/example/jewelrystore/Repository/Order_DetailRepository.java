package com.example.jewelrystore.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Order_Detail;

@Repository
public interface Order_DetailRepository extends JpaRepository<Order_Detail, Integer> {
    List<Order_Detail> findByOrderId(Integer id);

    // Lấy tất cả order detail có order trong khoảng thời gian
    List<Order_Detail> findByOrder_OrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
