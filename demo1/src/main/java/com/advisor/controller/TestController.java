package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.service.TestService;
import com.advisor.vo.QuestionVO;
import com.advisor.vo.TestResultVO;
import com.advisor.vo.TestTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    @Autowired
    private TestService testService;
    
    /**
     * 获取所有测试类型
     */
    @GetMapping
    public Result<List<TestTypeVO>> getAllTestTypes() {
        List<TestTypeVO> testTypes = testService.getAllTestTypes();
        return Result.success(testTypes);
    }
    
    /**
     * 获取测试详情
     */
    @GetMapping("/{testTypeId}")
    public Result<TestTypeVO> getTestTypeDetail(@PathVariable String testTypeId) {
        TestTypeVO testType = testService.getTestTypeDetail(testTypeId);
        if (testType == null) {
            return Result.fail("测试不存在");
        }
        return Result.success(testType);
    }
    
    /**
     * 获取测试问题
     */
    @GetMapping("/{testTypeId}/questions")
    public Result<List<QuestionVO>> getTestQuestions(@PathVariable String testTypeId) {
        List<QuestionVO> questions = testService.getTestQuestions(testTypeId);
        return Result.success(questions);
    }
    
    /**
     * 提交测试答案
     */
    @PostMapping("/{testTypeId}/submit")
    public Result<TestResultVO> submitTestAnswers(
            @RequestHeader("userId") String userId,
            @PathVariable String testTypeId,
            @RequestBody Map<String, String> answers) {
        TestResultVO result = testService.submitTestAnswers(userId, testTypeId, answers);
        return Result.success(result);
    }
    
    /**
     * 获取用户测试历史
     */
    @GetMapping("/history")
    public Result<List<TestResultVO>> getUserTestHistory(
            @RequestHeader("userId") String userId,
            @RequestParam(required = false) String testTypeId) {
        List<TestResultVO> history = testService.getUserTestHistory(userId, testTypeId);
        return Result.success(history);
    }
    
    /**
     * 获取测试结果详情
     */
    @GetMapping("/results/{resultId}")
    public Result<TestResultVO> getTestResultDetail(@PathVariable String resultId) {
        TestResultVO result = testService.getTestResultDetail(resultId);
        if (result == null) {
            return Result.fail("测试结果不存在");
        }
        return Result.success(result);
    }
}