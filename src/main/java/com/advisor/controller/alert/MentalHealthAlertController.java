package com.advisor.controller.alert;

import com.advisor.common.Result;
import com.advisor.entity.alert.AlertRecord;
import com.advisor.entity.alert.EmergencyContact;
import com.advisor.entity.alert.UserAlertSetting;
import com.advisor.service.alert.MentalHealthAlertService;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import com.advisor.util.UserUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 心理健康预警控制器
 */
@RestController
@RequestMapping("/api/alert")
public class MentalHealthAlertController {
    
    @Autowired
    private MentalHealthAlertService mentalHealthAlertService;
    
    /**
     * 获取用户预警设置
     */
    @GetMapping("/settings")
    public Result<UserAlertSetting> getUserAlertSettings() {
        String userId = UserUtil.getCurrentUserId();
        UserAlertSetting settings = mentalHealthAlertService.getUserAlertSetting(userId);
        return Result.success(settings);
    }
    
    /**
     * 更新用户预警设置
     */
    @PostMapping("/settings")
    public Result<Boolean> updateUserAlertSettings(@RequestBody UserAlertSetting settings) {
        String userId = UserUtil.getCurrentUserId();
        settings.setUserId(userId);
        boolean success = mentalHealthAlertService.updateUserAlertSetting(settings);
        return Result.success(success);
    }
    
    /**
     * 获取紧急联系人列表
     */
    @GetMapping("/emergency-contacts")
    public Result<List<EmergencyContact>> getEmergencyContacts(
            @RequestHeader(value = "userId", required = false) String headerUserId) {
        String userId;
        
        // 优先使用请求头中的 userId
        if (headerUserId != null && !headerUserId.trim().isEmpty()) {
            userId = headerUserId;
        } else {
            // 如果请求头中没有，则尝试从 Spring Security 上下文获取
            userId = UserUtil.getCurrentUserId();
        }
        
        if (userId == null) {
            return Result.fail(401, "未提供有效的用户ID");
        }
        
        List<EmergencyContact> contacts = mentalHealthAlertService.getUserEmergencyContacts(userId);
        return Result.success(contacts);
    }
    
    /**
     * 添加紧急联系人
     */
    @PostMapping("/emergency-contacts")
    public Result<Boolean> addEmergencyContact(@RequestBody EmergencyContact contact) {
        String userId = UserUtil.getCurrentUserId();
        contact.setUserId(userId);
        boolean success = mentalHealthAlertService.addEmergencyContact(contact);
        return Result.success(success);
    }
    
    /**
     * 更新紧急联系人
     */
    @PutMapping("/emergency-contacts")
    public Result<Boolean> updateEmergencyContact(@RequestBody EmergencyContact contact) {
        String userId = UserUtil.getCurrentUserId();
        
        // 验证联系人是否属于当前用户
        EmergencyContact existingContact = mentalHealthAlertService.getUserEmergencyContacts(userId).stream()
                .filter(c -> c.getId().equals(contact.getId()))
                .findFirst()
                .orElse(null);
        
        if (existingContact == null) {
            return Result.fail("无效的联系人ID");
        }
        
        contact.setUserId(userId);
        boolean success = mentalHealthAlertService.updateEmergencyContact(contact);
        return Result.success(success);
    }
    
    /**
     * 删除紧急联系人
     */
    @DeleteMapping("/emergency-contacts/{contactId}")
    public Result<Boolean> deleteEmergencyContact(@PathVariable String contactId) {
        String userId = UserUtil.getCurrentUserId();
        
        // 验证联系人是否属于当前用户
        boolean isUserContact = mentalHealthAlertService.getUserEmergencyContacts(userId).stream()
                .anyMatch(c -> c.getId().equals(contactId));
        
        if (!isUserContact) {
            return Result.fail("无效的联系人ID");
        }
        
        boolean success = mentalHealthAlertService.deleteEmergencyContact(contactId);
        return Result.success(success);
    }
    
    /**
     * 获取用户预警记录
     */
    @GetMapping("/records")
    public Result<Page<AlertRecord>> getAlertRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = UserUtil.getCurrentUserId();
        Page<AlertRecord> records = mentalHealthAlertService.getUserAlertRecords(userId, page, size);
        return Result.success(records);
    }
    
    /**
     * 主动触发风险评估
     */
    @PostMapping("/assess")
    public Result<Map<String, Object>> triggerRiskAssessment() {
        String userId = UserUtil.getCurrentUserId();
        List<RiskEvaluationResult> results = mentalHealthAlertService.assessUserRisk(userId);
        
        if (results.isEmpty()) {
            return Result.success(Map.of("status", "no_risk", "message", "未检测到风险"));
        }
        
        Map<String, Object> alertResult = mentalHealthAlertService.triggerAlert(userId, results);
        return Result.success(alertResult);
    }
    
    /**
     * 处理预警记录
     */
    @PostMapping("/handle/{alertId}")
    public Result<Boolean> handleAlert(
            @PathVariable String alertId,
            @RequestParam int status) {
        String userId = UserUtil.getCurrentUserId();
        boolean success = mentalHealthAlertService.handleAlert(alertId, status, userId);
        return Result.success(success);
    }
    
    /**
     * 获取预警记录详情
     */
    @GetMapping("/records/{alertId}")
    public Result<AlertRecord> getAlertDetail(@PathVariable String alertId) {
        String userId = UserUtil.getCurrentUserId();
        AlertRecord alertRecord = mentalHealthAlertService.getAlertDetail(userId, alertId);
        
        if (alertRecord == null) {
            return Result.error("预警记录不存在");
        }
        
        return Result.success(alertRecord);
    }
    
    /**
     * 测试触发预警通知功能
     * @param testParams 测试参数
     * @return 测试结果
     */
    @PostMapping("/test-alert")
    public Result<Map<String, Object>> testAlertNotification(@RequestBody Map<String, Object> testParams) {
        String userId = UserUtil.getCurrentUserId();
        Integer riskLevel = testParams.get("riskLevel") != null ?
            Integer.parseInt(testParams.get("riskLevel").toString()) : 4;
        String content = testParams.get("content") != null ?
            testParams.get("content").toString() : "这是一条测试预警消息";

        // 创建测试预警记录
        AlertRecord testRecord = new AlertRecord();
        testRecord.setUserId(userId);
        testRecord.setRiskLevel(riskLevel);
        testRecord.setContent(content);
        testRecord.setDataSourceType("manual_test");
        testRecord.setCreateTime(LocalDateTime.now());

        // 触发通知测试
        Map<String, Object> result = mentalHealthAlertService.triggerTestAlert(userId, testRecord);
        return Result.success(result);
    }
} 