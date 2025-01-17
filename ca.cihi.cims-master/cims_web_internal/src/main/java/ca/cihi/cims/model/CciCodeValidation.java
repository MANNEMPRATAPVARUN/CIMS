package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * 
 * @author wxing
 * 
 */
public class CciCodeValidation extends BaseSerializableCloneableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3131558920511961372L;

	private String code;
	private String dataHolding;
	private String ageRange;
	private String gender;
	private String statusRef;
	private String locationRef;
	private String extentRef;
	private String validationXml;

	public String getAgeRange() {
		return ageRange;
	}

	public String getCode() {
		return code;
	}

	public String getDataHolding() {
		return dataHolding;
	}

	public String getExtentRef() {
		return extentRef;
	}

	public String getGender() {
		return gender;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public String getStatusRef() {
		return statusRef;
	}

	public String getValidationXml() {
		return validationXml;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDataHolding(String dataHolding) {
		this.dataHolding = dataHolding;
	}

	public void setExtentRef(String extentRef) {
		this.extentRef = extentRef;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setLocationRef(String locationRef) {
		this.locationRef = locationRef;
	}

	public void setStatusRef(String statusRef) {
		this.statusRef = statusRef;
	}

	public void setValidationXml(String validationXml) {
		this.validationXml = validationXml;
	}

}
