package com.example.jewelrystore.Form.CartForm;

import jakarta.validation.constraints.NotNull;

public class CartCreateForm {

    @NotNull(message = "User ID is required")
    private Integer userId;

    public CartCreateForm() {
    }

    public CartCreateForm(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
