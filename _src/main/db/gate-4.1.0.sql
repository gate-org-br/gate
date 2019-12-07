ALTER TABLE `gate`.`Uzer`
ADD COLUMN `CPF` CHAR(14) NULL,
ADD COLUMN `sex` tinyint(1) NULL,
ADD COLUMN `birthdate` DATE NULL,
ADD COLUMN `registration` DATE not null default '1500-04-22 00:00:00';