package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.service.AuthService;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "用户登录", description = "登录成功返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserCreateDTO dto) {
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "获取当前登录用户", description = "返回用户信息及角色、权限列表，需在 Header 携带 Authorization: Bearer {token}")
    @GetMapping("/me")
    public Result<CurrentUserVO> me() {
        return Result.success(authService.getCurrentUser(UserContext.getUserId()));
    }
}
