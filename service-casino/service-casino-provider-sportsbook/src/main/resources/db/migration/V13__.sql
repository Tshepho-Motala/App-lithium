ALTER TABLE `settlement_entry`
    CHANGE COLUMN `accounting_transaction_id` `accounting_transaction_id` BIGINT(20) NULL,
    ALGORITHM = INPLACE, LOCK = SHARED;

ALTER TABLE `settlement`
    CHANGE COLUMN `balance_after` `balance_after` DOUBLE NULL,
    ALGORITHM = INPLACE, LOCK = SHARED;
