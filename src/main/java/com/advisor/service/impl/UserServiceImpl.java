package com.advisor.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.advisor.entity.base.User;
import com.advisor.entity.base.VerificationCode;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.base.VerificationCodeMapper;
import com.advisor.service.UserService;
import com.advisor.service.userbehavior.UserLoginLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserLoginLogService userLoginLogService;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public void sendVerificationCode(String email) {
        // 1. 检查邮箱是否已被注册
        User existingUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, email)
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
        User existingUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
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
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // 密码加密
        newUser.setEmail(email);
        newUser.setStatus(1); // 默认状态为正常
        newUser.setCreateTime(LocalDateTime.now());
        userMapper.insert(newUser);
    }

    @Override
    public User login(String username, String password) {
        // 只使用IP地址为null的登录方法
        return login(username, password, null);
    }
    
    @Override
    public User login(String username, String password, String ipAddress) {
        System.out.println("尝试登录用户: " + username);
        
        // 1. 查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        
        if (user == null) {
            System.out.println("用户不存在: " + username);
            // 记录登录失败日志
            if (username != null) {
                userLoginLogService.recordLoginFailed(null, ipAddress, "用户不存在");
            }
            throw new RuntimeException("用户名或密码错误");
        }

        System.out.println("找到用户: " + username);
        System.out.println("存储的密码: " + user.getPassword());
        
        // 2. 验证密码
        try {
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("密码匹配结果: " + matches);
            
            if (!matches) {
                // 记录登录失败日志
                userLoginLogService.recordLoginFailed(user.getId(), ipAddress, "密码错误");
                throw new RuntimeException("用户名或密码错误");
            }
        } catch (Exception e) {
            System.out.println("密码验证错误: " + e.getMessage());
            System.out.println("输入密码: " + password);
            System.out.println("存储密码: " + user.getPassword());
            // 记录登录失败日志
            userLoginLogService.recordLoginFailed(user.getId(), ipAddress, "密码验证异常: " + e.getMessage());
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 4. 生成JWT令牌
        String token = generateToken(user.getId(),username);
        user.setToken(token);
        
        // 记录登录成功日志
        userLoginLogService.recordLoginSuccess(user.getId(), ipAddress);
        
        // 不返回密码
        user.setPassword(null);
        
        System.out.println("用户登录成功: " + username);
        return user;
    }

    @Override
    public User getUserByToken(String token) {
        try {
            System.out.println("开始解析token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
            
            // 使用新的 API
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            System.out.println("Token解析成功，claims: " + claims);
            
            String userId = claims.getSubject();
            System.out.println("从token中获取的userId: " + userId);
            
            User user = userMapper.selectById(userId);
            System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
            
            if (user != null) {
                user.setPassword(null); // 不返回密码
            }
            return user;
        } catch (Exception e) {
            System.out.println("解析Token时发生异常: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 生成 Token 方法
    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 根据用户ID获取用户信息
     */
    @Override
    public User getUserById(String userId) {
        return getById(userId);
    }
    
    /**
     * 更新用户信息
     */
    @Override
    public void updateUser(User user) {
        updateById(user);
    }
    
    /**
     * 检查用户名是否可用
     */
    @Override
    public boolean isUsernameAvailable(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return count(wrapper) == 0;
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        // 获取用户
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }

        // 验证新密码格式
        if (!isValidPassword(newPassword)) {
            throw new RuntimeException("新密码不符合要求");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    /**
     * 验证密码是否符合要求
     */
    private boolean isValidPassword(String password) {
        // 至少8位
        if (password.length() < 8) return false;
        
        // 包含大小写字母和数字
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}