package com.advisor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.advisor.entity.FacialEmotionHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为 Mapper 接口，让 Spring Boot 扫描到
public interface FacialEmotionHistoryMapper extends BaseMapper<FacialEmotionHistory> {
    // BaseMapper 提供了常用的 CRUD 方法
    // 如果有复杂 SQL，可以在这里定义方法，并在 XML 文件中实现或使用 MP 的注解 SQL
} 