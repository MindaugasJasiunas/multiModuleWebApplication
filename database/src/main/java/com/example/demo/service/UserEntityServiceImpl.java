package com.example.demo.service;

import com.example.demo.dao.authentication.UserEntityRepository;
import com.example.demo.entity.authentication.Role;
import com.example.demo.entity.authentication.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserEntityServiceImpl implements UserEntityService{
    private final UserEntityRepository userEntityRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepo, RoleService roleService, PasswordEncoder passwordEncoder, CartService cartService) {
        this.userEntityRepo = userEntityRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }

    @Override
    public Optional<UserEntity> saveOrUpdate(UserEntity user){
        if(user.getId()==null || user.getId()<1){
            //new user - set disabled
            user.setEnabled(false);
            //add role
            if(roleService.isRoleExistsByRoleName("CUSTOMER")){
                Role customerRole= roleService.getRoleByRoleName("CUSTOMER").get();
                user.setRoles(Set.of(customerRole)); // add customer role to user
            }
        }
        Optional<UserEntity> userSavedInDB=Optional.empty();
        try{
            UserEntity saved= userEntityRepo.save(user);
            //create and assign cart for every new user
            cartService.createNewOrFindExistingCart(saved);
            userSavedInDB= Optional.of(saved);
        }catch (ConstraintViolationException emailDuplicate){
            //email already exists in DB
            //should never be thrown because of custom UserEntity validation
        }
        return userSavedInDB;
    }


    @Override
    public Optional<UserEntity> saveOrUpdate(UserEntity user, boolean newPassword){
        user.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
        return saveOrUpdate(user);
    }

    @Override
    public List<UserEntity> getUsers(){
        return userEntityRepo.findAll();
    }

    @Override
    public Optional<UserEntity> findUserEntityByEmail(String email){
        return userEntityRepo.findUserEntityByEmail(email);
    }

    @Override
    public Optional<UserEntity> findUserEntityByPublicId(UUID publicId){
        return userEntityRepo.findUserEntityByPublicId(publicId);
    }

    @Override
    public void deleteUserEntityByPublicId(UUID publicId){
        userEntityRepo.deleteUserEntityByPublicId(publicId);
    }


    @Override
    public boolean makeUserDisabledByEmail(String email){
        if(isUserExistsByUserEntityEmail(email)){
            UserEntity user=userEntityRepo.findUserEntityByEmail(email).get();
            user.setEnabled(false);
            userEntityRepo.save(user);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean makeUserEnabledByEmail(String email){
        if(isUserExistsByUserEntityEmail(email)){
            UserEntity user=userEntityRepo.findUserEntityByEmail(email).get();
            user.setEnabled(true);
            userEntityRepo.save(user);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isUserExistsByUserEntityPublicId(UUID publicId){
        if(userEntityRepo.findUserEntityByPublicId(publicId).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserExistsByUserEntityEmail(String email) {
        if(userEntityRepo.findUserEntityByEmail(email).isPresent()){
            return true;
        }
        return false;
    }
}
