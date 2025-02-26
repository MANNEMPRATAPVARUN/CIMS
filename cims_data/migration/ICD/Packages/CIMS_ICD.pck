create or replace package CIMS_ICD is
    TYPE ref_cursor IS REF CURSOR;
/*
    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;
    errString varchar(4000);
    isVersionYear boolean := FALSE;

    PROCEDURE compareXMLData(version_Code_from number, version_Code_to number, type_code varchar2, lang varchar2, typeClassID number);
    PROCEDURE cleanUp;
    PROCEDURE part2_XML(version_Code_from IN number, version_Code_to IN number);
    PROCEDURE part1_text(version_Code_from IN number, version_Code_to IN number);
*/
    function getICD10CAClassID(tblName varchar2, cName varchar2) return number;
    FUNCTION GETICD10CASTRUCTUREIDBYYEAR(VERSION_CODE NUMBER) RETURN NUMBER;
    FUNCTION GETICD10CAROOT(P_VERSION_CODE IN VARCHAR2) RETURN NUMBER;
    function getICD10CAChapterInfo(chapterElementID number, version_code number, lang varchar2) RETURN CIMS_ICD.ref_cursor;
    function getICD10CANodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN number, lang_code IN varchar2) RETURN CIMS_ICD.ref_cursor;
    function getICD10CARootNode(version_code IN varchar2, lang_code IN varchar2) RETURN CIMS_ICD.ref_cursor;
    PROCEDURE UpdateCode;
    PROCEDURE RevertCode;
    PROCEDURE deleteicd;
    PROCEDURE DELETEICDBYYEAR(version_code number);
    PROCEDURE TABLE_STATS;

end CIMS_ICD;
/
create or replace package body CIMS_ICD is


    /**************************************************************************************************************************************
    * NAME:          RevertCode
    * DESCRIPTION:   Reverts the code to its original state.  Looks for traces of '/' or '.' and removes them.                 
    **************************************************************************************************************************************/
    procedure RevertCode is
        cursor c is
            SELECT 
                t.textpropertyid, t.text, 
                REPLACE(REPLACE(t.text, '.', ''), '/', '')  REVERTEDTEXT
            FROM TEXTPROPERTYVERSION t
            JOIN PROPERTYVERSION p on t.textpropertyid = p.propertyid
            JOIN ELEMENTVERSION ev on p.propertyid = ev.elementversionid
            JOIN ELEMENT e on e.elementid = ev.elementid
            JOIN CLASS c on e.classid = c.classid
            WHERE c.classname = 'Code'
            AND (t.text LIKE '%/%' OR t.text LIKE '%.%');

        rec_cc c%rowtype;
        tid number;
        oldText VARCHAR2(30);
        newText VARCHAR2(30);
    begin
         for rec_cc in c loop
             tid := TRIM(rec_cc.textpropertyid);
             oldText := TRIM(rec_cc.text);
             newText := TRIM(rec_cc.revertedtext);

             UPDATE TEXTPROPERTYVERSION t
             SET t.text = newText
             WHERE t.textpropertyid = tid;
             
         end loop;

         commit;

    end RevertCode;

    /**************************************************************************************************************************************
    * NAME:          UpdateCode
    * DESCRIPTION:   If ICD10 code (starts with a letter) apply format as follows: One dot after three digits
    *                If ICD10CA morphology code (starts with a number) apply format: Always one slash ("/") before the last digit
    **************************************************************************************************************************************/
    procedure UpdateCode is
        cursor c1 is
            SELECT 
                t.textpropertyid, t.text, SUBSTR(t.text,1, 3) || '.' || SUBSTR(t.text,4) NEWTEXT
            --    LENGTH(TRIM(TRANSLATE(t.text, ' +-.0123456789', ' '))) nullIfNumeric
            FROM TEXTPROPERTYVERSION t
            JOIN PROPERTYVERSION p on t.textpropertyid = p.propertyid
            JOIN ELEMENTVERSION ev on p.propertyid = ev.elementversionid
            JOIN ELEMENT e on e.elementid = ev.elementid
            JOIN CLASS c on e.classid = c.classid
            WHERE c.classname = 'Code'
            AND LENGTH(TRIM(TRANSLATE(SUBSTR(t.text, 1, 1), ' +-.0123456789', ' '))) is NOT null
            AND LENGTH(t.text) > 3
            AND t.text NOT LIKE '%-%';
        
        cursor c is
            SELECT 
                t.textpropertyid, t.text, SUBSTR(t.text,1, LENGTH(t.text) - 1) || '/' || SUBSTR(t.text,LENGTH(t.text), 1) NEWTEXT
                --LENGTH(TRIM(TRANSLATE(t.text, ' +-.0123456789', ' '))) nullIfNumeric
            FROM TEXTPROPERTYVERSION t
            JOIN PROPERTYVERSION p on t.textpropertyid = p.propertyid
            JOIN ELEMENTVERSION ev on p.propertyid = ev.elementversionid
            JOIN ELEMENT e on e.elementid = ev.elementid
            JOIN CLASS c on e.classid = c.classid
            WHERE c.classname = 'Code'
            AND LENGTH(TRIM(TRANSLATE(SUBSTR(t.text, 1, 1), ' +-.0123456789', ' '))) is null
            AND LENGTH(t.text) > 3
            AND t.text NOT LIKE '%-%';

        rec_cc c%rowtype;
        rec_cc1 c1%rowtype;
        tid number;
        oldText VARCHAR2(30);
        newText VARCHAR2(30);
    begin
         for rec_cc in c loop
             tid := TRIM(rec_cc.textpropertyid);
             oldText := TRIM(rec_cc.text);
             newText := TRIM(rec_cc.newtext);

             UPDATE TEXTPROPERTYVERSION t
             SET t.text = newText
             WHERE t.textpropertyid = tid;
             
         end loop;

         for rec_cc1 in c1 loop
             tid := TRIM(rec_cc1.textpropertyid);
             oldText := TRIM(rec_cc1.text);
             newText := TRIM(rec_cc1.newtext);

             UPDATE TEXTPROPERTYVERSION t
             SET t.text = newText
             WHERE t.textpropertyid = tid;

         end loop;

         commit;
    end UpdateCode;


    /**************************************************************************************************************************************
    * NAME:          getICD10CAClassID
    * DESCRIPTION:   Returns the Class ID for a given class name               
    **************************************************************************************************************************************/
    function getICD10CAClassID(tblName varchar2, cName varchar2) return number is
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


    /**************************************************************************************************************************************
    * NAME:          GETICD10CASTRUCTUREIDBYYEAR
    * DESCRIPTION:   Return the structure ID for a given year               
    **************************************************************************************************************************************/
    FUNCTION GETICD10CASTRUCTUREIDBYYEAR(VERSION_CODE NUMBER) RETURN NUMBER IS
        STRUCTUREID NUMBER;
        CID NUMBER;
    BEGIN
        CID := GETICD10CACLASSID('BASECLASSIFICATION', 'ICD-10-CA');

        SELECT EV.ELEMENTVERSIONID
        INTO STRUCTUREID
        FROM ELEMENTVERSION EV
        JOIN ELEMENT E ON EV.ELEMENTID = E.ELEMENTID
        JOIN STRUCTUREVERSION S ON S.STRUCTUREID = EV.ELEMENTVERSIONID
        WHERE E.CLASSID = CID
        AND EV.VERSIONCODE = VERSION_CODE;

        RETURN STRUCTUREID;

    EXCEPTION
        WHEN TOO_MANY_ROWS THEN
            RETURN -9999;
        WHEN NO_DATA_FOUND THEN
            RETURN -9999;
        WHEN OTHERS THEN
            RETURN -9999;


    END GETICD10CASTRUCTUREIDBYYEAR;


    /**************************************************************************************************************************************
    * NAME:          GETICD10CAROOT
    * DESCRIPTION:   Returns the Classification Root Element ID for a given year                
    **************************************************************************************************************************************/
    FUNCTION GETICD10CAROOT(P_VERSION_CODE IN VARCHAR2) RETURN NUMBER IS
        VIEWERROOTELEMENTID NUMBER;
        STRUCTID NUMBER;
    BEGIN

        STRUCTID := GETICD10CASTRUCTUREIDBYYEAR(P_VERSION_CODE);

        SELECT E.ELEMENTID 
        INTO VIEWERROOTELEMENTID
        FROM CLASS C
        JOIN ELEMENT E ON C.CLASSID = E.CLASSID
        JOIN ELEMENTVERSION EV ON E.ELEMENTID = EV.ELEMENTID AND EV.STATUS = 'ACTIVE'
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = STRUCTID
        WHERE UPPER(TRIM(C.TABLENAME)) = 'CONCEPTVERSION' AND UPPER(TRIM(C.CLASSNAME)) = 'CLASSIFICATIONROOT' 
        AND UPPER(TRIM(C.BASECLASSIFICATIONNAME)) = 'ICD-10-CA';

        RETURN VIEWERROOTELEMENTID;
    END GETICD10CAROOT;

    /**************************************************************************************************************************************
    * NAME:          getICD10CARootNode
    * DESCRIPTION:   Returns the Classification Root Node and its data               
    **************************************************************************************************************************************/
    function getICD10CARootNode(version_code IN varchar2, lang_code IN varchar2)
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


    /**************************************************************************************************************************************
    * NAME:          getICD10CAChapterInfo
    * DESCRIPTION:   Returns everything below a chapter.                 
    **************************************************************************************************************************************/
    function getICD10CAChapterInfo(chapterElementID number, version_code number, lang varchar2)
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


    /**************************************************************************************************************************************
    * NAME:          getICD10CANodeData
    * DESCRIPTION:   Returns 1 level of data.  Will return everything about the node.
    *                Passing in a null node element ID will result in returing Chapters               
    **************************************************************************************************************************************/
    function getICD10CANodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN number, lang_code IN varchar2)
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
                join ENUMERATEDPROPERTYVERSION epv on p1.propertyid = epv.enumeratedpropertyid
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
                MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator'), text, NULL)) CA_ENHANCEMENT_FLAG,
                MAX(DECODE(classid, getICD10CAClassID('EnumeratedPropertyVersion', 'DaggerAsteriskIndicator'), text, NULL)) DAGGER_ASTERISK,
                MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'ValidCodeIndicator'), text, NULL)) VALID_CODE,
                MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableIndicator'), text, NULL)) RENDER_CHILDREN_AS_TABLE_FLAG,
                (
                select xp.xmltext
                from PROPERTYVERSION p1
                     join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                     join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                     join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = icdStructureID
                     join ELEMENT e on ev.elementid = e.elementid
                where p1.domainelementid = ep.domainelementid 
                and e.classid = getICD10CAClassID('XMLPropertyVersion', 'IncludePresentation') 
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
                and e.classid = getICD10CAClassID('XMLPropertyVersion', 'ExcludePresentation') 
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
                and e.classid = getICD10CAClassID('XMLPropertyVersion', 'CodeAlsoPresentation') 
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
                and e.classid = getICD10CAClassID('XMLPropertyVersion', 'NotePresentation') 
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
                and e.classid = getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation') 
                and xp.languagecode = lang_code
                ) TABLE_OUTPUT,     
                ep.domainelementid ElementID,
                (select ELEMENTID from element where classid = getICD10CAClassID('ConceptVersion', 'ClassificationRoot')) ParentElementID
            FROM elementPropertys ep
            GROUP BY ep.domainelementid
            ORDER BY Code;

            RETURN rootNodeData_cursor;
        exception
            when others then
                dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

    end getICD10CANodeData;


    /**************************************************************************************************************************************
    * NAME:          DELETEICD
    * DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.
    *                Prints a summary table listing row counts of all tables afterwards
    **************************************************************************************************************************************/
    procedure DELETEICD is
        cID number;
        v_count integer;
    begin

        cID := getICD10CAClassID('BaseClassification', 'ICD-10-CA');

        --DELETE FROM H_ICD_TEMP;
        --COMMIT;

        --Insert into H_ICD_TEMP any ElementID that is related to ICD
        INSERT INTO Z_ICD_TEMP
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
        INSERT INTO Z_ICD_TEMP1
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
        WHERE e.elementid in (SELECT elementID FROM Z_ICD_TEMP);

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid IN (SELECT * FROM Z_ICD_TEMP1);

        commit;

        dbms_output.put_line(rpad('Table Name', 30, ' ') || '    ' || 'Count');
        dbms_output.put_line(rpad('=', 45, '='));

        for r in (select table_name, owner from all_tables
                  where owner = 'CIMS_D1')
        loop
            execute immediate 'select count(*) from ' || r.table_name
                into v_count;
            dbms_output.put_line(rpad(r.table_name, 30, ' ') || '    ' || v_count);
        end loop;

    end DELETEICD;


    /**************************************************************************************************************************************
    * NAME:          DELETEICDBYYEAR
    * DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.
    *                Prints a summary table listing row counts of all tables afterwards
    **************************************************************************************************************************************/
    procedure DELETEICDBYYEAR(version_code number) is      
        v_count integer;
        sID number;
    begin
        sID := geticd10castructureidbyyear(version_code);

        DELETE FROM Z_ICD_TEMP;
        DELETE FROM Z_ICD_TEMP1;
        COMMIT;

        --Insert into H_ICD_TEMP any ElementID that is related to ICD
        INSERT INTO Z_ICD_TEMP
        select ev.elementid
        from ELEMENTVERSION ev
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.structureid
        where se.structureid = sID;

        --Delete all ElementVersion that is related to ICD.
        --Causes a cascade delete across most tables (*PROPERTYVERSION)
        DELETE FROM ELEMENTVERSION ev
        WHERE ev.elementversionid IN (
            select se.elementversionid
            from ELEMENTVERSION ev
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.structureid
            where se.structureid = sID
        );

        --Most of the ELEMENTVERSION records are deleted, but not the base classsifications
        --Expecting multiple records, so you need to store the Element IDs before you can delete them
        --because you need to delete the ELEMENTVERSION first
        INSERT INTO Z_ICD_TEMP1
        select ev.elementid
        from ELEMENTVERSION ev
        where ev.elementversionid = sID;

        --Delete those base classification ELEMENTVERSIONs
        DELETE FROM ELEMENTVERSION ev
        WHERE ev.elementversionid IN (
            select ev.elementversionid
            from ELEMENTVERSION ev
            where ev.elementversionid = sID
        );

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid in (SELECT elementID FROM Z_ICD_TEMP);

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid IN (SELECT * FROM Z_ICD_TEMP1);

        commit;

        dbms_output.put_line(rpad('Table Name', 30, ' ') || '    ' || 'Count');
        dbms_output.put_line(rpad('=', 45, '='));

        for r in (select table_name, owner from all_tables
                  where owner = 'CIMS_D1')
        loop
            execute immediate 'select count(*) from ' || r.table_name
                into v_count;
            dbms_output.put_line(rpad(r.table_name, 30, ' ') || '    ' || v_count);
        end loop;

    end DELETEICDBYYEAR;

    /**************************************************************************************************************************************
    * NAME:          TABLE_STATS
    * DESCRIPTION:   Prints a summary table listing row counts of all tables afterwards               
    **************************************************************************************************************************************/
    procedure TABLE_STATS is
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

end CIMS_ICD;
/
