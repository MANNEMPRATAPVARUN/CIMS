create or replace package ICD_DATA_MIGRATION is

    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;
    errString varchar(4000);
    --function getICD10CAClassID(tblName varchar2, cName varchar2) return number;
    PROCEDURE cleanUp_LookupTables;
    PROCEDURE populate_lookUp;
    PROCEDURE icd_data_migration_cleanup(version_Code number);
    function checkRunStatus(p_version_code varchar2) return varchar2;
    PROCEDURE icd_data_migration(version_Code IN number);

end ICD_DATA_MIGRATION;
/
create or replace package body ICD_DATA_MIGRATION is


    /**************************************************************************************************************************************
    * NAME:          checkRunStatus
    * DESCRIPTION:   Checks the log table to see if it has completed successfully.
    *                If it has not, then it is assumed that it is either currently running or has not completed successfully.
    *                It will do a second check to see if the run date is less than 30 minutes from the current time.
    *                Returns true if we will allow the migration script to run   
    **************************************************************************************************************************************/ 
    function checkRunStatus(p_version_code varchar2) return varchar2 is
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
        where run_id = (select max(run_id) from log where classification = icd_classification_code and fiscal_year = p_version_code)
        and message like 'Ending ICD10CA migration%'
        order by id; 

        if (notRunning > 0) then
            return 'TRUE';
        end if;

        select count(*)
        into notRunning  
        from log
        where run_id = (select max(run_id) from log where classification = icd_classification_code and fiscal_year = p_version_code)
        and messagedate < (SYSDATE - INTERVAL '30' MINUTE);
        --and messagedate < (SYSDATE - INTERVAL '2' HOUR)

        if (notRunning > 0) then
            return 'TRUE';
        else 
            return 'FALSE';
        end if;


    end checkRunStatus;


    /**************************************************************************************************************************************
    * NAME:          insertLog
    * DESCRIPTION:   Write to the log table 
    **************************************************************************************************************************************/ 
    procedure insertLog(message varchar2) is
        logDate date;
        logID number := 0;
        logRunID number := 0;
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
    * NAME:          getICD10CAClassID
    * DESCRIPTION:   Convenience function to retrieve the class ID
    **************************************************************************************************************************************/    
    function getICD10CAClassID(tblName varchar2, cName varchar2) return number is
        classID number;
    begin
        SELECT c.CLASSID 
        INTO classID 
        FROM CLASS c 
        WHERE UPPER(TRIM(c.TABLENAME)) = UPPER(TRIM(tblName))
        AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName))
        AND UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM(icd_classification_code));

        return classID;
    end getICD10CAClassID;


    /**************************************************************************************************************************************
    * NAME:          getDaggerAsteriskDomainID
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    function getDaggerAsteriskDomainID(propertyClassID number) return number is
        domainID number;
    begin
        select vd.domainID
        INTO domainID
        from element e 
        join elementversion ev on e.elementid = ev.elementid
        join valuedomainVERSION vd on ev.elementversionid = vd.domainid
        where e.classid = propertyClassID;

        return domainID;
    end getDaggerAsteriskDomainID;


    /**************************************************************************************************************************************
    * NAME:          getEnumerationID
    * DESCRIPTION:   Retrieve and return the domain value ID 
    **************************************************************************************************************************************/    
    procedure getEnumerationID(litValue varchar2, did number, dvid OUT number) is

    begin        
        if litValue is null then
            SELECT e.domainvalueid
            INTO dvid 
            FROM ENUMERATION e 
            WHERE e.literalvalue is null
            and e.domainid = did;
        else
            SELECT e.domainvalueid
            INTO dvid 
            FROM ENUMERATION e 
            WHERE TRIM(litValue) = e.literalvalue
            and e.domainid = did;
        end if;
        
    end getEnumerationID;


    /**************************************************************************************************************************************
    * NAME:          insertHTMLProperty
    * DESCRIPTION:    
    **************************************************************************************************************************************/ 
    procedure insertHTMLProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, null);
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into HTMLPROPERTYVERSION (HTMLPROPERTYID, LANGUAGECODE, HTMLTEXT)
        values (elementVersionID, SUBSTR(language_code, 0, 3), xmlData);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insertLog('Error occured in insertHTMLProperty procedure');
            insertLog(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertHTMLProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertHTMLProperty;


    /**************************************************************************************************************************************
    * NAME:          insertXMLProperty
    * DESCRIPTION:    
    **************************************************************************************************************************************/ 
    procedure insertXMLProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              xmlData clob, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, null);
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into XMLPROPERTYVERSION (Xmlpropertyid, Languagecode, Xmlschemaurl, Xmltext)
        values (elementVersionID, SUBSTR(language_code, 0, 3), 'XML', xmlData);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insertLog('Error occured in insertXMLProperty procedure');
            insertLog(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertXMLProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertXMLProperty;


    /**************************************************************************************************************************************
    * NAME:          insertBooleanProperty
    * DESCRIPTION:   
    **************************************************************************************************************************************/ 
    procedure insertBooleanProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              booleanProp char) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, propertyClassID || ':' || booleanProp);
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into BOOLEANPROPERTYVERSION ( BOOLEANPROPERTYID, BOOLEANVALUE )
        values (elementVersionID, booleanProp);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);


    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));            
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insertLog('Error occured in insertBooleanProperty procedure');
            insertLog(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertBooleanProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertBooleanProperty;  


    /**************************************************************************************************************************************
    * NAME:          insertTextProperty
    * DESCRIPTION:   
    **************************************************************************************************************************************/ 
    procedure insertTextProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              textProp varchar2, language_code char) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, null);
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into TEXTPROPERTYVERSION (TEXTPROPERTYID, LANGUAGECODE, TEXTTYPE, TEXT)
        values (elementVersionID, SUBSTR(language_code, 0, 3), 'SIM', textProp);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);


    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insertLog('Error occured in insertTextProperty procedure');
            insertLog(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertTextProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertTextProperty;           


    /**************************************************************************************************************************************
    * NAME:          insertEnumeratedProperty
    * DESCRIPTION:   
    **************************************************************************************************************************************/ 
    procedure insertEnumeratedProperty(p_version_code number, domainElementID number, propertyClassID number, domainID number, 
              structureVersionID number, litValue varchar2) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
        domainValueID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, 'EnumProp ' || propertyClassID || ':' || litValue);
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        getEnumerationID(litValue, domainID, domainValueID);

        insert into ENUMERATEDPROPERTYVERSION (ENUMERATEDPROPERTYID, DOMAINID, DOMAINVALUEID)
        values (elementVersionID, domainID, domainValueID);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);


    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insertLog('Error occured in insertEnumeratedProperty procedure');
            insertLog(errString);
            insertLog('lit value: ' || litValue || ' domainID: ' || domainID || ' domainValueID: ' || domainValueID);
            commit;
            raise_application_error(-20011, 'Error occurred in insertEnumeratedProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertEnumeratedProperty;     


    /**************************************************************************************************************************************
    * NAME:          populateDaggerAsteriskLookup
    * DESCRIPTION:   Populates the tables necessary to perform dagger asterisk lookups.   
    **************************************************************************************************************************************/ 
    procedure populateDaggerAsteriskLookup(p_version_code number, structureVersionID number) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
        daggerAsteriskClassID number := 0;
    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;
        daggerAsteriskClassID := getICD10CAClassID('ValueDomainVersion', 'DaggerAsteriskDomain');

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, daggerAsteriskClassID, elementUUID, 'Dagger Asterisk Domain');
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', 'Dagger Asterisk');

        insert into VALUEDOMAINVERSION (DOMAINID)
        values (elementVersionID);

        insert into ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, ENUMERATION_SEQ.NEXTVAL, null, null, null, '', 'Dagger Asterisk: Null Value'); 

        insert into ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, ENUMERATION_SEQ.NEXTVAL, null, null, null, '*', 'Dagger Asterisk: *');

        insert into ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, ENUMERATION_SEQ.NEXTVAL, null, null, null, '+', 'Dagger Asterisk: +');

        insert into ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, ENUMERATION_SEQ.NEXTVAL, null, null, null, 'Y', 'Dagger Asterisk: Y');

        insert into ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, ENUMERATION_SEQ.NEXTVAL, null, null, null, 'N', 'Dagger Asterisk: N');

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);

    exception
        when others then
            insertLog('Error occured in populateDaggerAsteriskLookup procedure');
            insertLog('Error inside populateDaggerAsteriskLookup: ' || SQLCODE || ' ' || SQLERRM);          
            commit;
            raise_application_error(-20011, 'Error occurred in populateDaggerAsteriskLookup. <br> Error:' || substr(sqlerrm, 1, 50));
                  
    end populateDaggerAsteriskLookup;


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
                classID := getICD10CAClassID('XMLPropertyVersion', 'ExcludePresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'I' THEN
                classID := getICD10CAClassID('XMLPropertyVersion', 'IncludePresentation');  
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'A' THEN
                classID := getICD10CAClassID('XMLPropertyVersion', 'CodeAlsoPresentation');
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code);
            ELSIF UPPER(TRIM(category_detail_type_code)) = 'N' THEN
                classID := getICD10CAClassID('XMLPropertyVersion', 'NotePresentation');                
                insertXMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_detail_data, language_code); 
            ELSE
                dbms_output.put_line('UNK CDTC ' || category_detail_type_code);
                insert into log(message) values('Unknown category detail type code: ' || category_detail_type_code);
                commit;                    
            END IF;

        end loop;

    exception
        when others then
            insertLog('Error in migrateIncludeExclude with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);          
            commit;
            raise_application_error(-20011, 'Error occurred in migrateIncludeExclude. <br> Error:' || substr(sqlerrm, 1, 50));

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

            classID := getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation');
            insertHTMLProperty(p_version_code, domainElementID, classID, structureVersionID, category_table_output_data, language_code);

        end loop;

    exception
        when others then
            insertLog('Error in migrateTableOutput with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM);  
            commit;
            raise_application_error(-20011, 'Error occurred in migrateTableOutput. <br> Error:' || substr(sqlerrm, 1, 50));

    end migrateTableOutput;


    /**************************************************************************************************************************************
    * NAME:          buildNarrowRelationship
    * DESCRIPTION:   Builds the parent-child relationship between two nodes in a tree
    **************************************************************************************************************************************/    
    procedure buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number, 
        rangeElementID number, structureVersionID number) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

    begin
        elementID := ELEMENTID_SEQ.Nextval;
        elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
        elementUUID := ELEMENTUUID_SEQ.Nextval;

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, relationshipClassID, elementUUID, elementID || ' Build Narrow Relationship w classID ' || relationshipClassID );
        
        insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into CONCEPTPROPERTYVERSION (CONCEPTPROPERTYID, RANGEELEMENTID, INVERSECONCEPTPROPERTYID)
        values ( elementVersionID, rangeElementID, null);

        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, 'Narrow Relationship structure element');
        
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
            select ic.*, fcd.short_desc F_SHORT_DESC, fcd.long_desc F_LONG_DESC, fcd.user_desc F_USER_DESC
            from icd.category ic
            LEFT OUTER join icd.french_category_desc fcd on ic.category_id = fcd.category_id
            where ic.parent_category_id = parentCategoryID
            and TRIM(ic.clinical_classification_code) = '10CA' || p_version_code
            order by ic.category_code;

        rec_cc c%rowtype;
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

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

        daDomainID number := 0;
        categoryTypeCode VARCHAR2(10);
        nodeType VARCHAR2(10);

        nodeClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
    
    begin

        for rec_cc in c loop
            elementID := ELEMENTID_SEQ.Nextval;
            elementVersionID := ELEMENTVERSIONID_SEQ.Nextval; 
            elementUUID := ELEMENTUUID_SEQ.Nextval;

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

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';            
            ELSE 
                status_code := 'DISABLED';
            END IF;
            
            IF TRIM(categoryTypeCode) = 'BL1' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'BL2' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'BL3' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'CAT1' THEN

                IF TRIM(v_chapter_code) = '22' THEN
                    nodeClassID := getICD10CAClassID('ConceptVersion', 'Block');
                    nodeType := 'BLOCK';
                    dbms_output.put_line('Chapter 22 special case.  Converting CAT1 to BLOCK: ' || categoryID || ': ' || v_code);
                    insertLog('Chapter 22 special case.  Converting CAT1 to BLOCK: ' || categoryID || ': ' || v_code);
                ELSE
                    nodeClassID := getICD10CAClassID('ConceptVersion', 'Category');
                    nodeType := 'CATEGORY';
                END IF;
            ELSIF TRIM(categoryTypeCode) = 'CAT2' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CAT3' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CODE' THEN
                nodeClassID := getICD10CAClassID('ConceptVersion', 'Category');
                nodeType := 'CODE';
            ELSE 
                dbms_output.put_line('ERROR!: Category Type Code ' || categoryTypeCode || ' is unknown'); 
                insertLog('Category type code is not right: ' || categoryTypeCode);
                commit;
                raise_application_error(-20011, 'Error occurred in migrateChildNodes.');
            END IF;

            insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
            values (elementID, nodeClassID, elementUUID, nodeType || ' ' || v_code);
        
            insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
            values (elementVersionID, elementID, p_version_code, sysdate, status_code, nodeType || ' ' || v_code);

            insert into CONCEPTVERSION (CONCEPTID) 
            values (elementVersionID);
        
            insert into STRUCTUREELEMENTVERSION (elementversionid, structureid, contextstatus, contextstatusdate, notes)
            values (elementVersionID, structureVersionID, 'ACTIVE', sysdate, nodeType || ' ' || v_code);

            -- Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, categoryID, elementID, structureVersionID);

            -- Table Output
            migrateTableOutput(p_version_code, categoryID, elementID, structureVersionID);

            --Store Short title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_short_title, 'ENG');

            --Store Long title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_long_title, 'ENG');

            --Store User title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_user_title, 'ENG');

            --Store Chapters Short title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_short_title_fr,' '), 'FRA');

            --Store Chapters Long title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_long_title_fr,' '), 'FRA');

            --Store Chapters User title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_user_title_fr,' '), 'FRA');

            --Store Code value (Category Code in ICD)
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'Code');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code, 'ENG');

            --Store CA Enhancement flag
            IF ( LENGTH(trim(v_ca_enhancement_flag)) ) > 0 THEN
                propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');             
                insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_ca_enhancement_flag);
            END IF;

            --Store Dagger Asterisk
            propertyClassID := getICD10CAClassID('ValueDomainVersion', 'DaggerAsteriskDomain');             
            daDomainID := getDaggerAsteriskDomainID(propertyClassID);
            insertEnumeratedProperty(p_version_code, elementID, propertyClassID, daDomainID, structureVersionID, Trim(v_dagger_asterisk));

            --Store Code flag
            propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'ValidCodeIndicator');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code_flag);

            --Store Render Children as Table flag
            propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableIndicator');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_render_child_flag);

            --Build Narrow Relationship
            relationshipClassID := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');             
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, parentElementID, structureVersionID);
            
            --Recursively call
            migrateChildNodes(p_version_code, structureVersionID, elementID, categoryID, v_chapter_code);

        end loop;
        commit;

    exception
        when others then
            insertLog('migrateChildNodes:' || SQLCODE || ' ' || SQLERRM);  
            commit;
            raise_application_error(-20011, 'Error occurred in migrateChildNodes. <br> Error:' || substr(sqlerrm, 1, 50));
    end migrateChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateChapterNodes
    * DESCRIPTION:   Migrates the chapters 
    **************************************************************************************************************************************/    
    procedure migrateChapterNodes(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c_chapter is
            select ic.*, fcd.short_desc F_SHORT_DESC, fcd.long_desc F_LONG_DESC, fcd.user_desc F_USER_DESC
            from icd.category ic
            LEFT OUTER join icd.french_category_desc fcd on ic.category_id = fcd.category_id
            where ic.category_type_code = 'CHP'
            and TRIM(ic.clinical_classification_code) = '10CA' || p_version_code
            order by ic.category_code;

        rec_cc c_chapter%rowtype;
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

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

        daDomainID number := 0;
        chapterClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
    
    begin
        chapterClassID := getICD10CAClassID('ConceptVersion', 'Chapter'); 
    
        for rec_cc in c_chapter loop
            elementID := elementid_SEQ.nextval;
            elementVersionID := elementversionid_SEQ.nextval; 
            elementUUID := elementuuid_SEQ.nextval;

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

            IF TRIM(v_status_code) = 'A' THEN
                status_code := 'ACTIVE';            
            ELSE 
                status_code := 'DISABLED';
            END IF;

            insertLog('Migrating chapter ' || v_chapter_code);

            -- Chapter
            insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
            values (elementID, chapterClassID, elementUUID, 'Chapter' || v_chapter_code);
        
            insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
            values (elementVersionID, elementID, p_version_code, sysdate, status_code, 'Chapter ' || v_chapter_code);

            insert into CONCEPTVERSION (CONCEPTID) 
            values (elementVersionID);
        
            insert into STRUCTUREELEMENTVERSION (elementversionid, structureid, contextstatus, contextstatusdate, notes)
            values (elementVersionID, structureVersionID, 'ACTIVE', sysdate, 'Chapter ' || v_chapter_code);

            -- Chapter Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, chapter_category_ID, elementID, structureVersionID);

            -- Chapter Table Output
            migrateTableOutput(p_version_code, chapter_category_ID, elementID, structureVersionID);

            --Store Chapters Short title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_short_title, 'ENG');

            --Store Chapters Long title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_long_title, 'ENG');

            --Store Chapters User title English
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_user_title, 'ENG');

            --Store Chapters Short title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_short_title_fr,' '), 'FRA');

            --Store Chapters Long title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_long_title_fr,' '), 'FRA');

            --Store Chapters User title French
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_chapter_user_title_fr,' '), 'FRA');

            --Store Chapters Code value (Category Code in ICD)
            propertyClassID := getICD10CAClassID('TextPropertyVersion', 'Code');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_code, 'ENG');

            --Store Chapters CA Enhancement flag
            propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, nvl(v_ca_enhancement_flag, 'N'));

            --Store Chapters Dagger Asterisk
            propertyClassID := getICD10CAClassID('ValueDomainVersion', 'DaggerAsteriskDomain');   
            daDomainID := getDaggerAsteriskDomainID(propertyClassID);          
            insertEnumeratedProperty(p_version_code, elementID, propertyClassID, daDomainID, structureVersionID, Trim(v_dagger_asterisk));

            --Store Chapters Code flag
            propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'ValidCodeIndicator');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code_flag);

            --Store Chapters Render Children as Table flag
            propertyClassID := getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableIndicator');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_render_child_flag);

            --Build Narrow Relationship
            relationshipClassID := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');             
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID, structureVersionID);
                        
            migrateChildNodes(p_version_code, structureVersionID, elementID, chapter_category_ID, v_chapter_code);
            
        end loop;
        
        commit;

    exception
        when others then
            insertLog('migrateChapterNodes ' || SQLCODE || ' ' || SQLERRM);            
            commit;
            raise_application_error(-20011, 'Error occurred in migrateChapterNodes. <br> Error:' || substr(sqlerrm, 1, 50));
    end migrateChapterNodes;


    /**************************************************************************************************************************************
    * NAME:          init_BaseClassification
    * DESCRIPTION:   Initializes the base classification for ICD-10-CA
    **************************************************************************************************************************************/    
    procedure init_BaseClassification(p_version_code varchar2, structureVersionID OUT number) is
        classID number := 0;
        elementID number := 0;
        elementUUID number := 0;
        elementVersionID number := 0;
    begin
        elementID := elementid_SEQ.nextval;
        elementUUID := elementuuid_SEQ.nextval; 
        elementVersionID := elementversionid_SEQ.nextval;
        structureVersionID := elementVersionID;

        classID := getICD10CAClassID('BaseClassification', 'ICD-10-CA'); 

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, classID, elementUUID, 'BaseClassification');
    
        insert into ELEMENTVERSION (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', p_version_code || 'Base');
    
        insert into STRUCTUREVERSION (STRUCTUREID) values (elementVersionID);
        commit;
    
    end init_BaseClassification;


    /**************************************************************************************************************************************
    * NAME:          createViewerRoot
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/ 
    procedure createViewerRoot(p_version_code number, structureVersionID number, viewerRootID out number) is
        classID number := 0;
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
        propertyClassID number := 0;
        v_short_title varchar2(255);  
        v_long_title varchar2(255);
        v_user_title varchar2(255);

    begin
        elementID := elementid_SEQ.nextval;
        elementVersionID := elementversionid_SEQ.nextval;
        elementUUID := elementuuid_SEQ.nextval;
        viewerRootID := elementID;

        v_short_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED ' || 
                        'HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                        p_version_code;  

        v_long_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED ' || 
                        'HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                        p_version_code;

        v_user_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED ' || 
                        'HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                        p_version_code;

        classID := getICD10CAClassID('ConceptVersion', 'ClassificationRoot'); 

        insert into ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, classID, elementUUID, 'ViewerRoot');
    
        insert into ELEMENTVERSION (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', p_version_code || 'ClassificationRoot');

        insert into CONCEPTVERSION (CONCEPTID) 
        values (elementVersionID);
    
        insert into STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (elementVersionID, structureVersionID, null, SYSDATE, null);
   
        --Store Short title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_short_title, 'ENG');

        --Store Long title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_long_title, 'ENG');

        --Store User title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_user_title, 'ENG');

        --Store Short title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_short_title, 'FRA');

        --Store Long title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_long_title, 'FRA');

        --Store User title
        propertyClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');             
        insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_user_title, 'FRA');

        commit;
    
    end createViewerRoot;

    /**************************************************************************************************************************************
    * NAME:          cleanUp_Log_Tables
    * DESCRIPTION:   Clean up procedure
    *                Currently not used.  Keep log records.
    **************************************************************************************************************************************/   
    procedure cleanUp_Log_Tables is
    begin
        delete from LOG;    
        commit;
    
    end cleanUp_Log_Tables;


    /**************************************************************************************************************************************
    * NAME:          cleanUp_Tables
    * DESCRIPTION:   Clean up procedure
    *                This procedure will need to be removed at some point.  Cannot delete single year ICD.  
    *                Use procedure DELETEICD to delete entire ICD instance.
    **************************************************************************************************************************************/   
    procedure cleanUp_Tables is
    begin
        delete from TEXTPROPERTYVERSION;
        delete from DATAPROPERTYVERSION;
        delete from XMLPROPERTYVERSION;  
        delete from HTMLPROPERTYVERSION;      
        delete from ENUMERATION;
        delete from VALUEDOMAINVERSION;
        delete from ENUMERATEDPROPERTYVERSION;
        delete from BooleanPropertyVERSION;
        delete from propertyVERSION;
        delete from STRUCTUREELEMENTVERSION;
        delete from STRUCTUREVERSION;
        delete from ELEMENTVERSION;
        delete from ELEMENT;
        delete from CONCEPTVERSION;
    
        commit;
    
    end cleanUp_Tables;


    /**************************************************************************************************************************************
    * NAME:          cleanUp_LookupTables
    * DESCRIPTION:   Clean up procedure to clean the CLASS, LANGUAGE, and the dagger asterisk tables
    *                Ensure any referencing records are deleted first.
    **************************************************************************************************************************************/   
    procedure cleanUp_LookupTables is
    begin
        delete from CLASS;
        delete from LANGUAGE;

        commit;
    
    end cleanUp_LookupTables;


    /**************************************************************************************************************************************
    * NAME:          populate_lookUp
    * DESCRIPTION:   
    **************************************************************************************************************************************/   
    procedure populate_lookUp is
    begin
        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'ClassificationRoot', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'BaseClassification', icd_classification_code, 'ICD-10-CA', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Chapter', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Block', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ConceptVersion', icd_classification_code, 'Category', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'Code', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'ShortTitle', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'LongTitle', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'TextPropertyVersion', icd_classification_code, 'UserTitle', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ConceptPropertyVersion', icd_classification_code, 'Narrower', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'ValueDomainVersion', icd_classification_code, 'DaggerAsteriskDomain', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'IncludePresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'ExcludePresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'CodeAlsoPresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'XMLPropertyVersion', icd_classification_code, 'NotePresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'TablePresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', icd_classification_code, 'CaEnhancementIndicator', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'EnumeratedPropertyVersion', icd_classification_code, 'DaggerAsteriskIndicator', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', icd_classification_code, 'ValidCodeIndicator', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'BooleanPropertyVersion', icd_classification_code, 'RenderChildrenAsTableIndicator', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'LongPresentation', null);

        insert into CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) 
        values (CLASSID_SEQ.Nextval, 'HTMLPropertyVersion', icd_classification_code, 'ShortPresentation', null);

        --LANGUAGE
        insert into LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('ENG', 'English');
        insert into LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('FRA', 'French');
    
        commit;
    
    end populate_lookUp;


    /**************************************************************************************************************************************
    * NAME:          icd_data_migration_cleanup
    * DESCRIPTION:   WIP procedure to clean up lookup tables.  Should not be run everytime
    *                Procedure is currently not public.
    *                Will not run successfully if there are referencing records  
    **************************************************************************************************************************************/   
    PROCEDURE icd_data_migration_cleanup(version_Code number) is
        runStatus varchar2(10);
    BEGIN
        f_year := version_Code;
        dbms_output.enable(1000000);        
        runStatus := checkRunStatus(version_Code);

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
    PROCEDURE icd_data_migration(version_Code number) is
        structureVersionID number := 0;
        viewerRootElementID number := 0;
        logRunID number := 0;
        runStatus varchar2(10);

    BEGIN
        f_year := version_Code;
        dbms_output.enable(1000000);
        runStatus := checkRunStatus(version_Code);

        IF runStatus = 'FALSE' THEN 
            dbms_output.put_line('Script already running....');
            RETURN;
        END IF;
        
        logRunID := LOG_RUN_SEQ.Nextval;
        dbms_output.put_line('ICD Data Migration Run ID: ' || logRunID);

        insertLog('Starting ICD10CA migration ' || version_code);

        insertLog('Cleaning up tables to begin migration');
        cleanUp_Tables;
        insertLog('Ending clean up tables');

        insertLog('Creation of base classification');
        init_BaseClassification(version_Code, structureVersionID);
        insertLog('Ending creation of base classification');

        insertLog('Populating dagger Asterisk lookup tables');
        populateDaggerAsteriskLookup(version_Code, structureVersionID);     
        insertLog('Ending population of dagger Asterisk lookup tables');  

        insertLog('Creation of root node');
        createViewerRoot(version_Code, structureVersionID, viewerRootElementID);
        insertLog('Ending creation of root node');

        insertLog('-- Main migration --');
        migrateChapterNodes(version_Code, structureVersionID, viewerRootElementID); 
        insertLog('-- Ending main migration --');

        insertLog('Ending ICD10CA migration ' || version_code);

    END icd_data_migration;


end ICD_DATA_MIGRATION;
/
