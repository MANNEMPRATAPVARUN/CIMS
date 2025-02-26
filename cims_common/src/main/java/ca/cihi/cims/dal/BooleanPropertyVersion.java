package ca.cihi.cims.dal;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public class BooleanPropertyVersion extends DataPropertyVersion<Boolean> {

	@RequiredForUpdate
	private Boolean value;

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValue(Boolean value) {
		this.value = value;
	}

}
