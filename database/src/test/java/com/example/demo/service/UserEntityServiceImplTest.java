package com.example.demo.service;

import com.example.demo.dao.authentication.UserEntityRepository;
import com.example.demo.entity.Cart;
import com.example.demo.entity.authentication.Role;
import com.example.demo.entity.authentication.UserEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserEntityServiceImplTest {
    @Mock
    private UserEntityRepository userEntityRepo;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CartService cartService;
    @InjectMocks
    private UserEntityServiceImpl userEntityService;

    @DisplayName("saveOrUpdate() - save new")
    @Test
    void saveOrUpdate_save() {
        //given
        UserEntity userEntity= new UserEntity();
        userEntity.setId(0L);
        Role role=new Role();
        Mockito.when(roleService.isRoleExistsByRoleName("CUSTOMER")).thenReturn(true);
        Mockito.when(roleService.getRoleByRoleName("CUSTOMER")).thenReturn(Optional.of(role));

        final ArgumentCaptor<UserEntity> stringArgumentCaptor= ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityRepo.save(stringArgumentCaptor.capture())).thenReturn(userEntity);
        Cart cart= new Cart();
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);

        //when
        userEntityService.saveOrUpdate(userEntity);

        //then
        assertFalse( stringArgumentCaptor.getValue().getEnabled());
        assertEquals(1, stringArgumentCaptor.getValue().getRoles().size());
        assertTrue(stringArgumentCaptor.getValue().getRoles().contains(role));

        Mockito.verify(roleService, Mockito.times(1)).isRoleExistsByRoleName(anyString());
        Mockito.verify(roleService, Mockito.times(1)).getRoleByRoleName(anyString());
        Mockito.verify(userEntityRepo, Mockito.times(1)).save(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
    }

    @DisplayName("saveOrUpdate() - update existing")
    @Test
    void saveOrUpdate_update() {
        //given
        UserEntity userEntity= new UserEntity();
        userEntity.setId(1L);

        final ArgumentCaptor<UserEntity> stringArgumentCaptor= ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityRepo.save(stringArgumentCaptor.capture())).thenReturn(userEntity);
        Cart cart= new Cart();
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);

        //when
        userEntityService.saveOrUpdate(userEntity);

        //then
        assertEquals(userEntity, stringArgumentCaptor.getValue());

        Mockito.verify(roleService, Mockito.never()).isRoleExistsByRoleName(anyString());
        Mockito.verify(roleService, Mockito.never()).getRoleByRoleName(anyString());
        Mockito.verify(userEntityRepo, Mockito.times(1)).save(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
    }

    @Test
    void saveOrUpdate_newPassword() {
        //given
        UserEntity userEntity= new UserEntity();
        userEntity.setId(1L);
        String userPass="test";
        userEntity.setEncryptedPassword(userPass);

        String encPass="encrypted";

        final ArgumentCaptor<UserEntity> stringArgumentCaptor= ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityRepo.save(stringArgumentCaptor.capture())).thenReturn(userEntity);
        Cart cart= new Cart();
        Mockito.when(cartService.createNewOrFindExistingCart(any(UserEntity.class))).thenReturn(cart);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn(encPass);

        //when
        userEntityService.saveOrUpdate(userEntity, true);

        //then
        assertNotEquals(userPass, stringArgumentCaptor.getValue().getEncryptedPassword());
        assertEquals(encPass, stringArgumentCaptor.getValue().getEncryptedPassword());

        Mockito.verify(roleService, Mockito.never()).isRoleExistsByRoleName(anyString());
        Mockito.verify(roleService, Mockito.never()).getRoleByRoleName(anyString());
        Mockito.verify(userEntityRepo, Mockito.times(1)).save(any(UserEntity.class));
        Mockito.verify(cartService, Mockito.times(1)).createNewOrFindExistingCart(any(UserEntity.class));
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(anyString());
    }

    @Test
    void getUsers() {
        //given
        UserEntity userEntity=new UserEntity();
        List<UserEntity> list= Arrays.asList(userEntity);
        Mockito.when(userEntityRepo.findAll()).thenReturn(list);

        //when
        List<UserEntity> result= userEntityService.getUsers();

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(list, result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findAll();
    }

    @Test
    void findUserEntityByPublicId() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityRepo.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.of(userEntity));

        //when
        Optional<UserEntity> result= userEntityService.findUserEntityByPublicId(UUID.randomUUID());

        //then
        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
    }

    @DisplayName("findUserEntityByPublicId() - user doesn't exist")
    @Test
    void findUserEntityByPublicId_nonExistent() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        Optional<UserEntity> result= userEntityService.findUserEntityByPublicId(UUID.randomUUID());

        //then
        assertFalse(result.isPresent());
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
    }

    @Test
    void findUserEntityByEmail() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        //when
        Optional<UserEntity> result= userEntityService.findUserEntityByEmail("");

        //then
        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
    }

    @DisplayName("findUserEntityByEmail() - user doesn't exist")
    @Test
    void findUserEntityByEmail_nonExistent() {
        //given
        UserEntity userEntity=new UserEntity();
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));

        //when
        Optional<UserEntity> result= userEntityService.findUserEntityByEmail("");

        //then
        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
    }

    @Test
    void deleteUserEntityByPublicId() {
        //given
        Mockito.doNothing().when(userEntityRepo).deleteUserEntityByPublicId(any(UUID.class));

        //when
        userEntityService.deleteUserEntityByPublicId(UUID.randomUUID());

        //then
        Mockito.verify(userEntityRepo, Mockito.times(1)).deleteUserEntityByPublicId(any(UUID.class));
    }

    @Test
    void makeUserDisabledByEmail() {
        //given
        UserEntity userEntity=new UserEntity();
        userEntity.setEnabled(true);
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));
        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor=ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityRepo.save(userEntityArgumentCaptor.capture())).thenReturn(userEntity);

        //when
        boolean result= userEntityService.makeUserDisabledByEmail("");

        //then
        assertTrue(result);
        assertFalse(userEntityArgumentCaptor.getValue().getEnabled());
        Mockito.verify(userEntityRepo, Mockito.times(2)).findUserEntityByEmail(anyString());
        Mockito.verify(userEntityRepo, Mockito.times(1)).save(any(UserEntity.class));
    }

    @DisplayName("makeUserDisabledByEmail() user doesn't exist")
    @Test
    void makeUserDisabledByEmail_nonExistent() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.empty());

        //when
        boolean result= userEntityService.makeUserDisabledByEmail("");

        //then
        assertFalse(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
        Mockito.verify(userEntityRepo, Mockito.never()).save(any(UserEntity.class));
    }

    @Test
    void makeUserEnabledByEmail() {
        //given
        UserEntity userEntity=new UserEntity();
        userEntity.setEnabled(false);
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.of(userEntity));
        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor=ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userEntityRepo.save(userEntityArgumentCaptor.capture())).thenReturn(userEntity);

        //when
        boolean result= userEntityService.makeUserEnabledByEmail("");

        //then
        assertTrue(result);
        assertTrue(userEntityArgumentCaptor.getValue().getEnabled());
        Mockito.verify(userEntityRepo, Mockito.times(2)).findUserEntityByEmail(anyString());
        Mockito.verify(userEntityRepo, Mockito.times(1)).save(any(UserEntity.class));
    }

    @DisplayName("makeUserEnabledByEmail() user doesn't exist")
    @Test
    void makeUserEnabledByEmail_nonExistentUser() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.empty());

        //when
        boolean result= userEntityService.makeUserEnabledByEmail("");

        //then
        assertFalse(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
        Mockito.verify(userEntityRepo, Mockito.never()).save(any(UserEntity.class));
    }

    @Test
    void isUserExistsByUserEntityPublicId() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.of(new UserEntity()));

        //when
        boolean result= userEntityService.isUserExistsByUserEntityPublicId(UUID.randomUUID());

        //then
        assertTrue(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
    }

    @DisplayName("isUserExistsByUserEntityPublicId() - doesn't exist")
    @Test
    void isUserExistsByUserEntityPublicId_nonExistent() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        boolean result= userEntityService.isUserExistsByUserEntityPublicId(UUID.randomUUID());

        //then
        assertFalse(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByPublicId(any(UUID.class));
    }

    @Test
    void isUserExistsByUserEntityEmail() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.of(new UserEntity()));

        //when
        boolean result= userEntityService.isUserExistsByUserEntityEmail(anyString());

        //then
        assertTrue(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
    }

    @DisplayName("isUserExistsByUserEntityEmail() user doesn't exist")
    @Test
    void isUserExistsByUserEntityEmail_nonExistent() {
        //given
        Mockito.when(userEntityRepo.findUserEntityByEmail(anyString())).thenReturn(Optional.empty());

        //when
        boolean result= userEntityService.isUserExistsByUserEntityEmail("");

        //then
        assertFalse(result);
        Mockito.verify(userEntityRepo, Mockito.times(1)).findUserEntityByEmail(anyString());
    }
}