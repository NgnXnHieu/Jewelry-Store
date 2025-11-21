package com.example.jewelrystore.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Đếm tổng số danh mục
    @Query("SELECT COUNT(c) FROM Category c")
    Long countAllCategory();

}
