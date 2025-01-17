CREATE OR REPLACE TYPE t_cci_status_reference_value force is OBJECT (
    status_ref_value_code varchar2(20),
    version_code varchar2(20),
    status_e_desc varchar2(500),
    status_f_desc varchar2(500),
    mandatory_ind_code varchar2(1),
    status_uuid varchar2(200)
  )
/
