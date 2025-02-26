package ca.cihi.cims.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ResourceAccessServiceIntegrationTest {
	@Autowired
	ResourceAccessService resourceAccessService;

	@Test
	public void testFindInitiatorResourceAccess() {
		Set<SecurityRole> userRoles = new HashSet<SecurityRole>();
		userRoles.add(SecurityRole.ROLE_INITIATOR);
		List<ResourceAccess> resourceAccesses = resourceAccessService.findCurrentUserResourceAccesses(userRoles,
				ChangeRequestStatus.NEW_INIT_NO_OWNER, AssignmentTypeCode.ASSIGNEE, ChangeRequestLanguage.ENG);
		System.out.println(resourceAccesses.size());
		for (ResourceAccess resourceAccess : resourceAccesses) {
			System.out.println(resourceAccess.getResourceCode());
		}
	}

	@Test
	public void testFindEngContentDeveoperResourceAccess() {
		Set<SecurityRole> userRoles = new HashSet<SecurityRole>();
		userRoles.add(SecurityRole.ROLE_ENG_CONTENT_DEVELOPER);
		userRoles.add(SecurityRole.ROLE_REVIEWER);
		List<ResourceAccess> resourceAccesses = resourceAccessService.findCurrentUserResourceAccesses(userRoles,
				ChangeRequestStatus.NEW_WITH_OWNER, AssignmentTypeCode.NO_ASSIGNEE, ChangeRequestLanguage.ENG);
		System.out.println(resourceAccesses.size());
		for (ResourceAccess resourceAccess : resourceAccesses) {
			System.out.println(resourceAccess.getResourceCode());
		}
	}
}
