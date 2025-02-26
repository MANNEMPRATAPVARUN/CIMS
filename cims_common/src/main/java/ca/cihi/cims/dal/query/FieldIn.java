package ca.cihi.cims.dal.query;

import java.util.Collection;

public class FieldIn extends FieldRestriction {

	private Collection<?> values;

	public FieldIn(ElementRef element, String field,
			Collection<?> values) {
		super(element, field);
		this.values = values;
	}

	public Collection<?> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return super.toString() + " in " + values;
	}

}
