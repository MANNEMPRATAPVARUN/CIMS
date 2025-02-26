package ca.cihi.cims.bll.query;

public class EqCriterion extends WrapperPropertyCriterion {
	private Object value;

	public EqCriterion(Ref wrapper, String property, Object value) {
		super(wrapper, property);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

}
