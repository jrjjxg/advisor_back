package com.advisor.controller.auth;
import com.advisor.service.FileService;
import com.advisor.common.Result;
import com.advisor.dto.*;
import com.advisor.entity.base.User;
import com.advisor.service.UserService;
import com.advisor.util.UserUtil;
import com.advisor.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import com.advisor.service.community.FollowService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    private final FileService fileService;

    @Autowired
    public UserController(FileService fileService) {
        this.fileService = fileService;
    }
    
        @PostMapping("/register/send-code")
        public Result<?> sendVerificationCode(@RequestBody EmailRequest emailRequest) {
            boolean sentSuccessfully = userService.sendVerificationCode(emailRequest.getEmail());
            if (sentSuccessfully) {
                return Result.success(null);
            } else {
                return Result.fail(500, "验证码发送失败，请稍后重试");
            }
        }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(), request.getEmail(), request.getCode());
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = userService.login(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            return Result.success(user);
        } catch (RuntimeException e) {
            if ("账号已被禁用，请遵守社区规范。如有疑问，请咨询管理员2902756263@qq.com".equals(e.getMessage())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return Result.fail(HttpStatus.FORBIDDEN.value(), e.getMessage());
            } else if ("用户名或密码错误".equals(e.getMessage())) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return Result.fail(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "登录时发生内部错误");
            }
        }
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        // 移动端登出只需要客户端清除token即可
        return Result.success(null);
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.fail(401, "未登录或Token无效");
        }

        String username = authentication.getName();

        try {
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return Result.fail(404, "用户不存在");
            }
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.fail(500, "获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户个人资料
     */
    @GetMapping("/profile/{userId}")
    public Result<UserVO> getUserProfile(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 手动映射名称不同的字段
        userVO.setFollowingCount(user.getFollowCount()); // 从 User 实体的 followCount 映射
        userVO.setFollowerCount(user.getFansCount());   // 从 User 实体的 fansCount 映射

        // 检查当前登录用户是否关注了该用户 (保留这部分逻辑)
        String currentUserId = UserUtil.getCurrentUserId();
        if (currentUserId != null && !currentUserId.equals(userId)) {
            boolean isFollowed = followService.checkFollowed(userId, currentUserId);
            userVO.setIsFollowed(isFollowed);
        } else {
            userVO.setIsFollowed(false); // 自己不能关注自己，或者未登录
        }
        
        return Result.success(userVO);
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public Result<?> updateUserInfo(@RequestBody User userInfo) {
        String username = UserUtil.getCurrentUsername();
        if (username == null) {
            return Result.fail(401, "用户未登录");
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        // 只允许更新部分字段
        user.setNickname(userInfo.getNickname());
        user.setAvatar(userInfo.getAvatar());
        user.setGender(userInfo.getGender());
        user.setBirthDate(userInfo.getBirthDate());
        user.setPhone(userInfo.getPhone());
        
        userService.updateUser(user);
        return Result.success(null);
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/avatar")
    public Result<?> updateAvatar(@RequestBody UpdateAvatarRequest request) {
        String username = UserUtil.getCurrentUsername();
        if (username == null) {
            return Result.fail(401, "用户未登录");
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        user.setAvatar(request.getAvatarUrl());
        userService.updateUser(user);
        return Result.success(null);
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<?> checkUsername(@RequestParam String username, HttpServletResponse response) {
        boolean exists = userService.checkUsernameExists(username);
        if (exists) {
            response.setStatus(HttpStatus.CONFLICT.value());
            return Result.fail(HttpStatus.CONFLICT.value(), "用户名已存在");
        } else {
            return Result.success(null);
        }
    }

    /**
     * 新增：检查邮箱是否已被注册
     */
    @GetMapping("/check-email")
    public Result<?> checkEmail(@RequestParam String email, HttpServletResponse response) {
        boolean exists = userService.checkEmailExists(email);
        if (exists) {
            response.setStatus(HttpStatus.CONFLICT.value());
            return Result.fail(HttpStatus.CONFLICT.value(), "邮箱已被注册");
        } else {
            return Result.success(null);
        }
    }
    
    @PostMapping("/password")
    public Result<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        String username = UserUtil.getCurrentUsername();
        if (username == null) {
            return Result.fail(401, "用户未登录");
        }
        
        try {
            userService.updatePassword(username, request.getOldPassword(), request.getNewPassword());
            return Result.success(null);
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户ID
     * 此接口用于Python后端调用
     */
    @GetMapping("/get-user-id-by-username")
    public Result<String> getUserIdByUsername(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.success(user.getId());
    }



}
