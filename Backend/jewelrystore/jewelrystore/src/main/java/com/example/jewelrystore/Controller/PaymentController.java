package com.example.jewelrystore.Controller;

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

import com.example.jewelrystore.DTO.PaymentDTO;
import com.example.jewelrystore.Form.PaymentForm.PaymentCreateForm;
import com.example.jewelrystore.Form.PaymentForm.PaymentUpdateForm;
import com.example.jewelrystore.Service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {
    @Autowired
    PaymentService service;

    @GetMapping
    public Page<PaymentDTO> getAll(Pageable pageable) {
        return service.getAllPayment(pageable);
    }

    @GetMapping("/{id}")
    public PaymentDTO getPaymentById(@PathVariable Integer id) {
        return service.getPaymentById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid PaymentCreateForm addressCreateForm) {
        PaymentDTO created = service.createPayment(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody PaymentUpdateForm addressUpdateForm) {
        PaymentDTO updated = service.updatePayment(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deletePayment(id);
        return "Xóa thành công";
    }

}
