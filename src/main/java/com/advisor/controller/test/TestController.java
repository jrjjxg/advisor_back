package com.advisor.controller.test;

import com.advisor.common.Result;
import com.advisor.service.test.TestService;
import com.advisor.vo.test.QuestionVO;
import com.advisor.vo.test.TestResultVO;
import com.advisor.vo.test.TestTypeVO;
import com.advisor.vo.test.OptionTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
//springboot框架，@RestController注解表示该类是一个控制器，
//该控制器类处理所有以 /api/tests 开头的 HTTP 请求，/api/tests：基础路径

//在 Web 开发中，"接口"（API 接口）通常指的是：
// 定义：服务器提供的一个访问点，客户端可以通过这个访问点获取或操作数据
// 组成：通常包括 URL 路径、HTTP 方法（GET、POST 等）和可能的参数
// 功能：提供特定的功能，如获取数据、创建记录、更新信息等
public class TestController {

    @Autowired
    private TestService testService;
    //springboot框架，@Autowired注解表示自动装配，将TestService注入到TestController中

    /**
     * 获取所有测试类型
     */
    /**
     * Spring Boot 会自动将 TestTypeVO 对象转换为 JSON
     * Java 类型会自动转换为对应的 JSON 类型：
     * String → JSON 字符串
     * Integer → JSON 数字
     */

    /**
     * Result<List<TestTypeVO>>: 返回类型
     * Result: 统一响应封装类
     * List<TestTypeVO>: 表示返回多个测试类型对象的列表
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
        // 添加调试日志
        System.out.println("返回的测试问题: " + questions.size() + " 个");
        for (QuestionVO q : questions) {
            System.out.println("问题: id=" + q.getId() + ", 内容=" + q.getContent() + ", question=" + q.getQuestion());
            System.out.println("  选项数量: " + (q.getOptions() != null ? q.getOptions().size() : 0));
            if (q.getOptions() != null) {
                for (int i = 0; i < q.getOptions().size(); i++) {
                    System.out.println("  选项" + (i+1) + ": " + q.getOptions().get(i).getContent());
                }
            }
        }
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

    /**
     * 添加或更新测试题目
     */
    @PostMapping("/questions")
    public Result<QuestionVO> saveQuestion(@RequestBody QuestionVO questionVO) {
        // 参数校验
        if (questionVO.getTestTypeId() == null || questionVO.getTestTypeId().isEmpty()) {
            return Result.fail("测试类型ID不能为空");
        }
        if (questionVO.getContent() == null || questionVO.getContent().isEmpty()) {
            return Result.fail("题目内容不能为空");
        }
        
        // 检查是否需要验证选项
        boolean needOptions = questionVO.getOptionTemplateId() == null || questionVO.getOptionTemplateId().isEmpty();
        if (needOptions && (questionVO.getOptions() == null || questionVO.getOptions().isEmpty())) {
            return Result.fail("题目选项不能为空");
        }

        try {
            QuestionVO savedQuestion = testService.saveQuestion(questionVO);
            return Result.success(savedQuestion);
        } catch (Exception e) {
            return Result.fail("保存题目失败: " + e.getMessage());
        }
    }

    /**
     * 删除测试题目
     */
    @DeleteMapping("/questions/{questionId}")
    public Result<Boolean> deleteQuestion(@PathVariable String questionId) {
        try {
            testService.deleteQuestion(questionId);
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("删除题目失败: " + e.getMessage());
        }
    }

    /**
     * 获取测试完成人数
     */
    @GetMapping("/completion-counts")
    public Result<Map<String, Integer>> getTestCompletionCounts(
            @RequestParam(value = "testTypeId", required = false) List<String> testTypeIds) {
        if (testTypeIds == null) {
            testTypeIds = new ArrayList<>();
        }
        Map<String, Integer> counts = testService.getTestCompletionCounts(testTypeIds);
        return Result.success(counts);
    }

    @PutMapping("/{testTypeId}/image")
    public Result<TestTypeVO> updateTestTypeImage(
            @PathVariable String testTypeId,
            @RequestBody Map<String, String> payload) {

        String imageUrl = payload.get("imageUrl");
        if (imageUrl == null || imageUrl.isEmpty()) {
            return Result.fail("图片URL不能为空");
        }

        TestTypeVO testType = testService.updateTestTypeImage(testTypeId, imageUrl);
        if (testType == null) {
            return Result.fail("测试类型不存在");
        }

        return Result.success(testType);
    }

    // 添加测试类型
    @PostMapping("/types")
    public Result<TestTypeVO> addTestType(@RequestBody TestTypeVO testTypeVO) {
        // 参数校验
        if (testTypeVO.getName() == null || testTypeVO.getName().isEmpty()) {
            return Result.fail("测试名称不能为空");
        }
        if (testTypeVO.getCategory() == null || testTypeVO.getCategory().isEmpty()) {
            return Result.fail("测试分类不能为空");
        }

        try {
            TestTypeVO savedType = testService.saveTestType(testTypeVO);
            return Result.success(savedType);
        } catch (Exception e) {
            return Result.fail("添加测试类型失败: " + e.getMessage());
        }
    }

    // 更新测试类型
    @PutMapping("/types/{testTypeId}")
    public Result<TestTypeVO> updateTestType(
            @PathVariable String testTypeId,
            @RequestBody TestTypeVO testTypeVO) {

        // 设置ID
        testTypeVO.setId(testTypeId);

        // 参数校验
        if (testTypeVO.getName() == null || testTypeVO.getName().isEmpty()) {
            return Result.fail("测试名称不能为空");
        }
        if (testTypeVO.getCategory() == null || testTypeVO.getCategory().isEmpty()) {
            return Result.fail("测试分类不能为空");
        }

        try {
            TestTypeVO updatedType = testService.saveTestType(testTypeVO);
            if (updatedType == null) {
                return Result.fail("测试类型不存在");
            }
            return Result.success(updatedType);
        } catch (Exception e) {
            return Result.fail("更新测试类型失败: " + e.getMessage());
        }
    }

    // 删除测试类型
    @DeleteMapping("/types/{testTypeId}")
    public Result<Boolean> deleteTestType(@PathVariable String testTypeId) {
        try {
            boolean success = testService.deleteTestType(testTypeId);
            if (!success) {
                return Result.fail("测试类型不存在或无法删除");
            }
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("删除测试类型失败: " + e.getMessage());
        }
    }

    // 获取所有选项模板
    @GetMapping("/option-templates")
    public Result<List<OptionTemplateVO>> getAllOptionTemplates() {
        List<OptionTemplateVO> templates = testService.getAllOptionTemplates();
        return Result.success(templates);
    }

    // 获取选项模板详情
    @GetMapping("/option-templates/{templateId}")
    public Result<OptionTemplateVO> getOptionTemplateDetail(@PathVariable String templateId) {
        OptionTemplateVO template = testService.getOptionTemplateDetail(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }
        return Result.success(template);
    }

    // 保存选项模板
    @PostMapping("/option-templates")
    public Result<OptionTemplateVO> saveOptionTemplate(@RequestBody OptionTemplateVO templateVO) {
        // 参数校验
        if (templateVO.getName() == null || templateVO.getName().isEmpty()) {
            return Result.fail("模板名称不能为空");
        }
        if (templateVO.getOptions() == null || templateVO.getOptions().isEmpty()) {
            return Result.fail("模板选项不能为空");
        }

        try {
            OptionTemplateVO savedTemplate = testService.saveOptionTemplate(templateVO);
            return Result.success(savedTemplate);
        } catch (Exception e) {
            return Result.fail("保存模板失败: " + e.getMessage());
        }
    }

    // 删除选项模板
    @DeleteMapping("/option-templates/{templateId}")
    public Result<Boolean> deleteOptionTemplate(@PathVariable String templateId) {
        try {
            boolean success = testService.deleteOptionTemplate(templateId);
            if (!success) {
                return Result.fail("模板已被使用，无法删除");
            }
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("删除模板失败: " + e.getMessage());
        }
    }

    // 使用模板创建题目
    @PostMapping("/questions/with-template/{templateId}")
    public Result<QuestionVO> createQuestionWithTemplate(
            @PathVariable String templateId,
            @RequestBody QuestionVO questionVO) {
        // 参数校验
        if (questionVO.getTestTypeId() == null || questionVO.getTestTypeId().isEmpty()) {
            return Result.fail("测试类型ID不能为空");
        }
        if (questionVO.getContent() == null || questionVO.getContent().isEmpty()) {
            return Result.fail("题目内容不能为空");
        }

        try {
            // 设置模板ID
            questionVO.setOptionTemplateId(templateId);
            QuestionVO savedQuestion = testService.saveQuestion(questionVO);
            return Result.success(savedQuestion);
        } catch (Exception e) {
            return Result.fail("创建题目失败: " + e.getMessage());
        }
    }

    /**
     * 根据条件搜索和筛选测试
     */
    @GetMapping("/search")
    public Result<List<TestTypeVO>> searchTests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy) {
        
        List<TestTypeVO> testTypes = testService.searchTests(keyword, sortBy);
        return Result.success(testTypes);
    }

    /**
     * 获取所有测试分类
     */
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = testService.getAllCategories();
        return Result.success(categories);
    }
}