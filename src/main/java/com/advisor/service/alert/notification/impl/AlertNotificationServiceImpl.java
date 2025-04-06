package com.advisor.service.alert.notification.impl;

import com.advisor.entity.alert.AlertRecord;
import com.advisor.entity.alert.EmergencyContact;
import com.advisor.entity.alert.RiskLevel;
import com.advisor.entity.base.User;
import com.advisor.mapper.base.UserMapper;
import com.advisor.service.alert.notification.AlertNotificationService;
import com.advisor.service.notification.SystemNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 预警通知服务实现
 */
@Service
public class AlertNotificationServiceImpl implements AlertNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertNotificationServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SystemNotificationService systemNotificationService;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${app.notification.email.from:noreply@advisor.com}")
    private String emailFrom;
    
    @Value("${app.notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Override
    public boolean notifyUser(String userId, AlertRecord alertRecord) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            logger.error("通知用户失败，用户不存在: {}", userId);
            return false;
        }
        
        // 构建通知内容
        String title = "心理健康提醒";
        String content = buildUserNotificationContent(alertRecord);
        
        // 发送应用内消息
        boolean appMessageSent = sendAppMessage(userId, title, content, alertRecord);
        
        // 如果风险较高，发送邮件通知
        boolean emailSent = false;
        if (alertRecord.getRiskLevel() >= RiskLevel.MODERATE.getLevel() && user.getEmail() != null) {
            emailSent = sendEmail(user.getEmail(), title, content);
        }
        
        return appMessageSent || emailSent;
    }
    
    @Override
    public boolean notifyEmergencyContact(String userId, EmergencyContact contact, AlertRecord alertRecord) {
        User user = userMapper.selectById(userId);
        if (user == null || contact == null) {
            logger.error("通知紧急联系人失败，用户或联系人不存在: {}, {}", userId, contact);
            return false;
        }
        
        // 构建通知内容
        String message = buildEmergencyContactMessage(user, contact, alertRecord);
        
        // 尝试发送短信
        boolean smsSent = false;
        if (smsEnabled && contact.getPhone() != null) {
            smsSent = sendSms(contact.getPhone(), message);
        }
        
        // 如果短信发送失败或未启用短信，尝试发送邮件
        boolean emailSent = false;
        if ((!smsSent || !smsEnabled) && contact.getEmail() != null) {
            String subject = "关于 " + user.getNickname() + " 的心理健康提醒";
            emailSent = sendEmail(contact.getEmail(), subject, message);
        }
        
        return smsSent || emailSent;
    }
    
    @Override
    public boolean sendSms(String phone, String message) {
        if (!smsEnabled) {
            logger.info("短信服务未启用，跳过发送短信: {}", phone);
            return false;
        }
        
        try {
            // 实际短信发送逻辑
            logger.info("向 {} 发送短信: {}", phone, message);
            // TODO: 接入实际的短信发送服务
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendEmail(String email, String subject, String content) {
        if (mailSender == null) {
            logger.info("邮件服务未配置，跳过发送邮件: {}", email);
            return false;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            logger.info("邮件发送成功: {}", email);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendAppMessage(String userId, String title, String content, Object data) {
        try {
            // 使用新的系统通知服务发送预警通知
            boolean result = systemNotificationService.sendAlertNotification(userId, title, content, data);
            logger.info("发送应用内消息结果: {}, 用户ID: {}", result, userId);
            return result;
        } catch (Exception e) {
            logger.error("发送应用内消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 构建用户通知内容
     */
    private String buildUserNotificationContent(AlertRecord alertRecord) {
        RiskLevel riskLevel = RiskLevel.fromLevel(alertRecord.getRiskLevel());
        StringBuilder sb = new StringBuilder();
        
        sb.append("亲爱的用户，我们关注到您的心理健康状况可能需要关注。\n\n");
        sb.append("根据系统评估，您当前的风险等级为: ").append(riskLevel.getName()).append("\n\n");
        sb.append("具体情况: ").append(alertRecord.getContent()).append("\n\n");
        sb.append("建议: ").append(alertRecord.getSuggestion()).append("\n\n");
        sb.append("我们关心您的健康，如需帮助，请随时联系专业人士。");
        
        return sb.toString();
    }
    
    /**
     * 构建紧急联系人通知内容
     */
    private String buildEmergencyContactMessage(User user, EmergencyContact contact, AlertRecord alertRecord) {
        RiskLevel riskLevel = RiskLevel.fromLevel(alertRecord.getRiskLevel());
        StringBuilder sb = new StringBuilder();
        
        sb.append("尊敬的 ").append(contact.getName()).append("，\n\n");
        sb.append("您是 ").append(user.getNickname()).append(" 的紧急联系人。\n\n");
        sb.append("我们检测到他/她可能面临 ").append(riskLevel.getName()).append(" 的心理健康状况，");
        sb.append("建议您及时与他/她取得联系并提供适当的支持。\n\n");
        
        // 不提供过多具体细节，保护隐私
        sb.append("为保护隐私，我们不会提供具体的评估细节，但鼓励您：\n");
        sb.append("1. 与他/她保持联系\n");
        sb.append("2. 倾听并表达关怀\n");
        sb.append("3. 必要时鼓励寻求专业帮助\n\n");
        
        sb.append("感谢您的支持与理解。\n");
        sb.append("——心理健康顾问系统");
        
        return sb.toString();
    }
} 