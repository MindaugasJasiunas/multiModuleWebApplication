package com.example.demo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString

@Table(name = "shopping_cart_order_item")
@Entity
public class OrderItem {
    @Id
    @SequenceGenerator(name = "order_item_sequence", sequenceName = "order_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_sequence")
    @Column(name = "id", unique = true)
    private long id;
    //(FK) order_id
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    int quantity;
}
