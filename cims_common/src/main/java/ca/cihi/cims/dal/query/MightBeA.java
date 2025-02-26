package ca.cihi.cims.dal.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Indicates that one element might actually be an instance of a different
 * element - for example, an ElementVersion might be a TextPropertyVersion.
 * Implemented at the query level by an outer join.
 */
public class MightBeA extends ElementRestriction {
	private ElementRef targetElement;

	public MightBeA(ElementRef element, ElementRef targetElement) {
		super(element);
		this.targetElement = targetElement;
	}

	public ElementRef getTargetElement() {
		return targetElement;
	}

	@Override
	public String toString() {
		return getElement() + " might be a " + targetElement;
	}

	@Override
	public Set<ElementRef> appliesTo() {
		return new HashSet<ElementRef>(Arrays.asList(getElement(),
				targetElement));
	}
}
