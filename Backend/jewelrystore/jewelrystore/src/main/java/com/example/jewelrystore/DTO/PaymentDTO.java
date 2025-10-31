package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

public class PaymentDTO {
    private Integer id;
    private String method;
    private String status;
    private Double amount;
    private LocalDateTime paymentDate;
    private Integer orderId; // chỉ lấy id của order

    public PaymentDTO() {
    }

    public PaymentDTO(Integer id, String method, String status, Double amount, LocalDateTime paymentDate, Integer orderId) {
        this.id = id;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
