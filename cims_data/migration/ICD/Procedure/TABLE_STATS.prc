create or replace procedure TABLE_STATS is
/**************************************************************************************************************************************
* NAME:          TABLE_STATS
* DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.  
*                Prints a summary table listing row counts of all tables afterwards
**************************************************************************************************************************************/   
    v_count integer;  
begin

    dbms_output.put_line(rpad('Table Name', 30, ' ') || '    ' || 'Count');
    dbms_output.put_line(rpad('=', 45, '='));

    for r in (select table_name, owner from all_tables
              where owner = 'CIMS_D1') 
    loop
        execute immediate 'select count(*) from ' || r.table_name 
        into v_count;
        dbms_output.put_line(rpad(r.table_name, 30, ' ') || '    ' || v_count);
    end loop;

end TABLE_STATS;
/
