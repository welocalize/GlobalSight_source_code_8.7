#GBS-2469, remove the auto_increment attribute of Table PERMISSION

ALTER TABLE PERMISSION ADD ID_TMP bigint(20) null AFTER ID;

UPDATE PERMISSION SET ID_TMP = ID;

ALTER TABLE PERMISSION DROP COLUMN ID;

ALTER TABLE PERMISSION ADD ID bigint(20) null AFTER id_tmp;

UPDATE PERMISSION SET ID = ID_TMP;

ALTER TABLE PERMISSION DROP COLUMN ID_TMP;

ALTER TABLE PERMISSION ADD PRIMARY KEY (ID);