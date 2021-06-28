package com.example.demo.service;

import com.example.demo.dao.authentication.RoleRepository;
import com.example.demo.entity.authentication.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepo;

    public RoleServiceImpl(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public boolean isRoleExistsByRoleName(String roleName) {
        if(roleRepo.findRoleByRoleName(roleName).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public Optional<Role> getRoleByRoleName(String roleName) {
        if(isRoleExistsByRoleName(roleName)){
            return roleRepo.findRoleByRoleName(roleName);
        }
        return Optional.empty();
    }
}
