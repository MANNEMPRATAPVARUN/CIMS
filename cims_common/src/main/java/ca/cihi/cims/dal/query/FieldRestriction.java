package ca.cihi.cims.dal.query;

/**
 * A restriction relating to a field of ElementVersion or one of its subclasses.
 */
public abstract class FieldRestriction extends ElementRestriction {

	private String field;

	public FieldRestriction(ElementRef element, String field) {
		super(element);
		this.field = field;
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return getElement() + "." + field;
	}

}
