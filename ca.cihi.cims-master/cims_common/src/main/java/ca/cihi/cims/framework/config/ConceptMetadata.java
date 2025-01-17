package ca.cihi.cims.framework.config;

import java.util.Map;

import ca.cihi.cims.framework.enums.ConceptLoadDegree;

/**
 * There should be an shared instance of this class for each type of concept(e.g. Refset, PickList)
 *
 * @author MIftimie
 * @version 1.0
 * @created 14-Jun-2016 3:23:06 PM
 */
public class ConceptMetadata {

	/**
	 * Identifies all the properties types a certain concept type is expected to have.
	 */
	private Map<ConceptLoadDegree, ConceptPropertyConfiguration> propertyConfigurations;
	/**
	 * Examples if key/value
	 *
	 * Name_ENG/ (Name,ENG,TextProperty) Name_FRA/(Name,FRA,TextProperty)
	 * EffectiveYearFrom/(EffectiveYearFrom,NOLANGUAGE,NumericProperty)
	 */
	private Map<String, PropertyKey> propertyKeys;

	public ConceptMetadata() {

	}

	public Map<ConceptLoadDegree, ConceptPropertyConfiguration> getPropertyConfigurations() {
		return propertyConfigurations;
	}

	public Map<String, PropertyKey> getPropertyKeys() {
		return propertyKeys;
	}

	public void setPropertyConfigurations(Map<ConceptLoadDegree, ConceptPropertyConfiguration> propertyConfigurations) {
		this.propertyConfigurations = propertyConfigurations;
	}

	public void setPropertyKeys(Map<String, PropertyKey> propertyKeys) {
		this.propertyKeys = propertyKeys;
	}

}