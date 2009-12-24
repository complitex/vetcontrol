INSERT INTO project1.`roles` (`name`) VALUES ('ADMIN');
INSERT INTO project1.roles (`name`) VALUES ('DONT_EDIT_ADMIN');

INSERT INTO project1.`user`
        (`name`,
	`login`,
	`password`,
	`parentNodeId`,
	`blocked`
	)
	VALUES
	('ADMIN',
	'ADMIN',
	'ADMIN',
	null,
	0
	);
INSERT INTO project1.`user`
        (`name`,
	`login`,
	`password`,
	`parentNodeId`,
	`blocked`
	)
	VALUES
	('DONT_EDIT_ADMIN',
	'DONT_EDIT_ADMIN',
	'DONT_EDIT_ADMIN',
	null,
	0
	);

INSERT INTO project1.`role`
	(`role`,
	`userName`
	)
	VALUES
	('ADMIN',
	'ADMIN'
	);
INSERT INTO project1.`role`
	(`role`,
	`userName`
	)
	VALUES
	('DONT_EDIT_ADMIN',
	'DONT_EDIT_ADMIN'
	);

INSERT INTO `generator`(`generatorName`, `generatorValue`) VALUES ('books', 0);

INSERT INTO `locales`(`language`, `isSystem`) VALUES ('en', '1');
INSERT INTO `locales`(`language`) VALUES ('ru');