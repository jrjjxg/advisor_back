package com.advisor.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.advisor.dto.UserQueryDTO;
import com.advisor.entity.base.User;
import com.advisor.entity.base.VerificationCode;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.base.VerificationCodeMapper;
import com.advisor.service.UserService;
import com.advisor.service.userbehavior.UserLoginLogService;
import com.advisor.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.advisor.vo.UserManagementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import java.util.Collections;

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

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public boolean sendVerificationCode(String email) {
        // 1. 检查邮箱是否已被注册
        User existingUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );
        if (existingUser != null) {
            return false;
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
            helper.setSubject("注册Uniheart");
            helper.setText("您的验证码是：" + code + "，有效期为5分钟。", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
        return true;
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

        // 新增: 校验密码强度
        if (!isValidPassword(password)) {
            throw new RuntimeException("密码不符合要求 (至少8位，包含大小写字母和数字)");
        }

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

        // 新增：检查用户状态
        if (user.getStatus() == 0) {
            System.out.println("用户账号已被禁用: " + username);
            // 记录登录失败日志
            userLoginLogService.recordLoginFailed(user.getId(), ipAddress, "账号已被禁用");
            throw new RuntimeException("账号已被禁用，请遵守社区规范。如有疑问，请咨询管理员2902756263@qq.com");
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

        // 4. 使用 JwtUtil 生成 JWT 令牌，包含 ROLE_USER 角色
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), Collections.singletonList("ROLE_USER"));
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
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        System.out.println("开始解析token (getUserByToken): " + actualToken.substring(0, Math.min(actualToken.length(), 20)) + "...");

        try {
            // 使用 JwtUtil 从 Token 获取 userId
            String userId = jwtUtil.getUserIdFromToken(actualToken);
            System.out.println("从token中获取的userId: " + userId);

            if (userId == null) {
                System.out.println("无法从Token中解析出userId");
                return null; // Token 无效或解析失败
            }

            User user = userMapper.selectById(userId);
            System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
            
            if (user != null) {
                user.setPassword(null); // 不返回密码
            }
            return user;
        } catch (Exception e) {
            // JwtUtil 内部已经打印了错误日志
            System.out.println("getUserByToken 发生异常: " + e.getMessage());
            // e.printStackTrace(); // JwtUtil 已处理日志，这里可以选择不打印
            return null; // 明确返回 null 表示失败
        }
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

    @Override
    public IPage<UserManagementVO> listUsersForAdmin(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 添加筛选条件
        if (StringUtils.isNotBlank(queryDTO.getUsername())) {
            wrapper.like(User::getUsername, queryDTO.getUsername());
        }
        if (StringUtils.isNotBlank(queryDTO.getEmail())) {
            wrapper.like(User::getEmail, queryDTO.getEmail());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        // 修改后的排序逻辑
        boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
        String sortBy = queryDTO.getSortBy();

        if (StringUtils.isNotBlank(sortBy)) {
            // 根据前端传入的字段名决定调用哪个 orderBy 方法
            if ("username".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getUsername);
            } else if ("nickname".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getNickname);
            } else if ("email".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getEmail);
            } else if ("status".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getStatus);
            } else if ("createTime".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getCreateTime);
            } else if ("lastLoginTime".equalsIgnoreCase(sortBy)) {
                wrapper.orderBy(true, isAsc, User::getLastLoginTime);
            } else {
                // 如果传入的 sortBy 不合法或未匹配，默认按创建时间降序
                wrapper.orderByDesc(User::getCreateTime);
            }
        } else {
            // 如果没有传入 sortBy，默认按创建时间降序
            wrapper.orderByDesc(User::getCreateTime);
        }
        
        // 执行查询
        IPage<User> userPage = this.page(page, wrapper);

        // 转换结果为 UserManagementVO
        IPage<UserManagementVO> voPage = userPage.convert(user -> {
            UserManagementVO vo = new UserManagementVO();
            BeanUtils.copyProperties(user, vo);
            // 注意：这里返回给管理员的列表信息，是否需要包含 Token？通常不需要。
            // 如果需要，可以在这里设置 vo.setToken(jwtUtil.generateToken(...)); 但一般不推荐
            return vo;
        });

        return voPage;
    }

    @Override
    public void updateUserStatus(String userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验状态值是否合法 (Controller 层已有校验，这里可以省略或再次校验)
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("无效的用户状态值");
        }
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void deleteUser(String userId) {
        // 可以根据业务需求选择逻辑删除或物理删除
        // 逻辑删除示例: (需要 User 实体有 isDeleted 字段和 @TableLogic 注解)
        // User user = this.getById(userId);
        // if (user != null) {
        //     this.removeById(userId);
        // }

        // 物理删除示例:
        boolean removed = this.removeById(userId);
        if (!removed) {
            // 可以选择抛出异常或记录日志，表明用户可能不存在
             System.out.println("尝试删除用户失败，用户可能不存在: " + userId);
            // throw new RuntimeException("删除用户失败，用户不存在");
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return userMapper.selectCount(queryWrapper) > 0;
    }
}