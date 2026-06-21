USE gonature_db_new;

-- 1. נוודא שהפארקים קיימים במערכת כדי שהמפתח הזר לא יקרוס
INSERT IGNORE INTO parks (park_name, max_capacity, casual_gap, current_occupancy, full_price, additonal_discount, estimated_staying_time) VALUES 
('Achziv', 500, 50, 0, 50.0, 0.0, 4),
('Banias', 400, 40, 0, 50.0, 0.0, 4);

-- 2. הכנסת 2 הזמנות בסטטוס Booked למחר (אמורות לקבל הודעת תזכורת ולהפוך ל-Pending)
INSERT INTO `order` (order_date, number_of_visitors, QR_code, id, date_of_placing_order, entry_time, status, type_of_visitor, park_name, email, phone_number)
VALUES 
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 3, 'QR-9901', 111111111, CURDATE(), '10:00:00', 'Booked', 'Regular', 'Achziv', 'booked1@test.com', '0501111111'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 5, 'QR-9902', 222222222, CURDATE(), '11:00:00', 'Booked', 'Subscriber', 'Banias', 'booked2@test.com', '0502222222');

-- 3. הכנסת 2 הזמנות בסטטוס Waiting list unconfirmed למחר (אמורות להתבטל בסימולציית 1-hour timeout)
INSERT INTO `order` (order_date, number_of_visitors, QR_code, id, date_of_placing_order, entry_time, status, type_of_visitor, park_name, email, phone_number)
VALUES 
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 2, NULL, 333333333, CURDATE(), '10:00:00', 'Waiting list unconfirmed', 'Regular', 'Achziv', 'wait1@test.com', '0503333333'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 4, NULL, 444444444, CURDATE(), '11:00:00', 'Waiting list unconfirmed', 'Group', 'Banias', 'wait2@test.com', '0504444444');

-- 4. הכנסת 2 הזמנות בסטטוס Pending confirmation למחר (אמורות להתבטל בסימולציית 2-hour timeout)
INSERT INTO `order` (order_date, number_of_visitors, QR_code, id, date_of_placing_order, entry_time, status, type_of_visitor, park_name, email, phone_number)
VALUES 
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 6, 'QR-9905', 555555555, CURDATE(), '10:00:00', 'Pending confirmation', 'Regular', 'Achziv', 'pend1@test.com', '0505555555'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 12, 'QR-9906', 666666666, CURDATE(), '11:00:00', 'Pending confirmation', 'Group', 'Banias', 'pend2@test.com', '0506666666');

-- 5. בונוס: הכנסת 2 הזמנות ברשימת המתנה רגילה לאותן שעות, כדי שתוכלי לראות אותן "קופצות" כשפארק מתפנה עקב ביטול!
INSERT INTO `order` (order_date, number_of_visitors, QR_code, id, date_of_placing_order, entry_time, status, type_of_visitor, park_name, email, phone_number)
VALUES 
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 2, NULL, 777777777, CURDATE(), '10:00:00', 'On waiting list', 'Regular', 'Achziv', 'in_line1@test.com', '0507777777'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 4, NULL, 888888888, CURDATE(), '11:00:00', 'On waiting list', 'Subscriber', 'Banias', 'in_line2@test.com', '0508888888');