-- 预警记录表
CREATE TABLE `mental_health_alert` (
  `id` varchar(50) NOT NULL COMMENT '预警ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `data_source_id` varchar(50) DEFAULT NULL COMMENT '触发预警的数据源ID',
  `data_source_type` varchar(50) DEFAULT NULL COMMENT '数据源类型',
  `risk_level` int NOT NULL COMMENT '风险等级(1-5级)',
  `rule_id` varchar(50) DEFAULT NULL COMMENT '触发规则ID',
  `rule_name` varchar(100) DEFAULT NULL COMMENT '触发规则名称',
  `content` text DEFAULT NULL COMMENT '预警内容描述',
  `suggestion` text DEFAULT NULL COMMENT '建议措施',
  `is_notified` tinyint(1) DEFAULT 0 COMMENT '是否已通知用户',
  `is_emergency_notified` tinyint(1) DEFAULT 0 COMMENT '是否已通知紧急联系人',
  `handled_by` varchar(50) DEFAULT NULL COMMENT '处理人(用户/系统/咨询师ID)',
  `status` int DEFAULT 0 COMMENT '状态(0-未处理,1-已通知,2-已干预,3-已解决)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理健康预警记录表';

-- 紧急联系人表
CREATE TABLE `emergency_contact` (
  `id` varchar(50) NOT NULL COMMENT '联系人ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '联系人姓名',
  `relationship` varchar(50) DEFAULT NULL COMMENT '与用户关系',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `notify_threshold` int DEFAULT 3 COMMENT '通知阈值(1-5级)',
  `is_primary` tinyint(1) DEFAULT 0 COMMENT '是否为主要联系人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急联系人表';

-- 用户预警设置表
CREATE TABLE `alert_setting` (
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `enable_self_alert` tinyint(1) DEFAULT 1 COMMENT '启用自我预警',
  `enable_emergency_alert` tinyint(1) DEFAULT 0 COMMENT '启用紧急联系人预警',
  `emergency_threshold` int DEFAULT 4 COMMENT '紧急通知阈值(1-5级)',
  `consent_time` datetime DEFAULT NULL COMMENT '同意时间',
  `consent_ip` varchar(50) DEFAULT NULL COMMENT '同意时的IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户预警设置表'; 