package com.example.demo.dao;

import com.example.demo.entity.Category;
import com.example.demo.entity.Size;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizeRepository extends CrudRepository<Size, Long> {
}
