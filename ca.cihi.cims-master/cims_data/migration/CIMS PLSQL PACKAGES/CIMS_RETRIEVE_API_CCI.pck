create or replace package CIMS_RETRIEVE_API_CCI is

  -- Author  : TYANG
  -- Created : 01/05/2015 1:53:25 PM
  -- Purpose : Retrieve CCI data from cims
  
  -- Public type declarations
  
  -- Public constant declarations

  -- Public variable declarations

  -- Public function and procedure declarations
  function getCCITabularInfo(pVersionCode varchar2) return t_cci_tabular_tab pipelined;
  function getCCITabularInfo(pVersionCode varchar2, pClassName varchar2) return t_cci_tabular_tab pipelined;
  
  function getCCIComponentDesc(pVersionCode varchar2, pSectionCode varchar2, sComponentClass varchar2, sCompToSectionClass varchar2) return t_cci_comp_desc_tab pipelined;
  
  function getCCICompGroup(pVersionCode varchar2, pSectionCode varchar2) return t_cci_group_tab pipelined;
  function getCCIInterv(pVersionCode varchar2, pSectionCode varchar2) return t_cci_interv_tab pipelined;
  function getCCITissue(pVersionCode varchar2, pSectionCode varchar2) return t_cci_tissue_tab pipelined;
  function getCCIDeviceAgent(pVersionCode varchar2, pSectionCode varchar2) return t_cci_device_agent_tab pipelined;
  function getCCIApproachTechnique(pVersionCode varchar2, pSectionCode varchar2) return t_cci_app_tech_tab pipelined;
  
  function getCCIComponent(pVersionCode varchar2) return t_cci_component_tab pipelined;
  
  function getCCIRefenceValues(pVersionCode varchar2, pAttributeType varchar2) return t_cci_ref_value_tab pipelined; 
  
  function getCCILocationRefValues(pVersionCode varchar2) return t_cci_loc_ref_value_tab pipelined;
  function getCCIModeOfDelRefValues(pVersionCode varchar2) return t_cci_mode_ref_value_tab pipelined;
  function getCCIExtentRefValues(pVersionCode varchar2) return t_cci_ext_ref_value_tab pipelined;
  function getCCIStatusRefValues(pVersionCode varchar2) return t_cci_sta_ref_value_tab pipelined;
  
  function getCCIGenericAttributes(pVersionCode varchar2, pRefValue varchar2) return t_cci_generic_attr_tab pipelined;
  
  function getCCIValidationRule(pVersionCode varchar2) return t_cci_validation_rule_tab pipelined;

end CIMS_RETRIEVE_API_CCI;
/
create or replace package body CIMS_RETRIEVE_API_CCI is

  -- Private type declarations
  
  -- Private constant declarations

  -- Private variable declarations

  -- Function and procedure implementations
  function getCCITabularInfo(pVersionCode varchar2) return t_cci_tabular_tab pipelined
  IS
      nNarrowerClassID    NUMBER(9);
      nCodeClassID        NUMBER(9);
      nShortDescClassID   NUMBER(9);
      nLongDescClassID    NUMBER(9);
      nSectionClassId     NUMBER(9);
      nBlockClassId     NUMBER(9);
      nGroupClassId     NUMBER(9);
      nRubricClassId     NUMBER(9);
      nCCICODEClassId     NUMBER(9);
      nContextId          NUMBER(9);
      
      cursor cTabularInfo is 
        with elementproperties as
        ( select tp.text , cv.elementid, c.classname, tp.classid as textclassId, tp.languagecode, cv.status,
            cims_util.retrieveCodeNestingLevel('CCI',nContextId, cv.elementid) as nestingLevel,
           (select text 
           from textpropertyversion tpv1, structureelementversion sev2 
           where tpv1.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
           and tpv1.classid=nCodeClassId and tpv1.domainelementid=cpv.rangeelementid) as parentcode
          from TextPropertyversion tp,  structureelementversion sev ,structureelementversion sev1, conceptversion cv, class c,
               conceptpropertyversion cpv, structureelementversion sev3
          where tp.textpropertyId = sev.elementversionid and sev.structureId= nContextId  
          and sev1.structureId= nContextId and sev3.structureid=nContextId
          and cv.conceptid = sev1.elementversionid
          and cpv.conceptpropertyid=sev3.elementversionid
          and cpv.classid=nNarrowerClassID and cpv.domainelementid(+)=cv.elementid -- 70
          and tp.domainelementid = cv.elementid
          and tp.classid in (nCodeClassID,nShortDescClassID,nLongDescClassID) --66,67,68
          and cv.classid = c.classid and c.classid in (nSectionClassId, nBlockClassId, nGroupClassId, nRubricClassId, nCCICODEClassId) --61.62.63.64.65   
          and  cv.status = 'ACTIVE' 
          order by cv.classid, tp.textpropertyId
          )
           select REGEXP_REPLACE(blk.concept_code,decode(blk.classname, 'Block', '\.|\^|\\','[^a-zA-Z0-9]'),'') as cci_tabular_code, 
            blk.concept_short_title_eng as tabular_short_e_desc, blk.concept_short_title_fra as tabular_short_f_desc, blk.concept_long_title_eng as tabular_long_e_desc, 
            blk.concept_long_title_fra as tabular_long_f_desc, blk.status as tabular_status_code, blk.classname as tabular_type_code, 
            REGEXP_REPLACE(blk.parentcode,'\.|\^|\\','') as parent_tabular_code, e.elementuuid as tabular_uuid, blk.concept_code as formatted_code
            from
              (select ep.elementid, ep.classname, ep.status, ep.parentcode, ep.nestingLevel,
                MAX(DECODE(textclassId,  nCodeClassID, text, NULL)) as concept_code,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_short_title_eng, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_long_title_eng,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_short_title_fra, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_long_title_fra
              from elementproperties ep
              group by ep.elementid, ep.classname, ep.status, ep.parentcode, ep.nestingLevel) blk, element e
            where blk.elementid=e.elementid
            order by e.classid, blk.nestingLevel;
         
  BEGIN
          nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
          if nContextID>0 then
            nSectionClassId := Cims_Cci.getCCIClassID('ConceptVersion','Section');
            nBlockClassId := Cims_Cci.getCCIClassID('ConceptVersion','Block');
            nGroupClassId := Cims_Cci.getCCIClassID('ConceptVersion','Group');
            nRubricClassId := Cims_Cci.getCCIClassID('ConceptVersion','Rubric');
            nCCICodeClassId := Cims_Cci.getCCIClassID('ConceptVersion','CCICODE');
            nCodeClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
            nNarrowerClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            nShortDescClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ShortTitle');
            nLongDescClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'LongTitle');
            
            for cRecord in cTabularInfo 
            loop
                pipe row(t_cci_tabular(cRecord.cci_tabular_code, pVersionCode, cRecord.tabular_short_e_desc,
                      cRecord.tabular_short_f_desc, cRecord.tabular_long_e_desc, cRecord.tabular_long_f_desc,
                      cRecord.tabular_status_code, cRecord.tabular_type_code, cRecord.parent_tabular_code,
                      cRecord.tabular_uuid,cRecord.formatted_code));
            end loop;
          end if;
          return;
  END getCCITabularInfo;
  
  function getCCITabularInfo(pVersionCode varchar2, pClassName varchar2) return t_cci_tabular_tab pipelined
  IS
      nNarrowerClassID    NUMBER(9);
      nCodeClassID        NUMBER(9);
      nShortDescClassID   NUMBER(9);
      nLongDescClassID    NUMBER(9);
      nTabularClassId     NUMBER(9);
      nContextId          NUMBER(9);
      
      cursor cTabularInfo is 
        with elementproperties as
        ( select tp.text , cv.elementid, c.classname, tp.classid as textclassId, tp.languagecode, cv.status,
            cims_util.retrieveCodeNestingLevel('CCI',nContextId, cv.elementid) as nestingLevel,
           (select text 
           from textpropertyversion tpv1, structureelementversion sev2 
           where tpv1.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
           and tpv1.classid=nCodeClassId and tpv1.domainelementid=cpv.rangeelementid) as parentcode
          from TextPropertyversion tp,  structureelementversion sev ,structureelementversion sev1, conceptversion cv, class c,
               conceptpropertyversion cpv, structureelementversion sev3
          where tp.textpropertyId = sev.elementversionid and sev.structureId= nContextId  
          and sev1.structureId= nContextId and sev3.structureid=nContextId
          and cv.conceptid = sev1.elementversionid
          and cpv.conceptpropertyid=sev3.elementversionid
          and cpv.classid=nNarrowerClassID and cpv.domainelementid(+)=cv.elementid -- 70
          and tp.domainelementid = cv.elementid
          and tp.classid in (nCodeClassID,nShortDescClassID,nLongDescClassID) --66,67,68
          and cv.classid = c.classid and c.classid = nTabularClassId   
          and  cv.status = 'ACTIVE' 
          order by cv.classid, tp.textpropertyId
          )
           select REGEXP_REPLACE(blk.concept_code,decode(blk.classname, 'Block', '\.|\^|\\','[^a-zA-Z0-9]'),'') as cci_tabular_code, 
            blk.concept_short_title_eng as tabular_short_e_desc, blk.concept_short_title_fra as tabular_short_f_desc, blk.concept_long_title_eng as tabular_long_e_desc, 
            blk.concept_long_title_fra as tabular_long_f_desc, blk.status as tabular_status_code, blk.classname as tabular_type_code, 
            REGEXP_REPLACE(blk.parentcode,'\.|\^|\\','') as parent_tabular_code, e.elementuuid as tabular_uuid, blk.concept_code as formatted_code
            from
              (select ep.elementid, ep.classname, ep.status, ep.parentcode, ep.nestingLevel,
                MAX(DECODE(textclassId,  nCodeClassID, text, NULL)) as concept_code,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_short_title_eng, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'ENG', text, NULL), NULL)) as concept_long_title_eng,
                MAX(DECODE(textclassId,  nShortDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_short_title_fra, 
                MAX(DECODE(textclassId,  nLongDescClassID, DECODE(languageCode, 'FRA', text, NULL), NULL)) as concept_long_title_fra
              from elementproperties ep
              group by ep.elementid, ep.classname, ep.status, ep.parentcode, ep.nestingLevel) blk, element e
            where blk.elementid=e.elementid
            order by blk.nestingLevel;
         
  BEGIN
          nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
          if nContextID>0 then
            nTabularClassId := Cims_Cci.getCCIClassID('ConceptVersion',pClassName);
            nCodeClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
            nNarrowerClassID := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
            nShortDescClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ShortTitle');
            nLongDescClassID := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'LongTitle');
            
            for cRecord in cTabularInfo 
            loop
                pipe row(t_cci_tabular(cRecord.cci_tabular_code, pVersionCode, cRecord.tabular_short_e_desc,
                      cRecord.tabular_short_f_desc, cRecord.tabular_long_e_desc, cRecord.tabular_long_f_desc,
                      cRecord.tabular_status_code, cRecord.tabular_type_code, cRecord.parent_tabular_code,
                      cRecord.tabular_uuid, cRecord.formatted_code));
            end loop;
          end if;
          return;
  END getCCITabularInfo;
  
  function getCCIComponentDesc(pVersionCode varchar2, pSectionCode varchar2, sComponentClass varchar2, sCompToSectionClass varchar2) return t_cci_comp_desc_tab pipelined
  IS
       nSectionElementId   NUMBER(9);
       nCodeClassId        NUMBER(9);
        nContextId            NUMBER(9);
        nSectionClassId       NUMBER(9);
        nComponentClassId     NUMBER(9);
        nCompToSectionClassId NUMBER(9);
       nCompCodeClassId     NUMBER(9);
       nCompShortDescClassId    NUMBER(9);
       nCompLongDescClassId     NUMBER(9);
       nCompDefDescClassId      NUMBER(9);
       nAgentTypeDescClassId    NUMBER(9);
       nAgentExampleClassId     NUMBER(9);
       cursor cComponentDesc is
       with elementproperties as

       ( 
           select tp.text as text , cv.elementid, c.classname, tp.classid as textclassId, tp.languagecode, cv.status
            from TextPropertyversion tp,  structureelementversion sev ,structureelementversion sev1, conceptversion cv, class c,
            conceptpropertyversion cpv, structureelementversion sev2
            where tp.textpropertyId = sev.elementversionid and sev.structureId= nContextId 
            and sev1.structureId= nContextId 
            and cv.conceptid = sev1.elementversionid
            and sev2.structureid=nContextId and cpv.conceptpropertyid=sev2.elementversionid
            and cpv.classid=nCompToSectionClassId and cpv.domainelementid=cv.elementid and cpv.rangeelementid=nSectionElementId
            and tp.domainelementid = cv.elementid
            and tp.classid in (nCompCodeClassId,nCompShortDescClassId,nCompLongDescClassId,nAgentTypeDescClassId,nAgentExampleClassId) 
            and cv.classid = c.classid and c.classid = nComponentClassId   -- section and block and group
            and  cv.status = 'ACTIVE' 
        ), comps as 
         (select ep.elementid ,
              MAX(DECODE(textclassId,  nCompCodeClassId, text, NULL)) as concept_code,
              MAX(DECODE(textclassId,  nCompShortDescClassId, DECODE(languageCode, 'ENG', text, NULL), NULL)) as short_e_desc, 
              MAX(DECODE(textclassId,  nCompLongDescClassId, DECODE(languageCode, 'ENG', text, NULL), NULL)) as long_e_desc,
              MAX(DECODE(textclassId,  nCompShortDescClassId, DECODE(languageCode, 'FRA', text, NULL), NULL)) as short_f_desc, 
              MAX(DECODE(textclassId,  nCompLongDescClassId, DECODE(languageCode, 'FRA', text, NULL), NULL)) as long_f_desc,
              MAX(DECODE(textclassId,  nAgentTypeDescClassId, DECODE(languageCode, 'ENG', text, NULL), NULL)) as agent_type_e_desc, 
              MAX(DECODE(textclassId,  nAgentExampleClassId, DECODE(languageCode, 'ENG', text, NULL), NULL)) as agent_e_example,
              MAX(DECODE(textclassId,  nAgentTypeDescClassId, DECODE(languageCode, 'FRA', text, NULL), NULL)) as agent_type_f_desc, 
              MAX(DECODE(textclassId,  nAgentExampleClassId, DECODE(languageCode, 'FRA', text, NULL), NULL)) as agent_f_example,
              (
                select xpv.xmltext
                from XMLPROPERTYVERSION xpv, structureelementversion sev 
                where xpv.domainelementid = ep.elementid and xpv.status='ACTIVE'
                and xpv.xmlpropertyid = sev.elementversionid and sev.structureid=nContextId
                and xpv.classid = nCompDefDescClassId
                and xpv.languagecode = 'ENG'
              ) as def_e_desc,
              (
                select xpv.xmltext
                from XMLPROPERTYVERSION xpv, structureelementversion sev 
                where xpv.domainelementid = ep.elementid and xpv.status='ACTIVE'
                and xpv.xmlpropertyid = sev.elementversionid and sev.structureid=nContextId
                and xpv.classid = nCompDefDescClassId
                and xpv.languagecode = 'FRA'
              ) as def_f_desc
            from   elementproperties ep
            group by ep.elementid order by concept_code
            ) 
         Select * from comps c where c.concept_code<>'XX';
  BEGIN
       
       nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
       if nContextID>0 then
          nCodeClassId := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
          nSectionClassId := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
          nComponentClassId := CIMS_CCI.getCCIClassID('ConceptVersion', sComponentClass);
          nCompToSectionClassId := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', sCompToSectionClass);
          
          select tpv.domainElementId into nSectionElementId
          from textpropertyversion tpv, structureelementversion sev, conceptversion cv, structureelementversion sev1
          where tpv.textpropertyid=sev.elementversionid and tpv.classid=nCodeClassId and sev.structureid=nContextId
          and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId and cv.classid=nSectionClassId
          and tpv.domainelementid=cv.elementid and tpv.text=pSectionCode and (tpv.languagecode='ENG' or tpv.languagecode is null);
         
         nCompCodeClassId    := Cims_Cci.getCCIClassID('TextPropertyVersion', 'ComponentCode');
         nCompShortDescClassId   := Cims_Cci.getCCIClassID('TextPropertyVersion', 'ComponentShortTitle');
         nCompLongDescClassId     := Cims_Cci.getCCIClassID('TextPropertyVersion', 'ComponentLongTitle');
         nCompDefDescClassId      := Cims_Cci.getCCIClassID('XmlPropertyVersion', 'ComponentDefinitionTitle');
         nAgentTypeDescClassId    := Cims_Cci.getCCIClassID('TextPropertyVersion', 'AgentTypeDescription');
         nAgentExampleClassId     := Cims_Cci.getCCIClassID('TextPropertyVersion', 'AgentExample');
         for cRecord in cComponentDesc
         loop
             pipe row(t_cci_component_desc(cRecord.elementid, cRecord.concept_code, cRecord.short_e_desc, cRecord.short_f_desc,
                 cRecord.long_e_desc, crecord.long_f_desc, cRecord.def_e_desc, cRecord.def_f_desc, crecord.agent_type_e_desc,
                 cRecord.agent_type_f_desc, cRecord.agent_e_example, crecord.agent_f_example));        
         end loop;
       end if;
       return ;
  END ;
  
  function getCCICompGroup(pVersionCode varchar2, pSectionCode varchar2) return t_cci_group_tab pipelined
  IS
        
        cursor cCCIGroup is
        select d.concept_code as cci_group_code, 
            d.short_e_desc as group_short_e_desc, d.short_f_desc as group_short_f_desc,
            d.long_e_desc as group_long_e_desc, d.long_f_desc as group_long_f_desc,
            d.def_e_desc as group_definition_e_desc, d.def_f_desc as group_definition_f_desc
        from table(getCCIComponentDesc(pVersionCode, pSectionCode, 'GroupComp', 'GroupCompToSectionCPV')) d;
  BEGIN
        for cRecord in cCCIGroup
        loop
          pipe row(t_cci_group(cRecord.cci_group_code, pSectionCode, pVersionCode, cRecord.group_short_e_desc,
               cRecord.group_short_f_desc, cRecord.group_long_e_desc, cRecord.group_long_f_desc,
               cRecord.group_definition_e_desc, cRecord.group_definition_f_desc));
        end loop;     
     
      return ;
        
  END getCCICompGroup;
  
  function getCCIInterv(pVersionCode varchar2, pSectionCode varchar2) return t_cci_interv_tab pipelined
  IS
        cursor cCCIInterv is
            select d.concept_code as cci_interv_code, 
            d.short_e_desc as interv_short_e_desc, d.short_f_desc as interv_short_f_desc,
            d.long_e_desc as interv_long_e_desc, d.long_f_desc as interv_long_f_desc,
            d.def_e_desc as interv_definition_e_desc, d.def_f_desc as interv_definition_f_desc
            from table(getCCIComponentDesc(pVersionCode , pSectionCode , 'Intervention', 'InterventionToSectionCPV'))  d;
  BEGIN
      for cRecord in cCCIInterv
        loop
          pipe row(t_cci_interv(cRecord.cci_interv_code, pSectionCode, pVersionCode, cRecord.interv_short_e_desc,
               cRecord.interv_short_f_desc, cRecord.interv_long_e_desc, cRecord.interv_long_f_desc,
               cRecord.interv_definition_e_desc, cRecord.interv_definition_f_desc));
        end loop;     
      
      return ;
  END getCCIInterv;
  
  function getCCITissue(pVersionCode varchar2, pSectionCode varchar2) return t_cci_tissue_tab pipelined
  IS
        cursor cCCITissue   is
            select d.concept_code as cci_tissue_code, 
            d.short_e_desc as tissue_short_e_desc, d.short_f_desc as tissue_short_f_desc,
            d.long_e_desc as tissue_long_e_desc, d.long_f_desc as tissue_long_f_desc
            from table(getCCIComponentDesc(pVersionCode , pSectionCode , 'Tissue', 'TissueToSectionCPV'))  d;
  BEGIN
        for cRecord in cCCITissue
        loop
          pipe row(t_cci_tissue(cRecord.cci_tissue_code, pSectionCode, pVersionCode, cRecord.tissue_short_e_desc,
               cRecord.tissue_short_f_desc, cRecord.tissue_long_e_desc, cRecord.tissue_long_f_desc));
        end loop; 
             
      
      return;
  END getCCITissue;
  
  function getCCIDeviceAgent(pVersionCode varchar2, pSectionCode varchar2) return t_cci_device_agent_tab pipelined
  IS
        cursor cCCIDeviceAgent  is
            select d.concept_code as cci_device_agent_code, 
            d.short_e_desc as device_agent_short_e_desc, d.short_f_desc as device_agent_short_f_desc,
            d.long_e_desc as device_agent_long_e_desc, d.long_f_desc as device_agent_long_f_desc,
            d.agent_type_e_desc as device_agent_type_e_desc, d.agent_type_f_desc as device_agent_type_f_desc,
            d.agent_e_example as device_agent_e_example, d.agent_f_example as device_agent_f_example
            from table(getCCIComponentDesc(pVersionCode , pSectionCode , 'DeviceAgent', 'DeviceAgentToSectionCPV'))  d;
  BEGIN
        for cRecord in cCCIDeviceAgent
        loop
          pipe row(t_cci_device_agent(cRecord.cci_device_agent_code, pSectionCode, pVersionCode, cRecord.device_agent_short_e_desc,
               cRecord.device_agent_short_f_desc, cRecord.device_agent_long_e_desc, cRecord.device_agent_long_f_desc,
               cRecord.device_agent_type_e_desc, cRecord.device_agent_type_f_desc, cRecord.device_agent_e_example,
               cRecord.device_agent_f_example));
        end loop  ; 
      
      return ;
  END getCCIDeviceAgent;
  
  function getCCIApproachTechnique(pVersionCode varchar2, pSectionCode varchar2) return t_cci_app_tech_tab pipelined
  IS
        cursor cCCIAppTech  is
            select d.concept_code as cci_approach_technique_code, 
            d.short_e_desc, d.short_f_desc,
            d.long_e_desc, d.long_f_desc 
            from table(getCCIComponentDesc(pVersionCode , pSectionCode , 'ApproachTechnique', 'ApproachTechniqueToSectionCPV'))  d;
  BEGIN
        for cRecord in cCCIAppTech
        loop
          pipe row(t_cci_approach_technique(cRecord.cci_approach_technique_code, pSectionCode, pVersionCode, cRecord.short_e_desc,
               cRecord.short_f_desc, cRecord.long_e_desc, cRecord.long_f_desc));
        end loop ;  
      
      return ;
  END getCCIApproachTechnique;
  
  function getCCIComponent(pVersionCode varchar2) return t_cci_component_tab pipelined
  IS
       nContextId       NUMBER(9);
       nCodeClassId     NUMBER(9);
       nGroupClassId    NUMBER(9);
       nRubricClassId   NUMBER(9);
       nCCICodeClassId  NUMBER(9);
       nTissueCompClassId          NUMBER(9);
       nDeviceCompClassId          NUMBER(9);
       nApproachCompClassId          NUMBER(9);
       nIntervCompClassId          NUMBER(9);
       nGroupCompClassId          NUMBER(9);
       nTissueCompCpvClassId          NUMBER(9);
       nDeviceCompCpvClassId          NUMBER(9);
       nApproachCompCpvClassId          NUMBER(9);
       nIntervCompCpvClassId          NUMBER(9);
       nGroupCompCpvClassId          NUMBER(9);
       nComponentCodeClassId         NUMBER(9);
       cursor cCCIComponet  is
       with componentCodes as
            (
            select REGEXP_REPLACE(tpv.text,'[^a-zA-Z0-9]','') as tabular_code, substr(tpv.text, 1,1) as section_code, cv1.classid as componentClassId, tpv1.text as componentCode
            from textpropertyversion tpv, structureelementversion sev, conceptversion cv, structureelementversion sev1,
            conceptpropertyversion cpv, structureelementversion sev2, conceptversion cv1, structureelementversion sev3,
            textpropertyversion tpv1, structureelementversion sev4
            where tpv.textpropertyid=sev.elementversionid and sev.structureid=nContextId
            and tpv.domainelementid=cv.elementid and tpv.classid=nCodeClassId
            and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId and cv.classid in (nCCICodeClassId,nRubricClassId,nGroupClassId)
            and cpv.domainelementid=cv.elementid and cpv.classid in (nTissueCompCpvClassId,nDeviceCompCpvClassId,nApproachCompCpvClassId,nIntervCompCpvClassId,nGroupCompCpvClassId)
            and cpv.conceptpropertyid=sev2.elementversionid and sev2.structureid=nContextId
            and cpv.rangeelementid=cv1.elementid and cv1.classid in (nTissueCompClassId,nDeviceCompClassId,nApproachCompClassId,nIntervCompClassId,nGroupCompClassId)
            and cv1.conceptid=sev3.elementversionid and sev3.structureid=nContextId
            and tpv1.domainelementid=cv1.elementid and tpv1.classid = nComponentCodeClassId
            and tpv1.textpropertyid=sev4.elementversionid and sev4.structureid=nContextId
            and cv.status='ACTIVE'
            )
            select  c.tabular_code, c.section_code,
             max(decode(c.componentClassId, nApproachCompClassId, decode(c.componentCode, 'XX', null, c.componentCode), null)) as cci_app_tech_code,
             max(decode(c.componentClassId, nIntervCompClassId, c.componentCode, null)) as cci_interv_code,
             max(decode(c.componentClassId, nTissueCompClassId, decode(c.componentCode, 'XX', null, c.componentCode), null)) as cci_tissue_code,
             max(decode(c.componentClassId, nGroupCompClassId, c.componentCode, null)) as cci_group_code,
             max(decode(c.componentClassId, nDeviceCompClassId, decode(c.componentCode, 'XX', null, c.componentCode), null)) as cci_device_agent_code
            from componentCodes c 
            group by c.tabular_code, c.section_code
            order by c.section_code, c.tabular_code;
  BEGIN
       nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
      if nContextID>0 then
           nCodeClassId := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
           nGroupClassId    := CIMS_CCI.getCCIClassID('ConceptVersion', 'Group');
           nRubricClassId   := CIMS_CCI.getCCIClassID('ConceptVersion', 'Rubric');
           nCCICodeClassId  := CIMS_CCI.getCCIClassID('ConceptVersion', 'CCICODE');
           nTissueCompClassId      := CIMS_CCI.getCCIClassID('ConceptVersion', 'Tissue');
           nDeviceCompClassId      := CIMS_CCI.getCCIClassID('ConceptVersion', 'DeviceAgent');
           nApproachCompClassId    := CIMS_CCI.getCCIClassID('ConceptVersion', 'ApproachTechnique');
           nIntervCompClassId      := CIMS_CCI.getCCIClassID('ConceptVersion', 'Intervention');
           nGroupCompClassId       := CIMS_CCI.getCCIClassID('ConceptVersion', 'GroupComp');
           nTissueCompCpvClassId   := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'TissueCPV');
           nDeviceCompCpvClassId     := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'DeviceAgentCPV');
           nApproachCompCpvClassId   := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'ApproachTechniqueCPV');
           nIntervCompCpvClassId     := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'InterventionCPV');
           nGroupCompCpvClassId      := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'GroupCompCPV');
           nComponentCodeClassId     := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'ComponentCode');
         
           
            for cRecord in cCCIComponet
            loop
              pipe row(t_cci_component(pVersionCode, cRecord.tabular_code,
                   cRecord.section_code, cRecord.cci_app_tech_code, cRecord.cci_interv_code,
                   cRecord.cci_tissue_code, cRecord.cci_group_code, cRecord.cci_device_agent_code));
            end loop ;
       end if;
       
       return ;
  END getCCIComponent;
  
  function getCCIRefenceValues(pVersionCode varchar2, pAttributeType varchar2) return t_cci_ref_value_tab pipelined
  IS
       nContextId            NUMBER(9);
       nCodeClassId          NUMBER(9);
       nDescriptionClassId   NUMBER(9);
       nAttrTypeIndClassId   NUMBER(9);
       nAttrCodeClassId      NUMBER(9);
       nRefValueClassId      NUMBER(9);
       nMandatoryIndicatorClassId NUMBER(9);
       Cursor cRefValues IS
       with refValues as
        (
        select cv.elementid, tpv.text, tpv1.languageCode, tpv1.text as attrDesc, bpv.booleanvalue
        from textpropertyversion tpv, structureelementversion sev, conceptversion cv, structureelementversion sev1,
        textpropertyversion tpv1, booleanpropertyversion bpv, structureelementversion sev2, conceptpropertyversion atcpv, textpropertyversion atcode,
        structureelementversion sev3, structureelementversion sev4, structureelementversion sev5
        where tpv.textpropertyid=sev.elementversionid and sev.structureid=nContextId
        and tpv1.textpropertyid=sev2.elementversionid and sev2.structureid=nContextId
        and tpv.domainelementid=cv.elementid and tpv.classid = nCodeClassID and cv.status='ACTIVE'
        and tpv1.domainelementid=cv.elementid and tpv1.classid = nDescriptionClassId
        and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId and cv.classid = nRefValueClassId
        and atcpv.conceptpropertyid=sev3.elementversionid and sev3.structureid=nContextId
        and atcpv.domainelementid=cv.elementid and atcpv.rangeelementid=atcode.domainelementid
        and atcpv.classid=nAttrTypeIndClassId
        and atcode.textpropertyid=sev4.elementversionid and sev4.structureid=nContextId
        and atcode.classid=nAttrCodeClassId and atcode.text=pAttributeType
        and bpv.booleanpropertyid = sev5.elementversionid and sev5.structureid=nContextId
        and bpv.domainelementid=cv.elementid and bpv.classid=nMandatoryIndicatorClassId
        )
        select r.elementid,  r.text as ref_value_code,
         max(decode(r.languageCode, 'ENG', r.attrDesc, null)) as e_desc,
         max(decode(r.languageCode, 'FRA', r.attrDesc, null)) as f_desc,
         r.booleanvalue,
         e.elementuuid
        from refValues r, element e
        where r.elementid=e.elementid 
        group by r.elementid, r.text, r.booleanvalue, e.elementuuid order by r.text;
        
  BEGIN
       nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
      if nContextID>0 then
         nCodeClassID := Cims_Cci.getCCIClassID('TextPropertyVersion','AttributeCode');
         nDescriptionClassId := cims_cci.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
         nAttrTypeIndClassId := cims_cci.getCCIClassID('ConceptPropertyVersion', 'AttributeTypeIndicator');
         nAttrCodeClassId := cims_cci.getCCIClassID('TextPropertyVersion', 'DomainValueCode');
         nRefValueClassId := cims_cci.getCCIClassID('ConceptVersion', 'ReferenceAttribute');
         nMandatoryIndicatorClassId := cims_cci.getCCIClassID('BooleanPropertyVersion','AttributeMandatoryIndicator');
         
         for cRecord in cRefValues
         loop
             pipe row(t_cci_ref_value(cRecord.elementid, cRecord.ref_value_code, pVersionCode, cRecord.e_desc,
             cRecord.f_desc, cRecord.booleanvalue, cRecord.elementuuid));        
         end loop;
       end if;
       return ;
       
  END getCCIRefenceValues;
  
  function getCCILocationRefValues(pVersionCode varchar2) return t_cci_loc_ref_value_tab pipelined
  IS
      cursor cCCILocRef    is
      select r.ref_value_code, r.version_code, r.ref_e_desc, r.ref_f_desc, r.mandatory_ind_code, r.element_uuid from table(getCCIRefenceValues(pVersionCode, 'L')) r;
      
  BEGIN
      for cRecord in cCCILocRef
         loop
             pipe row(t_cci_location_reference_value(cRecord.ref_value_code, cRecord.version_code, cRecord.ref_e_desc,
             cRecord.ref_f_desc, cRecord.mandatory_ind_code, cRecord.element_uuid));        
         end loop;
      
      return ;
  END getCCILocationRefValues;
  
  function getCCIModeOfDelRefValues(pVersionCode varchar2) return t_cci_mode_ref_value_tab pipelined
  IS
      cursor cCCIModeRef    is
            select r.ref_value_code, r.version_code, r.ref_e_desc, r.ref_f_desc, r.mandatory_ind_code, r.element_uuid from table(getCCIRefenceValues(pVersionCode, 'M')) r;
  BEGIN
      for cRecord in cCCIModeRef
         loop
             pipe row(t_cci_mode_of_del_ref_value(cRecord.ref_value_code, cRecord.version_code, cRecord.ref_e_desc,
             cRecord.ref_f_desc, cRecord.mandatory_ind_code, cRecord.element_uuid));        
         end loop;
      
      return ;
  END getCCIModeOfDelRefValues;
  
  function getCCIExtentRefValues(pVersionCode varchar2) return t_cci_ext_ref_value_tab pipelined
  IS
      cursor cCCIExtentRef    is
      select r.ref_value_code, r.version_code, r.ref_e_desc, r.ref_f_desc, r.mandatory_ind_code, r.element_uuid from table(getCCIRefenceValues(pVersionCode, 'E')) r;
      
  BEGIN
      for cRecord in cCCIExtentRef
         loop
             pipe row(t_cci_extent_reference_value(cRecord.ref_value_code, cRecord.version_code, cRecord.ref_e_desc,
             cRecord.ref_f_desc, cRecord.mandatory_ind_code, cRecord.element_uuid));        
         end loop;
      
      return ;
  END getCCIExtentRefValues;
  
  function getCCIStatusRefValues(pVersionCode varchar2) return t_cci_sta_ref_value_tab pipelined
  IS
      cursor cCCIStatusRef    is
      select r.ref_value_code, r.version_code, r.ref_e_desc, r.ref_f_desc, r.mandatory_ind_code, r.element_uuid from table(getCCIRefenceValues(pVersionCode, 'S')) r;
      
  BEGIN
      for cRecord in cCCIStatusRef
         loop
             pipe row(t_cci_status_reference_value(cRecord.ref_value_code, cRecord.version_code, cRecord.ref_e_desc,
             cRecord.ref_f_desc, cRecord.mandatory_ind_code, cRecord.element_uuid));        
         end loop;
      
      return ;
  END getCCIStatusRefValues;
  
  function getCCIGenericAttributes(pVersionCode varchar2, pRefValue varchar2) return t_cci_generic_attr_tab pipelined
  IS
        nAttributeCodeClassId          NUMBER(9);
        nContextId                     NUMBER(9);
        nRefValueClassId               NUMBER(9);
        nAttributeDescClassId          NUMBER(9);
        nGenericAttrCPVClassId         NUMBER(9);
        nRefValueCPVClassId            NUMBER(9);
        
        cursor genericAttributes is
         with refCPV as
        (
             select cpv.* from conceptpropertyversion cpv, structureelementversion sev where cpv.classid=nRefValueCPVClassId and cpv.conceptpropertyid=sev.elementversionid and sev.structureid=nContextId
        ),
        geneCPV as
        (
              select cpv.* from conceptpropertyversion cpv, structureelementversion sev where cpv.classid=nGenericAttrCPVClassId and cpv.conceptpropertyid=sev.elementversionid and sev.structureid=nContextId  
        ),
        geneCodePro as
        (
              select tpv.* from textpropertyversion tpv, structureelementversion sev where tpv.classid=nAttributeCodeClassId and tpv.textpropertyid=sev.elementversionid and sev.structureid= nContextId  
        ),
        geneDesc as (
                 select tpv.* from textpropertyversion tpv, structureelementversion sev where tpv.classid=nAttributeDescClassId and tpv.languagecode = 'ENG' and tpv.textpropertyid=sev.elementversionid and sev.structureid= nContextId  
        ),
        geneDescFra as (
                 select tpv.* from textpropertyversion tpv, structureelementversion sev where tpv.classid=nAttributeDescClassId and tpv.languagecode = 'FRA' and tpv.textpropertyid=sev.elementversionid and sev.structureid= nContextId  
        )
        
        select tpv1.text as ga_code, tpv.text as code, tpv2.text as attribute_e_desc, tpv3.text as attribute_f_desc, e.elementuuid
            from textpropertyversion tpv, structureelementversion sev,
            geneCPV cpv2, refCPV cpv1, geneCodePro tpv1, geneDesc tpv2, geneDescFra tpv3,
            conceptversion cv, structureelementversion sev5, element e
            where tpv.textpropertyid=sev.elementversionid and sev.structureid=nContextId
            and cv.elementid=tpv.domainelementid and cv.classid=nRefValueClassId and cv.status='ACTIVE' 
            and cv.conceptid=sev5.elementversionid and sev5.structureid=nContextId
            and cv.elementid = cpv1.rangeelementid
            and cpv1.domainelementid = cpv2.domainelementid
            and cpv2.rangeelementid = tpv1.domainelementid
            and cpv2.domainelementid = tpv2.domainelementid 
            and cpv2.domainelementid = tpv3.domainelementid 
            and tpv.classid=nAttributeCodeClassId and tpv.text=pRefValue
            and tpv1.domainelementid=e.elementid;
  BEGIN
       nContextID := cims_cci.getCCIStructureByYear(pVersionCode);
       if nContextID>0 then
         nAttributeCodeClassId := cims_cci.getCCIClassID('TextPropertyVersion', 'AttributeCode');
         nAttributeDescClassId := cims_cci.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
         nRefValueClassId := cims_cci.getCCIClassID('ConceptVersion', 'ReferenceAttribute');
         nGenericAttrCPVClassId := cims_cci.getCCIClassID('ConceptPropertyVersion', 'GenericAttributeCPV');
         nRefValueCPVClassId := cims_cci.getCCIClassID('ConceptPropertyVersion', 'ReferenceAttributeCPV');
         
         for cRecord in genericAttributes
         loop
             pipe row(t_cci_generic_attribute(cRecord.ga_code, cRecord.code, pVersionCode, 
             cRecord.attribute_e_desc, cRecord.attribute_f_desc, cRecord.elementuuid));        
         end loop;
       end if;
       return;
  END getCCIGenericAttributes; 
  
  
  function getCCIValidationRule(pVersionCode varchar2) return t_cci_validation_rule_tab pipelined
  IS
        nContextId                  NUMBER(9);
        nValidationCCIClassId       NUMBER(9);
        nFacilityTypeClassId        NUMBER(9);
        nValidationCCICPVClassId    NUMBER(9);
        nRubricClassId              NUMBER(9);
        nCCICODEClassId             NUMBER(9);
        nCodeClassId                NUMBER(9);
        nValidationFacilityClassId  NUMBER(9);
        nDomainValueCodeClassId     NUMBER(9);
        nValidationDefClassId      NUMBER(9);
        
        out_rec t_cci_validation_rule := t_cci_validation_rule(NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
        
        
        cursor cValidationRules is
        with elementproperties as
          ( SELECT 
                   cv1.elementid as elementid, -- Category
                   cv.elementid as validationEid,
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
              and cv.classid=nValidationCCIClassId
              and cv1.classid in (nRubricClassId,nCCICODEClassId)
              and dh.classid=nFacilityTypeClassId
              and cpv.classid = nValidationCCICPVClassId
              and dhcpv.classid= nValidationFacilityClassId
              and tpv.classid= nCodeClassId
              and dhcode.classid= nDomainValueCodeClassId
              and cpv.conceptpropertyid = sev.elementversionid
              and cv.conceptid = sev1.elementversionid
              and cv1.conceptid= sev2.elementversionid
              and dhcpv.conceptpropertyid=sev3.elementversionid
              and dh.conceptid=sev4.elementversionid
              and dhcode.textpropertyid=sev5.elementversionid
              and validationrule.xmlpropertyid=sev6.elementversionid
              and validationrule.classid=nValidationDefClassId
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
          select ep.elementid,
                 REGEXP_REPLACE(ep.code,'[^a-zA-Z0-9]','') as cci_tabular_code,
                 hasChild,
                 dhcode as data_holding_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/GENDER_CODE') as gender_validation_code,
                 cims_retrieve_api.getMinAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_min,
                 cims_retrieve_api.getMaxAgeFromAgeRange(cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/AGE_RANGE')) as age_max,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/STATUS_REF') as status_ref_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/EXTENT_REF') as extent_ref_code,
                 cims_retrieve_api.getXMLPropertyValue(ep.xmltext, '/validation/LOCATION_REF') as location_ref_code,
                 e.elementuuid as data_holding_validation_uuid
          from elementproperties ep, element e where ep.validationEid=e.elementid
          order by data_holding_code, cci_tabular_code;
  BEGIN
          
          nContextId := cims_cci.getCCIStructureByYear(pVersionCode);
          if nContextId>0 then
            nValidationCCIClassId       := cims_cci.getCCIClassID('ConceptVersion', 'ValidationCCI');
            nFacilityTypeClassId        := cims_cci.getCCIClassID('ConceptVersion', 'FacilityType');
            nValidationCCICPVClassId    := cims_cci.getCCIClassID('ConceptPropertyVersion', 'ValidationCCICPV');
            nRubricClassId              := cims_cci.getCCIClassID('ConceptVersion', 'Rubric');
            nCCICODEClassId             := cims_cci.getCCIClassID('ConceptVersion', 'CCICODE');
            nCodeClassId                := cims_cci.getCCIClassID('TextPropertyVersion', 'Code');
            nValidationFacilityClassId  := cims_cci.getCCIClassID('ConceptPropertyVersion', 'ValidationFacility');
            nDomainValueCodeClassId     := cims_cci.getCCIClassID('TextPropertyVersion', 'DomainValueCode');
            nValidationDefClassId     := cims_cci.getCCIClassID('XMLPropertyVersion', 'ValidationDefinition');
            for cRecord in cValidationRules
            Loop
                out_rec.element_id := cRecord.elementid;
                out_rec.version_code := pVersionCode;
                out_rec.tabular_code := cRecord.cci_tabular_code;
                out_rec.has_child := trim(cRecord.hasChild);
                out_rec.data_holding_code := cRecord.data_holding_code;
                out_rec.age_min := cRecord.age_min;
                out_rec.age_max := cRecord.age_max;
                out_rec.gender_validation_code := cRecord.gender_validation_code;
                out_rec.status_ref_code := cRecord.status_ref_code;
                out_rec.extent_ref_code := cRecord.extent_ref_code;
                if substr(cRecord.location_ref_code, 1, 1)='L' then
                   out_rec.location_ref_code := cRecord.location_ref_code;
                   out_rec.mode_of_del_ref_code := NULL;
                else
                   out_rec.location_ref_code := NULL;
                   out_rec.mode_of_del_ref_code := cRecord.location_ref_code;
                end if;
                out_rec.data_holding_validation_uuid := cRecord.data_holding_validation_uuid;
                pipe row(out_rec);
            end Loop;
          end if;
          
          return;
  END getCCIValidationRule;
end CIMS_RETRIEVE_API_CCI;
/
