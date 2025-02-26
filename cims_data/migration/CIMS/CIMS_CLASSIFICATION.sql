prompt PL/SQL Developer import file
prompt Created on Monday, April 22, 2013 by flaw
set feedback off
set define off
prompt Loading CIMS_CLASSIFICATION...
insert into CIMS_CLASSIFICATION (CLASSIFICATION_CODE, CLASSIFICATION_E_DESC, CLASSIFICATION_F_DESC)
values ('ICD-10-CA', 'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA]', 'Classification statistique internationale des maladies et des problèmes de santé connexes, dixième version, Canada');
insert into CIMS_CLASSIFICATION (CLASSIFICATION_CODE, CLASSIFICATION_E_DESC, CLASSIFICATION_F_DESC)
values ('FAMILY', 'Family of Classifications', 'Family of Classifications');
commit;
prompt 2 records loaded
set feedback on
set define on
prompt Done.
