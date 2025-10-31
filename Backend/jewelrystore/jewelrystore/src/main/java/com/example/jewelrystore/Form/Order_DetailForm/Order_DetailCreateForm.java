package com.example.jewelrystore.Form.Order_DetailForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Order_DetailCreateForm {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Order ID is required")
    private Integer orderId;

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "TotalPrice is required")
    private Double totalPrice;

    public Order_DetailCreateForm() {
    }

    public Order_DetailCreateForm(Long quantity, Double price, Integer orderId, Integer productId, Double totalPrice) {
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
        this.productId = productId;
        this.totalPrice = totalPrice;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
