package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.entity.test.TestCategory;
import com.advisor.service.test.TestCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试分类管理控制器
 */
@RestController
@RequestMapping("/api/tests/categories")
public class TestCategoryController {

    @Autowired
    private TestCategoryService testCategoryService;

    /**
     * 创建测试分类
     */
    @PostMapping
    public Result<?> createCategory(@RequestBody TestCategory category) {
        testCategoryService.createCategory(category);
        return Result.success();
    }

    /**
     * 更新测试分类
     */
    @PutMapping("/{code}")
    public Result<?> updateCategory(@PathVariable String code, @RequestBody TestCategory category) {
        category.setCode(code);
        testCategoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 删除测试分类
     */
    @DeleteMapping("/{code}")
    public Result<?> deleteCategory(@PathVariable String code) {
        testCategoryService.deleteCategory(code);
        return Result.success();
    }

    /**
     * 获取各分类下的测试数量
     */
    @GetMapping("/test-counts")
    public Result<Map<String, Integer>> getTestCountsByCategory() {
        Map<String, Integer> counts = testCategoryService.getTestCountsByCategory();
        return Result.success(counts);
    }

    /**
     * 获取所有测试分类
     */
    @GetMapping("/list")
    public Result<List<TestCategory>> getAllCategories() {
        List<TestCategory> categories = testCategoryService.getAllCategories();
        return Result.success(categories);
    }
} 