package com.advisor.service.alert.collector;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {
    TEST_RESULT("测试结果"),
    MOOD_RECORD("情绪记录"),
    JOURNAL("日记内容"),
    USER_BEHAVIOR("用户行为"),
    CUSTOM("自定义数据源");
    
    private final String name;
    
    DataSourceType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
} 