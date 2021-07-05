package com.example.demo.service.authentication;

import com.example.demo.dao.authentication.UserEntityRepository;
import com.example.demo.entity.authentication.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceImplTest {
    @Mock
    UserEntityRepository userEntityRepository;
    @InjectMocks
    JpaUserDetailsServiceImpl jpaUserDetailsService;

    @Test
    void loadUserByUsername() {
        //given
        UserEntity userEntity=new UserEntity();
        userEntity.setEmail("exampleEmail");
        Mockito.when(userEntityRepository.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        //when
        UserDetails userDetails= jpaUserDetailsService.loadUserByUsername("email");

        //then
        assertNotNull(userDetails);
        assertEquals(userEntity.getEmail(), userDetails.getUsername());

        Mockito.verify(userEntityRepository, Mockito.times(1)).findUserEntityByEmail(anyString());
    }

    @Test
    void loadUserByUsername_notFound() {
        //given
        Mockito.when(userEntityRepository.findUserEntityByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, ()-> jpaUserDetailsService.loadUserByUsername(anyString()));

        Mockito.verify(userEntityRepository, Mockito.times(1)).findUserEntityByEmail(anyString());
    }
}