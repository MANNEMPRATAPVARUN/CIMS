package ca.cihi.cims.bll.query;

public class LikeCriterion extends WrapperPropertyCriterion {
	public String likeExpression;

	public LikeCriterion(Ref wrapper, String property,
			String likeExpression) {
		super(wrapper, property);
		this.likeExpression = likeExpression;
	}

	public String getLikeExpression() {
		return likeExpression;
	}

}
