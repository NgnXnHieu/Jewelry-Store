package com.example.jewelrystore.Mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.CheckoutDTO;
import com.example.jewelrystore.DTO.Checkout_ItemDTO;
import com.example.jewelrystore.Entity.Checkout;
import com.example.jewelrystore.Entity.Checkout_Item;

@Mapper(componentModel = "spring")
public abstract class CheckoutMapper {
    @Autowired
    Checkout_ItemMapper checkout_ItemMapper;
    @Autowired
    AddressMapper AddressMapper;

    public abstract CheckoutDTO toCheckoutDTO(Checkout checkout);

    @AfterMapping
    void solve(Checkout checkout, @MappingTarget CheckoutDTO checkoutDTO) {
        List<Checkout_ItemDTO> list = new ArrayList<>();
        for (Checkout_Item item : checkout.getCheckout_Items()) {
            list.add(checkout_ItemMapper.toCheckout_ItemDTO(item));
        }
        checkoutDTO.setCheckout_Items(list);
        checkoutDTO.setAddress(AddressMapper.toAddressDTO(checkout.getAddress()));
    }

}
