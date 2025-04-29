package com.advisor.service.base;


import com.advisor.entity.base.AdminUser;

public interface AdminUserService {
    void sendVerificationCode(String email);
    void register(String username, String password, String email, String code);
    AdminUser login(String username, String password);
    AdminUser getCurrentUser();
    AdminUser findByUsername(String username);
}