package com.liuyang.admin.service;

import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.LogoutDTO;
import com.liuyang.admin.dto.RefreshTokenDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.TokenRefreshVO;
import com.liuyang.admin.vo.UserVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    TokenRefreshVO refresh(RefreshTokenDTO dto);

    UserVO register(UserCreateDTO dto);

    CurrentUserVO getCurrentUser(Long userId);

    void logout(String accessToken, LogoutDTO dto);
}
