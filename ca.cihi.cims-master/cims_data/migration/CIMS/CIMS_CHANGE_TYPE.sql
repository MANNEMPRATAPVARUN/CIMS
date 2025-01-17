prompt PL/SQL Developer import file
prompt Created on Friday, April 26, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_CHANGE_TYPE...
insert into CIMS_CHANGE_TYPE (CHANGE_TYPE_CODE, CHANGE_TYPE_DESC)
values ('C', 'Create');
insert into CIMS_CHANGE_TYPE (CHANGE_TYPE_CODE, CHANGE_TYPE_DESC)
values ('U', 'Update');
insert into CIMS_CHANGE_TYPE (CHANGE_TYPE_CODE, CHANGE_TYPE_DESC)
values ('D', 'Delete');
commit;
prompt 3 records loaded
set feedback on
set define on
prompt Done.
