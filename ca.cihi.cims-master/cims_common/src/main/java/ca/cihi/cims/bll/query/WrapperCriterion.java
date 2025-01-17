package ca.cihi.cims.bll.query;

public abstract class WrapperCriterion extends FindCriterion {
	private Ref wrapper;

	public WrapperCriterion(Ref wrapper) {
		this.wrapper = wrapper;
	}

	public Ref getWrapper() {
		return wrapper;
	}

}
