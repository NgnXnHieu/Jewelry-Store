package com.example.jewelrystore.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.jewelrystore.DTO.Checkout_ItemDTO;
import com.example.jewelrystore.Entity.Checkout_Item;

@Mapper(componentModel = "spring")
public abstract class Checkout_ItemMapper {
    @Mapping(source = "product.id", target = "productId")
    public abstract Checkout_ItemDTO toCheckout_ItemDTO(Checkout_Item checkout_Item);
}
