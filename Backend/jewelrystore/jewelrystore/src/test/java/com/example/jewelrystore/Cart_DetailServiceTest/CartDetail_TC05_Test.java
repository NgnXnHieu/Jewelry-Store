package com.example.jewelrystore.Cart_DetailServiceTest;

import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Entity.Cart_Detail;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;
import com.example.jewelrystore.Implement.Cart_DetailImpl;
import com.example.jewelrystore.Repository.Cart_DetailRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartDetail_TC05_Test {

    @Mock
    private Cart_DetailRepository cart_DetailRepository;

    // Các mock khác cần thiết để khởi tạo Service mà không bị lỗi NullPointer
    // Dù trong logic TC05 có thể không dùng đến mapper, nhưng Service vẫn cần nó để Autowired
    @Mock private com.example.jewelrystore.Mapper.Cart_DetailMapper cart_DetailMapper;
    @Mock private com.example.jewelrystore.Repository.CartRepository cartRepository;
    @Mock private com.example.jewelrystore.Repository.UserRepository userRepository;

    @InjectMocks
    private Cart_DetailImpl cart_DetailService;

    @Test
    @DisplayName("TC_CD_05: Update Cart_Detail - Access Denied (Wrong Owner)")
    void updateCart_Detail_AccessDenied() {
        // --- 1. PREPARE DATA (MOCK) ---
        Integer cartDetailId = 10;
        String hackerName = "hacker"; // Người đang cố thực hiện hành động
        String ownerName = "owner";   // Chủ sở hữu thực sự của giỏ hàng

        // Giả lập User chủ sở hữu
        User ownerUser = new User();
        ownerUser.setUsername(ownerName);

        // Giả lập Cart thuộc về chủ sở hữu
        Cart cart = new Cart();
        cart.setUser(ownerUser);

        // Giả lập Cart_Detail thuộc về Cart đó
        Cart_Detail item = new Cart_Detail();
        item.setId(cartDetailId);
        item.setCart(cart);

        // --- 2. MOCK BEHAVIOR ---
        // Khi tìm ID, trả về item của "owner"
        when(cart_DetailRepository.findById(cartDetailId)).thenReturn(Optional.of(item));

        // --- 3. ACTION & ASSERT ---
        // Gọi hàm update với user là "hacker"
        // Mong đợi ném ra AccessDeniedException
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class, () -> {
            cart_DetailService.updateCart_Detail(cartDetailId, new Cart_DetailUpdateForm(), hackerName);
        });

        // Kiểm tra message lỗi (Optional)
        Assertions.assertEquals("You don't have permission to change quantity this item", exception.getMessage());
    }
}