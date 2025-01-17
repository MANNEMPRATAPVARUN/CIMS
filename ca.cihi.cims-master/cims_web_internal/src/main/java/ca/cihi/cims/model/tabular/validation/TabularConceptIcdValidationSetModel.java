package ca.cihi.cims.model.tabular.validation;

import javax.validation.constraints.NotNull;

public class TabularConceptIcdValidationSetModel extends TabularConceptValidationSetModel {

	@NotNull
	private Long dxTypeId;
	private boolean newBorn;

	// --------------------------------------------------

	public Long getDxTypeId() {
		return dxTypeId;
	}

	public boolean isNewBorn() {
		return newBorn;
	}

	public void setDxTypeId(Long dxTypeId) {
		this.dxTypeId = dxTypeId;
	}

	public void setNewBorn(boolean newBorn) {
		this.newBorn = newBorn;
	}

	@Override
	public String toString() {
		return "TabularConceptIcdValidationSetModel [dxTypeId=" + dxTypeId + ", newBorn=" + newBorn
				+ ", getAgeMaximum()=" + getAgeMaximum() + ", getAgeMinimum()=" + getAgeMinimum() + ", getElementId()="
				+ getElementId() + ", getGender()=" + getGenderCode() + "]";
	}

}
