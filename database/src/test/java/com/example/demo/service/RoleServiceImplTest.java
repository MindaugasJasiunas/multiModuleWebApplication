package com.example.demo.service;

import com.example.demo.dao.authentication.RoleRepository;
import com.example.demo.entity.authentication.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepo;
    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void isRoleExistsByRoleName() {
        //given
        Role role=new Role();
        Mockito.when(roleRepo.findRoleByRoleName(anyString())).thenReturn(Optional.of(role));

        //when
        boolean result= roleService.isRoleExistsByRoleName("");

        //then
        assertTrue(result);

        Mockito.verify(roleRepo, Mockito.times(1)).findRoleByRoleName(anyString());
    }

    @Test
    void isRoleExistsByRoleName_notExists() {
        //given
        Mockito.when(roleRepo.findRoleByRoleName(anyString())).thenReturn(Optional.empty());

        //when
        boolean result= roleService.isRoleExistsByRoleName("");

        //then
        assertFalse(result);

        Mockito.verify(roleRepo, Mockito.times(1)).findRoleByRoleName(anyString());
    }

    @Test
    void getRoleByRoleName() {
        //given
        Role role=new Role();
        Mockito.when(roleRepo.findRoleByRoleName(anyString())).thenReturn(Optional.of(role));

        //when
        Optional<Role> optionalRole= roleService.getRoleByRoleName("");

        //then
        assertTrue(optionalRole.isPresent());
        assertEquals(role, optionalRole.get());

        Mockito.verify(roleRepo, Mockito.times(2)).findRoleByRoleName(anyString());
    }

    @Test
    void getRoleByRoleName_roleNotExists() {
        //given
        Mockito.when(roleRepo.findRoleByRoleName(anyString())).thenReturn(Optional.empty());

        //when
        Optional<Role> optionalRole= roleService.getRoleByRoleName("");

        //then
        assertTrue(optionalRole.isEmpty());

        Mockito.verify(roleRepo, Mockito.times(1)).findRoleByRoleName(anyString());
    }
}