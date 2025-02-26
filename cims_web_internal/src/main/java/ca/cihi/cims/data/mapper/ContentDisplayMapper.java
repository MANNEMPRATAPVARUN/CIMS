package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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

public interface ContentDisplayMapper {

	List<CCIRubric> findCCIRubric(Map<String, Object> paramMap);

	List<CodeDescription> getAllBookIndexes(Map<String, Object> map);

	List<CodeDescription> getAllBookIndexesNoLang(Map<String, Object> map);

	List<ContentViewerModel> getAttributesFromReferenceCode(Map<String, Object> map);

	List<ContentViewerModel> getBaseContentList(Map<String, Object> map);

	Long getBaseContextId(Map<String, Object> map);

	Long getCategoryClassId(Map<String, Object> map);

	Long getCCIClassID(Map<String, Object> map);

	List<CCIComponentSupplement> getCciComponentsWithDefinitionForSupplement(Map<String, Object> map);

	String getCCIGroupTitle(Map<String, Object> parameters);

	List<CCIReferenceAttribute> getCCIReferenceAttributesForSupplement(Map<String, Object> params);

	Long getCCISectionId(@Param("sectionCode") String sectionCode, @Param("contextId") Long contextId);

	Long getCodeClassId(Map<String, Object> map);

	String getConceptCode(Map<String, Object> map);

	String getConceptIdFromCode(Map<String, Object> map);

	// baseClassification varchar2, contextId number, elemId NUMBER
	String getConceptIdPathByElementId(@Param("baseClassification") String baseClassification,
			@Param("contextId") Long contextId, @Param("elemId") Long elemId);

	String getConceptMajorType(Map<String, Object> map);

	Long getContainerPage(Map<String, Object> params);

	List<ContentViewerModel> getContentList(Map<String, Object> map);

	List<ContentViewerModel> getContentListFromLeadTerm(Map<String, Object> map);

	Object getContentListTemp(Map<String, Object> params);

	List<ContentViewerModel> getContentListWithoutChildren(Map<String, Object> map);

	List<ContentToSynchronize> getContentToSynchronize(Map<String, Object> map);

	String getFormattedLongDescription(Map<String, Object> map);

	List<IdCodeDescription> getGenericAttributesForSupplement(Map<String, Object> params);

	Long getGroupClassId(Map<String, Object> map);

	// List<CciCodeValidation> getHierValidationRulesFromConceptId(Map<String, Object> map);
	List<CciCodeValidation> getHierCCIValidationRulesFromConceptId(Map<String, Object> map);

	List<IcdCodeValidation> getHierICDValidationRulesFromConceptId(Map<String, Object> map);

	Long getICDClassID(Map<String, Object> map);

	List<IndexBookReferencedLink> getIndexBookReferencedLinks(Map<String, Object> map);

	Long getIndexDescClassId(Map<String, Object> map);

	Long getLevelClassId(Map<String, Object> map);

	Long getLongPresentationClassId(Map<String, Object> map);

	Long getNarrowClassId(Map<String, Object> map);

	Long getRubricClassId(Map<String, Object> map);

	List<SearchResultModel> getSearchResultsByBookIndex(Map<String, Object> map);

	List<SearchResultModel> getSearchResultsByBookIndexAll(Map<String, Object> map);

	List<SearchResultModel> getSearchResultsByCode(Map<String, Object> map);

	List<SearchResultModel> getSearchResultsByCodeCat1(Map<String, Object> map);

	String getShortPresentation(Map<String, Object> params);

	Long getShortPresentationClassId(Map<String, Object> map);

	Long getSortingHintClassId(Map<String, Object> map);

	Long getSupplementDescClassId(Map<String, Object> map);

	List<CodeDescription> getSupplementReferencedLinks(Map<String, Object> parameters);

	Long getSupplementTypeIndicatorClassId(Map<String, Object> map);

	Long getTablePresentationClassId(Map<String, Object> map);

	TabularConceptDetails getTabularConceptDetails(Map<String, Object> map);

	List<TabularReferencedLink> getTabularReferencedLinks(Map<String, Object> map);

	List<ContentViewerModel> getTreeNodes(Map<String, Object> map);

	String getUserTitle(Map<String, Object> map);

	String isCCIBlockLevel1(@Param("conceptId") Long conceptId, @Param("contextId") Long contextId);

	String isCCIBlockLevel2(@Param("conceptId") Long conceptId, @Param("contextId") Long contextId);

	void runStats();

	Long getClassNameClassId(Map<String, Object> paramMap);

}
