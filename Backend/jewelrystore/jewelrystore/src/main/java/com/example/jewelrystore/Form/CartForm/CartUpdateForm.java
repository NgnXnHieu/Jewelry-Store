package com.example.jewelrystore.Form.CartForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartUpdateForm {

    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price must be >= 0")
    private Double totalPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be >= 0")
    private Long quantity;

    public CartUpdateForm() {
    }

    public CartUpdateForm(Double totalPrice, Long quantity) {

        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
