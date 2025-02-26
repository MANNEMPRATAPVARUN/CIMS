package ca.cihi.cims.dal;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public class NumericPropertyVersion extends DataPropertyVersion<Number> {

	@RequiredForUpdate
	private Number value;

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public void setValue(Number value) {
		this.value = value;
	}
}
