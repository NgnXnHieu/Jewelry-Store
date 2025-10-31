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
import com.example.jewelrystore.DTO.ShippingDTO;
import com.example.jewelrystore.Form.ShippingForm.ShippingCreateForm;
import com.example.jewelrystore.Form.ShippingForm.ShippingUpdateForm;
import com.example.jewelrystore.Service.ShippingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shippings")
@Validated
public class ShippingController {
    @Autowired
    ShippingService service;

    @GetMapping
    public Page<ShippingDTO> getAll(Pageable pageable) {
        return service.getAllShipping(pageable);
    }

    @GetMapping("/{id}")
    public ShippingDTO getShippingById(@PathVariable Integer id) {
        return service.getShippingById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid ShippingCreateForm addressCreateForm) {
        ShippingDTO created = service.createShipping(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody ShippingUpdateForm addressUpdateForm) {
        ShippingDTO updated = service.updateShipping(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteShipping(id);
        return "Xóa thành công";
    }

}
