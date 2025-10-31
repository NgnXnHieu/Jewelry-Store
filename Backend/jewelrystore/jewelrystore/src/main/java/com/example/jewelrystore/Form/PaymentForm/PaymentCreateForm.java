package com.example.jewelrystore.Form.PaymentForm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentCreateForm {

    @NotNull(message = "Payment method is required")
    private String method;

    @NotNull(message = "Payment status is required")
    private String status;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Order ID is required")
    private Integer orderId;

    public PaymentCreateForm() {
    }

    public PaymentCreateForm(String method, String status, Double amount, Integer orderId) {
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
