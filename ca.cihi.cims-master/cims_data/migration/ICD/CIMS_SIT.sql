set echo on
set define off
conn CIMS@devdb01_user.cihi.ca 
@CIMSSchemaCREATEOctober8.sql
@Tables.sql
@Sequences.sql

@'./Packages/ICD_DATA_MIGRATION.pck'
@'./Packages/ICD_DATA_MIGRATION_YR_TO_YR.pck'
@'./Packages/ICD_DATA_PREPARATION.pck'
@'./Packages/CIMS_ICD.pck'

commit;