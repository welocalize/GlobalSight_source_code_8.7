#for GBS-3794
Alter TABLE html_filter ADD COLUMN `WHITESPACE_PRESERVE` char(1) NOT NULL DEFAULT 'N' after IGNORE_INVALIDE_HTML_TAGS;