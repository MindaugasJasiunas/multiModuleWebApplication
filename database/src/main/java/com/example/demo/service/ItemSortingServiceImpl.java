package com.example.demo.service;

import com.example.demo.dao.CategoryRepository;
import com.example.demo.dao.GenderRepository;
import com.example.demo.dao.SizeRepository;
import com.example.demo.entity.Category;
import com.example.demo.entity.Gender;
import com.example.demo.entity.Size;
import org.springframework.stereotype.Service;

@Service
public class ItemSortingServiceImpl implements ItemSortingService{
    private CategoryRepository categoryRepository;
    private GenderRepository genderRepository;
    private SizeRepository sizeRepository;

    public ItemSortingServiceImpl(CategoryRepository categoryRepository, GenderRepository genderRepository, SizeRepository sizeRepository) {
        this.categoryRepository = categoryRepository;
        this.genderRepository = genderRepository;
        this.sizeRepository = sizeRepository;
    }

    @Override
    public Iterable<Gender> getGenders() {
        return genderRepository.findAll();
    }

    @Override
    public Iterable<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Iterable<Size> getSizes() {
        return sizeRepository.findAll();
    }
}
