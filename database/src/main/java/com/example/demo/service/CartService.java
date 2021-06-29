package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.UserEntity;

import java.util.UUID;

public interface CartService {
    Cart createNewOrFindExistingCart(UserEntity userEntity);
    void addItemToCart(UserEntity userEntity, UUID itemPublicId, int quantity);
}
