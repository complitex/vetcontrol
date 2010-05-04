INSERT INTO `user`(`id`, `login`, `_password`, `last_name`, `first_name`, `middle_name`, `department_id`, `passing_border_point_id`) VALUES
(2,'admin','21232f297a57a5a743894a0e4a801fc3','Иванов','Иван','Иванович',1, NULL),
(17,'login_0','480335867a6c40d7bd4cf04e184910cc','Петров','Петр','Петрович',1, NULL),
(18,'login_1','e81b90f3ee27f42bb010251021b1fc09','Сидоров','Сидор','Сидорович',2, NULL),
(19,'login_2','3c681bd9a527167284e1f67a9da4f4dc','Александров','Александр','Александрович',3, 1),
(20,'login_3','3466c36ad772ab4e32e6ea6e7a59e142','Васильев','Василий','Васильевич',3, 2),
(21,'login_4','97b701a71f43ccd4dca24feb29df7437','Николаев','Николай','Николаевич',3, 3);

INSERT INTO `usergroup` (`id`, `login`, `usergroup`) VALUES
(2,'admin','ADMINISTRATORS'),(13,'login_0','DEPARTMENT_OFFICERS'),
(14,'login_1','DEPARTMENT_OFFICERS'),(15,'login_2','LOCAL_OFFICERS'),
(16,'login_3','LOCAL_OFFICERS'),(17,'login_4','LOCAL_OFFICERS');

INSERT INTO `stringculture`(`id`, `locale`, `value`) VALUES
(10045,'en','Получатель 1'),(10045,'ru','Получатель 1'),(10046,'en','Получатель 2'),(10046,'ru','Получатель 2'),
(10047,'en','Получатель 3'),(10047,'ru','Получатель 3'),(10048,'en','Отправитель 1'),(10048,'ru','Отправитель 1'),
(10049,'en','Отправитель 2'),(10049,'ru','Отправитель 2'),(10050,'en','Отправитель 3'),(10050,'ru','Отправитель 3'),
(10051,'en','Производитель 1'),(10051,'ru','Производитель 1'),
(10052,'en','Производитель 2'),(10052,'ru','Производитель 2'),(10053,'en','Производитель 3'),(10053,'ru','Производитель 3'),
(10054,'en','тонна'),(10054,'ru','тонна'),(10055,'en','голова'),(10055,'ru','голова'),(10056,'en','тыс. голов'),(10056,'ru','тыс. голов'),
(10057,'en','штука'),(10057,'ru','штука'),(10058,'en','тыс. штук'),(10058,'ru','тыс. штук'),(10059,'en','литр'),(10059,'ru','литр'),
(10060, 'ru', 'т.'), (10060, 'en', 'т.'), (10061, 'ru', 'г.'), (10061, 'en', 'г.'), (10062, 'ru', 'т.г.'), (10062, 'en', 'т.г.'),
(10063,'ru','ш.'),(10063,'en','ш.'),(10064,'ru','т.ш.'),(10064,'en','т.ш.'),(10065,'ru','л.'),(10065,'en','л.'),
(10066,'en','молоко отборное'), (10066,'ru','молоко  отборное'), (10067,'en','хлеб белый'),(10067,'ru','хлеб белый'),
(10068,'en','сыр алтайский'), (10068,'ru','сыр алтайский'),(10069,'en','молоко топленое'),(10069,'ru','молоко топленое'),
(10070,'en','молоко обезжиренное'),(10070,'ru','молоко обезжиренное'), (10071,'en','хлеб черный'),(10071,'ru','хлеб черный'),
(10072,'en','сыр голландский'),(10072,'ru','сыр голландский'),
(10073,'ru','Вид1'), (10074,'ru','Вид2'), (10075,'ru','Вид3'),
(10076, 'ru', 'Россия'), (10076, 'en', 'Russia'), (10077, 'ru', 'Англия'), (10077, 'en', 'England'),
(10078, 'ru', 'Таможня 1'), (10079, 'ru', 'Таможня 2'),
(10080, 'ru', 'Відсутні ветдокументи'), (10081, 'ru', 'Не виконані ветвимоги України'), (10082, 'ru', 'Фальсифікація ветдокументів'), (10083, 'ru', 'Заборона на ввезення');

UPDATE `generator` SET `generatorValue` = 10083 WHERE `generatorName` = 'books';

INSERT INTO `unit_type`(`id`, `name`, `short_name`) VALUES (1,10054,10060),(2,10055,10061),(3,10056,10062),(4,10057,10063),(5,10058,10064),(6,10059,10065);

INSERT INTO `cargo_type`(`id`, `name`, `ukt_zed_code`) VALUES (119,10066,'2602000000'),(120,10067,'2603000000'),(123,10068,'2605000000');

INSERT INTO `cargo_mode`(`id`, `name`, `parent_id`) VALUES (3,10075, NULL), (1,10073,3), (2,10074,3);

INSERT INTO `cargo_mode_cargo_type` VALUES (1, 119, CURRENT_TIMESTAMP), (1, 120, CURRENT_TIMESTAMP), (2, 119, CURRENT_TIMESTAMP);

INSERT INTO `cargo_mode_unit_type` VALUES (1, 1, CURRENT_TIMESTAMP), (2, 1, CURRENT_TIMESTAMP);

INSERT INTO `countrybook`(`id`, `name`, `code`) VALUES (1, 10076, 'ru'), (2, 10077, 'en');
INSERT INTO `cargo_producer`(`id`, `name`, `country_id`) VALUES (1,10051, 1),(2,10052, 1),(3,10053, 2);

INSERT INTO `customs_point`(`id`, `name`) VALUES(6, 10078), (7, 10079);

UPDATE `department` d SET d.`custom_point_id` = 6 WHERE d.`id` = 2;

INSERT INTO `arrest_reason`(`id`, `name`) VALUES (1,10080), (2,10081), (3,10082), (4,10083);

INSERT INTO `document_cargo`(`id`, `client_id`, `department_id`, `creator_id`, `cargo_mode_id`, `created`, `updated`, `movement_type`,
                                `vehicle_type`, `cargo_sender_name`, `cargo_sender_country_id`, `cargo_receiver_name`,
                                `cargo_receiver_address`, `passing_border_point_id`, `details`, `detention_details`, `sync_status`)
                                VALUES
                  (1,1,3,20,1, '2010-01-15 17:59:09', '2010-01-15 17:59:09', 'IMPORT','CAR', 'sender 1',1, 'receiver 1', 'receiver 1 address', 1, 'примечание 3','задержан 2', 'NOT_SYNCHRONIZED'),
                  (2,1,3,20,1, '2010-02-15 17:48:00', '2010-02-15 17:48:00', 'IMPORT','SHIP', 'sender 2',1,'receiver 2', 'receiver 2 address', 2, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (3,1,3,20,1, '2010-02-16 17:48:00', '2010-02-16 17:48:00', 'IMPORT','CONTAINER', 'sender 3',1,'receiver 3', 'receiver 3 address', 3, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (4,1,3,20,1, '2010-03-30 17:48:00', '2010-03-30 17:48:00', 'IMPORT','CARRIAGE', 'sender 1',2,'receiver 1', 'receiver 1 address', 1, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (5,1,3,20,1, '2010-03-31 17:48:00', '2010-03-31 17:48:00', 'IMPORT','AIRCRAFT', 'sender 2',2,'receiver 2', 'receiver 2 address', 2, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (6,1,3,20,1, '2010-02-16 17:25:00', '2010-02-16 17:25:00', 'IMPORT','CAR', 'sender 1',1,'receiver 1', 'receiver 1 address', 1, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (7,1,3,20,2, '2010-02-16 17:49:00', '2010-02-16 17:48:00', 'IMPORT','CONTAINER', 'sender 3',1,'receiver 3', 'receiver 3 address', 3, 'примечание','задержан', 'NOT_SYNCHRONIZED');

INSERT INTO `cargo`(`id`, `client_id`, `department_id`, `document_cargo_id`, `cargo_type_id`, `unit_type_id`, `cargo_producer_id`, `vehicle_id`,
 `count`, `certificate_date`, `certificate_details`, `sync_status`)
 VALUES (1,1,3,1,119,1,1,NULL,20.2,'2010-01-15','сертификат 1', 'NOT_SYNCHRONIZED'),
        (2,1,3,2,119,2,2,NULL,30.5,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (3,1,3,2,120,3,3,NULL,23.8,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (4,1,3,2,120,4,1,NULL,73,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (5,1,3,4,119,5,1,NULL,40, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (6,1,3,4,119,6,2,NULL,89, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (7,1,3,3,119,1,2,NULL,50,'2010-02-16', 'сертификат 4', 'NOT_SYNCHRONIZED'),
        (8,1,3,7,119,1,2,NULL,145.99,'2010-02-16', 'сертификат 4', 'NOT_SYNCHRONIZED'),
        (9,1,3,7,119,NULL,2,NULL,NULL,'2010-02-16', 'сертификат 4', 'NOT_SYNCHRONIZED');

INSERT INTO `arrest_document`(`id`, `client_id`, `department_id`, `creator_id`, `arrest_date`, `arrest_reason_id`, `arrest_reason_details`, `passing_border_point_id`,
                                `count`, `cargo_mode_id`, `certificate_date`, `certificate_details`, `cargo_sender_name`, `cargo_sender_country_id`, `cargo_receiver_name`, `cargo_receiver_address`,
                                `cargo_type_id`, `unit_type_id`, `vehicle_type`, `vehicle_details`, `document_cargo_created`, `updated`, `sync_status`)
VALUES
             (1,1,3,20,'2010-04-15 17:00:00',1,'Детали задержания.', 1, 20.83, 1, '2010-01-01', '№ 12345678', 'sender 1', 1, 'receiver 1', 'receiver 1 address', 119, 1, 'CAR', '1234567', '2010-04-15 16:00:00', '2010-04-15 17:00:00', 'NOT_SYNCHRONIZED'),
             (2,1,3,20,'2010-04-15 17:10:00',2,'Детали задержания.', 1, 50.77, 1, '2010-01-02', '№ 12345678', 'sender 2', 2, 'receiver 2', 'receiver 2 address', 120, 1, 'CAR', '7654321', '2010-04-15 16:00:00', '2010-04-15 17:10:00', 'NOT_SYNCHRONIZED'),
             (3,1,3,20,'2010-04-15 17:20:00',3,'Детали задержания.', 2, 100, 1, '2010-01-03', '№ 12345678', 'sender 3', 1, 'receiver 3', 'receiver 3 address', 120, 1, 'SHIP', 'abc1234', '2010-04-15 16:00:00', '2010-04-15 17:20:00', 'NOT_SYNCHRONIZED'),
             (4,1,3,20,'2010-04-15 17:30:00',1,'Детали задержания.', 2, 140, 1, '2010-01-04', '№ 12345678', 'sender 4', 2, 'receiver 4', 'receiver 4 address', 120, 1, 'AIRCRAFT', '', '2010-04-15 16:00:00', '2010-04-15 17:30:00', 'NOT_SYNCHRONIZED');


UPDATE `generator` SET `generatorValue` = 100 WHERE `generatorName` = 'arrest_document';
UPDATE `generator` SET `generatorValue` = 100 WHERE `generatorName` = 'cargo';
UPDATE `generator` SET `generatorValue` = 100 WHERE `generatorName` = 'document_cargo';
UPDATE `generator` SET `generatorValue` = 100 WHERE `generatorName` = 'vehicle';