package com.example.jewelrystore.Service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;

public interface ProductService {
    ProductDTO createProduct(ProductCreateForm Form);

    ProductDTO updateProduct(Integer id, ProductUpdateForm Form);

    Page<ProductDTO> getAllProduct(Pageable pageable);

    ProductDTO getProductById(Integer id);

    void deleteProduct(Integer id);

    Page<ProductDTO> getRelatedProducts(Integer id, Pageable pageable);

    Page<ProductDTO> getProductsByCategoryId(Integer id, Pageable pageable);

    Page<BestSellerDTO> getBestSellerProduct(Pageable pageable);

}
