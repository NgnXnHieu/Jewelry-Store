package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.PaymentDTO;
import com.example.jewelrystore.Entity.Payment;
import com.example.jewelrystore.Form.PaymentForm.PaymentCreateForm;
import com.example.jewelrystore.Form.PaymentForm.PaymentUpdateForm;
import com.example.jewelrystore.Repository.OrderRepository;

@Mapper(componentModel = "spring")

public abstract class PaymentMapper {
    @Autowired
    OrderRepository orderRepository;

    public abstract Payment toEntity(PaymentCreateForm paymentCreateForm);

    @Mapping(source = "order.id", target = "orderId")
    public abstract PaymentDTO toPaymentDTO(Payment payment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updatePayment(PaymentUpdateForm form,
            @MappingTarget Payment payment);

    @AfterMapping
    void mapRelations(PaymentCreateForm form, @MappingTarget Payment payment) {
        Integer id = form.getOrderId();
        if (id != null) {
            payment.setOrder(orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cart not Found")));

        }
    }
}
