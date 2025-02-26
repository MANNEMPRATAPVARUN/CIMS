create or replace package ICD_DATA_MIGRATION is

    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;
    facilityType1ElementID number;
    facilityTypeAElementID number;

    TYPE facilityType_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    facilityType facilityType_t;

    TYPE sv_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    sv sv_t;

    TYPE sv_DESC_ENG_t IS TABLE OF VARCHAR2(50) INDEX BY VARCHAR2(30);
    svDESCEng sv_DESC_ENG_t;

    TYPE sv_DESC_FRA_t IS TABLE OF VARCHAR2(50) INDEX BY VARCHAR2(30);
    svDESCFra sv_DESC_FRA_t;

    TYPE da_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    da da_t;

    TYPE supplementType_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    supplementType supplementType_t;

    --errString varchar(4000);

    --Common Procedures
    FUNCTION insertConcept(p_version_code varchar2, propertyClassID number, businessKey varchar2, structureVersionID number,
        status_code varchar2) RETURN NUMBER;
    procedure insertHTMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number, xmlData clob,
        language_code char);
    procedure insertXMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number, xmlData clob,
        language_code char);
    procedure insertBooleanProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
        booleanProp char);
    procedure insertTextProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number, textProp varchar2,
        language_code char);
    procedure insertNumericProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
        numValue number);
    procedure insertGraphicProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number, graphicData blob,
        language_code char);

    procedure buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number, rangeElementID number,
        structureVersionID number);
    procedure migrateTableOutput(p_version_code varchar2, icd_category_ID number, domainElementID number, structureVersionID number);
    procedure migrateIncludeExclude(p_version_code varchar2, icd_category_ID number, domainElementID number, structureVersionID number);

    --Public for Index Migration
    function populateDomainValueLookup(p_version_code varchar2, structureVersionID number,
        textCode varchar2, textCodeLanguage varchar2 Default null,
        textDescription varchar2, textLabel varchar2, textDefinition varchar2,
        textDescription_fr varchar2, textLabel_fr varchar2, textDefinition_fr varchar2,
        domainValueClassID number) return number;


    PROCEDURE cleanUp_LookupTables;
    PROCEDURE populate_lookUp;
    PROCEDURE icd_data_migration_cleanup(version_Code varchar2);
    PROCEDURE icd_data_migration(version_Code IN varchar2);

end ICD_DATA_MIGRATION;
/
create or replace package body ICD_DATA_MIGRATION is


    /**************************************************************************************************************************************
    * NAME:          insertLog
    * DESCRIPTION:   Write to the log table
    **************************************************************************************************************************************/
    procedure insertLog(message varchar2) is
        logDate date;
        logID number := 0;
        logRunID number := 0;

        PRAGMA AUTONOMOUS_TRANSACTION;
    begin

        dbms_output.put_line(message);

        logID := LOG_SEQ.Nextval;
        logRunID := LOG_RUN_SEQ.CURRVAL;
        logDate := sysdate;

        insert into LOG(ID, MESSAGE, MESSAGEDATE, CLASSIFICATION, FISCAL_YEAR, RUN_ID)
        values (logID, message, logDate, icd_classification_code, f_year, logRunID);

       commit;

    end insertLog;


    /**************************************************************************************************************************************
    * NAME:          generateBusinessKey
    * DESCRIPTION:   Examples

            ICD-10-CA:A00
            CCI:1892660:6:ENG
            CCI:1892660:HtmlPropertyVersion:LongPresentation:ENG
            ICD-10-CA:2747198:ConceptPropertyVersion:Narrower
            ICD-10-CA:2747198:TextPropertyVersion:Code
            ICD-10-CA:2747198:TextPropertyVersion:ShortTitle:ENG
            ICD-10-CA:2778423:ConceptPropertyVersion:Narrower
            ICD-10-CA:2778423:TextPropertyVersion:Code
            ICD-10-CA:2778423:TextPropertyVersion:ShortTitle:ENG
            ICD-10-CA:71:XmlPropertyVersion:IncludePresentation:ENG
            ICD-10-CA:A00
            ICD-10-CA:A00000
            ICD-10-CA:A00000000
            ICD-10-CA:CONTEXT:1234567890123456789012345678901
            ICD-10-CA:CONTEXT:2012
            ICD-10-CA:CONTEXT:2012_1
            ICD-10-CA:CONTEXT:2012_1234567890123456789012345678901
            ICD-10-CA:CONTEXT:2022
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
    * NAME:          insertGraphicProperty
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure insertGraphicProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              graphicData blob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, language_code);

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
    procedure insertHTMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, language_code);

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
    procedure insertXMLProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, language_code);
        cleanedXmlData clob;

    begin

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
    procedure insertBooleanProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              booleanProp char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, null);

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
    procedure insertTextProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              textProp varchar2, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, language_code);

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
    procedure insertNumericProperty(p_version_code varchar2, domainElementID number, propertyClassID number, structureVersionID number,
              numValue number) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, propertyClassID, null);

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
            status_code varchar2)

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
        values (elementID, propertyClassID, busKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, status_code, null, propertyClassID, null, structureVersionID);

        insert into CONCEPTVERSION (CONCEPTID, CLASSID, STATUS, ELEMENTID)
        values (elementVersionID, propertyClassID, status_code, elementID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, elementID, null, sysdate, null);

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
    function populateDomainValueLookup(p_version_code varchar2, structureVersionID number,
        textCode varchar2, textCodeLanguage varchar2 DEFAULT NULL,
        textDescription varchar2, textLabel varchar2, textDefinition varchar2,
        textDescription_fr varchar2, textLabel_fr varchar2, textDefinition_fr varchar2,
        domainValueClassID number) return number is

        elementID number := 0;
        businessKey varchar2(100) := '';
        status_code varchar2(10) := 'ACTIVE';
        propertyClassID number := 0;
        lang_ENG varchar2(3) := 'ENG';
        lang_FRA varchar2(3) := 'FRA';
    begin
        businessKey := generateConceptBusinessKey(icd_classification_code, domainValueClassID, textCode);
        elementID := insertConcept(p_version_code, domainValueClassID, businessKey, structureVersionID, status_code);

        --Store Domain Value Code
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, textCode, textCodeLanguage);

        --Store Domain Value Description
        if (textDescription is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDescription,''), lang_ENG);
        end if;

        --Store Domain Value Label
        if (textLabel is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueLabel');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textLabel,''), lang_ENG);
        end if;

        --Store Domain Value Description
        if (textDefinition is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueDefinition');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDefinition,''), lang_ENG);
        end if;

        --Store Domain Value Description French
        if (textDescription_fr is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueDescription');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDescription_fr,''), lang_FRA);
        end if;

        --Store Domain Value Label French
        if (textLabel_fr is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueLabel');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textLabel_fr,''), lang_FRA);
        end if;

        --Store Domain Value Description
        if (textDefinition_fr is not null) then
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueDefinition');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(textDefinition_fr,''), lang_FRA);
        end if;

        return elementid;

    exception
        when others then
            insertLog('Error occured in populateDomainValueLookup procedure');
            insertLog('Error inside populateDomainValueLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateDomainValueLookup. <br> Error:' || substr(sqlerrm, 1, 50));

    end populateDomainValueLookup;


    /**************************************************************************************************************************************
    * NAME:          insertValidationRule
    * DESCRIPTION:   Updated due to change control CSRE-458
    *
    *   MDB Validation Sets						            CIMS Full DxType Validity for MDB Validation Sets
    *   -----------------------------------------------------------------------------------------------------
    *   MRDx	DxT1	DxT2	Dx Type	Prim/Sec		    MRDx	1	2	3	4	6	9	W	X	Y	Label
    *   ----------------------------------------            -------------------------------------------------
    *   Y	    Y	    Y	    -	    P		            Y	    Y	Y	Y	N	N	N	Y	Y	Y	Set 1
    *   N	    Y	    Y	    -	    P		            N	    Y	Y	Y	N	N	N	Y	Y	Y	Set 2
    *   Y	    N	    N	    -	    P		            Y	    N	N	N	N	N	N	N	N	N	Set 3
    *   N	    N	    N	    6	    P		            N	    N	N	Y	N	Y	N	N	N	N	Set 4
    *   N	    N	    N	    3	    P		            N	    N	N	Y	N	N	N	N	N	N	Set 5
    *   N	    N	    N	    4	    S		            N	    N	N	N	Y	N	N	N	N	N	Set 6
    *   N	    N	    N	    9	    S		            N	    N	N	N	N	N	Y	N	N	N	Set 7
    *
    *   CIMS DxType 3,6,4,9 Migration Rules:
    *   1. If DxType (MDB field) is 3, 4 or 9, set the corresponding CIMS DxType field to "Y" and other DxType 3,4,6,9 fields to "N"
    *   2. If DxType (MDB field) is 6, set the CIMS DxType 3, 6 fields to "Y" and DxType 4,9 fields to "N"
    *   3. If DxType (MDB field) is blank, and DxT1 (MDB field) is Y, set CIMS DxType 3 ="Y" and set CIMS DxType 4, 6, 9 fields to "N"
    *   4. If DxType (MDB field) is blank, and DxT1 (MDB field) is N, set CIMS DxType 3, 4, 6, 9 fields to "N"
    **************************************************************************************************************************************/
    function insertValidationRule(p_version_code varchar2, icdValidationID number, structureVersionID number, domainElementID number,
        facilityTypeElementID number)
        RETURN NUMBER
    IS
        cursor c is
            select v.*
            from icd.icd_validation v
            where v.icd_validation_id = icdValidationID;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';
        icd_validation_id number;
        sex_validation_code varchar2(3);
        icd_validation_desc varchar2(60);
        type_code varchar2(1);
        mr_diag varchar2(1);
        diag_type_1_flag varchar2(1);
        diag_type_2_flag varchar2(1);
        diag_type_3_flag varchar2(1) := 'N';
        diag_type_4_flag varchar2(1) := 'N';
        diag_type_6_flag varchar2(1) := 'N';
        diag_type_9_flag varchar2(1) := 'N';
        diag_type_code varchar2(1);
        newborn_flag varchar2(1);
        age_min number;
        age_max number;
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;
        --domainValueElementID number := 0;

        validationDefinitionXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE validation SYSTEM "/dtd/cihi_cims_validation.dtd">'
            || '<validation classification="ICD-10-CA" language="">';
        validationDefinitionXMLEnd VARCHAR2(50) := '</validation>';
        validationDefinitionXML VARCHAR2(3000);

    begin
        status_code := 'ACTIVE';
        mainClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ValidationICD');

        for rec_cc in c loop
            icd_validation_id := TRIM(rec_cc.icd_validation_id);
            sex_validation_code := TRIM(rec_cc.sex_validation_code);
            icd_validation_desc := TRIM(rec_cc.validation_desc);
            type_code := TRIM(rec_cc.classification_type_code);
            mr_diag := TRIM(rec_cc.mr_diag);
            diag_type_1_flag := TRIM(rec_cc.diag_type_1_flag);
            diag_type_2_flag := TRIM(rec_cc.diag_type_2_flag);
            diag_type_code := TRIM(rec_cc.diag_type_code);
            newborn_flag := TRIM(rec_cc.newborn_flag);
            age_min := TRIM(rec_cc.age_min);
            age_max := TRIM(rec_cc.age_max);

            if (diag_type_code = 3) then
                diag_type_3_flag := 'Y';
            elsif (diag_type_code = 4) then
                diag_type_4_flag := 'Y';
            elsif (diag_type_code = 6) then
                diag_type_3_flag := 'Y';
                diag_type_6_flag := 'Y';
            elsif (diag_type_code = 9) then
                diag_type_9_flag := 'Y';
            else
                --DIAG_TYPE_CODE IS NULL.
                --If DxT1 is Y, set DxType 3 ="Y" and DxType 4, 6, 9 to "N"
                --If DxT1 is N, set DxType 3, 4, 6, 9 to "N"
                if (diag_type_1_flag = 'Y') then
                    diag_type_3_flag := 'Y';
                end if;

            end if;

            businessKey := generateConceptBusinessKey(icd_classification_code, mainClassID,
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

            -- Add MR Diag
            validationDefinitionXML := validationDefinitionXML || '<MRDX_MAIN>' || nvl(mr_diag, 'N') || '</MRDX_MAIN>';

            -- Add Diag Type Flag 1
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_1>' || nvl(diag_type_1_flag, 'N') || '</DX_TYPE_1>';

            -- Add Diag Type Flag 2
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_2>' || nvl(diag_type_2_flag, 'N') || '</DX_TYPE_2>';

            -- Add Diag Type Flag 3
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_3>' || diag_type_3_flag || '</DX_TYPE_3>';

            -- Add Diag Type Flag 4
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_4>' || diag_type_4_flag || '</DX_TYPE_4>';

            -- Add Diag Type Flag 6
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_6>' || diag_type_6_flag || '</DX_TYPE_6>';

            -- Add Diag Type Flag 9
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_9>' || diag_type_9_flag || '</DX_TYPE_9>';

            -- Add Diag Type Flag W
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_W>' || nvl(diag_type_1_flag, 'N') || '</DX_TYPE_W>';

            -- Add Diag Type Flag X
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_X>' || nvl(diag_type_1_flag, 'N') || '</DX_TYPE_X>';

            -- Add Diag Type Flag Y
            validationDefinitionXML := validationDefinitionXML || '<DX_TYPE_Y>' || nvl(diag_type_1_flag, 'N') || '</DX_TYPE_Y>';

            -- Add NewBorn Flag
            validationDefinitionXML := validationDefinitionXML || '<NEW_BORN>' || nvl(newborn_flag, 'N') || '</NEW_BORN>';

            -- Add XML declaration and validation element end
            validationDefinitionXML := validationDefinitionXML || validationDefinitionXMLEnd;

            -- Add newly created XML to Validation Definition property
            propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'ValidationDefinition');
            insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, to_clob(nvl(validationDefinitionXML, '')), '');

        end loop;

         return elementid;

    exception
        when others then
            insertLog('insertValidationRule ' || SQLCODE || ' ' || SQLERRM);
            insertLog(icdValidationID || ' <-- icdValidationID ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertValidationRule. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertValidationRule;


    /**************************************************************************************************************************************
    * NAME:          insertValidation
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure insertValidation(p_version_code varchar2, domainElementID number, structureVersionID number,
              fType varchar2, icdValidationID number) is

        relationshipClassID number;
        facilityTypeElementID number;
        validationElementID number;
    begin
        facilityTypeElementID := facilityType(fType);
        validationElementID := insertValidationRule(p_version_code, icdValidationID, structureVersionID, domainElementID, facilityTypeElementID);

        --Validation ICD Relationship
        relationshipClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
        buildNarrowRelationship(p_version_code, relationshipClassID, validationElementID, domainElementID, structureVersionID);

        --Facility Type Relationship
        relationshipClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
        buildNarrowRelationship(p_version_code, relationshipClassID, validationElementID, facilityTypeElementID, structureVersionID);

    exception
        when others then
            insertLog('Error occured in insertValidation procedure');
            insertLog(domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertValidation. <br> Error:' || substr(sqlerrm, 1, 512));

    end insertValidation;


    /**************************************************************************************************************************************
    * NAME:          migrateIncludeExclude
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure migrateIncludeExclude(p_version_code varchar2, icd_category_ID number, domainElementID number, structureVersionID number) is

        cursor c_data is
            select *
            from icd.category_detail cd
            where cd.category_id = icd_category_ID;

        rec_cc c_data%rowtype;
        category_detail_type_code varchar2(20);
        language_code char(3);
        category_detail_data clob;
        classID number;

    begin
        for rec_cc in c_data loop
            category_detail_type_code := rec_cc.category_detail_type_code;
            language_code := rec_cc.language_code;
            category_detail_data := rec_cc.category_detail_data;

            IF UPPER(TRIM(category_detail_type_code)) = 'E' THEN
                classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'ExcludePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'I' THEN
                classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IncludePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'A' THEN
                classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'CodeAlsoPresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'N' THEN
                classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'NotePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSE
                insertLog('Unknown category detail type code: ' || category_detail_type_code);
            END IF;

        end loop;

    exception
        when others then
            insertLog('Error in migrateIncludeExclude with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIncludeExclude. <br> Error:' || substr(sqlerrm, 1, 512));

    end migrateIncludeExclude;


    /**************************************************************************************************************************************
    * NAME:          migrateTableOutput
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure migrateTableOutput(p_version_code varchar2, icd_category_ID number, domainElementID number, structureVersionID number) is

        cursor c_data is
            select *
            from icd.category_table_output cto
            where cto.category_id = icd_category_ID;

        rec_cc c_data%rowtype;
        language_code char(3);
        category_table_output_data clob;
        classID number;

    begin
        for rec_cc in c_data loop
            language_code := rec_cc.language_code;
            category_table_output_data := rec_cc.category_table_output_data;

            classID := CIMS_ICD.getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation');
            insertHTMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_table_output_data, language_code);

        end loop;

    exception
        when others then
            insertLog('Error in migrateTableOutput with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateTableOutput. <br> Error:' || substr(sqlerrm, 1, 512));

    end migrateTableOutput;


    /**************************************************************************************************************************************
    * NAME:          buildNarrowRelationship
    * DESCRIPTION:   Builds the parent-child relationship between two nodes in a tree
    **************************************************************************************************************************************/
    procedure buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number,
        rangeElementID number, structureVersionID number) is

        elementID number := 0;
        elementVersionID number := 0;
        status_code VARCHAR(10) := 'ACTIVE';
        businessKey varchar2(100) := generateBusinessKey(icd_classification_code, domainElementID, relationshipClassID, null);

    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, relationshipClassID, businessKey, null);

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
    * NAME:          migrateChildNodes
    * DESCRIPTION:   Recursive procedure to migrate child nodes
    *                Cannot combine with migrateChapterNodes, takes in different number of parameters
    *                Adding in parameter v_chapter_code, which was a special case where for chapter 22 the category type code needs to
    *                be changed.
    *                TODO: If ICD10 code (starts with a letter) apply format as follows: One dot after three digits
    **************************************************************************************************************************************/
    procedure migrateChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number, parentCategoryID number,
        v_chapter_code varchar2) is

        cursor c is
            select c.*, fcd.short_desc F_SHORT_DESC, fcd.long_desc F_LONG_DESC, fcd.user_desc F_USER_DESC,
                cv.facility_type_code, cv.icd_validation_id,
                cv1.facility_type_code AFTC, cv1.icd_validation_id ACVID
            from icd.category c
            LEFT OUTER join icd.french_category_desc fcd on c.category_id = fcd.category_id
            LEFT OUTER JOIN icd.category_validation cv on c.category_id = cv.category_id and cv.facility_type_code = '1'
            LEFT OUTER JOIN icd.category_validation cv1 on c.category_id = cv1.category_id and cv1.facility_type_code = 'A'
            where c.parent_category_id = parentCategoryID
            and TRIM(c.clinical_classification_code) = '10CA' || p_version_code
            and c.category_code not like '%.%'
            order by c.category_code;

        rec_cc c%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';

        categoryID number := 0;
        v_short_title varchar2(255);
        v_long_title varchar2(255);
        v_user_title varchar2(255);
        v_short_title_fr varchar2(255);
        v_long_title_fr varchar2(255);
        v_user_title_fr varchar2(255);
        v_code VARCHAR2(12);
        v_ca_enhancement_flag VARCHAR2(1);
        v_dagger_asterisk VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);
        v_status_code VARCHAR(1);
        status_code VARCHAR(10);
        domainValueClassID number := 0;
        domainValueElementID number := 0;
        categoryTypeCode VARCHAR2(10);
        nodeType VARCHAR2(10);

        nodeClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;

        --Validation Variables
        v_facilityType varchar2(1);
        v_facilityTypeA varchar2(1);
        v_icdValidationID number;
        v_icdValidationIDA number;
    begin

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
            v_ca_enhancement_flag := TRIM(rec_cc.ca_enhancement_flag);
            v_dagger_asterisk := TRIM(rec_cc.dagger_asterisk);
            v_code_flag := TRIM(rec_cc.code_flag);
            v_render_child_flag := TRIM(rec_cc.render_children_as_table_flag);
            v_status_code := TRIM(rec_cc.status_code);
            v_facilityType := TRIM(rec_cc.facility_type_code);
            v_facilityTypeA := TRIM(rec_cc.aftc);
            v_icdValidationID := TRIM(rec_cc.icd_validation_id);
            v_icdValidationIDA := TRIM(rec_cc.acvid);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            IF TRIM(categoryTypeCode) = 'BL1' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
                --insertLog('  - Migrating Block ' || v_code);
            ELSIF TRIM(categoryTypeCode) = 'BL2' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
                --insertLog('  - Migrating Block 2 ' || v_code);
            ELSIF TRIM(categoryTypeCode) = 'BL3' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
                --insertLog('  - Migrating Block 3 ' || v_code);
            ELSIF TRIM(categoryTypeCode) = 'CAT1' THEN

                IF TRIM(v_chapter_code) = '22' THEN
                    nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
                    nodeType := 'BLOCK';
                    insertLog('  - Chapter 22 special case.  Converting CAT1 to BLOCK: ' || categoryID || ': ' || v_code);

                    v_user_title := v_code || ' ' || v_user_title;
                    v_user_title_fr := v_code || ' ' || v_user_title_fr;
                    insertLog('      - Converted Long Title to: : ' || v_long_title);
                ELSE
                    nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
                    nodeType := 'CATEGORY';
                END IF;
            ELSIF TRIM(categoryTypeCode) = 'CAT2' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CAT3' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CODE' THEN
                nodeClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CODE';
            ELSE
                insertLog('Category type code is not right: ' || categoryTypeCode);
                raise_application_error(-20011, 'Error occurred in migrateChildNodes.');
            END IF;

            businessKey := generateConceptBusinessKey(icd_classification_code, nodeClassID, CIMS_ICD.formatXREFCode(v_code)) ;
            elementID := insertConcept(p_version_code, nodeClassID, businessKey, structureVersionID, status_code);

            INSERT INTO Z_ICD_TEMP (A, B, E, F)
            VALUES (categoryID, elementID, v_code, 'ICD');

            -- Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, categoryID, elementID, structureVersionID);

            -- Table Output
            migrateTableOutput(p_version_code, categoryID, elementID, structureVersionID);

            --Store Short title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_short_title, 'ENG');

            --Store Long title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_long_title, 'ENG');

            --Store User title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_user_title, 'ENG');

            --Store Chapters Short title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title_fr,''), 'FRA');

            --Store Chapters Long title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title_fr,''), 'FRA');

            --Store Chapters User title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title_fr,''), 'FRA');

            --Store Code value (Category Code in ICD)
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code, null);

            --Store CA Enhancement flag
            propertyClassID := CIMS_ICD.getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_ca_enhancement_flag,'N'));

            IF ( LENGTH(trim(v_dagger_asterisk)) ) > 0 THEN
                --Store Dagger Asterisk
                domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DaggerAsterisk');
                domainValueElementID := da(v_dagger_asterisk);

                --Build a relationship to that DA now
                propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'DaggerAsteriskIndicator');
                buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);
            end if;

            --Only insert validations for CODES
            IF (TRIM(UPPER(v_code_flag)) = 'Y') THEN
                --Validation Facility Type 1
                if (v_facilityType is not null) then
                    insertValidation(p_version_code, elementID, structureVersionID, v_facilityType, v_icdValidationID);

                end if;

                --Validation Facility Type A
                if (v_facilityTypeA is not null) then
                    insertValidation(p_version_code, elementID, structureVersionID, v_facilityTypeA, v_icdValidationIDA);
                end if;
            END IF;

            --Build Narrow Relationship
            relationshipClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, parentElementID, structureVersionID);

            --Recursively call
            migrateChildNodes(p_version_code, structureVersionID, elementID, categoryID, v_chapter_code);

        end loop;

    exception
        when others then
            insertLog('migrateChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateChildNodes. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateChapterNodes
    * DESCRIPTION:   Migrates the chapters
    **************************************************************************************************************************************/
    procedure migrateChapterNodes(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c_chapter is
            select c.*, fcd.short_desc F_SHORT_DESC, fcd.long_desc F_LONG_DESC, fcd.user_desc F_USER_DESC,
                cv.facility_type_code, cv.icd_validation_id,
                cv1.facility_type_code AFTC, cv1.icd_validation_id ACVID
            from icd.category c
            LEFT OUTER join icd.french_category_desc fcd on c.category_id = fcd.category_id
            LEFT OUTER JOIN icd.category_validation cv on c.category_id = cv.category_id and cv.facility_type_code = '1'
            LEFT OUTER JOIN icd.category_validation cv1 on c.category_id = cv1.category_id and cv1.facility_type_code = 'A'
            where c.category_type_code = 'CHP'
            and TRIM(c.clinical_classification_code) = '10CA' || p_version_code
            and c.category_code not like '%.%'
            order by c.category_code;

        rec_cc c_chapter%rowtype;
        elementID number := 0;
        businessKey varchar2(100) := '';

        chapter_category_ID number := 0;
        v_chapter_short_title varchar2(255);
        v_chapter_long_title varchar2(255);
        v_chapter_user_title varchar2(255);
        v_chapter_short_title_fr varchar2(255);
        v_chapter_long_title_fr varchar2(255);
        v_chapter_user_title_fr varchar2(255);
        v_chapter_code VARCHAR2(12);
        v_ca_enhancement_flag VARCHAR2(1);
        v_dagger_asterisk VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);
        v_status_code VARCHAR(1);
        status_code VARCHAR(10);
        domainValueClassID number := 0;
        domainValueElementID number := 0;
        chapterClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;

        --Validation Variables
        v_facilityType varchar2(1);
        v_facilityTypeA varchar2(1);
        v_icdValidationID number;
        v_icdValidationIDA number;
    begin
        chapterClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');

        for rec_cc in c_chapter loop
            chapter_category_ID := TRIM(rec_cc.category_id);
            v_chapter_short_title := TRIM(rec_cc.short_desc);
            v_chapter_long_title := TRIM(rec_cc.long_desc);
            v_chapter_user_title := TRIM(rec_cc.user_desc);
            v_chapter_short_title_fr := TRIM(rec_cc.f_short_desc);
            v_chapter_long_title_fr := TRIM(rec_cc.f_long_desc);
            v_chapter_user_title_fr := TRIM(rec_cc.f_user_desc);
            v_chapter_code := TRIM(rec_cc.category_code);
            v_ca_enhancement_flag := TRIM(rec_cc.ca_enhancement_flag);
            v_dagger_asterisk := TRIM(rec_cc.dagger_asterisk);
            v_code_flag := TRIM(rec_cc.code_flag);
            v_render_child_flag := TRIM(rec_cc.render_children_as_table_flag);
            v_status_code := TRIM(rec_cc.status_code);
            v_facilityType := TRIM(rec_cc.facility_type_code);
            v_facilityTypeA := TRIM(rec_cc.aftc);
            v_icdValidationID := TRIM(rec_cc.icd_validation_id);
            v_icdValidationIDA := TRIM(rec_cc.acvid);

            businessKey := generateConceptBusinessKey(icd_classification_code, chapterClassID, v_chapter_code);

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            insertLog('Migrating chapter ' || v_chapter_code);

            -- Chapter
            elementID := insertConcept(p_version_code, chapterClassID, businessKey, structureVersionID, status_code);

            INSERT INTO Z_ICD_TEMP (A, B, E, F)
            VALUES (chapter_category_ID, elementID, v_chapter_code, 'ICD');

            -- Chapter Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, chapter_category_ID, elementID, structureVersionID);

            -- Chapter Table Output
            migrateTableOutput(p_version_code, chapter_category_ID, elementID, structureVersionID);

            --Store Chapters Short title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_short_title, 'ENG');

            --Store Chapters Long title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_long_title, 'ENG');

            --Store Chapters User title English
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_user_title, 'ENG');

            --Store Chapters Short title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_short_title_fr,''), 'FRA');

            --Store Chapters Long title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_long_title_fr,''), 'FRA');

            --Store Chapters User title French
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_user_title_fr,''), 'FRA');

            --Store Chapters Code value (Category Code in ICD)
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_code, null);

            --Store Chapters CA Enhancement flag
            propertyClassID := CIMS_ICD.getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_ca_enhancement_flag, 'N'));

            IF ( LENGTH(trim(v_dagger_asterisk)) ) > 0 THEN
                --Store Chapters Dagger Asterisk
                domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DaggerAsterisk');
                domainValueElementID := da(v_dagger_asterisk);

                --Build a relationship to that DA now
                propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'DaggerAsteriskIndicator');
                buildNarrowRelationship(p_version_code, propertyClassID, elementID, domainValueElementID, structureVersionID);
            end if;

            --Only insert validations for CODES
            IF (TRIM(UPPER(v_code_flag)) = 'Y') THEN
                --Validation Facility Type 1
                if (v_facilityType is not null) then
                    insertValidation(p_version_code, elementID, structureVersionID, v_facilityType, v_icdValidationID);
                end if;

                --Validation Facility Type A
                if (v_facilityTypeA is not null) then
                    insertValidation(p_version_code, elementID, structureVersionID, v_facilityTypeA, v_icdValidationIDA);
                end if;
            END IF;

            --Build Narrow Relationship
            relationshipClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID, structureVersionID);

            migrateChildNodes(p_version_code, structureVersionID, elementID, chapter_category_ID, v_chapter_code);

        end loop;

    exception
        when others then
            insertLog('migrateChapterNodes ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateChapterNodes. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateChapterNodes;


    /**************************************************************************************************************************************
    * NAME:          replaceGraphicPropertyEng
    * DESCRIPTION:   In the old system, the Supplement XML contains a graphic property.  In CIMS, it needs to be replaced with a graphic
    *                filename.
    **************************************************************************************************************************************/
    FUNCTION replaceGraphicPropertyEng(origXML clob)
        RETURN CLOB
    IS
        tempXML clob := origXML;

    begin
        tempXML := REPLACE(tempXML, 'FIGURE.1', 'E_figure1icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.2', 'E_fig2_ICD.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.3', 'E_fig3_ICD.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.4', 'E_fig4icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.5', 'E_fig5icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.6', 'E_fig6icd.gif');

        return tempXML;

    end replaceGraphicPropertyEng;


    /**************************************************************************************************************************************
    * NAME:          replaceGraphicPropertyFra
    * DESCRIPTION:   In the old system, the Supplement XML contains a graphic property.  In CIMS, it needs to be replaced with a graphic
    *                filename.
    **************************************************************************************************************************************/
    FUNCTION replaceGraphicPropertyFra(origXML clob)
        RETURN CLOB
    IS
        tempXML clob := origXML;

    begin
        tempXML := REPLACE(tempXML, 'FIGURE.1', 'F_figure1icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.2', 'F_fig2_ICD.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.3', 'F_fig3_ICD.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.4', 'f_fig4icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.5', 'F_fig5icd.gif');
        tempXML := REPLACE(tempXML, 'GRAPH.6', 'F_fig6icd.gif');

        return tempXML;

    end replaceGraphicPropertyFra;


    /**************************************************************************************************************************************
    * NAME:          insertSupplement
    * DESCRIPTION:   Insert Supplement
    **************************************************************************************************************************************/
    procedure insertSupplement(p_version_code varchar2, structureVersionID number, viewerRootElementID number,
        suppDescription varchar2, suppType varchar2, xmlFilename varchar2, sortingHint number, lang varchar2) IS

        text_data clob;
        suppDefinition clob;
        elementID number;
        supplementClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Supplement');
        propertyClassID number := 0;
    begin

        select CIMS_ICD.clobfromblob(TEXT_DATA)
        into text_data
        from icd.text
        where file_name = xmlFilename
        and clinical_classification_code = '10CA' || p_version_code
        and language_code = lang;

        elementID := insertConcept(p_version_code, supplementClassID, null, structureVersionID, 'ACTIVE');

        if (lang = 'ENG') then
            suppDefinition := replaceGraphicPropertyEng(text_data);
        else
            suppDefinition := replaceGraphicPropertyFra(text_data);
        end if;

        --Store Supplement Description
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'SupplementDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, suppDescription, lang);

        --Store Supplement Definition
        propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'SupplementDefinition');
        insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, suppDefinition, lang);

        -- Store Sorting Hint
        propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'SortingHint');
        insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, sortingHint);

        -- Store Supplement Type Indicator
        propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'SupplementTypeIndicator');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, supplementType(suppType), structureVersionID);

        --Build Narrow Relationship
        propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, viewerRootElementID, structureVersionID);

    exception
        when others then
            insertLog('insertSupplement ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertSupplement. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertSupplement;


    /**************************************************************************************************************************************
    * NAME:          insertSupplement
    * DESCRIPTION:   Insert Supplement
    **************************************************************************************************************************************/
    /*procedure insertSupplement(p_version_code varchar2, structureVersionID number, viewerRootElementID number,
        descEng varchar2, descFra varchar2, suppType varchar2, xmlFilenameEng varchar2, xmlFilenameFra varchar2, sortingHint number) IS

        text_data_eng clob;
        text_data_fra clob;
        elementID number;
        supplementClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Supplement');
        propertyClassID number := 0;
    begin

        SELECT
            (select CIMS_ICD.clobfromblob(TEXT_DATA) from icd.text
            where file_name = xmlFilenameEng
            and clinical_classification_code = '10CA' || p_version_code
            and language_code = 'ENG') t_data_eng,

            (select CIMS_ICD.clobfromblob(TEXT_DATA) from icd.text
            where file_name = xmlFilenameFra
            and clinical_classification_code = '10CA' || p_version_code
            and language_code = 'FRA') t_data_fra
        INTO text_data_eng, text_data_fra
        FROM dual;

        elementID := insertConcept(p_version_code, supplementClassID, null, structureVersionID, 'ACTIVE');

        --Store Supplement Description English
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'SupplementDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, descEng, 'ENG');

        --Store Supplement Description French
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'SupplementDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, descFra, 'FRA');

        --Store Supplement Definition English
        propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'SupplementDefinition');
        insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, replaceGraphicPropertyEng(text_data_eng), 'ENG');

        --Store Supplement Definition French
        propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'SupplementDefinition');
        insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, replaceGraphicPropertyFra(text_data_fra), 'FRA');

        -- Store Sorting Hint
        propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'SortingHint');
        insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, sortingHint);

        -- Store Supplement Type Indicator
        propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'SupplementTypeIndicator');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, supplementType(suppType), structureVersionID);

        --Build Narrow Relationship
        propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        buildNarrowRelationship(p_version_code, propertyClassID, elementID, viewerRootElementID, structureVersionID);

    exception
        when others then
            insertLog('insertSupplement ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertSupplement. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertSupplement;
    */

    /**************************************************************************************************************************************
    * NAME:          migrateSupplements
    * DESCRIPTION:   Migrates Supplements
    **************************************************************************************************************************************/
    procedure migrateSupplements(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is

    begin
        -- Front Matter English
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Table of Contents',
            'F', 'icd_toc.xml', 1000, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'ICD-10-CA/CCI, Version ' || p_version_code || ' Licence Agreement',
            'F', 'icd_licence.xml', 1100, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'About the Canadian Institute for Health Information (CIHI)',
            'F', 'icd_about.xml', 1200, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Contact Us for More Information About ICD-10-CA and CCI',
            'F', 'icd_contact.xml', 1300, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Preface',
            'F', 'icd_preface.xml', 1400, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Acknowledgments',
            'F', 'icd_acknowl.xml', 1500, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Introduction',
            'F', 'icd_intro.xml', 1600, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Conventions used in the Tabular List of Diseases',
            'F', 'icd_conventab.xml', 1700, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Basic Coding Guidelines',
            'F', 'icd_codeguide.xml', 1800, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'WHO Collaborating Centres for the Family of International Classifications',
            'F', 'icd_who.xml', 1900, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Report of the International Conference for the Tenth Revision of the International Classification of Diseases',
            'F', 'icd_report.xml', 2000, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Maintenance and Development of ICD-10-CA Enhancements',
            'F', 'icd_development.xml', 2100, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Conventions used in the Alphabetical Index',
            'F', 'icd_convenalph.xml', 2200, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Diagrams in ICD-10-CA',
            'F', 'icd_diagrams_eng.xml', 2300, 'ENG');

        -- Front Matter French
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Table des matières',
            'F', 'icd_toc_fra.xml', 6000, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Accord de licence de la CIM-10-CA/CCI',
            'F', 'icd_licence.xml', 6100, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'À propos de l''Institut canadien d''information sur la santé (ICIS)',
            'F', 'icd_about.xml', 6200, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Pour plus d''informations concernant la CIM-10-CA et la CCI, n''hésitez pas à nous contacter',
            'F', 'icd_contact.xml', 6300, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Préface',
            'F', 'icd_preface.xml', 6400, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Remerciements',
            'F', 'icd_acknowl.xml', 6500, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Introduction',
            'F', 'icd_intro.xml', 6600, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Conventions utilisées dans las table analytique des maladies',
            'F', 'icd_conventab.xml', 6700, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Directives de base concernant la codification',
            'F', 'icd_codeguide.xml', 6800, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Centres collaborateurs de l''OMS pour la famille des classifications internationales',
            'F', 'icd_who.xml', 6900, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Rapport de la conférence internationale pour la dixième version de la classification internationale des maladies',
            'F', 'icd_report.xml', 7000, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Développement de la CIM-10-CA',
            'F', 'icd_development.xml', 7100, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Conventions utilisées dans l''index alphabétique',
            'F', 'icd_convenalph.xml', 7200, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Schémas de la CIM-10-CA',
            'F', 'icd_diagrams_fra.xml', 7300, 'FRA');

        -- Back Matter English
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Special tabulation lists for mortality and morbidity',
            'B', 'icd_spec_tbl_mortality_morbidity.xml', 2400, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Appendix A - New ICD-10-CA Codes',
            'B', 'icd_appendix_a_new_codes.xml', 2500, 'ENG');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Appendix B - Disabled ICD-10-CA Codes',
            'B', 'icd_appendix_b_disabled_codes.xml', 2600, 'ENG');

        -- Back Matter French
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Listes spéciales concernant les données de mortalité et de morbidité',
            'B', 'icd_spec_tbl_mortality_morbidity.xml', 7400, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Annexe A - Nouveaux Codes CIM-10-CA',
            'B', 'icd_french_appendix_a_new_codes.xml', 7500, 'FRA');
        insertSupplement(p_version_code, structureVersionID, viewerRootElementID,
            'Annexe B - Codes Désactivés CIM-10-CA',
            'B', 'icd_french_appendix_b_disabled_codes.xml', 7600, 'FRA');
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
        diagramClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Diagram');
        propertyClassID number := 0;
    begin

        select Graphic_desc, Graphic_data
        into diagDescription, diag
        from icd.graphic
        where file_name = diagramFilename
        and language_code = lang
        and clinical_classification_code = '10CA' || p_version_code;

        elementID := insertConcept(p_version_code, diagramClassID, null, structureVersionID, 'ACTIVE');

        --Store Diagram Description
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramDescription');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, diagDescription, null);

        --Store Diagram Filename
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DiagramFileName');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, diagramFilename, null);

        -- Store Diagram Graphic
        propertyClassID := CIMS_ICD.getICD10CAClassID('GraphicsPropertyVersion', 'DiagramFigure');
        insertGraphicProperty(p_version_code, elementID, propertyClassID, structureVersionID, diag, null);

        --Build Narrow Relationship
        propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
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

        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_figure1icd.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig2_ICD.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig3_ICD.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig4icd.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig5icd.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_fig6icd.gif', 'ENG');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'E_brac3.gif', 'ENG');

        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_figure1icd.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig2_ICD.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig3_ICD.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'f_fig4icd.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig5icd.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_fig6icd.gif', 'FRA');
        insertDiagram(p_version_code, structureVersionID, viewerRootElementID, 'F_brac3.gif', 'FRA');

    exception
        when others then
            insertLog('migrateDiagrams ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateDiagrams. <br> Error:' || substr(sqlerrm, 1, 512));
    end migrateDiagrams;


    /**************************************************************************************************************************************
    * NAME:          init_BaseClassification
    * DESCRIPTION:   Initializes the base classification for ICD-10-CA
    **************************************************************************************************************************************/
    procedure init_BaseClassification(p_version_code varchar2, structureVersionID OUT number) is

        classID number := CIMS_ICD.getICD10CAClassID('BaseClassification', 'ICD-10-CA');
        elementID number := 0;
        elementVersionID number := 0;
        businessKey varchar2(100) := generateConceptBusinessKey(icd_classification_code, classID, null);

    begin
        elementID := elementid_SEQ.nextval;
        elementVersionID := elementversionid_SEQ.nextval;
        structureVersionID := elementVersionID;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES)
        values (elementID, classID, businessKey, null);

        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES, CLASSID, CHANGEDFROMVERSIONID, ORIGINATINGCONTEXTID)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null, classID, null, structureVersionID);

        insert into STRUCTUREVERSION (STRUCTUREID, CLASSID, STATUS, ELEMENTID, BASESTRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, ISVERSIONYEAR)
        values (elementVersionID, classID, 'ACTIVE', elementID, null, 'OPEN', sysdate, 'Y');

    end init_BaseClassification;


    /**************************************************************************************************************************************
    * NAME:          createClassificationRoot
    * DESCRIPTION:   Creates ICD-10-CA Root node
    **************************************************************************************************************************************/
    procedure createClassificationRoot(p_version_code varchar2, structureVersionID number, viewerRootID out number) is
        classID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ClassificationRoot');
        elementID number := 0;
        propertyClassID number := 0;
        v_title_eng varchar2(255);
        v_title_fra varchar2(255);
        status_code varchar2(10) := 'ACTIVE';
        businessKey varchar2(100);

    begin
        v_title_eng := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED ' ||
                       'HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' ||
                       p_version_code;

        v_title_fra := 'CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ ' ||
                       'CONNEXES DIXIÈME VERSION, CANADA [CIM-10-CA] ' ||
                       p_version_code;

        businessKey := generateConceptBusinessKey(icd_classification_code, classID, 'ViewerRoot');
        elementID := insertConcept(p_version_code, classID, businessKey, structureVersionID, status_code);
        viewerRootID := elementID;

        --Store Short title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store Long title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store User title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_eng, 'ENG');

        --Store Short title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

        --Store Long title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

        --Store User title
        propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_title_fra, 'FRA');

    end createClassificationRoot;


    /**************************************************************************************************************************************
    * NAME:          cleanUp_LookupTables
    * DESCRIPTION:   Clean up procedure to clean the CLASS, LANGUAGE, and the dagger asterisk tables
    *                Ensure any referencing records are deleted first.
    **************************************************************************************************************************************/
    procedure cleanUp_LookupTables is

    begin
        delete from CLASS where BASECLASSIFICATIONNAME = icd_classification_code;
        --delete from LANGUAGE;

        commit;

    end cleanUp_LookupTables;


    /**************************************************************************************************************************************
    * NAME:          populate_DomainValues
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure populate_DomainValues(version_code varchar2, structureVersionID number) is
        domainValueClassID number;
        dvElementID number;
    begin
        insertLog('  - Dagger Asterisk');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DaggerAsterisk');
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, '+', null, null, null, null, null, null, null, domainValueClassID);
        da('+') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, '*', null, null, null, null, null, null, null, domainValueClassID);
        da('*') := dvElementID;

        insertLog('  - Sex Validation');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'SexValidation');
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'A', null, 'Male, Female & Other', null, null,
            'Homme, Femme & Autre', null, null, domainValueClassID);
        sv('A') := dvElementID;
        svDESCEng('A') := 'Male, Female & Other';
        svDESCFra('A') := 'Homme, Femme & Autre';

        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'G', null, 'Female & Other', null, null,
            'Femme & Autre', null, null, domainValueClassID);
        sv('G') := dvElementID;
        svDESCEng('G') := 'Female & Other';
        svDESCFra('G') := 'Femme & Autre';

        sv('F') := dvElementID;
        svDESCEng('F') := 'Female & Other';
        svDESCFra('F') := 'Femme & Autre';

        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'N', null, 'Male & Other', null, null,
            'Homme & Autre', null, null, domainValueClassID);
        sv('N') := dvElementID;
        svDESCEng('N') := 'Male & Other';
        svDESCFra('N') := 'Homme & Autre';

        sv('M') := dvElementID;
        svDESCEng('M') := 'Male & Other';
        svDESCFra('M') := 'Homme & Autre';

        insertLog('  - Facility Type');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType');
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'A', null, 'NACRS - Ambulatory Care', null, null,
            'SNISA - Soins ambulatoires', null, null, domainValueClassID);
        facilityType('A') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, '1', null, 'DAD - Acute Care', null, null,
            'BDCP - Soins de courte durée', null, null, domainValueClassID);
        facilityType('1') := dvElementID;

        insertLog('  - Supplement Type');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'SupplementType');
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'F', null, 'Front Matter', null, null,
            'Front Matter', null, null, domainValueClassID);
        supplementType('F') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'B', null, 'Back Matter', null, null,
            'Back Matter', null, null, domainValueClassID);
        supplementType('B') := dvElementID;
        dvElementID := populateDomainValueLookup(version_Code, structureVersionID, 'O', null, 'Other', null, null,
            'Other', null, null, domainValueClassID);
        supplementType('O') := dvElementID;

    end populate_DomainValues;


    /**************************************************************************************************************************************
    * NAME:          populate_lookUp
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure populate_lookUp is
        languageCount number;
    begin
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'ClassificationRoot', null, 'Classification Root');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BaseClassification', icd_classification_code, 'ICD-10-CA', null, 'ICD-10-CA');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Chapter', null, 'Chapter');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Block', null, 'Block');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Category', null, 'Category');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'Code', null, 'Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'ShortTitle', null, 'Title Short');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'LongTitle', null, 'Title Long');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'UserTitle', null, 'Title User');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'Narrower', null, 'Narrower Relationship');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', icd_classification_code, 'CaEnhancementIndicator', null, 'Canadian Enhancement Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'GraphicsPropertyVersion', icd_classification_code, 'Diagram', null, 'Diagram');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DiagramFileName', null, 'Diagram File Name');

        --XML Presentation
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'IncludePresentation', null, 'Directive Includes');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'ExcludePresentation', null, 'Directive Excludes');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'CodeAlsoPresentation', null, 'Directive Code Also');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'NotePresentation', null, 'Directive Note');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'DefinitionPresentation', null, 'Directive Definition');

        --HTML Presentation
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'TablePresentation', null, 'Table ICD');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'LongPresentation', null, 'Long Presentation');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'ShortPresentation', null, 'Short Presentation');

        --Domain Value - Dagger Asterisk
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'DaggerAsterisk', null, 'Dagger Asterisk Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'DaggerAsteriskIndicator', null, 'Dagger Asterisk Indicator');

        --Domain Values - Sex Validation
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'SexValidation', null, 'Sex Validation');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'SexValidationIndicator', null, 'Sex Validation Indicator');

        --Domain Values - Facility Type
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'FacilityType', null, 'Data Holding');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'FacilityTypeIndicator', null, 'Data Holding Indicator');

        --Domain Value - Common Properties
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DomainValueCode', null, 'Domain Value Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DomainValueDescription', null, 'Domain Value Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DomainValueLabel', null, 'Domain Value Label');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DomainValueDefinition', null, 'Domain Value Definition');

        --Validation Rules
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'ValidationICD', null, 'Validation');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'ValidationDefinition', null, 'Validation Definition');

        --Validation - Category Relationships
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'ValidationICDCPV', null, 'Validation Concept Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'ValidationFacility', null, 'Validation Data Holding Indicator');

        --Index related Classes
        --A	Alphabetic Index to Diseases and Nature of Injury
        --E	External Causes of Injury Index

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'LetterIndex', null, 'Letter Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'BookIndex', null, 'Book Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'AlphabeticIndex', null, 'Alphabetic Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'ExternalInjuryIndex', null, 'External Injury Index');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'ReferenceIndex', null, 'Reference Index');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'IndexCode', null, 'Index Code');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'IndexDesc', null, 'Index Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'RefLinkDesc', null, 'Reference Link Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'IndexNoteDesc', null, 'Index Note Description');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'IndexReference', null, 'Index Reference Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'IndexReferredTo', null, 'Index Referred To Indicator');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'NumericPropertyVersion', icd_classification_code, 'Level', null, 'Index Level');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'IndexRefDefinition', null, 'Index Reference Definition');

        --Index See Also Flag
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'SeeAlso', null, 'See / See Also');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'SeeAlsoFlagIndicator', null, 'See / See Also Indidcator');

        --Index Site Indicator Flag
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'SiteIndicator', null, 'Site');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'SiteIndicatorFlagIndicator', null, 'Site Indicator');

        --Category Reference Index
        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'CategoryReferenceIndex', null, 'Category Reference');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'CategoryReferenceIndexCPV', null, 'Category Reference Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'MainCodeCPV', null, 'Main Code Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PairedCodeCPV', null, 'Paired Code Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'MainCodeDaggerAsteriskIndicator', null, 'Main Code Dagger Asterisk Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PairedCodeCPVAsteriskIndicator', null, 'Paired Code Dagger Asterisk Indicator');

        --Neoplasm index
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'NeoplasmIndex', null, 'Neoplasm Index');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'BenignCPV', null, 'Benign Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'InSituCPV', null, 'InSitu Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'MalignantPriCPV', null, 'Malignant Primary Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'MalignantSecCPV', null, 'Malignant Secondary Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'UnknownBehaviourCPV', null, 'Unknown Behaviour Indicator');

        --Drugs and Chemicals Index
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'DrugsAndChemicalsIndex', null, 'Drugs and Chemcials Index');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'AdverseEffectCPV', null, 'Adverse Effect Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PoisoningAccidentalCPV', null, 'Poisioning Accidental Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PoisoningXIXCPV', null, 'Poisoning XIX Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PoisoningIntentionalCPV', null, 'Poisoning Intentional Indicator');

        --insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        --values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'PoisoningUndeterminedCPV', null, 'Poisining Undetermined Indicator');

        --Change Request related
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', icd_classification_code, 'RequestTouched', null, 'Modified Flag');

        -- Supplement related
        -- Supplements
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Supplement', null, 'Supplement');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'SupplementDefinition', null, 'Supplement Definition');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'SupplementDescription', null, 'Supplement Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'NumericPropertyVersion', icd_classification_code, 'SortingHint', null, 'Sorting Hint');

        -- Domain value - Supplement Type
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'SupplementType', null, 'Supplement Type');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'SupplementTypeIndicator', null, 'Supplement Type Indicator');

        -- Diagram Concept
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Diagram', null, 'Diagram');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'DiagramDescription', null, 'Diagram Description');

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES, FRIENDLYNAME)
        values (CLASSID_SEQ.Nextval, 'GraphicsPropertyVersion', icd_classification_code, 'DiagramFigure', null, 'Diagram Figure');


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
    * NAME:          icd_data_migration_cleanup
    * DESCRIPTION:   WIP procedure to clean up lookup tables.  Should not be run everytime
    *                Procedure is currently not public.
    *                Will not run successfully if there are referencing records
    **************************************************************************************************************************************/
    PROCEDURE icd_data_migration_cleanup(version_Code varchar2) is
        runStatus varchar2(10) := CIMS_ICD.checkRunStatus;
    BEGIN
        f_year := version_Code;
        dbms_output.enable(1000000);

        IF runStatus = 'FALSE' THEN
            dbms_output.put_line('Script already running....');
            RETURN;
        END IF;

        insertLog('Cleaning up lookup tables for ICD10CA ' || version_code);

        insertLog('Clean up tables to begin migration');
        cleanUp_LookupTables;
        insertLog('Ending clean up tables');

        insertLog('Population of lookup tables');
        populate_lookUp;
        insertLog('Ending population of lookup tables');

        insertLog('Ending of cleanup tables for ICD10CA ' || version_code);

    END icd_data_migration_cleanup;


    /**************************************************************************************************************************************
    * NAME:          icd_data_migration
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/
    PROCEDURE icd_data_migration(version_Code varchar2) is
        structureVersionID number := 0;
        viewerRootElementID number := 0;
        logRunID number := 0;
        runStatus varchar2(10) := CIMS_ICD.checkRunStatus;

    BEGIN
        f_year := version_Code;
        dbms_output.enable(1000000);

        IF runStatus = 'FALSE' THEN
            dbms_output.put_line('Script already running....');
            RETURN;
        END IF;

        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('Starting ICD10CA migration ' || version_code || '.  Migration Run ID: ' || logRunID);

        insertLog('Creating Base Classification');
        init_BaseClassification(version_Code, structureVersionID);

        insertLog('Populating Domain Value tables');
        populate_DomainValues(version_Code, structureVersionID);

        insertLog('Creating Classification Root');
        createClassificationRoot(version_Code, structureVersionID, viewerRootElementID);

        insertLog('-- Main migration --');
        migrateChapterNodes(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending main migration --');

        insertLog('-- Migrating Supplements --');
        migrateSupplements(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending Migrating Supplements --');

        insertLog('-- Migrating Diagrams --');
        migrateDiagrams(version_Code, structureVersionID, viewerRootElementID);
        insertLog('-- Ending Migrating Diagrams --');

        CIMS_ICD.GATHER_SCHEMA_STATS;

        --Do not do this.  Only do after we migrate the indexes!
        --insertLog('-- Code Update --');
        --CIMS_ICD.UpdateCode;
        --CIMS_ICD.UpdateCodeInClob;

        insertLog('Ending ICD10CA migration ' || version_code);

        commit;

    END icd_data_migration;


end ICD_DATA_MIGRATION;
/
