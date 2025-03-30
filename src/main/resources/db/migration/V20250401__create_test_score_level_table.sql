CREATE TABLE `test_score_level` (
  `id` VARCHAR(36) NOT NULL COMMENT '级别ID',
  `test_type_id` VARCHAR(36) NOT NULL COMMENT '测试类型ID',
  `level_name` VARCHAR(50) NOT NULL COMMENT '级别名称',
  `min_score` INT NOT NULL COMMENT '最小分数（包含）',
  `max_score` INT NOT NULL COMMENT '最大分数（包含）',
  `description` TEXT COMMENT '该级别的描述模板',
  `suggestions` TEXT COMMENT '该级别的建议模板',
  `order_num` INT NOT NULL DEFAULT 0 COMMENT '排序编号',
  PRIMARY KEY (`id`),
  INDEX `idx_test_type` (`test_type_id`),
  CONSTRAINT `fk_level_test_type` FOREIGN KEY (`test_type_id`) REFERENCES `test_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试分数等级表';

-- 预填充 PHQ-9 测试分数等级
INSERT INTO `test_score_level` 
(`id`, `test_type_id`, `level_name`, `min_score`, `max_score`, `description`, `suggestions`, `order_num`) 
VALUES
(UUID(), 'phq9', '正常', 0, 4, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您的情绪状态良好，没有明显的抑郁症状。', '1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。', 1),
(UUID(), 'phq9', '轻度抑郁', 5, 9, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在一定程度的抑郁症状，如情绪低落、兴趣减退等。', '1. 注意休息，保持规律的作息时间。\n2. 适当进行体育锻炼，如散步、慢跑等。\n3. 学习简单的放松技巧，如深呼吸、冥想等。\n4. 与亲友交流，分享自己的感受。', 2),
(UUID(), 'phq9', '中度抑郁', 10, 14, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在较明显的抑郁症状，如情绪低落、睡眠问题、精力不足等。', '1. 建议寻求专业心理咨询师的帮助。\n2. 培养积极的应对方式，如运动、社交活动等。\n3. 与亲友保持联系，获取社会支持。\n4. 规律作息，健康饮食，适当运动。', 3),
(UUID(), 'phq9', '中重度抑郁', 15, 19, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在明显的抑郁症状，影响了日常生活和工作。', '1. 强烈建议咨询专业心理医生或精神科医生。\n2. 可能需要心理治疗或药物治疗，请遵循医生建议。\n3. 保持与家人朋友的联系，获取社会支持。\n4. 避免重大决策，关注自身安全。', 4),
(UUID(), 'phq9', '重度抑郁', 20, 27, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在严重的抑郁症状，已明显影响生活。', '1. 请立即咨询专业精神科医生。\n2. 需要综合治疗，包括药物治疗和心理治疗。\n3. 密切关注情绪变化和安全问题。\n4. 寻求家人和朋友的支持和陪伴。\n5. 遵医嘱规律作息，避免酒精和药物滥用。', 5);

-- 预填充 GAD-7 测试分数等级
INSERT INTO `test_score_level` 
(`id`, `test_type_id`, `level_name`, `min_score`, `max_score`, `description`, `suggestions`, `order_num`) 
VALUES
(UUID(), 'gad-7', '正常', 0, 4, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您的焦虑水平在正常范围内。', '1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。', 1),
(UUID(), 'gad-7', '轻度焦虑', 5, 9, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在一定程度的焦虑症状，如担忧、紧张等。', '1. 学习基础的放松技巧，如深呼吸法、渐进性肌肉放松等。\n2. 适量运动，有助于缓解焦虑。\n3. 保持规律作息，避免过度劳累。\n4. 减少咖啡因摄入。', 2),
(UUID(), 'gad-7', '中度焦虑', 10, 14, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您的焦虑症状较为明显，可能影响日常生活。', '1. 建议咨询专业心理咨询师。\n2. 练习正念冥想和认知调整技巧。\n3. 寻求社会支持，与亲友交流感受。\n4. 适当调整工作和生活压力。', 3),
(UUID(), 'gad-7', '重度焦虑', 15, 21, '您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您的焦虑症状严重，已明显影响生活。', '1. 建议尽快咨询专业心理医生或精神科医生。\n2. 可能需要心理治疗或药物治疗，请遵循医嘱。\n3. 学习应对焦虑的特定技巧，如认知行为疗法技术。\n4. 避免过度关注焦虑症状。\n5. 规律作息，健康饮食，适当运动。', 4); 