package ca.cihi.cims.model.tabular.validation;

public class TabularConceptCciValidationSetReportModel {

	private String dataHolding;
	private String gender;
	private String ageRange;
	private String statusReference;
	private String locationReference;
	private String modeOfDeliveryReference;
	private String extentReference;

	// ---------------------------------------------------

	public String getAgeRange() {
		return ageRange;
	}

	public String getDataHolding() {
		return dataHolding;
	}

	public String getExtentReference() {
		return extentReference;
	}

	public String getGender() {
		return gender;
	}

	public String getLocationReference() {
		return locationReference;
	}

	public String getModeOfDeliveryReference() {
		return modeOfDeliveryReference;
	}

	public String getStatusReference() {
		return statusReference;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public void setDataHolding(String dataHolding) {
		this.dataHolding = dataHolding;
	}

	public void setExtentReference(String extentReference) {
		this.extentReference = extentReference;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setLocationReference(String locationReference) {
		this.locationReference = locationReference;
	}

	public void setModeOfDeliveryReference(String modeOfDeliveryReference) {
		this.modeOfDeliveryReference = modeOfDeliveryReference;
	}

	public void setStatusReference(String statusReference) {
		this.statusReference = statusReference;
	}

}
