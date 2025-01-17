package ca.cihi.cims.dal.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class OrRestriction extends Restriction {

	private Collection<Restriction> subRestrictions;

	public OrRestriction(Restriction... restrict) {
		subRestrictions = Arrays.asList(restrict);
	}

	public OrRestriction(Collection<Restriction> subRestrictions) {
		this.subRestrictions = subRestrictions;
	}

	public Collection<Restriction> getSubRestrictions() {
		return subRestrictions;
	}

	public String toString() {
		return "( " + StringUtils.join(subRestrictions, "\n\tOR\n\t") + " )";
	}

	@Override
	public Set<ElementRef> appliesTo() {
		Set<ElementRef> appliesTo = new HashSet<ElementRef>();
		for (Restriction r : subRestrictions) {
			appliesTo.addAll(r.appliesTo());
		}
		return appliesTo;
	}
}
