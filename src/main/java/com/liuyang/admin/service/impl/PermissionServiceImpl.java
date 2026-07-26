package com.liuyang.admin.service.impl;

import com.liuyang.admin.entity.Permission;
import com.liuyang.admin.mapper.PermissionMapper;
import com.liuyang.admin.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        return permissions.stream()
                .anyMatch(p -> permissionCode.equals(p.getCode()));
    }
}
