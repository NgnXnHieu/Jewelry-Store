package com.example.jewelrystore.Form.Cart_DetailForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Cart_DetailUpdateForm {
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    public Cart_DetailUpdateForm() {
    }

    public Cart_DetailUpdateForm(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
