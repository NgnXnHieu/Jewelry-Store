package com.example.jewelrystore.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.Cart_DetailDTO;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailCreateForm;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;

public interface Cart_DetailService {
    Cart_DetailDTO createCart_Detail(String userName, Cart_DetailCreateForm cart_DetailCreateForm);

    Cart_DetailDTO updateCart_Detail(Integer id, Cart_DetailUpdateForm cart_DetailUpdateForm, String userName);

    Page<Cart_DetailDTO> getAll(Pageable pageable);

    Cart_DetailDTO getCart_DetailById(Integer id);

    void deleteCart_Detail(Integer id, String userName);

    List<Cart_DetailDTO> getCart_DetailsByUserName(String userName, Integer cursor, int limit);
}
