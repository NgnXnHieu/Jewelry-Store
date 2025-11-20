package com.example.jewelrystore.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByUsername(String username);

        // Lấy ra tất cả user không có role là X
        Page<User> findByRoleNot(String role, Pageable pageable);

        // đếm tất cả user có role khác X
        Long countByRoleNot(String role);

        // đếm tất cả user có role bằng X
        Long countByRole(String role);

        // Đếm tất cả người dùng có role là :role từ ngày A đến B
        @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.created_at BETWEEN :startDate AND :endDate")
        Long getCountUserByRoleBetweenDates(
                        @Param("role") String role,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Đếm tất cả user có role khác :role từ ngày A đến B
        @Query("SELECT COUNT(u) FROM User u WHERE u.role != :role AND u.created_at BETWEEN :startDate AND :endDate")
        Long getCountUserNotRoleBetweenDates(
                        @Param("role") String role,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

}
