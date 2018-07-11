-- MySQL dump 10.14  Distrib 5.5.50-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: gate
-- ------------------------------------------------------
-- Server version	5.5.50-MariaDB-1~trusty

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Auth` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Role$id` int(10) unsigned DEFAULT NULL,
  `Uzer$id` int(10) unsigned DEFAULT NULL,
  `module` varchar(32) DEFAULT NULL,
  `screen` varchar(32) DEFAULT NULL,
  `action` varchar(32) DEFAULT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `mode` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Auth$fk1_idx` (`Role$id`),
  KEY `Auth$fk2_idx` (`Uzer$id`),
  CONSTRAINT `Auth$fk1` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`),
  CONSTRAINT `Auth$fk2` FOREIGN KEY (`Uzer$id`) REFERENCES `Uzer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Msg`
--

DROP TABLE IF EXISTS `Msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Msg` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sender` varchar(64) DEFAULT NULL,
  `receiver` varchar(64) NOT NULL,
  `dispatch` datetime NOT NULL,
  `failures` tinyint(2) NOT NULL,
  `mimeMail` mediumtext NOT NULL,
  `exception` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5379 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Org`
--

DROP TABLE IF EXISTS `Org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Org` (
  `orgID` varchar(16) NOT NULL,
  `name` varchar(256) NOT NULL,
  `icon` longblob NOT NULL,
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
  `sat$time2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`orgID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `master` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `Role$id` int(10) unsigned DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `Manager$id` int(10) unsigned DEFAULT NULL,
  `roleID` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roleID_UNIQUE` (`roleID`),
  KEY `Role$fk1_idx` (`Manager$id`),
  CONSTRAINT `Role$fk1` FOREIGN KEY (`Manager$id`) REFERENCES `Uzer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Uzer`
--

DROP TABLE IF EXISTS `Uzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `cellPhone` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_Uzer_userID` (`userID`),
  KEY `Uzer$fk1_idx` (`Role$id`),
  CONSTRAINT `Uzer$fk1` FOREIGN KEY (`Role$id`) REFERENCES `Role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-02  7:32:00
