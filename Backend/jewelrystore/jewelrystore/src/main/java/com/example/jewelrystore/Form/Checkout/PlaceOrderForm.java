package com.example.jewelrystore.Form.Checkout;

import com.example.jewelrystore.Entity.Order.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderForm {
    private Integer checkoutId;
    private Integer addressId;
    private PaymentMethod payment_method;
}
