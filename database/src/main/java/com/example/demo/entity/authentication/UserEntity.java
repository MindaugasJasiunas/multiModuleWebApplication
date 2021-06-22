package com.example.demo.entity.authentication;

import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "userentity")
@Entity
public class UserEntity implements UserDetails, CredentialsContainer {
    @Id
    @SequenceGenerator( name = "userentity_sequence", sequenceName = "userentity_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "userentity_sequence")
    @Column(name = "id")
    private Long id;
    @Column(name = "public_id", unique = true)
    @Builder.Default
    private UUID publicId= UUID.randomUUID();
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "encrypted_password")
    private String encryptedPassword;
    @Column(name = "email", unique = true)
    private String email;
    @Builder.Default
    private Boolean accountNotExpired=true;
    @Builder.Default
    private Boolean accountNotLocked=true;
    @Builder.Default
    private Boolean credentialsNotExpired=true;
    @Builder.Default
    private Boolean enabled=false;  // will be enabled when confirmed by email

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;


    @Transient
    public Set< GrantedAuthority> getAuthorities(){
        return roles.stream().map(Role::getAuthorities).flatMap(Set::stream).map(authority -> new SimpleGrantedAuthority(authority.getPermission())).collect(Collectors.toSet());
    }


    @Override
    public void eraseCredentials() {
        this.encryptedPassword = null;
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNotExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNotExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
