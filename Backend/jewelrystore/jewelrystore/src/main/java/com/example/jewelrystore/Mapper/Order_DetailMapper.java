package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.Order_DetailDTO;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Entity.Order_Detail;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailCreateForm;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailUpdateForm;
import com.example.jewelrystore.Repository.OrderRepository;
import com.example.jewelrystore.Repository.ProductRepository;

@Mapper(componentModel = "spring")
public abstract class Order_DetailMapper {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    public abstract Order_Detail toEntity(Order_DetailCreateForm order_DetailCreateForm);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    public abstract Order_DetailDTO toOrder_DetailDTO(Order_Detail order_Detail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateOrder_Detail(Order_DetailUpdateForm form,
            @MappingTarget Order_Detail order_Detail);

    @AfterMapping
    void mapRelations(Order_DetailCreateForm form, @MappingTarget Order_Detail order_Detail) {
        Integer orderId = form.getOrderId();
        Integer productId = form.getProductId();
        if (orderId != null) {
            order_Detail.setOrder(orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Cart not Found")));
        }
        if (productId != null) {
            order_Detail.setProduct(productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not Found")));
        }
    }

    @AfterMapping
    void calculate(Order_DetailCreateForm form, @MappingTarget Order_Detail order_Detail) {
        Order order = orderRepository
                .findById(form.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not Found"));

        Long quantity = form.getQuantity();
        Double totalPrice = form.getTotalPrice();
        if (quantity != null) {
            order.setQuantity(order.getQuantity() + quantity);

            if (totalPrice != null) {
                order.setTotalAmount(order.getTotalAmount() + totalPrice);
            }
        }
    }
}
