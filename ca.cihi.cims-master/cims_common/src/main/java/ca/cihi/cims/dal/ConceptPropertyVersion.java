package ca.cihi.cims.dal;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public class ConceptPropertyVersion extends PropertyVersion {
	
	@RequiredForUpdate
	private Long rangeElementId;

	public Long getRangeElementId() {
		return rangeElementId;
	}

	public void setRangeElementId(Long rangeElementId) {
		this.rangeElementId = rangeElementId;
	}

}
