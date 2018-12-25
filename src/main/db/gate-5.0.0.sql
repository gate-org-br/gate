CREATE TABLE Func (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY Func$uk$name (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE RoleFunc (
  Role$id int(10) unsigned NOT NULL,
  Func$id int(10) unsigned NOT NULL,
  PRIMARY KEY (Role$id,Func$id),
  KEY RoleFunc$fk$Func (Func$id),
  CONSTRAINT RoleFunc$fk$Func FOREIGN KEY (Func$id) REFERENCES Func (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT RoleFunc$fk$Role FOREIGN KEY (Role$id) REFERENCES Role (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE UzerFunc (
  Uzer$id int(10) unsigned NOT NULL,
  Func$id int(10) unsigned NOT NULL,
  PRIMARY KEY (Uzer$id,Func$id),
  KEY UzerFunc$fk$Func (Func$id),
  CONSTRAINT UzerFunc$fk$Func FOREIGN KEY (Func$id) REFERENCES Func (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT UzerFunc$fk$Uzer FOREIGN KEY (Uzer$id) REFERENCES Uzer (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `gate`.`Auth`
ADD COLUMN `Func$id` INT(10) UNSIGNED NULL AFTER `Uzer$id`,
ADD INDEX `Auth$fk$Func` (`Func$id` ASC);
ALTER TABLE `gate`.`Auth`
ADD CONSTRAINT `Auth$fk$Func`
  FOREIGN KEY (`Func$id`)
  REFERENCES `gate`.`Func` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `gate`.`Auth`
DROP FOREIGN KEY `Auth$fk1`;
ALTER TABLE `gate`.`Auth`
DROP INDEX `Auth$fk1_idx` ,
ADD INDEX `Auth$fk$Role` (`Role$id` ASC);
ALTER TABLE `gate`.`Auth`
ADD CONSTRAINT `Auth$fk$Role`
  FOREIGN KEY (`Role$id`)
  REFERENCES `gate`.`Role` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `gate`.`Auth`
DROP FOREIGN KEY `Auth$fk2`;
ALTER TABLE `gate`.`Auth`
DROP INDEX `Auth$fk2_idx` ,
ADD INDEX `Auth$fk$Uzer` (`Uzer$id` ASC);
ALTER TABLE `gate`.`Auth`
ADD CONSTRAINT `Auth$fk$Uzer`
  FOREIGN KEY (`Uzer$id`)
  REFERENCES `gate`.`Uzer` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
