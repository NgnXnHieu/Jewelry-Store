package com.example.jewelrystore.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jewelrystore.DTO.AddressDTO;
import com.example.jewelrystore.Form.AddressForm.AddressCreateForm;
import com.example.jewelrystore.Form.AddressForm.AddressUpdateForm;
import com.example.jewelrystore.Service.AddressService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/addresses")
@Validated
public class AddressController {
    @Autowired
    AddressService service;

    @GetMapping
    public Page<AddressDTO> getAll(Pageable pageable) {
        return service.getAllAddresses(pageable);
    }

    @GetMapping("/{id}")
    public AddressDTO getAddressById(@PathVariable Integer id) {
        return service.getAddressById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid AddressCreateForm addressCreateForm) {
        AddressDTO created = service.create(addressCreateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody AddressUpdateForm addressUpdateForm) {
        AddressDTO updated = service.updateAddress(id, addressUpdateForm);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteAddress(id);
        return "Xóa thành công";
    }

    @PostMapping("/myAddress")
    public ResponseEntity createMyAddress(@RequestBody @Valid AddressCreateForm addressCreateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // return null; // hoặc ném exception 401
            throw new AccessDeniedException("You don't have permission to create address");
        }
        AddressDTO created = service.createMyAddress(addressCreateForm, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @GetMapping("/myAddress")
    public List<AddressDTO> getAllAddressesByUsername(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // return null; // hoặc ném exception 401
            throw new AccessDeniedException("You don't have permission to change quantity this item");

        }
        return service.getAllAddressesByUsername(userDetails.getUsername());
    }

    @PutMapping("/myAddress/{id}")
    public ResponseEntity updateMyAddress(@PathVariable Integer id,
            @Valid @RequestBody AddressUpdateForm addressUpdateForm,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // return null; // hoặc ném exception 401
            throw new AccessDeniedException("You don't have permission to change quantity this item");
        }
        AddressDTO updated = service.updateMyAddresses(id, addressUpdateForm, userDetails.getUsername());
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("myAddress/{id}")
    public String deleteMyAddress(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            service.deleteMyAddress(id, userDetails.getUsername());
            return "Xóa thành công";
        }
        return "Yêu cầu đăng nhập";

    }

    @GetMapping("defaultAddress")
    public AddressDTO getDefaultAddress(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return service.findDefaultAddress(userDetails.getUsername());
        } else
            throw new AccessDeniedException("You need to login");

    }

}
