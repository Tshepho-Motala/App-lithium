ALTER TABLE `user` ADD COLUMN `require_sow_document` BIT(1) DEFAULT 0, ALGORITHM INPLACE, LOCK NONE;
