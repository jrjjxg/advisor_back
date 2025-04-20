package com.advisor.vo.userbehavior;

import lombok.Data;
import java.util.List;

/**
 * 用户功能偏好视图对象
 */
@Data
public class FeaturePreferenceVO {
    
    /**
     * 最常用功能
     */
    private String mostUsedFeature;
    
    /**
     * 功能使用总次数
     */
    private int totalCount;
    
    /**
     * 功能使用列表（排序后）
     */
    private List<FeatureCount> topFeatures;
    
    /**
     * 功能使用计数内部类
     */
    @Data
    public static class FeatureCount {
        /**
         * 功能名称
         */
        private String name;
        
        /**
         * 使用次数
         */
        private int count;
        
        /**
         * 使用时长（毫秒）
         */
        private Long duration;
    }
} 