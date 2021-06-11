package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor

@Table(name = "userentity")
@Entity
public class UserEntity {
    @Id
    @SequenceGenerator( name = "userentity_sequence", sequenceName = "userentity_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "userentity_sequence")
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "encrypted_password")
    private String encryptedPassword;
    @Column(name = "email")
    private String email;



}
