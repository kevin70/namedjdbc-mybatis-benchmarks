-- init data
DROP PROCEDURE IF EXISTS init_data;
DELIMITER //
CREATE PROCEDURE init_data()
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE (i <= 1000000) DO
      INSERT INTO `t_person` (firstName, lastName, age, gender, height, weghst, address, hobby, createdTime) VALUES (
        CONCAT('kevin', i),
        CONCAT('zou', i),
        i % 9,
        i % 2,
        175,
        65,
        CONCAT('Chinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna Shanghaaaaaaaaaaaaaaaaaaaaaaaaaaaai', i),
        CONCAT('Coooooooooooooooooooooooooooooooooooooooooooooooooooooooode', i),
        UNIX_TIMESTAMP()
      );
      SET i = i + 1;
    END WHILE;
  END;

CALL init_data();