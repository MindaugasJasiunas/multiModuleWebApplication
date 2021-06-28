package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.entity.authentication.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    public boolean isRoleExistsByRoleName(String roleName);
    public Optional<Role> getRoleByRoleName(String roleName);
}
