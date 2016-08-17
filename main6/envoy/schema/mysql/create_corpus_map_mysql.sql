--  CORPUS MAP
-- 
DROP TABLE IF EXISTS CORPUS_MAP CASCADE;
CREATE TABLE CORPUS_MAP
(
   ID BIGINT
      AUTO_INCREMENT
      PRIMARY KEY,
   TM_ID BIGINT NOT NULL,
   PROJECT_TUV_ID BIGINT
      NOT NULL,
   CUV_ID BIGINT
      NOT NULL,
   PROJECT_TU_ID BIGINT
      NOT NULL,
   PARTIAL_CONTEXT VARCHAR(4000)
      NOT NULL,
   LINK_DATE DATETIME
      NOT NULL,
   KEY (PROJECT_TU_ID),
   KEY (PROJECT_TUV_ID),
   KEY (TM_ID),
   CONSTRAINT FK_CORPUS_MAP_CUV_ID FOREIGN KEY (CUV_ID) REFERENCES CORPUS_UNIT_VARIANT(ID)
) AUTO_INCREMENT = 1000;
