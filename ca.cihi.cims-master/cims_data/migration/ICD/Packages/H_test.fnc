create or replace function H_test(version_code_fr number, version_code_to number)
    RETURN TYPES.ref_cursor
AS


    rootNodeData_cursor TYPES.ref_cursor;
    begin
                       
        OPEN rootNodeData_cursor FOR
            WITH elementPropertys AS (
                select c.category_id, c.parent_category_id, TRIM(c.category_code) CATEGORY_CODE, version_code_fr VERSION_YEAR
                from icd.category c 
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_code_fr

                union all

                select c.category_id, c.parent_category_id, TRIM(c.category_code) CATEGORY_CODE, version_code_to VERSION_YEAR
                from icd.category c 
                WHERE TRIM(c.clinical_classification_code) =  '10CA' || version_code_to
            )
            SELECT
                version_code_fr Migration_Year, 
                category_code,
                MAX(DECODE(VERSION_YEAR, version_code_fr, category_id, NULL)) F_CATEGORY_ID, 
                MAX(DECODE(VERSION_YEAR, version_code_to, category_id, NULL)) T_CATEGORY_ID,
                version_code_fr f_VERSION_YEAR, 
                version_code_to t_VERSION_YEAR
            FROM elementPropertys ep
            GROUP BY CATEGORY_CODE
            ORDER BY CATEGORY_CODE;


        RETURN rootNodeData_cursor;
    exception
        when others then
            dbms_output.put_line('Error is: ' || substr(sqlerrm, 1, 5000));


end H_test;
/
