package com.advisor.service;

import com.advisor.entity.base.User;

public interface UserService {
    void sendVerificationCode(String email);
    void register(String username, String password, String email, String code);
    User login(String username, String password);
    User getUserByToken(String token);
    User getUserByUsername(String username);
    User getUserById(String userId);
    void updateUser(User user);
    boolean isUsernameAvailable(String username);
    // 新增的密码更新方法
    void updatePassword(String username, String oldPassword, String newPassword);
    // 在 UserService 接口中添加了新方法，但在 UserServiceImpl 实现类中
    // 没有实现这些方法。
    // Java 要求实现类必须实现接口中声明的所有方法，否则该类必须被声明为抽象类。
}