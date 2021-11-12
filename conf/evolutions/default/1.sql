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
    UNIQUE KEY `uk_p_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `type_map` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `pokemon_id`                  int unsigned NOT NULL,
    `type_id`                     int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tm_pokemon_type` (`pokemon_id`, `type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `candy_map` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `pokemon_id`                  int unsigned NOT NULL,
    `candy_pokemon_id`            int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cm_pokemon_candy` (`pokemon_id`, `candy_pokemon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `item_map` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `pokemon_id`                  int unsigned NOT NULL,
    `item_id`                     int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_im_pokemon_item` (`pokemon_id`, `item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `forms` (
    `id`                          int unsigned AUTO_INCREMENT NOT NULL,
    `pokemon_id`                  int unsigned NOT NULL,
    `name`                        varchar(100) NOT NULL,
    `image_url`                   varchar(250),
    `released_date`               timestamp,
    `evolved_from`                int unsigned,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_f_pokemon_form` (`pokemon_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE pokemons ADD CONSTRAINT fk_pokemons_regions_id FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE type_map ADD CONSTRAINT fk_type_map_pokemon_id FOREIGN KEY (`pokemon_id`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE type_map ADD CONSTRAINT fk_type_map_type_id FOREIGN KEY (`type_id`) REFERENCES `types` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE candy_map ADD CONSTRAINT fk_candy_map_pokemon_id FOREIGN KEY (`pokemon_id`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE candy_map ADD CONSTRAINT fk_candy_map_candy_id FOREIGN KEY (`candy_pokemon_id`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_map ADD CONSTRAINT fk_item_map_pokemon_id FOREIGN KEY (`pokemon_id`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE item_map ADD CONSTRAINT fk_item_map_type_id FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE forms ADD CONSTRAINT fk_forms_pokemon_id FOREIGN KEY (`pokemon_id`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE forms ADD CONSTRAINT fk_forms_evolve_id FOREIGN KEY (`evolved_from`) REFERENCES `pokemons` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;