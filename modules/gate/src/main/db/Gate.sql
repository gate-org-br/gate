CREATE DATABASE gate;
USE gate;

DROP TABLE IF EXISTS `App`;
CREATE TABLE `App`
(
	`id`   VARCHAR(32) NOT NULL,
	`json` TEXT        NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `Auth`;
CREATE TABLE `Auth`
(
	`id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`Role$id` INT UNSIGNED DEFAULT NULL,
	`Uzer$id` INT UNSIGNED DEFAULT NULL,
	`Func$id` INT UNSIGNED DEFAULT NULL,
	`scope`   VARCHAR(32)  NOT NULL,
	`access`  VARCHAR(32)  NOT NULL,
	`module`  VARCHAR(32)  DEFAULT NULL,
	`screen`  VARCHAR(32)  DEFAULT NULL,
	`action`  VARCHAR(32)  DEFAULT NULL,
	`type`    TINYINT GENERATED ALWAYS AS (IF((`scope` = _latin1'PUBLIC'), 0, 1)) VIRTUAL,
	`mode`    TINYINT GENERATED ALWAYS AS (IF((`access` = _latin1'GRANT'), 0, 1)) VIRTUAL,
	PRIMARY KEY (`id`),
	KEY `Auth$fk$Func` (`Func$id`),
	KEY `Auth$fk$Role` (`Role$id`),
	KEY `Auth$fk$Uzer` (`Uzer$id`),
	CONSTRAINT `Auth$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `Auth$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `Auth$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `Func`;
CREATE TABLE `Func`
(
	`id`   INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(64)  NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `Func$uk$name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `Mail`;
CREATE TABLE `Mail`
(
	`id`         INT          NOT NULL AUTO_INCREMENT,
	`app`        VARCHAR(32)  NOT NULL,
	`date`       DATETIME     NOT NULL,
	`sender`     VARCHAR(128) NOT NULL,
	`receiver`   VARCHAR(128) NOT NULL,
	`attempts`   INT UNSIGNED NOT NULL,
	`expiration` DATETIME     NOT NULL,
	`message`    LONGTEXT     NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `Org`;
CREATE TABLE `Org`
(
	`orgID`          VARCHAR(16)  NOT NULL,
	`name`           VARCHAR(256) NOT NULL,
	`icon`           LONGTEXT,
	`description`    VARCHAR(256) DEFAULT NULL,
	`authenticators` TEXT,
	`sun__min`       TIME         DEFAULT NULL,
	`sun__max`       TIME         DEFAULT NULL,
	`mon__min`       TIME         DEFAULT NULL,
	`mon__max`       TIME         DEFAULT NULL,
	`tue__min`       TIME         DEFAULT NULL,
	`tue__max`       TIME         DEFAULT NULL,
	`wed__min`       TIME         DEFAULT NULL,
	`wed__max`       TIME         DEFAULT NULL,
	`thu__min`       TIME         DEFAULT NULL,
	`thu__max`       TIME         DEFAULT NULL,
	`fri__min`       TIME         DEFAULT NULL,
	`fri__max`       TIME         DEFAULT NULL,
	`sat__min`       TIME         DEFAULT NULL,
	`sat__max`       TIME         DEFAULT NULL,
	`sun$time1`      TIME GENERATED ALWAYS AS (`sun__min`) VIRTUAL,
	`sun$time2`      TIME GENERATED ALWAYS AS (`sun__max`) VIRTUAL,
	`mon$time1`      TIME GENERATED ALWAYS AS (`mon__min`) VIRTUAL,
	`mon$time2`      TIME GENERATED ALWAYS AS (`mon__max`) VIRTUAL,
	`tue$time1`      TIME GENERATED ALWAYS AS (`tue__min`) VIRTUAL,
	`tue$time2`      TIME GENERATED ALWAYS AS (`tue__max`) VIRTUAL,
	`wed$time1`      TIME GENERATED ALWAYS AS (`wed__min`) VIRTUAL,
	`wed$time2`      TIME GENERATED ALWAYS AS (`wed__max`) VIRTUAL,
	`thu$time1`      TIME GENERATED ALWAYS AS (`thu__min`) VIRTUAL,
	`thu$time2`      TIME GENERATED ALWAYS AS (`thu__max`) VIRTUAL,
	`fri$time1`      TIME GENERATED ALWAYS AS (`fri__min`) VIRTUAL,
	`fri$time2`      TIME GENERATED ALWAYS AS (`fri__max`) VIRTUAL,
	`sat$time1`      TIME GENERATED ALWAYS AS (`sat__min`) VIRTUAL,
	`sat$time2`      TIME GENERATED ALWAYS AS (`sat__max`) VIRTUAL,
	PRIMARY KEY (`orgID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `Role`;
CREATE TABLE `Role`
(
	`id`          INT UNSIGNED     NOT NULL AUTO_INCREMENT,
	`active`      TINYINT UNSIGNED NOT NULL DEFAULT '1',
	`master`      TINYINT UNSIGNED NOT NULL,
	`name`        VARCHAR(64)      NOT NULL,
	`description` VARCHAR(256)              DEFAULT NULL,
	`Role$id`     INT UNSIGNED              DEFAULT NULL,
	`email`       VARCHAR(64)               DEFAULT NULL,
	`Manager$id`  INT UNSIGNED              DEFAULT NULL,
	`rolename`    VARCHAR(64)               DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `Role$uk$rolename` (`rolename`),
	KEY `Role$fk1_idx` (`Manager$id`),
	KEY `Roke$fk$Role_idx` (`Role$id`),
	CONSTRAINT `Roke$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `Role$fk$Uzer` FOREIGN KEY (`Manager$id`) REFERENCES `Uzer` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `RoleFunc`;
CREATE TABLE `RoleFunc`
(
	`Role$id` INT UNSIGNED NOT NULL,
	`Func$id` INT UNSIGNED NOT NULL,
	PRIMARY KEY (`Role$id`, `Func$id`),
	KEY `RoleFunc$fk$Func` (`Func$id`),
	CONSTRAINT `RoleFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `RoleFunc$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `Server`;
CREATE TABLE `Server`
(
	`type`     VARCHAR(32)      NOT NULL,
	`host`     VARCHAR(32)      NOT NULL,
	`port`     INT UNSIGNED     NOT NULL,
	`username` VARCHAR(45)               DEFAULT NULL,
	`password` VARCHAR(45)               DEFAULT NULL,
	`useTLS`   TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`useSSL`   TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`timeout`  INT UNSIGNED              DEFAULT NULL,
	PRIMARY KEY (`type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `Session`;
CREATE TABLE `Session`
(
	`id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`Uzer$id` INT UNSIGNED NOT NULL,
	`date`    DATE         NOT NULL,
	PRIMARY KEY (`id`),
	KEY `Session$fk$Uzer_idx` (`Uzer$id`),
	CONSTRAINT `Session$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `Uzer`;
CREATE TABLE `Uzer`
(
	`id`       INT UNSIGNED     NOT NULL AUTO_INCREMENT,
	`active`   TINYINT UNSIGNED NOT NULL DEFAULT '1',
	`Role$id`  INT UNSIGNED              DEFAULT NULL,
	`username` VARCHAR(64)      NOT NULL,
	`password` VARCHAR(60)      NOT NULL,
	`name`     VARCHAR(128)     NOT NULL,
	`email`    VARCHAR(64)               DEFAULT NULL,
	`creation` DATETIME         NOT NULL,
	`activity` DATETIME                  DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `Uzer$fk$username` (`username`),
	UNIQUE KEY `Uzer$uk$email` (`email`),
	KEY `Uzer$fk$Role` (`Role$id`),
	CONSTRAINT `Uzer$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `UzerFunc`;
CREATE TABLE `UzerFunc`
(
	`Uzer$id` INT UNSIGNED NOT NULL,
	`Func$id` INT UNSIGNED NOT NULL,
	PRIMARY KEY (`Uzer$id`, `Func$id`),
	KEY `UzerFunc$fk$Func` (`Func$id`),
	CONSTRAINT `UzerFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `UzerFunc$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Functions

DROP FUNCTION IF EXISTS `fullname`;
DELIMITER ;;
CREATE FUNCTION `fullname`(roleId INTEGER) RETURNS TEXT CHARSET utf8mb4
	READS SQL DATA
	NOT DETERMINISTIC
BEGIN
	DECLARE result TEXT;
	DECLARE current INTEGER;
	DECLARE part VARCHAR(64);
	SELECT Role$id, COALESCE(rolename, name) INTO current, result FROM Role WHERE id = roleId;
	REPEAT
		SELECT Role$id, COALESCE(rolename, name) INTO current, part FROM Role WHERE id = current;
		IF part IS NOT NULL THEN
			SET result = CONCAT(part, ' / ', result);
		END IF;
	UNTIL current IS NULL
		END REPEAT;
	RETURN result;
END ;;
DELIMITER ;

DROP FUNCTION IF EXISTS `getmaster`;
DELIMITER ;;
CREATE FUNCTION `getmaster`(roleId INTEGER) RETURNS INT
	READS SQL DATA
	NOT DETERMINISTIC
BEGIN
	DECLARE current INTEGER;
	DECLARE isMaster INTEGER;
	SET current = roleId;
	SELECT Role.master INTO isMaster FROM Role WHERE id = current;
	REPEAT
		IF isMaster = 1 THEN
			RETURN current;
		END IF;
		SELECT Role.Role$id, Role.master INTO current, isMaster FROM Role WHERE id = current;
	UNTIL current IS NULL
		END REPEAT;
	RETURN roleId;
END ;;
DELIMITER ;

DROP FUNCTION IF EXISTS `ismaster`;
DELIMITER ;;
CREATE FUNCTION `ismaster`(ancestorId INTEGER, childId INTEGER) RETURNS INT
	READS SQL DATA
	NOT DETERMINISTIC
BEGIN
	DECLARE current INTEGER;
	DECLARE isMaster INTEGER;
	SET current = childId;
	SELECT Role.master INTO isMaster FROM Role WHERE id = current;
	REPEAT
		IF isMaster = 1 AND current = ancestorId THEN
			RETURN TRUE;
		END IF;
		SELECT Role.Role$id, Role.master INTO current, isMaster FROM Role WHERE id = current;
	UNTIL current IS NULL
		END REPEAT;
	RETURN FALSE;
END ;;
DELIMITER ;

DROP FUNCTION IF EXISTS `isparent`;
DELIMITER ;;
CREATE FUNCTION `isparent`(ancestorId INTEGER, childId INTEGER) RETURNS INT
	READS SQL DATA
	NOT DETERMINISTIC
BEGIN
	DECLARE current INTEGER;
	SET current = childId;
	REPEAT
		IF current = ancestorId THEN
			RETURN TRUE;
		END IF;
		SELECT Role$id INTO current FROM Role WHERE id = current;
	UNTIL current IS NULL
		END REPEAT;
	RETURN FALSE;
END ;;
DELIMITER ;

DROP FUNCTION IF EXISTS `secure`;
DELIMITER ;;
CREATE FUNCTION `secure`(userId INTEGER, module VARCHAR(64), screen VARCHAR(32), action VARCHAR(32)) RETURNS INT
	READS SQL DATA
	NOT DETERMINISTIC
BEGIN
	DECLARE role INTEGER;
	DECLARE current INTEGER;

	IF module IS NULL THEN
		RETURN FALSE;
	END IF;

	IF (SELECT EXISTS(SELECT Auth.id
	                  FROM Auth
	                  WHERE Auth.Uzer$id = userId
		                AND (Auth.module IS NULL OR Auth.module = module)
		                AND (screen IS NULL OR Auth.screen IS NULL OR Auth.screen = screen)
		                AND (action IS NULL OR Auth.action IS NULL OR Auth.action = action)) OR
	    SELECT EXISTS (
			SELECT Auth.id FROM Auth
			JOIN UzerFunc ON Auth.Func$id = UzerFunc.Func$id
			WHERE UzerFunc.Uzer$id = userId
			AND (Auth.module IS NULL OR Auth.module = module)
			AND (screen IS NULL OR Auth.screen IS NULL OR Auth.screen = screen)
			AND (ACTION IS NULL OR Auth.action IS NULL OR Auth.action = ACTION)
			)) THEN
		RETURN TRUE;
	END IF;

	SET current = (SELECT Role$id FROM Uzer WHERE id = userId);

	REPEAT
		SELECT Role.id, Role.Role$id FROM Role WHERE Role.id = current INTO role, current;
		IF (SELECT EXISTS(SELECT Auth.id
		                  FROM Auth
		                  WHERE Auth.Role$id = role
			                AND (Auth.module IS NULL OR Auth.module = module)
			                AND (screen IS NULL OR Auth.screen IS NULL OR Auth.screen = screen)
			                AND (action IS NULL OR Auth.action IS NULL OR Auth.action = action)) OR
		    SELECT EXISTS (
				SELECT Auth.id FROM Auth
				JOIN RoleFunc ON Auth.Func$id = RoleFunc.Func$id
				WHERE RoleFunc.Role$id = ROLE
				AND (Auth.module IS NULL OR Auth.module = module)
				AND (screen IS NULL OR Auth.screen IS NULL OR Auth.screen = screen)
				AND (ACTION IS NULL OR Auth.action IS NULL OR Auth.action = ACTION)
				)) THEN
			RETURN TRUE;
		END IF;
	UNTIL current IS NULL
		END REPEAT;

	RETURN FALSE;
END ;;
DELIMITER ;

-- Procedures

DROP PROCEDURE IF EXISTS `auths`;
DELIMITER ;;
CREATE PROCEDURE `auths`(userId INT)
BEGIN
	WITH RECURSIVE role_hierarchy AS (SELECT Role$id AS id
	                                  FROM Uzer
	                                  WHERE id = userId
	                                  UNION
	                                  SELECT Role.Role$id
	                                  FROM Role
										   JOIN role_hierarchy ON Role.id = role_hierarchy.id
	                                  WHERE Role.Role$id IS NOT NULL)
	SELECT DISTINCT Auth.id,
	                Auth.Uzer$id AS 'user.id',
	                Auth.Role$id AS 'role.id',
	                Auth.Func$id AS 'func.id',
	                Auth.module,
	                Auth.screen,
	                Auth.action,
	                Auth.scope,
	                Auth.access
	FROM Auth
	WHERE Auth.Uzer$id = userId
	UNION
	SELECT Auth.id,
	       Auth.Uzer$id,
	       Auth.Role$id,
	       Auth.Func$id,
	       Auth.module,
	       Auth.screen,
	       Auth.action,
	       Auth.scope,
	       Auth.access
	FROM Auth
		 JOIN UzerFunc ON Auth.Func$id = UzerFunc.Func$id
	WHERE UzerFunc.Uzer$id = userId
	UNION
	SELECT Auth.id,
	       Auth.Uzer$id,
	       Auth.Role$id,
	       Auth.Func$id,
	       Auth.module,
	       Auth.screen,
	       Auth.action,
	       Auth.scope,
	       Auth.access
	FROM Auth
		 JOIN role_hierarchy ON Auth.Role$id = role_hierarchy.id
	UNION
	SELECT Auth.id,
	       Auth.Uzer$id,
	       Auth.Role$id,
	       Auth.Func$id,
	       Auth.module,
	       Auth.screen,
	       Auth.action,
	       Auth.scope,
	       Auth.access
	FROM Auth
		 JOIN RoleFunc ON Auth.Func$id = RoleFunc.Func$id
	     JOIN role_hierarchy ON RoleFunc.Role$id = role_hierarchy.id;
END ;;
DELIMITER ;


DROP PROCEDURE IF EXISTS `get_authorized_users`;
DELIMITER ;;
CREATE PROCEDURE `get_authorized_users`(IN p_module VARCHAR(255), IN p_screen VARCHAR(255), IN p_action VARCHAR(255))
BEGIN
	WITH RECURSIVE
		role_hierarchy AS (SELECT Uzer.id AS userId, Uzer.`Role$id` AS roleId, 0 AS depth
		                   FROM gate.Uzer
		                   UNION
		                   SELECT rh.userId, Role.`Role$id`, rh.depth + 1
		                   FROM gate.Role
								JOIN role_hierarchy rh ON Role.id = rh.roleId
		                   WHERE Role.`Role$id` IS NOT NULL),
		auth_users AS (SELECT Uzer.id, Uzer.username, Uzer.name, Uzer.email, Auth.module, Auth.screen, Auth.action
		               FROM gate.Auth
							JOIN gate.Uzer ON Auth.`Uzer$id` = Uzer.id
		               UNION
		               SELECT Uzer.id, Uzer.username, Uzer.name, Uzer.email, Auth.module, Auth.screen, Auth.action
		               FROM gate.Auth
							JOIN gate.UzerFunc ON UzerFunc.`Func$id` = Auth.`Func$id`
		                    JOIN gate.Uzer ON UzerFunc.`Uzer$id` = Uzer.id
		               UNION
		               SELECT Uzer.id, Uzer.username, Uzer.name, Uzer.email, Auth.module, Auth.screen, Auth.action
		               FROM gate.Auth
							JOIN role_hierarchy ON Auth.`Role$id` = role_hierarchy.roleId
							   AND (role_hierarchy.depth = 0 OR Auth.scope = 'PUBLIC')
		                    JOIN gate.Uzer ON role_hierarchy.userId = Uzer.id
		               UNION
		               SELECT Uzer.id, Uzer.username, Uzer.name, Uzer.email, Auth.module, Auth.screen, Auth.action
		               FROM gate.Auth
							JOIN gate.RoleFunc ON RoleFunc.`Func$id` = Auth.`Func$id`
		                    JOIN role_hierarchy ON RoleFunc.`Role$id` = role_hierarchy.roleId
							   AND (role_hierarchy.depth = 0 OR Auth.scope = 'PUBLIC')
		                    JOIN gate.Uzer ON role_hierarchy.userId = Uzer.id)
	SELECT DISTINCT id, username, name, email
	FROM auth_users
	WHERE (p_module IS NULL OR module IS NULL OR module = p_module)
	  AND (p_screen IS NULL OR screen IS NULL OR screen = p_screen)
	  AND (p_action IS NULL OR action IS NULL OR action = p_action);
END ;;
DELIMITER ;

INSERT INTO Role (id, active, master, name, description, Role$id)
VALUES (1, 1, 1, 'Root', 'Root role with full access', NULL);

INSERT INTO Func (id, name)
VALUES (1, 'root');

INSERT INTO RoleFunc (Role$id, Func$id)
VALUES (1, 1);

INSERT INTO Auth (id, Func$id, scope, access, module, screen, action)
VALUES (1, 1, 'PUBLIC', 'GRANT', NULL, NULL, NULL);

INSERT INTO Uzer (id, active, Role$id, username, password, name, email, creation)
VALUES (1, 1, 1, 'root', '$2b$10$Inety21IRU/b5b4PbKtgj.dzBqojhKmm28SDGLKzjJVi7kaqdC19O', 'Root', NULL, NOW());