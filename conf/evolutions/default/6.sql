-- !Ups

ALTER TABLE `forms` ADD `is_costumed` BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE `forms` ADD `is_shiny` BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE `forms` ADD `is_female` BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE `forms` ADD `is_alolan` BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE `forms` ADD `is_galarian` BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE `forms` ADD `is_hisuian` BOOLEAN NOT NULL DEFAULT FALSE;

-- !Downs

ALTER TABLE `forms` DROP `is_hisuian`;
ALTER TABLE `forms` DROP `is_galarian`;
ALTER TABLE `forms` DROP `is_alolan`;
ALTER TABLE `forms` DROP `is_female`;
ALTER TABLE `forms` DROP `is_shiny`;
ALTER TABLE `forms` DROP `is_costumed`;

