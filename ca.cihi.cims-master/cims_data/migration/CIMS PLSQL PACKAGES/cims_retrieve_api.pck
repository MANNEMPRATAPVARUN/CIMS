create or replace package cims_retrieve_api is

  -- Author  : TYANG
  -- Created : 14/04/2015 9:56:43 AM
  -- Purpose : Retrieve classification tabular concept and validation rules
  
  -- Public type declarations
  
  TYPE ref_cursor is ref cursor;
  
  
  
  TYPE t_data_holding_tab IS table of t_data_holding;
  -- Public constant declarations
  
  icd10ca_classification_code varchar2(20) := 'ICD-10-CA';
  -- Public variable declarations

  -- Public function and procedure declarations
  
  function getDataHolding(pClassificationCode varchar2, pVersionCode varchar2) return t_data_holding_tab pipelined;
  
  function getXMLPropertyValue(xml_text CLOB, field varchar2) return varchar2;
  
  function getMinAgeFromAgeRange(ageRange varchar2) return number;
  
  function getMaxAgeFromAgeRange(ageRange varchar2) return number;
  
  
end cims_retrieve_api;
/
create or replace package body cims_retrieve_api is

  -- Private type declarations
  
  -- Private constant declarations

  -- Private variable declarations

  -- Function and procedure implementations
  
  function getDataHolding(pClassificationCode varchar2, pVersionCode varchar2) return t_data_holding_tab pipelined
  IS
      nContextId      NUMBER(9);
      nDHClassId      NUMBER(9);
      nDomainValueCodeClassId  NUMBER(9);
      nDomainValueDescClassId  NUMBER(9);
      
      Cursor cDataHolding is
      select dhcode.text, dhdesc.text as dhdescription, e.elementuuid from  conceptversion dh, textpropertyversion dhcode, textpropertyversion dhdesc, element e,
         structureelementversion sev1, structureelementversion sev2, structureelementversion sev3
         where dh.classid = nDHClassId
         and dhcode.domainelementid = dh.elementid
         and dhcode.classid = nDomainValueCodeClassId
         and dh.status = 'ACTIVE'
         and dh.conceptid = sev1.elementversionid and sev1.structureid=nContextId
         and dhcode.textpropertyid = sev2.elementversionid and sev2.structureid=nContextId
         and dhdesc.classid = nDomainValueDescClassId and dhdesc.domainelementid=dh.elementid and dhdesc.languagecode='ENG'
         and dhdesc.textpropertyid = sev3.elementversionid and sev3.structureid=nContextId
         and dh.elementid=e.elementid
        ;
         
  BEGIN
       
       select sv.structureid into nContextId
       from  structureversion sv, elementversion ev, class c
       where sv.structureid=ev.elementversionid and ev.versioncode=pVersionCode and sv.basestructureid is null
       and sv.classid=c.classid and c.baseclassificationname=pClassificationCode;
       
       if nContextId >0 then
           if pClassificationCode=cims_retrieve_api.icd10ca_classification_code then
              nDHClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion','FacilityType');
              nDomainValueCodeClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion','DomainValueCode');
              nDomainValueDescClassId := CIMS_ICD.getICD10CAClassID('TextPropertyVersion','DomainValueDescription');
           else
              nDHClassId := CIMS_CCI.getCCIClassID('ConceptVersion','FacilityType');
              nDomainValueCodeClassId := CIMS_CCI.getCCIClassID('TextPropertyVersion','DomainValueCode');
              nDomainValueDescClassId := CIMS_CCI.getCCIClassID('TextPropertyVersion','DomainValueDescription');
           end if;
           
           for cRecord in cDataholding
           loop
               pipe row(t_data_holding(cRecord.Text, pVersionCode, cRecord.Dhdescription, cRecord.Elementuuid));
           end loop;
       end if;
       return;
  END getDataHolding;
  
  function getXMLPropertyValue(xml_text CLOB, field varchar2) return varchar2
  IS
       field_value varchar2(20);
  BEGIN

      SELECT EXTRACTVALUE(XMLType(dbms_lob.substr(xml_text, 4000, 99 )), field) 
      INTO field_value
      FROM DUAL;
       
      return field_value;
  END getXMLPropertyValue;

  function getMinAgeFromAgeRange(ageRange varchar2) return number
  IS
      pos number;
      min_age number;
  BEGIN
      pos := instr(ageRange, '-');
      if pos <= 1 then
        return null;
      end if;
        
      min_age := substr(ageRange, 1, pos-1);
      return min_age;
  END getMinAgeFromAgeRange;      

  function getMaxAgeFromAgeRange(ageRange varchar2) return number
  IS
      pos number;
      max_age number;
  BEGIN
      pos := instr(ageRange, '-');
      if pos <= 1 then
        return null;
      end if;

      max_age := substr(ageRange, pos+1);
      return max_age;
  END getMaxAgeFromAgeRange;      

end cims_retrieve_api;
/
