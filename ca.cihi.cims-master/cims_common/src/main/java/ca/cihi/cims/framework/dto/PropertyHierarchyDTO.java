package ca.cihi.cims.framework.dto;

import java.io.Serializable;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.Language;

/**
 * Describes a property in terms of its classs, identifier, value and language. Includes concept identifier and parent
 * concept identifier. The intended use of this classs is to construct hierarchies of concepts for a specified
 * relationship.
 *
 * @author tyang
 * @version 1.0
 * @created 22-Jun-2016 9:49:17 AM
 */
public class PropertyHierarchyDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1408660414086845743L;
	private Long conceptClasssId;
	private ElementIdentifier conceptId;
	private ElementIdentifier parentId;
	private Long propertyClasssId;
	private ElementIdentifier propertyId;
	private Language propertyLanguage;
	private String propertyValue;

	public PropertyHierarchyDTO() {

	}

	public Long getConceptClasssId() {
		return conceptClasssId;
	}

	public ElementIdentifier getConceptId() {
		return conceptId;
	}

	public ElementIdentifier getParentId() {
		return parentId;
	}

	public Long getPropertyClasssId() {
		return propertyClasssId;
	}

	public ElementIdentifier getPropertyId() {
		return propertyId;
	}

	public Language getPropertyLanguage() {
		return propertyLanguage;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setConceptClasssId(Long conceptClasssId) {
		this.conceptClasssId = conceptClasssId;
	}

	public void setConceptId(ElementIdentifier conceptId) {
		this.conceptId = conceptId;
	}

	public void setParentId(ElementIdentifier parentId) {
		this.parentId = parentId;
	}

	public void setPropertyClasssId(Long propertyClasssId) {
		this.propertyClasssId = propertyClasssId;
	}

	public void setPropertyId(ElementIdentifier propertyId) {
		this.propertyId = propertyId;
	}

	public void setPropertyLanguage(Language propertyLanguage) {
		this.propertyLanguage = propertyLanguage;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}