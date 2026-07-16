package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.RoleCreateDTO;
import com.liuyang.admin.dto.UserRoleAssignDTO;
import com.liuyang.admin.entity.Role;
import com.liuyang.admin.mapper.RoleMapper;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.mapper.UserRoleMapper;
import com.liuyang.admin.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(RoleMapper roleMapper,
                           UserMapper userMapper,
                           UserRoleMapper userRoleMapper) {
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<Role>().orderByAsc(Role::getId)
        );
    }

    @Override
    public Role getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return role;
    }

    @Override
    public Role create(RoleCreateDTO dto) {
        Long count = roleMapper.selectCount(
                new LambdaQueryWrapper<Role>().eq(Role::getCode, dto.getCode())
        );
        if (count > 0) {
            throw new BusinessException(400, "角色编码已存在");
        }

        Role role = new Role();
        role.setCode(dto.getCode());
        role.setName(dto.getName());
        roleMapper.insert(role);
        return role;
    }

    @Override
    public List<Role> listByUserId(Long userId) {
        ensureUserExists(userId);
        return roleMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, UserRoleAssignDTO dto) {
        ensureUserExists(userId); // 校验用户存在

        List<Long> roleIds = dto.getRoleIds()oleIds();
        if (roleIds != null) {  // 检验每个roleId存在
            for (Long roleId : roleIds) {
                if (roleMapper.selectById(roleId) == null) {
                    throw new BusinessException(400, "角色不存在: " + roleId);
                }
            }
        }

        userRoleMapper.deleteByUserId(userId); // 删除该用户全部旧绑定
        if (roleIds != null) {  // 插入新的 (userId, roleId)
            for (Long roleId : roleIds) {
                userRoleMapper.insert(userId, roleId);
            }
        }
    }

    private void ensureUserExists(Long userId) {
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(404, "用户不存在");
        }
    }
}
