package com.advisor.service.alert.rule.impl;

import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.journal.Journal;
import com.advisor.service.alert.collector.DataSourceType;
import com.advisor.service.alert.rule.RiskAssessmentRule;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日记内容风险评估规则
 */
@Component
public class JournalContentRule implements RiskAssessmentRule {
    
    private static final String RULE_ID = "journal-content-rule";
    private static final String RULE_NAME = "日记内容风险评估";
    
    // 极高风险关键词
    private static final Set<String> CRITICAL_KEYWORDS = new HashSet<>(Arrays.asList(
        "自杀", "结束生命", "不想活了", "想死", "告别", "遗书", "永别"
    ));
    
    // 高风险关键词
    private static final Set<String> SEVERE_KEYWORDS = new HashSet<>(Arrays.asList(
        "绝望", "痛苦", "折磨", "无助", "崩溃", "活不下去", "解脱", "受不了了"
    ));
    
    // 中风险关键词
    private static final Set<String> MODERATE_KEYWORDS = new HashSet<>(Arrays.asList(
        "抑郁", "焦虑", "失眠", "恐惧", "恐慌", "无意义", "绝境", "噩梦", "悲伤"
    ));
    
    // 轻度风险关键词
    private static final Set<String> MILD_KEYWORDS = new HashSet<>(Arrays.asList(
        "压力", "疲惫", "难过", "伤心", "失望", "担忧", "孤独", "哭泣"
    ));
    
    @Override
    public RiskEvaluationResult evaluate(Object data) {
        if (!(data instanceof Journal)) {
            return createDefaultResult();
        }
        
        Journal journal = (Journal) data;
        String content = journal.getContent();
        
        if (content == null || content.isEmpty()) {
            return createDefaultResult();
        }
        
        // 评估风险级别
        RiskLevel riskLevel = RiskLevel.NORMAL;
        String description = "";
        String suggestion = "";
        
        // 匹配极高风险关键词
        if (containsAnyKeyword(content, CRITICAL_KEYWORDS)) {
            riskLevel = RiskLevel.CRITICAL;
            description = "日记内容中包含极高风险表达，可能存在自伤或自杀风险";
            suggestion = "建议立即寻求专业心理干预，联系心理危机热线或前往医院精神科就诊";
        }
        // 匹配高风险关键词
        else if (containsAnyKeyword(content, SEVERE_KEYWORDS)) {
            riskLevel = RiskLevel.SEVERE;
            description = "日记内容表达了强烈的负面情绪和痛苦";
            suggestion = "建议尽快联系专业心理咨询师，获取专业支持和帮助";
        }
        // 匹配中风险关键词
        else if (containsAnyKeyword(content, MODERATE_KEYWORDS)) {
            riskLevel = RiskLevel.MODERATE;
            description = "日记内容表达了较明显的消极情绪";
            suggestion = "建议进行情绪管理，可考虑寻求心理咨询支持";
        }
        // 匹配轻度风险关键词
        else if (containsAnyKeyword(content, MILD_KEYWORDS)) {
            riskLevel = RiskLevel.MILD;
            description = "日记内容表达了轻微的负面情绪";
            suggestion = "建议关注自身情绪变化，适当进行放松活动和社交支持";
        }
        
        // 只有发现风险才返回评估结果
        if (riskLevel != RiskLevel.NORMAL) {
            return RiskEvaluationResult.create(
                RULE_ID,
                RULE_NAME,
                riskLevel,
                String.valueOf(journal.getId()),
                DataSourceType.JOURNAL.name(),
                description,
                suggestion
            );
        }
        
        return createDefaultResult();
    }
    
    /**
     * 检查内容中是否包含任何指定关键词
     */
    private boolean containsAnyKeyword(String content, Set<String> keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Class<?> getDataType() {
        return Journal.class;
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
            DataSourceType.JOURNAL.name(),
            "未发现风险",
            "无需干预"
        );
    }
} 