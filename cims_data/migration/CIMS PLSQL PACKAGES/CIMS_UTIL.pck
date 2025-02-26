set define off
spool CIMS_UTIL.log

prompt
prompt Creating package CIMS_UTIL
prompt ==========================
prompt
CREATE OR REPLACE PACKAGE CIMS_UTIL IS
    TYPE ref_cursor IS REF CURSOR;
    FUNCTION getElementId(elemVersionId NUMBER) RETURN NUMBER;
    FUNCTION getElementVersionId(elemId NUMBER, context NUMBER) RETURN NUMBER;
    FUNCTION getConceptMajorType(pConceptid NUMBER) RETURN VARCHAR2;
    FUNCTION getChangedFromVersionId( pElementId number, pContextId number) return number;
    FUNCTION getContextYear( pContextId number) return VARCHAR2;
    FUNCTION getBaseVersionId( pElementId number, pContextId number) return number;
    FUNCTION getClassIDForClassName(bcName varchar2, cName varchar2) RETURN number;
    FUNCTION getClassNameForClassId(id NUMBER) RETURN VARCHAR2;
    FUNCTION getClassNameForElementId(pElementId NUMBER) RETURN VARCHAR2;
    FUNCTION getBaseClassifNameForElem(pElementId number) RETURN VARCHAR2;
    FUNCTION getBaseClassifIDForContextID(pStructureId number) RETURN NUMBER;
    FUNCTION getBaseClassificationVersionID(bcName varchar2, versCode VARCHAR2) RETURN number;
    FUNCTION getConceptStatus( pConceptId NUMBER, pContextId NUMBER) RETURN VARCHAR2;
    FUNCTION getTextProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) RETURN VARCHAR2;
    FUNCTION getNumericProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return VARCHAR2;
    FUNCTION getHTMLProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) return HTMLPROPERTYVERSION.HTMLTEXT%TYPE;
    FUNCTION getDomainPropertyCode(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return VARCHAR2;
    FUNCTION getClassificationRootId(bcName VARCHAR2) return VARCHAR2;
    FUNCTION getICDConceptChapterVersionId(elemId NUMBER, context NUMBER) RETURN NUMBER;
    FUNCTION searchNodesByCode(bcName VARCHAR2, versionCode IN VARCHAR2, nodeStatus VARCHAR2, searchString VARCHAR2, maxRows NUMBER, language VARCHAR2)
       RETURN CIMS_UTIL.ref_cursor;
    FUNCTION isValidCode(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR;
    FUNCTION hasChildren(pContextId NUMBER, pConceptId NUMBER, pStatus varchar2 DEFAULT 'ACTIVE,DISABLED,REMOVED') return CHAR;
    FUNCTION hasActiveChildren(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR;
    FUNCTION hasActiveParent(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR;
    FUNCTION hasActiveValidationRule(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR;
    FUNCTION hasActiveValidationRuleForDH(pContextId NUMBER, pConceptId NUMBER, pDHCode Varchar2 ) RETURN CHAR;
    FUNCTION hasChildWithActiveValidRule(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR;
    FUNCTION hasChildWithActiveValidRuleDH(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 ) RETURN CHAR;
    FUNCTION getReferenceCount(pContextId NUMBER, pText VARCHAR2 ) RETURN NUMBER;
    FUNCTION isIndexReferredInXML(pContextId NUMBER, indexConceptId VARCHAR2 ) RETURN NUMBER;
    FUNCTION getActiveChildrenCodes(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor;
    FUNCTION getActivePeersCodes(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor;
    FUNCTION getActivePeersIndexDesc(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor;
    FUNCTION getActivePeersClasses(pContextId NUMBER, pConceptId NUMBER) RETURN CIMS_UTIL.ref_cursor;
    FUNCTION getParentId(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN NUMBER;
    FUNCTION getParentCode(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN VARCHAR2;
    FUNCTION getValidationRuleDHCode (pContextId NUMBER, pConceptId NUMBER) RETURN VARCHAR2;
    FUNCTION getValidationRuleTabularId (pContextId NUMBER, pValidationId NUMBER) RETURN NUMBER;
    FUNCTION getValidationRuleDHDescription (pContextId NUMBER, pConceptId NUMBER, pLanguageCode VARCHAR2) RETURN VARCHAR2;
    FUNCTION getValidationRuleRefCodes(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 ) RETURN VARCHAR2;
    FUNCTION getChildValidationRuleRefCodes(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 )RETURN VARCHAR2;
    FUNCTION isRefAttributeMandatory(pContextId NUMBER, pAttributeCodeCode VARCHAR2) RETURN CHAR;
    FUNCTION isRefAttributeActive(pContextId NUMBER, pAttributeCodeCode VARCHAR2) RETURN CHAR;
    FUNCTION isCCIBlockLevel2(elemId NUMBER, contextId NUMBER) RETURN CHAR;
    FUNCTION isCCIBlockLevel1(elemId NUMBER, contextId NUMBER) RETURN CHAR;
    FUNCTION isCatDisplayedinTableAbove (pContextId number, pConceptId number, pLanguage varchar2) RETURN CHAR;
    FUNCTION getFormattedLongDescription(longDesc VARCHAR2, code VARCHAR2, codeplus VARCHAR2,langCode LANGUAGE.languagecode%TYPE, classification VARCHAR2, conceptClassid NUMBER, conceptStatus VARCHAR2 DEFAULT '')
    RETURN VARCHAR2;
    FUNCTION getContainerPage(parentConceptId NUMBER, childConceptId NUMBER, childClassId NUMBER, classification Class.Baseclassificationname%type, containerId NUMBER, pContextId NUMBER)
    RETURN NUMBER;
    FUNCTION hasSecondLevelChild(elemId NUMBER, contextId NUMBER, classification VARCHAR2) RETURN CHAR;

    FUNCTION retrieveContainingPagebyId(baseClassification varchar2, contextId number, elemId number) RETURN NUMBER;

    FUNCTION retrieveContainingIdPathbyCode(baseClassification varchar2, contextId number, cat_code varchar2) RETURN VARCHAR2;

    FUNCTION retrieveContainingIdPathbyEId(baseClassification varchar2, contextId number, elemId NUMBER) RETURN VARCHAR2;

    FUNCTION retrievePagebyIdForFolio(baseClassification varchar2, contextId number, elemId number) RETURN NUMBER;

    FUNCTION retrieveCodeNestingLevel(baseClassification varchar2, contextId number, elemId number) RETURN NUMBER;

    FUNCTION numberOfChildrenWithValidation(baseClassification varchar2, contextId number, conceptCode varchar2) RETURN NUMBER;

    FUNCTION getActiveChildCount(baseClassification varchar2, structureVersionID number, parentElementId number) RETURN NUMBER;

    FUNCTION getIndexPath(indexElementId NUMBER,  contextId NUMBER) RETURN VARCHAR2;

    FUNCTION getSupplementPath(supplementId NUMBER,  contextId NUMBER) RETURN VARCHAR2;

    FUNCTION testingOracleIssue(contextId number, unitConceptId varchar2, classification varchar2, language varchar2, requestId number, narrowClassId number, codeClassId number
      , indexDescClassId  number, levelClassId number, longPresentationClassId number, tablePresentationClassId number ) RETURN CIMS_UTIL.ref_cursor;

    FUNCTION startswithWithOrAvec (pStr Textpropertyversion.text%type) return CHAR;

    PROCEDURE runStats;
    FUNCTION getBooleanProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return CHAR;

    FUNCTION getChangeRequestFromStatus(changeRequestHistoryId NUMBER, changeRequestId NUMBER) RETURN CLOB;

    FUNCTION getChangeRequestHistoryOwner(changeRequestHistoryId NUMBER, changeRequestId NUMBER) RETURN CLOB;
    FUNCTION getXMLProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) return XMLPROPERTYVERSION.XMLTEXT%TYPE;
    FUNCTION html_to_xhtml(html CLOB) return CLOB;
    FUNCTION to_xhtml(text IN VARCHAR2) RETURN VARCHAR2;
    FUNCTION getInitialElementVersionIdCR(v_elementId NUMBER, v_changeRequestId NUMBER) RETURN NUMBER;
    FUNCTION getChangeRequestCount(nConceptId NUMBER) RETURN NUMBER;
    FUNCTION getChapterOrSectionID(sClassification VARCHAR2, nContextId NUMBER, sConceptCode VARCHAR2) RETURN NUMBER;
    FUNCTION findAncestorId(nContextId NUMBER, nAncestorClasssId NUMBER, nRelationshipClasssId NUMBER, nConceptId NUMBER) RETURN NUMBER;
    FUNCTION isNumber (p_string IN VARCHAR2) RETURN INT;
END CIMS_UTIL;
/

prompt
prompt Creating package body CIMS_UTIL
prompt ===============================
prompt
CREATE OR REPLACE PACKAGE BODY CIMS_UTIL IS

    /**************************************************************************************************************************************
    * NAME:          getElementId
    * DESCRIPTION:   Returns the element ID for a given element version
    **************************************************************************************************************************************/
       FUNCTION getElementId(elemVersionId NUMBER) RETURN NUMBER IS
         elemid NUMBER;
       BEGIN
         IF elemVersionId is null then return null;END IF;
         SELECT elementid into elemid
         FROM elementversion
         WHERE elementVersionId = elemVersionId
         ;
         RETURN elemId;
       END getElementId;

    /**************************************************************************************************************************************
    * NAME:          getConceptMajorType
    * DESCRIPTION:   Returns the mahor type of the element: TABULAR, INDEX, SUPPLEMENT
    **************************************************************************************************************************************/
       FUNCTION getConceptMajorType(pConceptid NUMBER) RETURN VARCHAR2 IS
         vclassname VARCHAR2(100);
       BEGIN
         SELECT cims_util.getClassNameForClassId(e.classid) into vclassname
         FROM element e
         WHERE e.elementid = pConceptid
         ;
         CASE vclassname
           WHEN 'Chapter' THEN RETURN 'TABULAR' ;
           WHEN 'Block' THEN RETURN 'TABULAR' ;
           WHEN 'Category' THEN RETURN 'TABULAR' ;
           WHEN 'Section' THEN RETURN 'TABULAR' ;
           WHEN 'Group' THEN RETURN 'TABULAR' ;
           WHEN 'Rubric' THEN RETURN 'TABULAR' ;
           WHEN 'CCICODE' THEN RETURN 'TABULAR' ;
           WHEN 'BookIndex' THEN RETURN 'INDEX';
           WHEN 'LetterIndex' THEN RETURN 'INDEX';
           WHEN 'AlphabeticIndex' THEN RETURN 'INDEX';
           WHEN 'ExternalInjuryIndex' THEN RETURN 'INDEX';
           WHEN 'NeoplasmIndex' THEN RETURN 'INDEX';
           WHEN 'DrugsAndChemicalsIndex' THEN RETURN 'INDEX';
           WHEN 'Index' THEN RETURN 'INDEX';
           WHEN 'ValidationICD' THEN RETURN 'VALIDATION';
           WHEN 'ValidationCCI' THEN RETURN 'VALIDATION';
           WHEN 'Supplement' THEN RETURN 'SUPPLEMENT';
           ELSE raise_application_error(-20011, ' cims_util.getConceptMajorType Error: Invalid Concept ID:  '|| pConceptid|| ' ' || substr(sqlerrm, 1, 512));
         END CASE;
         RETURN 'TABULAR';
       END getConceptMajorType;

    /**************************************************************************************************************************************
    * NAME:          getElementVersionId
    * DESCRIPTION:   Returns the elementversion ID for a given element and context
    **************************************************************************************************************************************/
       FUNCTION getElementVersionId(elemId NUMBER, context NUMBER) RETURN NUMBER IS
         elemvid NUMBER;
       BEGIN
         IF elemId is null then return null; END IF;
         SELECT ev.elementversionid into elemvid
         FROM elementversion ev, structureelementversion strlev
         WHERE strlev.structureid = context
         and strlev.elementversionid = ev.elementversionid
         and ev.elementid = elemId;
         RETURN elemvId;
       END getElementVersionId;

       FUNCTION getICDConceptChapterVersionId(elemId NUMBER, context NUMBER) RETURN NUMBER IS
         chaptervid NUMBER;
         narrowClassID NUMBER    := getClassIDForClassName('ICD-10-CA', 'Narrower');
       BEGIN
         		SELECT cims_util.getElementVersionId(max(child_elid),context)into chaptervid from (
		        WITH concepthierarchy AS (
		         SELECT narrow_cp.rangeelementid parent_elid, narrow_cp.domainelementid child_elid, child_cptv.classid
	           FROM CONCEPTPROPERTYVERSION narrow_cp
	           , CONCEPTVERSION child_cptv
	           , STRUCTUREELEMENTVERSION strelv_child
	           , STRUCTUREELEMENTVERSION strelv_cp
	           WHERE
    	           narrow_cp.conceptpropertyid = strelv_cp.elementversionid
	           and narrow_cp.classid = narrowClassID
	           and strelv_cp.structureid = context
	           AND child_cptv.elementid = narrow_cp.domainelementid
	           and strelv_child.structureid = context
	           and strelv_child.elementversionid = child_cptv.conceptid
	           and narrow_cp.status = 'ACTIVE'
	           and child_cptv.status = 'ACTIVE'
             )
             SELECT child_elid, classid
             FROM concepthierarchy ep
             CONNECT BY nocycle   child_elid = prior parent_elid
             start with child_elid = elemId
             ) where classid= getClassIdForClassName('ICD-10-CA', 'Chapter');

         RETURN chaptervid;
       END getICDConceptChapterVersionId;

    /**************************************************************************************************************************************
    * NAME:          getChangedFromVersionId
    * DESCRIPTION:   Returns the changedfromvesrionid for a specified elementid and contextIf
    **************************************************************************************************************************************/
    FUNCTION getChangedFromVersionId( pElementId number, pContextId number) return number
    IS
      vChangedFromVersionId number := 0;
    BEGIN
      select nvl(ev.changedfromversionid, 0)into vChangedFromVersionId
      from elementversion ev, structureelementversion sev
      where sev.structureid = pContextId
      and sev.elementversionid = ev.elementversionid
      and ev.elementid = pElementId;
      return vChangedFromVersionId;
    exception when others then
      raise_application_error(-20011, ' cims_util.getChangedFromVersionId Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
    END;

    /**************************************************************************************************************************************
    * NAME:          getContextYear
    * DESCRIPTION:   Returns the fiscal year for the specified context
    **************************************************************************************************************************************/
    FUNCTION getContextYear( pContextId number) return VARCHAR2
    IS
      vBaseContextId number := null;
      vYear elementversion.versioncode%type;
    BEGIN
    select sv.basestructureid into vBaseContextId from structureversion sv
    where sv.structureid = pContextId;

    IF vBaseContextId is null THEN
      vBaseContextId := pContextId;
    END IF;

    select ev.versioncode into vYear from structureversion sv, elementversion ev
    where sv.structureid = ev.elementversionid
    and sv.structureid = vBaseContextId;

    RETURN vYear;

    EXCEPTION WHEN NO_DATA_FOUND THEN RETURN NULL;
    END;

    /**************************************************************************************************************************************
    * NAME:          getBaseVersionId
    * DESCRIPTION:   Returns the elementversionid of the specified element currently linked to the basecontext of the specified context
    **************************************************************************************************************************************/
    FUNCTION getBaseVersionId( pElementId number, pContextId number) return number
    IS
      vBaseContextId number := null;
      vBaseVersionId number := 0;
    BEGIN
      select sv.basestructureid vBaseContextId into vBaseContextId from structureversion sv where sv.structureid = pContextId;
      IF vBaseContextId is null then
         raise_application_error(-20011, ' cims_util.getBaseVersionId Error: Illegal argument pContextId! ' || pContextId || ' '   || substr(sqlerrm, 1, 512));
      end if;

      select nvl(ev.elementversionid, 0)into vBaseVersionId
      from elementversion ev, structureelementversion sev
      where sev.structureid = vBaseContextId
      and sev.elementversionid = ev.elementversionid
      and ev.elementid = pElementId;
      return vBaseVersionId;
    exception when no_data_found then return 0;
      when others then
      raise_application_error(-20011, ' cims_util.getBaseVersionId Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
    END;
    /**************************************************************************************************************************************
    * NAME:          getClassIDForClassName
    * DESCRIPTION:   Returns the Class ID for a given class name and classification name
    **************************************************************************************************************************************/
    FUNCTION getClassIDForClassName(bcName VARCHAR2, cName VARCHAR2) RETURN NUMBER IS
        classID NUMBER;
    BEGIN
        SELECT c.CLASSID
        INTO classID
        FROM CLASS c
        WHERE UPPER(TRIM(c.baseclassificationname)) = UPPER(TRIM(bcName))
        AND UPPER(TRIM(c.CLASSNAME)) = UPPER(TRIM(cName));

        RETURN classID;
    END getClassIDForClassName;

    /**************************************************************************************************************************************
    * NAME:          getClassNameForClassId
    * DESCRIPTION:   Returns the Class name for a given class id
    **************************************************************************************************************************************/
    FUNCTION getClassNameForClassId(id NUMBER) RETURN VARCHAR2 IS
        className VARCHAR2(100);
    BEGIN
        SELECT c.classname
        INTO className
        FROM CLASS c
        WHERE c.classid = id;

        RETURN className;
    END getClassNameForClassId;

   /**************************************************************************************************************************************
    * NAME:          getClassNameForElementId
    * DESCRIPTION:   Returns the Class name for a given element id
    **************************************************************************************************************************************/
    FUNCTION getClassNameForElementId(pElementId NUMBER) RETURN VARCHAR2 IS
        className VARCHAR2(100);
    BEGIN
        SELECT c.classname
        INTO className
        FROM CLASS c, ELEMENT e
        WHERE c.classid = e.classid
        and e.elementid = pElementId;

        RETURN className;
    END getClassNameForElementId;

    /**************************************************************************************************************************************
    * NAME:          getBaseClassifNameForElem Element
    * DESCRIPTION:   Returns the baseclassification name for an element
    **************************************************************************************************************************************/
    FUNCTION getBaseClassifNameForElem(pElementId number) RETURN VARCHAR2 IS
        classificationName class.baseclassificationname%TYPE;
    BEGIN
        SELECT c.baseclassificationname
        INTO classificationName
        FROM CLASS c, element e
        WHERE e.classid = c.classid
        and e.elementid = pElementId;

        RETURN classificationName;
    END getBaseClassifNameForElem;

    /**************************************************************************************************************************************
    * NAME:          getBaseClassificationVersionID
    * DESCRIPTION:   Returns the ElementVersionID for a given classification name
    **************************************************************************************************************************************/
    FUNCTION getBaseClassificationVersionID(bcName VARCHAR2, versCode VARCHAR2) RETURN NUMBER IS
        structureversionID NUMBER;
        bcClassId NUMBER := getClassIDForClassName(bcName, bcName);

    BEGIN
        SELECT    ev.elementversionid
        INTO      structureversionID
        FROM      ELEMENTVERSION ev
        WHERE     ev.classid = bcClassId
        AND       ev.versioncode = versCode
        ;

        RETURN structureversionID;
    end getBaseClassificationVersionID;


    /**************************************************************************************************************************************
    * NAME:          getBaseClassifIDForContextID
    * DESCRIPTION:   Returns the ElementVersionID for a given classification name
    **************************************************************************************************************************************/
    FUNCTION getBaseClassifIDForContextID(pStructureId number) RETURN NUMBER IS
        structureversionID NUMBER;
    BEGIN
        SELECT    sv.basestructureid
        INTO      structureversionID
        FROM      structureversion sv
        WHERE     sv.structureid = pStructureId
        ;
        RETURN structureversionID;
    exception when no_data_found then
      return null;
    end getBaseClassifIDForContextID;

    /**************************************************************************************************************************************
    * NAME:          getConceptStatus
    * DESCRIPTION:   Returns the status for a specified concept
    **************************************************************************************************************************************/
    FUNCTION getConceptStatus( pConceptId NUMBER, pContextId NUMBER) RETURN VARCHAR2
    IS
      vStatus Elementversion.Status%TYPE;
    BEGIN
        WITH strelementversion AS (
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
         SELECT cv.status INTO vStatus FROM
          strelementversion strev, conceptversion cv
         WHERE cv.elementid = pConceptId
         AND strev.elementversionid = cv.conceptid
         ;
         RETURN vStatus;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getTextProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
    END;

    /**************************************************************************************************************************************
    * NAME:          getTextProperty
    * DESCRIPTION:   Returns the text for a specified TextProperty
    **************************************************************************************************************************************/

       FUNCTION getTextProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) return VARCHAR2 IS
         txt VARCHAR2(4000);
       BEGIN
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
         SELECT tpv.text INTO txt FROM
         textpropertyversion tpv, propertyversion pv, strelementversion strev, class c, elementversion ev, element e
         WHERE tpv.textpropertyid = pv.propertyid
         AND pv.domainelementid = elemId
         AND strev.elementversionid = pv.propertyid
         AND nvl(tpv.languagecode,'XX') = nvl(LANGUAGE,'XX')
         AND pv.propertyid = ev.elementversionid
         AND ev.elementid = e.elementid
         AND c.classid = e.classid
         AND c.classname = classnme;
         RETURN txt;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getTextProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END getTextProperty;

    /**************************************************************************************************************************************
    * NAME:          getNumericProperty
    * DESCRIPTION:   Returns the number for a specified TextProperty
    **************************************************************************************************************************************/
       FUNCTION getNumericProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return VARCHAR2 IS
         vNum numericpropertyversion.numericvalue%type;
       BEGIN
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
         SELECT npv.numericvalue INTO vNum FROM
         numericpropertyversion npv, strelementversion strev, class c
         WHERE npv.domainelementid = elemId
         AND strev.elementversionid = npv.numericpropertyid
         AND c.classid = npv.classid
         AND c.classname = classnme;
         RETURN vNum;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getTextProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END getNumericProperty;


    /**************************************************************************************************************************************
    * NAME:          getDomainPropertyCode
    * DESCRIPTION:   Returns the code of a domain property for a concept
    **************************************************************************************************************************************/

       FUNCTION getDomainPropertyCode(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return VARCHAR2 IS
         txt VARCHAR2(4000);
         vClassificationName Class.Baseclassificationname%TYPE := getBaseClassifNameForElem(elemId);
         vDomainClassId number               := getClassIDForClassName(vClassificationName,classnme);
         vDomainCPVClassId number            := getClassIDForClassName(vClassificationName, classnme||'Indicator');
         vDomainCodeClassId number           := getClassIDForClassName(vClassificationName,'DomainValueCode');
       BEGIN
        IF vDomainClassId is null OR vDomainCPVClassId is null THEN
          return null;
        END IF;
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
        , strelementversion1 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
         SELECT tpv.text INTO txt FROM
         textpropertyversion tpv, conceptpropertyversion cpv, strelementversion strev, strelementversion1 strev1
         WHERE tpv.domainelementid = cpv.rangeelementid
         AND cpv.domainelementid = elemId
         AND strev.elementversionid = cpv.conceptpropertyid
         and strev1.elementversionid = tpv.textpropertyid
         and tpv.classid = vDomainCodeClassId
         and cpv.classid = vDomainCPVClassId;

          RETURN txt;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getDomainPropertyCode Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END getDomainPropertyCode;

    /**************************************************************************************************************************************
    * NAME:          getHTMLProperty
    * DESCRIPTION:   Returns the text for a specified HTMLProperty
    **************************************************************************************************************************************/

       FUNCTION getHTMLProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) return HTMLPROPERTYVERSION.HTMLTEXT%TYPE IS
         html HTMLPROPERTYVERSION.HTMLTEXT%TYPE;
       BEGIN
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
         SELECT hpv.htmltext INTO html FROM
         htmlpropertyversion hpv, propertyversion pv, strelementversion strev, class c, elementversion ev, element e
         WHERE hpv.htmlpropertyid = pv.propertyid
         AND pv.domainelementid = elemId
         AND strev.elementversionid = pv.propertyid
         AND nvl(hpv.languagecode,'XX') = nvl(LANGUAGE,'XX')
         AND pv.propertyid = ev.elementversionid
         AND ev.elementid = e.elementid
         AND c.classid = e.classid
         AND c.classname = classnme;
         RETURN html;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getHTMLProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
       END getHTMLProperty;

   /**************************************************************************************************************************************
    * NAME:          getClassificationRootId
    * DESCRIPTION:   Returns the text for a specified TextProperty
    **************************************************************************************************************************************/

       FUNCTION getClassificationRootId(bcName VARCHAR2) return VARCHAR2
       AS
         rootVersionId NUMBER;
         rootClassId NUMBER := getClassIDForClassName(bcName, 'ClassificationRoot');
       BEGIN
       SELECT e.elementid INTO rootVersionId
       FROM element e
       WHERE e.classid = rootClassId
       ;
       RETURN rootVersionId;
       END getClassificationRootId;

   /**************************************************************************************************************************************
    * NAME:          getFormattedLongDescription
    * DESCRIPTION:   Formats the input long description according to the business rules for presenting the concept in the Classification Viewer tree
    **************************************************************************************************************************************/
       FUNCTION getFormattedLongDescription(longDesc VARCHAR2, code VARCHAR2, codeplus VARCHAR2,langCode LANGUAGE.languagecode%TYPE, classification VARCHAR2, conceptClassid number, conceptStatus VARCHAR2 DEFAULT '')
         RETURN VARCHAR2
       AS
         formattedDesc VARCHAR2(1000);
         conceptClassname VARCHAR2(100) := getClassNameForClassId(conceptClassid);
         formattedCode VARCHAR2(1000) := code;
       BEGIN

         CASE conceptClassname
           WHEN 'Chapter' THEN
             select decode(code,'0','0',trim(to_char(code,'RM'))) into formattedCode from dual ;
             CASE langCode
               WHEN 'ENG' THEN
                 formattedDesc := 'Chapter ' || formattedCode || ' - ' || longdesc;
               WHEN 'FRA' THEN
                 formattedDesc := 'Chapitre ' || formattedCode || ' - ' || longdesc;
              END CASE;
            WHEN 'Section' THEN
             CASE langCode
               WHEN 'ENG' THEN
                 formattedDesc := 'Section ' || formattedCode || ' - ' || longdesc;
               WHEN 'FRA' THEN
                 formattedDesc := 'Section ' || formattedCode || ' - ' || longdesc;
             END CASE;
            WHEN 'Block' THEN
                 formattedDesc := longdesc;
            ELSE
              formattedDesc := code|| (case when codeplus='+' then '&#134;' else codeplus end) || ' '|| longdesc;
            END CASE;
            IF conceptStatus = 'DISABLED' THEN
              formattedDesc := '<font color="red"><B>&#215;</B></font>' || ' ' || formattedDesc;
            END IF;
         RETURN formattedDesc;
       END;

   /**************************************************************************************************************************************
    * NAME:          isCCIBlockLevel2
    * DESCRIPTION:   Returns 'Y' if the concept specified by elemId parameter is a second level block
    * Note:          This could heve been a more general method but it was done in this specific way to get the best performance possible
    **************************************************************************************************************************************/

        FUNCTION isCCIBlockLevel2(elemId NUMBER, contextId NUMBER) RETURN CHAR AS
         sectionId number:=0;
         --blockClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Block');
         narrowClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Narrower');
         sectionClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Section');
       BEGIN
             with strelementversion as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             , strelementversion1 as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             select e.classid into sectionId from conceptpropertyversion cpv, strelementversion s, element e
             where cpv.domainelementid = (
                   select cpv.rangeelementid from conceptpropertyversion cpv, strelementversion1 s, element e
                   where cpv.domainelementid = elemId
                   and cpv.classid=narrowClassId
                   and e.elementid = cpv.rangeelementid
                   and s.elementid = cpv.domainelementid
                   )
             and cpv.classid=narrowClassId
             and s.elementid = cpv.domainelementid
             and e.elementid = cpv.rangeelementid
             and e.classid=sectionClassId;
             IF sectionId > 0 THEN
               RETURN 'Y';
             END IF;
             RETURN 'N';

             Exception when others then
               RETURN 'N';

    END;

    /**************************************************************************************************************************************
    * NAME:          isCCIBlockLevel1
    * DESCRIPTION:   Returns 'Y' if the concept specified by elemId parameter is a first level block
    * Note:          This could heve been a more general method but it was done in this specific way to get the best performance possible
    **************************************************************************************************************************************/

        FUNCTION isCCIBlockLevel1(elemId NUMBER, contextId NUMBER) RETURN CHAR AS
         sectionId number:=0;
         --blockClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Block');
         narrowClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Narrower');
         sectionClassId class.classid%type := getClassIDForClassName(bcName => 'CCI', cName => 'Section');
       BEGIN
             with strelementversion as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             select e.classid into sectionId from conceptpropertyversion cpv, strelementversion s, element e
             where cpv.domainelementid = elemId
             and cpv.classid=narrowClassId
             and s.elementid = cpv.domainelementid
             and e.elementid = cpv.rangeelementid
             and e.classid=sectionClassId;
             IF sectionId > 0 THEN
               RETURN 'Y';
             END IF;
             RETURN 'N';
             Exception when others then
               RETURN 'N';

        END;

        FUNCTION hasSecondLevelChild(elemId NUMBER, contextId NUMBER, classification VARCHAR2) RETURN CHAR AS
                 narrowClassId class.classid%type := getClassIDForClassName(bcName => classification, cName => 'Narrower');
                 blockClassId class.classid%type := getClassIDForClassName(bcName => classification, cName => 'Block');
                 childNo NUMBER:= 0;
        BEGIN
             with strelementversion as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             , strelementversion1 as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             , strelementversion2 as
             (
             select elementversionid, elementid from structureelementversion where structureid=contextId
             UNION  ALL
             select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
             and not exists (
             select elementid from structureelementversion cv where cv.structureid=contextId
             and cv.elementid = sv.elementid
             ))
             select count(*) into childNo from conceptpropertyversion cpv, conceptversion c, strelementversion sev, strelementversion1 sev1
                    where cpv.rangeelementid  in (
                    select domainelementid from conceptpropertyversion cpv, conceptversion c, strelementversion2 sev, structureelementversion sev1
                    where cpv.rangeelementid =  elemId
                    and cpv.classid=narrowClassId
--                    and sev.structureid = contextId
                    and sev.elementid = cpv.elementid
                    and c.elementid = cpv.domainelementid
                    and c.classid = blockClassId
--                    and sev1.structureid = sev.structureid
                    and sev1.elementid = cpv.elementid
                    )
                    and cpv.classid=narrowClassId
                    --and sev.structureid = contextId
                    and sev.elementid = cpv.elementid
                    and c.elementid = cpv.domainelementid
                    and c.classid = blockClassId
                    --and sev1.structureid = sev.structureid
                    and sev1.elementid = cpv.elementid
                    ;
                    IF childNo > 0 THEN
                      RETURN 'Y';
                    END IF;
                    RETURN 'N';
         END;


   /**************************************************************************************************************************************
    * NAME:          getContainerPage
    * DESCRIPTION:   Utility method that implements the business rules for determining the container page for a concept
    **************************************************************************************************************************************/
       FUNCTION getContainerPage(parentConceptId NUMBER, childConceptId NUMBER, childClassId NUMBER, classification Class.Baseclassificationname%type, containerId NUMBER, pContextId NUMBER)
         RETURN NUMBER
       AS
         parentClassId NUMBER := 0;
         nConceptClassId NUMBER := 0;
         parentClassname VARCHAR2(100) := null;
         childClassname Class.Classname%Type := null;
         vIsBaseContext CHAR:='Y';
         vBookIndexType VARCHAR2(100) := null;
       BEGIN
         -- container already found above in the hierarchy
         if containerId is not null and containerId > 0 THEN
           RETURN containerId;
         END IF;

         select nvl2(sv.basestructureid, 'N','Y') into vIsBaseContext from structureversion sv where sv.structureid = pContextId;

         SELECT distinct classId INTO parentClassId FROM conceptversion WHERE elementId = parentConceptId;
         select distinct classId INTO nConceptClassId From conceptversion where elementId = childConceptId;
         parentClassname := getClassNameForClassId(parentClassId);
         childClassname := getClassNameForClassId(nConceptClassId);

         IF vIsBaseContext = 'Y' THEN
         CASE classification
           WHEN 'ICD-10-CA' THEN
             CASE childClassname
               WHEN 'Chapter' THEN
                    RETURN childConceptId;
               WHEN 'LetterIndex' THEN
                    RETURN childConceptId;
               WHEN 'NeoplasmIndex' THEN
                 IF parentClassname = 'BookIndex' THEN
                   RETURN childConceptId;
                 END IF;
               ELSE
                 RETURN 0;
             END CASE;
           WHEN 'CCI' THEN
             CASE childClassName
               WHEN 'Section' THEN
                 IF hasSecondLevelChild(childConceptId, pContextId, classification) = 'Y' THEN
                   RETURN 0;
                 ELSE
                   RETURN childConceptId;
                 END IF;
               WHEN 'Block' THEN
                 IF childClassname = 'Block' THEN
                   IF isCCIBlockLevel2(childConceptId, pContextid)='Y' then
                     RETURN childConceptId;
                   END IF;
                 END IF ;
               WHEN 'LetterIndex' THEN
                 RETURN childConceptId;
               ELSE
                 RETURN 0;
               END CASE;
          END CASE;
         RETURN 0;
         ELSE
         CASE classification
           WHEN 'ICD-10-CA' THEN
             CASE childClassname
               WHEN 'Category' THEN
                 IF parentClassname = 'Block' THEN
                    RETURN childConceptId;
                 ELSE
                   RETURN 0;
                 END IF;
               WHEN 'NeoplasmIndex' THEN
                 IF parentClassname = 'BookIndex' THEN
                   RETURN childConceptId;
                 END IF;
               WHEN 'AlphabeticIndex' THEN
                 IF parentClassname = 'LetterIndex' THEN
                   RETURN childConceptId;
                 END IF;
               WHEN 'ExternalInjuryIndex' THEN
                 IF parentClassname = 'LetterIndex' THEN
                   RETURN childConceptId;
                 END IF;
               WHEN 'LetterIndex' THEN
                 IF parentClassname = 'BookIndex' THEN
                    vBookIndexType := getTextProperty(parentConceptId,'IndexCode',pContextId, 'ENG');
                    if vBookIndexType = 'D' THEN
                       RETURN childConceptId;
                    end if;
                    vBookIndexType := getTextProperty(parentConceptId,'IndexCode',pContextId, 'FRA');
                    if vBookIndexType = 'D' THEN
                       RETURN childConceptId;
                    end if;
                 END IF;
               WHEN 'DrugsAndChemicalsIndex' THEN
                 IF parentClassname = 'LetterIndex' THEN
                   RETURN childConceptId;
                 END IF;
               ELSE
                 RETURN 0;
             END CASE;
           WHEN 'CCI' THEN
             CASE childClassName
               WHEN 'Group' THEN
                    RETURN childConceptId;
               WHEN 'Index' THEN
                 IF parentClassname = 'LetterIndex' THEN
                   RETURN childConceptId;
                 END IF;
               ELSE
                 RETURN 0;
               END CASE;
          END CASE;
         RETURN 0;
         END IF;
       END;

   /**************************************************************************************************************************************
    * NAME:          getValidationRuleRefAttrCode
    * DESCRIPTION:   Returns code of specified reference attribute for a validation rule
    **************************************************************************************************************************************/

  /**************************************************************************************************************************************
    * NAME:          searchNodesByCode
    * DESCRIPTION:   Searches concepts with a specified code pattern (searchstring)
    * and returns hierarchical information about each found concept
    **************************************************************************************************************************************/
       FUNCTION searchNodesByCode(bcName VARCHAR2, versionCode IN VARCHAR2, nodeStatus VARCHAR2
         , searchString VARCHAR2, maxRows NUMBER, language VARCHAR2)
        RETURN CIMS_UTIL.ref_cursor
       AS
        node_cursor CIMS_UTIL.ref_cursor;
        contextVersionId NUMBER := getBaseClassificationVersionID(bcName, versionCode);
        narrowClassID NUMBER    := getClassIDForClassName(bcName, 'Narrower');
        codeClassID   NUMBER    := getClassIDForClassName(bcName, 'Code');

        begin
          OPEN node_cursor FOR
               with conceptvers as (
                    SELECT cv.* from conceptversion cv, structureelementversion strelv
                    WHERE cv.conceptid = strelv.elementversionid
                    AND strelv.structureid = contextVersionId
                    AND (nodeStatus IS NULL OR cv.status = nodeStatus)
                    ), code_hierarchy AS (
                       SELECT tp4.text conceptcode
                       , c1.conceptid conceptversionid
                       , c3.conceptid parentconceptversionid
                       FROM   conceptvers c1             -- child concept
                       , CONCEPTPROPERTYVERSION cp2      -- narrow relationship
                       , conceptvers c3                  -- parent concept
                       , TEXTPROPERTYVERSION tp4         -- code
                       , STRUCTUREELEMENTVERSION se_cp2
                       , STRUCTUREELEMENTVERSION se_tp4
                       WHERE  cp2.domainelementid  = c1.elementid
                       AND cp2.rangeelementid   = c3.elementid
                       AND c1.elementid         = tp4.domainelementid
                       AND cp2.classid = narrowClassID
                       AND tp4.classid = codeClassID
                       AND cp2.status = 'ACTIVE'         -- always check only active
                       AND se_cp2.elementversionid = cp2.conceptpropertyid
                       AND se_tp4.elementversionid = tp4.textpropertyid
                       )
                       SELECT conceptpath concept_id_path
                       , conceptversionid concept_id
                       , conceptcode concept_code
                       , conceptpath concept_id_path
                       , (select classname from class where classid = (select classid from elementversion where elementversionid=conceptversionid))
                       , cims_util.getTextProperty(elemid => cims_util.getElementId(conceptversionid),classnme => 'LongTitle', strid => contextVersionId, language => language)
                       , conceptcode concept_code_desc
                       FROM (
                            SELECT conceptversionid, parentconceptversionid,conceptcode, conceptpath
                            , length(conceptpath)-length(replace(conceptpath,'/','')) nodes
                            , max(length(conceptpath)-length(replace(conceptpath,'/',''))) over (partition by conceptversionid, parentconceptversionid,conceptcode) maxl
                            FROM (
                                 SELECT conceptversionid, conceptcode, parentconceptversionid
                                 , SYS_CONNECT_BY_PATH(parentconceptversionid, '/')||'/'||conceptversionid conceptpath
                                 FROM code_hierarchy
                                 CONNECT BY NOCYCLE PRIOR conceptversionid = parentconceptversionid
                                 )
                                 ORDER by conceptcode
                             )
                             where nodes = maxl
                             AND conceptcode like searchString
                             and rownum <= maxRows;
          RETURN node_cursor;
          END searchNodesByCode;

      /**************************************************************************************************************************************
    * NAME:          isValidCode
    * DESCRIPTION:   Returns 'Y' if the concept is a valid Code
    *  otherwise return 'N'
    * Note. A Valid Code is a concept that is a ICD-10-CA CATEGORY that is ACTIVE and has no ACTIVE children
   **************************************************************************************************************************************/
    FUNCTION isValidCode(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR
    IS
    BEGIN
      IF getClassNameForElementId(pConceptId) != 'Category'
         OR getConceptStatus(pConceptId, pContextId) != 'ACTIVE'
         OR hasActiveChildren(pContextId, pConceptId) = 'Y'
      THEN
        RETURN 'N';
      END IF;
      RETURN 'Y';
    END;


   /**************************************************************************************************************************************
    * NAME:          hasChildren
    * DESCRIPTION:   Returns 'Y' if the concept has children(via Narrower CPV) with STATUS,
    *  otherwise return 'N'
    **************************************************************************************************************************************/
    FUNCTION hasChildren(pContextId NUMBER, pConceptId NUMBER, pStatus varchar2 DEFAULT 'ACTIVE,DISABLED,REMOVED') return CHAR
    IS
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vActiveChildrenNo NUMBER := 0;
    BEGIN
        select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid;

          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';

        WITH strelementversion AS (
          select /*+ INLINE */ * from
           (
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
                and cv.elementid = sv.elementid)
            )
        )

        select count(*) into vActiveChildrenNo
          from conceptpropertyversion cpv, conceptversion cv,
          strelementversion sev, strelementversion sev2
          where cpv.rangeelementid = pConceptid
          and cv.elementid = cpv.domainelementid
          and cpv.classid = vNarrowClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.status in (select REGEXP_SUBSTR (pStatus, '[^,]+', 1, LEVEL) from dual
                             connect by REGEXP_SUBSTR (pStatus, '[^,]+', 1, LEVEL) IS NOT NULL )
          -- don't really care about contents, as long as a row is returned
          and rownum = 1;

        IF vActiveChildrenNo > 0 THEN
          RETURN 'Y';
        ELSE
          RETURN 'N';
        END IF;
    exception
        when others then
            return null;
    END;


   /**************************************************************************************************************************************
    * NAME:          hasActiveChildren
    * DESCRIPTION:   Returns 'Y' if the concept has active children(via Narrower CPV),
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasActiveChildren(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR
    IS
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vActiveChildrenNo NUMBER := 0;
    BEGIN
           select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';

        WITH strelementversion AS (
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
          select count(*) into vActiveChildrenNo
          from conceptpropertyversion cpv, conceptversion cv,
          strelementversion sev, strelementversion2 sev2
          where cpv.rangeelementid = pConceptid
          and cv.elementid = cpv.domainelementid
          and cpv.classid = vNarrowClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.status = 'ACTIVE'
          ;
          IF vActiveChildrenNo > 0 THEN
            RETURN 'Y';
          ELSE
            RETURN 'N';
          END IF;
    exception
        when others then
            return null;
    END;

   /**************************************************************************************************************************************
    * NAME:          hasActiveParent
    * DESCRIPTION:   Returns 'Y' if the concept has an active parent(via Narrower CPV),
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasActiveParent(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR
    IS
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vActiveParentNo NUMBER := 0;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';

        WITH strelementversion AS (
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
          select count(*) into vActiveParentNo
          from conceptpropertyversion cpv, conceptversion cv,
          strelementversion sev, strelementversion2 sev2
          where cpv.domainelementid = pConceptid
          and cv.elementid = cpv.rangeelementid
          and cpv.classid = vNarrowClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.status = 'ACTIVE'
          ;
          IF vActiveParentNo > 0 THEN
            RETURN 'Y';
          ELSE
            RETURN 'N';
          END IF;
    exception
        when others then
            return null;
    END;

    /**************************************************************************************************************************************
    * NAME:          hasActiveValidationRule
    * DESCRIPTION:   Returns 'Y' if the concept has an active validation rule,
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasActiveValidationRule(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR
    IS
      vValidationCCICPVId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCICPV');
      vValidationICDCPVId Class.Classid%Type := cims_util.getClassIDForClassName('ICD-10-CA','ValidationICDCPV');
      vValidationCCIId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCI');
      vValidationICDId Class.Classid%Type := cims_util.getClassIDForClassName('ICD-10-CA','ValidationICD');
      vCnt NUMBER := 0;
    BEGIN
        WITH strelementversion AS (
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
      SELECT count(*) into vCnt from conceptpropertyversion cpv, conceptversion cv
      , strelementversion sev, strelementversion1 sev1
      where cpv.rangeelementid = pConceptId
      and cpv.classid in (vValidationCCICPVId, vValidationICDCPVId)
      and cv.classid in (vValidationCCIId, vValidationICDId)
      and cpv.domainelementid = cv.elementid
      and cv.status = 'ACTIVE'
      and cpv.conceptpropertyid = sev.elementversionid
      and cv.conceptid = sev1.elementversionid
      ;
      if vCnt > 0 then
        RETURN 'Y';
      END IF;
      RETURN 'N';
    exception when no_data_found THEN
      return 'N';
    END;
    /**************************************************************************************************************************************
    * NAME:          hasActiveValidationRuleForDH
    * DESCRIPTION:   Returns 'Y' if the concept has an active validation rule,
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasActiveValidationRuleForDH(pContextId NUMBER, pConceptId NUMBER, pDHCode Varchar2 ) RETURN CHAR
    IS
      vValidationCCICPVId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCICPV');
      vValidationICDCPVId Class.Classid%Type := cims_util.getClassIDForClassName('ICD-10-CA','ValidationICDCPV');
      vValidationCCIId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCI');
      vValidationICDId Class.Classid%Type := cims_util.getClassIDForClassName('ICD-10-CA','ValidationICD');
      vCnt NUMBER := 0;
    BEGIN
        WITH strelementversion AS (
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
      SELECT count(*) into vCnt from conceptpropertyversion cpv, conceptversion cv
      , strelementversion sev, strelementversion1 sev1
      where cpv.rangeelementid = pConceptId
      and cpv.classid in (vValidationCCICPVId, vValidationICDCPVId)
      and cv.classid in (vValidationCCIId, vValidationICDId)
      and cpv.domainelementid = cv.elementid
      and cv.status = 'ACTIVE'
      and cpv.conceptpropertyid = sev.elementversionid
      and cv.conceptid = sev1.elementversionid
      and getValidationRuleDHCode(pContextId, cv.elementid) = pDHCode
      ;
      if vCnt > 0 then
        RETURN 'Y';
      END IF;
      RETURN 'N';
    exception when no_data_found THEN
      return 'N';
    END;

    /**************************************************************************************************************************************
    * NAME:          hasChildWithActiveValidRule
    * DESCRIPTION:   Returns 'Y' if the concept has an active validation rule,
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasChildWithActiveValidRule(pContextId NUMBER, pConceptId NUMBER ) RETURN CHAR
    IS

      vConceptCode textpropertyversion.text%Type := cims_util.getTextProperty(pConceptId, 'Code', pContextId,null);
      vBaseClassificationName class.baseclassificationname%type:= getBaseClassifNameForElem(pConceptId);
      vConceptClassName class.classname%type:= getClassNameForElementId(pConceptId);
      vCodeClassID class.classid%type := getClassIDForClassName(vBaseClassificationName, 'Code');
      vCnt number := 0;
    BEGIN
     IF vConceptClassName NOT IN ('Rubric', 'Category') THEN
         raise_application_error(-20011, ' cims_util.hasChildWithActiveValidRule Error: Illegal Concept Type, ConceptId: ' || pConceptId || ' ContextId: ' || pContextId || ' ' || substr(sqlerrm, 1, 512));
      END IF;

      IF vConceptClassName = 'Rubric' THEN
        vConceptCode := substr(vConceptCode,1,7);
      END IF;

        WITH strelementversion AS (
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
     , conceptids as(
           select domainelementid conceptid
           from textpropertyversion tp, conceptversion cv, strelementversion sev, strelementversion1 sev1
           where tp.classid = vCodeClassID
           and tp.text like vConceptCode || '%'
           and tp.domainelementid != pConceptId
           and tp.domainelementid = cv.elementid
           and cv.status = 'ACTIVE'
           and tp.textpropertyid = sev.elementversionid
           and cv.conceptid = sev1.elementversionid
      )
      select count(*) into vCnt
      from conceptids cs
      where hasActiveValidationRule( pContextId, cs.conceptid)='Y'
      and rownum < 2
      ;
      IF vCnt > 0 THEN
        RETURN 'Y';
      END IF;
      RETURN 'N';
      exception when no_data_found then
        return 'N';
    END;

    /**************************************************************************************************************************************
    * NAME:          hasChildWithActiveValidRule
    * DESCRIPTION:   Returns 'Y' if the concept has an active validation rule for specified DH,
    *  otherwise return 'N'
    **************************************************************************************************************************************/

    FUNCTION hasChildWithActiveValidRuleDH(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 ) RETURN CHAR
    IS

      vConceptCode textpropertyversion.text%Type := cims_util.getTextProperty(pConceptId, 'Code', pContextId,null);
      vBaseClassificationName class.baseclassificationname%type:= getBaseClassifNameForElem(pConceptId);
      vConceptClassName class.classname%type:= getClassNameForElementId(pConceptId);
      vCodeClassID class.classid%type := getClassIDForClassName(vBaseClassificationName, 'Code');
      vCnt number := 0;
    BEGIN
     IF vConceptClassName NOT IN ('Rubric', 'Category') THEN
         raise_application_error(-20011, ' cims_util.hasChildWithActiveValidRule Error: Illegal Concept Type, ConceptId: ' || pConceptId || ' ContextId: ' || pContextId || ' ' || substr(sqlerrm, 1, 512));
      END IF;

      IF vConceptClassName = 'Rubric' THEN
        vConceptCode := substr(vConceptCode,1,7);
      END IF;

        WITH strelementversion AS (
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
     , conceptids as(
           select domainelementid conceptid
           from textpropertyversion tp, conceptversion cv, strelementversion sev, strelementversion1 sev1
           where tp.classid = vCodeClassID
           and tp.text like vConceptCode || '%'
           and tp.domainelementid != pConceptId
           and tp.domainelementid = cv.elementid
           and cv.status = 'ACTIVE'
           and tp.textpropertyid = sev.elementversionid
           and cv.conceptid = sev1.elementversionid
      )
      select count(*) into vCnt
      from conceptids cs
      where hasActiveValidationRuleForDH(pContextId, conceptid, pDHCode) ='Y' and
      rownum < 2
      ;
      IF vCnt > 0 THEN
        RETURN 'Y';
      END IF;
      RETURN 'N';
      exception when no_data_found then
        return 'N';
    END;

   /**************************************************************************************************************************************
    * NAME:          getReferenceCount
    * DESCRIPTION:   Searches XML properties of active concepts in the specified context that contain the specified text and returns the
    *                number of occurences
    **************************************************************************************************************************************/
    FUNCTION getReferenceCount(pContextId NUMBER, pText VARCHAR2 ) RETURN NUMBER
    IS
      cnt number := 0;
      vText VARCHAR2(4000);
    BEGIN
      IF (isNumber(pText)=1) THEN
        vText := '\>' || pText || '\<';
      ELSE
        vText := pText;
      END IF;

      with strelementversion as
     (
      select
        elementversionid, elementid
      from
        structureelementversion
      where
        structureid=pContextId
      UNION ALL
      select
        elementversionid, elementid
      from
        structureelementversion sv
      where
        sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
        and not exists (select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid )
    )
    , strelementversion1 as
    (
      select
        elementversionid, elementid
      from
        structureelementversion
      where
        structureid=pContextId
      UNION ALL
      select
        elementversionid, elementid
      from
        structureelementversion sv
      where
        sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
        and not exists ( select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid)
    )
        select count(*) into cnt
        from
        xmlpropertyversion tr, strelementversion sv, conceptversion cv, strelementversion1 sv2
        where
        contains(tr.xmltext, substr (vText, 1, decode(instr(vText,  '-'),0 ,length(vText), instr(vText,  '-')-1))  ) > 0
          and tr.xmltext like '%' || vText || '%'
          and tr.xmlpropertyid=sv.elementversionid
          and cv.elementid=tr.domainelementid
          and cv. conceptid = sv2.elementversionid
          and cv.status = 'ACTIVE'
          and tr.classid not in (select classid from class where classname = 'ValidationDefinition')
          ;

       return cnt;
    END;

    /**************************************************************************************************************************************
    * NAME:          isNumber
    * DESCRIPTION:   check is the string is a number, returns 1 if it is a number; else return 0.
    *
    **************************************************************************************************************************************/
    FUNCTION isNumber (p_string IN VARCHAR2) RETURN INT
    IS
       v_new_num NUMBER;

    BEGIN
       v_new_num := TO_NUMBER(p_string);
       RETURN 1;
    EXCEPTION
    WHEN VALUE_ERROR THEN
       RETURN 0;
    END isNumber;

     /**************************************************************************************************************************************
    * NAME:          isIndexReferredInXML
    * DESCRIPTION:   Searches XML properties of active concepts in the specified context that contain the specified text and returns the
    *                number of occurences
    **************************************************************************************************************************************/
    FUNCTION isIndexReferredInXML(pContextId NUMBER, indexConceptId VARCHAR2 ) RETURN NUMBER
    IS
      cnt number := 0;
      vText textpropertyversion.text%type;
    BEGIN
          with strelementversion as
    (
      select
        elementversionid, elementid
      from
        structureelementversion
      where
        structureid=pContextId
      UNION ALL
      select
        elementversionid, elementid
      from
        structureelementversion sv
      where
        sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
        and not exists (select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid )
    )
    , strelementversion1 as
    (
      select
        elementversionid, elementid
      from
        structureelementversion
      where
        structureid=pContextId
      UNION ALL
      select
        elementversionid, elementid
      from
        structureelementversion sv
      where
        sv.structureid=(select basestructureid from structureversion where structureid=pContextId)
        and not exists ( select elementid from structureelementversion cv where cv.structureid=pContextId and cv.elementid = sv.elementid)
    )
        select count(*) into cnt
        from
        xmlpropertyversion tr, strelementversion sv, conceptversion cv, strelementversion1 sv2
        where
        contains(tr.xmltext, indexConceptId ) > 0
          and tr.xmltext like '%/' || indexConceptId || '<%'
          and tr.xmlpropertyid=sv.elementversionid
          and cv.elementid=tr.domainelementid
          and cv. conceptid = sv2.elementversionid
          and cv.status = 'ACTIVE';
       return cnt;
    END;
   /**************************************************************************************************************************************
    * NAME:          getActiveChildrenCodes
    * DESCRIPTION:   Returns the codes for all children(via Narrower CPV)
    *  that have the specified class of the specified concept in the specified context
    **************************************************************************************************************************************/

    FUNCTION getActiveChildrenCodes(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vClassId Class.Classid%TYPE;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vCodeClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Code';
          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';
          select c.classid into vClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = pClasName;

          OPEN vNodeCursor FOR
        WITH strelementversion AS (
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
          select tpv.text
          from conceptpropertyversion cpv, textpropertyversion tpv, conceptversion cv,
          strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
          where cpv.rangeelementid = pConceptid
          and tpv.domainelementid = cpv.domainelementid
          and cv.elementid = cpv.domainelementid
          and tpv.classid = vCodeClassId
          and cpv.classid = vNarrowClassId
          and cv.classid = vClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev1.elementversionid = tpv.textpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.status = 'ACTIVE'
          ;
          RETURN vNodeCursor;
    exception
        when others then
            return null;
    END;
    FUNCTION getActivePeersCodes(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vClassId Class.Classid%TYPE;
        vParentId NUMBER;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vCodeClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Code';
          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';
          select c.classid into vClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = pClasName;

        WITH strelementversion AS (
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
        select cpv.rangeelementid into vParentId from conceptpropertyversion cpv, strelementversion sev
        where cpv.domainelementid = pConceptId
        and cpv.classid = vNarrowClassId
        and sev.elementversionid = cpv.conceptpropertyid;



          OPEN vNodeCursor FOR
        WITH strelementversion AS (
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
          select tpv.text
          from conceptpropertyversion cpv, textpropertyversion tpv, conceptversion cv,
          strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
          where cpv.rangeelementid = vParentId
          and tpv.domainelementid = cpv.domainelementid
          and cv.elementid = cpv.domainelementid
          and tpv.classid = vCodeClassId
          and cpv.classid = vNarrowClassId
          and cv.classid = vClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev1.elementversionid = tpv.textpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.elementid != pConceptid
          and cv.status = 'ACTIVE'
          ;
          RETURN vNodeCursor;
    exception
        when others then
            return null;
    END;

    FUNCTION getActivePeersIndexDesc(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN CIMS_UTIL.ref_cursor
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vClassId Class.Classid%TYPE;
        vParentId NUMBER;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vCodeClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'IndexDesc';
          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';
          select c.classid into vClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = pClasName;

        WITH strelementversion AS (
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
        select cpv.rangeelementid into vParentId from conceptpropertyversion cpv, strelementversion sev
        where cpv.domainelementid = pConceptId
        and cpv.classid = vNarrowClassId
        and sev.elementversionid = cpv.conceptpropertyid;



          OPEN vNodeCursor FOR
        WITH strelementversion AS (
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
          select tpv.text
          from conceptpropertyversion cpv, textpropertyversion tpv, conceptversion cv,
          strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
          where cpv.rangeelementid = vParentId
          and tpv.domainelementid = cpv.domainelementid
          and cv.elementid = cpv.domainelementid
          and tpv.classid = vCodeClassId
          and cpv.classid = vNarrowClassId
          and cv.classid = vClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev1.elementversionid = tpv.textpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.elementid != pConceptid
          and cv.status = 'ACTIVE'
          ;
          RETURN vNodeCursor;
    exception
        when others then
            return null;
    END;

    FUNCTION getActivePeersClasses(pContextId NUMBER, pConceptId NUMBER) RETURN CIMS_UTIL.ref_cursor
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vParentId NUMBER;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vCodeClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Code';
          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';

        WITH strelementversion AS (
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
        select cpv.rangeelementid into vParentId from conceptpropertyversion cpv, strelementversion sev
        where cpv.domainelementid = pConceptId
        and cpv.classid = vNarrowClassId
        and sev.elementversionid = cpv.conceptpropertyid;



          OPEN vNodeCursor FOR
        WITH strelementversion AS (
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
          select distinct cv.classid
          from conceptpropertyversion cpv, conceptversion cv,
          strelementversion sev, strelementversion2 sev2
          where cpv.rangeelementid = vParentId
          and cv.elementid = cpv.domainelementid
          and cpv.classid = vNarrowClassId
          and sev.elementversionid = cpv.conceptpropertyid
          and sev2.elementversionid = cv.conceptid
          and cv.elementid != pConceptid
          and cv.status = 'ACTIVE'
          ;
          RETURN vNodeCursor;
    exception
        when others then
            return null;
    END;

    FUNCTION getParentId(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN NUMBER
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vClassId Class.Classid%TYPE;
        vParentId Element.Elementid%type;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';

        WITH strelementversion AS (
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
        select cpv.rangeelementid into vParentId
        from conceptpropertyversion cpv, strelementversion sev
        where cpv.domainelementid = pConceptId
        and cpv.classid = vNarrowClassId
        and sev.elementversionid = cpv.conceptpropertyid
        ;

        RETURN vParentId;
    END;

    FUNCTION getParentCode(pContextId NUMBER, pConceptId NUMBER, pClasName VARCHAR2) RETURN VARCHAR2
    IS
        vNodeCursor CIMS_UTIL.ref_cursor;
        vBaseClassification Class.Baseclassificationname%TYPE;
        vNarrowClassId Class.Classid%TYPE;
        vCodeClassId Class.Classid%TYPE;
        vClassId Class.Classid%TYPE;
        vParentCode Textpropertyversion.text%type;
    BEGIN
          select c.baseclassificationname into vBaseClassification from structureversion sv, class c
          where sv.structureid = pContextId
          and sv.classid = c.classid
          ;

          select c.classid into vCodeClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Code';
          select c.classid into vNarrowClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = 'Narrower';
          select c.classid into vClassId
          from class c where c.baseclassificationname = vBaseClassification and c.classname = pClasName;

        WITH strelementversion AS (
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
        select tpv.text into vParentCode
        from conceptpropertyversion cpv, textpropertyversion tpv, conceptversion cv
        , strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
        where cpv.domainelementid = pConceptId
        and cpv.classid = vNarrowClassId
        and sev.elementversionid = cpv.conceptpropertyid
        and tpv.domainelementid = cpv.rangeelementid
        and tpv.classid = vCodeClassId
        and sev1.elementversionid = tpv.textpropertyid
        and cv.conceptid = sev2.elementversionid
        and cv.classid = vClassId
        and cv.elementid = tpv.domainelementid
        ;

        return vParentCode;
    exception
        when others then
            return null;
    END;


     /**************************************************************************************************************************************
    * NAME:          getChildValidationRuleRefCodes
    * DESCRIPTION:   returns reference value codes for any child that has a validation rule of the specified data holding
    * Note - Curently we are picking the first child that has  validation for the specified data holding because the
    * assumption is they are all the same. However at some tpoint we need a business rule as to which child to pick
    **************************************************************************************************************************************/
    FUNCTION getChildValidationRuleRefCodes(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 )
    RETURN VARCHAR2
    IS
      vConceptCode textpropertyversion.text%Type := cims_util.getTextProperty(pConceptId, 'Code', pContextId,null);
      vBaseClassificationName class.baseclassificationname%type:= getBaseClassifNameForElem(pConceptId);
      vConceptClassName class.classname%type:= getClassNameForElementId(pConceptId);
      vCodeClassID class.classid%type := getClassIDForClassName(vBaseClassificationName, 'Code');
      vRefList VARCHAR2 (200);
    BEGIN
      IF vConceptClassName NOT IN ('Rubric') THEN
         raise_application_error(-20011, ' cims_util.getChildValidationRuleRefCodes Error: Illegal Concept Type, ConceptId: ' || pConceptId || ' ContextId: ' || pContextId || ' DataHoldingCode: ' || pDHCode|| ' ' || substr(sqlerrm, 1, 512));
      END IF;

      IF vConceptClassName = 'Rubric' THEN
        vConceptCode := substr(vConceptCode,1,7);
      END IF;

        WITH strelementversion AS (
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
     , conceptids as(
           select domainelementid conceptid
           from textpropertyversion tp, conceptversion cv, strelementversion sev, strelementversion1 sev1
           where tp.classid = vCodeClassID
           and tp.text like vConceptCode || '%'
           and tp.domainelementid != pConceptId
           and tp.domainelementid = cv.elementid
           and cv.status = 'ACTIVE'
           and tp.textpropertyid = sev.elementversionid
           and cv.conceptid = sev1.elementversionid
      )
      select getValidationRuleRefCodes( pContextId, cs.conceptid,pDHCode) into vRefList
      from conceptids cs
      where length(trim(getValidationRuleRefCodes( pContextId, cs.conceptid,pDHCode))) > 2
      and rownum < 2
      ;
      RETURN vRefList;
      exception when no_data_found then
        return '';
    END;

    /**************************************************************************************************************************************
    * NAME:          isRefAttributeMandatory
    * DESCRIPTION:   returns yes if the specified ref attribute is mandatory, otherwise 'N'
    **************************************************************************************************************************************/
    FUNCTION isRefAttributeMandatory(pContextId NUMBER, pAttributeCodeCode VARCHAR2) RETURN CHAR
    IS
      vIsMandatory CHAR := 'N';
      vAttributeMandatoryIndicator Class.Classid%Type := cims_util.getClassIDForClassName('CCI','AttributeMandatoryIndicator');
      vReferenceAttribute Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ReferenceAttribute');
      vAttributeCode Class.Classid%Type := cims_util.getClassIDForClassName('CCI','AttributeCode');
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
    select bv.booleanvalue into vIsMandatory
    from conceptversion cv, textpropertyversion tp, booleanpropertyversion bv
         , strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
         where cv.classid = vReferenceAttribute
         and tp.domainelementid = cv.elementid
         and tp.classid = vAttributeCode
         and bv.classid = vAttributeMandatoryIndicator
         and bv.domainelementid = cv.elementid
         and tp.text = pAttributeCodeCode
         and cv.conceptid = sev.elementversionid
         and tp.textpropertyid = sev1.elementversionid
         and bv.booleanpropertyid = sev2.elementversionid
         and cv.status != 'REMOVED'
         ;
         RETURN vIsMandatory;
    exception when no_data_found then
      return 'N';
    END;


    /**************************************************************************************************************************************
    * NAME:          isRefAttributeActive
    * DESCRIPTION:   returns yes if the specified ref attribute is active, otherwise 'N'
    **************************************************************************************************************************************/
    FUNCTION isRefAttributeActive(pContextId NUMBER, pAttributeCodeCode VARCHAR2) RETURN CHAR
    IS
      vReferenceAttribute Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ReferenceAttribute');
      vAttributeCode Class.Classid%Type := cims_util.getClassIDForClassName('CCI','AttributeCode');
      vStatus Elementversion.Status%Type;
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
    select cv.status into vStatus
    from conceptversion cv, textpropertyversion tp
         , strelementversion sev, strelementversion1 sev1
         where cv.classid = vReferenceAttribute
         and tp.domainelementid = cv.elementid
         and tp.classid = vAttributeCode
         and tp.text = pAttributeCodeCode
         and cv.conceptid = sev.elementversionid
         and tp.textpropertyid = sev1.elementversionid
         ;
         IF vStatus = 'ACTIVE' THEN
           RETURN 'Y';
         ELSE
           RETURN 'N';
         END IF;
    exception when others then
      return 'N';
    END;

     /**************************************************************************************************************************************
    * NAME:          getValidationRuleRefCodes
    * DESCRIPTION:   returns reference value codes for the validation rule of a specified data holding code for a specified concept
    **************************************************************************************************************************************/
    FUNCTION getValidationRuleRefCodes(pContextId NUMBER, pConceptId NUMBER, pDHCode VARCHAR2 )
    RETURN VARCHAR2
    IS
      vValidationCCICPVId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCICPV');
      vValidationDefinitionId Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationDefinition');
      vRefList VARCHAR2 (200);
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
         select statusref || ',' || extentref || ',' ||  locationref
         into vRefList
         from (
         select cims_util.getValidationRuleDHCode(pContextId, vr.elementid) dataholdingcode
         , substr(to_Char(REGEXP_substr (vrdef.xmltext, '<STATUS_REF>S([a-zA-Z0-9][a-zA-Z0-9])</STATUS_REF>')), 13, 3) statusref
         , substr(to_Char(REGEXP_substr (vrdef.xmltext, '<EXTENT_REF>E([a-zA-Z0-9][a-zA-Z0-9])</EXTENT_REF>')), 13, 3) extentref
         , substr(to_Char(REGEXP_substr (vrdef.xmltext, '<LOCATION_REF>(L|M)([a-zA-Z0-9][a-zA-Z0-9])</LOCATION_REF>')), 15, 3) locationref
         from conceptpropertyversion cpv, conceptversion vr, xmlpropertyversion vrdef
         , strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
         where cpv.rangeelementid = pConceptId
         and cpv.classid = vValidationCCICPVId
         and vr.elementid = cpv.domainelementid
         and vrdef.classid = vValidationDefinitionId
         and vrdef.domainelementid = vr.elementid
         and cpv.conceptpropertyid = sev.elementversionid
         and vr.conceptid = sev1.elementversionid
         and vrdef.xmlpropertyid = sev2.elementversionid
         and vr.status='ACTIVE'
         )
         where dataholdingcode = pDHCode
         ;
         RETURN vRefList;
         exception when no_data_found then
           return null;
         when others then
            raise_application_error(-20011, ' cims_util.getValidationRuleRefCodes Error: Unexpected error! ConceptId: ' || pConceptId || ' ContextId: ' || pContextId || ' DataHoldingCode: ' || pDHCode|| ' ' || substr(sqlerrm, 1, 512));

    END;

     /**************************************************************************************************************************************
    * NAME:          getValidationRuleDHCode
    * DESCRIPTION:   returns the Data Holding code for a specified validation rule in a specified context
    **************************************************************************************************************************************/
    FUNCTION getValidationRuleDHCode (pContextId NUMBER, pConceptId NUMBER) RETURN VARCHAR2
    IS
      dhcode Textpropertyversion.text%type;
      vBaseClassificationName class.baseclassificationname%type:= getBaseClassifNameForElem(pConceptId);
      vValidationFacilityClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'ValidationFacility');
      vFacilityTypeClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'FacilityType');
      vDomainValueCodeClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'DomainValueCode');
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
         select dhcode.text into dhcode from conceptpropertyversion dhcpv, conceptversion dh, textpropertyversion dhcode,
         strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
         where dhcpv.domainelementid = pConceptId
         and dhcpv.rangeelementid = dh.elementid
         and dhcpv.classid = vValidationFacilityClassId
         and dh.classid = vFacilityTypeClassId
         and dhcode.domainelementid = dh.elementid
         and dhcode.classid = vDomainValueCodeClassId
         and dh.status = 'ACTIVE'
         and dhcpv.conceptpropertyid = sev.elementversionid
         and dh.conceptid = sev1.elementversionid
         and dhcode.textpropertyid = sev2.elementversionid;
      return dhcode;
    exception when others then
      return '';
    END;

     /**************************************************************************************************************************************
    * NAME:          getValidationRuleTabularId
    * DESCRIPTION:   returns the element id of the tabular concept for a specified validation rule in a specified context
    **************************************************************************************************************************************/
    FUNCTION getValidationRuleTabularId (pContextId NUMBER, pValidationId NUMBER) RETURN NUMBER
    IS
      vTabularId Conceptversion.Conceptid%TYPE;
      vValidationICDCPV Class.Classid%Type := cims_util.getClassIDForClassName('ICD-10-CA','ValidationICDCPV');
      vValidationCCICPV Class.Classid%Type := cims_util.getClassIDForClassName('CCI','ValidationCCICPV');
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
         select cpv.rangeelementid into vTabularId from conceptpropertyversion cpv,
         strelementversion sev
         where cpv.domainelementid = pValidationId
         and cpv.conceptpropertyid = sev.elementversionid
         and cpv.classid in (vValidationICDCPV, vValidationCCICPV)
         ;
      return vTabularId;
    exception when others then
      return '';
    END;

     /**************************************************************************************************************************************
    * NAME:          getValidationRuleDHDescription
    * DESCRIPTION:   returns the Data Holding code for a specified validation rule in a specified context
    **************************************************************************************************************************************/
    FUNCTION getValidationRuleDHDescription (pContextId NUMBER, pConceptId NUMBER, pLanguageCode VARCHAR2) RETURN VARCHAR2
    IS
      dhcode Textpropertyversion.text%type;
      vBaseClassificationName class.baseclassificationname%type:= getBaseClassifNameForElem(pConceptId);
      vValidationFacilityClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'ValidationFacility');
      vFacilityTypeClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'FacilityType');
      vDomainValueDescriptionClassId class.classid%type := cims_util.getClassIDForClassName(vBaseClassificationName, 'DomainValueDescription');
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
         select dhcode.text into dhcode from conceptpropertyversion dhcpv, conceptversion dh, textpropertyversion dhcode,
         strelementversion sev, strelementversion1 sev1, strelementversion2 sev2
         where dhcpv.domainelementid = pConceptId
         and dhcpv.rangeelementid = dh.elementid
         and dhcpv.classid = vValidationFacilityClassId
         and dh.classid = vFacilityTypeClassId
         and dhcode.domainelementid = dh.elementid
         and dhcode.classid = vDomainValueDescriptionClassId
         and dh.status = 'ACTIVE'
         and dhcpv.conceptpropertyid = sev.elementversionid
         and dh.conceptid = sev1.elementversionid
         and dhcode.textpropertyid = sev2.elementversionid
         and dhcode.languageCode = pLanguageCode
         ;
      return dhcode;
    exception when others then
      return '';
    END;

     /**************************************************************************************************************************************
    * NAME:          retrieveContainingPagebyIdForFolio
    * DESCRIPTION:   Retrieve the Element ID which contains the html content of the elemId
    **************************************************************************************************************************************/
    FUNCTION retrievePagebyIdForFolio(baseClassification varchar2, contextId number, elemId number)
        RETURN NUMBER
    IS
        eID number := 0;
        cID number := 0;
        className VARCHAR2(30);

        codeClassID number;
        chapterClassID number;
        narrowClassID number;

        ICD_codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        ICD_chapterClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');

        CCI_codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        CCI_chapterClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Block');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
    BEGIN
        select  e.elementid, e.classid, c.classname
        into eID, cID, className
        from element e, class c
        where e.elementid = elemId and e.classid=c.classid;

        if (baseClassification = 'ICD-10-CA') then
            if className='Chapter' or className='Block' or className='Category' then
                codeClassID := ICD_codeClassID;
                chapterClassID := ICD_chapterClassID;
                narrowClassID := ICD_narrowClassID;
            else
                return null;
            end if;
        elsif (baseClassification = 'CCI') then
            case className
               when 'Section' then
                    return eID;
               when 'Block' then
                   IF isCCIBlockLevel2(eID, contextId)='Y' or isCCIBlockLevel1(eID, contextId)='Y' then
                     RETURN eID;
                   else
                     return null;  -- use parent
                   END IF;
               when 'Group' then
                   codeClassID := CCI_codeClassID;
                    chapterClassID := CCI_chapterClassID;
                    narrowClassID := CCI_narrowClassID;
               when 'Rubric' then
                   codeClassID := CCI_codeClassID;
                    chapterClassID := CCI_chapterClassID;
                    narrowClassID := CCI_narrowClassID;
               when 'CCICode' then
                   codeClassID := CCI_codeClassID;
                    chapterClassID := CCI_chapterClassID;
                    narrowClassID := CCI_narrowClassID;
               else
                    return null; -- use parent
               end case;

        end if;



        while (cID != chapterClassID) or (cID = chapterClassID and baseClassification = 'CCI' and isCCIBlockLevel2(eID, contextID)='N' and isCCIBlockLevel1(eID, contextID)='N') LOOP

            WITH strelementversion AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            , strelementversion1 AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            select cpv.rangeelementid, e.classid
            into eID, cID
            FROM CONCEPTPROPERTYVERSION cpv
            join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
            join strelementversion sev on t.textpropertyid = sev.elementversionid
            join strelementversion1 sev1 on cpv.conceptpropertyid = sev1.elementversionid
            join element e on t.domainelementid = e.elementid
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID;

--            if (eID is null) then
--                cID := chapterClassID;
--            end if;
        END LOOP;

        return eID;

    exception
        when others then
            return null;

    end retrievePagebyIdForFolio;

     /**************************************************************************************************************************************
    * NAME:          retrieveContainingPagebyId
    * DESCRIPTION:   Retrieve the Chapter Element ID
    **************************************************************************************************************************************/
    FUNCTION retrieveContainingPagebyId(baseClassification varchar2, contextId number, elemId number)
        RETURN NUMBER
    IS
        eID number := 0;
        cID number := 0;

        codeClassID number;
        chapterClassID number;
        narrowClassID number;

        ICD_codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        ICD_chapterClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');

        CCI_codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        CCI_chapterClassID number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            codeClassID := ICD_codeClassID;
            chapterClassID := ICD_chapterClassID;
            narrowClassID := ICD_narrowClassID;
        elsif (baseClassification = 'CCI') then
            codeClassID := CCI_codeClassID;
            chapterClassID := CCI_chapterClassID;
            narrowClassID := CCI_narrowClassID;
        end if;

        select  e.elementid, e.classid
        into eID, cID
        from element e
        where e.elementid = elemId;

        while (cID != chapterClassID) LOOP

            WITH strelementversion AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            , strelementversion1 AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            select cpv.rangeelementid, e.classid
            into eID, cID
            FROM CONCEPTPROPERTYVERSION cpv
            join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
            join strelementversion sev on t.textpropertyid = sev.elementversionid
            join strelementversion1 sev1 on cpv.conceptpropertyid = sev1.elementversionid
            join element e on t.domainelementid = e.elementid
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID;

--            if (eID is null) then
--                cID := chapterClassID;
--            end if;
        END LOOP;

        return eID;

    exception
        when others then
            return null;

    end retrieveContainingPagebyId;


    /**************************************************************************************************************************************
    * NAME:          retrieveContainingIdPathbyCode
    * DESCRIPTION:   Retrieve the ContainingIdPath by passing any code,
    *  return value like '37/57', here 37 is chapter element Id, 57 is block element Id
    **************************************************************************************************************************************/
    FUNCTION retrieveContainingIdPathbyCode(baseClassification varchar2, contextId number, cat_code varchar2)
        RETURN VARCHAR2
    IS

        eID number := 0;
        cID number := 0;
        idPath VARCHAR2(300) ;
        codeClassID number;

        narrowClassID number;
        rootElementID number:= getClassificationRootId(baseClassification);

        ICD_codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        CCI_codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');

    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            codeClassID := ICD_codeClassID;
            narrowClassID := ICD_narrowClassID;
        elsif (baseClassification = 'CCI') then
            codeClassID := CCI_codeClassID;
            narrowClassID := CCI_narrowClassID;
        end if;

        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )
        select t.domainelementid, e.classid
        into eID, cID
        from textpropertyversion t
        join strelementversion sev on t.textpropertyid = sev.elementversionid
        join element e on t.domainelementid = e.elementid
        where t.text = cat_code
        and t.classid = codeClassID;

        idPath :=eID ;
        while (eID != rootElementID) LOOP

            WITH strelementversion AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            select cpv.rangeelementid
            into eId
            FROM CONCEPTPROPERTYVERSION cpv, strelementversion s
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID
            and s.elementversionid = cpv.conceptpropertyid;
           --dbms_output.put_line('1 ' || eID || ' ' || idPath);
            idPath := eID ||'/'||idPath ;
            --dbms_output.put_line('2 ' || idPath);

        END LOOP;

        return '/'||idPath;

    exception
        when others then
       -- dbms_output.put_line(SQLCODE || ' ' || SQLERRM);
        return null;

    end retrieveContainingIdPathbyCode;

   /**************************************************************************************************************************************
    * NAME:          retrieveContainingIdPathbyEId
    * DESCRIPTION:   Retrieve the ContainingIdPath by passing any elementId,
    *  return value like '37/57', here 37 is chapter element Id, 57 is block element Id
    **************************************************************************************************************************************/

    FUNCTION retrieveContainingIdPathbyEId(baseClassification varchar2, contextId number, elemId NUMBER)
        RETURN VARCHAR2
    IS
        eID number := 0;
        cID number := 0;
        idPath VARCHAR2(200) ;
        narrowClassID number;

        rootElementID number:= getClassificationRootId(baseClassification);
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            narrowClassID := ICD_narrowClassID;
        elsif (baseClassification = 'CCI') then
            narrowClassID := CCI_narrowClassID;
        end if;

        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )
        select e.elementid, e.classid
        into eID, cID
        from elementVersion ev
        join strelementversion sev on ev.elementversionid = sev.elementversionid
        join element e on ev.elementid = e.elementid
        where e.elementid = elemId;

        idPath :=eID ;
        while (eID != rootElementID) LOOP

        WITH strelementversion1 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )
            select cpv.rangeelementid
            into eId
            FROM CONCEPTPROPERTYVERSION cpv, strelementversion1 s
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID
            and s.elementversionid = cpv.conceptpropertyid;

            idPath := eID ||'/'||idPath ;

        END LOOP;

        return '/'||idPath;

    exception
        WHEN NO_DATA_FOUND then
          return null;
        when others then
          raise_application_error(-20012, ' cims_util.retrieveCodeNestingLevel Error! ' || substr(sqlerrm, 1, 512));

    end retrieveContainingIdPathbyEId;

    FUNCTION isCatDisplayedinTableAbove (pContextId number, pConceptId number, pLanguage varchar2) RETURN CHAR
      IS
             baseClassification CLASS.BASECLASSIFICATIONNAME%TYPE;
             baseContextId STRUCTUREVERSION.STRUCTUREID%TYPE;
             vClassId CLASS.CLASSID%TYPE;
             vCatClassId CLASS.CLASSID%TYPE := getClassIDForClassName(bcName => 'ICD-10-CA', cName => 'Category');
             vTablePresClassId CLASS.CLASSID%TYPE := getClassIDForClassName(bcName => 'ICD-10-CA', cName => 'TablePresentation');
             vElementId ConceptVersion.Elementid%TYPE;
      BEGIN
        BEGIN

        -- this function is specific only to ICD-10-CA Categories
        -- need to make sure that the concept class is Category

        SELECT sv.basestructureid into baseContextId FROM STRUCTUREVERSION sv where sv.structureid = pContextId;
        select NVL2(baseContextId,baseContextId,pContextId) into baseContextId from dual;
        select distinct classid into vClassId from conceptversion cv , structureelementversion sev
        where cv.elementid = pConceptId
        and sev.elementversionid = cv.conceptid
        and sev.structureid in( baseContextId, pContextId);
        IF vClassId != vCatClassId THEN
          return 'N';
        END IF;
        exception when no_data_found then
         return 'N';
        END;
        vElementId := pConceptId;
        LOOP BEGIN
                WITH strelementversion AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = pContextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (
                    select basestructureid
                    from structureversion
                    where structureid = pContextId
                    )
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = pContextId
                    and cv.elementid = sv.elementid
                ))
                , strelementversion1 AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = pContextId
                UNION  ALL
                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (
                    select basestructureid
                    from structureversion
                    where structureid = pContextId
                    )
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = pContextId
                    and cv.elementid = sv.elementid
                ))
                select cv.elementid, cv.classid into vElementId, vClassId
                from conceptpropertyversion cpv, conceptversion cv
                ,strelementversion sev, strelementversion1 sev1
                where cpv.domainelementid = vElementId
                and cpv.rangeelementid = cv.elementid
                and cv.classid = vCatClassId
                and cpv.conceptpropertyid = sev.elementversionid
                and cv.conceptid = sev1.elementversionid
                ;
                IF trim(getHTMLProperty( vElementId , 'TablePresentation' , pContextId , pLanguage)) is not null THEN
                   RETURN 'Y';
                END IF;
         EXCEPTION WHEN NO_DATA_FOUND THEN
                  RETURN 'N';
                  END;
         END LOOP;
                return 'N';
      END;

    /**************************************************************************************************************************************
    * NAME:          retrieveCodeNestingLevel
    * DESCRIPTION:   Retrieve the code nesting level.  Chapters are nesting level 1
    **************************************************************************************************************************************/
    FUNCTION retrieveCodeNestingLevel(baseClassification varchar2, contextId number, elemId number)
        RETURN NUMBER
    IS
        eID number := 0;
        cID number := 0;
        elementClassID number := 0;

        nestingLevel number := 1;

        codeClassID number;
        narrowClassID number;

        ICD_codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');

        CCI_codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');

    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            codeClassID := ICD_codeClassID;
            narrowClassID := ICD_narrowClassID;
        elsif (baseClassification = 'CCI') then
            codeClassID := CCI_codeClassID;
            narrowClassID := CCI_narrowClassID;
        end if;

        /* START CHANGE CONTEXT STRUCTUREVERSION */
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId

            UNION  ALL

            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (
                select basestructureid
                from structureversion
                where structureid = contextId
                )
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        ) /* END CHANGE CONTEXT STRUCTUREVERSION */
        select t.domainelementid, e.classid
        into eID, elementClassID
        from textpropertyversion t
        join strelementversion sev on t.textpropertyid = sev.elementversionid
        join element e on t.domainelementid = e.elementid
        where t.domainelementid = elemId
        and t.classid = codeClassID;

        cID := elementClassID;
        while (cID = elementClassID) LOOP

            WITH strelementversion AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId

                UNION  ALL

                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (
                    select basestructureid
                    from structureversion
                    where structureid = contextId
                    )
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            ), strelementversion1 AS (
                select elementversionid, elementid
                from structureelementversion
                where structureid = contextId

                UNION  ALL

                select elementversionid, elementid
                from structureelementversion sv
                where sv.structureid = (
                    select basestructureid
                    from structureversion
                    where structureid = contextId
                    )
                and not exists (
                    select elementid
                    from structureelementversion cv
                    where cv.structureid = contextId
                    and cv.elementid = sv.elementid
                )
            )
            select cpv.rangeelementid, e.classid
            into eID, cID
            FROM CONCEPTPROPERTYVERSION cpv
            join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
            join strelementversion sev on t.textpropertyid = sev.elementversionid
            join strelementversion1 sev1 on cpv.conceptpropertyid = sev1.elementversionid
            join element e on t.domainelementid = e.elementid
            WHERE cpv.classid = narrowClassID
            and cpv.domainelementid = eID;

            if (cID = elementClassID) then
                nestingLevel := nestingLevel + 1;
            end if;


        END LOOP;

        return nestingLevel;
    exception
        WHEN NO_DATA_FOUND then
          return nestingLevel;
        when others then
          raise_application_error(-20011, ' cims_util.retrieveCodeNestingLevel Error! ' || substr(sqlerrm, 1, 512));

     end retrieveCodeNestingLevel;



    /**************************************************************************************************************************************
    * NAME:          numberOfChildrenWithValidation
    * DESCRIPTION:   Returns a count of children who have validation.
    *                Note:  With the addition of change contexts, this thing is slow.  Please try not to use it.
    **************************************************************************************************************************************/
    FUNCTION numberOfChildrenWithValidation(baseClassification varchar2, contextId number, conceptCode varchar2)
        RETURN NUMBER
    IS
        numCount number;

        codeClassID number;
        narrowClassID number;
        validationCPVClassID number;
        codeElementID number;

        ICD_codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
        ICD_narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
        ICD_validationCPVClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'ValidationICDCPV');

        CCI_codeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
        CCI_narrowClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
        CCI_validationCPVClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'ValidationCCICPV');

    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            codeClassID := ICD_codeClassID;
            narrowClassID := ICD_narrowClassID;
            validationCPVClassID := ICD_validationCPVClassID;
        elsif (baseClassification = 'CCI') then
            codeClassID := CCI_codeClassID;
            narrowClassID := CCI_narrowClassID;
            validationCPVClassID := CCI_validationCPVClassID;
        end if;

        SELECT domainElementID
        INTO codeElementID
        FROM TEXTPROPERTYVERSION t
        WHERE t.text = conceptCode
        AND t.classid = codeClassID;


        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        ),
        strelementversion1 AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )
        SELECT count(distinct rangeElementID)
        into numCount
        FROM CONCEPTPROPERTYVERSION cpv
        WHERE cpv.classid = validationCPVClassID
        and cpv.status = 'ACTIVE'
        and cpv.rangeelementid IN
        (
            select elementid from (
                WITH elementPropertys AS (

                    SELECT cp.domainelementid elementid, cp.rangeelementid ParentElementID, t.text
                    FROM CONCEPTPROPERTYVERSION cp
                    join strelementversion se on cp.conceptpropertyid = se.elementversionid
                    join TEXTPROPERTYVERSION t on cp.domainelementid = t.domainelementid and t.classid = codeClassID and t.status = 'ACTIVE'
                    join strelementversion1 sev1 on t.textpropertyid = sev1.elementversionid
                    WHERE cp.status = 'ACTIVE'
                    and cp.classid = narrowClassID
                    )
                SELECT ep.elementid
                FROM elementPropertys ep
                CONNECT BY nocycle prior ep.elementID = ep.ParentElementID
--            start with ep.elementid = codeElementID
                start with ep.ParentElementID = codeElementID

              ORDER SIBLINGS BY ep.text
            )
        );


        return numCount;

    exception
        when others then
            return 0;

    end numberOfChildrenWithValidation;


    /**************************************************************************************************************************************
    * NAME:          getActiveChildCount
    * DESCRIPTION:   Returns count of child nodes that are in ACTIVE status
    **************************************************************************************************************************************/
    FUNCTION getActiveChildCount(baseClassification varchar2, structureVersionID number, parentElementId number) RETURN NUMBER IS
        activeChildCount NUMBER;

        ICD_concept1 number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
        ICD_concept2 number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Block');
        ICD_concept3 number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Category');

        CCI_concept1 number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
        CCI_concept2 number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Block');
        CCI_concept3 number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Group');
        CCI_concept4 number := CIMS_CCI.getCCIClassID('ConceptVersion', 'Rubric');
        CCI_concept5 number := CIMS_CCI.getCCIClassID('ConceptVersion', 'CCICODE');

        conceptClassId1 number := 0;
        conceptClassId2 number := 0;
        conceptClassId3 number := 0;
        conceptClassId4 number := 0;
        conceptClassId5 number := 0;

    BEGIN

        if (baseClassification = 'ICD-10-CA') then
            conceptClassId1 := ICD_concept1;
            conceptClassId2 := ICD_concept2;
            conceptClassId3 := ICD_concept3;
        elsif (baseClassification = 'CCI') then
            conceptClassId1 := CCI_concept1;
            conceptClassId2 := CCI_concept2;
            conceptClassId3 := CCI_concept3;
            conceptClassId4 := CCI_concept4;
            conceptClassId5 := CCI_concept5;
        end if;

        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = structureVersionID
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = structureVersionID)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = structureVersionID
                and cv.elementid = sv.elementid
            )
        )
        SELECT COUNT(*)
        INTO activeChildCount
        FROM conceptpropertyversion cpv, element e, strelementversion sev, elementversion ev
        WHERE cpv.rangeelementid = parentElementId
        and cpv.domainelementid = e.elementid
        and e.classid IN (conceptClassId1, conceptClassId2, conceptClassId3, conceptClassId4, conceptClassId5)
        and e.elementid = sev.elementid
        and sev.elementversionid = ev.elementversionid
        and ev.status = 'ACTIVE';


        RETURN activeChildCount;
    END getActiveChildCount;

    FUNCTION getIndexPath(indexElementId NUMBER,  contextId NUMBER) RETURN VARCHAR2
    IS
        baseClassification VARCHAR2(10);
        narrowClassId number;
        indexDescclassId number;
        rootClassId number;
        path VARCHAR2(3000);
        parentId number;
        clid number;
    BEGIN
      select c.baseclassificationname into baseClassification from structureversion s, class c
      where structureid=contextId and c.classid = s.classid;

      if (baseClassification = 'ICD-10-CA') then
         narrowClassId:= CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
         indexDescclassId:=CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'IndexDesc');
         rootClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ClassificationRoot');
      elsif (baseClassification = 'CCI') then
         narrowClassId:= CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
         indexDescclassId:=CIMS_CCI.getCCIClassID('TextPropertyVersion', 'IndexDesc');
         rootClassId := CIMS_CCI.getCCIClassID('ConceptVersion', 'ClassificationRoot');
      end if;

     parentId := indexElementId;
     loop
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )select tp.text || decode (path, null,'',' > '|| path) into path from textpropertyversion tp, strelementversion sev
          where sev.elementversionid = tp.textpropertyid
          and tp.domainelementid = parentId
          and tp.classid = indexDescclassId;

          WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
          ) select rangeelementid into parentId
          from conceptpropertyversion narrowcpv, strelementversion sev
          where narrowcpv.classid = narrowClassId
          and sev.elementversionid = narrowcpv.conceptpropertyid
          and narrowcpv.domainelementid = parentId;

          if parentid is not null then
            select classid into clid from element where elementid=parentId;
            if clid = rootClassId then
              return path;
            end if;
          end if;

     end loop;
    END;

    FUNCTION getSupplementPath(supplementId NUMBER,  contextId NUMBER) RETURN VARCHAR2
    IS
        baseClassification VARCHAR2(10);
        narrowClassId number;
        supplementDescclassId number;
        rootClassId number;
        path VARCHAR2(3000);
        parentId number;
        clid number;
    BEGIN
      select c.baseclassificationname into baseClassification from structureversion s, class c
      where structureid=contextId and c.classid = s.classid;

      if (baseClassification = 'ICD-10-CA') then
         narrowClassId:= CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
         supplementDescclassId:=CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'SupplementDescription');
         rootClassId := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'ClassificationRoot');
      elsif (baseClassification = 'CCI') then
         narrowClassId:= CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
         supplementDescclassId:=CIMS_CCI.getCCIClassID('TextPropertyVersion', 'SupplementDescription');
         rootClassId := CIMS_CCI.getCCIClassID('ConceptVersion', 'ClassificationRoot');
      end if;

     parentId := supplementId;
     loop
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
        )select tp.text || decode (path, null,'',' > '|| path) into path from textpropertyversion tp, strelementversion sev
          where sev.elementversionid = tp.textpropertyid
          and tp.domainelementid = parentId
          and tp.classid = supplementDescclassId;

          WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = contextId
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = contextId)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = contextId
                and cv.elementid = sv.elementid
            )
          ) select rangeelementid into parentId
          from conceptpropertyversion narrowcpv, strelementversion sev
          where narrowcpv.classid = narrowClassId
          and sev.elementversionid = narrowcpv.conceptpropertyid
          and narrowcpv.domainelementid = parentId;

          if parentid is not null then
            select classid into clid from element where elementid=parentId;
            if clid = rootClassId then
              return path;
            end if;
          end if;

     end loop;
    END;




    FUNCTION testingOracleIssue(contextId number, unitConceptId varchar2, classification varchar2, language varchar2, requestId number, narrowClassId number, codeClassId number
      , indexDescClassId  number, levelClassId number, longPresentationClassId number, tablePresentationClassId number ) RETURN CIMS_UTIL.ref_cursor

    IS
        rootNodeData_cursor CIMS_UTIL.ref_cursor;

        refAttrCPVClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'ReferenceAttributeCPV');
        genAttrCPVClassID number := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'GenericAttributeCPV');
        attrDescClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeDescription');
        attrCodeClassID number := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'AttributeCode');

    BEGIN
        OPEN rootNodeData_cursor FOR
       with strelementversion as
    (
    select /*+inline*/elementversionid, elementid from (
    select elementversionid, elementid from structureelementversion where structureid=contextId
    UNION  ALL
    select elementversionid, elementid  from structureelementversion sv where sv.structureid=(select basestructureid from structureversion where structureid=contextId)
    and not exists (
    select elementid from structureelementversion cv where cv.structureid=contextId
    and cv.elementid = sv.elementid
    ))
    )
    , concepthierarchy AS (
      select narrow_cp.rangeelementid parent_elid, narrow_cp.domainelementid child_elid, tp_code.text code
      FROM CONCEPTPROPERTYVERSION narrow_cp, STRELEMENTVERSION strelv_cp
        , TEXTPROPERTYVERSION tp_code, STRELEMENTVERSION strelv_tp
        , STRELEMENTVERSION strelv_child
        , Conceptversion child_cptv
      WHERE narrow_cp.conceptpropertyid = narrow_cp.conceptpropertyid
      and narrow_cp.conceptpropertyid = strelv_cp.elementversionid
      and narrow_cp.classid =  narrowClassId
      and tp_code.domainelementid = narrow_cp.domainelementid
      and tp_code.textpropertyid = strelv_tp.elementversionid
      and tp_code.classid in ( codeClassId, indexDescClassId)
      and child_cptv.elementid = tp_code.domainelementid
      and strelv_child.elementversionid = child_cptv.conceptid
      and tp_code.status = 'ACTIVE'
      and narrow_cp.status = 'ACTIVE'
      and child_cptv.status = 'ACTIVE'
    )
    , htmlprops as(
    select h.htmltext htmlText, h.domainelementid from htmlpropertyversion h , strelementversion strelh
    where strelh.elementversionid = h.htmlpropertyid
    and h.languagecode = language
    and h.classid = longPresentationClassId
    )
    , numprops as(
    select n.numericvalue, n.domainelementid from numericpropertyversion n, strelementversion streln
    where streln.elementversionid = n.numericpropertyid
    and n.classid = levelClassId
    )
    , tableprops as(
    select h.htmltext htmlText, h.domainelementid from htmlpropertyversion h , strelementversion strelh
    where strelh.elementversionid = h.htmlpropertyid
    and h.languagecode = language
    and h.classid = tablePresentationClassId
    )
    ,con as (
         SELECT  child_elid elementid, parent_elid parentelementid,code text, h.htmlText, n.numericvalue
     FROM concepthierarchy ep, htmlprops h, numprops n, tableprops tb
     where child_elid = h.domainelementid(+)
     and child_elid=n.domainelementid(+)
     and parent_elid=tb.domainelementid(+)
    CONNECT BY nocycle prior child_elid = parent_elid and tb.htmltext is null

    start with child_elid = unitConceptId
    ORDER SIBLINGS BY  NLSSORT(lower(decode(numericvalue, 2 ,decode(instr(code,'with '),1,'a '||code,code),code)), 'NLS_SORT=generic_baseletter')

    )
    select c.elementid elementid, c.parentelementid parentelementid,c.text text ,c.htmlText htmlText
    from con c;
        RETURN rootNodeData_cursor;
    END;

        PROCEDURE runStats IS
          BEGIN
            execute immediate 'analyze table structureelementversion estimate statistics';
          END;

    /**************************************************************************************************************************************
    * NAME:          startswithWithAvec
    * DESCRIPTION:   Returns 'Y' if the input string starts with word 'with' or 'avec'
    **************************************************************************************************************************************/
    FUNCTION startswithWithOrAvec (pStr Textpropertyversion.text%type) RETURN CHAR
    IS
      vStr VARCHAR2(10);
    BEGIN
      IF length (regexp_substr(pStr || ' ', '(\Aavec[^a-z])|(\Awith[^a-z])',1,1,'i')) > 1 THEN
         return 'Y';
      END IF;
      return 'N';
      exception when others then
        return 'N';
    END;

    /**************************************************************************************************************************************
    * NAME:          getBooleanProperty
    * DESCRIPTION:   Returns boolean value text for a specified BooleanProperty
    **************************************************************************************************************************************/
    FUNCTION getBooleanProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER) return CHAR IS
         boolvalue CHAR;
       BEGIN
        WITH strelementversion AS (
            select elementversionid, elementid
            from structureelementversion
            where structureid = strid
            UNION  ALL
            select elementversionid, elementid
            from structureelementversion sv
            where sv.structureid = (select basestructureid from structureversion where structureid = strid)
            and not exists (
                select elementid
                from structureelementversion cv
                where cv.structureid = strid
                and cv.elementid = sv.elementid
            )
        )
         SELECT bpv.booleanvalue INTO boolvalue FROM
         booleanpropertyversion bpv, propertyversion pv, strelementversion strev, class c, elementversion ev, element e
         WHERE bpv.booleanpropertyid = pv.propertyid
         AND pv.domainelementid = elemId
         AND strev.elementversionid = pv.propertyid
         AND pv.propertyid = ev.elementversionid
         AND ev.elementid = e.elementid
         AND c.classid = e.classid
         AND c.classname = classnme;
         RETURN boolvalue;
   exception
        WHEN NO_DATA_FOUND then
          return '';
        when others then
          raise_application_error(-20011, ' cims_util.getBooleanProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
   END getBooleanProperty;

    /**************************************************************************************************************************************
    * NAME:          getChangeRequestFromStatus
    * DESCRIPTION:   Returns from status of a specified change request history id
    **************************************************************************************************************************************/
    FUNCTION getChangeRequestFromStatus(changeRequestHistoryId NUMBER, changeRequestId NUMBER) RETURN CLOB
    IS
      vStr CLOB;
    BEGIN
      select item into vStr
      from
      (select item
              from change_request_history_item crhi, change_request_history crh
              where crh.change_request_history_id=crhi.change_request_history_id and crhi.label_code='RequestStatus'
                    and crh.change_request_id=changeRequestId and crh.change_request_history_id<changeRequestHistoryId order by crh.change_request_history_id desc)
      where rownum=1;
      return vStr;
    END getChangeRequestFromStatus;

     /**************************************************************************************************************************************
    * NAME:          getChangeRequestFromStatus
    * DESCRIPTION:   Returns from status of a specified change request history id
    **************************************************************************************************************************************/
    FUNCTION getChangeRequestHistoryOwner(changeRequestHistoryId NUMBER, changeRequestId NUMBER) RETURN CLOB
    IS
      vStr CLOB;
    BEGIN
      select item into vStr
      from
      (select item
              from change_request_history_item crhi, change_request_history crh
              where crh.change_request_history_id=crhi.change_request_history_id and crhi.label_code='Owner'
                    and crh.change_request_id=changeRequestId and crh.change_request_history_id<changeRequestHistoryId order by crh.change_request_history_id desc)
      where rownum=1;
      return vStr;
    END getChangeRequestHistoryOwner;

    /**************************************************************************************************************************************
    * NAME:          getXMLProperty
    * DESCRIPTION:   Returns the text for a specified HTMLProperty
    **************************************************************************************************************************************/
     FUNCTION getXMLProperty(elemId NUMBER, classnme VARCHAR2, strid NUMBER, language VARCHAR2) return XMLPROPERTYVERSION.XMLTEXT%TYPE IS
       xml XMLPROPERTYVERSION.XMLTEXT%TYPE;
     BEGIN
      WITH strelementversion AS (
          select elementversionid, elementid
          from structureelementversion
          where structureid = strid
          UNION  ALL
          select elementversionid, elementid
          from structureelementversion sv
          where sv.structureid = (select basestructureid from structureversion where structureid = strid)
          and not exists (
              select elementid
              from structureelementversion cv
              where cv.structureid = strid
              and cv.elementid = sv.elementid
          )
      )
       SELECT xpv.xmltext INTO xml FROM
       xmlpropertyversion xpv, propertyversion pv, strelementversion strev, class c, elementversion ev, element e
       WHERE xpv.xmlpropertyid = pv.propertyid
       AND pv.domainelementid = elemId
       AND strev.elementversionid = pv.propertyid
       AND nvl(xpv.languagecode,'XX') = nvl(LANGUAGE,'XX')
       AND pv.propertyid = ev.elementversionid
       AND ev.elementid = e.elementid
       AND c.classid = e.classid
       AND c.classname = classnme;
       RETURN xml;
     exception
      WHEN NO_DATA_FOUND then
        return '';
      when others then
        raise_application_error(-20011, ' cims_util.getXMLProperty Error: Unexpected error! ' || substr(sqlerrm, 1, 512));
     END getXMLProperty;

    /**************************************************************************************************************************************
    * NAME:          html_to_xhtml
    * DESCRIPTION:   Converts encoded HTML text into proper encoded XHTML
    **************************************************************************************************************************************/
    FUNCTION html_to_xhtml(html CLOB) RETURN CLOB IS
       xhtml CLOB;
    BEGIN
      xhtml := REPLACE(html,'&#39;','&apos;');
      xhtml := REPLACE(xhtml,'&nbsp;',' ');
      RETURN xhtml;
    END html_to_xhtml;

    /**************************************************************************************************************************************
    * NAME:          to_xhtml
    * DESCRIPTION:   Converts text into proper encoded XHTML
    **************************************************************************************************************************************/
    FUNCTION to_xhtml(text IN VARCHAR2) RETURN VARCHAR2 IS
      OUT CLOB;
      TYPE ARR_STRING IS VARRAY (38) OF VARCHAR2 (64);

      ENTITIES_SEARCH_FOR   ARR_STRING;
      ENTITIES_REPLACE      ARR_STRING;
      CONT                  NUMBER;
      BEGIN
         -- to accelerate the issue
         IF text IS NULL
         THEN
            RETURN text;
         END IF;

         ENTITIES_REPLACE :=
            ARR_STRING ( '&Agrave;',
                         '&AGRAVE;',
                         '&agrave;',
                         '&Aacute;',
                         '&AACUTE;',
                         '&aacute;',
                         '&Egrave;',
                         '&EGRAVE;',
                         '&egrave;',
                         '&Eacute;',
                         '&EACUTE;',
                         '&eacute;',
                         '&Igrave;',
                         '&IGRAVE;',
                         '&igrave;',
                         '&Iacute;',
                         '&IACUTE;',
                         '&iacute;',
                         '&Ograve;',
                         '&OGRAVE;',
                         '&ograve;',
                         '&Oacute;',
                         '&OACUTE;',
                         '&oacute;',
                         '&Ugrave;',
                         '&UGRAVE;',
                         '&ugrave;',
                         '&Uacute;',
                         '&UACUTE;',
                         '&uacute;',
                         '&laquo;',
                         '&LAQUO;',
                         '&raquo;',
                         '&RAQUO;',
                         '&euro;',
                         '&EURO;',
                         '&deg;',
                         '&DEG;' );

        ENTITIES_SEARCH_FOR  :=
            ARR_STRING ( 'À',
                         'À',
                         'à',
                         'Á',
                         'Á',
                         'á',
                         'È',
                         'È',
                         'è',
                         'É',
                         'É',
                         'é',
                         'Ì',
                         'Ì',
                         'ì',
                         'Í',
                         'Í',
                         'í',
                         'Ò',
                         'Ò',
                         'ò',
                         'Ó',
                         'Ó',
                         'ó',
                         'Ù',
                         'Ù',
                         'ù',
                         'Ú',
                         'Ú',
                         'ú',
                         '«',
                         '«',
                         '»',
                         '»',
                         '¿',
                         '¿',
                         '°',
                         '°' );

         OUT := UTL_I18N.ESCAPE_REFERENCE(text);

         FOR CONT IN 1 .. 38
         LOOP
            OUT         :=
               REPLACE ( OUT,
                         ENTITIES_SEARCH_FOR ( CONT ),
                         ENTITIES_REPLACE ( CONT ) );
         END LOOP;

       RETURN (OUT);
    END to_xhtml;

    /**************************************************************************************************************************************
    * NAME:          getInitialElementVersionIdCR
    * DESCRIPTION:   Returns the first elementversion ID for a given element and change request
    **************************************************************************************************************************************/
     FUNCTION getInitialElementVersionIdCR(v_elementId NUMBER, v_changeRequestId NUMBER) RETURN NUMBER IS
       v_elementversion elementversion%ROWTYPE;
     BEGIN
       IF v_elementId is null THEN RETURN null; END IF;
       SELECT * into v_elementversion FROM (
         select ev.*
         FROM structureelementversion sev, structureversion sv, elementversion ev
         WHERE sev.structureid = sv.structureid
         and sev.elementid = v_elementId
         and sv.change_request_id = v_changeRequestId
         and sev.elementid = ev.elementid
         and sev.elementversionid = ev.elementversionid
         order by ev.elementversionid
       )
       where rownum = 1;
       RETURN v_elementversion.changedfromversionid;
     END getInitialElementVersionIdCR;

     /**************************************************************************************************************************************
    * NAME:          getChangeRequestCount
    * DESCRIPTION:   Returns the number of change requests in all Open Year for the given conceptId
    **************************************************************************************************************************************/
     FUNCTION getChangeRequestCount(nConceptId NUMBER) RETURN NUMBER IS
       nCount NUMBER;
     BEGIN
       with changedElement as  (
      select  /*+ RESULT_CACHE*/ change_request_id, conceptid from (
        select cr.change_request_id, pv.domainelementid as conceptid from change_request cr, structureversion sv, structureelementversion sev, propertyversion pv
          where sv.basestructureid in (select structureid from structureversion s where s.contextstatus='OPEN') and sv.change_request_id=cr.change_request_id and cr.change_request_status_id not in (6,7,8)
                and sv.structureid =sev.structureid and sev.elementversionid=pv.propertyid
            and pv.classid not in (select classid from class c where className in('LongPresentation',
                                                                                  'ShortPresentation',
                                                                                   'SexValidationIndicator',
                                                                                   'ValidationMRDiag',
                                                                                   'ValidationDiagType1Flag',
                                                                                   'ValidationDiagType2Flag',
                                                                                   'ValidationDiagType3Flag',
                                                                                   'ValidationDiagType4Flag',
                                                                                   'ValidationDiagType6Flag',
                                                                                   'ValidationDiagType9Flag',
                                                                                   'ValidationDiagTypeWFlag',
                                                                                   'ValidationDiagTypeXFlag',
                                                                                   'ValidationDiagTypeYFlag',
                                                                                   'ValidationNewbornFlag',
                                                                                   'ValidationICDCPV',
                                                                                   'ValidationCCICPV',
                                                                                   'ValidationFacility',
                                                                                   'ValidationDefinition',
                                                                                   'AgeMinimum',
                                                                                   'AgeMaximum'))

            union
            select cr.change_request_id, cv.elementid from change_request cr, structureversion sv, structureelementversion sev, conceptversion cv
          where sv.basestructureid in (select structureid from structureversion s where s.contextstatus='OPEN') and sv.change_request_id=cr.change_request_id and cr.change_request_status_id not in (6,7,8)
                and sv.structureid =sev.structureid and sev.elementversionid=cv.conceptid
            and cv.classid not in (select classid from class c where className in( 'SexValidation',
                                                                       'ValidationICD',
                                                                       'ValidationCCI',
                                                                       'Validation'
                                                                       ) )

            union
            select cr.change_request_id,cims_util.getValidationRuleTabularId(sv.structureid, xpv.domainelementid) from change_request cr, structureversion sv, structureelementversion sev, xmlpropertyversion xpv
            where sv.basestructureid in (select structureid from structureversion s where s.contextstatus='OPEN') and sv.change_request_id=cr.change_request_id and cr.change_request_status_id not in (6,7,8)
                and sv.structureid =sev.structureid and sev.elementversionid=xpv.xmlpropertyid
                and xpv.classid in (select classid from class c where className ='ValidationDefinition')

            union
            select cr.change_request_id, cims_util.getValidationRuleTabularId(sv.structureid, cv.elementid) from change_request cr, structureversion sv, structureelementversion sev, conceptversion cv
            where sv.basestructureid in (select structureid from structureversion s where s.contextstatus='OPEN') and sv.change_request_id=cr.change_request_id and cr.change_request_status_id not in (6,7,8)
                and sv.structureid =sev.structureid and sev.elementversionid=cv.conceptid
            and cv.classid in (select classid from class c where className in ('ValidationICD', 'ValidationCCI' ) )
         ))
         select count(change_request_id) into nCount from changedElement c where c.conceptid=nConceptId;
       RETURN nCount;
     END getChangeRequestCount;

     FUNCTION getChapterOrSectionID(sClassification VARCHAR2, nContextId NUMBER, sConceptCode VARCHAR2) RETURN NUMBER IS
          eID number := 0;
          cID number := 0;

          codeClassID number := CIMS_ICD.getICD10CAClassID('TextPropertyVersion', 'Code');
          chapterClassID number := CIMS_ICD.getICD10CAClassID('ConceptVersion', 'Chapter');
          narrowClassID number := CIMS_ICD.getICD10CAClassID('ConceptPropertyVersion', 'Narrower');
     BEGIN
          if sClassification='CCI' then
              codeClassID  := CIMS_CCI.getCCIClassID('TextPropertyVersion', 'Code');
              chapterClassID  := CIMS_CCI.getCCIClassID('ConceptVersion', 'Section');
              narrowClassID  := CIMS_CCI.getCCIClassID('ConceptPropertyVersion', 'Narrower');
          end if;

          select t.domainelementid, e.classid
          into eID, cID
          from textpropertyversion t
          join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = nContextId
          join element e on t.domainelementid = e.elementid
          where t.text = sConceptCode
          and t.classid = codeClassID;

          while (cID != chapterClassID) LOOP
              select cpv.rangeelementid, e.classid
              into eID, cID
              FROM CONCEPTPROPERTYVERSION cpv
              join textpropertyversion t on cpv.rangeelementid = t.domainelementid and t.classid = codeClassID
              join structureelementversion sev on t.textpropertyid = sev.elementversionid and sev.structureid = nContextId
              join element e on t.domainelementid = e.elementid
              WHERE cpv.classid = narrowClassID
              and cpv.domainelementid = eID;

          END LOOP;

        return eID;
     END getChapterOrSectionID;

     FUNCTION findAncestorId(nContextId NUMBER, nAncestorClasssId NUMBER, nRelationshipClasssId NUMBER, nConceptId NUMBER) RETURN NUMBER IS
          eID number := 0;
          cID number := 0;
     BEGIN

          eID:=nConceptID;


          select distinct c.classid
          into  cID
          from conceptVersion c
          where c.elementId=nConceptId;

          while (cID != nAncestorClasssId) LOOP
              select cpv.rangeelementid, e.classid
              into eID, cID
              FROM CONCEPTPROPERTYVERSION cpv
              join structureelementversion sev on cpv.conceptpropertyid= sev.elementversionid and sev.structureid = nContextId
              join element e on cpv.rangeelementid = e.elementid
              WHERE cpv.classid = nRelationshipClasssId
              and cpv.domainelementid = eID;

             --dbms_output.put_line(cID);
          END LOOP;

        return eID;
     END findAncestorId;
END CIMS_UTIL;
/


spool off
