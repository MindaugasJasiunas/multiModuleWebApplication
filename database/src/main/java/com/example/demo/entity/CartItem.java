package com.example.demo.entity;

import com.example.demo.entity.authentication.UserEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data

@Table(name = "shopping_cart_item")
@Entity
public class CartItem {
    @Id
    @SequenceGenerator(name = "cart_item_sequence", sequenceName = "cart_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_sequence")
    @Column(name = "id", unique = true)
    private long id;
    //(FK) cart_id
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    int quantity;


}
