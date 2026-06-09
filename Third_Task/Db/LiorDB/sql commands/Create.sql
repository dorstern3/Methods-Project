-- create GoNature DB
DROP SCHEMA IF EXISTS `gonature_db_new`;
CREATE SCHEMA `gonature_db_new`;
USE `gonature_db_new`;

-- create table park
CREATE TABLE `Parks`(
    `park_name` VARCHAR(50) NOT NULL ,
    `max_capacity` INT NOT NULL ,
    `casual_gap` INT NOT NULL ,
    `current_occupancy` INT NOT NULL ,
    `full_price` FLOAT NOT NULL ,
    `additonal_discount` FLOAT not null default 0,    
    `estimated_staying_time` INT NOT NULL,
    PRIMARY KEY (`park_name`)
);

-- create table Subscriber
CREATE TABLE `Subscriber` (
  `id` int NOT NULL,
  `fname` VARCHAR(45) NOT NULL,
  `lname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `phone_number` VARCHAR(50) NOT NULL,
  `credit_card_number` VARCHAR(50),
  `family_members` int NOT NULL,             -- family min 1 because it includes himself 
  `sub_number` int NOT NULL,
  
  PRIMARY KEY (`id`)
);

-- create table Order
CREATE TABLE `Order` (
  `order_number` INT NOT NULL AUTO_INCREMENT,  -- .1 order_number (int PK)
  `order_date` DATE NOT NULL,                  -- .2 order_date (Date)
  `number_of_visitors` INT NOT NULL,           -- .3 number_of_visitors (int)
  `QR_code` VARCHAR(50),                       -- CHANGED TO VARCHAR TO SUPPORT 'QR-1234'
  `id` int,                                   
  `date_of_placing_order` DATE ,               -- .6 date_of_placing_order (Date)
  `entry_time` TIME NOT NULL,                  -- e.g "15:00:00"
  `exit_time` TIME,
  `status` ENUM('Confirmed' , 'Canceled' , 'On waiting list' , 'Pending confirmation' , 'Entered','Waiting list unconfirmed') DEFAULT 'Pending confirmation' NOT NULL,
  `type_of_visitor` ENUM('Regular' , 'Group' , 'Subscriber') NOT NULL,
  `park_name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(50),
  `phone_number` VARCHAR(50),
  
  PRIMARY KEY (`order_number`),
    
 -- foreign key to parks
  CONSTRAINT `fk_park_name`
    FOREIGN KEY (`park_name`)
    REFERENCES `Parks`(`park_name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) AUTO_INCREMENT = 3520;

-- create table Guide
CREATE TABLE `Guide`(
    `guide_id` INT NOT NULL,
    `fname` VARCHAR(50) NOT NULL,
    `lname` VARCHAR(50) NOT NULL,
    `email` VARCHAR(50) NOT NULL,
    `phone_number`VARCHAR(50) NOT NULL,
    PRIMARY KEY (`guide_id`)
);

-- create table workers
CREATE TABLE `Workers`(
    `worker_id` int not null AUTO_INCREMENT,
    `hash_password` varchar(50) not null,
    `fname` VARCHAR(50) NOT NULL,
    `lname` VARCHAR(50) NOT NULL,
    `email` VARCHAR(50) NOT NULL,
    `park_name` varchar(50) not null,
    `role` enum('Dept_manager','Park_manager','Customer_service' ,'Entrance_emp') not null,
    
  PRIMARY KEY (`worker_id`),
  
  -- foreign key to parks
  CONSTRAINT `fk_worker_to_park_name`
    FOREIGN KEY (`park_name`)
    REFERENCES `Parks` (`park_name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- create table parameter_requests
CREATE TABLE `parameter_requests`(
    `request_id` INT NOT NULL AUTO_INCREMENT,
    `park_name` VARCHAR(50) NOT NULL,
    `worker_id` INT NOT NULL,
    `parameter_name` VARCHAR(50) NOT NULL,
    `current_value` INT NOT NULL,
    `request_value` INT NOT NULL,    
    `status` ENUM('Pending', 'Approved', 'Declined') NOT NULL DEFAULT 'Pending',
    `request_date` DATE NOT NULL,
    PRIMARY KEY (`request_id`),
    CONSTRAINT `fk_param_park_name` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_param_worker_id` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
);