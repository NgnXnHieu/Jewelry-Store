package com.example.jewelrystore.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.jewelrystore.DTO.CategoryDTO;
import com.example.jewelrystore.Form.CategoryForm.CategoryCreateForm;
import com.example.jewelrystore.Form.CategoryForm.CategoryUpdateForm;

public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateForm Form);

    CategoryDTO updateCategory(Integer id, CategoryUpdateForm Form);

    Page<CategoryDTO> getAllCategory(Pageable pageable);

    CategoryDTO getCategoryById(Integer id);

    void deleteCategory(Integer id);
}
