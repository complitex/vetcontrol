INSERT INTO `department` VALUES (1,'��������������� ������� ������������ ��������',NULL,1),
(2,'ϳ�������� �������� �������',1,2),(3,'������ ���������',2,3),(4,'��������� ������',2,3),
(5,'�-������������� ������',2,3),(6,'����������� ������',2,3),(7,'������������ ������',2,3),
(8,'������-������������ ������',2,3),(9,'������������� ������',2,3),(10,'��������������� ������',2,3),
(11,'����������� ������',2,3),(12,'����������� ������',2,3),(13,'����������� ������',2,3),
(14,'���������� ������',2,3),(15,'ʳ������� ������',2,3),(16,'ʳ������������� ������',2,3),
(17,'���������� ������',2,3),(18,'���������� ������',2,3),(19,'������-���������� ������',2,3),
(20,'������������ ������',2,3),(21,'�������� ������',2,3),(22,'������������ ������',2,3),
(23,'�������������� ������',2,3),(24,'���������� ������',2,3),(32,'����������� ������',2,3),
(33,'�������������� ������',2,3),(34,'����������� ������',2,3),(35,'������������ ������',2,3),
(36,'��������� ������',2,3),(37,'����������� ������',2,3),(38,'���������� ������',2,3),
(39,'������������� ������',2,3),(40,'���������� ������',2,3);

--Login:test Password:test
INSERT INTO `user` VALUES (1,'test','098f6bcd4621d373cade4e832627b4f6',NULL,NULL,NULL,NULL);
INSERT INTO `usergroup` VALUES (1,'test','ADMINISTRATORS');

INSERT INTO `generator`(`generatorName`, `generatorValue`) VALUES ('books', 0);

INSERT INTO `locales`(`language`, `isSystem`) VALUES ('en', '1');
INSERT INTO `locales`(`language`) VALUES ('ru');