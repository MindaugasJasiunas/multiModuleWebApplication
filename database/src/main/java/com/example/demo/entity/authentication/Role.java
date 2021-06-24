package com.example.demo.entity.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter

@Entity
public class Role {
    @Id
    @SequenceGenerator( name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @Column(name = "id", unique = true)
    private Long id;
    @Column(name = "role_name", unique = true)
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "role_authority", joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")}
    )
    private Set<Authority> authorities;
}
