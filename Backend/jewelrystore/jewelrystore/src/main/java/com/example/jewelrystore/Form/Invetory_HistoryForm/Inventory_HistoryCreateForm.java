package com.example.jewelrystore.Form.Invetory_HistoryForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Inventory_HistoryCreateForm {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "User ID is required")
    private Integer userId;

    public Inventory_HistoryCreateForm() {
    }

    public Inventory_HistoryCreateForm(Long quantity, Integer productId, Integer userId) {
        this.quantity = quantity;
        this.productId = productId;
        this.userId = userId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    
}
