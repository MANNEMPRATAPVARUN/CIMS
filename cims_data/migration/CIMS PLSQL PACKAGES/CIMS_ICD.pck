create or replace package CIMS_ICD is
    TYPE ref_cursor IS REF CURSOR;

    icd_classification_code varchar2(20) := 'ICD-10-CA';

    FUNCTION getICD10CAClassID(tblName varchar2, cName varchar2) return number;
    FUNCTION getICD10CAStructureIDByYear(VERSION_CODE varchar2) RETURN NUMBER;
    FUNCTION getICD10CARoot(P_VERSION_CODE IN VARCHAR2) RETURN NUMBER;
    --FUNCTION getICD10CAChapterInfo(chapterElementID number, version_code varchar2, lang varchar2) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getICD10CAChapterInfo(chapterCode varchar2, version_code varchar2, lang varchar2) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getICD10CANodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN varchar2, lang_code IN varchar2) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getICD10CARootNode(version_code IN varchar2, lang_code IN varchar2) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getDomainValueElementID(daText varchar2, domainValueClassID number, structureVersionID number) return number;
    FUNCTION getICD10CAValidationDifference(validation_id_fr number, validation_id_to number) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getICD10CAChapterValidation(chapterCode varchar2, version_code varchar2, facilityType varchar2) RETURN CIMS_ICD.ref_cursor;
    FUNCTION retrieveNodeElementIDbyCode(version_Code varchar2, cat_code varchar2) RETURN NUMBER;

    --Format Code functions
    PROCEDURE UpdateCode;
    PROCEDURE RevertCode;
    FUNCTION formatXREFCode(code varchar2) return varchar2;
    FUNCTION formatXREFClob(p_Source IN CLOB) RETURN CLOB;
    PROCEDURE UpdateCodeInClob;

    --Misc functions
    PROCEDURE deleteicd;
    PROCEDURE deleteICDByYear(version_code varchar2);
    PROCEDURE table_Stats(sName varchar2 DEFAULT NULL);
    PROCEDURE GATHER_SCHEMA_STATS(sName varchar2 DEFAULT NULL);
    FUNCTION checkRunStatus return varchar2;
    FUNCTION clobfromblob(p_blob blob) return clob;

    --Index Functions
    --FUNCTION getICD10CAIndex(indexElementID number, version_code number) RETURN CIMS_ICD.ref_cursor;
    FUNCTION getICD10CAIndex(indexType varchar2, lang varchar2, version_code varchar2) RETURN CIMS_ICD.ref_cursor;

    --Possible use
    FUNCTION retrieveChapterIDbyCode(version_Code varchar2, cat_code varchar2) RETURN NUMBER;
    FUNCTION retrieveCodeNestingLevel(version_Code varchar2, cat_code varchar2) RETURN NUMBER;


    FUNCTION getChildNodes(chapterElementID number, version_code varchar2) RETURN NUMBER;

    --FUNCTION howardTest return CIMS_ICD.ref_cursor;
    --FUNCTION howardTest1(cid number, eid number, contextId number) RETURN VARCHAR2;
    FUNCTION getPropertyValue(cid number, evid number) RETURN VARCHAR2;
    FUNCTION getFriendlyName(cid number, evid number) RETURN VARCHAR2;

    procedure determineColumnsByTableName(tname varchar2, tvalueColName OUT varchar2, tevid OUT varchar2);

end CIMS_ICD;
/
create or replace package body CIMS_ICD is


    /**************************************************************************************************************************************
    * NAME:          formatXREFClob
    * DESCRIPTION:   Specific function just for formating a XREF code
    *
    *   SELECT h.*,
    *       REGEXP_REPLACE(h.htmltext, '([Rr][Ee][Ff][Ii][Dd]\s*=\s*")([A-Z0-9]*)(")',
    *           'refid="' || CIMS_ICD.formatXREFCode('\2') || '"')
    *   FROM HTMLPROPERTYVERSION h
    *   WHERE h.htmlpropertyid = 44275
    *
    *   Ideally use regular expression, but functions not allowed on backreference
    **************************************************************************************************************************************/
    FUNCTION formatXREFClob(p_Source IN CLOB) RETURN CLOB IS
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
            v_String := formatXREFCode(SUBSTR(p_Source, position + 1, position1 - position - 1));

            v_Result := v_Result || SUBSTR(p_Source, startPosition, position - startPosition + 1);
            v_Result := v_Result || v_string;
            startPosition  := position1;
        END LOOP;

        RETURN v_Result;

    exception
        when others then
            raise_application_error(-20011, 'Error! formatXREFClob: ' || substr(sqlerrm, 1, 512));

    END formatXREFClob;

    /**************************************************************************************************************************************
    * NAME:          formatXREFCode
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION formatXREFCode(code varchar2) return varchar2 is
        updatedCode VARCHAR2(30);

    BEGIN
        --dbms_output.put_line('Code is:[' || code || ']');

        IF ( (INSTR(code, '/') = 0) AND (INSTR(code, '.') = 0) ) THEN
            IF LENGTH(nvl(code, 'X')) <= 3 OR code LIKE '%-%' THEN
                updatedCode := code;
            ELSE
                --Letter
                IF LENGTH(TRIM(TRANSLATE(SUBSTR(nvl(code, 'X'), 1, 1), ' +-.0123456789', ' '))) is NOT null THEN
                    updatedCode := SUBSTR(code,1, 3) || '.' || SUBSTR(code,4);
                ELSE
                --Number
                    updatedCode := SUBSTR(code, 1, LENGTH(code) - 1) || '/' || SUBSTR(code,LENGTH(code), 1);
                END IF;
            END IF;
        ELSE
            --Already contains the converted code
            updatedCode := code;
        END IF;



        RETURN TRIM(updatedCode);

    exception
        when others then
            raise_application_error(-20011, 'Error! formatXREFCode: ' || substr(sqlerrm, 1, 512));

    end formatXREFCode;


    /**************************************************************************************************************************************
    * NAME:          UpdateCodeInClob
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE UpdateCodeInClob is
        cursor c is
            SELECT *
            FROM XMLPROPERTYVERSION x
            JOIN CLASS c on x.classid = c.classid
            WHERE UPPER(x.xmltext) like '%XREF%'
            AND c.baseclassificationname = 'ICD-10-CA';

        cursor c1 is
            SELECT *
            FROM HTMLPROPERTYVERSION h
            JOIN CLASS c on h.classid = c.classid
            WHERE UPPER(h.htmltext) like '%XREF%'
            AND c.baseclassificationname = 'ICD-10-CA';

        rec_cc c%rowtype;
        rec_cc1 c1%rowtype;
        tid number;
        oldClob CLOB;

    BEGIN

        dbms_output.put_line('Updating the code inside the ICD-10-CA Clob');

        dbms_output.put_line('Starting on the XMLPropertyVersion table');
        for rec_cc in c loop
            tid := TRIM(rec_cc.xmlpropertyid);
            oldClob := TRIM(rec_cc.xmltext);
            --dbms_output.put_line('XML ID:[' || tid || ']');
            UPDATE XMLPROPERTYVERSION x
            SET x.xmltext = formatXREFClob(x.xmltext)
            WHERE x.xmlpropertyid = tid;
        end loop;

        dbms_output.put_line('Starting on the HTMLPropertyVersion table');
        for rec_cc1 in c1 loop
            tid := TRIM(rec_cc1.htmlpropertyid);
            oldClob := TRIM(rec_cc1.htmltext);
            --dbms_output.put_line('HTML ID:[' || tid || ']');
            UPDATE HTMLPROPERTYVERSION h
            SET h.htmltext = formatXREFClob(h.htmltext)
            WHERE h.htmlpropertyid = tid;
        end loop;

        dbms_output.put_line('End');

        commit;
        --rollback;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error UpdateCodeInClob!: ' || substr(sqlerrm, 1, 512));

    end UpdateCodeInClob;


    /**************************************************************************************************************************************
    * NAME:          RevertCode
    * DESCRIPTION:   Reverts the code to its original state.  Looks for traces of '/' or '.' and removes them.
    **************************************************************************************************************************************/
    PROCEDURE RevertCode is
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
            AND c.baseclassificationname = 'ICD-10-CA'
            AND (t.text LIKE '%/%' OR t.text LIKE '%.%');

        rec_cc c%rowtype;
        tid number;
        oldText VARCHAR2(30);
        newText VARCHAR2(30);

    BEGIN

         for rec_cc in c loop
             tid := TRIM(rec_cc.textpropertyid);
             oldText := TRIM(rec_cc.text);
             newText := TRIM(rec_cc.revertedtext);

             UPDATE TEXTPROPERTYVERSION t
             SET t.text = newText
             WHERE t.textpropertyid = tid;

         end loop;

         commit;

    END RevertCode;

    /**************************************************************************************************************************************
    * NAME:          UpdateCode
    * DESCRIPTION:   If ICD10 code (starts with a letter) apply format as follows: One dot after three digits
    *                If ICD10CA morphology code (starts with a number) apply format: Always one slash ("/") before the last digit
    **************************************************************************************************************************************/
    PROCEDURE UpdateCode is
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
            AND c.baseclassificationname = 'ICD-10-CA'
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

    BEGIN
         for rec_cc in c loop
             tid := TRIM(rec_cc.textpropertyid);
             oldText := TRIM(rec_cc.text);
             newText := TRIM(rec_cc.newtext);

             --Check the oldText.  If it is already updated (if this was function was already run), do not update again
             IF ( (INSTR(oldText, '/') = 0) AND (INSTR(oldText, '.') = 0) ) THEN
                 UPDATE TEXTPROPERTYVERSION t
                 SET t.text = newText
                 WHERE t.textpropertyid = tid;
             END IF;

         end loop;

         for rec_cc1 in c1 loop
             tid := TRIM(rec_cc1.textpropertyid);
             oldText := TRIM(rec_cc1.text);
             newText := TRIM(rec_cc1.newtext);

             --Check the oldText.  If it is already updated (if this was function was already run), do not update again
             IF ( (INSTR(oldText, '/') = 0) AND (INSTR(oldText, '.') = 0) ) THEN
                 UPDATE TEXTPROPERTYVERSION t
                 SET t.text = newText
                 WHERE t.textpropertyid = tid;
             END IF;

         end loop;

         commit;
    end UpdateCode;




    /**************************************************************************************************************************************
    * NAME:          checkRunStatus
    * DESCRIPTION:   Checks the log table to see if it has completed successfully.
    *                If it has not, then it is assumed that it is either currently running or has not completed successfully.
    *                It will do a second check to see if the run date is less than 30 minutes from the current time.
    *                Returns true if we will allow the migration script to run
    **************************************************************************************************************************************/
    function checkRunStatus return varchar2 is
        notRunning number;
    begin
        --Allow to run if table is empty
        select count(*)
        into notRunning
        from log;

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
    * NAME:          getICD10CAValidationDifference
    * DESCRIPTION:   Give me the diffence between two validation rules
    **************************************************************************************************************************************/
    FUNCTION getICD10CAValidationDifference(validation_id_fr number, validation_id_to number)
        RETURN CIMS_ICD.ref_cursor
    AS
        rootNodeData_cursor CIMS_ICD.ref_cursor;
    BEGIN

        OPEN rootNodeData_cursor FOR
            select
                   v.icd_validation_id,
                   v1.icd_validation_id,
                   DECODE(v.icd_validation_id, v1.icd_validation_id, null, v1.icd_validation_id) ValidationID,
                   DECODE(v.classification_type_code, v1.classification_type_code, null, v1.classification_type_code) PS,
                   DECODE(v.sex_validation_code, v1.sex_validation_code, null, v1.sex_validation_code) gender,
                   DECODE(v.mr_diag, v1.mr_diag, null, v1.mr_diag) MRDx,
                   DECODE(v.diag_type_1_flag, v1.diag_type_1_flag, null, v1.diag_type_1_flag) DxT1,
                   DECODE(v.diag_type_2_flag, v1.diag_type_2_flag, null, v1.diag_type_2_flag) DxT2,
                   DECODE(v.diag_type_code, v1.diag_type_code, null, v1.diag_type_code) DxType,
                   DECODE(v.newborn_flag, v1.newborn_flag, null, v1.newborn_flag) NewBorn,
                   DECODE(v.age_min, v1.age_min, null, v1.age_min) AgeMin,
                   DECODE(v.age_max, v1.age_max, null, v1.age_max) AgeMax
            from icd.icd_validation v,
            icd.icd_validation v1
            where v.icd_validation_id = validation_id_fr --From
            and v1.icd_validation_id = validation_id_to; --To

        RETURN rootNodeData_cursor;

    EXCEPTION
        when others then
            RETURN rootNodeData_cursor;

    end getICD10CAValidationDifference;


    /**************************************************************************************************************************************
    * NAME:          getICD10CAClassID
    * DESCRIPTION:   Returns the Class ID for a given class name
    **************************************************************************************************************************************/
    FUNCTION getICD10CAClassID(tblName varchar2, cName varchar2)
        RETURN NUMBER
    IS
        classID number;
    BEGIN
        SELECT c.CLASSID
        INTO classID
        FROM CLASS c
        WHERE UPPER(TRIM(c.TABLENAME)) = UPPER(TRIM(tblName))
        AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName))
        AND UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM(icd_classification_code));

        return classID;

    exception
        when others then
            raise_application_error(-20011, 'Error! getIC10CAClassID: ' || cName || ' not found!');
    end getICD10CAClassID;


    /**************************************************************************************************************************************
    * NAME:          getDomainValueElementID
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/
    FUNCTION getDomainValueElementID(daText varchar2, domainValueClassID number, structureVersionID number)
        RETURN NUMBER
    IS
        domainID number;
        propertyClassID number;

    BEGIN

        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');

        SELECT t.domainelementid
        INTO domainID
        FROM TEXTPROPERTYVERSION t
        JOIN STRUCTUREELEMENTVERSION sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
        JOIN ELEMENT e on t.domainelementid = e.elementid AND e.classid = domainValueClassID
        WHERE TRIM(t.text) = TRIM(daText)
        AND t.classid = propertyClassID;

        return domainID;
    end getDomainValueElementID;


    /**************************************************************************************************************************************
    * NAME:          retrieveNodeElementIDbyCode
    * DESCRIPTION:   Retrieve the Node Element ID by the Category Code
    **************************************************************************************************************************************/
    FUNCTION retrieveNodeElementIDbyCode(version_Code varchar2, cat_code varchar2)
        RETURN NUMBER
    IS
        eID number;
        codeClassID number;
        structureVersionID number;
    BEGIN
        codeClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        structureVersionID := CIMS_ICD.getICD10CAStructureIDByYear(version_Code);

        select p.domainelementid
        into eID
        from element e
        join elementversion ev on e.elementid = ev.elementid
        join structureelementversion sev on ev.elementversionid = sev.elementversionid
        join propertyversion p on ev.elementversionid = p.propertyid
        join textpropertyversion tp on p.propertyid = tp.textpropertyid
        where e.classid = codeClassID
        and sev.structureid = structureVersionID
        and tp.text = cat_code;

        return eID;

    exception
        when TOO_MANY_ROWS then
            return -9997;
        when NO_DATA_FOUND then
            return -9998;
        when others then
            return -9999;

    end retrieveNodeElementIDbyCode;


    /**************************************************************************************************************************************
    * NAME:          retrieveChapterIDbyCode
    * DESCRIPTION:   Retrieve the Chapter Element ID by passing any code
    **************************************************************************************************************************************/
    FUNCTION retrieveChapterIDbyCode(version_Code varchar2, cat_code varchar2)
        RETURN NUMBER
    IS
        eID number := 0;
        cID number := 0;

        codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        chapterClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
        structureVersionID number := CIMS_ICD.getICD10CAStructureIDByYear(version_Code);
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
    BEGIN

        select t.domainelementid, e.classid
        into eID, cID
        from textpropertyversion t
        join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
        join element e on t.domainelementid = e.elementid
        where t.text = cat_code
        and t.classid = codeClassID;

        while (cID != chapterClassID) LOOP
            select cpv.rangeelementid, e.classid
            into eID, cID
            FROM CONCEPTPROPERTYVERSION cpv
            join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
            join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
            join element e on t.domainelementid = e.elementid
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID;

--            if (eID is null) then
--                cID := chapterClassID;
--            end if;
        END LOOP;

        return eID;

    exception
        when others then
            return null;

    end retrieveChapterIDbyCode;


    /**************************************************************************************************************************************
    * NAME:          retrieveCodeNestingLevel
    * DESCRIPTION:   Retrieve the code nesting level.  Chapters are nesting level 1
    **************************************************************************************************************************************/
    FUNCTION retrieveCodeNestingLevel(version_Code varchar2, cat_code varchar2)
        RETURN NUMBER
    IS
        eID number := 0;
        cID number := 0;
        elementClassID number := 0;

        nestingLevel number := 1;

        codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        structureVersionID number := CIMS_ICD.getICD10CAStructureIDByYear(version_Code);
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
    BEGIN

        select t.domainelementid, e.classid
        into eID, elementClassID
        from textpropertyversion t
        join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
        join element e on t.domainelementid = e.elementid
        where t.text = cat_code
        and t.classid = codeClassID;

        dbms_output.put_line('This element class is ' || elementClassID);
        cID := elementClassID;

        while (cID = elementClassID) LOOP
            select cpv.rangeelementid, e.classid
            into eID, cID
            FROM CONCEPTPROPERTYVERSION cpv
            join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
            join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
            join element e on t.domainelementid = e.elementid
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID;

            dbms_output.put_line('I found  ' || cID);

            if (cID = elementClassID) then
                nestingLevel := nestingLevel + 1;
            end if;

            dbms_output.put_line('We are at nesting level  ' || nestingLevel);

        END LOOP;

        return nestingLevel;

    exception
        when others then
            return null;

    end retrieveCodeNestingLevel;


    /**************************************************************************************************************************************
    * NAME:          getICD10CAStructureIDByYear
    * DESCRIPTION:   Return the structure ID for a given year
    **************************************************************************************************************************************/
    FUNCTION getICD10CAStructureIDByYear(VERSION_CODE varchar2)
        RETURN NUMBER
    IS
        STRUCTUREID NUMBER;
        CID NUMBER;
    BEGIN
        CID := getICD10CAClassID('BASECLASSIFICATION', 'ICD-10-CA');

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


    END getICD10CAStructureIDByYear;


    /**************************************************************************************************************************************
    * NAME:          getICD10CARoot
    * DESCRIPTION:   Returns the Classification Root Element ID for a given year
    **************************************************************************************************************************************/
    FUNCTION getICD10CARoot(P_VERSION_CODE IN VARCHAR2)
        RETURN NUMBER
    IS
        VIEWERROOTELEMENTID NUMBER;
        STRUCTID NUMBER;
    BEGIN

        STRUCTID := getICD10CAStructureIDByYear(P_VERSION_CODE);

        SELECT E.ELEMENTID
        INTO VIEWERROOTELEMENTID
        FROM CLASS C
        JOIN ELEMENT E ON C.CLASSID = E.CLASSID
        JOIN ELEMENTVERSION EV ON E.ELEMENTID = EV.ELEMENTID AND EV.STATUS = 'ACTIVE'
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = STRUCTID
        WHERE UPPER(TRIM(C.TABLENAME)) = 'CONCEPTVERSION' AND UPPER(TRIM(C.CLASSNAME)) = 'CLASSIFICATIONROOT'
        AND UPPER(TRIM(C.BASECLASSIFICATIONNAME)) = 'ICD-10-CA';

        RETURN VIEWERROOTELEMENTID;
    END getICD10CARoot;

    /**************************************************************************************************************************************
    * NAME:          getICD10CARootNode
    * DESCRIPTION:   Returns the Classification Root Node and its data
    **************************************************************************************************************************************/
    FUNCTION getICD10CARootNode(version_code IN varchar2, lang_code IN varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        rootNodeData_cursor CIMS_ICD.ref_cursor;
        icdElementID number := 0;
        icdStructureID number := 0;

    BEGIN
        icdElementID := getICD10CARoot(version_code);
        icdStructureID := getICD10CAStructureIDByYear(version_code);

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
    * NAME:          getICD10CAChapterValidation
    * DESCRIPTION:   Retrieves the validation information per chapter and facility type
    **************************************************************************************************************************************/
    function getICD10CAChapterValidation(chapterCode varchar2, version_code varchar2, facilityType varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');
        chapterClassID number := getICD10CAClassID('ConceptVersion', 'Chapter');
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        domainValueCodeClassID number := getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
        valrelationshipClassID number := getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
        valMRDIAGClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationMRDiag');
        valDiag1ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType1Flag');
        valDiag2ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType2Flag');
        valDiag3ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType3Flag');
        valDiag4ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType4Flag');
        valDiag6ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType6Flag');
        valDiag9ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType9Flag');
        valDiagWClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagTypeWFlag');
        valDiagXClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagTypeXFlag');
        valDiagYClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagTypeYFlag');

        valNewBornClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationNewbornFlag');
        ageMinClassID number := getICD10CAClassID('NumericPropertyVersion', 'AgeMinimum');
        ageMaxClassID number := getICD10CAClassID('NumericPropertyVersion', 'AgeMaximum');

        validationFacilityClassID number := getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
        sexValidationIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'SexValidationIndicator');

        facilityElementID number := getDomainValueElementID(facilityType, CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType'), icdStructureID);
        rootNodeData_cursor CIMS_ICD.ref_cursor;
        chapterElementID number;

    begin

        SELECT t.domainelementid
        INTO chapterElementID
        FROM TEXTPROPERTYVERSION t
        JOIN STRUCTUREELEMENTVERSION sev on t.textpropertyid = sev.elementversionid
        JOIN ELEMENT e on t.domainelementid = e.elementid and e.classid = chapterClassID
        WHERE t.text = chapterCode
        and t.classid = codeClassID
        and sev.structureid = icdStructureID;

        OPEN rootNodeData_cursor FOR
        SELECT tmp.*,
        (
        select t.text
        from conceptpropertyversion cp
        join propertyversion p on cp.rangeelementid = p.domainelementid and cp.classid = sexValidationIndicatorClassID
        join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
        join textpropertyversion t on p.propertyid = t.textpropertyid
        join propertyversion p1 on cp.conceptpropertyid = p1.propertyid
        join ElementVersion ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE'
        join STRUCTUREELEMENTVERSION se1 on ev1.elementversionid = se1.elementversionid
        where cp.domainelementid = tmp.domainelementid
        and t.classid = domainValueCodeClassID
        and se.structureid = icdStructureID
        and se1.structureid = icdStructureID
        ) Gender,
        (
        select t.text
        from conceptpropertyversion cp
        join propertyversion p on cp.rangeelementid = p.domainelementid and cp.classid = validationFacilityClassID
        join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
        join textpropertyversion t on p.propertyid = t.textpropertyid
        join propertyversion p1 on cp.conceptpropertyid = p1.propertyid
        join ElementVersion ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE'
        join STRUCTUREELEMENTVERSION se1 on ev1.elementversionid = se1.elementversionid
        where cp.domainelementid = tmp.domainelementid
        and t.classid = domainValueCodeClassID
        and se.structureid = icdStructureID
        and se1.structureid = icdStructureID
        and cp.rangeelementid = facilityElementID
        ) FacilityType,
        (
        select n.numericvalue
        FROM NUMERICPROPERTYVERSION n
        JOIN STRUCTUREELEMENTVERSION sev2 on n.numericpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE n.domainelementid = tmp.domainelementid
        and n.classid = ageMinClassID
        ) AgeMin,
        (
        select n.numericvalue
        FROM NUMERICPROPERTYVERSION n
        JOIN STRUCTUREELEMENTVERSION sev2 on n.numericpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE n.domainelementid = tmp.domainelementid
        and n.classid = ageMaxClassID
        ) AgeMax,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valMRDIAGClassID
        ) MRDx,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag1ClassID
        ) DxT1,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag2ClassID
        ) DxT2,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag3ClassID
        ) DxT3,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag4ClassID
        ) DxT4,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag6ClassID
        ) DxT6,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiag9ClassID
        ) DxT9,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiagWClassID
        ) DxTW,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiagXClassID
        ) DxTX,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valDiagYClassID
        ) DxTY,
        (
        select b.booleanvalue
        FROM BOOLEANPROPERTYVERSION b
        JOIN STRUCTUREELEMENTVERSION sev2 on b.booleanpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
        WHERE b.domainelementid = tmp.domainelementid
        and b.classid = valNewBornClassID
        ) NewBorn
        FROM
        (
        WITH elementPropertys AS (
            SELECT e.elementid, e.notes, cp.rangeelementid ParentElementID, tp.text
            FROM CONCEPTPROPERTYVERSION cp
            join PROPERTYVERSION p on p.propertyid = cp.conceptpropertyid
            join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' --Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join ELEMENT e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
            join PROPERTYVERSION p1 on e.elementid = p1.domainelementid
            join TEXTPROPERTYVERSION tp on p1.propertyid = tp.textpropertyid
            join ELEMENTVERSION ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE' --Code EV
            join STRUCTUREELEMENTVERSION se3 on ev1.elementversionid = se3.elementversionid
            join ELEMENT e1 on ev1.elementid = e1.elementid
            WHERE e1.classid = codeClassID
            and se.structureid = icdStructureID
            and se2.structureid = icdStructureID
            and se3.structureid = icdStructureID
            )
        SELECT ep.elementid, ep.notes, ep.ParentElementID, ep.text, level + 1 as treeLevel,
        cpv.conceptpropertyid, cpv.rangeelementid, sev1.structureid, cpv.domainelementid
        FROM elementPropertys ep
        LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv on ep.elementID = cpv.rangeelementid and cpv.classid = valrelationshipClassID
            and cpv.conceptpropertyid IN (
                SELECT cpv.conceptpropertyid
                FROM CONCEPTPROPERTYVERSION cpv
                JOIN CONCEPTPROPERTYVERSION cpv1 ON cpv.domainelementid = cpv1.domainelementid
                JOIN STRUCTUREELEMENTVERSION sev on cpv.conceptpropertyid = sev.elementversionid and sev.structureid = icdStructureID
                JOIN STRUCTUREELEMENTVERSION sev1 on cpv1.conceptpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
                WHERE cpv.rangeelementid = ep.elementID
                AND cpv.classid = valrelationshipClassID
                and cpv1.rangeelementid = facilityElementID
                and cpv1.classid = validationFacilityClassID
                )
        LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev1 on sev1.elementversionid = cpv.conceptpropertyid and sev1.structureid = icdStructureID
        CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
        start with ep.elementid = chapterElementID
        ORDER SIBLINGS BY ep.text
        ) tmp;

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getICD10CAChapterValidation;


    /**************************************************************************************************************************************
    * NAME:          determineColumnsByTableName
    * DESCRIPTION:   Based on the table name, determine which columns we need which represent the 'value' for the table and
    *                which column represents the property ID
    *                TODO: CONCEPTPROPERTYVERSION??????? GRAPHICSPROPERTYVERSION --> BLOB???
    **************************************************************************************************************************************/
    procedure determineColumnsByTableName(tname varchar2, tvalueColName OUT varchar2, tevid OUT varchar2) is

    begin
        dbms_output.put_line('Table IS : ' || tname);

        if UPPER(tname) = 'TEXTPROPERTYVERSION' THEN
            tvalueColName := 'text';
            tevid := 'textpropertyid';
        ELSIF UPPER(tname) = 'XMLPROPERTYVERSION' THEN
            tvalueColName := 'xmltext';
            tevid := 'xmlpropertyid';
        ELSIF UPPER(tname) = 'HTMLPROPERTYVERSION' THEN
            tvalueColName := 'htmltext';
            tevid := 'htmlpropertyid';
        ELSIF UPPER(tname) = 'NUMERICPROPERTYVERSION' THEN
            tvalueColName := 'numericvalue';
            tevid := 'numericpropertyid';
        ELSIF UPPER(tname) = 'BOOLEANPROPERTYVERSION' THEN
            tvalueColName := 'booleanvalue';
            tevid := 'booleanpropertyid';
        ELSIF UPPER(tname) = 'CONCEPTPROPERTYVERSION' THEN
            tvalueColName := 'rangeelementid';
            tevid := 'conceptpropertyid';
        ELSIF UPPER(tname) = 'CONCEPTVERSION' THEN
            tvalueColName := 'status';
            tevid := 'conceptid';
        ELSE
            tvalueColName := 'TABLENAME (' || tname || ') UNKNOWN';
            tevid := 'TABLENAME (' || tname || ') UNKNOWN';
        END IF;

    end determineColumnsByTableName;


    /**************************************************************************************************************************************
    * NAME:          getFriendlyName
    * DESCRIPTION:   Returns the friendly name from the CLASS table.  Attempts to append the language if applicable.
    *                Ex: Short Title --> Short Title English
    *                Only works for tables that have a language column such as:
    *                textpropertyversion, xmlpropertyversion, htmlpropertyversion, graphicspropertyversion
    **************************************************************************************************************************************/
    FUNCTION getFriendlyName(cid number, evid number)
        RETURN VARCHAR2
    AS
        friendName VARCHAR2(100);
        tblName VARCHAR2(100);
        tvalueColName varchar2(100);
        tevid varchar2(100);
        langCode VARCHAR2(100);
        translatedLangCode VARCHAR2(100) := '';
        rootNodeData_cursor CIMS_ICD.ref_cursor;

    BEGIN

        select tablename, friendlyName
        into tblName, friendName
        from class
        where classid = cid;

        determineColumnsByTableName(tblName, tvalueColName, tevid);

        if UPPER(tblName) NOT IN ('TEXTPROPERTYVERSION', 'XMLPROPERTYVERSION', 'HTMLPROPERTYVERSION', 'GRAPHICSPROPERTYVERSION') THEN
            return friendName;
        end if;

        OPEN rootNodeData_cursor FOR
            'select z.languagecode from ' || tblName || ' z where z.' || tevid || ' = ' || evid ||
            ' and z.classId = ' || cid;
        LOOP
            FETCH rootNodeData_cursor
            INTO  langCode;
            EXIT WHEN rootNodeData_cursor%NOTFOUND;
        END LOOP;
        CLOSE rootNodeData_cursor;

        IF langCode = 'ENG' THEN
           translatedLangCode := 'English';
        ELSIF langCode = 'FRA' THEN
           translatedLangCode := 'French';
        END IF;

        friendName := friendName || ' ' || translatedLangCode;

        return TRIM(friendName);


    END getFriendlyName;


    /**************************************************************************************************************************************
    * NAME:
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*
    FUNCTION howardTest
        RETURN CIMS_ICD.ref_cursor
    AS
        userTitleClassID number := 9;
        rootNodeData_cursor CIMS_ICD.ref_cursor;
        tname varchar2(100);
    BEGIN

        select tablename
        into tname
        from class
        where classid = userTitleClassID;

        OPEN rootNodeData_cursor FOR


        'select *  from ' || tname || ' where classid = ' || userTitleClassID;

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end howardTest;
    */


   /**************************************************************************************************************************************
    * NAME:
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*
    FUNCTION howardTest1(cid number, eid number, contextId number)
        RETURN VARCHAR2
    AS
        tname varchar2(100);
        tvalueColName varchar2(100);
        tvalue VARCHAR2(3000);
        tevid varchar2(100);
        rootNodeData_cursor CIMS_ICD.ref_cursor;

    BEGIN

        tvalueColName := 'text';
        tevid := 'textpropertyid';
        tvalue := 'darn';
        select tablename
        into tname
        from class
        where classid = cid;

        dbms_output.put_line('Table IS : ' || tname);

        OPEN rootNodeData_cursor FOR
            'select z.' || tvalueColName || ' from ' || tname || ' z join structureelementversion sev on z.' || tevid ||
            ' = sev.elementversionid and sev.structureid = ' || contextId || ' where z.elementid = ' || eid ||
            ' and z.classId = ' || cid;


        LOOP
            FETCH rootNodeData_cursor
            INTO  tvalue;
            EXIT WHEN rootNodeData_cursor%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(tvalue);
        END LOOP;
        CLOSE rootNodeData_cursor;


        RETURN tvalue;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end howardTest1;
    */

    /**************************************************************************************************************************************
    * NAME:          getPropertyValue
    * DESCRIPTION:   Passing in the class ID and element version ID, first determine which table this class ID is related to.
    *                Then call procedure determineColumnsByTableName to determine which columns to use based on the table name.
    *                Then using the ref cursor, execute the dynamic sql.
    *                Currently only works for properties, need to investigate further if/how to expand to concepts among other things..
    **************************************************************************************************************************************/
    FUNCTION getPropertyValue(cid number, evid number)
        RETURN VARCHAR2
    AS
        tname varchar2(100);
        tvalueColName varchar2(100);
        tvalue VARCHAR2(3000) := '';
        tevid varchar2(100);
        rootNodeData_cursor CIMS_ICD.ref_cursor;

    BEGIN

        select tablename
        into tname
        from class
        where classid = cid;

        determineColumnsByTableName(tname, tvalueColName, tevid);

        OPEN rootNodeData_cursor FOR
            'select z.' || tvalueColName || ' from ' || tname || ' z where z.' || tevid || ' = ' || evid ||
            ' and z.classId = ' || cid;
        LOOP
            FETCH rootNodeData_cursor
            INTO  tvalue;
            EXIT WHEN rootNodeData_cursor%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(tvalue);
        END LOOP;
        CLOSE rootNodeData_cursor;


        RETURN tvalue;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getPropertyValue;



    /**************************************************************************************************************************************
    * NAME:          getICD10CAChapterInfo
    * DESCRIPTION:   Returns everything below a chapter.
    **************************************************************************************************************************************/
    FUNCTION getICD10CAChapterInfo(chapterElementID number, version_code varchar2, lang varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        shortTitleClassID number := getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        domainValueCodeClassID number := getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
        daggerAsteriskIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'DaggerAsteriskIndicator');

        rootNodeData_cursor CIMS_ICD.ref_cursor;
    BEGIN

        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT cp.domainelementid elementid, cp.rangeelementid ParentElementID, t.text
                FROM CONCEPTPROPERTYVERSION cp
                join STRUCTUREELEMENTVERSION se on cp.conceptpropertyid = se.elementversionid and se.structureid = icdStructureID
                join TEXTPROPERTYVERSION t on cp.domainelementid = t.domainelementid and t.classid = codeClassID and t.status = 'ACTIVE'
                join STRUCTUREELEMENTVERSION sev1 on t.textpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
                WHERE cp.status = 'ACTIVE'
                and cp.classid = narrowClassID
/*
                SELECT e.elementid, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join PROPERTYVERSION p on p.propertyid = cp.conceptpropertyid
                join ELEMENTVERSION ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' --Narrow Relationship EV
                join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
                join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
                join ELEMENT e on p.domainelementid = e.elementid
                join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
                join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
                join PROPERTYVERSION p1 on e.elementid = p1.domainelementid
                join TEXTPROPERTYVERSION tp on p1.propertyid = tp.textpropertyid
                join ELEMENTVERSION ev1 on p1.propertyid = ev1.elementversionid and ev1.status = 'ACTIVE' --Code EV
                join STRUCTUREELEMENTVERSION se3 on ev1.elementversionid = se3.elementversionid
                join ELEMENT e1 on ev1.elementid = e1.elementid
                WHERE e1.classid = codeClassID
                and se.structureid = icdStructureID
                and se2.structureid = icdStructureID
                and se3.structureid = icdStructureID
*/
                )
            SELECT ep.elementid, ep.ParentElementID, ep.text, level + 1 as treeLevel,
                (
                SELECT tp1.text
                FROM TEXTPROPERTYVERSION tp1
                join PROPERTYVERSION p1 on tp1.textpropertyid = p1.propertyid
                join ELEMENTVERSION ev1 on ev1.elementversionid = p1.propertyid and ev1.status = 'ACTIVE'
                join STRUCTUREELEMENTVERSION se on ev1.elementversionid = se.elementversionid
                join ELEMENT e1 on e1.elementid = ev1.elementid
                where p1.domainelementid = ep.elementID
                and e1.classid = shortTitleClassID
                and tp1.languagecode = lang
                and se.structureid = icdStructureID
                ) ShortTitle,
                (
                select t.text
                from conceptpropertyversion cp
                join structureelementversion sev on cp.conceptpropertyid = sev.elementversionid and sev.structureid = icdStructureID
                join textpropertyversion t on cp.rangeelementid = t.domainelementid and t.classid = domainValueCodeClassID
                join structureelementversion sev1 on t.textpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
                where cp.classid = daggerAsteriskIndicatorClassID
                and cp.status = 'ACTIVE'
                and cp.domainelementid = ep.elementID
                ) DaggerAsterisk
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
    * NAME:          getICD10CAChapterInfo
    * DESCRIPTION:   Returns everything below a chapter.
    **************************************************************************************************************************************/
    FUNCTION getICD10CAChapterInfo(chapterCode varchar2, version_code varchar2, lang varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');
        chapterClassID number := getICD10CAClassID('ConceptVersion', 'Chapter');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        chapterElementID number;
    BEGIN

        SELECT t.domainelementid
        INTO chapterElementID
        FROM TEXTPROPERTYVERSION t
        JOIN STRUCTUREELEMENTVERSION sev on t.textpropertyid = sev.elementversionid
        JOIN ELEMENT e on t.domainelementid = e.elementid and e.classid = chapterClassID
        WHERE t.text = chapterCode
        and t.classid = codeClassID
        and sev.structureid = icdStructureID;

        RETURN getICD10CAChapterInfo(chapterElementID, version_code, lang);

    EXCEPTION
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    END getICD10CAChapterInfo;


   /**************************************************************************************************************************************
    * NAME:          getChildNodes
    * DESCRIPTION:   Returns everything below a chapter.
    **************************************************************************************************************************************/
    FUNCTION getChildNodes(chapterElementID number, version_code varchar2)
        RETURN NUMBER
    AS
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        valrelationshipClassID number := getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        childValidationCount number;
    BEGIN

SELECT count(distinct rangeElementID)
into childValidationCount
FROM CONCEPTPROPERTYVERSION cpv
WHERE cpv.classid = valrelationshipClassID
and cpv.status = 'ACTIVE'
and cpv.rangeelementid IN
(
select elementid from (
            WITH elementPropertys AS (
                SELECT cp.domainelementid elementid, cp.rangeelementid ParentElementID, t.text
                FROM CONCEPTPROPERTYVERSION cp
                join STRUCTUREELEMENTVERSION se on cp.conceptpropertyid = se.elementversionid and se.structureid = icdStructureID
                join TEXTPROPERTYVERSION t on cp.domainelementid = t.domainelementid and t.classid = codeClassID and t.status = 'ACTIVE'
                join STRUCTUREELEMENTVERSION sev1 on t.textpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
                WHERE cp.status = 'ACTIVE'
                and cp.classid = narrowClassID
                )

            SELECT ep.elementid--, ep.ParentElementID, ep.text
            FROM elementPropertys ep
            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
--            start with ep.elementid = chapterElementID
            start with ep.ParentElementID = chapterElementID

            ORDER SIBLINGS BY ep.text
)
)
;

        RETURN childValidationCount;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getChildNodes;


    /**************************************************************************************************************************************
    * NAME:          getICD10CANodeData
    * DESCRIPTION:   Returns 1 level of data.  Will return everything about the node.
    *                Passing in a null node element ID will result in returning Chapters
    **************************************************************************************************************************************/
    FUNCTION getICD10CANodeData(nodeElementID IN NUMBER DEFAULT NULL, version_code IN varchar2, lang_code IN varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        rootNodeData_cursor CIMS_ICD.ref_cursor;
        eID number;
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        shortTitleClassID number := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        longTitleClassID number := getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        userTitleClassID number := getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');
        domainValueCodeClassID number := getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
        daggerAsteriskIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'DaggerAsteriskIndicator');

    BEGIN

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
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join TextpropertyVERSION tp on p1.propertyid = tp.textpropertyid and tp.languagecode = lang_code
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            and e1.classid IN ( shortTitleClassID, longTitleClassID, userTitleClassID)
            and se.structureid = icdStructureID
            and se2.structureid = icdStructureID
            and se3.structureid = icdStructureID

            union all

            select tp.text, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join TextpropertyVERSION tp on p1.propertyid = tp.textpropertyid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            and e1.classid = codeClassID
            and se.structureid = icdStructureID
            and se2.structureid = icdStructureID
            and se3.structureid = icdStructureID

            union all

            select bp.booleanvalue, p.domainelementid, e1.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
            join PropertyVERSION p1 on e.elementid = p1.domainelementid
            join BOOLEANPROPERTYVERSION bp on p1.propertyid = bp.booleanpropertyid
            join elementversion ev3 on ev3.elementversionid = p1.propertyid and ev3.status = 'ACTIVE'
            join STRUCTUREELEMENTVERSION se3 on ev3.elementversionid = se3.elementversionid
            join element e1 on ev3.elementid = e1.elementid
            where cp.rangeelementid = eID
            and se.structureid = icdStructureID
            and se2.structureid = icdStructureID
            and se3.structureid = icdStructureID

            union all

            select t.text, p.domainelementid, t.classid
            from conceptPropertyVERSION cp
            join PropertyVERSION p on cp.conceptpropertyid = p.propertyid
            join ElementVersion ev on p.propertyid = ev.elementversionid and ev.status = 'ACTIVE' -- Narrow Relationship EV
            join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
            join ELEMENT e2 on ev.elementid = e2.elementid and e2.classid = narrowClassID --Narrow Relationship Element
            join Element e on p.domainelementid = e.elementid
            join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
            join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
            join CONCEPTPROPERTYVERSION cp1 on e.elementid = cp1.domainelementid and cp1.classid = daggerAsteriskIndicatorClassID
            join TEXTPROPERTYVERSION t on cp1.rangeelementid = t.domainelementid and t.classid = domainValueCodeClassID
            where cp.rangeelementid = eID
            and se.structureid = icdStructureID
            and se2.structureid = icdStructureID

            )
        SELECT
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'ShortTitle'), text, NULL)) ShortTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'LongTitle'), text, NULL)) LongTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'UserTitle'), text, NULL)) UserTitle,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'Code'), text, NULL)) Code,
            MAX(DECODE(classid, getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator'), text, NULL)) CA_ENHANCEMENT_FLAG,
            MAX(DECODE(classid, getICD10CAClassID('TextPropertyVersion', 'DomainValueCode'), text, NULL)) DAGGER_ASTERISK,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'IncludePresentation')
            and xp.languagecode = lang_code
            and se.structureid = icdStructureID
            ) INCLUDE_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'ExcludePresentation')
            and xp.languagecode = lang_code
            and se.structureid = icdStructureID
            ) EXCLUDE_PRESENTATION,
            (
            select xp.xmltext
            from PROPERTYVERSION p1
                 join XMLPROPERTYVERSION xp on xp.xmlpropertyid = p1.propertyid
                 join ELEMENTVERSION ev on p1.propertyid = ev.elementversionid and ev.status = 'ACTIVE'
                 join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
                 join ELEMENT e on ev.elementid = e.elementid
            where p1.domainelementid = ep.domainelementid
            and e.classid = getICD10CAClassID('XMLPropertyVersion', 'CodeAlsoPresentation')
            and xp.languagecode = lang_code
            and se.structureid = icdStructureID
            ) CODE_ALSO_PRESENTATION,
            (
            select xp.xmltext
            from XMLPROPERTYVERSION xp
            join STRUCTUREELEMENTVERSION se on xp.xmlpropertyid = se.elementversionid
            where xp.domainelementid = ep.domainelementid
            and xp.classid = CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'NotePresentation')
            and xp.languagecode = lang_code
            and se.structureid = icdStructureID
            and xp.status = 'ACTIVE'
            ) NOTE_PRESENTATION,
            (
            select hp.htmltext
            from HTMLPROPERTYVERSION hp
            join STRUCTUREELEMENTVERSION se on hp.htmlpropertyid = se.elementversionid
            where hp.domainelementid = ep.domainelementid
            and hp.classid = getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation')
            and hp.languagecode = lang_code
            and se.structureid = icdStructureID
            and hp.status = 'ACTIVE'
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
    * NAME:          getICDValidationRules
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*
    function getICDValidationRules(version_code number)
        RETURN CIMS_ICD.ref_cursor
    AS
        icdStructureID number := getICD10CAStructureIDByYear(version_code);

        validationClassID number := getICD10CAClassID('ConceptVersion', 'ValidationICD');
        sexValidationIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'SexValidationIndicator');
        domainValueCodeClassID number := getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
        ageMinClassID number := getICD10CAClassID('NumericPropertyVersion', 'AgeMinimum');
        ageMaxClassID number := getICD10CAClassID('NumericPropertyVersion', 'AgeMaximum');

        validationDescClassID number := getICD10CAClassID('TextPropertyVersion', 'ValidationDescription');
        validationMRDiagClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationMRDiag');
        validationType1ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType1Flag');
        validationType2ClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationDiagType2Flag');
        validationNewBornClassID number := getICD10CAClassID('BooleanPropertyVersion', 'ValidationNewbornFlag');
        diagTypeCodeIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'DiagTypeCodeIndicator');
        typeCodeIndicatorClassID number := getICD10CAClassID('ConceptPropertyVersion', 'ClassificationTypeCodeIndicator');

        rootNodeData_cursor CIMS_CCI.ref_cursor;
        begin

            OPEN rootNodeData_cursor FOR
                SELECT e.elementid, e.notes, t.text GENDER, n.numericvalue AgeMin, n1.numericvalue AgeMax, t1.text Description,
                    b.booleanvalue MR_DIAG, b1.booleanvalue DxT1, b2.booleanvalue DxT2, t2.text DxType, b3.booleanvalue NewBorn, t3.text Primary_Secondary
                FROM ELEMENT e
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

                JOIN TEXTPROPERTYVERSION t1 on e.elementid = t1.domainelementid and t1.classid = validationDescClassID
                JOIN ELEMENTVERSION ev4 on t1.textpropertyid = ev4.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev4 on ev4.elementversionid = sev4.elementversionid

                JOIN BOOLEANPROPERTYVERSION b on e.elementid = b.domainelementid and b.classid = validationMRDiagClassID
                JOIN ELEMENTVERSION ev5 on b.booleanpropertyid = ev5.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev5 on ev5.elementversionid = sev5.elementversionid

                JOIN BOOLEANPROPERTYVERSION b1 on e.elementid = b1.domainelementid and b1.classid = validationType1ClassID
                JOIN ELEMENTVERSION ev6 on b1.booleanpropertyid = ev6.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev6 on ev6.elementversionid = sev6.elementversionid

                JOIN BOOLEANPROPERTYVERSION b2 on e.elementid = b2.domainelementid and b2.classid = validationType2ClassID
                JOIN ELEMENTVERSION ev7 on b2.booleanpropertyid = ev7.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev7 on ev7.elementversionid = sev7.elementversionid

                JOIN BOOLEANPROPERTYVERSION b3 on e.elementid = b3.domainelementid and b3.classid = validationNewBornClassID
                JOIN ELEMENTVERSION ev8 on b3.booleanpropertyid = ev8.elementversionid
                JOIN STRUCTUREELEMENTVERSION sev8 on ev8.elementversionid = sev8.elementversionid

                LEFT OUTER JOIN PROPERTYVERSION p1 on e.elementid = p1.domainelementid and p1.classid = diagTypeCodeIndicatorClassID
                LEFT OUTER JOIN CONCEPTPROPERTYVERSION cp1 on p1.propertyid = cp1.conceptpropertyid
                LEFT OUTER JOIN TEXTPROPERTYVERSION t2 on cp1.rangeelementid = t2.domainelementid AND t2.classid = domainValueCodeClassID
                LEFT OUTER JOIN ELEMENTVERSION ev9 on t2.textpropertyid = ev9.elementversionid
                LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev9 on ev9.elementversionid = sev9.elementversionid AND sev9.structureid = icdStructureID  --Only add if possibly not exist

                LEFT OUTER JOIN PROPERTYVERSION p2 on e.elementid = p2.domainelementid and p2.classid = typeCodeIndicatorClassID
                LEFT OUTER JOIN CONCEPTPROPERTYVERSION cp2 on p2.propertyid = cp2.conceptpropertyid
                LEFT OUTER JOIN TEXTPROPERTYVERSION t3 on cp2.rangeelementid = t3.domainelementid AND t3.classid = domainValueCodeClassID
                LEFT OUTER JOIN ELEMENTVERSION ev10 on t3.textpropertyid = ev10.elementversionid
                LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev10 on ev10.elementversionid = sev10.elementversionid AND sev10.structureid = icdStructureID


                WHERE e.classid = validationClassID
                AND sev.structureid = icdStructureID
                AND sev1.structureid = icdStructureID
                AND sev2.structureid = icdStructureID
                AND sev3.structureid = icdStructureID
                AND sev4.structureid = icdStructureID
                AND sev5.structureid = icdStructureID
                AND sev6.structureid = icdStructureID
                AND sev7.structureid = icdStructureID
                AND sev8.structureid = icdStructureID
                ORDER BY TO_NUMBER(e.notes);

            RETURN rootNodeData_cursor;
        exception
            when others then
                dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));

    end getICDValidationRules;
    */


    /**************************************************************************************************************************************
    * NAME:          getICD10CANeoplasmIndex
    * DESCRIPTION:   Retrieve the index based on index ID
    **************************************************************************************************************************************/
    FUNCTION getICD10CANeoplasmIndex(indexElementID number, version_code varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        iTermDescClassID number := getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        nestingLevelClassID number := getICD10CAClassID('NumericPropertyVersion', 'Level');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        codeClassID number := getICD10CAClassID('TextPropertyVersion', 'Code');

        rootNodeData_cursor CIMS_ICD.ref_cursor;

        benignRefClassID number := getICD10CAClassID('ConceptPropertyVersion', 'BenignCPV');
        inSituRefClassID number := getICD10CAClassID('ConceptPropertyVersion', 'InSituCPV');
        malPriRefClassID number := getICD10CAClassID('ConceptPropertyVersion', 'MalignantPriCPV');
        malSecRefClassID number := getICD10CAClassID('ConceptPropertyVersion', 'MalignantSecCPV');
        uncBehRefClassID number := getICD10CAClassID('ConceptPropertyVersion', 'UnknownBehaviourCPV');

    BEGIN

        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT
                    e.elementid, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join STRUCTUREELEMENTVERSION se on cp.conceptpropertyid = se.elementversionid and cp.status = 'ACTIVE' --Narrow Relationship EV
                join ELEMENT e on cp.domainelementid = e.elementid
                join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
                join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
                join TEXTPROPERTYVERSION tp on e.elementid = tp.domainelementid and tp.status = 'ACTIVE' and tp.classid = iTermDescClassID --Code EV
                join STRUCTUREELEMENTVERSION se3 on tp.textpropertyid = se3.elementversionid
                WHERE cp.classid = narrowClassID --Narrow Relationship Element
                and se.structureid = icdStructureID
                and se2.structureid = icdStructureID
                and se3.structureid = icdStructureID
                )
            SELECT ep.elementid, ep.ParentElementID, rpad('-', level - 1, '-') || ep.text INDEX_TERM_DESCRIPTION,
                tp3.text Malignant_Primary, tp4.text Malignant_Secondary, tp2.text InSitu, tp1.text Benign, tp5.text Unk_Behaviour,
                level + 1 as treeLevel, np.numericvalue LevelProperty
            FROM elementPropertys ep
            LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv1 on ep.elementID = cpv1.domainelementid and cpv1.classid = benignRefClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev1 on cpv1.conceptpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
            LEFT OUTER JOIN TEXTPROPERTYVERSION tp1 on cpv1.rangeelementid = tp1.domainelementid and tp1.classid = codeClassID

            LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv2 on ep.elementID = cpv2.domainelementid and cpv2.classid = inSituRefClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev2 on cpv2.conceptpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
            LEFT OUTER JOIN TEXTPROPERTYVERSION tp2 on cpv2.rangeelementid = tp2.domainelementid and tp2.classid = codeClassID

            LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv3 on ep.elementID = cpv3.domainelementid and cpv3.classid = malPriRefClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev3 on cpv3.conceptpropertyid = sev3.elementversionid and sev3.structureid = icdStructureID
            LEFT OUTER JOIN TEXTPROPERTYVERSION tp3 on cpv3.rangeelementid = tp3.domainelementid and tp3.classid = codeClassID

            LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv4 on ep.elementID = cpv4.domainelementid and cpv4.classid = malSecRefClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev4 on cpv4.conceptpropertyid = sev4.elementversionid and sev4.structureid = icdStructureID
            LEFT OUTER JOIN TEXTPROPERTYVERSION tp4 on cpv4.rangeelementid = tp4.domainelementid and tp4.classid = codeClassID

            LEFT OUTER JOIN CONCEPTPROPERTYVERSION cpv5 on ep.elementID = cpv5.domainelementid and cpv5.classid = uncBehRefClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev5 on cpv5.conceptpropertyid = sev5.elementversionid and sev5.structureid = icdStructureID
            LEFT OUTER JOIN TEXTPROPERTYVERSION tp5 on cpv5.rangeelementid = tp5.domainelementid and tp5.classid = codeClassID

            LEFT OUTER JOIN NUMERICPROPERTYVERSION np on np.domainelementid = ep.elementID and np.classid = nestingLevelClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev6 on np.numericpropertyid = sev6.elementversionid and sev6.structureid = icdStructureID

            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
            start with ep.ParentElementID = indexElementID
            ORDER SIBLINGS BY UPPER(ep.text);

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getICD10CANeoplasmIndex;


    /**************************************************************************************************************************************
    * NAME:          getICD10CAIndex
    * DESCRIPTION:   Retrieve the index based on index ID
    **************************************************************************************************************************************/
    FUNCTION getICD10CAIndex(indexElementID number, version_code varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        iTermDescClassID number := getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
        nestingLevelClassID number := getICD10CAClassID('NumericPropertyVersion', 'Level');
        narrowClassID number := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);

        rootNodeData_cursor CIMS_ICD.ref_cursor;
    BEGIN

        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                SELECT
                    e.elementid, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join STRUCTUREELEMENTVERSION se on cp.conceptpropertyid = se.elementversionid and cp.status = 'ACTIVE' --Narrow Relationship EV
                join ELEMENT e on cp.domainelementid = e.elementid
                join ELEMENTVERSION ev2 on ev2.elementid = e.elementid and ev2.status = 'ACTIVE' -- Actual Node EV
                join STRUCTUREELEMENTVERSION se2 on ev2.elementversionid = se2.elementversionid
                join TEXTPROPERTYVERSION tp on e.elementid = tp.domainelementid and tp.status = 'ACTIVE' and tp.classid = iTermDescClassID --Code EV
                join STRUCTUREELEMENTVERSION se3 on tp.textpropertyid = se3.elementversionid
                WHERE cp.classid = narrowClassID --Narrow Relationship Element
                and se.structureid = icdStructureID
                and se2.structureid = icdStructureID
                and se3.structureid = icdStructureID
                )
            SELECT ep.elementid, ep.ParentElementID, rpad('-', level - 1, '-') || ep.text INDEX_TERM_DESCRIPTION, level + 1 as treeLevel,
                np.numericvalue LevelProperty
            FROM elementPropertys ep
            LEFT OUTER JOIN NUMERICPROPERTYVERSION np on np.domainelementid = ep.elementID and np.classid = nestingLevelClassID
            LEFT OUTER JOIN STRUCTUREELEMENTVERSION sev on np.numericpropertyid = sev.elementversionid and sev.structureid = icdStructureID
            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
--            start with ep.elementid = indexElementID
            start with ep.ParentElementID = indexElementID
            ORDER SIBLINGS BY UPPER(ep.text);

        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getICD10CAIndex;


    /**************************************************************************************************************************************
    * NAME:          getICD10CAIndex
    * DESCRIPTION:   Retrieve the index based on index type
    *                A	Alphabetic Index to Diseases and Nature of Injury
    *                N	Table of Neoplasm Index
    *                E	External Causes of Injury Index
    *                D	Table of Drugs and Chemicals Index
    **************************************************************************************************************************************/
    FUNCTION getICD10CAIndex(indexType varchar2, lang varchar2, version_code varchar2)
        RETURN CIMS_ICD.ref_cursor
    AS
        bookIndexCodeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexCode');
        --bookIndexClassID number := getICD10CAClassID('ConceptVersion', 'BookIndex');
        bookIndexDescClassID number := getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
        icdStructureID number := getICD10CAStructureIDByYear(version_code);
        indexElementID number;

    BEGIN

        select t.domainElementID
        into indexElementID
        from textpropertyversion t
        join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = icdStructureID
        join textpropertyversion t1 on t.domainelementid = t1.domainelementid and t1.classid = bookIndexDescClassID 
        where t.classid = bookIndexCodeClassID
        and t.text = indexType
        and t.languagecode = lang;

        if (indexType = 'N') then
            RETURN getICD10CANeoplasmIndex(indexElementID, version_code);
        else
            RETURN getICD10CAIndex(indexElementID, version_code);
        end if;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


    end getICD10CAIndex;


    /**************************************************************************************************************************************
    * NAME:          deleteICD
    * DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.
    **************************************************************************************************************************************/
    PROCEDURE deleteICD is

    BEGIN

        DELETE FROM Z_ICD_TEMP;

        --Insert any ElementID that is related to CCI
        INSERT INTO Z_ICD_TEMP (A, B, F)
        SELECT ev.elementid, ev.elementversionid, 'A'
        FROM ELEMENTVERSION ev
        JOIN CLASS c on ev.classid = c.classid
        WHERE c.baseclassificationname = 'ICD-10-CA';


        --Delete all ElementVersion that is related to ICD.
        --Causes a cascade delete across most tables (*PROPERTYVERSION)
        DELETE FROM ELEMENTVERSION ev
        WHERE ev.elementversionid IN (
            select b
            from Z_ICD_TEMP
            where f = 'A'
        );

        --Now we are free to delete those Elements
        DELETE FROM ELEMENT e
        WHERE e.elementid in (SELECT a FROM Z_ICD_TEMP where F = 'A');

        DELETE FROM Z_ICD_TEMP;
        COMMIT;
--        rollback;

    end deleteICD;


    /**************************************************************************************************************************************
    * NAME:          deleteICDByYear
    * DESCRIPTION:   Deletes all traces of ICD-10-CA from the database.
    **************************************************************************************************************************************/
    PROCEDURE deleteICDByYear(version_code varchar2) is
        sID number;
    BEGIN
        sID := geticd10castructureidbyyear(version_code);

        DELETE FROM Z_ICD_TEMP;
        COMMIT;

        --Insert any ElementID that belong to the ICD Structure year
        INSERT INTO Z_ICD_TEMP (A, F)
        select ev.elementid, 'A'
        from ELEMENTVERSION ev
        join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid
        where se.structureid = sID;

        --Base classifications do not belong to the structure, so store those too
        --Expecting multiple records, so you need to store the Element IDs before you can delete them
        --because you need to delete the ELEMENTVERSION first
        INSERT INTO Z_ICD_TEMP (A, F)
        select ev.elementid, 'B'
        from ELEMENTVERSION ev
        where ev.elementversionid = sID;

        --Delete all ElementVersions and from the Base Classification
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

        --We have a list of elements that are related to the year, but they also are related to other year.  Delete if possible
        DELETE FROM ELEMENT e
        WHERE e.elementid in (
            SELECT A FROM Z_ICD_TEMP
            WHERE F IN ('A', 'B')
            AND A NOT IN (SELECT DISTINCT ELEMENTID FROM ELEMENTVERSION)
        );

        commit;

        DELETE FROM Z_ICD_TEMP;
        COMMIT;

    end deleteICDByYear;


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

        dbms_output.put_line(rpad('Table Name ' || schemaName, 30, ' ') || '    ' || 'Count');
        dbms_output.put_line(rpad('=', 45, '='));

        for r in (select table_name, owner from all_tables
                  where owner = schemaName)
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

    /**************************************************************************************************************************************
    * NAME:          clobfromblob
    * DESCRIPTION:   http://stackoverflow.com/questions/12849025/convert-blob-to-clob
    **************************************************************************************************************************************/
    function clobfromblob(p_blob blob) return clob is
        l_clob         clob;
        l_dest_offsset integer := 1;
        l_src_offsset  integer := 1;
        l_lang_context integer := dbms_lob.default_lang_ctx;
        l_warning      integer;

    begin

        if p_blob is null then
            return null;
        end if;

        dbms_lob.createTemporary(lob_loc => l_clob
            ,cache   => false);

        dbms_lob.converttoclob(dest_lob     => l_clob
            ,src_blob     => p_blob
            ,amount       => dbms_lob.lobmaxsize
            ,dest_offset  => l_dest_offsset
            ,src_offset   => l_src_offsset
            ,blob_csid    => dbms_lob.default_csid
            ,lang_context => l_lang_context
            ,warning      => l_warning);

        return l_clob;

    end;


END CIMS_ICD;
/
