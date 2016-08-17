# GBS-2815:Poor efficiency of "like" query for segment issues.

ALTER TABLE ISSUE ADD COLUMN TARGET_PAGE_ID BIGINT DEFAULT 0 AFTER LOGICAL_KEY;

UPDATE ISSUE SET TARGET_PAGE_ID = SUBSTRING(LOGICAL_KEY, 1, (SELECT LOCATE('_', LOGICAL_KEY) - 1));