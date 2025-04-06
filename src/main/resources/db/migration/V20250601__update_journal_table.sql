-- 移除原有字段
ALTER TABLE `user_journal` DROP COLUMN `mood_tags`;
ALTER TABLE `user_journal` DROP COLUMN `mood_score`;

-- 添加关联字段
ALTER TABLE `user_journal` ADD COLUMN `related_mood_id` VARCHAR(36) NULL COMMENT '关联的心情记录ID';

-- 添加索引
CREATE INDEX `idx_related_mood_id` ON `user_journal` (`related_mood_id`); 