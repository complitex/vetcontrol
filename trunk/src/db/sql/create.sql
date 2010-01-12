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


-- users --

/*Table structure for table `node` */

DROP TABLE IF EXISTS `node`;

CREATE TABLE `node` (
  `nodeId` int(10) unsigned NOT NULL auto_increment,
  `nodeName` varchar(45) NOT NULL,
  PRIMARY KEY  (`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(32) NOT NULL,
  `_password` varchar(32) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_login` (`login`),
  KEY `fk_user_department` (`department_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE  `usergroup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(32) NOT NULL,
  `usergroup` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_usergroup` (`login`,`usergroup`),
  CONSTRAINT `fk_user_login` FOREIGN KEY (`login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



 -- books --


-- auxiliary tables --

/*Table structure for table `locales` */

DROP TABLE IF EXISTS `locales`;

CREATE TABLE `locales` (
  `language` varchar(2) NOT NULL,
  `isSystem` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `generator` */
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
  `regnumber` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `country` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_registeredproducts_name` (`name`),
  CONSTRAINT `FK_registeredproducts_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_classificator` (`classificator`),
  CONSTRAINT `FK_registeredproducts_classificator` FOREIGN KEY (`classificator`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_countryref` (`country`),
  CONSTRAINT `FK_registeredproducts_countryref` FOREIGN KEY (`country`) REFERENCES `countrybook` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `vehicletypes` */

DROP TABLE IF EXISTS `vehicletypes`;

CREATE TABLE `vehicletypes` (
    `id` int(11) NOT NULL auto_increment,
    `name` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_vehicletypes_name` (`name`),
    CONSTRAINT `FK_vehicletypes_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `department` */

DROP TABLE IF EXISTS `department`;
CREATE TABLE  `department` (
    `id` int(11) NOT NULL auto_increment,
    `name` int(11) NOT NULL,
    `parent_id` int(11) NULL,
    PRIMARY KEY (`id`),
    KEY `FK_department_name` (`name`),
    CONSTRAINT `FK_department_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_department_parent` (`parent_id`),
    CONSTRAINT `FK_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `baseunits` */

--DROP TABLE IF EXISTS `baseunits`;
--
--CREATE TABLE `baseunits` (
--    `id` int(11) NOT NULL auto_increment,
--    `name` int(11) NOT NULL,
--    `parent_id` int(11) NULL,
----    `level` int(11) NOT NULL,
--    PRIMARY KEY (`id`),
--    KEY `FK_baseunits_name` (`name`),
--    CONSTRAINT `FK_baseunits_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
--    KEY `fk_baseunits_parent` (`parent_id`),
--    CONSTRAINT `fk_baseunits_parent` FOREIGN KEY (`parent_id`) REFERENCES `baseunits` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
