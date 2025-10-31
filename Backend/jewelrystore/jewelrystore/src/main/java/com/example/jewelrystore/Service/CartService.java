package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.CartDTO;
import com.example.jewelrystore.Form.CartForm.CartCreateForm;
import com.example.jewelrystore.Form.CartForm.CartUpdateForm;

public interface CartService {
    CartDTO createCart(CartCreateForm cartCreateForm);

    CartDTO updateCart(Integer id, CartUpdateForm cartUpdateForm);

    Page<CartDTO> getAllCart(Pageable pageable);

    CartDTO getCartById(Integer id);

    void deleteCart(Integer id);

}