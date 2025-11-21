package com.example.jewelrystore.Form.UserForm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserUpdateForm {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "password not blank")
    private String password;
    @NotBlank(message = "Full name is required")
    private String full_name;

    private String phone;
    private String role;
    private Boolean is_active;
    private String image_url;

    public UserUpdateForm() {
    }

    public UserUpdateForm(String email, String fullName, String phone,
            Boolean is_active, String image_url) {
        this.email = email;
        this.full_name = fullName;
        this.phone = phone;
        this.is_active = is_active;
        this.image_url = image_url;
    }

    public UserUpdateForm(String email, String fullName, String phone, String role,
            Boolean is_active, String image_url) {
        this.email = email;
        this.full_name = fullName;
        this.phone = phone;
        this.role = role;
        this.is_active = is_active;
        this.image_url = image_url;
    }

    public UserUpdateForm(
            @Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "password not blank") String password,
            @NotBlank(message = "Full name is required") String full_name, String phone, String image_url) {
        this.email = email;
        this.password = password;
        this.full_name = full_name;
        this.phone = phone;
        this.image_url = image_url;
    }

    // Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIs_active() {
        return is_active;
    }

}