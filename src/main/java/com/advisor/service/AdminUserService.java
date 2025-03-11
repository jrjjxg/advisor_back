package com.advisor.service;

import com.advisor.entity.AdminUser;

public interface AdminUserService {
    void sendVerificationCode(String email);
    void register(String username, String password, String email, String code);
    AdminUser login(String username, String password);
    AdminUser getCurrentUser();
}