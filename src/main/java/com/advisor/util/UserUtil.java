package com.advisor.util;

import com.advisor.entity.base.User;
import com.advisor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 用户工具类
 */
@Component
public class UserUtil {
    
    private static UserService userService;
    
    @Autowired
    public void setUserService(UserService userService) {
        UserUtil.userService = userService;
    }
    
    /**
     * 获取当前登录用户ID
     * 通过用户名获取用户ID
     */
    public static String getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }

        // 通过用户名查询用户信息
        User user = userService.getUserByUsername(username);
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return authentication.getName();
        }
        return null;
    }
}
    
