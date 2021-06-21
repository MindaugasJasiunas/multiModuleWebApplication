package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data

@Table(name = "store_item")
@Entity
@IdClass(StoreItemPK.class)
public class StoreItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
//    @JoinColumn(name= "id", insertable = false, updatable = false)
    private Item item;
    @Id
    @ManyToOne
//    @JoinColumn(name= "id", insertable = false, updatable = false)
    private Store store;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
