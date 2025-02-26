create or replace package CIMS_ASOT_API is

  -- Author  : TYANG
  -- Created : 29/04/2015 1:48:06 PM
  -- Purpose : Write data to ASOT tables
  
  -- Public type declarations
  TYPE t_classification_tab IS table of t_classification;
  TYPE t_classification_version_tab IS table of t_classification_version;
  TYPE t_classification_release_tab IS table of t_classification_release;
  -- Public constant declarations

  -- Public variable declarations

  -- Public function and procedure declarations
  procedure generateASOT(pVersionCode varchar2, pReleaseId number, email varchar2);
  
  function getClassification return t_classification_tab pipelined;
  function getClassificationVersion(pClassificationCode varchar2, pVersionCode varchar2) return t_classification_version_tab pipelined;
  function getClassificationRelease(pClassificationCode varchar2, pVersionCode varchar2, pReleaseId number) return t_classification_release_tab pipelined;
  
  function getClassificationReleaseCode(pClassificationCode varchar2, pVersionCode varchar2) return varchar2;
  
end CIMS_ASOT_API;
/
create or replace package body CIMS_ASOT_API is

  -- Private type declarations
  
  -- Private constant declarations

  -- Private variable declarations
  procedure insertLog(pMessage varchar2, pReleaseId number, pStatusCode varchar2, pTypeCode varchar2);
  procedure deleteLog(pReleaseId number);
  procedure prepareRelease(pVersionCode varchar2, nReleaseId number);
  procedure processDataHolding(pClassification varchar2, pVersionCode varchar2);
  procedure processICD10CA(pVersionCode varchar2, nReleaseId number);
  procedure processICDValidationRule(pTabularCode varchar2, pElementId number, pHasChild varchar2, pValidationRule t_icd10ca_dh_validation, nContextId number, sReleaseCode varchar2);
  procedure processICDParentCodeVR(pElementId number, pValidationRule t_icd10ca_dh_validation, nContextId number, sReleaseCode varchar2);  
  
  procedure processCCI(pVersionCode varchar2, nReleaseId number);
  procedure processCCIValidationRule(pTabularCode varchar2, pElementId number, pHasChild varchar2, pValidationRule t_cci_validation_rule, nContextId number, sReleaseCode varchar2);
  procedure processCCIParentCodeVR(pElementId number, pValidationRule t_cci_validation_rule, nContextId number, sReleaseCode varchar2);

  procedure insertICD10CATabular(pVersionCode varchar2, pReleaseCode varchar2);
  
  
  -- Function and procedure implementations
  
  /**************************************************************************************************************************************
    * NAME:          insertLog
    * DESCRIPTION:   Write to the log table
    **************************************************************************************************************************************/
    procedure insertLog(pMessage varchar2, pReleaseId number, pStatusCode varchar2, pTypeCode varchar2) is
        logDate date;
        logID number := 0;

        PRAGMA AUTONOMOUS_TRANSACTION;
    begin

        dbms_output.put_line(pMessage);

        logID := LOG_SEQ.Nextval;
        logDate := sysdate;

        insert into ASOT_ETL_LOG(ASOT_ETL_LOG_ID, ASOT_ETL_LOG, START_DATE, PUBLICATION_RELEASE_ID,
               ASOT_ETL_LOG_STATUS_CODE, ASOT_ETL_LOG_TYPE_CODE)
        values (logID, pMessage, logDate, pReleaseId, pStatusCode, pTypeCode);

       commit;

    end insertLog;
    
    procedure deleteLog(pReleaseId number) is
        PRAGMA AUTONOMOUS_TRANSACTION;
    begin
        if pReleaseId is null then
            delete from asot_etl_log where publication_release_id is null;
        else
           delete from asot_etl_log where publication_release_id = pReleaseId;
        end if;
        commit;
    end deleteLog;
  
  function getClassification return t_classification_tab pipelined
  IS
      cursor cClassification is
      Select c.ClassName  
      from Class c where c.tablename='BaseClassification' ;
      
  BEGIN
       for cRecord in cClassification
       loop
           pipe row(t_classification(cRecord.className, cRecord.ClassName, cRecord.Classname));        
       end loop;
      return;
  END getClassification;
  
  function getClassificationVersion(pClassificationCode varchar2, pVersionCode varchar2) return t_classification_version_tab pipelined
  IS
      Cursor cClassification_version is
      Select c.classname||'_'||ev.versioncode as clssification_version_code, c.classname as classification_code, c.classname||'_'||ev.versioncode as classification_version_desc, ev.versioncode as version_code
         from structureversion sv, elementversion ev, class c 
         where sv.basestructureid is null and sv.structureid=ev.elementversionid and c.classname=pClassificationCode and c.classid=sv.classid and ev.versioncode=pVersionCode;

  BEGIN
      for cRecord in cClassification_version
      loop
         pipe row(t_classification_version(cRecord.Clssification_Version_Code, cRecord.Classification_Code, cRecord.Classification_Version_Desc, cRecord.Version_Code));  
      end loop;
      return;
  END getClassificationVersion;
  
  function getClassificationRelease(pClassificationCode varchar2, pVersionCode varchar2, pReleaseId number) return t_classification_release_tab pipelined
  IS
      cursor cRelease is
      select cv.classification_version_code as release_code, r.release_desc, r.release_date, 
      r.release_status_code, cv.classification_version_code 
      from classification_version cv, (
          select rt.release_type_code as release_desc, pr.creation_date as release_date, pr.release_status_code, pr.fiscal_year 
          from publication_release pr, release_type rt 
          where pr.release_type_id=rt.release_type_id and pr.fiscal_year=pVersionCode and pr.publication_release_id=pReleaseId
          order by pr.creation_date desc
        ) r
      where cv.classification_code=pClassificationCode and cv.version_code=pVersionCode and cv.version_code=r.fiscal_year;
      
        
  BEGIN
      for cRecord in cRelease
      loop
        pipe row(t_classification_release(cRecord.Release_Code, cRecord.release_desc, cRecord.release_date, cRecord.release_status_code, cRecord.classification_version_code));  
      end loop;
      return;
  END getClassificationRelease;
  
  function getClassificationReleaseCode(pClassificationCode varchar2, pVersionCode varchar2) return varchar2
  IS
       sReleaseCode VARCHAR2(20);
  BEGIN
       select classification_version_code into sReleaseCode 
       from classification_version 
       where classification_code=pClassificationCode and version_code=pVersionCode;
       
       return sReleaseCode;
  END getClassificationReleaseCode;
  
  procedure generateASOT(pVersionCode varchar2, pReleaseId number, email varchar2)
  IS
  BEGIN
      dbms_output.put_line('start asot at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      
      --deleteLog(nReleaseId);
      if pReleaseId>0 or pVersionCode='2015' then
         
          insertLog( 'Start ASOT table generation for fiscal year: '||pVersionCode, pReleaseId, 'E', 'GEN');
          --1. 
          prepareRelease(pVersionCode, pReleaseId);
          --2.
          processICD10CA(pVersionCode, pReleaseId);
          --3.
          processCCI(pVersionCode, pReleaseId);
          commit;
          --4. replicate data
          --cims_replication_proc(p_email_address=>email,p_env=>sys_context( 'USERENV' ,'DB_NAME'));
          
          insertLog( 'Finished data replication. ', null, 'E','GEN');
          
      else
          insertLog( 'Release not ready for fiscal year: '||pVersionCode||', please check. ', null, 'F','ERR');
          raise_application_error(-20011, 'Release not ready for fiscal year: '||pVersionCode||', please check. ');
      end if;
      
      insertLog( 'Finished ASOT table generation for fiscal year: '||pVersionCode, pReleaseId, 'E', 'GEN');
      
      exception
          when no_data_found then
            insertLog( 'Release not ready for fiscal year: '||pVersionCode||', please check. ', null, 'F','ERR');
            raise_application_error(-20011, 'Release not ready for fiscal year: '||pVersionCode||', please check. ');
      
  END generateASOT;
  
  
  procedure prepareRelease(pVersionCode varchar2, nReleaseId number)
  IS
     nCount  NUMBER(9);
     CURSOR cClassifications IS Select * from classification;
     sClassificationVersionCode VARCHAR2(20);
  BEGIN
     select count(1) into nCount from classification;
     if nCount=0 then
        -- insert classification, should only need do once
        insert into classification 
        Select c.ClassName as classification_code, c.ClassName classification_e_desc, c.ClassName classification_F_desc 
        from Class c where c.tablename='BaseClassification' ;
        
        insertLog('Finished insert classification table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'PRE');
     end if;
     
     -- prepare classification_version, classification_release
     for rClassification in cClassifications
     loop
         select count(1) into nCount from classification_version cv 
         where cv.classification_code=rClassification.classification_code and cv.version_code=pVersionCode;
         
         if nCount=0 then
             -- each year run once
             insert into classification_version select * from table(getClassificationVersion(rClassification.classification_code,pVersionCode));
             
             insertLog('Finished insert classification_version table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'PRE');
         end if;
         
         --right now each version_code will only have one release_code and use same value
         select classification_version_code into sClassificationVersionCode from classification_version cv 
         where cv.classification_code=rClassification.classification_code and cv.version_code=pVersionCode;
         
         -- clean old release data
         delete from icd10ca_dh_validation where classification_release_code = sClassificationVersionCode;
         delete from icd10ca_tabular where classification_release_code = sClassificationVersionCode;
         delete from cci_location_validation where classification_release_code = sClassificationVersionCode;
         delete from cci_mode_of_del_validation where classification_release_code = sClassificationVersionCode;
         delete from cci_status_validation where classification_release_code = sClassificationVersionCode;
         delete from cci_extent_validation where classification_release_code = sClassificationVersionCode;
         delete from cci_dh_validation where classification_release_code = sClassificationVersionCode;
         delete from cci_mode_of_del_attribute where classification_release_code = sClassificationVersionCode;
         delete from cci_status_attribute where classification_release_code = sClassificationVersionCode;
         delete from cci_extent_attribute where classification_release_code = sClassificationVersionCode;
         delete from cci_location_attribute where classification_release_code = sClassificationVersionCode;
         delete from cci_location_reference_value where classification_release_code = sClassificationVersionCode;
         delete from cci_mode_of_del_ref_value where classification_release_code = sClassificationVersionCode;
         delete from cci_status_reference_value where classification_release_code = sClassificationVersionCode;
         delete from cci_extent_reference_value where classification_release_code = sClassificationVersionCode;
         delete from cci_component where classification_release_code = sClassificationVersionCode;
         delete from cci_interv where classification_release_code = sClassificationVersionCode;
         delete from cci_approach_technique where classification_release_code = sClassificationVersionCode;
         delete from cci_tissue where classification_release_code = sClassificationVersionCode;
         delete from cci_device_agent where classification_release_code = sClassificationVersionCode;
         delete from cci_group where classification_release_code = sClassificationVersionCode;
         delete from cci_tabular where classification_release_code = sClassificationVersionCode;
         delete from data_holding where classification_release_code = sClassificationVersionCode;
         delete from classification_release where classification_release_code = sClassificationVersionCode;
         
         
         -- insert new release
         -- for 2015, there are no release data, has to manually handle it
         if pVersionCode='2015' then
             insert into classification_release select sClassificationVersionCode, 'Official', sysdate, 'E', sClassificationVersionCode from dual;
         else
             insert into classification_release select * from table(getClassificationRelease(rClassification.classification_code, pVersionCode, nReleaseId));
         end if;
         
         insertLog('Finished insert '||rClassification.classification_code||' classification_release table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'PRE');
         -- data holding
         processDataHolding(rClassification.classification_code, pVersionCode);
         
         insertLog('Finished insert '||rClassification.classification_code||' data_holding table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'PRE');
     end loop;
     dbms_output.put_line('finished prepare release at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
     exception
        when others then
            insertLog('Error occured in prepareRelease procedure: '|| SQLCODE || ' ' || SQLERRM, nReleaseId, 'F', 'ERR');
            raise_application_error(-20011, 'Error occurred in prepareRelease. <br> Error:' || substr(sqlerrm, 1, 512));
  END prepareRelease;
  
  procedure processDataHolding(pClassification varchar2, pVersionCode varchar2)
  IS
      
  BEGIN
     insert into data_holding (data_holding_code, classification_release_code, data_holding_desc, data_holding_uuid) 
     select d.data_holding_code, cv.classification_version_code, d.data_holding_desc, d.data_holding_uuid
     from table( cims_retrieve_api.getDataHolding(pClassification,pVersionCode) ) d, classification_version cv where d.version_code=cv.version_code and cv.classification_code=pClassification;
  END processDataHolding;
  
  procedure processICD10CA(pVersionCode varchar2, nReleaseId number)
  IS
      sReleaseCode VARCHAR2(20) := getClassificationReleaseCode(cims_retrieve_api.icd10ca_classification_code, pVersionCode);
      nContextId              NUMBER(9);
      cursor cICDValidationRules is select * from table(cims_retrieve_api_icd.getICD10CADHValidationsTab(pVersionCode));
  
      BEGIN
      --1. icd tabular
      insertICD10CATabular(pVersionCode, sReleaseCode);
      
      dbms_output.put_line('finished icd tabular at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert icd10ca_tabular table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'ICD');
      --2. icd validation
      --insertICD10CADHValidations(pVersionCode);
      nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
      for cRecord in cICDValidationRules
      loop
          processICDValidationRule(cRecord.Icd10ca_Tabular_Code, cRecord.Element_Id, cRecord.Has_Child, 
                 t_icd10ca_dh_validation(cRecord.Element_Id, cRecord.Has_Child, cRecord.Icd10ca_Tabular_Code, 
                 cRecord.Data_Holding_Code, cRecord.Version_Code, cRecord.Gender_Validation_Code, cRecord.Age_Min,
                 cRecord.Age_Max, cRecord.Newborn_Ind_Code, cRecord.Main_Diagnostic_Ind_Code, cRecord.Diagnostic_Type_1_Ind_Code,
                 cRecord.Diagnostic_Type_2_Ind_Code, cRecord.Diagnostic_Type_3_Ind_Code,cRecord.Diagnostic_Type_4_Ind_Code,
                 cRecord.Diagnostic_Type_6_Ind_Code,cRecord.Diagnostic_Type_9_Ind_Code,cRecord.Diagnostic_Type_w_Ind_Code,
                 cRecord.Diagnostic_Type_x_Ind_Code,cRecord.Diagnostic_Type_y_Ind_Code), nContextId, sReleaseCode);   

      end loop;
      
      dbms_output.put_line('finished icd validation at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert icd10ca_dh_validation table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'ICD');
      
      exception
        when others then
            insertLog('Error occured in processICD10CA procedure: '|| SQLCODE || ' ' || SQLERRM, nReleaseId, 'F', 'ERR');
            raise_application_error(-20011, 'Error occurred in processICD10CA. <br> Error:' || substr(sqlerrm, 1, 512));
  END processICD10CA;
    
    /*
   * process icd validation, based on if current tabular code has child code or not recursive call to insert only leaf level validation rule.
   */
  procedure processICDValidationRule(pTabularCode varchar2, pElementId number, pHasChild varchar2, pValidationRule t_icd10ca_dh_validation, 
    nContextId number, sReleaseCode varchar2) 
  is
  begin
    
    if pHasChild='N' then
       --dbms_output.put_line('tabularCode: '||pTabularCode );
       --insert data
       insert into icd10ca_dh_validation ( icd10ca_tabular_code, data_holding_code, classification_release_code, 
           gender_validation_code, age_min, age_max, newborn_ind_code, main_diagnostic_ind_code, diagnostic_type_1_ind_code,
           diagnostic_type_2_ind_code,diagnostic_type_3_ind_code, diagnostic_type_4_ind_code, diagnostic_type_6_ind_code,
           diagnostic_type_9_ind_code, diagnostic_type_w_ind_code, diagnostic_type_x_ind_code, diagnostic_type_y_ind_code)
       values ( pTabularCode,  pValidationRule.data_holding_code, sReleaseCode, pValidationRule.gender_validation_code,
           pValidationRule.age_min, pValidationRule.age_max, pValidationRule.newborn_ind_code, pValidationRule.main_diagnostic_ind_code,
           pValidationRule.diagnostic_type_1_ind_code,pValidationRule.diagnostic_type_2_ind_code,pValidationRule.diagnostic_type_3_ind_code,
           pValidationRule.diagnostic_type_4_ind_code,pValidationRule.diagnostic_type_6_ind_code,pValidationRule.diagnostic_type_9_ind_code,
           pValidationRule.diagnostic_type_w_ind_code,pValidationRule.diagnostic_type_x_ind_code,pValidationRule.diagnostic_type_y_ind_code);
       
    else
       --find child codes
       processICDParentCodeVR(pElementId, pValidationRule, nContextId, sReleaseCode);
    end if;
  end processICDValidationRule;

  procedure processICDParentCodeVR(pElementId number, pValidationRule t_icd10ca_dh_validation, 
    nContextId number, sReleaseCode varchar2) 
  is
       nCodeClassId           NUMBER(9);
       nCategoryClassId        NUMBER(9);
       nNarrowerClassId       NUMBER(9);
       cursor cChildCodes is
       select cv.elementid, REGEXP_REPLACE(tpv.text,'[^a-zA-Z0-9]','') as tabularCode,  cims_util.hasActiveChildren(nContextId, cv.elementid) as hasChild
         from conceptpropertyversion cpv, conceptversion cv, textpropertyversion tpv
         , structureelementversion sev,  structureelementversion sev1,
         structureelementversion sev2
         where cv.classid = nCategoryClassId and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId 
         and cpv.classid = nNarrowerClassId and tpv.domainelementid=cv.elementid and tpv.classid=nCodeClassId and tpv.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
         and cpv.rangeelementid=pElementId
         and cpv.domainelementid=cv.elementid
         and cpv.conceptpropertyid = sev.elementversionid
         and sev.structureid=nContextId
         and cv.status='ACTIVE';
  begin
       nCategoryClassId := cims_icd.getICD10CAClassID('ConceptVersion', 'Category');
       nNarrowerClassId := cims_icd.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
       nCodeClassId := cims_icd.getICD10CAClassID('TextPropertyVersion', 'Code');
       
            
       for cCode in cChildCodes
       loop
           processICDValidationRule(cCode.tabularCode, cCode.elementId, cCode.Haschild, pValidationRule, nContextId, sReleaseCode);
       end loop;
  end processICDParentCodeVR;
  
  procedure insertICD10CATabular(pVersionCode varchar2, pReleaseCode varchar2)
  AS
      nChapterClassId number(9) := cims_icd.getICD10CAClassID('ConceptVersion', 'Chapter');
      nBlockClassId number(9) := cims_icd.getICD10CAClassID('ConceptVersion', 'Block');
      nCategoryClassId number(9) := cims_icd.getICD10CAClassID('ConceptVersion', 'Category');
  BEGIN

      INSERT INTO icd10ca_tabular (icd10ca_tabular_code, classification_release_code, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code)
      select icd10ca_tabular_code, pReleaseCode, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code
      from table (cims_retrieve_api_icd.getICD10TabularInfoTab(pVersionCode, nChapterClassId, 'N'));
      


      INSERT INTO icd10ca_tabular (icd10ca_tabular_code, classification_release_code, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code)
      select icd10ca_tabular_code, pReleaseCode, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code 
      from table (cims_retrieve_api_icd.getICD10TabularInfoTab(pVersionCode, nBlockClassId, 'N'));
      
      INSERT INTO icd10ca_tabular (icd10ca_tabular_code, classification_release_code, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code)
      select icd10ca_tabular_code, pReleaseCode, tabular_short_e_desc, 
          tabular_short_f_desc, tabular_long_e_desc, tabular_long_f_desc, tabular_type_code, tabular_status_code,
          code_ind_code,parent_tabular_code,tabular_uuid,formatted_code 
      from table (cims_retrieve_api_icd.getICD10TabularInfoTab(pVersionCode, nCategoryClassId, 'N'));


  end insertICD10CATabular;
  
  procedure processCCI(pVersionCode varchar2, nReleaseId number)
  IS
      
      cCCIComp t_cci_component_tab;
     
      cCCILocRef t_cci_loc_ref_value_tab;
     
      cCCIModeRef t_cci_mode_ref_value_tab;
     
      cCCIStaRef t_cci_sta_ref_value_tab;
     
      cCCIExtRef t_cci_ext_ref_value_tab;
      
      sReleaseCode VARCHAR2(20) := getClassificationReleaseCode('CCI', pVersionCode);
      cursor cCCISections is select * from cci_tabular ct where ct.classification_release_code=sReleaseCode and ct.tabular_type_code='Section';
      cursor cCCIValidationRules is select * from table(cims_retrieve_api_cci.getCCIValidationRule(pVersionCode));
      nContextId NUMBER(9) := cims_cci.getCCIStructureByYear(pVersionCode);
      
  BEGIN
    
      insert into cci_tabular (cci_tabular_code, classification_release_code,
               tabular_short_e_desc, tabular_short_f_desc, tabular_long_e_desc,
               tabular_long_f_desc,tabular_status_code, tabular_type_code,
               parent_tabular_code, tabular_uuid, formatted_code) 
      select cci_tabular_code, sReleaseCode, tabular_short_e_desc,
               tabular_short_f_desc, tabular_long_e_desc,
               tabular_long_f_desc, tabular_status_code,
               tabular_type_code, parent_tabular_code,
               tabular_uuid, formatted_code
      from table( cims_retrieve_api_cci.getCCITabularInfo(pVersionCode) );
      
      dbms_output.put_line('finished cci tabular at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_tabular table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      for cRecord in cCCISections
      loop
          insert into cci_group (cci_group_code, classification_release_code, section_code,
                  group_short_e_desc,group_short_f_desc, group_long_e_desc, group_long_f_desc, 
                  group_definition_e_desc, group_definition_f_desc) 
          select cci_group_code, sReleaseCode, section_code,
                 group_short_e_desc, group_short_f_desc,
                 group_long_e_desc, group_long_f_desc,
                 group_definition_e_desc, group_definition_f_desc
          from table(cims_retrieve_api_cci.getCCICompGroup(pVersionCode,cRecord.cci_tabular_code));
          
          
          insert into cci_tissue (cci_tissue_code, classification_release_code,
                          section_code, tissue_short_e_desc, tissue_short_f_desc, tissue_long_e_desc,
                          tissue_long_f_desc) 
          select cci_tissue_code, sReleaseCode, section_code,
                 decode( tissue_short_e_desc, null, 'DO NOT USE', tissue_short_e_desc),
                 tissue_short_f_desc, 
                 decode( tissue_long_e_desc, null, 'DO NOT USE', tissue_long_e_desc),
                 tissue_long_f_desc
          from table( cims_retrieve_api_cci.getCCITissue(pVersionCode,cRecord.cci_tabular_code) );
          
          
          
          
          insert into cci_interv (cci_interv_code, classification_release_code, section_code,
                  interv_short_e_desc,interv_short_f_desc, interv_long_e_desc, interv_long_f_desc, 
                  interv_definition_e_desc, interv_definition_f_desc) 
          select cci_interv_code, sReleaseCode, section_code,
                 interv_short_e_desc, interv_short_f_desc,
                 interv_long_e_desc, interv_long_f_desc,
                 interv_definition_e_desc, interv_definition_f_desc
          from table(cims_retrieve_api_cci.getCCIInterv(pVersionCode,cRecord.cci_tabular_code));
         
          
           insert into cci_device_agent (cci_device_agent_code, classification_release_code, section_code,
                            device_agent_short_e_desc,device_agent_short_f_desc, device_agent_long_e_desc, device_agent_long_f_desc, 
                            device_agent_type_e_desc, device_agent_type_f_desc, device_agent_e_example, device_agent_f_example) 
           select cci_device_agent_code, sReleaseCode, section_code,
                           decode( device_agent_short_e_desc, null, 'DO NOT USE', device_agent_short_e_desc),
                           device_agent_short_f_desc,
                           decode( device_agent_long_e_desc, null, 'DO NOT USE', device_agent_long_e_desc),
                           device_agent_long_f_desc,
                           device_agent_type_e_desc, device_agent_type_f_desc,
                           device_agent_e_example, device_agent_f_example
           from table( cims_retrieve_api_cci.getCCIDeviceAgent(pVersionCode,cRecord.cci_tabular_code));
           
         
          
          insert into cci_approach_technique (cci_approach_technique_code, classification_release_code,
                  section_code, approach_technique_short_e_des, approach_technique_short_f_des, approach_technique_long_e_desc,
                  approach_technique_long_f_desc) 
           select cci_approach_technique_code, sReleaseCode, section_code,
                     approach_technique_short_e_des, approach_technique_short_f_des, approach_technique_long_e_desc,
                  approach_technique_long_f_desc
           from table(cims_retrieve_api_cci.getCCIApproachTechnique(pVersionCode,cRecord.cci_tabular_code));
          
      end loop;
      
      dbms_output.put_line('finished cci component description at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_group, cci_interv, cci_tissue, cci_device_agent, cci_appoach_technique table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
       
      
      select cims_retrieve_api_cci.getCCIComponent(pVersionCode) into cCCIComp from dual;
      for i in 1 .. cCCIComp.count
      loop
          begin
              
              insert into cci_component (classification_release_code,cci_tabular_code,
              section_code, cci_approach_technique_code, cci_interv_code, cci_tissue_code,
              cci_group_code, cci_device_agent_code) 
              values 
              (sReleaseCode, cCCIComp(i).cci_tabular_code,
              cCCIComp(i).section_code, cCCIComp(i).cci_approach_technique_code, 
              cCCIComp(i).cci_interv_code, cCCIComp(i).cci_tissue_code,
              cCCIComp(i).cci_group_code, cCCIComp(i).cci_device_agent_code);
              
              EXCEPTION
                  WHEN OTHERS THEN
                    -- Cannot catch exception. This handler is never invoked.
                    DBMS_OUTPUT.PUT_LINE
                      ('Component code for: '||cCCIComp(i).cci_tabular_code||' has problem. '|| SQLERRM);
                    dbms_output.put_line('Group: '||cCCIComp(i).cci_group_code||' Intervention: '||cCCIComp(i).cci_interv_code||' Device: '||cCCIComp(i).cci_device_agent_code||' Tissue: '||cCCIComp(i).cci_tissue_code||' Approach: '||cCCIComp(i).cci_approach_technique_code);
              
          end;
          
      end loop;
      
      dbms_output.put_line('finished cci component at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_component table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      select cims_retrieve_api_cci.getCCILocationRefValues(pVersionCode) into cCCILocRef from dual;
      for i in 1 .. cCCILocRef.count
      Loop
          
          insert into cci_location_reference_value (location_ref_value_code,
          classification_release_code, location_e_desc, location_f_desc, mandatory_ind_code, location_uuid)
          values (cCCILocRef(i).location_ref_value_code,
          sReleaseCode, cCCILocRef(i).location_e_desc, cCCILocRef(i).location_f_desc, cCCILocRef(i).mandatory_ind_code,
          cCCILocRef(i).location_uuid);
          
          
          insert into cci_location_attribute (location_attribute_code, location_ref_value_code,
            classification_release_code,location_attribute_e_desc,location_attribute_f_desc,
            location_attribute_uuid)
          select attribute_code, ref_value_code, sReleaseCode,
                 attribute_e_desc, attribute_f_desc, element_uuid
          from table(cims_retrieve_api_cci.getCCIGenericAttributes(pVersionCode,cCCILocRef(i).location_ref_value_code));
      end loop;
      
      dbms_output.put_line('finished cci location ref at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_location_reference_value, cci_location_attribute table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      select cims_retrieve_api_cci.getCCIModeOfDelRefValues(pVersionCode) into cCCIModeRef from dual;
      for i in 1 .. cCCIModeRef.count
      Loop
          
          insert into cci_mode_of_del_ref_value (mode_of_del_ref_value_code, classification_release_code,
                 mode_of_delivery_e_desc, mode_of_delivery_f_desc, mandatory_ind_code, mode_of_delivery_uuid)
          values (cCCIModeRef(i).mode_of_del_ref_value_code, sReleaseCode,
                 cCCIModeRef(i).mode_of_delivery_e_desc,cCCIModeRef(i).mode_of_delivery_f_desc, cCCIModeRef(i).mandatory_ind_code, cCCIModeRef(i).mode_of_delivery_uuid);
          
          insert into cci_mode_of_del_attribute (mode_of_del_attribute_code, mode_of_del_ref_value_code,
                 classification_release_code, mode_of_del_attribute_e_desc, mode_of_del_attribute_f_desc, mode_of_del_attribute_uuid)
          select attribute_code, ref_value_code, sReleaseCode,
                 attribute_e_desc, attribute_f_desc, element_uuid
          from table(cims_retrieve_api_cci.getCCIGenericAttributes(pVersionCode,cCCIModeRef(i).mode_of_del_ref_value_code)) ;
      end loop;
      
      dbms_output.put_line('finished cci mode ref at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_mode_of_del_ref_value, cci_mode_of_del_attribute table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      select cims_retrieve_api_cci.getCCIStatusRefValues(pVersionCode) into cCCIStaRef from dual;
      for i in 1 .. cCCIStaRef.count
      Loop
          
          insert into cci_status_reference_value(status_ref_value_code, classification_release_code,
                 status_e_desc, status_f_desc, mandatory_ind_code, status_uuid)
          values (cCCIStaRef(i).status_ref_value_code, sReleaseCode,
                 cCCIStaRef(i).status_e_desc, cCCIStaRef(i).status_f_desc, cCCIStaRef(i).mandatory_ind_code, cCCIStaRef(i).status_uuid);
                 
          insert into cci_status_attribute (status_attribute_code, status_ref_value_code, classification_release_code,
                 status_attribute_e_desc, status_attribute_f_desc, status_attribute_uuid)
          select attribute_code, ref_value_code, sReleaseCode,
                 attribute_e_desc, attribute_f_desc, element_uuid
          from table(cims_retrieve_api_cci.getCCIGenericAttributes(pVersionCode,cCCIStaRef(i).status_ref_value_code)) ;
      end loop;
      
      dbms_output.put_line('finished cci status ref at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_status_reference_value, cci_status_attribute table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      
      select cims_retrieve_api_cci.getCCIExtentRefValues(pVersionCode) into cCCIExtRef from dual;
      for i in 1 .. cCCIExtRef.count
      Loop
          
          insert into cci_extent_reference_value (extent_ref_value_code, classification_release_code,
                 extent_e_desc, extent_f_desc, mandatory_ind_code, extent_uuid)
          values (cCCIExtRef(i).extent_ref_value_code, sReleaseCode,
                 cCCIExtRef(i).extent_e_desc, cCCIExtRef(i).extent_f_desc, cCCIExtRef(i).mandatory_ind_code, cCCIExtRef(i).extent_uuid);
          insert into cci_extent_attribute (extent_attribute_code, extent_ref_value_code, classification_release_code,
                 extent_attribute_e_desc, extent_attribute_f_desc, extent_attribute_uuid)
          select attribute_code, ref_value_code, sReleaseCode,
                 attribute_e_desc, attribute_f_desc, element_uuid
          from table(cims_retrieve_api_cci.getCCIGenericAttributes(pVersionCode,cCCIExtRef(i).extent_ref_value_code));
      end loop;
      
      dbms_output.put_line('finished cci extent ref at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci_extent_reference_value, cci_extent_attribute table for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      
      for cRecord in cCCIValidationRules
      loop
          processCCIValidationRule(cRecord.tabular_code, cRecord.element_id, cRecord.has_child,
          t_cci_validation_rule(cRecord.element_id, cRecord.version_code, cRecord.tabular_code, 
                cRecord.has_child, cRecord.data_holding_code, cRecord.age_min, cRecord.age_max, 
                cRecord.gender_validation_code, cRecord.status_ref_code, cRecord.extent_ref_code,
                cRecord.location_ref_code, cRecord.mode_of_del_ref_code, cRecord.data_holding_validation_uuid), nContextId, sReleaseCode);    
      end loop;
      
      
      
      dbms_output.put_line('finished cci validation at: '||to_char(sysdate,'YYYY/MM/DD HH24:MI:SS'));
      insertLog('Finished insert cci validation tables for fiscal year '||pVersionCode||'.', nReleaseId, 'E', 'CCI'); 
      
      exception
        when others then
            insertLog('Error occured in processCCI procedure: '|| SQLCODE || ' ' || SQLERRM, nReleaseId, 'F', 'ERR');
            raise_application_error(-20011, 'Error occurred in processCCI. <br> Error:' || substr(sqlerrm, 1, 512));
  END processCCI;
  
  /*
   * process cci validation, based on if current tabular code has child code or not recursive call to insert only leaf level validation rule.
   */
  procedure processCCIValidationRule(pTabularCode varchar2, pElementId number, pHasChild varchar2, pValidationRule t_cci_validation_rule, nContextId number, sReleaseCode varchar2) is
       
  begin
    
    if pHasChild='N' then
       --dbms_output.put_line('tabularCode: '||pTabularCode );
       --insert data
       insert into cci_dh_validation values ( pValidationRule.data_holding_code, pTabularCode, 
       sReleaseCode, pValidationRule.age_min, pValidationRule.age_max, pValidationRule.gender_validation_code, pValidationRule.data_holding_validation_uuid);
       if pValidationRule.location_ref_code is not null then
           insert into cci_location_validation values ( pValidationRule.location_ref_code, pValidationRule.data_holding_code, pTabularCode, sReleaseCode);
       end if;
       
       if pValidationRule.mode_of_del_ref_code is not null then
         --dbms_output.put_line('mode: '||pValidationRule.mode_of_del_ref_code );
           insert into cci_mode_of_del_validation (mode_of_del_ref_value_code, data_holding_code, cci_tabular_code, classification_release_code) values ( pValidationRule.mode_of_del_ref_code, pValidationRule.data_holding_code, pTabularCode, sReleaseCode);
       end if;
       
       if pValidationRule.extent_ref_code is not null then
           insert into cci_extent_validation values ( pValidationRule.extent_ref_code, pValidationRule.data_holding_code, pTabularCode, sReleaseCode);
       end if;
       
       if pValidationRule.status_ref_code is not null then
           insert into cci_status_validation values ( pValidationRule.status_ref_code, pValidationRule.data_holding_code, pTabularCode, sReleaseCode);
       end if;
    else
       --find child codes
       processCCIParentCodeVR(pElementId, pValidationRule, nContextId, sReleaseCode);
    end if;
  end processCCIValidationRule;
  
  procedure processCCIParentCodeVR(pElementId number, pValidationRule t_cci_validation_rule, nContextId number, sReleaseCode varchar2) is
       nCodeClassId           NUMBER(9);
       nCCICODEClassId        NUMBER(9);
       nNarrowerClassId       NUMBER(9);
       cursor cChildCodes is
       select cv.elementid, REGEXP_REPLACE(tpv.text,'[^a-zA-Z0-9]','') as tabularCode,  cims_util.hasActiveChildren(nContextId, cv.elementid) as hasChild
         from conceptpropertyversion cpv, conceptversion cv, textpropertyversion tpv
         , structureelementversion sev,  structureelementversion sev1,
         structureelementversion sev2
         where cv.classid = nCCICODEClassId and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId 
         and cpv.classid = nNarrowerClassId and tpv.domainelementid=cv.elementid and tpv.classid=nCodeClassId and tpv.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
         and cpv.rangeelementid=pElementId
         and cpv.domainelementid=cv.elementid
         and cpv.conceptpropertyid = sev.elementversionid
         and sev.structureid=nContextId
         and cv.status='ACTIVE';
  begin
       nCCICODEClassId := cims_cci.getCCIClassID('ConceptVersion', 'CCICODE');
       nNarrowerClassId := cims_cci.getCCIClassID('ConceptPropertyVersion', 'Narrower');
       nCodeClassId := cims_cci.getCCIClassID('TextPropertyVersion', 'Code');
       
            
       for cCode in cChildCodes
       loop
           processCCIValidationRule(cCode.tabularCode, cCode.elementId, cCode.Haschild, pValidationRule, nContextId, sReleaseCode);
       end loop;
  end processCCIParentCodeVR;


end CIMS_ASOT_API;
/
