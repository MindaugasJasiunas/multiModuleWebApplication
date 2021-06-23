package com.example.demo.entity.authentication.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy= UniqueEmailConstraintValidator.class) // - logic
@Target({ElementType.FIELD}) // - where it can be applied
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    public String message() default "Email already exists";
    public Class<?>[] groups() default{};  //can group related constraints
    public Class<? extends Payload>[] payload() default {};  //additional details about error
}