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
