package com.example.jewelrystore.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;

public interface ProductService {
    ProductDTO createProduct(ProductCreateForm Form, MultipartFile image, String username);

    ProductDTO updateProduct(Integer id, ProductUpdateForm Form, MultipartFile image);

    Page<ProductDTO> getAllProduct(Pageable pageable);

    ProductDTO getProductById(Integer id);

    void deleteProduct(Integer id);

    Page<ProductDTO> getRelatedProducts(Integer id, Pageable pageable);

    Page<ProductDTO> getProductsByCategoryId(Integer id, Pageable pageable);

    Page<BestSellerDTO> getBestSellerProduct(Pageable pageable);

    Page<ProductDTO> getProductsByStockStatus(String status, Pageable pageable);

    Long getTotalQuantity();

    Long countProductByQuantityBetween(Long min, Long max);

    Long countProductByQuantityBySpecificQuantity(Long quantity);

    Long countProductByQuantityGreaterSpecificQuantity(Long quantity);

    Long getCountAllProducts();

    Long getCountInProducts();

    Long getCountOutProducts();

    Map<String, Long> getStockStats(Map<String, Long> body);

    Map<String, Object> getBestSellerProductByUnitTime(String time);

    Map<String, Object> getTopAndBotSellingCategories(String time);

}
