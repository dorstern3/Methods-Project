-- =============================================
-- 1. DATABASE SETUP
-- =============================================
CREATE DATABASE IF NOT EXISTS `gonature_db_new`;
USE `gonature_db_new`;

SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables to ensure a clean start
DROP TABLE IF EXISTS `parameter_requests`;
DROP TABLE IF EXISTS `workers`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `subscriber`;
DROP TABLE IF EXISTS `guide`;
DROP TABLE IF EXISTS `parks`;

-- =============================================
-- 2. CREATE TABLES
-- =============================================

CREATE TABLE `parks` (
  `park_name` varchar(50) NOT NULL,
  `max_capacity` int NOT NULL,
  `casual_gap` int NOT NULL,
  `current_occupancy` int NOT NULL,
  `full_price` float NOT NULL,
  `additonal_discount` float NOT NULL DEFAULT '0',
  `estimated_staying_time` int NOT NULL,
  PRIMARY KEY (`park_name`)
);

CREATE TABLE `guide` (
  `guide_id` int NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  PRIMARY KEY (`guide_id`),
  CONSTRAINT `chk_guide_id` CHECK (`guide_id` >= 10000 AND `guide_id` <= 99999)
);

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
  CONSTRAINT `chk_sub_id` CHECK (`id` >= 10000 AND `id` <= 99999),
  CONSTRAINT `chk_sub_num` CHECK (`sub_number` >= 1000 AND `sub_number` <= 9999)
);

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
  CONSTRAINT `fk_order_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_order_id` CHECK (`id` >= 10000 AND `id` <= 99999)
) AUTO_INCREMENT = 3520;

CREATE TABLE `workers` (
  `worker_id` int NOT NULL AUTO_INCREMENT,
  `hash_password` varchar(50) NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `park_name` varchar(50) NOT NULL,
  `role` enum('Dept_manager','Park_manager','Customer_service','Entrance_emp') NOT NULL,
  PRIMARY KEY (`worker_id`),
  CONSTRAINT `fk_worker_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `parameter_requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `park_name` varchar(50) NOT NULL,
  `worker_id` int NOT NULL,
  `parameter_name` varchar(50) NOT NULL,
  `current_value` int NOT NULL,
  `request_value` int NOT NULL,
  `status` enum('Pending','Approved','Declined') NOT NULL DEFAULT 'Pending',
  `request_date` date NOT NULL default (current_date),
  PRIMARY KEY (`request_id`),
  CONSTRAINT `fk_param_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_param_worker` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
