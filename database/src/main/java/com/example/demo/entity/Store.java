package com.example.demo.entity;


import com.example.demo.converters.MonetaryAmountConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode


@Entity
public class Store {
    @Id
    @SequenceGenerator( name = "store_sequence", sequenceName = "store_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "store_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "store_title", unique = true)
    private String storeTitle;

    /*@EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "store")
    private Set<StoreItem> groupStoreItem;*/
}
