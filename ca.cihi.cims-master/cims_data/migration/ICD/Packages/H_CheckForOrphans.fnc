create or replace function H_CheckForOrphans(chapterElementID number, version_code number)
    RETURN TYPES.ref_cursor
AS
    codeClassID number := 0;
    icdStructureID number := 0;

    rootNodeData_cursor TYPES.ref_cursor;
    begin
        codeClassID := getICD10CAClassID('TextProperty', 'Code');
        icdStructureID := getICD10CASTRUCTUREIDBYYEAR(version_code);
                       
        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT e.elementid, e.notes, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTY cp
                join PROPERTY p on p.propertyid = cp.conceptpropertyid
                join ELEMENT e on p.domainelementid = e.elementid
                join PROPERTY p1 on e.elementid = p1.domainelementid
                join TEXTPROPERTY tp on p1.propertyid = tp.textpropertyid
                join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid
                join STRUCTUREELEMENT se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                join ELEMENT e1 on ev.elementid = e1.elementid
                WHERE e1.classid = codeClassID
                )
            SELECT ep.elementid, ep.notes, ep.ParentElementID, ep.text, level treeLevel
            FROM elementPropertys ep
--            WHERE level < 3
            WHERE level = 2
            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
            start with ep.elementid = chapterElementID
            ORDER SIBLINGS BY ep.text;

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


end H_CheckForOrphans;
/
