CREATE OR REPLACE PACKAGE CIMS_INCOMPLETE IS
    TYPE ref_cursor IS REF CURSOR;

    FUNCTION checkTabularConcept(pContextId NUMBER, pConceptId NUMBER, pIsVersionYear char)  RETURN varchar2;
    FUNCTION checkIndexConcept(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2;
    FUNCTION checkSupplementConcept(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2;
    FUNCTION checkAttribute(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2;
    FUNCTION checkComponent(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2;
    FUNCTION verifycheckRU048_52(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2) RETURN VARCHAR2;
    FUNCTION verifycheckIndexRU125(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2) RETURN VARCHAR2;

END CIMS_INCOMPLETE;
/
CREATE OR REPLACE PACKAGE BODY CIMS_INCOMPLETE IS


    FUNCTION getTextRefsInAllOpenContexts(pContextId NUMBER, pText VARCHAR2 ) RETURN NUMBER
    IS
      cnt number := 0;
      cnt1 number := 0;
      vText textpropertyversion.text%type;
    BEGIN
        select count(*) into cnt
        from
        xmlpropertyversion tr, structureelementversion sv, structureversion v
        where
        contains(tr.xmltext, substr (pText, 1, decode(instr(pText,  '-'),0 ,length(pText), instr(pText,  '-')-1))  ) > 0
          and tr.xmltext like '%' || pText || '%'
          and tr.xmlpropertyid=sv.elementversionid
          and sv.structureid = v.structureid
          and v.contextstatus = 'OPEN'
          and cims_util.getConceptStatus(tr.domainelementid, sv.structureid) != 'REMOVED'
          and v.basestructureid is not null;

    with strelementversion as
        (
          select elementversionid, elementid from structureelementversion where structureid=pContextId
          UNION  ALL
          select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
            and not exists (
              select elementid from structureelementversion cv where cv.structureid=pContextId
              and cv.elementid = sv.elementid
            ))
        select count(*) into cnt1
        from
        xmlpropertyversion tr, strelementversion sv
        where
        contains(tr.xmltext, substr (pText, 1, decode(instr(pText,  '-'),0 ,length(pText), instr(pText,  '-')-1))  ) > 0
          and tr.xmltext like '%' || pText || '%'
          and tr.xmlpropertyid=sv.elementversionid
          and cims_util.getConceptStatus(tr.domainelementid, pContextId) != 'REMOVED';


       return cnt+cnt1;
    END;

    FUNCTION getIndexXMLRefCntInAllOpenCCtx(pContextId NUMBER, indexConceptId VARCHAR2  ) RETURN NUMBER
    IS
      cnt number := 0;
      cnt1 number := 0;
      vText textpropertyversion.text%type;
    BEGIN
        select count(*) into cnt
        from
        xmlpropertyversion tr, structureelementversion sv, structureversion v
        where
        contains(tr.xmltext, indexConceptId ) > 0
          and tr.xmltext like '%/' || indexConceptId || '<%'
          and tr.xmlpropertyid=sv.elementversionid
          and sv.structureid = v.structureid
          and v.contextstatus = 'OPEN'
          and cims_util.getConceptStatus(tr.domainelementid, sv.structureid) != 'REMOVED'
          and v.basestructureid is not null;


    with strelementversion as
        (
          select elementversionid, elementid from structureelementversion where structureid=pContextId
          UNION  ALL
          select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
            and not exists (
              select elementid from structureelementversion cv where cv.structureid=pContextId
              and cv.elementid = sv.elementid
            ))
        select count(*) into cnt1
        from
        xmlpropertyversion tr, strelementversion sv
        where
        contains(tr.xmltext, indexConceptId ) > 0
          and tr.xmltext like '%/' || indexConceptId || '<%'
          and tr.xmlpropertyid=sv.elementversionid
          and cims_util.getConceptStatus(tr.domainelementid, pContextId) != 'REMOVED'
          ;

       return cnt+cnt1;
    END;

-- this method name would be better as checkRU025CategoryOrGroup
      PROCEDURE checkRU025Tabular(pContextId NUMBER, pConceptId NUMBER, rules IN OUT varchar2)IS
         cnt number:=0;
         vCode TextPropertyversion.text%TYPE;
         vChildCode TextPropertyversion.text%TYPE;
         vParentCode TextPropertyversion.text%TYPE;
         range1 VARCHAR2(3) := '';
         range2 VARCHAR2(3) := '';
         crange1 VARCHAR2(3) := '';
         crange2 VARCHAR2(3) := '';
         children_cursor ref_cursor;
         peer_cursor ref_cursor;
       BEGIN
         -- applies only if the tabular concept is an active Category or Group  that has been created or activated
         select count(*) into cnt from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and cv.classid in (select classid from class where classname in ('Category', 'Group'))
         and sev.structureid = pContextId
         and cv.elementid = pConceptId
         ;
         if cnt < 1 then
           return;
         end if;

         --1.1 if category/group has parent block the code is included in the parent range
         vParentCode := cims_util.getParentCode(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Block');
         vChildCode := substr(replace(cims_util.getTextProperty(elemId => pConceptId, classnme => 'Code', strid => pContextId, language => null),'.',''), 1,3);
         IF vParentCode is not null then
           select substr(vParentCode, 1,3) , nvl(substr(vParentCode, 5,3),substr(vParentCode, 1,3))  into crange1, crange2 from dual;
         IF vChildCode not between crange1 and crange2 THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end if;

       END;
-- this method name would be better as checkRU025Block
       PROCEDURE checkRU025(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, rules IN OUT varchar2)IS
         cnt number:=0;
         vCode TextPropertyversion.text%TYPE;
         vChildCode TextPropertyversion.text%TYPE;
         vParentCode TextPropertyversion.text%TYPE;
         range1 VARCHAR2(3) := '';
         range2 VARCHAR2(3) := '';
         crange1 VARCHAR2(3) := '';
         crange2 VARCHAR2(3) := '';
         children_cursor ref_cursor;
         peer_cursor ref_cursor;

       BEGIN
         -- applies only if the tabular concept is an active block and either block code changed or the concept has been activated
         IF pClassname != 'Block' then
           return;
         end if;

         select sum(no) into cnt from (
         select count(*) no from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and cv.classid in (select classid from class where classname = 'Block')
         and sev.structureid = pContextId
         and cv.conceptid = pConceptId
         UNION ALL
         select count(*) no from textpropertyversion tv, structureelementversion sev
         where tv.status='ACTIVE' and tv.textpropertyid = sev.elementversionid
         and tv.classid in (select classid from class where classname = 'Code')
         and sev.structureid = pContextId
         and tv.domainelementid = pConceptId
         );
         if cnt < 1 then
           return;
         end if;


         vCode := cims_util.getTextProperty(elemId => pConceptId,classnme => 'Code', strid => pContextId, language => null);
         select substr(vCode, 1,3) , nvl(substr(vCode, 5,3),substr(vCode, 1,3))  into range1, range2 from dual;


         -- 1. if the block has active block children check that the parent range includes all child ranges
         children_cursor := cims_util.getActiveChildrenCodes(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Block');
         loop
         fetch children_cursor into vChildCode;
         exit when children_cursor%NOTFOUND;
         select substr(vChildCode, 1,3) , nvl(substr(vChildCode, 5,3),substr(vChildCode, 1,3))  into crange1, crange2 from dual;
         IF (crange1 not between range1 and range2) OR (crange2 not between range1 and range2) THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end loop;
         --1.1 if block has parent block the range is included in the parent
         vParentCode := cims_util.getParentCode(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Block');
         IF vParentCode is not null then
           select substr(vParentCode, 1,3) , nvl(substr(vParentCode, 5,3),substr(vParentCode, 1,3))  into crange1, crange2 from dual;
         IF (range1 not between crange1 and crange2) OR (range2 not between crange1 and crange2) THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end if;

         -- 2. check that the range does not overlap with any peer
         -- need peer ranges
         peer_cursor := cims_util.getActivePeersCodes(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Block');
         loop
         fetch peer_cursor into vChildCode;
         exit when peer_cursor%NOTFOUND;
         select substr(vChildCode, 1,3) , nvl(substr(vChildCode, 5,3),substr(vChildCode, 1,3))  into crange1, crange2 from dual;
         --Dbms_Output.put_line('crange1: ' || crange1 || ' crange2: ' || crange2);
         --rules:=rules || 'crange1: ' || crange1 || ' crange2: ' || crange2;
         IF (crange1 between range1 and range2) OR (crange2 between range1 and range2) THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end loop;

         -- 3. if there are children that are icd categories or cci groups they should be included in the range
         -- need all children category codes or cci group codes
         children_cursor := cims_util.getActiveChildrenCodes(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Group');
         if children_cursor is not null then
         loop
         fetch children_cursor into vChildCode;
         exit when children_cursor%NOTFOUND;
         vChildCode := substr(replace(vChildCode,'.',''), 1,3);
         IF (vChildCode not between range1 and range2)THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end loop;
         END IF;

         children_cursor := cims_util.getActiveChildrenCodes(pContextId => pContextId, pConceptId => pConceptId, pClasName => 'Category');
         if children_cursor is not null then
         loop
         fetch children_cursor into vChildCode;
         exit when children_cursor%NOTFOUND;
         IF (vChildCode not between range1 and range2)THEN
                    rules:=rules || 'RU025,' ; RETURN;
         END IF;
         end loop;
         END IF;


         rules:=rules ;
    exception
        when others then
            raise_application_error(-20011, ' cims_incomplete.checkRU025 Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END;

       -- TO BE MOVED IN CIMS_UTIL
       FUNCTION getTextPropertyCountforText(pContextId NUMBER, pLanguage VARCHAR2, pClassname VARCHAR2, pText VARCHAR2) RETURN NUMBER
       IS
         cnt NUMBER := 0;
         clid NUMBER;
       BEGIN
            select c.classid into clid from class c, structureversion s, class c2
            where c.classname = pClassname
            and c.baseclassificationname = c2.baseclassificationname
            and c2.classid = s.classid
            and s.structureid = pContextId
            ;

            with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )select count(*) cnt into cnt
         from textpropertyversion tp, strelementversion sev
         where tp.textpropertyid = sev.elementversionid
         and tp.classid = clid
         and trim(tp.text) = trim(pText)
         and tp.languagecode = pLanguage
         ;

         return cnt;
   exception
        when others then
          raise_application_error(-20011, ' cims_incomplete.checkRU111 Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END;


       PROCEDURE checkRU020(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, rules IN OUT varchar2)IS
         longTitleENG TEXTPROPERTYVERSION.TEXT%TYPE;
         longTitleFRA TEXTPROPERTYVERSION.TEXT%TYPE;
         cnt NUMBER := 0;
       BEGIN
         -- applies only if the tabular concept is an active block
         if pClassname != 'Block' OR pStatus != 'ACTIVE' then
           return;
         end if;

         -- check that there is no other one active block with the same long title in the context
        longTitleENG:=trim(cims_util.getTextProperty( pConceptId , 'LongTitle', pContextId, 'ENG'));
        cnt:= getTextPropertyCountforText(pContextId ,'ENG' ,'LongTitle' , longTitleENG);
        if cnt > 1 then
            rules:=rules || 'RU020, ';
            return;
         end if;
        longTitleFRA:=trim(cims_util.getTextProperty( pConceptId , 'LongTitle', pContextId, 'FRA'));
        cnt:= getTextPropertyCountforText(pContextId ,'FRA' ,'LongTitle' , longTitleFRA);
         if cnt > 1 then
            rules:=rules || 'RU020, ';
         end if;
       END;

       PROCEDURE checkRU111(pContextId NUMBER, pConceptId NUMBER, rules IN OUT varchar2)IS
         cnt NUMBER := 0;
         peer_cursor ref_cursor;
       BEGIN
         -- applies to all tabular concepts
         -- check that short, long and user titles are provided for the concept in both languages

         WITH classes AS (
              SELECT classid from CLASS c
              where c.classname in ( 'LongTitle', 'ShortTitle', 'UserTitle')
         )
         ,strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         select count(*) into cnt
         from textpropertyversion tpv, strelementversion sev, classes c
         where tpv.textpropertyid = sev.elementversionid
         and   tpv.classid = c.classid
         and trim(tpv.text) is not null
         and tpv.domainelementid = pConceptId
         ;

         IF cnt != 6 THEN
            rules:=rules || 'RU111, ';
         END IF;
   exception
        WHEN NO_DATA_FOUND then
          raise_application_error(-20011, ' cims_incomplete.checkRU111 Error: Illegal tabular concept! ' || substr(sqlerrm, 1, 512));
        when others then
          raise_application_error(-20011, ' cims_incomplete.checkRU111 Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END;

    /**************************************************************************************************************************************
    * NAME:          UC29 RU026
    * DESCRIPTION:   Implements RU026
    **************************************************************************************************************************************/
       PROCEDURE checkRU026(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         vcnt NUMBER := 0;
         peer_cursor ref_cursor;
         vClassid Class.Classid%Type;
         vCurrentClassid Class.Classid%Type;
       BEGIN
         -- applies only to newly created tabular concepts
         WITH tabclasses AS (
              SELECT classid from CLASS c
              where c.classname in ( 'Block', 'Category', 'Group', 'Rubric','CCICODE')
         )
         select count(*) into vcnt from conceptversion cv, structureelementversion sev, elementversion ev, tabclasses c
         where cv.elementid = pConceptId
         and sev.elementversionid = cv.conceptid
         and sev.structureid = pContextId
         and ev.elementversionid = cv.conceptid
         and cv.classid = c.classid
         and ev.changedfromversionid is null
         ;
         IF vcnt < 1 THEN
           RETURN;
         END IF;

         select classid into vCurrentClassid from element where elementid = pConceptId;

         -- check that sibling concepts have the same hierarchy class
         peer_cursor := cims_util.getActivePeersClasses(pContextId => pContextId, pConceptId => pConceptId);
         loop
         fetch peer_cursor into vClassid;
         exit when peer_cursor%NOTFOUND;
         IF vClassid != vCurrentClassid then
            pRules:=pRules || 'RU026, ';
           return;
         END IF;
         end loop;

        prules:=prules;
       END;

       PROCEDURE checkRU110(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)
       IS
       BEGIN
         -- applies to tabular concepts that have been disabled or REMOVED
         IF pStatus != 'DISABLED'   THEN
           RETURN;
          END IF;
         -- 1. check that there are no active children
        if cims_util.hasActiveChildren(pContextId => pContextId, pConceptId => pConceptId) = 'Y' THEN
           pRules:=pRules || 'RU110, ';
           return;
        end if;
         -- 2. if concept is tabular block or bellow, check that there are no active XMLs that contain the code
         IF pClassname in ('Block','Category', 'Group', 'Rubric', 'CCICODE') THEN
         IF cims_util.getReferenceCount(pContextId => pContextId, pText =>
                      cims_util.getTextProperty(elemId => pConceptId, classnme => 'Code', strid => pContextId, language => null)) > 0 THEN
           pRules:=pRules || 'RU110, ';
           return;
        end if;
        end if;
       END;

       PROCEDURE checkIndexRU110(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)
       IS
       BEGIN
         -- applies to index concepts that have been disabled or REMOVED
         IF pStatus != 'DISABLED'   THEN
           RETURN;
          END IF;
         -- 1. check that there are no active children
        if cims_util.hasActiveChildren(pContextId => pContextId, pConceptId => pConceptId) = 'Y' THEN
           pRules:=pRules || 'RU110, ';
           return;
        end if;
         -- 2. if concept is an Index term, check that there are no active XMLs that contain the index term
         IF pClassname in ('AlphabeticIndex','ExternalInjuryIndex', 'NeoplasmIndex', 'DrugsAndChemicalsIndex', 'Index') THEN
          -- search all indexRefDefinition xml in the same index book
          IF cims_util.isIndexReferredInXML(pContextId => pContextId, indexConceptId => pConceptId) > 0 THEN
            pRules:=pRules || 'RU110, ';
           return;
        end if;
        end if;
       END;

       PROCEDURE checkSupplementRU110(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)
       IS
       BEGIN
         -- applies to index concepts that have been disabled or REMOVED
         IF pStatus != 'DISABLED'   THEN
           RETURN;
          END IF;
         -- 1. check that there are no active children
        if cims_util.hasActiveChildren(pContextId => pContextId, pConceptId => pConceptId) = 'Y' THEN
           pRules:=pRules || 'RU110, ';
           return;
        end if;
       END;



       PROCEDURE checkRU125(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         vcnt NUMBER := 0;
       BEGIN
         -- applies tabular concepts with status REMOVE
         IF pStatus != 'REMOVED'   THEN RETURN; END IF;

         -- 1. check in base context and in all open contexts(including contextId) that there are no  ConceptProperties versions where the rangeElementId = conceptId
         select count(*) into vcnt from conceptpropertyversion cpv
         join structureelementversion sev on cpv.conceptpropertyid = sev.elementversionid
         join structureversion s on sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 where cpv.rangeelementid = pConceptId
               and not exists (select * from conceptversion cv, structureelementversion sev1 where sev1.structureid = pContextId
				       and sev1.elementversionid=cv.conceptid and cv.elementid = cpv.domainelementid and cv.status = 'REMOVED');
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         -- 2. check that there are no open change requests modifying the  concept or its properties other than contextId(see CommomElementOperations.doesConceptHasNoChangesElsewhere)
            -- 2.1 check if the concept itself is being changed elsewhere
         select count(*) into vcnt from structureelementversion sev
         join structureversion s on sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid = pConceptId;
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         		-- 2.2 check if any properties of the concept are being changed elsewhere
         select count(*) into vcnt from structureelementversion sev join structureversion s on
				 sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid in (select distinct elementid from propertyversion where domainelementid = pConceptId);
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         -- 3. if concept is tabular block or bellow, check that there are no XMLs that contain the code in all open contextx
         IF pClassname in ('Block','Category', 'Group', 'Rubric', 'CCICODE') THEN
         IF getTextRefsInAllOpenContexts(pContextId, cims_util.getTextProperty(elemId => pConceptId, classnme => 'Code', strid => pContextId, language => null)) > 0 THEN
           pRules:=pRules || 'RU125, ';
           return;
        end if;
        end if;

         prules:=prules;
       END;


       PROCEDURE checkIndexRU125(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         vcnt NUMBER := 0;
       BEGIN
         -- applies index concepts with status REMOVE
         IF pStatus != 'REMOVED'   THEN RETURN; END IF;

         -- 1. check in base context and in all open contexts(including contextId) that there are no
         -- ConceptProperties versions where the rangeElementId = conceptId
         select count(*) into vcnt from conceptpropertyversion cpv
         join structureelementversion sev on cpv.conceptpropertyid = sev.elementversionid
         join structureversion s on sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 where cpv.rangeelementid = pConceptId
               and not exists (select * from conceptversion cv, structureelementversion sev1 where sev1.structureid = pContextId
				       and sev1.elementversionid=cv.conceptid and cv.elementid = cpv.domainelementid and cv.status = 'REMOVED');
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         -- 2. check that there are no open change requests modifying the  concept or its properties other than contextId(see CommomElementOperations.doesConceptHasNoChangesElsewhere)
            -- 2.1 check if the concept itself is being changed elsewhere
         select count(*) into vcnt from structureelementversion sev
         join structureversion s on sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid = pConceptId;
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         		-- 2.2 check if any properties of the concept are being changed elsewhere
         select count(*) into vcnt from structureelementversion sev join structureversion s on
				 sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid in (select distinct elementid from propertyversion where domainelementid = pConceptId);
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         -- 3. Check if there are any XMLs that contain references to the index in any open contexts
         IF pClassname in ('AlphabeticIndex','ExternalInjuryIndex', 'NeoplasmIndex', 'DrugsAndChemicalsIndex', 'Index') THEN
          IF getIndexXMLRefCntInAllOpenCCtx(pContextId, pConceptId) > 0 THEN
             pRules:=pRules || 'RU125, ';
             return;
           end if;
        end if;

         prules:=prules;
       END;
       FUNCTION verifycheckIndexRU125(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2) RETURN VARCHAR2 IS
         rules VARCHAR2(2000);
       BEGIN
         checkIndexRU125(pContextId , pConceptId , pClassname , pStatus, rules);


         return rules;
       END;


       PROCEDURE checkSupplementRU125(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         vcnt NUMBER := 0;
       BEGIN
         -- applies index concepts with status REMOVE
         IF pStatus != 'REMOVED'   THEN RETURN; END IF;

         -- 2. check that there are no open change requests modifying the  concept or its properties other than contextId(see CommomElementOperations.doesConceptHasNoChangesElsewhere)
            -- 2.1 check if the concept itself is being changed elsewhere
         select count(*) into vcnt from structureelementversion sev
         join structureversion s on sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid = pConceptId;
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         		-- 2.2 check if any properties of the concept are being changed elsewhere
         select count(*) into vcnt from structureelementversion sev join structureversion s on
				 sev.structureid = s.structureid and s.contextstatus = 'OPEN'
				 and s.basestructureid is not null and s.structureid != pContextId
				 where sev.elementid in (select distinct elementid from propertyversion where domainelementid = pConceptId);
         if vcnt > 0 then
            pRules:=pRules || 'RU125, '; return;
         end if;

         prules:=prules;
       END;



       PROCEDURE checkRU182(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)
       IS
         cnt number := 0;
         vBaseContextId number := 0;
         vOldCode textpropertyversion.Text%type;
       BEGIN
         -- check that the concept is newly added and return;
         select count(*) into cnt from structureelementversion sev, elementversion ev
         where sev.elementversionid = ev.elementversionid
         and sev.structureid = pContextId
         and ev.elementid = pConceptId
         and ev.changedfromversionid is null;
         if (cnt > 0)then
            return;
         end if;

         -- applies only to active block concepts that have changed the code
         if pClassname != 'Block' OR pStatus != 'ACTIVE' THEN return; END IF;

         select sv.basestructureid into vBaseContextId from structureversion sv where sv.structureid = pContextId;
         vOldCode := cims_util.getTextProperty(elemId =>pConceptId, classnme => 'Code', strid => vBaseContextId, language => null);
         if  nvl(vOldCode, 'X') = cims_util.getTextProperty(elemId =>pConceptId, classnme => 'Code', strid => pContextId, language => null) then
         return;
         end if;

         select count(*) into cnt from textpropertyversion tv, structureelementversion sev
         where tv.status='ACTIVE' and tv.textpropertyid = sev.elementversionid
         and tv.classid in (select classid from class where classname = 'Code')
         and sev.structureid = pContextId
         and tv.domainelementid = pConceptId
         ;
         IF cnt < 1 then return; end if;

         -- check that there are no active XMLs that contain the previous code
--         IF cims_util.getReferenceCount(pContextId => pContextId, pText =>
--                      cims_util.getTextProperty(elemId => pConceptId, classnme => 'Code', strid => pContextId, language => null)) > 0 THEN
         IF cims_util.getReferenceCount(pContextId => pContextId, pText => vOldCode) > 0 THEN
           pRules:=pRules || 'RU182, ';
           return;
        end if;

       END;
       PROCEDURE checkRU117(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         vcnt number := 0;
         vValidationCCICPVClassId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCICPV');
         vValidationDefinitionClassId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationDefinition');
       BEGIN
         -- applies to active tabular concepts that have changed status(to ACTIVE) or concepts that are newly created
         select count(*) into vcnt from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and sev.structureid = pContextId
         and cv.elementid = pConceptId
         ;
         if vcnt < 1 then
           return;
         end if;

         -- check that the parent is ACTIVE
         if cims_util.hasActiveParent(pConceptId => pconceptId, pContextId => pcontextId)='N' then
           pRules:=pRules || 'RU117, ';
           return;
         end if;
         -- for CCI concepts check that the components are active
         with classes as (
               select classid, classname from class where classname
               in ('InvasivenessLevelIndicator','ApproachTechniqueCPV','DeviceAgentCPV','InterventionCPV','GroupCompCPV')
               )
         ,strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         ,strelementversion1 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         select count(*) into vcnt
         from conceptpropertyversion cpv, classes c, conceptversion cv, strelementversion sev, strelementversion1 sev1
         where domainelementid = pConceptId
         and cpv.classid = c.classid
         and cv.elementid = cpv.rangeelementid
         and cv.conceptid = sev1.elementversionid
         and cpv.conceptpropertyid = sev.elementversionid
         and cv.status != 'ACTIVE';
         if vcnt > 0 then
           pRules:=pRules || 'RU117, ';
           return;
         end if;

         -- if there are validation rules on the concept check that they refer to active auxiliary tables value
         with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         , strelementversion1 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         , strelementversion2 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         , strelementversion3 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         , strelementversion4 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         , validations as(
           select vrule.* from conceptpropertyversion cpv, conceptversion vrule, strelementversion3 sev, strelementversion4 sev1
           where
           cpv.rangeelementid = pConceptId
           and cpv.classid = vValidationCCICPVClassId
           and vrule.elementid = cpv.domainelementid
           and vrule.status = 'ACTIVE'
           and vrule.conceptid = sev1.elementversionid
           and cpv.conceptpropertyid = sev.elementversionid
         )
         select count(*) into vcnt from (
         select ref, cims_util.isRefAttributeActive(pContextId , ref) isActive from (
                select distinct substr(to_Char(REGEXP_substr (xpv.xmltext, '<STATUS_REF>S([a-zA-Z0-9][a-zA-Z0-9])</STATUS_REF>')), 13, 3) ref
                from xmlpropertyversion xpv , strelementversion sev, validations v
                where xpv.domainelementid = v.elementid
                and xpv.classid = vValidationDefinitionClassId
                and xpv.xmlpropertyid = sev.elementversionid
         UNION ALL
               select  distinct substr(to_Char(REGEXP_substr (xpv.xmltext, '<LOCATION_REF>(L|M)([a-zA-Z0-9][a-zA-Z0-9])</LOCATION_REF>')), 15, 3) ref
               from xmlpropertyversion xpv  , strelementversion1 sev, validations v
               where xpv.domainelementid = v.elementid
               and xpv.classid = vValidationDefinitionClassId
               and xpv.xmlpropertyid = sev.elementversionid
                and v.elementid = xpv.domainelementid
         UNION ALL
               select distinct substr(to_Char(REGEXP_substr (xpv.xmltext, '<EXTENT_REF>E([a-zA-Z0-9][a-zA-Z0-9])</EXTENT_REF>')), 13, 3) ref
               from xmlpropertyversion xpv , strelementversion2 sev , validations v
               where xpv.domainelementid = v.elementid
               and xpv.classid = vValidationDefinitionClassId
               and xpv.xmlpropertyid = sev.elementversionid
                and v.elementid = xpv.domainelementid
         )
         where ref is not null)
         where isActive = 'N';

         if vcnt > 0 then
           pRules:=pRules || 'RU117, ';
           return;
         end if;

         prules:=prules;
       END;

       PROCEDURE checkIndexRU117(pContextId NUMBER, pConceptId NUMBER, pRules IN OUT varchar2)IS
         vcnt number := 0;
       BEGIN
         -- applies to active index concepts that have changed status(to ACTIVE) or concepts that are newly created
         select count(*) into vcnt from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and sev.structureid = pContextId
         and cv.elementid = pConceptId
         ;
         if vcnt < 1 then
           return;
         end if;

         -- check that the parent is ACTIVE
         if cims_util.hasActiveParent(pConceptId => pconceptId, pContextId => pcontextId)='N' then
           pRules:=pRules || 'RU117, ';
           return;
         end if;

         prules:=prules;
       END;

       PROCEDURE checkSupplementRU117(pContextId NUMBER, pConceptId NUMBER, pRules IN OUT varchar2)IS
         vcnt number := 0;
       BEGIN
         -- applies to active index concepts that have changed status(to ACTIVE) or concepts that are newly created
         select count(*) into vcnt from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and sev.structureid = pContextId
         and cv.elementid = pConceptId
         ;
         if vcnt < 1 then
           return;
         end if;

         -- check that the parent is ACTIVE
         if cims_util.hasActiveParent(pConceptId => pconceptId, pContextId => pcontextId)='N' then
           pRules:=pRules || 'RU117, ';
           return;
         end if;

         prules:=prules;
       END;

    /**************************************************************************************************************************************
    * NAME:          UC27 RU061
    * DESCRIPTION:   Implements RU061
    **************************************************************************************************************************************/
       PROCEDURE checkIndexRU061(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         peer_cursor ref_cursor;
         vIndexDesc TextPropertyversion.text%TYPE;
         vCurrentIndexDesc TextPropertyversion.text%TYPE;
       BEGIN
          IF pStatus != 'ACTIVE'   THEN
           RETURN;
          END IF;

         vCurrentIndexDesc := cims_util.getTextProperty(elemId => pConceptId, classnme => 'IndexDesc', strid => pContextId, language => null);

         -- check that the index lead term or term descriptions must be unique among its sibling index terms.
         peer_cursor := cims_util.getActivePeersIndexDesc(pContextId => pContextId, pConceptId => pConceptId, pClasName =>  pClassname);
         loop
         fetch peer_cursor into vIndexDesc;
         exit when peer_cursor%NOTFOUND;
         IF vIndexDesc = vCurrentIndexDesc then
            pRules:=pRules || 'RU061, ';
           return;
         END IF;
         end loop;

         prules:=prules;
       END;


       FUNCTION getValidationRuleTabConceptId (pContextId number, pVRConceptId number) RETURN NUMBER
       IS
        vConceptId number:= null;
        vClassValidationCCICPVId Class.Classid%type := cims_util.getClassIDForClassName('CCI', 'ValidationCCICPV');
        vClassValidationICDCPVId Class.Classid%type := cims_util.getClassIDForClassName('ICD-10-CA', 'ValidationICDCPV');
       BEGIN
            with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
            )
            select cpv.rangeelementid into vConceptId from conceptpropertyversion cpv, strelementversion sev
            where cpv.domainelementid = pVRConceptId
            and cpv.conceptpropertyid = sev.elementversionid
            and cpv.classid in (vClassValidationCCICPVId, vClassValidationICDCPVId)
            ;

         return vConceptId;
         exception when no_data_found then
           return null;
       END;


       -- A code and data holding combination should not have mutliple validations set (either configured for the code or inherited from an ancestor)
       PROCEDURE checkRU048_52(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2, pRules IN OUT varchar2)IS
         cnt number := 0;
         vdad number:= 0;
         vnacrs number := 0;
       BEGIN
         -- applies to activated or newly created Rubric, CCICODE, Category concepts or concepts
         select count(*) into cnt from conceptversion cv, structureelementversion sev
         where cv.status='ACTIVE' and cv.conceptid = sev.elementversionid
         and cv.classid in (select classid from class where classname in ('Category', 'Rubric', 'CCICODE'))
         and sev.structureid = pContextId
         and cv.elementid = pConceptId
         ;

          -- or applies to concepts where validation rules were added or disabled
         -- check if any cpv version
         IF cnt=0 then
         select count(*) into cnt
         from structureelementversion sev, conceptpropertyversion cpv
         where sev.structureid = pContextId
         and cpv.conceptpropertyid = sev.elementversionid
         and cpv.rangeelementid = pConceptId;
         END IF;
         -- check if any new version for the validation rule
         IF cnt = 0 then
         with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )
         select count(*) into cnt from conceptversion cv, structureelementversion sev, conceptpropertyversion cpv, strelementversion sev1
         where cv.conceptid = sev.elementversionid
         and cv.classid in (select classid from class where classname in ('ValidationCCI', 'ValidationICD'))
         and sev.structureid = pContextId
         and cpv.rangeelementid = pConceptId
         and cpv.domainelementid = cv.elementid
         and cpv.conceptpropertyid = sev1.elementversionid
         and cpv.classid in (select classid from class where classname in ('ValidationCCICPV', 'ValidationICDCPV'))
         ;
         END IF;
         if cnt < 1 then
           return;
         end if;
         -- check if above there are more than one rule per data holding
         with strelementversion as
         (
         select elementversionid, elementid
         from structureelementversion
         where structureid=pContextId
         UNION ALL
         select elementversionid, elementid
         from structureelementversion sv
         where sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
         and not exists (select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid )
        )
        , strelementversion1 as(
        select elementversionid, elementid
        from structureelementversion
        where structureid=pContextId
       UNION ALL
       select elementversionid, elementid
       from structureelementversion sv
       where sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
        and not exists ( select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid)
        )
       , narrowcpvs as (
         SELECT  cpv.conceptpropertyid cpvid, cims_util.getTextProperty(cpv.domainelementid, 'Code',pContextId,'') child
             , (select classid from element where elementid = cpv.domainelementid) childclass
             , cims_util.getConceptStatus(cpv.domainelementid,pContextId) childstatus
             , cpv.domainelementid childid
         from conceptpropertyversion cpv
         where
         cpv.classid in (select classid from class where classname = 'Narrower')
         connect by nocycle prior cpv.rangeelementid = cpv.domainelementid
         start with cpv.domainelementid = pConceptId
       )
       select sum(dad) dad, sum(nacrs) nacrs into vdad, vnacrs
       from(
              select distinct child
                     , decode(cims_util.hasActiveValidationRuleForDH(pContextId, childid, '1'),'Y',1,0) dad
                     , decode(cims_util.hasActiveValidationRuleForDH(pContextId, childid, 'A'),'Y',1,0) nacrs
              from narrowcpvs na, strelementversion sev
              where na.cpvid = sev.elementversionid
              and childclass in (select classid from class where classname in ('Category', 'Rubric', 'CCICODE'))
              and childstatus = 'ACTIVE'
       );

       -- CURRENTLY ASSUMING THAT RU052 will not be checked here
       -- there are missing validation rules looking above - only check for leaves
       /*
       IF ( vdad = 0 OR vnacrs=0) then
         IF cims_util.hasActiveChildren(pContextId, pConceptId)='N' THEN
                    pRules:=pRules || 'RU052, ';
         END IF;
       END IF;
       */

       -- TO DO to finish RU052
       -- If a validation rule was disabled in this request for this concept
       ---- determine all leaf children that have no changes in this request and for each:
       ---- invoke a new method that just checks 52 for leafs only - first one that fails

       IF pClassname = 'Rubric' or pClassname = 'Category' THEN
         IF cims_util.hasChildWithActiveValidRuleDH(pContextId,pConceptId,'A') = 'Y' THEN
           vnacrs := vnacrs +1;
         END IF;
         IF cims_util.hasChildWithActiveValidRuleDH(pContextId,pConceptId,'1') = 'Y' THEN
           vdad := vdad +1;
         END IF;
       END IF;
       IF ( (vdad > 1) OR (vnacrs > 1)) then
                    pRules:=pRules || 'RU048, ';
       END IF;
       
       -- Tiger for CIMS-37 RU0481
       if ( vdad=0 and vnacrs=0 ) then
          pRules:=pRules || 'RU0481, ';
       end if;
       -- End CIMS-37 RU0481
       
       END;

       FUNCTION verifycheckRU048_52(pContextId NUMBER, pConceptId NUMBER, pClassname VARCHAR2, pStatus VARCHAR2) RETURN VARCHAR2 IS
         rules VARCHAR2(2000);
       BEGIN
         checkRU048_52(pContextId , pConceptId , pClassname , pStatus, rules);

         return rules;
       END;

    /**************************************************************************************************************************************
    * NAME:          checkTabularConcept
    * DESCRIPTION:   Returns an array of incomplete rules that are not respected
    **************************************************************************************************************************************/
       FUNCTION checkTabularConcept(pContextId NUMBER, pConceptId NUMBER, pIsVersionYear char) RETURN varchar2 IS
         rules varchar2(4000);
         classname VARCHAR2(100);
         status VARCHAR2(10);
       BEGIN
       select c.classname into classname from element e, class c where e.elementId = pConceptId and c.classid = e.classid;

            with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )select status into status from conceptversion co, strelementversion sev
         where co.conceptid = sev.elementversionid
         and co.elementid = pConceptId
         ;


        -- verify all years rules
        IF pIsVersionYear = 'Y' THEN
         IF status != 'REMOVED' THEN
         checkRU111(pContextId,pConceptId, rules);
         checkRU020(pContextId,pConceptId,classname,status, rules);
         END IF;
        END IF;

        -- if version year verify version year rules
         IF status != 'REMOVED' THEN
         checkRU025Tabular(pContextId,pConceptId, rules);
         checkRU025(pContextId,pConceptId, classname, status, rules);
         checkRU026(pContextId,pConceptId, classname,status, rules);
         checkRU110(pContextId,pConceptId, classname, status, rules);
         checkRU182(pContextId,pConceptId, classname,status, rules);
         checkRU117(pContextId,pConceptId, classname,status, rules);
         checkRU048_52(pContextId,pConceptId, classname,status, rules);
         ELSE
         checkRU125(pContextId,pConceptId, classname,status, rules);
         END IF;
         return rules;
   exception
        WHEN NO_DATA_FOUND then
          raise_application_error(-20011, ' cims_incomplete.checkTabularConcept Error: Illegal tabular concept! ' || substr(sqlerrm, 1, 512));
        when others then
          raise_application_error(-20011, ' cims_incomplete.checkTabularConcept Error: Unexpected error! ' || substr(sqlerrm, 1, 512));

           return rules;

       END checkTabularConcept;

    /**************************************************************************************************************************************
    * NAME:          checkIndexConcept
    * DESCRIPTION:   Returns an array of incomplete rules that are not respected
    **************************************************************************************************************************************/
       FUNCTION checkIndexConcept(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2 IS
         rules varchar2(4000);
         classname VARCHAR2(100);
         status VARCHAR2(10);
       BEGIN
       select c.classname into classname from element e, class c where e.elementId = pConceptId and c.classid = e.classid;

        with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )select status into status from conceptversion co, strelementversion sev
         where co.conceptid = sev.elementversionid
         and co.elementid = pConceptId
         ;

        -- if version year verify version year rules
         IF status != 'REMOVED' THEN
           checkIndexRU061(pContextId, pConceptId, classname, status, rules);
           checkIndexRU110(pContextId, pConceptId, classname, status, rules);
           checkIndexRU117(pContextId, pConceptId, rules);
         ELSE
           checkIndexRU125(pContextId, pConceptId, classname, status, rules);
         END IF;
         return rules;
   exception
        WHEN NO_DATA_FOUND then
          raise_application_error(-20011, ' cims_incomplete.checkIndexConcept Error: Illegal index concept! ' || substr(sqlerrm, 1, 512));
        when others then
          raise_application_error(-20011, ' cims_incomplete.checkIndexConcept Error: Unexpected error! ' || substr(sqlerrm, 1, 512));

           return rules;

       END checkIndexConcept;

   /**************************************************************************************************************************************
    * NAME:          checkSupplementConcept
    * DESCRIPTION:   Returns an array of incomplete rules that are not respected
    **************************************************************************************************************************************/
       FUNCTION checkSupplementConcept(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2 IS
         rules varchar2(4000);
         classname VARCHAR2(100);
         status VARCHAR2(10);
       BEGIN
       select c.classname into classname from element e, class c where e.elementId = pConceptId and c.classid = e.classid;

        with strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = pContextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = pContextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = pContextId
                and cv.elementid = sv.elementid
            )
         )select status into status from conceptversion co, strelementversion sev
         where co.conceptid = sev.elementversionid
         and co.elementid = pConceptId
         ;

        -- if version year verify version year rules
        IF status != 'REMOVED' THEN
           checkSupplementRU110(pContextId, pConceptId, classname, status, rules);
           checkSupplementRU117(pContextId, pConceptId, rules);
         ELSE
           checkSupplementRU125(pContextId, pConceptId, classname, status, rules);
         END IF;

         return rules;
   exception
        WHEN NO_DATA_FOUND then
          raise_application_error(-20011, ' cims_incomplete.checkSupplementConcept Error: Illegal index concept! ' || substr(sqlerrm, 1, 512));
        when others then
          raise_application_error(-20011, ' cims_incomplete.checkSupplementConcept Error: Unexpected error! ' || substr(sqlerrm, 1, 512));

           return rules;

       END checkSupplementConcept;


       FUNCTION checkAttribute(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2 IS
         rules varchar2(4000);
       BEGIN
           RETURN 'RU111, RU222 ';
       END;

       FUNCTION checkComponent(pContextId NUMBER, pConceptId NUMBER) RETURN varchar2 IS
         rules varchar2(4000);
       BEGIN
           RETURN 'RU333, RU444 ';
       END;

END CIMS_INCOMPLETE ;
/
