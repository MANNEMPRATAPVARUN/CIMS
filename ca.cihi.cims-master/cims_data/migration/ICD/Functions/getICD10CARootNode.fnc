create or replace function getICD10CARootNode(version_code IN varchar2, lang_code IN varchar2)
    RETURN CIMS_ICD.ref_cursor
AS
    rootNodeData_cursor CIMS_ICD.ref_cursor;
    icdElementID number := 0;
    icdStructureID number := 0;

    BEGIN
        icdElementID := getICD10CARoot(version_code);
        icdStructureID := getICD10CASTRUCTUREIDBYYEAR(version_code);

        OPEN rootNodeData_cursor FOR
        WITH elementPropertys AS (
            select tp.text, p.domainelementid, e.classid
            FROM TEXTPROPERTYVERSION tp
            join PROPERTYVERSION p on tp.textpropertyid = p.propertyid
            join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
            join ELEMENT e on e.elementid = ev.elementid
            WHERE p.domainelementid = icdElementID                                                 
            and tp.languagecode = lang_code
            )
        SELECT 
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'ShortTitle'), text, NULL)) ShortTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'LongTitle'), text, NULL)) LongTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'UserTitle'), text, NULL)) UserTitle,
            ep.domainelementid ElementID,
            icdElementID ParentElementID  
        FROM elementPropertys ep
        GROUP BY ep.domainelementid;

        RETURN rootNodeData_cursor;
    EXCEPTION
        when TOO_MANY_ROWS then
            RETURN rootNodeData_cursor;
        when NO_DATA_FOUND then
            RETURN rootNodeData_cursor;
        when others then
            RETURN rootNodeData_cursor;

end getICD10CARootNode;
/
