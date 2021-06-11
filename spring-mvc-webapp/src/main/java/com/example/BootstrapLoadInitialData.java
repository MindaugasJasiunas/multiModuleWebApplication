package com.example;

import com.example.demo.dao.UserEntityDAOImpl;
import com.example.demo.entity.UserEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BootstrapLoadInitialData implements InitializingBean {
    @Autowired
    UserEntityDAOImpl userEntityDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        //names who doesn't exist in real life

        UserEntity userEntity1=new UserEntity();
        userEntity1.setEmail("john.doe@example.com");
        userEntity1.setFirstName("John");
        userEntity1.setLastName("Doe");
        userEntity1.setEncryptedPassword("pass");
        userEntityDAO.createUserEntity(userEntity1);

        UserEntity userEntity2=new UserEntity();
        userEntity2.setEmail("jane.doe@example.com");
        userEntity2.setFirstName("Jane");
        userEntity2.setLastName("Doe");
        userEntity2.setEncryptedPassword("encPass");
        userEntityDAO.createUserEntity(userEntity2);

        UserEntity userEntity3=new UserEntity();
        userEntity3.setEmail("fabian.khalid@example.com");
        userEntity3.setFirstName("Fabian");
        userEntity3.setLastName("Khalid");
        userEntity3.setEncryptedPassword("qwerty");
        userEntityDAO.createUserEntity(userEntity3);

        UserEntity userEntity4=new UserEntity();
        userEntity4.setEmail("thomas.claiton@example.com");
        userEntity4.setFirstName("Thomas");
        userEntity4.setLastName("Claiton");
        userEntity4.setEncryptedPassword("password123");
        userEntityDAO.createUserEntity(userEntity4);

        UserEntity userEntity5=new UserEntity();
        userEntity5.setEmail("josiah.wesley@example.com");
        userEntity5.setFirstName("Josiah");
        userEntity5.setLastName("Wesley");
        userEntity5.setEncryptedPassword("passpass");
        userEntityDAO.createUserEntity(userEntity5);

    }
}
