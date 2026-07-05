package com.liuyang.admin.service;

import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);

    User create(UserCreateDTO dto);

    User update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
