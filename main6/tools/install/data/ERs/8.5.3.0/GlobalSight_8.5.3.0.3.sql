# [#GBS-3282] hierarchical TMs stop search after hitting 100% match

ALTER TABLE tm_profile ADD COLUMN DYN_LEV_STOP_SEARCH CHAR(1) NOT NULL DEFAULT 'N' AFTER DYN_LEV_FROM_REFERENCE_TM;