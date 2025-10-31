package com.example.jewelrystore.Form.Cart_DetailForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Cart_DetailCreateForm {

    @NotNull(message = "Cart ID is required")
    private Integer cartId;

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    public Cart_DetailCreateForm() {
    }

    public Cart_DetailCreateForm(Integer cartId, Integer productId, Long quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
