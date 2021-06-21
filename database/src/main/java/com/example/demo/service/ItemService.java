package com.example.demo.service;

import com.example.demo.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    Page<Item> findAll(Pageable pageable);
}
