package com.advisor.service.alert.rule;

import com.advisor.entity.alert.RiskLevel;

/**
 * 风险评估规则接口
 */
public interface RiskAssessmentRule {
    /**
     * 评估风险级别
     * @param data 评估数据
     * @return 风险评估结果
     */
    RiskEvaluationResult evaluate(Object data);
    
    /**
     * 获取规则适用的数据类型
     * @return 数据类型
     */
    Class<?> getDataType();
    
    /**
     * 获取规则ID
     * @return 规则ID
     */
    String getId();
    
    /**
     * 获取规则名称
     * @return 规则名称
     */
    String getName();
} 