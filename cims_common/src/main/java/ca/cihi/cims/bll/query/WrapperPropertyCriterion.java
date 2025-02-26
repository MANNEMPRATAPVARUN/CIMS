package ca.cihi.cims.bll.query;

public abstract class WrapperPropertyCriterion extends WrapperCriterion {
	private String property;

	public WrapperPropertyCriterion(Ref wrapper, String property) {
		super(wrapper);
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
