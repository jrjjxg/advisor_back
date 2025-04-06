package com.advisor.service.alert.rule.impl;

import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.mood.MoodRecord;
import com.advisor.service.alert.collector.DataSourceType;
import com.advisor.service.alert.rule.RiskAssessmentRule;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 负面情绪风险评估规则
 */
@Component
public class NegativeMoodRule implements RiskAssessmentRule {
    
    private static final String RULE_ID = "negative-mood-rule";
    private static final String RULE_NAME = "负面情绪风险评估";
    
    // 定义高风险情绪类型
    private static final Set<String> HIGH_RISK_EMOTIONS = new HashSet<>(Arrays.asList(
        "悲伤/低落", "绝望", "无助", "愤怒/烦躁", "恐惧/害怕"
    ));
    
    // 定义中风险情绪类型
    private static final Set<String> MEDIUM_RISK_EMOTIONS = new HashSet<>(Arrays.asList(
        "焦虑/紧张", "疲惫/无力", "失望", "内疚", "羞耻"
    ));
    
    @Override
    public RiskEvaluationResult evaluate(Object data) {
        if (!(data instanceof MoodRecord)) {
            return createDefaultResult();
        }
        
        MoodRecord moodRecord = (MoodRecord) data;
        String emotionType = moodRecord.getEmotionType();
        Integer intensity = moodRecord.getIntensity();
        
        // 如果强度为空，默认设置为5
        if (intensity == null) {
            intensity = 5;
        }
        
        RiskLevel riskLevel;
        String description;
        String suggestion;
        
        // 评估风险级别
        if (HIGH_RISK_EMOTIONS.contains(emotionType) && intensity >= 8) {
            riskLevel = RiskLevel.SEVERE;
            description = "检测到强烈的负面情绪状态";
            suggestion = "建议寻求社会支持或专业帮助，了解情绪调节技巧";
        } else if (HIGH_RISK_EMOTIONS.contains(emotionType) || (MEDIUM_RISK_EMOTIONS.contains(emotionType) && intensity >= 8)) {
            riskLevel = RiskLevel.MODERATE;
            description = "检测到明显的负面情绪状态";
            suggestion = "建议进行放松活动，如深呼吸、冥想或运动，必要时寻求支持";
        } else if (MEDIUM_RISK_EMOTIONS.contains(emotionType) || intensity >= 8) {
            riskLevel = RiskLevel.MILD;
            description = "检测到轻微的负面情绪状态";
            suggestion = "建议关注自身情绪变化，尝试积极的应对策略";
        } else {
            riskLevel = RiskLevel.NORMAL;
            description = "情绪状态在正常范围内";
            suggestion = "继续保持健康的情绪管理";
        }
        
        //!!!!
        //数据库中id是long类型，需要转换为字符串
        //!!!!
        //数据库中id是long类型，需要转换为字符串
        //!!!!
        //数据库中id是long类型，需要转换为字符串
        //!!!!
        //数据库中id是long类型，需要转换为字符串
        //!!!!
        return RiskEvaluationResult.create(
            RULE_ID,
            RULE_NAME,
            riskLevel,
            String.valueOf(moodRecord.getId()),
            DataSourceType.MOOD_RECORD.name(),
            description,
            suggestion
        );
    }
    
    @Override
    public Class<?> getDataType() {
        return MoodRecord.class;
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
            DataSourceType.MOOD_RECORD.name(),
            "未发现风险",
            "无需干预"
        );
    }
} 