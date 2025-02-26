package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.domain.Element;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.framework.mapper.PropertyMapper;

/**
 * TB decided later whether we need a class for each type of properties or just have one class for all.
 *
 * @author tyang
 * @version 1.0
 * @created 14-Jun-2016 3:21:21 PM
 */
public abstract class PropertyHandler {

	private static Map<PropertyType, PropertyHandler> propertyHandlers = new HashMap<>();

	static {
		propertyHandlers.put(PropertyType.TextProperty, new TextPropertyHandler());
		propertyHandlers.put(PropertyType.NumericProperty, new NumericPropertyHandler());
		propertyHandlers.put(PropertyType.ConceptProperty, new ConceptPropertyHandler());
		propertyHandlers.put(PropertyType.XLSXProperty, new XLSXPropertyHandler());
	}

	public static boolean checkDuplicateValue(Long conceptElementId, String conceptClasssName,
			String propertyClasssName, String value, ElementIdentifier contextElementIdentifier,
			String tableNameFilter) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("conceptElementId", conceptElementId);
		params.put("contextId",
				contextElementIdentifier != null ? contextElementIdentifier.getElementVersionId() : null);
		params.put("conceptClasssName", conceptClasssName);
		params.put("propertyClasssName", propertyClasssName);
		params.put("value", value.toLowerCase().trim());
		params.put("tableNameFilter", tableNameFilter);
		PropertyMapper propertyMapper = ApplicationContextProvider.getApplicationContext()
				.getBean(PropertyMapper.class);
		Integer count = propertyMapper.countExistsValue(params);
		return (count > 0);
	}

	/**
	 *
	 * @param propertyType
	 */
	public static PropertyHandler findHandler(PropertyType propertyType) {
		PropertyHandler handler = propertyHandlers.get(propertyType);
		PropertyMapper propertyMapper = ApplicationContextProvider.getApplicationContext()
				.getBean(PropertyMapper.class);
		ElementHandler elementHandler = (ElementHandler) ApplicationContextProvider.getApplicationContext()
				.getBean("frameworkElementHandler");
		handler.setElementHandler(elementHandler);
		handler.setPropertyMapper(propertyMapper);
		return handler;
	}

	private ElementHandler elementHandler;

	private PropertyMapper propertyMapper;

	public PropertyHandler() {

	}

	public PropertyDTO findPropertyElementInContext(Long contextId, Long conceptId, String classsName,
			Language language) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("conceptId", conceptId);
		params.put("classsName", classsName);
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		return getPropertyMapper().findPropertyInContext(params);
	}

	public ElementHandler getElementHandler() {
		return elementHandler;
	}

	public PropertyMapper getPropertyMapper() {
		return propertyMapper;
	}

	private void insertPropertyVersion(Long elementVersionId, Long domainElementId, Long propertyClasssId,
			Long elementId, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("propertyId", elementVersionId);
		params.put("domainElementId", domainElementId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", elementId);
		propertyMapper.insertPropertyVersion(params);
		insertPropertyWithValue(domainElementId, propertyClasssId, new ElementIdentifier(elementId, elementVersionId),
				language, value);

	}

	protected abstract void insertPropertyWithValue(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier, Language language, Object value);

	public void setElementHandler(ElementHandler elementHandler) {
		this.elementHandler = elementHandler;
	}

	public void setPropertyMapper(PropertyMapper propertyMapper) {
		this.propertyMapper = propertyMapper;
	}

	/**
	 * Guidelines for implementing this abstract method in subclasses - if the property version does not exist (aka
	 * propertyElementIdentifier is null) -- create the property in the database including the value --- at this stage
	 * we have a new elementId and elementVersionId for the property Else If property version was created in the current
	 * context (check elementversion.originatingContextId) -- update the property value in the appropriate
	 * propertyversion table - Else - property was created in a previous context -- create a new propertyVersion and set
	 * the value return new ElementIdentifier for the property;
	 *
	 * @param contextId
	 * @param conceptIdentifier
	 * @param propertyClasssId
	 * @param language
	 * @param propertyElementIdentifier
	 * @param value
	 */
	@Transactional
	public ElementIdentifier updateProperty(Long contextId, ElementIdentifier conceptIdentifier, Long propertyClasssId,
			Language language, ElementIdentifier propertyElementIdentifier, Object value) {
		ElementIdentifier result = null;
		Long elementId = null;
		if (propertyElementIdentifier == null) {
			elementId = elementHandler.createElement(propertyClasssId, Element.generateBusinessKey());
		} else {
			elementId = propertyElementIdentifier.getElementId();
		}
		Long elementVersionId = elementHandler.updateElementInContext(contextId, elementId);
		ElementDTO elementDTO = elementHandler.findElementInContext(contextId, elementId);
		if (!contextId.equals(elementDTO.getOriginatingContextId()) || (propertyElementIdentifier == null)
				|| (!elementVersionId.equals(propertyElementIdentifier.getElementVersionId()))) {
			insertPropertyVersion(elementVersionId, conceptIdentifier.getElementId(), propertyClasssId, elementId,
					language, value);
		} else {
			updatePropertyValue(elementVersionId, language, value);
		}
		result = new ElementIdentifier(elementId, elementVersionId);
		return result;
	}

	protected abstract void updatePropertyValue(Long elementVersionId, Language language, Object value);

}