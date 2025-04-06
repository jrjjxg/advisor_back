package com.advisor.mapper.alert;

import com.advisor.entity.alert.AlertRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警记录Mapper接口
 */
@Mapper
public interface AlertMapper extends BaseMapper<AlertRecord> {
} 