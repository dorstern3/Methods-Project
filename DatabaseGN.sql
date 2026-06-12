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
  PRIMARY KEY (`guide_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; 
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide`
--

LOCK TABLES `guide` WRITE;
/*!40000 ALTER TABLE `guide` DISABLE KEYS */;
INSERT INTO `guide` VALUES 
(201,'Avi','Ronen','avi.guide@gmail.com','050-1111111'),
(202,'Gal','Tal','gal.guide@gmail.com','052-2222222'),
(203,'Tom','Nir','tom.guide@gmail.com','054-3333333'),
(204,'Adi','Bar','adi.guide@gmail.com','053-4444444'),
(205,'Omer','Golan','omer.guide@gmail.com','058-5555555');
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
  `id` int DEFAULT NULL,
  `date_of_placing_order` date DEFAULT NULL,
  `entry_time` time NOT NULL,
  `exit_time` time DEFAULT NULL,
  `status` enum('Confirmed','Canceled','On waiting list','Pending confirmation','Entered','Waiting list unconfirmed') NOT NULL DEFAULT 'Pending confirmation',
  `type_of_visitor` enum('Regular','Group','Subscriber') NOT NULL,
  `park_name` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_number`),
  KEY `fk_park_name` (`park_name`),
  CONSTRAINT `fk_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3525 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (3520,'2026-06-05',1,'QR-3520',NULL,'2026-06-01','10:00:00',NULL,'Confirmed','Regular','Banias','guest1@gmail.com','050-1111111'),(3521,'2026-06-06',4,'QR-3521',101,'2026-06-01','12:30:00',NULL,'Pending confirmation','Subscriber','Caesarea','yossi@gmail.com','050-1234567'),(3522,'2026-06-07',15,'QR-3522',NULL,'2026-05-30','09:00:00','11:00:00','Entered','Group','Masada','group_leader@gmail.com','052-7654321'),(3523,'2026-06-08',1,NULL,NULL,'2026-05-28','14:00:00',NULL,'Confirmed','Regular','Ein Gedi','guest2@gmail.com','054-8889990'),(3524,'2026-06-10',3,NULL,103,'2026-06-01','08:30:00',NULL,'On waiting list','Subscriber','Achziv','ron@gmail.com','054-1112223');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
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
INSERT INTO `parks` VALUES ('Achziv',10,2,0,35,0,4),('Banias',500,50,120,39,0,4),('Caesarea',1000,100,450,45,0.1,4),('Ein Gedi',600,60,200,28,0.15,4),('Masada',800,80,300,50,0,4);
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (101,'Yossi','Cohen','yossi@gmail.com','050-1234567','1234-5678-9012-3456',4,1001),(102,'Dana','Levi','dana@gmail.com','052-7654321','9876-5432-1098-7654',1,1002),(103,'Ron','Shani','ron@gmail.com','054-1112223',NULL,3,1003),(104,'Michal','Avraham','michal@gmail.com','053-4445556','5555-6666-7777-8888',5,1004),(105,'Amit','Perez','amit@gmail.com','058-9998887',NULL,2,1005);
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
  KEY `fk_worker_to_park_name` (`park_name`),
  CONSTRAINT `fk_worker_to_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workers`
--

LOCK TABLES `workers` WRITE;
/*!40000 ALTER TABLE `workers` DISABLE KEYS */;
INSERT INTO `workers` VALUES (1,'pass123','David','Meir','david@gonature.gov.il','Banias','Park_manager'),(2,'pass456','Sarah','Ashkenazi','sarah@gonature.gov.il','Caesarea','Dept_manager'),(3,'pass789','Idan','Kaufman','idan@gonature.gov.il','Masada','Entrance_emp'),(4,'passabc','Neta','Givon','neta@gonature.gov.il','Ein Gedi','Customer_service'),(5,'passxyz','Eran','Mor','eran@gonature.gov.il','Achziv','Entrance_emp');
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

-- Dump completed on 2026-06-05 19:20:30

-- create table parameter_requests
CREATE TABLE `parameter_requests`(
    `request_id` INT NOT NULL AUTO_INCREMENT,
    `park_name` VARCHAR(50) NOT NULL,
    `worker_id` INT NOT NULL,
    `parameter_name` VARCHAR(50) NOT NULL,
    `current_value` INT NOT NULL,
    `request_value` INT NOT NULL,    
    `status` ENUM('Pending', 'Approved', 'Declined','Rejected') NOT NULL DEFAULT 'Pending',
    `request_date` DATE NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (`request_id`),
    CONSTRAINT `fk_param_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_param_worker_id` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
);
