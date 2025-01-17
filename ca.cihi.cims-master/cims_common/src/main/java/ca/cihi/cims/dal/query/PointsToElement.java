package ca.cihi.cims.dal.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PointsToElement extends FieldRestriction {
	private ElementRef target;

	public PointsToElement(ElementRef source, String field,
			ElementRef target) {
		super(source, field);
		this.target = target;
	}

	public ElementRef getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return super.toString() + " = " + target;
	}

	@Override
	public Set<ElementRef> appliesTo() {
		return new HashSet<ElementRef>(Arrays.asList(getElement(), target));
	}

}
