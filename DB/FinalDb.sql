CREATE DATABASE  IF NOT EXISTS `gonature_db_new` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gonature_db_new`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: gonature_db_new
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `guide`
--

DROP TABLE IF EXISTS `guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide` (
  `guide_id` int NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  PRIMARY KEY (`guide_id`),
  CONSTRAINT `chk_guide_id` CHECK (((`guide_id` >= 10000) and (`guide_id` <= 99999)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide`
--

LOCK TABLES `guide` WRITE;
/*!40000 ALTER TABLE `guide` DISABLE KEYS */;
INSERT INTO `guide` VALUES (90001,'Avi','Ronen','avi.guide@gmail.com','050-1111111'),(90002,'Gal','Tal','gal.guide@gmail.com','052-2222222');
/*!40000 ALTER TABLE `guide` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_number` int NOT NULL AUTO_INCREMENT,
  `order_date` date NOT NULL,
  `number_of_visitors` int NOT NULL,
  `QR_code` varchar(50) DEFAULT NULL,
  `id` int NOT NULL,
  `date_of_placing_order` date DEFAULT NULL,
  `entry_time` time NOT NULL,
  `exit_time` time DEFAULT NULL,
  `status` enum('Booked','Confirmed','Canceled','On waiting list','Pending confirmation','Entered','Waiting list unconfirmed') NOT NULL DEFAULT 'Booked',
  `type_of_visitor` enum('Regular','Group','Subscriber') NOT NULL,
  `park_name` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_number`),
  KEY `fk_order_park` (`park_name`),
  CONSTRAINT `fk_order_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_order_id` CHECK (((`id` >= 10000) and (`id` <= 99999)))
) ENGINE=InnoDB AUTO_INCREMENT=3526 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (3520,'2026-07-05',1,'QR-3520',55555,'2026-06-15','10:00:00',NULL,'Booked','Regular','Banias',NULL,NULL),(3521,'2026-06-18',4,'QR-3521',11111,'2026-06-14','12:30:00',NULL,'Pending confirmation','Subscriber','Caesarea',NULL,NULL),(3522,'2026-06-25',1,'QR-3522',22222,'2026-06-12','14:00:00',NULL,'Confirmed','Subscriber','Ein Gedi',NULL,NULL),(3523,'2026-06-17',15,'QR-3523',90001,'2026-06-10','09:00:00',NULL,'Entered','Group','Masada',NULL,NULL),(3524,'2026-06-20',3,'QR-3525',33333,'2026-06-16','10:00:00',NULL,'On waiting list','Subscriber','Achziv',NULL,NULL),(3525,'2026-06-21',1,NULL,11111,'2026-06-21','16:41:37',NULL,'Entered','Subscriber','Masada',NULL,NULL);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parameter_requests`
--

DROP TABLE IF EXISTS `parameter_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parameter_requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `park_name` varchar(50) NOT NULL,
  `worker_id` int NOT NULL,
  `parameter_name` varchar(50) NOT NULL,
  `current_value` int NOT NULL,
  `request_value` int NOT NULL,
  `status` enum('Pending','Approved','Declined') NOT NULL DEFAULT 'Pending',
  `request_date` date NOT NULL,
  PRIMARY KEY (`request_id`),
  KEY `fk_param_park` (`park_name`),
  KEY `fk_param_worker` (`worker_id`),
  CONSTRAINT `fk_param_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_param_worker` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parameter_requests`
--

LOCK TABLES `parameter_requests` WRITE;
/*!40000 ALTER TABLE `parameter_requests` DISABLE KEYS */;
INSERT INTO `parameter_requests` VALUES (1,'Banias',1,'max_capacity',500,550,'Pending','2026-06-16'),(2,'Caesarea',1,'estimated_staying_time',4,3,'Approved','2026-06-10');
/*!40000 ALTER TABLE `parameter_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parks`
--

DROP TABLE IF EXISTS `parks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parks` (
  `park_name` varchar(50) NOT NULL,
  `max_capacity` int NOT NULL,
  `casual_gap` int NOT NULL,
  `current_occupancy` int NOT NULL,
  `full_price` float NOT NULL,
  `additonal_discount` float NOT NULL DEFAULT '0',
  `estimated_staying_time` int NOT NULL,
  PRIMARY KEY (`park_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parks`
--

LOCK TABLES `parks` WRITE;
/*!40000 ALTER TABLE `parks` DISABLE KEYS */;
INSERT INTO `parks` VALUES ('Achziv',10,2,0,35,0,4),('Banias',500,50,120,39,0,4),('Caesarea',1000,100,450,45,0.1,4),('Ein Gedi',600,60,200,28,0.15,4),('Masada',800,80,301,50,0,4);
/*!40000 ALTER TABLE `parks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriber` (
  `id` int NOT NULL,
  `fname` varchar(45) NOT NULL,
  `lname` varchar(45) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  `credit_card_number` varchar(50) DEFAULT NULL,
  `family_members` int NOT NULL,
  `sub_number` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_sub_number` (`sub_number`),
  CONSTRAINT `chk_sub_id` CHECK (((`id` >= 10000) and (`id` <= 99999))),
  CONSTRAINT `chk_sub_num` CHECK (((`sub_number` >= 1000) and (`sub_number` <= 9999)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (11111,'Yossi','Cohen','yossi.cohen@gmail.com','050-1234567','1234-5678-9012-3456',4,1001),(22222,'Dana','Levi','dana.levi@gmail.com','052-7654321','9876-5432-1098-7654',1,1002),(33333,'Ron','Shani','ron.shani@gmail.com','054-1112223',NULL,3,1003);
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workers`
--

DROP TABLE IF EXISTS `workers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workers` (
  `worker_id` int NOT NULL AUTO_INCREMENT,
  `hash_password` varchar(50) NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `park_name` varchar(50) NOT NULL,
  `role` enum('Dept_manager','Park_manager','Customer_service','Entrance_emp') NOT NULL,
  PRIMARY KEY (`worker_id`),
  KEY `fk_worker_park` (`park_name`),
  CONSTRAINT `fk_worker_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workers`
--

LOCK TABLES `workers` WRITE;
/*!40000 ALTER TABLE `workers` DISABLE KEYS */;
INSERT INTO `workers` VALUES (1,'pass123','David','Meir','david@gonature.gov.il','Banias','Park_manager'),(2,'pass456','Sarah','Ashkenazi','sarah@gonature.gov.il','Caesarea','Dept_manager'),(3,'pass789','Idan','Kaufman','idan@gonature.gov.il','Masada','Entrance_emp'),(4,'pass000','Aaron','Green','AaronGreen@gmail.com','Achziv','Entrance_emp'),(5,'pass111','Bob','Blue','AaronGreen@gmail.com','Ein Gedi','Customer_service');
/*!40000 ALTER TABLE `workers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-21 16:44:52
