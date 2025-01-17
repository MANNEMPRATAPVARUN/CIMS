create or replace package CIMS_CCI is

    cci_classification_code varchar2(20) := 'CCI';

    TYPE ref_cursor IS REF CURSOR;

    FUNCTION getCCIClassID(tblName varchar2, cName varchar2) return number;
    FUNCTION getCCIStructureByYear(VERSION_CODE varchar2) RETURN NUMBER;
    FUNCTION getCCIRoot(P_VERSION_CODE IN VARCHAR2) RETURN NUMBER;
    FUNCTION getCCISectionInfo(chapterCode varchar2, version_code varchar2, lang varchar2) RETURN CIMS_CCI.ref_cursor;
    FUNCTION getCCINodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN varchar2, lang_code IN varchar2) RETURN CIMS_CCI.ref_cursor;
    FUNCTION getCCIRootNode(version_code IN varchar2, lang_code IN varchar2) RETURN CIMS_CCI.ref_cursor;

    --Attribute Functions
    FUNCTION getCCIGenericAttribute(version_code varchar2, attribute_type varchar2) RETURN CIMS_CCI.ref_cursor;
    FUNCTION getCCIReferenceAttribute(version_code varchar2, attribute_type varchar2) RETURN CIMS_CCI.ref_cursor;

    PROCEDURE deletecci;
    PROCEDURE deleteCCIByYear(version_code varchar2);
    PROCEDURE table_Stats(sName varchar2 DEFAULT NULL);
    PROCEDURE gather_Schema_Stats(sName varchar2 DEFAULT NULL);

    --Format Code functions
    FUNCTION formatCode(code varchar2) return varchar2;
    PROCEDURE UpdateCode;
    PROCEDURE UpdateCodeInClob;
    FUNCTION formatXREFClob(p_Source IN CLOB) RETURN CLOB;
    FUNCTION getCCISectionElementId(sectionCode varchar2, contextId number) RETURN NUMBER;

    FUNCTION checkRunStatus RETURN varchar2;

end CIMS_CCI;
/
create or replace package body CIMS_CCI is


    /**************************************************************************************************************************************
    * NAME:          formatXREFClob
    * DESCRIPTION:   Specific function just for formating a XREF code
    **************************************************************************************************************************************/
    FUNCTION formatXREFClob(p_Source IN CLOB)
        RETURN CLOB
    IS
        startPosition number;
        position number;
        position1 number;
        stringLength number;
        v_String CLOB;
        v_Result CLOB;
        searchString  VARCHAR2(20);
    BEGIN
        stringLength   := LENGTH(p_Source);
        startPosition := 1;
        searchString := 'REFID';

        WHILE startPosition <= LENGTH(p_Source) LOOP
            position := INSTR(UPPER(p_Source), searchString, startPosition);
            position := INSTR(p_Source, '=', position);
            position := INSTR(p_Source, '"', position);
            position1 := INSTR(p_Source, '"', position + 1);

            IF position = 0 THEN
               v_Result := v_Result || SUBSTR(p_Source, startPosition, stringLength - startPosition + 1);
               return v_Result;
            END IF;

            --Retrieve the Code
            v_String := formatCode(SUBSTR(p_Source, position + 1, position1 - position - 1));

            v_Result := v_Result || SUBSTR(p_Source, startPosition, position - startPosition + 1);
            v_Result := v_Result || v_string;
            startPosition  := position1;
        END LOOP;

        RETURN v_Result;

    EXCEPTION
        when others then
            raise_application_error(-20011, 'Error! formatXREFClob: ' || substr(sqlerrm, 1, 512));

    END formatXREFClob;


    /**************************************************************************************************************************************
    * NAME:          formatCode
    * DESCRIPTION:   Formats CCI Codes to add punctuation.
    *                Applies to code length greater than 2, and does not contain a '-'
    **************************************************************************************************************************************/
    FUNCTION formatCode(code varchar2)
        RETURN VARCHAR2
    IS
        updatedCode VARCHAR2(30);

    BEGIN

        IF (INSTR(code, '.') = 0) THEN
            IF LENGTH(nvl(code, 'X')) <= 2 OR code LIKE '%-%' THEN
                updatedCode := code;
            ELSE
                IF (LENGTH(code) = 3) THEN
                    updatedCode := SUBSTR(code, 1, 1) || '.' || SUBSTR(code, 2) || '.^^.^^';
                ELSIF (LENGTH(code) = 5) THEN
                    updatedCode := SUBSTR(code, 1, 1) || '.' || SUBSTR(code, 2, 2) || '.' || SUBSTR(code, 4) || '.^^';
                ELSIF (LENGTH(code) = 7) THEN
                    updatedCode := SUBSTR(code, 1, 1) || '.' || SUBSTR(code, 2, 2) || '.' || SUBSTR(code, 4, 2) || '.' || SUBSTR(code, 6);
                ELSIF (LENGTH(code) = 9) THEN
                    updatedCode := SUBSTR(code, 1, 1) || '.' || SUBSTR(code, 2, 2) || '.' || SUBSTR(code, 4, 2) || '.' || SUBSTR(code, 6,2) || '-' || SUBSTR(code, 8);
                ELSIF (LENGTH(code) = 10) THEN
                    updatedCode := SUBSTR(code, 1, 1) || '.' || SUBSTR(code, 2, 2) || '.' || SUBSTR(code, 4, 2) || '.' || SUBSTR(code, 6,2) || '-' || SUBSTR(code, 8, 2) || '-' || SUBSTR(code, 10);
                ELSE
                    updatedCode := code;
                END IF;
            END IF;
        ELSE
            --Already contains the converted code
            updatedCode := code;
        END IF;

        RETURN TRIM(updatedCode);

    exception
        when others then
            raise_application_error(-20011, 'Error! formatCode: ' || substr(sqlerrm, 1, 512));

    end formatCode;


    /**************************************************************************************************************************************
    * NAME:          UpdateCode
    * DESCRIPTION:   Updates all CCI codes to add in punctuation
    **************************************************************************************************************************************/
    PROCEDURE UpdateCode is

        codeClassID number := getCCIClassID('TextPropertyVersion', 'Code');

        cursor c is
            SELECT
                t.textpropertyid, t.text, formatCode(TRIM(t.text)) newText
            FROM TEXTPROPERTYVERSION t
            WHERE t.classid = codeClassID
            AND LENGTH(t.text) > 2
            AND t.text NOT LIKE '%-%';

        rec_cc c%rowtype;
        tid number;
        oldText VARCHAR2(30);
        newText VARCHAR2(30);

    BEGIN
         for rec_cc in c loop
             tid := TRIM(rec_cc.textpropertyid);
             oldText := TRIM(rec_cc.text);
             newText := TRIM(rec_cc.newtext);

             UPDATE TEXTPROPERTYVERSION t
             SET t.text = newText
             WHERE t.textpropertyid = tid;

         end loop;

         commit;
    end UpdateCode;


    /**************************************************************************************************************************************
    * NAME:          UpdateCodeInClob
    * DESCRIPTION:   Updates all XML/HTML clobs to add in punctuation for CCI Codes
    **************************************************************************************************************************************/
    PROCEDURE UpdateCodeInClob is

        cursor c is
            SELECT *
            FROM XMLPROPERTYVERSION x
            JOIN CLASS c on x.classid = c.classid
            WHERE UPPER(x.xmltext) like '%XREF%'
            AND c.baseclassificationname = 'CCI';

        cursor c1 is
            SELECT *
            FROM HTMLPROPERTYVERSION h
            JOIN CLASS c on h.classid = c.classid
            WHERE UPPER(h.htmltext) like '%XREF%'
            AND c.baseclassificationname = 'CCI';

        rec_cc c%rowtype;
        rec_cc1 c1%rowtype;
        tid number;
        oldClob CLOB;

    BEGIN

        for rec_cc in c loop
            tid := TRIM(rec_cc.xmlpropertyid);
            oldClob := TRIM(rec_cc.xmltext);

            UPDATE XMLPROPERTYVERSION x
            SET x.xmltext = formatXREFClob(x.xmltext)
            WHERE x.xmlpropertyid = tid;
        end loop;

        for rec_cc1 in c1 loop
            tid := TRIM(rec_cc1.htmlpropertyid);
            oldClob := TRIM(rec_cc1.htmltext);

            UPDATE HTMLPROPERTYVERSION h
            SET h.htmltext = formatXREFClob(h.htmltext)
            WHERE h.htmlpropertyid = tid;
        end loop;


        commit;
        --rollback;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error UpdateCodeInClob!: ' || substr(sqlerrm, 1, 512));

    end UpdateCodeInClob;


    /**************************************************************************************************************************************
    * NAME:          checkRunStatus
    * DESCRIPTION:   Checks the log table to see if it has completed successfully.
    *                If it has not, then it is assumed that it is either currently running or has not completed successfully.
    *                It will do a second check to see if the run date is less than 30 minutes from the current time.
    *                Returns true if we will allow the migration script to run
    **************************************************************************************************************************************/
    FUNCTION checkRunStatus
        RETURN varchar2
    IS
        notRunning number;
    BEGIN
        --Allow to run if table is empty
        select count(*)
        into notRunning
        from log
        where classification = cci_classification_code;

        if (notRunning = 0) then
            return 'TRUE';
        end if;

        select count(*)
        into notRunning
        from log
        where run_id = (select max(run_id) from log)
        and message like 'Ending%'
        order by id;

        if (notRunning > 0) then
            return 'TRUE';
        end if;

        select count(*)
        into notRunning
        from log
        where run_id = (select max(run_id) from log)
        and messagedate < (SYSDATE - INTERVAL '30' MINUTE);

        if (notRunning > 0) then
            return 'TRUE';
        else
            return 'FALSE';
        end if;


    end checkRunStatus;


    /**************************************************************************************************************************************
    * NAME:          getCCIClassID
    * DESCRIPTION:   Returns the Class ID for a given class name
    **************************************************************************************************************************************/
    FUNCTION getCCIClassID(tblName varchar2, cName varchar2)
        RETURN NUMBER
    IS
        classID number;
    BEGIN
        SELECT c.CLASSID
        INTO classID
        FROM CLASS c
        WHERE UPPER(TRIM(c.TABLENAME)) = UPPER(TRIM(tblName))
        AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName))
        AND UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM('CCI'));

        return classID;
    end getCCIClassID;


    /**************************************************************************************************************************************
    * NAME:          getCCIStructureByYear
    * DESCRIPTION:   Return the structure ID for a given year
    **************************************************************************************************************************************/
    FUNCTION getCCIStructureByYear(VERSION_CODE varchar2)
        RETURN NUMBER
    IS
        STRUCTUREID NUMBER;
        CID NUMBER;
    BEGIN
        CID := getCCIClassID('BASECLASSIFICATION', 'CCI');

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

    END getCCIStructureByYear;


    /**************************************************************************************************************************************
    * NAME:          getCCIRoot
    * DESCRIPTION:   Returns the Classification Root Element ID for a given year
    **************************************************************************************************************************************/
    FUNCTION getCCIRoot(P_VERSION_CODE IN VARCHAR2)
        RETURN NUMBER
    IS
        VIEWERROOTELEMENTID NUMBER;
        STRUCTID NUMBER;
    BEGIN

        STRUCTID := getCCIStructureByYear(P_VERSION_CODE);

        SELECT E.ELEMENTID
        INTO VIEWERROOTELEMENTID
        FROM CLASS C
        JOIN ELEMENT E ON C.CLASSID = E.CLASSID
        JOIN ELEMENTVERSION EV ON E.ELEMENTID = EV.ELEMENTID AND EV.STATUS = 'ACTIVE'
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = STRUCTID
        WHERE UPPER(TRIM(C.TABLENAME)) = 'CONCEPTVERSION' AND UPPER(TRIM(C.CLASSNAME)) = 'CLASSIFICATIONROOT'
        AND UPPER(TRIM(C.BASECLASSIFICATIONNAME)) = 'CCI';

        RETURN VIEWERROOTELEMENTID;
    END getCCIRoot;

    /**************************************************************************************************************************************
    * NAME:          getCCIRootNode
    * DESCRIPTION:   Returns the Classification Root Node and its data
    **************************************************************************************************************************************/
    FUNCTION getCCIRootNode(version_code IN varchar2, lang_code IN varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        rootNodeData_cursor CIMS_CCI.ref_cursor;
        cciElementID number := 0;
        cciStructureID number := 0;

    BEGIN
        cciElementID := getCCIRoot(version_code);
        cciStructureID := getCCIStructureByYear(version_code);

        OPEN rootNodeData_cursor FOR
        WITH elementPropertys AS (
            select tp.text, p.domainelementid, e.classid
            FROM TEXTPROPERTYVERSION tp
            join PROPERTYVERSION p on tp.textpropertyid = p.propertyid
            join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
            join ELEMENT e on e.elementid = ev.elementid
            WHERE p.domainelementid = cciElementID
            and tp.languagecode = lang_code
            )
        SELECT
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'ShortTitle'), text, NULL)) ShortTitle,
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'LongTitle'), text, NULL)) LongTitle,
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'UserTitle'), text, NULL)) UserTitle,
            ep.domainelementid ElementID,
            cciElementID ParentElementID
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

    end getCCIRootNode;


    /**************************************************************************************************************************************
    * NAME:          getCCISectionInfo
    * DESCRIPTION:   Returns everything below a section
    **************************************************************************************************************************************/
    FUNCTION getCCISectionInfo(chapterElementID number, version_code varchar2, lang varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        codeClassID number := getCCIClassID('TextPropertyVersion', 'Code');
        narrowClassID number := getCCIClassID('ConceptPropertyVersion', 'Narrower');
        shortTitleClassID number := getCCIClassID('TextPropertyVersion', 'UserTitle');
        cciStructureID number := getCCIStructureByYear(version_code);

        rootNodeData_cursor CIMS_CCI.ref_cursor;
    BEGIN

        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT e.elementid, e.notes, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join PROPERTYVERSION p on p.propertyid = cp.conceptpropertyid
                join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' --Narrow Relationship EV
                join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
                join ELEMENT e on p.domainelementid = e.elementid
                join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
                join STRUCTUREELEMENTVERSION se on ev2.elementversionid = se.elementversionid and se.structureid = cciStructureID
                join PROPERTYVERSION p1 on e.elementid = p1.domainelementid
                join TEXTPROPERTYVERSION tp on p1.propertyid = tp.textpropertyid
                join ELEMENTVERSION ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE' --Code EV
                join STRUCTUREELEMENTVERSION se on ev1.elementversionid = se.elementversionid and se.structureid = cciStructureID
                join ELEMENT e1 on ev1.elementid = e1.elementid
                WHERE e1.classid = codeClassID
                )
            SELECT ep.elementid, ep.notes, ep.ParentElementID, ep.text, level + 1 as treeLevel,
                (
                SELECT tp1.text
                FROM TEXTPROPERTYVERSION tp1
                join PROPERTYVERSION p1 on tp1.textpropertyid = p1.propertyid
                join ELEMENTVERSION ev1 on ev1.elementversionid = p1.propertyid and ev1.status = 'ACTIVE'
                join STRUCTUREELEMENTVERSION se on ev1.elementversionid = se.elementversionid and se.structureid = cciStructureID
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

    end getCCISectionInfo;


    /**************************************************************************************************************************************
    * NAME:          getCCISectionInfo
    * DESCRIPTION:   Returns everything below a section
    **************************************************************************************************************************************/
    FUNCTION getCCISectionInfo(chapterCode varchar2, version_code varchar2, lang varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        cciStructureID number := getCCIStructureByYear(version_code);
        chapterElementID number := 0;

        sectionClassID number := getCCIClassID('ConceptVersion', 'Section');
        blockClassID number := getCCIClassID('ConceptVersion', 'Block');
        groupClassID number := getCCIClassID('ConceptVersion', 'Group');
        rubricClassID number := getCCIClassID('ConceptVersion', 'Rubric');
        cciCodeClassID number := getCCIClassID('ConceptVersion', 'CCICODE');

    BEGIN
        SELECT t.domainelementid
        INTO chapterElementID
        FROM TEXTPROPERTYVERSION t
        JOIN PROPERTYVERSION p on t.textpropertyid = p.propertyid
        JOIN ELEMENTVERSION ev on p.propertyid = ev.elementversionid
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid
        JOIN ELEMENT e on t.domainelementid = e.elementid
        WHERE t.text = chapterCode
        and sev.structureid = cciStructureID
        AND e.classid IN (sectionClassID, blockClassID, groupClassID, rubricClassID, cciCodeClassID);

        RETURN getCCISectionInfo(chapterElementID, version_code, lang);

    EXCEPTION
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

    END getCCISectionInfo;


    /**************************************************************************************************************************************
    * NAME:          getCCINodeData
    * DESCRIPTION:   Returns 1 level of data.  Will return everything about the node.
    *                Passing in a null node element ID will result in returning Section
    **************************************************************************************************************************************/
    FUNCTION getCCINodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN varchar2, lang_code IN varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        rootNodeData_cursor CIMS_CCI.ref_cursor;
        eID number := 0;
        cciStructureID number := getCCIStructureByYear(version_code);
        narrowClassID number := getCCIClassID('ConceptPropertyVersion', 'Narrower');
        shortTitleClassID number := getCCIClassID('TextPropertyVersion', 'ShortTitle');
        longTitleClassID number := getCCIClassID('TextPropertyVersion', 'LongTitle');
        userTitleClassID number := getCCIClassID('TextPropertyVersion', 'UserTitle');
        codeClassID number := getCCIClassID('TextPropertyVersion', 'Code');

    BEGIN
        IF (nodeElementID is null) THEN
            eID := getCCIRoot(version_code);
        ELSE
            eID := nodeElementID;
        END IF;

        OPEN rootNodeData_cursor FOR
        WITH elementPropertys AS (
            select tp.text, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = cciStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join TextpropertyVERSION tp on p1.propertyid = tp.textpropertyid and tp.languagecode = lang_code
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = cciStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            and e1.classid IN ( shortTitleClassID, longTitleClassID, userTitleClassID)

            union all

            select tp.text, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = cciStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join TextpropertyVERSION tp on p1.propertyid = tp.textpropertyid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = cciStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            and e1.classid = codeClassID

            union all

            select bp.booleanvalue, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid and se2.structureid = cciStructureID
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join BOOLEANPROPERTYVERSION bp on p1.propertyid = bp.booleanpropertyid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid and se3.structureid = cciStructureID
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            )
        SELECT
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'ShortTitle'), text, NULL)) ShortTitle,
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'LongTitle'), text, NULL)) LongTitle,
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'UserTitle'), text, NULL)) UserTitle,
            MAX(DECODE(classid, getCCIClassID('TextPropertyVersion', 'Code'), text, NULL)) Code,
            MAX(DECODE(classid, getCCIClassID('BooleanPropertyVersion', 'CaEnhancementIndicator'), text, NULL)) CA_ENHANCEMENT_FLAG,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('XMLPropertyVersion', 'IncludePresentation')
            and xp.languagecode = lang_code
            ) INCLUDE_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('XMLPropertyVersion', 'ExcludePresentation')
            and xp.languagecode = lang_code
            ) EXCLUDE_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('XMLPropertyVersion', 'CodeAlsoPresentation')
            and xp.languagecode = lang_code
            ) CODE_ALSO_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('XMLPropertyVersion', 'NotePresentation')
            and xp.languagecode = lang_code
            ) NOTE_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('XMLPropertyVersion', 'OmitCodePresentation')
            and xp.languagecode = lang_code
            ) OMIT_PRESENTATION,
            (
            select hp.htmltext
            from PROPERTYVERSION p1
                 join HTMLPROPERTYVERSION hp on hp.htmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = cciStructureID
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getCCIClassID('HTMLPropertyVersion', 'TablePresentation')
            and hp.languagecode = lang_code
            ) TABLE_OUTPUT,
            ep.domainelementid ElementID,
            (select ELEMENTID from element where classid = getCCIClassID('ConceptVersion', 'ClassificationRoot')) ParentElementID
        FROM elementPropertys ep
        GROUP BY ep.domainelementid
        ORDER BY Code;

        RETURN rootNodeData_cursor;

    EXCEPTION
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 512));

    END getCCINodeData;


    /**************************************************************************************************************************************
    * NAME:          getCCIGenericAttribute
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION getCCIGenericAttribute(version_code varchar2, attribute_type varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        cciStructureID number := getCCIStructureByYear(version_code);
        attrClassID number := getCCIClassID('ConceptVersion', 'GenericAttribute');
        attrDescClassID number := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        attrCodeClassID number := getCCIClassID('TextPropertyVersion', 'AttributeCode');
        attrTypeIndicatorClassID number := getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
        domainValueCodeClassID number := getCCIClassID('TextPropertyVersion', 'DomainValueCode');

        rootNodeData_cursor CIMS_CCI.ref_cursor;
    BEGIN

        OPEN rootNodeData_cursor FOR

            SELECT t.text GEN_ATTR_CODE, t3.text ATTR_TYPE, t1.text ENG_DESC, t2.text FRA_DESC
            FROM TEXTPROPERTYVERSION t
            JOIN ELEMENTVERSION ev on t.textpropertyid = ev.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid
            JOIN ELEMENT e on t.domainelementid = e.elementid and e.classid = attrClassID
            JOIN TEXTPROPERTYVERSION t1 on t1.domainelementid = e.elementid AND t1.classid = attrDescClassID AND t1.languagecode = 'ENG'
            JOIN ELEMENTVERSION ev1 on t1.textpropertyid = ev1.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev1 on ev1.elementversionid = sev1.elementversionid
            JOIN TEXTPROPERTYVERSION t2 on t2.domainelementid = e.elementid AND t2.classid = attrDescClassID AND t2.languagecode = 'FRA'
            JOIN ELEMENTVERSION ev2 on t2.textpropertyid = ev2.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev2 on ev2.elementversionid = sev2.elementversionid
            JOIN PROPERTYVERSION p on e.elementid = p.domainelementid and p.classid = attrTypeIndicatorClassID
            JOIN ELEMENTVERSION ev3 on p.propertyid = ev3.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev3 on ev3.elementversionid = sev3.elementversionid
            JOIN CONCEPTPROPERTYVERSION cp on p.propertyid = cp.conceptpropertyid
            JOIN TEXTPROPERTYVERSION t3 on cp.rangeelementid = t3.domainelementid AND t3.classid = domainValueCodeClassID
            JOIN ELEMENTVERSION ev4 on t3.textpropertyid = ev4.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev4 on ev4.elementversionid = sev4.elementversionid
            WHERE t.classid = attrCodeClassID
            AND sev.structureid = cciStructureID
            AND sev1.structureid = cciStructureID
            AND sev2.structureid = cciStructureID
            AND sev3.structureid = cciStructureID
            AND sev4.structureid = cciStructureID
            AND t3.text = attribute_type
            ORDER BY t.text;

        RETURN rootNodeData_cursor;

    EXCEPTION
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    END getCCIGenericAttribute;


    /**************************************************************************************************************************************
    * NAME:          getCCIReferenceAttribute
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION getCCIReferenceAttribute(version_code varchar2, attribute_type varchar2)
        RETURN CIMS_CCI.ref_cursor
    AS
        cciStructureID number := getCCIStructureByYear(version_code);
        attrClassID number := getCCIClassID('ConceptVersion', 'ReferenceAttribute');
        attrDescClassID number := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        attrCodeClassID number := getCCIClassID('TextPropertyVersion', 'AttributeCode');
        attrTypeIndicatorClassID number := getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
        domainValueCodeClassID number := getCCIClassID('TextPropertyVersion', 'DomainValueCode');
        attrMandClassID number := getCCIClassID('BooleanPropertyVersion', 'AttributeMandatoryIndicator');
        attrNodeDescClassID number := getCCIClassID('XMLPropertyVersion', 'AttributeNoteDescription');

        rootNodeData_cursor CIMS_CCI.ref_cursor;

    BEGIN

        OPEN rootNodeData_cursor FOR

            SELECT t.text GEN_ATTR_CODE, t3.text ATTR_TYPE, bp.booleanvalue, t1.text ENG_DESC, t2.text FRA_DESC,
                to_char(xp.xmltext) ENG_NOTE, to_char(xp1.xmltext) FRA_NOTE
            FROM TEXTPROPERTYVERSION t
            JOIN ELEMENTVERSION ev on t.textpropertyid = ev.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid
            JOIN ELEMENT e on t.domainelementid = e.elementid and e.classid = attrClassID
            JOIN TEXTPROPERTYVERSION t1 on t1.domainelementid = e.elementid AND t1.classid = attrDescClassID AND t1.languagecode = 'ENG'
            JOIN ELEMENTVERSION ev1 on t1.textpropertyid = ev1.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev1 on ev1.elementversionid = sev1.elementversionid
            JOIN TEXTPROPERTYVERSION t2 on t2.domainelementid = e.elementid AND t2.classid = attrDescClassID AND t2.languagecode = 'FRA'
            JOIN ELEMENTVERSION ev2 on t2.textpropertyid = ev2.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev2 on ev2.elementversionid = sev2.elementversionid
            JOIN PROPERTYVERSION p on e.elementid = p.domainelementid and p.classid = attrTypeIndicatorClassID
            JOIN ELEMENTVERSION ev3 on p.propertyid = ev3.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev3 on ev3.elementversionid = sev3.elementversionid
            JOIN CONCEPTPROPERTYVERSION cp on p.propertyid = cp.conceptpropertyid
            JOIN TEXTPROPERTYVERSION t3 on cp.rangeelementid = t3.domainelementid AND t3.classid = domainValueCodeClassID
            JOIN ELEMENTVERSION ev4 on t3.textpropertyid = ev4.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev4 on ev4.elementversionid = sev4.elementversionid
            JOIN BOOLEANPROPERTYVERSION bp on e.elementid = bp.domainelementid and bp.classid = attrMandClassID
            JOIN ELEMENTVERSION ev5 on bp.booleanpropertyid = ev5.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev5 on ev5.elementversionid = sev5.elementversionid
            JOIN XMLPROPERTYVERSION xp on e.elementid = xp.domainelementid and xp.classid = attrNodeDescClassID and xp.languagecode = 'ENG'
            JOIN ELEMENTVERSION ev6 on xp.xmlpropertyid = ev6.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev6 on ev6.elementversionid = sev6.elementversionid
            JOIN XMLPROPERTYVERSION xp1 on e.elementid = xp1.domainelementid and xp1.classid = attrNodeDescClassID and xp1.languagecode = 'FRA'
            JOIN ELEMENTVERSION ev7 on xp1.xmlpropertyid = ev7.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev7 on ev7.elementversionid = sev7.elementversionid
            WHERE t.classid = attrCodeClassID
            AND sev.structureid = cciStructureID
            AND sev1.structureid = cciStructureID
            AND sev2.structureid = cciStructureID
            AND sev3.structureid = cciStructureID
            AND sev4.structureid = cciStructureID
            AND sev5.structureid = cciStructureID
            AND sev6.structureid = cciStructureID
            AND sev7.structureid = cciStructureID
            AND t3.text = attribute_type
            ORDER BY t.text;

        RETURN rootNodeData_cursor;

    EXCEPTION
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

    END getCCIReferenceAttribute;
    
    FUNCTION getCCISectionElementId(sectionCode varchar2, contextId number) 
      RETURN NUMBER
    AS
        nSectionId   NUMBER;
        sectionClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
        sectionCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
    BEGIN
        with strelementversion as
       (
        select elementversionid, elementid from structureelementversion where structureid=contextId
        UNION  ALL 
        select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
        and not exists ( 
           select elementid from structureelementversion cv where cv.structureid=contextId
           and cv.elementid = sv.elementid 
       ))
        SELECT T.Domainelementid
        INTO nSectionId
        FROM TEXTPROPERTYVERSION T
        JOIN strelementversion SEV on T.textpropertyid = SEV.elementversionid 
        JOIN ELEMENT E on T.Domainelementid = E.Elementid and E.Classid = sectionClassID
        WHERE T.Text = sectionCode and T.Classid = sectionCodeClassID;
        
        return nSectionId;
    END getCCISectionElementId;
      


    /**************************************************************************************************************************************
    * NAME:          getCCIValidationRules
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*
    function getCCIValidationRules(version_code number)
        RETURN CIMS_CCI.ref_cursor
    AS
        cciStructureID number := getCCIStructureByYear(version_code);

        validationClassID number := getCCIClassID('ConceptVersion', 'ValidationCCI');
        attrCodeClassID number := getCCIClassID('TextPropertyVersion', 'AttributeCode');
        sexValidationIndicatorClassID number := getCCIClassID('ConceptPropertyVersion', 'SexValidationIndicator');
        domainValueCodeClassID number := getCCIClassID('TextPropertyVersion', 'DomainValueCode');
        lRefAttrClassID number := getCCIClassID('ConceptPropertyVersion', 'LocationReferenceAttributeCPV');
        eRefAttrClassID number := getCCIClassID('ConceptPropertyVersion', 'ExtentReferenceAttributeCPV');
        sRefAttrClassID number := getCCIClassID('ConceptPropertyVersion', 'StatusReferenceAttributeCPV');
        ageMinClassID number := getCCIClassID('NumericPropertyVersion', 'AgeMinimum');
        ageMaxClassID number := getCCIClassID('NumericPropertyVersion', 'AgeMaximum');

        rootNodeData_cursor CIMS_CCI.ref_cursor;
        begin

            OPEN rootNodeData_cursor FOR
                SELECT e.elementid, z.a, t.text GENDER, n.numericvalue AgeMin, n1.numericvalue AgeMax, t1.text LOCATION, t2.text EXTENT, t3.text STATUS
                FROM ELEMENT e
                JOIN z_icd_temp z on z.b = e.elementid
                JOIN NUMERICPROPERTYVERSION n on e.elementid = n.domainelementid and n.classid = ageMinClassID  --AgeMin
                JOIN ELEMENTVERSION ev on n.numericpropertyid = ev.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid
                JOIN NUMERICPROPERTYVERSION n1 on e.elementid = n1.domainelementid and n1.classid = ageMaxClassID  --AgeMax
                JOIN ELEMENTVERSION ev1 on n1.numericpropertyid = ev1.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev1 on ev1.elementversionid = sev1.elementversionid
                JOIN ELEMENTVERSION ev2 on e.elementid = ev2.elementid
                JOIN STRUCTUREELEMENTVERSION sev2 on ev2.elementversionid = sev2.elementversionid

                JOIN PROPERTYVERSION p on e.elementid = p.domainelementid and p.classid = sexValidationIndicatorClassID  --Gender
                JOIN CONCEPTPROPERTYVERSION cp on p.propertyid = cp.conceptpropertyid
                JOIN TEXTPROPERTYVERSION t on cp.rangeelementid = t.domainelementid AND t.classid = domainValueCodeClassID
                JOIN ELEMENTVERSION ev3 on t.textpropertyid = ev3.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev3 on ev3.elementversionid = sev3.elementversionid

                LEFT OUTER JOIN PROPERTYVERSION p1 on e.elementid = p1.domainelementid and p1.classid = lRefAttrClassID --Location
                LEFT OUTER JOIN CONCEPTPROPERTYVERSION cp1 on p1.propertyid = cp1.conceptpropertyid
                LEFT OUTER JOIN TEXTPROPERTYVERSION t1 on cp1.rangeelementid = t1.domainelementid AND t1.classid = attrCodeClassID
                LEFT OUTER JOIN ELEMENTVERSION ev4 on t1.textpropertyid = ev4.elementversionid
                LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev4 on ev4.elementversionid = sev4.elementversionid AND sev4.structureid = cciStructureID  --Only add if possibly not exist

                LEFT OUTER JOIN PROPERTYVERSION p2 on e.elementid = p2.domainelementid and p2.classid = eRefAttrClassID --Extent
                LEFT OUTER JOIN CONCEPTPROPERTYVERSION cp2 on p2.propertyid = cp2.conceptpropertyid
                LEFT OUTER JOIN TEXTPROPERTYVERSION t2 on cp2.rangeelementid = t2.domainelementid AND t2.classid = attrCodeClassID
                LEFT OUTER JOIN ELEMENTVERSION ev5 on t2.textpropertyid = ev5.elementversionid
                LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev5 on ev5.elementversionid = sev5.elementversionid AND sev5.structureid = cciStructureID  --Only add if possibly not exist

                LEFT OUTER JOIN PROPERTYVERSION p3 on e.elementid = p3.domainelementid and p3.classid = sRefAttrClassID --Extent
                LEFT OUTER JOIN CONCEPTPROPERTYVERSION cp3 on p3.propertyid = cp3.conceptpropertyid
                LEFT OUTER JOIN TEXTPROPERTYVERSION t3 on cp3.rangeelementid = t3.domainelementid AND t3.classid = attrCodeClassID
                LEFT OUTER JOIN ELEMENTVERSION ev6 on t3.textpropertyid = ev6.elementversionid
                LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev6 on ev6.elementversionid = sev6.elementversionid AND sev6.structureid = cciStructureID  --Only add if possibly not exist
                WHERE e.classid = validationClassID
                AND sev.structureid = cciStructureID
                AND sev1.structureid = cciStructureID
                AND sev2.structureid = cciStructureID
                AND sev3.structureid = cciStructureID;

            RETURN rootNodeData_cursor;
        exception
            when others then
                dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

    end getCCIValidationRules;
    */


    /**************************************************************************************************************************************
    * NAME:          deleteCCI
    * DESCRIPTION:   Deletes all traces of CCI from the database.
    **************************************************************************************************************************************/
    PROCEDURE deleteCCI IS

    BEGIN

        DELETE FROM Z_ICD_TEMP;

        --Insert any ElementID that is related to CCI
        INSERT INTO Z_ICD_TEMP (A, B, F)
        SELECT ev.elementid, ev.elementversionid, 'A'
        FROM ELEMENTVERSION ev
        JOIN CLASS c on ev.classid = c.classid
        WHERE c.baseclassificationname = 'CCI';


        --Delete all ElementVersion that is related to ICD.
        --Causes a cascade delete across most tables (*PROPERTYVERSION)
        DELETE FROM ELEMENTVERSION ev
        WHERE ev.elementversionid IN (
            select b
            from Z_ICD_TEMP
            where f = 'A'
        );

--        FORALL r in (SELECT b FROM Z_ICD_TEMP where F = 'A')
--            DELETE FROM ELEMENTVERSION ev
--            WHERE ev.elementversionid = r.b;

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid in (SELECT a FROM Z_ICD_TEMP where F = 'A');

--        FORALL r in (SELECT a FROM Z_ICD_TEMP where F = 'A')
--            DELETE FROM ELEMENT e
--            WHERE e.elementid = r.b;

        DELETE FROM Z_ICD_TEMP;
        COMMIT;
--        rollback;


    END deleteCCI;


    /**************************************************************************************************************************************
    * NAME:          deleteCCIByYear
    * DESCRIPTION:   Deletes all traces of CCI from the database.
    *                Prints a summary table listing row counts of all tables afterwards
    **************************************************************************************************************************************/
    PROCEDURE deleteCCIByYear(version_code varchar2) is
        sID number;
    BEGIN
        sID := getCCIStructureByYear(version_code);

        DELETE FROM Z_ICD_TEMP;
        COMMIT;

        --Insert any ElementID that belong to the CCI Structure year
        INSERT INTO Z_ICD_TEMP (A, F)
        select ev.elementid, 'A'
        from ELEMENTVERSION ev
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
        where se.structureid = sID;

        --Most of the ELEMENTVERSION records are deleted, but not the base classsifications
        --Expecting multiple records, so you need to store the Element IDs before you can delete them
        --because you need to delete the ELEMENTVERSION first
        INSERT INTO Z_ICD_TEMP (A, F)
        select ev.elementid, 'B'
        from ELEMENTVERSION ev
        where ev.elementversionid = sID;

        --Delete all ElementVersion that is related to CCI.
        --Causes a cascade delete across most tables (*PROPERTYVERSION)
        DELETE FROM ELEMENTVERSION ev
        WHERE ev.elementversionid IN (
            select se.elementversionid
            from ELEMENTVERSION ev
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            where se.structureid = sID

            union all

            select ev.elementversionid
            from ELEMENTVERSION ev
            where ev.elementversionid = sID
        );

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid in (
            SELECT A FROM Z_ICD_TEMP
            WHERE F IN ('A', 'B')
            AND A NOT IN (SELECT DISTINCT ELEMENTID FROM ELEMENTVERSION));

        commit;

        DELETE FROM Z_ICD_TEMP;
        COMMIT;

    END deleteCCIByYear;

    /**************************************************************************************************************************************
    * NAME:          table_Stats
    * DESCRIPTION:   Prints a summary table listing row counts of all tables afterwards
    **************************************************************************************************************************************/
    PROCEDURE table_Stats(sName varchar2 DEFAULT NULL) is
        v_count integer;
        schemaName varchar2(10) :=  sys_context('USERENV', 'CURRENT_SCHEMA');
    BEGIN

        IF (sName is not null) THEN
            schemaName := sName;
        END IF;

        dbms_output.put_line(rpad('Table Name' || schemaName, 30, ' ') || '    ' || 'Count');
        dbms_output.put_line(rpad('=', 45, '='));

        for r in (select table_name, owner from all_tables where owner = schemaName)
        loop
            execute immediate 'select count(*) from ' || r.table_name
            into v_count;
            dbms_output.put_line(rpad(r.table_name, 30, ' ') || '    ' || v_count);
        end loop;

    END table_Stats;


    /**************************************************************************************************************************************
    * NAME:          gather_Schema_Stats
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE gather_Schema_Stats(sName varchar2 DEFAULT NULL) is
        schemaName varchar2(10) :=  sys_context('USERENV', 'CURRENT_SCHEMA');
    BEGIN

        IF (sName is not null) THEN
            schemaName := sName;
        END IF;

        DBMS_STATS.gather_Schema_Stats(ownname => schemaName);

    END gather_Schema_Stats;

end CIMS_CCI;
/
