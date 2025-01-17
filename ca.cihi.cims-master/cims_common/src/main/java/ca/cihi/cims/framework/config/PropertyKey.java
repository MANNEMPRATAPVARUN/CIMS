package ca.cihi.cims.framework.config;

import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 11:17:43 AM
 */
public class PropertyKey {

	public static PropertyKey createKey(PropertyDTO propertyDTO) {
		return new PropertyKey(propertyDTO.getClasss().getClasssName(), propertyDTO.getLanguage(),
				propertyDTO.getPropertyType());
	}

	private String classsName;

	private Language language;

	private PropertyType propertyType;

	public PropertyKey(String classsName, Language language, PropertyType propertyType) {
		setClasssName(classsName);
		setLanguage(language);
		setPropertyType(propertyType);
	}

	public String generateKeyIdentifier() {
		return classsName + "_" + language.getCode();
	}

	public String getClassName() {
		return classsName;
	}

	public Language getLanguage() {
		return language;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setClasssName(String classsName) {
		this.classsName = classsName;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

}