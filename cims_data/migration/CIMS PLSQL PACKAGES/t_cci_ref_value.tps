CREATE OR REPLACE TYPE t_cci_ref_value force is OBJECT (
    element_id           NUMBER,
    ref_value_code  VARCHAR2(20),
    version_code  VARCHAR2(20),
    ref_e_desc  VARCHAR2(500),
    ref_f_desc   VARCHAR2(500),
    mandatory_ind_code varchar2(1),
    element_uuid   VARCHAR2(200)
  )
/
