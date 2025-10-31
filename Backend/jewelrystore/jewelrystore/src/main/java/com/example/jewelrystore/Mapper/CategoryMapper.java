package com.example.jewelrystore.Mapper;

import com.example.jewelrystore.Entity.Category;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.jewelrystore.DTO.CategoryDTO;
import com.example.jewelrystore.Form.CategoryForm.CategoryCreateForm;
import com.example.jewelrystore.Form.CategoryForm.CategoryUpdateForm;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    public abstract Category toEntity(CategoryCreateForm categoryCreateForm);

    public abstract CategoryDTO toCategoryDTO(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCategory(CategoryUpdateForm categoryUpdateForm, @MappingTarget Category category);

}