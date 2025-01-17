package ca.cihi.cims.framework.config;

import java.util.List;

/**
 * @author miftimie
 * @version 1.0
 * @created 14-Jun-2016 3:22:53 PM
 */
public class ConceptPropertyConfiguration {

	private List<PropertyKey> keys;

	public ConceptPropertyConfiguration() {

	}

	public List<PropertyKey> getKeys() {
		return keys;
	}

	public void setKeys(List<PropertyKey> keys) {
		this.keys = keys;
	}

}