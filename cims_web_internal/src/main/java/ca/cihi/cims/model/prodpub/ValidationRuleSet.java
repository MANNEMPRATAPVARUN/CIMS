package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

public class ValidationRuleSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8258364785507784486L;
	private Long conceptId;
	private String code;
	private String validationRuleText;
	private String xmlText;
	private String dhcode;
	private String statusRef;
	private String locationRef;
	private String extentRef;
	private String hasChild;

	public String getCode() {
		return code;
	}

	public Long getConceptId() {
		return conceptId;
	}

	public String getDhcode() {
		return dhcode;
	}

	public String getExtentRef() {
		return extentRef;
	}

	public String getHasChild() {
		return hasChild;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public String getStatusRef() {
		return statusRef;
	}

	public String getValidationRuleText() {
		return validationRuleText;
	}

	public String getXmlText() {
		return xmlText;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setConceptId(Long conceptId) {
		this.conceptId = conceptId;
	}

	public void setDhcode(String dhcode) {
		this.dhcode = dhcode;
	}

	public void setExtentRef(String extentRef) {
		this.extentRef = extentRef;
	}

	public void setHasChild(String hasChild) {
		this.hasChild = hasChild;
	}

	public void setLocationRef(String locationRef) {
		this.locationRef = locationRef;
	}

	public void setStatusRef(String statusRef) {
		this.statusRef = statusRef;
	}

	public void setValidationRuleText(String validationRuleText) {
		this.validationRuleText = validationRuleText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}
}
