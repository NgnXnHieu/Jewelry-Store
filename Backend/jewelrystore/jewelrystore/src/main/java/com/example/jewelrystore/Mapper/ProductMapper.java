package com.example.jewelrystore.Mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Repository.CategoryRepository;

@Mapper(componentModel = "spring")

public abstract class ProductMapper {
    @Autowired
    CategoryRepository categoryRepository;

    public abstract Product toEntity(ProductCreateForm productCreateForm);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    public abstract ProductDTO toProductDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateProduct(ProductUpdateForm form,
            @MappingTarget Product product);

    @AfterMapping
    void mapRelations(ProductCreateForm form, @MappingTarget Product product) {
        Integer id = form.getCategoryId();
        if (id != null) {
            product.setCategory(categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not Found")));

        }
    }

    @AfterMapping
    void mapRelations(ProductUpdateForm form, @MappingTarget Product product) {
        Integer id = form.getCategoryId();
        if (id != null) {
            product.setCategory(categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not Found")));

        }
    }
}
