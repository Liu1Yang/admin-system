package com.liuyang.admin.service;

import com.liuyang.admin.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);
}
