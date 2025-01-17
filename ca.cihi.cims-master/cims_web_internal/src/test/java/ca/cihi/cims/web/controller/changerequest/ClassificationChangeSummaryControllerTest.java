package ca.cihi.cims.web.controller.changerequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.ChangeRequestRealizationStep;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ConflictProposedChange;
import ca.cihi.cims.model.changerequest.ConflictProposedIndexChange;
import ca.cihi.cims.model.changerequest.ConflictProposedSupplementChange;
import ca.cihi.cims.model.changerequest.IncompleteProperty;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ChangeRequestSummaryService;
import ca.cihi.cims.service.IncompleteReportService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.RealizationService;
import ca.cihi.cims.service.ResourceAccessService;
import ca.cihi.cims.validator.ChangeRequestValidator;
import ca.cihi.cims.web.filter.CurrentContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ClassificationChangeSummaryControllerTest {

	ClassificationChangeSummaryController classificationChangeSummaryController;
	@Mock
	protected Model model;

	@Mock
	protected HttpServletRequest request;

	@Mock
	protected HttpSession session;

	@Mock
	HttpServletResponse response;
	@Mock
	PrintWriter out;
	@Mock
	BindingResult result;
	@Mock
	WebDataBinder binder;

	@Mock
	private ChangeRequestService changeRequestService;
	@Mock
	IncompleteReportService incompleteReportService;

	@Mock
	private ResourceAccessService resourcAccessService;

	@Mock
	private LookupService lookupService;

	@Mock
	private ChangeRequestSummaryService changeRequestSummaryService;
	@Mock
	ChangeRequestValidator changeRequestValidator;

	@Mock
	ContextProvider provider;
	@Mock
	ElementOperations elementOperations;

	@Mock
	CurrentContext context;
	@Mock
	RealizationService realizationService;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		classificationChangeSummaryController = new ClassificationChangeSummaryController();
		classificationChangeSummaryController.setChangeRequestService(changeRequestService);
		classificationChangeSummaryController.setLookupService(lookupService);
		classificationChangeSummaryController.setResourcAccessService(resourcAccessService);
		classificationChangeSummaryController.setChangeRequestSummaryService(changeRequestSummaryService);
		classificationChangeSummaryController.setProvider(provider);
		classificationChangeSummaryController.setElementOperations(elementOperations);
		classificationChangeSummaryController.setContext(context);
		classificationChangeSummaryController.setRealizationService(realizationService);
		classificationChangeSummaryController.setIncompleteReportService(incompleteReportService);
		classificationChangeSummaryController.setChangeRequestValidator(changeRequestValidator);

		when(changeRequestService.findCourseGrainedChangeRequestDTOById(1l)).thenReturn(mockChangeRequestDTO());
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequestDTO());
		when(changeRequestSummaryService.findModifiedIndexConceptElementCode(nullable(Long.class), nullable(Long.class), nullable(Long.class)))
				.thenReturn(mockConceptModification());

		when(changeRequestSummaryService.findModifiedIndexConceptElementCodes(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockConceptModifications());
		when(changeRequestSummaryService.findMaxStructureId(1l)).thenReturn(1l);
		when(changeRequestSummaryService.findModifiedConceptElementCodes(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockConceptModifications());
		when(changeRequestSummaryService.findModifiedSupplementConceptElementCodes(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockConceptModifications());

		when(lookupService.findOpenContextByChangeRquestId(1l)).thenReturn(mockContextIdentifier());
		when(lookupService.findNonClosedBaseContextIdentifiers(nullable(String.class))).thenReturn(mockContextIdentifiers());

		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		when(
				resourcAccessService.findCurrentUserResourceAccesses(mockUser().getRoles(), mockChangeRequestDTO()
						.getStatus(), assignmentTypeCode, ChangeRequestLanguage.fromString(mockChangeRequestDTO()
						.getLanguageCode()))).thenReturn(mockResourceAccess());

		when(incompleteReportService.checkSupplementConcept(nullable(Long.class), nullable(Long.class), nullable(String.class))).thenReturn(
				mockIncompletePropertys());
		when(incompleteReportService.checkTabularConcept(nullable(Long.class), nullable(Long.class), nullable(Boolean.class), nullable(String.class))).thenReturn(
				mockIncompletePropertys());

		when(elementOperations.getIndexPath(nullable(Long.class), nullable(Long.class))).thenReturn("ABC>");

	}

	private ChangeRequestDTO mockChangeRequestDTO() {
		ChangeRequestDTO dto = new ChangeRequestDTO();
		dto.setChangeRequestId(1l);
		dto.setBaseClassification("CCI");
		dto.setBaseVersionCode("2018");
		dto.setAssigneeUserId(1L);
		dto.setOwnerId(1L);
		dto.setCategory(ChangeRequestCategory.T);
		return dto;
	}

	private ChangeRequestRealization mockChangeRequestRealization() {
		ChangeRequestRealization changeRequestRealization = new ChangeRequestRealization();
		changeRequestRealization.setProcessStep(ChangeRequestRealizationStep.STEP_1_SYNC_VIEW);
		return changeRequestRealization;
	}

	private ConceptModification mockConceptModification() {
		ConceptModification conceptModification = new ConceptModification();
		conceptModification.setBreadCrumbs("breadCrumbs");
		conceptModification.setChangeRequestId(1L);
		conceptModification.setCode("code");
		conceptModification.setValidationId(1L);
		conceptModification.setElementId(1L);
		conceptModification.setStructureId(1L);
		conceptModification.setConceptClassName("conceptClassName");
		conceptModification.setProposedAndConflictIndexChanges(mockProposedChanges());
		conceptModification.setProposedAndConflictTabularChanges(mockProposedChanges());
		conceptModification.setProposedAndConflictSupplementChanges(mockProposedChanges());
		conceptModification.setProposedAndConflictValidationChanges(mockProposedChanges());
		conceptModification.setProposedIndexRefChange(mockProposedChange());

		return conceptModification;
	}

	private List<ConceptModification> mockConceptModifications() {
		List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		conceptModifications.add(mockConceptModification());
		return conceptModifications;
	}

	private ConflictProposedChange mockConflictProposedChange() {
		ConflictProposedChange conflictProposedChange = new ConflictProposedChange();
		conflictProposedChange.setElementId(1L);
		conflictProposedChange.setChangeType("Validation");
		conflictProposedChange.setCode("A00");
		conflictProposedChange.setConflictValue("conflictValue");
		conflictProposedChange.setConflictRealizedByContext(mockContextIdentifier());
		conflictProposedChange.setResolveActionCode(ConflictProposedChange.ActionCode_Keep);
		return conflictProposedChange;
	}

	private List<ConflictProposedChange> mockConflictProposedChanges() {
		List<ConflictProposedChange> conflictProposedChanges = new ArrayList<ConflictProposedChange>();
		conflictProposedChanges.add(mockConflictProposedChange());
		return conflictProposedChanges;
	}

	private ConflictProposedIndexChange mockConflictProposedIndexChange() {
		ConflictProposedIndexChange conflictProposedIndexChange = new ConflictProposedIndexChange();
		conflictProposedIndexChange.setElementId(2L);
		conflictProposedIndexChange.setConflictRealizedByContext(mockContextIdentifier());
		conflictProposedIndexChange.setResolveActionCode(ConflictProposedIndexChange.ActionCode_Keep);
		return conflictProposedIndexChange;
	}

	private List<ConflictProposedIndexChange> mockConflictProposedIndexChanges() {
		List<ConflictProposedIndexChange> conflictProposedIndexChanges = new ArrayList<ConflictProposedIndexChange>();
		conflictProposedIndexChanges.add(mockConflictProposedIndexChange());
		return conflictProposedIndexChanges;
	}

	private ConflictProposedSupplementChange mockConflictProposedSupplementChange() {
		ConflictProposedSupplementChange conflictProposedSupplementChange = new ConflictProposedSupplementChange();
		conflictProposedSupplementChange.setElementId(1L);
		conflictProposedSupplementChange.setConflictRealizedByContext(mockContextIdentifier());
		conflictProposedSupplementChange.setResolveActionCode(ConflictProposedSupplementChange.ACTIONCODE_KEEP);
		return conflictProposedSupplementChange;
	}

	private List<ConflictProposedSupplementChange> mockConflictProposedSupplementChanges() {
		List<ConflictProposedSupplementChange> conflictProposedSupplementChange = new ArrayList<ConflictProposedSupplementChange>();
		conflictProposedSupplementChange.add(mockConflictProposedSupplementChange());
		return conflictProposedSupplementChange;
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

	private IncompleteProperty mockIncompleteProperty() {
		IncompleteProperty incompleteProperty = new IncompleteProperty();
		incompleteProperty.setBreadCrumbs("ABC>ABCD");
		incompleteProperty.setCodeValue("codeValue");
		incompleteProperty.setIncompleteRatoinale("incompleteRatoinale");
		return incompleteProperty;
	}

	private List<IncompleteProperty> mockIncompletePropertys() {
		List<IncompleteProperty> incompletePropertys = new ArrayList<IncompleteProperty>();
		incompletePropertys.add(mockIncompleteProperty());
		return incompletePropertys;
	}

	private ProposedChange mockProposedChange() {
		ProposedChange proposedChange = new ProposedChange();
		proposedChange.setConflictRealizedByContext(mockContextIdentifier());
		proposedChange.setElementVersionId(1L);
		proposedChange.setConflictValue("conflictValue");
		proposedChange.setFieldName("A00");
		proposedChange.setOldValue("oldValue");
		proposedChange.setProposedValue("proposedValue");
		proposedChange.setTableName("A");
		proposedChange.setValidationId(1L);
		return proposedChange;
	}

	private List<ProposedChange> mockProposedChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		proposedChanges.add(mockProposedChange());
		return proposedChanges;

	}

	private ResolveConflict mockResolveConflict() {
		ResolveConflict resolveConflict = new ResolveConflict();
		resolveConflict.setChangeRequestId(1L);
		resolveConflict.setCurrentContextId(2L);

		List<ConflictProposedChange> conflictChanges = mockConflictProposedChanges();
		List<ConflictProposedIndexChange> conflictIndexChanges = mockConflictProposedIndexChanges();
		List<ConflictProposedSupplementChange> conflictProposedSupplementChanges = mockConflictProposedSupplementChanges();

		resolveConflict.setConflictChanges(conflictChanges);
		resolveConflict.setConflictIndexChanges(conflictIndexChanges);
		resolveConflict.setConflictSupplementChanges(conflictProposedSupplementChanges);
		return resolveConflict;
	}

	private List<ResourceAccess> mockResourceAccess() {
		List<ResourceAccess> resourceAccess = new ArrayList<ResourceAccess>();
		return resourceAccess;
	}

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	@Test
	public void testAcceptChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.acceptChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testApproveChangeRequestIndex() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		changeRequestDTO.setCategory(ChangeRequestCategory.I);
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.approveChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.INDEX_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testApproveChangeRequestSupplement() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		changeRequestDTO.setCategory(ChangeRequestCategory.S);
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.approveChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.SUPPLEMENT_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testClassificationIndexSummary() {
		final Model test_model = model;
		final long changeRequestId = 1l;
		final HttpSession test_session = session;
		String viewModal = classificationChangeSummaryController.classificationIndexSummary(test_model,
				changeRequestId, test_session);
		String expectedMav = ClassificationChangeSummaryController.INDEX_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testGetRealizationProcessState() throws IOException {
		long changeRequestId = 1l;
		HttpServletResponse test_response = response;
		when(changeRequestService.findCurrentRunningRealizationByChangeRequestId(nullable(Long.class))).thenReturn(
				mockChangeRequestRealization());
		when(response.getWriter()).thenReturn(out);
		classificationChangeSummaryController.getRealizationProcessState(changeRequestId, test_response);
		verify(changeRequestService, times(1)).findCurrentRunningRealizationByChangeRequestId(nullable(Long.class));

	}

	@Test
	public void testInitBinder() {
		classificationChangeSummaryController.initBinder(binder);
	}

	@Test
	public void testPrintClassificationSummary() {
		String viewModal = classificationChangeSummaryController.printClassificationSummary(model, 1l, session);
		String expectedMav = ClassificationChangeSummaryController.PRINT_TABULAR_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testPrintIndexChangeSummary() {
		HttpSession test_session = session;
		Model test_model = model;
		long changeRequestId = 1l;
		String viewModal = classificationChangeSummaryController.printIndexChangeSummary(test_model, changeRequestId,
				test_session);
		String expectedMav = ClassificationChangeSummaryController.PRINT_INDEX_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testPrintSupplementChangeSummary() {
		HttpSession test_session = session;
		Model test_model = model;
		long changeRequestId = 1l;
		String viewModal = classificationChangeSummaryController.printSupplementChangeSummary(test_model,
				changeRequestId, test_session);
		String expectedMav = ClassificationChangeSummaryController.PRINT_SUPPLEMENT_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testQaDoneChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.qaDoneChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testReadyForRealizeChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.readyForRealizeChangeRequest(test_session,
				changeRequestDTO, test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testReadyForTranslationChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.readyForTranslationChangeRequest(test_session,
				changeRequestDTO, test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testReadyForValidationChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.readyForValidationChangeRequest(test_session,
				changeRequestDTO, test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testRealizeChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.realizeChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testResolveConflicts() {
		HttpSession test_session = session;
		Model test_model = model;
		ResolveConflict resolveConflict = mockResolveConflict();
		when(changeRequestService.findLightWeightChangeRequestById(1l)).thenReturn(mockChangeRequestDTO());
		String viewModal = classificationChangeSummaryController.resolveConflicts(test_model, resolveConflict,
				test_session);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CONFLICTS_RESOLVE_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testResolveIndexConflicts() {
		HttpSession test_session = session;
		Model test_model = model;
		ResolveConflict resolveConflict = mockResolveConflict();
		when(changeRequestService.findLightWeightChangeRequestById(1l)).thenReturn(mockChangeRequestDTO());
		String viewModal = classificationChangeSummaryController.resolveIndexConflicts(test_model, resolveConflict,
				test_session);
		String expectedMav = ClassificationChangeSummaryController.INDEX_CONFLICTS_RESOLVE_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testResolveSupplementConflicts() {
		HttpSession test_session = session;
		Model test_model = model;
		ResolveConflict resolveConflict = mockResolveConflict();
		when(changeRequestService.findLightWeightChangeRequestById(1l)).thenReturn(mockChangeRequestDTO());
		String viewModal = classificationChangeSummaryController.resolveSupplementConflicts(test_model,
				resolveConflict, test_session);
		String expectedMav = ClassificationChangeSummaryController.SUPPLEMENT_CONFLICTS_RESOLVE_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testSendBackChangeRequest() {
		HttpSession test_session = session;
		ChangeRequestDTO changeRequestDTO = mockChangeRequestDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		String viewModal = classificationChangeSummaryController.sendBackChangeRequest(test_session, changeRequestDTO,
				test_result, test_model);
		String expectedMav = ClassificationChangeSummaryController.TABULAR_CHANGE_SUMMARY;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testShowIndexReferenceProposedAndConflict() {
		Model test_model = model;
		long changeRequestId = 1l;
		long elementId = 1l;
		HttpSession test_session = session;
		String viewModal = classificationChangeSummaryController.showIndexReferenceProposedAndConflict(test_model,
				changeRequestId, elementId, test_session);
		String expectedMav = ClassificationChangeSummaryController.INDEX_REFERENCE_CONFLICT_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);
		verify(changeRequestSummaryService, times(1)).findModifiedIndexConceptElementCode(nullable(Long.class), nullable(Long.class),
				nullable(Long.class));

	}

	@Test
	public void testShowValidationProposedAndConflict() {
		Model test_model = model;
		long changeRequestId = 1l;
		long validationId = 1l;
		long elementVersionId = 1l;
		HttpSession test_session = session;
		when(changeRequestService.findLightWeightChangeRequestById(1l)).thenReturn(mockChangeRequestDTO());
		when(changeRequestSummaryService.findModifiedConceptElementCode(nullable(Long.class), nullable(Long.class), nullable(Long.class))).thenReturn(
				mockConceptModification());
		String viewModal = classificationChangeSummaryController.showValidationProposedAndConflict(test_model,
				changeRequestId, validationId, elementVersionId, test_session);
		String expectedMav = ClassificationChangeSummaryController.VALIDATION_CONFLICT_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testViewHtmlProperty() {
		Model test_model = model;
		long htmlPropertyId = 1l;
		String fieldName = "fieldName";
		String code = "A00";
		String category = "T";
		String viewModal = classificationChangeSummaryController.viewHtmlProperty(test_model, htmlPropertyId,
				fieldName, code, category);
		String expectedMav = ClassificationChangeSummaryController.XML_PROPERTY_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testViewIndexIncompleteReport() {
		Model test_model = model;
		long changeRequestId = 1l;
		HttpSession test_session = session;
		String viewModal = classificationChangeSummaryController.viewIndexIncompleteReport(test_model, changeRequestId,
				test_session);
		String expectedMav = ClassificationChangeSummaryController.INCOMPLETE_INDEX_REPORT;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testViewIndexReference() {
		Model test_model = model;
		long changeRequestId = 1l;
		String indexRefId = "2";
		String viewModal = classificationChangeSummaryController.viewIndexReference(test_model, changeRequestId,
				indexRefId);
		String expectedMav = ClassificationChangeSummaryController.INDEX_REFERENCE_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

	@Test
	public void testViewSupplementIncompleteReport() {
		Model test_model = model;
		long changeRequestId = 1l;
		HttpSession test_session = session;
		String viewModal = classificationChangeSummaryController.viewSupplementIncompleteReport(test_model,
				changeRequestId, test_session);
		String expectedMav = ClassificationChangeSummaryController.INCOMPLETE_SUPPLEMENT_REPORT;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testViewTabularIncompleteReport() {
		Model test_model = model;
		long changeRequestId = 1l;
		HttpSession test_session = session;
		String viewModal = classificationChangeSummaryController.viewTabularIncompleteReport(test_model,
				changeRequestId, test_session);
		String expectedMav = ClassificationChangeSummaryController.INCOMPLETE_TABULAR_REPORT;
		assertEquals("Should get same view", viewModal, expectedMav);
	}

	@Test
	public void testViewXmlProperty() {
		Model test_model = model;
		long xmlPropertyId = 1l;
		String fieldName = "fieldName";
		String code = "A00";
		String category = "T";
		String viewModal = classificationChangeSummaryController.viewXmlProperty(test_model, xmlPropertyId, fieldName,
				code, category);
		String expectedMav = ClassificationChangeSummaryController.XML_PROPERTY_POPUP;
		assertEquals("Should get same view", viewModal, expectedMav);

	}

}
