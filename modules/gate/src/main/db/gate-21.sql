DROP TABLE IF EXISTS `gate`.`Server`;

CREATE TABLE IF NOT EXISTS `gate`.`Session`
(
	`id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`Uzer$id` INT UNSIGNED NOT NULL,
	`date`    DATE         NOT NULL,
	PRIMARY KEY (`id`),
	KEY `Session$fk$Uzer` (`Uzer$id`),
	CONSTRAINT `Session$fk$Uzer`
		FOREIGN KEY (`Uzer$id`)
			REFERENCES `gate`.`Uzer` (`id`)
			ON DELETE CASCADE
			ON UPDATE CASCADE
) ENGINE = InnoDB;

DROP PROCEDURE IF EXISTS `gate`.`run_migration`;

DELIMITER ;;

CREATE PROCEDURE `gate`.`run_migration`()
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
		BEGIN
		END;

	ALTER TABLE `gate`.`Uzer`
		ADD COLUMN `activity` DATETIME NULL;

	ALTER TABLE `gate`.`Uzer`
		CHANGE COLUMN `registration` `creation` DATETIME NOT NULL;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `details`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `passwd`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `userID`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `code`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `birthdate`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `sex`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `CPF`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `photo`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `cellPhone`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `phone`;

	ALTER TABLE `gate`.`Uzer`
		DROP COLUMN `description`;

	ALTER TABLE `gate`.`Uzer`
		DROP INDEX `Uzer$uk$CPF`;

	ALTER TABLE `gate`.`Uzer`
		CHANGE COLUMN `password` `password` VARCHAR(60) NOT NULL;
END ;;

DELIMITER ;

CALL `gate`.`run_migration`();

DROP PROCEDURE IF EXISTS `gate`.`run_migration`;