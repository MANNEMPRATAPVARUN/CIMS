package ca.cihi.cims.service;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.AttributeInfo;
import ca.cihi.cims.model.AttributeType;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.Diagram;
import ca.cihi.cims.model.IdCodeDescription;

public interface ConceptService {

	/**
	 * Find the validation rule for Acute Care for the given rubric concept
	 *
	 * @param elementId
	 *            Long the elementId of the given rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return AttributeInfo
	 */
	AttributeInfo findDadDHValidation(final Long elementId, final Long contextId);

	/**
	 * Find the validation rule for Acute Care at child levels.
	 *
	 * @param elementId
	 *            Long the elementId of the given rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return
	 */
	AttributeInfo findDadDHValidationAtChildLevels(final Long elementId, final Long contextId);

	/**
	 * Get ordered active category-one concepts with asterisk.
	 *
	 * @param chaperElementId
	 *            Long the elementId of the given chapter
	 * @param contextId
	 *            Long the contextId of the given chapter belongs to
	 */
	List<AsteriskBlockInfo> getAsteriskList(final Long chapterElementId, final Long contextId);

	/**
	 * Get ordered active blocks in all block levels.
	 *
	 * @param classificatioon
	 *            String the given classification
	 * @param chaperElementId
	 *            Long the elementId of the given chapter
	 * @param contextId
	 *            Long the contextId of the given chapter belongs to
	 */
	List<AsteriskBlockInfo> getBlockList(final String classification, final Long chapterElementId,
			final Long contextId);

	Long getCCIClassID(final String tablename, final String classname);

	/**
	 * Returns a list of CCI components for a specified section and component type
	 *
	 * @param sectionId
	 * @param contextId
	 * @param language
	 * @param type
	 */
	List<IdCodeDescription> getCciComponentsPerSection(final long sectionId, final long contextId,
			final Language language, CciComponentType type);

	/**
	 * Returns a list of CCI components for a specified section and component type with long title as description
	 *
	 * @param sectionId
	 * @param contextId
	 * @param language
	 * @param type
	 * @param orderBy
	 * @param firstLetter
	 * @return
	 */

	List<IdCodeDescription> getCciComponentsPerSectionLongTitle(long sectionId, long contextId, Language language,
			CciComponentType type, String orderBy, String firstLetter);

	/**
	 * Get section elementId based on section code
	 *
	 * @param section
	 * @param contextiId
	 * @return
	 */
	long getCCISectionIdBySectionCode(String sectionCode, long contextId);

	Long getClassId(final String classification, final String tableName, final String className);

	/**
	 * Get contextId based on classification and versionCode (fiscalYear)
	 *
	 * @param classification
	 * @param versionCode
	 * @return
	 */
	Long getContextId(String classification, String versionCode);

	byte[] getDiagram(String diagramFileName, Long currentContextId);

	List<Diagram> getDiagrams(Long paramLong);

	Long getICDClassID(final String tablename, final String classname);

	List<IdCodeDescription> getRefAttributePerType(final long contextId, final Language language,
			final AttributeType type);

	/**
	 * Check if the given concept one or rubric concept has active validations.
	 *
	 * @param elementId
	 *            Long the elementId of the given category one or rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return boolean
	 */
	boolean hasActiveValidationRule(final Long elementId, final Long contextId);

	/**
	 * Check if any children of the given category one or rubric concept has validations
	 *
	 * @param elementId
	 *            Long the elementId of the given category one or rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return boolean
	 */
	boolean hasChildWithActiveValidationRule(final Long elementId, final Long contextId);

	/**
	 * Check if the given reference attribute is mandatory in the given context
	 *
	 * @param contextId
	 *            Long the context id
	 * @param refAttributeCode
	 *            String the code of the given reference attribute
	 * @return
	 */
	boolean isRefAttributeMandatory(final Long contextId, final String refAttributeCode);

	/**
	 * Check if the given concept is a leaf node.
	 *
	 * @param elementId
	 *            Long the elementId of the given concept
	 * @param contextId
	 *            Long the context id
	 * @return boolean
	 */
	boolean isValidCode(final Long elementId, final Long contextId);

	boolean reBaseChangedFromVersionId(long elementId, long contextId, long classId, String languageCode);

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