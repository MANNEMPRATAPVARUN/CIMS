create or replace package cims_data_migration is

  icd_classification_code varchar2(20) := 'ICD-10-CA';
  
  procedure migrate_data;
  function get_concept_id_from_category(p_category_id number) return number;

end cims_data_migration;
/

