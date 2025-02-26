create or replace function getICD10CAChapterInfo(chapterElementID number, version_code number, lang varchar2)
    RETURN CIMS_ICD.ref_cursor
AS
    codeClassID number := 0;
    shortTitleClassID number := 0;
    icdStructureID number := 0;

    rootNodeData_cursor CIMS_ICD.ref_cursor;
    begin
        codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');
        shortTitleClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        icdStructureID := getICD10CASTRUCTUREIDBYYEAR(version_code);
                       
        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT e.elementid, e.notes, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join PROPERTYVERSION p on p.propertyid = cp.conceptpropertyid
                join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' --Narrow Relationship EV
                join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                join ELEMENT e on p.domainelementid = e.elementid
                join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
                join STRUCTUREELEMENTVERSION se on ev2.elementversionid = se.elementversionid and se.structureid = icdStructureID
                join PROPERTYVERSION p1 on e.elementid = p1.domainelementid
                join TEXTPROPERTYVERSION tp on p1.propertyid = tp.textpropertyid 
                join ELEMENTVERSION ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE' --Code EV
                join STRUCTUREELEMENTVERSION se on ev1.elementversionid = se.elementversionid and se.structureid = icdStructureID
                join ELEMENT e1 on ev1.elementid = e1.elementid
                WHERE e1.classid = codeClassID
                )
            SELECT ep.elementid, ep.notes, ep.ParentElementID, ep.text, level + 1 as treeLevel,
                (
                SELECT tp1.text
                FROM TEXTPROPERTYVERSION tp1
                join PROPERTYVERSION p1 on tp1.textpropertyid = p1.propertyid
                join ELEMENTVERSION ev1 on ev1.elementversionid = p1.propertyid and ev1.status = 'ACTIVE'
                join STRUCTUREELEMENTVERSION se on ev1.elementversionid = se.elementversionid and se.structureid = icdStructureID
                join ELEMENT e1 on e1.elementid = ev1.elementid
                where p1.domainelementid = ep.elementID
                and e1.classid = shortTitleClassID
                and tp1.languagecode = lang
                ) ShortTitle
            FROM elementPropertys ep
            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
            start with ep.elementid = chapterElementID
            ORDER SIBLINGS BY ep.text;

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


end getICD10CAChapterInfo;
/
