package com.example.demo.entity.authentication;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "persistent_logins")
public class PersistentTokenRememberMe {  // create default table for persistent remember-me tokens
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String series;
    private String username;
    private String token;
    private Timestamp last_used;

}
