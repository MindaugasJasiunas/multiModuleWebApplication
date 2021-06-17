package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data

@Table(name = "item_image")
@Entity
public class ItemImage {
    @Id
    @SequenceGenerator( name = "images_sequence", sequenceName = "images_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "images_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "image_name")
    private String imageName;




}
