package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.example.jewelrystore.Entity.Order_Detail;

public class OrderDTO {
    private Integer id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private Integer userId; // chỉ lấy id của user
    private String address;
    private String phone;
    private Long quantity;
    private List<Order_DetailDTO> orderDetails;

    public OrderDTO() {
    }

    public OrderDTO(Integer id, LocalDateTime orderDate, Double totalAmount, String status, Integer userId,
            String address, String phone, Long quantity, List<Order_DetailDTO> orderDetails) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.userId = userId;
        this.address = address;
        this.phone = phone;
        this.quantity = quantity;
        this.orderDetails = orderDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public List<Order_DetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<Order_DetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
