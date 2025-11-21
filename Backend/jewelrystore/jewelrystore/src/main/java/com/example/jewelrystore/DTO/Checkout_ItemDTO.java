package com.example.jewelrystore.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Checkout_ItemDTO {
    private Integer id;
    private Integer productId;
    private Double totalPrice;
    private Long quantity;
}