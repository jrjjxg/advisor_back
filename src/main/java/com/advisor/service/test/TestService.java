package com.advisor.service.test;


import com.advisor.vo.test.QuestionVO;
import com.advisor.vo.test.TestResultVO;
import com.advisor.vo.test.TestTypeVO;
import com.advisor.vo.test.OptionTemplateVO;

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
    
    // 获取测试完成人数
    Map<String, Integer> getTestCompletionCounts(List<String> testTypeId);
    
    // 更新测试类型图片
    TestTypeVO updateTestTypeImage(String testTypeId, String imageUrl);
    
    // 添加或更新测试类型
    TestTypeVO saveTestType(TestTypeVO testTypeVO);
    
    // 删除测试类型
    boolean deleteTestType(String testTypeId);

    // 获取所有选项模板
    List<OptionTemplateVO> getAllOptionTemplates();

    // 获取选项模板详情
    OptionTemplateVO getOptionTemplateDetail(String templateId);

    // 保存选项模板
    OptionTemplateVO saveOptionTemplate(OptionTemplateVO templateVO);

    // 删除选项模板
    boolean deleteOptionTemplate(String templateId);

    // 使用模板创建问题
    QuestionVO createQuestionWithTemplate(QuestionVO questionVO, String templateId);

    /**
     * 搜索测试
     * @param keyword 搜索关键词
     * @param sortBy 排序方式（"hot"表示最热，"new"表示最新）
     * @return 测试列表
     */
    List<TestTypeVO> searchTests(String keyword, String sortBy);

    /**
     * 获取所有测试分类
     * @return 分类列表
     */
    List<Map<String, Object>> getAllCategories();
}