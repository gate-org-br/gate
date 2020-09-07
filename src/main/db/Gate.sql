-- MySQL dump 10.13  Distrib 8.0.19, for Linux (x86_64)
--
-- Host: localhost    Database: gate
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Auth` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `Role$id` int unsigned DEFAULT NULL,
  `Uzer$id` int unsigned DEFAULT NULL,
  `Func$id` int unsigned DEFAULT NULL,
  `module` varchar(32) DEFAULT NULL,
  `screen` varchar(32) DEFAULT NULL,
  `action` varchar(32) DEFAULT NULL,
  `type` tinyint NOT NULL DEFAULT '0',
  `mode` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Auth$fk$Func` (`Func$id`),
  KEY `Auth$fk$Role` (`Role$id`),
  KEY `Auth$fk$Uzer` (`Uzer$id`),
  CONSTRAINT `Auth$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Auth$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Auth$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=484 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Func`
--

DROP TABLE IF EXISTS `Func`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Func` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Func$uk$name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Org`
--

DROP TABLE IF EXISTS `Org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Org` (
  `orgID` varchar(16) NOT NULL,
  `name` varchar(256) NOT NULL,
  `icon` longtext,
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Role` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `active` tinyint unsigned NOT NULL DEFAULT '1',
  `master` tinyint unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `Role$id` int unsigned DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `Manager$id` int unsigned DEFAULT NULL,
  `roleID` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roleID_UNIQUE` (`roleID`),
  KEY `Role$fk1_idx` (`Manager$id`),
  KEY `Roke$fk$Role_idx` (`Role$id`),
  CONSTRAINT `Roke$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Role$fk$Uzer` FOREIGN KEY (`Manager$id`) REFERENCES `Uzer` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RoleFunc`
--

DROP TABLE IF EXISTS `RoleFunc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RoleFunc` (
  `Role$id` int unsigned NOT NULL,
  `Func$id` int unsigned NOT NULL,
  PRIMARY KEY (`Role$id`,`Func$id`),
  KEY `RoleFunc$fk$Func` (`Func$id`),
  CONSTRAINT `RoleFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `RoleFunc$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Uzer`
--

DROP TABLE IF EXISTS `Uzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Uzer` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `active` tinyint unsigned NOT NULL DEFAULT '1',
  `Role$id` int unsigned DEFAULT NULL,
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
  `registration` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Uzer$uk$userID` (`userID`),
  UNIQUE KEY `Uzer$uk$email` (`email`),
  KEY `Uzer$fk$Role` (`Role$id`),
  CONSTRAINT `Uzer$fk$Role` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=599 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UzerFunc`
--

DROP TABLE IF EXISTS `UzerFunc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `UzerFunc` (
  `Uzer$id` int unsigned NOT NULL,
  `Func$id` int unsigned NOT NULL,
  PRIMARY KEY (`Uzer$id`,`Func$id`),
  KEY `UzerFunc$fk$Func` (`Func$id`),
  CONSTRAINT `UzerFunc$fk$Func` FOREIGN KEY (`Func$id`) REFERENCES `Func` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UzerFunc$fk$Uzer` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'gate'
--

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `fullname`(parameter integer) RETURNS text CHARSET utf8mb4
    READS SQL DATA
    DETERMINISTIC
BEGIN
	declare fullname  text;
    declare parent integer;
    declare simplename varchar(64);
        
  	SELECT Role$id, coalesce(roleID, name) INTO parent, fullname FROM Role WHERE  id = parameter;
    
	REPEAT
		SELECT Role$id, coalesce(roleID, name) INTO parent, simplename FROM Role WHERE  id = parent;
        IF simplename is not null THEN 
			set fullname = concat(simplename, ' / ', fullname);
		END IF;
	UNTIL parent is null
	END REPEAT;
    
	return fullname;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getmaster`(parameter integer) RETURNS int
    READS SQL DATA
    DETERMINISTIC
BEGIN
	declare parent integer;
	declare ismaster integer;

	set parent = parameter;
	SELECT Role.master INTO ismaster FROM Role WHERE  id = parent;

	REPEAT
		IF ismaster = 1 THEN 
			return parent;
		END IF;
		SELECT Role.Role$id, Role.master INTO parent, ismaster FROM Role WHERE  id = parent;
	UNTIL parent is null
	END REPEAT;
	return parameter;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `ismaster`(parameter integer, child INTEGER) RETURNS int
    READS SQL DATA
    DETERMINISTIC
BEGIN
declare parent integer;
	declare master integer;

	set parent = child;
	SELECT Role.master INTO master FROM Role WHERE  id = parent;

	REPEAT
		IF master = 1 and parent = parameter THEN 
			return true;
		END IF;
		SELECT Role.Role$id, Role.master INTO parent, master FROM Role WHERE  id = parent;
	UNTIL parent is null
	END REPEAT;
	return false;
END ;;

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `isparent`(parameter integer, child INTEGER) RETURNS int
    READS SQL DATA
    DETERMINISTIC
BEGIN
	declare parent integer;
	set parent = child;
	REPEAT
		IF parent = parameter THEN 
			return true;
		END IF;
		SELECT Role$id INTO parent FROM Role WHERE  id = parent;
	UNTIL parent is null
	END REPEAT;
	return false;
END ;;
DELIMITER ;