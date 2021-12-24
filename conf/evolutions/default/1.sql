CREATE TABLE `types` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `name`                        varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_t_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `regions` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `name`                        varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_r_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `items` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `name`                        varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_i_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pokemons` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `name`                        varchar(100) NOT NULL,
    `number`                      int unsigned NOT NULL,
    `region_id`                   int unsigned NOT NULL,
    `candies_to_evolve`           int unsigned DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_p_name` (`name`),
    UNIQUE KEY `uk_p_number` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `candy_map` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `pokemon_number`                  int unsigned NOT NULL,
    `candy_pokemon_number`            int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cm_pokemon_candy` (`pokemon_number`, `candy_pokemon_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE pokemons ADD CONSTRAINT fk_pokemons_regions_id FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE candy_map ADD CONSTRAINT fk_candy_map_pokemon_number FOREIGN KEY (`pokemon_number`) REFERENCES `pokemons` (`number`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE candy_map ADD CONSTRAINT fk_candy_map_candy_number FOREIGN KEY (`candy_pokemon_number`) REFERENCES `pokemons` (`number`) on DELETE RESTRICT ON UPDATE RESTRICT;