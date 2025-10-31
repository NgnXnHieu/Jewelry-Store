package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.CartDTO;
import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Form.CartForm.CartCreateForm;
import com.example.jewelrystore.Form.CartForm.CartUpdateForm;
import com.example.jewelrystore.Mapper.CartMapper;
import com.example.jewelrystore.Repository.CartRepository;
import com.example.jewelrystore.Service.CartService;

@Service

public class CartImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartMapper cartMapper;

    @Override
    public CartDTO createCart(CartCreateForm cartCreateForm) {
        Cart cart = cartMapper.toEntity(cartCreateForm);
        cartRepository.save(cart);
        return cartMapper.toCartDTO(cart);
    }

    @Override
    public CartDTO updateCart(Integer id, CartUpdateForm cartUpdateForm) {
        Cart existing = cartRepository.findById(id).orElse(null);
        if (existing != null) {
            cartMapper.updateCart(cartUpdateForm, existing);
            cartRepository.save(existing);
            return cartMapper.toCartDTO(existing);
        }
        return null;
    }

    @Override
    public Page<CartDTO> getAllCart(Pageable pageable) {
        return cartRepository.findAll(pageable).map(cartMapper::toCartDTO);
    }

    @Override
    public CartDTO getCartById(Integer id) {
        return cartRepository.findById(id).map(cartMapper::toCartDTO).orElse(null);
    }

    @Override
    public void deleteCart(Integer id) {
        cartRepository.deleteById(id);
    }

}
