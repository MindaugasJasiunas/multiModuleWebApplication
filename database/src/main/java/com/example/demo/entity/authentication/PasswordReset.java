package com.example.demo.entity.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Setter
@Getter

public class PasswordReset {
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be longer than 8 characters")
    private String password;
    private String repeatPassword;
}
