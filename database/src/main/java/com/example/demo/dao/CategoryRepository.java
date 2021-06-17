package com.example.demo.dao;

import com.example.demo.entity.Category;
import com.example.demo.entity.Dimensions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findCategoryByCategoryName(String categoryName);
}
