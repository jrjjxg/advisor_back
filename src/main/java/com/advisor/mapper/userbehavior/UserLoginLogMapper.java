package com.advisor.mapper.userbehavior;

import com.advisor.entity.userbehavior.UserLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录日志Mapper接口
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
} 