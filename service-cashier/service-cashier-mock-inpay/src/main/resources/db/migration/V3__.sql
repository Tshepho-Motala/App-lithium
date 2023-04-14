ALTER TABLE `inpay_reason` ADD COLUMN `object` VARCHAR(255) DEFAULT NULL;

UPDATE `inpay_reason` SET `object` = 'rejected' WHERE `id` = 1;
UPDATE `inpay_reason` SET `object` = 'returned' WHERE `id` = 2;
UPDATE `inpay_reason` SET `object` = 'pending' WHERE `id` = 3;
UPDATE `inpay_reason` SET `object` = 'birthdate' WHERE `id` = 4;

INSERT INTO `inpay_reason` (`id`, `version`, `kind`, `category`, `code`, `object`, `message`)
VALUES (5, 0, 'payment_request_rejected', 'schema_validation_error', 'invalid_creditor_account', 'iban', 'creditor_account country_code must match IBAN');
