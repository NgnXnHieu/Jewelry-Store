package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.Cart_DetailDTO;
import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Entity.Cart_Detail;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailCreateForm;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;
import com.example.jewelrystore.Mapper.Cart_DetailMapper;
import com.example.jewelrystore.Repository.CartRepository;
import com.example.jewelrystore.Repository.Cart_DetailRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.Cart_DetailService;

import jakarta.persistence.EntityNotFoundException;

@Service

public class Cart_DetailImpl implements Cart_DetailService {
    @Autowired
    private Cart_DetailRepository cart_DetailRepository;
    @Autowired
    private Cart_DetailMapper cart_DetailMapper;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart_DetailDTO createCart_Detail(String userName, Cart_DetailCreateForm cart_DetailCreateForm) {
        // Kiểm tra xem idProduct đã có trong các cart_detail chưa
        // Đã có --> tăng số lượng sản phẩm đó lên 1
        // Chưa --> thêm mới
        // Sửa lại số lượng, giá của cart
        Cart cart = cartRepository.findByUserUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Kiểm tra user hiện tại và set idCart bằng backend
        cart_DetailCreateForm.setCartId(cart.getId());
        for (Cart_Detail item : cart.getCartDetails()) {
            if (item.getProduct().getId() == cart_DetailCreateForm.getProductId()) {
                item.setQuantity(item.getQuantity() + (Long) cart_DetailCreateForm.getQuantity());
                cart_DetailRepository.save(item);
                return cart_DetailMapper.toCart_DetailDTO(item);
            }
        }
        Cart_Detail cart_Detail = cart_DetailMapper.toEntity(cart_DetailCreateForm);
        cart_DetailRepository.save(cart_Detail);
        return cart_DetailMapper.toCart_DetailDTO(cart_Detail);
    }

    @Override
    public Cart_DetailDTO updateCart_Detail(Integer id, Cart_DetailUpdateForm cart_DetailUpdateForm, String userName) {
        Cart_Detail cart_Detail = cart_DetailRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Item need change quantity that not found"));
        if (cart_Detail.getCart().getUser().getUsername().equals(userName)) {
            if (cart_Detail != null) {
                cart_DetailMapper.updateCart_Detail(cart_DetailUpdateForm, cart_Detail);
                cart_DetailRepository.save(cart_Detail);
                return cart_DetailMapper.toCart_DetailDTO(cart_Detail);
            }
            return null;
        } else {
            throw new AccessDeniedException("You don't have permission to change quantity this item");

        }

    }

    @Override
    public Page<Cart_DetailDTO> getAll(Pageable pageable) {
        return cart_DetailRepository.findAll(pageable).map(cart_DetailMapper::toCart_DetailDTO);
    }

    @Override
    public Cart_DetailDTO getCart_DetailById(Integer id) {
        return cart_DetailRepository.findById(id).map(cart_DetailMapper::toCart_DetailDTO).orElse(null);
    }

    @Override
    public void deleteCart_Detail(Integer id, String userName) {
        Cart_Detail cart_Detail = cart_DetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Removed Item not found"));
        if (cart_Detail.getCart().getUser().getUsername().equals(userName)) {
            cart_DetailRepository.deleteById(id);
        } else
            throw new AccessDeniedException("You don't have permission to delete this item");

    }

    @Override
    public Page<Cart_DetailDTO> getCart_DetailsByUserName(String userName, Pageable pageable) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        Integer cartId = cart.getId();
        return cart_DetailRepository.findByCartId(cartId, pageable).map(cart_DetailMapper::toCart_DetailDTO);
    }

}
