package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.ShippingDTO;
import com.example.jewelrystore.Form.ShippingForm.ShippingCreateForm;
import com.example.jewelrystore.Form.ShippingForm.ShippingUpdateForm;

public interface ShippingService {
    ShippingDTO createShipping(ShippingCreateForm Form);

    ShippingDTO updateShipping(Integer id, ShippingUpdateForm Form);

    Page<ShippingDTO> getAllShipping(Pageable pageable);

    ShippingDTO getShippingById(Integer id);

    void deleteShipping(Integer id);
}
