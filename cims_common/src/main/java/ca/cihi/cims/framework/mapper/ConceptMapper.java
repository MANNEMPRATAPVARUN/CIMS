package ca.cihi.cims.framework.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.dto.PropertyDTO;

public interface ConceptMapper {

	/**
	 * params should includes contextId, elementId, elementVersionId
	 *
	 * @param params
	 */
	void createConceptVersion(Map<String, Object> params);

	/**
	 * params should include contextId, conceptId and classsNames
	 *
	 * @param params
	 * @return
	 */
	List<PropertyDTO> loadPropertiesForConcept(Map<String, Object> params);

	/**
	 *
	 * @param contextId
	 * @param elementId
	 * @param elementVersionId
	 */
	void remove(@Param("contextId") Long contextId, @Param("elementId") Long elementId);

	/**
	 * params should includes contextId, conceptClasssId, propertyClasssIds, conditionList
	 *
	 * @param params
	 * @return
	 */
	List<ConceptDTO> findConceptsByClassAndValues(Map<String, Object> params);

	/**
	 * params should includes contextId, conceptClasssId, propertyClasssIds, conditionList
	 *
	 * @param params
	 * @return list of elementidentifiers based on specified params
	 */
	List<ElementIdentifier> findConceptIDsByClassAndValues(Map<String, Object> params);

	/**
	 *
	 * @param contextId
	 * @param classsId
	 * @param conceptId
	 * @return
	 */
	Long findAncestorId(@Param("contextId") Long contextId, @Param("ancestorClasssId") Long ancestorClasssId,
			@Param("relationshipClasssId") Long relationshipClasssId, @Param("conceptId") Long conceptId);

	/**
	 * Returns a List of ElementIdentifiers for all concepts that we disabled between the from and to context
	 *
	 * @param params
	 * @return
	 */
	List<ElementIdentifier> findDisabledConceptIds(Map<String, Object> params);
}
