package com.example.jewelrystore.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.OrderDTO;
import com.example.jewelrystore.DTO.OrderSumaryDTO;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Form.OrderForm.OrderCreateForm;
import com.example.jewelrystore.Form.OrderForm.OrderUpdateForm;
import com.example.jewelrystore.Service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    @Autowired
    OrderService service;

    @GetMapping
    public Page<OrderDTO> getAll(Pageable pageable) {
        return service.getAllOrder(pageable);
    }

    @GetMapping("/orderSumaries")
    public Page<OrderSumaryDTO> getOrderSumarys(Pageable pageable) {
        return service.getAllOrderSumary(pageable);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Integer id) {
        return service.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid OrderCreateForm addressCreateForm) {
        OrderDTO created = service.createOrder(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody OrderUpdateForm addressUpdateForm) {
        OrderDTO updated = service.updateOrder(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteOrder(id);
        return "Xóa thành công";
    }

    @GetMapping("/myOrders")
    public List<OrderDTO> getodersByUsername(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return null;
        return service.getOrdersByUsername(userDetails.getUsername());
    }

    @PostMapping("/myOrder")
    public ResponseEntity createMyOrder(@RequestBody @Valid OrderCreateForm orderCreateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        OrderDTO created = service.createMyOrder(orderCreateForm, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/myOrdersByStatus")
    public List<OrderDTO> getMyOrderByStatus(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String status) {
        if (userDetails == null)
            return null;
        return service.getMyOrderByStatus(userDetails.getUsername(), status);
    }

    // @GetMapping("revenuePerYear")
    // public String getMethodName(@RequestParam String param) {
    // return new String();
    // }

    // @GetMapping("revenuePerMonth")
    // public String getMethodName(@RequestParam String param) {
    // return new String();
    // }

    // @GetMapping("revenuePerDay")
    // public String getMethodName(@RequestParam String param) {
    // return new String();
    // }

}
