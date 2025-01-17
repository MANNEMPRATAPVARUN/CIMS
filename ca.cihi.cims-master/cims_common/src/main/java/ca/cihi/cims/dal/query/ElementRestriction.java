package ca.cihi.cims.dal.query;

import java.util.HashSet;
import java.util.Set;

public abstract class ElementRestriction extends Restriction {
	private ElementRef element;

	public ElementRestriction(ElementRef element) {
		if (element == null) {
			throw new IllegalArgumentException(
					"Element restrictions cannot apply to a null element.");
		}
		this.element = element;
	}

	public ElementRef getElement() {
		return element;
	}

	@Override
	public Set<ElementRef> appliesTo() {
		HashSet<ElementRef> set = new HashSet<ElementRef>();
		set.add(element);
		return set;
	}
}
