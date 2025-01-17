create or replace package CCI_DATA_MIGRATION_INDEX is

    cci_classification_code varchar2(20) := 'CCI';
    f_year number := 0;

    TYPE book_index_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    book_index book_index_t;

    TYPE book_index_lang_t IS TABLE OF VARCHAR2(255) INDEX BY VARCHAR2(3);
    book_index_language book_index_lang_t;
    book_index_description book_index_lang_t;
    book_index_typecode book_index_lang_t;

    TYPE letter_index_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    letter_index letter_index_t;

    --TYPE seeAlsoFlag_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    --saf seeAlsoFlag_t;

    --TYPE da_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    --da da_t;

    TYPE xml_t IS TABLE OF VARCHAR2(3000) INDEX BY VARCHAR2(3);
    index_ref xml_t;
    index_cat xml_t;

    PROCEDURE main(version_Code IN varchar2);
    --procedure buildXML(p_version_code varchar2, structureVersionID number, indexClassId number);
end CCI_DATA_MIGRATION_INDEX;
/
create or replace package body CCI_DATA_MIGRATION_INDEX is


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
        values (logID, message, logDate, cci_classification_code, f_year, logRunID);

       commit;

    end insertLog;


    /**************************************************************************************************************************************
    * NAME:          populateBookIndexLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure populateBookIndexLookup(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c is
            select
                i.book_index_id, i.language_code, i.index_type_code --, i.book_index_desc, t.index_type_desc
            from cci.book_index i
            join cci.index_type t on i.index_type_code = t.index_type_code
            where i.clinical_classification_code = 'CCI' || p_version_code
            order by i.index_type_code;

        rec_cc c%rowtype;
        bookIndexClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'BookIndex');
        bookIndexCode varchar2(1);
        bookIndexID number;
        bookIndexDesc varchar2(255);
        --bookIndexTypeDesc varchar2(255);
        relationshipClassID number;
        elementID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        languageCode varchar(3);
        propertyClassID number := 0;

        bookIndexDesc_ENG varchar2(255) := 'Alphabetical Index';
        bookIndexDesc_FRA varchar2(255) := 'Index Alphabétique';

        indexXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd">';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c loop
            bookIndexCode := TRIM(rec_cc.index_type_code);
            bookIndexID := TRIM(rec_cc.book_index_id);
            --bookIndexDesc := TRIM(rec_cc.book_index_desc);
            --bookIndexTypeDesc := TRIM(rec_cc.index_type_desc);
            languageCode := TRIM(rec_cc.language_code);

            if (languageCode = 'ENG') then
                bookIndexDesc := bookIndexDesc_ENG;
            else
                bookIndexDesc := bookIndexDesc_FRA;
            end if;

            elementID := CCI_DATA_MIGRATION.insertConcept(p_version_code, bookIndexClassID, null, structureVersionID, status_code);

            propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexCode');
            CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                bookIndexCode, languageCode);

            propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexDesc');
            CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                bookIndexDesc, null);


            indexXML := indexXMLTop;
            indexXML := indexXML || '<index language="' || languageCode || '" classification="CCI">';
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || bookIndexCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>BOOK_INDEX</INDEX_TYPE><LEVEL_NUM></LEVEL_NUM><SEE_ALSO_FLAG>X</SEE_ALSO_FLAG></index>';
            indexXML := indexXML || indexXMLEnd;

            propertyClassID := CIMS_CCI.getCCIClassID('XMLPropertyVersion', 'IndexRefDefinition');
            CCI_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                indexXML, languageCode);

            --Build Narrow Relationship
            relationshipClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            CCI_DATA_MIGRATION.buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID, structureVersionID);

            book_index(bookIndexID) := elementID;
            book_index_language(bookIndexID) := languageCode;
            book_index_description(bookIndexID) := bookIndexDesc;
            book_index_typecode(bookIndexID) := bookIndexCode;

        end loop;

    exception
        when others then
            insertLog('Error occured in populateBookIndexLookup procedure');
            insertLog('Error inside populateBookIndexLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateBookIndexLookup. <br> Error:' || substr(sqlerrm, 1, 512));

    end populateBookIndexLookup;


    /**************************************************************************************************************************************
    * NAME:          insertReferenceIndex
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*function insertReferenceIndex(p_version_code varchar2, structureVersionID number, refDescription varchar2)
        return number
    is
        elementID number := 0;
        status_code varchar(10);
        mainClassID number := 0;
        propertyClassID number := 0;

    begin
        status_code := 'ACTIVE';
        mainClassID := CIMS_CCI.getCCIClassID('ConceptVersion', 'ReferenceIndex');

        elementID := CCI_DATA_MIGRATION.insertConcept(p_version_code, mainClassID, null, structureVersionID, status_code);

        --Reference Index Description
        propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'RefLinkDesc');
        CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, refDescription, null);

        return elementid;

    exception
        when others then
            insertLog('insertReferenceIndex ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertReferenceIndex. <br> Error:' || substr(sqlerrm, 1, 512));
    end insertReferenceIndex;
    */


    /**************************************************************************************************************************************
    * NAME:          buildIndexReferenceLinks
    * DESCRIPTION:   Z_ICD_TEMP column description
    *                A: ICD index term ID
    *                B: Index Term Element ID
    *                C: ICD reference index term ID
    *                D: Index Lead Term Element ID
    *                E: ICD reference index term description
    *                F: Indicator record is INDEX related
    **************************************************************************************************************************************/
    procedure buildIndexReferenceLinks(structureVersionID number) is
        cursor c is
            select z.A, z.B, z.D, z1.G, z1.b refelementid, r.reference_link_desc
            from z_icd_temp z
            join cci.index_term_reference r on z.a = r.index_term_id
            left outer join z_icd_temp z1 on r.reference_index_term_id = z1.a and z1.f = 'CCIINDEX'
            where z.f = 'CCIINDEX';

        rec_cc c%rowtype;
        elementID number;
        refElementID number;
        refDescription varchar2(255);
        indexLeadTermElementID number;
        indexDescription varchar2(3000);

        indexXMLTop VARCHAR2(500) := '<INDEX_REF>';
        indexXMLEnd VARCHAR2(50) := '</INDEX_REF>';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c loop
            elementID := TRIM(rec_cc.b);
            refElementID := TRIM(rec_cc.refelementid);
            refDescription := TRIM(rec_cc.reference_link_desc);
            indexLeadTermElementID := TRIM(rec_cc.d);
            indexDescription := TRIM(rec_cc.g);

            --If both the Ref Index Element ID and the Description is null, then we dont have a
            IF (refDescription is null) AND (refElementID is null) THEN
                --Skip out
                CONTINUE;
            END IF;

            indexXML := indexXMLTop;
            --indexXML := indexXML || '<REF_DESC>' || DBMS_XMLGEN.CONVERT(indexDescription) || '</REF_DESC>';
            indexXML := indexXML || '<REFERENCE_LINK_DESC>' || DBMS_XMLGEN.CONVERT(nvl(refDescription,'')) || '</REFERENCE_LINK_DESC>';
            indexXML := indexXML || '<CONTAINER_INDEX_ID>' || cims_util.retrieveContainingIdPathbyEId('CCI', structureVersionID, refElementID) || '</CONTAINER_INDEX_ID>';
            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 11, null, null, indexXML, null, null, null);

        end loop;

    exception
        when others then
            insertLog('Error occured in buildIndexReferenceLinks procedure. ' ||TRIM(rec_cc.a)||' '||TRIM(rec_cc.b));
            insertLog('Error inside buildIndexReferenceLinks: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildIndexReferenceLinks. Error:' || substr(sqlerrm, 1, 512));

    end buildIndexReferenceLinks;


    /**************************************************************************************************************************************
    * NAME:          buildIndexCategoryLinks
    * DESCRIPTION:
    *
    *                Documenting the Index Category reference
    *
    *                	a.  Create the reference between the index and category
    *                		This is the 'Main Code Value'
    *                	b.  If the DaggerFlag is Y, Dagger is Y.  Otherwise N
    *
    *
    **************************************************************************************************************************************/
    procedure buildIndexCategoryLinks(structureVersionID number) is
        cursor c is
            select z.A, z.B index_elementid, r.category_id, nvl(r.dagger_asterisk, 'N') dagger, z1.b elementID, z1.e catCode
            from z_icd_temp z
            join cci.index_category_reference r on z.a = r.index_term_id
            left outer join z_icd_temp z1 on r.category_id = z1.a and z1.f = 'CCI'
            where z.f = 'CCIINDEX';

        rec_cc c%rowtype;
        daggerAsterisk varchar2(1);
        elementID number := 0;
        dagger varchar2(30);
        index_elementid number;
        eid number;
        categoryCode varchar2(30);
        sortingString varchar2(50);

        indexXMLTop VARCHAR2(500) := '<CATEGORY_REFERENCE>';
        indexXMLEnd VARCHAR2(50) := '</CATEGORY_REFERENCE>';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c loop
            dagger := TRIM(rec_cc.dagger);
            index_elementid := TRIM(rec_cc.index_elementid);
            eid := TRIM(rec_cc.elementid);
            categoryCode := TRIM(rec_cc.catCode);

            --Skip out when the index doesnt actually exist
            --Investigated, and it seems bad data.  Some indexes dont have a proper lead term, so its not migrated
            --Now also due to Neoplasm and other index, we wil get a lot of these cases
            if (index_elementid is null) then
                insertLog('Index Element ID is null, due to bad index data. SKIP!');
                CONTINUE;
            end if;

            indexXML := indexXMLTop;

            daggerAsterisk := '';
            sortingString := 'aaa-sort-string-ccc###' || CIMS_CCI.formatCode(categoryCode);

            indexXML := indexXML || '<MAIN_CODE_PRESENTATION>' || CIMS_CCI.formatCode(categoryCode) || '</MAIN_CODE_PRESENTATION>';
            indexXML := indexXML || '<MAIN_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('CCI', structureVersionID, eid) || '</MAIN_CONTAINER_CONCEPT_ID>';
            indexXML := indexXML || '<MAIN_CODE>' || CIMS_CCI.formatCode(categoryCode) || '</MAIN_CODE>';
            indexXML := indexXML || '<MAIN_DAGGER_ASTERISK>' || daggerAsterisk || '</MAIN_DAGGER_ASTERISK>';
            indexXML := indexXML || '<PAIRED_FLAG>X</PAIRED_FLAG>';
            indexXML := indexXML || '<SORT_STRING>' || sortingString || '</SORT_STRING>';

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (index_elementid, 12, null, null, indexXML, null, null, null);

        end loop;

    exception
        when others then
            insertLog('Error occured in buildIndexCategoryLinks procedure. ');
            insertLog('Error inside buildIndexCategoryLinks: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildIndexCategoryLinks. Error:' || substr(sqlerrm, 1, 512));

    end buildIndexCategoryLinks;


    /**************************************************************************************************************************************
    * NAME:          populateLetterIndex
    * DESCRIPTION:   TODO:  Add in conditions so that it doesnt populate the entire alphabet for certain indexes.
    *                I.e N Eng => 'N', N Fra => 'T'
    **************************************************************************************************************************************/
    procedure populateLetterIndex(p_version_code varchar2, structureVersionID number, indexRootElementID number, languageCode varchar2,
        bookIndexCode varchar2) is

        elementID number := 0;
        indexClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'LetterIndex');
        propertyClassID number;
        status_code varchar2(10) := 'ACTIVE';

        indexXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd">';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        FOR LetterLoop IN 65..90 LOOP
            elementID := CCI_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

            --Index Term Description
            propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexDesc');
            CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                CHR(LetterLoop), null);

            --Build Narrow Relationship
            propertyClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            CCI_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, indexRootElementID,
                structureVersionID);

            letter_index(CHR(LetterLoop)) := elementID;

            indexXML := indexXMLTop;
            indexXML := indexXML || '<index language="' || languageCode || '" classification="CCI">';
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || bookIndexCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>LETTER_INDEX</INDEX_TYPE><LEVEL_NUM></LEVEL_NUM><SEE_ALSO_FLAG>X</SEE_ALSO_FLAG></index>';
            indexXML := indexXML || indexXMLEnd;

            propertyClassID := CIMS_CCI.getCCIClassID('XMLPropertyVersion', 'IndexRefDefinition');
            CCI_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                indexXML, languageCode);
        END LOOP;

    exception
        when others then
            insertLog('Error occured in populateLetterIndex procedure');
            insertLog('Error inside populateLetterIndex: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateLetterIndex. Error:' || substr(sqlerrm, 1, 512));

    end populateLetterIndex;


    /**************************************************************************************************************************************
    * NAME:          migrateIndexChildNodes
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure migrateIndexChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number, parentIndexID number,
        language_code varchar2, ilTermElementID number, indexTypeCode varchar2, level number) is

        cursor c is
            select i.index_term_id, i.index_term_desc, i.status_code,
                i.see_also_flag, n.index_term_note_desc
            from cci.index_term i
            LEFT OUTER JOIN cci.index_term_note n on i.index_term_id = n.index_term_id
            where i.parent_index_term_id = parentIndexID
            and trim(i.index_term_desc) is not null
            order by UPPER(i.index_term_desc);

        rec_cc c%rowtype;
        elementID number := 0;
        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        seeAlsoFlag varchar2(1);
        iTermNoteDesc clob;

        indexClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Index');
        propertyClassID number;
        status_code varchar2(10);
        --domainValueClassID number;
        --domainValueElementID number;
        indexLeadTermElementID number;

        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        for rec_cc in c loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            indexLeadTermElementID := ilTermElementID;
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --insertLog('Migrating Index term: ' || iTermDesc);
            elementID := CCI_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

            indexXML := indexXMLTop;
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || indexTypeCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --Index Term Description
            propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexDesc');
            CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, iTermDesc, null);

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                --Need to null a few values out since we have bad SeeAlsoFlag values
                indexLeadTermElementID := null;
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, null, indexLeadTermElementID, null, 'CCIINDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_CCI.getCCIClassID('XMLPropertyVersion', 'IndexNoteDesc');
                CCI_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermNoteDesc, language_code);
            END IF;

            --Nesting Level
            propertyClassID := CIMS_CCI.getCCIClassID('NumericPropertyVersion', 'Level');
            CCI_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            CCI_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, parentElementID, structureVersionID);

            --Recursively call
            migrateIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, ilTermElementID, indexTypeCode, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateIndexChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIndexChildNodes. Error:' || substr(sqlerrm, 1, 512));
    end migrateIndexChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateIndex
    * DESCRIPTION:   These top level indexes appear to be called Index Lead Terms.
    **************************************************************************************************************************************/
    procedure migrateIndex(p_version_code varchar2, structureVersionID number, bookIndexID number, language_code varchar2) is
        cursor c_chapter is
            select i.index_term_id, i.index_term_desc index_term_desc, i.status_code,
                i.see_also_flag, n.index_term_note_desc
            from cci.index_term i
            LEFT OUTER JOIN cci.index_term_note n on i.index_term_id = n.index_term_id
            where i.book_index_id = bookIndexID
            and trim(i.index_term_desc) is not null
            and i.level_num = 1
            order by UPPER(i.index_term_desc);

        rec_cc c_chapter%rowtype;
        elementID number := 0;
        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        seeAlsoFlag varchar2(1);
        iTermNoteDesc clob;

        indexClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Index');
        propertyClassID number;
        status_code varchar2(10);
        letter number;
        letterAscii number;
        letterTmp varchar2(1);
        --domainValueClassID number;
        --domainValueElementID number;
        indexLeadTermElementID number;
        level number := 1;

        indexTypeCode VARCHAR2(1) := 'A';
        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        for rec_cc in c_chapter loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));
            indexLeadTermElementID := elementID;

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            elementID := CCI_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

            indexXML := indexXMLTop;
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || indexTypeCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --Index Term Description
            propertyClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexDesc');
            CCI_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID, iTermDesc, null);

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                --Need to null a few values to ensure correctness
                indexLeadTermElementID := null;
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, null, indexLeadTermElementID, null, 'CCIINDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_CCI.getCCIClassID('XMLPropertyVersion', 'IndexNoteDesc');
                CCI_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, iTermNoteDesc,
                    language_code);
            END IF;

            IF (LENGTH(TRIM(TRANSLATE (substr(iTermDesc, 1, 1), ' +-.0123456789',' '))) is null) THEN
                insertLog('Found ' || iTermDesc || '.  Using letter index A');
                letter := letter_index('A');
            ELSE

                SELECT ASCII(UPPER(substr(iTermDesc, 1, 1)))
                INTO letterAscii
                FROM DUAL;

                IF ( (letterAscii >= 65) AND (letterAscii <= 90) ) THEN
                    letter := letter_index(UPPER(substr(iTermDesc, 1, 1)));
                ELSE
                    --insertLog('Non standard character: ' || iTermDesc || '.  Possibly french');

                    select CONVERT(UPPER(substr(iTermDesc, 1, 1)), 'US7ASCII', 'WE8ISO8859P1')
                    into letterTmp
                    from dual;

                    SELECT ASCII(letterTmp)
                    INTO letterAscii
                    FROM DUAL;

                    IF ( (letterAscii >= 65) AND (letterAscii <= 90) ) THEN
                        --insertLog('Found english equivalent for: ' || iTermDesc || ' = ' || letterTmp);
                        letter := letter_index(letterTmp);
                    ELSE
                        insertLog('Cannot determine proper index.  Stopping migration. ' || iTermDesc);
                        raise_application_error(-20011, 'Error in migrateIndex. Cannot determine parent for index term ' ||
                            iTermDesc || '.  Stopping migration.');
                    END IF;

                END IF;
            END IF;

            --Nesting Level
            propertyClassID := CIMS_CCI.getCCIClassID('NumericPropertyVersion', 'Level');
            CCI_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            CCI_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, letter,
                structureVersionID);

            migrateIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, elementID, indexTypeCode, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateIndex ' || iTermID || ' ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIndex. Error:' || substr(sqlerrm, 1, 512));
    end migrateIndex;


    /**************************************************************************************************************************************
    * NAME:          buildXML
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE buildXML(p_version_code varchar2, structureVersionID number, indexClassId number)
    IS
        indexElementId number;
        indexXMLTop VARCHAR2(3000) := '';
        indexXMLEnd VARCHAR2(3000) := '';
        indexXML VARCHAR2(3000);

        xmlType number;
        xmlSnippet VARCHAR2(3000);
        xmlLanguage VARCHAR2(3000);

        cursor c is
            select elementid
            from element
            where classid = indexClassId;

        cursor x is
            select b, e, f
            from z_xml_temp
            where a = indexElementId;

        rec_cc c%rowtype;
        rec_xml x%rowtype;

        xmlType0 VARCHAR2(3000);         --Index Term 'main'

        type xmlType_va is varray(100) of VARCHAR2(3000);
        xmlType11 xmlType_va;            --Reference Link
        xmlType12 xmlType_va;            --Category Link
        xmlType11Counter NUMBER := 1;    --VARRAYS start at 1
        xmlType12Counter NUMBER := 1;    --VARRAYS start at 1

        xmlTypeLanguage VARCHAR2(3000);

        counter number := 0;
        propertyClassID number := CIMS_CCI.getCCIClassID('XMLPropertyVersion', 'IndexRefDefinition');
    begin
        insertLog('Beginning CCI Index XML building...');

        for rec_cc in c loop

            counter := counter + 1;
            IF ( MOD(counter, 1000) = 0 ) THEN
                insertLog('Processed Index XML: ' || counter);
            END IF;

            indexElementId := TRIM(rec_cc.elementid);

            xmlType11 := xmlType_va('');
            xmlType12 := xmlType_va('');
            xmlType11Counter := 1;
            xmlType12Counter := 1;

            -- Delete tmp variables that hold the XML
            xmlType0 := '';

            indexXMLTop := '';
            indexXMLEnd := '';
            indexXML := '';

            for rec_xml in x loop
                xmlType := TRIM(rec_xml.b);
                xmlSnippet := TRIM(rec_xml.e);
                xmlLanguage := TRIM(rec_xml.f);

                -- Consider if the variable is already set.  that would be an error
                if (xmlType = 0) then
                    xmlType0 := xmlSnippet;
                    xmlTypeLanguage := xmlLanguage;
                elsif (xmlType = 11) then
                    xmlType11.extend;
                    xmlType11(xmlType11Counter) := xmlSnippet;
                    xmlType11Counter := xmlType11Counter + 1;
                elsif (xmlType = 12) then
                    xmlType12.extend;
                    xmlType12(xmlType12Counter) := xmlSnippet;
                    xmlType12Counter := xmlType12Counter + 1;
                end if;

            end loop;

            -- TODO: Some checks should be done here to ensure the data is all here??
            indexXMLTop := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd"><index language="' || xmlTypeLanguage || '" classification="CCI">';
            --indexXMLTop := '<index language="' || xmlTypeLanguage || '" classification="CCI">';
            indexXMLEnd := '</index>';

            indexXML := indexXMLTop;
            indexXML := indexXML || xmlType0;

            indexXML := indexXML || '<REFERENCE_LIST>';

            if (xmlType11(1) is not null) then
                indexXML := indexXML || '<INDEX_REF_LIST>';

                FOR i IN xmlType11.FIRST .. xmlType11.LAST
                LOOP
                    indexXML := indexXML || xmlType11(i);
                END LOOP;

                indexXML := indexXML || '</INDEX_REF_LIST>';
            end if;


            if (xmlType12(1) is not null) then
                indexXML := indexXML || '<CATEGORY_REFERENCE_LIST>';

                FOR i IN xmlType12.FIRST .. xmlType12.LAST
                LOOP
                    indexXML := indexXML || xmlType12(i);
                END LOOP;

                indexXML := indexXML || '</CATEGORY_REFERENCE_LIST>';
            end if;

            indexXML := indexXML || '</REFERENCE_LIST>';

            indexXML := indexXML || indexXMLEnd;

            --INSERT INTO HOWARD (A, XMLTEXT)
            --VALUES (indexElementId, indexXML);

            CCI_DATA_MIGRATION.insertXMLProperty(p_version_code, indexElementId, propertyClassID, structureVersionID,
                indexXML, xmlTypeLanguage);

        end loop;

        insertLog('----------------------------------------------------');
        insertLog('Processed Index XML: ' || counter);
        insertLog('Ending CCI Index XML building...');

        commit;
    exception
        when others then
            insertLog('buildXML ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildXML. Error:' || substr(sqlerrm, 1, 512));
    end buildXML;


    /**************************************************************************************************************************************
    * NAME:          populate_DomainValues
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure populate_DomainValues(version_code varchar2, structureVersionID number) is
        domainValueClassID number;
        dvElementID number;
    begin
        insertLog('  - See Also Flag');
        domainValueClassID := CIMS_CCI.getCCIClassID('ConceptVersion', 'SeeAlso');

        dvElementID := CCI_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, 'N', null, null, null, null, null,
            null, null, domainValueClassID);
        dvElementID := CCI_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, 'Y', null, null, null, null, null,
            null, null, domainValueClassID);

    end populate_DomainValues;


    /**************************************************************************************************************************************
    * NAME:          main
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/
    PROCEDURE main(version_Code varchar2) is
        structureVersionID number := CIMS_CCI.getCCIStructureByYear(version_Code);
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

        --Ensure that the year already exists
        IF structureVersionID = -9999 THEN
            dbms_output.put_line(version_Code || ' does not exist in CCI.  Exiting...');
        END IF;

        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('Starting CCI index migration ' || version_code || '.  Migration Run ID: ' || logRunID);

        --Get the Viewer Root
        viewerRootElementID := CIMS_CCI.getCCIRoot(version_Code);
        insertLog('Viewer Root for ' || version_Code || ' is ' || viewerRootElementID);

        insertLog('Populating Domain Value tables');
        populate_DomainValues(version_Code, structureVersionID);

        insertLog('Populating Book Index');
        populateBookIndexLookup(version_Code, structureVersionID, viewerRootElementID);

        insertLog('Main migration');
        FOR i IN book_index.FIRST .. book_index.LAST LOOP
            insertLog('  - ' || book_index_description(i) || ': ' || i || ' = ' || book_index(i) ||
                         '.  Lang: ' || book_index_language(i) || '.  Type Code: ' || book_index_typecode(i));

            populateLetterIndex(version_Code, structureVersionID, book_index(i), book_index_language(i), book_index_typecode(i));
            migrateIndex(version_Code, structureVersionID, i, book_index_language(i));

        END LOOP;

        insertLog('Building reference links between index terms');
        buildIndexReferenceLinks(structureVersionID);

        --Build the Index to Category reference
        insertLog('Building links between index term and tabular concept');
        buildIndexCategoryLinks(structureVersionID);

        CIMS_CCI.GATHER_SCHEMA_STATS;

        --Build XML from snippet pieces
        insertLog('Building XML from snippet pieces');

        -- Alphabetic
        insertLog('  - Building XML for index');
        buildXML(version_Code, structureVersionID, CIMS_CCI.getCCIClassID('ConceptVersion', 'Index'));

        insertLog('Ending CCI index migration ' || version_code);

        commit;
--        rollback;

    END main;


end CCI_DATA_MIGRATION_INDEX;
/
