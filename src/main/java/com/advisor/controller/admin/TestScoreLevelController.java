package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.entity.test.TestScoreLevel;
import com.advisor.service.test.TestScoreLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试分数解读等级管理控制器
 */
@RestController
@RequestMapping("/api/admin/test/score-level")
public class TestScoreLevelController {

    @Autowired
    private TestScoreLevelService testScoreLevelService;

    /**
     * 获取测试类型的所有分数等级
     */
    @GetMapping("/list/{testTypeId}")
    public Result<List<TestScoreLevel>> getLevelsByTestType(@PathVariable String testTypeId) {
        List<TestScoreLevel> levels = testScoreLevelService.getLevelsByTestType(testTypeId);
        return Result.success(levels);
    }

    /**
     * 创建分数等级
     */
    @PostMapping("/create")
    public Result<String> createLevel(@RequestBody TestScoreLevel level) {
        String id = testScoreLevelService.saveLevel(level);
        return Result.success(id);
    }

    /**
     * 更新分数等级
     */
    @PutMapping("/update")
    public Result<Void> updateLevel(@RequestBody TestScoreLevel level) {
        testScoreLevelService.updateLevel(level);
        return Result.success();
    }

    /**
     * 删除分数等级
     */
    @DeleteMapping("/delete/{levelId}")
    public Result<Void> deleteLevel(@PathVariable String levelId) {
        testScoreLevelService.deleteLevel(levelId);
        return Result.success();
    }
}