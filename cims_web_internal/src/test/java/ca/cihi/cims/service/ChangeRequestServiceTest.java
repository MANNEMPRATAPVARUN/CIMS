package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.hg.HGContextAccess;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestEvolution;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.ChangeRequestRealizationStatus;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.DocumentReferenceType;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.service.synchronization.SynchronizationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestServiceTest {

	ChangeRequestServiceImpl changeRequestService;
	@Mock
	ChangeRequestMapper changeRequestMapper;
	@Mock
	ContextProvider contextProvider;
	// need mock a user here

	@Mock
	NotificationService notificationService;
	@Mock
	ChangeRequestHistoryService changeRequestHistoryService;
	@Mock
	AdminService adminService;
	@Mock
	ChangeRequestSummaryService changeRequestSummaryService;
	@Mock
	LookupService lookupService;
	@Mock
	ContextService contextService;
	@Mock
	FileService fileService;
	@Mock
	SynchronizationService synchronizationService;
	@Mock
	ContextOperations contextOperations;
	@Mock
	ContextAccess context;

	static Date testDate = Calendar.getInstance().getTime();

	@Before
	public void initializeMocks() throws IOException {
		MockitoAnnotations.initMocks(this);
		changeRequestService = new ChangeRequestServiceImpl();
		changeRequestService.setChangeRequestMapper(changeRequestMapper);
		changeRequestService.setAdminService(adminService);
		changeRequestService.setChangeRequestHistoryService(changeRequestHistoryService);
		changeRequestService.setContextService(contextService);
		changeRequestService.setFileService(fileService);
		changeRequestService.setLookupService(lookupService);
		changeRequestService.setNotificationService(notificationService);
		changeRequestService.setSynchronizationService(synchronizationService);
		changeRequestService.setContextProvider(contextProvider);
		changeRequestService.setContextOperations(contextOperations);
		changeRequestService.setChangeRequestSummaryService(changeRequestSummaryService);

		when(changeRequestMapper.findChangeRequestById(anyLong())).thenReturn(mockChangeRequest());
		when(changeRequestMapper.updateChangeRequest(any(ChangeRequest.class))).thenReturn(1);
		when(
				changeRequestMapper.updateChangeRequestLastUpdateTime(anyLong(), any(Date.class), anyLong(),
						any(Date.class))).thenReturn(1);
		when(changeRequestMapper.findCourseGrainedChangeRequestById(anyLong())).thenReturn(mockChangeRequest());
		when(changeRequestMapper.findReviewQuestionsForChangeRequest(anyLong())).thenReturn(mockQuestionForReviewers());
		when(changeRequestMapper.findAdvicesForChangeRequest(anyLong())).thenReturn(mockAdvices());
		when(changeRequestMapper.findDeferedChangeRequestByOriginalId(anyLong())).thenReturn(null);
		when(changeRequestMapper.findChangeRequestRealizationById(anyLong()))
				.thenReturn(mockChangeRequestRealization());
		when(changeRequestMapper.findRunningChangeRequestRealization()).thenReturn(mockChangeRequestRealization());
		when(changeRequestMapper.findCurrentRunningRealizationByChangeRequestId(anyLong())).thenReturn(
				mockChangeRequestRealization());

		when(changeRequestMapper.findAllChangeRequests()).thenReturn(mockChangeRequestList());
		when(changeRequestMapper.findChangeRequestsBySearchCriteria(any(UserSearchCriteria.class))).thenReturn(
				mockChangeRequestList());
		when(changeRequestMapper.findOpenChangeRequestsByClassificationAndVersionYear(anyString(), anyString()))
				.thenReturn(mockChangeRequestList());
		when(changeRequestMapper.findOpenTabularChangeRequestsByClassificationAndVersionYear(anyString(), anyString()))
				.thenReturn(mockChangeRequestList());

		when(changeRequestMapper.findNumOfMyChangeRequests(anyLong())).thenReturn(1);

		when(changeRequestMapper.isChangeRequestNameExist(anyString())).thenReturn("false");
		when(changeRequestMapper.isSameChangeRequestNameExist(anyString(), anyLong())).thenReturn("false");
		when(changeRequestMapper.isChangeRequestNameExistInContext(anyString(), anyLong())).thenReturn("false");
		when(changeRequestMapper.publishAllChangeRequestsForBaseContext(anyLong(), anyLong())).thenReturn(1);
		when(changeRequestMapper.searchPatternTopic(anyMap())).thenReturn(null);
		when(changeRequestMapper.searchPatternTopicByContext(anyString(), anyLong(), anyInt())).thenReturn(null);
		when(lookupService.findOpenContextByChangeRquestId(anyLong())).thenReturn(mockContextIdentifier());
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(anyString(), anyString())).thenReturn(
				mockContextIdentifier());
		when(lookupService.findContextIdentificationById(nullable(Long.class))).thenReturn(mockContextIdentifier());
		when(changeRequestSummaryService.hasIncompleteProperties(any(ChangeRequestDTO.class))).thenReturn(false);
		when(contextProvider.findContext(any(ContextIdentifier.class))).thenReturn(context);
		when(contextProvider.createChangeContext(any(ContextIdentifier.class), anyLong())).thenReturn(context);
		when(context.realizeChangeContext(anyBoolean())).thenReturn(new HashMap<ElementVersion, ElementVersion>());

		when(contextOperations.findContextById(anyString(), anyLong())).thenReturn(mockContextIdentifier());

		doNothing().when(changeRequestMapper).insertChangeRequest(any(ChangeRequestDTO.class));
		doNothing().when(changeRequestMapper).insertChangeRequestReviewGroups(any(ChangeRequestDTO.class));
		doNothing().when(changeRequestMapper).insertChangeRequestCommentDiscussion(any(UserComment.class));
		doNothing().when(changeRequestMapper).insertChangeRequestEvolution(any(ChangeRequestEvolution.class));
		doNothing().when(changeRequestMapper).insertChangeRequestRealization(any(ChangeRequestRealization.class));

		doNothing().when(changeRequestMapper).insertAdvice(any(Advice.class));

		doNothing().when(changeRequestHistoryService).createChangeRequestHistoryForUpdating(
				any(ChangeRequestDTO.class), any(User.class));

		doNothing().when(contextService).deleteContext(anyLong());
		doNothing().when(notificationService).removeChangeRequestPickupNotification(anyLong());
		doNothing().when(notificationService).postReadyForAcceptanceNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
		doNothing().when(notificationService).postChangeRequestOwnershipNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
		doNothing().when(notificationService).postPickupRealizationChangeRequestNotifcation(
				any(ChangeRequestDTO.class), any(User.class));
		doNothing().when(notificationService).postPickupTranslationChangeRequestNotifcation(
				any(ChangeRequestDTO.class), any(User.class));
		doNothing().when(notificationService).postPickupValidationChangeRequestNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
		doNothing().when(notificationService).postAdviceRequestNotifcation(any(ChangeRequestDTO.class),
				any(Advice.class), any(User.class));
		doNothing().when(notificationService).postPickupQANotifcation(any(ChangeRequestDTO.class), any(User.class));
		doNothing().when(notificationService).postSendBackChangeRequestNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
		doNothing().when(notificationService).postPickupNewNotifcation(any(ChangeRequestDTO.class), any(User.class));
		doNothing().when(fileService).copyFile(anyString(), anyString());
		doNothing().when(fileService).deleteFile(anyString());
	}

	private Advice mockAdvice() {
		Advice advice1 = new Advice();
		advice1.setAdviceId(1L);
		advice1.setChangeRequestId(1L);
		List<UserComment> userComments = new ArrayList<UserComment>();
		UserComment comment1 = new UserComment();
		comment1.setUserCommentTxt("userCommentTxt");
		userComments.add(comment1);
		advice1.setAdviceComments(userComments);
		return advice1;
	}

	private List<Advice> mockAdvices() {
		List<Advice> advices = new ArrayList<Advice>();
		Advice advice1 = new Advice();
		advice1.setAdviceId(1L);
		advice1.setChangeRequestId(1L);
		advice1.setUserProfileId(23L);
		advice1.setSenderId(12L);
		List<UserComment> userComments = new ArrayList<UserComment>();
		UserComment comment1 = new UserComment();
		comment1.setUserCommentTxt("userCommentTxt");
		userComments.add(comment1);
		advice1.setAdviceComments(userComments);
		Advice advice2 = new Advice();
		advice2.setAdviceId(2L);
		advice2.setChangeRequestId(1L);
		advice2.setUserProfileId(23L);
		advice2.setSenderId(12L);
		advices.add(advice1);
		advices.add(advice2);
		return advices;
	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setLastUpdatedTime(testDate);
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setAdvices(mockAdvices());
		changeRequest.setAdvice(mockAdvice());
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setTransferedTo(0L);
		changeRequest.setQuestionForReviewers(mockQuestionForReviewers());
		changeRequest.setOtherAttachments(mockOtherAttachments());
		changeRequest.setUrcAttachments(mockURLAttachments());
		changeRequest.setUrcLinks(mockUrcLinks());

		return changeRequest;
	}

	private List<ChangeRequest> mockChangeRequestList() {
		List<ChangeRequest> changeRequests = new ArrayList<ChangeRequest>();
		ChangeRequest changeRequest1 = new ChangeRequest();
		changeRequest1.setChangeRequestId(1L);
		changeRequests.add(changeRequest1);
		ChangeRequest changeRequest2 = new ChangeRequest();
		changeRequest2.setChangeRequestId(2L);
		changeRequests.add(changeRequest2);
		return changeRequests;
	}

	private ChangeRequestRealization mockChangeRequestRealization() {
		ChangeRequestRealization changeRequestRealization = new ChangeRequestRealization();
		changeRequestRealization.setChangeRequestId(1L);
		changeRequestRealization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS);
		return changeRequestRealization;

	}

	private ContextAccess mockContextAccess() {
		ContextAccess contextAccess = new HGContextAccess();
		return contextAccess;
	}

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setBaseClassification("ICD-10-CA");
		contextIdentifier.setVersionCode(CIMSTestConstants.TEST_VERSION);
		contextIdentifier.setContextId(1L);
		contextIdentifier.setIsVersionYear(true);
		return contextIdentifier;
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

	private List<DocumentReference> mockOtherAttachments() {
		List<DocumentReference> otherAttachments = new ArrayList<DocumentReference>();
		DocumentReference documentReference = new DocumentReference();
		documentReference.setChangeRequestId(1L);
		documentReference.setFileName("fileName");
		documentReference.setReferenceType(DocumentReferenceType.OTHER_FILE);
		otherAttachments.add(documentReference);
		return otherAttachments;
	}

	private List<QuestionForReviewer> mockQuestionForReviewers() {
		List<QuestionForReviewer> questions = new ArrayList<QuestionForReviewer>();
		QuestionForReviewer question1 = new QuestionForReviewer();
		question1.setQuestionForReviewerId(1L);
		question1.setQuestionForReviewerTxt("questionForReviewerTxt");
		question1.setChangeRequestId(1L);
		List<UserComment> questionComments = new ArrayList<UserComment>();
		UserComment comment1 = new UserComment();
		comment1.setUserCommentTxt("userCommentTxt");
		questionComments.add(comment1);
		question1.setQuestionComments(questionComments);
		questions.add(question1);
		return questions;

	}

	private List<DocumentReference> mockUrcLinks() {
		List<DocumentReference> urlLinks = new ArrayList<DocumentReference>();
		DocumentReference documentReference = new DocumentReference();
		documentReference.setChangeRequestId(1L);
		documentReference.setUrl("url");
		documentReference.setReferenceType(DocumentReferenceType.URC_LINK);
		urlLinks.add(documentReference);
		return urlLinks;
	}

	private List<DocumentReference> mockURLAttachments() {
		List<DocumentReference> otherAttachments = new ArrayList<DocumentReference>();
		DocumentReference documentReference = new DocumentReference();
		documentReference.setChangeRequestId(1L);
		documentReference.setFileName("fileName");
		documentReference.setReferenceType(DocumentReferenceType.URC_FILE);
		otherAttachments.add(documentReference);
		return otherAttachments;
	}

	@Test
	public void testAcceptChangeRequest() {
		when(changeRequestMapper.findChangeRequestById(anyLong())).thenReturn(mockChangeRequest());
		when(changeRequestMapper.updateChangeRequest(any(ChangeRequest.class))).thenReturn(1);
		when(changeRequestMapper.findCourseGrainedChangeRequestById(anyLong())).thenReturn(mockChangeRequest());
		when(changeRequestMapper.findReviewQuestionsForChangeRequest(anyLong())).thenReturn(mockQuestionForReviewers());
		when(changeRequestMapper.findAdvicesForChangeRequest(anyLong())).thenReturn(mockAdvices());
		when(changeRequestMapper.findDeferedChangeRequestByOriginalId(anyLong())).thenReturn(null);
		doNothing().when(changeRequestHistoryService).createChangeRequestHistoryForUpdating(
				any(ChangeRequestDTO.class), any(User.class));
		doNothing().when(notificationService).removeChangeRequestPickupNotification(anyLong());
		doNothing().when(notificationService).postAcceptChangeRequestNotifcation(any(ChangeRequest.class),
				any(User.class));

		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.acceptChangeRequest(changeRequest, currentUser);
		verify(notificationService, times(1)).postAcceptChangeRequestNotifcation(any(ChangeRequest.class),
				any(User.class));
	}

	@Test
	public void testAddCommentForAdvice() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		Long adviceId = 1L;
		User currentUser = mockCurrentUser();
		doNothing().when(changeRequestMapper).insertCommentForAdvice(any(UserComment.class));
		changeRequestService.addCommentForAdvice(changeRequestDTO, adviceId, currentUser);
		verify(changeRequestMapper, times(1)).insertCommentForAdvice(any(UserComment.class));
	}

	@Test
	public void testAddCommentForQuestion() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		Long questionId = 1L;
		User currentUser = mockCurrentUser();
		doNothing().when(changeRequestMapper).insertCommentForReviewerQuestion(any(UserComment.class));
		changeRequestService.addCommentForQuestion(changeRequestDTO, questionId, currentUser);
		verify(changeRequestMapper, times(1)).insertCommentForReviewerQuestion(any(UserComment.class));
	}

	@Test
	public void testApproveChangeRequest() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.approveChangeRequest(changeRequest, currentUser);
		verify(changeRequestMapper, times(1)).updateChangeRequest(any(ChangeRequest.class));
	}

	@Test
	public void testAssignAndTransferChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.assignAndTransferChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).removeChangeRequestPickupNotification(anyLong());
	}

	@Test
	public void testAssignChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.assignChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postChangeRequestPickUpNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
	}

	@Test
	public void testCheckChangeRequestIsLocked() {
		long changeRequestId = 1l;
		long changeRequestlastUpdatedTime = testDate.getTime();
		changeRequestService.checkChangeRequestIsLocked(changeRequestId, changeRequestlastUpdatedTime);
		verify(changeRequestMapper, times(1)).findChangeRequestById(anyLong());
	}

	@Test
	public void testCreateChangeRequest() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.createChangeRequest(changeRequest, currentUser);
		verify(changeRequestMapper, times(1)).insertChangeRequest(any(ChangeRequestDTO.class));

	}

	@Test
	public void testCreateChangeRequestRealization1() {
		ChangeRequest changeRequest = mockChangeRequest();
		changeRequestService.createChangeRequestRealization(changeRequest);
		verify(changeRequestMapper, times(1)).insertChangeRequestRealization(any(ChangeRequestRealization.class));
	}

	@Test
	public void testCreateChangeRequestRealization2() {
		ChangeRequestRealization changeRequestRealization = mockChangeRequestRealization();
		changeRequestService.createChangeRequestRealization(changeRequestRealization);
		verify(changeRequestMapper, times(1)).insertChangeRequestRealization(any(ChangeRequestRealization.class));
	}

	@Test
	public void testDeferChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.deferChangeRequest(changeRequestDTO, currentUser);
		verify(changeRequestMapper, times(1)).insertChangeRequest(any(ChangeRequestDTO.class));
	}

	@Test
	public void testDeleteChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.deleteChangeRequest(changeRequestDTO, currentUser);
		verify(contextService, times(1)).deleteContext(anyLong());
	}

	@Test
	public void testFindAllChangeRequests() {
		List<ChangeRequest> allChangeRequests = changeRequestService.findAllChangeRequests();
		assertTrue(allChangeRequests.size() == 2);
	}

	@Test
	public void testFindChangeRequestRealizationById() {
		ChangeRequestRealization changeRequestRealization = changeRequestService.findChangeRequestRealizationById(0L);
		assertTrue(changeRequestRealization != null);
	}

	@Test
	public void testFindChangeRequestsBySearchCriteria() {
		UserSearchCriteria changeRequestSearchCriteria = new UserSearchCriteria();
		changeRequestSearchCriteria.setAscending(false);
		changeRequestSearchCriteria.setSortBy("ID");
		changeRequestSearchCriteria.setStartRow(1);
		changeRequestSearchCriteria.setEndRow(10);

		List<ChangeRequest> changeRequests = changeRequestService
				.findChangeRequestsBySearchCriteria(changeRequestSearchCriteria);
		assertTrue("should return 2 changerequests", changeRequests.size() == 2);
	}

	@Test
	public void testFindCourseGrainedChangeRequestDTOById() {
		Long changeRequestId = 1L;
		ChangeRequestDTO changeRequestDTO = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		assertTrue(changeRequestDTO != null);
	}

	@Test
	public void testFindCurrentRunningRealizationByChangeRequestId() {
		Long changeRequestId = 1L;
		ChangeRequestRealization changeRequestRealization = changeRequestService
				.findCurrentRunningRealizationByChangeRequestId(changeRequestId);
		assertTrue(changeRequestRealization != null);

	}

	@Test
	public void testFindLightWeightChangeRequestById() {
		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(1L);
		assertTrue("should return the mocked changerequest", changeRequest.getChangeRequestId().longValue() == 1L);
	}

	@Test
	public void testFindNumOfChangeRequests() {
		int num = changeRequestService.findNumOfMyChangeRequests(6L);
		assertTrue("should return 1", num == 1);
	}

	@Test
	public void testFindOpenChangeRequestsByClassificationAndVersionYear() {
		String baseClassification = "CCI";
		Long versionYear = 2017L;
		List<ChangeRequest> changeRequests = changeRequestService.findOpenChangeRequestsByClassificationAndVersionYear(
				baseClassification, versionYear);
		assertTrue(changeRequests.size() == 2);
	}

	@Test
	public void testfindOpenTabularChangeRequestsByClassificationAndVersionYear() {
		String baseClassification = "CCI";
		Long versionYear = 2017L;
		List<ChangeRequest> changeRequests = changeRequestService
				.findOpenTabularChangeRequestsByClassificationAndVersionYear(baseClassification, versionYear);
		assertTrue(changeRequests.size() == 2);
	}

	@Test
	public void testFindRunningRealization() {
		ChangeRequestRealization changeRequestRealization = changeRequestService.findRunningRealization();
		assertTrue(changeRequestRealization != null);
	}

	@Test
	public void testGetAdviceForChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.getAdviceForChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postAdviceRequestNotifcation(any(ChangeRequestDTO.class),
				any(Advice.class), any(User.class));
	}

	@Test
	public void testIsChangeRequestNameExist() {
		String name = "test";
		boolean exist = changeRequestService.isChangeRequestNameExist(name);
		assertTrue(!exist);
	}

	@Test
	public void testIsSameChangeRequestNameExist() {
		String name = "test";
		Long changeRequestId = 1L;
		boolean exist = changeRequestService.isSameChangeRequestNameExist(name, changeRequestId);
		assertTrue(!exist);
	}

	@Test
	public void testIsChangeRequestNameExistInContext() {
		String name = "test";
		Long changeRequestId = 1L;
		boolean exist = changeRequestService.isChangeRequestNameExistInContext(name, changeRequestId);
		assertTrue(!exist);
	}

	@Test
	public void testPublishAllChangeRequestsForYear() {
		String versionCode = CIMSTestConstants.TEST_VERSION;
		User currentUser = mockCurrentUser();
		changeRequestService.publishAllChangeRequestsForYear(versionCode, currentUser);
		verify(lookupService, times(2)).findBaseContextIdentifierByClassificationAndYear(anyString(), anyString());
	}

	@Test
	public void testQaDoneChangeRequest() {
		ChangeRequest changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.qaDoneChangeRequest(changeRequest, currentUser);
		verify(notificationService, times(1)).removeChangeRequestPickupNotification(anyLong());
	}

	@Test
	public void testReadyForAccept() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.readyForAccept(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postReadyForAcceptanceNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
	}

	@Test
	public void testReadyForRealizeChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.readyForRealizeChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postPickupRealizationChangeRequestNotifcation(
				any(ChangeRequestDTO.class), any(User.class));
	}

	@Test
	public void testReadyForTranslationChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.readyForTranslationChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postPickupTranslationChangeRequestNotifcation(
				any(ChangeRequestDTO.class), any(User.class));
	}

	@Test
	public void testReadyForValidationChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.readyForValidationChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postPickupValidationChangeRequestNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
	}

	@Test
	public void testRealizeChangeRequest() {
		ChangeRequest changeRequest = mockChangeRequest();
		ChangeRequestRealization runningRealization = mockChangeRequestRealization();
		User currentUser = mockCurrentUser();
		changeRequestService.realizeChangeRequest(changeRequest, runningRealization, currentUser);
	}

	@Test
	public void testRejectChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.rejectChangeRequest(changeRequestDTO, currentUser);
		verify(contextService, times(1)).deleteContext(anyLong());
	}

	@Test
	public void testSearchPatternTopic() {
		String searchString = "test";
		Integer maxResults = 5;
		List<String> searchedPatterns = changeRequestService.searchPatternTopic(searchString, maxResults);
		assertTrue(searchedPatterns == null);
	}

	@Test
	public void testSearchPatternTopicByContext() {
		String searchString = "test";
		Long searchContext = 1L;
		Integer maxResults = 5;
		List<String> searchedPatterns = changeRequestService.searchPatternTopicByContext(searchString, searchContext,
				maxResults);
		assertTrue(searchedPatterns == null);
	}

	@Test
	public void testSendBackChangeRequest() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.sendBackChangeRequest(changeRequest, currentUser);
		verify(notificationService, times(1)).postSendBackChangeRequestNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
	}

	@Test
	public void testSendQuestionForReviewer() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		int questionIndex = 0;
		User currentUser = mockCurrentUser();
		changeRequestService.sendQuestionForReviewer(changeRequestDTO, questionIndex, currentUser);
		verify(notificationService, times(1)).postReviewRequestNotifcation(any(ChangeRequestDTO.class),
				any(QuestionForReviewer.class), any(User.class));
	}

	@Test
	public void testSubmitChangeRequest() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.submitChangeRequest(changeRequest, currentUser);
		verify(notificationService, times(1)).postPickupNewNotifcation(changeRequest, currentUser);
	}

	@Test
	public void testTakeOverChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.takeOverChangeRequest(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).removeChangeRequestPickupNotification(anyLong());
	}

	@Test
	public void testTransferChangeRequestOwnerShip() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.transferChangeRequestOwnerShip(changeRequestDTO, currentUser);
		verify(notificationService, times(1)).postChangeRequestOwnershipNotifcation(any(ChangeRequestDTO.class),
				any(User.class));
	}

	@Test
	public void testUpdateChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.updateChangeRequest(changeRequestDTO, currentUser);
		verify(changeRequestMapper, times(1)).updateChangeRequest(any(ChangeRequestDTO.class));

	}

	@Test
	public void testUpdateChangeRequestLastUpdateTime() {
		long changeRequestId = 1l;
		User currentUser = mockCurrentUser();
		OptimisticLock changeRequestLock = new OptimisticLock(testDate);
		changeRequestService.updateChangeRequestLastUpdateTime(changeRequestId, currentUser, changeRequestLock);
		verify(changeRequestMapper, times(1)).updateChangeRequestLastUpdateTime(anyLong(), any(Date.class), anyLong(),
				any(Date.class));

	}

	@Test
	public void testUpdateChangeRequestRealization() {
		ChangeRequestRealization changeRequestRealization = mockChangeRequestRealization();
		changeRequestService.updateChangeRequestRealization(changeRequestRealization);
		verify(changeRequestMapper, times(1)).updateChangeRequestRealization(any(ChangeRequestRealization.class));
	}

	@Test
	public void testUpdateLightWeightChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.updateLightWeightChangeRequest(changeRequestDTO, currentUser);
		verify(changeRequestMapper, times(1)).updateChangeRequest(any(ChangeRequestDTO.class));
	}

	@Test
	public void testValidateChangeRequest() {
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		User currentUser = mockCurrentUser();
		changeRequestService.validateChangeRequest(changeRequestDTO, currentUser);
		verify(contextProvider, times(1)).createChangeContext(nullable(ContextIdentifier.class), anyLong());
	}
}
