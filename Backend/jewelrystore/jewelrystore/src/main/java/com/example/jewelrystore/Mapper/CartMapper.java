package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.CartDTO;
import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Form.CartForm.CartCreateForm;
import com.example.jewelrystore.Form.CartForm.CartUpdateForm;
import com.example.jewelrystore.Repository.UserRepository;

@Mapper(componentModel = "spring", uses = { Cart_DetailMapper.class }) // Sử dụng Cart_DetailMapper để map danh sách
                                                                       // Cart_Detail
public abstract class CartMapper {

    @Autowired
    UserRepository userRepository;

    public abstract Cart toEntity(CartCreateForm cartCreateForm);

    // Map list cartDetails -> items trong DTO
    @Mapping(source = "cartDetails", target = "items") // Tự tìm đến phương thức map trong Cart_DetailMapper để map danh
                                                       // sách
    public abstract CartDTO toCartDTO(Cart cart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCart(CartUpdateForm cartUpdateForm, @MappingTarget Cart cart);

    @AfterMapping
    void mapRelations(CartCreateForm cartCreateForm, @MappingTarget Cart cart) {
        Integer userId = cartCreateForm.getUserId();
        if (userId != null) {
            cart.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not Found")));
        }
    }

    @AfterMapping
    void calculateTotals(Cart cart, @MappingTarget CartDTO cartDTO) {
        if (cart.getCartDetails() != null) {
            double totalPrice = cart.getCartDetails().stream()
                    .mapToDouble(cd -> cd.getProduct().getPrice() * cd.getQuantity())
                    .sum();

            long totalQuantity = cart.getCartDetails().stream()
                    .mapToLong(cd -> cd.getQuantity())
                    .sum();

            cartDTO.setTotalPrice(totalPrice);
            cartDTO.setTotalQuantity(totalQuantity);
        } else {
            cartDTO.setTotalPrice(0.0);
            cartDTO.setTotalQuantity(0L);
        }
    }
}
