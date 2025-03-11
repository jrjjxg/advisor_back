package com.advisor.service;

import com.advisor.entity.TestQuestion;
import com.advisor.entity.TestResult;
import com.advisor.entity.TestType;
import com.advisor.vo.QuestionVO;
import com.advisor.vo.TestResultVO;
import com.advisor.vo.TestTypeVO;

import java.util.List;
import java.util.Map;

public interface TestService {
    // 获取所有测试类型
    List<TestTypeVO> getAllTestTypes();
    
    // 获取测试详情
    TestTypeVO getTestTypeDetail(String testTypeId);
    
    // 获取测试问题
    List<QuestionVO> getTestQuestions(String testTypeId);
    
    // 提交测试答案并获取结果
    TestResultVO submitTestAnswers(String userId, String testTypeId, Map<String, String> answers);
    
    // 获取用户的测试历史
    List<TestResultVO> getUserTestHistory(String userId, String testTypeId);
    
    // 获取测试结果详情
    TestResultVO getTestResultDetail(String resultId);
    
    // 添加或更新测试题目及其选项
    QuestionVO saveQuestion(QuestionVO questionVO);
    
    /**
     * 删除测试题目及其选项
     * @param questionId 题目ID
     */
    void deleteQuestion(String questionId);
}