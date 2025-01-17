create or replace package cims_refset is

  -- Author  : TYANG
  -- Created : 10/06/2016 3:22:51 PM
  -- Purpose : CSRE2 framework handler related query

  -- Public type declarations


  -- Public constant declarations


  -- Public variable declarations


  -- Public function and procedure declarations
  PROCEDURE updateAssignee(nAssigneeId in NUMBER, nRefsetId IN NUMBER) ;
  PROCEDURE removeEmptyRecords(nContextId IN NUMBER, nRecordClasssId IN NUMBER, nValueClasssId IN NUMBER, nRelationshipClasssId IN NUMBER);
  Procedure copyConfiguration(nFromContextId IN NUMBER, nToContextId IN NUMBER);
  PROCEDURE checkPicklistRemovable(nContextId IN NUMBER, nPicklistElementId IN NUMBER, nCount OUT NUMBER);
  PROCEDURE removePicklistOutputColumn(nContextId IN NUMBER, nColumnID IN NUMBER);
  PROCEDURE initAsotRelease(nPicklistOutputId IN NUMBER);
end cims_refset;
/
create or replace package body cims_refset is

  -- Private type declarations


  -- Private constant declarations


  -- Private variable declarations


  -- Function and procedure implementations
 /**************************************************************************************************************************************
    * NAME:          updateAssignee
    * DESCRIPTION:   create or update the record in refset_control table
    **************************************************************************************************************************************/
      PROCEDURE updateAssignee(nAssigneeId in NUMBER, nRefsetId IN NUMBER)  IS
            nCount NUMBER;
       BEGIN
         select count(1) into nCount from refset_control r where r.refset_control_id=nRefsetId;
         if nCount=0 then
           insert into refset_control (refset_control_id, refset_assignee_id, status) values (nRefsetId, nAssigneeId, 'ACTIVE');
         else
           update refset_control set refset_assignee_id=nAssigneeId where refset_control_id=nRefsetId;
         end if;

       END updateAssignee;
       
       /******************************************************************
       *  NAME:          deleteEmptyRecords
       *  DESCRIPTION:   After create new refset version, some records may not have any values now, this method will remove those record
	     *                 concept.
       ******************************************************************/
       
       PROCEDURE removeEmptyRecords(nContextId IN NUMBER, nRecordClasssId IN NUMBER, nValueClasssId IN NUMBER, nRelationshipClasssId IN NUMBER) IS
            relatedConceptId_array dbms_sql.Number_Table;
       BEGIN            
            select distinct cv1.Elementid bulk collect into relatedConceptId_array from conceptversion cv, structureelementversion sev1, conceptpropertyversion cpv, conceptversion cv1, structureelementversion sev2, structureelementversion sev 
            where cpv.conceptpropertyid = sev.elementversionId and sev.structureid = nContextId and cpv.rangeelementid=cv1.elementid and cpv.classid=nRelationshipClasssId
            and cv1.classid=nRecordClasssId and cv1.conceptid=sev2.elementversionid and sev2.structureid=nContextId and cv1.status='ACTIVE'
            and cpv.domainelementid=cv.elementid and cv.classid=nValueClasssId and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId and cv.status='REMOVED'
            and not exists (
                select 1 from conceptversion cv, conceptpropertyversion cpv1, structureelementversion sev1, structureelementversion sev3 
                where cv1.elementid=cpv1.rangeelementid and cpv1.classid=nRelationshipClasssId and cpv1.conceptpropertyid=sev3.elementversionid and sev3.structureid=nContextId
                and cpv1.domainelementid=cv.elementid and cv.classid=nValueClasssId 
                and cv.conceptid=sev1.elementversionid and sev1.structureid=nContextId and cv.status='ACTIVE'
            );
            
            for i in 1..relatedConceptId_array.count
            Loop
                cims_framework.removeConceptSelf(nContextId, relatedConceptId_array(i));
            end Loop;
            
       END removeEmptyRecords;  
       
       /***********************************************************************
       *  NAME:          copyConfiguration
       *  DESCRIPTION:   copy the output configuration from old to new context
       ************************************************************************/ 
       Procedure copyConfiguration(nFromContextId IN NUMBER, nToContextId IN NUMBER) IS
            type refsetOutputId_type is table of number index by BINARY_INTEGER ;
            nRefsetOutputIdMap refsetOutputId_type;
            type picklistOutputId_type is table of number index by BINARY_INTEGER ;
            nPickListOuputIdMap picklistOutputId_type;
            type columnId_type is table of number index by BINARY_INTEGER ;
            cursor cRefsetOutput is select * from refset_output where refset_context_id=nFromContextId;
            nColumnIdMap columnId_type;
            nRefsetOutputId  NUMBER;
            nNewRefsetOutputId NUMBER;
            nPicklistOutputId NUMBER;
            nNewPicklistOutputId NUMBER;
            nColumnOutputId NUMBER;
            nNewColumnOutputId NUMBER;
            cursor cPicklistOutput is select * from picklist_output where refset_context_id=nFromContextId; 
            cursor cRefsetOutputPicklist is 
                   select rop.* 
                   from refset_output_picklist rop,  refset_output ro 
                   where 
                   rop.refset_output_id = ro.refset_output_id and
                   refset_context_id=nFromContextId;
            
            cursor cSupplementOutput is select * from refset_supplement_output where refset_output_id=nRefsetOutputId; 
            cursor cParentColumnOutput is select * from picklist_column_output where picklist_output_id=nPicklistOutputId and parent_pl_column_output_id is null;
            cursor cChildColumnOutput is select * from picklist_column_output where picklist_output_id=nPicklistOutputId and parent_pl_column_output_id is not null;
       BEGIN
            -- copy picklist_output, map picklist_output_id from previous context to new context
            for pick_rec in cPicklistOutput
            loop
                select picklist_output_seq.nextval into nNewPicklistOutputId from dual;
                nPicklistOutputId:=pick_rec.picklist_output_id;
                nPickListOuputIdMap(nPicklistOutputId) := nNewPicklistOutputId;
                    
                insert into picklist_output
                select nNewPicklistOutputId, nToContextId, picklist_id, NAME, languagecode,TAB_NAME, table_name, picklist_output_code, asot_release_ind_code, nPicklistOutputId
                from picklist_output where picklist_output_id=nPicklistOutputId;
                    
                for column_rec in cParentColumnOutput
                loop
                    nColumnOutputId := column_rec.picklist_column_output_id;
                    select picklist_column_output_seq.nextval into nNewColumnOutputId from dual;
                        
                    nColumnIdMap(nColumnOutputId):=nNewColumnOutputId;
                        
                    insert into picklist_column_output
                    select nNewColumnOutputId, nToContextId,nNewPicklistOutputId,column_id,order_number,display_mode_code,null
                    from picklist_column_output
                    where picklist_column_output_id = nColumnOutputId;
                        
                end loop;
                    
                for column_rec in cChildColumnOutput
                loop
                    nColumnOutputId := column_rec.picklist_column_output_id;
                    select picklist_column_output_seq.nextval into nNewColumnOutputId from dual;
                        
                    insert into picklist_column_output
                    select nNewColumnOutputId, nToContextId,nNewPicklistOutputId,column_id,order_number,display_mode_code,nColumnIdMap(column_rec.parent_pl_column_output_id)
                    from picklist_column_output
                    where picklist_column_output_id = nColumnOutputId;
                        
                end loop;                    
                    
            end loop;

            -- copy refset_output data, map refset_output_id from previous context to new context
            for refsetoutput_rec in cRefsetOutput 
            loop
                select refset_output_seq.nextval into nNewRefsetOutputId from dual;
                nRefsetOutputId:=refsetoutput_rec.refset_output_id;
                nRefsetOutputIdMap(nRefsetOutputId) := nNewRefsetOutputId;
                
                insert into refset_output 
                values(nNewRefsetOutputId, nToContextId, refsetoutput_rec.refset_id, refsetoutput_rec.name, refsetoutput_rec.languagecode, refsetoutput_rec.file_name); 
                
                insert into refset_output_title_page
                select nNewRefsetOutputId, title, Supplement_Id from refset_output_title_page where refset_output_id=refsetoutput_rec.refset_output_id;
                
                for supp_rec in cSupplementOutput
                loop
                    insert into refset_supplement_output
                    values( REFSET_SUPPLEMENT_OUTPUT_SEQ.nextval, nToContextId, supp_rec.supplement_id, supp_rec.order_number, nNewRefsetOutputId );
                    
                end loop;
            end loop;
            
            -- copy refset_output_picklist data, based on the refset_output_id and picklist_output_id mapping saved in the above steps
            for refsetoutputpicklist_rec in cRefsetOutputPicklist
            loop
                insert into refset_output_picklist (
                    refset_output_id,
                    picklist_output_id,
                    order_number)
                values (
                    nRefsetOutputIdMap(refsetoutputpicklist_rec.refset_output_id),
                    nPickListOuputIdMap(refsetoutputpicklist_rec.picklist_output_id),
                    refsetoutputpicklist_rec.order_number
                );
            end loop;    
            
       END copyConfiguration;
       
       /**************************************************************************************
       *  NAME:          checkPicklistRemovable
       *  DESCRIPTION:   Check if a picklist has record or referenced by output configuration.
       ***************************************************************************************/
       
       PROCEDURE checkPicklistRemovable(nContextId IN NUMBER, nPicklistElementId IN NUMBER, nCount OUT NUMBER) IS
            nPartOfClassID  NUMBER;
            nRecordClassID  NUMBER;
       BEGIN
            select count(1) into nCount from picklist_output where refset_context_id=nContextId and picklist_id=nPicklistElementId;
            if nCount=0 then
              select classid into nPartOfClassID
              from class c
              where c.baseclassificationname in 
                    (select c1.baseclassificationname from class c1, element e where c1.classid=e.classid and e.elementid=nPicklistElementId)
                    and c.classname='PartOf';
                    
              select classid into nRecordClassID
              from class c
              where c.baseclassificationname in 
                    (select c1.baseclassificationname from class c1, element e where c1.classid=e.classid and e.elementid=nPicklistElementId)
                    and c.classname='Record';
              
              select count(1) into nCount 
              from conceptversion cv, conceptpropertyversion cpv, conceptversion cv1, structureelementversion sev, structureelementversion sev1, structureelementversion sev2
              where cv.conceptid=sev.elementversionid and sev.structureid=nContextId and cv.elementid=nPicklistElementId
              and cv.elementid=cpv.rangeelementid and cpv.conceptpropertyid = sev1.elementversionid and sev1.structureid=nContextId
              and cpv.classid = nPartOfClassID  and cpv.domainelementid=cv1.elementid and cv1.classid= nRecordClassID
              and cv1.status='ACTIVE' and cv1.conceptid=sev2.elementversionid and sev2.structureid=nContextId;
            end if;
            
       END checkPicklistRemovable; 
       
       /**********************************************************************************************************************************
       *  NAME:          removePicklistOutputColumnConfig
       *  DESCRIPTION:   When remove a column from picklist, this stored procedure will be call to remove correspond output column config.
       ***********************************************************************************************************************************/
       
       PROCEDURE removePicklistOutputColumn(nContextId IN NUMBER, nColumnID IN NUMBER) IS
       BEGIN
           DELETE FROM picklist_column_output WHERE refset_context_id = nContextId and parent_pl_column_output_id in (select picklist_column_output_id from picklist_column_output where refset_context_id = nContextId and column_id = nColumnID);
           DELETE FROM picklist_column_output WHERE refset_context_id = nContextId and column_id=nColumnID;
            
       END removePicklistOutputColumn; 
       
       /**********************************************************************************************************************************
       *  NAME:          initiate asot release
       *  DESCRIPTION:   cleanup existing asot data before release
       ***********************************************************************************************************************************/
       
       PROCEDURE initAsotRelease(nPicklistOutputId IN NUMBER) IS
       BEGIN
           DELETE FROM record_column_value WHERE record_id in (select record_id from record where picklist_id = nPicklistOutputId);
           DELETE FROM record where picklist_id=nPicklistOutputId;
           DELETE from picklist_column where picklist_id=nPicklistOutputId;
           DELETE from picklist where picklist_id=nPicklistOutputId;
            
       END initAsotRelease; 
       

end cims_refset;
/
