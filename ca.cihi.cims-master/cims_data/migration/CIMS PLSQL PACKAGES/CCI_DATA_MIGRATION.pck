create or replace package CCI_DATA_MIGRATION is

    cci_classification_code varchar2(20) := 'CCI';
    f_year number := 0;
    --errString varchar(4000);

    TYPE facilityType_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    facilityType facilityType_t;

    TYPE sv_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    sv sv_t;

    TYPE sv_DESC_ENG_t IS TABLE OF VARCHAR2(50) INDEX BY VARCHAR2(30);
    svDESCEng sv_DESC_ENG_t;

    TYPE sv_DESC_FRA_t IS TABLE OF VARCHAR2(50) INDEX BY VARCHAR2(30);
    svDESCFra sv_DESC_FRA_t;

    TYPE attributeType_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    attributeType attributeType_t;

    TYPE invasiveness_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    invasiveness invasiveness_t;

    TYPE agentGroup_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    agentGroup agentGroup_t;

    TYPE attributeGeneric_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    attributeGeneric attributeGeneric_t;

    TYPE attributeReference_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    attributeReference attributeReference_t;

    TYPE tissue_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    tissue tissue_t;

    TYPE appTech_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    appTech appTech_t;

    TYPE intervention_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    intervention intervention_t;

    TYPE groupComp_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    groupComp groupComp_t;

    TYPE deviceAgent_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    deviceAgent deviceAgent_t;

    TYPE supplementType_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    supplementType supplementType_t;

    TYPE rubricValidation_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    rubricValidation rubricValidation_t;

    PROCEDURE cleanUp_LookupTables;
    PROCEDURE populate_lookUp;
    PROCEDURE cci_data_migration_cleanup(version_Code varchar2);
    PROCEDURE cci_data_migration(version_Code IN varchar2);

    --Public for Index Migration
    FUNCTION populateDomainValueLookup(p_version_code varchar2, structureVersionID number,
        textCode varchar2, textCodeLanguage varchar2 DEFAULT NULL,
        textDescription varchar2, textLabel varchar2, textDefinition varchar2,
        textDescription_fr varchar2, textLabel_fr varchar2, textDefinition_fr varchar2,
        domainValueClassID number) return number;

    --Common Procedures
    PROCEDURE buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number,
        rangeElementID number, structureVersionID number);
    PROCEDURE insertTextProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
        textProp varchar2, language_code char);
    PROCEDURE insertXMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
        xmlData clob, language_code char);
    PROCEDURE insertNumericProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              numValue number);
    procedure insertGraphicProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number, graphicData blob,
        language_code char);

    FUNCTION insertConcept(p_version_code varchar2, propertyClassID number, businessKey varchar2, structureVersionID number,
        status_code varchar2, notes varchar2 DEFAULT NULL) RETURN NUMBER;

end CCI_DATA_MIGRATION;
/
create or replace package body CCI_DATA_MIGRATION is


    /**************************************************************************************************************************************
    * NAME:          insertLog
    * DESCRIPTION:   Write to the log table
    **************************************************************************************************************************************/
    PROCEDURE insertLog(message varchar2) is
        logDate date;
        logID number := 0;
        logRunID number := 0;

        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN

        dbms_output.put_line(message);

        logID := LOG_SEQ.Nextval;
        logRunID := LOG_RUN_SEQ.CURRVAL;
        logDate := sysdate;

        insert into LOG(ID, MESSAGE, MESSAGEDATE, CLASSIFICATION, FISCAL_YEAR, RUN_ID)
        values (logID, message, logDate, cci_classification_code, f_year, logRunID);

       commit;

    end insertLog;


    /**************************************************************************************************************************************
    * NAME:          generateBusinessKey
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION generateBusinessKey(baseClassification varchar2, domainElementID number, propertyClassID number, language_code char)
        RETURN VARCHAR2
    IS
        businessKey varchar2(100) := '';
        tName varchar2(30);
        cName varchar2(50);

    begin

        SELECT tableName, className
        INTO tName, cName
        FROM CLASS
        WHERE CLASSID = propertyClassID;

        businessKey := businessKey || baseClassification;
        businessKey := businessKey || ':';
        businessKey := businessKey || domainElementID;
        businessKey := businessKey || ':';
        businessKey := businessKey || tName;
        businessKey := businessKey || ':';
        businessKey := businessKey || cName;

        if (language_code is not null) then
            businessKey := businessKey || ':';
            businessKey := businessKey || language_code;
        end if;

        return businessKey;

    end generateBusinessKey;


    /**************************************************************************************************************************************
    * NAME:          generateConceptBusinessKey
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION generateConceptBusinessKey(baseClassification varchar2, propertyClassID number, code varchar2)
        RETURN VARCHAR2
    IS
        businessKey varchar2(100) := '';
        tName varchar2(30);
        cName varchar2(50);

    begin

        SELECT tableName, className
        INTO tName, cName
        FROM CLASS
        WHERE CLASSID = propertyClassID;

        businessKey := businessKey || baseClassification;
        businessKey := businessKey || ':';
        businessKey := businessKey || tName;
        businessKey := businessKey || ':';
        businessKey := businessKey || cName;

        if (code is not null) then
            businessKey := businessKey || ':';
            businessKey := businessKey || code;
        end if;

        return businessKey;

    end generateConceptBusinessKey;


    /**************************************************************************************************************************************
    * NAME:          getCCIClassID
    * DESCRIPTION:   Convenience function to retrieve the class ID
    **************************************************************************************************************************************/
    FUNCTION getCCIClassID(tblName varchar2, cName varchar2) return number is
        classID number;
    BEGIN
        SELECT c.CLASSID
        INTO classID
        FROM CLASS c
        WHERE UPPER(TRIM(c.TABLENAME)) = UPPER(TRIM(tblName))
        AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName))
        AND UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM(cci_classification_code));

        return classID;

    exception
        when others then
            raise_application_error(-20011, 'Invalid Class search ' || cName);
    end getCCIClassID;


   /**************************************************************************************************************************************
    * NAME:          insertGraphicProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure insertGraphicProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              graphicData blob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, language_code);

    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into GRAPHICSPROPERTYVERSION (GRAPHICSPROPERTYID, LANGUAGECODE, GRAPHICSBLOBVALUE, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, SUBSTR(language_code, 0, 3), graphicData, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in insertGraphicProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertGraphicProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertGraphicProperty;


    /**************************************************************************************************************************************
    * NAME:          insertHTMLProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertHTMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, language_code);

    BEGIN
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into HTMLPROPERTYVERSION (HTMLPROPERTYID, LANGUAGECODE, HTMLTEXT, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, SUBSTR(language_code, 0, 3), xmlData, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in insertHTMLProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertHTMLProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertHTMLProperty;


    /**************************************************************************************************************************************
    * NAME:          insertXMLProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertXMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, language_code);
        cleanedXmlData clob;

    BEGIN

        cleanedXmlData := TRIM(REPLACE(xmlData,'\n',''));

        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into XMLPROPERTYVERSION (Xmlpropertyid, Languagecode, Xmltext, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, SUBSTR(language_code, 0, 3), cleanedXmlData, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in insertXMLProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertXMLProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertXMLProperty;


    /**************************************************************************************************************************************
    * NAME:          insertBooleanProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertBooleanProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              booleanProp char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, null);

    BEGIN
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into BOOLEANPROPERTYVERSION (BOOLEANPROPERTYID, BOOLEANVALUE, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, booleanProp, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);


    exception
        when others then
            insertLog('Error occured in insertBooleanProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertBooleanProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertBooleanProperty;


    /**************************************************************************************************************************************
    * NAME:          insertTextProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertTextProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              textProp varchar2, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, language_code);

    BEGIN
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into TEXTPROPERTYVERSION (TEXTPROPERTYID, LANGUAGECODE, TEXT, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, SUBSTR(language_code, 0, 3), textProp, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);


    exception
        when others then
            insertLog('Error occured in insertTextProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertTextProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertTextProperty;


    /**************************************************************************************************************************************
    * NAME:          insertNumericProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertNumericProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              numValue number) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, propertyClassID, null);

    BEGIN
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, propertyClassID, status_code, elementID);

        insert into DATAPROPERTYVERSION (Datapropertyid, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, domainElementID, elementID);

        insert into NUMERICPROPERTYVERSION (NUMERICPROPERTYID, NUMERICVALUE, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values (elementVersionID, numValue, propertyClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in insertNumericProperty procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertNumericProperty. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertNumericProperty;


    /**************************************************************************************************************************************
    * NAME:          insertConcept
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION insertConcept(p_version_code varchar2, propertyClassID number, businessKey varchar2, structureVersionID number,
            status_code varchar2, notes varchar2 DEFAULT NULL)

        RETURN NUMBER
    IS
        elementID number := ELEMENTID_SEQ.Nextval;
        elementVersionID number := ELEMENTVERSIONID_SEQ.Nextval;
        busKey varchar2(100);

    begin
        if (businessKey is null) then
            busKey := elementID;
        else
            busKey := businessKey;
        end if;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, propertyClassID, busKey, notes);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, notes, propertyClassID, null, structureVersionID);

        insert into CONCEPTVERSION (CONCEPTID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, sysdate, notes);

        RETURN elementID;

    exception
        when others then
            insertLog('Error occured in insertConcept procedure.  Business Key: ' || busKey);
            raise_application_error(-20011, 'Error occurred in insertConcept. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertConcept;


    /**************************************************************************************************************************************
    * NAME:          populateDomainValueLookup
    * DESCRIPTION:   Populates the tables necessary to perform domain value lookups.
    **************************************************************************************************************************************/
    FUNCTION populateDomainValueLookup(p_version_code varchar2, structureVersionID number,
        textCode varchar2, textCodeLanguage varchar2 DEFAULT NULL,
        textDescription varchar2, textLabel varchar2, textDefinition varchar2,
        textDescription_fr varchar2, textLabel_fr varchar2, textDefinition_fr varchar2,
        domainValueClassID number)

        RETURN NUMBER
    IS
        elementID number := 0;
        businessKey varchar2(100) := '';
        status_code varchar2(10);
        propertyClassID number := 0;
        lang_ENG varchar2(3);
        lang_FRA varchar2(3);
    BEGIN
        status_code := 'ACTIVE';

        lang_ENG := 'ENG';
        lang_FRA := 'FRA';

        businessKey := generateConceptBusinessKey(cci_classification_code, domainValueClassID, textCode);
        elementID := insertConcept(p_version_code, domainValueClassID, businessKey, structureVersionID, status_code);

        --Store Domain Value Code
        propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueCode');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, textCode, textCodeLanguage);

        --Store Domain Value Description
        if (textDescription is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDescription,''), lang_ENG);
        end if;

        --Store Domain Value Label
        if (textLabel is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueLabel');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textLabel,''), lang_ENG);
        end if;

        --Store Domain Value Description
        if (textDefinition is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueDefinition');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDefinition,''), lang_ENG);
        end if;

        --Store Domain Value Description French
        if (textDescription_fr is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDescription_fr,''), lang_FRA);
        end if;

        --Store Domain Value Label French
        if (textLabel_fr is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueLabel');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textLabel_fr,''), lang_FRA);
        end if;

        --Store Domain Value Description
        if (textDefinition_fr is not null) then
            propertyClassID := getCCIClassID('TextPropertyVersion', 'DomainValueDefinition');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDefinition_fr,''), lang_FRA);
        end if;

        RETURN elementID;

    exception
        when others then
            insertLog('Error occured in populateDomainValueLookup procedure');
            insertLog('Error inside populateDomainValueLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateDomainValueLookup. <br> Error:' || substr(sqlerrm, 1, 50));

    end populateDomainValueLookup;


    /**************************************************************************************************************************************
    * NAME:          populateSupplementTypeLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE populateSupplementTypeLookup(version_code varchar2, structureVersionID number) is

        domainValueClassID number := 0;
        dvElementID number;

    BEGIN
        domainValueClassID := getCCIClassID('ConceptVersion', 'SupplementType');
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'F', null, 'Front Matter', null, null,
            'Front Matter', null, null, domainValueClassID);
        supplementType('F') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'B', null, 'Back Matter', null, null,
            'Back Matter', null, null, domainValueClassID);
        supplementType('B') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'O', null, 'Other', null, null,
            'Other', null, null, domainValueClassID);
        supplementType('O') := dvElementID;

    exception
        when others then
            insertLog('Error occured in populateSupplementTypeLookup procedure');
            insertLog('Error inside populateSupplementTypeLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateSupplementTypeLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateSupplementTypeLookup;


    /**************************************************************************************************************************************
    * NAME:          populateFacilityTypeLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE populateFacilityTypeLookup(p_version_code varchar2, structureVersionID number) is

        ftClassID number := 0;
        dvElementID number;

    BEGIN
        ftClassID := getCCIClassID('ConceptVersion', 'FacilityType');

        dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'A', null, 'NACRS - Ambulatory Care', null, null,
            'SNISA - Soins ambulatoires', null, null, ftClassID);
        facilityType('A') := dvElementID;

        dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, '1', null, 'DAD - Acute Care', null, null,
            'BDCP - Soins de courte durée', null, null, ftClassID);
        facilityType('1') := dvElementID;

    exception
        when others then
            insertLog('Error occured in populateFacilityTypeLookup procedure');
            insertLog('Error inside populateFacilityTypeLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateFacilityTypeLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateFacilityTypeLookup;


    /**************************************************************************************************************************************
    * NAME:          populateSexValidationLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE populateSexValidationLookup(p_version_code varchar2, structureVersionID number) is

        svClassID number := 0;
        dvElementID number;

    BEGIN
        svClassID := getCCIClassID('ConceptVersion', 'SexValidation');

        dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'A', null, 'Male, Female & Other', null, null,
            'Homme, Femme & Autre', null, null, svClassID);
        sv('A') := dvElementID;
        svDESCEng('A') := 'Male, Female & Other';
        svDESCFra('A') := 'Homme, Femme & Autre';

        --dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'F', null, 'Female', null, null,
        --    'Femme', null, null, svClassID);
        --sv('F') := dvElementID;
        dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'G', null, 'Female & Other', null, null,
            'Femme & Autre', null, null, svClassID);
        sv('G') := dvElementID;
        svDESCEng('G') := 'Female & Other';
        svDESCFra('G') := 'Femme & Autre';

        sv('F') := dvElementID;
        svDESCEng('F') := 'Female & Other';
        svDESCFra('F') := 'Femme & Autre';

        --dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'M', null, 'Male', null, null,
        --    'Homme', null, null, svClassID);
        --sv('M') := dvElementID;
        dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, 'N', null, 'Male & Other', null, null,
            'Homme & Autre', null, null, svClassID);
        sv('N') := dvElementID;
        svDESCEng('N') := 'Male & Other';
        svDESCFra('N') := 'Homme & Autre';

        sv('M') := dvElementID;
        svDESCEng('M') := 'Male & Other';
        svDESCFra('M') := 'Homme & Autre';
    exception
        when others then
            insertLog('Error occured in populateSexValidationLookup procedure');
            insertLog('Error inside populateSexValidationLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateSexValidationLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateSexValidationLookup;


    /**************************************************************************************************************************************
    * NAME:          populateAttributeTypeLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE populateAttributeTypeLookup(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from CCI.ATTRIBUTE_TYPE ;

        rec_cc c%rowtype;
        attributeTypeClassID number := 0;
        attributeType_code VARCHAR2(1);
        attributeType_e_desc VARCHAR2(60);
        dvElementID number;
    BEGIN
        attributeTypeClassID := getCCIClassID('ConceptVersion', 'AttributeType');

        for rec_cc in c loop
            attributeType_code := TRIM(rec_cc.attribute_type_code);
            attributeType_e_desc := TRIM(rec_cc.attribute_type_desc);

            dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, attributeType_code, null, attributeType_e_desc,
                null, null, null, null, null, attributeTypeClassID);

            attributeType(attributeType_code) := dvElementID;

        end loop;

    exception
        when others then
            insertLog('Error occured in populateAttributeTypeLookup procedure');
            insertLog('Error inside populateAttributeTypeLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateAttributeTypeLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateAttributeTypeLookup;


    /**************************************************************************************************************************************
    * NAME:          populateInvasivenessLookup
    * DESCRIPTION:   Populates the tables necessary to perform Invasiveness lookups.
    **************************************************************************************************************************************/
    PROCEDURE populateInvasivenessLookup(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from CCI.Invasiveness_Level ;

        rec_cc c%rowtype;
        invasivenessClassID number := 0;
        il_code VARCHAR2(1);
        il_e_desc VARCHAR2(60);
        il_e_def VARCHAR2(4000);
        il_f_desc VARCHAR2(60);
        il_f_def VARCHAR2(4000);
        dvElementID number;

    BEGIN
        invasivenessClassID := getCCIClassID('ConceptVersion', 'InvasivenessLevel');

        for rec_cc in c loop
            il_code := TRIM(rec_cc.invasiveness_level_code);
            il_e_desc := TRIM(rec_cc.invasiveness_level_e_desc);
            il_e_def := TRIM(rec_cc.definition_e_desc);
            il_f_desc := TRIM(rec_cc.invasiveness_level_f_desc);
            il_f_def := TRIM(rec_cc.definition_f_desc);

            dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, il_code, null, il_e_desc, null, il_e_def,
                il_f_desc, null, il_f_def, invasivenessClassID);

            invasiveness(il_code) := dvElementID;

        end loop;

    exception
        when others then
            insertLog('Error occured in populateInvasivenessLookup procedure');
            insertLog('Error inside populateInvasivenessLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateInvasivenessLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateInvasivenessLookup;


    /**************************************************************************************************************************************
    * NAME:          populateAgentGroup
    * DESCRIPTION:   Populates the tables necessary to perform Agent Group lookups.
    **************************************************************************************************************************************/
    PROCEDURE populateAgentGroup(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from CCI.Agent_Group ;

        rec_cc c%rowtype;
        agentGroupClassID number := 0;
        agent_group_code VARCHAR2(1);
        agent_group_e_desc VARCHAR2(60);
        agent_group_f_desc VARCHAR2(60);
        dvElementID number;
    BEGIN
        agentGroupClassID := getCCIClassID('ConceptVersion', 'AgentGroup');

        for rec_cc in c loop
            agent_group_code := TRIM(rec_cc.agent_group_code);
            agent_group_e_desc := TRIM(rec_cc.agent_group_desc);
            agent_group_f_desc := TRIM(rec_cc.agent_group_f_desc);

            dvElementID := populateDomainValueLookup(p_version_code, structureVersionID, agent_group_code, null, agent_group_e_desc,
                null, null, agent_group_f_desc, null, null, agentGroupClassID);

            agentGroup(agent_group_code) := dvElementID;

        end loop;

    exception
        when others then
            insertLog('Error occured in populateAgentGroup procedure');
            insertLog('Error inside populateAgentGroup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateAgentGroup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateAgentGroup;


    /**************************************************************************************************************************************
    * NAME:          buildNarrowRelationship
    * DESCRIPTION:   Builds the parent-child relationship between two nodes in a tree
    **************************************************************************************************************************************/
    PROCEDURE buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number,
        rangeElementID number, structureVersionID number) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(cci_classification_code, domainElementID, relationshipClassID, null);

    BEGIN
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, relationshipClassID, businessKey, null );

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, relationshipClassID, null, structureVersionID);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, domainElementID, null, null, relationshipClassID, status_code, elementID);

        insert into CONCEPTPROPERTYVERSION (CONCEPTPROPERTYID, RANGEELEMENTID, INVERSECONCEPTPROPERTYID, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
        values ( elementVersionID, rangeElementID, null, relationshipClassID, status_code, domainElementID, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in buildNarrowRelationship procedure. ' || relationshipClassID);
            insertLog('Error inside buildNarrowRelationship: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildNarrowRelationship. Error:' || substr(sqlerrm, 1, 512));

    end buildNarrowRelationship;


    /**************************************************************************************************************************************
    * NAME:          insertValidationRule
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION insertValidationRule(p_version_code varchar2, cciValidationID number, structureVersionID number, domainElementID number,
        facilityTypeElementID number)
        RETURN NUMBER
    IS
        cursor c is
            select c.*
            from cci.cci_validation c
            where c.cci_validation_id = cciValidationID
            order by c.cci_validation_id;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        cci_validation_id number;
        sex_validation_code varchar(3);
        age_min number;
        age_max number;
        l_ref_CODE VARCHAR2(3);
        e_ref_CODE VARCHAR2(3);
        s_ref_CODE VARCHAR2(3);
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;

        validationDefinitionXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE validation SYSTEM "/dtd/cihi_cims_validation.dtd">'
            || '<validation classification="CCI" language="">';
        validationDefinitionXMLEnd VARCHAR2(50) := '</validation>';
        validationDefinitionXML VARCHAR2(3000);

    BEGIN
        status_code := 'ACTIVE';
        mainClassID := getCCIClassID('ConceptVersion', 'ValidationCCI');

        for rec_cc in c loop
            cci_validation_id := TRIM(rec_cc.cci_validation_id);
            sex_validation_code := TRIM(rec_cc.sex_validation_code);
            age_min := TRIM(rec_cc.age_min);
            age_max := TRIM(rec_cc.age_max);
            l_ref_CODE := TRIM(rec_cc.location_reference_code);
            e_ref_CODE := TRIM(rec_cc.extent_reference_code);
            s_ref_CODE := TRIM(rec_cc.status_reference_code);

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID,
                domainElementID || '__' || facilityTypeElementID);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code);

            -- Add XML declaration and validation element
            validationDefinitionXML := validationDefinitionXMLTop;

            -- Add Validation element Id
            validationDefinitionXML := validationDefinitionXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';

            -- Add Sex Validation Code
            validationDefinitionXML := validationDefinitionXML || '<GENDER_CODE>' || Trim(sex_validation_code) || '</GENDER_CODE>';

            -- Add Sex Validation Description ENG
            validationDefinitionXML := validationDefinitionXML || '<GENDER_DESC_ENG>' || DBMS_XMLGEN.CONVERT(svDESCEng(Trim(sex_validation_code))) || '</GENDER_DESC_ENG>';

            -- Add Sex Validation Description FRA
            validationDefinitionXML := validationDefinitionXML || '<GENDER_DESC_FRA>' || DBMS_XMLGEN.CONVERT(svDESCFra(Trim(sex_validation_code))) || '</GENDER_DESC_FRA>';

            -- Add Age Range
            validationDefinitionXML := validationDefinitionXML || '<AGE_RANGE>' || age_min || '-' || age_max || '</AGE_RANGE>';

            -- Add Status Reference
            validationDefinitionXML := validationDefinitionXML || '<STATUS_REF>' || nvl(s_ref_CODE, '') || '</STATUS_REF>';

            -- Add Location Reference
            validationDefinitionXML := validationDefinitionXML || '<LOCATION_REF>' || nvl(l_ref_CODE, '') || '</LOCATION_REF>';

            -- Add Extent Reference
            validationDefinitionXML := validationDefinitionXML || '<EXTENT_REF>' || nvl(e_ref_CODE, '') || '</EXTENT_REF>';

            -- Add XML declaration and validation element end
            validationDefinitionXML := validationDefinitionXML || validationDefinitionXMLEnd;

            -- Add newly created XML to Validation Definition property
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'ValidationDefinition');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(validationDefinitionXML, '')), '');

        end loop;

        RETURN elementID;

    EXCEPTION
        when others then
            insertLog('insertValidationRule ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertValidationRule. <br> Error:' || substr(sqlerrm, 1, 512));
    END insertValidationRule;


    /**************************************************************************************************************************************
    * NAME:          insertValidation
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE insertValidation(p_version_code varchar2, domainElementID number, parentElementID number, structureVersionID number,
              fType varchar2, cciValidationID number) is

        relationshipClassID number;
        facilityTypeElementID number;
        validationElementID number;

    BEGIN
        facilityTypeElementID := facilityType(fType);
        validationElementID := insertValidationRule(p_version_code, cciValidationID, structureVersionID, parentElementID, facilityTypeElementID);

        --Validation CCI Relationship
        relationshipClassID := getCCIClassID('ConceptPropertyVersion', 'ValidationCCICPV');
        buildNarrowRelationship(p_version_code, relationshipClassID, validationElementID, parentElementID, structureVersionID);

        --Facility Type Relationship
        relationshipClassID := getCCIClassID('ConceptPropertyVersion', 'ValidationFacility');
        buildNarrowRelationship(p_version_code, relationshipClassID, validationElementID, facilityTypeElementID, structureVersionID);

    EXCEPTION
        when others then
            insertLog('Error occured in insertValidation procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            insertLog(parentElementID || ' <-- parent element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertValidation. <br> Error:' || substr(sqlerrm, 1, 512));

    END insertValidation;


    /**************************************************************************************************************************************
    * NAME:          migrateTissueComponent
    * DESCRIPTION:   Migrates the Sections in CCI
    **************************************************************************************************************************************/
    PROCEDURE migrateTissueComponent(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select *
            from CCI.CCI_TISSUE;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        CCI_TISSUE_ID NUMBER;
        CCI_TISSUE_CODE VARCHAR2(1);
        SHORT_E_DESC VARCHAR2(255);
        SHORT_F_DESC VARCHAR2(255);
        LONG_E_DESC VARCHAR2(255);
        LONG_F_DESC VARCHAR2(255);
        SECTION_CODE VARCHAR2(1);
        STATUS_CODE VARCHAR2(10);
        v_status_code VARCHAR2(1);
        mainClassID number := 0;
        propertyClassID number := 0;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'Tissue');

        for rec_cc in c loop
            CCI_TISSUE_ID := TRIM(rec_cc.cci_tissue_id);
            CCI_TISSUE_CODE := TRIM(rec_cc.cci_tissue_code);
            SHORT_E_DESC := TRIM(rec_cc.short_e_desc);
            SHORT_F_DESC := TRIM(rec_cc.short_f_desc);
            LONG_E_DESC := TRIM(rec_cc.long_e_desc);
            LONG_F_DESC := TRIM(rec_cc.long_f_desc);
            SECTION_CODE := TRIM(rec_cc.section_code);
            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID, CCI_TISSUE_CODE || '__' || SECTION_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code, SECTION_CODE);

            tissue(CCI_TISSUE_CODE || '__' || SECTION_CODE) := elementID;

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_E_DESC, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_E_DESC, ''), 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_F_DESC,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_F_DESC,''), 'FRA');

            --Code flag
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, CCI_TISSUE_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateTissueComponent ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateTissueComponent. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateTissueComponent;


    /**************************************************************************************************************************************
    * NAME:          migrateAppTechComponent
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateAppTechComponent(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from
            CCI.cci_Approach_Technique
            WHERE TRIM(cci_approach_technique_code) IS NOT NULL;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        CCI_AT_ID NUMBER;
        CCI_AT_CODE VARCHAR2(2);
        SHORT_E_DESC VARCHAR2(255);
        SHORT_F_DESC VARCHAR2(255);
        LONG_E_DESC VARCHAR2(255);
        LONG_F_DESC VARCHAR2(255);
        SECTION_CODE VARCHAR2(1);
        STATUS_CODE VARCHAR2(10);
        v_status_code VARCHAR2(1);
        mainClassID number := 0;
        propertyClassID number := 0;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'ApproachTechnique');

        for rec_cc in c loop
            CCI_AT_ID := TRIM(rec_cc.cci_approach_technique_id);
            CCI_AT_CODE := TRIM(rec_cc.cci_approach_technique_code);
            SHORT_E_DESC := TRIM(rec_cc.short_e_desc);
            SHORT_F_DESC := TRIM(rec_cc.short_f_desc);
            LONG_E_DESC := TRIM(rec_cc.long_e_desc);
            LONG_F_DESC := TRIM(rec_cc.long_f_desc);
            SECTION_CODE := TRIM(rec_cc.section_code);
            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID, CCI_AT_CODE || '__' || SECTION_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code, SECTION_CODE);

            appTech(CCI_AT_CODE || '__' || SECTION_CODE) := elementID;

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_E_DESC, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_E_DESC, ''), 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_F_DESC,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_F_DESC,''), 'FRA');

            --Code flag
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, CCI_AT_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateAppTechComponent ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateAppTechComponent. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateAppTechComponent;


    /**************************************************************************************************************************************
    * NAME:          migrateInterventionComponent
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateInterventionComponent(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from
            CCI.cci_Intervention;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        CCI_INT_ID NUMBER;
        CCI_INT_CODE VARCHAR2(2);
        SHORT_E_DESC VARCHAR2(255);
        SHORT_F_DESC VARCHAR2(255);
        LONG_E_DESC VARCHAR2(255);
        LONG_F_DESC VARCHAR2(255);
        DEF_E_DESC VARCHAR2(4000);
        DEF_F_DESC VARCHAR2(4000);
        SECTION_CODE VARCHAR2(1);
        STATUS_CODE VARCHAR2(10);
        v_status_code VARCHAR2(1);
        mainClassID number := 0;
        propertyClassID number := 0;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'Intervention');

        for rec_cc in c loop
            CCI_INT_ID := TRIM(rec_cc.cci_intervention_id);
            CCI_INT_CODE := TRIM(rec_cc.cci_intervention_code);
            SHORT_E_DESC := TRIM(rec_cc.short_e_desc);
            SHORT_F_DESC := TRIM(rec_cc.short_f_desc);
            LONG_E_DESC := TRIM(rec_cc.long_e_desc);
            LONG_F_DESC := TRIM(rec_cc.long_f_desc);
            DEF_E_DESC := TRIM(rec_cc.definition_e_desc);
            DEF_F_DESC := TRIM(rec_cc.definition_f_desc);
            SECTION_CODE := TRIM(rec_cc.section_code);
            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID, CCI_INT_CODE || '__' || SECTION_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code, SECTION_CODE);

            intervention(CCI_INT_CODE || '__' || SECTION_CODE) := elementID;

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_E_DESC, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_E_DESC, ''), 'ENG');

            --Definition title English
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'ComponentDefinitionTitle');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(DEF_E_DESC, '')), 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_F_DESC,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_F_DESC,''), 'FRA');

            --Definition title French
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'ComponentDefinitionTitle');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(DEF_F_DESC, '')), 'FRA');

            --Code flag
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, CCI_INT_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateInterventionComponent ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateInterventionComponent. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateInterventionComponent;


    /**************************************************************************************************************************************
    * NAME:          migrateGroupComponent
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateGroupComponent(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select * from
            CCI.cci_Group
            WHERE TRIM(cci_group_code) IS NOT NULL;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        CCI_GROUP_ID NUMBER;
        CCI_GROUP_CODE VARCHAR2(2);
        SHORT_E_DESC VARCHAR2(255);
        SHORT_F_DESC VARCHAR2(255);
        LONG_E_DESC VARCHAR2(255);
        LONG_F_DESC VARCHAR2(255);
        DEF_E_DESC VARCHAR2(4000);
        DEF_F_DESC VARCHAR2(4000);
        SECTION_CODE VARCHAR2(1);
        STATUS_CODE VARCHAR2(10);
        v_status_code VARCHAR2(1);
        mainClassID number := 0;
        propertyClassID number := 0;
        updatedDefinition clob := null;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'GroupComp');

        for rec_cc in c loop
            CCI_GROUP_ID := TRIM(rec_cc.cci_group_id);
            CCI_GROUP_CODE := TRIM(rec_cc.cci_group_code);
            SHORT_E_DESC := TRIM(rec_cc.short_e_desc);
            SHORT_F_DESC := TRIM(rec_cc.short_f_desc);
            LONG_E_DESC := TRIM(rec_cc.long_e_desc);
            LONG_F_DESC := TRIM(rec_cc.long_f_desc);
            DEF_E_DESC := TRIM(rec_cc.definition_e_desc);
            DEF_F_DESC := TRIM(rec_cc.definition_f_desc);
            SECTION_CODE := TRIM(rec_cc.section_code);
            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID, CCI_GROUP_CODE || '__' || SECTION_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code, SECTION_CODE);

            groupComp(CCI_GROUP_CODE || '__' || SECTION_CODE) := elementID;

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_E_DESC, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_E_DESC, ''), 'ENG');

            --Definition title English
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'ComponentDefinitionTitle');
            IF (DEF_E_DESC is not null) THEN
                updatedDefinition := '<block><ulist><label>' || DEF_E_DESC || '</label></ulist></block>';
            ELSE
                updatedDefinition := '';
            END IF;

            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, updatedDefinition, 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_F_DESC,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_F_DESC,''), 'FRA');

            --Definition title French
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'ComponentDefinitionTitle');
            IF (DEF_F_DESC is not null) THEN
                updatedDefinition := '<block><ulist><label>' || DEF_F_DESC || '</label></ulist></block>';
            ELSE
                updatedDefinition := '';
            END IF;

            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, updatedDefinition, 'FRA');

            --Code flag
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, CCI_GROUP_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateGroupComponent ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateGroupComponent. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateGroupComponent;


    /**************************************************************************************************************************************
    * NAME:          migrateDeviceAgentComponent
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateDeviceAgentComponent(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select *
            from CCI.cci_Device_Agent
            where TRIM(cci_device_agent_code) is not null;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        CCI_DA_ID NUMBER;
        CCI_DA_CODE VARCHAR2(2);
        SHORT_E_DESC VARCHAR2(255);
        SHORT_F_DESC VARCHAR2(255);
        LONG_E_DESC VARCHAR2(255);
        LONG_F_DESC VARCHAR2(255);
        AGENT_TYPE_E_DESC VARCHAR2(255);
        AGENT_TYPE_F_DESC VARCHAR2(255);
        AGENT_E_EXAMPLE VARCHAR2(255);
        AGENT_F_EXAMPLE VARCHAR2(255);
        AGENT_ATC_CODE VARCHAR2(20);
        AGENT_GROUP_CODE VARCHAR2(1);
        SECTION_CODE VARCHAR2(1);
        STATUS_CODE VARCHAR2(10);
        v_status_code VARCHAR2(1);
        mainClassID number := 0;
        propertyClassID number := 0;
        domainValueClassID number := 0;
        domainValueElementID number := 0;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'DeviceAgent');

        for rec_cc in c loop
            CCI_DA_ID := TRIM(rec_cc.cci_device_agent_id);
            CCI_DA_CODE := TRIM(rec_cc.cci_device_agent_code);
            SHORT_E_DESC := TRIM(rec_cc.short_e_desc);
            SHORT_F_DESC := TRIM(rec_cc.short_f_desc);
            LONG_E_DESC := TRIM(rec_cc.long_e_desc);
            LONG_F_DESC := TRIM(rec_cc.long_f_desc);
            AGENT_TYPE_E_DESC := TRIM(rec_cc.agent_type_e_desc);
            AGENT_TYPE_F_DESC := TRIM(rec_cc.agent_type_f_desc);
            AGENT_E_EXAMPLE := TRIM(rec_cc.agent_e_example);
            AGENT_F_EXAMPLE := TRIM(rec_cc.agent_f_example);
            AGENT_ATC_CODE := TRIM(rec_cc.agent_atc_code);
            AGENT_GROUP_CODE := TRIM(rec_cc.agent_group_code);
            SECTION_CODE := TRIM(rec_cc.section_code);
            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID, CCI_DA_CODE || '__' || SECTION_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code, SECTION_CODE);

            deviceAgent(CCI_DA_CODE || '__' || SECTION_CODE) := elementID;

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_E_DESC, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_E_DESC, ''), 'ENG');

            --Agent Description English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AgentTypeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(AGENT_TYPE_E_DESC, ''), 'ENG');

            --Agent Example English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AgentExample');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(AGENT_E_EXAMPLE, ''), 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(SHORT_F_DESC,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(LONG_F_DESC,''), 'FRA');

            --Agent Description French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AgentTypeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(AGENT_TYPE_F_DESC, ''), 'FRA');

            --Agent Example French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AgentExample');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(AGENT_F_EXAMPLE, ''), 'FRA');

            --Agent ATC Code
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AgentATCCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(AGENT_ATC_CODE, ''), null);

            --Agent Group Code
            if (AGENT_GROUP_CODE is not null) then
                domainValueClassID := getCCIClassID('ConceptVersion', 'AgentGroup');
                domainValueElementID := agentGroup(Trim(AGENT_GROUP_CODE));
                propertyClassID := getCCIClassID('ConceptPropertyVersion', 'AgentGroupIndicator');
                buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);
            end if;

            --Code flag
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ComponentCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, CCI_DA_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateDeviceAgentComponent ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateDeviceAgentComponent. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateDeviceAgentComponent;


    /**************************************************************************************************************************************
    * NAME:          migrateAttribute
    * DESCRIPTION:   Do not migrate attributes where the generic attribute code is null.
    *                Do not migrate disabled records
    **************************************************************************************************************************************/
    PROCEDURE migrateAttribute(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select *
            from cci.attribute
            where trim(generic_attribute_code) is not null
            and status_code = 'A';

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        attr_ref_CODE VARCHAR2(3);
        attr_generic_CODE VARCHAR2(2);
        attr_type_CODE VARCHAR2(1);
        attr_E_DESC VARCHAR2(255);
        attr_F_DESC VARCHAR2(255);
        v_status_code VARCHAR2(1);
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;
        attributeRelationshipClassID number := 0;
        attributeElementID number := 0;
        domainValueClassID number := 0;
        domainValueElementID number := 0;

    BEGIN
        mainClassID := getCCIClassID('ConceptVersion', 'Attribute');

        for rec_cc in c loop
            attr_ref_CODE := TRIM(rec_cc.attribute_reference_code);
            attr_generic_CODE := TRIM(rec_cc.generic_attribute_code);
            attr_type_CODE := TRIM(rec_cc.attribute_type_code);

            attr_E_DESC := TRIM(rec_cc.attribute_e_desc);
            attr_F_DESC := TRIM(rec_cc.attribute_f_desc);

            v_status_code := TRIM(rec_cc.status_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID,
                attr_ref_CODE || '__' || attr_generic_CODE || '__' || attr_type_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code);

            --Description English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_E_DESC, ''), 'ENG');

            --Description French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_F_DESC, ''), 'FRA');

            --Notes English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeNote');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, '', 'ENG');

            --Notes French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeNote');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, '', 'FRA');

            --Attribute Type Code
            domainValueClassID := getCCIClassID('ConceptVersion', 'AttributeType');
            domainValueElementID := attributeType(Trim(attr_type_CODE));
            propertyClassID := getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
            buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);

            --Attribute Reference RelationShip
            if (attr_ref_CODE is not null) then
               attributeElementID := attributeReference(attr_ref_CODE || '__' || attr_type_CODE);
               attributeRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'ReferenceAttributeCPV');
               buildNarrowRelationship(p_version_code, attributeRelationshipClassID, elementID, attributeElementID, structureVersionID);
            end if;

            --Attribute Generic RelationShip
            if (attr_generic_CODE is not null) then
               attributeElementID := attributeGeneric(attr_generic_CODE || '__' || attr_type_CODE);
               attributeRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'GenericAttributeCPV');
               buildNarrowRelationship(p_version_code, attributeRelationshipClassID, elementID, attributeElementID, structureVersionID);
            end if;


        end loop;

    exception
        when others then
            insertLog('migrateAttribute ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateAttribute. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateAttribute;


    /**************************************************************************************************************************************
    * NAME:          migrateAttributeReference
    * DESCRIPTION:   Migrate attribute reference value codes.  Ensure that the code exists has at least one in-context generic
    *                attribute code in a active status
    **************************************************************************************************************************************/
    PROCEDURE migrateAttributeReference(p_version_code varchar2, structureVersionID number) is
        cursor c is
        select ar.*, arn.note_e_desc, arn.note_f_desc
        from cci.attribute_reference ar
        left outer join cci.attribute_reference_note arn on ar.attribute_reference_code = arn.attribute_reference_code
        left outer join
            (
            select distinct attribute_reference_code, attribute_type_code
            from cci.attribute
            where trim(generic_attribute_code) is not null
            and status_code = 'A'
            ) attr
            on ar.attribute_reference_code = attr.attribute_reference_code and ar.attribute_type_code = attr.attribute_type_code;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        attr_CODE VARCHAR2(3);
        attr_E_DESC VARCHAR2(255);
        attr_F_DESC VARCHAR2(255);
        attr_type_CODE VARCHAR2(1);
        mandatory_Flag VARCHAR2(1);
        attrNote_E_DESC VARCHAR2(4000);
        attrNote_F_DESC VARCHAR2(4000);
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;
        domainValueClassID number := 0;
        domainValueElementID number := 0;

    BEGIN
        status_code := 'ACTIVE';
        mainClassID := getCCIClassID('ConceptVersion', 'ReferenceAttribute');

        for rec_cc in c loop
            attr_CODE := TRIM(rec_cc.attribute_reference_code);
            attr_E_DESC := TRIM(rec_cc.attribute_reference_e_desc);
            attr_F_DESC := TRIM(rec_cc.attribute_reference_f_desc);
            attr_type_CODE := TRIM(rec_cc.attribute_type_code);
            mandatory_Flag := TRIM(rec_cc.mandatory_flag);
            attrNote_E_DESC := TRIM(rec_cc.note_e_desc);
            attrNote_F_DESC := TRIM(rec_cc.note_f_desc);

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID,
                attr_CODE || '__' || attr_type_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code);

            attributeReference(attr_CODE || '__' || attr_type_CODE) := elementID;

            --Description English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_E_DESC, ''), 'ENG');

            --Description French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_F_DESC, ''), 'FRA');

            --Attribute Type Code
            domainValueClassID := getCCIClassID('ConceptVersion', 'AttributeType');
            domainValueElementID := attributeType(Trim(attr_type_CODE));
            propertyClassID := getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
            buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);

            --Code
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, attr_CODE, null);

            --Mandatory Flag
            propertyClassID := getCCIClassID('BooleanPropertyVersion', 'AttributeMandatoryIndicator');
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, mandatory_Flag);

            --Note English
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'AttributeNoteDescription');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(attrNote_E_DESC, '')), 'ENG');

            --Note French
            propertyClassID := getCCIClassID('XMLPropertyVersion', 'AttributeNoteDescription');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(attrNote_F_DESC, '')), 'FRA');

        end loop;

    exception
        when others then
            insertLog('migrateAttributeReference ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateAttributeReference. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateAttributeReference;


    /**************************************************************************************************************************************
    * NAME:          migrateAttributeGeneric
    * DESCRIPTION:   Migrate Attribute Generic codes.  Ensure that they are associated with at least one reference value in an active
    *                status
    **************************************************************************************************************************************/
    PROCEDURE migrateAttributeGeneric(p_version_code varchar2, structureVersionID number) is
        cursor c is
            select ga.*
            from CCI.Generic_Attribute ga
            left outer join
                (
                select distinct Generic_attribute_code, attribute_type_code
                from cci.attribute
                where trim(generic_attribute_code) is not null
                and status_code = 'A'
                ) attr
                on ga.generic_attribute_code = attr.generic_attribute_code and ga.attribute_type_code = attr.attribute_type_code
            where TRIM(ga.generic_attribute_code) is not null;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        attr_CODE VARCHAR2(2);
        attr_E_DESC VARCHAR2(255);
        attr_F_DESC VARCHAR2(255);
        attr_type_CODE VARCHAR2(1);
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;
        domainValueClassID number := 0;
        domainValueElementID number := 0;

    BEGIN
        status_code := 'ACTIVE';
        mainClassID := getCCIClassID('ConceptVersion', 'GenericAttribute');

        for rec_cc in c loop
            attr_CODE := TRIM(rec_cc.generic_attribute_code);
            attr_E_DESC := TRIM(rec_cc.generic_attribute_e_desc);
            attr_F_DESC := TRIM(rec_cc.generic_attribute_f_desc);
            attr_type_CODE := TRIM(rec_cc.attribute_type_code);

            businessKey := generateConceptBusinessKey(cci_classification_code, mainClassID,
                attr_CODE || '__' || attr_type_CODE);
            elementID := insertConcept(p_version_code, mainClassID, businessKey, structureVersionID, status_code);

            attributeGeneric(attr_CODE || '__' || attr_type_CODE) := elementID;

            --Description English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_E_DESC, ''), 'ENG');

            --Description French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(attr_F_DESC, ''), 'FRA');

            --Attribute Type Code
            domainValueClassID := getCCIClassID('ConceptVersion', 'AttributeType');
            domainValueElementID := attributeType(Trim(attr_type_CODE));
            propertyClassID := getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
            buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);

            --Code
            propertyClassID := getCCIClassID('TextPropertyVersion', 'AttributeCode');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, attr_CODE, null);

        end loop;

    exception
        when others then
            insertLog('migrateAttributeGeneric ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateAttributeGeneric. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateAttributeGeneric;


    /**************************************************************************************************************************************
    * NAME:          migrateIncludeExclude
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateIncludeExclude(p_version_code varchar2, cci_category_ID number, domainElementID number, structureVersionID number) is

        cursor c_data is
            select *
            from cci.category_detail cd
            where cd.category_id = cci_category_ID;

        rec_cc c_data%rowtype;
        category_detail_type_code varchar2(20);
        language_code char(3);
        category_detail_data clob;
        classID number;

    BEGIN
        for rec_cc in c_data loop
            category_detail_type_code := rec_cc.category_detail_type_code;
            language_code := rec_cc.language_code;
            category_detail_data := rec_cc.category_detail_data;

            IF UPPER(TRIM(category_detail_type_code)) = 'E' THEN
                classID := getCCIClassID('XMLPropertyVersion', 'ExcludePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'I' THEN
                classID := getCCIClassID('XMLPropertyVersion', 'IncludePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'A' THEN
                classID := getCCIClassID('XMLPropertyVersion', 'CodeAlsoPresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'N' THEN
                classID := getCCIClassID('XMLPropertyVersion', 'NotePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'O' THEN
                classID := getCCIClassID('XMLPropertyVersion', 'OmitCodePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSE
                insertLog('Unknown category detail type code: ' || category_detail_type_code);
            END IF;

        end loop;

    exception
        when others then
            insertLog('Error in migrateIncludeExclude with CCI Cat ID ' || cci_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIncludeExclude. <br> Error:' || substr(sqlerrm, 1, 512));

    end migrateIncludeExclude;


    /**************************************************************************************************************************************
    * NAME:          migrateTableOutput
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE migrateTableOutput(p_version_code varchar2, cci_category_ID number, domainElementID number, structureVersionID number) is

        cursor c_data is
            select *
            from cci.category_table_output cto
            where cto.category_id = cci_category_ID;

        rec_cc c_data%rowtype;
        language_code char(3);
        category_table_output_data clob;
        classID number;

    BEGIN
        for rec_cc in c_data loop
            language_code := rec_cc.language_code;
            category_table_output_data := rec_cc.category_table_output_data;

            classID := getCCIClassID('HTMLPropertyVersion', 'TablePresentation');
            insertHTMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_table_output_data, language_code);

        end loop;

    exception
        when others then
            insertLog('Error in migrateTableOutput with CCI Cat ID ' || cci_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateTableOutput. <br> Error:' || substr(sqlerrm, 1, 512));

    end migrateTableOutput;


    /**************************************************************************************************************************************
    * NAME:          assocComponentSection
    * DESCRIPTION:   Builds the relationship between a node and CCI Component
    **************************************************************************************************************************************/
    PROCEDURE assocComponentSection(p_version_code varchar2, componentRelationshipClassID number, componentClassID number,
        rangeElementID number, sectionCode varchar2, structureVersionID number) is

        cursor c is
            select e.*, ev.elementversionid
            from ELEMENT e
            join ELEMENTVERSION ev on e.elementid = ev.elementid
            join STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = structureVersionID
            WHERE e.classid = componentClassID
            and e.notes = sectionCode;

        rec_cc c%rowtype;
        elementID number := 0;
        elementVersionID number := 0;
        componentElementID number := 0;
        status_code VARCHAR(10);
        businessKey varchar2(100) := '';

    BEGIN

        status_code := 'ACTIVE';

        for rec_cc in c loop
            elementID := ELEMENTID_SEQ.Nextval;
            elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

            componentElementID := TRIM(rec_cc.elementid);
            businessKey := generateBusinessKey(cci_classification_code, componentElementID, componentRelationshipClassID, null);

            insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
            values (elementID, componentRelationshipClassID, businessKey, null );

            insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
            values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, componentRelationshipClassID, null, structureVersionID);

            insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID, CLASSID, STATUS, ELEMENTID)
            values (elementVersionID, componentElementID, null, null, componentRelationshipClassID, status_code, elementID);

            insert into CONCEPTPROPERTYVERSION (CONCEPTPROPERTYID, RANGEELEMENTID, INVERSECONCEPTPROPERTYID, CLASSID, STATUS, DOMAINELEMENTID, ELEMENTID)
            values ( elementVersionID, rangeElementID, null, componentRelationshipClassID, status_code, componentElementID, elementID);

            insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
            values (elementVersionID, structureVersionID, elementID, null, SYSDATE, null);

        end loop;

    exception
        when others then
            insertLog('assocComponentSection ' || SQLCODE || ' ' || SQLERRM);

    end assocComponentSection;


    /**************************************************************************************************************************************
    * NAME:          migrateChildNodes
    * DESCRIPTION:   Recursive procedure to migrate child nodes
    *                Cannot combine with migrateSectionNodes, takes in different number of parameters
    **************************************************************************************************************************************/
    PROCEDURE migrateChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number, parentCategoryID number,
        sectionElementID number, sectionCode varchar2) is

        cursor c is
            select c.*, f.short_desc F_SHORT_DESC, f.long_desc F_LONG_DESC, f.user_desc F_USER_DESC,
                t.cci_tissue_code, a.cci_approach_technique_code, d.cci_device_agent_code,
                i.cci_intervention_code, g.cci_group_code,
	            cv.facility_type_code, cv.cci_validation_id, cv1.facility_type_code AFTC, cv1.cci_validation_id ACVID
            from cci.category c
            LEFT OUTER join cci.french_category_desc f on c.category_id = f.category_id
            LEFT OUTER join cci.cci_tissue t on c.cci_tissue_id = t.cci_tissue_id
            LEFT OUTER JOIN cci.cci_approach_technique a on c.cci_approach_technique_id = a.cci_approach_technique_id
            LEFT OUTER JOIN cci.cci_device_agent d on c.cci_device_agent_id = d.cci_device_agent_id
            LEFT OUTER JOIN cci.cci_intervention i on c.cci_intervention_id = i.cci_intervention_id
            LEFT OUTER JOIN cci.cci_group g on c.cci_group_id = g.cci_group_id
            LEFT OUTER JOIN cci.category_validation cv on c.category_id = cv.category_id and cv.facility_type_code = '1'
            LEFT OUTER JOIN cci.category_validation cv1 on c.category_id = cv1.category_id and cv1.facility_type_code = 'A'
            where c.parent_category_id = parentCategoryID
            and TRIM(c.clinical_classification_code) = 'CCI' || p_version_code
            order by c.category_code;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        categoryID number := 0;
        v_short_title varchar2(500);
        v_long_title varchar2(500);
        v_user_title varchar2(500);
        v_short_title_fr varchar2(500);
        v_long_title_fr varchar2(500);
        v_user_title_fr varchar2(500);
        v_code VARCHAR2(12);
        v_invasiveness VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);
        v_status_code VARCHAR(1);
        status_code VARCHAR(10);
        v_tissue_id number := 0;
        v_device_agent_id number := 0;
        v_approach_id number := 0;
        v_intervention_id number := 0;
        v_group_id number := 0;
        v_tissue_code VARCHAR2(10);
        v_device_agent_code VARCHAR2(10);
        v_approach_code VARCHAR2(10);
        v_intervention_code VARCHAR2(10);
        v_group_code VARCHAR2(10);
        categoryTypeCode VARCHAR2(10);
        nodeType VARCHAR2(10);
        nodeClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
        componentElementID number := 0;
        componentRelationshipClassID number := 0;
        domainValueElementID number := 0;

        --Validation Variables
        v_facilityType varchar2(1);
        v_facilityTypeA varchar2(1);
        v_cciValidationID number;
        v_cciValidationIDA number;

        rubricAlreadySet boolean;

    BEGIN

        for rec_cc in c loop
            categoryID := TRIM(rec_cc.category_id);
            categoryTypeCode := TRIM(rec_cc.category_type_code);
            v_short_title := TRIM(rec_cc.short_desc);
            v_long_title := TRIM(rec_cc.long_desc);
            v_user_title := TRIM(rec_cc.user_desc);
            v_short_title_fr := TRIM(rec_cc.f_short_desc);
            v_long_title_fr := TRIM(rec_cc.f_long_desc);
            v_user_title_fr := TRIM(rec_cc.f_user_desc);
            v_code := TRIM(rec_cc.category_code);
            v_invasiveness := TRIM(rec_cc.invasiveness_level_code);
            v_code_flag := TRIM(rec_cc.code_flag);
            v_render_child_flag := TRIM(rec_cc.render_children_as_table_flag);
            v_status_code := TRIM(rec_cc.status_code);
            v_tissue_id := TRIM(rec_cc.cci_tissue_id);
	        v_device_agent_id := TRIM(rec_cc.cci_device_agent_id);
            v_approach_id := TRIM(rec_cc.cci_approach_technique_id);
            v_intervention_id := TRIM(rec_cc.cci_intervention_id);
            v_group_id := TRIM(rec_cc.cci_group_id);
            v_tissue_code := TRIM(rec_cc.cci_tissue_code);
            v_device_agent_code := TRIM(rec_cc.cci_device_agent_code);
            v_approach_code := TRIM(rec_cc.cci_approach_technique_code);
            v_intervention_code := TRIM(rec_cc.cci_intervention_code);
            v_group_code := TRIM(rec_cc.cci_group_code);
            v_facilityType := TRIM(rec_cc.facility_type_code);
            v_facilityTypeA := TRIM(rec_cc.aftc);
            v_cciValidationID := TRIM(rec_cc.cci_validation_id);
            v_cciValidationIDA := TRIM(rec_cc.acvid);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            IF TRIM(categoryTypeCode) IN ( 'BL1', 'BL2', 'BL3' ) THEN
                nodeClassID := getCCIClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
                insertLog('  - Migrating Block ' || v_code);
            ELSIF TRIM(categoryTypeCode) = 'GRP' THEN
                nodeClassID := getCCIClassID('ConceptVersion', 'Group');
                nodeType := 'GROUP';
            ELSIF TRIM(categoryTypeCode) = 'RUB' THEN
                nodeClassID := getCCIClassID('ConceptVersion', 'Rubric');
                nodeType := 'RUBRIC';
            ELSIF TRIM(categoryTypeCode) = 'CODE' THEN
                nodeClassID := getCCIClassID('ConceptVersion', 'CCICode');
                nodeType := 'CCICODE';
            ELSE
                insertLog('Category type code is not right: ' || categoryTypeCode);
                raise_application_error(-20011, 'Error occurred in migrateChildNodes.');
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, nodeClassID, CIMS_CCI.formatCode(v_code));
            elementID := insertConcept(p_version_code, nodeClassID, businessKey, structureVersionID, status_code);

            INSERT INTO Z_ICD_TEMP (A, B, E, F)
            VALUES (categoryID, elementID, v_code, 'CCI');

            -- Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, categoryID, elementID, structureVersionID);

            -- Table Output
            migrateTableOutput(p_version_code, categoryID, elementID, structureVersionID);

            --Store Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title, ''), 'ENG');

            --Store Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title, ''), 'ENG');

            --Store User title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title, ''), 'ENG');

            --Store Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title_fr,''), 'FRA');

            --Store Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title_fr,''), 'FRA');

            --Store User title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title_fr,''), 'FRA');

            --Store Code value (Category Code)
            propertyClassID := getCCIClassID('TextPropertyVersion', 'Code');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code, null);

            --Store Invasiveness Level
            if (v_invasiveness is not null) then
                domainValueElementID := invasiveness(Trim(v_invasiveness));
                propertyClassID := getCCIClassID('ConceptPropertyVersion', 'InvasivenessLevelIndicator');
                buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);
            end if;

            --Tissue RelationShip
            if (v_tissue_code is not null) then
               componentElementID := tissue(v_tissue_code || '__' || sectionCode);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'TissueCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Approach Technique RelationShip
            if (v_approach_code is not null) then
               componentElementID := appTech(v_approach_code || '__' || sectionCode);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'ApproachTechniqueCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Device Agent RelationShip
            if (v_device_agent_code is not null) then
               componentElementID := deviceAgent(v_device_agent_code || '__' || sectionCode);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'DeviceAgentCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Intervention RelationShip
            if (v_intervention_code is not null) then
               componentElementID := intervention(v_intervention_code || '__' || sectionCode);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'InterventionCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Group RelationShip
            if (v_group_code is not null) then
               componentElementID := groupComp(v_group_code || '__' || sectionCode);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'GroupCompCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Validation Facility Type 1
            if (v_facilityType is not null) then

                rubricAlreadySet := rubricValidation.exists(parentElementID);
                if rubricAlreadySet = FALSE then
                    rubricValidation(parentElementID) := parentElementID;
                    --insertLog('Adding ' || parentElementID);
                    insertValidation(p_version_code, elementID, parentElementID, structureVersionID, v_facilityType, v_cciValidationID);

                    --Automatically create one for Validation Facility Type A which is identical except the facility type
                    insertValidation(p_version_code, elementID, parentElementID, structureVersionID, 'A', v_cciValidationID);
                --else
                    --insertLog('Skipping ' || parentElementID);
                end if;
            end if;

            --Build Narrow Relationship
            relationshipClassID := getCCIClassID('ConceptPropertyVersion', 'Narrower');
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, parentElementID, structureVersionID);

            --Recursively call
            migrateChildNodes(p_version_code, structureVersionID, elementID, categoryID, sectionElementID, sectionCode);

        end loop;

    exception
        when others then
            insertLog('migrateChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateChildNodes. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateSectionNodes
    * DESCRIPTION:   Migrates the Sections in CCI
    *                We do not migrate Section 04, as it was deemed to be incorrect
    **************************************************************************************************************************************/
    PROCEDURE migrateSectionNodes(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c is
            select
                c.*, f.short_desc F_SHORT_DESC, f.long_desc F_LONG_DESC, f.user_desc F_USER_DESC,
                t.cci_tissue_code, a.cci_approach_technique_code, d.cci_device_agent_code,
                i.cci_intervention_code, g.cci_group_code,
                cv.facility_type_code, cv.cci_validation_id, cv1.facility_type_code AFTC, cv1.cci_validation_id ACVID
            from cci.category c
            LEFT OUTER join cci.french_category_desc f on c.category_id = f.category_id
            LEFT OUTER join cci.cci_tissue t on c.cci_tissue_id = t.cci_tissue_id
            LEFT OUTER JOIN cci.cci_approach_technique a on c.cci_approach_technique_id = a.cci_approach_technique_id
            LEFT OUTER JOIN cci.cci_device_agent d on c.cci_device_agent_id = d.cci_device_agent_id
            LEFT OUTER JOIN cci.cci_intervention i on c.cci_intervention_id = i.cci_intervention_id
            LEFT OUTER JOIN cci.cci_group g on c.cci_group_id = g.cci_group_id
            LEFT OUTER JOIN cci.category_validation cv on c.category_id = cv.category_id and cv.facility_type_code = '1'
            LEFT OUTER JOIN cci.category_validation cv1 on c.category_id = cv1.category_id and cv1.facility_type_code = 'A'
            where c.category_type_code = 'SEC'
            and TRIM(c.category_code) != '4'
            and TRIM(c.clinical_classification_code) = 'CCI' || p_version_code
            order by c.category_code;


        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        category_ID number := 0;
        v_short_title varchar2(500);
        v_long_title varchar2(500);
        v_user_title varchar2(500);
        v_short_title_fr varchar2(500);
        v_long_title_fr varchar2(500);
        v_user_title_fr varchar2(500);
        v_code VARCHAR2(12);
        v_invasiveness VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);
        v_status_code VARCHAR2(1);
        status_code VARCHAR2(10);
        v_tissue_id number := 0;
        v_device_agent_id number := 0;
        v_approach_id number := 0;
        v_intervention_id number := 0;
        v_group_id number := 0;
        v_tissue_code VARCHAR2(10);
        v_device_agent_code VARCHAR2(10);
        v_approach_code VARCHAR2(10);
        v_intervention_code VARCHAR2(10);
        v_group_code VARCHAR2(10);

        sectionClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
        componentClassID number := 0;
        componentElementID number := 0;
        componentRelationshipClassID number := 0;
        domainValueElementID number := 0;

        --Validation Variables
        v_facilityType varchar2(1);
        v_facilityTypeA varchar2(1);
        v_cciValidationID number;
        v_cciValidationIDA number;

    BEGIN
        sectionClassID := getCCIClassID('ConceptVersion', 'Section');

        for rec_cc in c loop
            category_ID := TRIM(rec_cc.category_id);
            v_short_title := TRIM(rec_cc.short_desc);
            v_long_title := TRIM(rec_cc.long_desc);
            v_user_title := TRIM(rec_cc.user_desc);
            v_short_title_fr := TRIM(rec_cc.f_short_desc);
            v_long_title_fr := TRIM(rec_cc.f_long_desc);
            v_user_title_fr := TRIM(rec_cc.f_user_desc);
            v_code := TRIM(rec_cc.category_code);
            v_invasiveness := TRIM(rec_cc.invasiveness_level_code);
            v_code_flag := TRIM(rec_cc.code_flag);
            v_render_child_flag := TRIM(rec_cc.render_children_as_table_flag);
            v_status_code := TRIM(rec_cc.status_code);
            v_tissue_id := TRIM(rec_cc.cci_tissue_id);
	        v_device_agent_id := TRIM(rec_cc.cci_device_agent_id);
            v_approach_id := TRIM(rec_cc.cci_approach_technique_id);
            v_intervention_id := TRIM(rec_cc.cci_intervention_id);
            v_group_id := TRIM(rec_cc.cci_group_id);
            v_tissue_code := TRIM(rec_cc.cci_tissue_code);
            v_device_agent_code := TRIM(rec_cc.cci_device_agent_code);
            v_approach_code := TRIM(rec_cc.cci_approach_technique_code);
            v_intervention_code := TRIM(rec_cc.cci_intervention_code);
            v_group_code := TRIM(rec_cc.cci_group_code);
            v_facilityType := TRIM(rec_cc.facility_type_code);
            v_facilityTypeA := TRIM(rec_cc.aftc);
            v_cciValidationID := TRIM(rec_cc.cci_validation_id);
            v_cciValidationIDA := TRIM(rec_cc.acvid);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            businessKey := generateConceptBusinessKey(cci_classification_code, sectionClassID, v_code);

            insertLog('Migrating section ' || v_code);
            elementID := insertConcept(p_version_code, sectionClassID, businessKey, structureVersionID, status_code);

            INSERT INTO Z_ICD_TEMP (A, B, E, F)
            VALUES (category_ID, elementID, v_code, 'CCI');

            migrateIncludeExclude(p_version_code, category_ID, elementID, structureVersionID);

            migrateTableOutput(p_version_code, category_ID, elementID, structureVersionID);

            --Short title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title, ''), 'ENG');

            --Long title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title, ''), 'ENG');

            --User title English
            propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title, ''), 'ENG');

            --Short title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title_fr,''), 'FRA');

            --Long title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title_fr,''), 'FRA');

            --User title French
            propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title_fr,''), 'FRA');

            --Code value (Category Code)
            propertyClassID := getCCIClassID('TextPropertyVersion', 'Code');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code, null);

            --Invasiveness Level
            if (v_invasiveness is not null) then
                domainValueElementID := invasiveness(Trim(v_invasiveness));
                propertyClassID := getCCIClassID('ConceptPropertyVersion', 'InvasivenessLevelIndicator');
                buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);
            end if;

            --Associate Component and the Section
            componentClassID := getCCIClassID('ConceptPropertyVersion', 'TissueToSectionCPV');
            propertyClassID := getCCIClassID('ConceptVersion', 'Tissue');
            assocComponentSection(p_version_code, componentClassID, propertyClassID, elementID, v_code, structureVersionID);

            componentClassID := getCCIClassID('ConceptPropertyVersion', 'DeviceAgentToSectionCPV');
            propertyClassID := getCCIClassID('ConceptVersion', 'DeviceAgent');
            assocComponentSection(p_version_code, componentClassID, propertyClassID, elementID, v_code, structureVersionID);

            componentClassID := getCCIClassID('ConceptPropertyVersion', 'ApproachTechniqueToSectionCPV');
            propertyClassID := getCCIClassID('ConceptVersion', 'ApproachTechnique');
            assocComponentSection(p_version_code, componentClassID, propertyClassID, elementID, v_code, structureVersionID);

            componentClassID := getCCIClassID('ConceptPropertyVersion', 'GroupCompToSectionCPV');
            propertyClassID := getCCIClassID('ConceptVersion', 'GroupComp');
            assocComponentSection(p_version_code, componentClassID, propertyClassID, elementID, v_code, structureVersionID);

            componentClassID := getCCIClassID('ConceptPropertyVersion', 'InterventionToSectionCPV');
            propertyClassID := getCCIClassID('ConceptVersion', 'Intervention');
            assocComponentSection(p_version_code, componentClassID, propertyClassID, elementID, v_code, structureVersionID);

            --Tissue RelationShip
            if (v_tissue_code is not null) then
               componentElementID := tissue(v_tissue_code || '__' || v_code);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'TissueCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Approach Technique RelationShip
            if (v_approach_code is not null) then
               componentElementID := appTech(v_approach_code || '__' || v_code);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'ApproachTechniqueCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Device Agent RelationShip
            if (v_device_agent_code is not null) then
               componentElementID := deviceAgent(v_device_agent_code || '__' || v_code);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'DeviceAgentCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Intervention RelationShip
            if (v_intervention_code is not null) then
               componentElementID := intervention(v_intervention_code || '__' || v_code);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'InterventionCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Group RelationShip
            if (v_group_code is not null) then
               componentElementID := groupComp(v_group_code || '__' || v_code);
               componentRelationshipClassID := getCCIClassID('ConceptPropertyVersion', 'GroupCompCPV');
               buildNarrowRelationship(p_version_code, componentRelationshipClassID, elementID, componentElementID, structureVersionID);
            end if;

            --Build Narrow Relationship
            relationshipClassID := getCCIClassID('ConceptPropertyVersion', 'Narrower');
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID, structureVersionID);

            migrateChildNodes(p_version_code, structureVersionID, elementID, category_ID, elementID, v_code);

        end loop;

    exception
        when others then
            insertLog('migrateChapterNodes ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateChapterNodes. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateSectionNodes;


    /**************************************************************************************************************************************
    * NAME:          replaceGraphicPropertyEng
    * DESCRIPTION:   In the old system, the Supplement XML contains a graphic property.  In CIMS, it needs to be replaced with a graphic
    *                filename.
    *                Note:  Because this function simply does a replace, it suffers when the digit portion has two digits.
    *                Example:  GRAPH.1 replaces GRAPH.10, but leaves the 0.  Do the single digits last to avoid this issue.
    **************************************************************************************************************************************/
    FUNCTION replaceGraphicPropertyEng(origXML clob)
        RETURN CLOB
    IS
        tempXML clob := origXML;

    begin
        tempXML := REPLACE(tempXML, 'GRAPH.10', 'E_fig10cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.11', 'E_fig11cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.12', 'E_fig12cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.13', 'E_fig13cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.14', 'E_fig14cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.15', 'E_fig15cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.16', 'E_fig16cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.17', 'E_fig17cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.18', 'E_fig18cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.19', 'E_fig19cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.20', 'E_fig20cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.21', 'E_fig21cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.22', 'E_fig22cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.23', 'E_fig23cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.24', 'E_fig24cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25', 'E_fig25cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25a', 'E_fig25acci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25b', 'E_fig25bcci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26', 'E_fig26cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26a', 'E_fig26acci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26b', 'E_fig26bcci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26c', 'E_fig26ccci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.27', 'E_fig27cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.28', 'E_fig28cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.29', 'E_fig29cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.30', 'E_fig30cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.31', 'E_fig31cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.32', 'E_fig32cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.33', 'E_fig33cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.34', 'E_fig34cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.35', 'E_fig35cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.36', 'E_fig36cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.37', 'E_fig37cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.38', 'E_fig38cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.39', 'E_fig39cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.40', 'E_fig40cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.41', 'E_fig41cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.42', 'E_fig42cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.43', 'E_fig43cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.44', 'E_fig44cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.45', 'E_fig45cci.gif');
        tempXML := REPLACE(tempXML, 'TABLE.1', 'E_table1cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.1', 'E_figure1cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.2', 'E_figure2cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.3', 'E_figure3cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.4', 'E_figure4cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.5', 'E_figure5cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.1', 'E_fig1cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.2', 'E_fig2cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.3', 'E_fig3cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.4', 'E_fig4cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.5', 'E_fig5cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.6', 'E_fig6cci[1].gif');
        tempXML := REPLACE(tempXML, 'GRAPH.7', 'E_fig7cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.8', 'E_fig8cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.9', 'E_fig9cci.gif');
        return tempXML;

    end replaceGraphicPropertyEng;


    /**************************************************************************************************************************************
    * NAME:          replaceGraphicPropertyFra
    * DESCRIPTION:   In the old system, the Supplement XML contains a graphic property.  In CIMS, it needs to be replaced with a graphic
    *                filename.
    *                Note:  Because this function simply does a replace, it suffers when the digit portion has two digits.
    *                Example:  GRAPH.1 replaces GRAPH.10, but leaves the 0.  Do the single digits last to avoid this issue.
    **************************************************************************************************************************************/
    FUNCTION replaceGraphicPropertyFra(origXML clob)
        RETURN CLOB
    IS
        tempXML clob := origXML;

    begin
        tempXML := REPLACE(tempXML, 'GRAPH.10', 'F_fig10cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.11', 'F_fig11cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.12', 'F_fig12cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.13', 'F_fig13cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.14', 'F_fig14cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.15', 'F_fig15cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.16', 'frfig16.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.17', 'F_fig17cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.18', 'F_fig18cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.19', 'F_fig19cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.20', 'F_fig20cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.21', 'F_fig21cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.22', 'F_fig22cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.23', 'f_fig23cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.24', 'F_fig24cci[2].gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25', 'F_fig25cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25a', 'F_fig25acci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.25b', 'F_fig25bcci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26', 'F_fig26cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26a', 'F_fig26acci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26b', 'F_fig26bcci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.26c', 'F_fig26ccci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.27', 'F_fig27cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.28', 'F_fig28cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.29', 'F_fig29cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.30', 'F_fig30cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.31', 'F_fig31cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.32', 'F_fig32cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.33', 'F_fig33cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.34', 'F_fig34cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.35', 'F_fig35cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.36', 'F_fig36cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.37', 'F_fig37cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.38', 'F_fig38cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.39', 'F_fig39cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.40', 'F_fig40cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.41', 'F_fig41cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.42', 'F_fig42cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.43', 'F_fig43cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.44', 'F_fig44cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.45', 'F_fig45cci.gif');
        tempXML := REPLACE(tempXML, 'TABLE.1', 'F_table1cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.1', 'F_figure1cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.2', 'F_figure2cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.3', 'F_figure3cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.4', 'F_figure4cci.gif');
        tempXML := REPLACE(tempXML, 'FIGURE.5', 'F_figure5cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.1', 'F_fig1cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.2', 'F_fig2cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.3', 'F_fig3cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.4', 'F_fig4cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.5', 'F_fig5cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.6', 'F_fig6cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.7', 'F_fig7cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.8', 'F_fig8cci.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.9', 'F_fig9cci.gif');

        return tempXML;

    end replaceGraphicPropertyFra;


    /**************************************************************************************************************************************
    * NAME:          insertSupplement
    * DESCRIPTION:   Insert Supplement
    **************************************************************************************************************************************/
    procedure insertSupplement(p_version_code varchar2, structureVersionID number, viewerRootElementID number,
        suppDescription varchar2, suppType varchar2, xmlFilename varchar2, sortingHint number, lang varchar2, supplementID out number) IS

        text_data clob;
        suppDefinition clob;
        elementID number;
        supplementClassID number := getCCIClassID('ConceptVersion', 'Supplement');
        propertyClassID number := 0;
    begin

        select CIMS_ICD.clobfromblob(TEXT_DATA)
        into text_data
        from cci.text
        where file_name = xmlFilename
        and clinical_classification_code = 'CCI' || p_version_code
        and language_code = lang;

        elementID := insertConcept(p_version_code, supplementClassID, null, structureVersionID, 'ACTIVE');
        supplementID := elementID;

        if (lang = 'ENG') then
            suppDefinition := replaceGraphicPropertyEng(text_data);
        else
            suppDefinition := replaceGraphicPropertyFra(text_data);
        end if;

        --Store Supplement Description
        propertyClassID := getCCIClassID('TextPropertyVersion', 'SupplementDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, suppDescription, lang);

        --Store Supplement Definition English
        propertyClassID := getCCIClassID('XMLPropertyVersion', 'SupplementDefinition');
        insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, suppDefinition, lang);

        -- Store Sorting Hint
        propertyClassID := getCCIClassID('NumericPropertyVersion', 'SortingHint');
        insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, sortingHint);

        -- Store Supplement Type Indicator
        propertyClassID := getCCIClassID('ConceptPropertyVersion', 'SupplementTypeIndicator');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, supplementType(suppType), structureVersionID);

        --Build Narrow Relationship
        propertyClassID := getCCIClassID('ConceptPropertyVersion', 'Narrower');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, viewerRootElementID, structureVersionID);

    exception
        when others then
            insertLog('insertSupplement ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertSupplement. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertSupplement;


    /**************************************************************************************************************************************
    * NAME:          migrateSupplements
    * DESCRIPTION:   Migrates Supplements
    **************************************************************************************************************************************/
    procedure migrateSupplements(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is

        supplementID number;
        parentSupplementID number;

    begin
        -- Front Matter English
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Table of Contents',
            'F', 'cci_toc.xml', 1000, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'ICD-10-CA/CCI, Version ' || p_version_code || ' Licence Agreement',
            'F', 'cci_licence.xml', 1100, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'About the Canadian Institute for Health Information',
			'F', 'cci_about.xml', 1200, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Introduction to CCI',
			'F', 'cci_intro.xml', 1300, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Contact Us for More Information About ICD-10-CA and CCI',
			'F', 'cci_contact.xml', 1400, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Acknowledgments',
			'F', 'cci_acknowl.xml', 1500, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Diagrams in CCI',
			'F', 'cci_diagrams.xml', 1600, 'ENG', supplementID);

        -- Front Matter French
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Table des matières',
            'F', 'cci_toc.xml', 6000, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Accord de licence de la CIM-10-CA/CCI',
            'F', 'cci_licence.xml', 6100, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'(CIHI)	À propos de l'' Institut canadien d'' information sur la santé (ICIS)',
			'F', 'cci_about.xml', 6200, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Présentation de la CCI',
			'F', 'cci_intro.xml', 6300, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Pour plus d¿informations concernant la CIM-10-CA et la CCI, n'' hésitez pas à nous contacter.',
			'F', 'cci_contact.xml', 6400, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Remerciements',
			'F', 'cci_acknowl.xml', 6500, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Schémas de la CCI',
			'F', 'cci_diagrams_fra.xml', 6600, 'FRA', supplementID);

        -- Back Matter English
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Appendix A -- CCI Code Structure',
			'B', 'cci_app_a_intro.xml', 1700, 'ENG', supplementID);

        parentSupplementID := supplementID;
        -- this one has a different parent
        insertSupplement(p_version_code, structureVersionID, parentSupplementID,
			'An Overview:',
			'B', 'cci_app_a_def.xml', 1800, 'ENG', supplementID);

        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Appendix C -- CCI Attributes',
			'B', 'cci_app_c_intro.xml', 1900, 'ENG', supplementID);

        parentSupplementID := supplementID;
        -- this one has a different parent
        insertSupplement(p_version_code, structureVersionID, parentSupplementID,
			'Use of Reference Codes for CCI Attributes',
			'B', 'cci_app_c_ref_intro.xml', 2000, 'ENG', supplementID);

        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Appendix D -- Pharmacological, Biological and Other Agents: Table of Conversions for CCI Code Component to ATC Code',
			'B', 'cci_app_d_intro.xml', 2100, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Appendix E -- New CCI Codes',
			'B', 'cci_appendix_e_new_codes.xml', 2200, 'ENG', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Appendix F -- Disabled CCI Codes',
			'B', 'cci_appendix_f_disabled_codes.xml', 2300, 'ENG', supplementID);

        -- Back Matter French
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Annexe A -- Structure du code de la CCI',
			'B', 'cci_app_a_intro.xml', 6700, 'FRA', supplementID);

        parentSupplementID := supplementID;
        -- this one has a different parent
        insertSupplement(p_version_code, structureVersionID, parentSupplementID,
			'Groupe :',
			'B', 'cci_app_a_def.xml', 6800, 'FRA', supplementID);

        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Annexe C -- Attributs de la CCI',
			'B', 'cci_app_c_intro.xml', 6900, 'FRA', supplementID);

        parentSupplementID := supplementID;
        -- this one has a different parent
        insertSupplement(p_version_code, structureVersionID, parentSupplementID,
			'Annexe C -- Attributs de la CCI',
			'B', 'cci_app_c_ref_intro.xml', 7000, 'FRA', supplementID);

        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Annexe D -- Agents pharmacologiques, biologiques et autres - Tableau des conversions des composants de code de la CCI en code ATC',
			'B', 'cci_app_d_intro.xml', 7100, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Annexe E - Nouveaux Codes de la CCI',
			'B', 'cci_french_appendix_e_new_codes.xml', 7200, 'FRA', supplementID);
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
			'Annexe F - Codes Désactivés de la CCI',
			'B', 'cci_french_appendix_f_disabled_codes.xml', 7300, 'FRA', supplementID);

    exception
        when others then
            insertLog('migrateSupplements ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateSupplements. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateSupplements;


    /**************************************************************************************************************************************
    * NAME:          insertDiagram
    * DESCRIPTION:   Insert Diagram
    **************************************************************************************************************************************/
    procedure insertDiagram(p_version_code varchar2, structureVersionID number, viewerRootElementID number,
        diagramFilename varchar2, lang varchar2) IS

        diag blob;
        diagDescription varchar2(3000);
        elementID number;
        diagramClassID number := getCCIClassID('ConceptVersion', 'Diagram');
        propertyClassID number := 0;
    begin

        select Graphic_desc, Graphic_data
        into diagDescription, diag
        from cci.graphic
        where UPPER(file_name) = UPPER(diagramFilename)
        and language_code = lang
        and clinical_classification_code = 'CCI' || p_version_code;

        elementID := insertConcept(p_version_code, diagramClassID, null, structureVersionID, 'ACTIVE');

        --Store Diagram Description
        propertyClassID := getCCIClassID('TextPropertyVersion', 'DiagramDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, diagDescription, null);

        --Store Diagram Filename
        propertyClassID := getCCIClassID('TextPropertyVersion', 'DiagramFileName');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, diagramFilename, null);

        -- Store Diagram Graphic
        propertyClassID := getCCIClassID('GraphicsPropertyVersion', 'DiagramFigure');
        insertGraphicProperty(p_version_code, elementID, propertyClassID, structureVersionID, diag, null);

        --Build Narrow Relationship
        propertyClassID := getCCIClassID('ConceptPropertyVersion', 'Narrower');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, viewerRootElementID, structureVersionID);

    exception
        when others then
            insertLog('insertDiagram ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertDiagram. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertDiagram;


    /**************************************************************************************************************************************
    * NAME:          migrateDiagrams
    * DESCRIPTION:   Migrates Diagrams
    **************************************************************************************************************************************/
    procedure migrateDiagrams(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is

    begin

        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_table1cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure1cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure2cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure3cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure4cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure5cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig1cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig2cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig3cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig4cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig5cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig6cci[1].gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig7cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig8cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig9cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig10cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig11cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig12cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig13cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig14cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig15cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig16cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig17cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig18cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig19cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig20cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig21cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig22cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig23cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig24cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig25cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig25acci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig25bcci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig26cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig26acci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig26bcci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig26ccci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig27cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig28cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig29cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig30cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig31cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig32cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig33cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig34cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig35cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig36cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig37cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig38cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig39cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig40cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig41cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig42cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig43cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig44cci.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig45cci.gif', 'ENG');




        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_table1cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure1cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure2cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure3cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure4cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure5cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig1cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig2cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig3cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig4cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig5cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig6cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig7cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig8cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig9cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig10cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig11cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig12cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig13cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig14cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig15cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'frfig16.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig17cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig18cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig19cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig20cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig21cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig22cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'f_fig23cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig24cci[2].gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig25cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig25acci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig25bcci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig26cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig26acci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig26bcci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig26ccci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig27cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig28cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig29cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig30cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig31cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig32cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig33cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig34cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig35cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig36cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig37cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig38cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig39cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig40cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig41cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig42cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig43cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig44cci.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig45cci.gif', 'FRA');

    exception
        when others then
            insertLog('migrateDiagrams ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateDiagrams. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateDiagrams;


    /**************************************************************************************************************************************
    * NAME:          init_BaseClassification
    * DESCRIPTION:   Initializes the base classification for CCI
    **************************************************************************************************************************************/
    PROCEDURE init_BaseClassification(p_version_code varchar2, structureVersionID OUT number) is

        classID number := getCCIClassID('BaseClassification', 'CCI');
        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateConceptBusinessKey(cci_classification_code, classID, null);

    BEGIN
        elementID := elementid_SEQ.nextval;
        elementVersionID := elementversionid_SEQ.nextval;
        structureVersionID := elementVersionID;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, classID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, classID, null, structureVersionID);

        insert into STRUCTUREVERSION (STRUCTUREID, CLASSID, STATUS, ELEMENTID, BASESTRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, ISVERSIONYEAR)
        values (elementVersionID, classID, status_code, elementID, null, 'OPEN', sysdate, 'Y');

    end init_BaseClassification;


    /**************************************************************************************************************************************
    * NAME:          createClassificationRoot
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/
    PROCEDURE createClassificationRoot(p_version_code varchar2, structureVersionID number, viewerRootID out number) is

        classID number := getCCIClassID('ConceptVersion', 'ClassificationRoot');
        elementID number := 0;
        propertyClassID number := 0;
        v_title_eng varchar2(255) := 'CANADIAN CLASSIFICATION OF HEALTH INTERVENTIONS[CCI] ' || p_version_code;
        v_title_fra varchar2(255) := 'LA CLASSIFICATION CANADIENNE DES INTERVENTIONS EN SANTÉ [CCI] ' || p_version_code;

        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100);

    BEGIN
        businessKey := generateConceptBusinessKey(cci_classification_code, classID, 'ViewerRoot');
        elementID := insertConcept(p_version_code, classID, businessKey, structureVersionID, status_code);
        viewerRootID := elementID;

        --Store Short title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store Long title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store User title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store Short title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'ShortTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

        --Store Long title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'LongTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

        --Store User title
        propertyClassID := getCCIClassID('TextPropertyVersion', 'UserTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

    end createClassificationRoot;


    /**************************************************************************************************************************************
    * NAME:          cleanUp_LookupTables
    * DESCRIPTION:   Clean up procedure to clean the CLASS, LANGUAGE, and the dagger asterisk tables
    *                Ensure any referencing records are deleted first.
    **************************************************************************************************************************************/
    PROCEDURE cleanUp_LookupTables is

    BEGIN
        delete from CLASS where BASECLASSIFICATIONNAME = cci_classification_code;
        --delete from LANGUAGE;

        commit;

    end cleanUp_LookupTables;


    /**************************************************************************************************************************************
    * NAME:          populate_lookUp
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE populate_lookUp IS

        languageCount number;

    BEGIN

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'ClassificationRoot', null, 'Classification Root');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BaseClassification', cci_classification_code, 'CCI', null, 'CCI');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Section', null, 'Section');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Block', null, 'Block');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Group', null, 'Group');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Rubric', null, 'Rubric');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'CCICODE', null, 'Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'Code', null, 'Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'ShortTitle', null, 'Title Short');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'LongTitle', null, 'Title Long');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'UserTitle', null, 'Title User');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'Narrower', null, 'Narrower Relationship');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'IncludePresentation', null, 'Directive Include');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'ExcludePresentation', null, 'Directive Exclude');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'CodeAlsoPresentation', null, 'Directive Code Also');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'NotePresentation', null, 'Directive Note');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'OmitCodePresentation', null, 'Directive Omit Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', cci_classification_code, 'TablePresentation', null, 'Table CCI');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', cci_classification_code, 'CaEnhancementIndicator', null, 'Canadian Enhancement Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', cci_classification_code, 'LongPresentation', null, 'Long Presentation');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', cci_classification_code, 'ShortPresentation', null, 'Short Presentation');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'GraphicsPropertyVersion', cci_classification_code, 'Diagram', null, 'Diagram');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DiagramFileName', null, 'Diagram File Name');

        --Component Concepts
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Tissue', null, 'Tissue');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'DeviceAgent', null, 'Device Agent');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'ApproachTechnique', null, 'Approach Technique');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Intervention', null, 'Intervention');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'GroupComp', null, 'Group');

        --Component Properties
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'ComponentShortTitle', null, 'Short Title');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'ComponentLongTitle', null, 'Long Title');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'ComponentDefinitionTitle', null, 'Definition');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'ComponentCode', null, 'Code');

        --Component Properties - Specific to Device Agent
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AgentTypeDescription', null, 'Agent Type Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AgentExample', null, 'Agent Example');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AgentATCCode', null, 'Agent ATC Code');

        --Component Relationships
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'TissueCPV', null, 'Tissue Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'DeviceAgentCPV', null, 'Device Agent Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'ApproachTechniqueCPV', null, 'Approach Technique Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'InterventionCPV', null, 'Intervention Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'GroupCompCPV', null, 'Group Indicator');

        --Component To Section Relationships
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'TissueToSectionCPV', null, 'Tissue To Section Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'DeviceAgentToSectionCPV', null, 'Device Agent To Section Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'ApproachTechniqueToSectionCPV', null, 'Approach Technique To Section Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'InterventionToSectionCPV', null, 'Intervention To Section Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'GroupCompToSectionCPV', null, 'Group To Section Indicator');

        --Attribute Concepts
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'GenericAttribute', null, 'Generic Attribute');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'ReferenceAttribute', null, 'Reference Attribute');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Attribute', null, 'Attribute');

        --Attribute Properties
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AttributeDescription', null, 'Attribute Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AttributeCode', null, 'Attribute Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', cci_classification_code, 'AttributeMandatoryIndicator', null, 'Attribute Mandatory Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'AttributeNoteDescription', null, 'Attribute Note Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'AttributeNote', null, 'Attribute Note');

        --Attribute Relationships
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'GenericAttributeCPV', null, 'Generic Attribute Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'ReferenceAttributeCPV', null, 'Reference Attribute Indicator');

        --Validation rules
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'ValidationCCI', null, 'Validation');

        --Validation - Category
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Validation', null, 'Validation to Tabular Concept');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'ValidationDefinition', null, 'Validation Definition');

        --Validation - Category Relationships
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'ValidationCCICPV', null, 'Validation to Tabular Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'ValidationFacility', null, 'Validation to Data Holiding Indicator');

        --Domain Values - Common properties
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DomainValueCode', null, 'Domain Value Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DomainValueDescription', null, 'Domain Value Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DomainValueDefinition', null, 'Domain Value Definition');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DomainValueLabel', null, 'Domain Value Label');

        --Domain Values - Invasiveness Level
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'InvasivenessLevel', null, 'Invasiveness Level Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'InvasivenessLevelIndicator', null, 'Invasiveness Level Indicator');

        --Domain Values - Agent Group
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'AgentGroup', null, 'Agent Group');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'AgentGroupIndicator', null, 'Agent Group Indicator');

        --Domain Values - Attribute Type
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'AttributeType', null, 'Attribute Type');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'AttributeTypeIndicator', null, 'Attribute Type Indicator');

        --Domain Values - Sex Validation
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'SexValidation', null, 'Sex Validation');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'SexValidationIndicator', null, 'Sex Validation Indicator');

        --Domain Values - Facility Type
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'FacilityType', null, 'Data Holding');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'FacilityTypeIndicator', null, 'Data Holding Indicator');

        --Index related Classes
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'LetterIndex', null, 'Letter Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'BookIndex', null, 'Book Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Index', null, 'Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'ReferenceIndex', null, 'Reference Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'IndexCode', null, 'Index Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'IndexDesc', null, 'Index Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'RefLinkDesc', null, 'Reference Link Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'IndexNoteDesc', null, 'Index Note Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'IndexReference', null, 'Index Reference Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'IndexReferredTo', null, 'Index Referred To Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'NumericPropertyVersion', cci_classification_code, 'Level', null, 'Level');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'IndexRefDefinition', null, 'Index Reference Definition');

        --Index See Also Flag
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'SeeAlso', null, 'See / See Also');

        --Change Request related
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', cci_classification_code, 'RequestTouched', null, 'Modified Flag');

        -- Supplement related
        -- Supplements
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Supplement', null, 'Supplement');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', cci_classification_code, 'SupplementDefinition', null, 'Supplement Definition');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'SupplementDescription', null, 'Supplement Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'NumericPropertyVersion', cci_classification_code, 'SortingHint', null, 'Sorting Hint');

        -- Domain value - Supplement Type
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'SupplementType', null, 'Supplement Type');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', cci_classification_code, 'SupplementTypeIndicator', null, 'Supplement Type Indicator');

        -- Diagram Concept
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', cci_classification_code, 'Diagram', null, 'Diagram');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', cci_classification_code, 'DiagramDescription', null, 'Diagram Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'GraphicsPropertyVersion', cci_classification_code, 'DiagramFigure', null, 'Diagram Figure');

        SELECT count(*)
        INTO languageCount
        FROM language;

        --LANGUAGE
        if (languageCount = 0) then
            insert into LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('ENG', 'English');
            insert into LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('FRA', 'French');
        end if;

        commit;

    end populate_lookUp;


    /**************************************************************************************************************************************
    * NAME:          cci_data_migration_cleanup
    * DESCRIPTION:   Will not run successfully if there are referencing records
    **************************************************************************************************************************************/
    PROCEDURE cci_data_migration_cleanup(version_Code varchar2) is
        runStatus varchar2(10) := CIMS_CCI.checkRunStatus;

    BEGIN

        f_year := version_Code;
        dbms_output.enable(1000000);

        IF runStatus = 'FALSE' THEN
            dbms_output.put_line('Script already running....');
            RETURN;
        END IF;

        insertLog('Cleaning up lookup tables for CCI ' || version_code);
        insertLog('---------------------------------------------------------------');
        insertLog('    Clean up tables to begin migration');
        cleanUp_LookupTables;

        insertLog('    Population of lookup tables');
        populate_lookUp;

        insertLog('Ending of cleanup tables for CCI ' || version_code);

        commit;

    END cci_data_migration_cleanup;


    /**************************************************************************************************************************************
    * NAME:          cci_data_migration
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/
    PROCEDURE cci_data_migration(version_Code varchar2) is
        structureVersionID number := 0;
        viewerRootElementID number := 0;
        logRunID number := 0;
        runStatus varchar2(10) := CIMS_CCI.checkRunStatus;

    BEGIN
        f_year := version_Code;
        dbms_output.enable(1000000);

        IF runStatus = 'FALSE' THEN
            dbms_output.put_line('Script already running....');
            RETURN;
        END IF;

        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('Starting CCI migration ' || version_code || ' Migration Run ID: ' || logRunID);
        insertLog('---------------------------------------------------------------');

        insertLog('Creating Base Classification');
        init_BaseClassification(version_Code, structureVersionID);

        insertLog('Populating Lookup tables');
        insertLog('  - Invasiveness');
        populateInvasivenessLookup(version_Code, structureVersionID);
        insertLog('  - Agent Group');
        populateAgentGroup(version_Code, structureVersionID);
        insertLog('  - Attribute Type');
        populateAttributeTypeLookup(version_Code, structureVersionID);
        insertLog('  - Sex Validation');
        populateSexValidationLookup(version_Code, structureVersionID);
        insertLog('  - Facility Type');
        populateFacilityTypeLookup(version_Code, structureVersionID);
        insertLog('  - Supplement Type');
        populateSupplementTypeLookup(version_Code, structureVersionID);

        insertLog('Migrating Attributes');
        insertLog('  - Generic Attributes');
        migrateAttributeGeneric(version_Code, structureVersionID);
        insertLog('  - Reference Attributes');
        migrateAttributeReference(version_Code, structureVersionID);
        insertLog('  - Attributes');
        migrateAttribute(version_Code, structureVersionID);

        insertLog('Creating Classification Root');
        createClassificationRoot(version_Code, structureVersionID, viewerRootElementID);

        insertLog('Migrating Components');
        insertLog('  - Tissue');
        migrateTissueComponent(version_Code, structureVersionID);
        insertLog('  - Approach Technique');
        migrateAppTechComponent(version_Code, structureVersionID);
        insertLog('  - Intervention');
        migrateInterventionComponent(version_Code, structureVersionID);
        insertLog('  - Group');
        migrateGroupComponent(version_Code, structureVersionID);
        insertLog('  - Device Agent');
        migrateDeviceAgentComponent(version_Code, structureVersionID);

        insertLog('-- Main migration --');
        migrateSectionNodes(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending main migration --');

        insertLog('-- Migrating Supplements --');
        migrateSupplements(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending Migrating Supplements --');

        insertLog('-- Migrating Diagrams --');
        migrateDiagrams(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending Migrating Diagrams --');

        insertLog('-- Code Update --');
        CIMS_CCI.UpdateCode;
        CIMS_CCI.UpdateCodeInClob;

        insertLog('Ending CCI migration ' || version_code);

--        rollback;
        commit;

    END cci_data_migration;


end CCI_DATA_MIGRATION;
/
