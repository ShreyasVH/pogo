-- !Ups

CREATE TABLE `form_type_map` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `form_id`                     int unsigned NOT NULL,
    `type_id`                     int unsigned NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_ftm_form_type` (`form_id`, `type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE form_type_map ADD CONSTRAINT fk_form_type_map_form FOREIGN KEY (`form_id`) REFERENCES `forms` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE form_type_map ADD CONSTRAINT fk_form_type_map_type FOREIGN KEY (`type_id`) REFERENCES `types` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

-- !Downs

DROP TABLE `form_type_map`;