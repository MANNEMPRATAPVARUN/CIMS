create or replace function getICD10CAClassID(tblName varchar2, cName varchar2) return number is
    classID number;
begin
    SELECT c.CLASSID 
    INTO classID 
    FROM CLASS c 
    WHERE UPPER(TRIM(c.TABLENAME)) = UPPER(TRIM(tblName)) 
    AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName))
    AND UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM('ICD-10-CA'));

    return classID;
end getICD10CAClassID;
/
