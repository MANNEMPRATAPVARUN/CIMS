package ca.cihi.cims.content.shared;

import ca.cihi.cims.bll.Identified;

public abstract class BaseConcept implements Identified {

	@Override
	public abstract Long getElementId();

	@Override
	public abstract void setElementId(Long elementId);
}
