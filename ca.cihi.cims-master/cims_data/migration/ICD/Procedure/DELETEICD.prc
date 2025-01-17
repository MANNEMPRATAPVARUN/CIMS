create or replace procedure DELETEICD is
/**************************************************************************************************************************************
* NAME:          DELETEICD
* DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.  
*                Prints a summary table listing row counts of all tables afterwards
**************************************************************************************************************************************/   
    cID number;
    v_count integer;  
begin

    cID := getICD10CAClassID('BaseClassification', 'ICD-10-CA');

    --DELETE FROM H_ICD_TEMP;
    --COMMIT;

    --Insert into H_ICD_TEMP any ElementID that is related to ICD
    INSERT INTO H_ICD_TEMP
    SELECT ev.elementid
    FROM ELEMENTVERSION ev
    WHERE ev.elementversionid IN (
        select se.elementversionid
        from ELEMENTVERSION ev 
        join ELEMENT e on ev.elementid = e.elementid
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.structureid 
        where e.classid = cID
    );

    --Delete all ElementVersion that is related to ICD.  
    --Causes a cascade delete across most tables (*PROPERTYVERSION)
    DELETE FROM ELEMENTVERSION ev
    WHERE ev.elementversionid IN (
        select se.elementversionid
        from ELEMENTVERSION ev 
        join ELEMENT e on ev.elementid = e.elementid
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.structureid 
        where e.classid = cID
    );

    --Most of the ELEMENTVERSION records are deleted, but not the base classsifications
    --Expecting multiple records, so you need to store the Element IDs before you can delete them
    --because you need to delete the ELEMENTVERSION first
    INSERT INTO H_ICD_TEMP1
    SELECT ev.elementid
    FROM ELEMENTVERSION ev
    WHERE ev.elementversionid IN (
        select ev.elementversionid
        from ELEMENTVERSION ev 
        join ELEMENT e on ev.elementid = e.elementid
        where e.classid = cID
    );

    --Delete those base classification ELEMENTVERSIONs
    DELETE FROM ELEMENTVERSION ev
    WHERE ev.elementversionid IN (
        select ev.elementversionid
        from ELEMENTVERSION ev 
        join ELEMENT e on ev.elementid = e.elementid
        where e.classid = cID
    );

    --Now we are free to delete those Elements
    DELETE FROM ELEMENT e
    WHERE e.elementid in (SELECT elementID FROM H_ICD_TEMP);

    --Now we are free to delete those Elements
    DELETE FROM ELEMENT e
    WHERE e.elementid IN (SELECT * FROM H_ICD_TEMP1);

    commit;

    dbms_output.put_line(rpad('Table Name', 30, ' ') || '    ' || 'Count');
    dbms_output.put_line(rpad('=', 45, '='));

    for r in (select table_name, owner from all_tables
              where owner = 'CIMS_D1') 
    loop
        execute immediate 'select count(*) from ' || r.table_name 
            into v_count;
        --INSERT INTO STATS_TABLE(TABLE_NAME,SCHEMA_NAME,RECORD_COUNT,CREATED)
        --VALUES (r.table_name,r.owner,v_count,SYSDATE);
        dbms_output.put_line(rpad(r.table_name, 30, ' ') || '    ' || v_count);
    end loop;

end DELETEICD;
/
