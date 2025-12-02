package com.example.jewelrystore.AddressServiceTest;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;
import com.example.jewelrystore.Implement.AddressImpl;
import com.example.jewelrystore.Mapper.AddressMapper;
import com.example.jewelrystore.Repository.AddressRepository;
import com.example.jewelrystore.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceFullSuite {

    @Mock AddressRepository addressRepository;
    @Mock AddressMapper addressMapper;
    @Mock UserRepository userRepository;
    @InjectMocks AddressImpl addressService;

    // --- GROUP 1: CREATE ---

    @Test // TC_WB_01
    void createMyAddress_DefaultTrue_ShouldResetOthers() {
        AddressCreateForm form = new AddressCreateForm(); form.setIs_defaut(true);
        User user = new User(); user.setId(1);
        Address address = new Address(); address.setIs_defaut(true);

        when(userRepository.findByUsername("hieu")).thenReturn(Optional.of(user));
        when(addressMapper.toEntity(form)).thenReturn(address);
        when(addressRepository.save(any())).thenReturn(address);

        addressService.createMyAddress(form, "hieu");

        verify(addressRepository).setAllDefaultFalseForUser(1); // Kiểm tra logic if
        verify(addressRepository).save(address);
    }

    @Test // TC_WB_02
    void createMyAddress_DefaultFalse_ShouldNotReset() {
        AddressCreateForm form = new AddressCreateForm(); form.setIs_defaut(false);
        User user = new User(); user.setId(1);
        Address address = new Address(); address.setIs_defaut(false);

        when(userRepository.findByUsername("hieu")).thenReturn(Optional.of(user));
        when(addressMapper.toEntity(form)).thenReturn(address);

        addressService.createMyAddress(form, "hieu");

        verify(addressRepository, never()).setAllDefaultFalseForUser(anyInt()); // Kiểm tra nhánh Else
        verify(addressRepository).save(address);
    }

    @Test // TC_WB_03
    void createMyAddress_UserNotFound_ThrowException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.createMyAddress(new AddressCreateForm(), "ghost"));
    }

    // --- GROUP 2: UPDATE (LOGIC PHỨC TẠP NHẤT) ---

    @Test // TC_WB_04: Update thành Default -> Reset cái cũ
    void updateMyAddresses_SetDefault_ShouldLoopAndReset() {
        Integer idToUpdate = 1;
        String username = "hieu";
        User user = new User(); user.setId(10); user.setUsername(username);

        // Address đang sửa (sẽ thành default)
        Address targetAddr = new Address(); targetAddr.setId(idToUpdate); targetAddr.setUser(user); targetAddr.setIs_defaut(true);

        // Address khác đang là default (cần bị reset)
        Address otherAddr = new Address(); otherAddr.setId(2); otherAddr.setIs_defaut(true);

        when(addressRepository.findById(idToUpdate)).thenReturn(Optional.of(targetAddr));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        // Mock list trả về để vòng lặp for chạy
        when(addressRepository.findAllByUserId(user.getId())).thenReturn(List.of(targetAddr, otherAddr));

        addressService.updateMyAddresses(idToUpdate, new AddressUpdateForm(), username);

        // Verify: otherAddr phải được set thành false và save lại
        assertFalse(otherAddr.isIs_defaut());
        verify(addressRepository).save(otherAddr);
    }

    @Test // TC_WB_05: Update thường
    void updateMyAddresses_Normal_NoReset() {
        Integer id = 1;
        User user = new User(); user.setId(10); user.setUsername("hieu");
        Address addr = new Address(); addr.setId(id); addr.setUser(user); addr.setIs_defaut(false);

        when(addressRepository.findById(id)).thenReturn(Optional.of(addr));
        when(userRepository.findByUsername("hieu")).thenReturn(Optional.of(user));

        addressService.updateMyAddresses(id, new AddressUpdateForm(), "hieu");

        verify(addressRepository, never()).findAllByUserId(anyInt()); // Không chạy vào logic reset
    }

    @Test // TC_WB_06: Sai chủ sở hữu
    void updateMyAddresses_WrongOwner_ThrowAccessDenied() {
        User owner = new User(); owner.setUsername("UserA");
        Address addr = new Address(); addr.setId(1); addr.setUser(owner);
        User hacker = new User(); hacker.setUsername("UserB");

        when(addressRepository.findById(1)).thenReturn(Optional.of(addr));
        when(userRepository.findByUsername("UserB")).thenReturn(Optional.of(hacker));

        assertThrows(AccessDeniedException.class,
                () -> addressService.updateMyAddresses(1, new AddressUpdateForm(), "UserB"));
    }

    @Test // TC_WB_07: Not Found
    void updateMyAddresses_NotFound_ThrowException() {
        when(addressRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> addressService.updateMyAddresses(999, new AddressUpdateForm(), "hieu"));
    }

    // --- GROUP 3: DELETE ---

    @Test // TC_WB_08
    void deleteMyAddress_CorrectOwner_Success() {
        User user = new User(); user.setUsername("hieu");
        Address addr = new Address(); addr.setUser(user);

        when(addressRepository.findById(1)).thenReturn(Optional.of(addr));

        addressService.deleteMyAddress(1, "hieu");

        verify(addressRepository).deleteById(1); // Kiểm tra logic if đúng
    }

    @Test // TC_WB_09
    void deleteMyAddress_WrongOwner_Fail() {
        User user = new User(); user.setUsername("hieu");
        Address addr = new Address(); addr.setUser(user);

        when(addressRepository.findById(1)).thenReturn(Optional.of(addr));

        assertThrows(AccessDeniedException.class, () -> addressService.deleteMyAddress(1, "hacker"));
        verify(addressRepository, never()).deleteById(anyInt()); // Kiểm tra logic else
    }

    @Test // TC_WB_10
    void deleteMyAddress_NotFound() {
        when(addressRepository.findById(9999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> addressService.deleteMyAddress(9999, "hieu"));
    }

    // --- GROUP 4: OTHERS ---

    @Test // TC_WB_11
    void findDefaultAddress_Found() {
        User user = new User(); user.setId(1);
        Address addr = new Address();

        when(userRepository.findByUsername("hieu")).thenReturn(Optional.of(user));
        when(addressRepository.findDefaultAddressByUserId(1)).thenReturn(Optional.of(addr));
        when(addressMapper.toAddressDTO(addr)).thenReturn(new AddressDTO());

        AddressDTO result = addressService.findDefaultAddress("hieu");
        assertNotNull(result);
    }

    @Test // TC_WB_12
    void updateAddress_Admin_IdExists() {
        Address addr = new Address();
        when(addressRepository.findById(1)).thenReturn(Optional.of(addr));

        addressService.updateAddress(1, new AddressUpdateForm());

        verify(addressRepository).save(addr);
    }

    @Test // TC_WB_13
    void updateAddress_Admin_IdNotExists() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        AddressDTO result = addressService.updateAddress(99, new AddressUpdateForm());

        assertNull(result);
        verify(addressRepository, never()).save(any());
    }
}