package com.example.jewelrystore;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;
import com.example.jewelrystore.Implement.AddressImpl;
import com.example.jewelrystore.Mapper.AddressMapper;
import com.example.jewelrystore.Repository.AddressRepository;
import com.example.jewelrystore.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AddressImplTest {

    @Autowired
    private AddressImpl addressService; // Class cần test

    @MockitoBean
    private AddressRepository addressRepository; // Giả lập database Address

    @MockitoBean
    private UserRepository userRepository; // Giả lập database User

    @MockitoBean
    private AddressMapper addressMapper; // Giả lập Mapper

    // --- TEST CASE 1: KIỂM TRA BẢO MẬT (ACCESS DENIED) ---
    @Test
    public void testUpdateMyAddresses_AccessDenied() {
        // 1. Giả lập dữ liệu
        String hackerName = "hacker123";
        String ownerName = "owner123";
        Integer addressId = Integer.valueOf(1);

        User owner = new User();
        owner.setUsername(ownerName);

        Address address = new Address();
        address.setId(addressId);
        address.setUser(owner); // Địa chỉ này thuộc về owner

        User hacker = new User();
        hacker.setUsername(hackerName);

        // 2. Dạy Mockito cách trả lời
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(hackerName)).thenReturn(Optional.of(hacker));

        // 3. Thực thi và Assert: Hacker cố sửa địa chỉ của Owner
        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            addressService.updateMyAddresses(addressId, new AddressUpdateForm(), hackerName);
        });

        assertEquals("You don't have permission to change this Address", exception.getMessage());
    }

    // --- TEST CASE 2: KIỂM TRA LOGIC SET DEFAULT (VÒNG LẶP) ---
    @Test
    public void testUpdateMyAddresses_SetDefaultLogic() {
        // Kịch bản: User muốn sửa Address A thành Mặc định.
        // Hệ thống phải tự động tìm Address B (đang là mặc định cũ) để set thành False.

        // 1. Setup dữ liệu
        String username = "user1";
        Integer idToUpdate = (Integer) 10;
        Integer oldDefaultId = (Integer) 20;

        User user = new User();
        user.setId(Integer.valueOf(1));
        user.setUsername(username);

        // Địa chỉ đang cần sửa (Sẽ thành mặc định)
        Address addressToUpdate = new Address();
        addressToUpdate.setId(idToUpdate);
        addressToUpdate.setUser(user);
        addressToUpdate.setIs_defaut(true); // Form gửi lên yêu cầu set true

        // Địa chỉ mặc định CŨ (Cần bị tắt đi)
        Address oldDefaultAddress = new Address();
        oldDefaultAddress.setId(oldDefaultId);
        oldDefaultAddress.setIs_defaut(true); // Đang là true
        oldDefaultAddress.setUser(user);

        List<Address> listAddress = new ArrayList<>();
        listAddress.add(addressToUpdate);
        listAddress.add(oldDefaultAddress);

        AddressUpdateForm form = new AddressUpdateForm(); // Form dummy

        // 2. Mocking
        when(addressRepository.findById(idToUpdate)).thenReturn(Optional.of(addressToUpdate));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(addressRepository.findAllByUserId(user.getId())).thenReturn(listAddress);

        // Mock Mapper để tránh lỗi NullPointer khi return
        when(addressMapper.toAddressDTO(any())).thenReturn(new AddressDTO());

        // 3. Thực thi
        addressService.updateMyAddresses(idToUpdate, form, username);

        // 4. VERIFY (Kiểm thử hộp trắng quan trọng nhất chỗ này)
        // Kiểm tra xem code có chạy vào đoạn loop để save cái địa chỉ cũ không?
        verify(addressRepository, times(1)).save(oldDefaultAddress);

        // Assert logic: Địa chỉ cũ phải bị set về false
        assertFalse(oldDefaultAddress.isIs_defaut(), "Địa chỉ cũ phải bị hủy trạng thái mặc định");
    }
}