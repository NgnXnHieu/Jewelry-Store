package com.example.jewelrystore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jewelrystore.Entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
