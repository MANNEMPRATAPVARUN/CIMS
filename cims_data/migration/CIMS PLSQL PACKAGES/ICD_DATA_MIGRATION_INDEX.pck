create or replace package ICD_DATA_MIGRATION_INDEX is

    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;

    TYPE book_index_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    book_index book_index_t;

    TYPE book_index_lang_t IS TABLE OF VARCHAR2(255) INDEX BY VARCHAR2(3);
    book_index_language book_index_lang_t;
    book_index_description book_index_lang_t;
    book_index_typecode book_index_lang_t;

    TYPE letter_index_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    letter_index letter_index_t;


    TYPE xml_t IS TABLE OF VARCHAR2(3000) INDEX BY VARCHAR2(3);
    index_ref xml_t;
    index_cat xml_t;



    --TYPE seeAlsoFlag_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    --saf seeAlsoFlag_t;

    TYPE da_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    da da_t;

    --TYPE siteIndicator_t IS TABLE OF NUMBER INDEX BY VARCHAR2(30);
    --si siteIndicator_t;

    PROCEDURE main(version_Code IN varchar2);
    --procedure buildXML(p_version_code varchar2, structureVersionID number, indexClassId number, indexTypeCode varchar2);
    --procedure buildXMLTest(p_version_code varchar2, structureVersionID number, indexClassId number, indexTypeCode varchar2, indexElementId number);


    FUNCTION codeCleaner(code varchar2) return varchar2;
    FUNCTION isCodeARange(code varchar2) return varchar2;
    FUNCTION getSeeAlsoText(seeAlsoFlag varchar2, languageCode varchar2) return varchar2;

end ICD_DATA_MIGRATION_INDEX;
/
create or replace package body ICD_DATA_MIGRATION_INDEX is


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
    * NAME:          isCodeARange
    * DESCRIPTION:
    **************************************************************************************************************************************/
    FUNCTION isCodeARange(code varchar2) return varchar2 is
        rangeCheck number;
        ret varchar2(1);
    BEGIN

        -- Returns 1 if code is a range, else 0
        SELECT REGEXP_INSTR(code, '^\D\d\d-\D\d\d$')
        INTO rangeCheck
        FROM dual;

        IF rangeCheck = 0 THEN
            ret := 'N';
        ELSE
            ret := 'Y';
        END IF;

        return ret;

    exception
        when others then
            raise_application_error(-20011, 'Error! isCodeARange: ' || substr(sqlerrm, 1, 512));
            return 'N';

    end isCodeARange;


    /**************************************************************************************************************************************
    * NAME:          getSeeAlsoText
    * DESCRIPTION:   
    *
    **************************************************************************************************************************************/
    FUNCTION getSeeAlsoText(seeAlsoFlag varchar2, languageCode varchar2) return varchar2 is
        seeAlsoFlagText VARCHAR2(30);

    BEGIN

        IF (seeAlsoFlag = 'Y') THEN
            IF (languageCode = 'ENG') THEN
                seeAlsoFlagText := 'see also';
            ELSE
                seeAlsoFlagText := 'voir aussi';
            END IF;
        ELSE
            IF (languageCode = 'ENG') THEN
                seeAlsoFlagText := 'see';
            ELSE
                seeAlsoFlagText := 'voir';
            END IF;
        END IF;

        RETURN TRIM(seeAlsoFlagText);

    exception
        when others then
            raise_application_error(-20011, 'Error! getSeeAlsoText: ' || substr(sqlerrm, 1, 512));
            return '';

    end getSeeAlsoText;


    /**************************************************************************************************************************************
    * NAME:          codeCleaner
    * DESCRIPTION:   Removes the following characters from the passed in string
    *
    *               #134;
    *               &
    *               .-
    *               -
    *               *
    *
    *              TODO: If the passed in code is a range, then simply return the range
    *
    *
    **************************************************************************************************************************************/
    FUNCTION codeCleaner(code varchar2) return varchar2 is
        updatedCode VARCHAR2(500);

    BEGIN

        updatedCode := replace(replace(replace(replace(replace(trim(code), '#134;', ''), '&', ''), '.-', ''), '-', ''), '*', '');

        RETURN TRIM(updatedCode);

    exception
        when others then
            raise_application_error(-20011, 'Error! codeCleaner: ' || substr(sqlerrm, 1, 512));
            return '';

    end codeCleaner;


    /**************************************************************************************************************************************
    * NAME:         HACK cleanCode
    * DESCRIPTION:  Removes the following characters from the passed in string
    *
    *               #134;
    *               &
    *               .-
    *               -
    *               *
    *
    *              TODO: If the passed in code is a range, then simply return the range
    *
    *
    **************************************************************************************************************************************/
    /*FUNCTION cleanCode(code varchar2) return varchar2 is
        updatedCode VARCHAR2(500);

    BEGIN

        updatedCode := replace(replace(replace(replace(replace(trim(code), '#134;', ''), '&', ''), '.-', ''), '-', ''), '*', '');

        RETURN TRIM(updatedCode);

    exception
        when others then
            raise_application_error(-20011, 'Error! cleanCode: ' || substr(sqlerrm, 1, 512));
            return '';

    end cleanCode;
    */




    /**************************************************************************************************************************************
    * NAME:          insertExternalInjuryIndexNote
    * DESCRIPTION:   For the Index 'External Injury', we have to insert a special XML note
    **************************************************************************************************************************************/
    procedure insertExternalInjuryIndexNote(p_version_code varchar2, structureVersionID number, elementID number,
        languageCode varchar2)
    is

        fileName varchar(50) := 'icd_land_transport_accidents.xml';

        cursor c is
            select CIMS_ICD.clobfromblob(t.text_data) text_data
            from icd.text t
            where t.file_name = fileName
            and t.clinical_classification_code = '10CA' || p_version_code
            and t.language_code = languageCode;

        rec_cc c%rowtype;
        propertyClassID number := 0;

        text_Data CLOB;
    begin

        for rec_cc in c loop
            text_Data := TRIM(rec_cc.text_data);

            propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
            ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                text_Data, languageCode);
        end loop;

    exception
        when others then
            insertLog('Error occured in insertExternalInjuryIndexNote procedure');
            insertLog('Error inside insertExternalInjuryIndexNote: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in insertExternalInjuryIndexNote. Error:' || substr(sqlerrm, 1, 512));

    end insertExternalInjuryIndexNote;


    /**************************************************************************************************************************************
    * NAME:          populateBookIndexLookup
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure populateBookIndexLookup(p_version_code varchar2, structureVersionID number, viewerRootElementID number) is
        cursor c is
            select
                i.book_index_id, i.language_code, i.index_type_code, i.book_index_desc--, t.index_type_desc
            from icd.book_index i
            join icd.index_type t on i.index_type_code = t.index_type_code
            where i.clinical_classification_code = '10CA' || p_version_code
            order by i.index_type_code;

        rec_cc c%rowtype;
        bookIndexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'BookIndex');
        bookIndexCode varchar2(1);
        bookIndexID number;
        bookIndexDesc varchar2(255);
        --bookIndexTypeDesc varchar2(255);
        relationshipClassID number;
        elementID number := 0;
        status_code varchar2(10) := 'ACTIVE';
        languageCode varchar(3);
        propertyClassID number := 0;

        indexXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd">';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        for rec_cc in c loop
            bookIndexCode := TRIM(rec_cc.index_type_code);
            bookIndexID := TRIM(rec_cc.book_index_id);
            bookIndexDesc := TRIM(rec_cc.book_index_desc);
            --bookIndexTypeDesc := TRIM(rec_cc.index_type_desc);
            languageCode := TRIM(rec_cc.language_code);

            --Add in the Section text to the description
            --If French, manually set to the text below
            if (bookIndexCode = 'A') then
                bookIndexDesc := 'Section I -- ' || bookIndexDesc;

                if (languageCode = 'FRA') then
                    bookIndexDesc := 'Section I -- ' || 'Index alphabétique des maladies et de la nature du traumatisme';
                end if;

            elsif (bookIndexCode = 'E') then
                bookIndexDesc := 'Section II -- ' || bookIndexDesc;

                if (languageCode = 'FRA') then
                    bookIndexDesc := 'Section II -- ' || 'Index alphabétique des causes externes de morbidité et de mortalité';
                end if;

            elsif (bookIndexCode = 'D') then
                bookIndexDesc := 'Section III -- ' || bookIndexDesc;

                if (languageCode = 'FRA') then
                    bookIndexDesc := 'Section III -- ' || 'Index alphabétique des médicaments et autres substances chimique (Table des effets nocifs)';
                end if;

            else --N
                bookIndexDesc := 'Section IV -- ' || bookIndexDesc;

                if (languageCode = 'FRA') then
                    bookIndexDesc := 'Section IV -- ' || 'Index alphabétique (Table de tumeur)';
                end if;

            end if;

            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, bookIndexClassID, null, structureVersionID, status_code);

            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexCode');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                bookIndexCode, languageCode);

            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                bookIndexDesc, null);

            --External Injury Index has a XML Note
            if (bookIndexCode = 'E') then
                insertExternalInjuryIndexNote(p_version_code, structureVersionID, elementID, languageCode);
            end if;

            indexXML := indexXMLTop;
            indexXML := indexXML || '<index language="' || languageCode || '" classification="ICD-10-CA">';
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || bookIndexCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>BOOK_INDEX</INDEX_TYPE><LEVEL_NUM></LEVEL_NUM><SEE_ALSO_FLAG>X</SEE_ALSO_FLAG></index>';
            indexXML := indexXML || indexXMLEnd;

            propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexRefDefinition');
            ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                indexXML, languageCode);

            --Build Narrow Relationship
            relationshipClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, relationshipClassID, elementID, viewerRootElementID, structureVersionID);

            book_index(bookIndexID) := elementID;
            book_index_language(bookIndexID) := languageCode;
            book_index_description(bookIndexID) := bookIndexDesc;
            book_index_typecode(bookIndexID) := bookIndexCode;

        end loop;

    exception
        when others then
            insertLog('Error occured in populateBookIndexLookup procedure');
            insertLog('Error inside populateBookIndexLookup: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateBookIndexLookup. Error:' || substr(sqlerrm, 1, 512));

    end populateBookIndexLookup;


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
            select z.A, z.B, z.C, z.D, z.E, z.G, z1.B refElementID
            from z_icd_temp z
            left outer join z_icd_temp z1 on z.C = z1.A and z1.f = 'INDEX'
            where z.f = 'INDEX';
            --and z.C is not null;

        rec_cc c%rowtype;
        elementID number;
        refElementID number;
        refDescription varchar2(255);
        --indexLeadTermElementID number;
        indexDescription varchar2(3000);

        indexXMLTop VARCHAR2(500) := '<INDEX_REF>';
        indexXMLEnd VARCHAR2(50) := '</INDEX_REF>';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c loop
            elementID := TRIM(rec_cc.b);
            refElementID := TRIM(rec_cc.refelementid);
            refDescription := TRIM(rec_cc.e);
            --indexLeadTermElementID := TRIM(rec_cc.d);
            indexDescription := TRIM(rec_cc.g);

            --If both the Ref Index Element ID and the Description is null, then we dont have a
            IF (refDescription is null) AND (refElementID is null) THEN
                --Skip out
                CONTINUE;
            END IF;

            indexXML := indexXMLTop;
            --indexXML := indexXML || '<REF_DESC>' || DBMS_XMLGEN.CONVERT(indexDescription) || '</REF_DESC>';
            indexXML := indexXML || '<REFERENCE_LINK_DESC>' || DBMS_XMLGEN.CONVERT(nvl(refDescription,'')) || '</REFERENCE_LINK_DESC>';
            indexXML := indexXML || '<CONTAINER_INDEX_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, refElementID) || '</CONTAINER_INDEX_ID>';
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
    *                1.  If the description does not contain a pair
    *
    *                	a.  Create the reference between the index and category
    *                		This is the 'Main Code Value'
    *                	b.  Evaluate the last character in the description
    *                		If '*' Asterisk is Y.  Otherwise N
    *                	c.  If the DaggerFlag is Y, Dagger is Y.  Otherwise N
    *                	d.  Set the MAIN_DAGGER_ASTERISK according to Dagger and Asterisk.
    *                	    Only one documented case where both is Y.  Sent to Business to clarify.
    *                	e.  Description is used as the presentation, removing undesirable characters beforehand.
    *
    *                2.  If the description does contain a pair
    *
    *                	a.  Create the reference between the index and category
    *                		This is the 'Main Code Value'
    *                	b.  Split the code pairs on the description
    *                	c.  Set the MAIN_DAGGER_ASTERISK according to Dagger.
    *                	d.  Description is used as the presentation, removing undesirable characters beforehand.
    *                	e.  Using the second code in the pair.
    *                		This is the 'Paired Code Value'
    *                	    1.  Evaluate the last character
    *                		    If '*' Asterisk is Y.  Otherwise N
    *                		2.  Check if this code exists
    *                			a.  If exists, create the reference between the index and category
    *                				1.  Set the Dagger Asterisk flag according to Asterisk.
    *                			b.  If not, do not create anything.  Skip.
    *
    *
    **************************************************************************************************************************************/
    procedure buildIndexCategoryLinks(p_version_code varchar2, structureVersionID number) is

        chapterClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
        blockClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
        categoryClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
        codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');

        cursor c is
            select
                tmp.*, DECODE(tmp.a, tmp.b, null, 'DIFFERENT') diffs, z.b index_elementID, z1.b elementID, z1.e catCode, t.domainelementid, t.text pairedCode
                --count(*)
            from
                (
                select
                    r.index_term_id,
                    r.category_id,
                    CIMS_ICD.formatXREFCode(trim(c.category_code)) a,        --Format code to CIMS standard

                    DECODE(isCodeARange(trim(r.category_reference_desc)),    --Returns cleaned code if not a range
                        'Y', SUBSTR(trim(r.category_reference_desc), 1, 3),  --else it returns the first part
                        'N', codeCleaner(r.category_reference_desc)          --of the range
                    ) b,

                    trim(r.category_reference_desc) orig,                    --Return unaltered description
                    isCodeARange(trim(r.category_reference_desc)) isARange,  --Return Y/N if code description is a range

                    --Dagger Asterisk Checks
                    nvl(r.dagger_asterisk, 'N') dagger,
                    DECODE(substr(trim(r.category_reference_desc), -1), '*', 'Y', 'N') asterisk,
                    DECODE(substr(substr(trim(r.category_reference_desc), instr(trim(r.category_reference_desc), '/') + 1), -1), '*', 'Y', 'N') CIR_ASTERISK,
                    --End Dagger Asterisk Checks

                    codeCleaner(trim(substr(trim(r.category_reference_desc), 1 , instr(trim(r.category_reference_desc), '/') - 1))) category_id_left_description,
                    codeCleaner(trim(substr(trim(r.category_reference_desc), instr(trim(r.category_reference_desc), '/') + 1))) category_id_right_description,

                    DECODE(LENGTH(TRIM(TRANSLATE(SUBSTR(nvl(r.category_reference_desc, 'X'), 1, 1), ' +-.0123456789', ' '))), null,
                        replace(trim(r.category_reference_desc), '/', ''), --Is a num

                        DECODE(isCodeARange(substr(trim(r.category_reference_desc), instr(trim(r.category_reference_desc), '/') + 1)),
                            'Y', SUBSTR(substr(trim(r.category_reference_desc), instr(trim(r.category_reference_desc), '/') + 1), 1, 3),
                            'N', REPLACE(codeCleaner(substr(trim(r.category_reference_desc), instr(trim(r.category_reference_desc), '/') + 1)), '.', '')
                        )
                    ) category_id_right_clean,

                    --Returns Y/N if it is a number by checking the first character to determine
                    DECODE(LENGTH(TRIM(TRANSLATE(SUBSTR(nvl(r.category_reference_desc, 'X'), 1, 1), ' +-.0123456789', ' '))), null, 'Y' /*Is a num*/, 'N') IsANum
                from icd.index_category_reference r
                join icd.index_term i on r.index_term_id = i.index_term_id
                join icd.category c on r.category_id = c.category_id
                where i.book_index_id in (select book_index_id from icd.book_index where clinical_classification_code = '10CA' || p_version_code)
                ) tmp
            LEFT OUTER join z_icd_temp z on tmp.index_term_id = z.a and z.f = 'INDEX'
            LEFT OUTER join z_icd_temp z1 on tmp.category_id = z1.a and z1.f = 'ICD'
            LEFT OUTER join textpropertyversion t on tmp.category_id_right_clean = t.text and t.classid = codeClassID
            LEFT OUTER join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = structureVersionID
            JOIN  ELEMENT e on t.domainelementid = e.elementid and e.classid IN (chapterClassID, blockClassID, categoryClassID);


        rec_cc c%rowtype;
        daggerAsterisk varchar2(1);
        --status_code varchar2(10) := 'ACTIVE';
        --elementID number := 0;
        dagger varchar2(30);
        asterisk varchar2(30);
        diffs varchar2(30);
        index_elementid number;
        eid number;
        pairedElementId number;
        categoryCode varchar2(30);
        pairedCategoryCode varchar2(30);
        sortingString varchar2(50);
        categoryCodeDescription varchar2(30);
        pairedCategoryCodeDescription varchar2(30);
        origCategoryCodeDescription varchar2(30);
        IsANum varchar2(1);
        IsARange varchar2(1);
        NonPairMainCodeDescription varchar2(30);

        indexXMLTop VARCHAR2(500) := '<CATEGORY_REFERENCE>';
        indexXMLEnd VARCHAR2(50) := '</CATEGORY_REFERENCE>';
        indexXML VARCHAR2(3000);
    begin

        for rec_cc in c loop
            dagger := TRIM(rec_cc.dagger);
            asterisk := TRIM(rec_cc.asterisk);
            diffs := TRIM(rec_cc.diffs);
            index_elementid := TRIM(rec_cc.index_elementid);
            eid := TRIM(rec_cc.elementid);
            pairedElementId := TRIM(rec_cc.domainelementid);
            categoryCode := TRIM(rec_cc.catCode);
            pairedCategoryCode := TRIM(rec_cc.pairedCode);
            pairedCategoryCodeDescription := TRIM(rec_cc.category_id_right_description);
            categoryCodeDescription := TRIM(rec_cc.category_id_left_description);
            origCategoryCodeDescription := TRIM(rec_cc.orig);
            IsANum := TRIM(rec_cc.IsANum);
            IsARange := TRIM(rec_cc.IsARange);

            --Skip out when the index doesnt actually exist
            --Investigated, and it seems bad data.  Some indexes dont have a proper lead term, so its not migrated
            --Now also due to Neoplasm and other index, we wil get a lot of these cases
            if (index_elementid is null) then
                --insertLog('Index Element ID is null, due to bad index data. SKIP!');
                CONTINUE;
            end if;

            indexXML := indexXMLTop;

            --Null indicates the description does not contain a pair
            if (diffs is null) then

                --Determine the Dagger Asterisk
                if (dagger = 'Y') then
                    daggerAsterisk := '+';
                    sortingString := 'aaa-sort-string-bbb###' || CIMS_ICD.formatXREFCode(categoryCode);
                else
                    if (asterisk = 'Y') then
                        daggerAsterisk := '*';
                        sortingString := 'aaa-sort-string-zzz###' || CIMS_ICD.formatXREFCode(categoryCode);
                    else
                        daggerAsterisk := '';
                        sortingString := 'aaa-sort-string-ccc###' || CIMS_ICD.formatXREFCode(categoryCode);
                    end if;
                end if;

                -- For non pairs, we need to check if the category code starts with a number.
                -- If it does, we need to format the category code rather than the code description as it contains an incorrect value
                IF (IsANum = 'Y') THEN
                    NonPairMainCodeDescription := CIMS_ICD.formatXREFCode(categoryCode);
                ELSE
                    IF (IsARange = 'Y') THEN
                        NonPairMainCodeDescription := origCategoryCodeDescription;
                    ELSE
                        NonPairMainCodeDescription := pairedCategoryCodeDescription;
                    END IF;

                END IF;

                --Due to the way the query above works, for non pairs the category code description is actuallyinside the pairedCategoryCodeDescription
                indexXML := indexXML || '<MAIN_CODE_PRESENTATION>' || NonPairMainCodeDescription || '</MAIN_CODE_PRESENTATION>';
                indexXML := indexXML || '<MAIN_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, eid) || '</MAIN_CONTAINER_CONCEPT_ID>';
                indexXML := indexXML || '<MAIN_CODE>' || CIMS_ICD.formatXREFCode(categoryCode) || '</MAIN_CODE>';
                indexXML := indexXML || '<MAIN_DAGGER_ASTERISK>' || daggerAsterisk || '</MAIN_DAGGER_ASTERISK>';
                indexXML := indexXML || '<PAIRED_FLAG>X</PAIRED_FLAG>';
                indexXML := indexXML || '<SORT_STRING>' || sortingString || '</SORT_STRING>';

            else
            --This Index contains a reference to a pair of codes
            --In pairs, we need to switch up the main and paired code.  This is because of how the logic in the query above.
            --Daggers relate to the main code.  Asterisks relate to the paired code.

                --Determine the sorting string
                sortingString := 'aaa-sort-string-aaa###' || CIMS_ICD.formatXREFCode(categoryCode);

                --Determine if the main code should have a Dagger.
                if (dagger = 'Y') then
                    daggerAsterisk := '+';
                else
                    daggerAsterisk := '';
                end if;

                indexXML := indexXML || '<MAIN_CODE_PRESENTATION>' || categoryCodeDescription || '</MAIN_CODE_PRESENTATION>';
                indexXML := indexXML || '<MAIN_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, eid) || '</MAIN_CONTAINER_CONCEPT_ID>';
                indexXML := indexXML || '<MAIN_CODE>' || CIMS_ICD.formatXREFCode(categoryCode) || '</MAIN_CODE>';
                indexXML := indexXML || '<MAIN_DAGGER_ASTERISK>' || daggerAsterisk || '</MAIN_DAGGER_ASTERISK>';
                indexXML := indexXML || '<PAIRED_FLAG>Y</PAIRED_FLAG>';
                indexXML := indexXML || '<SORT_STRING>' || sortingString || '</SORT_STRING>';

                --Determine if the paired code should have an asterisk.
                if (asterisk = 'Y') then
                    daggerAsterisk := '*';
                else
                    daggerAsterisk := '';
                end if;

                indexXML := indexXML || '<PAIRED_CODE_PRESENTATION>' || pairedCategoryCodeDescription || '</PAIRED_CODE_PRESENTATION>';
                indexXML := indexXML || '<PAIRED_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, pairedElementId) || '</PAIRED_CONTAINER_CONCEPT_ID>';
                indexXML := indexXML || '<PAIRED_CODE>' || CIMS_ICD.formatXREFCode(pairedCategoryCode) || '</PAIRED_CODE>';
                indexXML := indexXML || '<PAIRED_DAGGER_ASTERISK>' || daggerAsterisk || '</PAIRED_DAGGER_ASTERISK>';

            end if;

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
        indexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'LetterIndex');
        propertyClassID number;
        status_code varchar2(10) := 'ACTIVE';

        indexXMLTop VARCHAR2(500) := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd">';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        FOR LetterLoop IN 65..90 LOOP

            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

            --Index Term Description
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                CHR(LetterLoop), null);

            --Build Narrow Relationship
            propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, indexRootElementID,
                structureVersionID);

            letter_index(CHR(LetterLoop)) := elementID;
            --insertLog(CHR(LetterLoop) || ' is ' || elementID);

            indexXML := indexXMLTop;
            indexXML := indexXML || '<index language="' || languageCode || '" classification="ICD-10-CA">';
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || bookIndexCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>LETTER_INDEX</INDEX_TYPE><LEVEL_NUM></LEVEL_NUM><SEE_ALSO_FLAG>X</SEE_ALSO_FLAG></index>';
            indexXML := indexXML || indexXMLEnd;

            propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexRefDefinition');
            ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                indexXML, languageCode);

        END LOOP;

    exception
        when others then
            insertLog('Error occured in populateLetterIndex procedure');
            insertLog('Error inside populateLetterIndex: ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in populateLetterIndex. Error:' || substr(sqlerrm, 1, 512));

    end populateLetterIndex;


    /**************************************************************************************************************************************
    * NAME:          migrateDrugsIndexChildNodes
    * DESCRIPTION:   Checks the index term description, looking out for five specific terms.  These terms aren't real indexes
    *                but are now instead properties on the parent term.
    **************************************************************************************************************************************/
    procedure migrateDrugsIndexChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number,
        parentIndexID number, language_code varchar2, ilTermElementID number, level number)
    is
        cursor c is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc, r.category_id, z.b elementid, z.e catCode,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            left outer join icd.index_category_reference r on i.index_term_id = r.index_term_id
            left outer join z_icd_temp z on r.category_id = z.a and z.f = 'ICD'
            where i.parent_index_term_id = parentIndexID
            order by UPPER(i.index_term_desc);

        rec_cc c%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DrugsAndChemicalsIndex');
        propertyClassID number;
        status_code varchar2(10);
        indexLeadTermElementID number;
        codeElementID number;
        categoryCode varchar2(30);

        advEff varchar2(50) := 'Adverse effect in therapeutic use';
        poiAcc varchar2(50) := 'Poisoning - Accidental';
        poiXIX varchar2(50) := 'Poisoning - Chapter XIX';
        poiInt varchar2(50) := 'Poisoning - Intentional self-harm';
        poiUin varchar2(50) := 'Poisoning - Undetermined intent';

        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        IF (language_code = 'FRA') THEN
            advEff := 'Effet indésirable en usage thérapeutique';
            poiAcc := 'Empoisonnement - Accidentel';
            poiXIX := 'Empoisonnement - Chapitre XIX';
            poiInt := 'Empoisonnement - Intentionnel autoinduit';
            poiUin := 'Empoisonnement - Intention nondéterminée';
        END IF;

        for rec_cc in c loop

            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            indexLeadTermElementID := ilTermElementID;
            codeElementID := TRIM(rec_cc.elementid);
            categoryCode := TRIM(rec_cc.catCode);
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --Build Narrow Relationship
            if (itermDesc = advEff) then
                if (codeElementID is not null) then
                    indexXMLTop := '<TABULAR_REF type="AETU">';
                    indexXMLEnd := '</TABULAR_REF>';

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 6, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = poiAcc) then
                if (codeElementID is not null) then
                    indexXMLTop := '<TABULAR_REF type="ACCIDENTAL">';
                    indexXMLEnd := '</TABULAR_REF>';

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 7, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = poiXIX) then
                if (codeElementID is not null) then
                    indexXMLTop := '<TABULAR_REF type="CHAPTER_XIX">';
                    indexXMLEnd := '</TABULAR_REF>';

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 8, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = poiInt) then
                if (codeElementID is not null) then
                    indexXMLTop := '<TABULAR_REF type="INT_SELF_HARM">';
                    indexXMLEnd := '</TABULAR_REF>';

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 9, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = poiUin) then
                if (codeElementID is not null) then
                    indexXMLTop := '<TABULAR_REF type="UNDE_INTENT">';
                    indexXMLEnd := '</TABULAR_REF>';

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 10, null, null, indexXML, language_code, null, null);

                end if;
            else
                --Actual Index item
                elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

                indexXMLTop := '';
                indexXMLEnd := '';

                indexXML := indexXMLTop;
                indexXML := indexXML || '<BOOK_INDEX_TYPE>D</BOOK_INDEX_TYPE>';
                indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
                indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
                indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

                --See Also Flag
                IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                    --See Also flag is a Y or a N

                    IF (seeAlsoFlag = 'Y') THEN
                        seeAlsoFlagText := 'see also';
                    ELSE
                        seeAlsoFlagText := 'see';
                    END IF;

                    --Check whether this Index has a referenced index description but no referenced index.
                    IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                        --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                        --as well as removing the see also flag
                        --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                        iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                        indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                        --Need to null a few values to ensure correctness
                        refItermID := null;
                        indexLeadTermElementID := null;
                        refLinkDesc := null;
                    ELSE
                        --No, proceed as normal
                        indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                    END IF;
                ELSE
                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values out since we have bad SeeAlsoFlag values
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                END IF;

                --Index Term Description
                propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
                ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermDesc, null);

                --Site Indicator
                IF (siDiamond > 0) THEN
                    indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
                ELSIF (siNumber > 0) THEN
                    indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
                END IF;

                --You need to store the iTermID and refItermID to build a reference link later on
                INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
                VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

                --Index Term Note Description
                IF (iTermNoteDesc is not null) THEN
                    propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                    ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                        iTermNoteDesc, language_code);
                END IF;

                --Nesting level
                propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
                ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

                indexXML := indexXML || indexXMLEnd;

                -- Store XML snippet inside temp table
                INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

                --Build Narrow Relationship
                propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
                ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, parentElementID, structureVersionID);

                --Recursively call
                migrateDrugsIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, ilTermElementID, level + 1);
            end if;

        end loop;

    exception
        when others then
            insertLog('migrateDrugsIndexChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateDrugsIndexChildNodes. Error:' || substr(sqlerrm, 1, 512));
    end migrateDrugsIndexChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateNeoplasmIndexChildNodes
    * DESCRIPTION:   Checks the index term description, looking out for five specific terms.  These terms aren't real indexes
    *                but are now instead properties on the parent term.
    **************************************************************************************************************************************/
    procedure migrateNeoplasmIndexChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number,
        parentIndexID number, language_code varchar2, ilTermElementID number, level number)
    is
        cursor c is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc, r.category_id, z.b elementid, z.e catCode,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            left outer join icd.index_category_reference r on i.index_term_id = r.index_term_id
            left outer join z_icd_temp z on r.category_id = z.a and z.f = 'ICD'
            where i.parent_index_term_id = parentIndexID
            order by UPPER(i.index_term_desc);

        rec_cc c%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'NeoplasmIndex');
        propertyClassID number;
        status_code varchar2(10);
        indexLeadTermElementID number;
        codeElementID number;
        categoryCode varchar2(30);

        benign varchar2(50) := 'Benign';
        inSitu varchar2(50) := 'In situ';
        malPri varchar2(50) := 'Malignant - Primary';
        malSec varchar2(50) := 'Malignant - Secondary';
        uncBeh varchar2(50) := 'Uncertain or unknown behaviour';

        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);
    begin

        IF (language_code = 'FRA') THEN
            benign := 'Bénignes';
            inSitu := 'In situ';
            malPri := 'Malignes - Primitives';
            malSec := 'Malignes - Secondaires';
            uncBeh := 'À évolution imprévisible ou inconnue';
        END IF;

        for rec_cc in c loop

            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            indexLeadTermElementID := ilTermElementID;
            codeElementID := TRIM(rec_cc.elementid);
            categoryCode := TRIM(rec_cc.catCode);
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --Build Narrow Relationship
            if (itermDesc = benign) then
                if (codeElementID is not null) then

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TABULAR_REF type="BENIGN">';
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || '</TABULAR_REF>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 1, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = inSitu) then
                if (codeElementID is not null) then

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TABULAR_REF type="IN_SITU">';
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || '</TABULAR_REF>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 2, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = malPri) then
                if (codeElementID is not null) then

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_PRIMARY">';
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || '</TABULAR_REF>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 3, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = malSec) then
                if (codeElementID is not null) then

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_SECONDARY">';
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || '</TABULAR_REF>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 4, null, null, indexXML, language_code, null, null);

                end if;
            elsif (itermDesc = uncBeh) then
                if (codeElementID is not null) then

                    indexXML := indexXMLTop;
                    indexXML := indexXML || '<TABULAR_REF type="UU_BEHAVIOUR">';
                    indexXML := indexXML || '<TF_CONTAINER_CONCEPT_ID>' || cims_util.retrieveContainingIdPathbyEId('ICD-10-CA', structureVersionID, codeElementID) || '</TF_CONTAINER_CONCEPT_ID>';
                    indexXML := indexXML || '<CODE_PRESENTATION>' || CIMS_ICD.formatXREFCode(categoryCode) || '</CODE_PRESENTATION>';
                    indexXML := indexXML || '</TABULAR_REF>';
                    indexXML := indexXML || indexXMLEnd;

                    -- Store XML snippet inside temp table
                    INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                    VALUES (parentElementID, 5, null, null, indexXML, language_code, null, null);

                end if;
            else
                --Actual Index item
                elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

                indexXML := indexXMLTop;
                indexXML := indexXML || '<BOOK_INDEX_TYPE>N</BOOK_INDEX_TYPE>';
                indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
                indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
                indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

                --See Also Flag
                IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                    --See Also flag is a Y or a N
                    seeAlsoFlagText := getSeeAlsoText(seeAlsoFlag, language_code);

                    --Check whether this Index has a referenced index description but no referenced index.
                    IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                        --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                        --as well as removing the see also flag
                        --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                        iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                        indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                        --Need to null a few values to ensure correctness
                        refItermID := null;
                        indexLeadTermElementID := null;
                        refLinkDesc := null;
                    ELSE
                        --No, proceed as normal
                        indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                    END IF;
                ELSE
                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values out since we have bad SeeAlsoFlag values
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                END IF;

                --Index Term Description
                propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
                ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermDesc, null);

                --Site Indicator
                IF (siDiamond > 0) THEN
                    indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
                ELSIF (siNumber > 0) THEN
                    indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
                END IF;

                --You need to store the iTermID and refItermID to build a reference link later on
                INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
                VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

                --Index Term Note Description
                IF (iTermNoteDesc is not null) THEN
                    propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                    ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                        iTermNoteDesc, language_code);
                END IF;

                --Nesting level
                propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
                ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

                indexXML := indexXML || indexXMLEnd;

                -- Store XML snippet inside temp table
                INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
                VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

                --Build Narrow Relationship
                propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
                ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, parentElementID,
                    structureVersionID);

                --Recursively call
                migrateNeoplasmIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, ilTermElementID, level + 1);
            end if;

        end loop;

    exception
        when others then
            insertLog('migrateNeoplasmIndexChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateNeoplasmIndexChildNodes. Error:' || substr(sqlerrm, 1, 512));
    end migrateNeoplasmIndexChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateIndexChildNodes
    * DESCRIPTION:
    **************************************************************************************************************************************/
    procedure migrateIndexChildNodes(p_version_code varchar2, structureVersionID number, parentElementID number, parentIndexID number,
        language_code varchar2, ilTermElementID number, indexTypeCode varchar2, level number) is

        cursor c is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            where i.parent_index_term_id = parentIndexID
            order by UPPER(i.index_term_desc);


        rec_cc c%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number;
        propertyClassID number;
        status_code varchar2(10);
        indexLeadTermElementID number;

        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        if (indexTypeCode = 'A') then
            indexClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'AlphabeticIndex');
        elsif (indexTypeCode = 'E') then
            indexClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ExternalInjuryIndex');
        end if;

        for rec_cc in c loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            indexLeadTermElementID := ilTermElementID;
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --insertLog('Migrating Index term: ' || iTermDesc);
            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);

            indexXML := indexXMLTop;
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || indexTypeCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                --See Also flag is a Y or a N
                seeAlsoFlagText := getSeeAlsoText(seeAlsoFlag, language_code);

                --Check whether this Index has a referenced index description but no referenced index.
                IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                    --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                    --as well as removing the see also flag
                    --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                    iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values to ensure correctness
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                ELSE
                    --No, proceed as normal
                    indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                END IF;
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                --Need to null a few values out since we have bad SeeAlsoFlag values
                refItermID := null;
                indexLeadTermElementID := null;
                refLinkDesc := null;
            END IF;

            --Index Term Description
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                iTermDesc, null);

            --Site Indicator
            IF (siDiamond > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
            ELSIF (siNumber > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermNoteDesc, language_code);
            END IF;

            --Nesting level
            propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
            ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, parentElementID,
                structureVersionID);

            --Recursively call
            migrateIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, ilTermElementID, indexTypeCode, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateIndexChildNodes:' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIndexChildNodes. Error:' || substr(sqlerrm, 1, 512));
    end migrateIndexChildNodes;


    /**************************************************************************************************************************************
    * NAME:          migrateDrugsIndex
    * DESCRIPTION:   These top level indexes appear to be called Index Lead Terms.
    **************************************************************************************************************************************/
    procedure migrateDrugsIndex(p_version_code varchar2, structureVersionID number, bookIndexID number, language_code varchar2) is
        cursor c_chapter is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            where i.book_index_id = bookIndexID
            and i.level_num = 1
            order by UPPER(i.index_term_desc);

        rec_cc c_chapter%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DrugsAndChemicalsIndex');
        propertyClassID number;
        status_code varchar2(10);
        letter number;
        letterAscii number;
        letterTmp varchar2(1);
        indexLeadTermElementID number;
        level number := 1;

        indexXMLTop VARCHAR2(500) := '<BOOK_INDEX_TYPE>D</BOOK_INDEX_TYPE>';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c_chapter loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --insertLog('Migrating Index term: ' || iTermDesc);
            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);
            indexLeadTermElementID := elementID;

            indexXML := indexXMLTop;
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                --See Also flag is a Y or a N
                seeAlsoFlagText := getSeeAlsoText(seeAlsoFlag, language_code);

                --Check whether this Index has a referenced index description but no referenced index.
                IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                    --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                    --as well as removing the see also flag
                    --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                    iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values to ensure correctness
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                ELSE
                    --No, proceed as normal
                    indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                END IF;
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';

                --Need to null a few values to ensure correctness
                refItermID := null;
                indexLeadTermElementID := null;
                refLinkDesc := null;
            END IF;

            --Index Term Description
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                iTermDesc, null);

            --Site Indicator
            IF (siDiamond > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
            ELSIF (siNumber > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID, iTermNoteDesc,
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

            --Nesting level
            propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
            ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, letter, structureVersionID);

            migrateDrugsIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, elementID, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateDrugsIndex ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateDrugsIndex. Error:' || substr(sqlerrm, 1, 512));
    end migrateDrugsIndex;


    /**************************************************************************************************************************************
    * NAME:          migrateNeoplasmIndex
    * DESCRIPTION:   These top level indexes appear to be called Index Lead Terms.
    **************************************************************************************************************************************/
    procedure migrateNeoplasmIndex(p_version_code varchar2, structureVersionID number, viewerRootElementID number,
        bookIndexID number, language_code varchar2)
    is
        cursor c_chapter is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            where i.book_index_id = bookIndexID
            and i.level_num = 1
            order by UPPER(i.index_term_desc);

        rec_cc c_chapter%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'NeoplasmIndex');
        propertyClassID number;
        status_code varchar2(10);
        indexLeadTermElementID number;
        level number := 1;

        indexXMLTop VARCHAR2(500) := '<BOOK_INDEX_TYPE>N</BOOK_INDEX_TYPE>';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        for rec_cc in c_chapter loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);
            indexLeadTermElementID := elementID;

            indexXML := indexXMLTop;
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                --See Also flag is a Y or a N
                seeAlsoFlagText := getSeeAlsoText(seeAlsoFlag, language_code);

                --Check whether this Index has a referenced index description but no referenced index.
                IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                    --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                    --as well as removing the see also flag
                    --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                    iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values to ensure correctness
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                ELSE
                    --No, proceed as normal
                    indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                END IF;
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                --Need to null a few values to ensure correctness
                refItermID := null;
                indexLeadTermElementID := null;
                refLinkDesc := null;
            END IF;

            --Index Term Description
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                iTermDesc, null);

            --Site Indicator
            IF (siDiamond > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
            ELSIF (siNumber > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermNoteDesc, language_code);
            END IF;

            --Nesting level
            propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
            ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID, level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, viewerRootElementID,
                structureVersionID);

            migrateNeoplasmIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, elementID, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateNeoplasmIndex ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateNeoplasmIndex. Error:' || substr(sqlerrm, 1, 512));
    end migrateNeoplasmIndex;


    /**************************************************************************************************************************************
    * NAME:          migrateIndex
    * DESCRIPTION:   These top level indexes appear to be called Index Lead Terms.
    **************************************************************************************************************************************/
    procedure migrateIndex(p_version_code varchar2, structureVersionID number, bookIndexID number, language_code varchar2,
        indexTypeCode varchar2)
    is
        cursor c_chapter is
            select i.index_term_id, trim(replace(replace(i.index_term_desc,'#'),'$')) index_term_desc, i.status_code,
                i.reference_index_term_id, i.reference_link_desc, i.see_also_flag, n.index_term_note_desc,
                instr(i.index_term_desc, '$') siDiamond, instr(i.index_term_desc, '#') siNumber
            from icd.index_term i
            LEFT OUTER JOIN icd.index_term_note n on i.index_term_id = n.index_term_id
            where i.book_index_id = bookIndexID
            and i.level_num = 1
            order by UPPER(i.index_term_desc);

        rec_cc c_chapter%rowtype;
        elementID number := 0;

        iTermID number;
        iTermDesc varchar2(1000);
        iStatusCode varchar2(1);
        refItermID number;
        refLinkDesc varchar2(255);
        seeAlsoFlag varchar2(1);
        seeAlsoFlagText varchar2(10);
        iTermNoteDesc clob;
        siDiamond number;
        siNumber number;

        indexClassID number;
        propertyClassID number;
        status_code varchar2(10);
        letter number;
        letterAscii number;
        letterTmp varchar2(1);
        indexLeadTermElementID number;
        level number := 1;

        indexXMLTop VARCHAR2(500) := '';
        indexXMLEnd VARCHAR2(50) := '';
        indexXML VARCHAR2(3000);

    begin

        if (indexTypeCode = 'A') then
            indexClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'AlphabeticIndex');
        elsif (indexTypeCode = 'E') then
            indexClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ExternalInjuryIndex');
        end if;

        for rec_cc in c_chapter loop
            iTermID := TRIM(rec_cc.index_term_id);
            iTermDesc := TRIM(rec_cc.index_term_desc);
            iStatusCode := TRIM(rec_cc.status_code);
            refItermID := TRIM(rec_cc.reference_index_term_id);
            refLinkDesc := TRIM(REPLACE(rec_cc.reference_link_desc, chr(10), chr(32)));
            seeAlsoFlag := TRIM(rec_cc.see_also_flag);
            iTermNoteDesc := TRIM(to_clob(rec_cc.index_term_note_desc));
            siDiamond := TRIM(rec_cc.sidiamond);
            siNumber := TRIM(rec_cc.sinumber);

            IF TRIM(iStatusCode) = 'A' THEN
                status_code := 'ACTIVE';
            ELSE
                status_code := 'DISABLED';
            END IF;

            --insertLog('Migrating Index term: ' || iTermDesc);
            elementID := ICD_DATA_MIGRATION.insertConcept(p_version_code, indexClassID, null, structureVersionID, status_code);
            indexLeadTermElementID := elementID;

            indexXML := indexXMLTop;
            indexXML := indexXML || '<BOOK_INDEX_TYPE>' || indexTypeCode || '</BOOK_INDEX_TYPE>';
            indexXML := indexXML || '<ELEMENT_ID>' || elementID || '</ELEMENT_ID>';
            indexXML := indexXML || '<INDEX_TYPE>INDEX_TERM</INDEX_TYPE>';
            indexXML := indexXML || '<LEVEL_NUM>' || level || '</LEVEL_NUM>';

            --See Also Flag
            IF ( (seeAlsoFlag is not null) AND (seeAlsoFlag != 'X') ) THEN
                --See Also flag is a Y or a N
                seeAlsoFlagText := getSeeAlsoText(seeAlsoFlag, language_code);

                --Check whether this Index has a referenced index description but no referenced index.
                IF ( (refLinkDesc is not null) AND (refItermID is null) ) THEN
                    --It is a yes, so in this case we modify the Index's description and remove any trace of the referenced description,
                    --as well as removing the see also flag
                    --Example:  echogram ==> echogram (See Abnormal, diagnostic imaging)
                    iTermDesc := iTermDesc || ' (' || seeAlsoFlagText || ' ' || refLinkDesc || ')';

                    indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                    --Need to null a few values to ensure correctness
                    refItermID := null;
                    indexLeadTermElementID := null;
                    refLinkDesc := null;
                ELSE
                    --No, proceed as normal
                    indexXML := indexXML || '<SEE_ALSO_FLAG>' || seeAlsoFlag || '</SEE_ALSO_FLAG>';
                END IF;
            ELSE
                indexXML := indexXML || '<SEE_ALSO_FLAG/>';
                --Need to null a few values to ensure correctness
                refItermID := null;
                indexLeadTermElementID := null;
                refLinkDesc := null;
            END IF;

            --Index Term Description
            propertyClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
            ICD_DATA_MIGRATION.insertTextProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                iTermDesc, null);

            --Site Indicator
            IF (siDiamond > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>$</SITE_INDICATOR>';
            ELSIF (siNumber > 0) THEN
                indexXML := indexXML || '<SITE_INDICATOR>#</SITE_INDICATOR>';
            END IF;

            --You need to store the iTermID and refItermID to build a reference link later on
            INSERT INTO Z_ICD_TEMP (A, B, C, D, E, F, G, H)
            VALUES (iTermID, elementID, refItermID, indexLeadTermElementID, refLinkDesc, 'INDEX', iTermDesc, null);

            --Index Term Note Description
            IF (iTermNoteDesc is not null) THEN
                propertyClassID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexNoteDesc');
                ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                    iTermNoteDesc, language_code);
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

            --Nesting level
            propertyClassID := CIMS_ICD.getICD10CAClassID('NumericPropertyVersion', 'Level');
            ICD_DATA_MIGRATION.insertNumericProperty(p_version_code, elementID, propertyClassID, structureVersionID,
                level);

            indexXML := indexXML || indexXMLEnd;

            -- Store XML snippet inside temp table
            INSERT INTO Z_XML_TEMP (A, B, C, D, E, F, G, H)
            VALUES (elementID, 0, null, null, indexXML, language_code, null, null);

            --Build Narrow Relationship
            propertyClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            ICD_DATA_MIGRATION.buildNarrowRelationship(p_version_code, propertyClassID, elementID, letter, structureVersionID);

            migrateIndexChildNodes(p_version_code, structureVersionID, elementID, iTermID, language_code, elementID, indexTypeCode, level + 1);

        end loop;

    exception
        when others then
            insertLog('migrateIndex ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in migrateIndex. Error:' || substr(sqlerrm, 1, 512));
    end migrateIndex;


    /**************************************************************************************************************************************
    * NAME:          buildXML
    * DESCRIPTION:
    **************************************************************************************************************************************/
    PROCEDURE buildXML(p_version_code varchar2, structureVersionID number, indexClassId number, indexTypeCode varchar2)
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
            where classid = indexClassId /* and rownum <= 5000*/;

        cursor x is
            select b, e, f
            from z_xml_temp
            where a = indexElementId;

        rec_cc c%rowtype;
        rec_xml x%rowtype;

        xmlType0 VARCHAR2(3000);         --Index Term 'main'
        xmlType1 VARCHAR2(3000);         --Neoplasm Benign snippet
        xmlType2 VARCHAR2(3000);         --Neoplasn InSitu snippet
        xmlType3 VARCHAR2(3000);         --Neoplasn Malignant Primary snippet
        xmlType4 VARCHAR2(3000);         --Neoplasn Malignant Secondary snippet
        xmlType5 VARCHAR2(3000);         --Neoplasn Unknown Behavior snippet
        xmlType6 VARCHAR2(3000);         --Drugs Adverse Effect snippet
        xmlType7 VARCHAR2(3000);         --Drugs Poison accident snippet
        xmlType8 VARCHAR2(3000);         --Drugs Poison XIX snippet
        xmlType9 VARCHAR2(3000);         --Drugs Intentional snippet
        xmlType10 VARCHAR2(3000);        --Drugs Unintentional snippet

        type xmlType_va is varray(100) of VARCHAR2(3000);
        xmlType11 xmlType_va;            --Reference Link
        xmlType12 xmlType_va;            --Category Link
        xmlType11Counter NUMBER := 1;    --VARRAYS start at 1
        xmlType12Counter NUMBER := 1;    --VARRAYS start at 1

        hasNeoplasm VARCHAR2(10) := 'FALSE';
        hasDrugs VARCHAR2(10) := 'FALSE';
        xmlTypeLanguage VARCHAR2(3000);

        counter number := 0;
        propertyClassID number := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexRefDefinition');
    begin
        insertLog('Beginning Index XML building...');

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
            xmlType1 := '';
            xmlType2 := '';
            xmlType3 := '';
            xmlType4 := '';
            xmlType5 := '';
            xmlType6 := '';
            xmlType7 := '';
            xmlType8 := '';
            xmlType9 := '';
            xmlType10 := '';

            hasNeoplasm := 'FALSE';
            hasDrugs := 'FALSE';

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
                elsif (xmlType = 1) then
                    xmlType1 := xmlSnippet;
                    hasNeoplasm := 'TRUE';
                elsif (xmlType = 2) then
                    xmlType2 := xmlSnippet;
                    hasNeoplasm := 'TRUE';
                elsif (xmlType = 3) then
                    xmlType3 := xmlSnippet;
                    hasNeoplasm := 'TRUE';
                elsif (xmlType = 4) then
                    xmlType4 := xmlSnippet;
                    hasNeoplasm := 'TRUE';
                elsif (xmlType = 5) then
                    xmlType5 := xmlSnippet;
                    hasNeoplasm := 'TRUE';
                elsif (xmlType = 6) then
                    xmlType6 := xmlSnippet;
                    hasDrugs := 'TRUE';
                elsif (xmlType = 7) then
                    xmlType7 := xmlSnippet;
                    hasDrugs := 'TRUE';
                elsif (xmlType = 8) then
                    xmlType8 := xmlSnippet;
                    hasDrugs := 'TRUE';
                elsif (xmlType = 9) then
                    xmlType9 := xmlSnippet;
                    hasDrugs := 'TRUE';
                elsif (xmlType = 10) then
                    xmlType10 := xmlSnippet;
                    hasDrugs := 'TRUE';
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
            indexXMLTop := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd"><index language="' || xmlTypeLanguage || '" classification="ICD-10-CA">';
            --indexXMLTop := '<index language="' || xmlTypeLanguage || '" classification="ICD-10-CA">';
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

            if (indexTypeCode = 'D') then
                if (hasDrugs = 'TRUE') then

                    indexXML := indexXML || '<DRUGS_DETAIL>';

                    if (xmlType8 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="CHAPTER_XIX"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType8;
                    end if;

                    if (xmlType7 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="ACCIDENTAL"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType7;
                    end if;

                    if (xmlType9 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="INT_SELF_HARM"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType9;
                    end if;

                    if (xmlType10 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="UNDE_INTENT"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType10;
                    end if;

                    if (xmlType6 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="AETU"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType6;
                    end if;

                    indexXML := indexXML || '</DRUGS_DETAIL>';

                else
                    indexXML := indexXML || '<DRUGS_DETAIL/>';
                end if;
            end if;

            if (indexTypeCode = 'N') then
                if (hasNeoplasm = 'TRUE') then

                    indexXML := indexXML || '<NEOPLASM_DETAIL>';

                    if (xmlType3 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_PRIMARY"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType3;
                    end if;

                    if (xmlType4 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_SECONDARY"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType4;
                    end if;

                    if (xmlType2 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="IN_SITU"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType2;
                    end if;

                    if (xmlType1 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="BENIGN"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType1;
                    end if;

                    if (xmlType5 is NULL) then
                        indexXML := indexXML || '<TABULAR_REF type="UU_BEHAVIOUR"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                    else
                        indexXML := indexXML || xmlType5;
                    end if;

                    indexXML := indexXML || '</NEOPLASM_DETAIL>';
                else
                    indexXML := indexXML || '<NEOPLASM_DETAIL/>';
                end if;
            end if;

            indexXML := indexXML || indexXMLEnd;

            --INSERT INTO HOWARD (A, XMLTEXT)
            --VALUES (indexElementId, indexXML);

            ICD_DATA_MIGRATION.insertXMLProperty(p_version_code, indexElementId, propertyClassID, structureVersionID,
                indexXML, xmlTypeLanguage);

        end loop;

        insertLog('----------------------------------------------------');
        insertLog('Processed Index XML: ' || counter);
        insertLog('Ending Index XML building...');

        commit;
    exception
        when others then
            insertLog('buildXML ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildXML. Error:' || substr(sqlerrm, 1, 512));
    end buildXML;


    /**************************************************************************************************************************************
    * NAME:          buildXML
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*PROCEDURE buildXMLTest(p_version_code varchar2, structureVersionID number, indexClassId number, indexTypeCode varchar2, indexElementId number)
    IS
        indexXMLTop VARCHAR2(3000) := '';
        indexXMLEnd VARCHAR2(3000) := '';
        indexXML VARCHAR2(3000);

        xmlType number;
        xmlSnippet VARCHAR2(3000);
        xmlLanguage VARCHAR2(3000);

        cursor x is
            select b, e, f
            from z_xml_temp
            where a = indexElementId;

        rec_xml x%rowtype;

        xmlType0 VARCHAR2(3000);         --Index Term 'main'
        xmlType1 VARCHAR2(3000);         --Neoplasm Benign snippet
        xmlType2 VARCHAR2(3000);         --Neoplasn InSitu snippet
        xmlType3 VARCHAR2(3000);         --Neoplasn Malignant Primary snippet
        xmlType4 VARCHAR2(3000);         --Neoplasn Malignant Secondary snippet
        xmlType5 VARCHAR2(3000);         --Neoplasn Unknown Behavior snippet
        xmlType6 VARCHAR2(3000);         --Drugs Adverse Effect snippet
        xmlType7 VARCHAR2(3000);         --Drugs Poison accident snippet
        xmlType8 VARCHAR2(3000);         --Drugs Poison XIX snippet
        xmlType9 VARCHAR2(3000);         --Drugs Intentional snippet
        xmlType10 VARCHAR2(3000);        --Drugs Unintentional snippet

        type xmlType_va is varray(100) of VARCHAR2(3000);
        xmlType11 xmlType_va;            --Reference Link
        xmlType12 xmlType_va;            --Category Link
        xmlType11Counter NUMBER := 1;    --VARRAYS start at 1
        xmlType12Counter NUMBER := 1;    --VARRAYS start at 1

        hasNeoplasm VARCHAR2(10) := 'FALSE';
        hasDrugs VARCHAR2(10) := 'FALSE';
        xmlTypeLanguage VARCHAR2(3000);

        counter number := 0;
        propertyClassID number := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IndexRefDefinition');
    begin

        xmlType11 := xmlType_va('');
        xmlType12 := xmlType_va('');
        xmlType11Counter := 1;
        xmlType12Counter := 1;

        -- Delete tmp variables that hold the XML
        xmlType0 := '';
        xmlType1 := '';
        xmlType2 := '';
        xmlType3 := '';
        xmlType4 := '';
        xmlType5 := '';
        xmlType6 := '';
        xmlType7 := '';
        xmlType8 := '';
        xmlType9 := '';
        xmlType10 := '';

        hasNeoplasm := 'FALSE';
        hasDrugs := 'FALSE';

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
            elsif (xmlType = 1) then
                xmlType1 := xmlSnippet;
                hasNeoplasm := 'TRUE';
            elsif (xmlType = 2) then
                xmlType2 := xmlSnippet;
                hasNeoplasm := 'TRUE';
            elsif (xmlType = 3) then
                xmlType3 := xmlSnippet;
                hasNeoplasm := 'TRUE';
            elsif (xmlType = 4) then
                xmlType4 := xmlSnippet;
                hasNeoplasm := 'TRUE';
            elsif (xmlType = 5) then
                xmlType5 := xmlSnippet;
                hasNeoplasm := 'TRUE';
            elsif (xmlType = 6) then
                xmlType6 := xmlSnippet;
                hasDrugs := 'TRUE';
            elsif (xmlType = 7) then
                xmlType7 := xmlSnippet;
                hasDrugs := 'TRUE';
            elsif (xmlType = 8) then
                xmlType8 := xmlSnippet;
                hasDrugs := 'TRUE';
            elsif (xmlType = 9) then
                xmlType9 := xmlSnippet;
                hasDrugs := 'TRUE';
            elsif (xmlType = 10) then
                xmlType10 := xmlSnippet;
                hasDrugs := 'TRUE';
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

dbms_output.put_line('xmlType6: ' || xmlType6);
dbms_output.put_line(xmlType6);
        -- TODO: Some checks should be done here to ensure the data is all here??
        indexXMLTop := '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE index SYSTEM "/dtd/cihi_cims_index.dtd"><index language="' || xmlTypeLanguage || '" classification="ICD-10-CA">';
        --indexXMLTop := '<index language="' || xmlTypeLanguage || '" classification="ICD-10-CA">';
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

        if (indexTypeCode = 'D') then
            if (hasDrugs = 'TRUE') then

                indexXML := indexXML || '<DRUGS_DETAIL>';

                if (xmlType8 is NULL) then
                    indexXML := indexXML || '<TABULAR_REF type="CHAPTER_XIX"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType8;
                end if;

                if (xmlType7 is NULL) then
                    indexXML := indexXML || '<TABULAR_REF type="ACCIDENTAL"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType7;
                end if;

                if (xmlType9 is NULL) then
                    indexXML := indexXML || '<TABULAR_REF type="INT_SELF_HARM"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType9;
                end if;

                if (xmlType10 is NULL) then
                    indexXML := indexXML || '<TABULAR_REF type="UNDE_INTENT"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType10;
                end if;

                if (xmlType6 is NULL) then
dbms_output.put_line('1');
                    indexXML := indexXML || '<TABULAR_REF type="AETU"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
dbms_output.put_line('2');
                    indexXML := indexXML || xmlType6;
                end if;

                indexXML := indexXML || '</DRUGS_DETAIL>';

            else
                indexXML := indexXML || '<DRUGS_DETAIL/>';
            end if;
        end if;

        if (indexTypeCode = 'N') then
            if (hasNeoplasm = 'TRUE') then

                indexXML := indexXML || '<NEOPLASM_DETAIL>';

                if (xmlType3 = '') then
                    indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_PRIMARY"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType3;
                end if;

                if (xmlType4 = '') then
                    indexXML := indexXML || '<TABULAR_REF type="MALIGNANT_SECONDARY"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType4;
                end if;

                if (xmlType2 = '') then
                    indexXML := indexXML || '<TABULAR_REF type="IN_SITU"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType2;
                end if;

                if (xmlType1 = '') then
                    indexXML := indexXML || '<TABULAR_REF type="BENIGN"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType1;
                end if;

                if (xmlType5 = '') then
                    indexXML := indexXML || '<TABULAR_REF type="UU_BEHAVIOUR"><TF_CONTAINER_CONCEPT_ID></TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION></CODE_PRESENTATION></TABULAR_REF>';
                else
                    indexXML := indexXML || xmlType5;
                end if;

                indexXML := indexXML || '</NEOPLASM_DETAIL>';
            else
                indexXML := indexXML || '<NEOPLASM_DETAIL/>';
            end if;
        end if;

        indexXML := indexXML || indexXMLEnd;

        dbms_output.put_line(indexXML);


    exception
        when others then
            dbms_output.put_line('buildXML ' || SQLCODE || ' ' || SQLERRM);
            raise_application_error(-20011, 'Error occurred in buildXML. Error:' || substr(sqlerrm, 1, 512));
    end buildXMLTest;*/


    /**************************************************************************************************************************************
    * NAME:          populate_DomainValues
    * DESCRIPTION:
    **************************************************************************************************************************************/
    /*procedure populate_DomainValues(version_code varchar2, structureVersionID number) is
        domainValueClassID number;
        dvElementID number;
    begin

        insertLog('  - See Also Flag');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'SeeAlso');

        dvElementID := ICD_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, 'N', null, null, null, null, null,
            null, null, domainValueClassID);
        dvElementID := ICD_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, 'Y', null, null, null, null, null,
            null, null, domainValueClassID);

        insertLog('  - Site Indicator Flag');
        domainValueClassID := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'SiteIndicator');

        dvElementID := ICD_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, '$', null, null, null, null, null,
            null, null, domainValueClassID);
        dvElementID := ICD_DATA_MIGRATION.populateDomainValueLookup(version_Code, structureVersionID, '#', null, null, null, null, null,
            null, null, domainValueClassID);

    end populate_DomainValues;*/


    /**************************************************************************************************************************************
    * NAME:          main
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/
    PROCEDURE main(version_Code varchar2) is
        structureVersionID number := CIMS_ICD.getICD10CAStructureIDByYear(version_Code);
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

        --Ensure that the year already exists
        IF structureVersionID = -9999 THEN
            dbms_output.put_line(version_Code || ' does not exist in ICD.  Exiting...');
        END IF;

        logRunID := LOG_RUN_SEQ.Nextval;
        insertLog('Starting ICD10CA index migration ' || version_code || '.  Migration Run ID: ' || logRunID);

        --Get the Viewer Root
        viewerRootElementID := CIMS_ICD.geticd10caroot(version_Code);
        insertLog('Viewer Root for ' || version_Code || ' is ' || viewerRootElementID);

        --insertLog('Populating Domain Value tables');
        --populate_DomainValues(version_Code, structureVersionID);

        insertLog('Populating Book Indexes');
        populateBookIndexLookup(version_Code, structureVersionID, viewerRootElementID);

        insertLog('Main migration');
        FOR i IN book_index.FIRST .. book_index.LAST LOOP
            insertLog('  - ' || book_index_description(i) || ': ' || i || ' = ' || book_index(i) ||
                         '.  Lang: ' || book_index_language(i) || '.  Type Code: ' || book_index_typecode(i));

            IF (book_index_typecode(i) = 'N') THEN
                migrateNeoplasmIndex(version_Code, structureVersionID, book_index(i), i, book_index_language(i));
            ELSIF (book_index_typecode(i) = 'D') THEN
                populateLetterIndex(version_Code, structureVersionID, book_index(i), book_index_language(i), book_index_typecode(i));
                migrateDrugsIndex(version_Code, structureVersionID, i, book_index_language(i));
            ELSE
                populateLetterIndex(version_Code, structureVersionID, book_index(i), book_index_language(i), book_index_typecode(i));
                migrateIndex(version_Code, structureVersionID, i, book_index_language(i), book_index_typecode(i));
            END IF;
        END LOOP;

        insertLog('Building reference links between index terms');
        buildIndexReferenceLinks(structureVersionID);

        --Build the Index to Category reference
        insertLog('Building links between index term and tabular concept');
        buildIndexCategoryLinks(version_Code, structureVersionID);

        CIMS_ICD.GATHER_SCHEMA_STATS;

        --Build XML from snippet pieces
        insertLog('Building XML from snippet pieces');

        -- Neoplasm
        insertLog('  - Building XML for Neoplasm index');
        buildXML(version_Code, structureVersionID, CIMS_ICD.getICD10CAClassID('ConceptVersion', 'NeoplasmIndex'), 'N');

        -- Alphabetic
        insertLog('  - Building XML for Alphabetic index');
        buildXML(version_Code, structureVersionID, CIMS_ICD.getICD10CAClassID('ConceptVersion', 'AlphabeticIndex'), 'A');

        -- Drugs and Chemicals
        insertLog('  - Building XML for Drugs index');
        buildXML(version_Code, structureVersionID, CIMS_ICD.getICD10CAClassID('ConceptVersion', 'DrugsAndChemicalsIndex'), 'D');

        -- External Injury
        insertLog('  - Building XML for External Injury index');
        buildXML(version_Code, structureVersionID, CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ExternalInjuryIndex'), 'E');

        insertLog('Ending ICD10CA index migration ' || version_code);

        commit;
--        rollback;

    END main;


end ICD_DATA_MIGRATION_INDEX;
/
