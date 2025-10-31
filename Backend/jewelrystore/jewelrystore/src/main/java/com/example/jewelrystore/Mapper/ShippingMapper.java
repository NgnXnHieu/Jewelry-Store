package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.ShippingDTO;
import com.example.jewelrystore.Entity.Shipping;
import com.example.jewelrystore.Form.ShippingForm.ShippingCreateForm;
import com.example.jewelrystore.Form.ShippingForm.ShippingUpdateForm;
import com.example.jewelrystore.Repository.OrderRepository;

@Mapper(componentModel = "spring")

public abstract class ShippingMapper {
    @Autowired
    OrderRepository orderRepository;

    public abstract Shipping toEntity(ShippingCreateForm shippingCreateForm);

    @Mapping(source = "order.id", target = "orderId")
    public abstract ShippingDTO toShippingDTO(Shipping shipping);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateShipping(ShippingUpdateForm form,
            @MappingTarget Shipping shipping);

    @AfterMapping
    void mapRelations(ShippingCreateForm form, @MappingTarget Shipping shipping) {
        Integer id = form.getOrderId();
        if (id != null) {
            shipping.setOrder(orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cart not Found")));

        }
    }
}
