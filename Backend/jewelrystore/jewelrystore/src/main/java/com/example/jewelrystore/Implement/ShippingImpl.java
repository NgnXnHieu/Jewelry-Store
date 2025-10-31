package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.ShippingDTO;
import com.example.jewelrystore.Entity.Shipping;
import com.example.jewelrystore.Form.ShippingForm.ShippingCreateForm;
import com.example.jewelrystore.Form.ShippingForm.ShippingUpdateForm;
import com.example.jewelrystore.Mapper.ShippingMapper;
import com.example.jewelrystore.Repository.ShippingRepository;
import com.example.jewelrystore.Service.ShippingService;

@Service

public class ShippingImpl implements ShippingService {
    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ShippingDTO createShipping(ShippingCreateForm Form) {
        Shipping shipping = shippingMapper.toEntity(Form);
        shippingRepository.save(shipping);
        return shippingMapper.toShippingDTO(shipping);
    }

    @Override
    public ShippingDTO updateShipping(Integer id, ShippingUpdateForm Form) {
        Shipping existing = shippingRepository.findById(id).orElse(null);
        if (existing != null) {
            shippingMapper.updateShipping(Form, existing);
            shippingRepository.save(existing);
            return shippingMapper.toShippingDTO(existing);
        }
        return null;
    }

    @Override
    public Page<ShippingDTO> getAllShipping(Pageable pageable) {
        return shippingRepository.findAll(pageable).map(shippingMapper::toShippingDTO);
    }

    @Override
    public ShippingDTO getShippingById(Integer id) {
        return shippingRepository.findById(id).map(shippingMapper::toShippingDTO).orElse(null);
    }

    @Override
    public void deleteShipping(Integer id) {
        shippingRepository.deleteById(id);
    }

}
