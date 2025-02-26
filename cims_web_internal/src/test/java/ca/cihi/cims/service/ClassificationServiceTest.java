package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.Errors;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.query.EqCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.bll.query.WrapperPropertyCriterion;
import ca.cihi.cims.content.cci.CciApproachTechniqueComponent;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.cci.CciTissueComponent;
import ca.cihi.cims.content.cci.CciValidation;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.icd.IcdValidation;
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.SexValidation;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.content.shared.SupplementType;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.AttributeType;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.DxType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.IndexCategoryReferenceModel;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexTermReferenceModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.model.index.SiteIndicatorModel;
import ca.cihi.cims.model.sgsc.CCIComponentSupplement;
import ca.cihi.cims.model.sgsc.CCIRubric;
import ca.cihi.cims.model.supplement.SupplementMatter;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.model.tabular.TabularConceptDetails;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.model.tabular.TabularConceptXmlType;
import ca.cihi.cims.model.tabular.validation.TabularConceptCciValidationSetReportModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetReportModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationGenderModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationSetModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.filter.CurrentContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ClassificationServiceTest {
	static Date testDate = Calendar.getInstance().getTime();

	@Mock
	private ChangeRequestAccessService accessService;
	@Mock
	private BookIndex bookIndex;
	@Mock
	private CciApproachTechniqueComponent cciApproachTechniqueComponent;
	@Mock
	private CciDeviceAgentComponent cciDeviceAgentComponent;
	@Mock
	private CciGroupComponent cciGroupComponent;
	@Mock
	private CciInvasivenessLevel cciInvasivenessLevel;
	@Mock
	private CciTabular cciTabular;
	@Mock
	private CciTissueComponent cciTissueComponent;
	@Mock
	private CciValidation cciValidation;
	@Mock
	private ChangeRequestService changeRequestService;

	@Autowired
	private ClassificationService classificationService;
	@Mock
	private ConceptService conceptService;

	@Mock
	private TransformationService conceptTransformationService;

	@Mock
	private CurrentContext context;
	@Mock
	private ContextAccess contextAccess;
	@Mock
	private ContextOperations contextOperations;
	@Mock
	private DaggerAsterisk daggerAsterisk;
	@Mock
	private ElementOperations elementOperations;

	@Mock
	private Errors errors;
	@Mock
	private FacilityType facilityType;
	@Mock
	private IcdTabular icdTabular;
	@Mock
	private IcdValidation icdValidation;
	@Mock
	private IcdIndexAlphabetical index;

	@Mock
	private IcdIndexNeoplasm indexMock;
	@Mock
	private TransformIndexService indexTransformationService;
	@Mock
	private CciInterventionComponent intervention;
	@Mock
	private NonContextOperations nonContextOperations;

	@Mock
	private SexValidation sexValidation;
	@Mock
	private Supplement supplement;
	@Mock
	private SupplementType supplementType1;
	@Mock
	private TransformSupplementService transformSupplementService;

	@Mock
	private ViewService viewService;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		// classificationService = new ClassificationService();
		classificationService.setTransformationService(conceptTransformationService);
		classificationService.setAccessService(accessService);
		classificationService.setChangeRequestService(changeRequestService);
		classificationService.setConceptService(conceptService);
		classificationService.setConceptTransformationService(conceptTransformationService);
		classificationService.setContext(context);
		classificationService.setContextOperations(contextOperations);
		classificationService.setElementOperations(elementOperations);
		classificationService.setIndexTransformationService(indexTransformationService);
		classificationService.setNonContextOperations(nonContextOperations);
		classificationService.setTransformSupplementService(transformSupplementService);
		classificationService.setViewService(viewService);
		when(context.context()).thenReturn(contextAccess);

		when(contextAccess.getContextId()).thenReturn(mockICDContextIdentifier());

		when(accessService.getChangeRequestClassificationPermission(nullable(User.class), nullable(Long.class),
				nullable(ChangeRequestCategory.class))).thenReturn(mockChangeRequestPermission());

		when(index.getStatus()).thenReturn("ACTIVE");
		when(index.getContainingBook()).thenReturn(bookIndex);
		when(index.getParent()).thenReturn(bookIndex);
		when(bookIndex.getDescription()).thenReturn("a");
		when(bookIndex.getElementId()).thenReturn(1L);

	}

	private IndexCategoryReferenceModel mockCategoryReference() {
		IndexCategoryReferenceModel categoryReference = new IndexCategoryReferenceModel();
		categoryReference.setMainElementId(1L);
		// categoryReference.setMainCode("A");
		return categoryReference;
	}

	private List<IndexCategoryReferenceModel> mockCategoryReferences() {
		List<IndexCategoryReferenceModel> categoryReferences = new ArrayList<IndexCategoryReferenceModel>();
		categoryReferences.add(mockCategoryReference());
		return categoryReferences;
	}

	private TabularConceptDetails mockCCICodeTabularConceptDetails() {
		TabularConceptDetails tabularConceptDetails = new TabularConceptDetails();
		tabularConceptDetails.setCode("01");
		tabularConceptDetails.setClassName("CCICODE");
		tabularConceptDetails.setParentCode("00");

		return tabularConceptDetails;
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

	private TabularConceptDetails mockCCIGroupTabularConceptDetails() {
		TabularConceptDetails tabularConceptDetails = new TabularConceptDetails();
		tabularConceptDetails.setCode("01");
		tabularConceptDetails.setClassName("Group");
		tabularConceptDetails.setParentCode("00");

		return tabularConceptDetails;
	}

	private Iterator<CciInvasivenessLevel> mockCciInvasivenessLevels() {
		List<CciInvasivenessLevel> cciInvasivenessLevels = new ArrayList<CciInvasivenessLevel>();
		cciInvasivenessLevels.add(cciInvasivenessLevel);

		return cciInvasivenessLevels.iterator();
	}

	private Collection<CciValidation> mockCciValidations() {
		Collection<CciValidation> cciValidations = new ArrayList<CciValidation>();
		cciValidations.add(cciValidation);
		return cciValidations;
	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setLastUpdatedTime(testDate);
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setLanguageCode("ALL");
		changeRequest.setBaseVersionCode(CIMSTestConstants.TEST_VERSION);
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setTransferedTo(0L);

		return changeRequest;
	}

	private ChangeRequestPermission mockChangeRequestPermission() {
		StandardChangeRequestPermission changeRequestPermission = new StandardChangeRequestPermission(true, true,
				Language.ENGLISH, true);
		return changeRequestPermission;
	}

	private ContentViewerModel mockContentViewerModel() {
		ContentViewerModel contentViewerModel = new ContentViewerModel();
		contentViewerModel.setConceptCode("conceptCode");
		return contentViewerModel;
	}

	private List<ContentViewerModel> mockContentViewerModels() {
		List<ContentViewerModel> cvms = new ArrayList<ContentViewerModel>();
		ContentViewerModel cvm = mockContentViewerModel();
		cvms.add(cvm);
		return cvms;

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

	private List<DaggerAsterisk> mockDaggerAsterisks() {
		List<DaggerAsterisk> daggerAsterisks = new ArrayList<DaggerAsterisk>();
		daggerAsterisks.add(daggerAsterisk);
		return daggerAsterisks;

	}

	private ErrorBuilder mockErrorBuilder() {
		ErrorBuilder errorBuilder = new ErrorBuilder("model", errors);
		return errorBuilder;
	}

	private List<FacilityType> mockFacilities() {
		List<FacilityType> facilities = new ArrayList<FacilityType>();
		facilities.add(facilityType);
		return facilities;
	}

	private TabularConceptDetails mockICDBlockTabularConceptDetails() {
		TabularConceptDetails tabularConceptDetails = new TabularConceptDetails();
		tabularConceptDetails.setChapterCode("01");
		tabularConceptDetails.setClassName("BLOCK");
		tabularConceptDetails.setParentCode("00");
		tabularConceptDetails.setStatus("ACTIVE");

		return tabularConceptDetails;
	}

	private TabularConceptModel mockICDBlockTabularConceptModel() {
		TabularConceptModel model = new TabularConceptModel();
		model.setElementId(1L);
		model.setType(TabularConceptType.ICD_BLOCK);
		model.setStatus(ConceptStatus.ACTIVE);
		return model;
	}

	private TabularConceptDetails mockICDChapterTabularConceptDetails() {
		TabularConceptDetails tabularConceptDetails = new TabularConceptDetails();
		tabularConceptDetails.setChapterCode("01");
		tabularConceptDetails.setClassName("CHAPTER");
		tabularConceptDetails.setParentCode("00");

		return tabularConceptDetails;
	}

	private ContextIdentifier mockICDContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setBaseClassification("ICD-10-CA");
		contextIdentifier.setVersionCode(CIMSTestConstants.TEST_VERSION);
		contextIdentifier.setContextId(1L);
		contextIdentifier.setRequestId(1L);

		contextIdentifier.setIsVersionYear(true);
		return contextIdentifier;
	}

	private Collection<IcdValidation> mockIcdValidations() {
		Collection<IcdValidation> icdValidations = new ArrayList<IcdValidation>();
		icdValidations.add(icdValidation);
		return icdValidations;
	}

	private List<IdCodeDescription> mockIdCodeDescriptions() {
		List<IdCodeDescription> idCodeDescriptions = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCodeDescription = new IdCodeDescription();
		idCodeDescription.setCode("statusRef");
		idCodeDescription.setId(1L);
		idCodeDescription.setDescription("description");
		idCodeDescriptions.add(idCodeDescription);
		return idCodeDescriptions;
	}

	private IndexModel mockIndexModel() {
		IndexModel indexModel = new IndexModel();
		indexModel.setElementId(1L);
		indexModel.setEntity(bookIndex);
		indexModel.setStatus(ConceptStatus.ACTIVE);
		indexModel.setDescription("a");

		return indexModel;
	}

	private IndexModel mockIndexModelWithReferences() {
		IndexModel indexModel = mockIndexModel();
		indexModel.setIndexReferences(mockIndexReferences());
		indexModel.setCategoryReferences(mockCategoryReferences());
		indexModel.setType(IndexType.CCI_ALPHABETIC_INDEX);

		return indexModel;
	}

	private List<IndexTermReferenceModel> mockIndexReferences() {
		List<IndexTermReferenceModel> indexReferences = new ArrayList<IndexTermReferenceModel>();
		indexReferences.add(mockIndexTermReferenceModel());
		return indexReferences;
	}

	private IndexTermReferenceModel mockIndexTermReferenceModel() {
		IndexTermReferenceModel indexTermReference = new IndexTermReferenceModel();
		indexTermReference.setElementId(1l);
		return indexTermReference;
	}

	private OptimisticLock mockOptimisticLock() {
		OptimisticLock optimisticLock = new OptimisticLock();
		optimisticLock.setTimestamp(testDate);
		return optimisticLock;
	}

	private List<SexValidation> mockSexValidations() {
		List<SexValidation> sexValidations = new ArrayList<SexValidation>();
		sexValidations.add(sexValidation);
		return sexValidations;
	}

	private SupplementModel mockSupplementModel() {
		SupplementModel supplementModel = new SupplementModel();
		supplementModel.setElementId(1L);
		supplementModel.setEntity(supplement);
		supplementModel.setMatter(SupplementMatter.FRONT);
		supplementModel.setStatus(ConceptStatus.ACTIVE);
		supplementModel.setDescription("a");
		return supplementModel;
	}

	private Iterator<SupplementType> mockSupplementTypes() {

		List<SupplementType> supplementTypes = new ArrayList<SupplementType>();
		supplementTypes.add(supplementType1);
		return supplementTypes.iterator();

	}

	private TabularConceptIcdValidationSetModel mockTabularConceptIcdValidationSetModel() {
		TabularConceptIcdValidationSetModel model = new TabularConceptIcdValidationSetModel();
		model.setDxTypeId(1L);
		return model;
	}

	private TabularConceptModel mockTabularConceptModel() {
		TabularConceptModel model = new TabularConceptModel();
		model.setType(TabularConceptType.CCI_GROUP);
		return model;
	}

	private TabularConceptValidationSetModel mockTabularConceptValidationSetModel() {
		TabularConceptValidationSetModel model = new TabularConceptValidationSetModel();
		return model;
	}

	private TabularConceptXmlModel mockTabularConceptXmlModel() {
		TabularConceptXmlModel model = new TabularConceptXmlModel();
		model.setElementId(1l);
		model.setType(TabularConceptXmlType.CODE_ALSO);
		return model;
	}

	@Test
	public void testCreateICDBlockTabular() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		TabularConceptType type = TabularConceptType.ICD_BLOCK;
		Long parentId = 1L;
		String code = "b1";
		when(contextAccess.load(nullable(Long.class))).thenReturn(icdTabular);
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());

		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(icdTabular);
		when(icdTabular.getCode()).thenReturn("BLOCK");
		when(icdTabular.getTypeCode()).thenReturn("BLOCK");

		doNothing().when(contextAccess).persist();
		doNothing().when(conceptTransformationService).transformConcept(nullable(TabularConcept.class),
				nullable(ContextAccess.class), nullable(Boolean.class));
		// ClassificationService classificationServiceSpy = Mockito.spy(classificationService);
		/*
		 * Mockito.doReturn(icdTabular).when(classificationServiceSpy) .createIcdTabular(nullable(ContextAccess.class),
		 * nullable(String.class), nullable(String.class));
		 */
		classificationService.createTabular(lock, result, user, type, parentId, code);
		verify(contextAccess, times(3)).persist();
	}

	@Test
	public void testCreateIndex() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long parentId = 0l;
		IndexModel model = mockIndexModel();
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(index);
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(index);
		classificationService.createIndex(lock, result, user, parentId, model, lang);
		verify(accessService, times(2)).getChangeRequestClassificationPermission(nullable(User.class), nullable(Long.class),
				nullable(ChangeRequestCategory.class));

	}

	@Test
	public void testCreateSupplement() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long parentId = 0l;
		SupplementModel model = mockSupplementModel();
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(supplement);
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(supplement);
		when(contextAccess.findAll(SupplementType.class)).thenReturn(mockSupplementTypes());
		when(supplement.getStatus()).thenReturn(ConceptStatus.ACTIVE.name());
		when(supplementType1.getCode()).thenReturn(SupplementMatter.FRONT.getCode());
		classificationService.createSupplement(lock, result, user, parentId, model, lang);
		verify(contextAccess, times(1)).findAll(SupplementType.class);
	}

	@Test
	public void testCreateTabularCciCode() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long parentId = 0l;
		Long cciApproachTechniqueId = 1L;
		Long cciDeviceId = 2L;
		Long cciTissueId = 3L;
		when(contextAccess.load(cciApproachTechniqueId)).thenReturn(cciApproachTechniqueComponent);
		when(contextAccess.load(cciDeviceId)).thenReturn(cciDeviceAgentComponent);
		when(contextAccess.load(cciTissueId)).thenReturn(cciTissueComponent);
		when(contextAccess.load(parentId)).thenReturn(cciTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCCICodeTabularConceptDetails());
		when(cciTabular.getGroupComponent()).thenReturn(cciGroupComponent);
		when(cciTabular.getInterventionComponent()).thenReturn(intervention);
		when(contextAccess.getContextId()).thenReturn(mockCCIContextIdentifier());
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(cciTabular);
		when(cciTabular.getTypeCode()).thenReturn("CCICODE");
		classificationService.createTabularCciCode(lock, result, user, parentId, cciApproachTechniqueId, cciDeviceId,
				cciTissueId);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));

	}

	@Test
	public void testCreateTabularCciGroup() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long parentId = 0l;
		long cciGroupId = 1l;
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(contextAccess.load(parentId)).thenReturn(cciTabular);
		when(cciTabular.getTypeCode()).thenReturn("Group");
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCCIGroupTabularConceptDetails());
		when(contextAccess.getContextId()).thenReturn(mockCCIContextIdentifier());
		when(contextAccess.load(cciGroupId)).thenReturn(cciGroupComponent);
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(cciTabular);
		classificationService.createTabularCciGroup(lock, result, user, parentId, cciGroupId);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));

	}

	@Test
	public void testCreateTabularCciRubric() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long parentId = 0l;
		long cciInterventionId = 1l;
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(contextAccess.load(parentId)).thenReturn(cciTabular);
		when(cciTabular.getTypeCode()).thenReturn("Rubric");
		when(cciTabular.getGroupComponent()).thenReturn(cciGroupComponent);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCCIGroupTabularConceptDetails());
		when(contextAccess.getContextId()).thenReturn(mockCCIContextIdentifier());
		when(contextAccess.load(cciInterventionId)).thenReturn(intervention);
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(cciTabular);

		classificationService.createTabularCciRubric(lock, result, user, parentId, cciInterventionId);

		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));

	}

	@Test
	public void testDeleteIndexById() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(indexMock);
		when(indexMock.getStatus()).thenReturn(ConceptStatus.ACTIVE.name());
		when(indexMock.getContainingBook()).thenReturn(bookIndex);
		when(bookIndex.getElementId()).thenReturn(1L);

		classificationService.deleteIndexById(lock, user, id, lang);

		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));
	}

	@Test
	public void testDeleteSupplementById() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(supplement);
		when(supplement.getStatus()).thenReturn(ConceptStatus.ACTIVE.name());
		classificationService.deleteSupplementById(lock, user, id, lang);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));

	}

	@Test
	public void testDeleteTabularById() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(icdTabular);
		when(icdTabular.getParent()).thenReturn(icdTabular);
		when(icdTabular.getTypeCode()).thenReturn("CODE");
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());

		classificationService.deleteTabularById(lock, user, id, lang);
		verify(contextAccess, times(1)).load(nullable(Long.class));
	}

	@Test
	public void testDeleteTabularValidationSet() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		long tabularId = 1l;
		long dataHoldingId = 2l;
		when(contextAccess.load(tabularId)).thenReturn(icdTabular);
		when(icdTabular.getParent()).thenReturn(icdTabular);
		when(icdTabular.getTypeCode()).thenReturn("CATEGORY");
		when(icdTabular.getValidations()).thenReturn(mockIcdValidations());
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(icdValidation.getFacilityType()).thenReturn(facilityType);
		when(facilityType.getElementId()).thenReturn(2L);
		classificationService.deleteTabularValidationSet(lock, user, tabularId, dataHoldingId);
		verify(contextAccess, times(1)).persist();
	}

	@Test
	public void testDtdMapped() {
		assertEquals(TabularConceptXmlType.values().length, ClassificationService.CONCEPT_XML_DTDS.length);
	}

	@Test
	public void testGetCciComponentsPerSection() {
		long sectionElementId = 1l;
		Language language = Language.ENGLISH;
		CciComponentType type = CciComponentType.GroupComp;
		when(conceptService.getCciComponentsPerSection(nullable(Long.class), nullable(Long.class), nullable(Language.class),
				nullable(CciComponentType.class))).thenReturn(mockIdCodeDescriptions());
		List<IdCodeDescription> idCodeDescriptions = classificationService.getCciComponentsPerSection(sectionElementId,
				language, type);
		assertTrue(idCodeDescriptions.size() == 1);

	}

	@Test
	public void testGetCciExtentReferences() {
		Language language = Language.ENGLISH;
		when(conceptService.getRefAttributePerType(nullable(Long.class), nullable(Language.class), nullable(AttributeType.class)))
				.thenReturn(mockIdCodeDescriptions());
		List<IdCodeDescription> idCodeDescriptions = classificationService.getCciExtentReferences(language);
		assertTrue(idCodeDescriptions.size() == 1);
	}

	@Test
	public void testGetCCIGroupContent() {
		List<CCIComponentSupplement> groupASection1Comps = new ArrayList<CCIComponentSupplement>();
		CCIComponentSupplement section1Comp1 = new CCIComponentSupplement();
		section1Comp1.setConceptCode("AA");
		section1Comp1.setDescription("Meninges and dura mater of brain");
		section1Comp1.setNote(
				"Includes: dura mater of brain, epidural space [of brain], subdural space [of brain], fossa (frontal, temporal and posterior clinoid), falx");
		groupASection1Comps.add(section1Comp1);

		CCIComponentSupplement section1Comp2 = new CCIComponentSupplement();
		section1Comp2.setConceptCode("AC");
		section1Comp2.setDescription("Ventricles of brain");
		section1Comp2.setNote(
				"Includes: aqueduct of Sylvius, cerebral ventricles [lateral, 3rd and 4th], choroid plexus of 3rd and 4th ventricles, Dandy Walker malformation of 4th ventricle, foramen of Monro, Luschka and Magendie");
		groupASection1Comps.add(section1Comp2);

		when(viewService.getCciGroupComponentsWithDefinition(Language.ENGLISH.getCode(), 1l, "1", "A"))
				.thenReturn(groupASection1Comps);
		when(viewService.getCCIGroupTitle(1l, 1l, Language.ENGLISH.getCode())).thenReturn("Brain and Spinal Cord");

		String section1Result = classificationService.getCCIGroupContent(Language.ENGLISH.getCode(), 1l, "1", "A", 1l);
		String section1ResultExpected = "<table class='conceptTable'><tr><td colspan='4'><span class='title'>(A)&nbsp;&nbsp;Brain and Spinal Cord</span></td></tr><tr><td colspan='4'>(AA)&nbsp;&nbsp;Meninges and dura mater of brain</td></tr>Includes: dura mater of brain, epidural space [of brain], subdural space [of brain], fossa (frontal, temporal and posterior clinoid), falx<tr><td height='10' colspan='4'>&nbsp;</td></tr><tr><td colspan='4'>(AC)&nbsp;&nbsp;Ventricles of brain</td></tr>Includes: aqueduct of Sylvius, cerebral ventricles [lateral, 3rd and 4th], choroid plexus of 3rd and 4th ventricles, Dandy Walker malformation of 4th ventricle, foramen of Monro, Luschka and Magendie<tr><td height='10' colspan='4'>&nbsp;</td></tr></table>";
		assertEquals(section1ResultExpected, section1Result);
	}

	@Test
	public void testGetCciInvasivenessLevels() {
		when(contextAccess.findAll(CciInvasivenessLevel.class)).thenReturn(mockCciInvasivenessLevels());
		List<CciInvasivenessLevel> cciInvasivenessLevels = classificationService.getCciInvasivenessLevels();
		assertTrue(cciInvasivenessLevels.size() == 1);
	}

	@Test
	public void testGetCciLocationReferences() {
		Language language = Language.ENGLISH;
		when(conceptService.getRefAttributePerType(nullable(Long.class), nullable(Language.class), nullable(AttributeType.class)))
				.thenReturn(mockIdCodeDescriptions());
		List<IdCodeDescription> idCodeDescriptions = classificationService.getCciLocationReferences(language);
		assertTrue(idCodeDescriptions.size() == 1);
	}

	@Test
	public void testGetCCIRubricContent() {
		List<IdCodeDescription> results1 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("AA");
		idCode.setDescription("Meninges and dura mater of brain");
		results1.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("AB");
		idCode1.setDescription("Subarachnoid");
		results1.add(idCode1);

		List<IdCodeDescription> results11 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode11 = new IdCodeDescription();
		idCode11.setCode("13");
		idCode11.setDescription("Control of bleeding");
		results11.add(idCode11);
		IdCodeDescription idCode12 = new IdCodeDescription();
		idCode12.setCode("86");
		idCode12.setDescription("Closure fistula");
		results11.add(idCode12);

		List<IdCodeDescription> results5 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode51 = new IdCodeDescription();
		idCode51.setCode("AB");
		idCode51.setDescription("Antepartum diagnostic");
		results5.add(idCode51);
		IdCodeDescription idCode52 = new IdCodeDescription();
		idCode52.setCode("AD");
		idCode52.setDescription("Antepartum supportive");
		results5.add(idCode52);

		List<IdCodeDescription> results51 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode511 = new IdCodeDescription();
		idCode511.setCode("09");
		idCode511.setDescription("Biopsy");
		results51.add(idCode511);
		IdCodeDescription idCode512 = new IdCodeDescription();
		idCode512.setCode("14");
		idCode512.setDescription("Counseling");
		results51.add(idCode512);

		when(conceptService.getCCISectionIdBySectionCode("1", 1l)).thenReturn(1l);
		when(conceptService.getCCISectionIdBySectionCode("5", 1l)).thenReturn(5l);
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", "A")).thenReturn(results1);
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.Intervention,
				"description", null)).thenReturn(results11);
		List<CCIRubric> section1GroupARubrics = new ArrayList<CCIRubric>();
		CCIRubric section1GA1 = new CCIRubric();
		section1GA1.setGroupCode("AA");
		section1GA1.setInterventionCode("13");
		section1GA1.setRubricCode("1AA13");
		section1GA1.setContainingPath("/123/2456/555");
		section1GroupARubrics.add(section1GA1);
		CCIRubric section1GA2 = new CCIRubric();
		section1GA2.setGroupCode("AB");
		section1GA2.setInterventionCode("86");
		section1GA2.setRubricCode("1AB86");
		section1GA2.setContainingPath("/33/56/658");
		section1GroupARubrics.add(section1GA2);

		when(viewService.findCCIRubric(1l, "1", "A")).thenReturn(section1GroupARubrics);
		when(conceptService.getCciComponentsPerSectionLongTitle(5l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(results5);
		when(conceptService.getCciComponentsPerSectionLongTitle(5l, 1l, Language.ENGLISH, CciComponentType.Intervention,
				"description", null)).thenReturn(results51);
		List<CCIRubric> section5GroupARubrics = new ArrayList<CCIRubric>();
		CCIRubric section5GA1 = new CCIRubric();
		section5GA1.setGroupCode("AB");
		section5GA1.setInterventionCode("09");
		section5GA1.setRubricCode("5AB09");
		section5GA1.setContainingPath("/123/2456/555");
		section5GroupARubrics.add(section5GA1);
		CCIRubric section5GA2 = new CCIRubric();
		section5GA2.setGroupCode("AD");
		section5GA2.setInterventionCode("14");
		section5GA2.setRubricCode("5AD14");
		section5GA2.setContainingPath("/33/56/658");
		section5GroupARubrics.add(section5GA2);

		when(viewService.findCCIRubric(1l, "5", null)).thenReturn(section5GroupARubrics);
		when(viewService.getCCIGroupTitle(1l, 1l, Language.ENGLISH.getCode())).thenReturn("Brain and Spinal Cord");
		when(viewService.getUserTitle(5l, 1l, Language.ENGLISH.getCode()))
				.thenReturn("Obstetrical and Fetal Interventions");

		String section1Result = classificationService.getCCIRubricContent(Language.ENGLISH.getCode(), 1l, "1", "A", 1l);
		String section1ResultExpected = "<table class='conceptTable'><tr><td colspan='4'><span class='title'>(A) Brain and Spinal Cord</span></td></tr></table><div id='sticker'><table style='width:auto'><tr><th style='min-width: 240px;width:240px'>AA - Meninges and dura mater of brain</th><th style='min-width: 240px;width:240px'>AB - Subarachnoid</th><th style='min-width:82px;width:82px;'></th><th style='min-width:82px;width:82px;'></th></tr><tr><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width:82px;width:82px; text-align:center;'>AA</th><th style='border: 1px solid black;min-width:82px;width:82px; text-align:center;'>AB</th></tr></table></div><div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 240px;width:240px'>Control of bleeding</td><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:center;'>(13)</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'><a href=\"javascript:navigateFromDynaTree('/123/2456/555');\">1AA13</a></td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'>&nbsp;</td></tr><tr><td style='border: 1px solid black;min-width: 240px;width:240px'>Closure fistula</td><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:center;'>(86)</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'>&nbsp;</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'><a href=\"javascript:navigateFromDynaTree('/33/56/658');\">1AB86</a></td></tr></table></div>";
		assertEquals(section1ResultExpected, section1Result);

		String section5Result = classificationService.getCCIRubricContent(Language.ENGLISH.getCode(), 1l, "5", null,
				null);
		String section5ResultExpected = "<table class='conceptTable'><tr><td colspan='4'><span class='title'>Obstetrical and Fetal Interventions</span></td></tr></table><div id='sticker'><table style='width:auto'><tr><th style='min-width: 240px;width:240px'>AB - Antepartum diagnostic</th><th style='min-width: 240px;width:240px'>AD - Antepartum supportive</th><th style='min-width:82px;width:82px;'></th><th style='min-width:82px;width:82px;'></th></tr><tr><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width:82px;width:82px; text-align:center;'>AB</th><th style='border: 1px solid black;min-width:82px;width:82px; text-align:center;'>AD</th></tr></table></div><div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 240px;width:240px'>Biopsy</td><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:center;'>(09)</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'><a href=\"javascript:navigateFromDynaTree('/123/2456/555');\">5AB09</a></td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'>&nbsp;</td></tr><tr><td style='border: 1px solid black;min-width: 240px;width:240px'>Counseling</td><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:center;'>(14)</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'>&nbsp;</td><td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'><a href=\"javascript:navigateFromDynaTree('/33/56/658');\">5AD14</a></td></tr></table></div>";
		assertEquals(section5ResultExpected, section5Result);
	}

	@Test
	public void testGetCCIRubricContent8() {

		List<CCIRubric> section8GroupARubrics = new ArrayList<CCIRubric>();
		CCIRubric section8GA1 = new CCIRubric();
		section8GA1.setGroupCode("AA");
		section8GA1.setInterventionCode("70");
		section8GA1.setRubricCode("8AA70");
		section8GA1.setContainingPath("/123/2456/555");
		section8GroupARubrics.add(section8GA1);
		CCIRubric section8GA2 = new CCIRubric();
		section8GA2.setGroupCode("PF");
		section8GA2.setInterventionCode("10");
		section8GA2.setRubricCode("8PF10");
		section8GA2.setContainingPath("/33/56/658");
		section8GroupARubrics.add(section8GA2);

		List<IdCodeDescription> results8 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode81 = new IdCodeDescription();
		idCode81.setCode("AA");
		idCode81.setDescription("Adenovirus");
		results8.add(idCode81);
		IdCodeDescription idCode82 = new IdCodeDescription();
		idCode82.setCode("PF");
		idCode82.setDescription("Fall eastern pollen (allergy)");
		results8.add(idCode82);

		List<IdCodeDescription> results81 = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode811 = new IdCodeDescription();
		idCode811.setCode("70");
		idCode811.setDescription("Immunization (to prevent)");
		results81.add(idCode811);
		IdCodeDescription idCode812 = new IdCodeDescription();
		idCode812.setCode("10");
		idCode812.setDescription("Essence dilution (to strengthen against)");
		results81.add(idCode812);

		when(conceptService.getCCISectionIdBySectionCode("8", 1l)).thenReturn(8l);
		when(conceptService.getCciComponentsPerSectionLongTitle(8l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(results8);
		when(conceptService.getCciComponentsPerSectionLongTitle(8l, 1l, Language.ENGLISH, CciComponentType.Intervention,
				"description", null)).thenReturn(results81);

		when(viewService.findCCIRubric(1l, "8", null)).thenReturn(section8GroupARubrics);
		when(viewService.getUserTitle(8l, 1l, Language.ENGLISH.getCode()))
				.thenReturn("Therapeutic Interventions Strengthening the Immune System and/or Genetic Composition");

		String section8Result = classificationService.getCCIRubricContent(Language.ENGLISH.getCode(), 1l, "8", null,
				null);
		String section8ResultExpected = "<table class='conceptTable'><tr><td colspan='4'><span class='title'>Therapeutic Interventions Strengthening the Immune System and/or Genetic Composition</span></td></tr></table><div id='sticker'><table style='width:auto'><tr><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width:150px;width:150px; text-align:center;'>Immunization (to prevent)<br/>(70)</th><th style='border: 1px solid black;min-width:150px;width:150px; text-align:center;'>Essence dilution (to strengthen against)<br/>(10)</th></tr></table></div><div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:left'>AA - Adenovirus</td><td style='border: 1px solid black;min-width:150px;width:150px;text-align:center'><a href=\"javascript:navigateFromDynaTree('/123/2456/555');\">8AA70</a></td><td style='border: 1px solid black;min-width:150px;width:150px;text-align:center'>&nbsp;</td></tr><tr><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:left'>PF - Fall eastern pollen (allergy)</td><td style='border: 1px solid black;min-width:150px;width:150px;text-align:center'>&nbsp;</td><td style='border: 1px solid black;min-width:150px;width:150px;text-align:center'><a href=\"javascript:navigateFromDynaTree('/33/56/658');\">8PF10</a></td></tr></table></div>";
		assertEquals(section8ResultExpected, section8Result);
	}

	@Test
	public void testGetCciValidationSets() {
		long tabularId = 1l;
		Language lang = Language.ENGLISH;
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(contextAccess.load(tabularId)).thenReturn(cciTabular);
		when(cciTabular.getValidations()).thenReturn(mockCciValidations());
		when(conceptService.getRefAttributePerType(nullable(Long.class), nullable(Language.class), nullable(AttributeType.class)))
				.thenReturn(mockIdCodeDescriptions());
		when(conceptService.getRefAttributePerType(nullable(Long.class), nullable(Language.class), nullable(AttributeType.class)))
				.thenReturn(mockIdCodeDescriptions());
		when(cciValidation.isActive()).thenReturn(true);
		when(cciValidation.getValidationDefinition())
				.thenReturn("<validation><STATUS_REF>statusRef</STATUS_REF></validation>");
		when(cciValidation.getFacilityType()).thenReturn(facilityType);
		when(facilityType.getDescription(nullable(String.class))).thenReturn("dataHolding");
		List<TabularConceptCciValidationSetReportModel> cciValidationSetReportModels = classificationService
				.getCciValidationSets(tabularId, lang);

		assertTrue(cciValidationSetReportModels.size() == 1);
	}

	@Test
	public void testGetChangeRequestClassificationPermission() {
		User user = mockCurrentUser();
		ChangeRequestCategory category = ChangeRequestCategory.T;
		when(accessService.getChangeRequestClassificationPermission(nullable(User.class), nullable(Long.class),
				nullable(ChangeRequestCategory.class))).thenReturn(mockChangeRequestPermission());
		ChangeRequestPermission changeRequestPermission = classificationService
				.getChangeRequestClassificationPermission(user, category);
		assertTrue(changeRequestPermission != null);
	}

	@Test
	public void testGetConceptInfoPermission() {
		User user = mockCurrentUser();
		ChangeRequestCategory category = ChangeRequestCategory.T;
		when(accessService.getChangeRequestClassificationPermission(nullable(User.class), nullable(Long.class),
				nullable(ChangeRequestCategory.class))).thenReturn(mockChangeRequestPermission());
		ChangeRequestPermission changeRequestPermission = classificationService.getConceptInfoPermission(user,
				category);
		assertTrue(changeRequestPermission != null);
	}

	@Test
	public void testGetConceptNonInfoPermission() {
		User user = mockCurrentUser();
		when(accessService.getChangeRequestClassificationPermission(nullable(User.class), nullable(Long.class),
				nullable(ChangeRequestCategory.class))).thenReturn(mockChangeRequestPermission());
		ChangeRequestPermission changeRequestPermission = classificationService.getConceptNonInfoPermission(user);
		assertTrue(changeRequestPermission != null);
	}

	@Test
	public void testGetContainedPageId() {
		long elemenId = 1l;
		when(contextAccess.determineContainingPage(nullable(Long.class))).thenReturn(2L);
		long containedPageId = classificationService.getContainedPageId(elemenId);
		assertTrue(containedPageId == 2l);

	}

	@Test
	public void testGetCurrentChangeRequestId() {
		Long currentChangeRequestId = classificationService.getCurrentChangeRequestId();
		assertTrue(currentChangeRequestId == 1l);
	}

	@Test
	public void testGetCurrentChangeRequestYear() {
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		int currentChangeRequestYear = classificationService.getCurrentChangeRequestYear();
		assertTrue(currentChangeRequestYear > 0);
	}

	@Test
	public void testGetCurrentClassification() {
		Classification currentClassification = classificationService.getCurrentClassification();
		assertTrue(currentClassification == Classification.ICD);
	}

	@Test
	public void testGetCurrentContextId() {
		long currentContextId = classificationService.getCurrentContextId();
		assertTrue(currentContextId == 1l);
	}

	@Test
	public void testGetCurrentVersionCode() {
		String currentVersionCode = classificationService.getCurrentVersionCode();
		assertTrue(CIMSTestConstants.TEST_VERSION.equalsIgnoreCase(currentVersionCode));
	}

	@Test
	public void testGetDaggerAsteriskTypes() {
		Ref<DaggerAsterisk> da = ref(DaggerAsterisk.class);
		when(contextAccess.findList(nullable(Ref.class))).thenReturn(mockDaggerAsterisks());
		List<DaggerAsterisk> daggerAsterisks = classificationService.getDaggerAsteriskTypes();

		assertTrue(daggerAsterisks.size() == 1);
	}

	@Test
	public void testGetDataHoldings() {
		Language lang = Language.ENGLISH;
		Ref<FacilityType> ft = ref(FacilityType.class);
		when(contextAccess.findList(nullable(Ref.class))).thenReturn(mockFacilities());
		List<TabularConceptValidationDadHoldingModel> validationDadHoldingModels = classificationService
				.getDataHoldings(lang);

		assertTrue(validationDadHoldingModels.size() == 1);
	}

	@Test
	public void testGetGenderByCode() {
		String code = "M";
		Ref<SexValidation> sex = ref(SexValidation.class);
		when(contextAccess.findOne(nullable(Ref.class), nullable(WrapperPropertyCriterion.class))).thenReturn(sexValidation);
		SexValidation sexValidation = classificationService.getGenderByCode(code);
		assertTrue(sexValidation != null);
	}

	@Test
	public void testGetGenders() {
		Language lang = Language.ENGLISH;
		when(contextAccess.findList(nullable(Ref.class))).thenReturn(mockSexValidations());
		when(sexValidation.getCode()).thenReturn("M");
		when(sexValidation.getElementId()).thenReturn(1L);
		when(sexValidation.getDefinition(nullable(String.class))).thenReturn("Male");
		List<TabularConceptValidationGenderModel> genderModels = classificationService.getGenders(lang);
		assertTrue(genderModels.size() == 1);
	}

	@Test
	public void testGetIcdDxType() {
		long dxTypeId = 1l;
		Language lang = Language.ENGLISH;
		DxType dxType = classificationService.getIcdDxType(dxTypeId, lang);
		assertTrue(dxType.getId() == 1l);
	}

	@Test
	public void testGetIcdDxTypes() {
		Language lang = Language.ENGLISH;
		List<DxType> dxTypes = classificationService.getIcdDxTypes(lang);
		assertTrue(dxTypes.size() > 0);
	}

	@Test
	public void testGetIcdValidationSets() {
		long tabularId = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(tabularId)).thenReturn(icdTabular);
		when(icdTabular.getValidations()).thenReturn(mockIcdValidations());
		when(icdTabular.getTypeCode()).thenReturn("CATEGORY");
		when(icdValidation.isActive()).thenReturn(true);
		when(icdValidation.getValidationDefinition()).thenReturn(
				"<validation><classification>ICD</classification><MRDX_MAIN>test</MRDX_MAIN><AGE_RANGE>10-100</AGE_RANGE></validation>");
		when(icdValidation.getFacilityType()).thenReturn(facilityType);
		List<TabularConceptIcdValidationSetReportModel> models = classificationService.getIcdValidationSets(tabularId,
				lang);
		assertTrue(models.size() == 1);
	}

	@Test
	public void testGetIndexById() {
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(id)).thenReturn(bookIndex);

		when(bookIndex.getStatus()).thenReturn(ConceptStatus.ACTIVE.toString());
		when(bookIndex.getContainingBook()).thenReturn(bookIndex);
		IndexModel indexModel = classificationService.getIndexById(id, lang);
		assertTrue(indexModel != null);
	}

	@Test
	public void testGetIndexSiteIndicators() {
		Language lang = Language.ENGLISH;
		List<SiteIndicatorModel> siteIndicatorModels = classificationService.getIndexSiteIndicators(lang);
		assertTrue(siteIndicatorModels.size() == 1);
	}

	@Test
	public void testGetNodeTitle() {
		long elementId = 1l;
		String language = Language.ENGLISH.getCode();
		when(viewService.getTitleForNode(nullable(String.class), nullable(String.class), nullable(Long.class), nullable(String.class))).thenReturn("nodeTitle");
		String nodeTitle = classificationService.getNodeTitle(elementId, language);
		assertTrue("nodeTitle".equalsIgnoreCase(nodeTitle));
	}

	@Test
	public void testGetSupplementById() {
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(id)).thenReturn(supplement);
		when(supplement.getStatus()).thenReturn(ConceptStatus.ACTIVE.toString());
		SupplementModel supplementModel = classificationService.getSupplementById(id, lang);
		assertTrue(supplementModel != null);
	}

	@Test
	public void testGetTabularById() {
		Long id = 1L;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		TabularConcept tabular = classificationService.getTabularById(id);
		assertTrue(tabular != null);

	}

	@Test
	public void testGetTabularByIdAndLoadParent() {
		long id = 1l;
		boolean loadParent = true;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		classificationService.getTabularById(id, loadParent);
	}

	@Test
	public void testGetTabularbyIdNotNull() {
		Long id = 1L;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		TabularConcept tabular = classificationService.getTabularbyIdNotNull(id);
		assertTrue(tabular != null);
	}

	@Test
	public void testGetTabularConceptById() {
		long id = 1l;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		TabularConceptModel conceptModel = classificationService.getTabularConceptById(id);
		assertTrue(conceptModel != null);
	}

	@Test
	public void testGetTabularConceptByIdAndNotLoadParent() {
		long id = 1l;
		boolean loadParent = false;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		TabularConceptModel conceptModel = classificationService.getTabularConceptById(id, loadParent);
		assertTrue(conceptModel != null);
	}

	@Test
	public void testGetTabularConceptLightById() {
		long elementId = 1l;
		when(contextAccess.load(elementId)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		TabularConceptModel lightModel = classificationService.getTabularConceptLightById(elementId);
		assertTrue(lightModel != null);
	}

	@Test
	public void testGetTabularDiagramContent() {
		long id = 1l;
		Language lang = Language.ENGLISH;
		when(contextAccess.load(id)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		byte[] bytes = classificationService.getTabularDiagramContent(id, lang);
		assertTrue(bytes == null);
	}

	@Test
	public void testGetTabularValidationSetForCCI() {
		long elementId = 1l;
		long dataHoldingId = 2l;
		when(contextAccess.load(elementId)).thenReturn(cciTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCCIGroupTabularConceptDetails());
		when(cciTabular.getValidations()).thenReturn(mockCciValidations());
		when(cciValidation.getFacilityType()).thenReturn(facilityType);
		when(cciValidation.isActive()).thenReturn(true);
		when(cciValidation.getValidationDefinition()).thenReturn(
				"<validation><STATUS_REF>statusRef</STATUS_REF><LOCATION_REF>L10</LOCATION_REF><AGE_RANGE>10-100</AGE_RANGE></validation>");
		when(facilityType.getElementId()).thenReturn(2l);
		TabularConceptValidationSetModel cciConceptModel = classificationService.getTabularValidationSet(elementId,
				dataHoldingId);
		assertTrue(cciConceptModel != null);
	}

	@Test
	public void testGetTabularValidationSetForICD() {
		long elementId = 1l;
		long dataHoldingId = 2l;
		when(contextAccess.load(elementId)).thenReturn(icdTabular);
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		when(icdTabular.getValidations()).thenReturn(mockIcdValidations());
		when(icdValidation.getFacilityType()).thenReturn(facilityType);
		when(icdValidation.isActive()).thenReturn(true);
		when(icdValidation.getValidationDefinition()).thenReturn(
				"<validation><classification>ICD</classification><MRDX_MAIN>test</MRDX_MAIN><AGE_RANGE>10-100</AGE_RANGE></validation>");
		when(facilityType.getElementId()).thenReturn(2l);
		TabularConceptValidationSetModel icdConceptModel = classificationService.getTabularValidationSet(elementId,
				dataHoldingId);
		assertTrue(icdConceptModel != null);
	}

	@Test
	public void testGetTabularXml() {
		long elementId = 1l;
		TabularConceptXmlType type = TabularConceptXmlType.INCLUDE;
		when(contextAccess.load(elementId)).thenReturn(icdTabular);
		when(icdTabular.getParent()).thenReturn(icdTabular);
		when(icdTabular.getTypeCode()).thenReturn("CATEGORY");
		TabularConceptXmlModel tabularConceptXmlModel = classificationService.getTabularXml(elementId, type);
		assertTrue(tabularConceptXmlModel != null);

	}

	@Test
	public void testHasChildren() {
		long conceptId = 1l;
		Language lang = Language.ENGLISH;

		when(viewService.getTreeNodes(nullable(String.class), nullable(String.class), nullable(Long.class), nullable(String.class), nullable(String.class)))
				.thenReturn(mockContentViewerModels());
		boolean hasChildren = classificationService.hasChildren(conceptId, lang);
		assertTrue(hasChildren);
	}

	@Test
	public void testIsAddedInCurrentVersionYear() {
		IndexModel model = mockIndexModel();
		when(contextOperations.hasConceptBeenPublished(nullable(Long.class))).thenReturn(false);
		boolean isAdded = classificationService.isAddedInCurrentVersionYear(model);
		assertTrue(isAdded);
	}

	@Test
	public void testIsAddQualifierEnabled() {
		TabularConceptType type = TabularConceptType.CCI_GROUP;
		boolean isEnabled = classificationService.isAddQualifierEnabled(type);
		assertTrue(isEnabled);
	}

	@Test
	public void testIsCanadianEnhancementEditable() {
		TabularConceptModel model = mockTabularConceptModel();
		boolean isEditable = classificationService.isCanadianEnhancementEditable(model);
		assertTrue(!isEditable);
	}

	@Test
	public void testIsDaggerAsteriskEditable() {
		TabularConceptModel model = mockTabularConceptModel();
		boolean isEditable = classificationService.isDaggerAsteriskEditable(model);
		assertTrue(!isEditable);
	}

	@Test
	public void testIsIndexDeletableShallow() {
		IndexModel model = mockIndexModel();
		boolean isDeletable = classificationService.isIndexDeletableShallow(model);
		assertTrue(isDeletable);
	}

	@Test
	public void testIsIndexStatusEditable() {
		IndexModel model = mockIndexModel();
		boolean isEditable = classificationService.isIndexStatusEditable(model);
		assertTrue(!isEditable);
	}

	@Test
	public void testIsSupplementAddedInCurrentVersionYear() {
		SupplementModel model = mockSupplementModel();
		when(contextOperations.hasConceptBeenPublished(nullable(Long.class))).thenReturn(false);
		boolean isAdded = classificationService.isAddedInCurrentVersionYear(model);
		assertTrue(isAdded);
	}

	@Test
	public void testIsSupplementDeletableShallow() {
		SupplementModel model = mockSupplementModel();
		boolean isDeletable = classificationService.isSupplementDeletableShallow(model);
		assertTrue(isDeletable);
	}

	@Test
	public void testIsTabularCodeEditable() {
		TabularConceptModel model = mockTabularConceptModel();
		boolean isEditable = classificationService.isTabularCodeEditable(model);
		assertTrue(!isEditable);
	}

	@Test
	public void testIsTabularStatusEditable() {
		TabularConceptModel model = mockTabularConceptModel();
		boolean isEditable = classificationService.isTabularStatusEditable(model);
		assertTrue(!isEditable);
	}

	@Test
	public void testIsUserTitleEditable() {
		TabularConceptModel model = mockTabularConceptModel();
		boolean isEditable = classificationService.isUserTitleEditable(model);
		assertTrue(isEditable);
	}

	@Test
	public void testSaveIndex() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		IndexModel model = mockIndexModelWithReferences();
		Language lang = Language.ENGLISH;
		when(contextAccess.load(nullable(Long.class))).thenReturn(icdTabular);
		classificationService.saveIndex(lock, result, user, model, lang);
	}

	@Test
	public void testSaveTabular() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		TabularConceptModel model = mockICDBlockTabularConceptModel();
		when(viewService.getTabularConceptDetails(nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockICDBlockTabularConceptDetails());
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(contextAccess.load(nullable(Long.class))).thenReturn(icdTabular);
		classificationService.saveTabular(lock, result, user, model);
		verify(contextAccess, times(1)).load(nullable(Long.class));
	}

	@Test
	public void testSaveTabularValidationSet() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		long tabularId = 1l;
		TabularConceptIcdValidationSetModel model = mockTabularConceptIcdValidationSetModel();
		List<Long> extendToOtherDataHoldings = new ArrayList<Long>();
		extendToOtherDataHoldings.add(2l);
		when(contextAccess.load(tabularId)).thenReturn(icdTabular);
		when(contextAccess.load(2l)).thenReturn(facilityType);
		when(contextAccess.createWrapper(nullable(Class.class), nullable(String.class), nullable(String.class))).thenReturn(icdValidation);
		when(contextAccess.findOne(nullable(Ref.class), nullable(EqCriterion.class))).thenReturn(sexValidation);
		when(icdTabular.getTypeCode()).thenReturn("CATEGORY");
		when(icdTabular.getNestingLevel()).thenReturn(1);
		classificationService.saveTabularValidationSet(lock, result, user, tabularId, model, extendToOtherDataHoldings);
		verify(contextAccess, times(3)).load(nullable(Long.class));
	}

	@Test
	public void testSaveTabularXml() {
		OptimisticLock lock = mockOptimisticLock();
		ErrorBuilder result = mockErrorBuilder();
		User user = mockCurrentUser();
		TabularConceptXmlModel model = mockTabularConceptXmlModel();
		when(contextAccess.load(nullable(Long.class))).thenReturn(icdTabular);
		when(icdTabular.getCode()).thenReturn("code");
		when(icdTabular.getTypeCode()).thenReturn("CATEGORY");
		classificationService.saveTabularXml(lock, result, user, model);
		verify(contextAccess, times(1)).load(nullable(Long.class));
	}

	@Test
	public void testTansformSupplement() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		Supplement concept = supplement;
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		doNothing().when(transformSupplementService).transformSupplement(nullable(String.class), nullable(String.class),
				nullable(Supplement.class), nullable(ContextAccess.class), nullable(Boolean.class));
		classificationService.transformSupplement(lock, user, concept);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));

	}

	@Test
	public void testTransformConcept() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		TabularConcept concept = icdTabular;
		doNothing().when(conceptTransformationService).transformConcept(nullable(TabularConcept.class),
				nullable(ContextAccess.class), nullable(Boolean.class));
		classificationService.transformConcept(lock, user, concept);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));
	}

	@Test
	public void testTransformIndex() {
		OptimisticLock lock = mockOptimisticLock();
		User user = mockCurrentUser();
		Index concept = bookIndex;
		doNothing().when(indexTransformationService).transformIndexConcept(nullable(Index.class), nullable(String.class),
				nullable(ContextAccess.class), nullable(Boolean.class));
		when(changeRequestService.findLightWeightChangeRequestById(nullable(Long.class))).thenReturn(mockChangeRequest());
		classificationService.transformIndex(lock, user, concept);
		verify(changeRequestService, times(1)).updateChangeRequestLastUpdateTime(nullable(Long.class), nullable(User.class),
				nullable(OptimisticLock.class));
	}
}
