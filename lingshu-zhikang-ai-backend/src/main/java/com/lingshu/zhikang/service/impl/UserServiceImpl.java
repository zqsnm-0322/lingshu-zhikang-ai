package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.mapper.UserMapper;
import com.lingshu.zhikang.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        System.out.println("=== 登录调试信息 ===");
        System.out.println("尝试登录用户: " + username);
        System.out.println("输入密码: " + password);
        
        User user = userMapper.findByUsername(username);
        
        if (user == null) {
            System.out.println("❌ 用户不存在: " + username);
            return null;
        }
        
        System.out.println("✓ 找到用户: " + user.getUsername());
        System.out.println("数据库密码: " + user.getPassword());
        System.out.println("密码匹配: " + user.getPassword().equals(password));
        
        if (user.getPassword().equals(password)) {
            System.out.println("✓ 登录成功");
            return user;
        } else {
            System.out.println("❌ 密码不匹配");
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User register(User user) {
        // 设置默认值
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsActivated(true); // 根据用户偏好，简单注册方式，直接激活
        
        // 设置默认会员等级和配额
        user.setRole("USER"); // 默认为普通用户
        user.setMemberLevel("FREE"); // 默认为免费版
        user.setDailyQuota(5); // 免费版每日5次
        user.setDailyUsed(0); // 初始使用次数为0
        user.setTotalUsed(0); // 累计使用次数为0
        user.setStatus("ACTIVE"); // 账号状态为正常
        user.setRegisterTime(LocalDateTime.now()); // 注册时间
        
        // 插入用户
        userMapper.insertUser(user);
        
        // 返回注册成功的用户（不包含密码）
        User registeredUser = userMapper.findByUsername(user.getUsername());
        if (registeredUser != null) {
            registeredUser.setPassword(null); // 不返回密码
        }
        return registeredUser;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        User user = userMapper.findByUsername(username);
        return user != null;
    }
} 