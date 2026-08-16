package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.LogoutDTO;
import com.liuyang.admin.dto.RefreshTokenDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.service.AuthService;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.TokenRefreshVO;
import com.liuyang.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "认证")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录", description = "返回 Access Token + Refresh Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "刷新 Access Token", description = "使用 Refresh Token 换取新的双 Token（轮换）")
    @PostMapping("/refresh")
    public Result<TokenRefreshVO> refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        return Result.success(authService.refresh(dto));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserCreateDTO dto) {
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public Result<CurrentUserVO> me() {
        return Result.success(authService.getCurrentUser(UserContext.getUserId()));
    }

    @Operation(summary = "用户登出", description = "Access Token 进黑名单，Refresh Token 从 Redis 吊销")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request,
                               @RequestBody(required = false) LogoutDTO dto) {
        authService.logout(resolveToken(request), dto);
        return Result.success();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
