package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.dto.LoginRequest;
import com.advisor.dto.RegisterRequest;
import com.advisor.dto.EmailRequest;
import com.advisor.entity.AdminUser;
import com.advisor.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/register/send-code")
    public Result<?> sendVerificationCode(@RequestBody EmailRequest emailRequest) {
        adminUserService.sendVerificationCode(emailRequest.getEmail());
        return Result.success(null);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        adminUserService.register(request.getUsername(), request.getPassword(), request.getEmail(), request.getCode());
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<AdminUser> login(@RequestBody LoginRequest request) {
        AdminUser user = adminUserService.login(request.getUsername(), request.getPassword());
        // 将用户信息存入Session
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        // 清除Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return Result.success(null);
    }

    @GetMapping("/me")
    public Result<AdminUser> getCurrentUser() {
        AdminUser user = adminUserService.getCurrentUser();
        if (user == null) {
            return Result.fail(401, "未登录");
        }
        return Result.success(user);
    }
}