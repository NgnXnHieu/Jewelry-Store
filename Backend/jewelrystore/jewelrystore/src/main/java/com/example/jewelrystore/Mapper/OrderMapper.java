package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Form.OrderForm.OrderCreateForm;
import com.example.jewelrystore.Form.OrderForm.OrderUpdateForm;
import com.example.jewelrystore.Repository.UserRepository;

@Mapper(componentModel = "spring", uses = { Order_DetailMapper.class })

public abstract class OrderMapper {
    @Autowired
    UserRepository userRepository;

    public abstract Order toEntity(OrderCreateForm orderCreateForm);

    @Mapping(source = "orderDetails", target = "orderDetails")
    @Mapping(source = "user.id", target = "userId")
    public abstract OrderDTO toOrderDTO(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateOrder(OrderUpdateForm form,
            @MappingTarget Order order);

    // @AfterMapping
    // void mapRelations(OrderCreateForm form, @MappingTarget Order order) {
    // Integer userId = form.getUserId();
    // if (userId != null) {
    // order.setUser(userRepository.findById(userId)
    // .orElseThrow(() -> new RuntimeException("User not Found")));

    // }
    // }

}
