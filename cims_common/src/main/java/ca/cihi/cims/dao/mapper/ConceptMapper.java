package ca.cihi.cims.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.Diagram;
import ca.cihi.cims.model.IdCodeDescription;

public interface ConceptMapper {

	String findDadDHValidation(Map<String, Object> map);

	String findDadDHValidationAtChildLevels(Map<String, Object> map);

	String findDHValidationAtChildLevels(Map<String, Object> map);

	List<AsteriskBlockInfo> getAsteriskList(Map<String, Object> map);

	Long getBaseContextId(Map<String, Object> map);

	List<AsteriskBlockInfo> getBlockList(Map<String, Object> map);

	Long getCCIClassID(java.util.Map<String, Object> map);

	List<IdCodeDescription> getCCIComponentsPerSection(Map<String, Object> map);

	List<IdCodeDescription> getCCIComponentsPerSectionLongTitle(Map<String, Object> map);

	long getCCISectionIdBySectionCode(@Param("sectionCode") String sectionCode,
			@Param("contextId") Long currentContextId);

	/**
	 * Get contextId based on classification and versionCode (fiscalYear)
	 *
	 * @param parameters
	 * @return
	 */
	Long getContextId(Map<String, Object> parameters);

	Diagram getDiagramByFileName(@Param("diagramFileName") String diagramFileName,
			@Param("currentContextId") Long currentContextId);

	List<Diagram> getDiagramByContextId(@Param("currentContextId") Long paramLong);

	Long getICDClassID(java.util.Map<String, Object> map);

	List<IdCodeDescription> getRefAttributePerType(Map<String, Object> map);

	String getValidationRuleRefCodes(Map<String, Object> map);

	String hasActiveChildren(Map<String, Object> map);

	String hasActiveValidationRule(Map<String, Object> map);

	String hasChildWithActiveValidationRule(Map<String, Object> map);

	String isRefAttributeMandatory(Map<String, Object> map);

	/**
	 * get containing page id for a give elementId
	 *
	 * @param map
	 * @return
	 */
	Long retrieveContainingPagebyId(Map<String, Object> map);

	Long retrievePagebyIdForFolio(Map<String, Object> params);

    Long retrievePagebyIdForClaml(Map<String, Object> params);
	
}