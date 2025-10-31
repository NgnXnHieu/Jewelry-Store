package com.example.jewelrystore.Entity;

import java.time.LocalDateTime;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private LocalDateTime shippedDate;
    @NonNull
    private String status;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private LocalDateTime delivered_at;
    private String tracking_number;

    public Shipping() {
    }

    public Shipping(Integer id, @NonNull LocalDateTime shippedDate, @NonNull String status, Order order,
            LocalDateTime deliverTime, String tracking_number) {
        this.id = id;
        this.shippedDate = shippedDate;
        this.status = status;
        this.order = order;
        this.delivered_at = deliverTime;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
