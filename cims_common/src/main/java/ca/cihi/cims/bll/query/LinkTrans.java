package ca.cihi.cims.bll.query;

public class LinkTrans<X, Y> extends WrapperPropertyCriterion {
	private Ref<Y> targetWrapper;

	public LinkTrans(Ref<X> wrapper, String property, Ref<Y> targetWrapper) {
		super(wrapper, property);
		this.targetWrapper = targetWrapper;
	}

	public Ref<Y> getTargetWrapper() {
		return targetWrapper;
	}

}
