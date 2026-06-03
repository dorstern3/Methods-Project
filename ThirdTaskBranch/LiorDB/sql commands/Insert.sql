INSERT INTO `Parks` (`park_name`, `max_capacity`, `casual_gap`, `current_occupancy`, `full_price`, `additonal_discount`) VALUES
('Banias', 500, 50, 120, 39.0, 0.0),
('Caesarea', 1000, 100, 450, 45.0, 0.1),
('Masada', 800, 80, 300, 50.0, 0.0),
('Ein Gedi', 600, 60, 200, 28.0, 0.15),
('Achziv', 400, 40, 50, 35.0, 0.0);

INSERT INTO `Subscriber` (`id`, `fname`, `lname`, `email`, `phone_number`, `credit_card_number`, `family_members`, `sub_number`) VALUES
(101, 'Yossi', 'Cohen', 'yossi@gmail.com', '050-1234567', '1234-5678-9012-3456', 4, 1001),
(102, 'Dana', 'Levi', 'dana@gmail.com', '052-7654321', '9876-5432-1098-7654', 1, 1002),
(103, 'Ron', 'Shani', 'ron@gmail.com', '054-1112223', NULL, 3, 1003),
(104, 'Michal', 'Avraham', 'michal@gmail.com', '053-4445556', '5555-6666-7777-8888', 5, 1004),
(105, 'Amit', 'Perez', 'amit@gmail.com', '058-9998887', NULL, 2, 1005);

INSERT INTO `Guide` (`fname`, `lname`, `email`, `phone_number`) VALUES
('Avi', 'Ronen', 'avi.guide@gmail.com', '050-1111111'),
('Gal', 'Tal', 'gal.guide@gmail.com', '052-2222222'),
('Tom', 'Nir', 'tom.guide@gmail.com', '054-3333333'),
('Adi', 'Bar', 'adi.guide@gmail.com', '053-4444444'),
('Omer', 'Golan', 'omer.guide@gmail.com', '058-5555555');

INSERT INTO `Workers` (`hash_password`, `fname`, `lname`, `email`, `park_name`, `role`) VALUES
('pass123', 'David', 'Meir', 'david@gonature.gov.il', 'Banias', 'Park_manager'),
('pass456', 'Sarah', 'Ashkenazi', 'sarah@gonature.gov.il', 'Caesarea', 'Dept_manager'),
('pass789', 'Idan', 'Kaufman', 'idan@gonature.gov.il', 'Masada', 'Entrance_emp'),
('passabc', 'Neta', 'Givon', 'neta@gonature.gov.il', 'Ein Gedi', 'Customer_service'),
('passxyz', 'Eran', 'Mor', 'eran@gonature.gov.il', 'Achziv', 'Entrance_emp');

INSERT INTO `Order` (
`order_date`, `number_of_visitors`, `QR_code`, `id`,
 `date_of_placing_order`, `entry_time`,`exit_time`, `status`,
 `type_of_visitor`, `park_name`, `email`, `phone_number`)
 VALUES
('2026-06-05', 4, 9991, 101, '2026-06-01', '10:00:00', '13:00:00', 'Confirmed', 'Subscriber', 'Banias', 'yossi@gmail.com', '050-1234567'),
('2026-06-06', 2, NULL, NULL, '2026-06-01', '12:30:00', NULL, 'Pending confirmation', 'Regular', 'Caesarea', 'guest1@gmail.com', '055-6667778'),
('2026-06-07', 1, 9992, 102, '2026-05-30', '09:00:00', '11:00:00', 'Entered', 'Subscriber', 'Masada', 'dana@gmail.com', '052-7654321'),
('2026-06-08', 15, 9993, NULL, '2026-05-28', '14:00:00', '17:00:00', 'Confirmed', 'Group', 'Ein Gedi', 'group_leader@gmail.com', '054-8889990'),
('2026-06-10', 3, NULL, 103, '2026-06-01', '08:30:00', NULL, 'On waiting list', 'Subscriber', 'Achziv', 'ron@gmail.com', '054-1112223');