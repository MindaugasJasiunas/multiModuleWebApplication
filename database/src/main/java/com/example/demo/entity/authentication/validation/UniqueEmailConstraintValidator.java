package com.example.demo.entity.authentication.validation;

import com.example.demo.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String>{
    @Autowired
    private UserEntityService service;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context){
        System.out.println("IS NOT NULL? : "+ (service!=null));
        return service.findUserEntityByEmail(email).isEmpty();
    }

}
