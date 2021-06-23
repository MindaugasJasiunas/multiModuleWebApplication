package com.example.demo.entity.authentication;

import com.example.demo.entity.authentication.validation.UniqueEmail;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    @NotNull(message = "First name can't be empty")
    @Size(min = 2, message = "First name is required")
    @Column(name = "first_name")
    private String firstName;
    @NotNull(message = "Last name can't be empty")
    @Size(min = 2, message = "Last name is required")
    @Column(name = "last_name")
    private String lastName;
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be longer than 8 characters")
    @Column(name = "encrypted_password")
    private String encryptedPassword;
    @NotNull(message = "Email is required")
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message = "Check email format.")
    @UniqueEmail(message = "User with this email already exists.")
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
