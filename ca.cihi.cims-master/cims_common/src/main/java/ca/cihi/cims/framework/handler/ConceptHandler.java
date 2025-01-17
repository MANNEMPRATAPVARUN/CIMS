package ca.cihi.cims.framework.handler;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.ConceptPropertyConfiguration;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.PropertyCriterion;
import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.ComparisonOperator;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.framework.enums.RelationshipDirection;
import ca.cihi.cims.framework.mapper.ConceptMapper;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 3:07:00 PM
 */
@Component
public class ConceptHandler {

	@Autowired
	private ClasssHandler classsHandler;

	@Autowired
	@Qualifier("frameworkConceptMapper")
	private ConceptMapper conceptMapper;

	@Autowired
	private ElementHandler elementHandler;

	public ConceptHandler() {

	}

	/**
	 * classId = ClasssHandler.getClasss(baseClassificationName, classsName) elementId =
	 * super.createElement(businessKey, classId) elementVersionId = super.updateElementVersion(Context.contextId,
	 * elementId, context.versionCode)
	 *
	 * insert into conceptversion (elementVersionId , classId, 'ACTIVE', elementId) insert into structureelementversion
	 * .....to link the concept to the context return new ElementIdentifier(elementId , elementVersionId )
	 *
	 * @param context
	 * @param baseClassificationName
	 * @param classsName
	 * @param businessKey
	 */
	public ElementIdentifier createConcept(Context context, String baseClassificationName, String classsName,
			String businessKey) {
		ClasssDTO classs = classsHandler.getClasss(classsName, baseClassificationName);
		Long elementId = elementHandler.createElement(classs.getClasssId(), businessKey);
		Long elementVersionId = elementHandler.updateElementInContext(context.getContextId(), elementId);
		Map<String, Object> params = new HashMap<>();
		params.put("classsId", classs.getClasssId());
		params.put("elementId", elementId);
		params.put("elementVersionId", elementVersionId);
		conceptMapper.createConceptVersion(params);
		return new ElementIdentifier(elementId, elementVersionId);
	}

	/**
	 * Returns true if the concept was created in a different context. Check the originating context id and return true
	 * if not null, otherwise return false.
	 *
	 * @return
	 */
	public boolean hasPreviousVersions(Long contextId, Long elementId) {
		ElementDTO elementDTO = elementHandler.findElementInContext(contextId, elementId);
		return (elementDTO.getOriginatingContextId() != null);
	}

	/**
	 * Retrieves in one query the properties of a concept as specified by the input ConceptPropertyConfiguration Returns
	 * a collection of fully populated PropertyDTO objects
	 *
	 * @param contextId
	 * @param conceptId
	 * @param conceptPropertyConfiguration
	 */
	public Map<PropertyKey, PropertyDTO> findPropertiesForConcept(Long contextId, Long conceptId,
			ConceptPropertyConfiguration conceptPropertyConfiguration) {

		List<String> classsNames = conceptPropertyConfiguration.getKeys().stream().map(item -> {
			String classsName = item.getClassName();
			return classsName;
		}).collect(toList());

		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("conceptId", conceptId);
		params.put("classsNames", classsNames);
		List<PropertyDTO> properties = conceptMapper.loadPropertiesForConcept(params);

		return properties.stream().collect(Collectors.toMap(PropertyKey::createKey, Function.identity()));
	}

	/**
	 *
	 * @psaram relationshipClassId
	 * @param classsIds
	 * @param contextId
	 * @param conceptElementId
	 * @param conditionList
	 * @param conceptClasssId
	 */
	public List<ConceptDTO> findReferencingConcepts(Long relationshipClasssId, List<Long> classsIds, Long contextId,
			Long conceptElementId, List<PropertyCriterion> conditionList, Long conceptClasssId) {
		PropertyCriterion criterion = new PropertyCriterion();
		criterion.setClasssId(relationshipClasssId);
		criterion.setValue(conceptElementId);
		criterion.setPropertyType(PropertyType.ConceptProperty.name());
		criterion.setOperator(ComparisonOperator.EQUALS.name());
		conditionList.add(criterion);

		return findConceptsByClassAndValues(contextId, conceptClasssId, classsIds, conditionList);
	}

	/**
	 *
	 * @param relationshipClassId
	 * @param classsIds
	 * @param contextId
	 * @param conceptElementId
	 */
	public ConceptDTO findReferencedConcept(Long relationshipClasssId, List<Long> classsIds, Long contextId,
			Long conceptElementId, List<PropertyCriterion> conditionList, Long conceptClasssId) {
		if (conditionList == null) {
			conditionList = new ArrayList<>();
		}

		PropertyCriterion criterion = new PropertyCriterion();
		criterion.setClasssId(relationshipClasssId);
		criterion.setValue(conceptElementId);
		criterion.setPropertyType(PropertyType.ConceptProperty.name());
		criterion.setOperator(ComparisonOperator.EQUALS.name());
		criterion.setRelationshipDirection(RelationshipDirection.UP.name());
		conditionList.add(criterion);

		List<ConceptDTO> dtos = findConceptsByClassAndValues(contextId, conceptClasssId, classsIds, conditionList);
		if (!CollectionUtils.isEmpty(dtos)) {
			return dtos.get(0);
		} else {
			return null;
		}
	}

	/**
	 * - Detach the concept version from the context - detach all its property versions from context - if the concept
	 * was created in the context - for all property and concept versions -- if created in the context ---- remove
	 * version -- if 0 versions for the element ---- remove element
	 *
	 * @param contextId
	 * @param elementIdentifier
	 */
	public void remove(Long contextId, ElementIdentifier elementIdentifier) {
		conceptMapper.remove(contextId, elementIdentifier.getElementId());
	}

	/**
	 * Finds all concepts with specified conceptClasssName and with properties matching the criteria and returns a list
	 * of ConceptDTO for them.
	 *
	 * @param contextId
	 * @param conceptClasssId
	 * @param propertyClasssIds
	 * @param conditionList
	 * @return
	 */
	public List<ConceptDTO> findConceptsByClassAndValues(Long contextId, Long conceptClasssId,
			List<Long> propertyClasssIds, List<PropertyCriterion> conditionList) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("conceptClasssId", conceptClasssId);
		params.put("propertyClasssIds", propertyClasssIds);
		params.put("conditionList", conditionList);
		List<ConceptDTO> results = conceptMapper.findConceptsByClassAndValues(params);
		return results;
	}

	/**
	 * Finds all concepts with specified conceptClasssName and with properties matching the criteria and returns a list
	 * of ElementIdentifier for them.
	 *
	 * @param contextId
	 * @param conceptClasssId
	 * @param propertyClasssIds
	 * @param conditionList
	 * @return
	 */
	public List<ElementIdentifier> findConceptIDsByClassAndValues(Long contextId, Long conceptClasssId,
			List<Long> propertyClasssIds, List<PropertyCriterion> conditionList) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("conceptClasssId", conceptClasssId);
		params.put("propertyClasssIds", propertyClasssIds);
		params.put("conditionList", conditionList);
		return conceptMapper.findConceptIDsByClassAndValues(params);
	}

	/**
	 * Based on relationship class trace ancestor conceptId, use case: find the chapterId for an ICD concept or
	 * sectionId for a CCI concept
	 *
	 * @param contextId
	 * @param classsId
	 * @param conceptId
	 * @return
	 */

	public Long findAncestorIdByRelationshipClasssId(Long contextId, Long ancestorClasssId, Long relationshipClasssId,
			Long conceptId) {
		return conceptMapper.findAncestorId(contextId, ancestorClasssId, relationshipClasssId, conceptId);
	}

	/**
	 * Returns a List of ElementIdentifiers for all concepts that we disabled between the from and to context
	 *
	 * @param fromContextId
	 * @param toContextId
	 * @param conceptClasssId
	 * @return
	 */
	public List<ElementIdentifier> findDisabledConceptIds(Long fromContextId, Long toContextId, Long conceptClasssId) {
		Map<String, Object> params = new HashMap<>();
		params.put("fromContextId", fromContextId);
		params.put("toContextId", toContextId);
		params.put("conceptClasssId", conceptClasssId);
		return conceptMapper.findDisabledConceptIds(params);
	}

}