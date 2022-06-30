CREATE TABLE `Chat` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `Sender$id` int unsigned NOT NULL,
  `Receiver$id` int unsigned NOT NULL,
  `date` datetime NOT NULL,
  `status` varchar(16) NOT NULL DEFAULT 'POSTED',
  `text` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Chat$fk$Sender` (`Sender$id`),
  KEY `Chat$fk$Receiver` (`Receiver$id`),
  CONSTRAINT `Chat$fk$Receiver` FOREIGN KEY (`Receiver$id`) REFERENCES `Uzer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `Chat$fk$Sender` FOREIGN KEY (`Sender$id`) REFERENCES `Uzer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;