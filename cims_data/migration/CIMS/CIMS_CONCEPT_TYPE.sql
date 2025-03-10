prompt PL/SQL Developer import file
prompt Created on Friday, April 19, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_CONCEPT_TYPE...
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('ALPHABET', 'Index Alphabet', null);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('BL1', 'Block 1', 41);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('BL2', 'Block 2', 42);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('BL3', 'Block 3', 43);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CAT1', 'Category 1', 51);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CAT2', 'Category 2', 51);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CAT3', 'Category 3', 51);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CHP', 'Chapter', 31);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CLS', 'Classification', 21);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CODE', 'Code', 51);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('CODE_ALSO', 'Code Also', 13);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('EXCLUDE', 'Exclude', 14);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('FAM', 'Family', 1);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('IDXSUBTERM', 'Index Subterm', null);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('IDXTERM', 'Index Term', null);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('INCLUDE', 'Include', 12);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('INDEX', 'Index', null);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('NOTE', 'Note', 11);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('OMIT_CODE', 'Omit Code', 15);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('TABLE', 'Table', 16);
insert into CIMS_CONCEPT_TYPE (CONCEPT_TYPE_CODE, CONCEPT_TYPE_DESC, CONCEPT_TYPE_SEQ_NUM)
values ('TEXT', 'Text', null);
commit;
prompt 21 records loaded
set feedback on
set define on
prompt Done.
