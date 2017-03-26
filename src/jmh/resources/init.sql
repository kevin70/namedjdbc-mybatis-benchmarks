DROP TABLE IF EXISTS `t_person`;

CREATE TABLE `t_person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `lastName` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `age` tinyint(4) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `hobby` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `createdTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
