create or replace package cims_framework is

  -- Author  : TYANG
  -- Created : 10/06/2016 3:22:51 PM
  -- Purpose : CSRE2 framework handler related query
  
  -- Public type declarations
  
  
  -- Public constant declarations
  

  -- Public variable declarations
  

  -- Public function and procedure declarations
  PROCEDURE createStructureVersion(nElementVersionId in NUMBER, nClasssId in NUMBER, nElementId in NUMBER, nBaseContextId in NUMBER) ;
  PROCEDURE createElement(nClasssId in NUMBER, sKey in VARCHAR2, elemid out NUMBER) ;
  PROCEDURE createContextVersion(nClasssId in NUMBER, nElementId in NUMBER, sVersionCode in VARCHAR2, contextId out NUMBER);
  PROCEDURE removeContext(pContextId in NUMBER);
  PROCEDURE removeConcept(pContextId in NUMBER, pElementId in NUMBER);
  PROCEDURE removeConceptSelf(pContextId in NUMBER, pElementId in NUMBER);
  PROCEDURE createElementVersionInContext(pContextId in NUMBER,pElementId in NUMBER, pChangedFromVersionId in NUMBER, pElementVersionId out NUMBER) ;
    

end cims_framework;
/
create or replace package body cims_framework is

  -- Private type declarations
  
  
  -- Private constant declarations
  

  -- Private variable declarations
  

  -- Function and procedure implementations
 /**************************************************************************************************************************************
    * NAME:          createStructureVersion
    * DESCRIPTION:   create record in structureversion table and link element from baseContent if not null
    **************************************************************************************************************************************/
       PROCEDURE createStructureVersion(nElementVersionId in NUMBER, nClasssId in NUMBER, nElementId in NUMBER, nBaseContextId in NUMBER)  IS
       BEGIN
         insert into structureversion (structureid, classid, status, elementid, basestructureid, contextstatus, contextstatusdate, isversionyear)
         values (nElementVersionId, nClasssId, 'ACTIVE', nElementId, nBaseContextId, 'OPEN', SYSDATE, 'N');
         
         if nBaseContextId is not null then
           INSERT INTO STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUSDATE)
           SELECT ELEMENTVERSIONID, nElementVersionId, ELEMENTID, SYSDATE
           FROM STRUCTUREELEMENTVERSION WHERE STRUCTUREID = nBaseContextId;
         end if; 
         
       END createStructureVersion;
    
    
    /**************************************************************************************************************************************
    * NAME:          createElement
    * DESCRIPTION:   Returns the element ID for the given classId and Key
    **************************************************************************************************************************************/
       PROCEDURE createElement(nClasssId in NUMBER, sKey in VARCHAR2, elemid out NUMBER)  IS
       BEGIN
         select elementid into elemid from element where classid=nClasssId and elementuuid = sKey;
         EXCEPTION
           WHEN no_data_found THEN
                select elementid_seq.nextval into elemid from dual;
                insert into element values (elemid, nClasssId, sKey, '');
         
       END createElement;
       
       
    /**************************************************************************************************************************************
    * NAME:          createContextVersion
    * DESCRIPTION:   create the elementversion record and Returns the elementversion ID for the context
    **************************************************************************************************************************************/
       PROCEDURE createContextVersion(nClasssId in NUMBER, nElementId in NUMBER, sVersionCode in VARCHAR2, contextId out NUMBER)  IS
       BEGIN
         select elementversionid_seq.nextval into contextId from dual;
         insert into ELEMENTVERSION(
                 ELEMENTVERSIONID,
                 ELEMENTID,
                 VERSIONCODE,
                 VERSIONTIMESTAMP,
                 STATUS,
                 NOTES,
                 CLASSID,
                 CHANGEDFROMVERSIONID,
                 ORIGINATINGCONTEXTID )
        values ( contextId,
                 nElementId,
                 sVersionCode,
                 SYSDATE,
                 'ACTIVE',
                 null,
                 nClasssId,
                 null,
                 null
            );
         
       END createContextVersion;
       
    /**************************************************************************************************************************************
    * NAME:          removeContext
    * DESCRIPTION:   When removing a context: -- select ev.elementid from elementversion ev, structureelementversion sev where
	  * ev.changedFromVersionId is null and ev.elementversionId = sev.elementversionId and sev.structureid = contextid -
	  * save this in elementlist
	  *
	  * - detach everything -- delete structureelementversion where structureid=contextid - delete everything where
	  * originatingcontextid= contextId - delete from elementversion( and structureversion) where structureid=contextid -
	  * delete from element where elementid in (elementlist)
    **************************************************************************************************************************************/
       PROCEDURE removeContext(pContextId in NUMBER)  IS
             relatedConceptId_array dbms_sql.Number_Table;
             pContextElementId NUMBER;
             nCount NUMBER;
             nConceptId NUMBER;
             nNewConceptId NUMBER;
       BEGIN
         
             -- save all the concept created in this context which is related to the concept being deleted
            select cv.elementid BULK COLLECT into relatedConceptId_array from conceptversion cv, structureelementversion sev 
            where cv.conceptid = sev.elementversionId and sev.structureid = pContextId and cv.status='ACTIVE' ;
                   
            -- update all the conceptversions in this context to REMOVED
            for i in 1..relatedConceptId_array.count
            Loop
                select sev.elementversionid into nConceptId from structureelementversion sev where sev.structureid=pContextId and sev.elementid=relatedConceptId_array(i);
                
                
                select count(1) into nCount from elementversion ev where ev.originatingcontextid=pContextId and elementid=relatedConceptId_array(i);
                
                if nCount=1 then -- created in the same context
                     update conceptversion set status='REMOVED' where conceptid = nConceptId;
                     update elementversion ev set ev.status='REMOVED' where ev.elementversionid=nConceptId;
                     update element e set e.elementuuid = dbms_random.string('x',30) where e.elementid=relatedConceptId_array(i);
                else
                 select elementversionid_seq.nextval into nNewConceptId from dual;
                 insert into elementversion select nNewConceptId, elementid, versioncode, sysdate, 'REMOVED', '', classid, elementversionid, pContextId from elementversion where elementversionid=nConceptId;
                 insert into conceptversion select nNewConceptId, classid, 'REMOVED', elementid from conceptversion where conceptid=nConceptId;
                 update structureelementversion sev set sev.elementversionid = nNewConceptId where sev.structureid=pContextId and sev.elementid=relatedConceptId_array(i);
                end if;
            end Loop;
            
           
            
            -- clean up the context itself
            select elementid into pContextElementId from elementversion where elementversionid=pContextId;
            select count(1) into nCount from elementversion where elementid=pContextElementId and status='ACTIVE';
            update elementversion ev set ev.status='REMOVED' where ev.elementversionid = pContextId;
            
            update structureversion sv set sv.contextstatus='DELETED' where structureid=pContextId;
           
            if nCount=1 then -- last context version removed, change element uuid
               update element e set e.elementuuid = dbms_random.string('x', 30) where elementid=pContextElementId;
            End if;
            
       END removeContext;
       
    /**************************************************************************************************************************************
    * NAME:          removeConcept
    * DESCRIPTION:   When removing a concept: -- Detach the concept version from the context - detach all its property versions from context - if the concept
	  * was created in the context - for all property and concept versions -- if created in the context ---- update status to remove
	  *   -- if 0 versions for the element ---- remove element
    **************************************************************************************************************************************/
       PROCEDURE removeConcept(pContextId in NUMBER, pElementId in NUMBER)  IS
             relatedConceptId_array dbms_sql.Number_Table;
       BEGIN
            -- save all the ACTIVE concept elementId in this context which is related to the concept being deleted
            select cpv.domainElementid BULK COLLECT into relatedConceptId_array from conceptpropertyversion cpv, conceptversion cv, structureelementversion sev1, structureelementversion sev 
            where cpv.conceptpropertyid = sev.elementversionId and sev.structureid = pContextId and cpv.rangeelementid=pElementId 
            and cpv.domainelementid=cv.elementid and cv.conceptid=sev1.elementversionid and sev1.structureid=pContextId and cv.status='ACTIVE';
                   
            -- recursivly delete all the child concepts
            for i in 1..relatedConceptId_array.count
            Loop
                removeConcept(pContextId, relatedConceptId_array(i));
            end Loop;
            
            removeConceptSelf(pContextId, pElementId);
            
       END removeConcept;
       
       /****************************************************************************************************
       *   remove the concept if self
       ****************************************************************************************************/
       
       PROCEDURE removeConceptSelf(pContextId in NUMBER, pElementId in NUMBER)  IS
             nCount NUMBER;
             nConceptId NUMBER;
             nNewConceptId NUMBER;
       BEGIN
            select sev.elementversionid into nConceptId from structureelementversion sev where sev.structureid=pContextId and sev.elementid=pElementId;
            select count(1) into nCount from elementversion ev where ev.originatingcontextid=pContextId and elementid=pElementId;
            
            if nCount=1 then -- created in the same context
                 update conceptversion set status='REMOVED' where conceptid = nConceptId;
                 update elementversion ev set ev.status='REMOVED' where ev.elementversionid=nConceptId;
                 update element e set e.elementuuid = dbms_random.string('x',30) where e.elementid=pElementId;
            else
                 select elementversionid_seq.nextval into nNewConceptId from dual;
                 insert into elementversion select nNewConceptId, elementid, versioncode, sysdate, 'REMOVED', '', classid, elementversionid, pContextId from elementversion where elementversionid=nConceptId;
                 insert into conceptversion select nNewConceptId, classid, 'REMOVED', elementid from conceptversion where conceptid=nConceptId;
                 update structureelementversion sev set sev.elementversionid = nNewConceptId where sev.structureid=pContextId and sev.elementid=pElementId;
            end if;
            
       END removeConceptSelf;
       
    /**************************************************************************************************************************************
    * NAME:          createElementVersionInContext
    * DESCRIPTION:   Returns the elementversionID for the given contextId and elementId
    **************************************************************************************************************************************/
    PROCEDURE createElementVersionInContext(pContextId in NUMBER,pElementId in NUMBER, pChangedFromVersionId in NUMBER, pElementVersionId out NUMBER)  IS
            nClasssId       NUMBER;
            sVersionCode   VARCHAR2(30) ;
       BEGIN
          select elementversionid_seq.nextval into pElementVersionId from dual;
          select classid into nClasssId from element where elementid=pElementId;
          select versioncode into sVersionCode from elementversion where elementversionid=pContextId;
          
          insert into ELEMENTVERSION(
                 ELEMENTVERSIONID,
                 ELEMENTID,
                 VERSIONCODE,
                 VERSIONTIMESTAMP,
                 STATUS,
                 NOTES,
                 CLASSID,
                 CHANGEDFROMVERSIONID,
                 ORIGINATINGCONTEXTID )
           values ( pElementVersionId,
                 pElementId,
                 sVersionCode,
                 SYSDATE,
                 'ACTIVE',
                 null,
                 nClasssId,
                 pChangedFromVersionId,
                 pContextId
          	);
            delete from structureelementversion where structureid=pContextId and elementId=pElementId;
            insert into structureelementversion (elementversionid, elementid, structureid) values ( pElementVersionId, pElementId, pContextId);
         
       END createElementVersionInContext;
    
end cims_framework;
/
