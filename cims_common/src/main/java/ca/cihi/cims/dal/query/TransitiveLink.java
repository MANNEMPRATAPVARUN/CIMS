package ca.cihi.cims.dal.query;

public class TransitiveLink extends ElementRestriction {
	private String relationshipClass;

	private boolean inverse;

	private ElementRef target;

	public TransitiveLink(ElementRef element, String relationshipClass,
			boolean inverse, ElementRef target) {
		super(element);
		this.relationshipClass = relationshipClass;
		this.inverse = inverse;
		this.target = target;
	}

	public String getRelationshipClass() {
		return relationshipClass;
	}

	public boolean isInverse() {
		return inverse;
	}

	public ElementRef getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return getElement() + " --" + relationshipClass
				+ (inverse ? "(inv)" : "") + "-->" + target;
	}
}
