package ca.cihi.cims.dal.query;

public class Fieldike extends FieldRestriction {

	private String likeExpression;

	public Fieldike(ElementRef element, String field,
			String likeExpression) {
		super(element, field);
		this.likeExpression = likeExpression;
	}

	public String getLikeExpression() {
		return likeExpression;
	}

	@Override
	public String toString() {
		return super.toString() + " like " + likeExpression;
	}
}
