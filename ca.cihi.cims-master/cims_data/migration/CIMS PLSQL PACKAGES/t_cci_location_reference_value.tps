CREATE OR REPLACE TYPE t_cci_location_reference_value force is OBJECT (
    location_ref_value_code varchar2(20),
    version_code varchar2(20),
    location_e_desc varchar2(500),
    location_f_desc varchar2(500),
    mandatory_ind_code varchar2(1),
    location_uuid varchar2(200)
  )
/
