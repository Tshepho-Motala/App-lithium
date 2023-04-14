SET foreign_key_checks = 0;

ALTER TABLE `promotion_revision`
    ADD COLUMN `redeemable_per_entry` int(11) DEFAULT NULL,
    ALGORITHM INPLACE, LOCK NONE;
ALTER TABLE `user_promotion`
    ADD INDEX `user_promotion_user_promotion_period_started` (`user_id`, `promotion_revision_id`, `period_id`, `started`);

CREATE TABLE `promo_exclusive_upload`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `total_records` INT(11) NOT NULL DEFAULT 0,
    `promotion_id` BIGINT(20) NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL ,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_promo_exclusive_upload_promotion_id` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`id`)
);

CREATE TABLE `promo_exclusive_upload_item`(
      `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
      `promo_exclusive_upload_id` BIGINT(20) NOT NULL,
      `guid` varchar(255) NOT NULL,
      `status` VARCHAR(50) NOT NULL,
      `reason_for_failure` VARCHAR(255) NULL,
      `created_at` DATETIME NOT NULL ,
      INDEX `idx_status_upload_id` (`status`, `promo_exclusive_upload_id`),
      PRIMARY KEY (`id`),
      CONSTRAINT `fk_promo_exclusive_upload_item_upload_id` FOREIGN KEY (`promo_exclusive_upload_id`) REFERENCES `promo_exclusive_upload` (`id`)
);


ALTER TABLE `promo_revision_exclusive_players`
    DROP INDEX `UK_ev1tgbjoobw1n4gvx4w821byx`,
    DROP FOREIGN KEY `FKcxbf0ukwrmduolu0q2tfcqlwp`,
    MODIFY COLUMN `promotion_revision_id` bigint(20) NULL,
    ADD COLUMN `last_added` DATETIME DEFAULT NULL,
    ADD COLUMN `promotion_id` BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT `fk_promo_exclusive_players_promotion_id` FOREIGN KEY (`promotion_id`) REFERENCES `promotion`(`id`),
    ADD CONSTRAINT `idx_player_id_promotion_id_unique` UNIQUE (`player_id`, `promotion_id`),
    ALGORITHM INPLACE, LOCK NONE;

RENAME TABLE `promo_revision_exclusive_players` TO `promo_exclusive_players`;

SET foreign_key_checks = 1;