create or replace package cims_retrieve_api_icd is

  -- Author  : YLU
  -- Created : 21/04/2015 4:29:08 PM
  -- Purpose : 
  
  -- Public type declarations
  
  --TYPE ref_cursor is ref cursor;
  
  --TYPE classification_type IS REF CURSOR return classification%ROWTYPE;
  --TYPE classification_version_type IS REF CURSOR return CLASSIFICATION_VERSION%ROWTYPE;
  --TYPE classification_release_type IS REF CURSOR return CLASSIFICATION_RELEASE%ROWTYPE;
  
  
  --TYPE icd10ca_tabular_type IS REF CURSOR return ICD10CA_TABULAR%ROWTYPE;
  --TYPE icd10ca_dh_validation_type IS REF CURSOR return ICD10CA_DH_VALIDATION%ROWTYPE;

  TYPE t_icd10ca_tabular_tab IS TABLE of t_icd10ca_tabular;
  TYPE t_icd10ca_dh_validation_tab IS TABLE of t_icd10ca_dh_validation;
  
  
  -- Public constant declarations

  -- Public variable declarations

  -- Public function and procedure declarations
  
  --function getClassification return classification_type;
  --function getClassificationVersion(pClassificationCode varchar2, pVersionCode varchar2) return classification_version_type;
  --function getClassificationRelease(pClassificationCode varchar2, pVersionCode varchar2) return classification_release_type;
  
  --function getClassificationReleaseCode(pClassificationCode varchar2, pVersionCode varchar2) return varchar2;
  
  --function getDataHolding(pClassificationCode varchar2, pVersionCode varchar2) return data_holding_type;

/*  
  function getICD10TabularInfo(pVersionCode varchar2, pTabularClassId number, codeOnly char) return icd10ca_tabular_type;
  function getICD10CAChapter(pVersionCode varchar2) return icd10ca_tabular_type;
  function getICD10CABlock(pVersionCode varchar2) return icd10ca_tabular_type;
  function getICD10CACategory(pVersionCode varchar2) return icd10ca_tabular_type;
  function getICD10CACode(pVersionCode varchar2) return icd10ca_tabular_type;
  function getICD10CADHValidation(pVersionCode varchar2, pTabularCode varchar2) return icd10ca_dh_validation_type;
  function getICD10CADHValidations(pVersionCode varchar2) return icd10ca_dh_validation_type;
*/

  function getICD10TabularInfoTab(pVersionCode varchar2, pTabularClassId number, codeOnly char) return t_icd10ca_tabular_tab pipelined;
  function getICD10CAChapterTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined;
  function getICD10CABlockTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined;
  function getICD10CACategoryTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined;
  function getICD10CACodeTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined;

  function getICD10CADHValidationTab(pVersionCode varchar2, pTabularCode varchar2) return t_icd10ca_dh_validation_tab pipelined;
  function getICD10CADHValidationsTab(pVersionCode varchar2) return t_icd10ca_dh_validation_tab pipelined;


end cims_retrieve_api_icd;
/
create or replace package body cims_retrieve_api_icd is

  -- Private type declarations
  
  -- Private constant declarations
  --cci_classification_code varchar2(20) := 'CCI';

  -- Private variable declarations

  -- Function and procedure implementations
/*  
  function getClassification return classification_type
  IS
      cClassification classification_type; 
      
  BEGIN
      open cClassification for 
      Select c.ClassName as classification_code, c.ClassName classification_e_desc, c.ClassName classification_F_desc 
      from Class c where c.tablename='BaseClassification' ;
      return cClassification;
  END getClassification;

  function getClassificationVersion(pClassificationCode varchar2, pVersionCode varchar2) return classification_version_type
  IS
      cClassification_version classification_version_type;
  BEGIN
      open cClassification_version for
      Select c.classname||'_'||ev.versioncode as clssification_version_code, c.classname as classification_code, c.classname||'_'||ev.versioncode as classification_version_desc, ev.versioncode as version_code
         from structureversion sv, elementversion ev, class c 
         where sv.basestructureid is null and sv.structureid=ev.elementversionid and c.classname=pClassificationCode and c.classid=sv.classid and ev.versioncode=pVersionCode;
      
      return cClassification_version;
  END getClassificationVersion;
  
  function getClassificationRelease(pClassificationCode varchar2, pVersionCode varchar2) return classification_release_type
  IS
      cRelease classification_release_type;
        
  BEGIN
      open cRelease for 
      with release as (
        select * from 
        (
          select rt.release_type_code as release_desc, pr.creation_date as release_date, pr.release_status_code, pr.fiscal_year 
          from publication_release pr, release_type rt 
          where pr.release_type_id=rt.release_type_id and pr.fiscal_year=pVersionCode and pr.release_status_code='E' 
          order by pr.creation_date desc
        )  
        where rownum=1
      )
      select cv.classification_version_code, r.release_desc, r.release_date, r.release_status_code, cv.classification_version_code from classification_version cv, release r
      where cv.classification_code=pClassificationCode and cv.version_code=pVersionCode and cv.version_code=r.fiscal_year;
      
      return cRelease;
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
*/  

/*
  function getICD10CAChapter(pVersionCode varchar2) return icd10ca_tabular_type
  IS
       nTabularClassId NUMBER(9);
  BEGIN
       nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Chapter');
       
       return getICD10TabularInfo(pVersionCode, nTabularClassId, 'N');
  END getICD10CAChapter;

  function getICD10CABlock(pVersionCode varchar2) return icd10ca_tabular_type
  IS
       nTabularClassId NUMBER(9);
  BEGIN
       nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Block');
       
       return getICD10TabularInfo(pVersionCode, nTabularClassId, 'N');
  END getICD10CABlock;

  function getICD10CACategory(pVersionCode varchar2) return icd10ca_tabular_type
  IS
       nTabularClassId NUMBER(9);
  BEGIN
       nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Category');
       
       return getICD10TabularInfo(pVersionCode, nTabularClassId, 'N');
  END getICD10CACategory;

  function getICD10CACode(pVersionCode varchar2) return icd10ca_tabular_type
  IS
       nTabularClassId NUMBER(9);
  BEGIN
       nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Category');
       
       return getICD10TabularInfo(pVersionCode, nTabularClassId, 'Y');
  END getICD10CACode;

  function getICD10TabularInfo(pVersionCode varchar2, pTabularClassId number, codeOnly char) return icd10ca_tabular_type
  IS
      nNarrowerClassID    NUMBER(9);
      nCodeClassID        NUMBER(9);
      nShortDescClassID   NUMBER(9);
      nLongDescClassID    NUMBER(9);
      sReleaseCode        varchar2(20);
      nContextId          NUMBER(9);
      
      cTabularInfo icd10ca_tabular_type;

      charY CHAR(1) := 'Y';
      charN CHAR(1) := 'N';
         
  BEGIN
          nCodeClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
          nNarrowerClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
          nShortDescClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
          nLongDescClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
          
          --sReleaseCode:=getClassificationReleaseCode(icd10ca_classification_code,pVersionCode);
          sReleaseCode:=cims_retrieve_api.getClassificationReleaseCode(icd10ca_classification_code,pVersionCode);

          if sReleaseCode is not null then

             nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
            
            open cTabularInfo for
            with elementproperties as
           ( select tp.text , cv.elementid, c.classname, tp.classid as textclassId, tp.languagecode, cv.status, 
                    --DECODE(cims_util.hasActiveChildren(nContextId, cv.elementid), 'Y', 'N', 'Y') as code_ind_code,
                    DECODE(cims_util.hasActiveChildren(nContextId, cv.elementid), 'Y', charN, charY) as code_ind_code,
             (select tpv1.text 
             from textpropertyversion tpv1, structureelementversion sev2 
             where tpv1.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
             and tpv1.classid=nCodeClassId and tpv1.domainelementid=cpv.rangeelementid) as parentcode,
             cims_util.retrieveCodeNestingLevel(icd10ca_classification_code, nContextId, cv.elementid) nesting_level
            from TextPropertyversion tp,  structureelementversion sev ,structureelementversion sev1, conceptversion cv, class c,
                 conceptpropertyversion cpv, structureelementversion sev3
            where tp.textpropertyId = sev.elementversionid and sev.structureId= nContextId  
            and sev1.structureId= nContextId and sev3.structureid=nContextId
            and cv.conceptid = sev1.elementversionid
            and cpv.conceptpropertyid=sev3.elementversionid
            and cpv.classid=nNarrowerClassID and cpv.domainelementid(+)=cv.elementid
            and tp.domainelementid = cv.elementid
            and tp.classid in (nCodeClassID,nShortDescClassID,nLongDescClassID) 
            and cv.classid = c.classid and c.classid = pTabularClassId   -- chapter and block and category (and code)
            and  cv.status = 'ACTIVE' 
            order by nesting_level, cv.classid, tp.textpropertyId
            )
            
            select REGEXP_REPLACE(blk.concept_code,'\.|\^|\\','') as icd10ca_tabular_code, sReleaseCode as classification_release_code, 
            blk.concept_short_title_eng as tabular_short_e_desc, blk.concept_short_title_fra as tabular_short_f_desc, blk.concept_long_title_eng as tabular_long_e_desc, 
            blk.concept_long_title_fra as tabular_long_f_desc, blk.classname as tabular_type_code,
            blk.code_ind_code, blk.status as tabular_status_code,  
            REGEXP_REPLACE(blk.parentcode,'\.|\^|\\','') as parent_tabular_code, e.elementuuid as tabular_uuid
            from
              (select ep.elementid, ep.classname, ep.status, ep.parentcode, ep.code_ind_code, ep.nesting_level,
                MAX(DECODE(textclassId,  nCodeClassID, text, NULL)) as concept_code,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_short_title_eng, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_long_title_eng,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_short_title_fra, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_long_title_fra
              from   elementproperties ep
              group by ep.elementid, ep.classname, ep.status, ep.parentcode, ep.code_ind_code, ep.nesting_level) blk, element e
            where blk.elementid=e.elementid
            and blk.code_ind_code = DECODE(codeOnly, 'Y', 'Y', blk.code_ind_code)
            order by blk.nesting_level, concept_code;
          
          else
              open cTabularInfo for select * from icd10ca_tabular where 1=0;
          end if;
          
       return cTabularInfo;
  END getICD10TabularInfo;
*/

  function getICD10CAChapterTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined
  IS
      nTabularClassId NUMBER(9);
      out_rec t_icd10ca_tabular; 
      cursor cICD10CAChapter is select * from table(getICD10TabularInfoTab(pVersionCode, nTabularClassId, 'N'));
  BEGIN
      nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Chapter');
       
      for cRecord in cICD10CAChapter
      Loop
          out_rec := t_icd10ca_tabular(cRecord.icd10ca_tabular_code,
                                       cRecord.version_code,
                                       cRecord.tabular_short_e_desc,
                                       cRecord.tabular_short_f_desc,
                                       cRecord.tabular_long_e_desc,
                                       cRecord.tabular_long_f_desc,
                                       cRecord.tabular_type_code,
                                       cRecord.code_ind_code,
                                       cRecord.tabular_status_code,
                                       cRecord.parent_tabular_code,
                                       cRecord.tabular_uuid,
                                       cRecord.formatted_code);
          
          pipe row(out_rec);
      end Loop;
       
  END getICD10CAChapterTab;

  function getICD10CABlockTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined
  IS
      nTabularClassId NUMBER(9);
      out_rec t_icd10ca_tabular; 
      cursor cICD10CABlock is select * from table(getICD10TabularInfoTab(pVersionCode, nTabularClassId, 'N'));
  BEGIN
      nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Block');
       
      for cRecord in cICD10CABlock
      Loop
          out_rec := t_icd10ca_tabular(cRecord.icd10ca_tabular_code,
                                       cRecord.version_code,
                                       cRecord.tabular_short_e_desc,
                                       cRecord.tabular_short_f_desc,
                                       cRecord.tabular_long_e_desc,
                                       cRecord.tabular_long_f_desc,
                                       cRecord.tabular_type_code,
                                       cRecord.code_ind_code,
                                       cRecord.tabular_status_code,
                                       cRecord.parent_tabular_code,
                                       cRecord.tabular_uuid,
                                       cRecord.formatted_code);
          
          pipe row(out_rec);
      end Loop;
       
  END getICD10CABlockTab;

  function getICD10CACategoryTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined
  IS
      nTabularClassId NUMBER(9);
      out_rec t_icd10ca_tabular; 
      cursor cICD10CACategory is select * from table(getICD10TabularInfoTab(pVersionCode, nTabularClassId, 'N'));
  BEGIN
      nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Category');
       
      for cRecord in cICD10CACategory
      Loop
          out_rec := t_icd10ca_tabular(cRecord.icd10ca_tabular_code,
                                       cRecord.version_code,
                                       cRecord.tabular_short_e_desc,
                                       cRecord.tabular_short_f_desc,
                                       cRecord.tabular_long_e_desc,
                                       cRecord.tabular_long_f_desc,
                                       cRecord.tabular_type_code,
                                       cRecord.code_ind_code,
                                       cRecord.tabular_status_code,
                                       cRecord.parent_tabular_code,
                                       cRecord.tabular_uuid,
                                       cRecord.formatted_code);
          
          pipe row(out_rec);
      end Loop;
       
  END getICD10CACategoryTab;

  function getICD10CACodeTab(pVersionCode varchar2) return t_icd10ca_tabular_tab pipelined
  IS
      nTabularClassId NUMBER(9);
      out_rec t_icd10ca_tabular; 
      cursor cICD10CACode is select * from table(getICD10TabularInfoTab(pVersionCode, nTabularClassId, 'Y'));
  BEGIN
      nTabularClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','Category');
       
      for cRecord in cICD10CACode
      Loop
          out_rec := t_icd10ca_tabular(cRecord.icd10ca_tabular_code,
                                       cRecord.version_code,
                                       cRecord.tabular_short_e_desc,
                                       cRecord.tabular_short_f_desc,
                                       cRecord.tabular_long_e_desc,
                                       cRecord.tabular_long_f_desc,
                                       cRecord.tabular_type_code,
                                       cRecord.code_ind_code,
                                       cRecord.tabular_status_code,
                                       cRecord.parent_tabular_code,
                                       cRecord.tabular_uuid,
                                       cRecord.formatted_code);
          
          pipe row(out_rec);
      end Loop;
       
  END getICD10CACodeTab;


  function getICD10TabularInfoTab(pVersionCode varchar2, pTabularClassId number, codeOnly char) return t_icd10ca_tabular_tab pipelined
  IS

      nNarrowerClassID    NUMBER(9);
      nCodeClassID        NUMBER(9);
      nShortDescClassID   NUMBER(9);
      nLongDescClassID    NUMBER(9);
      nContextId          NUMBER(9);
      
      charY CHAR(1) := 'Y';
      charN CHAR(1) := 'N';

      out_rec t_icd10ca_tabular := t_icd10ca_tabular(NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
      cursor cICD10TabularInfo is
            with elementproperties as
           ( select tp.text , cv.elementid, c.classname, tp.classid as textclassId, tp.languagecode, cv.status, 
                    --DECODE(cims_util.hasActiveChildren(nContextId, cv.elementid), 'Y', 'N', 'Y') as code_ind_code,
                    DECODE(cims_util.hasActiveChildren(nContextId, cv.elementid), 'Y', charN, charY) as code_ind_code,
             (select tpv1.text 
             from textpropertyversion tpv1, structureelementversion sev2 
             where tpv1.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
             and tpv1.classid=nCodeClassId and tpv1.domainelementid=cpv.rangeelementid) as parentcode,
             cims_util.retrieveCodeNestingLevel(cims_retrieve_api.icd10ca_classification_code, nContextId, cv.elementid) nesting_level
            from TextPropertyversion tp,  structureelementversion sev ,structureelementversion sev1, conceptversion cv, class c,
                 conceptpropertyversion cpv, structureelementversion sev3
            where tp.textpropertyId = sev.elementversionid and sev.structureId= nContextId  
            and sev1.structureId= nContextId and sev3.structureid=nContextId
            and cv.conceptid = sev1.elementversionid
            and cpv.conceptpropertyid=sev3.elementversionid
            and cpv.classid=nNarrowerClassID and cpv.domainelementid(+)=cv.elementid
            and tp.domainelementid = cv.elementid
            and tp.classid in (nCodeClassID,nShortDescClassID,nLongDescClassID) 
            and cv.classid = c.classid and c.classid = pTabularClassId   -- chapter and block and category (and code)
            and  cv.status = 'ACTIVE' 
            order by nesting_level, cv.classid, tp.textpropertyId
            )
            
            select REGEXP_REPLACE(blk.concept_code,'\.|/','') as icd10ca_tabular_code, pVersionCode as version_code, 
            blk.concept_short_title_eng as tabular_short_e_desc, blk.concept_short_title_fra as tabular_short_f_desc, blk.concept_long_title_eng as tabular_long_e_desc, 
            blk.concept_long_title_fra as tabular_long_f_desc, blk.classname as tabular_type_code,
            blk.code_ind_code, blk.status as tabular_status_code,  
            REGEXP_REPLACE(blk.parentcode,'\.|/','') as parent_tabular_code, e.elementuuid as tabular_uuid, blk.concept_code as formatted_code
            from
              (select ep.elementid, ep.classname, ep.status, ep.parentcode, ep.code_ind_code, ep.nesting_level,
                MAX(DECODE(textclassId,  nCodeClassID, text, NULL)) as concept_code,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_short_title_eng, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_long_title_eng,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_short_title_fra, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_long_title_fra
              from   elementproperties ep
              group by ep.elementid, ep.classname, ep.status, ep.parentcode, ep.code_ind_code, ep.nesting_level) blk, element e
            where blk.elementid=e.elementid
            and blk.code_ind_code = DECODE(codeOnly, 'Y', 'Y', blk.code_ind_code)
            order by blk.nesting_level, concept_code;

  BEGIN
          nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
          if nContextId > 0 then
            nCodeClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
            nNarrowerClassID := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
            nShortDescClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'ShortTitle');
            nLongDescClassID := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'LongTitle');
            
          
            
            for cRecord in cICD10TabularInfo
            Loop
                out_rec.icd10ca_tabular_code := cRecord.icd10ca_tabular_code;
                out_rec.version_code := cRecord.version_code;
                out_rec.tabular_short_e_desc := cRecord.tabular_short_e_desc;
                out_rec.tabular_short_f_desc := cRecord.tabular_short_f_desc;
                out_rec.tabular_long_e_desc := cRecord.tabular_long_e_desc;
                out_rec.tabular_long_f_desc := cRecord.tabular_long_f_desc;
                out_rec.tabular_type_code := cRecord.tabular_type_code;
                out_rec.code_ind_code := cRecord.code_ind_code;
                out_rec.tabular_status_code := cRecord.tabular_status_code;
                out_rec.parent_tabular_code := cRecord.parent_tabular_code;
                out_rec.tabular_uuid := cRecord.tabular_uuid ;
                out_rec.formatted_code := cRecord.formatted_code;

                pipe row(out_rec);
            end Loop;
          end if;
             
       return;

  END getICD10TabularInfoTab;
  
/*  
  function getICD10CADHValidation(pVersionCode varchar2, pTabularCode varchar2) return icd10ca_dh_validation_type
  IS


      nValidationCPVClassId    NUMBER(9);  -- 33
      nValidationClassId    NUMBER(9);  -- 31
      nCategoryClassId      NUMBER(9);  -- 5
      nTabularCodeClassId    NUMBER(9); -- 6
      nValidationFacilityClassId  NUMBER(9);  -- 34
      nFacilityTypeClassId    NUMBER(9);  -- 25
      nDomainValueCodeClassId  NUMBER(9);  --27
      nContextId              NUMBER(9);
      sReleaseCode        varchar2(20);


      cDHValidationInfo icd10ca_dh_validation_type;
         
  BEGIN
  

          sReleaseCode:=cims_retrieve_api.getClassificationReleaseCode(icd10ca_classification_code,pVersionCode);
          if sReleaseCode is not null then


             nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
             nValidationClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ValidationICD');
             nCategoryClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
             nFacilityTypeClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType');
             nValidationCPVClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
             nValidationFacilityClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
             nTabularCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
             nDomainValueCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
 
            open cDHValidationInfo for
            with elementproperties as
          ( SELECT 
                   cv1.elementid as elementid, -- Category
                   tpv.text as code, 
                   dhcode.text as dhcode, 
           cims_util.hasActiveChildren(nContextId, cv1.elementid) as hasChild,
           validationrule.xmltext as xmltext
         from structureelementversion sev,
                 structureelementversion sev1,
              structureelementversion sev2,
                 structureelementversion sev3, 
                 structureelementversion sev4, 
                 structureelementversion sev5, 
                 structureelementversion sev6,
                 structureelementversion sev7,
              conceptversion cv, 
              conceptversion cv1, 
                 conceptversion dh, 
              conceptpropertyversion cpv, 
                 conceptpropertyversion dhcpv, 
                 textpropertyversion tpv,
                 textpropertyversion dhcode,
                 xmlpropertyversion validationrule
            where sev.structureid=nContextId
              and sev1.structureid=nContextId
              and sev2.structureid=nContextId
              and sev3.structureid=nContextId
              and sev4.structureid=nContextId
              and sev5.structureid=nContextId
              and sev6.structureid=nContextId
              and sev7.structureid=nContextId
              and cv.classid=nValidationClassId
              and cv1.classid = nCategoryClassId
              and dh.classid=nFacilityTypeClassId
              and cpv.classid = nValidationCPVClassId
              and dhcpv.classid= nValidationFacilityClassId 
              and tpv.classid= nTabularCodeClassId
              and dhcode.classid= nDomainValueCodeClassId
              and cpv.conceptpropertyid = sev.elementversionid
              and cv.conceptid = sev1.elementversionid
              and cv1.conceptid= sev2.elementversionid
              and dhcpv.conceptpropertyid=sev3.elementversionid
              and dh.conceptid=sev4.elementversionid
              and dhcode.textpropertyid=sev5.elementversionid
              and validationrule.xmlpropertyid=sev6.elementversionid
              and tpv.textpropertyid=sev7.elementversionid
              and cpv.rangeelementid = cv1.elementid 
              and cpv.domainelementid = cv.elementid
              and tpv.domainelementid=cv1.elementid
              and validationrule.domainelementid=cv.elementid
              and dhcpv.rangeelementid=dh.elementid
              and dhcpv.domainelementid=cv.elementid
              and dhcode.domainelementid=dh.elementid
              and cv1.status='ACTIVE' 
              and cv.status='ACTIVE'
              --order by dhcode, tpv.text
          )
          select REGEXP_REPLACE(ep.code,'\.|\^|\\','') as icd10ca_tabular_code,
                 dhcode as data_holding_code,
                 sReleaseCode as classification_release_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/GENDER_CODE') as gender_validation_code,
                 cims_retrieve_api.getMinAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_min,
                 cims_retrieve_api.getMaxAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_max,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/NEW_BORN') as newborn_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/MRDX_MAIN') as main_diagnostic_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_1') as diagnostic_type_1_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_2') as diagnostic_type_2_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_3') as diagnostic_type_3_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_4') as diagnostic_type_4_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_6') as diagnostic_type_6_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_9') as diagnostic_type_9_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_W') as diagnostic_type_W_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_X') as diagnostic_type_X_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_Y') as diagnostic_type_Y_ind_code
          from elementproperties ep
          where REGEXP_REPLACE(ep.code,'\.|\^|\\','') = pTabularCode
          order by data_holding_code, icd10ca_tabular_code;

          else
              open cDHValidationInfo for select * from icd10ca_dh_validation where 1=0;
          end if;
             
       return cDHValidationInfo;

  END getICD10CADHValidation;

  function getICD10CADHValidations(pVersionCode varchar2) return icd10ca_dh_validation_type
  IS


      nValidationCPVClassId    NUMBER(9);  -- 33
      nValidationClassId    NUMBER(9);  -- 31
      nCategoryClassId      NUMBER(9);  -- 5
      nTabularCodeClassId    NUMBER(9); -- 6
      nValidationFacilityClassId  NUMBER(9);  -- 34
      nFacilityTypeClassId    NUMBER(9);  -- 25
      nDomainValueCodeClassId  NUMBER(9);  --27
      nContextId              NUMBER(9);
      sReleaseCode        varchar2(20);


      cDHValidationInfo icd10ca_dh_validation_type;
         
  BEGIN
  

          sReleaseCode:=cims_retrieve_api.getClassificationReleaseCode(icd10ca_classification_code,pVersionCode);
          if sReleaseCode is not null then


             nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
             nValidationClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ValidationICD');
             nCategoryClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
             nFacilityTypeClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType');
             nValidationCPVClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
             nValidationFacilityClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
             nTabularCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
             nDomainValueCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
 
            open cDHValidationInfo for
            with elementproperties as
          ( SELECT 
                   cv1.elementid as elementid, -- Category
                   tpv.text as code, 
                   dhcode.text as dhcode, 
           cims_util.hasActiveChildren(nContextId, cv1.elementid) as hasChild,
           validationrule.xmltext as xmltext
         from structureelementversion sev,
                 structureelementversion sev1,
              structureelementversion sev2,
                 structureelementversion sev3, 
                 structureelementversion sev4, 
                 structureelementversion sev5, 
                 structureelementversion sev6,
                 structureelementversion sev7,
              conceptversion cv, 
              conceptversion cv1, 
                 conceptversion dh, 
              conceptpropertyversion cpv, 
                 conceptpropertyversion dhcpv, 
                 textpropertyversion tpv,
                 textpropertyversion dhcode,
                 xmlpropertyversion validationrule
            where sev.structureid=nContextId
              and sev1.structureid=nContextId
              and sev2.structureid=nContextId
              and sev3.structureid=nContextId
              and sev4.structureid=nContextId
              and sev5.structureid=nContextId
              and sev6.structureid=nContextId
              and sev7.structureid=nContextId
              and cv.classid=nValidationClassId
              and cv1.classid = nCategoryClassId
              and dh.classid=nFacilityTypeClassId
              and cpv.classid = nValidationCPVClassId
              and dhcpv.classid= nValidationFacilityClassId 
              and tpv.classid= nTabularCodeClassId
              and dhcode.classid= nDomainValueCodeClassId
              and cpv.conceptpropertyid = sev.elementversionid
              and cv.conceptid = sev1.elementversionid
              and cv1.conceptid= sev2.elementversionid
              and dhcpv.conceptpropertyid=sev3.elementversionid
              and dh.conceptid=sev4.elementversionid
              and dhcode.textpropertyid=sev5.elementversionid
              and validationrule.xmlpropertyid=sev6.elementversionid
              and tpv.textpropertyid=sev7.elementversionid
              and cpv.rangeelementid = cv1.elementid 
              and cpv.domainelementid = cv.elementid
              and tpv.domainelementid=cv1.elementid
              and validationrule.domainelementid=cv.elementid
              and dhcpv.rangeelementid=dh.elementid
              and dhcpv.domainelementid=cv.elementid
              and dhcode.domainelementid=dh.elementid
              and cv1.status='ACTIVE' 
              and cv.status='ACTIVE'
              --order by dhcode, tpv.text
          )
          select REGEXP_REPLACE(ep.code,'\.|\^|\\','') as icd10ca_tabular_code,
                 dhcode as data_holding_code,
                 sReleaseCode as classification_release_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/GENDER_CODE') as gender_validation_code,
                 cims_retrieve_api.getMinAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_min,
                 cims_retrieve_api.getMaxAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_max,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/NEW_BORN') as newborn_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/MRDX_MAIN') as main_diagnostic_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_1') as diagnostic_type_1_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_2') as diagnostic_type_2_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_3') as diagnostic_type_3_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_4') as diagnostic_type_4_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_6') as diagnostic_type_6_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_9') as diagnostic_type_9_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_W') as diagnostic_type_W_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_X') as diagnostic_type_X_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_Y') as diagnostic_type_Y_ind_code
          from elementproperties ep
          order by data_holding_code, icd10ca_tabular_code;

          else
              open cDHValidationInfo for select * from icd10ca_dh_validation where 1=0;
          end if;
             
       return cDHValidationInfo;

  END getICD10CADHValidations;
*/

  function getICD10CADHValidationTab(pVersionCode varchar2, pTabularCode varchar2) return t_icd10ca_dh_validation_tab pipelined
  IS

      nValidationCPVClassId    NUMBER(9);  -- 33
      nValidationClassId    NUMBER(9);  -- 31
      nCategoryClassId      NUMBER(9);  -- 5
      nTabularCodeClassId    NUMBER(9); -- 6
      nValidationFacilityClassId  NUMBER(9);  -- 34
      nFacilityTypeClassId    NUMBER(9);  -- 25
      nDomainValueCodeClassId  NUMBER(9);  --27
      nContextId              NUMBER(9);

      out_rec t_icd10ca_dh_validation := t_icd10ca_dh_validation(NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

      cursor cDHValidationInfo is
            with elementproperties as
          ( SELECT 
                   cv1.elementid as elementid, -- Category
                   tpv.text as code, 
                   dhcode.text as dhcode, 
           cims_util.hasActiveChildren(nContextId, cv1.elementid) as hasChild,
           validationrule.xmltext as xmltext
         from structureelementversion sev,
                 structureelementversion sev1,
              structureelementversion sev2,
                 structureelementversion sev3, 
                 structureelementversion sev4, 
                 structureelementversion sev5, 
                 structureelementversion sev6,
                 structureelementversion sev7,
              conceptversion cv, 
              conceptversion cv1, 
                 conceptversion dh, 
              conceptpropertyversion cpv, 
                 conceptpropertyversion dhcpv, 
                 textpropertyversion tpv,
                 textpropertyversion dhcode,
                 xmlpropertyversion validationrule
            where sev.structureid=nContextId
              and sev1.structureid=nContextId
              and sev2.structureid=nContextId
              and sev3.structureid=nContextId
              and sev4.structureid=nContextId
              and sev5.structureid=nContextId
              and sev6.structureid=nContextId
              and sev7.structureid=nContextId
              and cv.classid=nValidationClassId
              and cv1.classid = nCategoryClassId
              and dh.classid=nFacilityTypeClassId
              and cpv.classid = nValidationCPVClassId
              and dhcpv.classid= nValidationFacilityClassId 
              and tpv.classid= nTabularCodeClassId
              and dhcode.classid= nDomainValueCodeClassId
              and cpv.conceptpropertyid = sev.elementversionid
              and cv.conceptid = sev1.elementversionid
              and cv1.conceptid= sev2.elementversionid
              and dhcpv.conceptpropertyid=sev3.elementversionid
              and dh.conceptid=sev4.elementversionid
              and dhcode.textpropertyid=sev5.elementversionid
              and validationrule.xmlpropertyid=sev6.elementversionid
              and tpv.textpropertyid=sev7.elementversionid
              and cpv.rangeelementid = cv1.elementid 
              and cpv.domainelementid = cv.elementid
              and tpv.domainelementid=cv1.elementid
              and validationrule.domainelementid=cv.elementid
              and dhcpv.rangeelementid=dh.elementid
              and dhcpv.domainelementid=cv.elementid
              and dhcode.domainelementid=dh.elementid
              and cv1.status='ACTIVE' 
              and cv.status='ACTIVE'
              --order by dhcode, tpv.text
          )
          select elementid, hasChild,
                 REGEXP_REPLACE(ep.code,'\.|\^|\\','') as icd10ca_tabular_code,
                 dhcode as data_holding_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/GENDER_CODE') as gender_validation_code,
                 cims_retrieve_api.getMinAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_min,
                 cims_retrieve_api.getMaxAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_max,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/NEW_BORN') as newborn_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/MRDX_MAIN') as main_diagnostic_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_1') as diagnostic_type_1_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_2') as diagnostic_type_2_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_3') as diagnostic_type_3_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_4') as diagnostic_type_4_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_6') as diagnostic_type_6_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_9') as diagnostic_type_9_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_W') as diagnostic_type_W_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_X') as diagnostic_type_X_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_Y') as diagnostic_type_Y_ind_code
          from elementproperties ep
          where REGEXP_REPLACE(ep.code,'\.|\^|\\','') = pTabularCode
          order by data_holding_code, icd10ca_tabular_code;
         
  BEGIN
          nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
          if nContextId > 0 then
            nValidationClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ValidationICD');
            nCategoryClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
            nFacilityTypeClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType');
            nValidationCPVClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
            nValidationFacilityClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
            nTabularCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
            nDomainValueCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
    
          
            for cRecord in cDHValidationInfo
            Loop
                out_rec.element_id := cRecord.elementid;
                out_rec.has_child := trim(cRecord.hasChild);
                out_rec.icd10ca_tabular_code := cRecord.icd10ca_tabular_code;
                out_rec.data_holding_code := cRecord.data_holding_code;
                out_rec.version_code := pVersionCode;
                out_rec.gender_validation_code := cRecord.gender_validation_code;
                out_rec.age_min := cRecord.age_min;
                out_rec.age_max := cRecord.age_max;
                out_rec.newborn_ind_code := cRecord.newborn_ind_code;
                out_rec.main_diagnostic_ind_code := cRecord.main_diagnostic_ind_code;
                out_rec.diagnostic_type_1_ind_code := cRecord.diagnostic_type_1_ind_code;
                out_rec.diagnostic_type_2_ind_code := cRecord.diagnostic_type_2_ind_code;
                out_rec.diagnostic_type_3_ind_code := cRecord.diagnostic_type_3_ind_code;
                out_rec.diagnostic_type_4_ind_code := cRecord.diagnostic_type_4_ind_code;
                out_rec.diagnostic_type_6_ind_code := cRecord.diagnostic_type_6_ind_code;
                out_rec.diagnostic_type_9_ind_code := cRecord.diagnostic_type_9_ind_code;
                out_rec.diagnostic_type_W_ind_code := cRecord.diagnostic_type_W_ind_code;
                out_rec.diagnostic_type_X_ind_code := cRecord.diagnostic_type_X_ind_code;
                out_rec.diagnostic_type_Y_ind_code := cRecord.diagnostic_type_Y_ind_code;

                pipe row(out_rec);
            end Loop;
          end if;
             
       return;

  END getICD10CADHValidationTab;

  function getICD10CADHValidationsTab(pVersionCode varchar2) return t_icd10ca_dh_validation_tab pipelined
  IS

      nValidationCPVClassId    NUMBER(9);  -- 33
      nValidationClassId    NUMBER(9);  -- 31
      nCategoryClassId      NUMBER(9);  -- 5
      nTabularCodeClassId    NUMBER(9); -- 6
      nValidationFacilityClassId  NUMBER(9);  -- 34
      nFacilityTypeClassId    NUMBER(9);  -- 25
      nDomainValueCodeClassId  NUMBER(9);  --27
      nContextId              NUMBER(9);

      out_rec t_icd10ca_dh_validation := t_icd10ca_dh_validation(NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

      cursor cDHValidationInfo is
            with elementproperties as
          ( SELECT 
                   cv1.elementid as elementid, -- Category
                   tpv.text as code, 
                   dhcode.text as dhcode, 
           cims_util.hasActiveChildren(nContextId, cv1.elementid) as hasChild,
           validationrule.xmltext as xmltext
         from structureelementversion sev,
                 structureelementversion sev1,
              structureelementversion sev2,
                 structureelementversion sev3, 
                 structureelementversion sev4, 
                 structureelementversion sev5, 
                 structureelementversion sev6,
                 structureelementversion sev7,
              conceptversion cv, 
              conceptversion cv1, 
                 conceptversion dh, 
              conceptpropertyversion cpv, 
                 conceptpropertyversion dhcpv, 
                 textpropertyversion tpv,
                 textpropertyversion dhcode,
                 xmlpropertyversion validationrule
            where sev.structureid=nContextId
              and sev1.structureid=nContextId
              and sev2.structureid=nContextId
              and sev3.structureid=nContextId
              and sev4.structureid=nContextId
              and sev5.structureid=nContextId
              and sev6.structureid=nContextId
              and sev7.structureid=nContextId
              and cv.classid=nValidationClassId
              and cv1.classid = nCategoryClassId
              and dh.classid=nFacilityTypeClassId
              and cpv.classid = nValidationCPVClassId
              and dhcpv.classid= nValidationFacilityClassId 
              and tpv.classid= nTabularCodeClassId
              and dhcode.classid= nDomainValueCodeClassId
              and cpv.conceptpropertyid = sev.elementversionid
              and cv.conceptid = sev1.elementversionid
              and cv1.conceptid= sev2.elementversionid
              and dhcpv.conceptpropertyid=sev3.elementversionid
              and dh.conceptid=sev4.elementversionid
              and dhcode.textpropertyid=sev5.elementversionid
              and validationrule.xmlpropertyid=sev6.elementversionid
              and tpv.textpropertyid=sev7.elementversionid
              and cpv.rangeelementid = cv1.elementid 
              and cpv.domainelementid = cv.elementid
              and tpv.domainelementid=cv1.elementid
              and validationrule.domainelementid=cv.elementid
              and dhcpv.rangeelementid=dh.elementid
              and dhcpv.domainelementid=cv.elementid
              and dhcode.domainelementid=dh.elementid
              and cv1.status='ACTIVE' 
              and cv.status='ACTIVE'
              --order by dhcode, tpv.text
          )
          select elementid, hasChild,
                 REGEXP_REPLACE(ep.code,'\.|/','') as icd10ca_tabular_code,
                 dhcode as data_holding_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/GENDER_CODE') as gender_validation_code,
                 cims_retrieve_api.getMinAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_min,
                 cims_retrieve_api.getMaxAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_max,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/NEW_BORN') as newborn_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/MRDX_MAIN') as main_diagnostic_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_1') as diagnostic_type_1_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_2') as diagnostic_type_2_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_3') as diagnostic_type_3_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_4') as diagnostic_type_4_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_6') as diagnostic_type_6_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_9') as diagnostic_type_9_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_W') as diagnostic_type_W_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_X') as diagnostic_type_X_ind_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/DX_TYPE_Y') as diagnostic_type_Y_ind_code
          from elementproperties ep
          order by data_holding_code, icd10ca_tabular_code;
         
  BEGIN
         nContextId := cims_icd.getICD10CAStructureIDByYear(pVersionCode);
          if nContextId > 0 then
            nValidationClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ValidationICD');
            nCategoryClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');
            nFacilityTypeClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'FacilityType');
            nValidationCPVClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');
            nValidationFacilityClassId := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationFacility');
            nTabularCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
            nDomainValueCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'DomainValueCode');
    
          
            for cRecord in cDHValidationInfo
            Loop
                out_rec.element_id := cRecord.elementid;
                out_rec.has_child := trim(cRecord.hasChild);
                out_rec.icd10ca_tabular_code := cRecord.icd10ca_tabular_code;
                out_rec.data_holding_code := cRecord.data_holding_code;
                out_rec.version_code := pVersionCode;
                out_rec.gender_validation_code := cRecord.gender_validation_code;
                out_rec.age_min := cRecord.age_min;
                out_rec.age_max := cRecord.age_max;
                out_rec.newborn_ind_code := cRecord.newborn_ind_code;
                out_rec.main_diagnostic_ind_code := cRecord.main_diagnostic_ind_code;
                out_rec.diagnostic_type_1_ind_code := cRecord.diagnostic_type_1_ind_code;
                out_rec.diagnostic_type_2_ind_code := cRecord.diagnostic_type_2_ind_code;
                out_rec.diagnostic_type_3_ind_code := cRecord.diagnostic_type_3_ind_code;
                out_rec.diagnostic_type_4_ind_code := cRecord.diagnostic_type_4_ind_code;
                out_rec.diagnostic_type_6_ind_code := cRecord.diagnostic_type_6_ind_code;
                out_rec.diagnostic_type_9_ind_code := cRecord.diagnostic_type_9_ind_code;
                out_rec.diagnostic_type_W_ind_code := cRecord.diagnostic_type_W_ind_code;
                out_rec.diagnostic_type_X_ind_code := cRecord.diagnostic_type_X_ind_code;
                out_rec.diagnostic_type_Y_ind_code := cRecord.diagnostic_type_Y_ind_code;

                pipe row(out_rec);
            end Loop;
          end if;
             
       return;

  END getICD10CADHValidationsTab;
  
end cims_retrieve_api_icd;
/
