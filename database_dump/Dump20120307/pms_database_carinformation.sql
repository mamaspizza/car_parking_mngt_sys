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
-- Table structure for table `carinformation`
--

DROP TABLE IF EXISTS `carinformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carinformation` (
  `carid` int(11) NOT NULL AUTO_INCREMENT,
  `platenumber` varchar(45) DEFAULT NULL,
  `carowner` varchar(45) DEFAULT NULL,
  `cartype` varchar(45) DEFAULT NULL,
  `cardno` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`carid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1 COMMENT='Information of the car';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carinformation`
--

LOCK TABLES `carinformation` WRITE;
/*!40000 ALTER TABLE `carinformation` DISABLE KEYS */;
INSERT INTO `carinformation` VALUES (1,'101','Romeo Mark Mateo','Nissan','-11110429999999934'),(2,'102','Jaewan Lee','Hyundai','-7830000000911'),(3,'103','Bobby Gerardo','Honda','-10397730000000000'),(4,'104','Frank Elijorde','Kia',''),(5,'105','John Doe','Ford',NULL),(6,'106','Jane Doe','Chevrolet',NULL),(7,'107','Bill Gates','BMW',NULL),(8,'108','Steve Jobs','Volkswagen',NULL),(9,'109','Chuck Norris','Ferrari',NULL),(10,'110','Bruce Lee','Toyota',NULL);
/*!40000 ALTER TABLE `carinformation` ENABLE KEYS */;
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
