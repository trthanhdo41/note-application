-- MySQL dump 10.13  Distrib 8.3.0, for macos14.2 (arm64)
--
-- Host: localhost    Database: note_app
-- ------------------------------------------------------
-- Server version	8.3.0

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
-- Table structure for table `account_activation_token`
--

DROP TABLE IF EXISTS `account_activation_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_activation_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkoajxv968tet95apni6u0nj7b` (`user_id`),
  CONSTRAINT `FKpdsifs4fdn575d43by6jiyk73` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_activation_token`
--

LOCK TABLES `account_activation_token` WRITE;
/*!40000 ALTER TABLE `account_activation_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_activation_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `join_at` datetime(6) NOT NULL,
  `blocked` bit(1) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnr9qg33qt2ovmv29g4vc3gtdx` (`user_id`),
  KEY `FK3n30113wehv816pg56yktid5s` (`group_id`),
  CONSTRAINT `FK3n30113wehv816pg56yktid5s` FOREIGN KEY (`group_id`) REFERENCES `groupss` (`id`),
  CONSTRAINT `FKnr9qg33qt2ovmv29g4vc3gtdx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_notes`
--

DROP TABLE IF EXISTS `group_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `note_content` text,
  `note_title` text,
  `created_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn0foseodwuiin2nvjuoglg1ei` (`user_id`),
  KEY `FKsu9nllinbn7q37f3pqm2hgwe8` (`group_id`),
  CONSTRAINT `FKn0foseodwuiin2nvjuoglg1ei` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKsu9nllinbn7q37f3pqm2hgwe8` FOREIGN KEY (`group_id`) REFERENCES `groupss` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_notes`
--

LOCK TABLES `group_notes` WRITE;
/*!40000 ALTER TABLE `group_notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groupss`
--

DROP TABLE IF EXISTS `groupss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `groupss` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_code` varchar(255) NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `creator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqoim6cqgju6u0kvm7qxh7ypx4` (`group_code`),
  KEY `FKolltxoxr8ribt280ndhana7x9` (`creator_id`),
  CONSTRAINT `FKolltxoxr8ribt280ndhana7x9` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groupss`
--

LOCK TABLES `groupss` WRITE;
/*!40000 ALTER TABLE `groupss` DISABLE KEYS */;
/*!40000 ALTER TABLE `groupss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note` (
  `id` bigint NOT NULL,
  `title` text,
  `content` text,
  `time` datetime(6) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaxew7axjawf2la92pc4yxcm87` (`user_id`),
  CONSTRAINT `FKaxew7axjawf2la92pc4yxcm87` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note`
--

LOCK TABLES `note` WRITE;
/*!40000 ALTER TABLE `note` DISABLE KEYS */;
/*!40000 ALTER TABLE `note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note_seq`
--

DROP TABLE IF EXISTS `note_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_seq`
--

LOCK TABLES `note_seq` WRITE;
/*!40000 ALTER TABLE `note_seq` DISABLE KEYS */;
INSERT INTO `note_seq` VALUES (1351);
/*!40000 ALTER TABLE `note_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(500) NOT NULL,
  `title` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (24,'Trang web ghi chú của chúng ta vừa được cập nhật với nhiều tính năng mới hấp dẫn. Hãy trải nghiệm và cho chúng tôi biết cảm nhận của bạn.','Bản Cập Nhật Mới Đã Ra Mắt','2024-07-04 23:51:45.000000'),(25,'Hệ thống sẽ được bảo trì vào ngày 15/07/2024 từ 22:00 đến 02:00. Trong thời gian này, các bạn có thể gặp gián đoạn khi truy cập vào trang web.','Bảo Trì Hệ Thống','2024-07-04 23:51:45.000000'),(26,'Tính năng đồng bộ ghi chú trên nhiều thiết bị đã có sẵn. Giờ đây, bạn có thể truy cập và chỉnh sửa ghi chú từ bất kỳ thiết bị nào.','Ra Mắt Tính Năng Đồng Bộ Ghi Chú','2024-07-04 23:51:45.000000'),(27,'Hãy tham gia cuộc thi viết ghi chú hay nhất và có cơ hội nhận được nhiều phần quà hấp dẫn. Hạn chót nộp bài dự thi là ngày 30/07/2024.','Tham Gia Cuộc Thi Viết Ghi Chú Hay','2024-07-04 23:51:45.000000'),(28,'Để giúp bạn sử dụng trang web hiệu quả hơn, chúng tôi đã cập nhật hướng dẫn sử dụng chi tiết. Hãy xem ngay tại mục Hướng Dẫn Sử Dụng trên trang chủ.','Hướng Dẫn Sử Dụng Trang Web','2024-07-04 23:51:45.000000'),(29,'Từ ngày 01/08/2024, trang web sẽ hoạt động 24/7 để phục vụ nhu cầu ghi chú của bạn mọi lúc mọi nơi.','Thay Đổi Thời Gian Hoạt Động','2024-07-04 23:51:45.000000'),(30,'Nhân dịp mùa hè, chúng tôi có chương trình khuyến mãi đặc biệt giảm giá 20% cho tất cả người dùng đăng ký gói Premium. Hãy đăng ký ngay!','Chương Trình Khuyến Mãi Mùa Hè','2024-07-04 23:51:45.000000');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`),
  CONSTRAINT `FK83nsrttkwkb6ym0anu051mtxn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `pw` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `danhxung` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `totp_secret` varchar(255) DEFAULT NULL,
  `registration_time` datetime(6) NOT NULL,
  `active` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'trthanhdo41','$2a$10$ANa2MnmBvhLH1xLYcZygs.pVqGA65z7nZ3rGmGX0yMRO7Uvog0cVq','trthanhdo41@gmail.com','Trần Thanh Độ','Mr','ROLE_ADMIN','/uploads/avatar/e3e1e35f-c97f-4222-93f2-448a26a62a10_avatar.jpg',NULL,'2024-06-29 18:23:50.189132',1),(2,'nguyenvanan1233','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','dott22@uef.edu.vn','Nguyễn Văn An','Mr','ROLE_VIPMEMBER','/uploads/avatar/Shin_Hye-Sung.jpg',NULL,'2024-04-15 14:21:30.123000',1),(4,'nguyenchipheo','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenchipheo@gmail.com','Nguyễn Chí Phèo','Other','ROLE_USER','/uploads/avatar/p.jpg',NULL,'2024-06-01 09:45:12.678000',1),(5,'phamthimay234','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phamthimay234@gmail.com','Phạm Thị Mây','Ms','ROLE_USER','/uploads/avatar/bhm3.jpg',NULL,'2024-03-25 10:50:47.456000',1),(6,'levantuan567','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','levantuan567@hotmail.com','Lê Văn Tuấn','Mr','ROLE_USER','/uploads/avatar/olp.jpg',NULL,'2024-06-11 13:25:34.567000',1),(8,'trthanhdo411','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dangvanhung1234@gmail.com','Đặng Văn Hùng','Mr','ROLE_MANAGER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png',NULL,'2024-04-22 15:27:36.789000',1),(12,'lethiha789r','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethiha789@gmail.com','Lê Thị Hà','Mr','ROLE_USER','/uploads/avatar/be7u.jpg',NULL,'2024-05-20 11:35:20.101000',1),(13,'1','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenthihuong1234@gmail.com','Nguyễn Thị Hương','Mr','ROLE_USER','/uploads/avatar/bhm3.jpg',NULL,'2024-04-21 10:21:30.654000',1),(14,'phanvantam','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phanvantam5678@gmail.com','2234','Mr','ROLE_USER','/uploads/avatar/be2o.jpg',NULL,'2024-04-18 15:45:30.321000',1),(16,'lethikim456','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethikim456@gmail.com','Lê Thị Kim','Mr','ROLE_USER','/uploads/avatar/bd6w.jpg',NULL,'2024-05-21 13:45:25.234000',1),(17,'nguyenvanbinh789','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenvanbinh789@gmail.com','Nguyễn Văn Bình','Ms','ROLE_USER','/uploads/avatar/a3zl.jpg',NULL,'2024-05-05 11:50:30.987000',1),(18,'nguyenminhtuan123','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenminhtuan123@hust.edu.vn','Nguyễn Minh Tuấn','Đồng tính','ROLE_USER','/uploads/avatar/Shin_Hye-Sung.jpg','ABCD1234EFGH5678','2024-06-15 08:21:30.123000',1),(19,'lethanhtung456','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethanhtung456@outlook.com','Lê Thanh Tùng','Lưỡng tính','ROLE_USER','/uploads/avatar/saostar-y937nvnpf0l6f2rl.jpg',NULL,'2024-05-10 11:35:25.567000',1),(20,'nguyenvanphuc789','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenvanphuc789@hcmut.edu.vn','Nguyễn Văn Phúc','Mr','ROLE_USER','/uploads/avatar/p.jpg','EFGH5678IJKL9012','2024-06-01 09:45:12.678000',1),(21,'phamthinga1234','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phamthinga1234@gmail.com','Phạm Thị Nga','Ms','ROLE_VIPMEMBER',NULL,NULL,'2024-03-25 10:50:47.456000',1),(22,'levanhoahust567','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','levanhoahust567@gmail.com','Lê Văn Hòa','Đồng tính','ROLE_VIPMEMBER','/uploads/avatar/olp.jpg','IJKL9012MNOP3456','2024-06-11 13:25:34.567000',1),(23,'vuthimai890','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','vuthimai890@hust.edu.vn','Vũ Thị Mai','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/nguoi-noi-tieng-cung-bao-binh_528.jpg',NULL,'2024-05-19 14:26:35.678000',1),(24,'dangvanlong1234','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dangvanlong1234@gmail.com','Đặng Văn Long','Mr','ROLE_VIPMEMBER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png','MNOP3456QRST7890','2024-04-22 15:27:36.789000',1),(25,'buithiquynh5678','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','buithiquynh5678@gmail.com','Bùi Thị Quỳnh','Ms','ROLE_VIPMEMBER','/uploads/avatar/harper-bazaar-review-phim-nguoi-noi-tieng-celebrity-lavieenbluu-e1688890221163.jpg',NULL,'2024-05-08 16:28:37.890000',1),(26,'dovankhoa123','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dovankhoa123@hotmail.com','Đỗ Văn Khoa','Đồng tính','ROLE_VIPMEMBER','/uploads/avatar/images.jpg','QRST7890UVWX1234','2024-06-05 17:29:38.901000',1),(27,'ngothituyet456','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','ngothituyet456@gmail.com','Ngô Thị Tuyết','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/kb.jpg',NULL,'2024-06-04 18:30:39.012000',1),(28,'lethivan789','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethivan789@gmail.com','Lê Thị Vân','Mr','ROLE_USER','/uploads/avatar/be7u.jpg',NULL,'2024-05-20 11:35:20.101000',1),(29,'nguyenthihan1234','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenthihan1234@gmail.com','Nguyễn Thị Hạnh','Ms','ROLE_VIPMEMBER','/uploads/avatar/bhm3.jpg',NULL,'2024-04-21 10:21:30.654000',1),(30,'phanvantung','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phanvantung@gmail.com','Phan Văn Tùng','Đồng tính','ROLE_VIPMEMBER','/uploads/avatar/be2o.jpg','LMNO2345QRST6789','2024-04-18 15:45:30.321000',1),(31,'tranvanminh','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','tranvanminh@hotmail.com','Trần Văn Minh','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/ad2i.jpg',NULL,'2024-05-30 14:15:20.789000',1),(32,'lethihoa','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethihoa@gmail.com','Lê Thị Hoa','Mr','ROLE_VIPMEMBER','/uploads/avatar/bd6w.jpg','UVWX6789YZAB1234','2024-05-21 13:45:25.234000',1),(33,'nguyenvannam','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenvannam@gmail.com','Nguyễn Văn Nam','Ms','ROLE_VIPMEMBER','/uploads/avatar/a3zl.jpg',NULL,'2024-05-05 11:50:30.987000',1),(34,'nguyenvanminh','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenvanminh@gmail.com','Nguyễn Văn Minh','Đồng tính','ROLE_VIPMEMBER','/uploads/avatar/Shin_Hye-Sung.jpg','QRST5678UVWX1234','2024-04-22 10:21:30.123000',1),(35,'phamthilan','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phamthilan@gmail.com','Phạm Thị Lan','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/saostar-y937nvnpf0l6f2rl.jpg',NULL,'2024-05-12 11:35:25.567000',1),(36,'tranvantam','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','tranvantam@gmail.com','Trần Văn Tâm','Mr','ROLE_VIPMEMBER','/uploads/avatar/p.jpg','EFGH5678IJKL9012','2024-06-02 09:45:12.678000',1),(37,'lethibinh','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethibinh@gmail.com','Lê Thị Bình','Ms','ROLE_VIPMEMBER',NULL,NULL,'2024-03-28 10:50:47.456000',1),(38,'vuvankien','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','vuvankien@hotmail.com','Vũ Văn Kiên','Đồng tính','ROLE_VIPMEMBER','/uploads/avatar/olp.jpg','IJKL9012MNOP3456','2024-06-11 13:25:34.567000',1),(39,'nguyenthixuan','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenthixuan@gmail.com','Nguyễn Thị Xuân','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/nguoi-noi-tieng-cung-bao-binh_528.jpg',NULL,'2024-05-20 14:26:35.678000',1),(40,'dangvanthanh','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dangvanthanh@gmail.com','Đặng Văn Thanh','Mr','ROLE_VIPMEMBER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png','MNOP3456QRST7890','2024-04-25 15:27:36.789000',1),(41,'buithihai','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','buithihai@gmail.com','Bùi Thị Hải','Ms','ROLE_VIPMEMBER','/uploads/avatar/harper-bazaar-review-phim-nguoi-noi-tieng-celebrity-lavieenbluu-e1688890221163.jpg',NULL,'2024-05-18 16:28:37.890000',1),(42,'dovanhai','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dovanhai@hotmail.com','Đỗ Văn Hải','Mr','ROLE_USER','/uploads/avatar/images.jpg','QRST7890UVWX1234','2024-06-07 17:29:38.901000',1),(43,'ngothimai','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','ngothimai@gmail.com','Ngô Thị Mai','Ms','ROLE_VIPMEMBER','/uploads/avatar/kb.jpg',NULL,'2024-06-06 18:30:39.012000',1),(44,'levanhoahcmut','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','levanhoahcmut@gmail.com','Lê Văn Hòa','Đồng tính','ROLE_USER','/uploads/avatar/be7u.jpg','ZYXW9876VUTS5432','2024-05-24 11:35:20.101000',1),(45,'nguyenthi2huong','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenthihuon2g@gmail.com','Nguyễn Thị Hương','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/bhm3.jpg',NULL,'2024-04-23 10:21:30.654000',1),(46,'phanvanloc','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phanvanloc@gmail.com','Phan Văn Lộc','Mr','ROLE_USER','/uploads/avatar/be2o.jpg','LMNO2345QRST6789','2024-04-20 15:45:30.321000',1),(47,'tranvanlam','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','tranvanlam@hotmail.com','Trần Văn Lâm','Ms','ROLE_VIPMEMBER','/uploads/avatar/ad2i.jpg',NULL,'2024-05-28 14:15:20.789000',1),(48,'lethihan','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethihan@gmail.com','Lê Thị Hạnh','Đồng tính','ROLE_USER','/uploads/avatar/bd6w.jpg','UVWX6789YZAB1234','2024-05-29 13:45:25.234000',1),(49,'nguyenvanngoc','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenvanngoc@gmail.com','Nguyễn Văn Ngọc','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/a3zl.jpg',NULL,'2024-05-07 11:50:30.987000',1),(50,'nguyenminhtuan_1k2h3k1','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','nguyenminhtuan_1k2h3k1@hust.edu.vn','Nguyễn Minh Tuấn','Mr','ROLE_USER','/uploads/avatar/Shin_Hye-Sung.jpg','ABCD1234EFGH5678','2024-06-15 08:21:30.123000',1),(51,'lethanhtung_k213j123','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethanhtung_k213j123@outlook.com','Lê Thanh Tùng','Ms','ROLE_VIPMEMBER','/uploads/avatar/saostar-y937nvnpf0l6f2rl.jpg',NULL,'2024-05-10 11:35:25.567000',1),(53,'phamthinga_s213k12','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','phamthinga_s213k12@gmail.com','Phạm Thị Nga','Lưỡng tính','ROLE_VIPMEMBER',NULL,NULL,'2024-03-25 10:50:47.456000',1),(54,'levanhoahust_23j12h','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','levanhoahust_23j12h@gmail.com','Lê Văn Hòa','Mr','ROLE_USER','/uploads/avatar/olp.jpg','IJKL9012MNOP3456','2024-06-11 13:25:34.567000',1),(55,'vuthimai_s23j1j2','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','vuthimai_s23j1j2@hust.edu.vn','Vũ Thị Mai','Ms','ROLE_VIPMEMBER','/uploads/avatar/nguoi-noi-tieng-cung-bao-binh_528.jpg',NULL,'2024-05-19 14:26:35.678000',1),(56,'dangvanlong_9s23j2h','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dangvanlong_9s23j2h@gmail.com','Đặng Văn Long','Đồng tính','ROLE_USER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png','MNOP3456QRST7890','2024-04-22 15:27:36.789000',1),(57,'buithiquynh_3s21kj3','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','buithiquynh_3s21kj3@gmail.com','Bùi Thị Quỳnh','Lưỡng tính','ROLE_VIPMEMBER','/uploads/avatar/harper-bazaar-review-phim-nguoi-noi-tieng-celebrity-lavieenbluu-e1688890221163.jpg',NULL,'2024-05-08 16:28:37.890000',1),(58,'dovankhoa_s23kj32','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','dovankhoa_s23kj32@hotmail.com','Đỗ Văn Khoa','Mr','ROLE_USER','/uploads/avatar/images.jpg','QRST7890UVWX1234','2024-06-05 17:29:38.901000',1),(59,'ngothituyet_k1h32k1','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','ngothituyet_k1h32k1@gmail.com','Ngô Thị Tuyết','Ms','ROLE_VIPMEMBER','/uploads/avatar/kb.jpg',NULL,'2024-06-04 18:30:39.012000',1),(60,'lethivan_12k3j1k','$2a$10$xv0LWzNG1XoQYhvgM3Q5qeZJCAzUWvISROXqHGu7xbrXipgWHyjlm','lethivan_12k3j1k@gmail.com','Lê Thị Vân','Đồng tính','ROLE_USER','/uploads/avatar/be7u.jpg','ZYXW9876VUTS5432','2024-05-20 11:35:20.101000',1),(65,'johndoe_akj12k3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','johndoe_akj12k3@gmail.com','John Doe','Mr','ROLE_USER','/uploads/avatar/Shin_Hye-Sung.jpg','ABCD1234EFGH5678','2024-06-15 08:21:30.123000',1),(66,'janesmith_akj12k3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','janesmith_akj12k3@outlook.com','Jane Smith','Ms','ROLE_USER','/uploads/avatar/saostar-y937nvnpf0l6f2rl.jpg',NULL,'2024-05-10 11:35:25.567000',1),(67,'michaeljohnson_2kj13k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','michaeljohnson_2kj13k@gmail.com','Michael Johnson','Mr','ROLE_USER','/uploads/avatar/p.jpg','EFGH5678IJKL9012','2024-06-01 09:45:12.678000',1),(68,'emilydavis_2kj1k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','emilydavis_2kj1k@gmail.com','Emily Davis','Ms','ROLE_USER',NULL,NULL,'2024-03-25 10:50:47.456000',1),(69,'davidwilson_23k1j2','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','davidwilson_23k1j2@hotmail.com','David Wilson','Mr','ROLE_USER','/uploads/avatar/olp.jpg','IJKL9012MNOP3456','2024-06-11 13:25:34.567000',1),(70,'sophiamartinez_2k31j2','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','sophiamartinez_2k31j2@gmail.com','Sophia Martinez','Ms','ROLE_USER','/uploads/avatar/nguoi-noi-tieng-cung-bao-binh_528.jpg',NULL,'2024-05-19 14:26:35.678000',1),(71,'jamesanderson_1j2k3j','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','jamesanderson_1j2k3j@gmail.com','James Anderson','Mr','ROLE_USER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png','MNOP3456QRST7890','2024-04-22 15:27:36.789000',1),(72,'oliviabrown_1k2j3k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','oliviabrown_1k2j3k@gmail.com','Olivia Brown','Ms','ROLE_USER','/uploads/avatar/harper-bazaar-review-phim-nguoi-noi-tieng-celebrity-lavieenbluu-e1688890221163.jpg',NULL,'2024-05-08 16:28:37.890000',1),(73,'williamjones_2kj1k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','williamjones_2kj1k@hotmail.com','William Jones','Mr','ROLE_USER','/uploads/avatar/images.jpg','QRST7890UVWX1234','2024-06-05 17:29:38.901000',1),(74,'isabellagarcia_12kj3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','isabellagarcia_12kj3@gmail.com','Isabella Garcia','Ms','ROLE_USER','/uploads/avatar/kb.jpg',NULL,'2024-06-04 18:30:39.012000',1),(75,'benjaminmiller_23kj1','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','benjaminmiller_23kj1@gmail.com','Benjamin Miller','Mr','ROLE_USER','/uploads/avatar/be7u.jpg','ZYXW9876VUTS5432','2024-05-20 11:35:20.101000',1),(76,'miarodriguez_2kj13','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','miarodriguez_2kj13@gmail.com','Mia Rodriguez','Ms','ROLE_USER','/uploads/avatar/bhm3.jpg',NULL,'2024-04-21 10:21:30.654000',1),(77,'ethanmartinez_1j2k3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','ethanmartinez_1j2k3@gmail.com','Ethan Martinez','Mr','ROLE_USER','/uploads/avatar/be2o.jpg','LMNO2345QRST6789','2024-04-18 15:45:30.321000',1),(78,'avahernandez_2kj3k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','avahernandez_2kj3k@hotmail.com','Ava Hernandez','Ms','ROLE_USER','/uploads/avatar/ad2i.jpg',NULL,'2024-05-30 14:15:20.789000',1),(79,'masonwilson_12kj3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','masonwilson_12kj3@gmail.com','Mason Wilson','Mr','ROLE_USER','/uploads/avatar/bd6w.jpg','UVWX6789YZAB1234','2024-05-21 13:45:25.234000',1),(80,'sophialopez_1k23j','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','sophialopez_1k23j@gmail.com','Sophia Lopez','Ms','ROLE_USER','/uploads/avatar/a3zl.jpg',NULL,'2024-05-05 11:50:30.987000',1),(81,'alexandergonzalez_12k3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','alexandergonzalez_12k3@gmail.com','Alexander Gonzalez','Mr','ROLE_USER','/uploads/avatar/Shin_Hye-Sung.jpg','QRST5678UVWX1234','2024-04-22 10:21:30.123000',1),(82,'ellaclark_23j1k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','ellaclark_23j1k@gmail.com','Ella Clark','Ms','ROLE_USER','/uploads/avatar/saostar-y937nvnpf0l6f2rl.jpg',NULL,'2024-05-12 11:35:25.567000',1),(83,'liamwalker_2j13k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','liamwalker_2j13k@gmail.com','Liam Walker','Mr','ROLE_USER','/uploads/avatar/p.jpg','EFGH5678IJKL9012','2024-06-02 09:45:12.678000',1),(84,'ameliaharris_2kj1','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','ameliaharris_2kj1@gmail.com','Amelia Harris','Ms','ROLE_USER',NULL,NULL,'2024-03-28 10:50:47.456000',1),(85,'lucasyoung_12kjh','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','lucasyoung_12kjh@hotmail.com','Lucas Young','Mr','ROLE_USER','/uploads/avatar/olp.jpg','IJKL9012MNOP3456','2024-06-11 13:25:34.567000',1),(86,'milalee_2k1jh23','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','milalee_2k1jh23@gmail.com','Mila Lee','Ms','ROLE_USER','/uploads/avatar/nguoi-noi-tieng-cung-bao-binh_528.jpg',NULL,'2024-05-20 14:26:35.678000',1),(87,'henryking_23j1k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','henryking_23j1k@gmail.com','Henry King','Mr','ROLE_USER','/uploads/avatar/nhan-sac-my-nhan-u40-gay-sot-trong-phim-nguoi-noi-tieng-aa0-6939897.png','MNOP3456QRST7890','2024-04-25 15:27:36.789000',1),(88,'zoewright_1k2j3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','zoewright_1k2j3@gmail.com','Zoe Wright','Ms','ROLE_USER','/uploads/avatar/harper-bazaar-review-phim-nguoi-noi-tieng-celebrity-lavieenbluu-e1688890221163.jpg',NULL,'2024-05-18 16:28:37.890000',1),(89,'danielscott_1k23j','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','danielscott_1k23j@hotmail.com','Daniel Scott','Mr','ROLE_USER','/uploads/avatar/images.jpg','QRST7890UVWX1234','2024-06-07 17:29:38.901000',1),(90,'gracegreen_2j31k','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','gracegreen_2j31k@gmail.com','Grace Green','Ms','ROLE_USER','/uploads/avatar/kb.jpg',NULL,'2024-06-06 18:30:39.012000',1),(91,'matthewadams_1k23j','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','matthewadams_1k23j@gmail.com','Matthew Adams','Mr','ROLE_USER','/uploads/avatar/be7u.jpg','ZYXW9876VUTS5432','2024-05-24 11:35:20.101000',1),(92,'averynelson_2kj1k3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','averynelson_2kj1k3@gmail.com','Avery Nelson','Ms','ROLE_USER','/uploads/avatar/bhm3.jpg',NULL,'2024-04-23 10:21:30.654000',1),(93,'jacksonperez_1k2j3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','jacksonperez_1k2j3@gmail.com','Jackson Perez','Mr','ROLE_USER','/uploads/avatar/be2o.jpg','LMNO2345QRST6789','2024-04-20 15:45:30.321000',1),(94,'scarlettcarter_2kj13','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','scarlettcarter_2kj13@hotmail.com','Scarlett Carter','Ms','ROLE_USER','/uploads/avatar/ad2i.jpg',NULL,'2024-05-28 14:15:20.789000',1),(95,'sebastianmitchell_12kj3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','sebastianmitchell_12kj3@gmail.com','Sebastian Mitchell','Mr','ROLE_USER','/uploads/avatar/bd6w.jpg','UVWX6789YZAB1234','2024-05-21 13:45:25.234000',1),(96,'laylaroberts_1k2j3','$2a$10$YIVlJ3IhP.cMlTxY83h.iO0OvF54QUJ.YKS2wbyoTIYhKsszJiEnq','laylaroberts_1k2j3@gmail.com','Layla Roberts2','Ms','ROLE_USER','/uploads/avatar/a3zl.jpg',NULL,'2024-05-05 11:50:30.987000',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-06 19:36:54
