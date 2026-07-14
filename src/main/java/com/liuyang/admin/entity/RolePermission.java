package com.liuyang.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-权限关联表（复合主键，不使用 BaseMapper 通用 CRUD）
 */
@Data
@TableName("role_permission")
public class RolePermission {

    private Long roleId;

    private Long permissionId;
}
