package com.example.jewelrystore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 11)
    private String phone;
    @Column(length = 100)
    private String village;
    @Column(length = 100)
    private String ward;
    @Column(length = 100)
    private String district;
    private boolean is_defaut;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address() {
    }

    public Address(Integer id, String phone, String village, String ward, String district, boolean is_defaut,
            User user) {
        this.id = id;
        this.phone = phone;
        this.village = village;
        this.ward = ward;
        this.district = district;
        this.is_defaut = is_defaut;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public boolean isIs_defaut() {
        return is_defaut;
    }

    public void setIs_defaut(boolean is_defaut) {
        this.is_defaut = is_defaut;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Address [id=" + id + ", phone=" + phone + ", village=" + village + ", ward=" + ward + ", district="
                + district + ", is_defaut=" + is_defaut + ", user=" + user + "]";
    }

}
