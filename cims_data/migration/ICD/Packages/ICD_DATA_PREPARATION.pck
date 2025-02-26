create or replace package ICD_DATA_PREPARATION is

    icd_classification_code varchar2(20) := 'ICD-10-CA';
    f_year number := 0;
    errString varchar(4000);
    isVersionYear boolean := FALSE;

    --PROCEDURE compareXMLData(version_Code_from number, version_Code_to number, type_code varchar2, lang varchar2, typeClassID number);
    PROCEDURE cleanUp;
    PROCEDURE part2_XML(version_Code_from IN number, version_Code_to IN number);
    --PROCEDURE part1_text(version_Code_from IN number, version_Code_to IN number);
    PROCEDURE main;    

end ICD_DATA_PREPARATION;
/
create or replace package body ICD_DATA_PREPARATION is

    /**************************************************************************************************************************************
    * NAME:          compareYears
    * DESCRIPTION:   
    **************************************************************************************************************************************/   
    PROCEDURE compareYears(version_Code_from number, version_Code_to number) is
        cursor c is
            select * 
            from icd.category c
            where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to
            and c.category_code not in (
                select category_code 
                from icd.category c
                where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_from
            )
            order by category_code;

    BEGIN
        dbms_output.put_line('Comparing ICD Year ' || version_Code_from || ' to ' || version_Code_to);

    END compareYears;


    /**************************************************************************************************************************************
    * NAME:          compareTableData
    * DESCRIPTION:   
    **************************************************************************************************************************************/   
    PROCEDURE compareTableData(version_Code_from number, version_Code_to number, lang varchar2, typeClassID number) is
        cursor c is
            SELECT * 
            FROM
                ( 
                select c.category_id CATEGORY_ID_TO, TRIM(c.category_code) CATEGORY_CODE_TO, cto.language_code LANGUAGE_CODE_TO, 
                    cto.category_table_output_data data_to
                from icd.category c
                LEFT OUTER JOIN icd.category_table_output cto on cto.category_id = c.category_id and cto.language_code = lang
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to 
                ) A
                FULL JOIN
                (
                select c.category_id CATEGORY_ID_FR, TRIM(c.category_code) CATEGORY_CODE_FR, cto.language_code LANGUAGE_CODE_FR, 
                    cto.category_table_output_data data_fr
                from icd.category c
                LEFT OUTER JOIN icd.category_table_output cto on cto.category_id = c.category_id and cto.language_code = lang
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_Code_from 
                ) B
                ON A.CATEGORY_CODE_TO = B.CATEGORY_CODE_FR;

        categoryCode VARCHAR2(30) := 0;        
        strTo CLOB;
        strFr CLOB;
    BEGIN

        for rec_cc in c loop

            categoryCode := TRIM(rec_cc.category_code_to);

            strTo := rec_cc.data_to;
            strFr := rec_cc.data_fr;
            
            IF (strTo IS NOT NULL) THEN
                IF dbms_lob.compare(nvl(strFr,'X'),nvl(strTo,'X')) != 0 THEN
                    INSERT INTO Z_ICD_DIFFS_XML(DIFFSID, Category_Id_From, Category_Id_To, Category_Code_From, CATEGORY_CODE_TO, 
                           Xmltext_From, XMLTEXT_TO, Classid, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGECODE)
                    VALUES(DIFFS_SEQ.Nextval, rec_cc.category_id_fr, rec_cc.category_id_to, rec_cc.category_code_fr, rec_cc.category_code_to,
                            rec_cc.data_fr, rec_cc.data_to, typeClassID, version_Code_from, version_Code_to, lang);
                END IF;
            END IF;

        end loop;
        commit;
    EXCEPTION 
        when others then
            dbms_output.put_line('Larger than 4k error I think on category code ' || categoryCode || '  '); 
    END compareTableData;


    /**************************************************************************************************************************************
    * NAME:          compareXMLData
    * DESCRIPTION:   
    **************************************************************************************************************************************/   
    PROCEDURE compareXMLData(version_Code_from number, version_Code_to number, type_code varchar2, lang varchar2, typeClassID number) is
        cursor c is

            SELECT * 
            FROM
                ( 
                select c.category_id CATEGORY_ID_TO, TRIM(c.category_code) CATEGORY_CODE_TO, cd.language_code LANGUAGE_CODE_TO, 
                    cd.category_detail_data data_to
                from icd.category c
                LEFT OUTER JOIN icd.category_detail cd on cd.category_id = c.category_id and cd.language_code = lang and TRIM(cd.category_detail_type_code) = type_code
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to 
                ) A
                FULL JOIN
                (
                select c.category_id CATEGORY_ID_FR, TRIM(c.category_code) CATEGORY_CODE_FR, cd.language_code LANGUAGE_CODE_FR, 
                    cd.category_detail_data data_fr
                from icd.category c
                LEFT OUTER JOIN icd.category_detail cd on cd.category_id = c.category_id and cd.language_code = lang and TRIM(cd.category_detail_type_code) = type_code
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_Code_from                 
                ) B
                ON A.CATEGORY_CODE_TO = B.CATEGORY_CODE_FR;

        categoryCode VARCHAR2(30) := 0;        
        strTo CLOB;
        strFr CLOB;
        
    BEGIN

        for rec_cc in c loop

            categoryCode := TRIM(rec_cc.CATEGORY_CODE_TO);

            strTo := rec_cc.data_to;
            strFr := rec_cc.data_fr;

            IF isVersionYear THEN
                IF dbms_lob.compare(nvl(strFr,'X'),nvl(strTo,'X')) != 0 THEN
                    INSERT INTO Z_ICD_DIFFS_XML(DIFFSID, Category_Id_From, Category_Id_To, Category_Code_From, CATEGORY_CODE_TO, 
                           XMLTEXT_FROM, XMLTEXT_TO, Classid, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGECODE)
                    VALUES(DIFFS_SEQ.Nextval, rec_cc.category_id_fr, rec_cc.category_id_to, TRIM(rec_cc.category_code_fr), 
                           TRIM(rec_cc.category_code_to), rec_cc.data_fr, rec_cc.data_to, typeClassID, version_Code_from, version_Code_to, lang);
                END IF;
            ELSE
                --Null indicates they didnt copy it over to the new year.  Do not add to diffs table
                IF ( strTo is NOT NULL ) THEN
                    IF dbms_lob.compare(nvl(strFr,'X'),nvl(strTo,'X')) != 0 THEN
                        INSERT INTO Z_ICD_DIFFS_XML(DIFFSID, Category_Id_From, Category_Id_To, Category_Code_From, CATEGORY_CODE_TO, 
                               XMLTEXT_FROM, XMLTEXT_TO, Classid, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGECODE)
                        VALUES(DIFFS_SEQ.Nextval, rec_cc.category_id_fr, rec_cc.category_id_to, TRIM(rec_cc.category_code_fr), 
                               TRIM(rec_cc.category_code_to), rec_cc.data_fr, rec_cc.data_to, typeClassID, version_Code_from, version_Code_to, lang);
                    END IF;
                END IF;    

            END IF;


        end loop;

        commit;

    END compareXMLData;


    /**************************************************************************************************************************************
    * NAME:          cleanUp
    * DESCRIPTION:   Deletes the Z_ICD_ tables used
    **************************************************************************************************************************************/   
    PROCEDURE cleanUp is

    BEGIN
        dbms_output.put_line('Deleting Z_ tables used for Historial Migration');
        DELETE FROM Z_Icd_Diffs_XML;
        DELETE FROM Z_Icd_Diffs_Text;
        COMMIT;
        dbms_output.put_line('Done');

    END cleanUp;


    /**************************************************************************************************************************************
    * NAME:          part2_XML
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE part2_XML(version_Code_from IN number, version_Code_to IN number) is
        classID number := 0;

    BEGIN

        IF ( version_Code_to IN (2002, 2004, 2005, 2008, 2010, 2011, 2013, 2014) ) THEN
            isVersionYear := FALSE;
        ELSE
            isVersionYear := TRUE;            
        END IF;

        --Compare I ENG / FRA data       
        classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'IncludePresentation');
        compareXMLData(version_Code_from, version_Code_to, 'I', 'ENG', classID);
        compareXMLData(version_Code_from, version_Code_to, 'I', 'FRA', classID);

        --Compare E ENG / FRA data       
        classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'ExcludePresentation');
        compareXMLData(version_Code_from, version_Code_to, 'E', 'ENG', classID);
        compareXMLData(version_Code_from, version_Code_to, 'E', 'FRA', classID);

        --Compare A ENG / FRA data       
        classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'CodeAlsoPresentation');
        compareXMLData(version_Code_from, version_Code_to, 'A', 'ENG', classID);
        compareXMLData(version_Code_from, version_Code_to, 'A', 'FRA', classID);

        --Compare N ENG / FRA data       
        classID := CIMS_ICD.getICD10CAClassID('XMLPropertyVersion', 'NotePresentation');
        compareXMLData(version_Code_from, version_Code_to, 'N', 'ENG', classID);
        compareXMLData(version_Code_from, version_Code_to, 'N', 'FRA', classID);

        --Compare N ENG / FRA data       
        classID := CIMS_ICD.getICD10CAClassID('HTMLPropertyVersion', 'TablePresentation');
        compareTableData(version_Code_from, version_Code_to, 'ENG', classID);
        compareTableData(version_Code_from, version_Code_to, 'FRA', classID);

    END part2_XML;


    /**************************************************************************************************************************************
    * NAME:          part1_text0203Fix
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE part1_text0203Fix(version_Code_from IN number, version_Code_to IN number) is
        cursor c is
            select 
                c.category_id T_CATEGORY_ID, cz.category_id F_CATEGORY_ID,  
                c.category_code T_category_code, cz.category_code F_category_code,
                f.short_desc T_SHORT_DESC_FRE, f1.short_desc F_SHORT_DESC_FRE,
                f.long_desc T_LONG_DESC_FRE, f1.LONG_desc F_LONG_DESC_FRE,
                f.USER_desc T_USER_DESC_FRE, f1.USER_desc F_USER_DESC_FRE
            FROM icd.category c
            LEFT JOIN icd.french_category_desc f on c.category_id = f.category_id
            join (
                SELECT c.*
                FROM ICD.CATEGORY c
                WHERE TRIM(c.clinical_classification_code) = '10CA2001'

                union all

                SELECT c1.*
                FROM ICD.CATEGORY c1
                WHERE TRIM(c1.clinical_classification_code) = '10CA2002'
                AND TRIM(c1.category_code) IN ('U0491', 'U0490', 'U04')
                ) cz on TRIM(cz.category_code) = TRIM(c.category_code)
            LEFT JOIN icd.french_category_desc f1 on cz.category_id = f1.category_id
            where TRIM(c.clinical_classification_code) =  '10CA' || 2003;

        shortTitleClassID number := 0;
        longTitleClassID number := 0;
        userTitleClassID number := 0;      

        categoryID_F number := 0;
        categoryID_T number := 0;
        categoryCode VARCHAR2(30) := 0;

        strTo VARCHAR2(255) := 0;
        strFr VARCHAR2(255) := 0;
    BEGIN

        shortTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        longTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        userTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
         
        DELETE FROM Z_ICD_DIFFS_TEXT z
        WHERE z.versioncode_to = 2003
        AND z.language = 'FRA'
        AND z.classid IN (shortTitleClassID, longTitleClassID, userTitleClassID);
        
        dbms_output.put_line('Starting data preparation....');
        for rec_cc in c loop

            categoryID_F := TRIM(rec_cc.F_category_id);
            categoryID_T := TRIM(rec_cc.T_category_id);
            categoryCode := TRIM(rec_cc.T_category_code);

            --French Short Title
            strTo := TRIM(rec_cc.t_short_desc_fre);
            strFr := TRIM(rec_cc.f_short_desc_fre);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), shortTitleClassID, version_Code_from, version_Code_to, 'FRA');
            END IF;        


            --French Long Title
            strTo := TRIM(rec_cc.t_long_desc_fre);
            strFr := TRIM(rec_cc.f_long_desc_fre);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), longTitleClassID, version_Code_from, version_Code_to, 'FRA');
            END IF;

            --French User Title
            strTo := TRIM(rec_cc.t_user_desc_fre);
            strFr := TRIM(rec_cc.f_user_desc_fre);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), userTitleClassID, version_Code_from, version_Code_to, 'FRA');
            END IF;

        end loop;
        commit;

    END part1_text0203Fix;


    /**************************************************************************************************************************************
    * NAME:          part1_text
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE part1_text(version_Code_from IN number, version_Code_to IN number, usePrevYrForFREIfNull boolean DEFAULT NULL) is
        cursor c is
            select 
                c.category_id T_CATEGORY_ID, c1.category_id F_CATEGORY_ID,  
                c.SHORT_DESC T_SHORT_DESC, c1.SHORT_DESC F_SHORT_DESC,
                c.LONG_DESC T_LONG_DESC, c1.LONG_DESC F_LONG_DESC,
                c.USER_DESC T_USER_DESC, c1.USER_DESC F_USER_DESC,
                c.category_code T_category_code, c1.category_code F_category_code,
                c.ca_enhancement_flag T_ca_enhancement_flag, c1.ca_enhancement_flag F_ca_enhancement_flag,
                c.dagger_asterisk T_dagger_asterisk, c1.dagger_asterisk F_dagger_asterisk,
                c.code_flag T_code_flag, c1.code_flag F_code_flag,
                c.render_children_as_table_flag T_render_flag, c1.render_children_as_table_flag F_render_flag,
                f.short_desc T_SHORT_DESC_FRE, f1.short_desc F_SHORT_DESC_FRE,
                f.long_desc T_LONG_DESC_FRE, f1.LONG_desc F_LONG_DESC_FRE,
                f.USER_desc T_USER_DESC_FRE, f1.USER_desc F_USER_DESC_FRE
            FROM icd.category c
            LEFT JOIN icd.french_category_desc f on c.category_id = f.category_id
            join icd.category c1 on c1.category_code = c.category_code
            LEFT JOIN icd.french_category_desc f1 on c1.category_id = f1.category_id
            where TRIM(c.clinical_classification_code) =  '10CA' || version_Code_to
            and TRIM(c1.clinical_classification_code) =  '10CA' || version_Code_from;

        shortTitleClassID number := 0;
        longTitleClassID number := 0;
        userTitleClassID number := 0;
        codeClassID number := 0;
        caEnhancementFlagClassID number := 0;
        daClassID number := 0;
        codeFlagClassID number := 0;
        renderClassID number := 0;


        categoryID_F number := 0;
        categoryID_T number := 0;
        categoryCode VARCHAR2(30) := 0;

        strTo VARCHAR2(255) := 0;
        strFr VARCHAR2(255) := 0;
    BEGIN

        shortTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
        longTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
        userTitleClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'UserTitle');
        codeClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');             
        caEnhancementFlagClassID := CIMS_ICD.getICD10CAClassID('BooleanPropertyVersion', 'CaEnhancementIndicator');             
        daClassID := CIMS_ICD.getICD10CAClassID('EnumeratedPropertyVersion', 'DaggerAsteriskIndicator');   
        codeFlagClassID := CIMS_ICD.getICD10CAClassID('BooleanPropertyVersion', 'ValidCodeIndicator');             
        renderClassID := CIMS_ICD.getICD10CAClassID('BooleanPropertyVersion', 'RenderChildrenAsTableIndicator');             
   
        dbms_output.put_line('Starting data preparation....');
        for rec_cc in c loop

            categoryID_F := TRIM(rec_cc.F_category_id);
            categoryID_T := TRIM(rec_cc.T_category_id);
            categoryCode := TRIM(rec_cc.T_category_code);

            strTo := TRIM(rec_cc.t_short_desc);
            strFr := TRIM(rec_cc.f_short_desc);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), shortTitleClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_long_desc);
            strFr := TRIM(rec_cc.f_long_desc);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), longTitleClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_user_desc);
            strFr := TRIM(rec_cc.f_user_desc);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), userTitleClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_category_code);
            strFr := TRIM(rec_cc.f_category_code);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), codeClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_ca_enhancement_flag);
            strFr := TRIM(rec_cc.f_ca_enhancement_flag);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), caEnhancementFlagClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_dagger_asterisk);
            strFr := TRIM(rec_cc.f_dagger_asterisk);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), daClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.t_code_flag);
            strFr := TRIM(rec_cc.f_code_flag);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), codeFlagClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;

            strTo := TRIM(rec_cc.T_render_flag);
            strFr := TRIM(rec_cc.F_render_flag);
            IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), renderClassID, version_Code_from, version_Code_to, 'ENG');
            END IF;



            --French Short Title
            strTo := TRIM(rec_cc.t_short_desc_fre);
            strFr := TRIM(rec_cc.f_short_desc_fre);
            IF (usePrevYrForFREIfNull) THEN
                IF ( (nvl(strFr,'X') <> nvl(strTo,'X')) AND (nvl(strTo,'X') <> 'X') ) THEN

                    --Established that it doesnt match.  Now only add to diff if strTo is not null

                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), shortTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;
            ELSE
                IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), shortTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;        
            END IF;

            --French Long Title
            strTo := TRIM(rec_cc.t_long_desc_fre);
            strFr := TRIM(rec_cc.f_long_desc_fre);
            IF (usePrevYrForFREIfNull) THEN

                IF ( (nvl(strFr,'X') <> nvl(strTo,'X')) AND (nvl(strTo,'X') <> 'X') ) THEN
                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), longTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;

            ELSE
                IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), longTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;
            END IF;

            --French User Title
            strTo := TRIM(rec_cc.t_user_desc_fre);
            strFr := TRIM(rec_cc.f_user_desc_fre);
            IF (usePrevYrForFREIfNull) THEN

                IF ( (nvl(strFr,'X') <> nvl(strTo,'X')) AND (nvl(strTo,'X') <> 'X') ) THEN
                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), userTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;

            ELSE            
                IF (nvl(strFr,'X') <> nvl(strTo,'X')) THEN
                    INSERT INTO Z_ICD_DIFFS_TEXT(DIFFSID, CATEGORY_ID_FROM, CATEGORY_ID_TO, CATEGORY_CODE, Text_From, Text_To, CLASSID, VERSIONCODE_FROM, VERSIONCODE_TO, LANGUAGE)
                    VALUES(DIFFS_SEQ.Nextval, categoryID_F, categoryID_T, categoryCode, nvl(strFr,' '), nvl(strTo,' '), userTitleClassID, version_Code_from, version_Code_to, 'FRA');
                END IF;
            END IF;

        end loop;
        commit;

    END part1_text;


    /**************************************************************************************************************************************
    * NAME:          main
    * DESCRIPTION:   Starting point
    **************************************************************************************************************************************/   
    PROCEDURE main is
    BEGIN

        cleanUp;

        part1_text(2001,2002, true);
        part1_text(2002,2003);
        part1_text0203Fix(2002,2003);
        part1_text(2003,2004);
        part1_text(2004,2005);
        part1_text(2005,2006);
        part1_text(2006,2007);
        part1_text(2007,2008);
        part1_text(2008,2009);
        part1_text(2009,2010);
        part1_text(2010,2011);
        part1_text(2011,2012);
        part1_text(2012,2013);
        part1_text(2013,2014);
        part1_text(2014,2015);

        part2_XML(2001, 2002);
        part2_XML(2001, 2003);
        part2_XML(2003, 2004);
        part2_XML(2003, 2005);    
        part2_XML(2003, 2006);
        part2_XML(2006, 2007);    
        part2_XML(2007, 2008);
        part2_XML(2007, 2009);    
        part2_XML(2009, 2010);
        part2_XML(2009, 2011);    
        part2_XML(2009, 2012);
        part2_XML(2012, 2013);    
        part2_XML(2012, 2014);
        part2_XML(2012, 2015);

        commit;

    END main;


end ICD_DATA_PREPARATION;
/
