package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.dto.LoginRequest;
import com.advisor.dto.RegisterRequest;
import com.advisor.dto.EmailRequest;
import com.advisor.entity.base.AdminUser;
import com.advisor.service.base.AdminUserService;
import com.advisor.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JwtUtil jwtUtil;

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
    public Result<String> login(@RequestBody LoginRequest request) {
        try {
            AdminUser user = adminUserService.login(request.getUsername(), request.getPassword());
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), Collections.singletonList("ROLE_ADMIN"));
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.fail(401, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Result.success(null);
    }

    @GetMapping("/me")
    public Result<AdminUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             return Result.fail(401, "未登录或Token无效");
        }

        String username = authentication.getName();
        AdminUser user = adminUserService.findByUsername(username);

        if (user == null) {
            return Result.fail(404, "管理员用户不存在");
        }
        return Result.success(user);
    }
}