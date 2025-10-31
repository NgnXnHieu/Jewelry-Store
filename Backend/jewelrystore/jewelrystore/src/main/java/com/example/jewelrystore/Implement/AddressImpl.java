package com.example.jewelrystore.Implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;
import com.example.jewelrystore.Mapper.AddressMapper;
import com.example.jewelrystore.Repository.AddressRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.AddressService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class AddressImpl implements AddressService {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    UserRepository userRepository;
    // @PersistenceContext
    // private EntityManager entityManager;

    @Override
    public Page<AddressDTO> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable).map(addressMapper::toAddressDTO);
    }

    @Override
    public AddressDTO getAddressById(Integer id) {
        return addressRepository.findById(id).map(addressMapper::toAddressDTO).orElse(null);
    }

    @Override
    public AddressDTO create(AddressCreateForm addressCreateForm) {
        Address address = addressMapper.toEntity(addressCreateForm);
        addressRepository.save(address);
        return addressMapper.toAddressDTO(address);
    }

    @Override
    public AddressDTO updateAddress(Integer id, AddressUpdateForm addressUpdateForm) {
        Address existing = addressRepository.findById(id).orElse(null);
        if (existing != null) {
            addressMapper.updateAddress(addressUpdateForm, existing);
            addressRepository.save(existing);
            return addressMapper.toAddressDTO(existing);
        }
        return null;
    }

    @Override
    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }

    // Bên client
    @Override
    public List<AddressDTO> getAllAddressesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findAddressByUserId(user.getId()).stream().map(addressMapper::toAddressDTO).toList();
    }

    @Override
    // @Transactional
    public AddressDTO updateMyAddresses(Integer id, AddressUpdateForm addressUpdateForm, String username) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
        String name = address.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (name.equals(username)) {
            if (address != null) {
                addressMapper.updateAddress(addressUpdateForm, address);
                System.out.println(address.isIs_defaut());
                if (address.isIs_defaut()) {
                    List<Address> userAddresses = addressRepository.findAllByUserId(user.getId());
                    Integer addressId = address.getId();
                    for (Address a : userAddresses) {
                        if (!a.getId().equals(addressId) && a.isIs_defaut()) {
                            a.setIs_defaut(false);
                            addressRepository.save(a);
                        }
                    }
                }
                return addressMapper.toAddressDTO(address);
            }
            return null;
        } else
            throw new AccessDeniedException("You don't have permission to change this Address");

    }

    @Override
    public AddressDTO createMyAddress(AddressCreateForm addressCreateForm, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressMapper.toEntity(addressCreateForm);
        address.setUser(user);
        if (address.isIs_defaut()) {
            addressRepository.setAllDefaultFalseForUser(user.getId());
        }
        addressRepository.save(address);
        return addressMapper.toAddressDTO(address);
    }

    @Override
    public void deleteMyAddress(Integer id, String username) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address need to delete that not found"));
        if (address.getUser().getUsername().equals(username)) {
            addressRepository.deleteById(id);
        } else
            throw new AccessDeniedException("You don't have permission to change this Address");

    }

    // Tìm địa chỉ mặc định
    @Override
    public AddressDTO findDefaultAddress(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        return addressRepository.findDefaultAddressByUserId(user.getId()).map(addressMapper::toAddressDTO)
                .orElseThrow(() -> new RuntimeException("Default Address not found"));

    }

}
