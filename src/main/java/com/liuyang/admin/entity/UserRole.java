package com.liuyang.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-角色关联表（复合主键，不使用 BaseMapper 通用 CRUD）
 */
@Data
@TableName("user_role")
public class UserRole {

    private Long userId;

    private Long roleId;
}
