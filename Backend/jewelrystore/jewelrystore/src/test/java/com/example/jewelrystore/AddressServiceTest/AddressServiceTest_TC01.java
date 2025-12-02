package com.example.jewelrystore.AddressServiceTest;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Implement.AddressImpl;
import com.example.jewelrystore.Mapper.AddressMapper;
import com.example.jewelrystore.Repository.AddressRepository;
import com.example.jewelrystore.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest_TC01 {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressImpl addressService;

    @Test
    void TC_WB_01_createMyAddress_WhenDefaultTrue_ShouldResetOthers() {
        // --- 1. ARRANGE (Chuẩn bị dữ liệu giả) ---
        String username = "hieu123123";
        Integer userId = 1;

        // Form đầu vào có isDefault = true
        AddressCreateForm form = new AddressCreateForm();
        form.setIs_defaut(true);

        // User giả
        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        // Entity giả (Sau khi map)
        Address addressEntity = new Address();
        addressEntity.setIs_defaut(true);

        // Giả lập hành vi của các dependencies
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(addressMapper.toEntity(form)).thenReturn(addressEntity);
        when(addressRepository.save(any(Address.class))).thenReturn(addressEntity);
        when(addressMapper.toAddressDTO(any(Address.class))).thenReturn(new AddressDTO());

        // --- 2. ACT (Gọi hàm cần test) ---
        addressService.createMyAddress(form, username);

        // --- 3. ASSERT / VERIFY (Kiểm tra logic bên trong) ---

        // Kiểm tra xem hàm setAllDefaultFalseForUser có được gọi không? (Đây là logic quan trọng của nhánh If)
        verify(addressRepository, times(1)).setAllDefaultFalseForUser(userId);

        // Kiểm tra xem hàm save có được gọi không?
        verify(addressRepository, times(1)).save(addressEntity);

        System.out.println("TC_WB_01: [PASS] Đã gọi hàm reset default trước khi lưu.");
    }
}