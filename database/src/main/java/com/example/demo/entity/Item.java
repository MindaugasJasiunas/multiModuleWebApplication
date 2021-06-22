package com.example.demo.entity;


import com.example.demo.converters.MonetaryAmountConverter;
import lombok.Data;
import lombok.ToString;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@ToString

@Entity
public class Item {
    @Id
    @SequenceGenerator( name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "item_sequence")
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "public_id", unique = true)
    private UUID publicId= UUID.randomUUID();
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "item_category", joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories=new HashSet<>();

    @Column(name = "price", columnDefinition = "numeric(19,2)") // 'numeric' especially recommended for storing monetary amounts and other quantities where exactness is required (Postgres).
    @Convert(converter = MonetaryAmountConverter.class)
    private MonetaryAmount price;

    @OneToMany(cascade= CascadeType.ALL) //One THIS class, MANY others
    @JoinColumn(name="item_id") //foreign key 'item_id' to item_image table
    private Set<ItemImage> images= new HashSet<>();

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name= "dimensions_id")
    private Dimensions dimensions; //foreign key "item_id" in Item table

    @ManyToMany
    @JoinTable(name = "item_size", joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id"))
    private Set<Size> sizeSet=new HashSet<>();

    @ManyToOne
    @JoinColumn(name="gender_id") //foreign key 'gender_id' to Item table
    private Gender gender;


    //helper method
    public void addCategory(Category category){
        if(categories==null){
            categories= new HashSet<>();
        }
        categories.add(category);
    }

    //helper method
    public void addImage(ItemImage image){
        if(images==null){
            images= new HashSet<>();
        }
        images.add(image);
    }

    //helper method
    public void addSize(Size size){
        if(sizeSet==null){
            sizeSet= new HashSet<>();
        }
        sizeSet.add(size);
    }


    /*@OneToMany(mappedBy = "item")
    private Set<StoreItem> groupStoreItem;*/
}
