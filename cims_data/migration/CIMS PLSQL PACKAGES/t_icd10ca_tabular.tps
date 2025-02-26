CREATE OR REPLACE TYPE t_icd10ca_tabular force is OBJECT (
  icd10ca_tabular_code        VARCHAR2(20),
  version_code VARCHAR2(20),
  tabular_short_e_desc        VARCHAR2(500),
  tabular_short_f_desc        VARCHAR2(500),
  tabular_long_e_desc         VARCHAR2(500),
  tabular_long_f_desc         VARCHAR2(500),
  tabular_type_code           VARCHAR2(20),
  code_ind_code               VARCHAR2(1),
  tabular_status_code         VARCHAR2(20),
  parent_tabular_code         VARCHAR2(20),
  tabular_uuid                VARCHAR2(200),
  formatted_code              VARCHAR2(20)
)
/
