package com.example.jewelrystore.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Service.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public Page<ProductDTO> getAll(Pageable pageable) {
        return productService.getAllProduct(pageable);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid ProductCreateForm productCreateForm) {
        ProductDTO created = productService.createProduct(productCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody ProductUpdateForm userUpdateForm) {
        ProductDTO updated = productService.updateProduct(id, userUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "Xóa thành công";
    }

    @GetMapping("/getRelatedProducts/{id}")
    public Page<ProductDTO> getRelatedProducts(@PathVariable Integer id, Pageable pageable) {
        return productService.getRelatedProducts(id, pageable);
    }

    @GetMapping("/productsByCategoryId/{id}")
    public Page<ProductDTO> getProductsByCategoryId(@PathVariable Integer id, Pageable pageable) {
        return productService.getProductsByCategoryId(id, pageable);
    }

    @GetMapping("/bestSeller")
    public Page<BestSellerDTO> getBestSellerProduct(Pageable pageable) {
        return productService.getBestSellerProduct(pageable);
    }

}
