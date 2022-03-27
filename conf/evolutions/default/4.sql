-- !Ups

CREATE TABLE `events` (
     `id`                          int unsigned AUTO_INCREMENT NOT NULL,
     `name`                        varchar(100) NOT NULL,
     `start_time`                   bigint,
     `end_time`                     bigint,
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_p_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `event_forms` (
     `id`                          int unsigned AUTO_INCREMENT NOT NULL,
     `event_id`                    int unsigned NOT NULL,
     `form_id`                     int unsigned NOT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- !Downs

DROP TABLE `event_forms`;
DROP TABLE `events`;
