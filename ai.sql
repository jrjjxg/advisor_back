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
INSERT INTO `admin_user` VALUES ('864a2dc3c9e49b54219b73e314cac969','lsd','$2a$10$rcNPQzvWMsduR47u182ZjeyOHifS/J1KyceQzfd/W1taAkvsd9FB6','1025828544@qq.com',1,'2025-03-07 17:36:17','2025-03-07 17:36:27'),('87dbec7f5c65093aef76fd9f4488c097','jxg','$2a$10$nee9cbyP2hBzzFrjXNtdku3wiy4r6lrB/GiOjgRXJ57bC1cy9V3DC','2902756263@qq.com',1,'2025-03-07 17:21:14','2025-03-21 01:08:02');
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
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
INSERT INTO `comment` VALUES ('123a','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,1,0,1,'2025-03-17 20:12:11',NULL),('28e99492f289bc12856f19738e69e946','c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','博主666',NULL,NULL,0,0,1,'2025-03-17 21:45:44',NULL),('31c3369683586718490bb9dfce853571','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,0,0,1,'2025-03-19 11:33:26',NULL),('3320debf05876f8120b4d63805703c31','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','66666',NULL,NULL,0,0,1,'2025-03-17 21:27:49',NULL),('3d605333e38af81ea3c51fd9fd4584c3','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','这个入是桂','123a',NULL,0,0,1,'2025-03-17 21:28:00','a2fee174e375fd82559200ffb13785f3'),('4bc4633549c65cd7b0f909976611b0c4','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','test',NULL,NULL,0,0,1,'2025-03-19 00:04:40',NULL),('51cdd3062e13a7e742743867cdce50b1','9ac7540140fe1b92d7e21c8ff09515f1','a2fee174e375fd82559200ffb13785f3','大神666',NULL,NULL,0,0,1,'2025-03-19 00:19:18',NULL),('54abc5c0af6a49ce2fe42f42f6b956e1','2ffc953cd1ac790906866e340f20ed1b','a2fee174e375fd82559200ffb13785f3','666',NULL,NULL,0,0,1,'2025-03-19 11:32:54',NULL),('5c4adc483dd7320be37655ab8ca7aba3','05ea89b3404969d391b515a0a89b4e73','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','楼主所言极是！妙哉妙哉',NULL,NULL,0,0,1,'2025-03-19 00:44:07',NULL),('5c5f14f40778b245fc9cbcf6af19cda7','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','功能正常',NULL,NULL,0,0,1,'2025-03-19 02:08:45',NULL),('5d089973e86d66232fcf8f025affb9ee','7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','2112',NULL,NULL,1,0,1,'2025-03-17 21:25:07',NULL),('5e5939f44c3f1d9ecb028bb182be0c8a','734dbe2b34228a5f4b4e0bef6e013123','a2fee174e375fd82559200ffb13785f3','66666',NULL,NULL,0,0,1,'2025-03-19 13:28:27',NULL),('8c56cfa8ea424039a07817eda076dfa9','40a614b88dfc3c25dc5547842c67d941','a2fee174e375fd82559200ffb13785f3','楼主666',NULL,NULL,0,0,1,'2025-03-19 11:19:00',NULL),('95b1d4a529f77d9fbf21c580b775b2e6','05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','哈哈哈',NULL,NULL,0,0,1,'2025-03-17 22:15:00',NULL),('c8f09a34cf8db8260908624a7bcceee6','c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','123',NULL,NULL,0,0,1,'2025-03-18 22:55:04',NULL);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
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
INSERT INTO `follow_relation` VALUES ('5f638c46f54b57647eb02052f587e9ea','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','2025-03-19 01:37:58'),('ac6a07aa4ae62e159a76ebf95d1586d1','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','2025-03-19 02:01:07');
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
INSERT INTO `like_record` VALUES ('0464c7d0ff2655ad092ba5dc144e3326','a2fee174e375fd82559200ffb13785f3','c73961a386567d3e0005313aaba528eb',1,'2025-03-19 00:41:11'),('09699a80b600bdc194c25b4de6a7b9b8','a2fee174e375fd82559200ffb13785f3','734dbe2b34228a5f4b4e0bef6e013123',1,'2025-03-19 00:40:58'),('101045fa07ac2d99c3d3f8047509ac1a','a2fee174e375fd82559200ffb13785f3','05ea89b3404969d391b515a0a89b4e73',1,'2025-03-19 00:40:53'),('2d813323bbf35cad395cf39eb85a84bb','a2fee174e375fd82559200ffb13785f3','5d089973e86d66232fcf8f025affb9ee',2,'2025-03-17 21:25:36'),('3d86c8e99c0c5cd0e572a3a0aac256b2','a2fee174e375fd82559200ffb13785f3','7b4f858e42856d4f3071aa2a0cbad663',1,'2025-03-19 00:40:20'),('6078f093a0278bab5fd9f4b162ca130c','a2fee174e375fd82559200ffb13785f3','9ac7540140fe1b92d7e21c8ff09515f1',1,'2025-03-19 00:41:01'),('6e079556f48f53b307625a1d2bbe4cc9','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','05ea89b3404969d391b515a0a89b4e73',1,'2025-03-19 00:43:45'),('a5eb7389bff0a20c84b8e8a0abe3a816','a2fee174e375fd82559200ffb13785f3','123a',2,'2025-03-17 21:24:56'),('de33ddfbbc4c1db9dcb6991eef7e8c17','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','40a614b88dfc3c25dc5547842c67d941',1,'2025-03-19 00:43:39'),('dec1c7a8394ae3ea42cf1321f3e86554','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','9ac7540140fe1b92d7e21c8ff09515f1',1,'2025-03-19 01:37:54'),('f8d4c9b1da9af69e6dd19967070d41cc','a2fee174e375fd82559200ffb13785f3','2ffc953cd1ac790906866e340f20ed1b',1,'2025-03-20 20:38:23');
/*!40000 ALTER TABLE `like_record` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_record`
--

LOCK TABLES `mood_record` WRITE;
/*!40000 ALTER TABLE `mood_record` DISABLE KEYS */;
INSERT INTO `mood_record` VALUES (1,'test_user_001','悲伤/低落',3,'55656','2025-03-10 18:53:11','2025-03-10 18:53:11'),(2,'test_user_001','悲伤/低落',3,'','2025-03-10 18:53:26','2025-03-10 18:53:26'),(3,'test_user_001','愤怒/烦躁',3,'','2025-03-11 18:56:21','2025-03-11 18:56:21'),(4,'test_user_001','平静/满足',3,'111','2025-03-11 19:03:47','2025-03-11 19:03:47'),(5,'test_user_001','平静/满足',3,'12','2025-03-15 21:26:46','2025-03-15 21:26:46'),(6,'test_user_001','疲惫/无力',3,'123','2025-03-15 21:31:54','2025-03-15 21:31:54'),(7,'test_user_001','愤怒/烦躁',3,'','2025-03-17 19:44:49','2025-03-17 19:44:49'),(8,'test_user_001','悲伤/低落',3,'太累了吧','2025-03-22 21:01:40','2025-03-22 21:01:40');
/*!40000 ALTER TABLE `mood_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mood_record_symptom`
--

DROP TABLE IF EXISTS `mood_record_symptom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mood_record_symptom` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `mood_record_id` bigint NOT NULL COMMENT '情绪记录ID',
  `symptom_id` bigint NOT NULL COMMENT '症状ID',
  `intensity` tinyint(1) NOT NULL COMMENT '症状强度，1-5',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_symptom` (`mood_record_id`,`symptom_id`),
  KEY `idx_symptom_id` (`symptom_id`),
  CONSTRAINT `fk_mrs_record` FOREIGN KEY (`mood_record_id`) REFERENCES `mood_record` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mrs_symptom` FOREIGN KEY (`symptom_id`) REFERENCES `physical_symptom` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪记录-身体症状关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_record_symptom`
--

LOCK TABLES `mood_record_symptom` WRITE;
/*!40000 ALTER TABLE `mood_record_symptom` DISABLE KEYS */;
/*!40000 ALTER TABLE `mood_record_symptom` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪记录-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mood_record_tag`
--

LOCK TABLES `mood_record_tag` WRITE;
/*!40000 ALTER TABLE `mood_record_tag` DISABLE KEYS */;
INSERT INTO `mood_record_tag` VALUES (1,2,1),(2,4,2),(3,4,3),(4,5,4),(5,5,5),(6,6,4),(7,6,6),(8,7,1),(10,8,3),(9,8,5);
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
INSERT INTO `notification` VALUES ('0a76de149a83032aff83015ef071c0ad','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:47:31'),('2a9879b61df48a72963dea6f28c8dac8','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:53:58'),('2e9c46769aa66265915d9fdba05d277a','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:59:12'),('2f8df20730fe698b48b030ee417bf3e0','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:51:02'),('37bf035dc21b6deeb0069de67e4ba9dd','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:48:01'),('5eb8455214776d2a7c2780a1d413ef0c','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 11:06:11'),('6082eebd7d0162766042cff53267b037','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 01:37:58'),('70348b8d107cb8d03a85a52349434b01','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','comment','5c4adc483dd7320be37655ab8ca7aba3','评论了你的帖子',0,'2025-03-19 00:44:07'),('96817eec28435fbb35e9413e6781bd69','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:50:33'),('9ef0ad51ff63517346875dfa7bdc7493','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','comment','8c56cfa8ea424039a07817eda076dfa9','评论了你的帖子',0,'2025-03-19 11:19:00'),('9ffd6e87725bdb3862477d5f993d33cd','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:48:12'),('a17423c982aa26b8c8ab5a7d07ceca6e','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:40:29'),('ad90f0258b928bc3f313efc1cd8a39b8','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','like','05ea89b3404969d391b515a0a89b4e73','点赞了你的帖子',0,'2025-03-19 00:43:45'),('b4ef83d9886cf702c1e549c1a8d372cb','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','like','9ac7540140fe1b92d7e21c8ff09515f1','点赞了你的帖子',0,'2025-03-19 01:37:54'),('d502c395bec0c1baaa2b46867032decf','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:47:47'),('e12daa98cfdb2e7510301d939d65a072','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','follow','a2fee174e375fd82559200ffb13785f3','关注了你',0,'2025-03-19 02:01:07'),('e88f9f039832021edefd5517a883dde3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:52:48'),('ec1571568c2e5b9e0c0d1b435c25a9f0','a2fee174e375fd82559200ffb13785f3','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','follow','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','关注了你',0,'2025-03-19 00:47:22'),('f7a176e33ff62e1b74ff2849a0430fbb','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','a2fee174e375fd82559200ffb13785f3','like','40a614b88dfc3c25dc5547842c67d941','点赞了你的帖子',0,'2025-03-19 10:46:46');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
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
INSERT INTO `post` VALUES ('05ea89b3404969d391b515a0a89b4e73','a2fee174e375fd82559200ffb13785f3','666666','blob:http://localhost:5173/f52304d8-315c-423d-9cfc-666e9e404c7e,blob:http://localhost:5173/df0a7c76-4255-443a-88b0-3bda60cc94ad',NULL,NULL,NULL,182,2,5,0,1,'2025-03-17 22:06:21','2025-03-19 11:49:18'),('2ffc953cd1ac790906866e340f20ed1b','a2fee174e375fd82559200ffb13785f3','666','blob:http://localhost:5173/6665f96a-9d16-4919-a31f-53b9c7bb5ebd',NULL,NULL,NULL,50,1,1,0,1,'2025-03-19 11:19:30','2025-03-20 20:39:03'),('40a614b88dfc3c25dc5547842c67d941','2e8e32a9-1609-4036-8ee6-c1cc252a91f7','多用户测试','blob:http://localhost:5173/d1369b9a-ad11-4f26-87e5-f04bcd677d9d',NULL,NULL,NULL,100,1,1,0,1,'2025-03-19 00:43:25','2025-03-20 09:19:47'),('734dbe2b34228a5f4b4e0bef6e013123','a2fee174e375fd82559200ffb13785f3','·12','blob:http://localhost:5173/f3b680de-4383-4903-9da8-6f1740bd623c',NULL,NULL,NULL,64,1,1,0,1,'2025-03-17 21:57:38','2025-03-19 13:28:30'),('7b4f858e42856d4f3071aa2a0cbad663','a2fee174e375fd82559200ffb13785f3','1111',NULL,NULL,NULL,NULL,210,1,3,0,1,'2025-03-17 18:49:45','2025-03-19 01:39:57'),('9ac7540140fe1b92d7e21c8ff09515f1','a2fee174e375fd82559200ffb13785f3','123',NULL,NULL,NULL,NULL,40,2,1,0,1,'2025-03-17 21:57:14','2025-03-20 20:39:45'),('c73961a386567d3e0005313aaba528eb','a2fee174e375fd82559200ffb13785f3','13','blob:http://localhost:5173/60a6229d-8d8e-4abd-bea2-05c261916582',NULL,NULL,NULL,68,1,2,1,1,'2025-03-17 21:45:32','2025-03-19 13:28:18');
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
INSERT INTO `question_option` VALUES ('1778c6cddd4441d688bc0005d5f6a4e4','be1f56842f144fe1bb7b7b600493b32a','1',1,1,'2025-03-14 14:17:59','2025-03-14 14:17:59'),('335ed99ddc7e4c5ea3557a15b0c02a00','fa09042028544e83a1cc4c4f389a8196','4',1,4,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('688d3e6fa30e42dc81a89410153ec61f','fa09042028544e83a1cc4c4f389a8196','3',1,3,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('a28b196484c2483483efd3f550705e25','fa09042028544e83a1cc4c4f389a8196','1',1,1,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('bfe87f3b4c30457bb031899f670dcee6','fa09042028544e83a1cc4c4f389a8196','2',1,2,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('sas_q2_o1','sas_q2','很少或从不',1,1,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o2','sas_q2','有时',2,2,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o3','sas_q2','大部分时间',3,3,'2025-03-05 09:22:25','2025-03-05 09:22:25'),('sas_q2_o4','sas_q2','绝大部分或全部时间',4,4,'2025-03-05 09:22:25','2025-03-05 09:22:25');
/*!40000 ALTER TABLE `question_option` ENABLE KEYS */;
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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_test_type` (`test_type_id`),
  CONSTRAINT `fk_question_test_type` FOREIGN KEY (`test_type_id`) REFERENCES `test_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_question`
--

LOCK TABLES `test_question` WRITE;
/*!40000 ALTER TABLE `test_question` DISABLE KEYS */;
INSERT INTO `test_question` VALUES ('be1f56842f144fe1bb7b7b600493b32a','sas','123??',3,1,'2025-03-14 14:17:59','2025-03-14 14:17:59'),('fa09042028544e83a1cc4c4f389a8196','sas','你是否经常疲惫',2,2,'2025-03-13 03:36:40','2025-03-13 03:36:40'),('sas_q2','sas','我无缘无故地感到害怕',1,1,'2025-03-05 09:22:18','2025-03-07 20:00:40');
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
INSERT INTO `test_result` VALUES ('0664fc53-6e17-4a5d-b517-5014e99d9ee0','a2fee174e375fd82559200ffb13785f3','sas',4,'低','您的焦虑自评量表 (SAS)测试得分为4分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-19 11:38:08','2025-03-19 11:48:08','2025-03-19 11:48:08'),('0beeff4e-dbef-4d06-95eb-85b2798f8f92','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:22:33','2025-03-07 20:32:33','2025-03-07 20:32:33'),('0cded7e7-74ea-4727-881f-7bad4f8c99fe','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:22:48','2025-03-07 20:32:48','2025-03-07 20:32:48'),('10dd6456-f1a1-46a0-9bc2-00c528803b64','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:03','2025-03-07 22:03:03','2025-03-07 22:03:03'),('12c983f4-570a-46ba-9bfc-01b1744887a6','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:29','2025-03-07 22:09:29','2025-03-07 22:09:29'),('1d9ca148-13be-4174-8bca-83720703bfac','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:21','2025-03-07 21:31:21','2025-03-07 21:31:21'),('23e6c74d-5a2c-44ff-b22e-928b007d1313','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-15 21:22:47','2025-03-15 21:32:47','2025-03-15 21:32:47'),('24dd54d8-6e01-482e-9b38-3f6c78ab7921','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:25','2025-03-07 21:31:25','2025-03-07 21:31:25'),('290576fa-1c65-4fb7-8d83-035b4932218e','test_user_001','sas',1,'低','您的焦虑自评量表 (SAS)测试得分为1分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:12:56','2025-03-07 22:22:56','2025-03-07 22:22:56'),('2afc0876-ae85-4e9b-be99-f8aa44759167','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:44:14','2025-03-07 19:54:14','2025-03-07 19:54:14'),('307ce13f-1dbc-43e6-a7b1-6b240c597337','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:50:54','2025-03-07 20:00:54','2025-03-07 20:00:53'),('30b0e368-42c0-4da9-8037-b86597902ae1','test_user_001','sas',1,'低','您的焦虑自评量表 (SAS)测试得分为1分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:31','2025-03-07 22:11:31','2025-03-07 22:11:31'),('39af6087-1c68-4b4a-893b-dce8d154668f','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:13','2025-03-07 22:03:13','2025-03-07 22:03:13'),('3a7f988c-b96e-4108-8167-e196f9dc1e84','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:47:15','2025-03-07 20:57:15','2025-03-07 20:57:15'),('3bd05878-e60f-4f9e-baee-1f7f11df3675','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:33','2025-03-07 22:09:33','2025-03-07 22:09:33'),('4a2e38bd-9f27-4b72-9efd-60bbc9db830a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:52:11','2025-03-07 20:02:11','2025-03-07 20:02:10'),('4d782567-1101-4bf5-bf97-559d639ed8fb','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:11','2025-03-07 21:31:11','2025-03-07 21:31:11'),('577d2618-e043-4900-9a7f-d62180633f81','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:59:32','2025-03-07 22:09:32','2025-03-07 22:09:32'),('5e03798f-08ce-4cf9-acd5-e72d033d676a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-06 03:59:13','2025-03-06 04:09:13','2025-03-06 04:09:12'),('5ed47f42-d07b-4a05-813f-1904ca8f83ed','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:21:02','2025-03-07 21:31:02','2025-03-07 21:31:02'),('61eec169-e789-4f1a-8872-ab9790708067','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:18:59','2025-03-07 21:28:59','2025-03-07 21:28:59'),('649fceff-a45d-4e01-a138-71276c608be9','test_user_001','sas',2,'低','您的焦虑自评量表 (SAS)测试得分为2分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:33','2025-03-07 22:11:33','2025-03-07 22:11:33'),('708c3e8f-d76d-41ff-9747-7ab9046511c7','test_user_001','sas',5,'低','您的焦虑自评量表 (SAS)测试得分为5分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:46:51','2025-03-13 12:56:51','2025-03-13 12:56:51'),('7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:46:38','2025-03-13 12:56:38','2025-03-13 12:56:38'),('7d4d4dee-2f3c-46dd-b49f-9b917fe277a9','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:36','2025-03-07 22:11:36','2025-03-07 22:11:36'),('7dc17f89-b31f-4d6a-b21a-a80a44e0aa9b','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:47:51','2025-03-07 20:57:51','2025-03-07 20:57:51'),('809df4da-3d91-4a31-8db2-4d079363829d','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:19:19','2025-03-07 21:29:19','2025-03-07 21:29:19'),('80ebcf8e-1c21-4b7a-b499-bb7193966a9a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:05:38','2025-03-07 20:15:38','2025-03-07 20:15:37'),('81488744-af4b-4c87-8401-67d8e884a942','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:53:07','2025-03-07 22:03:07','2025-03-07 22:03:07'),('81b2d4df-e2ad-4ab3-ba01-2cac89859ba7','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:21:22','2025-03-07 20:31:22','2025-03-07 20:31:22'),('89bdff55-1d46-490c-8220-8c5e00b6ecd4','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-11 18:53:26','2025-03-11 19:03:26','2025-03-11 19:03:26'),('978cdf41-3259-4cf0-8e58-9c877176b646','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:13:53','2025-03-07 20:23:53','2025-03-07 20:23:53'),('a346c5f3-0d9e-4779-8753-7d71ae220391','test_user_001','sas',4,'低','您的焦虑自评量表 (SAS)测试得分为4分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 22:01:37','2025-03-07 22:11:37','2025-03-07 22:11:37'),('a83e3889-8b69-4889-8551-fd158013f81d','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-14 09:23:10','2025-03-14 09:33:10','2025-03-14 09:33:10'),('af2ef208-944e-4b11-884b-6e0f1599e96c','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:06:01','2025-03-07 20:16:01','2025-03-07 20:16:00'),('b1f8bc23-24cc-46f6-868c-047713554e8a','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-15 16:42:23','2025-03-15 16:52:23','2025-03-15 16:52:23'),('b5da23e7-b76d-44b4-bc34-1597f66f7bf1','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:27:04','2025-03-07 21:37:04','2025-03-07 21:37:04'),('bab32992-a72a-413a-a93b-01336261dc7b','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:48:39','2025-03-07 21:58:39','2025-03-07 21:58:39'),('bd2f210a-08bb-4e47-a739-5cd1cabb8b1d','guest123','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:44:27','2025-03-07 20:54:27','2025-03-07 20:54:27'),('beeb17b5-6541-48f6-ae63-3025ce5d46f9','test_user_001','sas',3,'低','您的焦虑自评量表 (SAS)测试得分为3分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-13 12:45:58','2025-03-13 12:55:58','2025-03-13 12:55:58'),('c1094cfd-3468-4399-a971-10e266ad91e5','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:45:50','2025-03-07 20:55:50','2025-03-07 20:55:50'),('c97c21b1-a01a-40f0-855d-a5d4353b31fe','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:48:20','2025-03-07 19:58:20','2025-03-07 19:58:19'),('da752961-fba3-4abf-8ba4-52e53a44235a','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 20:13:15','2025-03-07 20:23:15','2025-03-07 20:23:14'),('e0658f3b-2fd3-4dc0-aab6-ec4c484a35de','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 19:49:11','2025-03-07 19:59:11','2025-03-07 19:59:10'),('f4bbb0ed-d4e0-4c2b-b9e3-8353f036dc84','test_user_001','sas',0,'低','您的焦虑自评量表 (SAS)测试得分为0分，结果显示您目前处于低状态。详细分析请参考下方建议。','1. 继续保持良好的生活习惯和积极的心态。\n2. 定期进行自我评估，关注心理健康。\n3. 培养健康的兴趣爱好，丰富生活内容。','2025-03-07 21:27:11','2025-03-07 21:37:11','2025-03-07 21:37:11');
/*!40000 ALTER TABLE `test_result` ENABLE KEYS */;
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
  `category` varchar(50) NOT NULL COMMENT '分类：情绪/人格/认知/关系等',
  `time_minutes` int DEFAULT NULL COMMENT '预计完成时间(分钟)',
  `question_count` int NOT NULL COMMENT '问题数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测试类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_type`
--

LOCK TABLES `test_type` WRITE;
/*!40000 ALTER TABLE `test_type` DISABLE KEYS */;
INSERT INTO `test_type` VALUES ('big5','大五人格测试','了解你的性格特点','icon-user-circle',NULL,'personality',10,50,'2025-03-05 09:21:44','2025-03-05 09:21:44',1),('mbti','MBTI人格类型测试','发现你的人格类型','icon-puzzle-piece',NULL,'personality',15,70,'2025-03-05 09:21:44','2025-03-05 09:21:44',1),('phq9','患者健康问卷 (PHQ-9)','抑郁症筛查工具','icon-clipboard-check',NULL,'common',3,9,'2025-03-05 09:21:44','2025-03-05 09:21:44',1),('pss','压力感知量表 (PSS)','评估压力水平的专业量表1','icon-bolt','','emotion',6,10,'2025-03-05 09:21:44','2025-03-20 08:58:13',1),('sas','焦虑自评量表 (SAS)','评估焦虑程度的专业量表','icon-brain','','emotion',5,20,'2025-03-05 09:21:44','2025-03-20 08:58:13',1),('sds','抑郁自评量表 (SDS)','评估抑郁程度的专业量表','icon-cloud-rain',NULL,'emotion',7,20,'2025-03-05 09:21:44','2025-03-05 09:21:44',1);
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
INSERT INTO `user` VALUES ('2e8e32a9-1609-4036-8ee6-c1cc252a91f7','testuser','$2a$10$7syCs2YvVuqV1eYS.6HjPegmVufNUpmt2USC2AlCr3AurFTbce4Ty','测试用户',NULL,NULL,NULL,NULL,'test@example.com','2025-03-17 17:39:02','2025-03-19 00:43:24','2025-03-22 21:32:17',1,1,1,0,1,0,1,'',1,7),('a2fee174e375fd82559200ffb13785f3','xg666','$2a$10$9w0swBkDI8A.ETWNqY4mSOFoXXspFtBawuD1peiO4lO50j2d5EYSK','jrj',NULL,1,'2025-02-17',NULL,'2902756263@qq.com','2025-03-17 17:52:05','2025-03-19 11:19:30','2025-03-22 21:00:49',1,1,1,0,1,0,7,'',6,2),('guest123','1','123','123',NULL,NULL,NULL,NULL,NULL,'2025-03-07 20:31:17','2025-03-07 20:31:17',NULL,1,0,0,0,0,0,0,'',0,0),('test_user_001','jxg666','xg999999','jxg',NULL,NULL,NULL,NULL,NULL,'2025-03-06 03:56:59','2025-03-17 17:35:06',NULL,1,0,0,0,0,0,0,'',0,0);
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
INSERT INTO `user_answer` VALUES ('0dff23c6-c6b9-4e3e-b04c-50bc2d705e47','beeb17b5-6541-48f6-ae63-3025ce5d46f9','sas_q2','sas_q2_o2',2,'2025-03-13 12:55:57'),('16913640-b309-4ba3-aebc-5892f48aacd0','7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','sas_q2','sas_q2_o2',2,'2025-03-13 12:56:37'),('169dc818-60bd-4ee7-accf-95b567d9b1a4','0664fc53-6e17-4a5d-b517-5014e99d9ee0','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-19 11:48:07'),('1aa1d634-fb2a-4fab-843d-8eadbd3f653d','89bdff55-1d46-490c-8220-8c5e00b6ecd4','sas_q2','sas_q2_o3',3,'2025-03-11 19:03:26'),('2ae5aee7-a80b-4b85-8d9a-4343cf7ed065','649fceff-a45d-4e01-a138-71276c608be9','sas_q2','sas_q2_o2',2,'2025-03-07 22:11:33'),('3e6f2928-ff64-4a90-9459-b6922e9b8a09','0664fc53-6e17-4a5d-b517-5014e99d9ee0','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-19 11:48:07'),('465c07fd-2376-469d-b427-8124e8a2811d','a83e3889-8b69-4889-8551-fd158013f81d','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-14 09:33:09'),('515b8038-e3b4-402f-9b83-ed27cddd4f09','30b0e368-42c0-4da9-8037-b86597902ae1','sas_q2','sas_q2_o1',1,'2025-03-07 22:11:31'),('59cc7ded-8077-4079-8c34-344e2e4801ca','708c3e8f-d76d-41ff-9747-7ab9046511c7','sas_q2','sas_q2_o4',4,'2025-03-13 12:56:51'),('6306a5f3-498a-429c-9838-c44d12836169','b1f8bc23-24cc-46f6-868c-047713554e8a','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-15 16:52:23'),('6454f41e-11ec-402f-8707-f8e2451949ec','23e6c74d-5a2c-44ff-b22e-928b007d1313','fa09042028544e83a1cc4c4f389a8196','688d3e6fa30e42dc81a89410153ec61f',1,'2025-03-15 21:32:46'),('65880fc3-4d7c-4bcb-a394-6384c92ebe83','a83e3889-8b69-4889-8551-fd158013f81d','sas_q2','sas_q2_o2',2,'2025-03-14 09:33:09'),('6696febe-9cbf-4fe8-9afa-876990d612b5','708c3e8f-d76d-41ff-9747-7ab9046511c7','fa09042028544e83a1cc4c4f389a8196','335ed99ddc7e4c5ea3557a15b0c02a00',1,'2025-03-13 12:56:51'),('741c78b7-9186-43b7-9a47-205008eac8b9','0664fc53-6e17-4a5d-b517-5014e99d9ee0','sas_q2','sas_q2_o2',2,'2025-03-19 11:48:07'),('7563b28a-d225-48c3-b2b0-038b563647aa','23e6c74d-5a2c-44ff-b22e-928b007d1313','sas_q2','sas_q2_o1',1,'2025-03-15 21:32:46'),('799d5ded-c6c6-4d1d-8b66-2251aa7dee3a','23e6c74d-5a2c-44ff-b22e-928b007d1313','be1f56842f144fe1bb7b7b600493b32a','1778c6cddd4441d688bc0005d5f6a4e4',1,'2025-03-15 21:32:46'),('99deacfb-630a-49b9-803e-ed94f740edf1','7a59ea94-cfd9-40b5-b775-c4ccdf5a5058','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-13 12:56:37'),('9b2a7152-a222-4668-aea7-18fe80560d8e','290576fa-1c65-4fb7-8d83-035b4932218e','sas_q2','sas_q2_o1',1,'2025-03-07 22:22:55'),('c8be5dbb-113f-4146-91ee-8fc082bccb9b','7d4d4dee-2f3c-46dd-b49f-9b917fe277a9','sas_q2','sas_q2_o3',3,'2025-03-07 22:11:35'),('e000292d-cc74-487c-8d46-5fe338dcbda5','b1f8bc23-24cc-46f6-868c-047713554e8a','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-15 16:52:23'),('e36a3d86-3148-4ae1-9524-db5262acbab0','b1f8bc23-24cc-46f6-868c-047713554e8a','sas_q2','sas_q2_o1',1,'2025-03-15 16:52:22'),('eb3671d5-4a81-471b-be4d-4d087f640650','beeb17b5-6541-48f6-ae63-3025ce5d46f9','fa09042028544e83a1cc4c4f389a8196','bfe87f3b4c30457bb031899f670dcee6',1,'2025-03-13 12:55:57'),('f0bd50bb-5ae1-436b-b84c-83551a519a7a','a346c5f3-0d9e-4779-8753-7d71ae220391','sas_q2','sas_q2_o4',4,'2025-03-07 22:11:37');
/*!40000 ALTER TABLE `user_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_view_history`
--

DROP TABLE IF EXISTS `user_view_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_view_history` (
  `id` varchar(36) NOT NULL COMMENT 'ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `video_id` varchar(36) NOT NULL COMMENT '视频ID',
  `progress` int DEFAULT '0' COMMENT '观看进度(秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`,`video_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户观看历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_view_history`
--

LOCK TABLES `user_view_history` WRITE;
/*!40000 ALTER TABLE `user_view_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_view_history` ENABLE KEYS */;
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
INSERT INTO `verification_code` VALUES ('2c3eca64e733f6d34d3df5abc0097c9d','2902756263@qq.com','527403','2025-03-17 17:51:40','2025-03-17 17:56:40',1),('7b9a8cee0e9dd160e2a7dbd2771be011','1025828544@qq.com','875962','2025-03-07 17:35:57','2025-03-07 17:40:57',1),('b84672148d623e69616a912b5e83a0ee','2902756263@qq.com','916838','2025-03-07 17:20:44','2025-03-07 17:25:44',1);
/*!40000 ALTER TABLE `verification_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_category`
--

DROP TABLE IF EXISTS `video_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_category` (
  `id` varchar(36) NOT NULL COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='视频分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_category`
--

LOCK TABLES `video_category` WRITE;
/*!40000 ALTER TABLE `video_category` DISABLE KEYS */;
INSERT INTO `video_category` VALUES ('0j1k2l3m-4n5o-6p7q-8r9s-0t1u2v3w4x5y','身心平衡',100,'balance-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('1a2b3c4d-5e6f-7g8h-9i0j-1k2l3m4n5o6p','冥想放松',10,'meditation-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('2b3c4d5e-6f7g-8h9i-0j1k-2l3m4n5o6p7q','瑜伽练习',20,'yoga-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('3c4d5e6f-7g8h-9i0j-1k2l-3m4n5o6p7q8r','呼吸训练',30,'breathing-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('4d5e6f7g-8h9i-0j1k-2l3m-4n5o6p7q8r9s','睡眠辅助',40,'sleep-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('5e6f7g8h-9i0j-1k2l-3m4n-5o6p7q8r9s0t','情绪管理',50,'emotion-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('6f7g8h9i-0j1k-2l3m-4n5o-6p7q8r9s0t1u','正念生活',60,'mindfulness-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t1u2v','压力缓解',70,'stress-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('8h9i0j1k-2l3m-4n5o-6p7q-8r9s0t1u2v3w','专注力提升',80,'focus-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21'),('9i0j1k2l-3m4n-5o6p-7q8r-9s0t1u2v3w4x','自我关怀',90,'self-care-icon.png','2025-03-21 00:52:21','2025-03-21 00:52:21');
/*!40000 ALTER TABLE `video_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_info`
--

DROP TABLE IF EXISTS `video_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_info` (
  `id` varchar(36) NOT NULL COMMENT '视频ID',
  `title` varchar(100) NOT NULL COMMENT '视频标题',
  `category_id` varchar(36) NOT NULL COMMENT '分类ID',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `file_key` varchar(255) NOT NULL COMMENT '七牛云文件key',
  `duration` int DEFAULT '0' COMMENT '视频时长(秒)',
  `description` text COMMENT '视频描述',
  `view_count` bigint DEFAULT '0' COMMENT '观看次数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='视频信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_info`
--

LOCK TABLES `video_info` WRITE;
/*!40000 ALTER TABLE `video_info` DISABLE KEYS */;
INSERT INTO `video_info` VALUES ('5405f1f98867bc3abc6cb793d4429a22','屏幕录制 2025-03-19 111938','',NULL,'videos/eb64eeb4-2a8a-4613-8338-332ae6bd6916.mp4',0,NULL,10,1,'2025-03-21 01:41:45','2025-03-21 03:15:48'),('9549c8336a51ddb01552927970101342','屏幕录制 2025-03-19 111938','',NULL,'videos/64097f32-4f61-41f2-8453-5cef11aead79.mp4',0,NULL,1,1,'2025-03-21 02:32:33','2025-03-21 02:32:33');
/*!40000 ALTER TABLE `video_info` ENABLE KEYS */;
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

-- Dump completed on 2025-03-22 22:16:56
