-- MySQL dump 10.13  Distrib 9.0.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ai
-- ------------------------------------------------------
-- Server version	9.0.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user` (
  `id` varchar(36) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密后）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` int DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES ('864a2dc3c9e49b54219b73e314cac969','lsd','$2a$10$rcNPQzvWMsduR47u182ZjeyOHifS/J1KyceQzfd/W1taAkvsd9FB6','1025828544@qq.com',1,'2025-03-07 17:36:17','2025-03-07 17:36:27'),('e3b960462e5a5c6c78f4fffeddd1020d','jrj','$2a$10$AlW.tJPetFwy12NXo3DSM.YA19Wyvayly5fcNkuKH9Q25r0Q8cnmC','2902756263@qq.com',1,'2025-03-29 19:14:44','2025-03-30 17:58:25');
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alert_setting`
--

DROP TABLE IF EXISTS `alert_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alert_setting` (
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `enable_self_alert` tinyint(1) DEFAULT '1' COMMENT '启用自我预警',
  `enable_emergency_alert` tinyint(1) DEFAULT '0' COMMENT '启用紧急联系人预警',
  `emergency_threshold` int DEFAULT '4' COMMENT '紧急通知阈值(1-5级)',
  `consent_time` datetime DEFAULT NULL COMMENT '同意时间',
  `consent_ip` varchar(50) DEFAULT NULL COMMENT '同意时的IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户预警设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alert_setting`
--

LOCK TABLES `alert_setting` WRITE;
/*!40000 ALTER TABLE `alert_setting` DISABLE KEYS */;
INSERT INTO `alert_setting` VALUES ('1001',1,1,4,NULL,NULL,'2025-04-02 04:56:16','2025-04-02 04:57:23'),('2e8e32a9-1609-4036-8ee6-c1cc252a91f7',1,1,4,NULL,NULL,'2025-04-05 13:55:11','2025-04-06 19:25:20');
/*!40000 ALTER TABLE `alert_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` varchar(36) NOT NULL COMMENT '评论ID',
  `post_id` varchar(36) NOT NULL COMMENT '帖子ID',
  `user_id` varchar(36) NOT NULL COMMENT '评论用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '父评论ID，回复评论时使用',
  `reply_to_user_id` varchar(36) DEFAULT NULL COMMENT '回复的用户ID',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名：0-否，1-是',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reply_user_id` varchar(36) DEFAULT NULL COMMENT '回复用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES ('123a','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,1,0,1,'2025-03-17 20:12:11',NULL),('12dac0a0d598d8642ffa47310f95efd5','6ab0f073f5d5503bc6aa6f73ad35eaa5','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','666',NULL,NULL,0,0,1,'2025-04-05 14:46:32',NULL),('28d9949d304d9261d241f951b53b6f10','6ab0f073f5d5503bc6aa6f73ad35eaa5','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','666',NULL,NULL,0,0,1,'2025-04-05 15:21:48',NULL),('28e99492f289bc12856f19738e69e946','c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','博主666',NULL,NULL,0,0,1,'2025-03-17 21:45:44',NULL),('31c3369683586718490bb9dfce853571','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,0,0,1,'2025-03-19 11:33:26',NULL),('3320debf05876f8120b4d63805703c31','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','66666',NULL,NULL,0,0,1,'2025-03-17 21:27:49',NULL),('3d605333e38af81ea3c51fd9fd4584c3','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','这个入是桂','123a',NULL,0,0,1,'2025-03-17 21:28:00','a2fee174e375fd82559200ffb13785f3'),('4bc4633549c65cd7b0f909976611b0c4','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','test',NULL,NULL,0,0,1,'2025-03-19 00:04:40',NULL),('51cdd3062e13a7e742743867cdce50b1','9ac7540140fe1b92d7e21c8ff09515f1','a2fee174e375fd82559200ffb13785f3','大神666',NULL,NULL,0,0,1,'2025-03-19 00:19:18',NULL),('54abc5c0af6a49ce2fe42f42f6b956e1','2ffc953cd1ac790906866e340f20ed1b','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,0,0,1,'2025-03-19 11:32:54',NULL),('5c4adc483dd7320be37655ab8ca7aba3','05ea89b3404969d391b515a0a89b4e73','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','楼主所言极是！妙哉妙哉',NULL,NULL,0,0,1,'2025-03-19 00:44:07',NULL),('5c5f14f40778b245fc9cbcf6af19cda7','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','功能正常',NULL,NULL,0,0,1,'2025-03-19 02:08:45',NULL),('5d089973e86d66232fcf8f025affb9ee','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','2112',NULL,NULL,1,0,1,'2025-03-17 21:25:07',NULL),('5e5939f44c3f1d9ecb028bb182be0c8a','734dbe2b34228a5f4b4e0bef6e013123','a2fee174e375fd82559200ffb13785f3','66666',NULL,NULL,0,0,1,'2025-03-19 13:28:27',NULL),('8c56cfa8ea424039a07817eda076dfa9','40a614b88dfc3c25dc5547842c67d941','a2fee174e375fd82559200ffb13785f3','楼主666',NULL,NULL,0,0,1,'2025-03-19 11:19:00',NULL),('91c87216115d0c5157e69f64db6fad9b','2ffc953cd1ac790906866e340f20ed1b','a2fee174e375fd82559200ffb13785f3','5666',NULL,NULL,0,0,1,'2025-03-28 14:55:09',NULL),('95b1d4a529f77d9fbf21c580b775b2e6','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','哈哈哈',NULL,NULL,0,0,1,'2025-03-17 22:15:00',NULL),('c8f09a34cf8db8260908624a7bcceee6','c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','123',NULL,NULL,0,0,1,'2025-03-18 22:55:04',NULL),('ce3d92416cd5afd601db46e6e0b46ddb','6ab0f073f5d5503bc6aa6f73ad35eaa5','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,0,0,1,'2025-04-05 15:18:01',NULL);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drift_bottle`
--

DROP TABLE IF EXISTS `drift_bottle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drift_bottle` (
  `id` varchar(36) NOT NULL COMMENT '漂流瓶ID',
  `user_id` varchar(36) NOT NULL COMMENT '发布用户ID',
  `content` text NOT NULL COMMENT '内容',
  `images` text COMMENT '图片，多个用逗号分隔',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名：0-否，1-是',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已发布，2-已捞取，3-已拒绝',
  `audit_user_id` varchar(36) DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_reason` varchar(255) DEFAULT NULL COMMENT '审核拒绝原因',
  `pick_user_id` varchar(36) DEFAULT NULL COMMENT '捞取用户ID',
  `pick_time` datetime DEFAULT NULL COMMENT '捞取时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_bottle_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漂流瓶表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drift_bottle`
--

LOCK TABLES `drift_bottle` WRITE;
/*!40000 ALTER TABLE `drift_bottle` DISABLE KEYS */;
INSERT INTO `drift_bottle` VALUES ('03c4860597f71b9928cbd621a465d458','a2fee174e375fd82559200ffb13785f3','12',NULL,0,3,NULL,'2025-03-27 11:18:46','11',NULL,NULL,'2025-03-26 21:32:12','2025-03-27 11:18:46'),('39fe2d52a11b6734bace00dcc6abe372','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12',NULL,1,2,NULL,'2025-03-26 21:30:06',NULL,'a2fee174e375fd82559200ffb13785f3','2025-03-26 21:36:31','2025-03-26 13:15:23','2025-03-26 21:36:31'),('677b91da8e0a66cf55dbde3dadbe8f84','a2fee174e375fd82559200ffb13785f3','2231231',NULL,0,2,NULL,'2025-03-27 11:18:47',NULL,'a2fee174e375fd82559200ffb13785f3','2025-03-27 11:24:13','2025-03-26 21:32:21','2025-03-27 11:24:13'),('ce59b29a9546a2410f446f2a338737c4','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','哈哈哈',NULL,0,2,NULL,'2025-03-29 16:01:02',NULL,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','2025-04-08 21:16:40','2025-03-27 20:45:54','2025-04-08 21:16:40');
/*!40000 ALTER TABLE `drift_bottle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drift_bottle_reply`
--

DROP TABLE IF EXISTS `drift_bottle_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drift_bottle_reply` (
  `id` varchar(36) NOT NULL COMMENT '回复ID',
  `bottle_id` varchar(36) NOT NULL COMMENT '漂流瓶ID',
  `user_id` varchar(36) NOT NULL COMMENT '回复用户ID',
  `content` text NOT NULL COMMENT '回复内容',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bottle_id` (`bottle_id`),
  KEY `fk_reply_user` (`user_id`),
  CONSTRAINT `fk_reply_bottle` FOREIGN KEY (`bottle_id`) REFERENCES `drift_bottle` (`id`),
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漂流瓶回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drift_bottle_reply`
--

LOCK TABLES `drift_bottle_reply` WRITE;
/*!40000 ALTER TABLE `drift_bottle_reply` DISABLE KEYS */;
INSERT INTO `drift_bottle_reply` VALUES ('94ba558758eec009ecabb545342da85c','677b91da8e0a66cf55dbde3dadbe8f84','a2fee174e375fd82559200ffb13785f3','666',1,'2025-03-27 11:24:23');
/*!40000 ALTER TABLE `drift_bottle_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drift_bottle_report`
--

DROP TABLE IF EXISTS `drift_bottle_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drift_bottle_report` (
  `id` varchar(36) NOT NULL COMMENT '举报ID',
  `bottle_id` varchar(36) NOT NULL COMMENT '漂流瓶ID',
  `user_id` varchar(36) NOT NULL COMMENT '举报用户ID',
  `reason` varchar(255) NOT NULL COMMENT '举报原因',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-待处理，1-已处理',
  `handle_user_id` varchar(36) DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bottle_id` (`bottle_id`),
  KEY `fk_report_user` (`user_id`),
  CONSTRAINT `fk_report_bottle` FOREIGN KEY (`bottle_id`) REFERENCES `drift_bottle` (`id`),
  CONSTRAINT `fk_report_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漂流瓶举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drift_bottle_report`
--

LOCK TABLES `drift_bottle_report` WRITE;
/*!40000 ALTER TABLE `drift_bottle_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `drift_bottle_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emergency_contact`
--

DROP TABLE IF EXISTS `emergency_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emergency_contact` (
  `id` varchar(50) NOT NULL COMMENT '联系人ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '联系人姓名',
  `relationship` varchar(50) DEFAULT NULL COMMENT '与用户关系',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `notify_threshold` int DEFAULT '3' COMMENT '通知阈值(1-5级)',
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否为主要联系人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='紧急联系人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emergency_contact`
--

LOCK TABLES `emergency_contact` WRITE;
/*!40000 ALTER TABLE `emergency_contact` DISABLE KEYS */;
INSERT INTO `emergency_contact` VALUES ('3d865f51-7b32-428d-9704-502a51885655','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','jrj','myself','','2902756263@qq.com',3,0,'2025-04-07 19:19:54','2025-04-07 19:19:54'),('9925e70b-c745-4ace-8213-b754e02e754c','1001','11','家人','17724132','2902756263@qq.com',5,0,'2025-04-02 04:56:45','2025-04-02 05:04:50');
/*!40000 ALTER TABLE `emergency_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow_relation`
--

DROP TABLE IF EXISTS `follow_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow_relation` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL COMMENT '用户id',
  `follow_user_id` varchar(36) NOT NULL COMMENT '被关注的用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`),
  KEY `idx_follow_user` (`follow_user_id`),
  CONSTRAINT `fk_follow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_followed_user` FOREIGN KEY (`follow_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关注关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_relation`
--

LOCK TABLES `follow_relation` WRITE;
/*!40000 ALTER TABLE `follow_relation` DISABLE KEYS */;
INSERT INTO `follow_relation` VALUES ('ac6a07aa4ae62e159a76ebf95d1586d1','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','2025-03-19 02:01:07'),('d50ffd1dabaf79651a2e8bcc63f84566','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','2025-04-07 17:02:41');
/*!40000 ALTER TABLE `follow_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `like_record`
--

DROP TABLE IF EXISTS `like_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like_record` (
  `id` varchar(64) NOT NULL COMMENT '点赞记录ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `target_id` varchar(36) NOT NULL COMMENT '点赞目标ID（帖子ID或评论ID）',
  `type` tinyint(1) NOT NULL COMMENT '类型：1-帖子，2-评论',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`type`),
  KEY `idx_target` (`target_id`,`type`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_record`
--

LOCK TABLES `like_record` WRITE;
/*!40000 ALTER TABLE `like_record` DISABLE KEYS */;
INSERT INTO `like_record` VALUES ('029bb43e85d98917a9e319d43a3fb37e','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','f78ef89f3d9c93ca8034eb62a4156424',1,'2025-04-07 08:54:11'),('0464c7d0ff2655ad092ba5dc144e3326','a2fee174e375fd82559200ffb13785f3','c73961a386567d3e0005313aaba528eb',1,'2025-03-19 00:41:11'),('09699a80b600bdc194c25b4de6a7b9b8','a2fee174e375fd82559200ffb13785f3','734dbe2b34228a5f4b4e0bef6e013123',1,'2025-03-19 00:40:58'),('101045fa07ac2d99c3d3f8047509ac1a','a2fee174e375fd82559200ffb13785f3','05ea89b3404969d391b515a0a89b4e73',1,'2025-03-19 00:40:53'),('28befec217dadfc356249b9f888c4662','a2fee174e375fd82559200ffb13785f3','6ab0f073f5d5503bc6aa6f73ad35eaa5',1,'2025-04-05 15:17:58'),('2d813323bbf35cad395cf39eb85a84bb','a2fee174e375fd82559200ffb13785f3','5d089973e86d66232fcf8f025affb9ee',2,'2025-03-17 21:25:36'),('3d86c8e99c0c5cd0e572a3a0aac256b2','a2fee174e375fd82559200ffb13785f3','7b4f858e42856d4f3071aa2a0cbad663',1,'2025-03-19 00:40:20'),('415823faa28afd8002c99868f7f6c73c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','bcb9a166a66901c160663439947974c1',1,'2025-04-04 03:08:15'),('6078f093a0278bab5fd9f4b162ca130c','a2fee174e375fd82559200ffb13785f3','9ac7540140fe1b92d7e21c8ff09515f1',1,'2025-03-19 00:41:01'),('6e079556f48f53b307625a1d2bbe4cc9','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','05ea89b3404969d391b515a0a89b4e73',1,'2025-03-19 00:43:45'),('7716588fb30656106440b8a044cf2b81','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','21905479168e55d58a091498bf6c5dd3',1,'2025-04-04 04:07:48'),('a5eb7389bff0a20c84b8e8a0abe3a816','a2fee174e375fd82559200ffb13785f3','123a',2,'2025-03-17 21:24:56'),('de33ddfbbc4c1db9dcb6991eef7e8c17','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','40a614b88dfc3c25dc5547842c67d941',1,'2025-03-19 00:43:39'),('dec1c7a8394ae3ea42cf1321f3e86554','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','9ac7540140fe1b92d7e21c8ff09515f1',1,'2025-03-19 01:37:54');
/*!40000 ALTER TABLE `like_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_category`
--

DROP TABLE IF EXISTS `media_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_category` (
  `id` varchar(36) NOT NULL COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `media_type` tinyint NOT NULL COMMENT '媒体类型：1=视频，2=音频',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
  `sort` int DEFAULT '0' COMMENT '排序字段，值越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='媒体分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_category`
--

LOCK TABLES `media_category` WRITE;
/*!40000 ALTER TABLE `media_category` DISABLE KEYS */;
INSERT INTO `media_category` VALUES ('02adb6e847ea4959712b81ab425cff67','深呼吸',2,'',2,'2025-03-29 15:18:42','2025-03-29 15:18:42'),('93af495520c849aace32f1866d9df345','冥想音频',2,'http://localhost:9000/uploads/6ff5b57e-1bb8-4985-b829-70420d525f9f.png',1,'2025-03-29 14:30:08','2025-03-29 14:30:08'),('bbb268d284bad68daebb9af06288b451','心理科普',1,'',1,'2025-03-29 15:18:56','2025-03-29 15:18:56');
/*!40000 ALTER TABLE `media_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_resource`
--

DROP TABLE IF EXISTS `media_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_resource` (
  `id` varchar(36) NOT NULL COMMENT '资源ID',
  `title` varchar(100) NOT NULL COMMENT '资源标题',
  `description` text COMMENT '资源描述',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `resource_url` varchar(255) NOT NULL COMMENT '资源URL',
  `media_type` tinyint NOT NULL COMMENT '媒体类型：1=视频，2=音频',
  `duration` int DEFAULT '0' COMMENT '时长（秒）',
  `category_id` varchar(36) DEFAULT NULL COMMENT '所属分类ID',
  `views` int DEFAULT '0' COMMENT '播放次数',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0=未发布，1=已发布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `media_resource_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `media_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='媒体资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_resource`
--

LOCK TABLES `media_resource` WRITE;
/*!40000 ALTER TABLE `media_resource` DISABLE KEYS */;
INSERT INTO `media_resource` VALUES ('745ffb69fa2b3a6d62d0faf5817ee1fe','123','123','covers/ebbf7111dafe4081914a8008e3f6aa83.png','audios/87bbe3ae153f422e829d08124a396c7e.mp3',2,0,'93af495520c849aace32f1866d9df345',0,0,0,'2025-03-29 15:24:39','2025-03-29 15:54:05'),('cc6e9bcc6f5443558253995c53641e60','deep-meditation',NULL,NULL,'audios/597955645d7b45daaed267fb2768f409.mp3',2,0,'93af495520c849aace32f1866d9df345',0,0,1,'2025-03-29 15:55:00','2025-03-29 15:55:00');
/*!40000 ALTER TABLE `media_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mental_health_activity`
--

DROP TABLE IF EXISTS `mental_health_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mental_health_activity` (
  `id` varchar(36) NOT NULL COMMENT '活动ID',
  `title` varchar(100) NOT NULL COMMENT '活动标题',
  `description` text NOT NULL COMMENT '活动描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(255) DEFAULT NULL COMMENT '地点',
  `is_online` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否线上：0-线下，1-线上',
  `online_url` varchar(255) DEFAULT NULL COMMENT '线上链接',
  `organizer_id` varchar(36) NOT NULL COMMENT '组织者ID',
  `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int NOT NULL DEFAULT '0' COMMENT '当前参与人数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-取消，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_organizer` (`organizer_id`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `fk_activity_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理健康活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mental_health_activity`
--

LOCK TABLES `mental_health_activity` WRITE;
/*!40000 ALTER TABLE `mental_health_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `mental_health_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mental_health_alert`
--

DROP TABLE IF EXISTS `mental_health_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mental_health_alert` (
  `id` varchar(50) NOT NULL COMMENT '预警ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `data_source_id` varchar(50) DEFAULT NULL COMMENT '触发预警的数据源ID',
  `data_source_type` varchar(50) DEFAULT NULL COMMENT '数据源类型',
  `risk_level` int NOT NULL COMMENT '风险等级(1-5级)',
  `rule_id` varchar(50) DEFAULT NULL COMMENT '触发规则ID',
  `rule_name` varchar(100) DEFAULT NULL COMMENT '触发规则名称',
  `content` text COMMENT '预警内容描述',
  `suggestion` text COMMENT '建议措施',
  `is_notified` tinyint(1) DEFAULT '0' COMMENT '是否已通知用户',
  `is_emergency_notified` tinyint(1) DEFAULT '0' COMMENT '是否已通知紧急联系人',
  `handled_by` varchar(50) DEFAULT NULL COMMENT '处理人(用户/系统/咨询师ID)',
  `status` int DEFAULT '0' COMMENT '状态(0-未处理,1-已通知,2-已干预,3-已解决)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理健康预警记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mental_health_alert`
--

LOCK TABLES `mental_health_alert` WRITE;
/*!40000 ALTER TABLE `mental_health_alert` DISABLE KEYS */;
INSERT INTO `mental_health_alert` VALUES ('03fc67c8-971b-49ae-ae81-d5495d7279c7','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:35','2025-04-05 13:55:35'),('0615379e-dc32-40a7-82e7-770ed923eae7','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:58:01','2025-04-05 13:58:01'),('0c103a0c-f6ab-4db9-81cc-1d5eca669fac','1001',NULL,'manual_test',4,NULL,NULL,'这是一条测试预警消息',NULL,0,0,NULL,0,'2025-04-02 05:24:12','2025-04-02 05:24:11'),('307cc943-1827-4b13-9672-751d58afdaab','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:58:00','2025-04-05 13:58:00'),('57748703-a87e-4406-a2ae-185232b7192d','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:18','2025-04-05 13:55:18'),('5deb30db-8988-44cb-ba5a-daff0c629de5','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:58:04','2025-04-05 13:58:04'),('696205e7-a240-44a9-a697-0c8c093e33eb','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:23','2025-04-05 13:57:23'),('6a497918-9ff7-49c2-92d4-9c40b56db3ff','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:12','2025-04-05 13:55:12'),('77dc8a5d-f495-4673-8990-7d3f7a44859a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:16','2025-04-05 13:55:16'),('7c777377-5ba2-4b37-ac1f-045fc580d5ed','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:59:49','2025-04-05 13:59:49'),('8258158f-6a88-4f68-9379-b982ac69bac8','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:11','2025-04-05 13:55:11'),('827de3d4-6fa9-4910-9531-4105b87fddcc','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:22','2025-04-05 13:57:22'),('86d661c1-530e-4c45-bc52-c17fc217e532','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:43','2025-04-05 13:57:43'),('8c3cd2bd-2d58-4db5-9155-f1dfc6f58639','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:59:51','2025-04-05 13:59:51'),('8c520388-f25c-4b8a-80a6-4c184dd8deeb','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:26','2025-04-05 13:55:26'),('922fb804-6764-429c-859a-e7db9f53ea7c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:34','2025-04-05 13:57:34'),('971bb2c4-b528-4ba0-9104-3bd7e943de85','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:59:53','2025-04-05 13:59:53'),('b58a9c9b-9d9e-4adc-9e8c-d590c428f8f6','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:55','2025-04-05 13:57:55'),('c4707465-7bc3-4762-8c25-2f7eb31fdcf8','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:48','2025-04-05 13:57:48'),('c61ce852-e85e-446b-a796-d865370b4893','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:26','2025-04-05 13:57:26'),('c6bb62cd-5891-4155-ba18-9793a675d4bf','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:18','2025-04-05 13:55:18'),('c9c56417-7ed3-42a7-a0ee-9bf0f4ef2707','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:49','2025-04-05 13:57:49'),('cded5659-625f-481e-a806-a209697ba2d2','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:24','2025-04-05 13:57:24'),('d8b83d71-c193-4695-bbdc-5faf98bb7f6c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:57:35','2025-04-05 13:57:35'),('e3894755-4c7d-463e-95a4-b59f2a17e953','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:59:53','2025-04-05 13:59:53'),('e4ad85cd-6379-4a17-a7b3-85eb72156a59','1001',NULL,'manual_test',4,NULL,NULL,'这是一条测试预警消息',NULL,0,0,NULL,0,'2025-04-02 05:23:04','2025-04-02 05:23:04'),('f0c51cf3-e614-456e-93da-8a2f3a6683a0','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:59:52','2025-04-05 13:59:52'),('fdb699de-8296-4cf2-9198-105c0eb0fc0b','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','17','MOOD_RECORD',2,'negative-mood-rule','负面情绪风险评估','检测到轻微的负面情绪状态','建议关注自身情绪变化，尝试积极的应对策略',1,0,NULL,1,'2025-04-05 13:55:23','2025-04-05 13:55:23');
/*!40000 ALTER TABLE `mental_health_alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mood_record`
--

DROP TABLE IF EXISTS `mood_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mood_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `emotion_type` varchar(20) NOT NULL COMMENT '情绪类型：快乐/愉悦、平静/满足、焦虑/紧张等',
  `intensity` tinyint(1) NOT NULL COMMENT '情绪强度，1-5',
  `note` text COMMENT '用户记录的文字描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_mood_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_record`
--

LOCK TABLES `mood_record` WRITE;
/*!40000 ALTER TABLE `mood_record` DISABLE KEYS */;
INSERT INTO `mood_record` VALUES (1,'test_user_001','悲伤/低落',3,'55656','2025-03-10 18:53:11','2025-03-10 18:53:11'),(2,'test_user_001','悲伤/低落',3,'','2025-03-10 18:53:26','2025-03-10 18:53:26'),(3,'test_user_001','愤怒/烦躁',3,'','2025-03-11 18:56:21','2025-03-11 18:56:21'),(4,'test_user_001','平静/满足',3,'111','2025-03-11 19:03:47','2025-03-11 19:03:47'),(5,'test_user_001','平静/满足',3,'12','2025-03-15 21:26:46','2025-03-15 21:26:46'),(6,'test_user_001','疲惫/无力',3,'123','2025-03-15 21:31:54','2025-03-15 21:31:54'),(7,'test_user_001','愤怒/烦躁',3,'','2025-03-17 19:44:49','2025-03-17 19:44:49'),(8,'test_user_001','悲伤/低落',3,'太累了吧','2025-03-22 21:01:40','2025-03-22 21:01:40'),(9,'test_user_001','愤怒/烦躁',3,'123','2025-03-23 13:48:57','2025-03-23 13:48:57'),(10,'test_user_001','平静/满足',3,'','2025-03-23 14:52:05','2025-03-23 14:52:05'),(11,'a2fee174e375fd82559200ffb13785f3','疲惫/无力',3,'','2025-03-23 15:10:01','2025-03-23 15:10:01'),(12,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','平静/满足',3,'','2025-03-25 12:20:51','2025-03-25 12:20:51'),(13,'a2fee174e375fd82559200ffb13785f3','疲惫/无力',3,'','2025-03-25 12:27:28','2025-03-25 12:27:28'),(14,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','中性/平淡',5,'111','2025-03-25 14:30:30','2025-03-25 14:30:30'),(15,'a2fee174e375fd82559200ffb13785f3','愤怒/烦躁',3,'12','2025-03-28 14:54:30','2025-03-28 14:54:30'),(16,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','焦虑/紧张',3,'学业','2025-03-31 02:53:32','2025-03-31 02:53:32'),(17,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','疲惫/无力',3,'123','2025-03-31 19:23:24','2025-03-31 19:23:24'),(18,'a2fee174e375fd82559200ffb13785f3','快乐/愉悦',3,'今天是清明节，天朗气清。','2025-04-04 20:57:21','2025-04-04 20:57:21'),(19,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','平静/满足',3,'','2025-04-09 09:02:06','2025-04-09 09:02:06'),(20,'2e8e32a9-1609-4036-8ee6-c1cc252a91f7','疲惫/无力',3,'','2025-04-09 09:02:14','2025-04-09 09:02:14');
/*!40000 ALTER TABLE `mood_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mood_record_tag`
--

DROP TABLE IF EXISTS `mood_record_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mood_record_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `mood_record_id` bigint NOT NULL COMMENT '情绪记录ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_tag` (`mood_record_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_mrt_record` FOREIGN KEY (`mood_record_id`) REFERENCES `mood_record` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mrt_tag` FOREIGN KEY (`tag_id`) REFERENCES `mood_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪记录-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_record_tag`
--

LOCK TABLES `mood_record_tag` WRITE;
/*!40000 ALTER TABLE `mood_record_tag` DISABLE KEYS */;
INSERT INTO `mood_record_tag` VALUES (1,2,1),(2,4,2),(3,4,3),(4,5,4),(5,5,5),(6,6,4),(7,6,6),(8,7,1),(10,8,3),(9,8,5),(11,9,1),(12,10,3),(13,11,3),(14,12,2),(15,13,2),(20,14,1),(17,14,2),(16,14,4),(19,14,5),(18,14,6),(21,15,2),(22,15,6),(23,16,4),(25,17,4),(24,17,6);
/*!40000 ALTER TABLE `mood_record_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mood_tag`
--

DROP TABLE IF EXISTS `mood_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mood_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `category` varchar(20) DEFAULT NULL COMMENT '标签分类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_tag`
--

LOCK TABLES `mood_tag` WRITE;
/*!40000 ALTER TABLE `mood_tag` DISABLE KEYS */;
INSERT INTO `mood_tag` VALUES (1,'休息质量',NULL,'2025-03-10 18:53:26'),(2,'人际关系',NULL,'2025-03-11 19:03:47'),(3,'个人成就',NULL,'2025-03-11 19:03:47'),(4,'工作/学习',NULL,'2025-03-15 21:26:46'),(5,'健康状况',NULL,'2025-03-15 21:26:46'),(6,'天气变化',NULL,'2025-03-15 21:31:54');
/*!40000 ALTER TABLE `mood_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` varchar(36) NOT NULL COMMENT '通知ID',
  `user_id` varchar(36) NOT NULL COMMENT '接收用户ID',
  `sender_id` varchar(36) DEFAULT NULL COMMENT '发送用户ID',
  `type` varchar(20) NOT NULL COMMENT '类型：like/comment/follow/system',
  `target_id` varchar(36) DEFAULT NULL COMMENT '目标ID',
  `content` text NOT NULL COMMENT '通知内容',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_notify_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES ('0683d8372a86d7c651997f2ed19144c1','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','comment','12dac0a0d598d8642ffa47310f95efd5','评论了你的帖子',1,'2025-04-05 14:46:32'),('0a76de149a83032aff83015ef071c0ad','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:47:31'),('1400f73f70435f9f56f863cc5685de7f','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:52'),('2a9879b61df48a72963dea6f28c8dac8','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:53:58'),('2e9c46769aa66265915d9fdba05d277a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:59:12'),('2f8df20730fe698b48b030ee417bf3e0','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:51:02'),('37bf035dc21b6deeb0069de67e4ba9dd','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:48:01'),('44465f614eb871b40f8c46a188065e6d','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-04-07 17:02:41'),('5e57aafb3ae2c71502c9b82f0c7bc577','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:46'),('5eb8455214776d2a7c2780a1d413ef0c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 11:06:11'),('6082eebd7d0162766042cff53267b037','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 01:37:58'),('66c068ca8bf79a759c4c70374bfb9aae','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:25:01'),('70348b8d107cb8d03a85a52349434b01','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','comment','5c4adc483dd7320be37655ab8ca7aba3','评论了你的帖子',1,'2025-03-19 00:44:07'),('96817eec28435fbb35e9413e6781bd69','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:50:33'),('9cc52d61ff71d9b65ac5e693b6aa8ad2','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:51'),('9ef0ad51ff63517346875dfa7bdc7493','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','comment','8c56cfa8ea424039a07817eda076dfa9','评论了你的帖子',1,'2025-03-19 11:19:00'),('9ffd6e87725bdb3862477d5f993d33cd','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:48:12'),('a17423c982aa26b8c8ab5a7d07ceca6e','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:40:29'),('a648d54ae14c58a8c5fe23eb03776b45','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','comment','28d9949d304d9261d241f951b53b6f10','评论了你的帖子',1,'2025-04-05 15:21:48'),('a7d30ef962d388253c69f31c75b2113e','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:53'),('ad90f0258b928bc3f313efc1cd8a39b8','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','like','05ea89b3404969d391b515a0a89b4e73','点赞了你的帖子',1,'2025-03-19 00:43:45'),('b4ef83d9886cf702c1e549c1a8d372cb','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','like','9ac7540140fe1b92d7e21c8ff09515f1','点赞了你的帖子',1,'2025-03-19 01:37:54'),('ce81d849cd128654c30d49f05a93b2b9','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:53'),('d502c395bec0c1baaa2b46867032decf','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:47:47'),('e12daa98cfdb2e7510301d939d65a072','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','follow','a2fee174e375fd82559200ffb13785f3','关注了你',1,'2025-03-19 02:01:07'),('e88f9f039832021edefd5517a883dde3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:52:48'),('ec1571568c2e5b9e0c0d1b435c25a9f0','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-03-19 00:47:22'),('f7a176e33ff62e1b74ff2849a0430fbb','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',1,'2025-03-19 10:46:46'),('fc4ecf114bdb967c416672dfbcc646bb','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',1,'2025-04-04 03:22:49');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `option_template`
--

DROP TABLE IF EXISTS `option_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `option_template` (
  `id` varchar(36) NOT NULL COMMENT '模板ID',
  `name` varchar(50) NOT NULL COMMENT '模板名称',
  `description` varchar(255) DEFAULT NULL COMMENT '模板描述',
  `test_type_id` varchar(36) DEFAULT NULL COMMENT '关联的测试类型ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_test_type` (`test_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选项模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `option_template`
--

LOCK TABLES `option_template` WRITE;
/*!40000 ALTER TABLE `option_template` DISABLE KEYS */;
INSERT INTO `option_template` VALUES ('2b4a2c2bf6544cb79b060d93a5654052','scl-90','',NULL,'2025-03-29 20:16:47','2025-03-29 20:16:47');
/*!40000 ALTER TABLE `option_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `physical_symptom`
--

DROP TABLE IF EXISTS `physical_symptom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `physical_symptom` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '症状ID',
  `name` varchar(50) NOT NULL COMMENT '症状名称',
  `category` varchar(20) DEFAULT NULL COMMENT '症状分类',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_symptom_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='身体症状表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `physical_symptom`
--

LOCK TABLES `physical_symptom` WRITE;
/*!40000 ALTER TABLE `physical_symptom` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_symptom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` varchar(36) NOT NULL COMMENT '帖子ID',
  `user_id` varchar(36) NOT NULL COMMENT '发布用户ID',
  `content` text NOT NULL COMMENT '帖子内容',
  `images` text COMMENT '图片URL，多个用逗号分隔',
  `location` varchar(100) DEFAULT NULL COMMENT '位置信息',
  `mood_record_id` bigint DEFAULT NULL COMMENT '关联的情绪记录ID',
  `test_result_id` varchar(36) DEFAULT NULL COMMENT '关联的测试结果ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名：0-否，1-是',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_mood_record` (`mood_record_id`),
  KEY `idx_test_result` (`test_result_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_post_mood` FOREIGN KEY (`mood_record_id`) REFERENCES `mood_record` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_post_test` FOREIGN KEY (`test_result_id`) REFERENCES `test_result` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES ('05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','666666','',NULL,NULL,NULL,186,2,5,0,1,'2025-03-17 22:06:21','2025-04-05 14:56:19'),('21905479168e55d58a091498bf6c5dd3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','2','http://su5mwlhe1.hb-bkt.clouddn.com/community/4cb094f7a8594a34b93a296a6d7409cf.jpg',NULL,NULL,NULL,22,1,0,0,0,'2025-04-04 03:20:47','2025-04-07 08:53:29'),('2ffc953cd1ac790906866e340f20ed1b','a2fee174e375fd82559200ffb13785f3','666','',NULL,NULL,NULL,62,0,2,0,1,'2025-03-19 11:19:30','2025-04-04 03:22:28'),('40a614b88dfc3c25dc5547842c67d941','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','多用户测试','',NULL,NULL,NULL,108,1,1,0,1,'2025-03-19 00:43:25','2025-04-04 03:22:28'),('6ab0f073f5d5503bc6aa6f73ad35eaa5','a2fee174e375fd82559200ffb13785f3','12',NULL,NULL,NULL,NULL,22,1,3,0,1,'2025-04-04 01:46:16','2025-04-05 15:22:15'),('734dbe2b34228a5f4b4e0bef6e013123','a2fee174e375fd82559200ffb13785f3','·12','',NULL,NULL,NULL,64,1,1,0,1,'2025-03-17 21:57:38','2025-04-04 03:22:28'),('7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','1111',NULL,NULL,NULL,NULL,212,1,3,0,1,'2025-03-17 18:49:45','2025-04-04 03:21:57'),('97edfb4eb0e7f3217cd250f8583a27f0','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','pic',NULL,NULL,NULL,NULL,0,0,0,0,1,'2025-04-04 03:18:34','2025-04-04 03:18:34'),('9ac7540140fe1b92d7e21c8ff09515f1','a2fee174e375fd82559200ffb13785f3','123',NULL,NULL,NULL,NULL,46,2,1,0,1,'2025-03-17 21:57:14','2025-04-05 15:17:45'),('b5517f010c24f999b0d2ee36490a68c1','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','·12',NULL,NULL,NULL,NULL,16,0,0,0,0,'2025-04-04 03:05:36','2025-04-04 03:07:35'),('bcb9a166a66901c160663439947974c1','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','1',NULL,NULL,NULL,NULL,6,1,0,0,1,'2025-04-04 03:07:48','2025-04-04 03:09:43'),('c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','13','',NULL,NULL,NULL,70,1,2,1,1,'2025-03-17 21:45:32','2025-04-04 03:22:38'),('f78ef89f3d9c93ca8034eb62a4156424','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','http://su5mwlhe1.hb-bkt.clouddn.com/other/605308fd160b413a898eddc3d9271802.png',NULL,NULL,NULL,14,1,0,0,1,'2025-04-07 08:54:01','2025-04-09 10:02:16');
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_tag`
--

DROP TABLE IF EXISTS `post_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tag` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `post_id` varchar(36) NOT NULL COMMENT '帖子ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_tag_name` (`tag_name`),
  CONSTRAINT `fk_post_tag_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_tag`
--

LOCK TABLES `post_tag` WRITE;
/*!40000 ALTER TABLE `post_tag` DISABLE KEYS */;
INSERT INTO `post_tag` VALUES ('63f0962dda8d6ed8b927c8f7c0fbcc55','c73961a386567d3e0005313aaba528eb','sleep'),('7387fc98b205f2c363e01c1e689a6cd2','c73961a386567d3e0005313aaba528eb','anxiety'),('7526b9c3d50330490742f016b5b325ee','7b4f858e42856d4f3071aa2a0cbad663','sleep'),('87b2185c3030aa7e705fe5d763213194','9ac7540140fe1b92d7e21c8ff09515f1','anxiety'),('f4e49f1ab7f47d2dcf850bb59d4405ea','7b4f858e42856d4f3071aa2a0cbad663','relationship');
/*!40000 ALTER TABLE `post_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `private_message`
--

DROP TABLE IF EXISTS `private_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `private_message` (
  `id` varchar(36) NOT NULL COMMENT '消息ID',
  `from_user_id` varchar(36) NOT NULL COMMENT '发送用户ID',
  `to_user_id` varchar(36) NOT NULL COMMENT '接收用户ID',
  `content` text NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_from_user` (`from_user_id`),
  KEY `idx_to_user` (`to_user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_msg_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_msg_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='私信表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `private_message`
--

LOCK TABLES `private_message` WRITE;
/*!40000 ALTER TABLE `private_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `private_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `psychological_profile`
--

DROP TABLE IF EXISTS `psychological_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `psychological_profile` (
  `id` varchar(36) NOT NULL COMMENT '档案ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `emotional_stability` int DEFAULT NULL COMMENT '情绪稳定性评分',
  `anxiety_level` int DEFAULT NULL COMMENT '焦虑水平评分',
  `depression_level` int DEFAULT NULL COMMENT '抑郁水平评分',
  `stress_level` int DEFAULT NULL COMMENT '压力水平评分',
  `social_adaptability` int DEFAULT NULL COMMENT '社交适应性评分',
  `summary` text COMMENT '综合评估',
  `risk_factors` text COMMENT '风险因素',
  `strengths` text COMMENT '心理优势',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `psychological_profile`
--

LOCK TABLES `psychological_profile` WRITE;
/*!40000 ALTER TABLE `psychological_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `psychological_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_option`
--

DROP TABLE IF EXISTS `question_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_option` (
  `id` varchar(36) NOT NULL COMMENT '选项ID',
  `question_id` varchar(36) NOT NULL COMMENT '问题ID',
  `content` varchar(255) NOT NULL COMMENT '选项内容',
  `score` int NOT NULL COMMENT '选项分值',
  `order_num` int NOT NULL COMMENT '选项顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question` (`question_id`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `test_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问题选项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_option`
--

LOCK TABLES `question_option` WRITE;
/*!40000 ALTER TABLE `question_option` DISABLE KEYS */;
INSERT INTO `question_option` VALUES ('07a1c0eff8d14e6e991f780ed326ea15','fee0d74a9c6d41fdb9b842dcc95ee9bb','中度',2,3,'2025-03-29 21:21:19','2025-03-29 21:21:19'),('1778c6cddd4441d688bc0005d5f6a4e4','be1f56842f144fe1bb7b7b600493b32a','1',1,1,'2025-03-14 14:17:59','2025-03-14 14:17:59'),('23f5d08a1c9b4d37aa5930be67ef517f','fe3472d3231d41c2b04010202ce1b000','轻度',1,2,'2025-03-29 21:21:13','2025-03-29 21:21:13'),('240e9166a74f4931a7d5e4c027702374','fee0d74a9c6d41fdb9b842dcc95ee9bb','严重',4,5,'2025-03-29 21:21:19','2025-03-29 21:21:19'),('2a2889c784614e8d9732849b6379322d','c2882a66032d409b8f547a6f62676975','2',2,2,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('335ed99ddc7e4c5ea3557a15b0c02a00','fa09042028544e83a1cc4c4f389a8196','4',1,4,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('40ae7be4d9744937a67283caba72b0b6','c2882a66032d409b8f547a6f62676975','1',1,3,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('425a25c9ed4e4ddc856c9ea6b85ef0e7','fee0d74a9c6d41fdb9b842dcc95ee9bb','无',0,1,'2025-03-29 21:21:19','2025-03-29 21:21:19'),('4c894b551bad47ac93e1de11f81c8020','c2882a66032d409b8f547a6f62676975','-1',0,5,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('55b786a0c8394f809a29f3c4e05ffceb','fe3472d3231d41c2b04010202ce1b000','中度',2,3,'2025-03-29 21:21:13','2025-03-29 21:21:13'),('5ad08766a93b4b6fa6438e74d4120232','841d820559a540bc866e5b70eeaf597b','偏重',3,4,'2025-03-29 21:21:10','2025-03-29 21:21:10'),('61363073f2e34a54b0a40cba8cb8b94d','324bd94265ff40d3a3591b97de91bc4d','中度',2,3,'2025-03-29 21:21:16','2025-03-29 21:21:16'),('688d3e6fa30e42dc81a89410153ec61f','fa09042028544e83a1cc4c4f389a8196','3',1,3,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('73a4ec9d4cdc41548ea78cd94e595a34','fee0d74a9c6d41fdb9b842dcc95ee9bb','轻度',1,2,'2025-03-29 21:21:19','2025-03-29 21:21:19'),('8a54ea55771d4c5d992626cf1cf845c2','324bd94265ff40d3a3591b97de91bc4d','严重',4,5,'2025-03-29 21:21:16','2025-03-29 21:21:16'),('8b506d5a7ece4aed986b40412e6c42dd','841d820559a540bc866e5b70eeaf597b','严重',4,5,'2025-03-29 21:21:10','2025-03-29 21:21:10'),('9856622ce2ef4edea6e4d962a349a3fe','841d820559a540bc866e5b70eeaf597b','无',0,1,'2025-03-29 21:21:10','2025-03-29 21:21:10'),('a28b196484c2483483efd3f550705e25','fa09042028544e83a1cc4c4f389a8196','1',1,1,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('a7ba4c3387224fde9211076ce74369fa','fe3472d3231d41c2b04010202ce1b000','严重',4,5,'2025-03-29 21:21:13','2025-03-29 21:21:13'),('af939abe4f3045d0a6f3ff116028dec3','fe3472d3231d41c2b04010202ce1b000','偏重',3,4,'2025-03-29 21:21:13','2025-03-29 21:21:13'),('b7a6993243ba42aa8f8f5377cf0ce11b','c2882a66032d409b8f547a6f62676975','-3',0,7,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('bfe87f3b4c30457bb031899f670dcee6','fa09042028544e83a1cc4c4f389a8196','2',1,2,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('c7fd542dc2524899b2253b29eb8f09ca','324bd94265ff40d3a3591b97de91bc4d','无',0,1,'2025-03-29 21:21:16','2025-03-29 21:21:16'),('d100cfb645fb41beba0e056b765fe1e5','c2882a66032d409b8f547a6f62676975','0',0,4,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('dc3dff3b6fbc407c8da39b05af7aa216','324bd94265ff40d3a3591b97de91bc4d','偏重',3,4,'2025-03-29 21:21:16','2025-03-29 21:21:16'),('e091f29b8a504e86baf96b1366efe990','fe3472d3231d41c2b04010202ce1b000','无',0,1,'2025-03-29 21:21:13','2025-03-29 21:21:13'),('e1c446c8e67649ce8b871ecc13839456','c2882a66032d409b8f547a6f62676975','-2',1,6,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('e75bdc30c18741deabcbfaa9c36d732a','fee0d74a9c6d41fdb9b842dcc95ee9bb','偏重',3,4,'2025-03-29 21:21:19','2025-03-29 21:21:19'),('f175dd45bfd04851a87001136277d5a5','841d820559a540bc866e5b70eeaf597b','轻度',1,2,'2025-03-29 21:21:10','2025-03-29 21:21:10'),('f329acbad66a4dc79e788c22a8ccd2f6','c2882a66032d409b8f547a6f62676975','3',3,1,'2025-03-27 11:17:17','2025-03-27 11:17:17'),('f852bb5abdc7408bae69c8f7d91afaee','324bd94265ff40d3a3591b97de91bc4d','轻度',1,2,'2025-03-29 21:21:16','2025-03-29 21:21:16'),('f99f0734c8ab4277852fd4b454705a93','841d820559a540bc866e5b70eeaf597b','中度',2,3,'2025-03-29 21:21:10','2025-03-29 21:21:10'),('sas_q2_o1','sas_q2','很少或从不',1,1,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o2','sas_q2','有时',2,2,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o3','sas_q2','大部分时间',3,3,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o4','sas_q2','绝大部分或全部时间',4,4,'2025-03-05 09:22:25','2025-03-05 09:22:25');
/*!40000 ALTER TABLE `question_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_notification`
--

DROP TABLE IF EXISTS `system_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notification` (
  `id` varchar(50) NOT NULL COMMENT '通知ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `type` varchar(20) NOT NULL COMMENT '通知类型',
  `data` text COMMENT '附加数据(JSON格式)',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_notification`
--

LOCK TABLES `system_notification` WRITE;
/*!40000 ALTER TABLE `system_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_option`
--

DROP TABLE IF EXISTS `template_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `template_option` (
  `id` varchar(36) NOT NULL COMMENT '选项ID',
  `template_id` varchar(36) NOT NULL COMMENT '模板ID',
  `content` varchar(255) NOT NULL COMMENT '选项内容',
  `score` int NOT NULL COMMENT '选项分值',
  `order_num` int NOT NULL COMMENT '选项顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_template` (`template_id`),
  CONSTRAINT `fk_option_template` FOREIGN KEY (`template_id`) REFERENCES `option_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模板选项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template_option`
--

LOCK TABLES `template_option` WRITE;
/*!40000 ALTER TABLE `template_option` DISABLE KEYS */;
INSERT INTO `template_option` VALUES ('3275a03c592947bd8a2794311e3b1390','2b4a2c2bf6544cb79b060d93a5654052','中度',2,3,'2025-03-29 20:16:47','2025-03-29 20:16:47'),('4cf0ed5637864cc9a3e2dee5b0555aa4','2b4a2c2bf6544cb79b060d93a5654052','偏重',3,4,'2025-03-29 20:16:47','2025-03-29 20:16:47'),('6fbd6106983f4aa395b4895a7a2d2578','2b4a2c2bf6544cb79b060d93a5654052','轻度',1,2,'2025-03-29 20:16:47','2025-03-29 20:16:47'),('941b64a2c8a440388815e8dd15fdfbfa','2b4a2c2bf6544cb79b060d93a5654052','无',0,1,'2025-03-29 20:16:47','2025-03-29 20:16:47'),('ea8f1f294ef94750bd7efa400abe846d','2b4a2c2bf6544cb79b060d93a5654052','严重',4,5,'2025-03-29 20:16:47','2025-03-29 20:16:47');
/*!40000 ALTER TABLE `template_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_category`
--

DROP TABLE IF EXISTS `test_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_category` (
  `code` varchar(50) NOT NULL COMMENT '分类代码（主键）',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `color` varchar(20) DEFAULT NULL COMMENT '分类颜色（可用于前端显示）',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_category`
--

LOCK TABLES `test_category` WRITE;
/*!40000 ALTER TABLE `test_category` DISABLE KEYS */;
INSERT INTO `test_category` VALUES ('common','通用测试','通用心理学测试和评估','common-icon','#9CA3AF',1,'2025-04-01 21:42:17','2025-04-01 22:44:09',1),('emotion','情绪压力','评估情绪状态和压力水平的测试','emotion-icon','#F87171',6,'2025-04-01 21:42:17','2025-04-01 22:44:21',1),('growth','个人成长','促进个人成长和自我认知的测试','growth-icon','#FBBF24',4,'2025-04-01 21:42:17','2025-04-01 21:42:17',1),('happy','TEST','123',NULL,'#409EFF',7,'2025-04-01 22:21:59','2025-04-01 22:44:25',1),('mental','心理问题','识别潜在心理健康问题的测试','mental-icon','#60A5FA',2,'2025-04-01 21:42:17','2025-04-01 21:42:17',1),('personality','人格特质','分析性格和人格特质的测试','personality-icon','#A78BFA',5,'2025-04-01 21:42:17','2025-04-01 21:42:17',1),('relationship','人际关系','评估人际关系和社交能力的测试','relationship-icon','#34D399',3,'2025-04-01 21:42:17','2025-04-01 21:42:17',1);
/*!40000 ALTER TABLE `test_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_question`
--

DROP TABLE IF EXISTS `test_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_question` (
  `id` varchar(36) NOT NULL COMMENT '问题ID',
  `test_type_id` varchar(36) NOT NULL COMMENT '所属测试类型ID',
  `content` text NOT NULL COMMENT '问题内容',
  `order_num` int NOT NULL COMMENT '问题顺序',
  `option_type` tinyint(1) NOT NULL COMMENT '选项类型：1-单选，2-多选，3-量表',
  `option_template_id` varchar(36) DEFAULT NULL COMMENT '选项模板ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `image_url` varchar(255) DEFAULT NULL COMMENT '题目图片URL',
  PRIMARY KEY (`id`),
  KEY `idx_test_type` (`test_type_id`),
  KEY `idx_option_template` (`option_template_id`),
  CONSTRAINT `fk_question_template` FOREIGN KEY (`option_template_id`) REFERENCES `option_template` (`id`),
  CONSTRAINT `fk_question_test_type` FOREIGN KEY (`test_type_id`) REFERENCES `test_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_question`
--

LOCK TABLES `test_question` WRITE;
/*!40000 ALTER TABLE `test_question` DISABLE KEYS */;
INSERT INTO `test_question` VALUES ('324bd94265ff40d3a3591b97de91bc4d','7b04a975b134468d811910de337dfd6e','1234',3,1,'2b4a2c2bf6544cb79b060d93a5654052','2025-03-29 21:21:16','2025-03-30 17:58:52',NULL),('841d820559a540bc866e5b70eeaf597b','7b04a975b134468d811910de337dfd6e','123',1,1,'2b4a2c2bf6544cb79b060d93a5654052','2025-03-29 21:21:10','2025-03-29 21:21:10',NULL),('be1f56842f144fe1bb7b7b600493b32a','sas','123??',3,1,NULL,'2025-03-14 14:17:59','2025-03-14 14:17:59',NULL),('c2882a66032d409b8f547a6f62676975','mbti','你经常交新朋友。',1,1,NULL,'2025-03-27 11:17:17','2025-03-27 11:17:17',NULL),('fa09042028544e83a1cc4c4f389a8196','sas','你是否经常疲惫',2,2,NULL,'2025-03-13 03:36:40','2025-03-13 03:36:40',NULL),('fe3472d3231d41c2b04010202ce1b000','7b04a975b134468d811910de337dfd6e','1233',2,1,'2b4a2c2bf6544cb79b060d93a5654052','2025-03-29 21:21:13','2025-03-29 21:21:13',NULL),('fee0d74a9c6d41fdb9b842dcc95ee9bb','7b04a975b134468d811910de337dfd6e','1255',4,1,'2b4a2c2bf6544cb79b060d93a5654052','2025-03-29 21:21:19','2025-03-29 21:21:19',NULL),('sas_q2','sas','我无缘无故地感到害怕',1,1,NULL,'2025-03-05 09:22:18','2025-03-07 20:00:40',NULL);
/*!40000 ALTER TABLE `test_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_result`
--

DROP TABLE IF EXISTS `test_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_result` (
  `id` varchar(36) NOT NULL COMMENT '结果ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `test_type_id` varchar(36) NOT NULL COMMENT '测试类型ID',
  `total_score` int NOT NULL COMMENT '总分',
  `result_level` varchar(50) NOT NULL COMMENT '结果级别：轻度/中度/重度等',
  `result_description` text COMMENT '结果描述',
  `suggestions` text COMMENT '建议',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_test` (`user_id`,`test_type_id`),
  KEY `idx_test_type` (`test_type_id`),
  CONSTRAINT `fk_result_test_type` FOREIGN KEY (`test_type_id`) REFERENCES `test_type` (`id`),
  CONSTRAINT `fk_result_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_result`
--

LOCK TABLES `test_result` WRITE;
/*!40000 ALTER TABLE `test_result` DISABLE KEYS */;
INSERT INTO `test_result` VALUES ('0664fc53-6e17-4a5d-b517-5014e99d9ee0','a2fee174e375fd82559200ffb13785f3','sas',4,'低','您的焦虑自评量表 (SAS)测试得分为4分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-19 11:38:08','2025-03-19 11:48:08','2025-03-19 11:48:08'),('0beeff4e-dbef-4d06-95eb-85b2798f8f92','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:22:33','2025-03-07 20:32:33','2025-03-07 20:32:33'),('0cded7e7-74ea-4727-881f-7bad4f8c99fe','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:22:48','2025-03-07 20:32:48','2025-03-07 20:32:48'),('10dd6456-f1a1-46a0-9bc2-00c528803b64','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:03','2025-03-07 22:03:03','2025-03-07 22:03:03'),('12c983f4-570a-46ba-9bfc-01b1744887a6','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:29','2025-03-07 22:09:29','2025-03-07 22:09:29'),('1d9ca148-13be-4174-8bca-83720703bfac','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:21','2025-03-07 21:31:21','2025-03-07 21:31:21'),('23e6c74d-5a2c-44ff-b22e-928b007d1313','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-15 21:22:47','2025-03-15 21:32:47','2025-03-15 21:32:47'),('24dd54d8-6e01-482e-9b38-3f6c78ab7921','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:25','2025-03-07 21:31:25','2025-03-07 21:31:25'),('290576fa-1c65-4fb7-8d83-035b4932218e','test_user_001','sas',1,'低','您的焦虑自评量表 (SAS)测试得分为1分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:12:56','2025-03-07 22:22:56','2025-03-07 22:22:56'),('2afc0876-ae85-4e9b-be99-f8aa44759167','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:44:14','2025-03-07 19:54:14','2025-03-07 19:54:14'),('307ce13f-1dbc-43e6-a7b1-6b240c597337','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:50:54','2025-03-07 20:00:54','2025-03-07 20:00:53'),('30b0e368-42c0-4da9-8037-b86597902ae1','test_user_001','sas',1,'低','您的焦虑自评量表 (SAS)测试得分为1分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:31','2025-03-07 22:11:31','2025-03-07 22:11:31'),('39af6087-1c68-4b4a-893b-dce8d154668f','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:13','2025-03-07 22:03:13','2025-03-07 22:03:13'),('3a7f988c-b96e-4108-8167-e196f9dc1e84','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:47:15','2025-03-07 20:57:15','2025-03-07 20:57:15'),('3bd05878-e60f-4f9e-baee-1f7f11df3675','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:33','2025-03-07 22:09:33','2025-03-07 22:09:33'),('4451f9ef-397e-491e-871f-c34ce177096a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','7b04a975b134468d811910de337dfd6e',16,'低','您的SCL-90测试得分为16分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-29 21:17:22','2025-03-29 21:27:22','2025-03-29 21:27:22'),('4a2e38bd-9f27-4b72-9efd-60bbc9db830a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:52:11','2025-03-07 20:02:11','2025-03-07 20:02:10'),('4d782567-1101-4bf5-bf97-559d639ed8fb','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:11','2025-03-07 21:31:11','2025-03-07 21:31:11'),('577d2618-e043-4900-9a7f-d62180633f81','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:32','2025-03-07 22:09:32','2025-03-07 22:09:32'),('5e03798f-08ce-4cf9-acd5-e72d033d676a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-06 03:59:13','2025-03-06 04:09:13','2025-03-06 04:09:12'),('5ed47f42-d07b-4a05-813f-1904ca8f83ed','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:02','2025-03-07 21:31:02','2025-03-07 21:31:02'),('5ed8552c-257f-4b60-bcaa-cf70a2d7fce9','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','7b04a975b134468d811910de337dfd6e',7,'低','您的SCL-90测试得分为7分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-29 21:12:33','2025-03-29 21:22:33','2025-03-29 21:22:33'),('61eec169-e789-4f1a-8872-ab9790708067','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:18:59','2025-03-07 21:28:59','2025-03-07 21:28:59'),('649fceff-a45d-4e01-a138-71276c608be9','test_user_001','sas',2,'低','您的焦虑自评量表 (SAS)测试得分为2分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:33','2025-03-07 22:11:33','2025-03-07 22:11:33'),('708c3e8f-d76d-41ff-9747-7ab9046511c7','test_user_001','sas',5,'低','您的焦虑自评量表 (SAS)测试得分为5分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:46:51','2025-03-13 12:56:51','2025-03-13 12:56:51'),('76585718-0732-47cb-9317-6e58309b6d1e','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','7b04a975b134468d811910de337dfd6e',13,'低','您的SCL-90测试得分为13分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-31 03:23:19','2025-03-31 03:33:19','2025-03-31 03:33:19'),('7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:46:38','2025-03-13 12:56:38','2025-03-13 12:56:38'),('7d4d4dee-2f3c-46dd-b49f-9b917fe277a9','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:36','2025-03-07 22:11:36','2025-03-07 22:11:36'),('7dc17f89-b31f-4d6a-b21a-a80a44e0aa9b','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:47:51','2025-03-07 20:57:51','2025-03-07 20:57:51'),('809df4da-3d91-4a31-8db2-4d079363829d','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:19:19','2025-03-07 21:29:19','2025-03-07 21:29:19'),('80ebcf8e-1c21-4b7a-b499-bb7193966a9a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:05:38','2025-03-07 20:15:38','2025-03-07 20:15:37'),('81452976-9385-4237-8379-a67860e6ef58','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','sas',5,'低','您的焦虑自评量表 (SAS)测试得分为5分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-25 12:11:06','2025-03-25 12:21:06','2025-03-25 12:21:06'),('81488744-af4b-4c87-8401-67d8e884a942','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:07','2025-03-07 22:03:07','2025-03-07 22:03:07'),('81b2d4df-e2ad-4ab3-ba01-2cac89859ba7','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:21:22','2025-03-07 20:31:22','2025-03-07 20:31:22'),('89bdff55-1d46-490c-8220-8c5e00b6ecd4','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-11 18:53:26','2025-03-11 19:03:26','2025-03-11 19:03:26'),('978cdf41-3259-4cf0-8e58-9c877176b646','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:13:53','2025-03-07 20:23:53','2025-03-07 20:23:53'),('a25233f7-5a3e-4df2-80d4-44750bf3fa85','a2fee174e375fd82559200ffb13785f3','sas',4,'低','您的焦虑自评量表 (SAS)测试得分为4分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-23 14:44:26','2025-03-23 14:54:26','2025-03-23 14:54:26'),('a346c5f3-0d9e-4779-8753-7d71ae220391','test_user_001','sas',4,'低','您的焦虑自评量表 (SAS)测试得分为4分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:37','2025-03-07 22:11:37','2025-03-07 22:11:37'),('a83e3889-8b69-4889-8551-fd158013f81d','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-14 09:23:10','2025-03-14 09:33:10','2025-03-14 09:33:10'),('af2ef208-944e-4b11-884b-6e0f1599e96c','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:06:01','2025-03-07 20:16:01','2025-03-07 20:16:00'),('b1f8bc23-24cc-46f6-868c-047713554e8a','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-15 16:42:23','2025-03-15 16:52:23','2025-03-15 16:52:23'),('b5da23e7-b76d-44b4-bc34-1597f66f7bf1','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:27:04','2025-03-07 21:37:04','2025-03-07 21:37:04'),('bab32992-a72a-413a-a93b-01336261dc7b','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:48:39','2025-03-07 21:58:39','2025-03-07 21:58:39'),('bd2f210a-08bb-4e47-a739-5cd1cabb8b1d','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:44:27','2025-03-07 20:54:27','2025-03-07 20:54:27'),('beeb17b5-6541-48f6-ae63-3025ce5d46f9','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:45:58','2025-03-13 12:55:58','2025-03-13 12:55:58'),('c1094cfd-3468-4399-a971-10e266ad91e5','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:45:50','2025-03-07 20:55:50','2025-03-07 20:55:50'),('c97c21b1-a01a-40f0-855d-a5d4353b31fe','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:48:20','2025-03-07 19:58:20','2025-03-07 19:58:19'),('cd1da3e5-3977-43ed-8ac8-442b7944c0db','a2fee174e375fd82559200ffb13785f3','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-28 14:45:26','2025-03-28 14:55:26','2025-03-28 14:55:26'),('d0470803-2d41-4518-819c-9489c4afcdd2','a2fee174e375fd82559200ffb13785f3','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-23 14:47:25','2025-03-23 14:57:25','2025-03-23 14:57:25'),('da752961-fba3-4abf-8ba4-52e53a44235a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:13:15','2025-03-07 20:23:15','2025-03-07 20:23:14'),('dfc203b0-c159-4441-af35-954588fada58','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','7b04a975b134468d811910de337dfd6e',9,'轻度','666','666','2025-03-31 03:24:24','2025-03-31 03:34:24','2025-03-31 03:34:24'),('e0658f3b-2fd3-4dc0-aab6-ec4c484a35de','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:49:11','2025-03-07 19:59:11','2025-03-07 19:59:10'),('e14d550a-63b6-4e69-9c2e-4574d473db57','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','7b04a975b134468d811910de337dfd6e',9,'轻度','666','666','2025-04-01 22:12:51','2025-04-01 22:22:51','2025-04-01 22:22:51'),('f4bbb0ed-d4e0-4c2b-b9e3-8353f036dc84','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:27:11','2025-03-07 21:37:11','2025-03-07 21:37:11');
/*!40000 ALTER TABLE `test_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_score_level`
--

DROP TABLE IF EXISTS `test_score_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_score_level` (
  `id` varchar(36) NOT NULL COMMENT '级别ID',
  `test_type_id` varchar(36) NOT NULL COMMENT '测试类型ID',
  `level_name` varchar(50) NOT NULL COMMENT '级别名称',
  `min_score` int NOT NULL COMMENT '最小分数（包含）',
  `max_score` int NOT NULL COMMENT '最大分数（包含）',
  `description` text COMMENT '该级别的描述模板',
  `suggestions` text COMMENT '该级别的建议模板',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '排序编号',
  PRIMARY KEY (`id`),
  KEY `idx_test_type` (`test_type_id`),
  CONSTRAINT `fk_level_test_type` FOREIGN KEY (`test_type_id`) REFERENCES `test_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试分数等级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_score_level`
--

LOCK TABLES `test_score_level` WRITE;
/*!40000 ALTER TABLE `test_score_level` DISABLE KEYS */;
INSERT INTO `test_score_level` VALUES ('377e534f1bd842abab6ca54897742d3b','7b04a975b134468d811910de337dfd6e','轻度',0,20,'666','666',1),('50e7026e-0ca3-11f0-8792-005056c00001','phq9','正常',0,4,'您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您的情绪状态良好，没有明显的抑郁症状。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。',1),('50e71aff-0ca3-11f0-8792-005056c00001','phq9','轻度抑郁',5,9,'您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在一定程度的抑郁症状，如情绪低落、兴趣减退等。','1. 注意休息，保持规律的作息时间。\n2. 适当进行体育锻炼，如散步、慢跑等。\n3. 学习简单的放松技巧，如深呼吸、冥想等。\n4. 与亲友交流，分享自己的感受。',2),('50e71f46-0ca3-11f0-8792-005056c00001','phq9','中度抑郁',10,14,'您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在较明显的抑郁症状，如情绪低落、睡眠问题、精力不足等。','1. 建议寻求专业心理咨询师的帮助。\n2. 培养积极的应对方式，如运动、社交活动等。\n3. 与亲友保持联系，获取社会支持。\n4. 规律作息，健康饮食，适当运动。',3),('50e72167-0ca3-11f0-8792-005056c00001','phq9','中重度抑郁',15,19,'您的{testName}测试得分为{score}分，结果显示您目前处于{level}状态。您可能存在明显的抑郁症状，影响了日常生活和工作。','1. 强烈建议咨询专业心理医生或精神科医生。\n2. 可能需要心理治疗或药物治疗，请遵循医生建议。\n3. 保持与家人朋友的联系，获取社会支持。\n4. 避免重大决策，关注自身安全。',4);
/*!40000 ALTER TABLE `test_score_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_type`
--

DROP TABLE IF EXISTS `test_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_type` (
  `id` varchar(36) NOT NULL COMMENT '测试类型ID',
  `name` varchar(100) NOT NULL COMMENT '测试名称',
  `description` text COMMENT '测试描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `image_url` varchar(255) DEFAULT NULL COMMENT '测试图片URL',
  `category` varchar(50) NOT NULL COMMENT '分类代码（关联test_category表）',
  `time_minutes` int DEFAULT NULL COMMENT '预计完成时间(分钟)',
  `question_count` int NOT NULL DEFAULT '0' COMMENT '题目数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `default_template_id` varchar(36) DEFAULT NULL COMMENT '默认选项模板ID',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  CONSTRAINT `fk_test_type_category` FOREIGN KEY (`category`) REFERENCES `test_category` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测试类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_type`
--

LOCK TABLES `test_type` WRITE;
/*!40000 ALTER TABLE `test_type` DISABLE KEYS */;
INSERT INTO `test_type` VALUES ('7b04a975b134468d811910de337dfd6e','SCL-90','1234',NULL,NULL,'emotion',NULL,4,'2025-03-29 20:13:14','2025-04-01 21:55:53',1,NULL),('mbti','MBTI人格类型测试','发现你的人格类型','icon-puzzle-piece',NULL,'personality',15,70,'2025-03-05 09:21:44','2025-03-05 09:21:44',1,NULL),('phq9','患者健康问卷 (PHQ-9)','抑郁症筛查工具','icon-clipboard-check',NULL,'mental',3,9,'2025-03-05 09:21:44','2025-04-01 22:23:48',1,NULL),('pss','压力感知量表 (PSS)','评估压力水平的专业量表1','icon-bolt','','emotion',6,10,'2025-03-05 09:21:44','2025-03-20 08:58:13',1,NULL),('sas','焦虑自评量表 (SAS)','评估焦虑程度的专业量表','icon-brain','','emotion',5,20,'2025-03-05 09:21:44','2025-03-20 08:58:13',1,NULL),('sds','抑郁自评量表 (SDS)','评估抑郁程度的专业量表','icon-cloud-rain',NULL,'emotion',7,20,'2025-03-05 09:21:44','2025-03-05 09:21:44',1,NULL);
/*!40000 ALTER TABLE `test_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `follow_count` int NOT NULL DEFAULT '0' COMMENT '关注人数',
  `fans_count` int NOT NULL DEFAULT '0' COMMENT '粉丝人数',
  `letter_unread` int NOT NULL DEFAULT '0' COMMENT '私信未读',
  `comment_unread` int NOT NULL DEFAULT '0' COMMENT '评论未读',
  `at_unread` int NOT NULL DEFAULT '0' COMMENT '@未读',
  `notification_unread` int NOT NULL DEFAULT '0' COMMENT '通知未读',
  `description` varchar(255) DEFAULT '' COMMENT '个人简介',
  `post_count` int NOT NULL DEFAULT '0' COMMENT '发帖数',
  `like_unread` int NOT NULL DEFAULT '0' COMMENT '点赞未读',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_phone` (`phone`),
  UNIQUE KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('2e8e32a9-1609-4036-8ee6-c1cc252a91f7','testuser','$2a$10$7syCs2YvVuqV1eYS.6HjPegmVufNUpmt2USC2AlCr3AurFTbce4Ty','姬瑞佳','http://su5mwlhe1.hb-bkt.clouddn.com/community/5a2b87ffa3c3415b946259d1e7f57de3.jpeg',1,NULL,NULL,'test@example.com','2025-03-17 17:39:02','2025-04-07 08:54:01','2025-04-09 23:49:59',1,1,1,0,0,0,0,'',4,0),('a2fee174e375fd82559200ffb13785f3','xg666','$2a$10$9w0swBkDI8A.ETWNqY4mSOFoXXspFtBawuD1peiO4lO50j2d5EYSK','jrj','http://su5mwlhe1.hb-bkt.clouddn.com/community/fea40d771a034e1f9188b27878f94d88.jpg?e=1743710221&token=94BvdJExQ963UsZRcc2rMNoIfoZb1-1CfCrsjoEg:IWugVnwfcgcODGZ4k7Fi1Pp1iB4=',1,'2025-02-17',NULL,'2902756263@qq.com','2025-03-17 17:52:05','2025-04-04 02:57:58','2025-04-06 19:19:57',1,1,1,0,0,0,1,'',7,0),('guest123','1','123','123',NULL,NULL,NULL,NULL,NULL,'2025-03-07 20:31:17','2025-03-07 20:31:17',NULL,1,0,0,0,0,0,0,'',0,0),('test_user_001','jxg666','$2a$10$9w0swBkDI8A.ETWNqY4mSOFoXXspFtBawuD1peiO4lO50j2d5EYSK','jxg',NULL,NULL,NULL,NULL,NULL,'2025-03-06 03:56:59','2025-03-23 15:11:37','2025-03-23 15:11:43',1,0,0,0,0,0,0,'',0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_answer`
--

DROP TABLE IF EXISTS `user_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_answer` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `test_result_id` varchar(36) NOT NULL COMMENT '测试结果ID',
  `question_id` varchar(36) NOT NULL COMMENT '问题ID',
  `option_id` varchar(36) NOT NULL COMMENT '选项ID',
  `score` int NOT NULL COMMENT '得分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_test_result` (`test_result_id`),
  KEY `idx_question` (`question_id`),
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `test_question` (`id`),
  CONSTRAINT `fk_answer_result` FOREIGN KEY (`test_result_id`) REFERENCES `test_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户答题记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_answer`
--

LOCK TABLES `user_answer` WRITE;
/*!40000 ALTER TABLE `user_answer` DISABLE KEYS */;
INSERT INTO `user_answer` VALUES ('05f2abce-122d-4a28-a527-d2e85f97692e','4451f9ef-397e-491e-871f-c34ce177096a','fee0d74a9c6d41fdb9b842dcc95ee9bb','240e9166a74f4931a7d5e4c027702374',4,'2025-03-29 21:27:22'),('0dff23c6-c6b9-4e3e-b04c-50bc2d705e47','beeb17b5-6541-48f6-ae63-3025ce5d46f9','sas_q2','sas_q2_o2',2,'2025-03-13 12:55:57'),('11227d9b-bef1-4b51-ac99-9d7e977f7555','dfc203b0-c159-4441-af35-954588fada58','324bd94265ff40d3a3591b97de91bc4d','61363073f2e34a54b0a40cba8cb8b94d',2,'2025-03-31 03:34:24'),('16913640-b309-4ba3-aebc-5892f48aacd0','7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','sas_q2','sas_q2_o2',2,'2025-03-13 12:56:37'),('169dc818-60bd-4ee7-accf-95b567d9b1a4','0664fc53-6e17-4a5d-b517-5014e99d9ee0','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-19 11:48:07'),('1aa1d634-fb2a-4fab-843d-8eadbd3f653d','89bdff55-1d46-490c-8220-8c5e00b6ecd4','sas_q2','sas_q2_o3',3,'2025-03-11 19:03:26'),('2ae5aee7-a80b-4b85-8d9a-4343cf7ed065','649fceff-a45d-4e01-a138-71276c608be9','sas_q2','sas_q2_o2',2,'2025-03-07 22:11:33'),('3087cb54-1c9f-461b-b0ad-943fd44b20e1','81452976-9385-4237-8379-a67860e6ef58','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-25 12:21:06'),('3e6f2928-ff64-4a90-9459-b6922e9b8a09','0664fc53-6e17-4a5d-b517-5014e99d9ee0','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-19 11:48:07'),('45e3aac4-f0e6-4f25-8505-1eab961e78d0','d0470803-2d41-4518-819c-9489c4afcdd2','sas_q2','sas_q2_o1',1,'2025-03-23 14:57:25'),('465c07fd-2376-469d-b427-8124e8a2811d','a83e3889-8b69-4889-8551-fd158013f81d','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-14 09:33:09'),('47bd9110-d374-474f-b58c-346a6db754f5','76585718-0732-47cb-9317-6e58309b6d1e','fee0d74a9c6d41fdb9b842dcc95ee9bb','e75bdc30c18741deabcbfaa9c36d732a',3,'2025-03-31 03:33:19'),('49848703-200d-498e-bc73-a76dad8a817a','a25233f7-5a3e-4df2-80d4-44750bf3fa85','sas_q2','sas_q2_o2',2,'2025-03-23 14:54:25'),('515b8038-e3b4-402f-9b83-ed27cddd4f09','30b0e368-42c0-4da9-8037-b86597902ae1','sas_q2','sas_q2_o1',1,'2025-03-07 22:11:31'),('5376f578-3701-4f5e-a060-37fc0e100322','e14d550a-63b6-4e69-9c2e-4574d473db57','841d820559a540bc866e5b70eeaf597b','f175dd45bfd04851a87001136277d5a5',1,'2025-04-01 22:22:51'),('59cc7ded-8077-4079-8c34-344e2e4801ca','708c3e8f-d76d-41ff-9747-7ab9046511c7','sas_q2','sas_q2_o4',4,'2025-03-13 12:56:51'),('59e2bca0-bd5d-43e8-8734-a040802db5eb','cd1da3e5-3977-43ed-8ac8-442b7944c0db','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-28 14:55:25'),('6306a5f3-498a-429c-9838-c44d12836169','b1f8bc23-24cc-46f6-868c-047713554e8a','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-15 16:52:23'),('6454f41e-11ec-402f-8707-f8e2451949ec','23e6c74d-5a2c-44ff-b22e-928b007d1313','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-15 21:32:46'),('65880fc3-4d7c-4bcb-a394-6384c92ebe83','a83e3889-8b69-4889-8551-fd158013f81d','sas_q2','sas_q2_o2',2,'2025-03-14 09:33:09'),('662cfd8e-a148-49cf-a98b-fa6f1188d11c','5ed8552c-257f-4b60-bcaa-cf70a2d7fce9','fe3472d3231d41c2b04010202ce1b000','55b786a0c8394f809a29f3c4e05ffceb',2,'2025-03-29 21:22:33'),('6696febe-9cbf-4fe8-9afa-876990d612b5','708c3e8f-d76d-41ff-9747-7ab9046511c7','fa09042028544e83a1cc4c4f389a8196','335ed99ddc7e4c5ea3557a15b0c02a00',1,'2025-03-13 12:56:51'),('6e5fe1fc-4269-471d-8184-37318ea45706','4451f9ef-397e-491e-871f-c34ce177096a','fe3472d3231d41c2b04010202ce1b000','a7ba4c3387224fde9211076ce74369fa',4,'2025-03-29 21:27:22'),('741c78b7-9186-43b7-9a47-205008eac8b9','0664fc53-6e17-4a5d-b517-5014e99d9ee0','sas_q2','sas_q2_o2',2,'2025-03-19 11:48:07'),('7563b28a-d225-48c3-b2b0-038b563647aa','23e6c74d-5a2c-44ff-b22e-928b007d1313','sas_q2','sas_q2_o1',1,'2025-03-15 21:32:46'),('7628ebfa-aec6-4f0a-9945-7906829639c3','e14d550a-63b6-4e69-9c2e-4574d473db57','fee0d74a9c6d41fdb9b842dcc95ee9bb','e75bdc30c18741deabcbfaa9c36d732a',3,'2025-04-01 22:22:51'),('76cf14fe-7d01-4b51-a448-366a0e4667be','a25233f7-5a3e-4df2-80d4-44750bf3fa85','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-23 14:54:25'),('78e1f81f-c838-429f-b98f-5329e247f07f','4451f9ef-397e-491e-871f-c34ce177096a','324bd94265ff40d3a3591b97de91bc4d','8a54ea55771d4c5d992626cf1cf845c2',4,'2025-03-29 21:27:22'),('799d5ded-c6c6-4d1d-8b66-2251aa7dee3a','23e6c74d-5a2c-44ff-b22e-928b007d1313','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-15 21:32:46'),('7e5dd75a-797b-416d-9a13-4741685740cf','e14d550a-63b6-4e69-9c2e-4574d473db57','324bd94265ff40d3a3591b97de91bc4d','dc3dff3b6fbc407c8da39b05af7aa216',3,'2025-04-01 22:22:51'),('89d98164-71b4-4123-ac7d-06c0b186b903','a25233f7-5a3e-4df2-80d4-44750bf3fa85','fa09042028544e83a1cc4c4f389a8196','a28b196484c2483483efd3f550705e25',1,'2025-03-23 14:54:25'),('8f9b2f31-2a4c-43af-9351-c69002610b68','76585718-0732-47cb-9317-6e58309b6d1e','324bd94265ff40d3a3591b97de91bc4d','61363073f2e34a54b0a40cba8cb8b94d',2,'2025-03-31 03:33:19'),('99deacfb-630a-49b9-803e-ed94f740edf1','7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-13 12:56:37'),('9b2a7152-a222-4668-aea7-18fe80560d8e','290576fa-1c65-4fb7-8d83-035b4932218e','sas_q2','sas_q2_o1',1,'2025-03-07 22:22:55'),('9dbfffe6-ea31-435c-81e0-442e5d580173','dfc203b0-c159-4441-af35-954588fada58','fe3472d3231d41c2b04010202ce1b000','55b786a0c8394f809a29f3c4e05ffceb',2,'2025-03-31 03:34:24'),('a80855f1-164e-4d7c-bde5-a6c82b8d5c21','5ed8552c-257f-4b60-bcaa-cf70a2d7fce9','324bd94265ff40d3a3591b97de91bc4d','f852bb5abdc7408bae69c8f7d91afaee',1,'2025-03-29 21:22:33'),('b2fddfbb-acd3-4e68-b4bd-883d5d6efcbe','5ed8552c-257f-4b60-bcaa-cf70a2d7fce9','841d820559a540bc866e5b70eeaf597b','f175dd45bfd04851a87001136277d5a5',1,'2025-03-29 21:22:33'),('b7b6835c-4a31-4c65-84f7-9bd5a62adeca','5ed8552c-257f-4b60-bcaa-cf70a2d7fce9','fee0d74a9c6d41fdb9b842dcc95ee9bb','e75bdc30c18741deabcbfaa9c36d732a',3,'2025-03-29 21:22:33'),('bbf60721-c8d7-4dd3-b37d-35a5dfd4b2f8','d0470803-2d41-4518-819c-9489c4afcdd2','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-23 14:57:25'),('c5b8f6a6-4b3f-4c39-88ed-e83b7b5e3c98','4451f9ef-397e-491e-871f-c34ce177096a','841d820559a540bc866e5b70eeaf597b','8b506d5a7ece4aed986b40412e6c42dd',4,'2025-03-29 21:27:22'),('c8be5dbb-113f-4146-91ee-8fc082bccb9b','7d4d4dee-2f3c-46dd-b49f-9b917fe277a9','sas_q2','sas_q2_o3',3,'2025-03-07 22:11:35'),('cb845b3a-b318-484f-b5eb-c3fdf41e5604','cd1da3e5-3977-43ed-8ac8-442b7944c0db','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-28 14:55:25'),('cc887d4e-1cba-4ebd-871f-9c1ea98ce99e','dfc203b0-c159-4441-af35-954588fada58','fee0d74a9c6d41fdb9b842dcc95ee9bb','e75bdc30c18741deabcbfaa9c36d732a',3,'2025-03-31 03:34:24'),('e000292d-cc74-487c-8d46-5fe338dcbda5','b1f8bc23-24cc-46f6-868c-047713554e8a','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-15 16:52:23'),('e130630f-3c01-4313-90cc-d340be3a94a2','dfc203b0-c159-4441-af35-954588fada58','841d820559a540bc866e5b70eeaf597b','f99f0734c8ab4277852fd4b454705a93',2,'2025-03-31 03:34:24'),('e36a3d86-3148-4ae1-9524-db5262acbab0','b1f8bc23-24cc-46f6-868c-047713554e8a','sas_q2','sas_q2_o1',1,'2025-03-15 16:52:22'),('e6ad84b3-04b9-4c9c-9ba1-a579450650eb','76585718-0732-47cb-9317-6e58309b6d1e','841d820559a540bc866e5b70eeaf597b','8b506d5a7ece4aed986b40412e6c42dd',4,'2025-03-31 03:33:19'),('e716ec00-3061-4231-9a6c-18d04d8bfd88','cd1da3e5-3977-43ed-8ac8-442b7944c0db','sas_q2','sas_q2_o1',1,'2025-03-28 14:55:25'),('e738518e-6b31-4a78-8ffa-bd6e5d8bce70','81452976-9385-4237-8379-a67860e6ef58','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-25 12:21:06'),('e9d33a75-da52-48b2-a498-eba927f800c7','76585718-0732-47cb-9317-6e58309b6d1e','fe3472d3231d41c2b04010202ce1b000','a7ba4c3387224fde9211076ce74369fa',4,'2025-03-31 03:33:19'),('eb3671d5-4a81-471b-be4d-4d087f640650','beeb17b5-6541-48f6-ae63-3025ce5d46f9','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-13 12:55:57'),('f0bd50bb-5ae1-436b-b84c-83551a519a7a','a346c5f3-0d9e-4779-8753-7d71ae220391','sas_q2','sas_q2_o4',4,'2025-03-07 22:11:37'),('f4d6aa55-95bc-4438-ab8a-ba303deda017','e14d550a-63b6-4e69-9c2e-4574d473db57','fe3472d3231d41c2b04010202ce1b000','55b786a0c8394f809a29f3c4e05ffceb',2,'2025-04-01 22:22:51'),('f6748197-eacd-49a0-b391-eda26a6b9b2c','d0470803-2d41-4518-819c-9489c4afcdd2','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-23 14:57:25'),('ffb1130a-54e9-4f6f-9a1f-8c19ffe71a07','81452976-9385-4237-8379-a67860e6ef58','sas_q2','sas_q2_o3',3,'2025-03-25 12:21:06');
/*!40000 ALTER TABLE `user_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_journal`
--

DROP TABLE IF EXISTS `user_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_journal` (
  `id` varchar(36) NOT NULL COMMENT '日记ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `title` varchar(100) DEFAULT NULL COMMENT '日记标题',
  `content` longtext COMMENT '日记内容(纯文本)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `keywords` varchar(255) DEFAULT NULL COMMENT '提取的关键词',
  `word_count` int DEFAULT '0' COMMENT '字数统计',
  `related_mood_id` varchar(36) DEFAULT NULL COMMENT '关联的心情记录ID',
  `is_private` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否私密',
  `has_images` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包含图片: 0-否, 1-是',
  `image_urls` text COMMENT '日记图片URL列表，JSON数组格式',
  `image_count` int DEFAULT '0' COMMENT '日记图片数量',
  `emotion_type` varchar(20) DEFAULT NULL COMMENT '情感类型：pessimistic/neutral/optimistic',
  `emotion_prob` decimal(5,4) DEFAULT NULL COMMENT '情感概率',
  `emotion_subtype` varchar(20) DEFAULT NULL COMMENT '情感子类型',
  `emotion_subtype_prob` decimal(5,4) DEFAULT NULL COMMENT '情感子类型概率',
  `emotion_analysis_time` datetime DEFAULT NULL COMMENT '情感分析时间',
  `emotion_analysis_result` text COMMENT '完整的情感分析结果(JSON)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_related_mood_id` (`related_mood_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户日记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_journal`
--

LOCK TABLES `user_journal` WRITE;
/*!40000 ALTER TABLE `user_journal` DISABLE KEYS */;
INSERT INTO `user_journal` VALUES ('0ce45fd74008bb54bcad9cbf11967c2f','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','1','12','2025-04-08 23:26:36','2025-04-08 23:26:36',0,'',2,'',0,0,'[]',0,'neutral',1.0000,NULL,NULL,'2025-04-08 23:26:36','{\"label\":\"neutral\",\"prob\":0.999999}'),('0d96bce33489ea204b4395a34c35fa67','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','1','哈哈哈哈哈哈','2025-04-08 23:16:40','2025-04-08 23:16:40',0,'',6,'',0,0,'[]',0,'optimistic',0.9995,'happy',0.9995,'2025-04-08 23:16:41','{\"label\":\"optimistic\",\"prob\":0.999469,\"subLabel\":\"happy\",\"subProb\":0.999469}'),('2aca37025b2d18306ca7365052242391','a2fee174e375fd82559200ffb13785f3','2','天朗气清，惠风和畅','2025-04-04 21:06:39','2025-04-04 21:06:39',0,'和畅,气,清,朗,惠风',9,'',0,0,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL),('3258bbda9d91563d8a2abfd4e2d3fa96','a2fee174e375fd82559200ffb13785f3','11','1','2025-04-04 21:08:06','2025-04-04 21:08:06',0,'',1,'',0,0,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL),('32cb595b4204674b3d1ebd8649b63858','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','hhh','今天心情不错哦哈哈哈哈','2025-04-08 22:57:13','2025-04-08 22:57:13',0,'今天,心情,不错',11,'',0,0,'[]',0,NULL,NULL,NULL,NULL,'2025-04-08 22:57:13','{}'),('36345f9498bcc4bffcba59be63fc682c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','1','1','2025-04-08 23:23:37','2025-04-08 23:23:37',0,'',1,'',0,0,'[]',0,'neutral',1.0000,NULL,NULL,'2025-04-08 23:23:38','{\"label\":\"neutral\",\"prob\":0.999966}'),('4dc5455f7c1e7c86f1ba9e7d01fad868','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','今天心情不错，风和日丽的','2025-04-08 23:18:35','2025-04-08 23:18:35',0,'今天,风和日丽,心情,不错',12,'',0,0,'[]',0,NULL,NULL,NULL,NULL,'2025-04-08 23:18:35','{}'),('59557ec5f4a7f77c8e299d231512e730','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','112','12','2025-04-04 21:38:46','2025-04-04 21:38:46',0,'',2,'',0,0,'[\"http://su5mwlhe1.hb-bkt.clouddn.com/journal/31095b9b619d4a16bf2c2f136ce60762.jpg\"]',1,NULL,NULL,NULL,NULL,NULL,NULL),('68382ce0ff82f35da73efc37b11d7f68','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','日记','哈哈哈哈','2025-04-08 23:25:44','2025-04-08 23:25:44',0,'',4,'',0,0,'[]',0,'optimistic',0.9992,'happy',0.9992,'2025-04-08 23:25:44','{\"label\":\"optimistic\",\"prob\":0.999245,\"subLabel\":\"happy\",\"subProb\":0.999245}'),('705ae3b1b365555769ee8d681a9a30bb','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','11','2','2025-04-04 22:36:43','2025-04-04 22:36:43',0,'',1,'',0,0,'[]',0,NULL,NULL,NULL,NULL,NULL,NULL),('7740e12c1321531fca32e0980efccab9','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','哈哈哈哈','2025-04-09 23:50:12','2025-04-09 23:50:12',0,'',4,'',0,0,'[]',0,'optimistic',0.9992,'happy',0.9992,'2025-04-09 23:50:13','{\"label\":\"optimistic\",\"prob\":0.999245,\"subLabel\":\"happy\",\"subProb\":0.999245}'),('8638f36c0baf78921b8e59dc5f526df9','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','hhh','今天心情不错哦哈哈哈哈','2025-04-08 22:57:13','2025-04-08 22:57:13',0,'今天,心情,不错',11,'',0,0,'[]',0,NULL,NULL,NULL,NULL,'2025-04-08 22:57:13','{}'),('8f8f806c6042d7d74e19bf976e0a5c98','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','今天心情不错','2025-04-08 23:26:11','2025-04-08 23:26:11',0,'今天,心情,不错',6,'',0,0,'[]',0,'optimistic',0.9983,'like',0.9983,'2025-04-08 23:26:11','{\"label\":\"optimistic\",\"prob\":0.998314,\"subLabel\":\"like\",\"subProb\":0.998314}'),('b7a0e7c153093ab7f197e3362c265e7a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','12','2025-04-06 19:25:48','2025-04-06 19:25:48',0,'',2,'',0,0,'[\"http://su5mwlhe1.hb-bkt.clouddn.com/journal/a4ff617af6dc459ea537622df0fc2e76.png\"]',1,NULL,NULL,NULL,NULL,NULL,NULL),('c2c878952128b91166709a6b667a3c41','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','12','今天心情不错，风和日丽的','2025-04-08 23:18:34','2025-04-08 23:18:34',0,'今天,风和日丽,心情,不错',12,'',0,0,'[]',0,'optimistic',0.9940,'like',0.9940,'2025-04-08 23:18:35','{\"label\":\"optimistic\",\"prob\":0.994005,\"subLabel\":\"like\",\"subProb\":0.994005}');
/*!40000 ALTER TABLE `user_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_media_favorite`
--

DROP TABLE IF EXISTS `user_media_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_media_favorite` (
  `id` varchar(36) NOT NULL COMMENT '收藏ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `media_id` varchar(36) NOT NULL COMMENT '媒体资源ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `media_id` (`media_id`),
  CONSTRAINT `user_media_favorite_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `media_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户媒体收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_media_favorite`
--

LOCK TABLES `user_media_favorite` WRITE;
/*!40000 ALTER TABLE `user_media_favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_media_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_media_history`
--

DROP TABLE IF EXISTS `user_media_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_media_history` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `media_id` varchar(36) NOT NULL COMMENT '媒体资源ID',
  `progress` int DEFAULT '0' COMMENT '播放进度（秒）',
  `is_completed` tinyint DEFAULT '0' COMMENT '是否播放完成：0=未完成，1=已完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `media_id` (`media_id`),
  CONSTRAINT `user_media_history_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `media_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户媒体历史记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_media_history`
--

LOCK TABLES `user_media_history` WRITE;
/*!40000 ALTER TABLE `user_media_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_media_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_report`
--

DROP TABLE IF EXISTS `user_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_report` (
  `id` varchar(36) NOT NULL COMMENT '报告ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `report_type` varchar(20) NOT NULL COMMENT '报告类型(monthly,weekly,yearly)',
  `period` varchar(50) DEFAULT NULL,
  `report_title` varchar(100) NOT NULL COMMENT '报告标题',
  `report_content` longtext NOT NULL COMMENT '报告内容(JSON格式)',
  `key_insights` text COMMENT '关键洞察点',
  `start_date` datetime NOT NULL COMMENT '开始日期',
  `end_date` datetime NOT NULL COMMENT '结束日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`),
  KEY `idx_user_period` (`user_id`,`period`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户报告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_report`
--

LOCK TABLES `user_report` WRITE;
/*!40000 ALTER TABLE `user_report` DISABLE KEYS */;
INSERT INTO `user_report` VALUES ('2b514589967a6cc55be9e4c5d5503be2','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','custom','20240409000000_20250409235959','自定义报告 (2024年4月9日 - 2025年4月9日)','{\"testData\":{\"totalTests\":6,\"testTypeDistribution\":[{\"test_type_id\":\"7b04a975b134468d811910de337dfd6e\",\"count\":5},{\"test_type_id\":\"sas\",\"count\":1}],\"scoreChanges\":{\"sas\":[{\"date\":\"2025-03-25\",\"total_score\":5}],\"7b04a975b134468d811910de337dfd6e\":[{\"date\":\"2025-03-29\",\"total_score\":7},{\"date\":\"2025-03-29\",\"total_score\":16},{\"date\":\"2025-03-31\",\"total_score\":13},{\"date\":\"2025-03-31\",\"total_score\":9},{\"date\":\"2025-04-01\",\"total_score\":9}]}},\"endDate\":\"2025-04-09T23:59:59\",\"generateTime\":\"2025-04-09T09:09:41.0170965\",\"userId\":\"2e8e32a9-1609-4036-8ee6-c1cc252a91f7\",\"journalData\":{\"totalJournals\":12,\"averageMoodScore\":0.0,\"averageWordCount\":5.8333,\"dailyStats\":[{\"date\":\"2025-04-04\",\"total_words\":3,\"count\":2},{\"date\":\"2025-04-06\",\"total_words\":2,\"count\":1},{\"date\":\"2025-04-08\",\"total_words\":65,\"count\":9}],\"moodTagStats\":[]},\"startDate\":\"2024-04-09T00:00:00\",\"moodData\":{\"averageMoodScore\":3.3333333333333335,\"dailyMoods\":[{\"date\":\"2025-03-25\",\"count\":2,\"avg_score\":4.0000},{\"date\":\"2025-03-31\",\"count\":2,\"avg_score\":3.0000},{\"date\":\"2025-04-09\",\"count\":2,\"avg_score\":3.0000}],\"emotionTypeDistribution\":[{\"emotion_type\":\"平静/满足\",\"count\":2},{\"emotion_type\":\"疲惫/无力\",\"count\":2},{\"emotion_type\":\"中性/平淡\",\"count\":1},{\"emotion_type\":\"焦虑/紧张\",\"count\":1}],\"commonTags\":[{\"count\":3,\"tags\":\"工作/学习\"},{\"count\":2,\"tags\":\"人际关系\"},{\"count\":2,\"tags\":\"天气变化\"},{\"count\":1,\"tags\":\"休息质量\"},{\"count\":1,\"tags\":\"健康状况\"}]}}','这份报告分析了你在 4月9日 到 4月9日 期间的使用情况。\n\n• 情绪概览：你的平均情绪评分为 3.3。情绪评分偏低，建议关注情绪波动。\n• 主要情绪：你最常体验的情绪是「平静/满足」，共记录了 2 次。\n• 日记记录：你共记录了 12 篇日记。平均篇幅约为 6 字。\n• 心理测试：你完成了 6 次心理测试。其中「类型 7b04a975b134468d811910de337dfd6e」测试做得最多，共 5 次。\n• 测试分数变化：\n\n💡 综合建议：\n1. 继续保持记录习惯，无论是情绪还是日记，都能帮助你更好地了解自己。\n2. 鉴于近期情绪评分偏低，尝试寻找积极的应对方式或寻求支持。\n3. 回顾你的日记，或许能发现一些模式或触发点。\n4. 根据测试结果，思考可以采取哪些行动来改善或维持心理健康。\n','2024-04-09 00:00:00','2025-04-09 23:59:59','2025-04-09 09:09:41',0),('89de6dc4e44c3469275c84e09e386621','a2fee174e375fd82559200ffb13785f3','custom','20250104000000_20250404235959','自定义报告 (2025年1月4日 - 2025年4月4日)','{\"keywordData\":[],\"testData\":{\"totalTests\":4,\"testTypeDistribution\":[{\"test_type_id\":\"sas\",\"count\":4}],\"scoreChanges\":{\"sas\":[{\"date\":\"2025-03-19\",\"total_score\":4},{\"date\":\"2025-03-23\",\"total_score\":4},{\"date\":\"2025-03-23\",\"total_score\":3},{\"date\":\"2025-03-28\",\"total_score\":3}]}},\"endDate\":\"2025-04-04T23:59:59\",\"generateTime\":\"2025-04-04T20:47:32.5420954\",\"userId\":\"a2fee174e375fd82559200ffb13785f3\",\"journalData\":{\"totalJournals\":1,\"averageMoodScore\":0.0,\"averageWordCount\":3.0,\"dailyStats\":[{\"date\":\"2025-04-04\",\"count\":1}],\"moodTagStats\":[]},\"startDate\":\"2025-01-04T00:00:00\",\"moodData\":{\"averageMoodScore\":3.0,\"dailyMoods\":[{\"date\":\"2025-03-23\",\"count\":1,\"avg_score\":3.0000},{\"date\":\"2025-03-25\",\"count\":1,\"avg_score\":3.0000},{\"date\":\"2025-03-28\",\"count\":1,\"avg_score\":3.0000}],\"emotionTypeDistribution\":[{\"emotion_type\":\"疲惫/无力\",\"count\":2},{\"emotion_type\":\"愤怒/烦躁\",\"count\":1}],\"commonTags\":[{\"count\":2,\"tags\":\"人际关系\"},{\"count\":1,\"tags\":\"个人成就\"},{\"count\":1,\"tags\":\"天气变化\"}]}}','这份报告分析了你在 1月4日 到 4月4日 期间的使用情况。\n\n• 情绪概览：你的平均情绪评分为 3.0。情绪评分偏低，建议关注情绪波动。\n• 主要情绪：你最常体验的情绪是「疲惫/无力」，共记录了 2 次。\n• 日记记录：你共记录了 1 篇日记。平均篇幅约为 3 字。\n• 心理测试：你完成了 4 次心理测试。其中「类型 sas」测试做得最多，共 4 次。\n• 测试分数变化：\n\n💡 综合建议：\n1. 继续保持记录习惯，无论是情绪还是日记，都能帮助你更好地了解自己。\n2. 鉴于近期情绪评分偏低，尝试寻找积极的应对方式或寻求支持。\n3. 回顾你的日记，或许能发现一些模式或触发点。\n4. 根据测试结果，思考可以采取哪些行动来改善或维持心理健康。\n','2025-01-04 00:00:00','2025-04-04 23:59:59','2025-04-04 20:47:33',0),('9315f3d72c9ddfea786a38dc86c8967a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','custom','20250104000000_20250404235959','自定义报告 (2025年1月4日 - 2025年4月4日)','{\"keywordData\":[],\"testData\":{\"totalTests\":6,\"testTypeDistribution\":[{\"test_type_id\":\"7b04a975b134468d811910de337dfd6e\",\"count\":5},{\"test_type_id\":\"sas\",\"count\":1}],\"scoreChanges\":{\"sas\":[{\"date\":\"2025-03-25\",\"total_score\":5}],\"7b04a975b134468d811910de337dfd6e\":[{\"date\":\"2025-03-29\",\"total_score\":7},{\"date\":\"2025-03-29\",\"total_score\":16},{\"date\":\"2025-03-31\",\"total_score\":13},{\"date\":\"2025-03-31\",\"total_score\":9},{\"date\":\"2025-04-01\",\"total_score\":9}]}},\"endDate\":\"2025-04-04T23:59:59\",\"generateTime\":\"2025-04-04T04:08:42.4759894\",\"userId\":\"2e8e32a9-1609-4036-8ee6-c1cc252a91f7\",\"journalData\":{\"totalJournals\":2,\"averageMoodScore\":0.0,\"averageWordCount\":1.5,\"dailyStats\":[{\"date\":\"2025-04-04\",\"count\":2}],\"moodTagStats\":[]},\"startDate\":\"2025-01-04T00:00:00\",\"moodData\":{\"averageMoodScore\":3.5,\"dailyMoods\":[{\"date\":\"2025-03-25\",\"count\":2,\"avg_score\":4.0000},{\"date\":\"2025-03-31\",\"count\":2,\"avg_score\":3.0000}],\"emotionTypeDistribution\":[{\"emotion_type\":\"平静/满足\",\"count\":1},{\"emotion_type\":\"中性/平淡\",\"count\":1},{\"emotion_type\":\"焦虑/紧张\",\"count\":1},{\"emotion_type\":\"疲惫/无力\",\"count\":1}],\"commonTags\":[{\"count\":3,\"tags\":\"工作/学习\"},{\"count\":2,\"tags\":\"人际关系\"},{\"count\":2,\"tags\":\"天气变化\"},{\"count\":1,\"tags\":\"休息质量\"},{\"count\":1,\"tags\":\"健康状况\"}]}}','这份报告分析了你在 1月4日 到 4月4日 期间的使用情况。\n\n• 情绪概览：你的平均情绪评分为 3.5。情绪评分偏低，建议关注情绪波动。\n• 主要情绪：你最常体验的情绪是「平静/满足」，共记录了 1 次。\n• 日记记录：你共记录了 2 篇日记。平均篇幅约为 2 字。\n• 心理测试：你完成了 6 次心理测试。其中「类型 7b04a975b134468d811910de337dfd6e」测试做得最多，共 5 次。\n• 测试分数变化：\n\n💡 综合建议：\n1. 继续保持记录习惯，无论是情绪还是日记，都能帮助你更好地了解自己。\n2. 鉴于近期情绪评分偏低，尝试寻找积极的应对方式或寻求支持。\n3. 回顾你的日记，或许能发现一些模式或触发点。\n4. 根据测试结果，思考可以采取哪些行动来改善或维持心理健康。\n','2025-01-04 00:00:00','2025-04-04 23:59:59','2025-04-04 04:08:43',0),('ec76c5037e1d0a60c18b3078f29e69d6','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','custom','20240228000000_20250331235959','自定义报告 (2024年2月28日 - 2025年3月31日)','{\"keywordData\":[],\"testData\":{\"totalTests\":5,\"testTypeDistribution\":[{\"test_type_id\":\"7b04a975b134468d811910de337dfd6e\",\"count\":4},{\"test_type_id\":\"sas\",\"count\":1}],\"scoreChanges\":{\"sas\":[{\"date\":\"2025-03-25\",\"total_score\":5}],\"7b04a975b134468d811910de337dfd6e\":[{\"date\":\"2025-03-29\",\"total_score\":7},{\"date\":\"2025-03-29\",\"total_score\":16},{\"date\":\"2025-03-31\",\"total_score\":13},{\"date\":\"2025-03-31\",\"total_score\":9}]}},\"endDate\":\"2025-03-31T23:59:59\",\"generateTime\":\"2025-03-31T23:01:24.2427229\",\"userId\":\"2e8e32a9-1609-4036-8ee6-c1cc252a91f7\",\"journalData\":{\"totalJournals\":0,\"averageMoodScore\":0.0,\"averageWordCount\":0.0,\"dailyStats\":[],\"moodTagStats\":[]},\"startDate\":\"2024-02-28T00:00:00\",\"moodData\":{\"averageMoodScore\":3.5,\"dailyMoods\":[{\"date\":\"2025-03-25\",\"count\":2,\"avg_score\":4.0000},{\"date\":\"2025-03-31\",\"count\":2,\"avg_score\":3.0000}],\"emotionTypeDistribution\":[{\"emotion_type\":\"平静/满足\",\"count\":1},{\"emotion_type\":\"中性/平淡\",\"count\":1},{\"emotion_type\":\"焦虑/紧张\",\"count\":1},{\"emotion_type\":\"疲惫/无力\",\"count\":1}],\"commonTags\":[{\"count\":3,\"tags\":\"工作/学习\"},{\"count\":2,\"tags\":\"人际关系\"},{\"count\":2,\"tags\":\"天气变化\"},{\"count\":1,\"tags\":\"休息质量\"},{\"count\":1,\"tags\":\"健康状况\"}]}}','这份报告分析了你在 2月28日 到 3月31日 期间的使用情况。\n\n• 情绪概览：你的平均情绪评分为 3.5。情绪评分偏低，建议关注情绪波动。\n• 主要情绪：你最常体验的情绪是「平静/满足」，共记录了 1 次。\n• 日记记录：这段时间你没有记录日记。尝试记录可以帮助整理思绪。\n• 心理测试：你完成了 5 次心理测试。其中「类型 7b04a975b134468d811910de337dfd6e」测试做得最多，共 4 次。\n• 测试分数变化：\n\n💡 综合建议：\n1. 继续保持记录习惯，无论是情绪还是日记，都能帮助你更好地了解自己。\n2. 鉴于近期情绪评分偏低，尝试寻找积极的应对方式或寻求支持。\n3. 如果还没开始写日记，不妨尝试一下，它可以成为你情绪的出口。\n4. 根据测试结果，思考可以采取哪些行动来改善或维持心理健康。\n','2024-02-28 00:00:00','2025-03-31 23:59:59','2025-03-31 23:01:24',0);
/*!40000 ALTER TABLE `user_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification_code`
--

DROP TABLE IF EXISTS `verification_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_code` (
  `id` varchar(36) NOT NULL COMMENT 'ID',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint(1) DEFAULT '0' COMMENT '是否已使用：0-未使用，1-已使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮箱验证码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_code`
--

LOCK TABLES `verification_code` WRITE;
/*!40000 ALTER TABLE `verification_code` DISABLE KEYS */;
INSERT INTO `verification_code` VALUES ('2c3eca64e733f6d34d3df5abc0097c9d','2902756263@qq.com','527403','2025-03-17 17:51:40','2025-03-17 17:56:40',1),('7b9a8cee0e9dd160e2a7dbd2771be011','1025828544@qq.com','875962','2025-03-07 17:35:57','2025-03-07 17:40:57',1),('b84672148d623e69616a912b5e83a0ee','2902756263@qq.com','916838','2025-03-07 17:20:44','2025-03-07 17:25:44',1),('cf51c7dffd374b4291e936bd18386ef0','2902756263@qq.com','498714','2025-03-29 19:14:25','2025-03-29 19:19:25',1);
/*!40000 ALTER TABLE `verification_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ai'
--

--
-- Dumping routines for database 'ai'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-10  0:09:44
