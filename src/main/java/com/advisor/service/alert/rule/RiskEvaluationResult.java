package com.advisor.service.alert.rule;

import com.advisor.entity.alert.RiskLevel;
import lombok.Data;

/**
 * 风险评估结果
 */
@Data
public class RiskEvaluationResult {
    private String ruleId;
    private String ruleName;
    private RiskLevel riskLevel;
    private String dataSourceId;
    private String dataSourceType;
    private String description;
    private String suggestion;
    
    // 创建评估结果的便捷方法
    public static RiskEvaluationResult create(String ruleId, String ruleName, RiskLevel riskLevel, 
                                             String dataSourceId, String dataSourceType, 
                                             String description, String suggestion) {
        RiskEvaluationResult result = new RiskEvaluationResult();
        result.setRuleId(ruleId);
        result.setRuleName(ruleName);
        result.setRiskLevel(riskLevel);
        result.setDataSourceId(dataSourceId);
        result.setDataSourceType(dataSourceType);
        result.setDescription(description);
        result.setSuggestion(suggestion);
        return result;
    }

    // 添加一个接受Object类型ID的重载方法
    public static RiskEvaluationResult create(String ruleId, String ruleName, RiskLevel riskLevel, 
                                             Object dataSourceId, String dataSourceType, 
                                             String description, String suggestion) {
        String dataSourceIdStr = dataSourceId != null ? String.valueOf(dataSourceId) : null;
        return create(ruleId, ruleName, riskLevel, dataSourceIdStr, dataSourceType, description, suggestion);
    }
} 