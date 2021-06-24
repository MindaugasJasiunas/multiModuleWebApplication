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
    private final UserEntityRepository userEntityRepo;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepo) {
        this.userEntityRepo = userEntityRepo;
    }

    @Override
    public Optional<UserEntity> saveOrUpdate(UserEntity user){
        if(user.getId()==null || user.getId()<1){
            //new user - save disabled
            user.setEnabled(false);
        }
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


    @Override
    public boolean makeUserDisabledByEmail(String email){
        if(userEntityRepo.findUserEntityByEmail(email).isPresent()){
            UserEntity user=userEntityRepo.findUserEntityByEmail(email).get();
            user.setEnabled(false);
            userEntityRepo.save(user);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean makeUserEnabledByEmail(String email){
        if(userEntityRepo.findUserEntityByEmail(email).isPresent()){
            UserEntity user=userEntityRepo.findUserEntityByEmail(email).get();
            user.setEnabled(true);
            userEntityRepo.save(user);
            return true;
        }else{
            return false;
        }
    }

}
