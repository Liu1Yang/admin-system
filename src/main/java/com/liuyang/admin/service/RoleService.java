package com.liuyang.admin.service;

import com.liuyang.admin.dto.RoleCreateDTO;
import com.liuyang.admin.dto.UserRoleAssignDTO;
import com.liuyang.admin.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> listAll();

    Role getById(Long id);

    Role create(RoleCreateDTO dto);

    List<Role> listByUserId(Long userId);

    void assignRoles(Long userId, UserRoleAssignDTO dto);
}
