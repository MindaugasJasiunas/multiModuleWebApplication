package com.example.demo.dao;

import com.example.demo.entity.Store;
import com.example.demo.entity.StoreItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreItemRepository extends CrudRepository<StoreItem, Long> {
}
