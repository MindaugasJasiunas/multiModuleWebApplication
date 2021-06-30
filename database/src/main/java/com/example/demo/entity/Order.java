package com.example.demo.entity;

import com.example.demo.entity.authentication.UserEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString

@Table(name = "shopping_cart_order")
@Entity
public class Order {
    @Id
    @SequenceGenerator(name = "order_sequence", sequenceName = "order_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_sequence")
    @Column(name = "id", unique = true)
    private long id;
    //    checkout info
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @NotNull(message = "First name field cannot be null")
    @NotEmpty(message = "First name field cannot be empty")
    private String firstName;
    @NotNull(message = "Last name field cannot be null")
    @NotEmpty(message = "Last name field cannot be empty")
    private String lastName;
    @NotNull(message = "Address field cant be null")
    @NotEmpty(message = "Address field is required")
    private String address;
    private String address2;
    @NotNull(message = "Country selection is required")
    @NotEmpty(message = "Country selection is required")
//    @Enumerated(EnumType.STRING)
    private String country;
    @NotNull(message = "State selection is required")
    @NotEmpty(message = "State selection is required")
    private String state;
    @NotNull(message = "ZIP code cant be null")
    @NotEmpty(message = "ZIP code must not be empty")
    private String zipCode;

    //dont save payment info - outsource to bank
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[0-9]{2}/[0-9]{2}$", message = "Check expiration format")
    private String expiration;
    @NotNull(message = "CVV cant be null")
    @NotEmpty(message = "CVV cant be empty")
    @Pattern(regexp = "^[0-9]{3}$", message = "Check CVV code")
    private String cvv;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$", message = "Check card number")
    private String creditCardNumber;
    @NotNull(message = "Card name cant be null")
    @NotEmpty(message = "Card name cant be empty")
    @Pattern(regexp = "^[a-zA-Z]{2,} [a-zA-Z]{2,}.*$", message = "Check card name")
    private String cardName;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    List<OrderItem> orderItems;


    //convenience method
    public void addOrderItem(OrderItem orderItem){
        if(orderItems==null){
            orderItems=new ArrayList<>();
        }
        orderItems.add(orderItem);
    }




}
