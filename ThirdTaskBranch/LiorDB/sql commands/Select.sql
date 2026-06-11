SELECT * FROM gonature_db_new.parks;
SELECT * FROM gonature_db_new.subscriber;
SELECT * FROM gonature_db_new.guide;
SELECT * FROM gonature_db_new.workers;
SELECT * FROM gonature_db_new.`order`;

INSERT INTO gonature_db_new.`order` 
(`order_date`, `number_of_visitors`, `QR_code`, `id`, `date_of_placing_order`, `entry_time`, `exit_time`, `status`, `type_of_visitor`, `park_name`, `email`, `phone_number`) 
VALUES 
('2026-06-03', 2, 'QR-3527', NULL, '2026-06-03', '20:15:00', NULL, 'Confirmed', 'Regular', 'Banias', 'test.test@gmail.com', '050-9876543');