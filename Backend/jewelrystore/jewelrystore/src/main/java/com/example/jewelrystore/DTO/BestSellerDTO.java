package com.example.jewelrystore.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BestSellerDTO {
    private Integer id;
    private String name;
    private Double price;
    private String imageUrl;
    private Long totalQuantity;

    public BestSellerDTO(Integer id, String name, Double price, String imageUrl, Long totalQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.totalQuantity = totalQuantity;
    }

    public BestSellerDTO() {
    }
}
