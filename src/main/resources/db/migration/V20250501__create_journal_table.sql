CREATE TABLE `user_journal` (
  `id` VARCHAR(36) NOT NULL COMMENT '日记ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) COMMENT '日记标题',
  `content` LONGTEXT COMMENT '日记内容(富文本)',
  `mood_tags` VARCHAR(100) COMMENT '情绪标签',
  `mood_score` INT COMMENT '情绪评分(1-10)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `keywords` VARCHAR(255) COMMENT '提取的关键词',
  `word_count` INT DEFAULT 0 COMMENT '字数统计',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户日记表'; 