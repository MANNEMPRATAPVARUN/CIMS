package ca.cihi.cims.framework.domain;

import static java.util.stream.Collectors.toList;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.ConceptMetadata;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.framework.handler.ConceptHandler;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 10:49:05 AM
 */
public abstract class Concept extends Element {

	private static ConceptHandler conceptHandler;

	/**
	 * Creates a new concept in the specified context with a specified classs name and business key. Returns the
	 * Elementidentifier for the newly created concept. Throws exception if a concept version already exists.
	 *
	 * @param className
	 * @param context
	 * @param businessKey
	 */
	protected static ElementIdentifier create(String classsName, Context context, String businessKey) {
		return conceptHandler.createConcept(context, context.getBaseClassificationName(), classsName, businessKey);
	}

	public static void setHandlers(ConceptHandler handler) {
		conceptHandler = handler;
	}

	private static final Logger LOGGER = LogManager.getLogger(Concept.class);

	/**
	 * configuration of the concept
	 */
	private ConceptMetadata conceptMetadata;

	/**
	 * Repository of property values for the concept based on the concept metadata.
	 */
	private Map<PropertyKey, Property> propertyMap;

	/**
	 * Constructs the concept object from input parameters
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	protected Concept(Classs classs, Context context, ElementIdentifier elementIdentifier,
			ConceptMetadata conceptMetadata) {
		super(classs, context, elementIdentifier);
		setConceptMetadata(conceptMetadata);
		setPropertyMap(new HashMap<PropertyKey, Property>());
	}

	/**
	 * Concatenates the classname and language e.g. Name_ENG, EffectiveYearFrom_NOLANGUAGE
	 *
	 * @param className
	 * @param language
	 */
	protected String buildPropertyIdentifier(String classsName, Language language) {
		return classsName + "_" + language.getCode();
	}

	public ConceptMetadata getConceptMetadata() {
		return conceptMetadata;
	}

	/**
	 * Retrieves the key from propertyKeys map Throws system exception if the key is not there
	 *
	 * @param keyIdentifier
	 */
	protected PropertyKey getKey(String keyIdentifier) {
		PropertyKey key = getConceptMetadata().getPropertyKeys().get(keyIdentifier);
		if (key == null) {
			throw new PropertyKeyNotFoundException("Property key: " + keyIdentifier + " not been configured!");
		}
		return key;
	}

	/**
	 * Retrieves the property from the map. If not found, loads property from the database. If not there, instantiates
	 * an empty Property object. Adds property to the map if not already there.
	 *
	 * - If property exists in propertiesMap -- return property - otherwise
	 *
	 * -- property = Property.loadProperty(context, elementId, key) -- add property to the propertiesMap -- return
	 * property
	 *
	 * @param key
	 */
	public Property getProperty(PropertyKey key) {
		Property property = propertyMap.get(key);
		if (property == null) {
			property = Property.loadProperty(getContext(), getElementIdentifier().getElementId(), key);
			propertyMap.put(key, property);
		}
		return property;
	}

	/**
	 * keyIdentifier = buildPropertyIdentifier(className, language) key = getKey(keyIdentifier ) return getProperty(key)
	 *
	 * @param className
	 * @param language
	 */
	public Object getProperty(String classsName, Language language) {
		Property property = getProperty(getKey(buildPropertyIdentifier(classsName, language)));
		if ((property != null) && (property.getValue() != null)) {
			if (property.getValue().getValue() instanceof Blob) {
				Blob blob = (Blob) property.getValue().getValue();
				try {
					return blob.getBytes(1, (int) blob.length());
				} catch (SQLException e) {
					LOGGER.error("Error retrieve blob data.", e);
					return null;
				}
			}
			return property.getValue().getValue();
		}
		return null;
	}

	public Map<PropertyKey, Property> getPropertyMap() {
		return propertyMap;
	}

	/**
	 * - figure out the class names using the loadDegree - prepare classIdList (Classs.findByNames) - prepare
	 * relationshipClassId (Class.findByName) - dtos = ConceptconceptHandler.loadRelatedConcepts(relationshipClassId ,
	 * classIdList , this.context.contextId, this.elementId) - for each dto in dtos list -- instantiate the proper
	 * concept object (e.g Picklist) based on input conceptClasssname Note that all concept classes must have a
	 * constructor with coceptDTO and context as input parameters -- add concept to the result list - return result list
	 *
	 * @param clazz
	 * @param metadata
	 *            the metadata of the referencingConcept
	 * @param loadDegree
	 * @param conceptClasssName
	 */
	public List<Concept> getReferencingConcepts(ConceptQueryCriteria queryCriteria) {
		if (queryCriteria.getConditionList() == null) {
			queryCriteria.setConditionList(new ArrayList<>());
		}
		List<String> classsNames = queryCriteria.getMetadata().getPropertyConfigurations()
				.get(queryCriteria.getLoadDegree()).getKeys().stream().map(PropertyKey::getClassName).collect(toList());
		List<Long> propertyClasssIds = Classs.findByNames(classsNames, getBaseClassificationName()).stream()
				.map(item -> item.getClassId()).collect(toList());
		Classs relationshipClasss = Classs.findByName(queryCriteria.getRelationshipClassName(),
				getBaseClassificationName());
		Classs conceptClasss = Classs.findByName(queryCriteria.getClasssName(), getBaseClassificationName());

		List<ConceptDTO> conceptDTOs = conceptHandler.findReferencingConcepts(relationshipClasss.getClassId(),
				propertyClasssIds, getContext().getContextId(), getElementIdentifier().getElementId(),
				queryCriteria.getConditionList(), conceptClasss.getClassId());

		Stream<Concept> concepts = conceptDTOs.stream()
				.map(dto -> ConceptFactory.getConcept(queryCriteria.getClazz(), dto, getContext()));
		return concepts.collect(toList());
	}

	/**
	 * - figure out the class names using the loadDegree - prepare classIdList (Classs.findByNames) - prepare
	 * relationshipClassId (Class.findByName) - dtos = ConceptconceptHandler.loadRelatedConcepts(relationshipClassId ,
	 * classIdList , this.context.contextId, this.elementId) - for each dto in dtos list -- instantiate the proper
	 * concept object (e.g Picklist) based on input conceptClasssname Note that all concept classes must have a
	 * constructor with coceptDTO and context as input parameters -- add concept to the result list - return result list
	 *
	 * @param queryCriteria
	 */
	public Concept getReferencedConcept(ConceptQueryCriteria queryCriteria) {
		if (queryCriteria.getConditionList() == null) {
			queryCriteria.setConditionList(new ArrayList<>());
		}
		List<String> classsNames = queryCriteria.getMetadata().getPropertyConfigurations()
				.get(queryCriteria.getLoadDegree()).getKeys().stream().map(PropertyKey::getClassName).collect(toList());
		List<Long> propertyClasssIds = Classs.findByNames(classsNames, getBaseClassificationName()).stream()
				.map(item -> item.getClassId()).collect(toList());
		Classs relationshipClasss = Classs.findByName(queryCriteria.getRelationshipClassName(),
				getBaseClassificationName());
		Classs conceptClasss = Classs.findByName(queryCriteria.getClasssName(), getBaseClassificationName());

		ConceptDTO dto = conceptHandler.findReferencedConcept(relationshipClasss.getClassId(), propertyClasssIds,
				getContext().getContextId(), getElementIdentifier().getElementId(), queryCriteria.getConditionList(),
				conceptClasss.getClassId());

		return ConceptFactory.getConcept(queryCriteria.getClazz(), dto, getContext());
	}

	/**
	 * Returns true if the concept was created in a previous context.
	 *
	 * @return
	 */
	public boolean hasPreviousVersions() {
		return conceptHandler.hasPreviousVersions(getContext().getContextId(), getElementIdentifier().getElementId());
	}

	/**
	 * Uses the metadata for the particular type of concept to load the concept according to the input load degree.
	 *
	 * @param degree
	 */
	public void loadProperties(ConceptLoadDegree degree) {

		if (degree.equals(ConceptLoadDegree.NONE)) {
			return;
		}
		Map<PropertyKey, PropertyDTO> properties = conceptHandler.findPropertiesForConcept(getContext().getContextId(),
				getElementIdentifier().getElementId(), getConceptMetadata().getPropertyConfigurations().get(degree));

		for (PropertyKey key : properties.keySet()) {
			Property property = new Property(properties.get(key), getContext());
			propertyMap.put(key, property);
		}

	}

	/**
	 * Instantiates Property objects and populates the propertiesMap accordingly
	 *
	 * @param dto
	 */
	public void populateProperties(ConceptDTO dto) {
		dto.getLoadedProperties().stream().forEach(item -> {
			Property property = new Property(item, getContext());
			propertyMap.put(property.getKey(), property);
		});
	}

	/**
	 * ConceptconceptHandler.remove(contextId, elementIdentifier)
	 */
	public void remove() {
		conceptHandler.remove(getContext().getContextId(), getElementIdentifier());
	}

	public void setConceptMetadata(ConceptMetadata conceptMetadata) {
		this.conceptMetadata = conceptMetadata;
	}

	/**
	 * property = getProperty(key) if property.getValue!= value then -- property.set(value, elementIdentifier) otherwise
	 * return
	 *
	 * @param key
	 * @param value
	 */
	protected void setProperty(PropertyKey key, PropertyValue value) {
		Property property = getProperty(key);
		if (!value.equals(property.getValue())) {
			property.setValue(value, getElementIdentifier());
		}
	}

	/**
	 * keyIdentifier = buildPropertyIdentifier(className, language) key = getKey(keyIdentifier ) setProperty(key, value)
	 *
	 * @param className
	 * @param value
	 * @param language
	 */
	protected void setProperty(String classsName, PropertyValue value, Language language) {
		setProperty(getKey(buildPropertyIdentifier(classsName, language)), value);
	}

	public void setPropertyMap(Map<PropertyKey, Property> propertyMap) {
		this.propertyMap = propertyMap;
	}

	/**
	 * Finds all concepts with specified conceptClasssName and with properties matching the criteria.
	 *
	 * @param contextId
	 * @param conceptClasssName
	 * @param criteria
	 * @return
	 */
	public static List<Concept> findConceptsByClassAndValues(Long contextId, ConceptQueryCriteria queryCriteria) {
		Context context = Context.findById(contextId);
		List<String> classsNames = queryCriteria.getMetadata().getPropertyConfigurations()
				.get(queryCriteria.getLoadDegree()).getKeys().stream().map(PropertyKey::getClassName).collect(toList());
		List<Long> propertyClasssIds = Classs.findByNames(classsNames, context.getBaseClassificationName()).stream()
				.map(item -> item.getClassId()).collect(toList());
		Classs conceptClasss = Classs.findByName(queryCriteria.getClasssName(), context.getBaseClassificationName());
		List<ConceptDTO> dtos = conceptHandler.findConceptsByClassAndValues(contextId, conceptClasss.getClassId(),
				propertyClasssIds, queryCriteria.getConditionList());
		Stream<Concept> concepts = dtos.stream()
				.map(dto -> ConceptFactory.getConcept(queryCriteria.getClazz(), dto, context));
		return concepts.collect(toList());
	}

	/**
	 * Finds all concepts with specified conceptClasssName and with properties matching the criteria and returns a list
	 * of ElementIdentifier for them.
	 *
	 * @param contextId
	 * @param queryCriteria
	 * @return
	 */
	public static List<ElementIdentifier> findConceptIDsByClassAndValues(Long contextId,
			ConceptQueryCriteria queryCriteria) {
		Context context = Context.findById(contextId);
		List<String> classsNames = queryCriteria.getMetadata().getPropertyConfigurations()
				.get(queryCriteria.getLoadDegree()).getKeys().stream().map(PropertyKey::getClassName).collect(toList());
		List<Long> propertyClasssIds = Classs.findByNames(classsNames, context.getBaseClassificationName()).stream()
				.map(item -> item.getClassId()).collect(toList());
		Classs conceptClasss = Classs.findByName(queryCriteria.getClasssName(), context.getBaseClassificationName());
		return conceptHandler.findConceptIDsByClassAndValues(contextId, conceptClasss.getClassId(), propertyClasssIds,
				queryCriteria.getConditionList());
	}

	protected static Long findAncestorIdByRelationshipClasssId(Long contextId, Long ancestorClasssId,
			Long relationshipClasssId, Long conceptId) {
		return conceptHandler.findAncestorIdByRelationshipClasssId(contextId, ancestorClasssId, relationshipClasssId,
				conceptId);
	}

	/**
	 * Returns a List of ElementIdentifiers for all concepts that we disabled between the from and to context
	 *
	 * @param fromContextId
	 * @param toContextId
	 * @param conceptClasssName
	 * @return
	 */
	public static List<ElementIdentifier> findDisabledConceptIds(Long fromContextId, Long toContextId,
			String conceptClasssName) {
		Context context = Context.findById(toContextId);
		Classs conceptClasss = Classs.findByName(conceptClasssName, context.getClasss().getBaseClassificationName());
		return conceptHandler.findDisabledConceptIds(fromContextId, toContextId, conceptClasss.getClassId());
	}

}