/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- users --

DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login` varchar(32) NOT NULL,
  `_password` varchar(32) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `locale` VARCHAR(2) NULL,
  `page_size` int(3) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_login` (`login`),
  KEY `fk_user_department` (`department_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE  `usergroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
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
  `id` bigint(20) NOT NULL,
  `locale` varchar(2) NOT NULL,
  `value` varchar(1024) default NULL,
  PRIMARY KEY  (`id`, `locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `countrybook` */

DROP TABLE IF EXISTS `countrybook`;

CREATE TABLE `countrybook` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(2) NOT NULL,
  `name` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_countrybook_name` (`name`),
  CONSTRAINT `FK_countrybook_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `registeredproducts` */

DROP TABLE IF EXISTS `registered_products`;

CREATE TABLE `registered_products` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` bigint(20) NOT NULL,
  `classificator` bigint(20) NOT NULL,
  `cargo_producer_id` bigint(20) NOT NULL,
  `regnumber` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `country_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_registeredproducts_name` (`name`),
  CONSTRAINT `FK_registeredproducts_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_classificator` (`classificator`),
  CONSTRAINT `FK_registeredproducts_classificator` FOREIGN KEY (`classificator`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_country_ref` (`country_id`),
  CONSTRAINT `FK_registeredproducts_country_ref` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`),
  KEY `FK_registeredproducts_producer_ref` (`cargo_producer_id`),
  CONSTRAINT `FK_registeredproducts_producer_ref` FOREIGN KEY (`cargo_producer_id`) REFERENCES `cargo_producer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `vehicletypes` */

DROP TABLE IF EXISTS `vehicletypes`;

CREATE TABLE `vehicletypes` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_vehicletypes_name` (`name`),
    CONSTRAINT `FK_vehicletypes_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `department` */

DROP TABLE IF EXISTS `department`;
CREATE TABLE  `department` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `parent_id` bigint(20) NULL,
    PRIMARY KEY (`id`),
    KEY `FK_department_name` (`name`),
    CONSTRAINT `FK_department_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_department_parent` (`parent_id`),
    CONSTRAINT `FK_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_sender` */

DROP TABLE IF EXISTS `cargo_sender`;
CREATE TABLE  `cargo_sender` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_cargo_sender_name` (`name`),
    CONSTRAINT `FK_cargo_sender_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_receiver` */

DROP TABLE IF EXISTS `cargo_receiver`;
CREATE TABLE  `cargo_receiver` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_cargo_receiver_name` (`name`),
    CONSTRAINT `FK_cargo_receiver_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `customs_point` */

DROP TABLE IF EXISTS `customs_point`;
CREATE TABLE  `customs_point` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_customs_point_name` (`name`),
    CONSTRAINT `FK_customs_point_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `movement_type` */

DROP TABLE IF EXISTS `movement_type`;
CREATE TABLE  `movement_type` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_movement_type_name` (`name`),
    CONSTRAINT `FK_movement_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_producer` */

DROP TABLE IF EXISTS `cargo_producer`;
CREATE TABLE  `cargo_producer` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_producer_name` (`name`),
    CONSTRAINT `FK_producer_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `unit_type` */

DROP TABLE IF EXISTS `unit_type`;
CREATE TABLE  `unit_type` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_unit_type_name` (`name`),
    CONSTRAINT `FK_unit_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_type` */

DROP TABLE IF EXISTS `cargo_type`;
CREATE TABLE  `cargo_type` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `ukt_zed_code` VARCHAR(10) NOT NULL,
    `controled` tinyint(1) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`ukt_zed_code`),
    KEY `FK_cargo_type_name` (`name`),
    CONSTRAINT `FK_cargo_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `job` */

DROP TABLE IF EXISTS `job`;
CREATE TABLE  `job` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_job_name` (`name`),
    CONSTRAINT `FK_job_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `prohibition_country` */

DROP TABLE IF EXISTS `prohibition_country`;
CREATE TABLE  `prohibition_country` (
    `id` bigint(20) NOT NULL auto_increment,
    `date` date NOT NULL,
    `number` VARCHAR(10) NOT NULL,
    `country_id` bigint(20) NOT NULL,
    `reason` bigint(20) NOT NULL,
    `region` bigint(20) NOT NULL,
    `target` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_prohibition_country_country_ref` (`country_id`),
    CONSTRAINT `FK_prohibition_country_country_ref` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`),
    KEY `FK_prohibition_country_reason` (`reason`),
    CONSTRAINT `FK_prohibition_country_reason` FOREIGN KEY (`reason`) REFERENCES `stringculture` (`id`),
    KEY `FK_prohibition_country_region` (`region`),
    CONSTRAINT `FK_prohibition_country_region` FOREIGN KEY (`region`) REFERENCES `stringculture` (`id`),
    KEY `FK_prohibition_country_target` (`target`),
    CONSTRAINT `FK_prohibition_country_target` FOREIGN KEY (`target`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arrest_reason` */

DROP TABLE IF EXISTS `arrest_reason`;
CREATE TABLE  `arrest_reason` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_arrest_reason_name` (`name`),
    CONSTRAINT `FK_arrest_reason_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `bad_epizootic_situation` */

DROP TABLE IF EXISTS `bad_epizootic_situation`;
CREATE TABLE  `bad_epizootic_situation` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_bad_epizootic_situation_name` (`name`),
    CONSTRAINT `FK_bad_epizootic_situation_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tariff` */

DROP TABLE IF EXISTS `tariff`;
CREATE TABLE  `tariff` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_tariff_name` (`name`),
    CONSTRAINT `FK_tariff_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `passing_border_point` */

DROP TABLE IF EXISTS `passing_border_point`;
CREATE TABLE  `passing_border_point` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_passing_border_point_name` (`name`),
    CONSTRAINT `FK_passing_border_point_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `addressbook` */

DROP TABLE IF EXISTS `addressbook`;
CREATE TABLE  `addressbook` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_addressbook_name` (`name`),
    CONSTRAINT `FK_addressbook_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*documents*/
DROP TABLE IF EXISTS `cargo`;
CREATE TABLE  `cargo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `document_cargo_id` bigint(20) NOT NULL,
  `cargo_type_id` bigint(20) NOT NULL,
  `unit_type_id` bigint(20) NOT NULL,
  `count` int(11) NOT NULL,
  `certificate_date` date NOT NULL,
  `certificate_details` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_document_cargo` (`document_cargo_id`),
  KEY `FK_cargo_type` (`cargo_type_id`),
  KEY `FK_unit_type` (`unit_type_id`),
  CONSTRAINT `FK_document_cargo` FOREIGN KEY (`document_cargo_id`) REFERENCES `document_cargo` (`id`),
  CONSTRAINT `FK_cargo_type` FOREIGN KEY (`cargo_type_id`) REFERENCES `cargo_type` (`id`),  
  CONSTRAINT `FK_unit_type` FOREIGN KEY (`unit_type_id`) REFERENCES `unit_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `document_cargo`;
CREATE TABLE `document_cargo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator_id` bigint(20) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime DEFAULT NULL,
  `movement_type_id` bigint(20) NOT NULL,
  `vehicle_type_id` bigint(20) NOT NULL,
  `vehicle_details` varchar(255) NOT NULL,
  `cargo_sender_id` bigint(20) NOT NULL,
  `cargo_receiver_id` bigint(20) NOT NULL,
  `cargo_producer_id` bigint(20) NOT NULL,
  `passing_border_point_id` bigint(20) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `detention_details` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_movement_type` (`movement_type_id`),
  KEY `FK_vehicle_type` (`vehicle_type_id`),
  KEY `FK_cargo_sender` (`cargo_sender_id`),
  KEY `FK_cargo_receiver` (`cargo_receiver_id`),
  KEY `FK_cargo_producer` (`cargo_producer_id`),
  KEY `FK_passing_border_point` (`passing_border_point_id`),
  CONSTRAINT `FK_movement_type` FOREIGN KEY (`movement_type_id`) REFERENCES `movement_type` (`id`),
  CONSTRAINT `FK_vehicle_type` FOREIGN KEY (`vehicle_type_id`) REFERENCES `vehicletypes` (`id`),
  CONSTRAINT `FK_cargo_sender` FOREIGN KEY (`cargo_sender_id`) REFERENCES `cargo_sender` (`id`),
  CONSTRAINT `FK_cargo_receiver` FOREIGN KEY (`cargo_receiver_id`) REFERENCES `cargo_receiver` (`id`),
  CONSTRAINT `FK_cargo_producer` FOREIGN KEY (`cargo_producer_id`) REFERENCES `cargo_producer` (`id`),
  CONSTRAINT `FK_passing_border_point` FOREIGN KEY (`passing_border_point_id`) REFERENCES `passing_border_point` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
