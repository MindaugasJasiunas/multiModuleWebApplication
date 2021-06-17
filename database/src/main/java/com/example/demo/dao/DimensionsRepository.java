package com.example.demo.dao;

import com.example.demo.entity.Dimensions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DimensionsRepository extends CrudRepository<Dimensions, Long> {
}
