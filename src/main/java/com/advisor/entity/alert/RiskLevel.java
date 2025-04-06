package com.advisor.entity.alert;

import lombok.Getter;

/**
 * 风险等级枚举
 */
@Getter
public enum RiskLevel {
    NORMAL(1, "正常", "心理状态正常，无需干预"),
    MILD(2, "轻度风险", "存在轻微心理健康问题，建议关注"),
    MODERATE(3, "中度风险", "存在明显心理健康问题，建议采取措施"),
    SEVERE(4, "严重风险", "存在严重心理健康问题，需要及时干预"),
    CRITICAL(5, "紧急风险", "存在极端心理健康问题，需要立即干预");
    
    private final int level;
    private final String name;
    private final String description;
    
    RiskLevel(int level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }
    
    public static RiskLevel fromLevel(int level) {
        for (RiskLevel riskLevel : values()) {
            if (riskLevel.getLevel() == level) {
                return riskLevel;
            }
        }
        return NORMAL;
    }
} 