package com.example.demo.dao;

import com.example.demo.entity.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<Store, Long> {
    Optional<Store> getStoreByStoreTitle(String storeTitle);
}
