package com.advisor.service.alert;

import com.advisor.entity.alert.AlertRecord;
import com.advisor.entity.alert.EmergencyContact;
import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.alert.UserAlertSetting;
import com.advisor.service.alert.rule.RiskEvaluationResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 心理健康预警服务接口
 */
public interface MentalHealthAlertService {
    
    /**
     * 评估用户心理健康风险
     * @param userId 用户ID
     * @return 风险评估结果列表
     */
    List<RiskEvaluationResult> assessUserRisk(String userId);
    
    /**
     * 根据评估结果触发预警措施
     * @param userId 用户ID
     * @param evaluationResults 评估结果列表
     * @return 干预操作结果
     */
    Map<String, Object> triggerAlert(String userId, List<RiskEvaluationResult> evaluationResults);
    
    /**
     * 获取用户预警设置
     * @param userId 用户ID
     * @return 预警设置
     */
    UserAlertSetting getUserAlertSetting(String userId);
    
    /**
     * 更新用户预警设置
     * @param setting 预警设置
     * @return 操作结果
     */
    boolean updateUserAlertSetting(UserAlertSetting setting);
    
    /**
     * 获取用户紧急联系人列表
     * @param userId 用户ID
     * @return 联系人列表
     */
    List<EmergencyContact> getUserEmergencyContacts(String userId);
    
    /**
     * 添加紧急联系人
     * @param contact 联系人信息
     * @return 操作结果
     */
    boolean addEmergencyContact(EmergencyContact contact);
    
    /**
     * 更新紧急联系人
     * @param contact 联系人信息
     * @return 操作结果
     */
    boolean updateEmergencyContact(EmergencyContact contact);
    
    /**
     * 删除紧急联系人
     * @param contactId 联系人ID
     * @return 操作结果
     */
    boolean deleteEmergencyContact(String contactId);
    
    /**
     * 获取用户预警记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预警记录分页
     */
    Page<AlertRecord> getUserAlertRecords(String userId, int page, int size);
    
    /**
     * 处理预警记录
     * @param alertId 预警ID
     * @param status 处理状态
     * @param handledBy 处理人
     * @return 操作结果
     */
    boolean handleAlert(String alertId, int status, String handledBy);
    
    /**
     * 测试结果提交后的风险评估
     * @param userId 用户ID
     * @param testResultId 测试结果ID
     */
    void assessTestResult(String userId, String testResultId);
    
    /**
     * 情绪记录提交后的风险评估
     * @param userId 用户ID
     * @param moodRecordId 情绪记录ID
     */
    void assessMoodRecord(String userId, String moodRecordId);
    
    /**
     * 日记提交后的风险评估
     * @param userId 用户ID
     * @param journalId 日记ID
     */
    void assessJournal(String userId, String journalId);
    
    /**
     * 获取预警记录详情
     * @param userId 用户ID
     * @param alertId 预警ID
     * @return 预警记录
     */
    AlertRecord getAlertDetail(String userId, String alertId);
    
    /**
     * 触发测试预警
     * @param userId 用户ID
     * @param testRecord 测试预警记录
     * @return 测试结果
     */
    Map<String, Object> triggerTestAlert(String userId, AlertRecord testRecord);
} 