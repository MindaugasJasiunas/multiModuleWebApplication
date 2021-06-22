package com.example.demo.service;

import com.example.demo.dao.authentication.UserEntityRepository;
import com.example.demo.entity.authentication.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserEntityServiceImpl implements UserEntityService{
    private UserEntityRepository userEntityRepo;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepo) {
        this.userEntityRepo = userEntityRepo;
    }

    @Override
    public Optional<UserEntity> saveOrUpdate(UserEntity user){
        Optional<UserEntity> userSavedInDB=Optional.empty();
        try{
            UserEntity saved= userEntityRepo.save(user);
            userSavedInDB= Optional.of(saved);
        }catch (ConstraintViolationException emailDuplicate){
            //email already exists in DB
        }
        return userSavedInDB;
    }

    @Override
    public List<UserEntity> getUsers(){
        return userEntityRepo.findAll();
    }

    @Override
    public Optional<UserEntity> findUserEntityByEmail(String email){
        return userEntityRepo.findUserEntityByEmail(email);
    }

    @Override
    public Optional<UserEntity> findUserEntityByPublicId(UUID publicId){
        return userEntityRepo.findUserEntityByPublicId(publicId);
    }

    @Override
    public void deleteUserEntityByPublicId(UUID publicId){
        userEntityRepo.deleteUserEntityByPublicId(publicId);
    }


}
