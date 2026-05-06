-- create GoNature DB 
DROP SCHEMA IF EXISTS `gonature_db`;
CREATE SCHEMA `gonature_db`;
USE `gonature_db`;

-- Fk for Subscriber (Subscriber_id)
CREATE TABLE `Subscriber` (
  `subscriber_id` INT NOT NULL,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  PRIMARY KEY (`subscriber_id`)
);

-- create table Order
CREATE TABLE `Order` (
  `order_number` INT NOT NULL AUTO_INCREMENT,  -- .1 order_number (int PK)
  `order_date` DATE NOT NULL,                  -- .2 order_date (Date)
  `number_of_visitors` INT NOT NULL,           -- .3 number_of_visitors (int)
  `confirmation_code` INT NOT NULL,            -- .4 confirmation_code (int)
  `subscriber_id` INT NOT NULL,                -- .5 subscriber_id (int FK)
  `date_of_placing_order` DATE NOT NULL,       -- .6 date_of_placing_order (Date)
  PRIMARY KEY (`order_number`),
  CONSTRAINT `fk_subscriber_id`
    FOREIGN KEY (`subscriber_id`)
    REFERENCES `Subscriber` (`subscriber_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Test for GUI

-- first subscriber
INSERT INTO `Subscriber` (subscriber_id, first_name, last_name) 
VALUES (123, 'Dor', 'Engineer');

-- connect order1 to subscriber
INSERT INTO `Order` (order_date, number_of_visitors, confirmation_code, subscriber_id, date_of_placing_order) 
VALUES ('2026-05-20', 4, 555, 123, '2026-04-22');

-- connect order2 to subscriber
INSERT INTO `Order` (order_date, number_of_visitors, confirmation_code, subscriber_id, date_of_placing_order) 
VALUES ('2026-06-12', 2, 777, 123, '2026-04-22');