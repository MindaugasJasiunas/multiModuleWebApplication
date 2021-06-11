package com.example.demo.dao;

import com.example.demo.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public interface UserEntityDAO {
    public Long createUserEntity(UserEntity s);
    public List<UserEntity> getAllUserEntities();
    public Long getUserEntitiesCount();

}
