package com.advisor.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
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
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 关注数
     */
    private Integer followCount;
    
    /**
     * 粉丝数
     */
    private Integer fansCount;
    
    /**
     * 未读私信数
     */
    private Integer letterUnread;
    
    /**
     * 未读评论数
     */
    private Integer commentUnread;
    
    /**
     * 未读@数
     */
    private Integer atUnread;
    
    /**
     * 未读通知数
     */
    private Integer notificationUnread;
    
    /**
     * 个人简介
     */
    private String description;
    
    /**
     * 帖子数
     */
    private Integer postCount;
    
    /**
     * 未读点赞数
     */
    private Integer likeUnread;
    
    /**
     * 令牌
     */
    @TableField(exist = false)
    private String token;
}