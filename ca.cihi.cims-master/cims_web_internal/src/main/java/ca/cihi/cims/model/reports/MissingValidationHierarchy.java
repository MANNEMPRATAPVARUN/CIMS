package ca.cihi.cims.model.reports;

import java.io.Serializable;

public class MissingValidationHierarchy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4110085613888838770L;

	private String code;
	private Long elementId;
	private String elementIdPath;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getElementId() {
		return elementId;
	}
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	public String getElementIdPath() {
		return elementIdPath;
	}
	public void setElementIdPath(String elementIdPath) {
		this.elementIdPath = elementIdPath;
	}
}
