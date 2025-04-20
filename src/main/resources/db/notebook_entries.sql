CREATE TABLE IF NOT EXISTS `notebook_entries` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `ai_message_content` TEXT NOT NULL COMMENT 'AI回复内容',
  `content_type` VARCHAR(50) DEFAULT '未分类' COMMENT '内容分类',
  `user_notes` TEXT COMMENT '用户笔记',
  `is_favorite` TINYINT(1) DEFAULT 0 COMMENT '是否收藏',
  `saved_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '保存时间',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI笔记本条目表'; 