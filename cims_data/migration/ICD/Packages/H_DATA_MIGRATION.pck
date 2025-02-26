create or replace package H_DATA_MIGRATION is

    --icd_classification_code varchar2(20) := 'ICD-10-CA';
    errString varchar(4000);
    --function getClassID(tblName varchar2, cName varchar2) return number;
    PROCEDURE migrate_icd_data(version_Code IN number);

end H_DATA_MIGRATION;
/
create or replace package body H_DATA_MIGRATION is


    /**************************************************************************************************************************************
    * NAME:          getClassID
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    function getClassID(tblName varchar2, cName varchar2) return number is
        classID number;
    begin
        SELECT c.CLASSID 
        INTO classID FROM H_CLASS c WHERE TRIM(c.TABLENAME) = tblName AND TRIM(c.CLASSNAME) = cName;

        return classID;
    end getClassID;


    /**************************************************************************************************************************************
    * NAME:          getDaggerAsteriskDomainID
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    function getDaggerAsteriskDomainID(propertyClassID number) return number is
        domainID number;
    begin
        select vd.domainID
        INTO domainID
        from h_element e 
        join h_elementversion ev on e.elementid = ev.elementid
        join h_valuedomain vd on ev.elementversionid = vd.domainid
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
            FROM H_ENUMERATION e 
            WHERE e.literalvalue is null
            and e.domainid = did;
        else
            SELECT e.domainvalueid
            INTO dvid 
            FROM H_ENUMERATION e 
            WHERE TRIM(litValue) = e.literalvalue
            and e.domainid = did;
        end if;
        
    end getEnumerationID;


    /**************************************************************************************************************************************
    * NAME:          insertXMLProperty
    * DESCRIPTION:    
    **************************************************************************************************************************************/ 
    procedure insertXMLProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              xmlData clob, language_code char, addStructureElement boolean) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, null);
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into H_PROPERTY (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into H_DATAPROPERTY (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into H_XMLPROPERTY1 (Xmlpropertyid, Languagecode, Xmlschemaurl, Xmltext)
        values (elementVersionID, SUBSTR(language_code, 0, 3), 'XML', xmlData);

        if (addStructureElement) then
	        insert into H_STRUCTUREELEMENT (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
            values (elementVersionID, structureVersionID, 'DONTKNOW', SYSDATE, null);
        end if;

       commit;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := domainElementID || ' <-- element id ' || SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertXMLProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertXMLProperty;


    /**************************************************************************************************************************************
    * NAME:          insertBooleanProperty
    * DESCRIPTION:   Might need a flag to skip structures.  Not sure yet!
    **************************************************************************************************************************************/ 
    procedure insertBooleanProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              booleanProp char, skipStructure boolean) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, 't el w cID ' || propertyClassID || ':' || booleanProp);
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into H_PROPERTY (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into H_DATAPROPERTY (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into H_BOOLEANPROPERTY ( BOOLEANPROPERTYID, BOOLEANVALUE )
        values (elementVersionID, booleanProp);

        if (skipStructure) then
            insert into H_STRUCTUREELEMENT (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
            values (elementVersionID, structureVersionID, 'DONTKNOW', SYSDATE, null);
        end if;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertBooleanProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertBooleanProperty;  


    /**************************************************************************************************************************************
    * NAME:          insertTextProperty
    * DESCRIPTION:   Might need a flag to skip structures.  Not sure yet!
    **************************************************************************************************************************************/ 
    procedure insertTextProperty(p_version_code number, domainElementID number, propertyClassID number, structureVersionID number, 
              textProp varchar2, language_code char, skipStructure boolean) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, 't el w cID ' || propertyClassID || ':' || textProp);
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into H_PROPERTY (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into H_DATAPROPERTY (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into H_TEXTPROPERTY (TEXTPROPERTYID, LANGUAGECODE, TEXTTYPE, TEXT)
        values (elementVersionID, SUBSTR(language_code, 0, 3), 'SIM', textProp);

        if (skipStructure) then
	        insert into H_STRUCTUREELEMENT (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
            values (elementVersionID, structureVersionID, 'DONTKNOW', SYSDATE, null);
        end if;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in insertTextProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertTextProperty;           


    /**************************************************************************************************************************************
    * NAME:          insertEnumeratedProperty
    * DESCRIPTION:   
    **************************************************************************************************************************************/ 
    procedure insertEnumeratedProperty(p_version_code number, domainElementID number, propertyClassID number, domainID number, 
              structureVersionID number, litValue varchar2, skipStructure boolean) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
        domainValueID number := 0;
    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, propertyClassID, elementUUID, 'EnumProp ' || propertyClassID || ':' || litValue);
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into H_PROPERTY (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into H_DATAPROPERTY (Datapropertyid, ISMETADATA)
        values (elementVersionID, 'N');

        insert into H_ENUMERATEDPROPERTY (ENUMERATEDPROPERTYID)
        values (elementVersionID);

        getEnumerationID(litValue, domainID, domainValueID);


        insert into H_ENUMERATIONPROPERTYVALUE (ENUMERATEDPROPERTYID, DOMAINID, DOMAINVALUEID)
        values (elementVersionID, domainID, domainValueID);

        if (skipStructure) then
	        insert into H_STRUCTUREELEMENT (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
            values (elementVersionID, structureVersionID, 'DONTKNOW', SYSDATE, null);
        end if;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            insert into h_log(message) values('lit value: ' || litValue || ' domainID: ' || domainID || ' domainValueID: ' || domainValueID);
            commit;
            raise_application_error(-20011, 'Error occurred in insertEnumeratedProperty. <br> Error:' || substr(sqlerrm, 1, 50));

    end insertEnumeratedProperty;     


    /**************************************************************************************************************************************
    * NAME:          populateDaggerAsteriskLookup
    * DESCRIPTION:    
    **************************************************************************************************************************************/ 
    procedure populateDaggerAsteriskLookup(p_version_code number) is

        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;
        daggerAsteriskClassID number :=0;
    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        daggerAsteriskClassID := getClassID('EnumeratedProperty', 'DaggerAsterisk');

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, daggerAsteriskClassID, elementUUID, 'Dagger Asterisk');
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', 'Dagger Asterisk');

        insert into H_VALUEDOMAIN (DOMAINID)
        values (elementVersionID);

        insert into H_ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, 1, 'ENG', null, null, '', 'Dagger Asterisk: Null Value'); 

        insert into H_ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, 2, 'ENG', null, null, '*', 'Dagger Asterisk: *');

        insert into H_ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, 3, 'ENG', null, null, '+', 'Dagger Asterisk: +');

        insert into H_ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, 4, 'ENG', null, null, 'Y', 'Dagger Asterisk: Y');

        insert into H_ENUMERATION (DOMAINID, DOMAINVALUEID, LANGUAGECODE, MINNUMERICVALUE, MAXNUMERICVALUE, LITERALVALUE, DESCRIPTION)
        values (elementVersionID, 5, 'ENG', null, null, 'N', 'Dagger Asterisk: N');

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := 'Error inside populateDaggerAsteriskLookup: ' || SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in populateDaggerAsteriskLookup. <br> Error:' || substr(sqlerrm, 1, 50));
                  
    end populateDaggerAsteriskLookup;


    /**************************************************************************************************************************************
    * NAME:          migrateIncludeExclude
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    procedure migrateIncludeExclude(p_version_code varchar2, icd_category_ID number, domainElementID number) is
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

            
            --Sanitize XML 
            category_detail_data := REPLACE(category_detail_data, '&laquo;', '&#171;');
            category_detail_data := REPLACE(category_detail_data, '&raquo;', '&#187;');

            --EXCLUDE
            IF TRIM(category_detail_type_code) = 'E' THEN
                classID := getClassID('XMLProperty', 'EXCLUDE_PRESENTATION');
                --insertXMLProperty(p_version_code, domainElementID, classID, null, XMLType(category_detail_data), language_code, false);
                insertXMLProperty(p_version_code, domainElementID, classID, null, category_detail_data, language_code, false);
            --INCLUDE
            ELSIF TRIM(category_detail_type_code) = 'I' THEN
                classID := getClassID('XMLProperty', 'INCLUDE_PRESENTATION');  
                --insertXMLProperty(p_version_code, domainElementID, classID, null, XMLType(category_detail_data), language_code, false);
                insertXMLProperty(p_version_code, domainElementID, classID, null, category_detail_data, language_code, false);
            ELSIF TRIM(category_detail_type_code) = 'A' THEN
                  classID := getClassID('XMLProperty', 'CODE_ALSO_PRESENTATION');
                --insertXMLProperty(p_version_code, domainElementID, classID, null, XMLType(category_detail_data), language_code, false);
                insertXMLProperty(p_version_code, domainElementID, classID, null, category_detail_data, language_code, false);
            ELSIF TRIM(category_detail_type_code) = 'O' THEN
                  classID := getClassID('XMLProperty', 'OMIT_CODE_PRESENTATION');
                --insertXMLProperty(p_version_code, domainElementID, classID, null, XMLType(category_detail_data), language_code, false);
                insertXMLProperty(p_version_code, domainElementID, classID, null, category_detail_data, language_code, false);
            ELSIF TRIM(category_detail_type_code) = 'N' THEN
                  classID := getClassID('XMLProperty', 'NOTE_PRESENTATION');
                --insertXMLProperty(p_version_code, domainElementID, classID, null, XMLType(category_detail_data), language_code, false);
                insertXMLProperty(p_version_code, domainElementID, classID, null, category_detail_data, language_code, false); 
            ELSE
                  dbms_output.put_line('UNK CDTC ' || category_detail_type_code);                    
            END IF;

        end loop;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000)); 
            errString := 'Error with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in migrateIncludeExclude. <br> Error:' || substr(sqlerrm, 1, 50));

    end migrateIncludeExclude;


    /**************************************************************************************************************************************
    * NAME:          migrateTableOutput
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    procedure migrateTableOutput(p_version_code varchar2, icd_category_ID number, domainElementID number) is
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

            --Sanitize XML 
            category_table_output_data := REPLACE(category_table_output_data, '&laquo;', '&#171;');
            category_table_output_data := REPLACE(category_table_output_data, '&raquo;', '&#187;');

            classID := getClassID('XMLProperty', 'TABLE_OUTPUT');
            insertXMLProperty(p_version_code, domainElementID, classID, null, category_table_output_data, language_code, false);

        end loop;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000)); 
            errString := 'Error in migrateTableOutput with icd_category_ID ' || icd_category_ID || ' --> ' || SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in migrateTableOutput. <br> Error:' || substr(sqlerrm, 1, 50));

    end migrateTableOutput;


    /**************************************************************************************************************************************
    * NAME:          buildNarrowRelationship
    * DESCRIPTION:   Builds the parent-child relationship between two nodes in a tree
    *
    *  select * 
    *  from H_conceptProperty cp 
    *  join H_Property p on cp.conceptpropertyid = p.propertyid
    *  join H_Element e on p.domainelementid = e.elementid
    *  where cp.rangeelementid = 564  <-- Parent Node
    **************************************************************************************************************************************/    
    procedure buildNarrowRelationship(p_version_code varchar2, relationshipClassID number, domainElementID number, rangeElementID number) is
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

    begin
        elementID := H_ELEMENTID.Nextval;
        elementVersionID := H_ELEMENTVERSIONID.Nextval; 
        elementUUID := H_ELEMENTUUID.Nextval;

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (elementID, relationshipClassID, elementUUID, elementID || ' Build Narrow Relationship w classID ' || relationshipClassID );
        
        insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', null);

        insert into H_PROPERTY (PROPERTYID, DOMAINELEMENTID, PARENTPROPERTYID, PROPERTYCATEGORYID)
        values (elementVersionID, domainElementID, null, null);

        insert into H_CONCEPTPROPERTY (CONCEPTPROPERTYID, RANGEELEMENTID, INVERSECONCEPTPROPERTYID)
        values ( elementVersionID, rangeElementID, null);
        
    end buildNarrowRelationship;


    /**************************************************************************************************************************************
    * NAME:          migrateChildNodes
    * DESCRIPTION:   Recursive procedure to migrate child nodes
    **************************************************************************************************************************************/    
    procedure migrateChildNodes(p_version_code varchar2, parentElementID number, parentCategoryID number) is
        cursor c is
            select *
            from icd.category ic
            where ic.parent_category_id = parentCategoryID
            and ic.clinical_classification_code like '10CA' || p_version_code || '%'
            order by ic.category_code;

        rec_cc c%rowtype;
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

        categoryID number := 0;
        v_short_title varchar2(255);  
        v_long_title varchar2(255);
        v_user_title varchar2(255);
        v_code VARCHAR2(12);
        v_ca_enhancement_flag VARCHAR2(1);
        v_dagger_asterisk VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);

        daDomainID number := 0;
        categoryTypeCode VARCHAR2(10);
        nodeType VARCHAR2(10);

        nodeClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
    
    begin

        for rec_cc in c loop
            elementID := H_ELEMENTID.Nextval;
            elementVersionID := H_ELEMENTVERSIONID.Nextval; 
            elementUUID := H_ELEMENTUUID.Nextval;

            categoryID := rec_cc.category_id;
            categoryTypeCode := rec_cc.category_type_code;
            v_short_title := rec_cc.short_desc;
            v_long_title := rec_cc.long_desc;
            v_user_title := rec_cc.user_desc;
            v_code := rec_cc.category_code;
            v_ca_enhancement_flag := rec_cc.ca_enhancement_flag;
            v_dagger_asterisk := rec_cc.dagger_asterisk;
            v_code_flag := rec_cc.code_flag;
            v_render_child_flag := rec_cc.render_children_as_table_flag;
            
            IF TRIM(categoryTypeCode) = 'BL1' THEN
                nodeClassID := getClassID('Concept', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'BL2' THEN
                nodeClassID := getClassID('Concept', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'BL3' THEN
                nodeClassID := getClassID('Concept', 'Block');
                nodeType := 'BLOCK';
            ELSIF TRIM(categoryTypeCode) = 'CAT1' THEN
                nodeClassID := getClassID('Concept', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CAT2' THEN
                nodeClassID := getClassID('Concept', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CAT3' THEN
                nodeClassID := getClassID('Concept', 'Category');
                nodeType := 'CATEGORY';
            ELSIF TRIM(categoryTypeCode) = 'CODE' THEN
                nodeClassID := getClassID('Concept', 'Category');
                nodeType := 'CODE';
            ELSE 
                dbms_output.put_line('ERROR!: Category Type Code ' || categoryTypeCode || ' is unknown'); 
                insert into h_log(message) values('catgorytype code is not right ' || categoryTypeCode);
                commit;
                raise_application_error(-20011, 'Error occurred in migrateChildNodes.');
            END IF;

            insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
            values (elementID, nodeClassID, elementUUID, nodeType || ' ' || v_code);
        
            insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
            values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', nodeType || ' ' || v_code);

            insert into H_CONCEPT (CONCEPTID) 
            values (elementVersionID);
        
            --insert into H_STRUCTUREELEMENT (elementversionid, structureid, contextstatus, contextstatusdate, notes)
            --values (elementVersionID, structureVersionID, 'ACTIVE', sysdate, nodeType || ' ' || v_chapter_code);

            -- Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, categoryID, elementID);

            -- Table Output
            migrateTableOutput(p_version_code, categoryID, elementID);

            --Store Short title
            propertyClassID := getClassID('TextProperty', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, null, v_short_title, 'ENG', false);

            --Store Long title
            propertyClassID := getClassID('TextProperty', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, null, v_long_title, 'ENG', false);

            --Store User title
            propertyClassID := getClassID('TextProperty', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, null, v_user_title, 'ENG', false);

            --Store Code value (Category Code in ICD)
            propertyClassID := getClassID('TextProperty', 'Code');             
            insertTextProperty(p_version_code, elementID, propertyClassID, null, v_code, 'ENG', false);

            --Store CA Enhancement flag
            IF ( LENGTH(trim(v_ca_enhancement_flag)) ) > 0 THEN
                propertyClassID := getClassID('BooleanProperty', 'CaEnhancementFlag');             
                insertBooleanProperty(p_version_code, elementID, propertyClassID, null, v_ca_enhancement_flag, false);
            END IF;

            --Store Dagger Asterisk
            propertyClassID := getClassID('EnumeratedProperty', 'DaggerAsterisk');             
            daDomainID := getDaggerAsteriskDomainID(propertyClassID);
            insertEnumeratedProperty(p_version_code, elementID, propertyClassID, daDomainID, null, Trim(v_dagger_asterisk), false);

            --Store Code flag
            propertyClassID := getClassID('BooleanProperty', 'CodeFlag');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, null, v_code_flag, false);

            --Store Render Children as Table flag
            propertyClassID := getClassID('BooleanProperty', 'RenderChildrenAsTableFlag');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, null, v_render_child_flag, false);

            --Build Narrow Relationship
            relationshipClassID := getClassID('Relationship', 'Narrower');             
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, parentElementID);
            
            --Recursively call
            migrateChildNodes(p_version_code, elementID, categoryID);

        end loop;
        commit;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in migrateChildNodes. <br> Error:' || substr(sqlerrm, 1, 50));
    end migrateChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateChapterNodes
    * DESCRIPTION:   Migrates the chapters 
    **************************************************************************************************************************************/    
    procedure migrateChapterNodes(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c_chapter is
            select *
            from icd.category ic
            where ic.category_type_code = 'CHP'
            and ic.clinical_classification_code like '10CA' || p_version_code || '%'
            order by ic.category_code;

        rec_cc c_chapter%rowtype;
        elementID number := 0;
        elementVersionID number := 0;
        elementUUID number := 0;

        chapter_category_ID number := 0;
        v_chapter_short_title varchar2(255);  
        v_chapter_long_title varchar2(255);
        v_chapter_user_title varchar2(255);
        v_chapter_code VARCHAR2(12);
        v_ca_enhancement_flag VARCHAR2(1);
        v_dagger_asterisk VARCHAR2(1);
        v_code_flag VARCHAR2(1);
        v_render_child_flag VARCHAR2(1);

        daDomainID number := 0;
        chapterClassID number := 0;
        propertyClassID number := 0;
        relationshipClassID number := 0;
    
    begin
        chapterClassID := getClassID('Concept', 'Chapter'); 
    
        for rec_cc in c_chapter loop
            elementID := H_ELEMENTID.Nextval;
            elementVersionID := H_ELEMENTVERSIONID.Nextval; 
            elementUUID := H_ELEMENTUUID.Nextval;

            chapter_category_ID := rec_cc.category_id;
            v_chapter_short_title := rec_cc.short_desc;
            v_chapter_long_title := rec_cc.long_desc;
            v_chapter_user_title := rec_cc.user_desc;
            v_chapter_code := rec_cc.category_code;
            v_ca_enhancement_flag := rec_cc.ca_enhancement_flag;
            v_dagger_asterisk := rec_cc.dagger_asterisk;
            v_code_flag := rec_cc.code_flag;
            v_render_child_flag := rec_cc.render_children_as_table_flag;

            -- Chapter
            insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
            values (elementID, chapterClassID, elementUUID, 'Chapter' || v_chapter_code);
        
            insert into H_ELEMENTVERSION  (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
            values (elementVersionID, elementID, p_version_code, sysdate, 'ACTIVE', 'Chapter ' || v_chapter_code);

            insert into H_CONCEPT (CONCEPTID) 
            values (elementVersionID);
        
            insert into H_STRUCTUREELEMENT (elementversionid, structureid, contextstatus, contextstatusdate, notes)
            values (elementVersionID, structureVersionID, 'ACTIVE', sysdate, 'Chapter ' || v_chapter_code);

            -- Chapter Includes/Excludes/Text
            migrateIncludeExclude(p_version_code, chapter_category_ID, elementID);

            -- Chapter Table Output
            migrateTableOutput(p_version_code, chapter_category_ID, elementID);

            --Store Chapters Short title
            propertyClassID := getClassID('TextProperty', 'ShortTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_short_title, 'ENG', true);

            --Store Chapters Long title
            propertyClassID := getClassID('TextProperty', 'LongTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_long_title, 'ENG', true);

            --Store Chapters User title
            propertyClassID := getClassID('TextProperty', 'UserTitle');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_user_title, 'ENG', true);

            --Store Chapters Code value (Category Code in ICD)
            propertyClassID := getClassID('TextProperty', 'Code');             
            insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_chapter_code, 'ENG', true);

            --Store Chapters CA Enhancement flag
            IF ( LENGTH(trim(v_ca_enhancement_flag)) ) > 0 THEN
                propertyClassID := getClassID('BooleanProperty', 'CaEnhancementFlag');             
                insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_ca_enhancement_flag, true);
            END IF;

            --Store Chapters Dagger Asterisk
            propertyClassID := getClassID('EnumeratedProperty', 'DaggerAsterisk');   
            daDomainID := getDaggerAsteriskDomainID(propertyClassID);          
            insertEnumeratedProperty(p_version_code, elementID, propertyClassID, daDomainID, structureVersionID, Trim(v_dagger_asterisk), true);

            --Store Chapters Code flag
            propertyClassID := getClassID('BooleanProperty', 'CodeFlag');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_code_flag, true);

            --Store Chapters Render Children as Table flag
            propertyClassID := getClassID('BooleanProperty', 'RenderChildrenAsTableFlag');             
            insertBooleanProperty(p_version_code, elementID, propertyClassID, structureVersionID, v_render_child_flag, true);

            --Build Narrow Relationship
            relationshipClassID := getClassID('Relationship', 'Narrower');             
            buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID);
            
            
            migrateChildNodes(p_version_code, elementID, chapter_category_ID);

        end loop;
        commit;

    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));
            errString := SQLCODE || ' ' || SQLERRM;
            insert into h_log(message) values(errString);
            commit;
            raise_application_error(-20011, 'Error occurred in migrateChapterNodes. <br> Error:' || substr(sqlerrm, 1, 50));
    end migrateChapterNodes;


    /**************************************************************************************************************************************
    * NAME:          init_BaseClassification
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/    
    procedure init_BaseClassification(p_version_code varchar2, structureVersionID OUT number) is
        bcClassID number := 0;
        bcElementID number := 0;
        bcElementUUID number := 0;
        bcElementVersionID number := 0;
    begin
        bcElementID := h_elementid.nextval;
        bcElementUUID := h_elementuuid.nextval; 
        bcElementVersionID := h_elementversionid.nextval;
        structureVersionID := bcElementVersionID;

        bcClassID := getClassID('BaseClassification', 'ICD-10-CA'); 

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (bcElementID, bcClassID, bcElementUUID, 'BaseClassification');
    
        insert into H_ELEMENTVERSION (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (bcElementVersionID, bcElementID, p_version_code, sysdate, 'ACTIVE', p_version_code || 'Base');
    
        insert into H_STRUCTURE (STRUCTUREID) values (bcElementVersionID);
        commit;
    
    end init_BaseClassification;


    /**************************************************************************************************************************************
    * NAME:          createViewerRoot
    * DESCRIPTION:   tbd
    **************************************************************************************************************************************/ 
    procedure createViewerRoot(p_version_code varchar2, structureVersionID number, viewerRootID out number) is
        bcClassID number := 0;
        bcElementID number := 0;
        bcElementVersionID number := 0;
        propertyClassID number := 0;
        v_short_title varchar2(255);  
        v_long_title varchar2(255);
        v_user_title varchar2(255);

    begin
        bcElementID := h_elementid.nextval;
        bcElementVersionID := h_elementversionid.nextval;
        viewerRootID := bcElementID;

        v_short_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                      p_version_code;  
        v_long_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                      p_version_code;
        v_user_title := 'ICD-10-CA INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || 
                      p_version_code;

        bcClassID := getClassID('Concept', 'ViewerRoot'); 

        insert into H_ELEMENT (ELEMENTID, CLASSID, ELEMENTUUID, NOTES) 
        values (bcElementID, bcClassID, h_elementuuid.nextval, 'ViewerRoot');
    
        insert into H_ELEMENTVERSION (ELEMENTVERSIONID, ELEMENTID, VERSIONCODE, VERSIONTIMESTAMP, STATUS, NOTES)
        values (bcElementVersionID, bcElementID, p_version_code, sysdate, 'ACTIVE', p_version_code || 'ViewerRoot');

        insert into H_CONCEPT (CONCEPTID) 
        values (bcElementVersionID);
    
        insert into H_STRUCTUREELEMENT (ELEMENTVERSIONID, STRUCTUREID, CONTEXTSTATUS, CONTEXTSTATUSDATE, NOTES)
        values (bcElementVersionID, structureVersionID, 'DONTKNOW', SYSDATE, null);
   
        --Store Short title
        propertyClassID := getClassID('TextProperty', 'ShortTitle');             
        insertTextProperty(p_version_code, bcElementID, propertyClassID, structureVersionID, v_short_title, 'ENG', true);

        --Store Long title
        propertyClassID := getClassID('TextProperty', 'LongTitle');             
        insertTextProperty(p_version_code, bcElementID, propertyClassID, structureVersionID, v_long_title, 'ENG', true);

        --Store User title
        propertyClassID := getClassID('TextProperty', 'UserTitle');             
        insertTextProperty(p_version_code, bcElementID, propertyClassID, structureVersionID, v_user_title, 'ENG', true);

        commit;
    
    end createViewerRoot;


    /**************************************************************************************************************************************
    * NAME:          cleanUp_Tables
    * DESCRIPTION:   Clean up procedure
    **************************************************************************************************************************************/   
    procedure cleanUp_Tables is
    begin
        delete from H_TEXTPROPERTY;
        delete from H_DATAPROPERTY;
        --delete from H_XMLPROPERTY;        
        delete from H_XMLPROPERTY1;
        delete from H_ENUMERATION;
        delete from H_VALUEDOMAIN;
        delete from H_ENUMERATEDPROPERTY;
        delete from H_ENUMERATIONPROPERTYVALUE;
        delete from H_BooleanProperty;
        delete from H_property;
        delete from H_STRUCTUREELEMENT;
        delete from H_STRUCTURE;
        delete from H_ELEMENTVERSION;
        delete from H_ELEMENT;
        delete from H_CLASS;
        delete from H_LANGUAGE;
        delete from H_CONCEPT;
        delete from H_LOG;
    
        commit;
    
    end cleanUp_Tables;


    /**************************************************************************************************************************************
    * NAME:          populate_lookUp
    * DESCRIPTION:   Need to figure out what to do with the CLASSID.  May create another sequence
    **************************************************************************************************************************************/   
    procedure populate_lookUp is
    begin
        -- CLASS TABLE
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (99, 'Concept', 'ICD-10-CA', 'ViewerRoot', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (100, 'BaseClassification', 'ICD-10-CA', 'ICD-10-CA', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (101, 'Concept', 'ICD-10-CA', 'Chapter', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (102, 'Concept', 'ICD-10-CA', 'Block', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (103, 'Concept', 'ICD-10-CA', 'Category', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (104, 'TextProperty', 'ICD-10-CA', 'Code', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (105, 'TextProperty', 'ICD-10-CA', 'ShortTitle', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (106, 'TextProperty', 'ICD-10-CA', 'UserDesc', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (107, 'EnumeratedProperty', 'ICD-10-CA', 'ValidCode', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (108, 'Relationship', 'ICD-10-CA', 'Narrower', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (109, 'ValueDomain', 'ICD-10-CA', 'YN', null);

        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (188, 'TextProperty', 'ICD-10-CA', 'INCLUDE_DIRECTIVE', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (189, 'XMLProperty', 'ICD-10-CA', 'INCLUDE_PRESENTATION', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (183, 'XMLProperty', 'ICD-10-CA', 'EXCLUDE_PRESENTATION', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (184, 'XMLProperty', 'ICD-10-CA', 'CODE_ALSO_PRESENTATION', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (185, 'XMLProperty', 'ICD-10-CA', 'OMIT_CODE_PRESENTATION', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (186, 'XMLProperty', 'ICD-10-CA', 'NOTE_PRESENTATION', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (187, 'XMLProperty', 'ICD-10-CA', 'TABLE_OUTPUT', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1001, 'TextProperty', 'ICD-10-CA', 'LongTitle', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1002, 'TextProperty', 'ICD-10-CA', 'UserTitle', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1003, 'BooleanProperty', 'ICD-10-CA', 'CaEnhancementFlag', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1004, 'EnumeratedProperty', 'ICD-10-CA', 'DaggerAsterisk', null);

        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1005, 'BooleanProperty', 'ICD-10-CA', 'CodeFlag', null);
        insert into H_CLASS (CLASSID, TABLENAME, BASECLASSIFICATIONNAME, CLASSNAME, NOTES) values (1006, 'BooleanProperty', 'ICD-10-CA', 'RenderChildrenAsTableFlag', null);

        --LANGUAGE
        insert into H_LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('ENG', 'English');
        insert into H_LANGUAGE (LANGUAGECODE, LANGUAGEDESCRIPTION) values ('FRA', 'French');
    
        commit;
    
    end populate_lookUp;


    /**************************************************************************************************************************************
    * NAME:          migrate_icd_data
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE migrate_icd_data(version_Code number) is
        structureVersionID number := 0;
        viewerRootElementID number := 0;

    BEGIN
        dbms_output.enable(1000000);
        --dbms_output.disable;
        dbms_output.put_line('Hi Howard');
        cleanUp_Tables;
        populate_lookUp;   
        populateDaggerAsteriskLookup(version_Code);     
        init_BaseClassification(version_Code, structureVersionID);
        createViewerRoot(version_Code, structureVersionID, viewerRootElementID);
        migrateChapterNodes(version_Code, structureVersionID, viewerRootElementID); 
    END migrate_icd_data;


end H_DATA_MIGRATION;
/
