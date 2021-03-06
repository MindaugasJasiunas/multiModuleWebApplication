package com.example.demo.dao;

import com.example.demo.entity.Gender;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenderRepository extends CrudRepository<Gender, Long> {
    Optional<Gender> findGenderByGender(String genderName);
}
