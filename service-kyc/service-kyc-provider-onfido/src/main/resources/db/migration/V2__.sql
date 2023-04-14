ALTER TABLE `onfido_check`
    ADD COLUMN `report_type` varchar(255) NOT NULL DEFAULT 'document_with_address_information', ALGORITHM INPLACE, LOCK NONE;
CREATE INDEX `report_type_idx` on `onfido_check`(report_type) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `status_idx` on `onfido_check`(status) ALGORITHM INPLACE LOCK NONE;