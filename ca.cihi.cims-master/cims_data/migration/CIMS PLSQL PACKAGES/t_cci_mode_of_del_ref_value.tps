CREATE OR REPLACE TYPE t_cci_mode_of_del_ref_value force is OBJECT (
    mode_of_del_ref_value_code varchar2(20),
    version_code varchar2(20),
    mode_of_delivery_e_desc varchar2(500),
    mode_of_delivery_f_desc varchar2(500),
    mandatory_ind_code varchar2(1),
    mode_of_delivery_uuid varchar2(200)
  )
/
