package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

public class ShippingDTO {
    private Integer id;
    private LocalDateTime shippedDate;
    private String status;
    private Integer orderId;
    private LocalDateTime delivered_at;
    private String tracking_number;

    public ShippingDTO() {
    }

    public ShippingDTO(Integer id, LocalDateTime shippedDate, String status, Integer orderId,
            LocalDateTime deliverTime, String tracking_number) {
        this.id = id;
        this.shippedDate = shippedDate;
        this.status = status;
        this.orderId = orderId;
        this.delivered_at = deliverTime;
        this.tracking_number = tracking_number;
    }

    public LocalDateTime getDelivered_at() {
        return delivered_at;
    }

    public void setDelivered_at(LocalDateTime delivered_at) {
        this.delivered_at = delivered_at;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
