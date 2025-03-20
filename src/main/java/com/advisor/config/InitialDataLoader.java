package com.advisor.config;

import com.advisor.entity.base.User;
import com.advisor.mapper.base.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在测试用户
        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, "testuser")
        );

        if (existingUser == null) {
            // 创建测试用户
            User testUser = new User();
            testUser.setId(UUID.randomUUID().toString());
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("password123")); // 使用BCrypt加密
            testUser.setEmail("test@example.com");
            testUser.setNickname("测试用户");
            testUser.setStatus(1); // 正常状态
            testUser.setCreateTime(LocalDateTime.now());
            testUser.setUpdateTime(LocalDateTime.now());
            testUser.setFollowCount(0);
            testUser.setFansCount(0);
            testUser.setPostCount(0);
            testUser.setLetterUnread(0);
            testUser.setCommentUnread(0);
            testUser.setAtUnread(0);
            testUser.setNotificationUnread(0);
            testUser.setLikeUnread(0);
            
            // 插入用户
            userMapper.insert(testUser);
            
            System.out.println("测试用户已创建: testuser / password123");
        } else {
            System.out.println("测试用户已存在，无需创建");
            
            // 如果密码不是BCrypt格式，更新密码
            if (!existingUser.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode("password123"));
                userMapper.updateById(existingUser);
                System.out.println("已更新测试用户密码为BCrypt格式");
            }
        }
    }
}