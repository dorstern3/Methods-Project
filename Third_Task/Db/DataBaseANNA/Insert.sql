USE `gonature_db_new`;

-- ------------------------------------------------------
-- ------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `order`;
TRUNCATE TABLE `parameter_requests`;
TRUNCATE TABLE `workers`;
TRUNCATE TABLE `guide`;
TRUNCATE TABLE `subscriber`;
TRUNCATE TABLE `parks`;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------
-- 1. Populating Parks 
-- ------------------------------------------------------
INSERT INTO `parks` (`park_name`, `max_capacity`, `casual_gap`, `current_occupancy`, `full_price`, `additonal_discount`, `estimated_staying_time`) VALUES 
('Achziv', 10, 2, 0, 35, 0, 4),
('Banias', 500, 50, 120, 39, 0, 4),
('Caesarea', 1000, 100, 450, 45, 0.1, 4),
('Ein Gedi', 600, 60, 200, 28, 0.15, 4),
('Masada', 800, 80, 300, 50, 0, 4);

-- ------------------------------------------------------
-- 2. Populating Subscribers
-- ------------------------------------------------------
INSERT INTO `subscriber` (`id`, `fname`, `lname`, `email`, `phone_number`, `credit_card_number`, `family_members`, `sub_number`) VALUES 
(111111111, 'Yossi', 'Cohen', 'yossi.cohen@gmail.com', '050-1234567', '1234-5678-9012-3456', 4, 1001),
(222222222, 'Dana', 'Levi', 'dana.levi@gmail.com', '052-7654321', '9876-5432-1098-7654', 1, 1002),
(333333333, 'Ron', 'Shani', 'ron.shani@gmail.com', '054-1112223', NULL, 3, 1003);

-- ------------------------------------------------------
-- 3. Populating Guides
-- ------------------------------------------------------
INSERT INTO `guide` (`guide_id`, `fname`, `lname`, `email`, `phone_number`) VALUES 
(90001, 'Avi', 'Ronen', 'avi.guide@gmail.com', '050-1111111'),
(90002, 'Gal', 'Tal', 'gal.guide@gmail.com', '052-2222222');

-- ------------------------------------------------------
-- 4. Populating Workers
-- ------------------------------------------------------
INSERT INTO `workers` (`hash_password`, `fname`, `lname`, `email`, `park_name`, `role`) VALUES 
('pass123', 'David', 'Meir', 'david@gonature.gov.il', 'Banias', 'Park_manager'),
('pass456', 'Sarah', 'Ashkenazi', 'sarah@gonature.gov.il', 'Caesarea', 'Dept_manager'),
('pass789', 'Idan', 'Kaufman', 'idan@gonature.gov.il', 'Masada', 'Entrance_emp');

-- ------------------------------------------------------
-- 5. Populating Orders (System Orders)
-- ------------------------------------------------------
INSERT INTO `order` 
(`order_number`, `order_date`, `number_of_visitors`, `QR_code`, `id`, `date_of_placing_order`, `entry_time`, `exit_time`, `status`, `type_of_visitor`, `park_name`, `email`, `phone_number`) 
VALUES 
-- Order 3520: Future order (Booked default state)
(3520, '2026-07-05', 1, 'QR-3520', 555555555, '2026-06-15', '10:00:00', NULL, 'Booked', 'Regular', 'Banias', 'regular_visitor@gmail.com', '050-8888881'),

-- Order 3521: Tomorrow's order (Pending confirmation for email alert simulation)
(3521, '2026-06-18', 4, 'QR-3521', 111111111, '2026-06-14', '12:30:00', NULL, 'Pending confirmation', 'Subscriber', 'Caesarea', 'yossi.cohen@gmail.com', '050-1234567'),

-- Order 3522: Confirmed order ready for park gate entry
(3522, '2026-06-25', 1, 'QR-3522', 222222222, '2026-06-12', '14:00:00', NULL, 'Confirmed', 'Subscriber', 'Ein Gedi', 'dana.levi@gmail.com', '052-7654321'),

-- Order 3523: Group inside the park (Entered state)
(3523, '2026-06-17', 15, 'QR-3523', 90001, '2026-06-10', '09:00:00', NULL, 'Entered', 'Group', 'Masada', 'avi.guide@gmail.com', '050-1111111'),

-- Order 3524: Canceled order record
(3524, '2026-06-16', 1, 'QR-3524', 777777777, '2026-06-11', '11:00:00', NULL, 'Canceled', 'Regular', 'Banias', 'canceled_user@gmail.com', '054-9999999'),

-- Order 3525: Waiting list test target at Achziv (Capacity = 10 baseline trigger)
(3525, '2026-06-20', 3, 'QR-3525', 333333333, '2026-06-16', '10:00:00', NULL, 'On waiting list', 'Subscriber', 'Achziv', 'ron.shani@gmail.com', '054-1112223');

-- ------------------------------------------------------
-- 6. Populating Parameter Requests
-- ------------------------------------------------------
INSERT INTO `parameter_requests` (`request_id`, `park_name`, `worker_id`, `parameter_name`, `current_value`, `request_value`, `status`, `request_date`) VALUES 
(1, 'Banias', 1, 'max_capacity', 500, 550, 'Pending', '2026-06-16'),
(2, 'Caesarea', 1, 'estimated_staying_time', 4, 3, 'Approved', '2026-06-10');