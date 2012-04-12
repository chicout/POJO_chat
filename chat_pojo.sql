CREATE TABLE `user` (
`user_name` varchar(16) COLLATE utf8_general_ci NOT NULL,
`user_password` varchar(32) NOT NULL,
`user_status` tinyint(1) DEFAULT '0',
PRIMARY KEY (`user_name`))
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci;

CREATE TABLE `message` (
`mes_username` varchar(16) DEFAULT NULL,
`mes_mes` text,
`mes_time` datetime DEFAULT NULL,
KEY `mes_username` (`mes_username`),
CONSTRAINT `message_ibfk_1` FOREIGN KEY (`mes_username`) REFERENCES `user` (`user_name`) ON DELETE SET NULL)
ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_general_ci;


