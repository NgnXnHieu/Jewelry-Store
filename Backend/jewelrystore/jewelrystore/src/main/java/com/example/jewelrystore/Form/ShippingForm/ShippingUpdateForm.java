package com.example.jewelrystore.Form.ShippingForm;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

public class ShippingUpdateForm {

    @NotNull(message = "Shipped date is required")
    private LocalDateTime shippedDate;

    @NotNull(message = "Status is required")
    private String status;

    public ShippingUpdateForm() {
    }

    public ShippingUpdateForm(LocalDateTime shippedDate, String status) {
        this.shippedDate = shippedDate;
        this.status = status;

    }

    // Getters & Setters
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

}
