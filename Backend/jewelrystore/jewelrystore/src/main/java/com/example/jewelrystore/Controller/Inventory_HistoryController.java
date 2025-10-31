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

import com.example.jewelrystore.DTO.Inventory_HistoryDTO;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Inventory_HistoryCreateForm;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Invetory_HistoryUpdateForm;
import com.example.jewelrystore.Service.Inventory_HistoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory_histories")
@Validated
public class Inventory_HistoryController {
    @Autowired
    Inventory_HistoryService service;

    @GetMapping
    public Page<Inventory_HistoryDTO> getAll(Pageable pageable) {
        return service.getAllInventory_History(pageable);
    }

    @GetMapping("/{id}")
    public Inventory_HistoryDTO getById(@PathVariable Integer id) {
        return service.getInventory_HistoryById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Inventory_HistoryCreateForm addressCreateForm) {
        Inventory_HistoryDTO created = service.createInventory_History(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } 

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody Invetory_HistoryUpdateForm addressUpdateForm) {
        Inventory_HistoryDTO updated = service.updateInventory_History(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteInventory_History(id);
        return "Xóa thành công";
    }

}
