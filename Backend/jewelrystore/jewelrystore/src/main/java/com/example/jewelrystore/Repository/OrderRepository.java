package com.example.jewelrystore.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.DTO.OrderSumaryDTO;
import com.example.jewelrystore.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
        List<Order> findByUserUsername(String username);

        // Tìm kiếm đơn hàng theo username và trạng thái
        List<Order> findByUserUsernameAndStatus(String username, String status);

        // Lấy ra tất cả orders không chứa order_details
        @Query("SELECT new com.example.jewelrystore.DTO.OrderSumaryDTO(o.id, o.orderDate, o.totalAmount, o.status, o.user.id, o.address, o.phone, o.quantity) FROM Order o")
        Page<OrderSumaryDTO> findAllOrderSummary(Pageable pageable);

        // Lấy ra tổng doanh thu theo khoảng thời gian A,B
        @Query("SELECT SUM(o.totalAmount) FROM Order o" + " WHERE o.orderDate BETWEEN :startDate AND :endDate" +
                        " AND o.status = :status")
        Double getSumTotalAmountByDateAndStatus(@Param("startDate") LocalDateTime starDate,
                        @Param("endDate") LocalDateTime endDate, @Param("status") String status);

        // Đếm tổng đơn hàng có ngày đặt trong (A,B) và status khác C
        @Query("SELECT COUNT(o) FROM Order o" + " WHERE o.orderDate BETWEEN :startDate AND :endDate" +
                        " AND o.status != :status")
        Long getCountOrdersByDateAndNotStatus(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("status") String status);

        // Đếm tổng đơn hàng có ngày đặt trong (A,B) và status == C
        @Query("SELECT COUNT(o) FROM Order o" + " WHERE o.orderDate BETWEEN :startDate AND :endDate" +
                        " AND o.status = :status")
        Long getCountOrdersByDateAndStatus(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("status") String status);

        // Đếm tổng số đơn hàng có ngày đặt trong (A,B) và status thuộc statuses
        @Query("SELECT COUNT(o) FROM Order o " +
                        "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                        "AND o.status IN :statuses")
        Long getCountOrdersByDateAndStatuses(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("statuses") List<String> statuses);

        // Lấy ra mã và giá của đơn hàng có giá cao nhất trong khoảng thời gian (A,B)
        // với status = C
        @Query("SELECT MAX(o.totalAmount) FROM Order o " +
                        "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                        "AND o.status = :status")
        Double findMaxTotalAmountByDateAndStatus(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("status") String status);

        // lấy TẤT CẢ đơn hàng (theo cursor)
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId " +
                        "AND (:cursor IS NULL OR o.id < :cursor) " + // Logic Cursor ở đây
                        "ORDER BY o.id DESC")
        List<Order> findMyOrdersCursor(
                        @Param("userId") Integer userId,
                        @Param("cursor") Integer cursor,
                        Pageable pageable // Dùng cái này để set Limit
        );

        // lấy theo TRẠNG THÁI (theo cursor)
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId " +
                        "AND o.status = :status " +
                        "AND (:cursor IS NULL OR o.id < :cursor) " +
                        "ORDER BY o.id DESC")
        List<Order> findMyOrdersByStatusCursor(
                        @Param("userId") Integer userId,
                        @Param("status") String status,
                        @Param("cursor") Integer cursor,
                        Pageable pageable);
}
