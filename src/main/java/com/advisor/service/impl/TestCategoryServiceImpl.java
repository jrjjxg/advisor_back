package com.advisor.service.impl;

import com.advisor.entity.test.TestCategory;
import com.advisor.entity.test.TestType;
import com.advisor.mapper.test.TestCategoryMapper;
import com.advisor.mapper.test.TestTypeMapper;
import com.advisor.service.test.TestCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试分类服务实现
 */
@Service
public class TestCategoryServiceImpl implements TestCategoryService {

    @Autowired
    private TestCategoryMapper testCategoryMapper;

    @Autowired
    private TestTypeMapper testTypeMapper;

    @Override
    @Transactional
    public void createCategory(TestCategory category) {
        // 检查分类代码是否已存在
        TestCategory existingCategory = testCategoryMapper.selectById(category.getCode());
        if (existingCategory != null) {
            throw new RuntimeException("分类代码已存在");
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        // 设置默认值
        category.setStatus(1);
        if (category.getDisplayOrder() == null) {
            category.setDisplayOrder(0);
        }

        testCategoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void updateCategory(TestCategory category) {
        // 检查分类是否存在
        TestCategory existingCategory = testCategoryMapper.selectById(category.getCode());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 设置更新时间
        category.setUpdateTime(LocalDateTime.now());

        testCategoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String code) {
        // 检查分类是否存在
        TestCategory existingCategory = testCategoryMapper.selectById(code);
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查是否有测试使用此分类
        Long count = testTypeMapper.selectCount(
                Wrappers.<TestType>lambdaQuery().eq(TestType::getCategory, code)
        );
        if (count > 0) {
            throw new RuntimeException("该分类下存在测试，无法删除");
        }

        testCategoryMapper.deleteById(code);
    }

    @Override
    public Map<String, Integer> getTestCountsByCategory() {
        Map<String, Integer> result = new HashMap<>();

        // 获取所有分类
        List<TestCategory> categories = testCategoryMapper.selectList(null);
        for (TestCategory category : categories) {
            // 统计每个分类下的测试数量
            Long count = testTypeMapper.selectCount(
                    Wrappers.<TestType>lambdaQuery().eq(TestType::getCategory, category.getCode())
            );
            result.put(category.getCode(), count.intValue());
        }

        return result;
    }

    @Override
    public List<TestCategory> getAllCategories() {
        // 查询所有状态为启用的分类，按显示顺序排序
        return testCategoryMapper.selectList(
            Wrappers.<TestCategory>lambdaQuery()
                .eq(TestCategory::getStatus, 1)
                .orderByAsc(TestCategory::getDisplayOrder)
        );
    }
} 