package com.example.demo.dao;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Category;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findCartByUser(UserEntity userEntity);
}
