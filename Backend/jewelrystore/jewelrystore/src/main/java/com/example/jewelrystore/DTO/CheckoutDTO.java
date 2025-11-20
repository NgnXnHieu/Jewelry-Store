package com.example.jewelrystore.DTO;

import java.util.List;

import com.example.jewelrystore.Entity.Checkout_Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {
    private Integer id;
    private List<Checkout_ItemDTO> checkout_Items;
    private Integer addressId;
}
