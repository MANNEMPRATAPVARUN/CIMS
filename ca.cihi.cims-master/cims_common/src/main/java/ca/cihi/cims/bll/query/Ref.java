package ca.cihi.cims.bll.query;

/**
 * This class indicates a wrapper of interest to a query. (This this class is
 * only used in queries, and is not a wrapper in and of itself, merely an
 * indication that you want to search one or refer to one in a query.)
 */
@SuppressWarnings("unchecked")
public class Ref<T> {
	private Class<T> wrapperClass;

	public Ref(Class<T> wrapperClass) {
		this.wrapperClass = wrapperClass;
	}

	public Class<T> getWrapperClass() {
		return wrapperClass;
	}

	public EqCriterion eq(String property, Object value) {
		return FindCriteria.eq(this, property, value);
	}

	public LikeCriterion like(String property, String expression) {
		return FindCriteria.like(this, property, expression);
	}

	public <X> Link<T, X> link(String property, Ref<X> target) {
		return FindCriteria.link(this, property, target);
	}

	public <X> LinkTrans<T, X> linkTrans(String property, Ref<X> target) {
		return FindCriteria.linkTrans(this, property, target);
	}
}