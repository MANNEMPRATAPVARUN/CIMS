package ca.cihi.cims.hg.mapper.config;

import ca.cihi.cims.dal.PropertyVersion;

/**
 * Describes a property of a wrapper class whose value is stored in a separate element.
 */
public abstract class PropertyElementConfig extends PropertyConfig {

	private String elementClassName;

	private boolean collection;

	public PropertyElementConfig(PropertyMethods propertyMethods,
					String elementClassName, boolean collection) {

		super(propertyMethods);
		this.elementClassName = elementClassName;
		this.collection = collection;
	}

	public String getPropertyElementClassName() {
		return elementClassName;
	}

	public boolean isCollection() {
		return collection;
	}

	public abstract Class<? extends PropertyVersion> getPropertyElementClass();

}
