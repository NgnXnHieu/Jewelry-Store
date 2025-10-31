package com.example.jewelrystore.Form.OrderForm;

import jakarta.validation.constraints.NotNull;

public class OrderUpdateForm {

    @NotNull(message = "Status is required")
    private String status;

    public OrderUpdateForm() {
    }

    public OrderUpdateForm(String status) {
        this.status = status;
    }
}
