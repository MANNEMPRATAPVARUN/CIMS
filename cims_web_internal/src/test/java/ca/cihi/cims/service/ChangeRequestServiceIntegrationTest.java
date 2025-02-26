package ca.cihi.cims.service;

import static ca.cihi.cims.util.CollectionUtils.asSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Transactional
@Rollback
public class ChangeRequestServiceIntegrationTest {

	private static final String ICD_BASE_CLASSIFICATION = "ICD-10-CA";
	private static final String CIMS_USER_INIT1 = "cims_init1";

	@Autowired
	private AdminService adminService;
	@Autowired
	private ChangeRequestService changeRequestService;
	@Autowired
	private LookupService lookupService;

	// ------------------------------------------------------------

	private User getInitiatorTestUser() {
		User user = adminService.getUserByUserName(CIMS_USER_INIT1);
		if (user == null) {
			user = new User();
			user.setUserId(new Long(999999999l));
			user.setUsername(CIMS_USER_INIT1);
			user.setFirstname("f");
			user.setLastname("l");
			user.setEmail(CIMS_USER_INIT1 + "@cihi.ca");
			user.setCreatedDate(new Date());
			adminService.createUser(user);
		}
		user.setRoles(asSet(SecurityRole.ROLE_INITIATOR));
		return user;
	}

	/*
	 * @Test public void testFindAllChangeRequests(){ List<ChangeRequest>
	 * allChangeRequests=changeRequestService.findAllChangeRequests(); System.out.println(allChangeRequests.size()); }
	 */

	private ContextIdentifier getOpenContextIdentifierForVersionYear() {
		Collection<ContextIdentifier> contextIdentifiers = lookupService
				.findBaseContextIdentifiers(ICD_BASE_CLASSIFICATION);
		for (ContextIdentifier id : contextIdentifiers) {
			if (!id.isChangeContext() && id.isVersionYear() && id.isContextOpen()) {
				return id;
			}
		}
		return null;
	}

	@Test
	public void testCreateAndFindChangeRequest() {
		ChangeRequestDTO cr = new ChangeRequestDTO();
		User currentUser = getInitiatorTestUser();
		ContextIdentifier contextId = getOpenContextIdentifierForVersionYear();
		cr.setBaseContextId(contextId.getContextId());
		cr.setLanguageCode("ENG");
		cr.setBaseVersionCode(contextId.getVersionCode());
		cr.setName("test create change request");
		cr.setStatus(ChangeRequestStatus.NEW_INIT_NO_OWNER);
		cr.setCategory(ChangeRequestCategory.T);
		cr.setRequestorId(7L);
		cr.setChangeNatureId(4L);
		cr.setChangeTypeId(1L);
		cr.setAssigneeUserId(currentUser.getUserId());
		cr.setOwnerId(currentUser.getUserId());
		cr.setChangeRationalTxt("just a test");
		cr.setPatternTopic("test");
		List<Distribution> reviewGroups = new ArrayList<Distribution>();
		Distribution dl1 = new Distribution();
		dl1.setDistributionlistid(Distribution.DL_ID_Classification);
		reviewGroups.add(dl1);
		cr.setReviewGroups(reviewGroups);
		changeRequestService.createChangeRequest(cr, currentUser); // test create
		cr.setName("test create change request update");

		ChangeRequestDTO n = changeRequestService.findCourseGrainedChangeRequestDTOById(cr.getChangeRequestId());
		cr.setLastUpdatedTime(n.getLastUpdatedTime());
		changeRequestService.updateChangeRequest(cr, currentUser); // test update

		n = changeRequestService.findCourseGrainedChangeRequestDTOById(cr.getChangeRequestId());
		cr.setLastUpdatedTime(n.getLastUpdatedTime());
		changeRequestService.submitChangeRequest(cr, currentUser); // test submit

		ChangeRequest changeRequestInTable = changeRequestService
				.findLightWeightChangeRequestById(cr.getChangeRequestId());
		assertTrue(changeRequestInTable != null);
		n = changeRequestService.findCourseGrainedChangeRequestDTOById(cr.getChangeRequestId());
		changeRequestService.deleteChangeRequest(n, currentUser);
	}

	@Test
	public void testFindChangeRequestsBySearchCriteria() {
		UserSearchCriteria changeRequestSearchCriteria = new UserSearchCriteria();
		changeRequestSearchCriteria.setStartRow(2);
		changeRequestSearchCriteria.setEndRow(6);
		changeRequestSearchCriteria.setSortBy("NAME");
		changeRequestSearchCriteria.setAscending(false);
		List<ChangeRequest> searchResult = changeRequestService
				.findChangeRequestsBySearchCriteria(changeRequestSearchCriteria);
		searchResult.size();
		// FIXME: not sure if <>0 number of change requests is present
		// FIXME: add assertion
	}

	@Test
	public void testFindNumOfMyChangeRequests() {
		changeRequestService.findNumOfMyChangeRequests(6L);
	}

	@Test
	public void testIsChangeRequestNameExist() {
		boolean exist = changeRequestService.isChangeRequestNameExist(System.currentTimeMillis() + "111");
		assertFalse(exist);
	}

}
