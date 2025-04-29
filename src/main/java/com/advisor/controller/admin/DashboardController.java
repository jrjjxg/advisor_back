package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.entity.test.TestType;
import com.advisor.service.UserService;
import com.advisor.service.driftbottle.DriftBottleService;
import com.advisor.service.media.MediaCategoryService;
import com.advisor.service.media.MediaResourceService;
import com.advisor.service.test.TestCategoryService;
import com.advisor.service.test.TestScoreLevelService;
import com.advisor.service.test.TestService;
import com.advisor.vo.test.TestTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 管理后台数据看板控制器
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private TestCategoryService testCategoryService;

    @Autowired
    private TestScoreLevelService testScoreLevelService;

    @Autowired
    private MediaCategoryService mediaCategoryService;

    @Autowired
    private MediaResourceService mediaResourceService;

    @Autowired
    private DriftBottleService driftBottleService;

    /**
     * 获取核心统计数据
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取用户总数
            long userCount = userService.count();
            stats.put("userCount", userCount);
            
            // 获取测试分类总数
            List<?> testCategories = testCategoryService.getAllCategories();
            stats.put("testCategoryCount", testCategories != null ? testCategories.size() : 0);
            
            // 获取测试类型总数，并明确类型为 TestTypeVO
            List<TestTypeVO> testTypes = testService.getAllTestTypes();
            stats.put("testTypeCount", testTypes != null ? testTypes.size() : 0);
            
            // 获取测试题目总数 - 直接累加 TestTypeVO 对象中的 questionCount 字段
            int totalQuestionCount = 0;
            if (testTypes != null && !testTypes.isEmpty()) {
                totalQuestionCount = testTypes.stream()
                                            .filter(vo -> vo != null && vo.getQuestionCount() != null)
                                            .mapToInt(TestTypeVO::getQuestionCount)
                                            .sum();
            }
            stats.put("testQuestionCount", totalQuestionCount);
            
            // 获取媒体分类总数
            Page<?> mediaCategories = mediaCategoryService.getCategoryList(1, 1000, null);
            stats.put("mediaCategoryCount", mediaCategories != null ? mediaCategories.getTotal() : 0);
            
            // 获取媒体资源总数
            Page<?> mediaResources = mediaResourceService.getMediaResourceList(1, 1, null, null, null, null, null);
            stats.put("mediaResourceCount", mediaResources != null ? mediaResources.getTotal() : 0);
            
            // 获取待审核漂流瓶数量
            Page<?> pendingAuditBottles = driftBottleService.getPendingAuditBottles(1, 1);
            stats.put("pendingAuditCount", pendingAuditBottles != null ? pendingAuditBottles.getTotal() : 0);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取近期活动趋势
     * @param days 天数，默认7天
     * @return 趋势数据
     */
    @GetMapping("/trends")
    public Result<Map<String, Object>> getTrends(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> trends = new HashMap<>();
        
        try {
            // 生成日期序列
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            List<String> dateList = new ArrayList<>();
            List<Long> userCountList = new ArrayList<>();
            List<Long> mediaCountList = new ArrayList<>();
            List<Long> testCountList = new ArrayList<>();
            
            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // 生成日期序列
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String formattedDate = date.format(formatter);
                dateList.add(formattedDate);
                
                // 这里是示例数据，实际应该从数据库中查询
                // 在实际项目中，需要根据数据库设计来获取每日数据
                userCountList.add(0L);
                mediaCountList.add(0L);
                testCountList.add(0L);
            }
            
            // 由于我们不能直接查询这些每日数据（需要数据库支持），这里使用模拟数据
            // 实际项目中应该替换为真实数据查询
            for (int i = 0; i < days; i++) {
                userCountList.set(i, (long) (Math.random() * 20 + 5)); // 模拟每日新增5-25个用户
                mediaCountList.set(i, (long) (Math.random() * 10 + 2)); // 模拟每日新增2-12个媒体资源
                testCountList.set(i, (long) (Math.random() * 30 + 10)); // 模拟每日新增10-40个测试记录
            }
            
            // 构建返回数据
            Map<String, Object> newUserTrend = new HashMap<>();
            newUserTrend.put("dates", dateList);
            newUserTrend.put("counts", userCountList);
            trends.put("newUserTrend", newUserTrend);
            
            Map<String, Object> newMediaTrend = new HashMap<>();
            newMediaTrend.put("dates", dateList);
            newMediaTrend.put("counts", mediaCountList);
            trends.put("newMediaTrend", newMediaTrend);
            
            Map<String, Object> newTestTrend = new HashMap<>();
            newTestTrend.put("dates", dateList);
            newTestTrend.put("counts", testCountList);
            trends.put("newTestTrend", newTestTrend);
            
            return Result.success(trends);
        } catch (Exception e) {
            log.error("获取趋势数据失败", e);
            return Result.error("获取趋势数据失败：" + e.getMessage());
        }
    }
} 