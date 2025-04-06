-- 系统通知表
CREATE TABLE `system_notification` (
  `id` varchar(50) NOT NULL COMMENT '通知ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text DEFAULT NULL COMMENT '通知内容',
  `type` varchar(20) NOT NULL COMMENT '通知类型',
  `data` text DEFAULT NULL COMMENT '附加数据(JSON格式)',
  `is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表'; 