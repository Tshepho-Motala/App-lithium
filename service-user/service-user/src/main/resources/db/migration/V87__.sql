ALTER TABLE `label_value`
    CHANGE COLUMN `value` `value` VARCHAR(1000) NULL DEFAULT NULL ,
    ALGORITHM=COPY, LOCK=SHARED;
