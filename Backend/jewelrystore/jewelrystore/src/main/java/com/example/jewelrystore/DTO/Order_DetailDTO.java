package com.example.jewelrystore.DTO;

public class Order_DetailDTO {
    private Integer id;
    private Long quantity;
    private Double totalPrice;
    private Integer orderId; // chỉ lấy id của order
    private Integer productId; // lấy tên sản phẩm từ product
    private Double price; // lấy giá sản phẩm nếu cần

    public Order_DetailDTO() {
    }

    public Order_DetailDTO(Integer id, Long quantity, Double totalPrice, Integer orderId, Integer productName,
            Double price) {
        this.id = id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderId = orderId;
        this.productId = productName;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double productPrice) {
        this.price = productPrice;
    }

}
