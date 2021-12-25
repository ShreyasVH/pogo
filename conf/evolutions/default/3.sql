CREATE TABLE `forms` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `name`                        varchar(100) NOT NULL,
    `pokemon_number`              int unsigned NOT NULL,
    `image_url`                   varchar(200),
    `release_date`                bigint,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_p_name_number` (`name`, `pokemon_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE forms ADD CONSTRAINT fk_forms_pokemon_number FOREIGN KEY (`pokemon_number`) REFERENCES `pokemons` (`number`) on DELETE RESTRICT ON UPDATE RESTRICT;