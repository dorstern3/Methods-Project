INSERT INTO `parks` VALUES 
('Achziv', 10, 2, 0, 35, 0, 4),
('Banias', 500, 50, 120, 39, 0, 4),
('Caesarea', 1000, 100, 450, 45, 0.1, 4),
('Ein Gedi', 600, 60, 200, 28, 0.15, 4),
('Masada', 800, 80, 300, 50, 0, 4);

INSERT INTO `subscriber` VALUES 
(11111, 'Yossi', 'Cohen', 'yossi.cohen@gmail.com', '050-1234567', '1234-5678-9012-3456', 4, 1001),
(22222, 'Dana', 'Levi', 'dana.levi@gmail.com', '052-7654321', '9876-5432-1098-7654', 1, 1002),
(33333, 'Ron', 'Shani', 'ron.shani@gmail.com', '054-1112223', NULL, 3, 1003);

INSERT INTO `guide` VALUES 
(90001, 'Avi', 'Ronen', 'avi.guide@gmail.com', '050-1111111'),
(90002, 'Gal', 'Tal', 'gal.guide@gmail.com', '052-2222222');

INSERT INTO `workers` (`hash_password`, `fname`, `lname`, `email`, `park_name`, `role`) VALUES 
('pass123', 'David', 'Meir', 'david@gonature.gov.il', 'Banias', 'Park_manager'),
('pass456', 'Sarah', 'Ashkenazi', 'sarah@gonature.gov.il', 'Caesarea', 'Dept_manager'),
('pass789', 'Idan', 'Kaufman', 'idan@gonature.gov.il', 'Masada', 'Entrance_emp'),
('pass000', 'Aaron', 'Green', 'AaronGreen@gmail.com', 'Achziv', 'Entrance_emp'),
('pass111', 'Bob', 'Blue', 'AaronGreen@gmail.com', 'Ein Gedi', 'Customer_service');

INSERT INTO `order` (`order_date`, `number_of_visitors`, `QR_code`, `id`, `date_of_placing_order`, `entry_time`, `status`, `type_of_visitor`, `park_name`) VALUES 
('2026-07-05', 1, 'QR-3520', 55555, '2026-06-15', '10:00:00', 'Booked', 'Regular', 'Banias'),
('2026-06-18', 4, 'QR-3521', 11111, '2026-06-14', '12:30:00', 'Pending confirmation', 'Subscriber', 'Caesarea'),
('2026-06-25', 1, 'QR-3522', 22222, '2026-06-12', '14:00:00', 'Confirmed', 'Subscriber', 'Ein Gedi'),
('2026-06-17', 15, 'QR-3523', 90001, '2026-06-10', '09:00:00', 'Entered', 'Group', 'Masada'),
('2026-06-20', 3, 'QR-3525', 33333, '2026-06-16', '10:00:00', 'On waiting list', 'Subscriber', 'Achziv');

INSERT INTO `parameter_requests` (`park_name`, `worker_id`, `parameter_name`, `current_value`, `request_value`, `status`, `request_date`) VALUES 
('Banias', 1, 'max_capacity', 500, 550, 'Pending', '2026-06-16'),
('Caesarea', 1, 'estimated_staying_time', 4, 3, 'Approved', '2026-06-10');