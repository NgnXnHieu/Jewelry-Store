package com.example.jewelrystore.Form.CategoryForm;

import jakarta.validation.constraints.NotBlank;

public class CategoryUpdateForm {

    @NotBlank(message = "Category name is required")
    private String name;

    public CategoryUpdateForm() {
    }

    public CategoryUpdateForm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
