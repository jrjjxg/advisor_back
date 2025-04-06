package com.advisor.service.alert.rule.impl;

import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.test.TestResult;
import com.advisor.service.alert.collector.DataSourceType;
import com.advisor.service.alert.rule.RiskAssessmentRule;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import org.springframework.stereotype.Component;

/**
 * 焦虑测试风险评估规则
 */
@Component
public class AnxietyTestRule implements RiskAssessmentRule {
    
    // 定义焦虑测试的测试类型ID - 这里需要根据实际数据库中的ID进行配置
    private static final String ANXIETY_TEST_TYPE_ID = "sas";
    
    private static final String RULE_ID = "anxiety-test-rule";
    private static final String RULE_NAME = "焦虑测试风险评估";
    
    @Override
    public RiskEvaluationResult evaluate(Object data) {
        if (!(data instanceof TestResult)) {
            return createDefaultResult();
        }
        
        TestResult testResult = (TestResult) data;
        
        // 检查是否是焦虑测试结果 - 使用testTypeId而不是TestType对象
        if (testResult.getTestTypeId() == null || !ANXIETY_TEST_TYPE_ID.equals(testResult.getTestTypeId())) {
            return createDefaultResult();
        }
        
        int score = testResult.getTotalScore();
        RiskLevel riskLevel;
        String description;
        String suggestion;
        
        if (score >= 15) {
            riskLevel = RiskLevel.SEVERE;
            description = "测试结果显示存在严重焦虑症状";
            suggestion = "建议尽快咨询专业心理医生或精神科医生";
        } else if (score >= 10) {
            riskLevel = RiskLevel.MODERATE;
            description = "测试结果显示存在中度焦虑症状";
            suggestion = "建议寻求心理咨询师的帮助，学习焦虑管理技巧";
        } else if (score >= 5) {
            riskLevel = RiskLevel.MILD;
            description = "测试结果显示存在轻度焦虑症状";
            suggestion = "建议学习放松技巧和压力管理方法，必要时寻求支持";
        } else {
            riskLevel = RiskLevel.NORMAL;
            description = "测试结果显示焦虑水平在正常范围内";
            suggestion = "继续保持健康的生活方式和良好的压力管理";
        }
        
        return RiskEvaluationResult.create(
            RULE_ID,
            RULE_NAME,
            riskLevel,
            String.valueOf(testResult.getId()),
            DataSourceType.TEST_RESULT.name(),
            description,
            suggestion
        );
    }
    
    @Override
    public Class<?> getDataType() {
        return TestResult.class;
    }
    
    @Override
    public String getId() {
        return RULE_ID;
    }
    
    @Override
    public String getName() {
        return RULE_NAME;
    }
    
    private RiskEvaluationResult createDefaultResult() {
        return RiskEvaluationResult.create(
            RULE_ID,
            RULE_NAME,
            RiskLevel.NORMAL,
            null,
            DataSourceType.TEST_RESULT.name(),
            "未发现风险",
            "无需干预"
        );
    }
} 