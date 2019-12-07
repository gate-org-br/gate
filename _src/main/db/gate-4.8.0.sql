ALTER TABLE `gate`.`Uzer`
DROP INDEX `UK_Uzer_userID` ,
ADD UNIQUE INDEX `Uzer$uk$userID` (`userID` ASC);


ALTER TABLE `gate`.`Uzer`
DROP INDEX `Uzer$fk1_idx` ,
ADD INDEX `Uzer$fk$Role` (`Role$id` ASC);

ALTER TABLE `gate`.`Uzer`
ADD UNIQUE INDEX `Uzer$uk$email` (`email` ASC);