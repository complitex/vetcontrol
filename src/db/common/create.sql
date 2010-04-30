/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- users --

DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(32) NOT NULL,
  `_password` VARCHAR(32) DEFAULT NULL,
  `first_name` VARCHAR(45) DEFAULT NULL,
  `middle_name` VARCHAR(45) DEFAULT NULL,
  `last_name` VARCHAR(45) DEFAULT NULL,
   `job_id` BIGINT(20) DEFAULT NULL,
  `department_id` BIGINT(20) DEFAULT NULL,
   `passing_border_point_id` BIGINT(20) DEFAULT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  `locale` VARCHAR(2) NULL,
  `page_size` INT(3) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_login` (`login`),
  KEY `fk_job` (`job_id`),
  KEY `fk_user_department` (`department_id`),
  KEY `fk_border_point` (`passing_border_point_id`),
  CONSTRAINT `fk_job` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_border_point` FOREIGN KEY (`passing_border_point_id`) REFERENCES `passing_border_point` (`id`),
    KEY `user_updated_INDEX` (`updated`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE  `usergroup` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(32) NOT NULL,
  `usergroup` VARCHAR(32) NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_usergroup` (`login`,`usergroup`),
  CONSTRAINT `fk_user_login` FOREIGN KEY (`login`) REFERENCES `user` (`login`),
  KEY `usergroup_updated_INDEX` (`updated`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;



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
  `updated` timestamp NOT NULL DEFAULT NOW(),
  PRIMARY KEY  (`id`, `locale`),
    KEY `stringculture_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `countrybook` */

DROP TABLE IF EXISTS `countrybook`;

CREATE TABLE `countrybook` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(2) NOT NULL,
  `name` bigint(20) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT NOW(),
  /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `FK_countrybook_name` (`name`),
  CONSTRAINT `FK_countrybook_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `countrybook_updated_INDEX` (`updated`)
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
  `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `FK_registeredproducts_name` (`name`),
  CONSTRAINT `FK_registeredproducts_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_classificator` (`classificator`),
  CONSTRAINT `FK_registeredproducts_classificator` FOREIGN KEY (`classificator`) REFERENCES `stringculture` (`id`),
  KEY `FK_registeredproducts_country_ref` (`country_id`),
  CONSTRAINT `FK_registeredproducts_country_ref` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`),
  KEY `FK_registeredproducts_producer_ref` (`cargo_producer_id`),
  CONSTRAINT `FK_registeredproducts_producer_ref` FOREIGN KEY (`cargo_producer_id`) REFERENCES `cargo_producer` (`id`),
    KEY `registered_products_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `department` */

DROP TABLE IF EXISTS `department`;
CREATE TABLE  `department` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `parent_id` bigint(20) NULL,
    `custom_point_id` bigint(20) NULL,
    `level` int(2) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_department_name` (`name`),
    CONSTRAINT `FK_department_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_department_parent` (`parent_id`),
    CONSTRAINT `FK_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`),
    KEY `FK_department_custom_point` (`custom_point_id`),
    CONSTRAINT `FK_department_custom_point` FOREIGN KEY (`custom_point_id`) REFERENCES `customs_point` (`id`),
    KEY `department_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_sender` */

DROP TABLE IF EXISTS `cargo_sender`;
CREATE TABLE  `cargo_sender` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` varchar(100) NOT NULL,
    `country_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_cargo_sender_country_ref` (`country_id`),
    CONSTRAINT `FK_cargo_sender_country_ref` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_receiver` */

DROP TABLE IF EXISTS `cargo_receiver`;
CREATE TABLE  `cargo_receiver` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` varchar(100) NOT NULL,
    `address` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `customs_point` */

DROP TABLE IF EXISTS `customs_point`;
CREATE TABLE  `customs_point` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_customs_point_name` (`name`),
    CONSTRAINT `FK_customs_point_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `customs_point_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_producer` */

DROP TABLE IF EXISTS `cargo_producer`;
CREATE TABLE  `cargo_producer` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `country_id` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_cargo_producer_name` (`name`),
    CONSTRAINT `FK_cargo_producer_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_cargo_producer_country` (`country_id`),
    CONSTRAINT `FK_cargo_producer_country` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`),
    KEY `cargo_producer_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_type` */

DROP TABLE IF EXISTS `cargo_type`;
CREATE TABLE  `cargo_type` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `ukt_zed_code` VARCHAR(10) NOT NULL,
    `updated` timestamp NOT NULL,
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`ukt_zed_code`),
    KEY `FK_cargo_type_name` (`name`),
    CONSTRAINT `FK_cargo_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `cargo_type_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_mode_cargo_type` */
/* Link table between cargo_mode and cargo_type. */

DROP TABLE IF EXISTS `cargo_mode_cargo_type`;
CREATE TABLE  `cargo_mode_cargo_type` (
    `cargo_mode_id` bigint(20) NOT NULL,
    `cargo_type_id` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`cargo_mode_id`, `cargo_type_id`),
    KEY `FK_cargo_mode_cargo_type_cargo_mode_id` (`cargo_mode_id`),
    CONSTRAINT `FK_cargo_mode_cargo_type_cargo_mode_id` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `FK_cargo_mode_cargo_type_cargo_type_id` (`cargo_type_id`),
    CONSTRAINT `FK_cargo_mode_cargo_type_cargo_type_id` FOREIGN KEY (`cargo_type_id`) REFERENCES `cargo_type` (`id`),
    KEY `cargo_mode_cargo_type_updated_INDEX` (`updated`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_mode` */

DROP TABLE IF EXISTS `cargo_mode`;
CREATE TABLE  `cargo_mode` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `parent_id` bigint(20) NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_cargo_mode_name` (`name`),
    CONSTRAINT `FK_cargo_mode_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_cargo_mode_parent` (`parent_id`),
    CONSTRAINT `FK_cargo_mode` FOREIGN KEY (`parent_id`) REFERENCES `cargo_mode` (`id`),
    KEY `cargo_mode_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cargo_mode_unit_type` */
/* Link table between cargo_mode and unit_type. */

DROP TABLE IF EXISTS `cargo_mode_unit_type`;
CREATE TABLE  `cargo_mode_unit_type` (
    `cargo_mode_id` bigint(20) NOT NULL,
    `unit_type_id` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`cargo_mode_id`, `unit_type_id`),
    KEY `FK_cargo_mode_unit_type_cargo_mode_id` (`cargo_mode_id`),
    CONSTRAINT `FK_cargo_mode_unit_type_cargo_mode_id` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `FK_cargo_mode_unit_type_unit_type_id` (`unit_type_id`),
    CONSTRAINT `FK_cargo_mode_unit_type_unit_type_id` FOREIGN KEY (`unit_type_id`) REFERENCES `unit_type` (`id`),
    KEY `cargo_mode_unit_type_updated_INDEX` (`updated`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `unit_type` */

DROP TABLE IF EXISTS `unit_type`;
CREATE TABLE  `unit_type` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `short_name` bigint(20) NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_unit_type_name` (`name`),
    CONSTRAINT `FK_unit_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_unit_type_short_name` (`short_name`),
    CONSTRAINT `FK_unit_type_short_name` FOREIGN KEY (`short_name`) REFERENCES `stringculture` (`id`),
    KEY `unit_type_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `job` */

DROP TABLE IF EXISTS `job`;
CREATE TABLE  `job` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_job_name` (`name`),
    CONSTRAINT `FK_job_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `job_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arrest_reason` */

DROP TABLE IF EXISTS `arrest_reason`;
CREATE TABLE  `arrest_reason` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_arrest_reason_name` (`name`),
    CONSTRAINT `FK_arrest_reason_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `arrest_reason_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `bad_epizootic_situation` */

DROP TABLE IF EXISTS `bad_epizootic_situation`;
CREATE TABLE  `bad_epizootic_situation` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_bad_epizootic_situation_name` (`name`),
    CONSTRAINT `FK_bad_epizootic_situation_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `bad_epizootic_situation_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `passing_border_point` */

DROP TABLE IF EXISTS `passing_border_point`;
CREATE TABLE  `passing_border_point` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` varchar(100) NOT NULL,
    `department_id` bigint(20) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_passing_border_point_department` (`department_id`),
    CONSTRAINT `FK_passing_border_point_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
    KEY `passing_border_point_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `container_validator` */

DROP TABLE IF EXISTS `container_validator`;
CREATE TABLE  `container_validator` (
    `id` bigint(20) NOT NULL auto_increment,
    `prefix` varchar(4) NOT NULL,
    `carrier_abbr` varchar(50) NULL,
    `carrier_name` varchar(100) NOT NULL,
    `updated` timestamp NOT NULL DEFAULT NOW(),
 /* Represents state of object. When disabled column's value is 1, when enabled(by default) - 1. */
  `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `container_validator_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*documents*/
DROP TABLE IF EXISTS `document_cargo`;
CREATE TABLE `document_cargo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `department_id` bigint(20) NOT NULL,
  `creator_id` bigint(20) NOT NULL,
  `created` timestamp NOT NULL,
  `updated` timestamp NOT NULL,
  `movement_type` VARCHAR(15) NOT NULL,
  `vehicle_type` varchar(10) NOT NULL,

  `cargo_mode_id` bigint(20) DEFAULT NULL,

  `cargo_sender_name` varchar(100) NOT NULL,
  `cargo_sender_country_id` bigint(20) NOT NULL,

  `cargo_receiver_name` varchar(100) NOT NULL,
  `cargo_receiver_address` varchar(100) NOT NULL,

  `passing_border_point_id` bigint(20) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `detention_details` varchar(255) DEFAULT NULL,
  `sync_status` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`,`department_id`,`client_id`),
  KEY `FK_department_0` (`department_id`),
  KEY `FK_client_0` (`client_id`),
  KEY `FK_document_cargo_cargo_sender_country_id` (`cargo_sender_country_id`),
  KEY `FK_document_cargo_passing_border_point` (`passing_border_point_id`),
  KEY `FK_document_cargo_cargo_mode_id` (`cargo_mode_id`),
  CONSTRAINT `FK_department_0` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_client_0` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FK_document_cargo_cargo_sender_country_id` FOREIGN KEY (`cargo_sender_country_id`) REFERENCES `countrybook` (`id`),
  CONSTRAINT `FK_document_cargo_passing_border_point` FOREIGN KEY (`passing_border_point_id`) REFERENCES `passing_border_point` (`id`),
  CONSTRAINT `FK_document_cargo_cargo_mode_id` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `document_cargo_updated_INDEX` (`updated`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cargo`;
CREATE TABLE  `cargo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `department_id` bigint(20) NOT NULL,
  `document_cargo_id` bigint(20) NOT NULL,  
  `cargo_type_id` bigint(20) NOT NULL,
  `unit_type_id` bigint(20) DEFAULT NULL,
  `cargo_producer_id` bigint(20) NOT NULL,
  `vehicle_id` bigint(20) DEFAULT NULL,
  `count` DOUBLE (11,2) DEFAULT NULL,
  `certificate_date` date NOT NULL,
  `certificate_details` varchar(255) NOT NULL,
  `updated` timestamp DEFAULT NOW(),
  `sync_status` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`,`department_id`,`client_id`),
  KEY `FK_department_1` (`department_id`),
  KEY `FK_client_1` (`client_id`),
  KEY `FK_cargo_type` (`cargo_type_id`),
  KEY `FK_unit_type` (`unit_type_id`),
  KEY `FK_cargo_producer` (`cargo_producer_id`),
  KEY `FK_document_cargo` (`document_cargo_id`,`department_id`,`client_id`),
  KEY `FK_cargo_vehicle` (`vehicle_id`),
  CONSTRAINT `FK_department_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_client_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FK_cargo_type` FOREIGN KEY (`cargo_type_id`) REFERENCES `cargo_type` (`id`),
  CONSTRAINT `FK_unit_type` FOREIGN KEY (`unit_type_id`) REFERENCES `unit_type` (`id`),
  CONSTRAINT `FK_cargo_producer` FOREIGN KEY (`cargo_producer_id`) REFERENCES `cargo_producer` (`id`),
  CONSTRAINT `FK_document_cargo` FOREIGN KEY (`document_cargo_id`, `department_id`, `client_id`) REFERENCES `document_cargo` (`id`, `department_id`, `client_id`),
  CONSTRAINT `FK_cargo_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`),
  KEY `cargo_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE  `vehicle` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `client_id` bigint(20) NOT NULL,
    `department_id` bigint(20) NOT NULL,
    `document_cargo_id` bigint(20) NOT NULL,
    `vehicle_type` varchar(10) NOT NULL,
    `vehicle_details` varchar(255) NOT NULL,
    `updated` timestamp DEFAULT NOW(),
    `sync_status` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`,`department_id`,`client_id`),
    KEY `FK_vehicle_client` (`client_id`),
    KEY `FK_vehicle_department` (`department_id`),
    KEY `FK_vehicle_document_cargo` (`document_cargo_id`,`department_id`,`client_id`),
    CONSTRAINT `FK_vehicle_client` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
    CONSTRAINT `FK_vehicle_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
    CONSTRAINT `FK_vehicle_document_cargo` FOREIGN KEY (`document_cargo_id`, `department_id`, `client_id`) REFERENCES `document_cargo` (`id`, `department_id`, `client_id`),
    KEY `vehicle_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `log`;
CREATE TABLE  `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NULL,
  `date` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `controller_class` varchar(255) DEFAULT NULL,
  `model_class` varchar(255) DEFAULT NULL,
  `event` varchar(255) DEFAULT NULL,
  `module` varchar(255) DEFAULT NULL,       
  `status` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user` (`user_id`),
  KEY `Index_date` (`date`),
  KEY `Index_controller_class` (`controller_class`),
  KEY `Index_model_class` (`model_class`),
  KEY `Index_event` (`event`),
  KEY `Index_module` (`module`),
  KEY `Index_status` (`status`),
  KEY `Index_description` (`description`),
  CONSTRAINT `FK_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  KEY `FK_log_client_id` (`client_id`),
  CONSTRAINT `FK_log_client_id` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(64) NOT NULL,
  `mac` varchar(64) NOT NULL,
  `secure_key` varchar(64) NOT NULL,
  `created` timestamp NOT NULL,
  `updated` timestamp NOT NULL,
  `last_sync` DATETIME DEFAULT NULL,
  `department_id` bigint(20) NOT NULL,
  `sync_status` varchar(64) DEFAULT NULL,
  `version` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mac` (`mac`),
  UNIQUE KEY `secure_key` (`secure_key`),
  KEY `FK_department` (`department_id`),
  CONSTRAINT `FK_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
    KEY `client_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `deleted_embedded_id`
 This table contains entries which has been removed from any application table.
 This information may be useful at client synchronization and logging. */
DROP TABLE IF EXISTS `deleted_embedded_id`;
CREATE TABLE `deleted_embedded_id` (
  /* Id of removed entry. It may be any type but table contains string value of that type.
     If id is composite id then what this value will be is up to application. For example,
     concatenating of compound properties. */
  `id` VARCHAR(100) NOT NULL,
  /* The name of entity of removed entry. */
  `entity` VARCHAR(100) NOT NULL,
  /* Timestamp of deleting. */
  `deleted` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`, `entity`),
    KEY `deleted_embedded_id_deleted_INDEX` (`deleted`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/* Table structure for table `deleted_embedded_id`
   This table contains entries which has been removed from any application table.
   This information may be useful at client synchronization and logging.*/
DROP TABLE IF EXISTS `deleted_long_id`;
CREATE TABLE `deleted_long_id` (
  `id` bigint(20) NOT NULL, /* Long Id of removed entry.*/
  `entity` VARCHAR(100) NOT NULL, /* The name of entity of removed entry. */
  `deleted` TIMESTAMP NOT NULL, /* Time stamp of deleting. */
  PRIMARY KEY (`id`, `entity`),
    KEY `deleted_long_id_deleted_INDEX` (`deleted`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `client_update`;
CREATE TABLE  `client_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created` datetime NOT NULL,
  `type` varchar(64) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `client_update_item`;
CREATE TABLE  `client_update_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `update_id` bigint(20) NOT NULL,
  `created` datetime NOT NULL,
  `packaging` varchar(64) NOT NULL,
  `check_sum` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_update_id` (`update_id`),
  CONSTRAINT `FK_update_id` FOREIGN KEY (`update_id`) REFERENCES `client_update` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `arrest_document`;
CREATE TABLE `arrest_document` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `client_id` bigint(20) NOT NULL,
    `department_id` bigint(20) NOT NULL,
    `creator_id` bigint(20) NOT NULL,

    `arrest_date` TIMESTAMP NOT NULL,
    `arrest_reason_id` bigint(20) NOT NULL,
    `arrest_reason_details` VARCHAR(500) DEFAULT NULL,

    `passing_border_point_id` bigint(20) DEFAULT NULL,
    `count` DOUBLE (11,2) NOT NULL,
    `cargo_mode_id` bigint(20) NOT NULL,
    `certificate_date` DATE NOT NULL,
    `certificate_details` VARCHAR(255) NOT NULL,

    `cargo_sender_name` VARCHAR(100) NOT NULL,
    `cargo_sender_country_id` bigint(20) NOT NULL,

    `cargo_receiver_name` VARCHAR(100) NOT NULL,
    `cargo_receiver_address` VARCHAR(100) NOT NULL,

    `cargo_type_id` bigint(20) NOT NULL,
    `unit_type_id` bigint(20) NOT NULL,

    `vehicle_type` VARCHAR(10) NOT NULL,
    `vehicle_details` VARCHAR(255) NOT NULL,

    `document_cargo_created` TIMESTAMP NOT NULL,

    `updated` TIMESTAMP NOT NULL,
    `sync_status` VARCHAR(64) DEFAULT NULL,
    PRIMARY KEY (`id`, `client_id`, `department_id`),
    KEY `FK_arrest_client` (`client_id`),
    CONSTRAINT `FK_arrest_client` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
    KEY `FK_arrest_department` (`department_id`),
    CONSTRAINT `FK_arrest_cargo` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
    KEY `FK_arrest_reason` (`arrest_reason_id`),
    CONSTRAINT `FK_arrest_reason` FOREIGN KEY (`arrest_reason_id`) REFERENCES `arrest_reason` (`id`),
    KEY `FK_arrest_passing_border_point` (`passing_border_point_id`),
    CONSTRAINT `FK_arrest_passing_border_point` FOREIGN KEY (`passing_border_point_id`) REFERENCES `passing_border_point` (`id`),
    KEY `FK_arrest_cargo_mode` (`cargo_mode_id`),
    CONSTRAINT `FK_arrest_cargo_mode` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `FK_arrest_cargo_sender_country` (`cargo_sender_country_id`),
    CONSTRAINT `FK_arrest_cargo_sender_country` FOREIGN KEY (`cargo_sender_country_id`) REFERENCES `countrybook` (`id`),
    KEY `FK_arrest_cargo_type` (`cargo_type_id`),
    CONSTRAINT `FK_arrest_cargo_type` FOREIGN KEY (`cargo_type_id`) REFERENCES `cargo_type` (`id`),
    KEY `FK_arrest_unit_type` (`unit_type_id`),
    CONSTRAINT `FK_arrest_unit_type` FOREIGN KEY (`unit_type_id`) REFERENCES `unit_type` (`id`),
    KEY `arrest_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
