package ca.cihi.cims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StopWatch;
import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.content.icd.IcdValidationXml;
import ca.cihi.cims.data.mapper.ContentDisplayMapper;
import ca.cihi.cims.model.CciCodeValidation;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.IcdCodeValidation;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.IndexBookReferencedLink;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.model.TabularReferencedLink;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.CCIComponentSupplement;
import ca.cihi.cims.model.sgsc.CCIRubric;
import ca.cihi.cims.model.tabular.TabularConceptDetails;
import ca.cihi.cims.service.sgsc.SGSCService;
import ca.cihi.cims.util.XmlUtils;

//TODO this class requires a lot of cleanup when time allows
public class ViewServiceImpl implements ViewService {

	private static final Log LOGGER = LogFactory.getLog(ViewServiceImpl.class);
	public static String SEARCHBY_BOOKINDEX = "bookIndex";
	public static String SEARCHBY_CODE = "code";
	private Map<String, Long> priorContentID = new HashMap<>();


	// private ContentDisplayDAO contentDisplayDAO;
	// mapper is dao
	private ContentDisplayMapper contentDisplayMapper;

	private String contentListWithChildrenSql;

	private LookupService lookupService;

	private SGSCService sgscService;

	private SqlSessionFactory sqlSessionFactory;

	@Override
	public List<CCIRubric> findCCIRubric(long contextId, String sectionCode, String groupCode) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("codeClassId", getCCIClassID("TextPropertyVersion", "Code"));
		paramMap.put("contextId", contextId);
		paramMap.put("rubricClassId", getCCIClassID("ConceptVersion", "Rubric"));
		paramMap.put("interventionCPVClassId", getCCIClassID("ConceptPropertyVersion", "InterventionCPV"));
		paramMap.put("groupCompCPVClassId", getCCIClassID("ConceptPropertyVersion", "GroupCompCPV"));
		paramMap.put("interventionClassId", getCCIClassID("ConceptVersion", "Intervention"));
		paramMap.put("groupCompClassId", getCCIClassID("ConceptVersion", "GroupComp"));
		paramMap.put("componentCodeClassId", getCCIClassID("TextPropertyVersion", "ComponentCode"));
		paramMap.put("sectionCode", sectionCode);
		paramMap.put("groupCode", groupCode);
		return contentDisplayMapper.findCCIRubric(paramMap);
	}

	@Override
	@Cacheable("BOOK_INDEXES_CACHE")
	public List<CodeDescription> getAllBookIndexes(String classification, Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getAllBookIndexes(parameters);
	}

	@Override
	@Cacheable("BOOK_INDEXES_CACHE_ALL")
	public List<CodeDescription> getAllBookIndexesNoLang(String classification) {
		Long currentContextId = lookupService.findBaseContextIdentifierByClassificationAndYear(classification,
				lookupService.findCurrentOpenYear(classification) + "").getContextId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("contextId", currentContextId);
		return contentDisplayMapper.getAllBookIndexesNoLang(parameters);
	}

	@Override
	public List<ContentViewerModel> getAttributesFromReferenceCode(String refAttrCode, String classification,
			Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("refAttrCode", refAttrCode);
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("contextId", contextId);
		parameters.put("attrDescClassID", getCCIClassID("TextPropertyVersion", "AttributeDescription"));
		parameters.put("attrNoteClassID", getCCIClassID("TextPropertyVersion", "AttributeNote"));
		parameters.put("attrCodeClassID", getCCIClassID("TextPropertyVersion", "AttributeCode"));
		parameters.put("refAttrClassID", getCCIClassID("ConceptVersion", "ReferenceAttribute"));
		parameters.put("refAttrCPVClassID", getCCIClassID("ConceptPropertyVersion", "ReferenceAttributeCPV"));
		parameters.put("genAttrClassID", getCCIClassID("ConceptPropertyVersion", "GenericAttributeCPV"));
		return contentDisplayMapper.getAttributesFromReferenceCode(parameters);
	}

	public Long getBaseContextId(long contextId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getBaseContextId(parameters);
	}

	public Long getCategoryClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getCategoryClassId(parameters);
	}

	@Override
	public Long getCCIClassID(String tablename, String classname) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tablename", tablename);
		parameters.put("classname", classname);
		return contentDisplayMapper.getCCIClassID(parameters);
	}

	@Override
	public List<CCIComponentSupplement> getCciGroupComponentsWithDefinition(String language, long contextId,
			String sectionCode, String firstLetter) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", language);
		paramMap.put("contextId", contextId);
		paramMap.put("sectionId", getCCISectionId(sectionCode, contextId));
		paramMap.put("groupCode", firstLetter);
		paramMap.put("longDescriptionClassId", getCCIClassID("TextPropertyVersion", "ComponentLongTitle"));
		paramMap.put("componentCodeClassId", getCCIClassID("TextPropertyVersion", "ComponentCode"));
		paramMap.put("componentClassId", getCCIClassID("ConceptVersion", "GroupComp"));
		paramMap.put("longPresentationClassId", getCCIClassID("HTMLPropertyVersion", "LongPresentation"));
		paramMap.put("componentToSectionCPVClassId", getCCIClassID("ConceptPropertyVersion", "GroupCompToSectionCPV"));
		return contentDisplayMapper.getCciComponentsWithDefinitionForSupplement(paramMap);
	}

	@Override
	public String getCCIGroupTitle(Long conceptId, Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("conceptId", conceptId);
		parameters.put("language", language);
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getCCIGroupTitle(parameters);
	}

	@Override
	public List<CCIComponentSupplement> getCciInterventionComponentsWithDefinition(String language, long contextId,
			String sectionCode, String orderBy) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", language);
		paramMap.put("contextId", contextId);
		paramMap.put("sectionId", getCCISectionId(sectionCode, contextId));
		paramMap.put("groupCode", null);
		paramMap.put("longDescriptionClassId", getCCIClassID("TextPropertyVersion", "ComponentLongTitle"));
		paramMap.put("componentCodeClassId", getCCIClassID("TextPropertyVersion", "ComponentCode"));
		paramMap.put("componentClassId", getCCIClassID("ConceptVersion", "Intervention"));
		paramMap.put("longPresentationClassId", getCCIClassID("HTMLPropertyVersion", "LongPresentation"));
		paramMap.put("componentToSectionCPVClassId",
				getCCIClassID("ConceptPropertyVersion", "InterventionToSectionCPV"));
		paramMap.put("orderBy", orderBy);
		return contentDisplayMapper.getCciComponentsWithDefinitionForSupplement(paramMap);
	}

	@Override
	public List<CCIReferenceAttribute> getCCIReferenceAttributesForSupplement(Long contextId, String referenceType,
			String languageCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeTypeIndicatorClassId",
				getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params.put("attributeCodeClassId", getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("domainValueCodeClassId", getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params.put("referenceAttributeClassId", getCCIClassID(WebConstants.CONCEPT_VERSION, "ReferenceAttribute"));
		params.put("contextId", contextId);
		params.put("attributeType", referenceType);
		params.put("languageCode", languageCode);

		return contentDisplayMapper.getCCIReferenceAttributesForSupplement(params);
	}

	private Long getCCISectionId(String sectionCode, long contextId) {
		return contentDisplayMapper.getCCISectionId(sectionCode, contextId);
	}

	public Long getCodeClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getCodeClassId(parameters);
		// return (Long) contentDisplayMapper.getSqlSession().selectOne("View.getCodeClassId", parameters);
	}

	/*
	 * public List<IcdCodeValidation> getHierICDValidationRules(String conceptCode,String classification, String
	 * contextId, String language){ List<IcdCodeValidation> myList = null;
	 *
	 * String conceptId = contentDisplayDAO.getConceptIdFromCode(conceptCode, contextId); if (conceptId != null &&
	 * conceptId.length()>0){ myList = contentDisplayDAO.getHierICDValidationRulesFromConceptId(conceptId, contextId,
	 * language); } return myList; }
	 */
	@Override
	public String getConceptCode(String conceptId, Long contextId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("unitConceptId", conceptId);
		parameters.put("language", null);
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getConceptCode(parameters);
	}

	public String getConceptIdFromCode(String code, String contextId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("code", code);
		return contentDisplayMapper.getConceptIdFromCode(parameters);
	}

	@Override
	public String getConceptIdPathByElementId(String classification, Long contextId, Long elementId) {
		return contentDisplayMapper.getConceptIdPathByElementId(classification, contextId, elementId);
	}

	public String getConceptMajorType(String elementId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("elementId", elementId);
		return contentDisplayMapper.getConceptMajorType(parameters);

	}

	@Override
	public String getConceptShortPresentation(String code, String classification, Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("code", code);
		parameters.put("codeClassId", getCodeClassId(classification));
		parameters.put("shortPresentationClassId", getShortPresentationClassId(classification));
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getShortPresentation(parameters);
	}

	@Override
	public Long getContainerPage(Long pConceptId, Long childConceptId, String classification, Long containerId,
			Long contextId) {
		Map<String, Object> params = new HashMap<>();
		params.put("pConceptId", pConceptId);
		params.put("childConceptId", childConceptId);
		params.put("classification", classification);
		params.put("containerId", containerId);
		params.put("contextId", contextId);
		return contentDisplayMapper.getContainerPage(params);
	}

	@Override
	public ContentDisplayMapper getContentDisplayMapper() {
		return contentDisplayMapper;
	}

	@Override
	public List<ContentViewerModel> getContentList(String unitConceptId, String classification, Long contextId,
			String language, String requestId, boolean withChildren, Boolean folio) {
		Map<String, Object> contextMap = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		List<ContentViewerModel> conceptList;
		contextMap.put("contextId", contextId);
		Long l = contentDisplayMapper.getBaseContextId(contextMap);
		parameters.put("unitConceptId", unitConceptId);
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("requestId", requestId);
		parameters.put("contextId", contextId);
		parameters.put("narrowClassId", getNarrowClassId(classification));
		parameters.put("codeClassId", getCodeClassId(classification));
		parameters.put("indexDescClassId", getIndexDescClassId(classification));
		parameters.put("supplementDescClassId", getSupplementDescClassId(classification));
		parameters.put("levelClassId", getLevelClassId(classification));
		parameters.put("longPresentationClassId", getLongPresentationClassId(classification));
		parameters.put("tablePresentationClassId", getTablePresentationClassId(classification));
		parameters.put("withChildren", withChildren);
		if ((l != null) && (l.longValue() > 0)) { // change request context
			if (!withChildren) {
				// special function for when one concept at a time disaplyes in Change Request Viewer as per requirement
				conceptList = contentDisplayMapper.getContentListWithoutChildren(parameters);
			} else { // withChildren
				// when multiple concepts are presented together on a page they can only be categories, groups and
				// rubrics
				// to optimize performance we are taking advantage of the concept code structure to identify and sort
				// concepts
				if ("TABULAR".equals(getConceptMajorType(unitConceptId))) {

					Map<String, Object> parameters1 = new HashMap<String, Object>();
					parameters1.put("unitConceptId", unitConceptId);
					parameters1.put("language", null);
					parameters1.put("contextId", contextId);
					String code = contentDisplayMapper.getConceptCode(parameters1);
					if (CIMSConstants.CCI.equals(classification)) {
						if ((code != null) && (code.length() >= 4)) {
							code = code.substring(0, 4);
						}

					}
					parameters.put("unitConceptCode", code);
					parameters.put("categoryClassId", getCategoryClassId(classification));
					parameters.put("rubricClassId", getRubricClassId(classification));
					parameters.put("groupClassId", getGroupClassId(classification));

					conceptList = contentDisplayMapper.getContentList(parameters);
				} else {
					conceptList = contentDisplayMapper.getContentListFromLeadTerm(parameters);
				}
			}
		} else {
			conceptList = contentDisplayMapper.getBaseContentList(parameters);
		}
		for (ContentViewerModel model : conceptList) {
			Long priorContextId = getPriorContextId(classification, contextId, !StringUtils.isEmpty(requestId));
			if (model!=null){
				model.setHtmlString(sgscService.replaceSystemGeneratedSupplementContent(
					model.getHtmlString() != null ? model.getHtmlString() : "", contextId, priorContextId, folio));
			}
		}
		return conceptList;
	}

	public List<ContentViewerModel> getContentListForJsonView(String unitConceptId, String classification, Long contextId, String language, String requestId, boolean withChildren, boolean folio) {
		List<ContentViewerModel> conceptList;
		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> parameters = new HashMap<>();
		contextMap.put("contextId", contextId);
		Long l = this.contentDisplayMapper.getBaseContextId(contextMap);
		parameters.put("unitConceptId", unitConceptId);
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("requestId", requestId);
		parameters.put("contextId", contextId);
		parameters.put("narrowClassId", getNarrowClassId(classification));
		parameters.put("codeClassId", getCodeClassId(classification));
		parameters.put("indexDescClassId", getIndexDescClassId(classification));
		parameters.put("supplementDescClassId", getSupplementDescClassId(classification));
		parameters.put("levelClassId", getLevelClassId(classification));
		parameters.put("longPresentationClassId", getLongPresentationClassId(classification));
		parameters.put("tablePresentationClassId", getTablePresentationClassId(classification));
		parameters.put("IncludePresentationClassId", getClassNameClassId(classification, "IncludePresentation"));
		parameters.put("ExcludePresentationClassId", getClassNameClassId(classification, "ExcludePresentation"));
		parameters.put("CodeAlsoPresentationClassId", getClassNameClassId(classification, "CodeAlsoPresentation"));
		parameters.put("NotePresentationClassId", getClassNameClassId(classification, "NotePresentation"));
		parameters.put("DefinitionPresentationClassId", getClassNameClassId(classification, "DefinitionPresentation"));
		parameters.put("LongTitleClassId", getClassNameClassId(classification, "LongTitle"));
		parameters.put("ShortTitleClassId", getClassNameClassId(classification, "ShortTitle"));
		parameters.put("UserTitleClassId", getClassNameClassId(classification, "UserTitle"));
		parameters.put("CaEnhancementIndicatorClassId", getClassNameClassId(classification, "CaEnhancementIndicator"));
		parameters.put("SupplementDefinitionClassId", getClassNameClassId(classification, "SupplementDefinition"));
		parameters.put("SupplementDescriptionClassId", getClassNameClassId(classification, "SupplementDescription"));
		parameters.put("IndexNoteDescClassId", getClassNameClassId(classification, "IndexNoteDesc"));
		parameters.put("IndexRefDefinitionClassId", getClassNameClassId(classification, "IndexRefDefinition"));
		parameters.put("OmitCodePresentationId", getClassNameClassId(classification, "OmitCodePresentation"));
		parameters.put("withChildren", Boolean.valueOf(withChildren));
		if (l != null && l.longValue() > 0L) {
		  if (!withChildren) {
			conceptList = this.contentDisplayMapper.getContentListWithoutChildren(parameters);
		  } else if ("TABULAR".equals(getConceptMajorType(unitConceptId))) {
			Map<String, Object> parameters1 = new HashMap<>();
			parameters1.put("unitConceptId", unitConceptId);
			parameters1.put("language", null);
			parameters1.put("contextId", contextId);
			String code = this.contentDisplayMapper.getConceptCode(parameters1);
			if ("CCI".equals(classification) && 
			  code != null && code.length() >= 4)
			  code = code.substring(0, 4); 
			parameters.put("unitConceptCode", code);
			parameters.put("categoryClassId", getCategoryClassId(classification));
			parameters.put("rubricClassId", getRubricClassId(classification));
			parameters.put("groupClassId", getGroupClassId(classification));
			conceptList = this.contentDisplayMapper.getContentList(parameters);
		  } else {
			conceptList = this.contentDisplayMapper.getContentListFromLeadTerm(parameters);
		  } 
		} else {
		  conceptList = this.contentDisplayMapper.getBaseContentList(parameters);
		} 
		for (ContentViewerModel model : conceptList) {
		  Long priorContextId = getPriorContextId(classification, contextId, !StringUtils.isEmpty(requestId));
		  if (model != null && model.getSupplementDefXmlText() != null && model.getSupplementDefXmlText().contains("<report")) {
			String newXmlContent = this.sgscService.replaceSystemGeneratedSupplementContent(model
				.getSupplementDefXmlText(), contextId, priorContextId, folio);
			model.setSupplementDefXmlText(newXmlContent);
		  } 
		} 
		return conceptList;
	  }
	

	public Long getClassNameClassId(String classification, String className) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("classification", classification);
		parameters.put("classname", className);
		return this.contentDisplayMapper.getClassNameClassId(parameters);
	}

	public String getContentListWithChildrenSql() {
		return contentListWithChildrenSql;
	}

	@Override
	public List<ContentToSynchronize> getContentToSynchronize(long contextId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getContentToSynchronize(parameters);
	}

	@Override
	public List<IdCodeDescription> getGenericAttributesForSupplement(String baseClassification, Long contextId,
			String attributeType, String languageCode) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", contextId);
		params.put("attributeType", attributeType);
		params.put("languageCode", languageCode);
		params.put("genericAttributeClassId", getCCIClassID("ConceptVersion", "GenericAttribute"));
		params.put("attributeTypeClassId", getCCIClassID("ConceptVersion", "AttributeType"));
		params.put("attributeTypeIndicatorClassId", getCCIClassID("ConceptPropertyVersion", "AttributeTypeIndicator"));
		params.put("descriptionClassId", getCCIClassID("TextPropertyVersion", "AttributeDescription"));
		params.put("codeClassId", getCCIClassID("TextPropertyVersion", "AttributeCode"));
		params.put("attributeTypeCodeClassId", getCCIClassID("TextPropertyVersion", "DomainValueCode"));

		return contentDisplayMapper.getGenericAttributesForSupplement(params);

	}

	public Long getGroupClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getGroupClassId(parameters);
	}

	@Override
	public List<CciCodeValidation> getHierCCIValidationRulesForRubric(String rubricCode, String classification,
			String contextId, String language) {
		List<CciCodeValidation> myList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("conceptCode", rubricCode);
		parameters.put("contextId", contextId);
		parameters.put("narrowClassId", getCCIClassID("ConceptPropertyVersion", "Narrower"));
		parameters.put("codeClassId", getCCIClassID("TextPropertyVersion", "Code"));
		parameters.put("validationCCICPVClassId", getCCIClassID("ConceptPropertyVersion", "ValidationCCICPV"));
		parameters.put("validationCCIClassId", getCCIClassID("ConceptVersion", "ValidationCCI"));
		parameters.put("facilityTypeClassId", getCCIClassID("ConceptVersion", "FacilityType"));
		parameters.put("validationDefClassId", getCCIClassID("XMLPropertyVersion", "ValidationDefinition"));
		parameters.put("language", language);
		parameters.put("domainValueDescription", this.getCCIClassID("TextPropertyVersion", "DomainValueDescription"));
		myList = contentDisplayMapper.getHierCCIValidationRulesFromConceptId(parameters);
		setCCIValidationData(myList, language);
		return myList;
	}

	@Override
	public List<IcdCodeValidation> getHierICDValidationRulesForCategory(String categoryCode, String classification,
			String contextId, String language) {
		List<IcdCodeValidation> myList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("conceptCode", categoryCode);
		parameters.put("contextId", contextId);
		parameters.put("narrowClassId", getICDClassID("ConceptPropertyVersion", "Narrower"));
		parameters.put("codeClassId", getICDClassID("TextPropertyVersion", "Code"));
		parameters.put("validationICDCPVClassId", getICDClassID("ConceptPropertyVersion", "ValidationICDCPV"));
		parameters.put("validationICDClassId", getICDClassID("ConceptVersion", "ValidationICD"));
		parameters.put("facilityTypeClassId", getICDClassID("ConceptVersion", "FacilityType"));
		parameters.put("validationDefClassId", getICDClassID("XMLPropertyVersion", "ValidationDefinition"));
		parameters.put("language", language);
		parameters.put("domainValueDescription", this.getICDClassID("TextPropertyVersion", "DomainValueDescription"));
		myList = contentDisplayMapper.getHierICDValidationRulesFromConceptId(parameters);
		setIcdValidationData(myList, language);
		return myList;
	}

	public Long getICDClassID(String tablename, String classname) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tablename", tablename);
		parameters.put("classname", classname);
		return contentDisplayMapper.getICDClassID(parameters);
	}

	@Override
	public List<IndexBookReferencedLink> getIndexBookReferencedLinks(long contextId, String codeValue,
			String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		boolean isNumeric = StringUtils.isNumeric(codeValue);
		if (isNumeric) {
			StringBuffer limitedCode = new StringBuffer().append("\\>").append(codeValue).append("\\<");
			parameters.put("codeValue", limitedCode.toString());
		}else {
			parameters.put("codeValue", codeValue);
		}
		parameters.put("classification", classification);
		return contentDisplayMapper.getIndexBookReferencedLinks(parameters);
	}

	public Long getIndexDescClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getIndexDescClassId(parameters);
	}

	@Override
	public List<SearchResultModel> getIndexTermSearchResults(String classification, Long indexElementId,
			String searchString, int maxAutocompleteSearchResults) {
		List<SearchResultModel> searchResults = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("searchString", searchString + "%");
		parameters.put("narrowClassId", getNarrowClassId(classification));
		parameters.put("maxResults", maxAutocompleteSearchResults);
		parameters.put("indexDescClassId", getIndexDescClassId(classification));
		parameters.put("indexElementId", indexElementId);
		Long contextId = lookupService.findBaseContextIdentifierByClassificationAndYear(classification,
				lookupService.findCurrentOpenYear(classification) + "").getContextId();
		parameters.put("contextId", contextId);
		searchResults = contentDisplayMapper.getSearchResultsByBookIndexAll(parameters);
		return searchResults;
	}

	public Long getLevelClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getLevelClassId(parameters);
	}

	public Long getLongPresentationClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getLongPresentationClassId(parameters);
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public Long getNarrowClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getNarrowClassId(parameters);
	}

	private Long getPriorContextId(String classification, Long contextId, boolean changeRequest) {
		String key = classification + "_" +  contextId + "_" + changeRequest;
		if (priorContentID.containsKey(key)){
			return priorContentID.get(key);
		}else{
			Long priorContextId =  lookupService.findPriorContextId(classification, contextId, changeRequest);
			priorContentID.put(key, priorContextId);
			return priorContextId;
		}
	}

	public Long getRubricClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getRubricClassId(parameters);
	}

	@Override
	public List<SearchResultModel> getSearchResults(String classification, Long contextId, String language,
			String searchBy, Long indexElementId, String searchString, int maxResults) {
		return getSearchResults(classification, contextId, language, searchBy, indexElementId, searchString, maxResults,
				true);
	}

	@Override
	public List<SearchResultModel> getSearchResults(String classification, Long contextId, String language,
			String searchBy, Long indexElementId, String searchString, int maxResults, boolean activeOnly) {
		List<SearchResultModel> searchResults = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("language", language);
		parameters.put("searchString", searchString + "%");
		parameters.put("maxResults", maxResults);
		parameters.put("contextId", contextId);
		if (SEARCHBY_CODE.equalsIgnoreCase(searchBy)) {
			parameters.put("codeClassId", getCodeClassId(classification));
			parameters.put("nodeStatus", "ACTIVE");
			searchResults = contentDisplayMapper.getSearchResultsByCode(parameters);
		} else {
			parameters.put("narrowClassId", getNarrowClassId(classification));
			parameters.put("indexDescClassId", getIndexDescClassId(classification));
			// parameters.put("levelClassId", getLevelClassId(classification));
			parameters.put("indexElementId", indexElementId);
			parameters.put("activeOnly", activeOnly);
			searchResults = contentDisplayMapper.getSearchResultsByBookIndex(parameters);
		}
		return searchResults;
	}

	@Override
	public List<SearchResultModel> getSearchResultsByCodeCat1(String classification, String searchString,
			int maxResults, Long contextId) {
		List<SearchResultModel> searchResults = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("searchString", searchString + "%");
		parameters.put("maxResults", maxResults);
		if (contextId == null) {
			contextId = lookupService.findBaseContextIdentifierByClassificationAndYear(classification,
					lookupService.findCurrentOpenYear(classification) + "").getContextId();
		}
		parameters.put("contextId", contextId);
		if ("CCI".equals(classification)) {
			parameters.put("codeClassId", getCCIClassID("TextPropertyVersion", "Code"));
			parameters.put("narrowClassId", getCCIClassID("ConceptPropertyVersion", "Narrower"));
			parameters.put("blockGroupClassId", getCCIClassID("ConceptVersion", "Group"));
			parameters.put("cat1ClassId", getCCIClassID("ConceptVersion", "Rubric"));
		} else {
			parameters.put("codeClassId", getICDClassID("TextPropertyVersion", "Code"));
			parameters.put("narrowClassId", getICDClassID("ConceptPropertyVersion", "Narrower"));
			parameters.put("blockGroupClassId", getICDClassID("ConceptVersion", "Block"));
			parameters.put("cat1ClassId", getICDClassID("ConceptVersion", "Category"));
		}

		searchResults = contentDisplayMapper.getSearchResultsByCodeCat1(parameters);

		return searchResults;
	}

	public SGSCService getSgscService() {
		return sgscService;
	}

	public Long getShortPresentationClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getShortPresentationClassId(parameters);
		// return (Long)contentDisplayMapper.getSqlSession().selectOne("View.getShortPresentationClassId", parameters);
	}

	public Long getSortingHintClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getSortingHintClassId(parameters);
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public Long getSupplementDescClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getSupplementDescClassId(parameters);
	}

	@Override
	public List<CodeDescription> getSupplementReferencedLinks(long contextId, String codeValue, String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("codeValue", codeValue);
		parameters.put("classification", classification);
		return contentDisplayMapper.getSupplementReferencedLinks(parameters);
	}

	public Long getSupplementTypeIndicatorClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getSupplementTypeIndicatorClassId(parameters);
	}

	public Long getTablePresentationClassId(String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		return contentDisplayMapper.getTablePresentationClassId(parameters);
	}

	@Override
	public TabularConceptDetails getTabularConceptDetails(long contextId, long elementId, String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("elementId", elementId);
		parameters.put("classification", classification);
		StopWatch stopWatch = new StopWatch("getTabularConceptDetails ElementID: " + elementId);
		stopWatch.start("getTabularConceptDetails");
		TabularConceptDetails t = contentDisplayMapper.getTabularConceptDetails(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);
		return t;
	}

	@Override
	public List<TabularReferencedLink> getTabularReferencedLinks(long contextId, String codeValue,
			String classification) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("codeValue", codeValue);
		parameters.put("classification", classification);
		return contentDisplayMapper.getTabularReferencedLinks(parameters);
	}

	@Override
	public String getTitleForNode(String conceptId, String classification, Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("conceptId", conceptId);
		parameters.put("language", language);
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getFormattedLongDescription(parameters);
	}

	/*
	 * It will replace the above one if we can pass in contextId anywhere,
	 *
	 * (non-Javadoc)
	 *
	 * @see ca.cihi.cims.service.ViewService#getTreeNodes(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ContentViewerModel> getTreeNodes(String conceptId, String classification, Long contextId,
			String language, String chapterId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("conceptId", conceptId);
		parameters.put("language", language);
		parameters.put("chapterId", chapterId);
		parameters.put("contextId", contextId);
		parameters.put("levelClassId", getLevelClassId(classification));
		parameters.put("sortingHintClassId", getSortingHintClassId(classification));
		parameters.put("supTypeIndicatorClassId", getSupplementTypeIndicatorClassId(classification));
		parameters.put("narrowClassId", getNarrowClassId(classification));
		return contentDisplayMapper.getTreeNodes(parameters);
	}

	@Override
	public String getUserTitle(Long conceptId, Long contextId, String language) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("conceptId", conceptId);
		parameters.put("languageCode", language);
		parameters.put("contextId", contextId);
		return contentDisplayMapper.getUserTitle(parameters);
	}

	@Override
	public String isCCIBlockLevel1(Long conceptId, Long contextId) {
		return contentDisplayMapper.isCCIBlockLevel1(conceptId, contextId);
	}

	@Override
	public String isCCIBlockLevel2(Long conceptId, Long contextId) {
		return contentDisplayMapper.isCCIBlockLevel2(conceptId, contextId);
	}

	private void setCCIValidationData(List<CciCodeValidation> myList, String language) {

		for (CciCodeValidation cciCodeValidation : myList) {
			String validationXmlString = cciCodeValidation.getValidationXml();

			if ((validationXmlString != null) && !validationXmlString.isEmpty()) {

				CciValidationXml cciValidationXml = XmlUtils.deserialize(CciValidationXml.class, validationXmlString);

				cciCodeValidation.setAgeRange(cciValidationXml.getAgeRange());

				if (Language.FRENCH.getCode().equals(language)) {
					cciCodeValidation.setGender(cciValidationXml.getGenderDescriptionFra());
				} else {
					cciCodeValidation.setGender(cciValidationXml.getGenderDescriptionEng());
				}
				cciCodeValidation.setStatusRef(cciValidationXml.getStatusReferenceCode());
				cciCodeValidation.setLocationRef(cciValidationXml.getLocationReferenceCode());
				cciCodeValidation.setExtentRef(cciValidationXml.getExtentReferenceCode());
			}
		}
	}

	@Override
	public void setContentDisplayMapper(ContentDisplayMapper contentDisplayMapper) {
		this.contentDisplayMapper = contentDisplayMapper;
	}

	public void setContentListWithChildrenSql(String contentListWithChildrenSql) {
		this.contentListWithChildrenSql = contentListWithChildrenSql;
	}

	private void setIcdValidationData(List<IcdCodeValidation> myList, String language) {
		for (IcdCodeValidation icdCodeValidation : myList) {
			String validationXmlString = icdCodeValidation.getValidationXml();
			if ((validationXmlString != null) && !validationXmlString.isEmpty()) {
				IcdValidationXml icdValidationXml = XmlUtils
						.deserialize(ca.cihi.cims.content.icd.IcdValidationXml.class, validationXmlString);
				icdCodeValidation.setAgeRange(icdValidationXml.getAgeRange());
				icdCodeValidation.setDxType1(icdValidationXml.getDxType1());
				icdCodeValidation.setDxType2(icdValidationXml.getDxType2());
				icdCodeValidation.setDxType3(icdValidationXml.getDxType3());
				icdCodeValidation.setDxType4(icdValidationXml.getDxType4());
				icdCodeValidation.setDxType6(icdValidationXml.getDxType6());
				icdCodeValidation.setDxType9(icdValidationXml.getDxType9());
				icdCodeValidation.setDxTypeW(icdValidationXml.getDxTypeW());
				icdCodeValidation.setDxTypeX(icdValidationXml.getDxTypeX());
				icdCodeValidation.setDxTypeY(icdValidationXml.getDxTypeY());
				if (Language.FRENCH.getCode().equals(language)) {
					icdCodeValidation.setGender(icdValidationXml.getGenderDescriptionFra());
				} else {
					icdCodeValidation.setGender(icdValidationXml.getGenderDescriptionEng());
				}
				icdCodeValidation.setMrdxMain(icdValidationXml.getMRDxMain());
				icdCodeValidation.setNewBorn(icdValidationXml.getNewBorn());
			}
		}
	}

	@Override
	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Autowired
	public void setSgscService(SGSCService sgscService) {
		this.sgscService = sgscService;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
}