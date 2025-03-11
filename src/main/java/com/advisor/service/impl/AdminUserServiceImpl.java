package com.advisor.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.advisor.entity.AdminUser;
import com.advisor.entity.VerificationCode;
import com.advisor.mapper.AdminUserMapper;
import com.advisor.mapper.VerificationCodeMapper;
import com.advisor.service.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationCode(String email) {
        // 1. 检查邮箱是否已被注册
        AdminUser existingUser = adminUserMapper.selectOne(
            new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getEmail, email)
        );
        if (existingUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 2. 生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 3. 保存验证码到数据库
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟后过期
        verificationCode.setUsed(false);
        verificationCodeMapper.insert(verificationCode);

        // 4. 发送邮件
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("注册验证码");
            helper.setText("您的验证码是：" + code + "，有效期为5分钟。", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    @Override
    @Transactional
    public void register(String username, String password, String email, String code) {
        // 1. 检查用户名是否已存在
        AdminUser existingUser = adminUserMapper.selectOne(
            new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username)
        );
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 验证验证码
        VerificationCode verificationCode = verificationCodeMapper.selectOne(
            new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getEmail, email)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getUsed, false)
                .gt(VerificationCode::getExpireTime, LocalDateTime.now())
        );
        if (verificationCode == null) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 3. 标记验证码为已使用
        verificationCode.setUsed(true);
        verificationCodeMapper.updateById(verificationCode);

        // 4. 创建用户
        AdminUser newUser = new AdminUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // 密码加密
        newUser.setEmail(email);
        newUser.setStatus(1); // 默认状态为正常
        adminUserMapper.insert(newUser);
    }

    @Override
    public AdminUser login(String username, String password) {
        // 1. 根据用户名查询用户
        AdminUser user = adminUserMapper.selectOne(
            new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username)
        );
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(user);

        return user;
    }

    @Override
    public AdminUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, ((UserDetails) principal).getUsername()));
        }
        return null;
    }
}