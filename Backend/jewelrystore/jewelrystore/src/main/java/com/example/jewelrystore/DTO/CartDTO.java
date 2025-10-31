package com.example.jewelrystore.DTO;

import java.util.List;

public class CartDTO {
    private Integer id;
    private List<Cart_DetailDTO> items; // danh sách sản phẩm trong giỏ
    private Double totalPrice; // tổng tiền toàn giỏ
    private Long totalQuantity; // tổng số lượng sản phẩm

    public CartDTO() {
    }

    public CartDTO(Integer id, List<Cart_DetailDTO> items, Double totalPrice, Long totalQuantity) {
        this.id = id;
        this.items = items;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cart_DetailDTO> getItems() {
        return items;
    }

    public void setItems(List<Cart_DetailDTO> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
