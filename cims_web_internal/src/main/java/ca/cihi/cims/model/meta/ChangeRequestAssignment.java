package ca.cihi.cims.model.meta;

import java.io.Serializable;

import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;

public class ChangeRequestAssignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ChangeRequestStatus status;
	private ChangeRequestLanguage language;
	private Long assignToDL;
	private AssigneeType assigneeType;
	private Boolean defaultInd; // indicator the DL is default or not

	public ChangeRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ChangeRequestStatus status) {
		this.status = status;
	}

	public ChangeRequestLanguage getLanguage() {
		return language;
	}

	public void setLanguage(ChangeRequestLanguage language) {
		this.language = language;
	}

	public Long getAssignToDL() {
		return assignToDL;
	}

	public void setAssignToDL(Long assignToDL) {
		this.assignToDL = assignToDL;
	}

	public Boolean getDefaultInd() {
		return defaultInd;
	}

	public void setDefaultInd(Boolean defaultInd) {
		this.defaultInd = defaultInd;
	}

	public AssigneeType getAssigneeType() {
		return assigneeType;
	}

	public void setAssigneeType(AssigneeType assigneeType) {
		this.assigneeType = assigneeType;
	}

}
