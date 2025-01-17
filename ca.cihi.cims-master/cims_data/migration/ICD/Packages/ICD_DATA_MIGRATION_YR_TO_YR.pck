create or replace package ICD_DATA_MIGRATION_YR_TO_YR is

    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;
    errString varchar(4000);

--    function checkRunStatus(p_version_code varchar2) return varchar2;
--    PROCEDURE icd_data_migration(version_Code IN number);
--    function retrieveParentElementIDbyCode(version_Code number, cat_code varchar2) return number;
--    PROCEDURE updateXMLDifferences(version_Code_to number, structureVersionID number);

    PROCEDURE part1(version_Code_fr number, version_Code_to number);
    PROCEDURE part2(version_Code_fr number, version_Code_to number);
end ICD_DATA_MIGRATION_YR_TO_YR;
/
create or replace package body ICD_DATA_MIGRATION_YR_TO_YR is
/**************************************************************************************************************************************************
Year to year migration:

1.  Create a new structure for the year
2.  Copy the previous years element versions into the new year that are of status 'ACTIVE' OR 'DISABLED'.  
    This makes previous year and new year identical
3.  Find out the nodes which are now gone.  Using their Category ID, reference Z_ICD_MIGRATION_MAPPING 
    using the Category ID and Version Year to retrieve the ELEMENT ID 
4.  Using the ELEMENT ID, retrieve the ELEMENT VERSION for the NEW year, and mark it as 'DISABLED'  
    More than likely will need to create another tracking table to indicate we disabled these nodes.
    TODO: Find correct status name
    TODO: Query Modifications on any reference to ELEMENTVERSION will require checking for status 'ACTIVE'
    TODO: Figure out how to deal with these orphaned child nodes
5.  Find out what nodes are NEW.
    For each of these new nodes, retrieve the Parent Category ID, and thus the Parent Category Code.
    Reference it against Z_ICD_MIGRATION_MAPPING for the TO (not From) version year, ultimately deriving the Parent ELEMENT ID.
    If for some reason you can't find it, stop and throw an error as something is wrong and I havent thought this through.
6.  Ensure the nodes from #5 are ordered by Category code to help or eliminate not finding the Parent ELEMENT ID
7.  For each of these nodes, add it in using basically a modified version of migrateChildNodes.  Do not recurse obviously.
    Build narrow relationship using the Parent ELEMENT ID

What do do with the child orphan nodes...
You only need the direct orphan nodes.  So query ELEMENTVERSION for the version year and 'DISABLED'
Looping through you check to see if the orphan nodes exist and is 'ACTIVE'
If no, skip
If yes, We need to reattach it to the proper parent

    
***************************************************************************************************************************************************/

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
        select count(*)  
        into notRunning
        from log 
        where run_id = (select max(run_id) from log where classification = icd_classification_code)
        and message like 'Ending ICD10CA migration%'
        order by id; 

        if (notRunning > 0) then
            return 'TRUE';
        end if;

        select count(*)
        into notRunning  
        from log
        where run_id = (select max(run_id) from log where classification = icd_classification_code)
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
        msg varchar2(4000);

        --PRAGMA AUTONOMOUS_TRANSACTION;

    begin

        msg := message || '                        ' || to_char(sysdate, 'dd-mon-yyyy hh24:mi:ss');
        dbms_output.put_line(msg);

        logID := LOG_SEQ.Nextval;
        logRunID := LOG_RUN_SEQ.CURRVAL;
        logDate := sysdate;

        insert into LOG(ID, MESSAGE, MESSAGEDATE, CLASSIFICATION, FISCAL_YEAR, RUN_ID) 
        values (logID, msg, logDate, icd_classification_code, f_year, logRunID);

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
    function getDaggerAsteriskDomainID(propertyClassID number, structureVersionID number) return number is
        domainID number;
    begin
        select vd.domainID
        INTO domainID
        from element e 
        join elementversion ev on e.elementid = ev.elementid
        join valuedomainVERSION vd on ev.elementversionid = vd.domainid
        join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid
        where e.classid = propertyClassID
        and sev.structureid = structureVersionID;

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
            insertLog('Error occured in insertXMLProperty procedure');
            insertLog(errString);
           

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

    end insertEnumeratedProperty;     


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
                insert into log(message) values('Unknown category detail type code: ' || category_detail_type_code);                    
            END IF;

        end loop;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000)); 
            errString := 'Error in migrateIncludeExclude with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM;
            insertLog(errString);          

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
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000)); 
            errString := 'Error in migrateTableOutput with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM;
            insertLog(errString);  

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
    * NAME:          migrateChildNode
    * DESCRIPTION:   Recursive procedure to migrate child nodes
    *                Cannot combine with migrateChapterNodes, takes in different number of parameters
    *                Adding in parameter v_chapter_code, which was a special case where for chapter 22 the category type code needs to
    *                be changed.
    *                TODO: If ICD10 code (starts with a letter) apply format as follows: One dot after three digits
    **************************************************************************************************************************************/    
    procedure migrateChildNode(newCategoryID number, p_version_code varchar2, structureVersionID number, parentElementID number,  
        v_chapter_code varchar2) is

        cursor c is
            select ic.*, fcd.short_desc F_SHORT_DESC, fcd.long_desc F_LONG_DESC, fcd.user_desc F_USER_DESC
            from icd.category ic
            LEFT OUTER join icd.french_category_desc fcd on ic.category_id = fcd.category_id
            where ic.category_id = newCategoryID
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
                insertLog('Category type code is not right: ' || categoryTypeCode);
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
        
            daDomainID := getDaggerAsteriskDomainID(propertyClassID, structureVersionID);

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

        end loop;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := 'migrateChildNode:' || SQLCODE || ' ' || SQLERRM;
            insertLog(errString);          
    end migrateChildNode;


    /**************************************************************************************************************************************
    * NAME:          compareYears
    * DESCRIPTION:   
    **************************************************************************************************************************************/   
    PROCEDURE compareYears(version_Code_from number, version_Code_to number) is
        cursor c is
            select * 
            from icd.category c
            where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to
            and TRIM(c.category_code) not in (
                select TRIM(category_code) 
                from icd.category c
                where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_from
            )
            order by category_code;

    BEGIN
        dbms_output.put_line('Comparing ICD Year ' || version_Code_from || ' to ' || version_Code_to);

    END compareYears;


    /**************************************************************************************************************************************
    * NAME:          doesElementAlreadyExist
    * DESCRIPTION:   
    **************************************************************************************************************************************/    
    function doesElementAlreadyExist(cat_code varchar2, structureVersionID number) return number is
        eID number;
        codeClassID number;
    begin

        codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');

        select count(*)
        into eID
        from textpropertyversion tp
        join propertyversion p on tp.textpropertyid = p.propertyid
        join elementversion ev on p.propertyid = ev.elementversionid --and ev.status = 'DISABLED'
        join structureelementversion sev on ev.elementversionid = sev.elementversionid
        join element e on ev.elementid = e.elementid
        join ELEMENT e1 on p.domainelementid = e1.elementid
        join ELEMENTVERSION ev2 on ev2.elementid = e1.elementid and ev2.status = 'DISABLED' -- Actual Node EV
        join STRUCTUREELEMENTVERSION se on ev2.elementversionid = se.elementversionid and se.structureid = structureVersionID
        where e.classid = codeClassID
        and sev.structureid = structureVersionID
        and tp.text = cat_code;

        return eID;
    end doesElementAlreadyExist;


    /**************************************************************************************************************************************
    * NAME:          retrieveNodeElementIDbyCode
    * DESCRIPTION:   Dont need the Language since Code is always English
    **************************************************************************************************************************************/    
    function retrieveNodeElementIDbyCode(version_Code number, cat_code varchar2) return number is
        eID number;
        codeClassID number;
        structureVersionID number;
    begin

        codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');
        structureVersionID := CIMS_ICD.geticd10castructureidbyyear(version_Code);

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
    * NAME:          internal_retrieveHTMLEV
    * DESCRIPTION:   Retrieve the ELEMENTVERSION ID for a given Element ID, Structure year, Class ID, and Language
                     The exceptions thrown is 
    **************************************************************************************************************************************/    
    function internal_retrieveHTMLEV(structureVersionID number, cID number, lang varchar2, nodeElementID number) return number is
        eID number;
    begin
        SELECT h.htmlpropertyid
        INTO eID
        FROM HTMLPROPERTYVERSION h
        join PROPERTYVERSION p on p.propertyid = h.htmlpropertyid and p.domainelementid = nodeElementID
        join ELEMENTVERSION ev on h.htmlpropertyid = ev.elementversionid
        join ELEMENT e on ev.elementid = e.elementid
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = structureVersionID
        WHERE h.languagecode = lang
        AND e.classid = cID;

        return eID;

    exception
        when TOO_MANY_ROWS then
            raise_application_error(-20011, 'Too Many rows');
        when NO_DATA_FOUND then
            return -9999;
        when others then
            raise_application_error(-20011, 'Unknown error');
    end internal_retrieveHTMLEV;


    /**************************************************************************************************************************************
    * NAME:          internal_retrieveXMLEV
    * DESCRIPTION:   Retrieve the ELEMENTVERSION ID for a given Element ID, Structure year, Class ID, and Language
                     The exceptions thrown is 
    **************************************************************************************************************************************/    
    function internal_retrieveXMLEV(structureVersionID number, cID number, lang varchar2, nodeElementID number) return number is
        eID number;
    begin
        SELECT x.xmlpropertyid
        INTO eID
        FROM XMLPROPERTYVERSION x
        join PROPERTYVERSION p on p.propertyid = x.xmlpropertyid and p.domainelementid = nodeElementID
        join ELEMENTVERSION ev on x.xmlpropertyid = ev.elementversionid
        join ELEMENT e on ev.elementid = e.elementid
        JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid and sev.structureid = structureVersionID
        WHERE x.languagecode = lang
        AND e.classid = cID;

        return eID;

    exception
        when TOO_MANY_ROWS then
            raise_application_error(-20011, 'Too Many rows');
        when NO_DATA_FOUND then
            return -9999;
        when others then
            raise_application_error(-20011, 'Unknown error');
    end internal_retrieveXMLEV;


    /**************************************************************************************************************************************
    * NAME:          getDirectChildNodes
    * DESCRIPTION:   Returns a cursor with only its direct child nodes
    **************************************************************************************************************************************/    
    procedure getDirectChildNodes(nodeElementID number, version_Code_to number, structureVersionID number, codeClassID number) is
        cursor c_data is 
            WITH elementPropertys AS (
                SELECT e.elementid, e.notes, cp.rangeelementid ParentElementID, tp.text
                FROM CONCEPTPROPERTYVERSION cp
                join PROPERTYVERSION p on p.propertyid = cp.conceptpropertyid
                join ELEMENTVERSION ev on p.propertyid = ev.elementversionid
                join STRUCTUREELEMENTVERSION se on ev.elementversionid = se.elementversionid and se.structureid = structureVersionID
                join ELEMENT e on p.domainelementid = e.elementid
                join PROPERTYVERSION p1 on e.elementid = p1.domainelementid
                join TEXTPROPERTYVERSION tp on p1.propertyid = tp.textpropertyid
                join ELEMENTVERSION ev1 on p1.propertyid = ev1.elementversionid
                join STRUCTUREELEMENTVERSION se1 on ev1.elementversionid = se1.elementversionid and se1.structureid = structureVersionID
                join ELEMENT e1 on ev1.elementid = e1.elementid
                WHERE e1.classid = codeClassID
                )
            SELECT ep.elementid, ep.notes, ep.ParentElementID, ep.text, level treeLevel
            FROM elementPropertys ep
            WHERE level = 2
            CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
            start with ep.elementid = nodeElementID
            ORDER SIBLINGS BY ep.text;

        categoryCode varchar2(30);
        eID number := 0;
        evID number := 0;
        nrEVID number := 0;
        nrClassID  number := 0;
        newParentElementID number := 0;
        parentCategoryID number := 0;
        parentCategoryCode varchar2(30);
        status varchar2(12);
        rec_cc c_data%rowtype;

    begin
        nrClassID := getICD10CAClassID('ConceptPropertyVersion', 'Narrower');

        --Looping through all child nodes who are orphaned
        for rec_cc in c_data loop
            categoryCode := TRIM(rec_cc.text);
            eID := rec_cc.elementid;
            
            --Retrieve this nodes ElementVersionID and status
            select ev.status, ev.elementversionid
            into status, evID
            from ELEMENTVERSION ev
            join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid and sev.structureid = structureVersionID
            where ev.elementid = eID;

            --Retrieve this nodes NARROW RELATIONSHIP ElementVersionID and status
            SELECT ev.elementversionid
            into nrEVID
            FROM PROPERTYVERSION p 
            join ELEMENTVERSION ev on p.propertyid = ev.elementversionid
            join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid and sev.structureid = structureVersionID
            join ELEMENT e on ev.elementid = e.elementid
            WHERE p.domainelementid = eID
            and e.classid = nrClassID;

            insertLog('    Child node ' || categoryCode || ' and status is: ' || status);

            if (status = 'ACTIVE') THEN

                --Using the categoryCode, go back to ICD and get its parent category ID and parent category code for the new year
                SELECT c.category_id, TRIM(c.category_code)
                INTO parentCategoryID, parentCategoryCode
                FROM icd.category c
                WHERE c.category_id IN (
                    SELECT ic.parent_category_id    
                    FROM icd.category ic
                    WHERE TRIM(ic.clinical_classification_code) = '10CA' || version_Code_to
                    and TRIM(ic.category_code) = categoryCode
                );

                --Using the parent category code, determine the parent Element ID to attach it to 
                newParentElementID := retrieveNodeElementIDbyCode(version_Code_to, parentCategoryCode);

                insertLog('    Updating Relationship of ' || categoryCode || ' from ' || nodeElementID || ' ' || ' to ' || newParentElementID);

                UPDATE CONCEPTPROPERTYVERSION cpv
                SET cpv.rangeelementid = newParentElementID
                WHERE cpv.rangeelementid = nodeElementID
                AND cpv.conceptpropertyid = nrEVID;
            ELSE
                insertLog('Orphaned child node is DISABLED in ICD, skip...  ' || categoryCode);
            END IF;

        end loop;

    end getDirectChildNodes;


    /**************************************************************************************************************************************
    * NAME:          updateTextDifferences
    * DESCRIPTION:   Texts in ICD (or any properties that are stored as a char)
    **************************************************************************************************************************************/    
    procedure updateTextDifferences(version_Code_to number, structureVersionID number, codeClassID number) is
        cursor c_data is 
            select h.category_code, h.text_to, h.classid, h.language, p.domainelementid
            from Z_icd_diffs_text h
            JOIN TEXTPROPERTYVERSION tp on h.category_code = tp.text
            JOIN PROPERTYVERSION p on tp.textpropertyid = p.propertyid
            JOIN ELEMENTVERSION ev on p.propertyid = ev.elementversionid
            JOIN STRUCTUREELEMENTVERSION sev on ev.elementversionid = sev.elementversionid AND sev.structureid = structureVersionID
            JOIN ELEMENT e on ev.elementid = e.elementid and e.classid = codeClassID
            where h.versioncode_to = version_Code_to;

        categoryCode varchar2(30);
        updText varchar2(255);
        updClassID number := 0;
        lang varchar2(3);
        rec_cc c_data%rowtype;
        nodeElementID number := 0;
        shortClassID number := 0;
        longClassID number := 0;
        userClassID number := 0;
        cefClassID number := 0;
        validCodeClassID number := 0;
        renderClassID number := 0;
        daClassID number := 0;
        daDomainClassID number := 0;
        daDomainID number := 0;
        domainValueID number := 0;
        textUpdateCount number := 0;
        counter number := 0;

    begin
        shortClassID := getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        longClassID := getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        userClassID := getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        cefClassID := getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');
        validCodeClassID := getICD10CAClassID('BooleanPropertyVersion', 'ValidCodeIndicator');
        renderClassID := getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableIndicator');
        daClassID := getICD10CAClassID('EnumeratedPropertyVersion', 'DaggerAsteriskIndicator');
        daDomainClassID := getICD10CAClassID('ValueDomainVersion', 'DaggerAsteriskDomain');

        select count(*)
        INTO textUpdateCount 
        from Z_icd_diffs_text h
        where h.versioncode_to = version_Code_to;

        insertLog('# of text differences to update ' || textUpdateCount);

        for rec_cc in c_data loop  
        
            counter := counter + 1;
            IF ( counter IN (1000, 2000, 5000, 10000, 15000, 20000)) THEN
                insertLog('Processed text differences: ' || counter);
            END IF;
          
            categoryCode := TRIM(rec_cc.category_code);
            updText := TRIM(rec_cc.text_to);
            updClassID := rec_cc.classid;
            lang := rec_cc.language;
            nodeElementID := rec_cc.domainElementID;

            if (updClassID IN ( shortClassID, longClassID, userClassID ) ) then                              
                UPDATE TEXTPROPERTYVERSION t
                SET t.text = nvl(updText, ' ')
                WHERE t.languagecode = lang
                and t.textpropertyid = (
                    SELECT t.textpropertyid
                    FROM PROPERTYVERSION p 
                    join ELEMENTVERSION ev on ev.elementversionid = p.propertyid
                    join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid
                    join ELEMENT e on ev.elementid = e.elementid
                    join TEXTPROPERTYVERSION t on p.propertyid = t.textpropertyid
                    WHERE p.domainelementid = nodeElementID
                    AND sev.structureid = structureVersionID
                    and e.classid = updClassID
                    and t.languagecode = lang);

            elsif (updClassID IN ( cefClassID, validCodeClassID, renderClassID ) ) then
                UPDATE BOOLEANPROPERTYVERSION b
                SET b.booleanvalue = nvl(updText, 'N')
                WHERE b.booleanpropertyid = (
                    SELECT b.booleanpropertyid
                    FROM PROPERTYVERSION p 
                    join ELEMENTVERSION ev on ev.elementversionid = p.propertyid
                    join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid
                    join ELEMENT e on ev.elementid = e.elementid
                    join BOOLEANPROPERTYVERSION b on b.booleanpropertyid = p.propertyid
                    WHERE p.domainelementid = nodeElementID
                    AND sev.structureid = structureVersionID
                    and e.classid = updClassID);

            elsif (updClassID = daClassID) then     
                daDomainID := getDaggerAsteriskDomainID(daDomainClassID, structureVersionID);
                getEnumerationID(TRIM(updText), daDomainID, domainValueID);
                --insertLog('Updated DA: ' || updText);

                UPDATE ENUMERATEDPROPERTYVERSION epv
                SET epv.domainvalueid = domainValueID
                WHERE epv.enumeratedpropertyid = (
                    SELECT epvv.enumeratedpropertyid
                    FROM PROPERTYVERSION p 
                    join ELEMENTVERSION ev on ev.elementversionid = p.propertyid
                    join STRUCTUREELEMENTVERSION sev on sev.elementversionid = ev.elementversionid
                    join ELEMENT e on ev.elementid = e.elementid
                    join ENUMERATEDPROPERTYVERSION epvv on epvv.enumeratedpropertyid = p.propertyid
                    WHERE p.domainelementid = nodeElementID
                    AND sev.structureid = structureVersionID
                    and e.classid = updClassID);
            end if;

        end loop;
    exception
        when others then
            insertLog('textDifference Error! : ' || SQLCODE || ' ' || SQLERRM);
            insertLog('Error is: ' || substr(sqlerrm, 1, 3900));

   
    end updateTextDifferences;


    /**************************************************************************************************************************************
    * NAME:          updateXMLDifferences
    * DESCRIPTION:   
    **************************************************************************************************************************************/    
    procedure updateXMLDifferences(version_Code_to number, structureVersionID number) is
        cursor c_data is 
--select * from  (
            select h.category_code_to, h.xmltext_to, h.classid, h.languagecode
            from Z_icd_diffs_xml h
            where h.versioncode_to = version_Code_to
            and h.category_code_to is not null;
--) where ROWNUM <= 50;

        categoryCode varchar2(30);
        updXMLText CLOB;
        updClassID number := 0;
        lang varchar2(3);
        rec_cc c_data%rowtype;
        nodeElementID number := 0;
        intID number := 0;
        tableClassID number := 0;
    begin
        tableClassID := getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation');

        for rec_cc in c_data loop            
            categoryCode := TRIM(rec_cc.category_code_to);
            updXMLText := rec_cc.xmltext_to;
            updClassID := rec_cc.classid;
            lang := rec_cc.languagecode;
            nodeElementID := retrieveNodeElementIDbyCode(version_Code_to, categoryCode);

            if (updClassID = tableClassID) THEN
                intID := internal_retrieveHTMLEV(structureVersionID, updClassID, lang, nodeElementID); 

                --New HTML
                if intID = -9999 then
                    insertLog('-9999 -- EV ID for ' || categoryCode || ' not found! Insert.  ClassID: ' || 
                        updClassID || ' Lang: ' || lang || ' under nodeElementID: ' || nodeElementID);
                    insertHTMLProperty(version_Code_to, nodeElementID, updClassID, structureVersionID, updXMLText, lang);   
                else
                --Existing HTML
                    if (updXMLText is NULL) then
                        UPDATE ELEMENTVERSION ev
                        SET ev.status = 'DISABLED'
                        WHERE ev.elementversionid = intID;
                    else
                        UPDATE ELEMENTVERSION ev
                        SET ev.status = 'ACTIVE'
                        WHERE ev.elementversionid = intID;

                        UPDATE HTMLPROPERTYVERSION h
                        SET h.htmltext = updXMLText
                        WHERE h.htmlpropertyid = intID;
                    end if;                             
                end if;

            ELSE
                intID := internal_retrieveXMLEV(structureVersionID, updClassID, lang, nodeElementID); 

                --New XML
                if intID = -9999 then
                    insertLog('-9999 -- EV ID for ' || categoryCode || ' not found! Insert.  ClassID: ' || 
                        updClassID || ' Lang: ' || lang || ' under nodeElementID: ' || nodeElementID);
                    insertXMLProperty(version_Code_to, nodeElementID, updClassID, structureVersionID, updXMLText, lang);   
                else
                --Existing XML
                    if (updXMLText is NULL) then
                        UPDATE ELEMENTVERSION ev
                        SET ev.status = 'DISABLED'
                        WHERE ev.elementversionid = intID;
                    else
                        UPDATE ELEMENTVERSION ev
                        SET ev.status = 'ACTIVE'
                        WHERE ev.elementversionid = intID;

                        UPDATE XMLPROPERTYVERSION x
                        SET x.xmltext = updXMLText
                        WHERE x.xmlpropertyid = intID;
                    end if;                             
                end if;
            END IF;



        end loop;

    end updateXMLDifferences;


    /**************************************************************************************************************************************
    * NAME:          attachOrphanNodes
    * DESCRIPTION:   
    **************************************************************************************************************************************/    
    procedure attachOrphanNodes(version_Code_to number, structureVersionID number) is
        cursor c_data is 
            SELECT h.* 
            FROM Z_ICD_DIFFS_NODES h
            where h.versioncode_to = version_Code_to
            and h.status = 'DISABLED';

        oldCategoryCode varchar2(30);
        oldElementID number := 0;
        rec_cc c_data%rowtype;
        codeClassID number := 0;

    begin
        codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');

        for rec_cc in c_data loop
            oldCategoryCode := TRIM(rec_cc.category_code);
            oldElementID := rec_cc.element_id_from;
            insertLog('Found category code orphaned: ' || oldCategoryCode || ' elementID: ' || oldElementID);
            getDirectChildNodes(oldElementID, version_Code_to, structureVersionID, codeClassID);

        end loop;

        insertLog('Finished looping through the orphan nodes');
    end attachOrphanNodes;


    /**************************************************************************************************************************************
    * NAME:          addNewNodes
    * DESCRIPTION:   
5.  Find out what nodes are NEW.
    For each of these new nodes, retrieve the Parent Category ID, and thus the Parent Category Code.
    Reference it against Z_ICD_MIGRATION_MAPPING for the TO (not From) version year, ultimately deriving the Parent ELEMENT ID.
    If for some reason you can't find it, stop and throw an error as something is wrong and I havent thought this through.
6.  Ensure the nodes from #5 are ordered by Category code to help or eliminate not finding the Parent ELEMENT ID
7.  For each of these nodes, add it in using basically a modified version of migrateChildNodes.  Do not recurse obviously.
    Build narrow relationship using the Parent ELEMENT ID

    **************************************************************************************************************************************/    
    procedure addNewNodes(version_Code_fr number, version_Code_to number, oldStructureID number, structureVersionID number) is
        cursor c_data is            
            select c.category_id, TRIM(c.category_code) cat_code, c.parent_category_id, c1.category_id dup_parent_category_id, 
                TRIM(c1.category_code) parent_category_code 
            from icd.category c
            join icd.category c1 on c.parent_category_id = c1.category_id
            where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to
            and TRIM(c.category_code) not in (
                select TRIM(category_code)
                from icd.category c
                where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_fr
            )
            order by c1.category_code;

        rec_cc c_data%rowtype;
        parentElementID number := 0;
        cat_code varchar2(30);
        parentCat_Code varchar2(30);
        parentCategoryID number := 0;
        categoryID number := 0;   
        tmpCheck number := 0;
        nodeElementID number := 0;
 
    begin
        for rec_cc in c_data loop
            categoryID := rec_cc.category_id;
            cat_code := TRIM(rec_cc.cat_code);
            parentCat_Code := TRIM(rec_cc.parent_category_code);
            parentElementID := retrieveNodeElementIDbyCode(version_Code_to, parentCat_Code); 
            parentCategoryID := rec_cc.parent_category_id;

            --Could return nothing since its a new node
            nodeElementID := retrieveNodeElementIDbyCode(version_Code_to, cat_code);

            --Only insert a new node if it doesnt already exist.  It could be marked as DISABLED
            tmpCheck := doesElementAlreadyExist(cat_code, oldStructureID);

            if tmpCheck = 0 then
                insertLog('NEW NODE: ' || cat_code || '.  Placing under ParentElementID: ' || parentElementID || ' cat code: ' || parentCat_Code);            
                migrateChildNode(categoryID, version_Code_to, structureVersionID, parentElementID, parentCat_Code);
            else 
                --Dont worry about whats changed in this reactivated node.  Part2 changes the texts and xml
                insertLog('EXISTING NODE.  Set status to active ' || cat_code);

	            UPDATE ELEMENTVERSION ev
                SET ev.status = 'ACTIVE'
                WHERE ev.elementversionid in (
                    SELECT ev1.elementversionid
                    FROM ELEMENTVERSION ev1
                    join STRUCTUREELEMENTVERSION sev on ev1.elementversionid = sev.elementversionid                 
                    WHERE sev.structureid = structureVersionID
                    and ev1.elementid = nodeElementID);

            end if;

        end loop;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000)); 
            errString := 'Error in addNewNodes --> ' || SQLCODE || ' ' || SQLERRM;
            insertLog(errString);  
            raise_application_error(-20011, 'Error occurred in addNewNodes. <br> Error:' || substr(sqlerrm, 1, 50));

    end addNewNodes;


    /**************************************************************************************************************************************
    * NAME:          disableRemovedNodes
    * DESCRIPTION:   
3.  Find out the nodes which are now gone.  Using their Category ID, reference Z_ICD_MIGRATION_MAPPING 
    using the Category ID and Version Year to retrieve the ELEMENT ID 
4.  Using the ELEMENT ID, retrieve the ELEMENT VERSION for the NEW year, and mark it as 'DISABLED'  
    More than likely will need to create another tracking table to indicate we disabled these nodes.
    TODO: Figure out how to deal with these orphaned child nodes

    **************************************************************************************************************************************/    
    procedure disableRemovedNodes(version_Code_fr number, version_Code_to number, structureVersionID number) is
        cursor c_data is            
            select * 
            from icd.category c
            where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_fr
            and TRIM(c.category_code) not in (
                select TRIM(category_code) 
                from icd.category c
                where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to
            )
            order by category_code;

        rec_cc c_data%rowtype;
        nodeElementID number := 0;
        cat_code varchar2(30);
        nodeElementVersionID number := 0;

    begin

        for rec_cc in c_data loop

            cat_code := Trim(rec_cc.category_code);
            nodeElementID := retrieveNodeElementIDbyCode(version_Code_to, cat_code);

            SELECT ev1.elementversionid
            INTO nodeElementVersionID
            FROM ELEMENTVERSION ev1
            join STRUCTUREELEMENTVERSION sev on ev1.elementversionid = sev.elementversionid                 
            WHERE sev.structureid = structureVersionID
            and ev1.elementid = nodeElementID;

            UPDATE ELEMENTVERSION ev
            SET ev.status = 'DISABLED'
            WHERE ev.elementversionid = nodeElementVersionID;
           
            insertLog('DISABLED CATEGORY CODE: ' || cat_code || ' YEAR: ' || version_Code_to || 
                ' NODE ELEMENT ID: ' || nodeElementID || ' NODE EV ID: ' || nodeElementVersionID);     

            INSERT INTO Z_ICD_DIFFS_NODES(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, ELEMENT_ID_FROM, ELEMENT_ID_TO, 
                CATEGORY_CODE, VERSIONCODE_FROM, VERSIONCODE_TO, STATUS)
            VALUES(DIFFS_SEQ.Nextval, rec_cc.category_id, null, nodeElementID, null, 
                cat_code, version_Code_fr, version_Code_to, 'DISABLED');
        end loop;

    exception
        when others then
            insertLog('Error in disableRemovedNodes --> ' || SQLCODE || ' ' || SQLERRM);  
    end disableRemovedNodes;


    /**************************************************************************************************************************************
    * NAME:          copyTree
    * DESCRIPTION:   Create a cursor which contains all the ELEMENTVERSIONs for the year
    *                Create a new ELEMENTVERSION for the new year, and attach to the existing ELEMENT ID
    *                and also attach to the new Structure ID
    *                Add it into a temporary diffs tabls, which is used to convert all other tables
    **************************************************************************************************************************************/    
    procedure copyTree(version_Code_fr number, version_Code_to number, oldStructureID number, structureVersionID number) is
        cursor c_data is            
            select s.ELEMENTVERSIONID, ev.elementid, s.contextstatus, ev.status
            FROM STRUCTUREELEMENTVERSION s
            join ELEMENTVERSION ev on s.elementversionid = ev.elementversionid
            WHERE s.STRUCTUREID = oldStructureID;

        rec_cc c_data%rowtype;
        elementVersionID number := 0;
        oldElementVersionID number := 0;
        oldElementID number := 0;
        oldStatus ELEMENTVERSION.STATUS%type;
        oldContextStatus STRUCTUREELEMENTVERSION.CONTEXTSTATUS%type;

        daClassID number := 0;
        oldDaDomainID number := 0;
        daDomainID number := 0;

    begin

        for rec_cc in c_data loop
            oldElementID := rec_cc.ELEMENTID;
            oldElementVersionID := rec_cc.ELEMENTVERSIONID;
            oldStatus := rec_cc.status;
            oldContextStatus := rec_cc.contextstatus;
            elementVersionID := ELEMENTVERSIONID_SEQ.Nextval;            

            --TODO:  Do not create a new ELEMENTVERSION, just attach the new structureVersionID to it
            --Need to create a record in ELEMENTVERSION
            insert into ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
            values (elementVersionID, oldElementID, version_Code_to, sysdate, oldStatus, null);

            --AND IN STRUCTUREELEMENT
            insert into STRUCTUREELEMENTVERSION (elementversionid, structureid, contextstatus, contextstatusdate, notes)
            values (elementVersionID, structureVersionID, oldContextStatus, sysdate, null);

            INSERT INTO Z_ICD_DIFFS_EV(DIFFSID, ELEMENTVERSIONID_FROM, ELEMENTVERSIONID_TO, ELEMENTID_FROM, VERSIONCODE_FROM, VERSIONCODE_TO, STATUS)
            VALUES(DIFFS_SEQ.Nextval, oldElementVersionID, elementVersionID, oldElementID, version_Code_fr, version_Code_to, null);  

        end loop;

        INSERT INTO CONCEPTVERSION (CONCEPTID)
        SELECT ev.ELEMENTVERSIONID_TO
        FROM Z_ICD_DIFFS_EV ev
        join CONCEPTVERSION c on ev.ELEMENTVERSIONID_FROM = c.conceptid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        INSERT INTO STRUCTUREVERSION (STRUCTUREID) 
        SELECT ev.ELEMENTVERSIONID_TO
        FROM Z_ICD_DIFFS_EV ev
        join STRUCTUREVERSION s on ev.ELEMENTVERSIONID_FROM = s.structureid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        INSERT INTO PROPERTYVERSION (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        SELECT ev.ELEMENTVERSIONID_TO, p.domainelementid, null, null
        FROM Z_ICD_DIFFS_EV ev
        join PROPERTYVERSION p on ev.elementversionid_from = p.propertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        insert into DATAPROPERTYVERSION (Datapropertyid, ISMETADATA)
        SELECT ev.ELEMENTVERSIONID_TO, dp.ismetadata
        FROM Z_ICD_DIFFS_EV ev
        join DATAPROPERTYVERSION dp on ev.elementversionid_from = dp.datapropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        insert into TEXTPROPERTYVERSION (TEXTPROPERTYID, LANGUAGECODE, TEXTTYPE, TEXT)
        SELECT ev.ELEMENTVERSIONID_TO, tp.languagecode, tp.texttype, tp.text
        FROM Z_ICD_DIFFS_EV ev
        join TEXTPROPERTYVERSION tp on ev.elementversionid_from = tp.textpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        insert into BOOLEANPROPERTYVERSION ( BOOLEANPROPERTYID, BOOLEANVALUE )
        SELECT ev.ELEMENTVERSIONID_TO, bp.booleanvalue
        FROM Z_ICD_DIFFS_EV ev
        join BOOLEANPROPERTYVERSION bp on ev.elementversionid_from = bp.booleanpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        insert into XMLPROPERTYVERSION (Xmlpropertyid, Languagecode, Xmlschemaurl, Xmltext)     
        SELECT ev.ELEMENTVERSIONID_TO, xp.languagecode, xp.xmlschemaurl, xp.xmltext
        FROM Z_ICD_DIFFS_EV ev
        join XMLPROPERTYVERSION xp on ev.elementversionid_from = xp.xmlpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        INSERT INTO URLPropertyVersion (Datapropertyid, Url)
        SELECT ev.ELEMENTVERSIONID_TO, up.url
        FROM Z_ICD_DIFFS_EV ev
        join URLPropertyVersion up on ev.elementversionid_from = up.datapropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;     

        INSERT INTO OtherPropertyVersion (Otherdatapropertyid, Dataformat, Datatype, Datasize, Datavalue)
        SELECT ev.ELEMENTVERSIONID_TO, op.dataformat, op.datatype, op.datasize, op.datavalue
        FROM Z_ICD_DIFFS_EV ev
        join OtherPropertyVersion op on ev.elementversionid_from = op.otherdatapropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;

        INSERT INTO DateTimePropertyVersion (Datetimepropertyid, Datetimevalue)
        SELECT ev.ELEMENTVERSIONID_TO, dtp.datetimevalue
        FROM Z_ICD_DIFFS_EV ev
        join DateTimePropertyVersion dtp on ev.elementversionid_from = dtp.datetimepropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;     

        INSERT INTO SpecializationVersion (Specializationid, Parentconceptid, Childconceptid)
        SELECT ev.ELEMENTVERSIONID_TO, sv.parentconceptid, sv.childconceptid
        FROM Z_ICD_DIFFS_EV ev
        join SpecializationVersion sv on ev.elementversionid_from = sv.specializationid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;     

        INSERT INTO GraphicsPropertyVersion (Graphicspropertyid, Languagecode, Graphicsblobvalue, Graphicformat)
        SELECT ev.ELEMENTVERSIONID_TO, gpv.languagecode, gpv.graphicsblobvalue, gpv.graphicformat
        FROM Z_ICD_DIFFS_EV ev
        join GraphicsPropertyVersion gpv on ev.elementversionid_from = gpv.graphicspropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;     

        INSERT INTO ConceptPropertyVersion (Conceptpropertyid, Rangeelementid, Inverseconceptpropertyid)
        SELECT ev.ELEMENTVERSIONID_TO, cpv.rangeelementid, cpv.inverseconceptpropertyid
        FROM Z_ICD_DIFFS_EV ev
        join ConceptPropertyVersion cpv on ev.elementversionid_from = cpv.conceptpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;     

        INSERT INTO ValidationRuleVersion (Ruleid)
        SELECT ev.ELEMENTVERSIONID_TO
        FROM Z_ICD_DIFFS_EV ev
        join ValidationRuleVersion vrv on ev.elementversionid_from = vrv.ruleid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;   

        INSERT INTO NumericPropertyVersion (Numericpropertyid, Numericformat, Numericvalue)
        SELECT ev.ELEMENTVERSIONID_TO, npv.numericformat, npv.numericvalue
        FROM Z_ICD_DIFFS_EV ev
        join NumericPropertyVersion npv on ev.elementversionid_from = npv.numericpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;   

        INSERT INTO EnumeratedPropertyVersion (Enumeratedpropertyid, Domainid, Domainvalueid)
        SELECT ev.ELEMENTVERSIONID_TO, epv.domainid, epv.domainvalueid
        FROM Z_ICD_DIFFS_EV ev
        join EnumeratedPropertyVersion epv on ev.elementversionid_from = epv.enumeratedpropertyid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to;   

        INSERT INTO ValueDomainVersion (Domainid)
        SELECT ev.ELEMENTVERSIONID_TO
        FROM Z_ICD_DIFFS_EV ev
        join ValueDomainVersion vdv on ev.elementversionid_from = vdv.domainid
        and ev.versioncode_from = version_Code_fr
        and ev.versioncode_to = version_Code_to; 

        daClassID := getICD10CAClassID('ValueDomainVersion', 'DaggerAsteriskDomain');
        oldDaDomainID := getDaggerAsteriskDomainID(daClassID, oldStructureID);
        daDomainID := getDaggerAsteriskDomainID(daClassID, structureVersionID);

        INSERT INTO ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        SELECT daDomainID, e.domainvalueid, e.languagecode, e.minnumericvalue, e.maxnumericvalue, e.literalvalue, e.description
        FROM ENUMERATION e
        WHERE e.domainid = oldDaDomainID;

    exception
        when others then
            insertLog('Error in copyTree --> ' || SQLCODE || ' ' || SQLERRM);
    end copyTree;


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
    
    end init_BaseClassification;


    /**************************************************************************************************************************************
    * NAME:          updateViewerRoot
    * DESCRIPTION:   The viewer Root for the new year still has the old year at the end of the text.  Replace with the new one.
    **************************************************************************************************************************************/ 
    procedure updateViewerRoot(version_Code_to number, structureVersionID number) is
        v_title varchar2(255);  
        viewerRootClassID number := 0;

    begin

        v_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED ' || 
                         'HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                         version_Code_to;  
        
        viewerRootClassID := getICD10CAClassID('ConceptVersion', 'ClassificationRoot');

        UPDATE TEXTPROPERTYVERSION t
        SET t.text = v_title
        WHERE t.textpropertyid IN (       
            select t.textPropertyID 
            from textpropertyVERSION t 
            join propertyVERSION p on t.textpropertyid = p.propertyid
            join STRUCTUREELEMENTVERSION sev on t.textpropertyid = sev.elementversionid
            join ELEMENT e on p.domainelementid = e.elementid
            WHERE sev.structureid = structureVersionID
            and e.classid = viewerRootClassID
            );
    
    end updateViewerRoot;


    /**************************************************************************************************************************************
    * NAME:          part1
    * DESCRIPTION:   (1) Creates a new Base Classification for the new year
    *                (2) Creates an IDENTICAL copy of the tree from the previous year
    *                (3) Updates the top level viewer root to contain the correct version year
    **************************************************************************************************************************************/   
    PROCEDURE part1(version_Code_fr number, version_Code_to number) is
        structureVersionID number := 0;
        logRunID number := 0;
        runStatus varchar2(10);

        oldStructureID number := 0;

    BEGIN
        f_year := version_Code_fr;
        dbms_output.enable(1000000);
        runStatus := checkRunStatus;

        oldStructureID := CIMS_ICD.GETICD10CASTRUCTUREIDBYYEAR(version_code_fr);

        IF runStatus = 'FALSE' THEN 
            dbms_output.put_line('Script already running....');
            --RETURN;
        END IF;

        --Ensure that the year does not already exist
        structureVersionID := CIMS_ICD.GETICD10CASTRUCTUREIDBYYEAR(version_code_to);

        IF structureVersionID != -9999 THEN 
            dbms_output.put_line(version_Code_to || ' already exists in ICD.  Exiting...');
            RETURN;
        END IF;
        
        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('ICD Year to Year Data Migration Run ID: ' || logRunID);

        insertLog('PART 1 of the Year to Year ICD10CA migration ' || version_Code_fr || ' to ' || version_Code_to);

        insertLog('Base classification for year ' || version_Code_to);
        init_BaseClassification(version_Code_to, structureVersionID);
        insertLog('End');

        insertLog('Copying node elements from ' || version_Code_fr || ' to ' || version_Code_to); 
        copyTree(version_Code_fr, version_Code_to, oldStructureID, structureVersionID);
        insertLog('End');

        insertLog('Viewer root adjustment');
        updateViewerRoot(version_Code_to, structureVersionID);
        insertLog('Done');

        DELETE FROM Z_ICD_DIFFS_EV;

        insertLog('Ending ICD10CA migration ' || version_Code_fr || ' to ' || version_Code_to);

        commit;
    exception
        when others then            
            raise_application_error(-20011, 'ASDF Error:' || substr(sqlerrm, 1, 50));

    END part1;


    /**************************************************************************************************************************************
    * NAME:          part2
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE part2(version_Code_fr number, version_Code_to number) is
        structureVersionID number := 0;
        logRunID number := 0;
        runStatus varchar2(10);
        oldStructureID number := 0;
        codeClassID number := 0;

    BEGIN
        f_year := version_Code_fr;
        dbms_output.enable(1000000);
        runStatus := checkRunStatus;

        codeClassID := getICD10CAClassID('TextPropertyVersion', 'Code');
        oldStructureID := CIMS_ICD.GETICD10CASTRUCTUREIDBYYEAR(version_code_fr);
        structureVersionID := CIMS_ICD.GETICD10CASTRUCTUREIDBYYEAR(version_code_to);

        IF runStatus = 'FALSE' THEN 
            dbms_output.put_line('Script already running....');
            --RETURN;
        END IF;

        DELETE FROM Z_ICD_DIFFS_NODES;

        --Ensure that the year exists
        IF ( (structureVersionID = -9999) OR (oldStructureID = -9999) ) THEN 
            dbms_output.put_line(version_Code_to || ' does not exist.  Exiting...');
            RETURN;
        END IF;
        
        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('ICD Year to Year Data Migration Run ID: ' || logRunID);

        insertLog('PART 2 of the Year to Year ICD10CA migration ' || version_Code_fr || ' to ' || version_Code_to);

        insertLog('Disabling Unused Nodes');
        disableRemovedNodes(version_Code_fr, version_Code_to, structureVersionID);
        insertLog('End');

        insertLog('Adding new Nodes');
        addNewNodes(version_Code_fr, version_Code_to, oldStructureID, structureVersionID);
        insertLog('End');

        insertLog('Attaching any orphaned nodes');
        attachOrphanNodes(version_Code_to, structureVersionID);
        insertLog('End');

        insertLog('Updating all text differences');
        updateTextDifferences(version_Code_to, structureVersionID, codeClassID);
        insertLog('End');

        insertLog('Updating all xml differences');
        updateXMLDifferences(version_Code_to, structureVersionID);
        insertLog('End');

        insertLog('Ending ICD10CA migration ' || version_Code_fr || ' to ' || version_Code_to);


--        rollback;
        commit;
    exception
        when others then            
            raise_application_error(-20011, 'ASDF Error:' || substr(sqlerrm, 1, 50));

    END part2;

end ICD_DATA_MIGRATION_YR_TO_YR;
/
