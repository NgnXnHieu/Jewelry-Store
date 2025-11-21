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

import com.example.jewelrystore.DTO.CartDTO;
import com.example.jewelrystore.Form.CartForm.CartCreateForm;
import com.example.jewelrystore.Form.CartForm.CartUpdateForm;
import com.example.jewelrystore.Service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carts")
@Validated
public class CartController {
    @Autowired
    CartService service;

    @GetMapping
    public Page<CartDTO> getAll(Pageable pageable) {
        return service.getAllCart(pageable);
    }

    @GetMapping("/{id}")
    public CartDTO getCartById(@PathVariable Integer id) {
        return service.getCartById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CartCreateForm cartCreateForm) {
        CartDTO created = service.createCart(cartCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody CartUpdateForm addressUpdateForm) {
        CartDTO updated = service.updateCart(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteCart(id);
        return "Xóa thành công";
    }

}
