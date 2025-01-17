package ca.cihi.cims.framework.dto;

import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;

/**
 * Important: In case of ConceptProperty the value type should be ElementIdentifier
 *
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:41:17 AM
 */
public class PropertyDTO extends ElementDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 601910113905199358L;
	private Long domainElementId;
	private Language language;
	private PropertyType propertyType;
	private Object value;

	public Long getDomainElementId() {
		return domainElementId;
	}

	public Language getLanguage() {
		return language;
	}

	public PropertyKey getPropertyKey() {
		return new PropertyKey(getClasss().getClasssName(), language, propertyType);
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public Object getValue() {
		return value;
	}

	public void setDomainElementId(Long domainElementId) {
		this.domainElementId = domainElementId;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}