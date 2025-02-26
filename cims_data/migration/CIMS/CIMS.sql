----------------------------------------------
-- Export file for user CIMS                --
-- Created by flaw on 5/15/2013, 1:20:35 PM --
----------------------------------------------

spool CIMS.log

prompt
prompt Creating table CIMS_CHANGE_TYPE
prompt ===============================
prompt
@@cims_change_type.tab
prompt
prompt Creating table CIMS_ELEMENT
prompt ===========================
prompt
@@cims_element.tab
prompt
prompt Creating table CIMS_CHANGE
prompt ==========================
prompt
@@cims_change.tab
prompt
prompt Creating table CIMS_CLASSIFICATION
prompt ==================================
prompt
@@cims_classification.tab
prompt
prompt Creating table CIMS_CONCEPT
prompt ===========================
prompt
@@cims_concept.tab
prompt
prompt Creating table CIMS_CLASSIFICATION_CONCEPT
prompt ==========================================
prompt
@@cims_classification_concept.tab
prompt
prompt Creating table CIMS_VERSION
prompt ===========================
prompt
@@cims_version.tab
prompt
prompt Creating table CIMS_CONCEPT_ATTRIBUTE
prompt =====================================
prompt
@@cims_concept_attribute.tab
prompt
prompt Creating table CIMS_REQUEST_STATUS
prompt ==================================
prompt
@@cims_request_status.tab
prompt
prompt Creating table CIMS_LANGUAGE
prompt ============================
prompt
@@cims_language.tab
prompt
prompt Creating table CIMS_REQUEST
prompt ===========================
prompt
@@cims_request.tab
prompt
prompt Creating table CIMS_CONCEPT_COMMAND
prompt ===================================
prompt
@@cims_concept_command.tab
prompt
prompt Creating table CIMS_CONCEPT_RELATIONSHIP
prompt ========================================
prompt
@@cims_concept_relationship.tab
prompt
prompt Creating table CIMS_CONCEPT_TYPE
prompt ================================
prompt
@@cims_concept_type.tab
prompt
prompt Creating table CIMS_DESC
prompt ========================
prompt
@@cims_desc.tab
prompt
prompt Creating sequence CIMS_CHANGE_SEQ
prompt =================================
prompt
@@cims_change_seq.seq
prompt
prompt Creating sequence CIMS_CONCEPT_SEQ
prompt ==================================
prompt
@@cims_concept_seq.seq
prompt
prompt Creating sequence CIMS_REQUEST_SEQ
prompt ==================================
prompt
@@cims_request_seq.seq
prompt
prompt Creating package CIMS_DATA_MIGRATION
prompt ====================================
prompt
@@cims_data_migration.spc
prompt
prompt Creating package body CIMS_DATA_MIGRATION
prompt =========================================
prompt
@@cims_data_migration.bdy

spool off
