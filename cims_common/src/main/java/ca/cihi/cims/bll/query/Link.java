package ca.cihi.cims.bll.query;

public class Link<X, Y> extends WrapperPropertyCriterion {
	private Ref<Y> targetWrapper;
	private Long targetElementId;

	public Link(Ref<X> wrapper, String property, Ref<Y> targetWrapper) {
		super(wrapper, property);
		this.targetWrapper = targetWrapper;
	}

	public Link(Ref<X> wrapper, String property, Long targetElementId) {
		super(wrapper, property);
		this.targetElementId = targetElementId;
	}

	public Ref<Y> getTargetWrapper() {
		return targetWrapper;
	}

	public Long getTargetElementId() {
		return targetElementId;
	}
}
