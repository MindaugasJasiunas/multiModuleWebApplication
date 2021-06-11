package com.example.demo.service;

import com.example.demo.entity.UserEntity;

import java.util.List;

public interface UserEntityService {
    Long createUserEntity(UserEntity s);
    List<UserEntity> getAllUserEntities();
    Long getUserEntitiesCount();
}
