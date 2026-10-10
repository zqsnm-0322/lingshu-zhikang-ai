package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.User;

public interface IUserService {
    User login(String username, String password);
    User findByUsername(String username);
    User findById(Long id);
    User register(User user);
    boolean checkUsernameExists(String username);
} 