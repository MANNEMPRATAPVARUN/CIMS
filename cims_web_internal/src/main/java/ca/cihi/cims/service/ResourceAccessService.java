package ca.cihi.cims.service;

import java.util.List;
import java.util.Set;

import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;

public interface ResourceAccessService {
	List<ResourceAccess> findCurrentUserResourceAccesses(Set<SecurityRole> userRoles,
			ChangeRequestStatus changeRequestStatus, AssignmentTypeCode assignmentTypeCode,
			ChangeRequestLanguage changeRequestLanguage);
}
