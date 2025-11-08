package com.example.jewelrystore.Implement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Entity.Inventory_History;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Mapper.ProductMapper;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.ProductService;

import net.coobird.thumbnailator.Thumbnails;

@Service

public class ProductImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserRepository userRepository;

    // @Override
    // public ProductDTO createProduct(ProductCreateForm Form, MultipartFile image)
    // {
    // String imagePath = null;
    // String image_url = Form.getImage_url();
    // try {
    // if (image != null && !image.isEmpty()) {
    // // Lưu file vào thư mục server, ví dụ: "uploads/"
    // String uploadDir = "D:\\DACN\\Picture";
    // // Lưu dưới dạng thời gian + tên file để tránh trùng tên
    // String fileName = System.currentTimeMillis() + "_" +
    // image.getOriginalFilename();
    // // Tạo đường dẫn đầy đủ tối file
    // Path filePath = Paths.get(uploadDir, fileName);
    // // Tạo các thư mục cha nếu chưa tồn tại
    // Files.createDirectories(filePath.getParent());
    // // Lưu file vào ổ cứng
    // Files.copy(image.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);
    // imagePath = fileName; // lưu tên file vào database
    // } else if (image_url != null && !image_url.isEmpty()) {
    // imagePath = image_url; // dùng link trực tiếp
    // }

    // // Tạo Product entity
    // Product product = productMapper
    // .toEntity(Form);
    // product.setImage_url(imagePath);

    // productRepository.save(product);

    // return productMapper.toProductDTO(product);

    // } catch (IOException e) {
    // e.printStackTrace();
    // return null;
    // }

    // }

    @Override
    public ProductDTO createProduct(ProductCreateForm Form, MultipartFile image, String username) {
        String imagePath = null;
        String image_url = Form.getImage_url();

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = "D:\\DACN\\Picture";
                Files.createDirectories(Paths.get(uploadDir));

                // Tên file: thời gian + tên gốc
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                // ✅ Resize ảnh về kích thước 500x500 và lưu xuống
                Thumbnails.of(image.getInputStream())
                        .size(700, 700)
                        .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                        .outputQuality(0.85) // giảm dung lượng, 85% chất lượng
                        .toFile(filePath.toFile());

                imagePath = fileName;

            } else if (image_url != null && !image_url.isEmpty()) {
                imagePath = image_url; // dùng link nếu có
            }

            // 🔹 Tạo entity và lưu DB
            Product product = productMapper.toEntity(Form);
            product.setImage_url(imagePath);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Inventory_History inventory_History = new Inventory_History();
            inventory_History.setUser(user);
            Long quantity = Form.getQuantity();
            inventory_History.setImportQuantity(quantity);
            inventory_History.setCurrentQuantity(quantity);
            inventory_History.setProduct(product);
            List<Inventory_History> histories = new ArrayList<Inventory_History>();
            histories.add(inventory_History);
            product.setInventory_Histories(histories);
            productRepository.save(product);
            return productMapper.toProductDTO(product);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
        }
    }

    @Override
    public ProductDTO updateProduct(Integer id, ProductUpdateForm Form, MultipartFile image) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            productMapper.updateProduct(Form, existing);
            String imagePath = null;
            String image_url = Form.getImage_url();

            try {
                if (image != null && !image.isEmpty()) {
                    String uploadDir = "D:\\DACN\\Picture";
                    Files.createDirectories(Paths.get(uploadDir));

                    // Tên file: thời gian + tên gốc
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // ✅ Resize ảnh về kích thước 700x700 và lưu xuống
                    Thumbnails.of(image.getInputStream())
                            .size(700, 700)
                            .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                            .outputQuality(0.85) // giảm dung lượng, 85% chất lượng
                            .toFile(filePath.toFile());

                    imagePath = fileName;

                } else if (image_url != null && !image_url.isEmpty()) {
                    imagePath = image_url; // dùng link nếu có
                }

                // Lưu lại sản phẩm
                if (imagePath != null && !imagePath.isEmpty()) {
                    existing.setImage_url(imagePath);
                }
                existing.setImage_url(imagePath);
                productRepository.save(existing);
                return productMapper.toProductDTO(existing);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
            }
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

    @Override
    public Page<ProductDTO> getProductsByStockStatus(String status, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        if (status.equalsIgnoreCase("In")) {
            return productRepository.findByQuantityGreaterThan(0L, sortedPageable).map(productMapper::toProductDTO);
        } else if (status.equalsIgnoreCase("Low")) {
            return productRepository.findByQuantityGreaterThanAndQuantityLessThan(0L, 21L, sortedPageable)
                    .map(productMapper::toProductDTO);
        } else {
            return productRepository.findByQuantity(0L, sortedPageable).map(productMapper::toProductDTO);
        }

    }

    @Override
    public Long getTotalQuantity() {
        return productRepository.getTotalQuantity();

    }

    @Override
    public Long countProductByQuantityBetween(Long min, Long max) {
        return productRepository.countProductByQuantityBetween(min, max);

    }

    @Override
    public Long countProductByQuantityBySpecificQuantity(Long quantity) {
        return productRepository.countProductByQuantityBySpecificQuantity(quantity);
    }

    @Override
    public Long countProductByQuantityGreaterSpecificQuantity(Long quantity) {
        return productRepository.countProductByQuantityGreaterSpecificQuantity(quantity);
    }

    @Override
    public Long getCountAllProducts() {
        return productRepository.countAllProducts();
    }
}