package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.UserEntity;

public interface CartService {
    Cart createNewOrFindExistingCart(UserEntity userEntity);
}
