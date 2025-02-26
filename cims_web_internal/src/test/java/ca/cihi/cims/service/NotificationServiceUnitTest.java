package ca.cihi.cims.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.data.mapper.NotificationMapper;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.model.notification.NotificationDistributionList;
import ca.cihi.cims.model.notification.NotificationTypeCode;
import ca.cihi.cims.model.notification.NotificationUserProfile;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class NotificationServiceUnitTest {

	NotificationServiceImpl notificationService;
	@Mock
	NotificationMapper notificationMapper;
	@Mock
	ChangeRequestMapper changeRequestMapper;
	@Mock
	AdminMapper adminMapper;
	@Autowired
	LookupService lookupService;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificationServiceImpl();
		notificationService.setAdminMapper(adminMapper);
		notificationService.setChangeRequestMapper(changeRequestMapper);
		notificationService.setLookupService(lookupService);
		notificationService.setNotificationMapper(notificationMapper);
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		when(notificationMapper.updateNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.disableNotification(anyLong())).thenReturn(1);

		when(notificationMapper.findNotificationsByUserId(anyLong())).thenReturn(mockNotificationDTOs());

	}

	private NotificationDTO mockAdviceRequestNotificationDTO() {
		NotificationDTO mockNotificationDTO = mockNotificationDTO();
		mockNotificationDTO.setNotificationTypeCode(NotificationTypeCode.AR);

		return mockNotificationDTO;
	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(0L);
		changeRequest.setBaseVersionCode("2016");
		return changeRequest;
	}

	private User mockCurrentUser() {
		User currentUser = new User();
		currentUser.setUserId(0L);
		Set<SecurityRole> roles = new HashSet<SecurityRole>();
		SecurityRole role = SecurityRole.ROLE_ADMINISTRATOR;
		roles.add(role);
		currentUser.setRoles(roles);
		return currentUser;
	}

	private GenerateReleaseTablesCriteria mockGenerateReleaseTablesCriteria() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		return generateReleaseTablesCriteria;
	}

	private List<Long> mockLongList() {
		List<Long> longList = new ArrayList<Long>();
		longList.add(1L);
		return longList;
	}

	private NotificationDTO mockNewAttributeReferenceNotificationDTO() {
		NotificationDTO mockNotificationDTO = mockNotificationDTO();
		mockNotificationDTO.setNotificationTypeCode(NotificationTypeCode.NRVR);
		return mockNotificationDTO;
	}

	private NotificationDTO mockNewComponentRequestNotificationDTO() {
		NotificationDTO mockNotificationDTO = mockNotificationDTO();
		mockNotificationDTO.setNotificationTypeCode(NotificationTypeCode.NCR);
		return mockNotificationDTO;
	}

	private NotificationDTO mockNotificationDTO() {
		NotificationDTO mockNotificationDTO = new NotificationDTO();
		mockNotificationDTO.setNotificationId(0L);
		mockNotificationDTO.setSenderId(1L);

		return mockNotificationDTO;
	}

	private List<NotificationDTO> mockNotificationDTOs() {

		List<NotificationDTO> notifications = new ArrayList<NotificationDTO>();
		NotificationDTO mockNotificationDTO1 = mockNotificationDTO();

		notifications.add(mockNotificationDTO1);

		return notifications;
	}

	private QuestionForReviewer mockQuestionForReviewer() {
		QuestionForReviewer questionForReviewer = new QuestionForReviewer();
		questionForReviewer.setChangeRequestId(1L);
		questionForReviewer.setQuestionForReviewerId(1L);
		questionForReviewer.setQuestionForReviewerTxt("questionForReviewerTxt");
		return questionForReviewer;

	}

	private NotificationDTO mockReviewRequestNotificationDTO() {
		NotificationDTO mockNotificationDTO = mockNotificationDTO();
		mockNotificationDTO.setNotificationTypeCode(NotificationTypeCode.RR);

		return mockNotificationDTO;
	}

	private List<Long> mockUserIds() {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(0L);
		userIds.add(1L);
		return userIds;
	}

	@Test
	public void testCompleteTask() {
		long notificationId = 1l;
		long currentUserId = 1l;
		when(notificationMapper.findNotificationByPrimaryKey(anyLong())).thenReturn(mockReviewRequestNotificationDTO());
		notificationService.completeTask(notificationId, currentUserId);
		when(notificationMapper.findNotificationByPrimaryKey(anyLong())).thenReturn(mockAdviceRequestNotificationDTO());
		notificationService.completeTask(notificationId, currentUserId);
		when(notificationMapper.findNotificationByPrimaryKey(anyLong())).thenReturn(
				mockNewComponentRequestNotificationDTO());
		notificationService.completeTask(notificationId, currentUserId);
		when(notificationMapper.findNotificationByPrimaryKey(anyLong())).thenReturn(
				mockNewAttributeReferenceNotificationDTO());
		notificationService.completeTask(notificationId, currentUserId);
		verify(notificationMapper, times(4)).insertNotification(any(NotificationDTO.class));
	}

	@Test
	public void testFindNotifcationById() {

		when(notificationMapper.findNotificationByPrimaryKey(anyLong())).thenReturn(mockNotificationDTO());
		NotificationDTO notificationDTO = notificationService.findNotifcationById(0L);
		assertTrue(notificationDTO.getNotificationId() == 0L);

	}

	@Test
	public void testFindNotificationsByUserId() {
		when(notificationMapper.findNotificationsByUserId(anyLong())).thenReturn(mockNotificationDTOs());
		List<NotificationDTO> notifications = notificationService.findNotificationsByUserId(0L);
		assertTrue(notifications.size() == 1);
	}

	@Test
	public void testFindNotificationsByUserSerachCriteria() {
		when(notificationMapper.findNotificationsByUserSearchCriteria(any(UserSearchCriteria.class))).thenReturn(
				mockNotificationDTOs());
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		List<NotificationDTO> notifications = notificationService
				.findNotificationsByUserSerachCriteria(userSearchCriteria);
		assertTrue(notifications.size() == 1);
	}

	@Test
	public void testFindNumOfMyNotifications() {
		when(notificationMapper.findNumOfMyNotifications(anyLong())).thenReturn(1);
		int num = notificationService.findNumOfMyNotifications(0L);
		assertTrue(num == 1);
	}

	@Test
	public void testPostAcceptChangeRequestNotifcation() {
		ChangeRequest changeRequest = new ChangeRequest();
		changeRequest.setChangeRequestId(0L);
		User currentUser = new User();
		currentUser.setUserId(0L);
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postAcceptChangeRequestNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(1)).insertNotification(any(NotificationDTO.class));
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostAdviceRequestNotifcation() {
		ChangeRequestDTO changeRequestDTO = new ChangeRequestDTO();
		changeRequestDTO.setChangeRequestId(0L);
		User currentUser = new User();
		currentUser.setUserId(0L);
		Advice advice = new Advice();
		advice.setAdviceId(0L);
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postAdviceRequestNotifcation(changeRequestDTO, advice, currentUser);
		verify(notificationMapper, times(1)).insertNotification(any(NotificationDTO.class));
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostChangeRequestAssignmentNotifcation() {
		ChangeRequestDTO changeRequestDTO = new ChangeRequestDTO();
		changeRequestDTO.setChangeRequestId(0L);
		User currentUser = new User();
		currentUser.setUserId(0L);
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postChangeRequestAssignmentNotifcation(changeRequestDTO, currentUser);
		verify(notificationMapper, times(1)).insertNotification(any(NotificationDTO.class));
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostChangeRequestOwnershipNotifcation() {
		ChangeRequestDTO changeRequestDTO = new ChangeRequestDTO();
		changeRequestDTO.setChangeRequestId(0L);
		changeRequestDTO.setOwnerId(0L);
		User currentUser = new User();
		currentUser.setUserId(0L);
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postChangeRequestOwnershipNotifcation(changeRequestDTO, currentUser);
		verify(notificationMapper, times(1)).insertNotification(any(NotificationDTO.class));
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostChangeRequestPickUpNotifcation() {
		ChangeRequestDTO changeRequestDTO = new ChangeRequestDTO();
		changeRequestDTO.setChangeRequestId(0L);
		changeRequestDTO.setAssigneeDLId(0L);
		User currentUser = new User();
		currentUser.setUserId(0L);
		when(notificationMapper.insertNotificationDistributionList(any(NotificationDistributionList.class)))
				.thenReturn(1);
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postChangeRequestPickUpNotifcation(changeRequestDTO, currentUser);
		verify(notificationMapper, times(1)).insertNotification(any(NotificationDTO.class));
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostNewComponentRequestNotifcation() {
		long changeRequestId = 0;
		String message = "testMag";
		long currentUserId = 0;

		when(changeRequestMapper.findChangeRequestById(anyLong())).thenReturn(mockChangeRequest());
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		when(notificationMapper.insertNotification(any(NotificationDTO.class))).thenReturn(1);
		when(notificationMapper.insertNotificationUserProfile(any(NotificationUserProfile.class))).thenReturn(1);
		notificationService.postNewComponentRequestNotifcation(changeRequestId, message, currentUserId);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPackageReleaseNotifcationOFFICIAL() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPackageReleaseNotifcation(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPackageReleaseNotifcationOFFICIAL_INTERNAL_QA() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL_INTERNAL_QA);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPackageReleaseNotifcation(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPackageReleaseNotifcationPRELIMINARY() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPackageReleaseNotifcation(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPackageReleaseNotifcationPRELIMINARY_INTERNAL_QA() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY_INTERNAL_QA);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPackageReleaseNotifcation(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPickupApprovalNotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPickupApprovalNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPickupQANotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPickupQANotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPickupRealizationChangeRequestNotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPickupRealizationChangeRequestNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPickupTranslationChangeRequestNotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPickupTranslationChangeRequestNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostPickupValidationChangeRequestNotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postPickupValidationChangeRequestNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostReadyForAcceptanceNotifcation() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postReadyForAcceptanceNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostReviewRequestNotifcation() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		QuestionForReviewer questionForReviewer = mockQuestionForReviewer();
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postReviewRequestNotifcation(changeRequestDTO, questionForReviewer, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostSendBackChangeRequestNotifcation() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		notificationService.postSendBackChangeRequestNotifcation(changeRequest, currentUser);
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));

	}

	@Test
	public void testPostWrapupBothCCIICDWorkNotifcationToAdministrator() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postWrapupWorkNotifcationToAdministrator(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostWrapupCCIWorkNotifcationToAdministrator() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postWrapupWorkNotifcationToAdministrator(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostWrapupICDWorkNotifcationToAdministrator() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postWrapupWorkNotifcationToAdministrator(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(2)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}

	@Test
	public void testPostWrapupWorkNotifcationToContentDeveloperAndReviewer() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI);
		User currentUser = mockCurrentUser();
		when(adminMapper.findDistinctUserIdsInDistributionListIds(anyList())).thenReturn(mockUserIds());
		notificationService.postWrapupWorkNotifcationToContentDeveloperAndReviewer(generateTablesCriteria, currentUser);
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD);
		notificationService.postWrapupWorkNotifcationToContentDeveloperAndReviewer(generateTablesCriteria, currentUser);
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		notificationService.postWrapupWorkNotifcationToContentDeveloperAndReviewer(generateTablesCriteria, currentUser);
		verify(notificationMapper, times(6)).insertNotificationUserProfile(any(NotificationUserProfile.class));

	}

	@Test
	public void testRemoveAllNotificationsForYear() {
		String fiscalYear = CIMSTestConstants.TEST_VERSION;
		when(notificationMapper.disableAllNotificationsForYear(fiscalYear)).thenReturn(1);
		notificationService.removeAllNotificationsForYear(fiscalYear);
		verify(notificationMapper, times(1)).disableAllNotificationsForYear(fiscalYear);
	}

	@Test
	public void testRemoveChangeRequestPickupNotification() {
		long changeRequestId = 1l;
		when(notificationMapper.disableChangeRequestPickupNotification(changeRequestId)).thenReturn(1);
		notificationService.removeChangeRequestPickupNotification(changeRequestId);
		verify(notificationMapper, times(1)).disableChangeRequestPickupNotification(changeRequestId);
	}

	@Test
	public void testRemoveMyNotification() {
		long userId = 1l;
		long notificationId = 1l;
		when(notificationMapper.disableMyNotification(anyMap())).thenReturn(1);
		notificationService.removeMyNotification(userId, notificationId);
		verify(notificationMapper, times(1)).disableMyNotification(anyMap());
	}

	@Test
	public void testRemoveMyNotifications() {
		User currentUser = mockCurrentUser();
		List<Long> notificationIds = mockLongList();
		when(notificationMapper.disableMyNotifications(currentUser, notificationIds)).thenReturn(1);
		notificationService.removeMyNotifications(currentUser, notificationIds);
		verify(notificationMapper, times(1)).disableMyNotifications(currentUser, notificationIds);
	}

	@Test
	public void testRemoveNotification() {
		long notificationId = 1l;
		when(notificationMapper.disableNotification(notificationId)).thenReturn(1);
		notificationService.removeNotification(notificationId);
		verify(notificationMapper, times(1)).disableNotification(notificationId);
	}

	@Test
	public void testRemovePickupAcceptanceNotification() {
		long changeRequestId = 1l;
		when(notificationMapper.disablePickupAcceptanceNotification(changeRequestId)).thenReturn(1);
		notificationService.removePickupAcceptanceNotification(changeRequestId);
		verify(notificationMapper, times(1)).disablePickupAcceptanceNotification(changeRequestId);
	}

	@Test
	public void testRemovePickupNewNotification() {
		long changeRequestId = 1l;
		when(notificationMapper.disablePickupNewNotification(changeRequestId)).thenReturn(1);
		notificationService.removePickupNewNotification(changeRequestId);
		verify(notificationMapper, times(1)).disablePickupNewNotification(changeRequestId);
	}

	@Test
	public void testRemoveWrapupWorkNotifcation() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI);
		notificationService.removeWrapupWorkNotifcation(generateTablesCriteria);
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD);
		notificationService.removeWrapupWorkNotifcation(generateTablesCriteria);
		generateTablesCriteria.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		notificationService.removeWrapupWorkNotifcation(generateTablesCriteria);
		verify(notificationMapper, times(3)).disableWrapupWorkNotification(any(NotificationTypeCode.class));
	}

	@Test
	public void testReviewChangeRquestTaskComplete() {
		long notificationId = 1l;
		long currentUserId = 1l;
		when(notificationMapper.findNotificationByPrimaryKey(notificationId)).thenReturn(mockNotificationDTO());
		notificationService.reviewChangeRquestTaskComplete(notificationId, currentUserId);
		verify(notificationMapper, times(1)).insertNotificationUserProfile(any(NotificationUserProfile.class));
	}
	
	
	@Test
	public void testPostNotificationtoDLs() {

		NotificationDTO notificationDTO = mockNotificationDTO();
		notificationDTO.setDlRecipients(Collections.<Distribution>emptyList());

		
		String messageLen494 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklm";
		notificationDTO.setMessage(messageLen494);
		notificationService.postNotificationToDLs(notificationDTO);

		assertThat(notificationDTO.getMessage(), not(isEmptyOrNullString()));
		assertThat(notificationDTO.getMessage().length(), lessThanOrEqualTo(500));

		
		
		String messageLen499 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklmnopqrs";
		notificationDTO.setMessage(messageLen499);
		notificationService.postNotificationToDLs(notificationDTO);

		assertThat(notificationDTO.getMessage(), not(isEmptyOrNullString()));
		assertThat(notificationDTO.getMessage().length(), equalTo(499));

		
		
		String messageLen500 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklmnopqrst";
		notificationDTO.setMessage(messageLen500);
		notificationService.postNotificationToDLs(notificationDTO);
		
		assertThat(notificationDTO.getMessage(), not(isEmptyOrNullString()));
		assertThat(notificationDTO.getMessage().length(), equalTo(500));

		
		String messageLen501 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklmnopqrstu";
		notificationDTO.setMessage(messageLen501);
		notificationService.postNotificationToDLs(notificationDTO);		

		assertThat(notificationDTO.getMessage(), not(isEmptyOrNullString()));
		assertThat(notificationDTO.getMessage().length(), equalTo(501));
		
		
		String messageWith_P_EndTag_Len500 = "<p>123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklm</p>";
		notificationDTO.setMessage(messageWith_P_EndTag_Len500);
		notificationService.postNotificationToDLs(notificationDTO);

		assertThat(notificationDTO.getMessage().length(), equalTo(500));
		assertThat(notificationDTO.getMessage(), endsWith("</p>"));


		String messageWith_P_Endtag_Len_504 = "<p>123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abcdefghijklmnopq</p>";
		notificationDTO.setMessage(messageWith_P_Endtag_Len_504);
		notificationService.postNotificationToDLs(notificationDTO);
		
		assertThat(notificationDTO.getMessage().length(), equalTo(504));
		assertThat(notificationDTO.getMessage(), endsWith("</p>"));
		  
	}
	
	
}
