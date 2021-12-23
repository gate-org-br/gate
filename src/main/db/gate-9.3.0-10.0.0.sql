ALTER TABLE gate.Auth ADD COLUMN scope VARCHAR(32) NULL AFTER Func$id;
update gate.Auth set scope = 'PUBLIC' where type = 0;
update gate.Auth set scope = 'PRIVATE' where type = 1;
ALTER TABLE gate.Auth CHANGE COLUMN scope scope VARCHAR(32) NOT NULL;
ALTER TABLE gate.Auth DROP COLUMN type;
ALTER TABLE gate.Auth ADD type tinyint GENERATED ALWAYS AS (if(scope = 'PUBLIC', 0, 1)) VIRTUAL;

ALTER TABLE gate.Auth ADD COLUMN access VARCHAR(32) NULL AFTER scope;
update gate.Auth set access = 'GRANT' where mode = 0;
update gate.Auth set access = 'BLOCK' where mode = 1;
ALTER TABLE gate.Auth CHANGE COLUMN access access VARCHAR(32) NOT NULL ;
ALTER TABLE gate.Auth DROP COLUMN mode;
ALTER TABLE gate.Auth ADD mode tinyint GENERATED ALWAYS AS (if(access = 'GRANT', 0, 1)) VIRTUAL;

alter table gate.Uzer add username VARCHAR(64) after Role$id;
update gate.Uzer set username = userID;
ALTER TABLE gate.Uzer CHANGE COLUMN username username VARCHAR(64) NOT NULL ;
ALTER TABLE gate.Uzer DROP COLUMN userID;
ALTER TABLE gate.Uzer ADD userID VARCHAR(64) GENERATED ALWAYS AS (username) VIRTUAL;

alter table gate.Uzer add password VARCHAR(32) after username;
update gate.Uzer set password = passwd;
ALTER TABLE gate.Uzer CHANGE COLUMN password password VARCHAR(64) NOT NULL ;
ALTER TABLE gate.Uzer DROP COLUMN passwd;
ALTER TABLE gate.Uzer ADD passwd VARCHAR(64) GENERATED ALWAYS AS (password) VIRTUAL;

alter table gate.Uzer add description VARCHAR(1024) after email;
update gate.Uzer set description = details;
ALTER TABLE gate.Uzer DROP COLUMN details;
ALTER TABLE gate.Uzer ADD details VARCHAR(1024) GENERATED ALWAYS AS (description) VIRTUAL;

ALTER TABLE gate.Role DROP INDEX Role$uk$roleID ;
alter table gate.Role add rolename VARCHAR(64) after Manager$id;
update gate.Role set rolename = roleID;
ALTER TABLE gate.Role DROP COLUMN roleID;
ALTER TABLE gate.Role ADD roleID VARCHAR(64) GENERATED ALWAYS AS (rolename) VIRTUAL;
ALTER TABLE gate.Role ADD UNIQUE INDEX Role$uk$rolename (rolename ASC) VISIBLE;

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
