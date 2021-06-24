package com.example.demo.entity.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

@Entity
public class Authority {
    @Id
    @SequenceGenerator( name = "authority_sequence", sequenceName = "authority_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "authority_sequence")
    @Column(name = "id", unique = true)
    private Long id;
    private String permission;
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
