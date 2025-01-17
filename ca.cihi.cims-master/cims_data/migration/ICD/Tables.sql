CREATE TABLE LOG (
    id NUMBER NOT NULL,
    message VARCHAR2(4000) NOT NULL,
    messageDate    DATE NULL,
    classification VARCHAR2(4000) NOT NULL,
    fiscal_year NUMBER NOT NULL,
    run_id NUMBER NOT NULL,
    PRIMARY KEY (id) 				
);

CREATE GLOBAL TEMPORARY TABLE Z_ICD_TEMP (
	elementID  NUMBER
) ON COMMIT DELETE ROWS;

CREATE GLOBAL TEMPORARY TABLE Z_ICD_TEMP1 (
	elementID  NUMBER
) ON COMMIT DELETE ROWS;

CREATE TABLE Z_ICD_DIFFS_TEXT (
    diffsID NUMBER NOT NULL,
    CATEGORY_ID_FROM NUMBER NOT NULL,
    CATEGORY_ID_TO NUMBER NOT NULL,
    CATEGORY_CODE VARCHAR2(30) NULL,
    TEXT_FROM VARCHAR2(255) NOT NULL,
    TEXT_TO VARCHAR2(255) NOT NULL,
    classID NUMBER NOT NULL,
    VersionCode_FROM VARCHAR2(30) NULL,
    VersionCode_TO VARCHAR2(30) NULL,
    language VARCHAR2(3) NULL,
    PRIMARY KEY (diffsID) 
);

CREATE TABLE Z_ICD_DIFFS_XML (
    diffsID NUMBER NOT NULL,
    CATEGORY_ID_FROM NUMBER NULL,
    CATEGORY_ID_TO NUMBER NULL,
    CATEGORY_CODE_FROM VARCHAR2(30) NULL,
    CATEGORY_CODE_TO VARCHAR2(30) NULL,
    xmlText_FROM CLOB NULL,
    xmlText_TO CLOB NULL,
    classID NUMBER NOT NULL,
    VersionCode_FROM VARCHAR2(30) NULL,
    VersionCode_TO VARCHAR2(30) NULL,
    LanguageCode CHAR(3) NOT NULL,
    PRIMARY KEY (diffsID) 
);


create table TRANSFORMATION_ERROR (
    ERROR_ID      	NUMBER not null,
    CLASSIFICATION 	VARCHAR2(20) not null,
    VERSION_CODE  	VARCHAR2(4) not null,
    CONCEPT_CODE  	VARCHAR2(20),
    CONCEPT_TYPE_CODE VARCHAR2(10),
    ERROR_MESSAGE 	VARCHAR2(255),
    XML_STRING     	CLOB,
    CREATE_DATE    	DATE,
    RUN_ID 		 	NUMBER
);

alter table TRANSFORMATION_ERROR
  add constraint PK_ERROR primary key (ERROR_ID);
  
CREATE TABLE Z_ICD_DIFFS_NODES (
    diffsID NUMBER NOT NULL,
    CATEGORY_ID_FROM NUMBER NULL,
    CATEGORY_ID_TO NUMBER NULL,
    ELEMENT_ID_FROM NUMBER NULL,
    ELEMENT_ID_TO NUMBER NULL,
    CATEGORY_CODE VARCHAR2(30) NULL,
    VersionCode_FROM VARCHAR2(30) NULL,
    VersionCode_TO VARCHAR2(30) NULL,
    status VARCHAR2(30) NULL,
    PRIMARY KEY (diffsID) 
);

CREATE TABLE Z_ICD_DIFFS_EV (
    diffsID NUMBER NOT NULL,
    ELEMENTVERSIONID_FROM NUMBER NULL, 
    ELEMENTVERSIONID_TO NUMBER NULL, 
    ELEMENTID_FROM NUMBER NULL, 
    VersionCode_FROM VARCHAR2(30) NULL,
    VersionCode_TO VARCHAR2(30) NULL,
    status VARCHAR2(30) NULL,
    PRIMARY KEY (diffsID) 
);

