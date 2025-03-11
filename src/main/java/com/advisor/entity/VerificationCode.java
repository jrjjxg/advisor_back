package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verification_code")
public class VerificationCode {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String email;
    private String code;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private Boolean used;
}