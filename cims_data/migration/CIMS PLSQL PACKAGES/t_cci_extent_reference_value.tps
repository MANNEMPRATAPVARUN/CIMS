CREATE OR REPLACE TYPE t_cci_extent_reference_value force is OBJECT (
    extent_ref_value_code varchar2(20),
    version_code varchar2(20),
    extent_e_desc varchar2(500),
    extent_f_desc varchar2(500),
    mandatory_ind_code varchar2(1),
    extent_uuid varchar2(200)
  )
/
