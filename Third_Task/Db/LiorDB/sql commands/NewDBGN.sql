CREATE DATABASE IF NOT EXISTS `gonature_db_new` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gonature_db_new`;

-- ------------------------------------------------------
-- Table structure for table `guide`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `guide`;
CREATE TABLE `guide` (
  `guide_id` int NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  PRIMARY KEY (`guide_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Table structure for table `parks`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `parks`;
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

-- ------------------------------------------------------
-- Table structure for table `order`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `order`;
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
  KEY `fk_park_name` (`park_name`),
  CONSTRAINT `fk_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3520 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Table structure for table `subscriber`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `subscriber`;
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

-- ------------------------------------------------------
-- Table structure for table `workers`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `workers`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Table structure for table `parameter_requests`
-- ------------------------------------------------------
DROP TABLE IF EXISTS `parameter_requests`;
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
  KEY `fk_param_park_name` (`park_name`),
  KEY `fk_param_worker_id` (`worker_id`),
  CONSTRAINT `fk_param_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_param_worker_id` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;