package ca.cihi.cims.service;

import java.util.List;
import java.util.Set;

import ca.cihi.cims.data.mapper.ResourceAccessMapper;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceAccessQueryCriteria;

public class ResourceAccessServiceImpl implements ResourceAccessService {

	private ResourceAccessMapper resourceAccessMapper;

	@Override
	public List<ResourceAccess> findCurrentUserResourceAccesses(Set<SecurityRole> userRoles,
			ChangeRequestStatus changeRequestStatus, AssignmentTypeCode assignmentTypeCode,
			ChangeRequestLanguage changeRequestLanguage) {
		// TODO Auto-generated method stub
		ResourceAccessQueryCriteria queryCriteria = new ResourceAccessQueryCriteria();
		queryCriteria.setUserRoles(userRoles);
		queryCriteria.setAssignmentTypeCode(assignmentTypeCode);
		queryCriteria.setChangeRequestLanguage(changeRequestLanguage);
		queryCriteria.setChangeRequestStatus(changeRequestStatus);

		return resourceAccessMapper.findMyResourceAccesses(queryCriteria);
	}

	public ResourceAccessMapper getResourceAccessMapper() {
		return resourceAccessMapper;
	}

	public void setResourceAccessMapper(ResourceAccessMapper resourceAccessMapper) {
		this.resourceAccessMapper = resourceAccessMapper;
	}

}
