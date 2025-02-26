prompt PL/SQL Developer import file
prompt Created on Friday, April 26, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_ELEMENT...
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('ST', 'Short Title');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('LT', 'Long Title');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('UT', 'User Title');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('VCF', 'Valid Code Flag');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('DA', 'Dagger Asterisk');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('CEF', 'Canadian Enhancemen Flag');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('D', 'Definition');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('N', 'Notes');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('I', 'Includes');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('E', 'Excludes');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('CA', 'Code Also');
insert into CIMS_ELEMENT (ELEMENT_CODE, ELEMENT_DESC)
values ('OM', 'Omit Code');
commit;
prompt 12 records loaded
set feedback on
set define on
prompt Done.
