package com.example.jewelrystore.ProductServiceTest;

import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Implement.ProductImpl;
import com.example.jewelrystore.Repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest_TC07 {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductImpl productService;

    @Test
    void TC_PR_07_updateProduct_IdNotFound_ShouldReturnNull() {
        // --- 1. ARRANGE (Chuẩn bị) ---
        Integer nonExistentId = 999;
        ProductUpdateForm form = new ProductUpdateForm();
        MultipartFile image = null; // Không quan trọng vì không tìm thấy ID

        // Giả lập Repository trả về Empty (Không tìm thấy)
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // --- 2. ACT (Thực thi) ---
        ProductDTO result = productService.updateProduct(nonExistentId, form, image);

        // --- 3. ASSERT / VERIFY (Kiểm tra) ---
        // Mong đợi kết quả là null
        assertNull(result);

        // Quan trọng: Đảm bảo hàm save() KHÔNG BAO GIỜ được gọi
        verify(productRepository, never()).save(any());

        System.out.println("TC_PR_07: [PASS] Trả về null và không lưu vào DB khi ID không tồn tại.");
    }
}