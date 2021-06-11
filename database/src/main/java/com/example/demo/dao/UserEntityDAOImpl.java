package com.example.demo.dao;

import com.example.demo.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserEntityDAOImpl implements UserEntityDAO{
    private SessionFactory sessionFactory;

    public UserEntityDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long createUserEntity(UserEntity s){
        Session session= sessionFactory.openSession(); //.getCurrentSession();
        session.beginTransaction();
        Long id=(Long) session.save(s);
        session.getTransaction().commit(); //use when add, update, delete
        session.close(); //if openSession(); used, otherwise not needed.
        return id;
    }

    @Override
    public List<UserEntity> getAllUserEntities(){
        Session session= sessionFactory.openSession(); //.getCurrentSession(); //or openSession();
        session.beginTransaction();

        Query<UserEntity> query= session.createQuery("from UserEntity", UserEntity.class);
        List<UserEntity> users= query.getResultList();
        return users;
    }

    @Override
    public Long getUserEntitiesCount(){
        Session session= sessionFactory.openSession(); //.getCurrentSession();
        session.beginTransaction();

        Query query= session.createQuery("select count (*) from UserEntity");
        Long count=(Long) query.getSingleResult();
        return count;

    }


}