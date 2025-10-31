package com.example.jewelrystore.Form.Order_DetailForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Order_DetailUpdateForm {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    public Order_DetailUpdateForm() {
    }

    public Order_DetailUpdateForm(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
