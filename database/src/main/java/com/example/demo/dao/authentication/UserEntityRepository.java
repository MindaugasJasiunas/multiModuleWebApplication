package com.example.demo.dao.authentication;

import com.example.demo.entity.authentication.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmail(String email);
    Optional<UserEntity> findUserEntityByPublicId(UUID publicId);
    void deleteUserEntityByPublicId(UUID publicId);

}
