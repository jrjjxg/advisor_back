package com.advisor.service.alert.rule.impl;

import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.test.TestResult;
import com.advisor.service.alert.collector.DataSourceType;
import com.advisor.service.alert.rule.RiskAssessmentRule;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抑郁测试风险评估规则
 */
@Component
public class DepressionTestRule implements RiskAssessmentRule {
    
    // 定义抑郁测试的测试类型ID - 这里需要根据实际数据库中的ID进行配置
    private static final String DEPRESSION_TEST_TYPE_ID = "sds";
    
    private static final String RULE_ID = "depression-test-rule";
    private static final String RULE_NAME = "抑郁测试风险评估";
    
    @Override
    public RiskEvaluationResult evaluate(Object data) {
        if (!(data instanceof TestResult)) {
            return createDefaultResult();
        }
        
        TestResult testResult = (TestResult) data;
        
        // 检查是否是抑郁测试结果 - 使用testTypeId而不是TestType对象
        if (testResult.getTestTypeId() == null || !DEPRESSION_TEST_TYPE_ID.equals(testResult.getTestTypeId())) {
            return createDefaultResult();
        }
        
        int score = testResult.getTotalScore();
        RiskLevel riskLevel;
        String description;
        String suggestion;
        
        if (score >= 20) {
            riskLevel = RiskLevel.CRITICAL;
            description = "测试结果显示存在严重抑郁症状";
            suggestion = "建议立即寻求专业心理医生的帮助，必要时可考虑住院治疗";
        } else if (score >= 15) {
            riskLevel = RiskLevel.SEVERE;
            description = "测试结果显示存在中度至重度抑郁症状";
            suggestion = "建议尽快咨询专业心理医生或精神科医生";
        } else if (score >= 10) {
            riskLevel = RiskLevel.MODERATE;
            description = "测试结果显示存在轻度至中度抑郁症状";
            suggestion = "建议寻求心理咨询师的帮助，了解改善情绪的方法";
        } else if (score >= 5) {
            riskLevel = RiskLevel.MILD;
            description = "测试结果显示存在轻微抑郁症状";
            suggestion = "建议关注自身情绪变化，增加积极活动，必要时寻求支持";
        } else {
            riskLevel = RiskLevel.NORMAL;
            description = "测试结果显示心理状态正常";
            suggestion = "继续保持健康的生活方式和积极心态";
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