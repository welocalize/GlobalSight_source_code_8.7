# COTI updates

ALTER TABLE `coti_package` CHANGE COLUMN `FILE_NAME` `FILE_NAME` VARCHAR(200) NULL DEFAULT NULL, CHANGE COLUMN `COTI_PROJECT_NAME` `COTI_PROJECT_NAME` VARCHAR(200) NULL DEFAULT NULL;

ALTER TABLE `coti_project` CHANGE COLUMN `COTI_PROJECT_ID` `COTI_PROJECT_ID` VARCHAR(100) NULL DEFAULT NULL, CHANGE COLUMN `COTI_PROJECT_NAME` `COTI_PROJECT_NAME` VARCHAR(200) NULL DEFAULT NULL, CHANGE COLUMN `DIR_NAME` `DIR_NAME` VARCHAR(200) NOT NULL;

ALTER TABLE `coti_document` CHANGE COLUMN `FILE_REF` `FILE_REF` VARCHAR(200) NOT NULL, CHANGE COLUMN `ENCODING` `ENCODING` VARCHAR(40) NULL DEFAULT NULL, CHANGE COLUMN `MIME_TYPE` `MIME_TYPE` VARCHAR(80) NULL DEFAULT NULL, CHANGE COLUMN `DESCRIPTION` `DESCRIPTION` VARCHAR(500) NULL DEFAULT NULL;