package com.example.demo.service;

import com.example.demo.entity.authentication.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityService {
    Optional<UserEntity> saveOrUpdate(UserEntity user);
    List<UserEntity> getUsers();
    Optional<UserEntity> findUserEntityByEmail(String email);
    Optional<UserEntity> findUserEntityByPublicId(UUID publicId);
    void deleteUserEntityByPublicId(UUID publicId);

}
