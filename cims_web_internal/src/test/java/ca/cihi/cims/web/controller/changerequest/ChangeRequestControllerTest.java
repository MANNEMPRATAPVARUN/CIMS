package ca.cihi.cims.web.controller.changerequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.DocumentReferenceType;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.model.meta.AssigneeType;
import ca.cihi.cims.model.meta.ChangeRequestAssignment;
import ca.cihi.cims.model.resourceaccess.AccessCode;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceCode;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ResourceAccessService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.validator.ChangeRequestValidator;

/**
 * @author szhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestControllerTest {

	ChangeRequestController changeRequestController;
	@Mock
	protected Model model;

	@Mock
	protected HttpServletRequest request;

	@Mock
	protected HttpSession session;

	@Mock
	HttpServletResponse response;

	@Mock
	protected BindingResult result;
	@Mock
	WebDataBinder binder;

	@Mock
	private ChangeRequestService changeRequestService;
	@Mock
	ChangeRequestValidator changeRequestValidator;

	@Autowired
	private ResourceAccessService resourcAccessService;

	@Mock
	private LookupService lookupService;
	@Mock
	ViewService viewService;
	@Autowired
	AdminService adminService;

	static Date testDate = Calendar.getInstance().getTime();

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		changeRequestController = new ChangeRequestController();
		changeRequestController.setChangeRequestService(changeRequestService);
		changeRequestController.setLookupService(lookupService);
		changeRequestController.setResourcAccessService(resourcAccessService);
		changeRequestController.setChangeRequestValidator(changeRequestValidator);
		changeRequestController.setAdminService(adminService);
		changeRequestController.setViewService(viewService);

		when(changeRequestService.findCourseGrainedChangeRequestDTOById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		
		when(lookupService.findOpenVersionYears(nullable(String.class))).thenReturn(mockOpenVersionYears());
		when(lookupService.findOpenContextByChangeRquestId(nullable(Long.class))).thenReturn(mockContextIdentifier());
		when(lookupService.findContextIdentificationById(nullable(Long.class))).thenReturn(mockContextIdentifier());
		when(lookupService.findBaseClassifications()).thenReturn(mockbaseClassifications());
		when(lookupService.findNonClosedBaseContextIdentifiers(nullable(String.class))).thenReturn(mockContextIdentifiers());
		when(lookupService.loadAllChangeRequestAssignmentMetaDataToMap()).thenReturn(
				mockChangeRequestAssignmentMetaMap());
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		/*
		 * when( resourcAccessService.findCurrentUserResourceAccesses(mockUser().getRoles(), mockChangeRequest()
		 * .getStatus(), assignmentTypeCode, ChangeRequestLanguage.fromString(mockChangeRequest()
		 * .getLanguageCode()))).thenReturn(mockResourceAccesses());
		 */

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
		List<UserComment> userComments = new ArrayList<UserComment>();
		UserComment comment1 = new UserComment();
		comment1.setUserCommentTxt("userCommentTxt");
		userComments.add(comment1);
		advice1.setAdviceComments(userComments);
		Advice advice2 = new Advice();
		advice2.setAdviceId(2L);
		advice2.setChangeRequestId(1L);
		advices.add(advice1);
		advices.add(advice2);
		return advices;
	}

	private List<String> mockbaseClassifications() {
		List<String> classifications = new ArrayList<String>();
		classifications.add("CCI");
		classifications.add("ICD-10-CA");
		return classifications;
	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setLastUpdatedTime(testDate);
		changeRequest.setBaseClassification("CCI");
		changeRequest.setBaseVersionCode("2018");
		changeRequest.setBaseContextId(1L);
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setAdvices(mockAdvices());
		changeRequest.setAdvice(mockAdvice());
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setOwner(mockUser());
		changeRequest.setTransferedTo(0L);
		changeRequest.setLanguageCode("ENG");
		changeRequest.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		changeRequest.setQuestionForReviewers(mockQuestionForReviewers());
		changeRequest.setOtherAttachments(mockOtherAttachments());
		changeRequest.setUrcAttachments(mockURLAttachments());
		changeRequest.setUrcLinks(mockUrcLinks());
		changeRequest.setReviewGroups(mockDistributions());

		return changeRequest;
	}

	private ChangeRequestAssignment mockChangeRequestAssignment() {
		ChangeRequestAssignment changeRequestAssignment = new ChangeRequestAssignment();
		changeRequestAssignment.setAssigneeType(AssigneeType.BOTH);
		changeRequestAssignment.setAssignToDL(Distribution.DL_ID_ADMINISTRATOR);
		changeRequestAssignment.setLanguage(ChangeRequestLanguage.ENG);
		changeRequestAssignment.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		return changeRequestAssignment;
	}

	private Map<String, List<ChangeRequestAssignment>> mockChangeRequestAssignmentMetaMap() {
		Map<String, List<ChangeRequestAssignment>> changeRequestAssignmentMetaMap = new HashMap<String, List<ChangeRequestAssignment>>();
		String key = "3ENG";
		changeRequestAssignmentMetaMap.put(key, mockChangeRequestAssignments());
		return changeRequestAssignmentMetaMap;
	}

	private List<ChangeRequestAssignment> mockChangeRequestAssignments() {
		List<ChangeRequestAssignment> changeRequestAssignments = new ArrayList<ChangeRequestAssignment>();
		changeRequestAssignments.add(mockChangeRequestAssignment());
		return changeRequestAssignments;

	}
	
	private List<String> mockOpenVersionYears(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("2018");
		return list;
	}

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2018", "CCI", 1l, "ACTIVE", new Date(),
				Boolean.TRUE, 1l, null);
		return contextIdentifier;
	}

	private List<ContextIdentifier> mockContextIdentifiers() {
		List<ContextIdentifier> contextIdentifiers = new ArrayList<ContextIdentifier>();
		contextIdentifiers.add(mockContextIdentifier());
		ContextIdentifier contextIdentifier2 = new ContextIdentifier(2l, "2019", "CCI", 1l, "ACTIVE", new Date(),
				Boolean.FALSE, 1l, null);
		contextIdentifiers.add(contextIdentifier2);
		return contextIdentifiers;
	}

	private Distribution mockDistribution() {
		Distribution distribution = new Distribution();
		distribution.setDistributionlistid(Distribution.DL_ID_ADMINISTRATOR);
		return distribution;
	}

	private List<Distribution> mockDistributions() {
		List<Distribution> reviewGroups = new ArrayList<Distribution>();
		reviewGroups.add(mockDistribution());
		return reviewGroups;

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

	private List<ResourceAccess> mockResourceAccesses() {
		List<ResourceAccess> resourceAccesses = new ArrayList<ResourceAccess>();
		ResourceAccess resourceAccess1 = new ResourceAccess();
		resourceAccess1.setAccessCode(AccessCode.EXECUTE);
		resourceAccess1.setResourceCode(ResourceCode.BUTTON_DELETE);
		resourceAccesses.add(resourceAccess1);
		ResourceAccess resourceAccess2 = new ResourceAccess();
		resourceAccess2.setAccessCode(AccessCode.EXECUTE);
		resourceAccess2.setResourceCode(ResourceCode.BUTTON_ASSIGN);
		resourceAccesses.add(resourceAccess2);
		ResourceAccess resourceAccess3 = new ResourceAccess();
		resourceAccess3.setAccessCode(AccessCode.EXECUTE);
		resourceAccess3.setResourceCode(ResourceCode.BUTTON_OWNER_TRANSFER);
		resourceAccesses.add(resourceAccess3);
		ResourceAccess resourceAccess4 = new ResourceAccess();
		resourceAccess4.setAccessCode(AccessCode.EXECUTE);
		resourceAccess4.setResourceCode(ResourceCode.BUTTON_GET_ADVICE);
		resourceAccesses.add(resourceAccess4);

		return resourceAccesses;
	}

	private Set<SecurityRole> mockSecurityRoles() {
		Set<SecurityRole> securityRoles = new HashSet<SecurityRole>();
		securityRoles.add(SecurityRole.ROLE_ADMINISTRATOR);
		securityRoles.add(SecurityRole.ROLE_ENG_CONTENT_DEVELOPER);
		return securityRoles;
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

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		currentUser.setRoles(mockSecurityRoles());
		currentUser.setResourceAccesses(mockResourceAccesses());
		return currentUser;
	}

	@Test
	public void testAddCommentForAdvice() {
		HttpSession test_session = session;
		Long adviceId = 1L;
		String lastActiveSectionDiv = "advice";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String viewModal = changeRequestController.addCommentForAdvice(test_session, adviceId, lastActiveSectionDiv,
				changeRequestDTO, test_result, test_model, test_request);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testAddCommentForChangeRequest() {
		HttpSession test_session = session;

		String lastActiveSectionDiv = "advice";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String viewModal = changeRequestController.addCommentForChangeRequest(test_session, lastActiveSectionDiv,
				changeRequestDTO, test_result, test_model, test_request);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testAddCommentForQuestion() {
		HttpSession test_session = session;
		Long questionId = 1L;
		String lastActiveSectionDiv = "advice";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String viewModal = changeRequestController.addCommentForQuestion(test_session, questionId,
				lastActiveSectionDiv, changeRequestDTO, test_result, test_model, test_request);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testAssignAndTransferChangeRequest() {
		HttpSession test_session = session;

		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String viewModal = changeRequestController.assignAndTransferChangeRequest(test_session, changeRequestDTO,
				test_result, test_model, test_request);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		verify(changeRequestService, times(1)).assignAndTransferChangeRequest(nullable(ChangeRequestDTO.class),
				nullable(User.class));
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testAssignChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String viewModal = changeRequestController.assignChangeRequest(test_session, changeRequestDTO, test_result,
				test_model, test_request);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		verify(changeRequestService, times(1)).assignChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testCreateChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		String viewModal = changeRequestController.createChangeRequest(test_session, changeRequestDTO, test_result,
				test_model);
		String expectedMav = ChangeRequestController.MANAGE_CHANGEREQUEST;
		verify(changeRequestService, times(1)).createChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testDeferChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String returnView = changeRequestController.deferChangeRequest(test_session, changeRequestDTO, test_result,
				test_model, test_request);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).deferChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testDeleteChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		String returnView = changeRequestController.deleteChangeRequest(test_session, changeRequestDTO, test_result,
				test_model);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).deleteChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testGetAdviceForChangeRequest() {
		HttpSession test_session = session;
		String adviceMsg = "a advice";
		String lastActiveSectionDiv = "advice";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String returnView = changeRequestController.getAdviceForChangeRequest(test_session, lastActiveSectionDiv,
				adviceMsg, changeRequestDTO, test_result, test_model, test_request);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).getAdviceForChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testGetDeferVersionForClassification() {
		final String baseClassification = "CCI";
		Long baseContextId = 1L;
		List<ContextIdentifier> deferableVersions = changeRequestController.getDeferVersionForClassification(
				baseClassification, baseContextId);
		assertTrue(deferableVersions.size() == 1);
	}

	@Test
	public void testGetVersionForClassification() {
		final String baseClassification = "CCI";
		List<ContextIdentifier> versions = changeRequestController.getVersionForClassification(baseClassification);
		assertTrue(versions.size() == 2);
	}

	@Test
	public void testInitBinder() {
		final WebDataBinder test_binder = binder;
		changeRequestController.initBinder(test_binder);
	}

	@Test
	public void testInitCreateChangeRequest() {
		Model test_model = model;
		String returnView = changeRequestController.initCreateChangeRequest(test_model);
		String expectedView = ChangeRequestController.CREATE_CHANGEREQUEST_VIEW;
		assertEquals("Should get same view", returnView, expectedView);
	}

	@Ignore
	@Test
	public void testManageClassification() {
		Model test_model = model;
		Long changeRequestId = 1L;
		String language = "ENG";
		HttpSession test_session = session;
		HttpServletRequest test_request = request;
		String returnView = changeRequestController.manageClassification(test_model, changeRequestId, language,
				test_session, test_request);
		String expectedView = ChangeRequestController.MANAGE_CLASSIFICATION;
		assertEquals("Should get same view", returnView, expectedView);
	}

	@Test
	public void testPrintChangeRequest() {
		String viewModal = changeRequestController.printChangeRequest(model, 1l, session);
		String expectedMav = ChangeRequestController.PRINT_CHANGEREQUEST;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testReadyForAccept() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String returnView = changeRequestController.readyForAccept(test_session, changeRequestDTO, test_result,
				test_model, test_request);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).readyForAccept(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testRejectChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		String returnView = changeRequestController.rejectChangeRequest(test_session, changeRequestDTO, test_result,
				test_model, test_request);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).rejectChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testSaveChangeRequest() {
		HttpSession test_session = session;
		String lastActiveSectionDiv = "baseInfo";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		String returnView = changeRequestController.saveChangeRequest(test_session, lastActiveSectionDiv,
				changeRequestDTO, test_result, test_model);
		String expectedView = ChangeRequestController.MANAGE_CHANGEREQUEST;
		assertEquals("Should get same view", returnView, expectedView);
		verify(changeRequestService, times(1)).updateChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testSearchPatternTopic() {
		HttpServletRequest test_request = request;
		HttpServletResponse test_response = response;
		when(request.getParameter(ChangeRequestController.JQUERY_UI_AUTOCOMPLETE_PARAMETER)).thenReturn("A00");
		when(request.getParameter("baseContextId")).thenReturn("1");
		changeRequestController.searchPatternTopic(test_request, test_response);
		verify(changeRequestService, times(1)).searchPatternTopicByContext(nullable(String.class), nullable(Long.class), nullable(Integer.class));
	}

	@Test
	public void testSendForReviewChangeRequest() {
		HttpSession test_session = session;
		int questionIndex = 1;
		String lastActiveSectionDiv = "baseInfo";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestController.sendForReviewChangeRequest(test_session, questionIndex, lastActiveSectionDiv,
				changeRequestDTO, test_result, test_model, test_request);
		verify(changeRequestService, times(1)).sendQuestionForReviewer(nullable(ChangeRequestDTO.class), nullable(Integer.class),
				nullable(User.class));
	}

	@Test
	public void testSubmitChangeRequest() {
		HttpSession test_session = session;
		String lastActiveSectionDiv = "baseInfo";
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestController.submitChangeRequest(test_session, lastActiveSectionDiv, changeRequestDTO, test_result,
				test_model, test_request);
		verify(changeRequestService, times(1)).submitChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testTakeOverChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestController.takeOverChangeRequest(test_session, changeRequestDTO, test_result, test_model,
				test_request);
		verify(changeRequestService, times(1)).takeOverChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

	@Test
	public void testTransferChangeRequestOwnership() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestController.transferChangeRequestOwnership(test_session, changeRequestDTO, test_result, test_model,
				test_request);
		verify(changeRequestService, times(1)).transferChangeRequestOwnerShip(nullable(ChangeRequestDTO.class),
				nullable(User.class));
	}

	@Test
	public void testValidateChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequest();
		BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestController.validateChangeRequest(test_session, changeRequestDTO, test_result, test_model,
				test_request);
		verify(changeRequestService, times(1)).validateChangeRequest(nullable(ChangeRequestDTO.class), nullable(User.class));
	}

}
