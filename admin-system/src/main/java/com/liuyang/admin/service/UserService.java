package com.liuyang.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.User;

public interface UserService {

    Page<User> page(int pageNum, int pageSize, String username);

    User getById(Long id);

    User create(UserCreateDTO dto);

    User update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
