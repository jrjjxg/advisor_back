package com.advisor.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户视图对象
 */
@Data
public class UserVO {
    
    /**
     * 用户ID
     */
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthDate;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 注册时间
     */
    private LocalDateTime createTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 是否为专家
     */
    private Boolean isExpert;
    
    /**
     * 帖子数量
     */
    private Integer postCount;
    
    /**
     * 关注数
     */
    private Integer followingCount;
    
    /**
     * 粉丝数
     */
    private Integer followerCount;
    
    /**
     * 是否已关注
     */
    private Boolean isFollowed;
}
