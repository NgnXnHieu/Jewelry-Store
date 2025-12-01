package com.example.jewelrystore.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.CheckoutDTO;
import com.example.jewelrystore.Form.Checkout.CheckoutCreateForm;
import com.example.jewelrystore.Form.Checkout.PlaceOrderForm;
import com.example.jewelrystore.Service.CheckoutService;

import jakarta.security.auth.message.AuthException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("api/checkout")
@Validated
public class CheckoutController {
    @Autowired
    CheckoutService checkoutService;

    @PostMapping()
    public Integer createCheckout(@RequestBody CheckoutCreateForm form,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("You don't have permission to change quantity this item");
        }
        return checkoutService.createCheckoutId(form, userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public CheckoutDTO getCheckout(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("You don't have permission to change quantity this item");
        }
        return checkoutService.getCheckout(id, userDetails.getUsername());
    }

    @PostMapping("/placeOrder")
    public String placeOrder(@RequestBody PlaceOrderForm form, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("You don't have permission to change quantity this item");
        }
        return checkoutService.placeOrder(form, userDetails.getUsername());
    }

    @GetMapping("/{id}/checkStatus")
    public Boolean checkStatus(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("You don't have permission to change quantity this item");
        }
        return checkoutService.checkStatus(id, userDetails.getUsername());
    }

    // Hàm tạo api lấy mã qr để test
    // @PostMapping("testWeebHook")
    // public String testWeebHook() {

    // }
}
