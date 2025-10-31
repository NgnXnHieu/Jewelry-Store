package com.example.jewelrystore.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String method;
    @NonNull
    private String status;
    @NonNull
    private Double amount;
    @NonNull
    @CreationTimestamp
    private LocalDateTime paymentDate;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment() {
    }

    public Payment(Integer id, @NonNull String method, @NonNull String status, @NonNull Double amount,
            @NonNull LocalDateTime paymentDate, Order order) {
        this.id = id;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.order = order;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Payment [id=" + id + ", method=" + method + ", status=" + status + ", amount=" + amount
                + ", paymentDate=" + paymentDate + ", order=" + order + "]";
    }

}
