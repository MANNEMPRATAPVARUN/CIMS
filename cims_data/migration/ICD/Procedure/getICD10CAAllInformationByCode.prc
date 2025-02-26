create or replace procedure getICD10CAAllInformationByCode(catCode varchar2, version_year number) is
    classID number;
    icdStructureID number;
    codeClassID number;
    domainElementID number;

    tmp1 number;
    tmp2 number;
    tmp3 number;
    tmp4 varchar(4000);
    tmp5 varchar(4000);
    tmp6 varchar(4000);
    tmp7 varchar(4000);
begin
    icdStructureID := getICD10CASTRUCTUREIDBYYEAR(version_year);
    codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');

    dbms_output.put_line(rpad('Information about ' || catCode || ' for year ' || version_year, 30, ' '));
    dbms_output.put_line(rpad('=', 45, '='));
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);

    select p.domainelementid
    into domainElementID
    from element e
    join elementversion ev on e.elementid = ev.elementid
    join structureelementversion sev on ev.elementversionid = sev.elementversionid
    join propertyversion p on ev.elementversionid = p.propertyid
    join textpropertyversion tp on p.propertyid = tp.textpropertyid
    where e.classid = codeClassID
    and sev.structureid = icdStructureID
    and tp.text = catCode;

    dbms_output.put_line('');
    dbms_output.put_line('');
    dbms_output.put_line('Listing out TEXT Properties:');
    dbms_output.put_line(rpad('-', 45, '-'));
    FOR icd_rec IN (
        SELECT t.textpropertyid, t.text, t.languagecode, ev.status, e.classid, c.classname
        FROM TEXTPROPERTYVERSION t
        join PROPERTYVERSION p on t.textpropertyid = p.propertyid and p.domainelementid = domainElementID
        join ELEMENTVERSION ev on p.propertyid = ev.elementversionid
        join ELEMENT e on ev.elementid = e.elementid
        join CLASS c on e.classid = c.classid)
    LOOP
        DBMS_OUTPUT.put_line (icd_rec.textpropertyid || '   ' || substr(icd_rec.text, 1, 10) || '   ' || icd_rec.status || '   ' || icd_rec.classid || '   ' || icd_rec.classname );
    END LOOP;


    dbms_output.put_line('');
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);
    dbms_output.put_line(rpad('Structure ID for ' || version_year, 25, ' ') || '    ' || icdStructureID);

EXCEPTION
    WHEN OTHERS THEN
        dbms_output.put_line(rpad('=', 45, '='));
        dbms_output.put_line('EXCEPTION THROWN');

end getICD10CAAllInformationByCode;
/
