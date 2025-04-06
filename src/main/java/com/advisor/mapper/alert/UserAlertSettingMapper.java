package com.advisor.mapper.alert;

import com.advisor.entity.alert.UserAlertSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户预警设置Mapper接口
 */
@Mapper
public interface UserAlertSettingMapper extends BaseMapper<UserAlertSetting> {
} 