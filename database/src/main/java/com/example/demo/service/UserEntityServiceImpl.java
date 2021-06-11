package com.example.demo.service;

import com.example.demo.dao.UserEntityDAO;
import com.example.demo.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserEntityServiceImpl implements UserEntityService{
    private UserEntityDAO userEntityDAO;

    public UserEntityServiceImpl(UserEntityDAO userEntityDAO) {
        this.userEntityDAO = userEntityDAO;
    }

    @Override
    public Long createUserEntity(UserEntity s){
        return userEntityDAO.createUserEntity(s);
    }

    @Override
    public List<UserEntity> getAllUserEntities(){
        return userEntityDAO.getAllUserEntities();
    }

    @Override
    public Long getUserEntitiesCount(){
        return userEntityDAO.getUserEntitiesCount();
    }


}
