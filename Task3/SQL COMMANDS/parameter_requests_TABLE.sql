USE `gonature_db_new`;

CREATE TABLE `parameter_requests` (
  `request_id` INT NOT NULL AUTO_INCREMENT,
  `park_name` VARCHAR(50) NOT NULL,
  `worker_id` INT NOT NULL, -- מזהה העובד (המנהל) שביקש
  `parameter_name` ENUM('max_capacity', 'casual_gap', 'estimated_stay_time') NOT NULL,
  `current_value` INT NOT NULL,
  `requested_value` INT NOT NULL,
  `status` ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
  `request_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  CONSTRAINT `fk_req_to_park` FOREIGN KEY (`park_name`) REFERENCES `parks` (`park_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_req_to_worker` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;