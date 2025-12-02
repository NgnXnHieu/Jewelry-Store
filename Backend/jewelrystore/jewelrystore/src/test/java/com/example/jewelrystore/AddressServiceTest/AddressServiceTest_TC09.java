package com.example.jewelrystore.AddressServiceTest;

import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Implement.AddressImpl;
import com.example.jewelrystore.Repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest_TC09 {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressImpl addressService;

    @Test
    void TC_WB_09_deleteMyAddress_WrongOwner_ShouldThrowException() {
        // --- 1. ARRANGE ---
        Integer addressId = 100;
        String ownerUsername = "hieu123123";
        String hackerUsername = "hacker";

        // Tạo địa chỉ thuộc về Hieu
        User owner = new User();
        owner.setUsername(ownerUsername);

        Address address = new Address();
        address.setId(addressId);
        address.setUser(owner);

        // Giả lập repo tìm thấy địa chỉ này
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // --- 2. ACT & ASSERT (Hành động và Kiểm tra lỗi) ---

        // Mong đợi: Ném ra AccessDeniedException khi Hacker cố xóa
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class, () -> {
            addressService.deleteMyAddress(addressId, hackerUsername);
        });

        // --- 3. VERIFY (Kiểm tra logic dòng lệnh) ---

        // Kiểm tra thông báo lỗi
        Assertions.assertEquals("You don't have permission to change this Address", exception.getMessage());

        // QUAN TRỌNG: Kiểm tra hàm deleteById KHÔNG BAO GIỜ được gọi
        verify(addressRepository, never()).deleteById(anyInt());

        System.out.println("TC_WB_09: [PASS] Hệ thống đã chặn Hacker và ném ngoại lệ thành công.");
    }
}