package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data

@Entity
public class Size {
    @Id
    @SequenceGenerator( name = "size_sequence", sequenceName = "size_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "size_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "size_title", unique = true)
    private String sizeTitle;

    @ManyToMany(mappedBy = "sizeSet")
    private Set<Item> studentSet=new HashSet<>();
}
