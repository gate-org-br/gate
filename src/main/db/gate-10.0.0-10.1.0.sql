CREATE TABLE `Chat` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `Sender$id` int unsigned NOT NULL,
  `Receiver$id` int unsigned NOT NULL,
  `date` datetime NOT NULL,
  `text` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Chat$fk$Sender` (`Sender$id`),
  KEY `Chat$fk$Receiver` (`Receiver$id`),
  CONSTRAINT `Chat$fk$Receiver` FOREIGN KEY (`Receiver$id`) REFERENCES `Uzer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `Chat$fk$Sender` FOREIGN KEY (`Sender$id`) REFERENCES `Uzer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


ALTER TABLE `gate`.`Uzer` 
ADD COLUMN `status` VARCHAR(16) NOT NULL DEFAULT 'OFFLINE' AFTER `id`;

update gate.Uzer set status = 'INACTIVE' where not Uzer.active;

DROP TRIGGER IF EXISTS `gate`.`Uzer_BEFORE_INSERT`;

DELIMITER $$
USE `gate`$$
CREATE DEFINER=`davins`@`%` TRIGGER `Uzer_BEFORE_INSERT` BEFORE INSERT ON `Uzer` FOR EACH ROW BEGIN
  DECLARE _username varchar(64);  
		DECLARE _password varchar(32);  
        DECLARE _description varchar(32);  
        DECLARE _status varchar(32);  
                
        set _username = coalesce(NEW.username, NEW.userID);
        SET NEW.userID = _username;
		SET NEW.username = _username;
        
        set _password = coalesce(NEW.password, NEW.passwd);        
        SET NEW.passwd = _password;
        SET NEW.password = _password;
        
		set _description = coalesce(NEW.description, NEW.details);        
        SET NEW.description = _description;
        SET NEW.details = _description;
        
        set _status = coalesce(NEW.status, NEW.active);
	SET NEW.status = if(_status = '0', 'INACTIVE', if(_status = '1', 'OFFLINE', _status));
	SET NEW.active = if(_status = '0' or _status = '1', _status, if (_status='INACTIVE', 0, 1));
END$$
DELIMITER ;


DROP TRIGGER IF EXISTS `gate`.`Uzer_BEFORE_UPDATE`;

DELIMITER $$
USE `gate`$$
CREATE DEFINER=`davins`@`%` TRIGGER `Uzer_BEFORE_UPDATE` BEFORE UPDATE ON `Uzer` FOR EACH ROW BEGIN
		DECLARE _username varchar(64);  
		DECLARE _password varchar(32);  
        DECLARE _description varchar(32);  
		DECLARE _status varchar(32);  
                
        set _username = if (OLD.username <> NEW.username, NEW.username, new.userID);
        SET NEW.userID = _username;
		SET NEW.username = _username;
        
        set _password = if (OLD.password <> NEW.password, NEW.password, new.passwd);
        SET NEW.passwd = _password;
        SET NEW.password = _password;
        
		set _description = if (OLD.description <> NEW.description, NEW.description, new.details);        
        SET NEW.description = _description;

		set _status = if (OLD.status <> NEW.status, NEW.status, new.active);
		SET NEW.status = if(_status = '0', 'INACTIVE', if(_status = '1', 'OFFLINE', _status));
		SET NEW.active = if(_status = '0' or _status = '1', _status, if (_status='INACTIVE', 0, 1));
END$$
DELIMITER ;
