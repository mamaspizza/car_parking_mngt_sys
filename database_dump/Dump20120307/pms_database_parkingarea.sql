CREATE DATABASE  IF NOT EXISTS `pms_database` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `pms_database`;
-- MySQL dump 10.13  Distrib 5.1.40, for Win32 (ia32)
--
-- Host: localhost    Database: pms_database
-- ------------------------------------------------------
-- Server version	5.5.14

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
-- Table structure for table `parkingarea`
--

DROP TABLE IF EXISTS `parkingarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parkingarea` (
  `idParkingArea` int(11) NOT NULL AUTO_INCREMENT,
  `Mainlocation` varchar(200) DEFAULT NULL,
  `LocationHint` varchar(200) DEFAULT NULL,
  `Area` varchar(45) DEFAULT NULL,
  `NumberofSlots` int(11) DEFAULT NULL,
  PRIMARY KEY (`idParkingArea`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COMMENT='Information of parking area location';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parkingarea`
--

LOCK TABLES `parkingarea` WRITE;
/*!40000 ALTER TABLE `parkingarea` DISABLE KEYS */;
INSERT INTO `parkingarea` VALUES (1,'School of Electronic and Information Engineering','Near the school of electronic bldg.','6',20),(2,'Natural Science Bldg.','Near the school of electronic bldg.','2',20),(3,'Student center','Near the school of electronic bldg.','1',20),(4,'Humanities bldg.','Near the school of electronic bldg.','3',20),(5,'Digital Information Center','Near the school of electronic bldg.','4',20),(6,'Marine Science','Near the school of electronic bldg.','5',20);
/*!40000 ALTER TABLE `parkingarea` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-03-07 22:07:35
