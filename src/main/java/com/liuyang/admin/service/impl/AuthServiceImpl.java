package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.common.JwtUtil;
import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.entity.Permission;
import com.liuyang.admin.entity.Role;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.PermissionMapper;
import com.liuyang.admin.mapper.RoleMapper;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.AuthService;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.PermissionVO;
import com.liuyang.admin.vo.RoleVO;
import com.liuyang.admin.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper,
                           RoleMapper roleMapper,
                           PermissionMapper permissionMapper,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        loginVO.setUser(buildCurrentUserVO(user));
        return loginVO;
    }

    @Override
    public UserVO register(UserCreateDTO dto) {
        User user = userService.create(dto);
        return toUserVO(user);
    }

    @Override
    public CurrentUserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return buildCurrentUserVO(user);
    }

    private CurrentUserVO buildCurrentUserVO(User user) {
        CurrentUserVO vo = new CurrentUserVO();
        BeanUtils.copyProperties(user, vo);

        List<RoleVO> roles = roleMapper.selectByUserId(user.getId()).stream()
                .map(this::toRoleVO)
                .collect(Collectors.toList());
        vo.setRoles(roles);

        List<PermissionVO> permissions = permissionMapper.selectByUserId(user.getId()).stream()
                .map(this::toPermissionVO)
                .collect(Collectors.toList());
        vo.setPermissions(permissions);
        return vo;
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private RoleVO toRoleVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }

    private PermissionVO toPermissionVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setCode(permission.getCode());
        vo.setName(permission.getName());
        return vo;
    }
}
