package ca.cihi.cims.validator;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.DocumentReferenceType;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestValidatorTest {
	ChangeRequestValidator changeRequestValidator;
	@Mock
	ChangeRequestService changeRequestService;
	@Mock
	LookupService lookupService;
	@Mock
	AdminService adminService;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpSession session;
	@Mock
	HttpServletResponse response;
	@Mock
	protected BindingResult result;
	static Date today = Calendar.getInstance().getTime();

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		changeRequestValidator = new ChangeRequestValidator();
		changeRequestValidator.setChangeRequestService(changeRequestService);
		changeRequestValidator.setLookupService(lookupService);
		changeRequestValidator.setAdminService(adminService);
		when(lookupService.findContextIdentificationById(nullable(Long.class))).thenReturn(mockContextIdentifierCCI());
		when(changeRequestService.findCourseGrainedChangeRequestDTOById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(adminService.getAuxTableValueByID(nullable(Long.class))).thenReturn(mockAuxTableValue());
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

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setName("Test");
		changeRequest.setLastUpdatedTime(today);
		changeRequest.setBaseClassification("CCI");
		changeRequest.setBaseVersionCode("2018");
		changeRequest.setBaseContextId(1L);
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setAdvices(mockAdvices());
		changeRequest.setAdvice(mockAdvice());
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setRequestorId(1L);
		changeRequest.setTransferedTo(0L);
		changeRequest.setLanguageCode("ENG");
		changeRequest.setRationaleForClosedDeferred("rationaleForClosedDeferred");
		changeRequest.setRationaleForIncomplete("rationaleForIncomplete");
		changeRequest.setRationaleForValid("rationaleForValid");

		changeRequest.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		changeRequest.setQuestionForReviewers(mockQuestionForReviewers());
		changeRequest.setOtherAttachments(mockOtherAttachments());
		changeRequest.setUrcAttachments(mockURLAttachments());
		changeRequest.setUrcLinks(mockUrcLinks());
		changeRequest.setReviewGroups(mockDistributions());
		changeRequest.setChangeNatureId(4L);

		return changeRequest;
	}

	private AuxTableValue mockAuxTableValue() {
		AuxTableValue auxValue = new AuxTableValue();
		auxValue.setAuxValueCode("M");
		auxValue.setAuxEngLable("Major Tabular");
		return auxValue;
	}

	private ContextIdentifier mockContextIdentifierCCI() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2018", "CCI", 1l, "ACTIVE", new Date(),
				Boolean.TRUE, 1l, null);
		return contextIdentifier;
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
		question1.setReviewerId(Distribution.DL_ID_Classification);
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
	public void testSupports() {
		boolean support = changeRequestValidator.supports(ChangeRequestDTO.class);
		assertTrue(support);
	}

	@Test
	public void testValidate() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validate(changeRequest, test_result);
		verify(changeRequestService, times(1)).isSameChangeRequestNameExist(nullable(String.class), nullable(Long.class));
	}

	@Test
	public void testValidateChangeRequestNameExistInContext() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validate(changeRequest, test_result);
		verify(changeRequestService, times(0)).isChangeRequestNameExistInContext(nullable(String.class), nullable(Long.class));
	}

	@Test
	public void testValidateAddCommentForAdviceButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		Long adviceId = 1L;
		BindingResult test_result = result;
		changeRequestValidator.validateAddCommentForAdviceButton(changeRequest, adviceId, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateAddCommentForQuestionButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		Long questionId = 1L;
		BindingResult test_result = result;
		changeRequestValidator.validateAddCommentForQuestionButton(changeRequest, questionId, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateDeferButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateDeferButton(changeRequest, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateGetAdviceButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateGetAdviceButton(changeRequest, test_result);
		verify(test_result, times(1)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateRejectButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateRejectButton(changeRequest, test_result);
		verify(test_result, times(1)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateSendBackButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateSendBackButton(changeRequest, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateSendForReviewButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		int questionIndex = 0;
		BindingResult test_result = result;
		changeRequestValidator.validateSendForReviewButton(changeRequest, questionIndex, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateTakeOverButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateTakeOverButton(changeRequest, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}

	@Test
	public void testValidateValidButton() {
		ChangeRequestDTO changeRequest = mockChangeRequest();
		BindingResult test_result = result;
		changeRequestValidator.validateValidButton(changeRequest, test_result);
		verify(test_result, times(0)).rejectValue(nullable(String.class), nullable(String.class), nullable(String.class));
	}
}
