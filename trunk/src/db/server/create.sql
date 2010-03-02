
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
  PRIMARY KEY (`id`, `entity`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/* Table structure for table `deleted_embedded_id`
   This table contains entries which has been removed from any application table.
   This information may be useful at client synchronization and logging.*/
DROP TABLE IF EXISTS `deleted_long_id`;
CREATE TABLE `deleted_long_id` (
  `id` bigint(20) NOT NULL, /* Long Id of removed entry.*/
  `entity` VARCHAR(100) NOT NULL, /* The name of entity of removed entry. */
  `deleted` TIMESTAMP NOT NULL, /* Time stamp of deleting. */
  PRIMARY KEY (`id`, `entity`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;