package com.advisor.service.test;

import com.advisor.entity.test.TestCategory;
import java.util.List;
import java.util.Map;

/**
 * 测试分类服务接口
 */
public interface TestCategoryService {

    /**
     * 创建测试分类
     *
     * @param category 分类信息
     */
    void createCategory(TestCategory category);

    /**
     * 更新测试分类
     *
     * @param category 分类信息
     */
    void updateCategory(TestCategory category);

    /**
     * 删除测试分类
     *
     * @param code 分类代码
     */
    void deleteCategory(String code);

    /**
     * 获取各分类下的测试数量
     *
     * @return 分类代码到测试数量的映射
     */
    Map<String, Integer> getTestCountsByCategory();

    /**
     * 获取所有测试分类
     * @return 分类列表
     */
    List<TestCategory> getAllCategories();
} 