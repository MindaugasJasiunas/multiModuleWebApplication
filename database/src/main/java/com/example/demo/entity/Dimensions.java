package com.example.demo.entity;

import com.example.demo.enums.LengthUnit;
import lombok.Data;

import javax.persistence.*;

@Data

@Entity
public class Dimensions {
    @Id
    @SequenceGenerator( name = "dimensions_sequence", sequenceName = "dimensions_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "dimensions_sequence")
    @Column(name = "id", unique = true)
    private long id;
    private double length;
    private double width;
    private double depth;
    @Enumerated(EnumType.STRING)
    @Column(name = "length_unit")
    private LengthUnit lengthUnit;
}
