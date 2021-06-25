package com.example.demo.service;

import com.example.demo.entity.authentication.Role;
import com.example.demo.entity.authentication.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityService {
    Optional<UserEntity> saveOrUpdate(UserEntity user);
    Optional<UserEntity> saveOrUpdate(UserEntity user, boolean newPassword);
    List<UserEntity> getUsers();
    Optional<UserEntity> findUserEntityByEmail(String email);
    Optional<UserEntity> findUserEntityByPublicId(UUID publicId);
    void deleteUserEntityByPublicId(UUID publicId);
    boolean makeUserDisabledByEmail(String email);
    boolean makeUserEnabledByEmail(String email);

}
