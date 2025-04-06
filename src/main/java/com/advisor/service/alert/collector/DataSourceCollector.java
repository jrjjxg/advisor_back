package com.advisor.service.alert.collector;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据源收集器接口
 */
public interface DataSourceCollector<T> {
    /**
     * 收集指定用户的数据
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 收集的数据
     */
    List<T> collectData(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取数据源类型
     * @return 数据源类型
     */
    DataSourceType getType();
} 