package com.liuyang.admin.service;

import com.liuyang.admin.dto.LoginDTO;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.vo.CurrentUserVO;
import com.liuyang.admin.vo.LoginVO;
import com.liuyang.admin.vo.UserVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    UserVO register(UserCreateDTO dto);

    CurrentUserVO getCurrentUser(Long userId);
}
