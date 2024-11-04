-- read the readme.txt



CREATE TABLE tickets (

                         id VARCHAR(255) PRIMARY KEY,
                         flight_id VARCHAR(255),
                         start_date DATE,
                         end_date DATE,
                         price INT



);




DELIMITER //


CREATE TRIGGER encrypt_dates_before_insert
    BEFORE INSERT ON tickets
    FOR EACH ROW
BEGIN
    SET NEW.start_date = AES_ENCRYPT(NEW.start_date, 'your_secret_key');
    SET NEW.end_date = AES_ENCRYPT(NEW.end_date, 'your_secret_key');
END //





DELIMITER ;




DELIMITER //


CREATE PROCEDURE get_ticket_decrypted(IN ticketId VARCHAR(255))
BEGIN
SELECT
    id,
    flight_id,
    AES_DECRYPT(start_date, 'your_secret_key') AS start_date,
    AES_DECRYPT(end_date, 'your_secret_key') AS end_date,
    price
FROM tickets
WHERE id = ticketId;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE insert_ticket(
    IN p_flight_id VARCHAR(255),
    IN p_start_date DATE,IN p_end_date DATE,IN p_price INT)
BEGIN
INSERT INTO tickets (flight_id, start_date, end_date, price)

VALUES (p_flight_id,
        AES_ENCRYPT(p_start_date, 'your_secret_key'),
        AES_ENCRYPT(p_end_date, 'your_secret_key'),
        p_price);
END //


DELIMITER ;




DELIMITER //
CREATE PROCEDURE update_ticket(
    IN p_id INT,
    IN p_flight_id VARCHAR(255),
    IN p_start_date DATE,
    IN p_end_date DATE,IN p_price INT)
BEGIN
UPDATE tickets
SET flight_id = p_flight_id,
    start_date = AES_ENCRYPT(p_start_date, 'your_secret_key'),
    end_date = AES_ENCRYPT(p_end_date, 'your_secret_key'),
    price = p_price
WHERE id = p_id;
END //

DELIMITER ;




DELIMITER //

CREATE PROCEDURE search_tickets(
    IN p_flight_id VARCHAR(255),
    IN p_start_date DATE,IN p_end_date DATE)
BEGIN
SELECT
    id,flight_id,AES_DECRYPT(start_date, 'your_secret_key') AS start_date,AES_DECRYPT(end_date, 'your_secret_key') AS end_date,price
FROM tickets
WHERE (flight_id = p_flight_id OR p_flight_id IS NULL)
  AND (AES_DECRYPT(start_date, 'your_secret_key') >= p_start_date OR p_start_date IS NULL)
  AND (AES_DECRYPT(end_date, 'your_secret_key') <= p_end_date OR p_end_date IS NULL);
END //


DELIMITER ;




DELIMITER //




CREATE PROCEDURE delete_ticket(IN p_id INT)
BEGIN

DELETE FROM tickets WHERE id = p_id;

END //




DELIMITER ;




