package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.service.cache.UserCacheService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserCacheService userCacheService;

    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           UserCacheService userCacheService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userCacheService = userCacheService;
    }

    @Override
    public Page<User> page(int pageNum, int pageSize, String username) {
        Page<User> page = new Page<>(pageNum, pageSize);// 创建分页容器（装数据的“盒子”）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();// 构建查询条件（拼装 SQL 的 WHERE 子句）
        if (StringUtils.hasText(username)) { //设置动态模糊查询（WHERE LIKE）
            wrapper.like(User::getUsername, username);
        }
        wrapper.orderByDesc(User::getCreateTime);//设置排序规则（ORDER BY）
        return userMapper.selectPage(page, wrapper); // 执行查询并返回结果（最关键的一步）
    }

    @Override
    public User getById(Long id) {
        User cached = userCacheService.getById(id);
        if (cached != null) {
            return cached;
        }

        User user = userMapper.selectById(id);
        if (user != null) {
            userCacheService.set(user);
        }
        return user;
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
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        userMapper.insert(user);
        userCacheService.set(user);
        return user;
    }

    @Override
    public User update(Long id, UserUpdateDTO dto) {
        User user = getUserOrThrow(id);

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        userMapper.updateById(user);
        userCacheService.delete(id);
        return user;
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userMapper.deleteById(id);
        userCacheService.delete(id);
    }

    private User getUserOrThrow(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
}
