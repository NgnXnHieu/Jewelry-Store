package com.example.jewelrystore.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;

public interface AddressService {
    AddressDTO create(AddressCreateForm addressCreateForm);

    AddressDTO createMyAddress(AddressCreateForm addressCreateForm, String username);

    List<AddressDTO> getAllAddressesByUsername(String username);

    AddressDTO updateAddress(Integer id, AddressUpdateForm addressUpdateForm);

    AddressDTO updateMyAddresses(Integer id, AddressUpdateForm addressUpdateForm, String username);

    Page<AddressDTO> getAllAddresses(Pageable pageable);

    AddressDTO getAddressById(Integer id);

    void deleteAddress(Integer id);

    void deleteMyAddress(Integer id, String username);

    AddressDTO findDefaultAddress(String username);

}
