package com.example.jewelrystore.Form.Invetory_HistoryForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Invetory_HistoryUpdateForm {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    public Invetory_HistoryUpdateForm() {
    }

    public Invetory_HistoryUpdateForm(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
