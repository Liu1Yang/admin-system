package com.liuyang.admin.service.impl;

import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
