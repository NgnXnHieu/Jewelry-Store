package com.example.jewelrystore.Form.AddressForm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddressCreateForm {

    @NotBlank(message = "Phone is required")
    @Size(max = 11, message = "Phone number cannot exceed 11 characters")
    private String phone;

    @NotBlank(message = "Village is required")
    @Size(max = 100, message = "Village cannot exceed 100 characters")
    private String village;

    @NotBlank(message = "Ward is required")
    @Size(max = 100, message = "Ward cannot exceed 100 characters")
    private String ward;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;

    private boolean is_defaut;

    @NotNull(message = "User ID is required")
    private Integer userId;

    public AddressCreateForm() {
    }

    public AddressCreateForm(String phone, String village, String ward, String district, boolean isDefault,
            Integer userId) {
        this.phone = phone;
        this.village = village;
        this.ward = ward;
        this.district = district;
        this.is_defaut = isDefault;
        this.userId = userId;
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

    public void setIs_defaut(boolean isDefault) {
        this.is_defaut = isDefault;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
