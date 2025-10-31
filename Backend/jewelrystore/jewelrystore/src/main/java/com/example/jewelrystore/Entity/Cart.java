package com.example.jewelrystore.Entity;

import java.util.List;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double totalPrice; // tổng tiền toàn giỏ
    private Long totalQuantity; // tổng số lượng sản phẩm

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart_Detail> cartDetails;

    public Cart() {
    }

    public Cart(Integer id, User user, Double totalPrice, Long totalQuantity) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }

    // getter, setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Cart_Detail> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<Cart_Detail> cartDetails) {
        this.cartDetails = cartDetails;
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
