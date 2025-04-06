package com.advisor.service.impl;

import com.advisor.entity.base.PsychologicalProfile;
import com.advisor.entity.test.*;
import com.advisor.mapper.*;

import com.advisor.mapper.test.*;
import com.advisor.service.test.TestScoreLevelService;
import com.advisor.service.test.TestService;
import com.advisor.vo.test.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

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

    @Autowired
    private OptionTemplateMapper optionTemplateMapper;

    @Autowired
    private TemplateOptionMapper templateOptionMapper;

    @Autowired
    private TestScoreLevelService testScoreLevelService;

    @Autowired
    private TestCategoryMapper testCategoryMapper;

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

        // 获取所有使用的模板ID
        List<String> templateIds = questions.stream()
                .map(TestQuestion::getOptionTemplateId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // 查询所有模板选项
        final Map<String, List<TemplateOption>> templateOptionsMap = new HashMap<>();
        if (!templateIds.isEmpty()) {
            List<TemplateOption> allTemplateOptions = templateOptionMapper.selectList(
                    new LambdaQueryWrapper<TemplateOption>()
                            .in(TemplateOption::getTemplateId, templateIds)
                            .orderByAsc(TemplateOption::getOrderNum)
            );

            // 按模板ID分组选项
            templateOptionsMap.putAll(allTemplateOptions.stream()
                    .collect(Collectors.groupingBy(TemplateOption::getTemplateId)));
        }

        // 构建问题VO
        return questions.stream().map(q -> {
            final QuestionVO vo = new QuestionVO();
            BeanUtils.copyProperties(q, vo);

            // 为每个问题创建一个新的选项列表变量
            final List<QuestionOptionVO> optionVOs = new ArrayList<>();

            // 获取模板选项
            if (q.getOptionTemplateId() != null && !q.getOptionTemplateId().isEmpty()) {
                final String templateId = q.getOptionTemplateId();
                final List<TemplateOption> templateOptions = templateOptionsMap.getOrDefault(templateId, Collections.emptyList());

                // 将模板选项转换为问题选项VO
                optionVOs.addAll(templateOptions.stream().map(option -> {
                    QuestionOptionVO optionVO = new QuestionOptionVO();
                    optionVO.setContent(option.getContent());
                    optionVO.setScore(option.getScore());
                    return optionVO;
                }).collect(Collectors.toList()));
            }

            vo.setOptions(optionVOs);
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
        testResult.setStartTime(LocalDateTime.now().minusMinutes(10));
        testResult.setEndTime(LocalDateTime.now());
        testResult.setCreateTime(LocalDateTime.now()); // 添加创建时间

        // 2. 计算得分并保存答案
        int totalScore = 0;
        List<UserAnswer> userAnswers = new ArrayList<UserAnswer>();

        // 获取该测试类型的所有问题
        List<TestQuestion> questions = testQuestionMapper.selectList(
                new LambdaQueryWrapper<TestQuestion>()
                        .eq(TestQuestion::getTestTypeId, testTypeId)
                        .orderByAsc(TestQuestion::getOrderNum)
        );

        // 创建问题ID到问题对象的映射
        Map<String, TestQuestion> questionMap = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            // 假设前端使用 q1, q2, ... 作为问题ID
            questionMap.put("q" + (i + 1), questions.get(i));
        }

        // 获取所有问题的选项
        List<String> questionIds = questions.stream()
                .map(TestQuestion::getId)
                .collect(Collectors.toList());

        List<QuestionOption> allOptions = questionOptionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                        .in(QuestionOption::getQuestionId, questionIds)
                        .orderByAsc(QuestionOption::getOrderNum)
        );

        // 创建问题ID到选项列表的映射
        Map<String, List<QuestionOption>> optionsMap = allOptions.stream()
                .collect(Collectors.groupingBy(QuestionOption::getQuestionId));

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String frontendQuestionId = entry.getKey(); // 如 q1, q2, ...
            String optionIndexStr = entry.getValue(); // 如 0, 1, 2, ...

            // 获取实际问题对象
            TestQuestion question = questionMap.get(frontendQuestionId);
            if (question == null) {
                System.out.println("Question not found for frontendQuestionId: " + frontendQuestionId);
                continue;
            }

            // 获取问题的所有选项
            List<QuestionOption> options = optionsMap.get(question.getId());
            if (options == null || options.isEmpty()) {
                System.out.println("No options found for questionId: " + question.getId());
                continue;
            }

            // 根据选项索引获取选项
            int optionIndex = Integer.parseInt(optionIndexStr);
            if (optionIndex < 0 || optionIndex >= options.size()) {
                System.out.println("Invalid option index: " + optionIndex + " for questionId: " + question.getId());
                continue;
            }

            QuestionOption option = options.get(optionIndex);

            // 查询选项得分
            if (option != null) {
                System.out.println("Option found - Score: " + option.getScore());
                totalScore += option.getScore();

                // 创建用户答案记录
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setId(UUID.randomUUID().toString());
                userAnswer.setTestResultId(testResult.getId());
                userAnswer.setQuestionId(question.getId());
                userAnswer.setOptionId(option.getId());
                userAnswer.setScore(option.getScore());

                userAnswers.add(userAnswer);
            } else {
                System.out.println("Option not found for id: " + option.getId());
            }
        }

        System.out.println("Final total score: " + totalScore);

        // 3. 设置最终得分和结果
        testResult.setTotalScore(totalScore);

        // 获取测试类型名称
        TestType testType = testTypeMapper.selectById(testTypeId);
        String testTypeName = testType != null ? testType.getName() : testTypeId;

        // 计算结果级别
        String resultLevel = calculateResultLevel(testTypeId, totalScore);
        testResult.setResultLevel(resultLevel);

        // 生成结果描述
        String resultDescription = generateResultDescription(testTypeId, resultLevel, totalScore);
        testResult.setResultDescription(resultDescription);

        // 生成建议
        String suggestions = generateSuggestions(testTypeId, resultLevel);
        testResult.setSuggestions(suggestions);

        // 4. 保存测试结果和用户答案
        testResultMapper.insert(testResult);
        for (UserAnswer userAnswer : userAnswers) {
            userAnswerMapper.insert(userAnswer);
        }

        // 5. 更新用户心理档案
//        updatePsychologicalProfile(userId, testTypeName, resultLevel, totalScore);

        // 6. 返回结果
        TestResultVO resultVO = new TestResultVO();
        BeanUtils.copyProperties(testResult, resultVO);
        resultVO.setTestTypeName(testTypeName);

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

    @Override
    @Transactional
    public QuestionVO saveQuestion(QuestionVO questionVO) {
        // 1. 保存题目基本信息
        TestQuestion question = new TestQuestion();
        if (questionVO.getId() != null && !questionVO.getId().isEmpty()) {
            question = testQuestionMapper.selectById(questionVO.getId());
            if (question == null) {
                throw new RuntimeException("题目不存在");
            }
        } else {
            question.setId(UUID.randomUUID().toString().replace("-", ""));
            question.setCreateTime(LocalDateTime.now());
        }

        // 设置题目属性
        question.setTestTypeId(questionVO.getTestTypeId());
        question.setContent(questionVO.getContent());
        question.setOrderNum(questionVO.getOrderNum());
        question.setOptionType(questionVO.getOptionType());
        question.setOptionTemplateId(questionVO.getOptionTemplateId()); // 设置模板ID
        question.setUpdateTime(LocalDateTime.now());

        // 保存或更新题目
        if (questionVO.getId() != null && !questionVO.getId().isEmpty()) {
            testQuestionMapper.updateById(question);
        } else {
            testQuestionMapper.insert(question);
            // 更新测试类型的题目数量
            updateTestTypeQuestionCount(question.getTestTypeId(), 1);
        }

        // 返回保存后的完整题目信息
        return getQuestionById(question.getId());
    }

    // 辅助方法：更新测试类型的题目数量
    private void updateTestTypeQuestionCount(String testTypeId, int delta) {
        TestType testType = testTypeMapper.selectById(testTypeId);
        if (testType != null) {
            testType.setQuestionCount(Math.max(0, testType.getQuestionCount() + delta));
            testType.setUpdateTime(LocalDateTime.now());
            testTypeMapper.updateById(testType);
        }
    }

    /**
     * 根据ID获取题目详情
     */
    private QuestionVO getQuestionById(String questionId) {
        TestQuestion question = testQuestionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }

        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);

        // 获取选项
        LambdaQueryWrapper<QuestionOption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionOption::getQuestionId, questionId);
        wrapper.orderByAsc(QuestionOption::getOrderNum);
        List<QuestionOption> options = questionOptionMapper.selectList(wrapper);

        // 将 QuestionOption 转换为 QuestionOptionVO
        List<QuestionOptionVO> optionVOs = options.stream().map(option -> {
            QuestionOptionVO optionVO = new QuestionOptionVO();
            BeanUtils.copyProperties(option, optionVO);
            return optionVO;
        }).collect(Collectors.toList());

        questionVO.setOptions(optionVOs);
        return questionVO;
    }

    // 辅助方法：将实体转换为VO
    private TestTypeVO convertToVO(TestType testType) {
        if (testType == null) {
            return null;
        }
        
        TestTypeVO vo = new TestTypeVO();
        BeanUtils.copyProperties(testType, vo);
        
        // 获取测试完成人数
        Integer testCount = testResultMapper.selectCount(
            new LambdaQueryWrapper<TestResult>()
                .eq(TestResult::getTestTypeId, testType.getId())
        ).intValue();
        vo.setTestCount(testCount);
        
        return vo;
    }

    // 根据测试类型和分数计算结果级别
    private String calculateResultLevel(String testTypeId, int score) {
        // 首先尝试从配置中获取级别
        TestScoreLevel level = testScoreLevelService.findLevelByScore(testTypeId, score);
        
        if (level != null) {
            return level.getLevelName();
        }
        
        // 如果没有配置，使用默认逻辑（向下兼容）
        TestType testType = testTypeMapper.selectById(testTypeId);
        String testTypeName = testType != null ? testType.getName() : testTypeId;
        
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
    private String generateResultDescription(String testTypeId, String level, int score) {
        TestType testType = testTypeMapper.selectById(testTypeId);
        String testTypeName = testType != null ? testType.getName() : testTypeId;
        
        // 首先尝试从配置中获取描述
        TestScoreLevel scoreLevel = testScoreLevelService.findLevelByScore(testTypeId, score);
        if (scoreLevel != null && scoreLevel.getDescription() != null) {
            // 替换描述中的变量
            return scoreLevel.getDescription()
                    .replace("{score}", String.valueOf(score))
                    .replace("{level}", level)
                    .replace("{testName}", testTypeName);
        }
        
        // 如果没有配置，使用默认逻辑（向下兼容）
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
    private String generateSuggestions(String testTypeId, String level) {
        TestType testType = testTypeMapper.selectById(testTypeId);
        String testTypeName = testType != null ? testType.getName() : testTypeId;
        
        // 首先尝试从配置中获取建议
        List<TestScoreLevel> levels = testScoreLevelService.getLevelsByTestType(testTypeId);
        for (TestScoreLevel scoreLevel : levels) {
            if (scoreLevel.getLevelName().equals(level) && scoreLevel.getSuggestions() != null) {
                // 替换建议中的变量
                return scoreLevel.getSuggestions()
                        .replace("{level}", level)
                        .replace("{testName}", testTypeName);
            }
        }
        
        // 如果没有配置，使用默认逻辑（向下兼容）
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

    @Override
    @Transactional
    public void deleteQuestion(String questionId) {
        TestQuestion question = testQuestionMapper.selectById(questionId);
        if (question != null) {
            // 删除题目
            testQuestionMapper.deleteById(questionId);
            
            // 更新测试类型的题目数量
            updateTestTypeQuestionCount(question.getTestTypeId(), -1);
        }
    }

    @Override
    public Map<String, Integer> getTestCompletionCounts(List<String> testTypeIds) {
        Map<String, Integer> resultMap = new HashMap<>();

        if (testTypeIds == null || testTypeIds.isEmpty()) {
            return resultMap;
        }

        // 查询每个测试类型的完成次数
        for (String testTypeId : testTypeIds) {
            Long countLong = testResultMapper.selectCount(
                    new LambdaQueryWrapper<TestResult>()
                            .eq(TestResult::getTestTypeId, testTypeId)
            );
            // 将 Long 转换为 Integer（注意可能的溢出风险）
            Integer count = countLong.intValue();
            resultMap.put(testTypeId, count);
        }

        return resultMap;
    }

    @Override
    public TestTypeVO updateTestTypeImage(String testTypeId, String imageUrl) {
        TestType testType = testTypeMapper.selectById(testTypeId);
        if (testType == null) {
            return null;
        }

        testType.setImageUrl(imageUrl);
        testType.setUpdateTime(LocalDateTime.now());
        testTypeMapper.updateById(testType);

        return convertToVO(testType);
    }

    @Override
    @Transactional
    public TestTypeVO saveTestType(TestTypeVO testTypeVO) {
        TestType testType = new TestType();
        
        // 如果是更新操作
        if (testTypeVO.getId() != null && !testTypeVO.getId().isEmpty()) {
            testType = testTypeMapper.selectById(testTypeVO.getId());
            if (testType == null) {
                return null;
            }
        } else {
            // 新增操作，生成ID
            testType.setId(UUID.randomUUID().toString().replace("-", ""));
            testType.setCreateTime(LocalDateTime.now());
            testType.setStatus(1); // 默认启用
            testType.setQuestionCount(0); // 设置初始题目数量为0
        }
        
        // 设置属性
        testType.setName(testTypeVO.getName());
        testType.setDescription(testTypeVO.getDescription());
        testType.setCategory(testTypeVO.getCategory());
        testType.setIcon(testTypeVO.getIcon());
        testType.setTimeMinutes(testTypeVO.getTimeMinutes());
        if (testTypeVO.getImageUrl() != null && !testTypeVO.getImageUrl().isEmpty()) {
            testType.setImageUrl(testTypeVO.getImageUrl());
        }
        testType.setUpdateTime(LocalDateTime.now());
        
        // 保存或更新
        if (testTypeVO.getId() != null && !testTypeVO.getId().isEmpty()) {
            testTypeMapper.updateById(testType);
        } else {
            testTypeMapper.insert(testType);
        }
        
        // 返回保存后的对象
        return convertToVO(testType);
    }

    @Override
    @Transactional
    public boolean deleteTestType(String testTypeId) {
        // 1. 检查测试类型是否存在
        TestType testType = testTypeMapper.selectById(testTypeId);
        if (testType == null) {
            return false;
        }

        // 2. 检查是否有关联的测试题目
        Long questionCount = testQuestionMapper.selectCount(
                new LambdaQueryWrapper<TestQuestion>()
                        .eq(TestQuestion::getTestTypeId, testTypeId)
        );

        if (questionCount > 0) {
            // 2.1 删除关联的测试题目和选项
            List<TestQuestion> questions = testQuestionMapper.selectList(
                    new LambdaQueryWrapper<TestQuestion>()
                            .eq(TestQuestion::getTestTypeId, testTypeId)
            );

            for (TestQuestion question : questions) {
                // 删除选项
                questionOptionMapper.delete(
                        new LambdaQueryWrapper<QuestionOption>()
                                .eq(QuestionOption::getQuestionId, question.getId())
                );
            }

            // 删除题目
            testQuestionMapper.delete(
                    new LambdaQueryWrapper<TestQuestion>()
                            .eq(TestQuestion::getTestTypeId, testTypeId)
            );
        }

        // 3. 检查是否有关联的测试结果
        Long resultCount = testResultMapper.selectCount(
                new LambdaQueryWrapper<TestResult>()
                        .eq(TestResult::getTestTypeId, testTypeId)
        );

        if (resultCount > 0) {
            // 可以选择物理删除或逻辑删除
            // 这里采用逻辑删除：将状态设为禁用
            testType.setStatus(0); // 禁用
            testType.setUpdateTime(LocalDateTime.now());
            testTypeMapper.updateById(testType);
        } else {
            // 没有关联结果，可以物理删除
            testTypeMapper.deleteById(testTypeId);
        }

        return true;
    }

    @Override
    public List<OptionTemplateVO> getAllOptionTemplates() {
        // 1. 获取所有模板
        List<OptionTemplate> templates = optionTemplateMapper.selectList(
                new LambdaQueryWrapper<OptionTemplate>()
                        .orderByDesc(OptionTemplate::getCreateTime)
        );
        
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 获取所有模板ID
        List<String> templateIds = templates.stream()
                .map(OptionTemplate::getId)
                .collect(Collectors.toList());
                
        // 3. 一次性查询所有模板的选项
        List<TemplateOption> allOptions = templateOptionMapper.selectList(
                new LambdaQueryWrapper<TemplateOption>()
                        .in(TemplateOption::getTemplateId, templateIds)
                        .orderByAsc(TemplateOption::getOrderNum)
        );
        
        // 4. 按模板ID分组
        Map<String, List<TemplateOption>> optionsMap = allOptions.stream()
                .collect(Collectors.groupingBy(TemplateOption::getTemplateId));
                
        // 5. 转换并设置选项
        return templates.stream().map(template -> {
            OptionTemplateVO vo = new OptionTemplateVO();
            BeanUtils.copyProperties(template, vo);
            
            // 设置选项
            List<TemplateOption> options = optionsMap.getOrDefault(template.getId(), Collections.emptyList());
            List<QuestionOptionVO> optionVOs = options.stream().map(option -> {
                QuestionOptionVO optionVO = new QuestionOptionVO();
                optionVO.setContent(option.getContent());
                optionVO.setScore(option.getScore());
                return optionVO;
            }).collect(Collectors.toList());
            
            vo.setOptions(optionVOs);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public OptionTemplateVO getOptionTemplateDetail(String templateId) {
        OptionTemplate template = optionTemplateMapper.selectById(templateId);
        if (template == null) {
            return null;
        }
        
        // 查询模板的选项
        List<TemplateOption> options = templateOptionMapper.selectList(
                new LambdaQueryWrapper<TemplateOption>()
                        .eq(TemplateOption::getTemplateId, templateId)
                        .orderByAsc(TemplateOption::getOrderNum)
        );
        
        OptionTemplateVO vo = new OptionTemplateVO();
        BeanUtils.copyProperties(template, vo);
        
        // 转换选项
        List<QuestionOptionVO> optionVOs = options.stream().map(option -> {
            QuestionOptionVO optionVO = new QuestionOptionVO();
            optionVO.setContent(option.getContent());
            optionVO.setScore(option.getScore());
            // 模板选项不需要ID，因为会在使用时重新生成
            return optionVO;
        }).collect(Collectors.toList());
        
        vo.setOptions(optionVOs);
        
        return vo;
    }

    @Override
    @Transactional
    public OptionTemplateVO saveOptionTemplate(OptionTemplateVO templateVO) {
        // 1. 保存模板基本信息
        OptionTemplate template = new OptionTemplate();
        if (templateVO.getId() != null && !templateVO.getId().isEmpty()) {
            // 更新模板
            template = optionTemplateMapper.selectById(templateVO.getId());
            if (template == null) {
                throw new RuntimeException("选项模板不存在");
            }
        } else {
            // 新增模板，生成ID
            template.setId(UUID.randomUUID().toString().replace("-", ""));
            template.setCreateTime(LocalDateTime.now());
        }
        
        // 设置模板属性
        template.setName(templateVO.getName());
        template.setDescription(templateVO.getDescription());
        template.setUpdateTime(LocalDateTime.now());
        
        // 保存或更新模板
        if (templateVO.getId() != null && !templateVO.getId().isEmpty()) {
            optionTemplateMapper.updateById(template);
        } else {
            optionTemplateMapper.insert(template);
        }
        
        // 2. 处理选项
        // 如果是更新，先删除原有选项
        if (templateVO.getId() != null && !templateVO.getId().isEmpty()) {
            LambdaQueryWrapper<TemplateOption> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TemplateOption::getTemplateId, template.getId());
            templateOptionMapper.delete(wrapper);
        }
        
        // 添加新选项
        if (templateVO.getOptions() != null && !templateVO.getOptions().isEmpty()) {
            for (int i = 0; i < templateVO.getOptions().size(); i++) {
                QuestionOptionVO optionVO = templateVO.getOptions().get(i);
                TemplateOption option = new TemplateOption();
                option.setId(UUID.randomUUID().toString().replace("-", ""));
                option.setTemplateId(template.getId());
                option.setContent(optionVO.getContent());
                option.setScore(optionVO.getScore());
                option.setOrderNum(i + 1); // 设置选项顺序
                option.setCreateTime(LocalDateTime.now());
                option.setUpdateTime(LocalDateTime.now());
                templateOptionMapper.insert(option);
            }
        }
        
        // 3. 返回保存后的完整模板信息
        return getOptionTemplateDetail(template.getId());
    }

    @Override
    @Transactional
    public boolean deleteOptionTemplate(String templateId) {
        // 检查是否有题目使用了该模板
        Long count = testQuestionMapper.selectCount(
                new LambdaQueryWrapper<TestQuestion>()
                        .eq(TestQuestion::getOptionTemplateId, templateId)
        );
        
        if (count > 0) {
            // 有题目使用该模板，不能删除
            return false;
        }
        
        // 删除模板选项
        templateOptionMapper.delete(
                new LambdaQueryWrapper<TemplateOption>()
                        .eq(TemplateOption::getTemplateId, templateId)
        );
        
        // 删除模板
        optionTemplateMapper.deleteById(templateId);
        return true;
    }

    @Override
    @Transactional
    public QuestionVO createQuestionWithTemplate(QuestionVO questionVO, String templateId) {
        // 1. 获取模板详情
        OptionTemplateVO template = getOptionTemplateDetail(templateId);
        if (template == null) {
            throw new RuntimeException("选项模板不存在");
        }
        
        // 2. 设置问题的模板ID
        questionVO.setOptionTemplateId(templateId);
        
        // 3. 设置问题的选项为模板选项
        questionVO.setOptions(template.getOptions());
        
        // 4. 保存问题
        return saveQuestion(questionVO);
    }

    // 辅助方法：将OptionTemplate转换为VO
    private OptionTemplateVO convertToTemplateVO(OptionTemplate template) {
        OptionTemplateVO vo = new OptionTemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }

    @Override
    public List<TestTypeVO> searchTests(String keyword, String sortBy) {
        LambdaQueryWrapper<TestType> queryWrapper = new LambdaQueryWrapper<TestType>()
                .eq(TestType::getStatus, 1);
        
        // 添加搜索条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(TestType::getName, keyword)
                    .or()
                    .like(TestType::getDescription, keyword));
        }
        
        // 添加排序条件
        if ("hot".equals(sortBy)) {
            // 最热：使用测试完成人数排序，需要从 TestResult 表中获取数据
            List<TestType> testTypes = testTypeMapper.selectList(queryWrapper);
            List<TestTypeVO> result = testTypes.stream().map(this::convertToVO).collect(Collectors.toList());
            
            // 获取所有测试ID
            List<String> testIds = result.stream().map(TestTypeVO::getId).collect(Collectors.toList());
            Map<String, Integer> completionCounts = getTestCompletionCounts(testIds);
            
            // 设置完成人数并排序
            for (TestTypeVO vo : result) {
                vo.setTestCount(completionCounts.getOrDefault(vo.getId(), 0));
            }
            
            return result.stream()
                    .sorted(Comparator.comparing(TestTypeVO::getTestCount, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else if ("new".equals(sortBy)) {
            // 最新：按创建时间倒序
            queryWrapper.orderByDesc(TestType::getCreateTime);
        } else {
            // 默认按分类和名称排序
            queryWrapper.orderByAsc(TestType::getCategory)
                    .orderByAsc(TestType::getName);
        }
        
        List<TestType> testTypes = testTypeMapper.selectList(queryWrapper);
        return testTypes.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllCategories() {
        // 添加日志
        log.info("开始获取所有测试分类");
        
        try {
            // 查询所有分类
            List<TestCategory> categories = testCategoryMapper.selectList(null);
            log.info("查询到{}个分类", categories.size());
            
            // 转换为前端需要的格式
            return categories.stream().map(category -> {
                Map<String, Object> map = new HashMap<>();
                map.put("code", category.getCode());
                map.put("name", category.getName());
                map.put("description", category.getDescription());
                map.put("icon", category.getIcon());
                map.put("color", category.getColor());
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取分类失败", e);
            throw e;
        }
    }
}