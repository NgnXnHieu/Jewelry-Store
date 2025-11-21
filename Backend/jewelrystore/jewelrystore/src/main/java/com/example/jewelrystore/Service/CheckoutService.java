package com.example.jewelrystore.Service;

import com.example.jewelrystore.DTO.CheckoutDTO;
import com.example.jewelrystore.Form.Checkout.CheckoutCreateForm;
import com.example.jewelrystore.Form.Checkout.PlaceOrderForm;
import com.example.jewelrystore.Form.WebHook.SePayWebhookDTO;

public interface CheckoutService {
    public Integer createCheckoutId(CheckoutCreateForm checkout, String username);

    String placeOrder(PlaceOrderForm form, String username);

    CheckoutDTO getCheckout(Integer id, String username);

    void processPaymentSuccess(SePayWebhookDTO payload);
}
