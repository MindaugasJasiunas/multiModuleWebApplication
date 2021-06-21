package com.example.demo.service;

import com.example.demo.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ItemSortingService {
    Iterable<Gender> getGenders();
    Iterable<Category> getCategories();
    Iterable<Size> getSizes();
}
