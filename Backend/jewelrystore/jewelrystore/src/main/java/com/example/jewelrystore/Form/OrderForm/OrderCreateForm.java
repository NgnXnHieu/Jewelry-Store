package com.example.jewelrystore.Form.OrderForm;

import java.util.List;
import java.util.Map;

import com.example.jewelrystore.Entity.Order_Detail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderCreateForm {

    // @NotNull(message = "Total amount is required")
    // @Positive(message = "Total amount must be greater than 0")
    // private Double totalAmount;

    @NotNull(message = "Status is required")
    private String status;

    // @NotNull(message = "User ID is required")
    // private Integer userId;

    private String address;
    private String phone;
    // private Long quantity;
    // private List<Order_Detail> orderDetails;
    private List<Map> idAndQuantityList;

    public OrderCreateForm() {
    }

    public OrderCreateForm(String address, String phone,
            List<Map> idAndQuantityList) {
        // this.totalAmount = totalAmount;
        // this.status = status;
        // this.userId = userId;
        this.address = address;
        this.phone = phone;
        // this.quantity = quantity;
        this.idAndQuantityList = idAndQuantityList;
    }

    // public Double getTotalAmount() {
    // return totalAmount;
    // }

    // public void setTotalAmount(Double totalAmount) {
    // this.totalAmount = totalAmount;
    // }

    // public String getStatus() {
    // return status;
    // }

    // public void setQuantity(Long quantity) {
    // this.quantity = quantity;
    // }

    // public Long getQuantity() {
    // return quantity;
    // }

    // public void setStatus(String status) {
    // this.status = status;
    // }

    // public Integer getUserId() {
    // return userId;
    // }

    // public void setUserId(Integer userId) {
    // this.userId = userId;
    // }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Map> getIdAndQuantityList() {
        return idAndQuantityList;
    }

    public void setIdAndQuantityList(List<Map> idAndQuantityList) {
        this.idAndQuantityList = idAndQuantityList;
    }

    // public List<Order_Detail> getOrderDetails() {
    // return orderDetails;
    // }

    // public void setOrderDetails(List<Order_Detail> orderDetails) {
    // this.orderDetails = orderDetails;
    // }

}
