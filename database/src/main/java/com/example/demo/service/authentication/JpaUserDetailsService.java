package com.example.demo.service.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface JpaUserDetailsService extends UserDetailsService {
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;  // optional (left for code consistency and clarity)
}
