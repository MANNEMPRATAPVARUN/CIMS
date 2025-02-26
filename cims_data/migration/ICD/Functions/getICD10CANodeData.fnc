create or replace function getICD10CANodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN number, lang_code IN varchar2)
    RETURN CIMS_ICD.ref_cursor
AS
    rootNodeData_cursor CIMS_ICD.ref_cursor;
    eID number := 0;
    icdStructureID number := 0;

    begin
        icdStructureID := getICD10CASTRUCTUREIDBYYEAR(version_code);
        IF (nodeElementID is null) THEN
            eID := getICD10CARoot(version_code);
        ELSE
            eID := nodeElementID;
        END IF;
                        
        OPEN rootNodeData_cursor FOR
        WITH elementPropertys AS (
            select tp.text, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = icdStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join TextpropertyVERSION tp on p1.propertyid = tp.textpropertyid and tp.languagecode = lang_code
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = icdStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID

            union all

            select bp.booleanvalue, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = icdStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join BOOLEANPROPERTYVERSION bp on p1.propertyid = bp.booleanpropertyid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = icdStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID

            union all

            select en.literalvalue, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = icdStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join ENUMERATIONPROPERTYVERVALUE epv on p1.propertyid = epv.enumeratedpropertyid
            join ENUMERATION en on epv.domainid = en.domainid and epv.domainvalueid = en.domainvalueid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = icdStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            )
        SELECT
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'ShortTitle'), text, NULL)) ShortTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'LongTitle'), text, NULL)) LongTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'UserTitle'), text, NULL)) UserTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'Code'), text, NULL)) Code,
            MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementFlag'), text, NULL)) CA_ENHANCEMENT_FLAG,
            MAX(DECODE(classid, getICD10CAClassID('EnumeratedPropertyVersion', 'DaggerAsterisk'), text, NULL)) DAGGER_ASTERISK,
            MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'VALID_CODE'), text, NULL)) VALID_CODE,
            MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableFlag'), text, NULL)) RENDER_CHILDREN_AS_TABLE_FLAG,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid 
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'INCLUDE_PRESENTATION') 
            and xp.languagecode = lang_code
            ) INCLUDE_PRESENTATION, 
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid 
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'EXCLUDE_PRESENTATION') 
            and xp.languagecode = lang_code
            ) EXCLUDE_PRESENTATION, 
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid 
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'CODE_ALSO_PRESENTATION') 
            and xp.languagecode = lang_code
            ) CODE_ALSO_PRESENTATION, 
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid 
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'NOTE_PRESENTATION') 
            and xp.languagecode = lang_code
            ) NOTE_PRESENTATION, 
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid 
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'TABLE_OUTPUT') 
            and xp.languagecode = lang_code
            ) TABLE_OUTPUT,     
            ep.domainelementid ElementID,
            (select ELEMENTID from element where classid = getICD10CAClassID('ConceptVersion', 'ViewerRoot')) ParentElementID
        FROM elementPropertys ep
        GROUP BY ep.domainelementid
        ORDER BY Code;

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

end getICD10CANodeData;
/
