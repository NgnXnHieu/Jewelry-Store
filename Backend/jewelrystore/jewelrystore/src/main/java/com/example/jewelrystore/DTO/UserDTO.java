package com.example.jewelrystore.DTO;

import java.time.LocalDateTime;

public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private String full_name;
    private String phone;
    private LocalDateTime created_at;

    public UserDTO() {
    }

    public UserDTO(Integer id, String username, String email, String role, String fullName, String phone,
            LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.full_name = fullName;
        this.phone = phone;
        this.created_at = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String fullName) {
        this.full_name = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }
}
