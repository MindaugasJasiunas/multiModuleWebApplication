package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsServiceImpl implements JpaUserDetailsService {
    private final UserEntityRepository userEntityRepository;

    public JpaUserDetailsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userEntityRepository.findUserEntityByEmail(email).orElseThrow(()->new UsernameNotFoundException("User with email "+email+" not found."));
    }
}
