package com.example.demo.dao;

import com.example.demo.entity.ItemImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends CrudRepository<ItemImage, Long> {
}
