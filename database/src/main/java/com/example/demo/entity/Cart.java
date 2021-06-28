package com.example.demo.entity;

import com.example.demo.entity.authentication.UserEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString

@Table(name = "shopping_cart")
@Entity
public class Cart {
    @Id
    @SequenceGenerator(name = "cart_sequence", sequenceName = "cart_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    List<CartItem> cartItems;


    //convenience method
    public void addCartItem(CartItem cartItem){
        if(cartItems==null){
            cartItems=new ArrayList<>();
        }
        cartItems.add(cartItem);
    }


}
