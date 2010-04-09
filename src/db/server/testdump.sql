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
(57,'en','штука'),(57,'ru','штука'),(58,'en','тыс. штук'),(58,'ru','тыс. штук'),(59,'en','молоко отборное'),
(59,'ru','молоко  отборное'), (60,'en','хлеб белый'),(60,'ru','хлеб белый'),(61,'en','сыр алтайский'),
(61,'ru','сыр алтайский'),(62,'en','молоко отборное'),
(62,'ru','молоко отборное'),(63,'en','молоко топленое'),(63,'ru','молоко топленое'),(64,'en','литр'),
(64,'ru','литр'),(65,'en','молоко обезжиренное'),(65,'ru','молоко обезжиренное'),(66,'en','хлеб белый'),
(66,'ru','хлеб белый'),(67,'en','хлеб черный'),(67,'ru','хлеб черный'),(68,'en','сыр голландский'),
(68,'ru','сыр голландский'),(69,'en','сыр алтайский'),(69,'ru','сыр алтайский'),(70,'en','Пункт 1'),
(71,'ru','Вид1'), (72,'ru','Вид2'), (73,'ru','Вид3'),
(74, 'ru', 'Россия'), (74, 'en', 'Russia'), (75, 'ru', 'Англия'), (75, 'en', 'England'),
(76, 'ru', 'Таможня 1'), (77, 'ru', 'Таможня 2');

UPDATE `generator` SET `generatorValue` = 77 WHERE `generatorName` = 'books';

INSERT INTO `unit_type`(`id`, `name`) VALUES (1,54),(2,55),(3,56),(4,57),(5,58),(6,64);

INSERT INTO `cargo_type`(`id`, `name`, `ukt_zed_code`) VALUES (1,59,'2602000000'),(2,60,'2603000000'),(3,61,'2605000000');

INSERT INTO `cargo_mode`(`id`, `name`, `parent_id`) VALUES (3,73, NULL), (1,71,3), (2,72,3);

INSERT INTO `cargo_mode_cargo_type` VALUES (1, 1, CURRENT_TIMESTAMP), (1, 2, CURRENT_TIMESTAMP);

INSERT INTO `cargo_mode_unit_type` VALUES (1, 1, CURRENT_TIMESTAMP);

INSERT INTO `countrybook`(`id`, `name`) VALUES (1, 74), (2, 75);
INSERT INTO `cargo_producer`(`id`, `name`, `country_id`) VALUES (1,51, 1),(2,52, 1),(3,53, 2);

INSERT INTO `customs_point`(`id`, `name`) VALUES(1, 76), (2, 77);

UPDATE `department` d SET d.`custom_point_id` = 1 WHERE d.`id` = 2;

INSERT INTO `container_validator`(`id`, `prefix`, `carrier_abbr`, `carrier_name`) VALUES
    (1, 'AAAU', NULL, 'Asia Container Leasing'),
    (2, 'ACLU', 'ACL', 'Atlantic Container Line'),
    (3, 'ACXU', 'Atlantic Cargo', 'Atlantic Cargo');

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
 VALUES (1,1,3,1,1,1,1,NULL,20,'2010-01-15','сертификат 1', 'NOT_SYNCHRONIZED'),
        (2,1,3,2,1,2,2,NULL,30,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (3,1,3,2,2,3,3,NULL,23,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (4,1,3,2,2,4,1,NULL,73,'2010-02-15','сертификат 2', 'NOT_SYNCHRONIZED'),
        (5,1,3,4,1,5,1,NULL,40, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (6,1,3,4,1,6,2,NULL,89, '2010-03-30', 'сертификат 3', 'NOT_SYNCHRONIZED'),
        (7,1,3,3,1,1,2,NULL,50,'2010-02-16', 'сертификат 4', 'NOT_SYNCHRONIZED');
