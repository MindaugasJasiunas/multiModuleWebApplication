package com.example.demo.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode

@Entity
public class Category {
    @Id
    @SequenceGenerator( name = "category_sequence", sequenceName = "category_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "category_sequence")
    @Column(name = "id", unique = true)
    @EqualsAndHashCode.Include
    private long id;
    @Column(name = "category", unique = true)
    private String categoryName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    private Set<Item> items=new HashSet<>();
}
