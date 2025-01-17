package ca.cihi.cims.bll.query;

public class FindCriteria {

	public static <T> Ref<T> ref(Class<T> clazz) {
		return new Ref<T>(clazz);
	}

	public static EqCriterion eq(Ref wrapper, String property, Object value) {
		return new EqCriterion(wrapper, property, value);
	}

	public static LikeCriterion like(Ref wrapper, String property,
			String expression) {
		return new LikeCriterion(wrapper, property, expression);
	}

	public static Link link(Ref wrapper, String property, Ref target) {
		return new Link(wrapper, property, target);
	}

	public static LinkTrans linkTrans(Ref wrapper, String property, Ref target) {
		return new LinkTrans(wrapper, property, target);
	}

}
