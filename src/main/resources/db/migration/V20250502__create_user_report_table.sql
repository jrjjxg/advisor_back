CREATE TABLE `user_report` (
  `id` VARCHAR(36) NOT NULL COMMENT '报告ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `report_type` VARCHAR(20) NOT NULL COMMENT '报告类型(monthly,weekly,yearly)',
  `period` VARCHAR(20) NOT NULL COMMENT '报告周期',
  `report_title` VARCHAR(100) NOT NULL COMMENT '报告标题',
  `report_content` LONGTEXT NOT NULL COMMENT '报告内容(JSON格式)',
  `key_insights` TEXT COMMENT '关键洞察点',
  `start_date` DATETIME NOT NULL COMMENT '开始日期',
  `end_date` DATETIME NOT NULL COMMENT '结束日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  PRIMARY KEY (`id`),
  INDEX `idx_user_period` (`user_id`, `period`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户报告表'; 