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

import com.example.jewelrystore.DTO.CategoryDTO;
import com.example.jewelrystore.Form.CategoryForm.CategoryCreateForm;
import com.example.jewelrystore.Form.CategoryForm.CategoryUpdateForm;
import com.example.jewelrystore.Service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    @Autowired
    CategoryService service;

    @GetMapping
    public Page<CategoryDTO> getAll(Pageable pageable) {
        return service.getAllCategory(pageable);
    }

    // Lấy tất cả mà không phân trang
    @GetMapping("/all")
    public List<CategoryDTO> getAllNoPage() {
        return service.getAllNoPage();
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategoryById(@PathVariable Integer id) {
        return service.getCategoryById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CategoryCreateForm addressCreateForm) {
        CategoryDTO created = service.createCategory(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody CategoryUpdateForm addressUpdateForm) {
        CategoryDTO updated = service.updateCategory(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteCategory(id);
        return "Xóa thành công";
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCategoriesQuantity() {
        Long total = service.getCountCategory();
        return ResponseEntity.ok(total);
    }

}
