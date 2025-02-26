prompt PL/SQL Developer import file
prompt Created on Friday, April 19, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_VERSION...
insert into CIMS_VERSION (VERSION_CODE, VERSION_STATUS_CODE)
values ('2009', 'R');
insert into CIMS_VERSION (VERSION_CODE, VERSION_STATUS_CODE)
values ('2012', 'R');
insert into CIMS_VERSION (VERSION_CODE, VERSION_STATUS_CODE)
values ('2015', 'D');
commit;
prompt 3 records loaded
set feedback on
set define on
prompt Done.
