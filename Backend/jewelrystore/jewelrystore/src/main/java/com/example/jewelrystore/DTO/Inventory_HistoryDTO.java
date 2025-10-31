package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

public class Inventory_HistoryDTO {
    private Integer id;
    private LocalDateTime date;
    private Long quantity;
    private Integer productId; // lấy từ Product
    private Integer userId; // lấy từ User

    public Inventory_HistoryDTO() {
    }

    public Inventory_HistoryDTO(Integer id, LocalDateTime date, Long quantity, Integer productId, Integer userId) {
        this.id = id;
        this.date = date;
        this.quantity = quantity;
        this.productId = productId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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
