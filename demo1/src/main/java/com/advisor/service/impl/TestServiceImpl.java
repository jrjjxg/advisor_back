package com.advisor.service.impl;

import com.advisor.entity.*;
import com.advisor.mapper.*;
import com.advisor.service.TestService;
import com.advisor.vo.QuestionVO;
import com.advisor.vo.TestResultVO;
import com.advisor.vo.TestTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestTypeMapper testTypeMapper;
    
    @Autowired
    private TestQuestionMapper testQuestionMapper;
    
    @Autowired
    private QuestionOptionMapper questionOptionMapper;
    
    @Autowired
    private TestResultMapper testResultMapper;
    
    @Autowired
    private UserAnswerMapper userAnswerMapper;
    
    @Autowired
    private PsychologicalProfileMapper profileMapper;

    @Override
    public List<TestTypeVO> getAllTestTypes() {
        List<TestType> testTypes = testTypeMapper.selectList(
            new LambdaQueryWrapper<TestType>()
                .eq(TestType::getStatus, 1)
                .orderByAsc(TestType::getCategory)
        );
        
        return testTypes.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public TestTypeVO getTestTypeDetail(String testTypeId) {
        TestType testType = testTypeMapper.selectById(testTypeId);
        if (testType == null) {
            return null;
        }
        return convertToVO(testType);
    }

    @Override
    public List<QuestionVO> getTestQuestions(String testTypeId) {
        // 查询测试问题
        List<TestQuestion> questions = testQuestionMapper.selectList(
            new LambdaQueryWrapper<TestQuestion>()
                .eq(TestQuestion::getTestTypeId, testTypeId)
                .orderByAsc(TestQuestion::getOrderNum)
        );
        
        // 获取所有问题ID
        List<String> questionIds = questions.stream()
            .map(TestQuestion::getId)
            .collect(Collectors.toList());
            
        // 查询所有选项
        List<QuestionOption> options = new ArrayList<>();
        if (!questionIds.isEmpty()) {
            options = questionOptionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                    .in(QuestionOption::getQuestionId, questionIds)
                    .orderByAsc(QuestionOption::getOrderNum)
            );
        }
        
        // 按问题ID分组选项
        Map<String, List<QuestionOption>> optionMap = options.stream()
            .collect(Collectors.groupingBy(QuestionOption::getQuestionId));
            
        // 构建问题VO
        return questions.stream().map(q -> {
            QuestionVO vo = new QuestionVO();
            BeanUtils.copyProperties(q, vo);
            
            List<QuestionOption> questionOptions = optionMap.getOrDefault(q.getId(), Collections.emptyList());
            vo.setOptions(questionOptions);
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TestResultVO submitTestAnswers(String userId, String testTypeId, Map<String, String> answers) {
        // 1. 创建测试结果记录
        TestResult testResult = new TestResult();
        testResult.setId(UUID.randomUUID().toString());
        testResult.setUserId(userId);
        testResult.setTestTypeId(testTypeId);
        testResult.setStartTime(LocalDateTime.now().minusMinutes(10)); // 假设测试开始于10分钟前
        testResult.setEndTime(LocalDateTime.now());
        
        // 2. 计算得分并保存答案
        int totalScore = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String questionId = entry.getKey();
            String optionId = entry.getValue();
            
            // 查询选项得分
            QuestionOption option = questionOptionMapper.selectById(optionId);
            if (option != null) {
                totalScore += option.getScore();
                
                // 创建用户答案记录
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setId(UUID.randomUUID().toString());
                userAnswer.setTestResultId(testResult.getId());
                userAnswer.setQuestionId(questionId);
                userAnswer.setOptionId(optionId);
                userAnswer.setScore(option.getScore());
                
                userAnswers.add(userAnswer);
            }
        }
        
        // 3. 根据总分确定结果级别和描述
        TestType testType = testTypeMapper.selectById(testTypeId);
        String resultLevel = calculateResultLevel(testType.getName(), totalScore);
        String resultDescription = generateResultDescription(testType.getName(), resultLevel, totalScore);
        String suggestions = generateSuggestions(testType.getName(), resultLevel);
        
        testResult.setTotalScore(totalScore);
        testResult.setResultLevel(resultLevel);
        testResult.setResultDescription(resultDescription);
        testResult.setSuggestions(suggestions);
        
        // 4. 保存测试结果和用户答案
        testResultMapper.insert(testResult);
        for (UserAnswer userAnswer : userAnswers) {
            userAnswerMapper.insert(userAnswer);
        }
        
        // 5. 更新用户心理档案
        updatePsychologicalProfile(userId, testType.getName(), resultLevel, totalScore);
        
        // 6. 返回结果
        TestResultVO resultVO = new TestResultVO();
        BeanUtils.copyProperties(testResult, resultVO);
        return resultVO;
    }

    @Override
    public List<TestResultVO> getUserTestHistory(String userId, String testTypeId) {
        LambdaQueryWrapper<TestResult> wrapper = new LambdaQueryWrapper<TestResult>()
            .eq(TestResult::getUserId, userId);
            
        if (testTypeId != null && !testTypeId.isEmpty()) {
            wrapper.eq(TestResult::getTestTypeId, testTypeId);
        }
        
        wrapper.orderByDesc(TestResult::getCreateTime);
        
        List<TestResult> results = testResultMapper.selectList(wrapper);
        
        return results.stream().map(r -> {
            TestResultVO vo = new TestResultVO();
            BeanUtils.copyProperties(r, vo);
            
            // 获取测试类型名称
            TestType testType = testTypeMapper.selectById(r.getTestTypeId());
            if (testType != null) {
                vo.setTestTypeName(testType.getName());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public TestResultVO getTestResultDetail(String resultId) {
        TestResult result = testResultMapper.selectById(resultId);
        if (result == null) {
            return null;
        }
        
        TestResultVO vo = new TestResultVO();
        BeanUtils.copyProperties(result, vo);
        
        // 获取测试类型名称
        TestType testType = testTypeMapper.selectById(result.getTestTypeId());
        if (testType != null) {
            vo.setTestTypeName(testType.getName());
        }
        
        // 获取用户答案详情
        List<UserAnswer> userAnswers = userAnswerMapper.selectList(
            new LambdaQueryWrapper<UserAnswer>()
                .eq(UserAnswer::getTestResultId, resultId)
        );
        
        // 获取问题和选项详情
        if (!userAnswers.isEmpty()) {
            List<String> questionIds = userAnswers.stream()
                .map(UserAnswer::getQuestionId)
                .collect(Collectors.toList());
                
            List<TestQuestion> questions = testQuestionMapper.selectList(
                new LambdaQueryWrapper<TestQuestion>()
                    .in(TestQuestion::getId, questionIds)
            );
            
            Map<String, TestQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(TestQuestion::getId, q -> q));
                
            List<String> optionIds = userAnswers.stream()
                .map(UserAnswer::getOptionId)
                .collect(Collectors.toList());
                
            List<QuestionOption> options = questionOptionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                    .in(QuestionOption::getId, optionIds)
            );
            
            Map<String, QuestionOption> optionMap = options.stream()
                .collect(Collectors.toMap(QuestionOption::getId, o -> o));
                
            // 构建答案详情
            List<Map<String, Object>> answerDetails = userAnswers.stream().map(ua -> {
                Map<String, Object> detail = new HashMap<>();
                
                TestQuestion question = questionMap.get(ua.getQuestionId());
                if (question != null) {
                    detail.put("questionId", question.getId());
                    detail.put("questionContent", question.getContent());
                }
                
                QuestionOption option = optionMap.get(ua.getOptionId());
                if (option != null) {
                    detail.put("optionId", option.getId());
                    detail.put("optionContent", option.getContent());
                    detail.put("score", option.getScore());
                }
                
                return detail;
            }).collect(Collectors.toList());
            
            vo.setAnswerDetails(answerDetails);
        }
        
        return vo;
    }

    // 辅助方法：将实体转换为VO
    private TestTypeVO convertToVO(TestType testType) {
        TestTypeVO vo = new TestTypeVO();
        BeanUtils.copyProperties(testType, vo);
        return vo;
    }
    
    // 根据测试类型和分数计算结果级别
    private String calculateResultLevel(String testTypeName, int score) {
        // 根据不同测试类型和分数范围确定级别
        switch (testTypeName) {
            case "PHQ-9":
                if (score <= 4) return "正常";
                else if (score <= 9) return "轻度抑郁";
                else if (score <= 14) return "中度抑郁";
                else if (score <= 19) return "中重度抑郁";
                else return "重度抑郁";
            case "GAD-7":
                if (score <= 4) return "正常";
                else if (score <= 9) return "轻度焦虑";
                else if (score <= 14) return "中度焦虑";
                else return "重度焦虑";
            case "SAS":
                if (score <= 49) return "正常";
                else if (score <= 59) return "轻度焦虑";
                else if (score <= 69) return "中度焦虑";
                else return "重度焦虑";
            default:
                if (score <= 50) return "低";
                else if (score <= 70) return "中";
                else return "高";
        }
    }
    
    // 生成结果描述
    private String generateResultDescription(String testTypeName, String level, int score) {
        // 根据测试类型和级别生成描述
        StringBuilder description = new StringBuilder();
        
        description.append("您的").append(testTypeName).append("测试得分为").append(score).append("分，");
        description.append("结果显示您目前处于").append(level).append("状态。");
        
        switch (testTypeName) {
            case "PHQ-9":
                if ("正常".equals(level)) {
                    description.append("您的情绪状态良好，没有明显的抑郁症状。");
                } else {
                    description.append("您可能存在一定程度的抑郁症状，如情绪低落、兴趣减退、睡眠问题等。");
                }
                break;
            case "GAD-7":
                if ("正常".equals(level)) {
                    description.append("您的焦虑水平在正常范围内。");
                } else {
                    description.append("您可能存在一定程度的焦虑症状，如担忧、紧张、不安等。");
                }
                break;
            default:
                description.append("详细分析请参考下方建议。");
        }
        
        return description.toString();
    }
    
 // 生成建议
    private String generateSuggestions(String testTypeName, String level) {
        // 根据测试类型和级别生成建议
        StringBuilder suggestions = new StringBuilder();
        
        if ("正常".equals(level) || "低".equals(level)) {
            suggestions.append("1. 继续保持良好的生活习惯和积极的心态。\n");
            suggestions.append("2. 定期进行自我评估，关注心理健康。\n");
            suggestions.append("3. 培养健康的兴趣爱好，丰富生活内容。");
        } else if ("轻度抑郁".equals(level) || "轻度焦虑".equals(level) || "中".equals(level)) {
            suggestions.append("1. 注意休息，保持规律的作息时间。\n");
            suggestions.append("2. 适当进行体育锻炼，如散步、慢跑等。\n");
            suggestions.append("3. 学习简单的放松技巧，如深呼吸、冥想等。\n");
            suggestions.append("4. 与亲友交流，分享自己的感受。\n");
            suggestions.append("5. 如症状持续，建议咨询专业心理医生。");
        } else {
            suggestions.append("1. 建议尽快咨询专业心理医生或精神科医生。\n");
            suggestions.append("2. 遵循医生建议，可能需要心理治疗或药物治疗。\n");
            suggestions.append("3. 保持与家人朋友的联系，获取社会支持。\n");
            suggestions.append("4. 学习应对压力的技巧，如认知重构、放松训练等。\n");
            suggestions.append("5. 规律作息，健康饮食，适当运动。");
        }
        
        return suggestions.toString();
    }
    
    // 更新用户心理档案
    private void updatePsychologicalProfile(String userId, String testTypeName, String resultLevel, int score) {
        // 查询用户是否已有心理档案
        PsychologicalProfile profile = profileMapper.selectOne(
            new LambdaQueryWrapper<PsychologicalProfile>()
                .eq(PsychologicalProfile::getUserId, userId)
        );
        
        // 如果没有则创建新档案
        if (profile == null) {
            profile = new PsychologicalProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setUserId(userId);
        }
        
        // 根据测试类型更新相应指标
        switch (testTypeName) {
            case "PHQ-9":
                profile.setDepressionLevel(convertScoreToLevel(score));
                break;
            case "GAD-7":
            case "SAS":
                profile.setAnxietyLevel(convertScoreToLevel(score));
                break;
            case "PSS":
                profile.setStressLevel(convertScoreToLevel(score));
                break;
            case "人格特质测评":
                profile.setEmotionalStability(convertScoreToLevel(score));
                break;
            case "社交能力评估":
                profile.setSocialAdaptability(convertScoreToLevel(score));
                break;
        }
        
        // 更新综合评估
        updateProfileSummary(profile);
        
        // 保存或更新档案
        if (profile.getId() == null) {
            profileMapper.insert(profile);
        } else {
            profileMapper.updateById(profile);
        }
    }
    
    // 将原始分数转换为1-10的等级
    private Integer convertScoreToLevel(int score) {
        // 简单转换逻辑，实际应根据不同量表的分数范围调整
        if (score <= 5) return 1;
        else if (score <= 10) return 2;
        else if (score <= 15) return 3;
        else if (score <= 20) return 4;
        else if (score <= 25) return 5;
        else if (score <= 30) return 6;
        else if (score <= 35) return 7;
        else if (score <= 40) return 8;
        else if (score <= 45) return 9;
        else return 10;
    }
    
    // 更新档案综合评估
    private void updateProfileSummary(PsychologicalProfile profile) {
        StringBuilder summary = new StringBuilder("心理健康综合评估：\n");
        
        // 评估情绪状态
        if (profile.getDepressionLevel() != null) {
            summary.append("抑郁水平：");
            if (profile.getDepressionLevel() <= 3) {
                summary.append("良好，情绪状态稳定。\n");
            } else if (profile.getDepressionLevel() <= 6) {
                summary.append("中等，存在一定情绪波动。\n");
            } else {
                summary.append("较高，建议关注情绪健康。\n");
            }
        }
        
        // 评估焦虑状态
        if (profile.getAnxietyLevel() != null) {
            summary.append("焦虑水平：");
            if (profile.getAnxietyLevel() <= 3) {
                summary.append("良好，心态平和。\n");
            } else if (profile.getAnxietyLevel() <= 6) {
                summary.append("中等，存在一定焦虑感。\n");
            } else {
                summary.append("较高，建议学习放松技巧。\n");
            }
        }
        
        // 评估压力状态
        if (profile.getStressLevel() != null) {
            summary.append("压力水平：");
            if (profile.getStressLevel() <= 3) {
                summary.append("良好，压力管理能力强。\n");
            } else if (profile.getStressLevel() <= 6) {
                summary.append("中等，存在一定压力。\n");
            } else {
                summary.append("较高，建议学习压力管理技巧。\n");
            }
        }
        
        // 评估社交适应性
        if (profile.getSocialAdaptability() != null) {
            summary.append("社交适应性：");
            if (profile.getSocialAdaptability() >= 7) {
                summary.append("良好，人际关系和谐。\n");
            } else if (profile.getSocialAdaptability() >= 4) {
                summary.append("中等，社交能力一般。\n");
            } else {
                summary.append("有待提高，建议增加社交活动。\n");
            }
        }
        
        // 评估情绪稳定性
        if (profile.getEmotionalStability() != null) {
            summary.append("情绪稳定性：");
            if (profile.getEmotionalStability() >= 7) {
                summary.append("良好，情绪控制能力强。\n");
            } else if (profile.getEmotionalStability() >= 4) {
                summary.append("中等，情绪有一定波动。\n");
            } else {
                summary.append("有待提高，建议学习情绪管理技巧。\n");
            }
        }
        
        // 设置风险因素
        StringBuilder riskFactors = new StringBuilder();
        if ((profile.getDepressionLevel() != null && profile.getDepressionLevel() > 6) ||
            (profile.getAnxietyLevel() != null && profile.getAnxietyLevel() > 6)) {
            riskFactors.append("情绪健康风险较高；");
        }
        
        if (profile.getStressLevel() != null && profile.getStressLevel() > 6) {
            riskFactors.append("压力水平较高；");
        }
        
        if (profile.getSocialAdaptability() != null && profile.getSocialAdaptability() < 4) {
            riskFactors.append("社交适应性较低；");
        }
        
        // 设置心理优势
        StringBuilder strengths = new StringBuilder();
        if (profile.getEmotionalStability() != null && profile.getEmotionalStability() >= 7) {
            strengths.append("情绪稳定性强；");
        }
        
        if (profile.getSocialAdaptability() != null && profile.getSocialAdaptability() >= 7) {
            strengths.append("社交能力良好；");
        }
        
        if (profile.getStressLevel() != null && profile.getStressLevel() <= 3) {
            strengths.append("压力管理能力强；");
        }
        
        profile.setSummary(summary.toString());
        profile.setRiskFactors(riskFactors.length() > 0 ? riskFactors.toString() : "暂无明显风险因素");
        profile.setStrengths(strengths.length() > 0 ? strengths.toString() : "需要进行更多测评");
    }
}