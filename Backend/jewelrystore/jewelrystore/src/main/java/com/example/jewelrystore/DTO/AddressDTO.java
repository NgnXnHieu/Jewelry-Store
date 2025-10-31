package com.example.jewelrystore.DTO;

public class AddressDTO {
    private Integer id;
    private String phone;
    private String village;
    private String ward;
    private String district;
    private boolean is_defaut;
    private Integer userId; // chỉ lưu userId thay vì cả User object

    public AddressDTO() {
    }

    public AddressDTO(Integer id, String phone, String village, String ward, String district, boolean isDefault,
            Integer userId) {
        this.id = id;
        this.phone = phone;
        this.village = village;
        this.ward = ward;
        this.district = district;
        this.is_defaut = isDefault;
        this.userId = userId;
    }

    // Getters & Setters
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
