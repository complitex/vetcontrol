/*
SQLyog Enterprise - MySQL GUI v8.14 
MySQL - 5.0.88-community-nt : Database - project1
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `project1`;

/*Table structure for table `book1` */

DROP TABLE IF EXISTS `book1`;

CREATE TABLE `book1` (
  `key1` int(11) NOT NULL auto_increment,
  `value1` varchar(20) default NULL,
  `value2` varchar(20) default NULL,
  `value3` varchar(100) default NULL,
  `dateValue` date default NULL,
  PRIMARY KEY  (`key1`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `book2` */

DROP TABLE IF EXISTS `book2`;

CREATE TABLE `book2` (
  `key1` int(11) NOT NULL auto_increment,
  `val1` varchar(20) default NULL,
  `val2` varchar(50) default NULL,
  `val3` varchar(50) default NULL,
  `val4` varchar(100) default NULL,
  PRIMARY KEY  (`key1`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `node` */

DROP TABLE IF EXISTS `node`;

CREATE TABLE `node` (
  `nodeId` int(10) unsigned NOT NULL auto_increment,
  `nodeName` varchar(45) NOT NULL,
  PRIMARY KEY  (`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `role` varchar(45) NOT NULL,
  `userName` varchar(45) NOT NULL,
  PRIMARY KEY  (`role`,`userName`),
  KEY `roleToUser` (`userName`),
  CONSTRAINT `roleToRoles` FOREIGN KEY (`role`) REFERENCES `roles` (`name`),
  CONSTRAINT `roleToUser` FOREIGN KEY (`userName`) REFERENCES `user` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `roles` */

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `name` varchar(45) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `name` varchar(45) NOT NULL,
  `login` varchar(45) default NULL,
  `password` varchar(45) NOT NULL,
  `parentNodeId` int(10) unsigned default NULL,
  `blocked` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`name`),
  KEY `userToParentNode` (`parentNodeId`),
  CONSTRAINT `userToParentNode` FOREIGN KEY (`parentNodeId`) REFERENCES `node` (`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




-- auxiliary tables --

/*Table structure for table `locales` */

DROP TABLE IF EXISTS `locales`;

CREATE TABLE `locales` (
  `language` varchar(2) NOT NULL,
  `isSystem` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `generator`;

create table `generator`(
   `generatorName` varchar(20) NOT NULL ,
   `generatorValue` bigint UNSIGNED NOT NULL DEFAULT '0' ,
   PRIMARY KEY (`generatorName`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `stringculture` */

DROP TABLE IF EXISTS `stringculture`;

CREATE TABLE `stringculture` (
  `id` int(11) NOT NULL,
  `locale` varchar(2) NOT NULL,
  `value` varchar(1024) default NULL,
  PRIMARY KEY  (`id`, `locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 -- books --

/*Table structure for table `countrybook` */

DROP TABLE IF EXISTS `countrybook`;

CREATE TABLE `countrybook` (
  `id` int(11) NOT NULL auto_increment,
  `code` varchar(2) NOT NULL,
  `name` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_countrybook_name` (`name`),
  CONSTRAINT `FK_countrybook_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `registeredproducts` */

DROP TABLE IF EXISTS `registeredproducts`;

CREATE TABLE `registeredproducts` (
  `id` int(11) NOT NULL auto_increment,
  `name` int(11) NOT NULL,
  `classificator` int(11) NOT NULL,
  `vendor` varchar(50) NOT NULL,
  `country` varchar(2) NOT NULL,
  `regnumber` varchar(50) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_registeredproducts_name` (`name`),
  CONSTRAINT `FK_registeredproducts_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_classificator` (`classificator`),
  CONSTRAINT `FK_registeredproducts_classificator` FOREIGN KEY (`classificator`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
