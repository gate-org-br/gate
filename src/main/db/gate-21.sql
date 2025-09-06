ALTER TABLE `gate`.`Uzer` 
ADD COLUMN `activity` DATETIME NULL AFTER `details`,
CHANGE COLUMN `registration` `creation` DATETIME NOT NULL ;

ALTER TABLE `gate`.`Uzer` 
DROP COLUMN `details`,
DROP COLUMN `passwd`,
DROP COLUMN `userID`;

ALTER TABLE `gate`.`Uzer` 
DROP COLUMN `code`,
DROP COLUMN `birthdate`,
DROP COLUMN `sex`,
DROP COLUMN `CPF`,
DROP COLUMN `photo`,
DROP COLUMN `cellPhone`,
DROP COLUMN `phone`,
DROP COLUMN `description`,
DROP INDEX `Uzer$uk$CPF`;


ALTER TABLE `gate`.`Uzer` 
CHANGE COLUMN `password` `password` VARCHAR(60) NOT NULL ;
