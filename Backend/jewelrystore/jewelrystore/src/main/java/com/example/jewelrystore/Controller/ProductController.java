package com.example.jewelrystore.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Service.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.data.domain.Sort;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public Page<ProductDTO> getAll(Pageable pageable) {
        // Custom lại phân trang để sắp xếp theo id giảm dần
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        return productService.getAllProduct(sortedPageable);
    }

    @GetMapping("/productsByStatus")
    public Page<ProductDTO> getAllByStockStatus(@RequestParam("status") String status, Pageable pageable) {
        return productService.getProductsByStockStatus(status, pageable);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Long quantity,
            @RequestParam("description") String description,
            @RequestParam(value = "image_url", required = false) String imageUrl,
            @RequestParam("categoryId") Integer categoryId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            ProductCreateForm form = new ProductCreateForm(name, description, price, quantity, imageUrl, categoryId);
            ProductDTO created = productService.createProduct(form, image, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Long quantity,
            @RequestParam("description") String description,
            @RequestParam(value = "image_url", required = false) String imageUrl,
            @RequestParam("categoryId") Integer categoryId,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductUpdateForm form = new ProductUpdateForm(name, description, price, imageUrl, categoryId);
        ProductDTO updated = productService.updateProduct(id, form, image);
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

    @PostMapping("/StockStats")
    public ResponseEntity<Map<String, Long>> getStockStats(@RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(productService.getStockStats(body));
    }

    // Đếm tổng số sản phẩm
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalProducts() {
        Long total = productService.getCountAllProducts();
        return ResponseEntity.ok(total);
    }

    // Đếm tổng số sản phẩm tồn kho
    @GetMapping("inProducts/count")
    public Long getCountInProducts() {
        return productService.getCountInProducts();
    }

    // Đếm tổng số sản phẩm hết hàng
    @GetMapping("outProducts/count")
    public Long getCountOutProducts() {
        return productService.getCountOutProducts();
    }

    // Lấy ra mã, tên và số lượng sản phẩm bán chạy nhất theo kiểu thời gian(unit
    // time)
    @GetMapping("oneBestSeller")
    public Map<String, Object> getBestSellerProductByUnitTime(@RequestParam String time) {
        return productService.getBestSellerProductByUnitTime(time);
    }

    @GetMapping("TopAndBotSellingCategories")
    public Map<String, Object> getTopAndBotSellingCategories(@RequestParam String time) {
        return productService.getTopAndBotSellingCategories(time);
    }

}