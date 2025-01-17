package ca.cihi.cims.model.tabular.validation;

import org.hibernate.validator.constraints.NotEmpty;

public/* abstract */class TabularConceptValidationSetModel {

	/** dataHoldingId **/
	private long elementId;

	@NotEmpty
	private String genderCode;

	private int ageMinimum = 0;
	private int ageMaximum = 130;

	private boolean disabled;

	// --------------------------------------------------

	public int getAgeMaximum() {
		return ageMaximum;
	}

	public int getAgeMinimum() {
		return ageMinimum;
	}

	/** dataHoldingId **/
	public long getElementId() {
		return elementId;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setAgeMaximum(int ageMaximum) {
		this.ageMaximum = ageMaximum;
	}

	public void setAgeMinimum(int ageMinimum) {
		this.ageMinimum = ageMinimum;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/** dataHoldingId **/
	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	@Override
	public String toString() {
		return "TabularConceptValidationSetModel [elementId=" + elementId + ", genderCode=" + genderCode
				+ ", disabled=" + disabled + ", ageMaximum=" + ageMaximum + ", ageMinimum=" + ageMinimum + "]";
	}

}
