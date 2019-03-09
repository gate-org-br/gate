create database gate;
use gate;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Auth`
--

DROP TABLE IF EXISTS `Auth`;
CREATE TABLE `Auth` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Role$id` int(10) unsigned DEFAULT NULL,
  `Uzer$id` int(10) unsigned DEFAULT NULL,
  `Func$id` int(10) unsigned DEFAULT NULL,
  `module` varchar(32) DEFAULT NULL,
  `screen` varchar(32) DEFAULT NULL,
  `action` varchar(32) DEFAULT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `mode` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Auth$fk$Func` (`Func$id`),
  KEY `Auth$fk$Role` (`Role$id`),
  KEY `Auth$fk$Uzer` (`Uzer$id`),
  CONSTRAINT `Auth$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Auth$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Auth$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Func`
--

DROP TABLE IF EXISTS `Func`;
CREATE TABLE `Func` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Func$uk$name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Org`
--

DROP TABLE IF EXISTS `Org`;
CREATE TABLE `Org` (
  `orgID` varchar(16) NOT NULL,
  `name` varchar(256) NOT NULL,
  `icon` longblob,
  `description` varchar(256) DEFAULT NULL,
  `authenticators` text,
  `sun$time1` time DEFAULT NULL,
  `sun$time2` time DEFAULT NULL,
  `mon$time1` time DEFAULT NULL,
  `mon$time2` time DEFAULT NULL,
  `tue$time1` time DEFAULT NULL,
  `tue$time2` time DEFAULT NULL,
  `wed$time1` time DEFAULT NULL,
  `wed$time2` time DEFAULT NULL,
  `thu$time1` time DEFAULT NULL,
  `thu$time2` time DEFAULT NULL,
  `fri$time1` time DEFAULT NULL,
  `fri$time2` time DEFAULT NULL,
  `sat$time1` time DEFAULT NULL,
  `sat$time2` time DEFAULT NULL,
  PRIMARY KEY (`orgID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
CREATE TABLE `Role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `master` tinyint(1) unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `Role$id` int(10) unsigned DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `Manager$id` int(10) unsigned DEFAULT NULL,
  `roleID` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roleID_UNIQUE` (`roleID`),
  KEY `Role$fk1_idx` (`Manager$id`),
  KEY `Roke$fk$Role_idx` (`Role$id`),
  CONSTRAINT `Roke$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Role$fk$Uzer` FOREIGN KEY (`Manager$id`) REFERENCES `Uzer` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `RoleFunc`
--

DROP TABLE IF EXISTS `RoleFunc`;
CREATE TABLE `RoleFunc` (
  `Role$id` int(10) unsigned NOT NULL,
  `Func$id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Role$id`,`Func$id`),
  KEY `RoleFunc$fk$Func` (`Func$id`),
  CONSTRAINT `RoleFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `RoleFunc$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Uzer`
--

DROP TABLE IF EXISTS `Uzer`;
CREATE TABLE `Uzer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `Role$id` int(10) unsigned DEFAULT NULL,
  `userID` varchar(64) NOT NULL,
  `passwd` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  `email` varchar(64) DEFAULT NULL,
  `details` varchar(1024) DEFAULT NULL,
  `phone` varchar(24) DEFAULT NULL,
  `cellPhone` varchar(45) DEFAULT NULL,
  `photo` text,
  `CPF` char(14) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `registration` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Uzer$uk$userID` (`userID`),
  UNIQUE KEY `Uzer$uk$email` (`email`),
  KEY `Uzer$fk$Role` (`Role$id`),
  CONSTRAINT `Uzer$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `UzerFunc`
--

DROP TABLE IF EXISTS `UzerFunc`;
CREATE TABLE `UzerFunc` (
  `Uzer$id` int(10) unsigned NOT NULL,
  `Func$id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Uzer$id`,`Func$id`),
  KEY `UzerFunc$fk$Func` (`Func$id`),
  CONSTRAINT `UzerFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UzerFunc$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

INSERT INTO `gate`.`Func` (`id`, `name`) VALUES ('1', 'Superuser');
INSERT INTO `gate`.`Role` (`active`, `master`, `name`, `roleID`) VALUES ('1', '1', 'Gate', 'gate');
INSERT INTO `gate`.`Uzer` (active, Role$id, userID, passwd, name, registration) VALUES(1, 1, 'gate', MD5('password'), 'Superuser', now());
INSERT INTO `gate`.`UzerFunc` (`Uzer$id`, `Func$id`) VALUES ('1', '1');
INSERT INTO `gate`.`Auth` (`Func$id`, `type`, `mode`) VALUES ('1', '1', '0');
