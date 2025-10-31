package com.example.jewelrystore.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jewelrystore.DTO.CategoryDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Form.CategoryForm.CategoryCreateForm;
import com.example.jewelrystore.Form.CategoryForm.CategoryUpdateForm;
import com.example.jewelrystore.Mapper.CategoryMapper;
import com.example.jewelrystore.Repository.CategoryRepository;
import com.example.jewelrystore.Service.CategoryService;

@Service

public class CategoryImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryCreateForm Form) {
        Category category = categoryMapper.toEntity(Form);
        categoryRepository.save(category);
        return categoryMapper.toCategoryDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(Integer id, CategoryUpdateForm Form) {
        Category existing = categoryRepository.findById(id).orElse(null);
        if (existing != null) {
            categoryMapper.updateCategory(Form, existing);
            categoryRepository.save(existing);
            return categoryMapper.toCategoryDTO(existing);
        }
        return null;
    }

    @Override
    public Page<CategoryDTO> getAllCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toCategoryDTO);
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        return categoryRepository.findById(id).map(categoryMapper::toCategoryDTO).orElse(null);
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

}
