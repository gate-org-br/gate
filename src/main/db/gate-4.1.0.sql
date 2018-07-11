ALTER TABLE `gate`.`Uzer`
ADD COLUMN `CPF` CHAR(11) NULL,
ADD COLUMN `sex` tinyint(1) NULL,
ADD COLUMN `birthdate` DATE NULL,
ADD COLUMN `registration` DATE NULL;

update Uzer set registration = curdate();

ALTER TABLE `gate`.`Uzer`
CHANGE COLUMN `registration` `registration` DATE NOT NULL ;

