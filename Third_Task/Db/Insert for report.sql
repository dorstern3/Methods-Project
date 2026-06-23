DELIMITER $$

CREATE PROCEDURE PopulateFlexibleOrders()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_order_date DATE;
    DECLARE v_visitors INT;
    DECLARE v_type VARCHAR(20);
    DECLARE v_park VARCHAR(50);
    DECLARE v_status VARCHAR(30);
    DECLARE v_entry_time TIME;
    DECLARE v_exit_time TIME;
    DECLARE v_hour INT;
    DECLARE v_minute VARCHAR(2);
    DECLARE v_id INT;
    DECLARE v_rand_hour INT;
    DECLARE v_rand_status INT;
    
    WHILE i <= 700 DO
        SET v_park = ELT(FLOOR(1 + RAND() * 4), 'Banias', 'Caesarea', 'Ein Gedi', 'Masada');
        SET v_id = FLOOR(10000 + RAND() * 89999);
        
        SET v_type = ELT(FLOOR(1 + RAND() * 3), 'Regular', 'Group', 'Subscriber');
        IF v_type = 'Regular' THEN
            SET v_visitors = 1;
        ELSEIF v_type = 'Group' THEN
            SET v_visitors = FLOOR(6 + RAND() * 10); 
        ELSE
            SET v_visitors = FLOOR(2 + RAND() * 7);  
        END IF;

        SET v_order_date = DATE_ADD('2026-06-01', INTERVAL FLOOR(RAND() * 32) DAY);

        IF v_order_date < '2026-06-22' THEN
            SET v_rand_status = FLOOR(1 + RAND() * 4);
            IF v_rand_status <= 2 THEN SET v_status = 'Entered';
            ELSEIF v_rand_status = 3 THEN SET v_status = 'Confirmed'; -- No-Show בעבר
            ELSE SET v_status = 'Canceled';
            END IF;
        ELSE
            SET v_status = 'On waiting list';
        END IF;
        IF v_status = 'On waiting list' THEN
            SET v_order_date = DATE_ADD('2026-06-29', INTERVAL FLOOR(RAND() * 4) DAY);
        END IF;
        SET v_rand_hour = FLOOR(1 + RAND() * 10);
        IF v_rand_hour <= 4 THEN
            SET v_hour = FLOOR(08 + RAND() * 3);  
        ELSEIF v_rand_hour <= 7 THEN
            SET v_hour = FLOOR(11 + RAND() * 3);  
        ELSE
            SET v_hour = FLOOR(14 + RAND() * 4); 
        END IF;
        
        SET v_minute = ELT(FLOOR(1 + RAND() * 4), '00', '15', '30', '45');
        SET v_entry_time = CAST(CONCAT(LPAD(v_hour, 2, '0'), ':', v_minute, ':00') AS TIME);
        
        IF v_status = 'Entered' THEN
            SET v_exit_time = DATE_ADD(v_entry_time, INTERVAL FLOOR(2 + RAND() * 3) HOUR);
            IF v_exit_time > '19:00:00' THEN SET v_exit_time = '19:00:00'; END IF;
        ELSE
            SET v_exit_time = NULL;
        END IF;
        
        INSERT INTO `order` (
            `order_date`, `number_of_visitors`, `QR_code`, `id`, 
            `date_of_placing_order`, `entry_time`, `exit_time`, `status`, `type_of_visitor`, 
            `park_name`, `email`, `phone_number`
        ) VALUES (
            v_order_date, 
            v_visitors, 
            CONCAT('QR-', FLOOR(3630 + i)), 
            v_id, 
            DATE_SUB(v_order_date, INTERVAL FLOOR(1 + RAND() * 14) DAY),
            v_entry_time, 
            v_exit_time,
            v_status, 
            v_type, 
            v_park,
            CONCAT('user', v_id, '@gmail.com'),
            CONCAT('050-', FLOOR(1000000 + RAND() * 8999999))
        );
        
        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

CALL PopulateFlexibleOrders();

DROP PROCEDURE PopulateFlexibleOrders;