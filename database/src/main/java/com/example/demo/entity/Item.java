package com.example.demo.entity;


import com.example.demo.converters.MonetaryAmountConverter;
import lombok.Data;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.UUID;

@Data

@Entity
public class Item {
    @Id
    @SequenceGenerator( name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "item_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "public_id", unique = true)
    private UUID publicId= UUID.randomUUID();

    private String title;

    private String category;

    @Column(name = "price", columnDefinition = "numeric(19,2)") // 'numeric' especially recommended for storing monetary amounts and other quantities where exactness is required (Postgres).
    @Convert(converter = MonetaryAmountConverter.class)
    private MonetaryAmount price;
}
