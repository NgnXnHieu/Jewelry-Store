package com.example.jewelrystore.Implement;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Mapper.ProductMapper;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Service.ProductService;

@Service

public class ProductImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductCreateForm Form) {
        Product product = productMapper.toEntity(Form);
        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Integer id, ProductUpdateForm Form) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            productMapper.updateProduct(Form, existing);
            productRepository.save(existing);
            return productMapper.toProductDTO(existing);
        }
        return null;
    }

    @Override
    public Page<ProductDTO> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toProductDTO);
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        return productRepository.findById(id).map(productMapper::toProductDTO).orElse(null);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> getRelatedProducts(Integer id, Pageable pageable) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Integer categoryId = product.getCategory().getId();

        Page<Product> relatedProducts = productRepository.findByCategoryIdAndIdNot(categoryId, id, pageable);

        return relatedProducts.map(productMapper::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategoryId(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(productMapper::toProductDTO);
    }

    @Override
    public Page<BestSellerDTO> getBestSellerProduct(Pageable pageable) {
        return productRepository.findBestSellerProducts(pageable);
    }

}
