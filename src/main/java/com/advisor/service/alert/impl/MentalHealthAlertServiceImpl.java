package com.advisor.service.alert.impl;

import com.advisor.entity.alert.AlertRecord;
import com.advisor.entity.alert.EmergencyContact;
import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.alert.UserAlertSetting;
import com.advisor.entity.journal.Journal;
import com.advisor.entity.mood.MoodRecord;
import com.advisor.entity.test.TestResult;
import com.advisor.mapper.alert.AlertMapper;
import com.advisor.mapper.alert.EmergencyContactMapper;
import com.advisor.mapper.alert.UserAlertSettingMapper;
import com.advisor.mapper.journal.JournalMapper;
import com.advisor.mapper.mood.MoodRecordMapper;
import com.advisor.mapper.test.TestResultMapper;
import com.advisor.service.alert.MentalHealthAlertService;
import com.advisor.service.alert.collector.DataSourceCollector;
import com.advisor.service.alert.collector.DataSourceType;
import com.advisor.service.alert.notification.AlertNotificationService;
import com.advisor.service.alert.rule.RiskAssessmentRule;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 心理健康预警服务实现
 */
@Service
public class MentalHealthAlertServiceImpl implements MentalHealthAlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(MentalHealthAlertServiceImpl.class);
    
    @Autowired
    private List<DataSourceCollector<?>> collectors;
    
    @Autowired
    private List<RiskAssessmentRule> rules;
    
    @Autowired
    private AlertMapper alertMapper;
    
    @Autowired
    private EmergencyContactMapper contactMapper;
    
    @Autowired
    private UserAlertSettingMapper settingMapper;
    
    @Autowired
    private TestResultMapper testResultMapper;
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private JournalMapper journalMapper;
    
    @Autowired
    private AlertNotificationService notificationService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 评估用户心理健康风险
     */
    @Override
    public List<RiskEvaluationResult> assessUserRisk(String userId) {
        logger.info("开始评估用户心理健康风险: {}", userId);
        
        // 设置评估时间范围，默认评估最近7天的数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(7);
        
        List<RiskEvaluationResult> allResults = new ArrayList<>();
        
        // 为每种数据源收集并评估数据
        for (DataSourceCollector<?> collector : collectors) {
            try {
                logger.debug("使用收集器: {} 收集数据", collector.getClass().getSimpleName());
                
                // 收集数据
                List<?> dataList = collector.collectData(userId, startTime, endTime);
                if (dataList == null || dataList.isEmpty()) {
                    logger.debug("收集到的数据为空");
                    continue;
                }
                
                logger.debug("收集到 {} 条数据", dataList.size());
                
                // 对每条数据应用所有适用的规则进行评估
                for (Object data : dataList) {
                    for (RiskAssessmentRule rule : rules) {
                        if (rule.getDataType().isInstance(data)) {
                            logger.debug("应用规则: {} 评估数据", rule.getName());
                            RiskEvaluationResult result = rule.evaluate(data);
                            if (result != null && result.getRiskLevel().getLevel() > RiskLevel.NORMAL.getLevel()) {
                                logger.debug("检测到风险: {}, 级别: {}", result.getRuleId(), result.getRiskLevel().getName());
                                allResults.add(result);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 记录异常，但不中断评估流程
                logger.error("数据收集或评估过程中发生错误: {}", e.getMessage(), e);
            }
        }
        
        logger.info("用户风险评估完成: {}, 发现风险数量: {}", userId, allResults.size());
        return allResults;
    }
    
    /**
     * 触发预警措施
     */
    @Override
    @Transactional
    public Map<String, Object> triggerAlert(String userId, List<RiskEvaluationResult> evaluationResults) {
        logger.info("开始触发用户预警措施: {}", userId);
        
        Map<String, Object> result = new HashMap<>();
        
        if (evaluationResults == null || evaluationResults.isEmpty()) {
            logger.info("无需触发预警，评估结果为空");
            result.put("success", false);
            result.put("message", "无需触发预警");
            return result;
        }
        
        // 获取最高风险级别
        RiskEvaluationResult highestRiskResult = evaluationResults.stream()
                .max(Comparator.comparing(r -> r.getRiskLevel().getLevel()))
                .orElse(null);
        
        if (highestRiskResult == null) {
            logger.warn("无法确定风险级别");
            result.put("success", false);
            result.put("message", "无法确定风险级别");
            return result;
        }
        
        // 获取用户预警设置
        UserAlertSetting setting = getUserAlertSetting(userId);
        
        // 创建预警记录
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setId(UUID.randomUUID().toString());
        alertRecord.setUserId(userId);
        alertRecord.setDataSourceId(highestRiskResult.getDataSourceId());
        alertRecord.setDataSourceType(highestRiskResult.getDataSourceType());
        alertRecord.setRiskLevel(highestRiskResult.getRiskLevel().getLevel());
        alertRecord.setRuleId(highestRiskResult.getRuleId());
        alertRecord.setRuleName(highestRiskResult.getRuleName());
        alertRecord.setContent(highestRiskResult.getDescription());
        alertRecord.setSuggestion(highestRiskResult.getSuggestion());
        alertRecord.setIsNotified(false);
        alertRecord.setIsEmergencyNotified(false);
        alertRecord.setStatus(0);
        alertRecord.setCreateTime(LocalDateTime.now());
        alertRecord.setUpdateTime(LocalDateTime.now());
        
        // 保存预警记录
        alertMapper.insert(alertRecord);
        logger.info("已创建预警记录: {}", alertRecord.getId());
        
        // 判断是否需要通知用户
        boolean userNotified = false;
        if (setting.getEnableSelfAlert()) {
            logger.info("尝试通知用户: {}", userId);
            userNotified = notificationService.notifyUser(userId, alertRecord);
            if (userNotified) {
                alertRecord.setIsNotified(true);
                alertRecord.setStatus(1);
                alertMapper.updateById(alertRecord);
                logger.info("用户通知成功");
            } else {
                logger.warn("用户通知失败");
            }
        } else {
            logger.info("用户禁用了自我预警通知");
        }
        
        // 判断是否需要通知紧急联系人
        boolean emergencyNotified = false;
        List<String> notifiedContacts = new ArrayList<>();
        
        if (setting.getEnableEmergencyAlert() && 
            highestRiskResult.getRiskLevel().getLevel() >= setting.getEmergencyThreshold()) {
            
            logger.info("尝试通知紧急联系人，风险级别: {}, 阈值: {}", 
                    highestRiskResult.getRiskLevel().getLevel(), setting.getEmergencyThreshold());
            
            // 获取紧急联系人列表
            List<EmergencyContact> contacts = getUserEmergencyContacts(userId);
            
            for (EmergencyContact contact : contacts) {
                // 如果联系人的通知阈值小于等于当前风险级别，则通知该联系人
                if (contact.getNotifyThreshold() <= highestRiskResult.getRiskLevel().getLevel()) {
                    logger.info("尝试通知联系人: {}, 联系人阈值: {}", contact.getName(), contact.getNotifyThreshold());
                    boolean notified = notificationService.notifyEmergencyContact(userId, contact, alertRecord);
                    if (notified) {
                        notifiedContacts.add(contact.getId());
                        emergencyNotified = true;
                        logger.info("联系人通知成功: {}", contact.getName());
                    } else {
                        logger.warn("联系人通知失败: {}", contact.getName());
                    }
                } else {
                    logger.info("跳过联系人通知，风险级别不足: {}, 联系人阈值: {}", 
                            highestRiskResult.getRiskLevel().getLevel(), contact.getNotifyThreshold());
                }
            }
            
            if (emergencyNotified) {
                alertRecord.setIsEmergencyNotified(true);
                alertMapper.updateById(alertRecord);
            }
        } else {
            if (!setting.getEnableEmergencyAlert()) {
                logger.info("用户禁用了紧急联系人通知");
            } else {
                logger.info("风险级别未达到紧急联系人通知阈值: {}, 阈值: {}", 
                        highestRiskResult.getRiskLevel().getLevel(), setting.getEmergencyThreshold());
            }
        }
        
        // 构建返回结果
        result.put("success", true);
        result.put("alertId", alertRecord.getId());
        result.put("userNotified", userNotified);
        result.put("emergencyNotified", emergencyNotified);
        result.put("notifiedContacts", notifiedContacts);
        result.put("riskLevel", highestRiskResult.getRiskLevel().getName());
        
        logger.info("预警触发完成: {}, 用户通知: {}, 紧急联系人通知: {}", 
                userId, userNotified, emergencyNotified);
        
        return result;
    }
    
    /**
     * 获取用户预警设置
     */
    @Override
    public UserAlertSetting getUserAlertSetting(String userId) {
        UserAlertSetting setting = settingMapper.selectById(userId);
        
        // 如果用户没有设置，创建默认设置
        if (setting == null) {
            setting = new UserAlertSetting();
            setting.setUserId(userId);
            setting.setEnableSelfAlert(true); // 默认启用自我预警
            setting.setEnableEmergencyAlert(false); // 默认禁用紧急联系人预警
            setting.setEmergencyThreshold(RiskLevel.SEVERE.getLevel()); // 默认严重风险才通知
            setting.setCreateTime(LocalDateTime.now());
            setting.setUpdateTime(LocalDateTime.now());
            
            settingMapper.insert(setting);
            logger.info("已为用户创建默认预警设置: {}", userId);
        }
        
        return setting;
    }
    
    /**
     * 更新用户预警设置
     */
    @Override
    public boolean updateUserAlertSetting(UserAlertSetting setting) {
        if (setting == null || setting.getUserId() == null) {
            return false;
        }
        
        // 检查设置是否存在
        UserAlertSetting existingSetting = settingMapper.selectById(setting.getUserId());
        
        if (existingSetting == null) {
            // 不存在则插入
            setting.setCreateTime(LocalDateTime.now());
            setting.setUpdateTime(LocalDateTime.now());
            settingMapper.insert(setting);
        } else {
            // 存在则更新
            setting.setUpdateTime(LocalDateTime.now());
            settingMapper.updateById(setting);
        }
        
        return true;
    }
    
    /**
     * 获取用户紧急联系人列表
     */
    @Override
    public List<EmergencyContact> getUserEmergencyContacts(String userId) {
        LambdaQueryWrapper<EmergencyContact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyContact::getUserId, userId)
                .orderByDesc(EmergencyContact::getIsPrimary)
                .orderByAsc(EmergencyContact::getNotifyThreshold);
        
        return contactMapper.selectList(queryWrapper);
    }
    
    /**
     * 添加紧急联系人
     */
    @Override
    public boolean addEmergencyContact(EmergencyContact contact) {
        if (contact == null || contact.getUserId() == null) {
            return false;
        }
        
        contact.setId(UUID.randomUUID().toString());
        contact.setCreateTime(LocalDateTime.now());
        contact.setUpdateTime(LocalDateTime.now());
        
        // 设置默认值
        if (contact.getNotifyThreshold() == null) {
            contact.setNotifyThreshold(RiskLevel.SEVERE.getLevel());
        }
        
        if (contact.getIsPrimary() == null) {
            contact.setIsPrimary(false);
        }
        
        // 如果设置为主要联系人，将其他联系人设为非主要
        if (Boolean.TRUE.equals(contact.getIsPrimary())) {
            LambdaQueryWrapper<EmergencyContact> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyContact::getUserId, contact.getUserId())
                    .eq(EmergencyContact::getIsPrimary, true);
            
            List<EmergencyContact> primaryContacts = contactMapper.selectList(queryWrapper);
            for (EmergencyContact primaryContact : primaryContacts) {
                primaryContact.setIsPrimary(false);
                contactMapper.updateById(primaryContact);
            }
        }
        
        contactMapper.insert(contact);
        return true;
    }
    
    /**
     * 更新紧急联系人
     */
    @Override
    public boolean updateEmergencyContact(EmergencyContact contact) {
        if (contact == null || contact.getId() == null) {
            return false;
        }
        
        EmergencyContact existingContact = contactMapper.selectById(contact.getId());
        if (existingContact == null) {
            return false;
        }
        
        // 检查是否需要更新主要联系人状态
        if (Boolean.TRUE.equals(contact.getIsPrimary()) && !Boolean.TRUE.equals(existingContact.getIsPrimary())) {
            // 将其他主要联系人设为非主要
            LambdaQueryWrapper<EmergencyContact> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyContact::getUserId, existingContact.getUserId())
                    .eq(EmergencyContact::getIsPrimary, true);
            
            List<EmergencyContact> primaryContacts = contactMapper.selectList(queryWrapper);
            for (EmergencyContact primaryContact : primaryContacts) {
                primaryContact.setIsPrimary(false);
                contactMapper.updateById(primaryContact);
            }
        }
        
        contact.setUpdateTime(LocalDateTime.now());
        contactMapper.updateById(contact);
        return true;
    }
    
    /**
     * 删除紧急联系人
     */
    @Override
    public boolean deleteEmergencyContact(String contactId) {
        if (contactId == null) {
            return false;
        }
        
        contactMapper.deleteById(contactId);
        return true;
    }
    
    /**
     * 获取用户预警记录
     */
    @Override
    public Page<AlertRecord> getUserAlertRecords(String userId, int page, int size) {
        Page<AlertRecord> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<AlertRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlertRecord::getUserId, userId)
                .orderByDesc(AlertRecord::getCreateTime);
        
        return alertMapper.selectPage(pageParam, queryWrapper);
    }
    
    /**
     * 处理预警记录
     */
    @Override
    public boolean handleAlert(String alertId, int status, String handledBy) {
        if (alertId == null) {
            return false;
        }
        
        AlertRecord alertRecord = alertMapper.selectById(alertId);
        if (alertRecord == null) {
            return false;
        }
        
        alertRecord.setStatus(status);
        alertRecord.setHandledBy(handledBy);
        alertRecord.setUpdateTime(LocalDateTime.now());
        
        alertMapper.updateById(alertRecord);
        return true;
    }
    
    /**
     * 测试结果提交后的风险评估
     */
    @Override
    public void assessTestResult(String userId, String testResultId) {
        logger.info("触发测试结果风险评估: {} - {}", userId, testResultId);
        
        try {
            // 获取测试结果
            TestResult testResult = testResultMapper.selectById(testResultId);
            if (testResult == null) {
                logger.warn("未找到测试结果: {}", testResultId);
                return;
            }
            
            // 应用所有适用的测试结果评估规则
            List<RiskEvaluationResult> evaluationResults = new ArrayList<>();
            for (RiskAssessmentRule rule : rules) {
                if (rule.getDataType() == TestResult.class) {
                    RiskEvaluationResult result = rule.evaluate(testResult);
                    if (result != null && result.getRiskLevel().getLevel() > RiskLevel.NORMAL.getLevel()) {
                        evaluationResults.add(result);
                        logger.info("测试结果评估发现风险: {}, 级别: {}", 
                                result.getRuleId(), result.getRiskLevel().getName());
                    }
                }
            }
            
            // 如果发现风险，触发预警
            if (!evaluationResults.isEmpty()) {
                Map<String, Object> alertResult = triggerAlert(userId, evaluationResults);
                logger.info("测试结果风险预警已触发: {}", alertResult);
            } else {
                logger.info("测试结果评估未发现风险");
            }
            
        } catch (Exception e) {
            logger.error("测试结果风险评估失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 情绪记录提交后的风险评估
     */
    @Override
    public void assessMoodRecord(String userId, String moodRecordId) {
        logger.info("触发情绪记录风险评估: {} - {}", userId, moodRecordId);
        
        try {
            // 获取情绪记录
            MoodRecord moodRecord = moodRecordMapper.selectById(moodRecordId);
            if (moodRecord == null) {
                logger.warn("未找到情绪记录: {}", moodRecordId);
                return;
            }
            
            // 应用所有适用的情绪记录评估规则
            List<RiskEvaluationResult> evaluationResults = new ArrayList<>();
            for (RiskAssessmentRule rule : rules) {
                if (rule.getDataType() == MoodRecord.class) {
                    RiskEvaluationResult result = rule.evaluate(moodRecord);
                    if (result != null && result.getRiskLevel().getLevel() > RiskLevel.NORMAL.getLevel()) {
                        evaluationResults.add(result);
                        logger.info("情绪记录评估发现风险: {}, 级别: {}", 
                                result.getRuleId(), result.getRiskLevel().getName());
                    }
                }
            }
            
            // 如果发现风险，触发预警
            if (!evaluationResults.isEmpty()) {
                Map<String, Object> alertResult = triggerAlert(userId, evaluationResults);
                logger.info("情绪记录风险预警已触发: {}", alertResult);
            } else {
                logger.info("情绪记录评估未发现风险");
            }
            
        } catch (Exception e) {
            logger.error("情绪记录风险评估失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 日记提交后的风险评估
     */
    @Override
    public void assessJournal(String userId, String journalId) {
        logger.info("触发日记内容风险评估: {} - {}", userId, journalId);
        
        try {
            // 获取日记
            Journal journal = journalMapper.selectById(journalId);
            if (journal == null) {
                logger.warn("未找到日记: {}", journalId);
                return;
            }
            
            // 应用所有适用的日记评估规则
            List<RiskEvaluationResult> evaluationResults = new ArrayList<>();
            for (RiskAssessmentRule rule : rules) {
                if (rule.getDataType() == Journal.class) {
                    RiskEvaluationResult result = rule.evaluate(journal);
                    if (result != null && result.getRiskLevel().getLevel() > RiskLevel.NORMAL.getLevel()) {
                        evaluationResults.add(result);
                        logger.info("日记内容评估发现风险: {}, 级别: {}", 
                                result.getRuleId(), result.getRiskLevel().getName());
                    }
                }
            }
            
            // 如果发现风险，触发预警
            if (!evaluationResults.isEmpty()) {
                Map<String, Object> alertResult = triggerAlert(userId, evaluationResults);
                logger.info("日记内容风险预警已触发: {}", alertResult);
            } else {
                logger.info("日记内容评估未发现风险");
            }
            
        } catch (Exception e) {
            logger.error("日记内容风险评估失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取预警记录详情
     */
    @Override
    public AlertRecord getAlertDetail(String userId, String alertId) {
        // 查询预警记录
        AlertRecord alertRecord = alertMapper.selectById(alertId);
        
        // 校验记录是否存在且属于当前用户
        if (alertRecord == null || !alertRecord.getUserId().equals(userId)) {
            return null;
        }
        
        return alertRecord;
    }
    
    /**
     * 触发测试预警
     * @param userId 用户ID
     * @param testRecord 测试预警记录
     * @return 测试结果
     */
    public Map<String, Object> triggerTestAlert(String userId, AlertRecord testRecord) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 保存测试预警记录
            testRecord.setId(UUID.randomUUID().toString());
            alertMapper.insert(testRecord);
            result.put("recordCreated", true);
            result.put("recordId", testRecord.getId());
            
            // 2. 获取用户预警设置
            UserAlertSetting setting = getUserAlertSetting(userId);
            if (setting == null || !setting.getEnableEmergencyAlert()) {
                result.put("emergencyAlertEnabled", false);
                return result;
            }
            
            result.put("emergencyAlertEnabled", true);
            result.put("emergencyThreshold", setting.getEmergencyThreshold());
            
            // 3. 检查是否满足紧急联系人通知条件
            if (testRecord.getRiskLevel() < setting.getEmergencyThreshold()) {
                result.put("thresholdReached", false);
                return result;
            }
            
            result.put("thresholdReached", true);
            
            // 4. 获取紧急联系人列表
            List<EmergencyContact> contacts = getUserEmergencyContacts(userId);
            if (contacts.isEmpty()) {
                result.put("contactsFound", false);
                return result;
            }
            
            result.put("contactsFound", true);
            result.put("contactCount", contacts.size());
            
            // 5. 发送测试通知
            List<Map<String, Object>> notificationResults = new ArrayList<>();
            for (EmergencyContact contact : contacts) {
                // 检查联系人的通知阈值
                if (testRecord.getRiskLevel() >= contact.getNotifyThreshold()) {
                    Map<String, Object> notificationResult = new HashMap<>();
                    notificationResult.put("contactId", contact.getId());
                    notificationResult.put("name", contact.getName());
                    
                    // 发送邮件通知
                    if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
                        boolean emailSent = sendEmailNotification(contact.getEmail(), testRecord);
                        notificationResult.put("emailSent", emailSent);
                    }
                    
                    // 电话通知逻辑（这里只模拟）
                    if (contact.getPhone() != null && !contact.getPhone().isEmpty()) {
                        notificationResult.put("phoneSmsSimulated", true);
                    }
                    
                    notificationResults.add(notificationResult);
                }
            }
            
            result.put("notifications", notificationResults);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            logger.error("测试预警通知出错", e);
        }
        
        return result;
    }
    
    /**
     * 发送邮件通知
     */
    private boolean sendEmailNotification(String email, AlertRecord alert) {
        try {
            // 这里调用您的邮件服务
            // 如果使用Spring Mail:
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("心理健康预警通知");
            message.setText("系统检测到用户心理健康状况出现异常，风险等级：" + 
                getRiskLevelName(alert.getRiskLevel()) + "\n\n详细信息：" + alert.getContent());
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件通知失败", e);
            return false;
        }
    }
    
    private String getRiskLevelName(int level) {
        switch (level) {
            case 2: return "轻度风险";
            case 3: return "中度风险";
            case 4: return "高度风险";
            case 5: return "紧急风险";
            default: return "未知风险";
        }
    }
} 