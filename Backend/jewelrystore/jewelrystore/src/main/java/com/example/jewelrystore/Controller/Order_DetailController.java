package com.example.jewelrystore.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.Order_DetailDTO;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailCreateForm;
import com.example.jewelrystore.Form.Order_DetailForm.Order_DetailUpdateForm;
import com.example.jewelrystore.Service.Order_DetailService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/order_details")
@Validated
public class Order_DetailController {
    @Autowired
    Order_DetailService service;

    @GetMapping
    public Page<Order_DetailDTO> getAll(Pageable pageable) {
        return service.getAllOrder_Detail(pageable);
    }

    @GetMapping("/{id}")
    public Order_DetailDTO getOrder_DetailById(@PathVariable Integer id) {
        return service.getOrder_DetailById(id);
    }

    @GetMapping("/{orderId}/orderDetalils")
    public List<Order_DetailDTO> getOrder_DetailByOrderId(@PathVariable Integer orderId) {
        return service.getOrder_DetailsByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Order_DetailCreateForm addressCreateForm) {
        Order_DetailDTO created = service.createOrder_Detail(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody Order_DetailUpdateForm addressUpdateForm) {
        Order_DetailDTO updated = service.updateOrder_Detail(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteOrder_Detail(id);
        return "Xóa thành công";
    }

}
