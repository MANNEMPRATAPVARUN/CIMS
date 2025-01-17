package ca.cihi.cims.dal.query;

import java.util.Set;

public abstract class Restriction {

	public abstract Set<ElementRef> appliesTo();
}
