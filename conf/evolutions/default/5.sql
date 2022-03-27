-- !Ups

ALTER TABLE `forms` ADD `is_released` BOOLEAN NOT NULL DEFAULT TRUE AFTER `release_date`;

-- !Downs

ALTER TABLE `forms` DROP `is_released`;