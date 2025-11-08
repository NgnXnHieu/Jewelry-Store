package com.example.jewelrystore.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.Inventory_HistoryDTO;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Inventory_HistoryCreateForm;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Invetory_HistoryUpdateForm;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Service.Inventory_HistoryService;

import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity create(@RequestBody @Valid Inventory_HistoryCreateForm historyCreateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getUsername() == null) {
            throw new AccessDeniedException("Need login");
        }
        Inventory_HistoryDTO created = service.createInventory_History(historyCreateForm, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody Invetory_HistoryUpdateForm historyCreateForm) {
        Inventory_HistoryDTO updated = service.updateInventory_History(id, historyCreateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteInventory_History(id);
        return "Xóa thành công";
    }

    // @PostMapping("/StockStats")
    // public ResponseEntity<Map<String, Long>> getStockStats(@RequestBody
    // Map<String, Long> body) {
    // // nếu null thì trả về 0, còn nếu không thì lấy giá trị mặc định
    // Long all = Objects.requireNonNullElse(productService.getTotalQuantity(), 0L);
    // Long low = Objects.requireNonNullElse(
    // productService.getTotalQuantityBetween(body.get("minOfLow"),
    // body.get("maxOfLow")), 0L);
    // Long out =
    // Objects.requireNonNullElse(productService.getTotalQuantityBySpecificQuantity(body.get("out")),
    // 0L);
    // Long in =
    // Objects.requireNonNullElse(productService.getTotalQuantityGreaterSpecificQuantity(body.get("in")),
    // 0L);
    // Long countAllProducts =
    // Objects.requireNonNullElse(productService.getCountAllProducts(), 0L);
    // // Các phần tử trong Map phải trùng tên với biến bên frontend nhận
    // Map<String, Long> result = new HashMap<>();
    // result.put("totalProducts", all);
    // result.put("lowStockCount", low);
    // result.put("outOfStockCount", out);
    // result.put("inStockCount", in);
    // result.put("totalUnits", countAllProducts);
    // // System.out.println("in result: " + result.get("outOfStockCount"));
    // // System.out.println("in out: " + out);

    // return ResponseEntity.ok(result);
    // }
}
