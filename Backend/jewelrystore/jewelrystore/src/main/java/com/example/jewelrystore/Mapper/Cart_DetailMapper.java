package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.Cart_DetailDTO;
import com.example.jewelrystore.Entity.Cart_Detail;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailCreateForm;
import com.example.jewelrystore.Form.Cart_DetailForm.Cart_DetailUpdateForm;
import com.example.jewelrystore.Repository.CartRepository;
import com.example.jewelrystore.Repository.ProductRepository;

@Mapper(componentModel = "spring")
public abstract class Cart_DetailMapper {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    public abstract Cart_Detail toEntity(Cart_DetailCreateForm cart_DetailCreateForm);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName") // lấy tên sp
    @Mapping(source = "product.image_url", target = "imageUrl") // lấy ảnh sp
    @Mapping(source = "product.price", target = "unitPrice") // giả sử field trong Product là price
    @Mapping(target = "totalPrice", expression = "java(cart_Detail.getQuantity() * cart_Detail.getProduct().getPrice())")
    public abstract Cart_DetailDTO toCart_DetailDTO(Cart_Detail cart_Detail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCart_Detail(Cart_DetailUpdateForm cart_DetailUpdateForm,
            @MappingTarget Cart_Detail cart_Detail);

    @AfterMapping
    void mapRelations(Cart_DetailCreateForm cart_DetailCreateForm, @MappingTarget Cart_Detail cart_Detail) {
        Integer cartId = cart_DetailCreateForm.getCartId();
        Integer productId = cart_DetailCreateForm.getProductId();
        if (cartId != null) {
            cart_Detail.setCart(cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not Found")));
        }
        if (productId != null) {
            cart_Detail.setProduct(productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not Found")));
        }
    }
}
