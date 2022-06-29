alter table gate.Uzer add username VARCHAR(64) after Role$id;
update gate.Uzer set username = userID;
ALTER TABLE gate.Uzer CHANGE COLUMN username username VARCHAR(64) NOT NULL ;

alter table gate.Uzer add password VARCHAR(32) after username;
update gate.Uzer set password = passwd;
ALTER TABLE gate.Uzer CHANGE COLUMN password password VARCHAR(64) NOT NULL ;

alter table gate.Uzer add description VARCHAR(1024) after email;
update gate.Uzer set description = details;
ALTER TABLE gate.Uzer ADD details VARCHAR(1024) GENERATED ALWAYS AS (description) VIRTUAL;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Uzer_BEFORE_INSERT` BEFORE INSERT ON `Uzer` FOR EACH ROW
BEGIN
	DECLARE _username varchar(64);  
	DECLARE _password varchar(32);  
        DECLARE _description varchar(32);  
                
        set _username = coalesce(NEW.username, NEW.userID);
        SET NEW.userID = _username;
	SET NEW.username = _username;
        
        set _password = coalesce(NEW.password, NEW.passwd);        
        SET NEW.passwd = _password;
        SET NEW.password = _password;
        
	set _description = coalesce(NEW.description, NEW.details);        
        SET NEW.description = _description;
        SET NEW.details = _description;
END$$
DELIMITER ;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Uzer_BEFORE_UPDATE` BEFORE UPDATE ON `Uzer` FOR EACH ROW
BEGIN
	DECLARE _username varchar(64);  
	DECLARE _password varchar(32);  
        DECLARE _description varchar(32);  
                
        set _username = if (OLD.username <> NEW.username, NEW.username, new.userID);
        SET NEW.userID = _username;
	SET NEW.username = _username;
        
        set _password = if (OLD.password <> NEW.password, NEW.password, new.passwd);
        SET NEW.passwd = _password;
        SET NEW.password = _password;
        
	set _description = if (OLD.description <> NEW.description, NEW.description, new.details);        
        SET NEW.description = _description;
END$$
DELIMITER ;


ALTER TABLE gate.Auth ADD COLUMN scope VARCHAR(32) NULL AFTER Func$id;
update gate.Auth set scope = 'PUBLIC' where type = 0;
update gate.Auth set scope = 'PRIVATE' where type = 1;
ALTER TABLE gate.Auth CHANGE COLUMN scope scope VARCHAR(32) NOT NULL;

ALTER TABLE gate.Auth ADD COLUMN access VARCHAR(32) NULL AFTER scope;
update gate.Auth set access = 'GRANT' where mode = 0;
update gate.Auth set access = 'BLOCK' where mode = 1;
ALTER TABLE gate.Auth CHANGE COLUMN access access VARCHAR(32) NOT NULL ;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Auth_BEFORE_INSERT` BEFORE INSERT ON `Auth` FOR EACH ROW
BEGIN
	DECLARE _scope varchar(32);
    DECLARE _access varchar(32);
    
    set _scope = coalesce(NEW.scope, NEW.type);
    SET NEW.scope = if(_scope = '0', 'PUBLIC', if(_scope = '1', 'PRIVATE', _scope));
	SET NEW.type = if(_scope = 'PUBLIC', '0', if(_scope = 'PRIVATE', '1', _scope));
    
    set _access = coalesce(NEW.access, NEW.mode);
    SET NEW.access = if(_access = '0', 'GRANT', if(_access = '1', 'BLOCK', _access));
	SET NEW.mode = if(_access = 'GRANT', '0', if(_access = '1', 'BLOCK', _scope));
END$$
DELIMITER ;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Auth_BEFORE_UPDATE` BEFORE UPDATE ON `Auth` FOR EACH ROW
BEGIN
    DECLARE _scope varchar(32);
    DECLARE _access varchar(32);
    
    set _scope = if (OLD.scope <> NEW.scope, NEW.scope, new.type);
    SET NEW.scope = if(_scope = '0', 'PUBLIC', if(_scope = '1', 'PRIVATE', _scope));
	SET NEW.type = if(_scope = 'PUBLIC', 0, if(_scope = 'PRIVATE', 1, _scope));
    
    set _access = if (OLD.access <> NEW.access, NEW.access, new.mode);
    SET NEW.access = if(_access = '0', 'GRANT', if(_access = '1', 'BLOCK', _access));
	SET NEW.mode = if(_access = 'GRANT', 0, if(_access = 'BLOCK', 1, _access));
END$$
DELIMITER ;

alter table gate.Role add rolename VARCHAR(64) after Manager$id;
update gate.Role set rolename = roleID;
ALTER TABLE gate.Role ADD UNIQUE INDEX Role$uk$rolename (rolename ASC) VISIBLE;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Role_BEFORE_INSERT` BEFORE UPDATE ON `Role` FOR EACH ROW
BEGIN
	DECLARE _rolename varchar(16);
			
	set _rolename = coalesce (NEW.rolename, new.roleID);
	SET NEW.rolename = rolename;
	SET NEW.roleID = roleID;
END$$
DELIMITER ;

DELIMITER $$
USE `gate`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gate`.`Role_BEFORE_UPDATE` BEFORE UPDATE ON `Role` FOR EACH ROW
BEGIN
	DECLARE _rolename varchar(16);
			
	set _rolename = if (OLD.rolename <> NEW.rolename, NEW.rolename, new.roleID);
	SET NEW.rolename = rolename;
	SET NEW.roleID = roleID;
END$$
DELIMITER ;

CREATE TABLE `gate`.`App` (
  `id` VARCHAR(32) NOT NULL,
  `json` TEXT NOT NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `Mail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(32) NOT NULL,
  `date` datetime NOT NULL,
  `sender` varchar(128) NOT NULL,
  `receiver` varchar(128) NOT NULL,
  `attempts` int unsigned NOT NULL,
  `expiration` datetime NOT NULL,
  `message` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `Server` (
  `type` varchar(32) NOT NULL,
  `host` varchar(32) NOT NULL,
  `port` int unsigned NOT NULL,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



USE `gate`;
DROP function IF EXISTS `gate`.`fullname`;

DELIMITER $$
USE `gate`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `fullname`(parameter integer) RETURNS text CHARSET utf8mb4
    READS SQL DATA
    DETERMINISTIC
BEGIN
	declare fullname  text;
    declare parent integer;
    declare simplename varchar(64); 

  	SELECT Role$id, coalesce(rolename, name) INTO parent, fullname FROM Role WHERE  id = parameter;

	REPEAT
		SELECT Role$id, coalesce(rolename, name) INTO parent, simplename FROM Role WHERE  id = parent;
        IF simplename is not null THEN
			set fullname = concat(simplename, ' / ', fullname);
		END IF;
	UNTIL parent is null
	END REPEAT;

	return fullname;
END$$
DELIMITER ;