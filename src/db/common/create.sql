/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


/* Пользователи */

/* Талица пользователей системы */
DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  /* Логин */
  `login` VARCHAR(32) NOT NULL,
  /* Пароль */
  `_password` VARCHAR(32) DEFAULT NULL,
  /* Имя */
  `first_name` VARCHAR(45) DEFAULT NULL,
  /* Отчество */
  `middle_name` VARCHAR(45) DEFAULT NULL,
  /* Фамилия */
  `last_name` VARCHAR(45) DEFAULT NULL,
  /* Должность пользователя */
  `job_id` BIGINT(20) DEFAULT NULL,
   /* Подразделение, в котором работает пользователь */
  `department_id` BIGINT(20) DEFAULT NULL,
  /* Пункт пропуска через границу, к которому принадлежит пользователь */
  `passing_border_point_id` BIGINT(20) DEFAULT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  /* Язык, на котором пользователь предпочитает работать с системой */
  `locale` VARCHAR(2) NULL,
  /* Количество записей на страницу, которое пользователь предпочитает использовать на страницах просмотра списка сущностей */
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

/* Таблица ролей пользователей */
DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE  `usergroup` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  /* Логин пользователя */
  `login` VARCHAR(32) NOT NULL,
  /* Роль пользователя */
  `usergroup` VARCHAR(32) NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_usergroup` (`login`,`usergroup`),
  CONSTRAINT `fk_user_login` FOREIGN KEY (`login`) REFERENCES `user` (`login`),
  KEY `usergroup_updated_INDEX` (`updated`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/* Вспомогательные таблицы */

/* Таблица поддерживаемых системой языков */
DROP TABLE IF EXISTS `locales`;
CREATE TABLE `locales` (
  /* ISO 639 код языка */
  `language` varchar(2) NOT NULL,
  /* Системный язык является языком по умолчанию для системы */
  `isSystem` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Вспомогательная таблица для хранения значений, генерируемых табличными генераторами */
DROP TABLE IF EXISTS `generator`;
create table `generator`(
   /* Наименование генератора */
   `generatorName` varchar(20) NOT NULL ,
   /* Значение генератора */
   `generatorValue` bigint UNSIGNED NOT NULL DEFAULT '0' ,
   PRIMARY KEY (`generatorName`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Таблица для хранения локализованных строк в справочниках */
DROP TABLE IF EXISTS `stringculture`;
CREATE TABLE `stringculture` (
  `id` bigint(20) NOT NULL,
  /* Код языка */
  `locale` varchar(2) NOT NULL,
  /* Локализованное значение */
  `value` varchar(1024) default NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp NOT NULL DEFAULT NOW(),
  PRIMARY KEY  (`id`, `locale`),
    KEY `stringculture_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочники */

/* Справочник стран */
DROP TABLE IF EXISTS `countrybook`;
CREATE TABLE `countrybook` (
  `id` bigint(20) NOT NULL auto_increment,
  /* ISO 3166 двух символьный код страны */
  `code` varchar(2) NOT NULL,
  /* Название страны */
  `name` bigint(20) NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp NOT NULL DEFAULT NOW(),
  /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
  `disabled` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `FK_countrybook_name` (`name`),
  CONSTRAINT `FK_countrybook_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `countrybook_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник перечня препаратов, зарегестрированных в Украине */
DROP TABLE IF EXISTS `registered_products`;
CREATE TABLE `registered_products` (
  `id` bigint(20) NOT NULL auto_increment,
  /* Наименование препарата */
  `name` bigint(20) NOT NULL,
  /* Классификатор */
  `classificator` bigint(20) NOT NULL,
  /* Производитель */
  `cargo_producer_id` bigint(20) NOT NULL,
  /* Регистрационный номер */
  `regnumber` varchar(50) NOT NULL,
  /* Дата регистрации */
  `date` date NOT NULL,
  /* Страна производителя */
  `country_id` bigint(20) NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp NOT NULL DEFAULT NOW(),
  /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
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

/* Справочник структурных единиц */
DROP TABLE IF EXISTS `department`;
CREATE TABLE  `department` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование подразделения */
    `name` bigint(20) NOT NULL,
    /* Идентификатор вышестоящего подразделения */
    `parent_id` bigint(20) NULL,
    /* Место таможенного оформления грузов */
    `custom_point_id` bigint(20) NULL,
    /* Уровень данного подразделения в 3-х уровневой иерархии */
    `level` int(2) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
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

/* Не используемые пока справочники */
-- DROP TABLE IF EXISTS `cargo_sender`;
-- CREATE TABLE  `cargo_sender` (
--     `id` bigint(20) NOT NULL auto_increment,
--     `name` varchar(100) NOT NULL,
--     `country_id` bigint(20) NOT NULL,
--     PRIMARY KEY (`id`),
--     KEY `FK_cargo_sender_country_ref` (`country_id`),
--     CONSTRAINT `FK_cargo_sender_country_ref` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- DROP TABLE IF EXISTS `cargo_receiver`;
-- CREATE TABLE  `cargo_receiver` (
--     `id` bigint(20) NOT NULL auto_increment,
--     `name` varchar(100) NOT NULL,
--     `address` varchar(100) NOT NULL,
--     PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник мест таможенного оформления грузов */
DROP TABLE IF EXISTS `customs_point`;
CREATE TABLE  `customs_point` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование */
    `name` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_customs_point_name` (`name`),
    CONSTRAINT `FK_customs_point_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `customs_point_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник производителей */
DROP TABLE IF EXISTS `cargo_producer`;
CREATE TABLE  `cargo_producer` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование */
    `name` bigint(20) NOT NULL,
    /* Страна производителя */
    `country_id` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_cargo_producer_name` (`name`),
    CONSTRAINT `FK_cargo_producer_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_cargo_producer_country` (`country_id`),
    CONSTRAINT `FK_cargo_producer_country` FOREIGN KEY (`country_id`) REFERENCES `countrybook` (`id`),
    KEY `cargo_producer_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник категорий(типов) грузов */
DROP TABLE IF EXISTS `cargo_type`;
CREATE TABLE  `cargo_type` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование категории грузов */
    `name` bigint(20) NOT NULL,
    /* Уникальный УКТ ЗЕД код категории грузов */
    `ukt_zed_code` VARCHAR(10) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL,
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`ukt_zed_code`),
    KEY `FK_cargo_type_name` (`name`),
    CONSTRAINT `FK_cargo_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `cargo_type_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Таблица, связывающая виды грузов и категории грузов */
DROP TABLE IF EXISTS `cargo_mode_cargo_type`;
CREATE TABLE  `cargo_mode_cargo_type` (
    `cargo_mode_id` bigint(20) NOT NULL,
    `cargo_type_id` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`cargo_mode_id`, `cargo_type_id`),
    KEY `FK_cargo_mode_cargo_type_cargo_mode_id` (`cargo_mode_id`),
    CONSTRAINT `FK_cargo_mode_cargo_type_cargo_mode_id` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `FK_cargo_mode_cargo_type_cargo_type_id` (`cargo_type_id`),
    CONSTRAINT `FK_cargo_mode_cargo_type_cargo_type_id` FOREIGN KEY (`cargo_type_id`) REFERENCES `cargo_type` (`id`),
    KEY `cargo_mode_cargo_type_updated_INDEX` (`updated`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник видов грузов */
DROP TABLE IF EXISTS `cargo_mode`;
CREATE TABLE  `cargo_mode` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование вида грузов */
    `name` bigint(20) NOT NULL,
    /* Идентификатор родителького вида грузов в 2-х уровневой иерархии видов грузов */
    `parent_id` bigint(20) NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_cargo_mode_name` (`name`),
    CONSTRAINT `FK_cargo_mode_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_cargo_mode_parent` (`parent_id`),
    CONSTRAINT `FK_cargo_mode` FOREIGN KEY (`parent_id`) REFERENCES `cargo_mode` (`id`),
    KEY `cargo_mode_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Таблица, связывающая виды грузов и единицы измерения */
DROP TABLE IF EXISTS `cargo_mode_unit_type`;
CREATE TABLE  `cargo_mode_unit_type` (
    `cargo_mode_id` bigint(20) NOT NULL,
    `unit_type_id` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`cargo_mode_id`, `unit_type_id`),
    KEY `FK_cargo_mode_unit_type_cargo_mode_id` (`cargo_mode_id`),
    CONSTRAINT `FK_cargo_mode_unit_type_cargo_mode_id` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`),
    KEY `FK_cargo_mode_unit_type_unit_type_id` (`unit_type_id`),
    CONSTRAINT `FK_cargo_mode_unit_type_unit_type_id` FOREIGN KEY (`unit_type_id`) REFERENCES `unit_type` (`id`),
    KEY `cargo_mode_unit_type_updated_INDEX` (`updated`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник единиц измерения */
DROP TABLE IF EXISTS `unit_type`;
CREATE TABLE  `unit_type` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование единицы измерения */
    `name` bigint(20) NOT NULL,
    /* Сокращенное название единицы измрения */
    `short_name` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_unit_type_name` (`name`),
    CONSTRAINT `FK_unit_type_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `FK_unit_type_short_name` (`short_name`),
    CONSTRAINT `FK_unit_type_short_name` FOREIGN KEY (`short_name`) REFERENCES `stringculture` (`id`),
    KEY `unit_type_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник должностей */
DROP TABLE IF EXISTS `job`;
CREATE TABLE  `job` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Название должности */
    `name` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_job_name` (`name`),
    CONSTRAINT `FK_job_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `job_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник причин задержания грузов */
DROP TABLE IF EXISTS `arrest_reason`;
CREATE TABLE  `arrest_reason` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Наименование причины */
    `name` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_arrest_reason_name` (`name`),
    CONSTRAINT `FK_arrest_reason_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `arrest_reason_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник стран с неблагоприятной эпизоотической ситуацией */
DROP TABLE IF EXISTS `bad_epizootic_situation`;
CREATE TABLE  `bad_epizootic_situation` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Название страны */
    `name` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_bad_epizootic_situation_name` (`name`),
    CONSTRAINT `FK_bad_epizootic_situation_name` FOREIGN KEY (`name`) REFERENCES `stringculture` (`id`),
    KEY `bad_epizootic_situation_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник пунктов пропуска через границу */
DROP TABLE IF EXISTS `passing_border_point`;
CREATE TABLE  `passing_border_point` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Название пункта */
    `name` varchar(100) NOT NULL,
    /* Подразделение, к которому относится данный пункт пропуска */
    `department_id` bigint(20) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `FK_passing_border_point_department` (`department_id`),
    CONSTRAINT `FK_passing_border_point_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
    KEY `passing_border_point_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Справочник кодов контейнеров */
DROP TABLE IF EXISTS `container_validator`;
CREATE TABLE  `container_validator` (
    `id` bigint(20) NOT NULL auto_increment,
    /* Уникальный 4-х значный префикс кода контейнера */
    `prefix` varchar(4) NOT NULL,
    /* Аббравиатура фирмы-производителя контейнера */
    `carrier_abbr` varchar(50) NULL,
    /* Полное название фирмы-производителя контейнера */
    `carrier_name` varchar(100) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp NOT NULL DEFAULT NOW(),
    /* Состояние объекта. Если значение равно 1, то объект деактивирован, если 0 - то активирован */
    `disabled` tinyint(1) NOT NULL default '0',
    PRIMARY KEY (`id`),
    KEY `container_validator_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Вспомогательная таблица связывающая таблицу видов грузов и таблицу отчетов. Показывает какие виды грузов используются в каких отчетах */
DROP TABLE IF EXISTS `cargo_mode_report`;
CREATE TABLE `cargo_mode_report` (
    /* Идентификатор вида грузов */
    `cargo_mode_id` bigint(20) NOT NULL,
    /* Идентификатор отчета */
    `report_id` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`cargo_mode_id`, `report_id`),
    KEY `FK_cargo_mode_report_cargo_mode` (`cargo_mode_id`),
    CONSTRAINT `FK_cargo_mode_report_cargo_mode` FOREIGN KEY (`cargo_mode_id`) REFERENCES `cargo_mode` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Отчеты */

/* Талица, перечисляющая все отчеты в системе. Используется например в таблице `cargo_mode_report` */
DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports` (
    `id` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Документы */

/* Карточка на груз */
DROP TABLE IF EXISTS `document_cargo`;
CREATE TABLE `document_cargo` (
  /* Суррогатный идентификатор */
  `id` bigint(20) NOT NULL,
  /* Идентификатор удаленного клиента */
  `client_id` bigint(20) NOT NULL,
  /* Подразделение */
  `department_id` bigint(20) NOT NULL,
  /* Идентификатор пользователя, создавшего карточку */
  `creator_id` bigint(20) NOT NULL,
  /* Дата создания карточки */
  `created` timestamp NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp NOT NULL,
  /* Тип передвижения. Все возможные значения перечислены в MovementType.class */
  `movement_type` VARCHAR(15) NOT NULL,
  /* Тип транспортного средства. Все возможные значения перечислены в VehicleType.class */
  `vehicle_type` varchar(10) NOT NULL,
  /* Вид груза */
  `cargo_mode_id` bigint(20) NOT NULL,
  /* Наименование отправителя грузов */
  `cargo_sender_name` varchar(255) NOT NULL,
  /* Страна отправителя */
  `cargo_sender_country_id` bigint(20) NOT NULL,
  /* Наименование получателя */
  `cargo_receiver_name` varchar(255) NOT NULL,
  /* Адрес получателя */
  `cargo_receiver_address` varchar(255) NOT NULL,
  /* Пункт пропуска через границу, на котором была оформлена данная карточка */
  `passing_border_point_id` bigint(20) DEFAULT NULL,
  /* Замечания */
  `details` varchar(1024) DEFAULT NULL,
  /* Детали задержания */
  `detention_details` varchar(255) DEFAULT NULL,
  /* Статус синронизации с сервером. Все возможные значения перечислены в Synchronized.SyncStatus.class */
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

/* Грузы */
DROP TABLE IF EXISTS `cargo`;
CREATE TABLE  `cargo` (
  /* Суррогатный идентификатор */
  `id` bigint(20) NOT NULL,
  /* Идентификатор удаленного клиента */
  `client_id` bigint(20) NOT NULL,
  /* Подразделение */
  `department_id` bigint(20) NOT NULL,
  /* Суррогатный идентификатор карточки на грузы, к которой данный груз принадлежит */
  `document_cargo_id` bigint(20) NOT NULL,
  /* Категория груза */
  `cargo_type_id` bigint(20) NOT NULL,
  /* Единица измерения */
  `unit_type_id` bigint(20) NOT NULL,
  /* Производитель груза */
  `cargo_producer_id` bigint(20) NOT NULL,
  /* Суррогатный идентификатор транспортного средства */
  `vehicle_id` bigint(20) NOT NULL,
  /* Количество груза */
  `count` DOUBLE (11,2) DEFAULT NULL,
  /* Дата сертификации груза */
  `certificate_date` date NOT NULL,
  /* Детали сертификации груза */
  `certificate_details` varchar(255) NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp DEFAULT NOW(),
  /* Статус синронизации с сервером. Все возможные значения перечислены в Synchronized.SyncStatus.class */
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

/* Таблица транспортных средств */
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE  `vehicle` (
    /* Суррогатный идентификатор */
    `id` bigint(20) NOT NULL,
    /* Идентификатор удаленного клиента */
    `client_id` bigint(20) NOT NULL,
    /* Подразделение */
    `department_id` bigint(20) NOT NULL,
    /* Суррогатный идентификатор карточки на грузы, которой данное транспортное средство принадлежит */
    `document_cargo_id` bigint(20) NOT NULL,
    /* Тип транспортного средства. Все возможные значения перечислены в VehicleType.class */
    `vehicle_type` varchar(10) NOT NULL,
    /* Детали транспортного средства(например, номер автомобиля) */
    `vehicle_details` varchar(255) NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` timestamp DEFAULT NOW(),
    /* Статус синронизации с сервером. Все возможные значения перечислены в Synchronized.SyncStatus.class */
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

/* Журнал логов приложения */
DROP TABLE IF EXISTS `log`;
CREATE TABLE  `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  /* Идентификатор удаленного клиента */
  `client_id` bigint(20) DEFAULT NULL,
  /* Дата */
  `date` datetime DEFAULT NULL,
  /* Пользователь, инициировавший логирование */
  `user_id` bigint(20) DEFAULT NULL,
  /* Страница(или другой класс), в котором произошла некая операция */
  `controller_class` varchar(255) DEFAULT NULL,
  /* Класс, над которым произошло действие */
  `model_class` varchar(255) DEFAULT NULL,
  /* Тип операции. Все возможные значения перечислены в Log.EVENT.class */
  `event` varchar(255) DEFAULT NULL,
  /* Модуль, в котором произошла операция. Все возможные значения перечислены в Log.MODULE.class*/
  `module` varchar(255) DEFAULT NULL,
  /* Успешность завершения операции. Все возможные значения перечислены в Log.STATUS.class */
  `status` varchar(255) DEFAULT NULL,
  /* Дополнительное описание */
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

/* Объект удаленного клиента */
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  /* Подразделение */
  `department_id` bigint(20) NOT NULL,
  /* Пункт пропуска через границу */
  `passing_border_point_id` bigint(20) DEFAULT NULL,
  /* IP адрес клиента */
  `ip` varchar(64) NOT NULL,
  /* MAC адрес клиента */
  `mac` varchar(64) NOT NULL,
  /* Защитный ключ */
  `secure_key` varchar(64) NOT NULL,
  /* Дата создания клиента */
  `created` timestamp NOT NULL,
  /* Дата последней модификации(создание/обновление) записи */
  `updated` timestamp NOT NULL,
  /* Дата последней синхронизации с сервером */
  `last_sync` DATETIME DEFAULT NULL,
  /* Статус синронизации с сервером. Все возможные значения перечислены в Synchronized.SyncStatus.class */
  `sync_status` varchar(64) DEFAULT NULL,
  /*TODO: add comment */
  `version` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mac` (`mac`),
  UNIQUE KEY `secure_key` (`secure_key`),
  KEY `FK_department` (`department_id`),
  CONSTRAINT `FK_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  KEY `FK_passing_border_point` (`passing_border_point_id`),
  CONSTRAINT `FK_passing_border_point` FOREIGN KEY (`passing_border_point_id`) REFERENCES `passing_border_point` (`id`),
  KEY `client_updated_INDEX` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* Удаленные из других таблиц записи, идентификаторы которых имеют сложное строение. Используется при синхронизации, например. */
DROP TABLE IF EXISTS `deleted_embedded_id`;
CREATE TABLE `deleted_embedded_id` (
  /* Идентификатор удаленной записи. */
  `id` VARCHAR(100) NOT NULL,
  /* Наименование удаленной сущности */
  `entity` VARCHAR(100) NOT NULL,
  /* Дата удаления */
  `deleted` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`, `entity`),
    KEY `deleted_embedded_id_deleted_INDEX` (`deleted`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/* Удаленные из других таблиц записи, идентификаторы которых имеют простое строение. Используется при синхронизации, например.*/
DROP TABLE IF EXISTS `deleted_long_id`;
CREATE TABLE `deleted_long_id` (
  /* Идентификатор удаленной записи. */
  `id` bigint(20) NOT NULL,
  /* Наименование удаленной сущности */
  `entity` VARCHAR(100) NOT NULL,
  /* Дата удаления */
  `deleted` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`, `entity`),
    KEY `deleted_long_id_deleted_INDEX` (`deleted`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*TODO: add comments */
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

/*TODO: add comments */
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

/* Таблица задержаний грузов */
DROP TABLE IF EXISTS `arrest_document`;
CREATE TABLE `arrest_document` (
    /* Суррогатный идентификатор */
    `id` bigint(20) NOT NULL,
    /* Идентификатор удаленного клиента */
    `client_id` bigint(20) NOT NULL,
    /* Подразделение */
    `department_id` bigint(20) NOT NULL,
    /* Идентификатор пользователя, создавшего документ */
    `creator_id` bigint(20) NOT NULL,
    /* Дата задержания */
    `arrest_date` TIMESTAMP NOT NULL,
    /* Причина задержания */
    `arrest_reason_id` bigint(20) NOT NULL,
    /* Детали задержания */
    `arrest_reason_details` VARCHAR(1024) DEFAULT NULL,
    /* Пункт пропуска через границу */
    `passing_border_point_id` bigint(20) DEFAULT NULL,
    /* Количество груза */
    `count` DOUBLE (11,2) DEFAULT NULL,
    /* Вид груза */
    `cargo_mode_id` bigint(20) NOT NULL,
    /* Дата сертификации груза */
    `certificate_date` DATE NOT NULL,
    /* Детали сертификации груза */
    `certificate_details` VARCHAR(255) NOT NULL,
    /* Наименование отправителя грузов */
    `cargo_sender_name` VARCHAR(255) NOT NULL,
    /* Страна отправителя */
    `cargo_sender_country_id` bigint(20) NOT NULL,
    /* Наименование получателя */
    `cargo_receiver_name` VARCHAR(255) NOT NULL,
    /* Адрес получателя */
    `cargo_receiver_address` VARCHAR(255) NOT NULL,
    /* Категория груза */
    `cargo_type_id` bigint(20) NOT NULL,
    /* Единица измерения */
    `unit_type_id` bigint(20) DEFAULT NULL,
    /* Тип транспортного средства. Все возможные значения перечислены в VehicleType.class */
    `vehicle_type` VARCHAR(10) NOT NULL,
    /* Детали транспортного средства(например, номер автомобиля) */
    `vehicle_details` VARCHAR(255) NOT NULL,
    /* Дата создания карточки на грузы, груз которой был задержан */
    `document_cargo_created` TIMESTAMP NOT NULL,
    /* Дата последней модификации(создание/обновление) записи */
    `updated` TIMESTAMP NOT NULL,
    /* Статус синронизации с сервером. Все возможные значения перечислены в Synchronized.SyncStatus.class */
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
