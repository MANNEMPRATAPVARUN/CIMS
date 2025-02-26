package ca.cihi.cims.web.bean;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * Java bean that presents a key-value pair object. List of KeyValueBean are used in a view to populate drop down list,
 * check boxes, radio buttons
 * 
 */
public class KeyValueBean extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = -1089322062852325883L;

	private String key;
	private String value;

	// -----------------------------------------------------------

	public KeyValueBean() {
	}

	public KeyValueBean(String key) {
		this(key, key);
	}

	public KeyValueBean(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "[" + key + "=" + value + "]";
	}
}
