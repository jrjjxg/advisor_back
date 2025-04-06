package com.advisor.service.alert.notification;

import com.advisor.entity.alert.AlertRecord;
import com.advisor.entity.alert.EmergencyContact;

/**
 * 预警通知服务接口
 */
public interface AlertNotificationService {
    
    /**
     * 通知用户预警情况
     * @param userId 用户ID
     * @param alertRecord 预警记录
     * @return 通知是否成功
     */
    boolean notifyUser(String userId, AlertRecord alertRecord);
    
    /**
     * 通知紧急联系人
     * @param userId 用户ID
     * @param contact 联系人信息
     * @param alertRecord 预警记录
     * @return 通知是否成功
     */
    boolean notifyEmergencyContact(String userId, EmergencyContact contact, AlertRecord alertRecord);
    
    /**
     * 发送短信通知
     * @param phone 电话号码
     * @param message 消息内容
     * @return 发送是否成功
     */
    boolean sendSms(String phone, String message);
    
    /**
     * 发送邮件通知
     * @param email 邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送是否成功
     */
    boolean sendEmail(String email, String subject, String content);
    
    /**
     * 发送应用内消息
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param data 附加数据
     * @return 发送是否成功
     */
    boolean sendAppMessage(String userId, String title, String content, Object data);
} 