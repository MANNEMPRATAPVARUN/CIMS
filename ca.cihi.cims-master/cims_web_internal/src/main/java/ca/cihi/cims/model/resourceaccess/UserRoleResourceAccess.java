package ca.cihi.cims.model.resourceaccess;

import java.io.Serializable;

import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;

public class UserRoleResourceAccess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SecurityRole userRole;
	private ChangeRequestStatus changeRequestStatus;
	private ChangeRequestLanguage changeRequestLanguage;
	private AssignmentTypeCode assignmentTypeCode; // are you assignee, owner_assignee, ot not assignee
	private ResourceCode resouceCode;
	private AccessCode accessCode;

	public SecurityRole getUserRole() {
		return userRole;
	}

	public void setUserRole(SecurityRole userRole) {
		this.userRole = userRole;
	}

	public ChangeRequestStatus getChangeRequestStatus() {
		return changeRequestStatus;
	}

	public void setChangeRequestStatus(ChangeRequestStatus changeRequestStatus) {
		this.changeRequestStatus = changeRequestStatus;
	}

	public ChangeRequestLanguage getChangeRequestLanguage() {
		return changeRequestLanguage;
	}

	public void setChangeRequestLanguage(ChangeRequestLanguage changeRequestLanguage) {
		this.changeRequestLanguage = changeRequestLanguage;
	}

	public AssignmentTypeCode getAssignmentTypeCode() {
		return assignmentTypeCode;
	}

	public void setAssignmentTypeCode(AssignmentTypeCode assignmentTypeCode) {
		this.assignmentTypeCode = assignmentTypeCode;
	}

	public ResourceCode getResouceCode() {
		return resouceCode;
	}

	public void setResouceCode(ResourceCode resouceCode) {
		this.resouceCode = resouceCode;
	}

	public AccessCode getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(AccessCode accessCode) {
		this.accessCode = accessCode;
	}

}
