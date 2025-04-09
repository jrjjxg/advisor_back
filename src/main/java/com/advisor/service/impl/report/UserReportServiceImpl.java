package com.advisor.service.impl.report;

import com.advisor.entity.report.UserReport;
import com.advisor.entity.base.User;
import com.advisor.mapper.report.UserReportMapper;
import com.advisor.mapper.base.UserMapper;
import com.advisor.service.report.ReportDataService;
import com.advisor.service.report.UserReportService;
import com.advisor.vo.report.JournalDataVO;
import com.advisor.vo.report.MoodDataVO;
import com.advisor.vo.report.TestResultDataVO;
import com.advisor.vo.report.UserReportVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserReportServiceImpl implements UserReportService {

    @Autowired
    private UserReportMapper userReportMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReportDataService reportDataService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String createWeeklyReport(String userId, LocalDateTime weekStart) {
        // 确保是周一的开始时间
        weekStart = weekStart.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusDays(6).toLocalDate().atTime(23, 59, 59);
        
        // 检查该周期报告是否已存在
        int weekNumber = weekStart.get(WeekFields.ISO.weekOfYear());
        int year = weekStart.getYear();
        String period = year + "-W" + String.format("%02d", weekNumber);
        
        if (checkReportExists(userId, "weekly", period)) {
            // 报告已存在，返回已有报告的ID
            return userReportMapper.selectOne(
                new LambdaQueryWrapper<UserReport>()
                    .eq(UserReport::getUserId, userId)
                    .eq(UserReport::getReportType, "weekly")
                    .eq(UserReport::getPeriod, period)
            ).getId();
        }
        
        // 收集报告数据
        Map<String, Object> reportData = collectReportData(userId, weekStart, weekEnd);
        
        // 生成洞察和建议
        String insights = generateInsights(reportData);
        
        // 创建并保存报告
        UserReport report = new UserReport();
        report.setUserId(userId);
        report.setReportType("weekly");
        report.setPeriod(period);
        report.setReportTitle(year + "年第" + weekNumber + "周使用报告");
        try {
            report.setReportContent(objectMapper.writeValueAsString(reportData));
        } catch (Exception e) {
            report.setReportContent("{}");
        }
        report.setKeyInsights(insights);
        report.setStartDate(weekStart);
        report.setEndDate(weekEnd);
        report.setCreateTime(LocalDateTime.now());
        report.setIsRead(false);
        
        userReportMapper.insert(report);
        return report.getId();
    }

    @Override
    public String createMonthlyReport(String userId, int year, int month) {
        // 获取月份的开始和结束时间
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // 检查该月份报告是否已存在
        String period = year + "-" + String.format("%02d", month);
        
        if (checkReportExists(userId, "monthly", period)) {
            // 报告已存在，返回已有报告的ID
            return userReportMapper.selectOne(
                new LambdaQueryWrapper<UserReport>()
                    .eq(UserReport::getUserId, userId)
                    .eq(UserReport::getReportType, "monthly")
                    .eq(UserReport::getPeriod, period)
            ).getId();
        }
        
        // 收集报告数据
        Map<String, Object> reportData = collectReportData(userId, monthStart, monthEnd);
        
        // 生成洞察和建议
        String insights = generateInsights(reportData);
        
        // 创建并保存报告
        UserReport report = new UserReport();
        report.setUserId(userId);
        report.setReportType("monthly");
        report.setPeriod(period);
        report.setReportTitle(year + "年" + month + "月使用报告");
        try {
            report.setReportContent(objectMapper.writeValueAsString(reportData));
        } catch (Exception e) {
            report.setReportContent("{}");
        }
        report.setKeyInsights(insights);
        report.setStartDate(monthStart);
        report.setEndDate(monthEnd);
        report.setCreateTime(LocalDateTime.now());
        report.setIsRead(false);
        
        userReportMapper.insert(report);
        return report.getId();
    }

    @Override
    public String createYearlyReport(String userId, int year) {
        // 获取年份的开始和结束时间
        LocalDateTime yearStart = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime yearEnd = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        
        // 检查该年份报告是否已存在
        String period = String.valueOf(year);
        
        if (checkReportExists(userId, "yearly", period)) {
            // 报告已存在，返回已有报告的ID
            return userReportMapper.selectOne(
                new LambdaQueryWrapper<UserReport>()
                    .eq(UserReport::getUserId, userId)
                    .eq(UserReport::getReportType, "yearly")
                    .eq(UserReport::getPeriod, period)
            ).getId();
        }
        
        // 收集报告数据
        Map<String, Object> reportData = collectReportData(userId, yearStart, yearEnd);
        
        // 生成洞察和建议
        String insights = generateInsights(reportData);
        
        // 创建并保存报告
        UserReport report = new UserReport();
        report.setUserId(userId);
        report.setReportType("yearly");
        report.setPeriod(period);
        report.setReportTitle(year + "年度使用报告");
        try {
            report.setReportContent(objectMapper.writeValueAsString(reportData));
        } catch (Exception e) {
            report.setReportContent("{}");
        }
        report.setKeyInsights(insights);
        report.setStartDate(yearStart);
        report.setEndDate(yearEnd);
        report.setCreateTime(LocalDateTime.now());
        report.setIsRead(false);
        
        userReportMapper.insert(report);
        return report.getId();
    }

    @Override
    public String createCustomReport(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // 1. 定义报告类型和周期标识
        String reportType = "custom";
        // 使用日期范围作为周期标识符，确保唯一性（如果需要严格唯一，可以考虑加上时间戳或UUID）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String period = startDate.format(formatter) + "_" + endDate.format(formatter);

        // 注意：对于自定义报告，通常不检查是否已存在完全相同时间段的报告，
        // 因为用户可能希望针对同一时间段生成多次报告（例如，如果数据更新了）。
        // 如果需要检查，可以取消下面的注释并调整逻辑。
        /*
        if (checkReportExists(userId, reportType, period)) {
            // 可以选择返回错误、返回现有报告ID或覆盖现有报告
            return userReportMapper.selectOne(
                new LambdaQueryWrapper<UserReport>()
                    .eq(UserReport::getUserId, userId)
                    .eq(UserReport::getReportType, reportType)
                    .eq(UserReport::getPeriod, period)
            ).getId();
        }
        */

        // 2. 收集报告数据
        Map<String, Object> reportData = collectReportData(userId, startDate, endDate);

        // 3. 生成洞察和建议
        String insights = generateInsights(reportData);

        // 4. 创建并保存报告
        UserReport report = new UserReport();
        report.setUserId(userId);
        report.setReportType(reportType);
        report.setPeriod(period); // 存储自定义时间范围标识

        // 生成报告标题
        DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
        report.setReportTitle("自定义报告 (" + startDate.format(titleFormatter) + " - " + endDate.format(titleFormatter) + ")");

        try {
            report.setReportContent(objectMapper.writeValueAsString(reportData));
        } catch (Exception e) {
            // 处理序列化错误，例如记录日志
            System.err.println("序列化报告内容失败: " + e.getMessage());
            report.setReportContent("{}"); // 保存空JSON对象以避免null
        }
        report.setKeyInsights(insights);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setCreateTime(LocalDateTime.now());
        report.setIsRead(false); // 新报告默认为未读

        userReportMapper.insert(report);
        return report.getId(); // 返回新生成的报告ID
    }

    @Override
    public Page<UserReportVO> getUserReports(String userId, int pageNum, int pageSize) {
        // 查询报告
        Page<UserReport> reportPage = userReportMapper.selectPage(
            new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<UserReport>()
                .eq(UserReport::getUserId, userId)
                .orderByDesc(UserReport::getCreateTime)
        );
        
        // 转换为VO
        Page<UserReportVO> voPage = new Page<>(pageNum, pageSize, reportPage.getTotal());
        List<UserReportVO> voList = reportPage.getRecords().stream().map(report -> {
            UserReportVO vo = new UserReportVO();
            BeanUtils.copyProperties(report, vo);
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public UserReport getReportById(String reportId) {
        return userReportMapper.selectById(reportId);
    }

    @Override
    public void markReportAsRead(String reportId) {
        UserReport report = userReportMapper.selectById(reportId);
        if (report != null && !report.getIsRead()) { // 只有未读时才更新
            report.setIsRead(true);
            userReportMapper.updateById(report);
        }
    }

    @Override
    public void generateWeeklyReportsForAllUsers() {
        // 获取上一周的开始时间（周一）
        LocalDateTime lastWeekStart = LocalDateTime.now()
            .minusWeeks(1)
            .with(ChronoField.DAY_OF_WEEK, 1)
            .toLocalDate().atStartOfDay();
        
        // 获取所有活跃用户
        List<User> activeUsers = userMapper.getActiveUsers(lastWeekStart);
        
        // 为每个用户生成周报告
        for (User user : activeUsers) {
            try {
                createWeeklyReport(user.getId(), lastWeekStart);
            } catch (Exception e) {
                // 记录错误，但继续处理其他用户
                System.err.println("生成用户 " + user.getId() + " 的周报告时出错: " + e.getMessage());
            }
        }
    }

    @Override
    public void generateMonthlyReportsForAllUsers() {
        // 获取上个月的年月
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();
        
        // 获取上个月有活动的用户
        LocalDateTime monthStart = YearMonth.of(year, month).atDay(1).atStartOfDay();
        List<User> activeUsers = userMapper.getActiveUsers(monthStart);
        
        // 为每个用户生成月报告
        for (User user : activeUsers) {
            try {
                createMonthlyReport(user.getId(), year, month);
            } catch (Exception e) {
                // 记录错误，但继续处理其他用户
                System.err.println("生成用户 " + user.getId() + " 的月报告时出错: " + e.getMessage());
            }
        }
    }
    
    /**
     * 收集生成报告所需的所有数据
     */
    private Map<String, Object> collectReportData(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> reportData = new HashMap<>();
        
        // 基本信息
        reportData.put("userId", userId);
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);
        reportData.put("generateTime", LocalDateTime.now());
        
        // 收集情绪数据
        MoodDataVO moodData = reportDataService.getMoodData(userId, startDate, endDate);
        reportData.put("moodData", moodData);
        
        // 收集日记数据
        JournalDataVO journalData = reportDataService.getJournalData(userId, startDate, endDate);
        reportData.put("journalData", journalData);
        
        // 收集测试结果数据
        TestResultDataVO testData = reportDataService.getTestResultData(userId, startDate, endDate);
        reportData.put("testData", testData);
        
        return reportData;
    }
    
    /**
     * 根据报告数据生成关键洞察和建议
     */
    private String generateInsights(Map<String, Object> reportData) {
        // 首先进行空检查
        if (reportData == null) {
            return "无法生成洞察：数据不可用。";
        }
        
        StringBuilder insights = new StringBuilder();
        
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日");
            LocalDateTime startDate = (LocalDateTime) reportData.get("startDate");
            LocalDateTime endDate = (LocalDateTime) reportData.get("endDate");

            if (startDate != null && endDate != null) {
                insights.append("这份报告分析了你在 " + startDate.format(dateFormatter) + " 到 " + endDate.format(dateFormatter) + " 期间的使用情况。\n\n");
            } else {
                insights.append("这份报告分析了你近期的使用情况。\n\n");
            }

            // 获取情绪数据
            MoodDataVO moodData = (MoodDataVO) reportData.get("moodData");
            if (moodData != null && moodData.getDailyMoods() != null && !moodData.getDailyMoods().isEmpty()) {
                // 情绪平均分洞察
                double avgScore = moodData.getAverageMoodScore();
                insights.append("• 情绪概览：你的平均情绪评分为 " + String.format("%.1f", avgScore) + "。");
                if (avgScore > 7) {
                    insights.append("整体情绪状态非常积极！\n");
                } else if (avgScore > 5) {
                    insights.append("整体情绪状态较为平稳。\n");
                } else {
                    insights.append("情绪评分偏低，建议关注情绪波动。\n");
                }

                // 情绪类型分析
                List<Map<String, Object>> emotionTypes = moodData.getEmotionTypeDistribution();
                if (emotionTypes != null && !emotionTypes.isEmpty()) {
                    try {
                        // 按 count 降序排序，添加空值检查
                        emotionTypes.sort((m1, m2) -> {
                            Number n1 = (Number) m2.get("count");
                            Number n2 = (Number) m1.get("count");
                            int v1 = (n1 != null) ? n1.intValue() : 0;
                            int v2 = (n2 != null) ? n2.intValue() : 0;
                            return v1 - v2;
                        });
                        
                        Map<String, Object> topEmotion = emotionTypes.get(0);
                        if (topEmotion != null) {
                            String emotionType = (String) topEmotion.get("emotion_type");
                            Number countObj = (Number) topEmotion.get("count");
                            if (emotionType != null && countObj != null) {
                                long count = countObj.longValue();
                                insights.append("• 主要情绪：你最常体验的情绪是「" + emotionType + "」，共记录了 " + count + " 次。\n");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("处理情绪类型数据时出错: " + e.getMessage());
                    }
                }
            } else {
                insights.append("• 情绪概览：这段时间没有足够的情绪记录来进行分析。\n");
            }

            // 日记数据洞察
            JournalDataVO journalData = (JournalDataVO) reportData.get("journalData");
            if (journalData != null) {
                int totalJournals = journalData.getTotalJournals();
                if (totalJournals > 0) {
                    insights.append("• 日记记录：你共记录了 " + totalJournals + " 篇日记。");
                    double avgLength = journalData.getAverageWordCount();
                    if(avgLength > 0) {
                        insights.append("平均篇幅约为 " + String.format("%.0f", avgLength) + " 字。\n");
                    } else {
                        insights.append("\n");
                    }

                    // 情绪标签分析 (如果 journalData 包含此信息)
                    List<Map<String, Object>> moodTags = journalData.getMoodTagStats();
                    if (moodTags != null && !moodTags.isEmpty()) {
                        try {
                            moodTags.sort((m1, m2) -> {
                                Number n1 = (Number) m2.get("count");
                                Number n2 = (Number) m1.get("count");
                                int v1 = (n1 != null) ? n1.intValue() : 0;
                                int v2 = (n2 != null) ? n2.intValue() : 0;
                                return v1 - v2;
                            });
                            
                            if (moodTags.get(0) != null) {
                                String topTag = (String) moodTags.get(0).get("mood_tags");
                                Number countObj = (Number) moodTags.get(0).get("count");
                                if (topTag != null && countObj != null) {
                                    long tagCount = countObj.longValue();
                                    insights.append("• 日记关键词：在日记中你最常用「" + topTag + "」标签，共 " + tagCount + " 次。\n");
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("处理日记标签数据时出错: " + e.getMessage());
                        }
                    }
                } else {
                    insights.append("• 日记记录：这段时间你没有记录日记。尝试记录可以帮助整理思绪。\n");
                }
            }

            // 测试数据洞察
            TestResultDataVO testData = (TestResultDataVO) reportData.get("testData");
            if (testData != null) {
                int totalTests = testData.getTotalTests();
                if (totalTests > 0) {
                    insights.append("• 心理测试：你完成了 " + totalTests + " 次心理测试。");

                    List<Map<String, Object>> testTypeDist = testData.getTestTypeDistribution();
                    if (testTypeDist != null && !testTypeDist.isEmpty()) {
                        try {
                            testTypeDist.sort((m1, m2) -> {
                                Number n1 = (Number) m2.get("count");
                                Number n2 = (Number) m1.get("count");
                                int v1 = (n1 != null) ? n1.intValue() : 0;
                                int v2 = (n2 != null) ? n2.intValue() : 0;
                                return v1 - v2;
                            });
                            
                            if (testTypeDist.get(0) != null) {
                                String topTest = (String) testTypeDist.get(0).get("test_name");
                                if (topTest == null) topTest = "类型 " + testTypeDist.get(0).get("test_type_id");
                                Number countObj = (Number) testTypeDist.get(0).get("count");
                                if (countObj != null) {
                                    long testCount = countObj.longValue();
                                    insights.append("其中「" + topTest + "」测试做得最多，共 " + testCount + " 次。\n");
                                } else {
                                    insights.append("\n");
                                }
                            } else {
                                insights.append("\n");
                            }
                        } catch (Exception e) {
                            System.err.println("处理测试类型数据时出错: " + e.getMessage());
                            insights.append("\n");
                        }
                    } else {
                        insights.append("\n");
                    }

                    // 如果有分数变化趋势，分析变化 - 这里是问题所在的第445行附近
                    Map<String, List<Map<String, Object>>> scoreChanges = testData.getScoreChanges();
                    if (scoreChanges != null && !scoreChanges.isEmpty()) {
                        insights.append("• 测试分数变化：\n");
                        for (Map.Entry<String, List<Map<String, Object>>> entry : scoreChanges.entrySet()) {
                            String testType = entry.getKey();
                            List<Map<String, Object>> scores = entry.getValue();

                            // 添加全面的空值检查
                            if (testType != null && scores != null && scores.size() >= 2) {
                                try {
                                    Map<String, Object> firstScoreMap = scores.get(0);
                                    Map<String, Object> lastScoreMap = scores.get(scores.size() - 1);
                                    
                                    if (firstScoreMap != null && lastScoreMap != null) {
                                        Number firstScoreNum = (Number) firstScoreMap.get("score");
                                        Number lastScoreNum = (Number) lastScoreMap.get("score");
                                        
                                        if (firstScoreNum != null && lastScoreNum != null) {
                                            double firstScore = firstScoreNum.doubleValue();
                                            double lastScore = lastScoreNum.doubleValue();
                                            String changeDesc = "";
                                            if (lastScore < firstScore) {
                                                changeDesc = "有所降低，情况可能在改善。";
                                            } else if (lastScore > firstScore) {
                                                changeDesc = "有所上升，建议关注。";
                                            } else {
                                                changeDesc = "保持稳定。";
                                            }
                                            insights.append("  - 你的「" + testType + "」测试分数" + changeDesc + "\n");
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("处理测试分数变化数据时出错: " + e.getMessage());
                                }
                            }
                        }
                    }
                } else {
                    insights.append("• 心理测试：这段时间你没有完成心理测试。定期自测有助于了解自身状态。\n");
                }
            }

            // 关键词云洞察
            List<Map<String, Object>> keywordData = (List<Map<String, Object>>) reportData.get("keywordData");
            if (keywordData != null && !keywordData.isEmpty()) {
                try {
                    // 添加额外的安全检查
                    keywordData.sort((m1, m2) -> {
                        Number n1 = (Number) m2.get("frequency");
                        Number n2 = (Number) m1.get("frequency");
                        int v1 = (n1 != null) ? n1.intValue() : 0;
                        int v2 = (n2 != null) ? n2.intValue() : 0;
                        return v1 - v2;
                    });
                    
                    if (!keywordData.isEmpty() && keywordData.get(0) != null) {
                        Object keywordObj = keywordData.get(0).get("keywords");
                        if (keywordObj != null) {
                            String topKeyword = keywordObj.toString();
                            insights.append("• 常用词：你近期思考或记录中，「" + topKeyword + "」是你关注较多的主题。\n");
                        }
                    }
                } catch (Exception e) {
                    // 捕获并记录任何可能的异常，防止中断报告生成
                    System.err.println("处理关键词数据时出错: " + e.getMessage());
                    // 不添加关键词洞察
                }
            } else {
                // 可选：添加无关键词数据的提示
                // insights.append("• 常用词：暂无足够数据分析您的常用词。\n");
            }

            // 根据所有数据给出综合建议
            insights.append("\n💡 综合建议：\n");
            insights.append("1. 继续保持记录习惯，无论是情绪还是日记，都能帮助你更好地了解自己。\n");
            if (moodData != null && moodData.getAverageMoodScore() <= 5) {
                insights.append("2. 鉴于近期情绪评分偏低，尝试寻找积极的应对方式或寻求支持。\n");
            } else {
                insights.append("2. 保持积极心态，关注生活中的小确幸。\n");
            }
            if (journalData == null || journalData.getTotalJournals() == 0) {
                insights.append("3. 如果还没开始写日记，不妨尝试一下，它可以成为你情绪的出口。\n");
            } else {
                insights.append("3. 回顾你的日记，或许能发现一些模式或触发点。\n");
            }
            if (testData == null || testData.getTotalTests() == 0) {
                insights.append("4. 探索一下心理测试功能，选择感兴趣的进行尝试。\n");
            } else {
                insights.append("4. 根据测试结果，思考可以采取哪些行动来改善或维持心理健康。\n");
            }
        } catch (Exception e) {
            // 捕获方法中任何可能的异常
            System.err.println("生成洞察时发生错误: " + e.getMessage());
            e.printStackTrace();
            return "无法生成完整的洞察报告，请稍后再试。";
        }

        return insights.toString();
    }
    
    /**
     * 检查指定类型和周期的报告是否已存在
     */
    private boolean checkReportExists(String userId, String reportType, String period) {
        return userReportMapper.selectCount(
            new LambdaQueryWrapper<UserReport>()
                .eq(UserReport::getUserId, userId)
                .eq(UserReport::getReportType, reportType)
                .eq(UserReport::getPeriod, period)
        ) > 0;
    }
} 