
/*Table structure for table `deleted` */
/* This table contains entries which has been removed from any application table. This information may be useful at client synchronization and logging. */
DROP TABLE IF EXISTS `deleted`;
CREATE TABLE `deleted` (
  /* Id of removed entry. It may be any type but table contains string value of that type. If id is composite id then what this value will be
        is up to application. For example, concatenating of compound properties. */
  `id` VARCHAR(32) NOT NULL,
  /* The name of entity of removed entry. */
  `entity` VARCHAR(32) NOT NULL,
  /* Timestamp of deleting. */
  `deleted` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;