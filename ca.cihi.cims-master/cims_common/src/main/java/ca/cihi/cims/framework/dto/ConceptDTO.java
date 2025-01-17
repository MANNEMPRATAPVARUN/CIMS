package ca.cihi.cims.framework.dto;

import java.util.List;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:49:58 AM
 */
public class ConceptDTO extends ElementDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1843727010054919800L;
	private List<PropertyDTO> loadedProperties;

	public List<PropertyDTO> getLoadedProperties() {
		return loadedProperties;
	}

	public void setLoadedProperties(List<PropertyDTO> loadedProperties) {
		this.loadedProperties = loadedProperties;
	}

}