CREATE OR REPLACE PACKAGE CIMS_API IS
    TYPE ref_cursor IS REF CURSOR;

    PROCEDURE reBaseChangedFromVersionId(elemId number, contextId number, cid number, lang varchar2, response_code out number);
    PROCEDURE retrieveGenAttributeRefPROC(genAttrElementId number, contextId number, cur_sys out sys_refcursor);
    PROCEDURE retrieveDiagramsPROCOrig(classificationYear number, cur_sys out sys_refcursor);
    PROCEDURE retrieveDiagramsPROC(classificationYear number, baseClassification varchar2, cur_sys out sys_refcursor);
    PROCEDURE hasConceptBeenPublished(elemId number, contextCount out number);
    PROCEDURE retrieveGenAttributes(attributeType varchar2, /*status varchar2,*/ contextId number, cur_sys out sys_refcursor);
    PROCEDURE retrieveRefAttributes(attributeType varchar2, /*status varchar2,*/ contextId number, cur_sys out sys_refcursor);
    FUNCTION isNewlyCreatedFunction(conceptElementId number, contextId number) RETURN CHAR;
    PROCEDURE retrieveComponents(sectionCode varchar2, componentRefLink varchar2, clazz varchar2, contextId number, cur_sys out sys_refcursor);
    FUNCTION hasChildrenContextSensitive(pContextId NUMBER, pConceptId NUMBER) return CHAR;    


END CIMS_API;
/
CREATE OR REPLACE PACKAGE BODY CIMS_API IS



    /**************************************************************************************************************************************
    * NAME:          reBaseChangedFromVersionId
    * DESCRIPTION:   Modifies a properties 'changedFromVersionId' to the current base context.  By doing so, it resolves the conflict
    *                which would have been detected otherwise.
    *                Response Codes:
    *                    0 Success
    *                    1 Context is not a change context
    *                    2 Context is not open
    *                    3 Property Version does not exist within the change context
    *                    4 Property Version does not exist within the base context
    *                    9 Unknown problem.  Requires debug
    **************************************************************************************************************************************/
    PROCEDURE reBaseChangedFromVersionId(elemId number, contextId number, cid number, lang varchar2, response_code out number)

    IS
        ccBaseStructureId number;
        ccContextStatus varchar(30);
        classTableName varchar(30);
        rootNodeData_cursor CIMS_ICD.ref_cursor;
        tvalueColName varchar2(100);
        tevid varchar2(100);
        langSQL varchar2(100) := '';

        propertyBaseVersionId number;
        dynamicSQL varchar2(500);
        propertyVersionId number;  -- Holds the propertys Version Id.

    BEGIN
        dbms_output.put_line('Starting reBaseChangedFromVersionId.  Passed in elemId: ' || elemId || ', contextId: ' ||
            contextId || ', cid: ' || cid || ', lang: ' || lang);
        response_code := 9;

        -- Ensure context is Open, Change Context
        select BASESTRUCTUREID, CONTEXTSTATUS
        INTO ccBaseStructureId, ccContextStatus
        from STRUCTUREVERSION
        WHERE structureid = contextId;

        if (ccBaseStructureId IS null) then
            response_code := 1;
            dbms_output.put_line('Context passed in is not a change context');
            return;
        end if;

        if (ccContextStatus != 'OPEN') then
            response_code := 2;
            dbms_output.put_line('Context passed in is not open');
            return;
        end if;

        -- Get the table name for this Class
        select c.tablename
        into classTableName
        from class c
        where c.classid = cid;

        -- Get the relevant columns for this table
        CIMS_ICD.determineColumnsByTableName(classTableName, tvalueColName, tevid);
        dbms_output.put_line('Class table name: ' || classTableName);
        dbms_output.put_line('Table Property column name: ' || tevid);

        -- Create SQL snippet for language if applicable
        if ( (lang = 'ENG') OR (lang = 'FRA') ) then
            langSQL := ' and z.languagecode = ''' || lang || '''';
            dbms_output.put_line('Language passed in.  Adding to SQL snippet: ' || langSQL);
        else
            dbms_output.put_line('No Language passed in.');
        end if;

        -- Get the current property version Id within the change context
        dynamicSQL := 'select z.' || tevid || ' from ' || classTableName || ' z' ||
            ' join STRUCTUREELEMENTVERSION sev on sev.elementversionid = z.' || tevid || ' and sev.structureid = ' || contextId ||
            ' WHERE z.classid = ' || cid ||
            ' and z.domainelementid = ' || elemId ||
            langSQL;
        dbms_output.put_line(dynamicSQL);

        OPEN rootNodeData_cursor FOR
            dynamicSQL;
        LOOP
            FETCH rootNodeData_cursor
            INTO  propertyVersionId;
            EXIT WHEN rootNodeData_cursor%NOTFOUND;
        END LOOP;
        CLOSE rootNodeData_cursor;

        dbms_output.put_line('Current property version within the change context: ' || propertyVersionId);
        if (propertyVersionId is null) then
            response_code := 3;
            dbms_output.put_line('Property Version does not exist within the change context');
            return;
        end if;

        -- Get the property version Id we need that is within the base context.
        -- We will use this value to update the ELEMENTVERSION table column 'changedFromVersionId' for the property
        dynamicSQL := 'select z.' || tevid || ' from ' || classTableName || ' z' ||
            ' join STRUCTUREELEMENTVERSION sev on sev.elementversionid = z.' || tevid || ' and sev.structureid = ' || ccBaseStructureId ||
            ' WHERE z.classid = ' || cid ||
            ' and z.domainelementid = ' || elemId ||
            langSQL;
        dbms_output.put_line(dynamicSQL);

        OPEN rootNodeData_cursor FOR
            dynamicSQL;
        LOOP
            FETCH rootNodeData_cursor
            INTO  propertyBaseVersionId;
            EXIT WHEN rootNodeData_cursor%NOTFOUND;
        END LOOP;
        CLOSE rootNodeData_cursor;

        dbms_output.put_line('Base Context property version: ' || propertyBaseVersionId);
        if (propertyBaseVersionId is null) then
            response_code := 4;
            dbms_output.put_line('Property Version does not exist within the base context');
            return;
        end if;

        -- Build SQL to update the ELEMENTVERSION table.
        -- Update the 'changedFromVersionId' to match the current base contexts version for this property
        dynamicSQL :=
            'BEGIN

            UPDATE ELEMENTVERSION
            SET CHANGEDFROMVERSIONID = ' || propertyBaseVersionId || ' ' || '
            WHERE ELEMENTVERSIONID =  ' || propertyVersionId || '; ' || '

            END;';
        dbms_output.put_line(dynamicSQL);

        execute immediate dynamicSQL;

        response_code := 0;

        dbms_output.put_line('Ending reBaseChangedFromVersionId.  Passed in elemId: ' || elemId || ', contextId: ' ||
            contextId || ', cid: ' || cid || ', lang: ' || lang);
        dbms_output.put_line('Response Code: ' || response_code);

        commit;
        --rollback;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error reBaseChangedFromVersionId!: ' || substr(sqlerrm, 1, 512));

    end reBaseChangedFromVersionId;


    /**************************************************************************************************************************************
    * NAME:          retrieveGenAttributeRefPROC
    * DESCRIPTION:   Retrieve the References for Generic Attributes
    **************************************************************************************************************************************/
    PROCEDURE retrieveGenAttributeRefPROC(genAttrElementId number, contextId number, cur_sys out sys_refcursor)

    IS
        refAttrCPVClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'ReferenceAttributeCPV');
        genAttrCPVClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'GenericAttributeCPV');
        attrDescClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        attrCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeCode');

    BEGIN
        OPEN cur_sys FOR

            SELECT CPV.Rangeelementid GenericElementID, CPV.Domainelementid AttrElementID, T1.Textpropertyid, T1.text INCONTEXTDESCENG,
                ev3.status RefStatus, t4.text refCode, t5.text refDescEng, t6.text genCode
            FROM CONCEPTPROPERTYVERSION CPV
            JOIN STRUCTUREELEMENTVERSION SEV on CPV.Conceptpropertyid = SEV.Elementversionid and SEV.Structureid = contextId
            -- In Context Description
            JOIN TEXTPROPERTYVERSION T1 on T1.Domainelementid = CPV.Domainelementid and T1.Languagecode = 'ENG' and T1.Classid = attrDescClassID
            JOIN STRUCTUREELEMENTVERSION SEV1 on T1.textpropertyid = SEV1.elementversionid and SEV1.structureid = contextId
            -- Reference Attribute
            JOIN conceptpropertyversion cpv2 on cpv.domainelementid = cpv2.domainelementid and cpv2.classid = refAttrCPVClassID
            JOIN STRUCTUREELEMENTVERSION SEV2 on cpv2.conceptpropertyid = sev2.elementversionid and sev2.structureid = contextId
            -- Reference Status
            JOIN elementversion ev3 on cpv2.rangeelementid = ev3.elementid
            JOIN STRUCTUREELEMENTVERSION sev3 on ev3.elementversionid = sev3.elementversionid and sev3.structureid = contextId
            -- Reference Attribute Code
            JOIN textpropertyversion t4 on cpv2.rangeelementid = t4.domainelementid and t4.classid = attrCodeClassID
            JOIN structureelementversion sev4 on t4.textpropertyid = sev4.elementversionid and sev4.structureid = contextId
            -- Reference Attribute English Description
            JOIN textpropertyversion t5 on cpv2.rangeelementid = t5.domainelementid and t5.languagecode = 'ENG' and t5.classid = attrDescClassID
            JOIN structureelementversion sev5 on t5.textpropertyid = sev5.elementversionid and sev5.structureid = contextId
            -- Generic Attribute Code
            JOIN textpropertyversion t6 on t6.domainelementid = cpv.rangeelementid and t6.classid = attrCodeClassID
            JOIN structureelementversion sev6 on t6.textpropertyid = sev6.elementversionid and sev6.structureid = contextId

            WHERE CPV.Rangeelementid = genAttrElementId
            AND CPV.Classid = genAttrCPVClassID;

    end retrieveGenAttributeRefPROC;


    /**************************************************************************************************************************************
    * NAME:          retrieveDiagramsPROCOrig
    * DESCRIPTION:   Retrieve Diagrams for both classifications.  Exclude the diagram itself to reduce data transfer amount
    **************************************************************************************************************************************/
    PROCEDURE retrieveDiagramsPROCOrig(classificationYear number, cur_sys out sys_refcursor)

    IS

        icdDiagramClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Diagram');
        icdDiagramDescClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramDescription');
        icdDiagramFileNameClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramFileName');

        cciDiagramClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Diagram');
        cciDiagramDescClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DiagramDescription');
        cciDiagramFileNameClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DiagramFileName');

        icdStructureID number := CIMS_ICD.getICD10CAStructureIDByYear(classificationYear);
        cciStructureID number := CIMS_CCI.getCCIStructureByYear(classificationYear);

    BEGIN

        OPEN cur_sys FOR

            select e.elementid, t1.text fileName, t2.text description, ev.status, 'ICD-10-CA' baseClassification
            from element e
            join elementversion ev on e.elementid = ev.elementid
            join structureelementversion sev on ev.elementversionid = sev.elementversionid and sev.structureid = icdStructureID
            --Filename
            LEFT OUTER JOIN textpropertyversion t1 on e.elementid = t1.domainelementid and t1.classid = icdDiagramFileNameClassID
            JOIN structureelementversion sev1 on t1.textpropertyid = sev1.elementversionid and sev1.structureid = icdStructureID
            --Description
            LEFT OUTER JOIN textpropertyversion t2 on e.elementid = t2.domainelementid and t2.classid = icdDiagramDescClassID
            JOIN structureelementversion sev2 on t2.textpropertyid = sev2.elementversionid and sev2.structureid = icdStructureID
            where e.classid = icdDiagramClassID

            union

            select e.elementid, t1.text fileName, t2.text description, ev.status, 'CCI' baseClassification
            from element e
            join elementversion ev on e.elementid = ev.elementid
            join structureelementversion sev on ev.elementversionid = sev.elementversionid and sev.structureid = cciStructureID
            --Filename
            LEFT OUTER JOIN textpropertyversion t1 on e.elementid = t1.domainelementid and t1.classid = cciDiagramFileNameClassID
            JOIN structureelementversion sev1 on t1.textpropertyid = sev1.elementversionid and sev1.structureid = cciStructureID
            --Description
            LEFT OUTER JOIN textpropertyversion t2 on e.elementid = t2.domainelementid and t2.classid = cciDiagramDescClassID
            JOIN structureelementversion sev2 on t2.textpropertyid = sev2.elementversionid and sev2.structureid = cciStructureID
            where e.classid = cciDiagramClassID;

    end retrieveDiagramsPROCOrig;


    /**************************************************************************************************************************************
    * NAME:          retrieveDiagramsPROC
    * DESCRIPTION:   Retrieve Diagrams for specified classification.  Exclude the diagram itself to reduce data transfer amount
    **************************************************************************************************************************************/
    PROCEDURE retrieveDiagramsPROC(classificationYear number, baseClassification varchar2, cur_sys out sys_refcursor)

    IS

        icdDiagramClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Diagram');
        icdDiagramDescClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramDescription');
        icdDiagramFileNameClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramFileName');

        cciDiagramClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Diagram');
        cciDiagramDescClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DiagramDescription');
        cciDiagramFileNameClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DiagramFileName');

        icdStructureID number := CIMS_ICD.getICD10CAStructureIDByYear(classificationYear);
        cciStructureID number := CIMS_CCI.getCCIStructureByYear(classificationYear);

        diagramClassID number;
        diagramDescClassID number;
        diagramFileNameClassID number;
        contextID number;


    BEGIN
        if (baseClassification = 'ICD-10-CA') THEN
            diagramClassID := icdDiagramClassID;
            diagramDescClassID := icdDiagramDescClassID;
            diagramFileNameClassID := icdDiagramFileNameClassID;
            contextID := icdStructureID;
        ELSE
            diagramClassID := cciDiagramClassID;
            diagramDescClassID := cciDiagramDescClassID;
            diagramFileNameClassID := cciDiagramFileNameClassID;
            contextID := cciStructureID;
        END IF;


        OPEN cur_sys FOR

            select e.elementid, t1.text fileName, t2.text description, ev.status--, baseClassification
            from element e
            join elementversion ev on e.elementid = ev.elementid
            join structureelementversion sev on ev.elementversionid = sev.elementversionid and sev.structureid = contextID
            --Filename
            LEFT OUTER JOIN textpropertyversion t1 on e.elementid = t1.domainelementid and t1.classid = diagramFileNameClassID
            JOIN structureelementversion sev1 on t1.textpropertyid = sev1.elementversionid and sev1.structureid = contextID
            --Description
            LEFT OUTER JOIN textpropertyversion t2 on e.elementid = t2.domainelementid and t2.classid = diagramDescClassID
            JOIN structureelementversion sev2 on t2.textpropertyid = sev2.elementversionid and sev2.structureid = contextID
            where e.classid = diagramClassID
            and ev.status in ('ACTIVE', 'DISABLED');

    end retrieveDiagramsPROC;


    /**************************************************************************************************************************************
    * NAME:          hasConceptBeenPublished
    * DESCRIPTION:   Checks to see if a concept belongs to a base context that is in a closed state
    **************************************************************************************************************************************/
    PROCEDURE hasConceptBeenPublished(elemId number, contextCount out number)

    IS

    BEGIN
        select count(*)
        into contextCount
        from structureelementversion sev
        where sev.elementid = elemId
        and sev.structureid in (select structureid from structureversion
        where basestructureid is null
        and contextstatus = 'CLOSED');

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error hasConceptBeenPublished!: ' || substr(sqlerrm, 1, 512));

    end hasConceptBeenPublished;

    /**************************************************************************************************************************************
    * NAME:          isNewlyCreatedFunction
    * DESCRIPTION:   A newly created component was created in the year being selected
    **************************************************************************************************************************************/
    FUNCTION isNewlyCreatedFunction(conceptElementId number, contextId number) RETURN CHAR

    IS
        contextCount number;
    BEGIN

        SELECT COUNT(*)
        into contextCount
        FROM STRUCTUREELEMENTVERSION SEV
        WHERE SEV.Elementid = conceptElementId
        AND SEV.Structureid < contextId;

        IF (contextCount = 0) THEN
            RETURN 'Y';
        ELSE
            RETURN 'N';
        END IF;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error isNewlyCreatedFunction!: ' || substr(sqlerrm, 1, 512));

    END isNewlyCreatedFunction;

    /**************************************************************************************************************************************
    * NAME:          retrieveGenAttributes
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE retrieveGenAttributes(attributeType varchar2, contextId number, cur_sys out sys_refcursor)

    IS
        mainClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'GenericAttribute');
        descriptionClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeCode');
        attributeTypeClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'AttributeType');
        attributeTypeIndicatorClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
        attributeTypeCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DomainValueCode');

        attributeTypeElementId number;

    BEGIN
        --Get the Element ID for the Attribute Type
        select domainelementid
        into attributeTypeElementId
        from textpropertyversion t
        join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = contextId
        join element e on t.domainelementid = e.elementid and e.classid = attributeTypeClassID
        where t.text = attributeType
        and t.classid = attributeTypeCodeClassID;

        dbms_output.put_line('Attribute Type Element ID: ' || attributeTypeElementId);

        OPEN cur_sys FOR

        SELECT e.elementid, ev.status, t1.text descEng, t2.text descFra, t3.text code, isNewlyCreatedFunction(e.elementid, contextId) AS isNewlyCreated
        FROM ELEMENT E
        JOIN ELEMENTVERSION EV on E.Elementid = EV.ELEMENTID
        JOIN STRUCTUREELEMENTVERSION SEV on EV.ELEMENTVERSIONID = SEV.ELEMENTVERSIONID and SEV.Structureid = contextId
        -- Description ENG
        LEFT OUTER JOIN TEXTPROPERTYVERSION T1 on T1.Domainelementid = E.elementid and T1.Classid = descriptionClassID and T1.Languagecode = 'ENG'
        JOIN STRUCTUREELEMENTVERSION SEV1 on T1.TEXTPROPERTYID = SEV1.ELEMENTVERSIONID and SEV1.Structureid = contextId
        -- Description FRA
        LEFT OUTER JOIN TEXTPROPERTYVERSION T2 on T2.Domainelementid = E.elementid and T2.Classid = descriptionClassID and T2.Languagecode = 'FRA'
        JOIN STRUCTUREELEMENTVERSION SEV2 on T2.TEXTPROPERTYID = SEV2.ELEMENTVERSIONID and SEV2.Structureid = contextId
        -- Code
        LEFT OUTER JOIN TEXTPROPERTYVERSION T3 on T3.Domainelementid = E.elementid and T3.Classid = codeClassID
        JOIN STRUCTUREELEMENTVERSION SEV3 on T3.TEXTPROPERTYID = SEV3.ELEMENTVERSIONID and SEV3.Structureid = contextId
        -- Filter against the Attribute Types
        JOIN CONCEPTPROPERTYVERSION CPV4 ON CPV4.DOMAINELEMENTID = e.elementid and CPV4.Rangeelementid = attributeTypeElementId
            and CPV4.Classid = attributeTypeIndicatorClassID
        JOIN STRUCTUREELEMENTVERSION SEV4 on CPV4.Conceptpropertyid = SEV4.ELEMENTVERSIONID and SEV4.Structureid = contextId

        WHERE E.Classid = mainClassID;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error retrieveGenAttributes!: ' || substr(sqlerrm, 1, 512));

    end retrieveGenAttributes;

    /**************************************************************************************************************************************
    * NAME:          retrieveRefAttributes
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE retrieveRefAttributes(attributeType varchar2, contextId number, cur_sys out sys_refcursor)

    IS
        mainClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'ReferenceAttribute');
        descriptionClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeCode');
        attributeTypeClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'AttributeType');
        attributeTypeIndicatorClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
        attributeTypeCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'DomainValueCode');
        attributeMandatoryClassID number := CIMS_CCI.getCCIClassID('BooleanPropertyVersion', 'AttributeMandatoryIndicator');

        attributeTypeElementId number;
    BEGIN
        --Get the Element ID for the Attribute Type
        select domainelementid
        into attributeTypeElementId
        from textpropertyversion t
        join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = contextId
        join element e on t.domainelementid = e.elementid and e.classid = attributeTypeClassID
        where t.text = attributeType
        and t.classid = attributeTypeCodeClassID;

        dbms_output.put_line('Attribute Type Element ID: ' || attributeTypeElementId);

        OPEN cur_sys FOR

        SELECT e.elementid, ev.status, t1.text descEng, t2.text descFra, t3.text code, B4.BOOLEANVALUE mandatory,
            isNewlyCreatedFunction(e.elementid, contextId) AS isNewlyCreated
        FROM ELEMENT E
        JOIN ELEMENTVERSION EV on E.Elementid = EV.ELEMENTID
        JOIN STRUCTUREELEMENTVERSION SEV on EV.ELEMENTVERSIONID = SEV.ELEMENTVERSIONID and SEV.Structureid = contextId
        -- Description ENG
        LEFT OUTER JOIN TEXTPROPERTYVERSION T1 on T1.Domainelementid = E.elementid and T1.Classid = descriptionClassID and T1.Languagecode = 'ENG'
        JOIN STRUCTUREELEMENTVERSION SEV1 on T1.TEXTPROPERTYID = SEV1.ELEMENTVERSIONID and SEV1.Structureid = contextId
        -- Description FRA
        LEFT OUTER JOIN TEXTPROPERTYVERSION T2 on T2.Domainelementid = E.elementid and T2.Classid = descriptionClassID and T2.Languagecode = 'FRA'
        JOIN STRUCTUREELEMENTVERSION SEV2 on T2.TEXTPROPERTYID = SEV2.ELEMENTVERSIONID and SEV2.Structureid = contextId
        -- Code
        LEFT OUTER JOIN TEXTPROPERTYVERSION T3 on T3.Domainelementid = E.elementid and T3.Classid = codeClassID
        JOIN STRUCTUREELEMENTVERSION SEV3 on T3.TEXTPROPERTYID = SEV3.ELEMENTVERSIONID and SEV3.Structureid = contextId
        -- Mandatory
        LEFT OUTER JOIN BOOLEANPROPERTYVERSION B4 on B4.Domainelementid = E.elementid and B4.Classid = attributeMandatoryClassID
        JOIN STRUCTUREELEMENTVERSION SEV4 on B4.Booleanpropertyid = SEV4.ELEMENTVERSIONID and SEV4.Structureid = contextId
        -- Filter against the Attribute Types
         JOIN CONCEPTPROPERTYVERSION CPV4 ON CPV4.DOMAINELEMENTID = e.elementid and CPV4.Rangeelementid = attributeTypeElementId
            and CPV4.Classid = attributeTypeIndicatorClassID
         JOIN STRUCTUREELEMENTVERSION SEV4 on CPV4.Conceptpropertyid = SEV4.ELEMENTVERSIONID and SEV4.Structureid = contextId

        WHERE E.Classid = mainClassID;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error retrieveRefAttributes!: ' || substr(sqlerrm, 1, 512));

    end retrieveRefAttributes;

    /**************************************************************************************************************************************
    * NAME:          retrieveComponents
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE retrieveComponents(sectionCode varchar2, componentRefLink varchar2, clazz varchar2, contextId number, cur_sys out sys_refcursor)

    IS
        mainClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', clazz);
        componentRefClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', componentRefLink);

        sectionClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
        sectionCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        sectionElementId number;

        shortTitleClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
        longTitleClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
        codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ComponentCode');
    BEGIN
        -- Determine Section Element ID
        SELECT T.Domainelementid
        INTO sectionElementId
        FROM TEXTPROPERTYVERSION T
        JOIN STRUCTUREELEMENTVERSION SEV on T.textpropertyid = SEV.elementversionid and SEV.structureid = contextId
        JOIN ELEMENT E on T.Domainelementid = E.Elementid and E.Classid = sectionClassID
        WHERE T.Text = sectionCode and T.Classid = sectionCodeClassID;


        OPEN cur_sys FOR

        SELECT e.elementid, ev.status, t1.text shortDescEng, t2.text shortDescFra, t3.text longDescEng, t4.text longDescFra,
            t5.text code, isNewlyCreatedFunction(e.elementid, contextId) AS isNewlyCreated
        FROM ELEMENT E
        JOIN ELEMENTVERSION EV on E.Elementid = EV.ELEMENTID
        JOIN STRUCTUREELEMENTVERSION SEV on EV.ELEMENTVERSIONID = SEV.ELEMENTVERSIONID and SEV.Structureid = contextId
        -- Short Description ENG
        LEFT OUTER JOIN TEXTPROPERTYVERSION T1 on T1.Domainelementid = E.elementid and T1.Classid = shortTitleClassID and T1.Languagecode = 'ENG'
        JOIN STRUCTUREELEMENTVERSION SEV1 on T1.TEXTPROPERTYID = SEV1.ELEMENTVERSIONID and SEV1.Structureid = contextId
        -- Short Description FRA
        LEFT OUTER JOIN TEXTPROPERTYVERSION T2 on T2.Domainelementid = E.elementid and T2.Classid = shortTitleClassID and T2.Languagecode = 'FRA'
        JOIN STRUCTUREELEMENTVERSION SEV2 on T2.TEXTPROPERTYID = SEV2.ELEMENTVERSIONID and SEV2.Structureid = contextId
        -- Long Description ENG
        LEFT OUTER JOIN TEXTPROPERTYVERSION T3 on T3.Domainelementid = E.elementid and T3.Classid = longTitleClassID and T3.Languagecode = 'ENG'
        JOIN STRUCTUREELEMENTVERSION SEV3 on T3.TEXTPROPERTYID = SEV3.ELEMENTVERSIONID and SEV3.Structureid = contextId
        -- Long Description FRA
        LEFT OUTER JOIN TEXTPROPERTYVERSION T4 on T4.Domainelementid = E.elementid and T4.Classid = longTitleClassID and T4.Languagecode = 'FRA'
        JOIN STRUCTUREELEMENTVERSION SEV4 on T4.TEXTPROPERTYID = SEV4.ELEMENTVERSIONID and SEV4.Structureid = contextId
        -- Code
        LEFT OUTER JOIN TEXTPROPERTYVERSION T5 on T5.Domainelementid = E.elementid and T5.Classid = codeClassID
        JOIN STRUCTUREELEMENTVERSION SEV5 on T5.TEXTPROPERTYID = SEV5.ELEMENTVERSIONID and SEV5.Structureid = contextId
        -- Filter against the Section
        JOIN CONCEPTPROPERTYVERSION CPV5 ON CPV5.DOMAINELEMENTID = e.elementid and CPV5.Rangeelementid = sectionElementId
            and CPV5.Classid = componentRefClassID
        JOIN STRUCTUREELEMENTVERSION SEV5 on CPV5.Conceptpropertyid = SEV5.ELEMENTVERSIONID and SEV5.Structureid = contextId

        WHERE E.Classid = mainClassID;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            raise_application_error(-20011, 'Error retrieveComponents!: ' || substr(sqlerrm, 1, 512));

    end retrieveComponents;


    /**************************************************************************************************************************************
    * NAME:          hasChildrenContextSensitive
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION hasChildrenContextSensitive(pContextId NUMBER, pConceptId NUMBER) return CHAR
    is
        vChangeRequestId cims.structureversion.change_request_id%TYPE;
    begin
        select sv.change_request_id into vChangeRequestId
        from cims.structureversion sv
        where sv.structureid = pContextId;
        
        if vChangeRequestId is null then
          return CIMS_UTIL.hasChildren(pContextId => pContextId, pConceptId => pConceptId, pStatus => 'ACTIVE' );
        else
          return CIMS_UTIL.hasChildren(pContextId => pContextId, pConceptId => pConceptId);
        end if;
    end;


END CIMS_API;
/
