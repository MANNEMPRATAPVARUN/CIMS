prompt PL/SQL Developer import file
prompt Created on Thursday, April 25, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_REQUEST_STATUS...
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('N', 'New');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('V', 'Valid');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('R', 'Rejected');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('CD', 'Closed - Deferred');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('I', 'Incomplete');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('A', 'Accepted');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('TR', 'Translation Done');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('VD', 'Validation Done');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('CA', 'Closed - Approved');
insert into CIMS_REQUEST_STATUS (REQUEST_STATUS_CODE, REQUEST_STATUS_DESC)
values ('P', 'Published');
commit;
prompt 10 records loaded
set feedback on
set define on
prompt Done.
