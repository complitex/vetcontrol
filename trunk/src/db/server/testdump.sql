INSERT INTO `user`(`id`, `login`, `_password`, `first_name`, `middle_name`, `last_name`, `department_id`) VALUES
(2,'admin','21232f297a57a5a743894a0e4a801fc3','Иванов','Иван','Иванович',1),
(17,'login_0','480335867a6c40d7bd4cf04e184910cc','Петров','Петр','Петрович',1),
(18,'login_1','e81b90f3ee27f42bb010251021b1fc09','Сидоров','Сидор','Сидорович',2),
(19,'login_2','3c681bd9a527167284e1f67a9da4f4dc','Александров','Александр','Александрович',3),
(20,'login_3','3466c36ad772ab4e32e6ea6e7a59e142','Васильев','Василий','Васильевич',3),
(21,'login_4','97b701a71f43ccd4dca24feb29df7437','Николаев','Николай','Николаевич',3);

INSERT INTO `usergroup` (`id`, `login`, `usergroup`) VALUES
(2,'admin','ADMINISTRATORS'),(13,'login_0','DEPARTMENT_OFFICERS'),
(14,'login_1','DEPARTMENT_OFFICERS'),(15,'login_2','LOCAL_OFFICERS'),
(16,'login_3','LOCAL_OFFICERS'),(17,'login_4','LOCAL_OFFICERS');

INSERT INTO `stringculture`(`id`, `locale`, `value`) VALUES
(45,'en','Получатель 1'),(45,'ru','Получатель 1'),(46,'en','Получатель 2'),(46,'ru','Получатель 2'),
(47,'en','Получатель 3'),(47,'ru','Получатель 3'),(48,'en','Отправитель 1'),(48,'ru','Отправитель 1'),
(49,'en','Отправитель 2'),(49,'ru','Отправитель 2'),(50,'en','Отправитель 3'),(50,'ru','Отправитель 3'),
(51,'en','Производитель 1'),(51,'ru','Производитель 1'),
(52,'en','Производитель 2'),(52,'ru','Производитель 2'),(53,'en','Производитель 3'),(53,'ru','Производитель 3'),
(54,'en','тонна'),(54,'ru','тонна'),(55,'en','голова'),(55,'ru','голова'),(56,'en','тыс. голов'),(56,'ru','тыс. голов'),
(57,'en','штука'),(57,'ru','штука'),(58,'en','тыс. штук'),(58,'ru','тыс. штук'),(59,'en','литр'),(59,'ru','литр'),
(60, 'ru', 'т.'), (60, 'en', 'т.'), (61, 'ru', 'г.'), (61, 'en', 'г.'), (62, 'ru', 'т.г.'), (62, 'en', 'т.г.'),
(63,'ru','ш.'),(63,'en','ш.'),(64,'ru','т.ш.'),(64,'en','т.ш.'),(65,'ru','л.'),(65,'en','л.'),
(66,'en','молоко отборное'), (66,'ru','молоко  отборное'), (67,'en','хлеб белый'),(67,'ru','хлеб белый'),
(68,'en','сыр алтайский'), (68,'ru','сыр алтайский'),(69,'en','молоко топленое'),(69,'ru','молоко топленое'),
(70,'en','молоко обезжиренное'),(70,'ru','молоко обезжиренное'), (71,'en','хлеб черный'),(71,'ru','хлеб черный'),
(72,'en','сыр голландский'),(72,'ru','сыр голландский'),
(73,'ru','Вид1'), (74,'ru','Вид2'), (75,'ru','Вид3'),
(76, 'ru', 'Россия'), (76, 'en', 'Russia'), (77, 'ru', 'Англия'), (77, 'en', 'England'),
(78, 'ru', 'Таможня 1'), (79, 'ru', 'Таможня 2'),
(80, 'ru', 'Причина задержания 1'), (81, 'ru', 'Причина задержания 2'), (82, 'ru', 'Причина задержания 3');

UPDATE `generator` SET `generatorValue` = 82 WHERE `generatorName` = 'books';

INSERT INTO `unit_type`(`id`, `name`, `short_name`) VALUES (1,54,60),(2,55,61),(3,56,62),(4,57,63),(5,58,64),(6,59,65);

INSERT INTO `cargo_type`(`id`, `name`, `ukt_zed_code`) VALUES (1,66,'2602000000'),(2,67,'2603000000'),(3,68,'2605000000');

INSERT INTO `cargo_mode`(`id`, `name`, `parent_id`) VALUES (3,75, NULL), (1,73,3), (2,74,3);

INSERT INTO `cargo_mode_cargo_type` VALUES (1, 1, CURRENT_TIMESTAMP), (1, 2, CURRENT_TIMESTAMP);

INSERT INTO `cargo_mode_unit_type` VALUES (1, 1, CURRENT_TIMESTAMP);

INSERT INTO `countrybook`(`id`, `name`, `code`) VALUES (1, 76, 'ru'), (2, 77, 'en');
INSERT INTO `cargo_producer`(`id`, `name`, `country_id`) VALUES (1,51, 1),(2,52, 1),(3,53, 2);

INSERT INTO `customs_point`(`id`, `name`) VALUES(1, 78), (2, 79);

UPDATE `department` d SET d.`custom_point_id` = 1 WHERE d.`id` = 2;

INSERT INTO `container_validator`(`id`, `prefix`, `carrier_abbr`, `carrier_name`) VALUES
    (1, 'AAAU', NULL, 'Asia Container Leasing'),
    (2, 'ACLU', 'ACL', 'Atlantic Container Line'),
    (3, 'ACXU', 'Atlantic Cargo', 'Atlantic Cargo');

INSERT INTO `arrest_reason`(`id`, `name`) VALUES (1,80), (2,81), (3,82);

INSERT INTO `document_cargo`(`id`, `client_id`, `department_id`, `creator_id`, `cargo_mode_id`, `created`, `updated`, `movement_type_id`,
                                `vehicle_type`, `cargo_sender_name`, `cargo_sender_country_id`, `cargo_receiver_name`,
                                `cargo_receiver_address`, `passing_border_point_id`, `details`, `detention_details`, `sync_status`)
                                VALUES
                  (1,1,3,20,1, '2010-01-15 17:59:09', '2010-01-15 17:59:09', 1,'CAR', 'sender 1',1, 'receiver 1', 'receiver 1 address', 1, 'примечание 3','задержан 2', 'NOT_SYNCHRONIZED'),
                  (2,1,3,20,1, '2010-02-15 17:48:00', '2010-02-15 17:48:00', 1,'SHIP', 'sender 2',1,'receiver 2', 'receiver 2 address', 2, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (3,1,3,20,1, '2010-02-16 17:48:00', '2010-02-16 17:48:00', 3,'CONTAINER', 'sender 3',1,'receiver 3', 'receiver 3 address', 3, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (4,1,3,20,1, '2010-03-30 17:48:00', '2010-03-30 17:48:00', 1,'CARRIAGE', 'sender 1',2,'receiver 1', 'receiver 1 address', 1, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (5,1,3,20,1, '2010-03-31 17:48:00', '2010-03-31 17:48:00', 2,'AIRCRAFT', 'sender 2',2,'receiver 2', 'receiver 2 address', 2, 'примечание','задержан', 'NOT_SYNCHRONIZED'),
                  (6,1,3,20,1, '2010-02-16 17:25:00', '2010-02-16 17:25:00', 2,'CAR', 'sender 1',1,'receiver 1', 'receiver 1 address', 1, 'примечание','задержан', 'NOT_SYNCHRONIZED');

INSERT INTO `cargo`(`id`, `client_id`, `department_id`, `document_cargo_id`, `cargo_type_id`, `unit_type_id`, `cargo_producer_id`, `vehicle_id`,
 `count`, `certificate_date`, `certificate_details`, `sync_status`)
 VALUES (1,1,3,1,1,1,1,NULL,20.2,'2010-01-15','сертификат 1', 'NOT_SYNCHRONIZED'),
        (2,1,3,2,1,2,2,NULL,30.5,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (3,1,3,2,2,3,3,NULL,23.8,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (4,1,3,2,2,4,1,NULL,73,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (5,1,3,4,1,5,1,NULL,40, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (6,1,3,4,1,6,2,NULL,89, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (7,1,3,3,1,1,2,NULL,50,'2010-02-16', 'сертификат 4', 'NOT_SYNCHRONIZED');

INSERT INTO `arrest_document`(`id`, `client_id`, `department_id`, `arrest_date`, `arrest_reason_id`, `arrest_reason_details`, `passing_border_point_id`,
                                `count`, `cargo_mode_id`, `cargo_sender_name`, `cargo_sender_country_id`, `cargo_receiver_name`, `cargo_receiver_address`,
                                `cargo_type_id`, `unit_type_id`, `vehicle_type`, `vehicle_details`, `document_cargo_created`, `updated`, `sync_status`)
VALUES
             (1,1,3,'2010-04-15 17:00:00',1,'Детали задержания.', 1, 20.83, 1, 'sender 1', 1, 'receiver 1', 'receiver 1 address', 1, 1, 'CAR', '1234567', '2010-04-15 16:00:00', '2010-04-15 17:00:00', 'NOT_SYNCHRONIZED'),
             (2,1,3,'2010-04-15 17:10:00',2,'Детали задержания.', 1, 50.77, 1, 'sender 2', 2, 'receiver 2', 'receiver 2 address', 2, 1, 'CAR', '7654321', '2010-04-15 16:00:00', '2010-04-15 17:10:00', 'NOT_SYNCHRONIZED'),
             (3,1,3,'2010-04-15 17:20:00',3,'Детали задержания.', 2, 100, 1, 'sender 3', 1, 'receiver 3', 'receiver 3 address', 2, 1, 'SHIP', 'abc1234', '2010-04-15 16:00:00', '2010-04-15 17:20:00', 'NOT_SYNCHRONIZED'),
             (4,1,3,'2010-04-15 17:30:00',1,'Детали задержания.', 2, 140, 1, 'sender 4', 2, 'receiver 4', 'receiver 4 address', 2, 1, 'AIRCRAFT', '', '2010-04-15 16:00:00', '2010-04-15 17:30:00', 'NOT_SYNCHRONIZED');
