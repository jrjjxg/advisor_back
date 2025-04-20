-- 创建用户活动日志表
CREATE TABLE `user_activity_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `event_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件类型',
  `event_timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件发生时间',
  `duration_ms` bigint DEFAULT NULL COMMENT '持续时长(毫秒)',
  `page_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '页面URL或标识',
  `feature_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '功能名称或标识',
  `event_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '事件详情(JSON格式)',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`event_timestamp`),
  KEY `idx_event_time` (`event_type`,`event_timestamp`),
  KEY `idx_feature` (`feature_name`),
  KEY `idx_timestamp` (`event_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户活动日志表'; 