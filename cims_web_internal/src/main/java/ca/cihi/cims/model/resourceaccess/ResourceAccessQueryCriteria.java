package ca.cihi.cims.model.resourceaccess;

import java.io.Serializable;
import java.util.Set;

import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;

public class ResourceAccessQueryCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<SecurityRole> userRoles;
	private ChangeRequestStatus changeRequestStatus;
	private AssignmentTypeCode assignmentTypeCode;
	private ChangeRequestLanguage changeRequestLanguage;

	public Set<SecurityRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<SecurityRole> userRoles) {
		this.userRoles = userRoles;
	}

	public ChangeRequestStatus getChangeRequestStatus() {
		return changeRequestStatus;
	}

	public void setChangeRequestStatus(ChangeRequestStatus changeRequestStatus) {
		this.changeRequestStatus = changeRequestStatus;
	}

	public AssignmentTypeCode getAssignmentTypeCode() {
		return assignmentTypeCode;
	}

	public void setAssignmentTypeCode(AssignmentTypeCode assignmentTypeCode) {
		this.assignmentTypeCode = assignmentTypeCode;
	}

	public ChangeRequestLanguage getChangeRequestLanguage() {
		return changeRequestLanguage;
	}

	public void setChangeRequestLanguage(ChangeRequestLanguage changeRequestLanguage) {
		this.changeRequestLanguage = changeRequestLanguage;
	}

}
