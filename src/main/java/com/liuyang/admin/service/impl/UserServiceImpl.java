package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Page<User> page(int pageNum, int pageSize, String username) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username); // 模糊搜索  有 username → WHERE username LIKE '%admin%'
        }
        wrapper.orderByDesc(User::getCreateTime); // 按创建时间倒序  没有 → 查全部，但只查当前页
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User create(UserCreateDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(Long id, UserUpdateDTO dto) {
        User user = getUserOrThrow(id);

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(dto.getPassword());
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        userMapper.updateById(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userMapper.deleteById(id);
    }

    private User getUserOrThrow(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
}
