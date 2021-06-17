package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Data

@Entity
public class Gender {
    @Id
    @SequenceGenerator( name = "gender_sequence", sequenceName = "gender_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gender_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "gender", unique = true, nullable = false)
    private String gender;

    //male, female, unisex
}
