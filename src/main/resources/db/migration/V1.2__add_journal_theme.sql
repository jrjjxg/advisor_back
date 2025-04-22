-- 添加日记背景主题字段
ALTER TABLE user_journal ADD COLUMN theme VARCHAR(20) DEFAULT 'vintage' COMMENT '日记背景主题';

-- 更新现有数据，设置默认主题为'vintage'
UPDATE user_journal SET theme = 'vintage' WHERE theme IS NULL; 