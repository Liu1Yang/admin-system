package com.liuyang.admin.service.impl;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.common.JwtUtil;
import com.liuyang.admin.config.JwtProperties;
import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.LogoutDTO;
import com.liuyang.admin.dto.RefreshTokenDTO;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.PermissionMapper;
import com.liuyang.admin.mapper.RoleMapper;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.service.cache.RefreshTokenService;
import com.liuyang.admin.service.cache.TokenBlacklistService;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.TokenRefreshVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private RefreshTokenService refreshTokenService;

    private JwtUtil jwtUtil;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-key-at-least-32-chars");
        properties.setExpiration(3600000L);
        properties.setRefreshExpiration(86400000L);
        jwtUtil = new JwtUtil(properties);
        authService = new AuthServiceImpl(
                userMapper, roleMapper, permissionMapper, userService,
                passwordEncoder, jwtUtil, tokenBlacklistService, refreshTokenService
        );
    }

    @Test
    void login_shouldReturnDualTokens() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encoded");
        user.setNickname("管理员");

        when(userMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
        when(roleMapper.selectByUserId(1L)).thenReturn(Collections.emptyList());
        when(permissionMapper.selectByUserId(1L)).thenReturn(Collections.emptyList());

        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        LoginVO result = authService.login(dto);

        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals(JwtUtil.TYPE_ACCESS, jwtUtil.getTokenType(result.getAccessToken()));
        assertEquals(JwtUtil.TYPE_REFRESH, jwtUtil.getTokenType(result.getRefreshToken()));
        verify(refreshTokenService).store(eq(result.getRefreshToken()), eq(1L), anyLong());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("nobody");
        dto.setPassword("123456");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(401, ex.getCode());
    }

    @Test
    void refresh_shouldRotateTokens() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        String refreshToken = jwtUtil.generateRefreshToken(1L, "admin");

        when(refreshTokenService.getUserIdIfValid(refreshToken)).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(user);

        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken);

        TokenRefreshVO result = authService.refresh(dto);

        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        verify(refreshTokenService).revoke(refreshToken);
        verify(refreshTokenService).store(eq(result.getRefreshToken()), eq(1L), anyLong());
    }

    @Test
    void logout_shouldBlacklistAccessAndRevokeRefresh() {
        String accessToken = jwtUtil.generateAccessToken(1L, "admin");
        String refreshToken = jwtUtil.generateRefreshToken(1L, "admin");
        LogoutDTO dto = new LogoutDTO();
        dto.setRefreshToken(refreshToken);

        authService.logout(accessToken, dto);

        verify(tokenBlacklistService).add(eq(accessToken), anyLong());
        verify(refreshTokenService).revoke(refreshToken);
    }

    @Test
    void logout_shouldIgnoreInvalidAccessToken() {
        authService.logout("bad.token", null);

        verify(tokenBlacklistService, never()).add(anyString(), anyLong());
    }
}
