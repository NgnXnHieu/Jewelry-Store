package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.Inventory_HistoryDTO;
import com.example.jewelrystore.Entity.Inventory_History;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Inventory_HistoryCreateForm;
import com.example.jewelrystore.Form.Invetory_HistoryForm.Invetory_HistoryUpdateForm;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class Inventory_HistoryMapper {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    public abstract Inventory_History toEntity(Inventory_HistoryCreateForm inventory_HistoryCreateForm);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    public abstract Inventory_HistoryDTO toInventory_HistoryDTO(Inventory_History inventory_History);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateInventory_History(Invetory_HistoryUpdateForm form,
            @MappingTarget Inventory_History inventory_History);

    @AfterMapping
    void mapRelations(Inventory_HistoryCreateForm form, @MappingTarget Inventory_History inventory_History) {
        Integer userId = form.getUserId();
        Integer productId = form.getProductId();
        if (userId != null) {
            inventory_History.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not Found")));
        }
        if (productId != null) {
            Product p = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not Found"));
            inventory_History.setProduct(p);
            // Cập nhật lại quantity của product
            if (p != null && form.getQuantity() != null) {
                p.setQuantity(p.getQuantity() + form.getQuantity());
                // productRepository.save(p);
            }
        }
    }
}
