package com.example.jewelrystore.Cart_DetailServiceTest;

import com.example.jewelrystore.DTO.Cart_DetailDTO;
import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Entity.Cart_Detail;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailCreateForm;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;
import com.example.jewelrystore.Implement.Cart_DetailImpl;
import com.example.jewelrystore.Mapper.Cart_DetailMapper;
import com.example.jewelrystore.Repository.CartRepository;
import com.example.jewelrystore.Repository.Cart_DetailRepository;
import com.example.jewelrystore.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartDetail_Full_Test {

    @Mock Cart_DetailRepository cart_DetailRepository;
    @Mock Cart_DetailMapper cart_DetailMapper;
    @Mock CartRepository cartRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    Cart_DetailImpl cart_DetailService;

    // ==========================================
    // GROUP 1: CREATE (Tạo mới / Cộng dồn)
    // ==========================================

    @Test
    @DisplayName("TC_CD_01: Create New Item - Success")
    void createCart_Detail_NewItem_Success() {
        String username = "user1";
        Cart_DetailCreateForm form = new Cart_DetailCreateForm();
        form.setProductId(1);
        form.setQuantity(2L);

        Cart cart = new Cart();
        cart.setId(100);
        cart.setCartDetails(new ArrayList<>()); // List rỗng để không trigger cộng dồn

        Cart_Detail newItem = new Cart_Detail();

        when(cartRepository.findByUserUsername(username)).thenReturn(Optional.of(cart));
        when(cart_DetailMapper.toEntity(form)).thenReturn(newItem);
        when(cart_DetailRepository.save(newItem)).thenReturn(newItem);
        when(cart_DetailMapper.toCart_DetailDTO(newItem)).thenReturn(new Cart_DetailDTO());

        Cart_DetailDTO result = cart_DetailService.createCart_Detail(username, form);

        Assertions.assertNotNull(result);
        verify(cart_DetailRepository).save(newItem); // Verify save item mới
    }

    @Test
    @DisplayName("TC_CD_02: Create Existing Item - Accumulate Quantity")
    void createCart_Detail_ExistingItem_Success() {
        String username = "user1";
        Cart_DetailCreateForm form = new Cart_DetailCreateForm();
        form.setProductId(1);
        form.setQuantity(3L);

        // Mock item đã tồn tại
        Product product = new Product();
        product.setId(1);
        Cart_Detail existingItem = new Cart_Detail();
        existingItem.setProduct(product);
        existingItem.setQuantity(2L);

        Cart cart = new Cart();
        cart.setCartDetails(List.of(existingItem)); // Giỏ hàng đã có sp này

        when(cartRepository.findByUserUsername(username)).thenReturn(Optional.of(cart));
        when(cart_DetailRepository.save(existingItem)).thenReturn(existingItem);
        when(cart_DetailMapper.toCart_DetailDTO(existingItem)).thenReturn(new Cart_DetailDTO());

        cart_DetailService.createCart_Detail(username, form);

        Assertions.assertEquals(5L, existingItem.getQuantity()); // 2 + 3 = 5
        verify(cart_DetailMapper, never()).toEntity(any()); // Không được tạo mới
    }

    @Test
    @DisplayName("TC_CD_03: Create - User Not Found")
    void createCart_Detail_UserNotFound() {
        when(cartRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            cart_DetailService.createCart_Detail("ghost", new Cart_DetailCreateForm());
        });
    }

    // ==========================================
    // GROUP 2: UPDATE (Cập nhật)
    // ==========================================

    @Test
    @DisplayName("TC_CD_04: Update - Success (Owner)")
    void updateCart_Detail_Success() {
        Integer id = 1;
        String username = "owner";

        User user = new User();
        user.setUsername(username);
        Cart cart = new Cart();
        cart.setUser(user);
        Cart_Detail item = new Cart_Detail();
        item.setCart(cart);

        when(cart_DetailRepository.findById(id)).thenReturn(Optional.of(item));
        when(cart_DetailRepository.save(item)).thenReturn(item);
        when(cart_DetailMapper.toCart_DetailDTO(item)).thenReturn(new Cart_DetailDTO());

        Cart_DetailDTO result = cart_DetailService.updateCart_Detail(id, new Cart_DetailUpdateForm(), username);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("TC_CD_05: Update - Access Denied")
    void updateCart_Detail_AccessDenied() {
        Integer id = 1;
        User user = new User();
        user.setUsername("owner");
        Cart cart = new Cart();
        cart.setUser(user);
        Cart_Detail item = new Cart_Detail();
        item.setCart(cart);

        when(cart_DetailRepository.findById(id)).thenReturn(Optional.of(item));

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            cart_DetailService.updateCart_Detail(id, new Cart_DetailUpdateForm(), "hacker");
        });
    }

    @Test
    @DisplayName("TC_CD_06: Update - Item Not Found")
    void updateCart_Detail_NotFound() {
        when(cart_DetailRepository.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            cart_DetailService.updateCart_Detail(999, new Cart_DetailUpdateForm(), "any");
        });
    }

    // ==========================================
    // GROUP 3: DELETE (Xóa)
    // ==========================================

    @Test
    @DisplayName("TC_CD_07: Delete - Success")
    void deleteCart_Detail_Success() {
        Integer id = 1;
        String username = "owner";
        User user = new User();
        user.setUsername(username);
        Cart cart = new Cart();
        cart.setUser(user);
        Cart_Detail item = new Cart_Detail();
        item.setCart(cart);

        when(cart_DetailRepository.findById(id)).thenReturn(Optional.of(item));

        cart_DetailService.deleteCart_Detail(id, username);
        verify(cart_DetailRepository).deleteById(id);
    }

    @Test
    @DisplayName("TC_CD_08: Delete - Access Denied")
    void deleteCart_Detail_AccessDenied() {
        Integer id = 1;
        User user = new User();
        user.setUsername("owner");
        Cart cart = new Cart();
        cart.setUser(user);
        Cart_Detail item = new Cart_Detail();
        item.setCart(cart);

        when(cart_DetailRepository.findById(id)).thenReturn(Optional.of(item));

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            cart_DetailService.deleteCart_Detail(id, "hacker");
        });
    }

    @Test
    @DisplayName("TC_CD_09: Delete - Not Found")
    void deleteCart_Detail_NotFound() {
        when(cart_DetailRepository.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cart_DetailService.deleteCart_Detail(999, "any");
        });
    }

    // ==========================================
    // GROUP 4: READ (Lấy dữ liệu)
    // ==========================================

    @Test
    @DisplayName("TC_CD_10: Get List By Username - Success")
    void getCart_DetailsByUserName_Success() {
        String username = "user1";
        User user = new User();
        user.setId(1);
        Cart cart = new Cart();
        cart.setId(100);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        when(cart_DetailRepository.getByCartIdByCursor(eq(100), any(), any()))
                .thenReturn(List.of(new Cart_Detail()));
        when(cart_DetailMapper.toCart_DetailDTO(any())).thenReturn(new Cart_DetailDTO());

        List<Cart_DetailDTO> list = cart_DetailService.getCart_DetailsByUserName(username, 0, 10);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TC_CD_11: Get List - User Not Found")
    void getCart_DetailsByUserName_UserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            cart_DetailService.getCart_DetailsByUserName("ghost", 0, 10);
        });
    }

    @Test
    @DisplayName("TC_CD_12: Get List - Cart Not Found")
    void getCart_DetailsByUserName_CartNotFound() {
        User user = new User();
        user.setId(1);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            cart_DetailService.getCart_DetailsByUserName("user1", 0, 10);
        });
    }

    @Test
    @DisplayName("TC_CD_13: Get All (Admin) - Success")
    void getAll_Success() {
        Pageable pageable = Pageable.unpaged();
        when(cart_DetailRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Cart_Detail())));
        when(cart_DetailMapper.toCart_DetailDTO(any())).thenReturn(new Cart_DetailDTO());

        Page<Cart_DetailDTO> page = cart_DetailService.getAll(pageable);
        Assertions.assertEquals(1, page.getTotalElements());
    }

    @Test
    @DisplayName("TC_CD_14: Get By ID - Found")
    void getCart_DetailById_Found() {
        when(cart_DetailRepository.findById(1)).thenReturn(Optional.of(new Cart_Detail()));
        when(cart_DetailMapper.toCart_DetailDTO(any())).thenReturn(new Cart_DetailDTO());

        Cart_DetailDTO result = cart_DetailService.getCart_DetailById(1);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("TC_CD_15: Get By ID - Not Found")
    void getCart_DetailById_NotFound() {
        when(cart_DetailRepository.findById(999)).thenReturn(Optional.empty());

        Cart_DetailDTO result = cart_DetailService.getCart_DetailById(999);
        Assertions.assertNull(result);
    }
}