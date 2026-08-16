package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.common.JwtUtil;
import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.LogoutDTO;
import com.liuyang.admin.dto.RefreshTokenDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.entity.Permission;
import com.liuyang.admin.entity.Role;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.PermissionMapper;
import com.liuyang.admin.mapper.RoleMapper;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.AuthService;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.service.cache.RefreshTokenService;
import com.liuyang.admin.service.cache.TokenBlacklistService;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.PermissionVO;
import com.liuyang.admin.vo.RoleVO;
import com.liuyang.admin.vo.TokenRefreshVO;
import com.liuyang.admin.vo.UserVO;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserMapper userMapper,
                           RoleMapper roleMapper,
                           PermissionMapper permissionMapper,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           TokenBlacklistService tokenBlacklistService,
                           RefreshTokenService refreshTokenService) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        refreshTokenService.store(refreshToken, user.getId(), jwtUtil.getRemainingSeconds(refreshToken));

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUser(buildCurrentUserVO(user));
        return loginVO;
    }

    @Override
    public TokenRefreshVO refresh(RefreshTokenDTO dto) {
        String refreshToken = dto.getRefreshToken();
        // 校验 Token 的“类型”必须是 REFRESH（防止有人拿 AccessToken 来这个接口冒充）
        try {
            jwtUtil.validateTokenType(refreshToken, JwtUtil.TYPE_REFRESH);
        } catch (JwtException e) {
            throw new BusinessException(401, "Refresh Token 无效");
        }

        Long userId = refreshTokenService.getUserIdIfValid(refreshToken);
        if (userId == null) {
            throw new BusinessException(401, "Refresh Token 已失效，请重新登录");
        }

        // 根据 userId 查数据库，确保用户还在（没被管理员删除）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        // ⚠️ 关键步骤：主动撤销（删除）旧的 refreshToken
        //  这样做是强制旧的 refreshToken 失效（防止被多次使用，即“一次有效”）
        refreshTokenService.revoke(refreshToken);

        // 生成全新的 AccessToken
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        // 生成全新的 RefreshToken
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        // 把新 RefreshToken 存进 Redis，重新计时（比如 7 天）
        refreshTokenService.store(newRefreshToken, user.getId(), jwtUtil.getRemainingSeconds(newRefreshToken));

        // 返回新的 Token 对给前端
        TokenRefreshVO vo = new TokenRefreshVO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        return vo;
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

    @Override
    public void logout(String accessToken, LogoutDTO dto) {
        if (StringUtils.hasText(accessToken)) {
            try {
                jwtUtil.validateTokenType(accessToken, JwtUtil.TYPE_ACCESS);
                long ttlSeconds = jwtUtil.getRemainingSeconds(accessToken);
                tokenBlacklistService.add(accessToken, ttlSeconds);
            } catch (JwtException ignored) {
                // Access Token 已过期或无效，忽略
            }
        }

        if (dto != null && StringUtils.hasText(dto.getRefreshToken())) {
            refreshTokenService.revoke(dto.getRefreshToken());
        }
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
