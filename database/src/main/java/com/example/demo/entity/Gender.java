package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy="gender") //field name in Course class
    private List<Item> items;

    //convenience method
    public void addItem(Item item){
        if(items==null){
            items=new ArrayList<>();
        }
        items.add(item);
    }

    //male, female, unisex
}
