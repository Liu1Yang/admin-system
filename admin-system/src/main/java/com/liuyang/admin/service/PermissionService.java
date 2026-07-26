package com.liuyang.admin.service;

public interface PermissionService {

    /**
     * 判断用户是否拥有指定权限编码
     */
    boolean hasPermission(Long userId, String permissionCode);
}
