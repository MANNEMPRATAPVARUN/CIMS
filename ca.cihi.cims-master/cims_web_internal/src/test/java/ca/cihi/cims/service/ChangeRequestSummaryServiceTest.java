package ca.cihi.cims.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ChangeRequestIndexSummaryMapper;
import ca.cihi.cims.data.mapper.ChangeRequestSummaryMapper;
import ca.cihi.cims.data.mapper.ChangeRequestSupplementSummaryMapper;
import ca.cihi.cims.data.mapper.IncompleteReportMapper;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ConflictProposedChange;
import ca.cihi.cims.model.changerequest.ConflictProposedIndexChange;
import ca.cihi.cims.model.changerequest.ConflictProposedSupplementChange;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.model.changerequest.ValidationChange;
import ca.cihi.cims.web.filter.CurrentContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestSummaryServiceTest {

	private ChangeRequestSummaryServiceImpl changeRequestSummaryService;

	private final String Language = "ENG";

	private Long changeRequestId;
	@Mock
	CurrentContext context;
	@Mock
	ContextAccess contextAccess;

	@Mock
	LookupService lookupService;
	@Mock
	ClassificationService classificationService;

	@Mock
	IncompleteReportMapper incompleteReportMapper;

	@Mock
	ChangeRequestSummaryMapper changeRequestSummaryMapper;

	@Mock
	ChangeRequestIndexSummaryMapper changeRequestIndexSummaryMapper;

	@Mock
	ChangeRequestSupplementSummaryMapper changeRequestSupplementSummaryMapper;

	@Mock
	ConceptService conceptService;
	@Mock
	TabularConcept tabularConcept;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);

		changeRequestSummaryService = new ChangeRequestSummaryServiceImpl();
		changeRequestSummaryService.setChangeRequestSummaryMapper(changeRequestSummaryMapper);
		changeRequestSummaryService.setIncompleteReportMapper(incompleteReportMapper);
		changeRequestSummaryService.setChangeRequestIndexSummaryMapper(changeRequestIndexSummaryMapper);
		changeRequestSummaryService.setChangeRequestSupplementSummaryMapper(changeRequestSupplementSummaryMapper);
		changeRequestSummaryService.setConceptService(conceptService);
		changeRequestSummaryService.setLookupService(lookupService);
		changeRequestSummaryService.setClassificationService(classificationService);
		changeRequestSummaryService.setContext(context);

		when(changeRequestSummaryMapper.findModifiedConceptElementCodes(anyMap())).thenReturn(
				mockConceptModificationList());

		when(changeRequestIndexSummaryMapper.findModifiedIndexConceptElementCodes(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockConceptModificationList());

		changeRequestId = Long.valueOf("421");
	}

	private ContextIdentifier mockCCIContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setBaseClassification("CCI");
		contextIdentifier.setVersionCode(CIMSTestConstants.TEST_VERSION);
		contextIdentifier.setContextId(1L);
		contextIdentifier.setRequestId(1L);

		contextIdentifier.setIsVersionYear(true);
		return contextIdentifier;
	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setLanguageCode("ALL");
		changeRequest.setBaseVersionCode(CIMSTestConstants.TEST_VERSION);
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setTransferedTo(0L);

		return changeRequest;
	}

	private ConceptModification mockConceptModification() {
		ConceptModification conceptModification = new ConceptModification();
		conceptModification.setBreadCrumbs("breadCrumbs");
		conceptModification.setChangeRequestId(1L);
		conceptModification.setCode("code");
		conceptModification.setValidationId(1L);
		conceptModification.setElementId(1L);

		conceptModification.setConceptClassName("conceptClassName");

		return conceptModification;
	}

	private List<ConceptModification> mockConceptModificationList() {
		List<ConceptModification> conceptModificationList = new ArrayList<ConceptModification>();
		conceptModificationList.add(mockConceptModification());
		return conceptModificationList;
	}

	private ConflictProposedChange mockConflictProposedChange() {
		ConflictProposedChange conflictProposedChange = new ConflictProposedChange();
		conflictProposedChange.setElementId(1L);
		conflictProposedChange.setChangeType("Validation");
		conflictProposedChange.setCode("A00");
		conflictProposedChange.setConflictValue("conflictValue");
		conflictProposedChange.setConflictRealizedByContext(mockCCIContextIdentifier());
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
		conflictProposedIndexChange.setConflictRealizedByContext(mockCCIContextIdentifier());
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
		conflictProposedSupplementChange.setConflictRealizedByContext(mockCCIContextIdentifier());
		conflictProposedSupplementChange.setResolveActionCode(ConflictProposedSupplementChange.ACTIONCODE_KEEP);
		return conflictProposedSupplementChange;
	}

	private List<ConflictProposedSupplementChange> mockConflictProposedSupplementChanges() {
		List<ConflictProposedSupplementChange> conflictProposedSupplementChange = new ArrayList<ConflictProposedSupplementChange>();
		conflictProposedSupplementChange.add(mockConflictProposedSupplementChange());
		return conflictProposedSupplementChange;
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

	private List<Long> mockLongList() {
		List<Long> longList = new ArrayList<Long>();
		longList.add(1L);
		return longList;
	}

	private ProposedChange mockProposedChange() {
		ProposedChange proposedChange = new ProposedChange();
		proposedChange.setConflictValue("<validation classification='CCI'><GENDER_CODE>F</GENDER_CODE></validation>");
		proposedChange.setOldValue("<validation classification='CCI'><GENDER_CODE>M</GENDER_CODE></validation>");
		proposedChange.setProposedValue("<validation classification='CCI'><GENDER_CODE>F</GENDER_CODE></validation>");
		proposedChange.setTableName("ValidationDefinition");

		return proposedChange;
	}

	private List<ProposedChange> mockProposedChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		proposedChanges.add(mockProposedChange());
		return proposedChanges;
	}

	private RealizedChange mockRealizedChange() {
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(Long.valueOf("1234567"));
		realizedChange1.setFieldName("Index Desc");
		realizedChange1.setTableName("TextProperty");
		realizedChange1.setOldValue("<validation classification='CCI'><GENDER_CODE>M</GENDER_CODE></validation>");
		realizedChange1.setNewValue("<validation classification='CCI'><GENDER_CODE>F</GENDER_CODE></validation>");
		return realizedChange1;
	}

	private List<RealizedChange> mockRealizedChanges() {
		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		realizedChanges.add(mockRealizedChange());
		return realizedChanges;
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

	@Test
	public void testFindHtmlTextFromHtmlPropertyId() {
		String htmlString = "<table><tr><td>John</td><td> Smith</td></tr></table>";
		when(changeRequestSummaryMapper.findHtmlTextFromHtmlPropertyId(Long.valueOf("1234567"))).thenReturn(htmlString);
		assertEquals(htmlString, changeRequestSummaryService.findHtmlTextFromHtmlPropertyId(Long.valueOf("1234567")));
	}

	@Test
	public void testFindIndexDesc() {
		String indexDesc = "test index Desc";
		when(changeRequestIndexSummaryMapper.findIndexDesc(Long.valueOf("1234567"), Long.valueOf("111222333")))
				.thenReturn(indexDesc);

		assertEquals(indexDesc,
				changeRequestSummaryService.findIndexDesc(Long.valueOf("1234567"), Long.valueOf("111222333")));

	}

	@Test
	public void testFindMaxStructureId() {
		Long structureId = Long.valueOf("9876543210");

		when(changeRequestSummaryMapper.findMaxStructureId(changeRequestId)).thenReturn(structureId);

		assertEquals(structureId, changeRequestSummaryService.findMaxStructureId(changeRequestId));
	}

	@Test
	public void testFindModifiedConceptElementCode() {
		final Long changeRequestId = 1L;
		final Long maxStructureId = 1L;
		final Long validationId = 1L;
		ConceptModification conceptModification = changeRequestSummaryService.findModifiedConceptElementCode(
				changeRequestId, maxStructureId, validationId);
		assertTrue(conceptModification.getChangeRequestId() == mockConceptModification().getChangeRequestId());
		verify(changeRequestSummaryMapper, times(1)).findModifiedConceptElementCodes(anyMap());
	}

	@Test
	public void testFindModifiedConceptElementCodes() {
		// List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();

		Long maxStructureId = Long.valueOf("987654321");

		when(conceptService.getICDClassID("XMLPropertyVersion", "ValidationDefinition")).thenReturn(Long.valueOf(201));
		when(conceptService.getCCIClassID("XMLPropertyVersion", "ValidationDefinition")).thenReturn(Long.valueOf(147));

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("maxStructureId", maxStructureId);
		parameters.put("ICDValidationDefinition", 201);
		parameters.put("CCIValidationDefinition", 147);

		when(changeRequestSummaryMapper.findModifiedConceptElementCodes(parameters)).thenReturn(
				mockConceptModificationList());

		List<ConceptModification> conceptModifications = changeRequestSummaryService.findModifiedConceptElementCodes(
				changeRequestId, maxStructureId);
		assertTrue(conceptModifications.size() == 1);
	}

	@Test
	public void testFindModifiedIndexConceptElementCode() {
		final Long changeRequestId = 1L;
		final Long maxStructureId = 1L;
		final Long elementId = 1L;
		ConceptModification conceptModification = changeRequestSummaryService.findModifiedIndexConceptElementCode(
				changeRequestId, maxStructureId, elementId);
		assertTrue(conceptModification.getChangeRequestId() == mockConceptModification().getChangeRequestId());

	}

	@Test
	public void testFindModifiedIndexConceptElementCodes() {

		List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();

		Long maxStructureId = Long.valueOf("987654321");

		when(changeRequestIndexSummaryMapper.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId))
				.thenReturn(conceptModifications);

		assertEquals(conceptModifications,
				changeRequestSummaryService.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId));
	}

	@Test
	public void testFindModifiedSupplementConceptElementCodes() {

		List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();

		Long maxStructureId = Long.valueOf("987654321");

		when(
				changeRequestSupplementSummaryMapper.findModifiedSupplementConceptElementCodes(changeRequestId,
						maxStructureId, "ENG")).thenReturn(conceptModifications);

		assertEquals(conceptModifications,
				changeRequestSupplementSummaryMapper.findModifiedSupplementConceptElementCodes(changeRequestId,
						maxStructureId, "ENG"));
	}

	@Test
	public void testFindProposedAndConflictIndexChanges() {
		final Long contextId = 1L;
		final Long domainElementId = 2L;
		when(changeRequestIndexSummaryMapper.findProposedIndexChanges(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockProposedChanges());
		when(changeRequestSummaryMapper.findConflictRealizedByContext(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockCCIContextIdentifier());
		List<ProposedChange> conflictChanges = changeRequestSummaryService.findProposedAndConflictIndexChanges(
				contextId, domainElementId);
		assertTrue(conflictChanges.size() == 1);
	}

	@Test
	public void testFindProposedAndConflictSupplementChanges() {
		final Long contextId = 1L;
		final Long domainElementId = 2L;
		String language = "ENG";
		when(changeRequestSupplementSummaryMapper.findProposedSupplementChanges(contextId, domainElementId, language))
				.thenReturn(mockProposedChanges());
		when(changeRequestSummaryMapper.findConflictRealizedByContext(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockCCIContextIdentifier());
		List<ProposedChange> conflictChanges = changeRequestSummaryService.findProposedAndConflictSupplementChanges(
				contextId, domainElementId, language);
		assertTrue(conflictChanges.size() == 1);
	}

	@Test
	public void testFindProposedAndConflictTabularChanges() {
		final Long contextId = 1L;
		final Long domainElementId = 2L;
		when(changeRequestSummaryMapper.findProposedTabularChanges(contextId, domainElementId)).thenReturn(
				mockProposedChanges());
		when(changeRequestSummaryMapper.findConflictRealizedByContext(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockCCIContextIdentifier());
		List<ProposedChange> conflictChanges = changeRequestSummaryService.findProposedAndConflictTabularChanges(
				contextId, domainElementId);
		assertTrue(conflictChanges.size() == 1);
	}

	@Test
	public void testFindProposedAndConflictValidationChanges() {
		final String classification = "CCI";
		final Long contextId = 1L;
		final Long validationId = 2L;
		when(conceptService.getICDClassID("ConceptVersion", "ValidationICD")).thenReturn(Long.valueOf(201));
		when(conceptService.getCCIClassID("ConceptVersion", "ValidationCCI")).thenReturn(Long.valueOf(147));
		when(changeRequestSummaryMapper.findProposedValidationChanges(nullable(Map.class))).thenReturn(mockProposedChanges());
		when(changeRequestSummaryMapper.findConflictRealizedByContext(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockCCIContextIdentifier());
		List<ProposedChange> conflictChanges = changeRequestSummaryService.findProposedAndConflictValidationChanges(
				classification, contextId, validationId);
		assertTrue(conflictChanges.size() == 1);
	}

	@Test
	public void testFindProposedIndexChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		ProposedChange proposedChange1 = new ProposedChange();
		proposedChange1.setElementVersionId(Long.valueOf("1234567"));
		proposedChange1.setFieldName("Index Desc");
		proposedChange1.setTableName("TextProperty");
		proposedChange1.setOldValue("Old Index Desc");
		proposedChange1.setProposedValue("New Index Desc");
		proposedChanges.add(proposedChange1);

		Long contextId = Long.valueOf("3216547");
		Long domainElementId = Long.valueOf("888885");

		when(changeRequestIndexSummaryMapper.findProposedIndexChanges(contextId, domainElementId)).thenReturn(
				proposedChanges);

		assertEquals(proposedChanges, changeRequestSummaryService.findProposedIndexChanges(contextId, domainElementId));

		ProposedChange proposedChange2 = new ProposedChange();
		proposedChange2.setElementVersionId(Long.valueOf("1234567"));
		proposedChange2.setFieldName("Index Desc");
		proposedChange2.setTableName("TextProperty");
		proposedChange2.setOldValue("New Index Desc");
		proposedChange2.setProposedValue("Old Index Desc");
		proposedChanges.add(proposedChange2);

		assertEquals(0, changeRequestSummaryService.findProposedIndexChanges(contextId, domainElementId).size());
	}

	@Test
	public void testFindProposedStatus() {
		String status = "ACTIVE";

		Long contextId = Long.valueOf("3216547");
		Long domainElementId = Long.valueOf("888885");

		when(changeRequestSummaryMapper.findProposedStatus(contextId, domainElementId)).thenReturn(status);

		assertEquals(status, changeRequestSummaryService.findProposedStatus(contextId, domainElementId));
	}

	@Test
	public void testFindProposedStatusChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		ProposedChange proposedChange1 = new ProposedChange();
		proposedChange1.setElementVersionId(Long.valueOf("1234567"));
		proposedChange1.setFieldName("User title");
		proposedChange1.setOldValue("old User title");
		proposedChange1.setProposedValue("new User title");
		proposedChanges.add(proposedChange1);

		Long contextId = Long.valueOf("3216547");
		Long domainElementId = Long.valueOf("888885");

		when(changeRequestSummaryMapper.findProposedStatusChanges(contextId, domainElementId)).thenReturn(
				proposedChanges);

		assertEquals(proposedChanges, changeRequestSummaryService.findProposedStatusChanges(contextId, domainElementId));

	}

	@Test
	public void testFindProposedSupplementChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		ProposedChange proposedChange1 = new ProposedChange();
		proposedChange1.setElementVersionId(Long.valueOf("1234567"));
		proposedChange1.setFieldName("supplement Desc");
		proposedChange1.setTableName("TextProperty");
		proposedChange1.setOldValue("Old supplement Desc");
		proposedChange1.setProposedValue("New supplement Desc");
		proposedChanges.add(proposedChange1);

		Long contextId = Long.valueOf("3216547");
		Long domainElementId = Long.valueOf("888885");

		when(changeRequestSupplementSummaryMapper.findProposedSupplementChanges(contextId, domainElementId, Language))
				.thenReturn(proposedChanges);

		assertEquals(proposedChanges,
				changeRequestSummaryService.findProposedSupplementChanges(contextId, domainElementId, Language));

		ProposedChange proposedChange2 = new ProposedChange();
		proposedChange2.setElementVersionId(Long.valueOf("1234567"));
		proposedChange2.setFieldName("supplement Desc");
		proposedChange2.setTableName("TextProperty");
		proposedChange2.setOldValue("New supplement Desc");
		proposedChange2.setProposedValue("Old supplement Desc");
		proposedChanges.add(proposedChange2);

		assertEquals(0, changeRequestSummaryService.findProposedSupplementChanges(contextId, domainElementId, Language)
				.size());
	}

	@Test
	public void testFindProposedTabularChanges() {
		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		ProposedChange proposedChange1 = new ProposedChange();
		proposedChange1.setElementVersionId(Long.valueOf("1234567"));
		proposedChange1.setFieldName("User title");
		proposedChange1.setTableName("TextProperty");
		proposedChange1.setOldValue("old User title");
		proposedChange1.setProposedValue("new User title");
		proposedChanges.add(proposedChange1);

		Long contextId = Long.valueOf("3216547");
		Long domainElementId = Long.valueOf("888885");

		when(changeRequestSummaryMapper.findProposedTabularChanges(contextId, domainElementId)).thenReturn(
				proposedChanges);

		assertEquals(proposedChanges,
				changeRequestSummaryService.findProposedTabularChanges(contextId, domainElementId));

		ProposedChange proposedChange2 = new ProposedChange();
		proposedChange2.setElementVersionId(Long.valueOf("1234567"));
		proposedChange2.setFieldName("User title");
		proposedChange2.setTableName("TextProperty");
		proposedChange2.setOldValue("new User title");
		proposedChange2.setProposedValue("old User title");
		proposedChanges.add(proposedChange2);

		assertEquals(0, changeRequestSummaryService.findProposedTabularChanges(contextId, domainElementId).size());
	}

	@Test
	public void testFindProposedValidationChanges() {
		Long contextId = 1L;
		String classification = "ICD-10-CA";
		Long validationId = 1L;
		Long validationCCIcid = 1L;
		Long validationICDcid = 1L;
		boolean showOldValue = true;

		List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		ProposedChange proposedChange1 = new ProposedChange();
		proposedChange1.setElementVersionId(1234567L);
		proposedChange1.setFieldName("DAD - Acute Care");
		proposedChange1.setOldValue(null);
		proposedChange1
				.setProposedValue("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>2148961</ELEMENT_ID><GENDER_CODE>N</GENDER_CODE><GENDER_DESC_ENG>Male &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>Y</MRDX_MAIN><DX_TYPE_1>Y</DX_TYPE_1><DX_TYPE_2>Y</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>Y</DX_TYPE_W><DX_TYPE_X>Y</DX_TYPE_X><DX_TYPE_Y>Y</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		proposedChange1.setTableName("ValidationDefinition");
		proposedChange1.setValidationId(validationId);
		proposedChanges.add(proposedChange1);

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("validationId", validationId);
		parameters.put("validationCCIcid", validationCCIcid);
		parameters.put("validationICDcid", validationICDcid);

		when(conceptService.getCCIClassID("ConceptVersion", "ValidationCCI")).thenReturn(validationCCIcid);
		when(conceptService.getICDClassID("ConceptVersion", "ValidationICD")).thenReturn(validationICDcid);
		when(changeRequestSummaryMapper.findProposedValidationChanges(parameters)).thenReturn(proposedChanges);
		List<ValidationChange> validationChanges = changeRequestSummaryService.findProposedValidationChanges(
				classification, contextId, validationId, showOldValue);

		assertEquals(1, validationChanges.size());
	}

	@Test
	public void testFindProposedValidationChanges1() {
		final String classification = "CCI";
		final Long contextId = 1L;
		final Long validationId = 2L;
		final boolean showOldValue = true;
		when(changeRequestSummaryMapper.findProposedValidationChanges(anyMap())).thenReturn(mockProposedChanges());
		changeRequestSummaryService
				.findProposedValidationChanges(classification, contextId, validationId, showOldValue);
		verify(changeRequestSummaryMapper, times(1)).findProposedValidationChanges(anyMap());

	}

	@Test
	public void testFindRealizedIndexChanges() {
		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(Long.valueOf("1234567"));
		realizedChange1.setFieldName("Index Desc");
		realizedChange1.setTableName("TextProperty");
		realizedChange1.setOldValue("Old Index Desc");
		realizedChange1.setNewValue("New Index Desc");
		realizedChanges.add(realizedChange1);

		Long domainElementId = Long.valueOf("888885");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		when(changeRequestIndexSummaryMapper.findRealizedIndexChanges(parameters)).thenReturn(realizedChanges);

		assertEquals(realizedChanges,
				changeRequestSummaryService.findRealizedIndexChanges(changeRequestId, domainElementId));

		RealizedChange realizedChange2 = new RealizedChange();
		realizedChange2.setElementVersionId(Long.valueOf("1234567"));
		realizedChange2.setFieldName("Index Desc");
		realizedChange2.setTableName("TextProperty");
		realizedChange2.setOldValue("New Index Desc");
		realizedChange2.setNewValue("Old Index Desc");
		realizedChanges.add(realizedChange2);

		assertEquals(0, changeRequestSummaryService.findRealizedIndexChanges(changeRequestId, domainElementId).size());

	}

	@Test
	public void testFindRealizedStatus() {
		String status = "ACTIVE";

		Long domainElementId = Long.valueOf("888885");

		when(changeRequestSummaryMapper.findRealizedStatus(changeRequestId, domainElementId)).thenReturn(status);

		assertEquals(status, changeRequestSummaryService.findRealizedStatus(changeRequestId, domainElementId));
	}

	@Test
	public void testFindRealizedStatusChanges() {
		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(Long.valueOf("1234567"));
		realizedChange1.setFieldName("Long title");
		realizedChange1.setOldValue("old Long title");
		realizedChange1.setNewValue("new Long title");
		realizedChanges.add(realizedChange1);

		Long domainElementId = Long.valueOf("11223344");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);

		when(changeRequestSummaryMapper.findRealizedStatusChanges(parameters)).thenReturn(realizedChanges);

		assertEquals(realizedChanges,
				changeRequestSummaryService.findRealizedStatusChanges(changeRequestId, domainElementId));

	}

	@Test
	public void testFindRealizedSupplementChanges() {
		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(Long.valueOf("1234567"));
		realizedChange1.setFieldName("Supplement Desc");
		realizedChange1.setTableName("TextProperty");
		realizedChange1.setOldValue("Old Supplement Desc");
		realizedChange1.setNewValue("New Supplement Desc");
		realizedChanges.add(realizedChange1);

		// Long changeRequestId = 222L;
		Long domainElementId = Long.valueOf("888885");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		when(
				changeRequestSupplementSummaryMapper.findRealizedSupplementChanges(changeRequestId, domainElementId,
						Language)).thenReturn(realizedChanges);

		assertEquals(realizedChanges,
				changeRequestSummaryService.findRealizedSupplementChanges(changeRequestId, domainElementId, Language));

		RealizedChange realizedChange2 = new RealizedChange();
		realizedChange2.setElementVersionId(Long.valueOf("1234567"));
		realizedChange2.setFieldName("Supplement Desc");
		realizedChange2.setTableName("TextProperty");
		realizedChange2.setOldValue("New Supplement Desc");
		realizedChange2.setNewValue("Old Supplement Desc");
		realizedChanges.add(realizedChange2);

		assertEquals(0,
				changeRequestSummaryService.findRealizedSupplementChanges(changeRequestId, domainElementId, Language)
						.size());

	}

	@Test
	public void testFindRealizedTabularChanges() {
		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(Long.valueOf("1234567"));
		realizedChange1.setFieldName("Long title");
		realizedChange1.setTableName("TextProperty");
		realizedChange1.setOldValue("old Long title");
		realizedChange1.setNewValue("new Long title");
		realizedChanges.add(realizedChange1);

		Long domainElementId = Long.valueOf("888885");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		when(changeRequestSummaryMapper.findRealizedTabularChanges(parameters)).thenReturn(realizedChanges);

		assertEquals(realizedChanges,
				changeRequestSummaryService.findRealizedTabularChanges(changeRequestId, domainElementId));

		RealizedChange realizedChange2 = new RealizedChange();
		realizedChange2.setElementVersionId(Long.valueOf("1234567"));
		realizedChange2.setFieldName("Long title");
		realizedChange2.setTableName("TextProperty");
		realizedChange2.setOldValue("new Long title");
		realizedChange2.setNewValue("old Long title");
		realizedChanges.add(realizedChange2);

		assertEquals(0, changeRequestSummaryService.findRealizedTabularChanges(changeRequestId, domainElementId).size());

	}

	@Test
	public void testFindRealizedValidationChanges() {
		Long contextId = 1L;
		String classification = "ICD-10-CA";
		Long validationId = 1L;
		Long validationCCIcid = 1L;
		Long validationICDcid = 1L;

		List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		RealizedChange realizedChange1 = new RealizedChange();
		realizedChange1.setElementVersionId(1234567L);
		realizedChange1.setFieldName("DAD - Acute Care");
		realizedChange1.setOldValue(null);
		realizedChange1
				.setNewValue("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>2148961</ELEMENT_ID><GENDER_CODE>N</GENDER_CODE><GENDER_DESC_ENG>Male &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>Y</MRDX_MAIN><DX_TYPE_1>Y</DX_TYPE_1><DX_TYPE_2>Y</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>Y</DX_TYPE_W><DX_TYPE_X>Y</DX_TYPE_X><DX_TYPE_Y>Y</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		realizedChange1.setTableName("ValidationDefinition");
		realizedChanges.add(realizedChange1);

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("structureId", contextId);
		parameters.put("validationId", validationId);
		parameters.put("validationCCIcid", validationCCIcid);
		parameters.put("validationICDcid", validationICDcid);

		when(conceptService.getCCIClassID("ConceptVersion", "ValidationCCI")).thenReturn(validationCCIcid);
		when(conceptService.getICDClassID("ConceptVersion", "ValidationICD")).thenReturn(validationICDcid);
		when(changeRequestSummaryMapper.findRealizedValidationChanges(parameters)).thenReturn(realizedChanges);

		List<RealizedChange> validationChanges = changeRequestSummaryService.findRealizedValidationChanges(
				classification, contextId, validationId);
		assertEquals(1, validationChanges.size());
	}

	@Test
	public void testFindRealizedValidationChanges1() {
		final String classification = "CCI";
		final Long structureId = 1L;
		final Long validationId = 2L;
		when(changeRequestSummaryMapper.findRealizedValidationChanges(anyMap())).thenReturn(mockRealizedChanges());
		List<RealizedChange> realizedValidationChanges = changeRequestSummaryService.findRealizedValidationChanges(
				classification, structureId, validationId);
		assertTrue(realizedValidationChanges.size() == 1);

	}

	@Test
	public void testFindXmlTextFromXmlPropertyId() {
		String xmlString = "<test><name>John</name><address>1 carlingwood</address></test>";
		when(changeRequestSummaryMapper.findXmlTextFromXmlPropertyId(Long.valueOf("1234567"))).thenReturn(xmlString);

		assertEquals(xmlString, changeRequestSummaryService.findXmlTextFromXmlPropertyId(Long.valueOf("1234567")));
	}

	@Test
	public void testGetChangeRequestIndexSummaryMapper() {
		assertEquals(changeRequestIndexSummaryMapper, changeRequestSummaryService.getChangeRequestIndexSummaryMapper());
	}

	@Test
	public void testGetChangeRequestSummaryMapper() {
		assertEquals(changeRequestSummaryMapper, changeRequestSummaryService.getChangeRequestSummaryMapper());
	}

	@Test
	public void testGetSet() {
		assertTrue(changeRequestSummaryService.getChangeRequestSummaryMapper().equals(changeRequestSummaryMapper));
		assertTrue(changeRequestSummaryService.getChangeRequestIndexSummaryMapper().equals(
				changeRequestIndexSummaryMapper));
		assertTrue(changeRequestSummaryService.getChangeRequestSupplementSummaryMapper().equals(
				changeRequestSupplementSummaryMapper));
		assertTrue(changeRequestSummaryService.getConceptService().equals(conceptService));
		assertTrue(changeRequestSummaryService.getIncompleteReportMapper().equals(incompleteReportMapper));
	}

	@Test
	public void testHasIncompleteIndexProperties() {
		final ChangeRequest changeRequest = mockChangeRequest();
		changeRequest.setCategory(ChangeRequestCategory.I);
		when(lookupService.findOpenContextByChangeRquestId(nullable(Long.class))).thenReturn(mockCCIContextIdentifier());
		when(changeRequestSummaryMapper.findMaxStructureId(nullable(Long.class))).thenReturn(1L);
		when(changeRequestSummaryMapper.findModifiedConceptElementCodes(anyMap())).thenReturn(
				mockConceptModificationList());
		when(incompleteReportMapper.checkIndexConcept(nullable(Long.class), nullable(Long.class))).thenReturn("incomplete");
		boolean hasIncompleteIndex = changeRequestSummaryService.hasIncompleteProperties(changeRequest);
		assertTrue(hasIncompleteIndex);
	}

	@Test
	public void testHasIncompleteProperties() {
		Long changeRequestId = 1L;
		Long structureId = 1L;
		Long conceptId = 1L;
		boolean isVersionYear = true;
		String languageCode = "ENG";

		ChangeRequest changeRequest = new ChangeRequest();
		changeRequest.setChangeRequestId(changeRequestId);
		changeRequest.setLanguageCode(languageCode);

		ContextIdentifier changeContext = new ContextIdentifier();
		changeContext.setContextId(structureId);
		changeContext.setIsVersionYear(isVersionYear);

		when(lookupService.findOpenContextByChangeRquestId(changeRequestId)).thenReturn(changeContext);
		when(changeRequestSummaryMapper.findMaxStructureId(changeRequestId)).thenReturn(structureId);

		List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		ConceptModification conceptModification = new ConceptModification();
		conceptModification.setElementId(conceptId);
		conceptModifications.add(conceptModification);
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", structureId);
		parameters.put("conceptId", conceptId);
		parameters.put("isVersionYear", 'Y');

		// Check tabular incomplete
		changeRequest.setCategory(ChangeRequestCategory.T);
		when(incompleteReportMapper.checkTabularConcept(parameters)).thenReturn("RU110");
		when(changeRequestSummaryService.findModifiedConceptElementCodes(changeRequestId, structureId)).thenReturn(
				conceptModifications);
		assertTrue(changeRequestSummaryService.hasIncompleteProperties(changeRequest));
		when(incompleteReportMapper.checkTabularConcept(parameters)).thenReturn("");
		assertFalse(changeRequestSummaryService.hasIncompleteProperties(changeRequest));

		// Check index incomplete
		changeRequest.setCategory(ChangeRequestCategory.I);
		when(incompleteReportMapper.checkIndexConcept(structureId, conceptId)).thenReturn("RU110");
		when(changeRequestSummaryService.findModifiedIndexConceptElementCodes(changeRequestId, structureId))
				.thenReturn(conceptModifications);
		assertTrue(changeRequestSummaryService.hasIncompleteProperties(changeRequest));
		when(incompleteReportMapper.checkIndexConcept(structureId, conceptId)).thenReturn("");
		assertFalse(changeRequestSummaryService.hasIncompleteProperties(changeRequest));

		// Check supplement incomplete
		changeRequest.setCategory(ChangeRequestCategory.S);
		when(incompleteReportMapper.checkSupplementConcept(structureId, conceptId)).thenReturn("RU110");
		when(
				changeRequestSummaryService.findModifiedSupplementConceptElementCodes(changeRequestId, structureId,
						languageCode)).thenReturn(conceptModifications);
		assertTrue(changeRequestSummaryService.hasIncompleteProperties(changeRequest));
		when(incompleteReportMapper.checkSupplementConcept(structureId, conceptId)).thenReturn("");
		assertFalse(changeRequestSummaryService.hasIncompleteProperties(changeRequest));
	}

	@Test
	public void testHasIncompleteSupplementProperties() {
		final ChangeRequest changeRequest = mockChangeRequest();
		changeRequest.setCategory(ChangeRequestCategory.S);
		when(lookupService.findOpenContextByChangeRquestId(nullable(Long.class))).thenReturn(mockCCIContextIdentifier());
		when(changeRequestSummaryMapper.findMaxStructureId(nullable(Long.class))).thenReturn(1L);
		when(
				changeRequestSupplementSummaryMapper.findModifiedSupplementConceptElementCodes(nullable(Long.class), nullable(Long.class),
						nullable(String.class))).thenReturn(mockConceptModificationList());

		when(incompleteReportMapper.checkSupplementConcept(nullable(Long.class), nullable(Long.class))).thenReturn("incomplete");
		boolean hasIncompleteSupplement = changeRequestSummaryService.hasIncompleteProperties(changeRequest);
		assertTrue(hasIncompleteSupplement);
	}

	@Test
	public void testHasIncompleteTabularProperties() {
		final ChangeRequest changeRequest = mockChangeRequest();
		changeRequest.setCategory(ChangeRequestCategory.T);
		when(lookupService.findOpenContextByChangeRquestId(nullable(Long.class))).thenReturn(mockCCIContextIdentifier());
		when(changeRequestSummaryMapper.findMaxStructureId(nullable(Long.class))).thenReturn(1L);
		when(changeRequestSummaryMapper.findModifiedConceptElementCodes(anyMap())).thenReturn(
				mockConceptModificationList());
		when(incompleteReportMapper.checkTabularConcept(anyMap())).thenReturn("incomplete");
		boolean hasIncompleteTabular = changeRequestSummaryService.hasIncompleteProperties(changeRequest);
		assertTrue(hasIncompleteTabular);
	}

	@Test
	public void testProcessRealizedValidationChange() {
		final HashMap<String, RealizedChange> realizedValidationMap = new HashMap<String, RealizedChange>();
		realizedValidationMap.put("change1", mockRealizedChange());
		final String classification = "CCI";
		List<ValidationChange> validationChanges = changeRequestSummaryService.processRealizedValidationChange(
				realizedValidationMap, classification);
		assertTrue(validationChanges.size() == 2);

	}

	@Test
	public void testResolveConflictsDiscard() {
		ResolveConflict resolveConflict = mockResolveConflict();
		List<ConflictProposedChange> conflictTabs = resolveConflict.getConflictChanges();
		for (ConflictProposedChange conflictProposedChange : conflictTabs) {
			conflictProposedChange.setResolveActionCode(ConflictProposedChange.ActionCode_Discard);
		}
		User currentUser = mockCurrentUser();
		when(
				changeRequestSummaryMapper.findValidationConceptAndPropertyIdsByValidationDefinitionElementVersionId(
						nullable(Long.class), nullable(Long.class))).thenReturn(mockLongList());
		when(context.context()).thenReturn(contextAccess);
		when(contextAccess.load(nullable(Long.class))).thenReturn(tabularConcept);

		changeRequestSummaryService.resolveConflicts(resolveConflict, currentUser);
		verify(changeRequestSummaryMapper, times(2)).deleteStructureElementVersion(nullable(Long.class));

	}

	@Test
	public void testResolveConflictsKeep() {
		final ResolveConflict resolveConflict = mockResolveConflict();
		User currentUser = mockCurrentUser();
		when(
				changeRequestSummaryMapper.findValidationConceptAndPropertyIdsByValidationDefinitionElementVersionId(
						nullable(Long.class), nullable(Long.class))).thenReturn(mockLongList());

		changeRequestSummaryService.resolveConflicts(resolveConflict, currentUser);
		verify(changeRequestSummaryMapper, times(2)).updateElementVersionChangedFromVersionId(nullable(Long.class), nullable(Long.class));

	}

	@Test
	public void testResolveIndexConflictsDiscard() {
		final ResolveConflict resolveConflict = mockResolveConflict();
		List<ConflictProposedIndexChange> conflictIndexes = resolveConflict.getConflictIndexChanges();
		for (ConflictProposedIndexChange conflictProposedIndexChange : conflictIndexes) {
			conflictProposedIndexChange.setResolveActionCode(ConflictProposedIndexChange.ActionCode_Discard);
		}
		User currentUser = mockCurrentUser();
		when(context.context()).thenReturn(contextAccess);
		when(contextAccess.load(nullable(Long.class))).thenReturn(tabularConcept);
		changeRequestSummaryService.resolveIndexConflicts(resolveConflict, currentUser);
	}

	@Test
	public void testResolveIndexConflictsKeep() {
		final ResolveConflict resolveConflict = mockResolveConflict();
		User currentUser = mockCurrentUser();
		when(context.context()).thenReturn(contextAccess);
		when(contextAccess.load(nullable(Long.class))).thenReturn(tabularConcept);
		changeRequestSummaryService.resolveIndexConflicts(resolveConflict, currentUser);
	}

	@Test
	public void testResolveSupplementConflictsDiscard() {
		final ResolveConflict resolveConflict = mockResolveConflict();
		List<ConflictProposedSupplementChange> conflictSupplements = resolveConflict.getConflictSupplementChanges();
		for (ConflictProposedSupplementChange conflictProposedSupplementChange : conflictSupplements) {
			conflictProposedSupplementChange.setResolveActionCode(ConflictProposedSupplementChange.ACTIONCODE_DISCARD);
		}
		User currentUser = mockCurrentUser();
		when(context.context()).thenReturn(contextAccess);
		when(contextAccess.load(nullable(Long.class))).thenReturn(tabularConcept);
		changeRequestSummaryService.resolveSupplementConflicts(resolveConflict, currentUser);

	}

	@Test
	public void testResolveSupplementConflictsKeep() {
		final ResolveConflict resolveConflict = mockResolveConflict();
		User currentUser = mockCurrentUser();
		when(context.context()).thenReturn(contextAccess);
		when(contextAccess.load(nullable(Long.class))).thenReturn(tabularConcept);
		changeRequestSummaryService.resolveSupplementConflicts(resolveConflict, currentUser);

	}
}
