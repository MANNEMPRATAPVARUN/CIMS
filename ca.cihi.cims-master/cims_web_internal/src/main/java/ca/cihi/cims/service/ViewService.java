package ca.cihi.cims.service;

import java.util.List;

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

public interface ViewService {

	/**
	 * Used in System generated Supplement Content, get cci rubric by sectionCode
	 *
	 * @param paramMap
	 * @return
	 */
	List<CCIRubric> findCCIRubric(long contextId, String sectionCode, String groupCode);

	/** Returns a list of validation rules for a specified CCI concept */
	List<CodeDescription> getAllBookIndexes(String classification, Long contextId, String language);

	List<CodeDescription> getAllBookIndexesNoLang(String classification);

	/** Returns attributes for a specified reference attribute code in the specified context */
	List<ContentViewerModel> getAttributesFromReferenceCode(String refAttrCode, String classification, Long contextId,
			String language);

	Long getCCIClassID(String tablename, String classname);

	/**
	 * Used in System Generated Supplement Content, get cci group content by first letter
	 *
	 * @param language
	 * @param contextId
	 * @param sectionCode
	 * @param firstLetter
	 * @return
	 */
	List<CCIComponentSupplement> getCciGroupComponentsWithDefinition(String language, long contextId,
			String sectionCode, String firstLetter);

	String getCCIGroupTitle(Long conceptId, Long contextId, String language);

	/**
	 * Used in System Generated Supplement Content, get cci intervent content, will get elements with definition html
	 * only
	 *
	 * @param language
	 * @param contextId
	 * @param sectionCode
	 * @param orderBy
	 * @return
	 */
	List<CCIComponentSupplement> getCciInterventionComponentsWithDefinition(String language, long contextId,
			String sectionCode, String orderBy);

	List<CCIReferenceAttribute> getCCIReferenceAttributesForSupplement(Long contextId, String referenceType,
			String languageCode);

	String getConceptCode(String conceptId, Long contextId);

	/** Returns a list of validation rules for a specified ICD concept */
	String getConceptIdPathByElementId(String classification, Long contextId, Long elementId);

	String getConceptShortPresentation(String code, String classification, Long contextId, String language);

	Long getContainerPage(Long pConceptId, Long childConceptId, String classification, Long containerId,
			Long contextId);

	ContentDisplayMapper getContentDisplayMapper();

	List<ContentViewerModel> getContentList(String unitConceptId, String classification, Long contextId,
			String language, String requestId, boolean withChildren, Boolean folio);

	List<ContentToSynchronize> getContentToSynchronize(long contextId);

	List<IdCodeDescription> getGenericAttributesForSupplement(String baseClassification, Long contextId,
			String attributeType, String languageCode);

	List<CciCodeValidation> getHierCCIValidationRulesForRubric(String conceptCode, String classification,
			String contextId, String language);

	List<IcdCodeValidation> getHierICDValidationRulesForCategory(String conceptCode, String classification,
			String contextId, String language);

	List<IndexBookReferencedLink> getIndexBookReferencedLinks(long contextId, String codeValue, String classification);

	List<SearchResultModel> getIndexTermSearchResults(String classification, Long indexElementId, String searchString,
			int maxAutocompleteSearchResults);

	List<SearchResultModel> getSearchResults(String classification, Long contextId, String language, String searchBy,
			Long indexElementId, String searchString, int maxResults);

	List<SearchResultModel> getSearchResults(String classification, Long contextId, String language, String searchBy,
			Long indexElementId, String searchString, int maxResults, boolean activeOnly);

	List<SearchResultModel> getSearchResultsByCodeCat1(String classification, String searchString, int maxResults,
			Long contextId);

	List<CodeDescription> getSupplementReferencedLinks(long contextId, String codeValue, String calssification);

	TabularConceptDetails getTabularConceptDetails(long contextId, long elementId, String classification);

	List<TabularReferencedLink> getTabularReferencedLinks(long contextId, String codeValue, String classification);

	String getTitleForNode(String conceptId, String classification, Long contextId, String language);

	List<ContentViewerModel> getTreeNodes(String conceptId, String classification, Long contextId, String language,
			String chapterId);

	String getUserTitle(Long sectionId, Long contextId, String language);

	String isCCIBlockLevel1(Long conceptId, Long contextId);

	String isCCIBlockLevel2(Long conceptId, Long contextId);

	void setContentDisplayMapper(ContentDisplayMapper contentDisplayMapper);

	void setLookupService(LookupService lookupService);

	List<ContentViewerModel> getContentListForJsonView(String unitConceptId, String classification, Long contextId, String language, String requestId, boolean withChildren, boolean folio);

}
