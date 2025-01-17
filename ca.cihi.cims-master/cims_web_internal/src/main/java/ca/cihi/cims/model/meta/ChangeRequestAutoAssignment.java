package ca.cihi.cims.model.meta;

import java.io.Serializable;

import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;

public class ChangeRequestAutoAssignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ChangeRequestStatus toStatus;
	private ChangeRequestLanguage language;
	private ChangeRequestStatus fromStatus;
	private Long assignToDL;

	public ChangeRequestStatus getToStatus() {
		return toStatus;
	}

	public void setToStatus(ChangeRequestStatus toStatus) {
		this.toStatus = toStatus;
	}

	public ChangeRequestLanguage getLanguage() {
		return language;
	}

	public void setLanguage(ChangeRequestLanguage language) {
		this.language = language;
	}

	public ChangeRequestStatus getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(ChangeRequestStatus fromStatus) {
		this.fromStatus = fromStatus;
	}

	public Long getAssignToDL() {
		return assignToDL;
	}

	public void setAssignToDL(Long assignToDL) {
		this.assignToDL = assignToDL;
	}

}
