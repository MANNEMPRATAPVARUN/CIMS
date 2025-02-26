package ca.cihi.cims.dal.query;

public class FieldEq extends FieldRestriction {

	private Object value;

	public FieldEq(ElementRef element, String field, Object value) {
		super(element, field);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + " = " + value;
	}
}
