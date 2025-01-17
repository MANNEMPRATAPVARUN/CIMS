CREATE OR REPLACE TYPE t_cci_tabular force is OBJECT (
    cci_tabular_code varchar2(20),
    version_code varchar2(20),
    tabular_short_e_desc varchar2(500),
    tabular_short_f_desc varchar2(500),
    tabular_long_e_desc varchar2(500),
    tabular_long_f_desc varchar2(500),
    tabular_status_code varchar2(20),
    tabular_type_code varchar2(20),
    parent_tabular_code varchar2(20),
    tabular_uuid varchar2(200),
    formatted_code varchar2(20)
  )
/
