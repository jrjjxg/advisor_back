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

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;

    private final FileService fileService;

    @Autowired
    public UserController(FileService fileService) {
        this.fileService = fileService;
    }
    
    @PostMapping("/register/send-code")
    public Result<?> sendVerificationCode(@RequestBody EmailRequest emailRequest) {
        userService.sendVerificationCode(emailRequest.getEmail());
        return Result.success(null);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(), request.getEmail(), request.getCode());
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return Result.success(user);
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        // 移动端登出只需要客户端清除token即可
        return Result.success(null);
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        System.out.println("接收到的完整token: " + token);
        
        String processedToken = token.replace("Bearer ", "");
        System.out.println("处理后的token: " + processedToken);
        
        try {
            User user = userService.getUserByToken(processedToken);
            System.out.println("getUserByToken返回结果: " + (user == null ? "null" : user.getUsername()));
            
            if (user == null) {
                return Result.fail(401, "未登录");
            }
            return Result.success(user);
        } catch (Exception e) {
            System.out.println("getUserByToken发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.fail(401, "Token处理异常: " + e.getMessage());
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
        // 设置其他需要的字段，如关注数、粉丝数等
        
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
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return Result.success(available);
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


@PostMapping("/upload")
public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
        String fileUrl = fileService.uploadFile(file);
        return Result.success(fileUrl);
    } catch (Exception e) {
        return Result.fail(500, e.getMessage());
    }
}
}
