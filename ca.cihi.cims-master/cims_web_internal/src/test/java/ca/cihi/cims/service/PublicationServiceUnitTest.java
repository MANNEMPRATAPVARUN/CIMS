package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.CodeDescriptionPublication;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.prodpub.FileGenerator;
import ca.cihi.cims.service.prodpub.FileGeneratorFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class PublicationServiceUnitTest {

	private PublicationServiceImpl publicationService;

	@Mock
	private PublicationMapper publicationMapper;
	@Autowired
	private LookupMapper lookupMapper;
	@Mock
	private ChangeRequestService changeRequestService;
	@Mock
	private NotificationService notificationService;
	@Mock
	private FileGeneratorFactory fileGeneratorFactory;
	@Mock
	private FileGenerator fileGenerator;
	@Mock
	private ContextService contextService;
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private MessageSource ntfMessageSource;
	@Mock
	private EmailService emailService;
	@Mock
	private ContextProvider contextProvider;
	@Mock
	private ContextAccess contextAccess;

	private final String currentProcessingYear = CIMSTestConstants.TEST_VERSION;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	private static final HashMap<String, String> processingFileNameMap = new HashMap<String, String>();

	// -----------------------------------------------------------------------

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		publicationService = new PublicationServiceImpl();
		publicationService.setChangeRequestService(changeRequestService);
		publicationService.setPublicationMapper(publicationMapper);
		publicationService.setContextService(contextService);
		publicationService.setCurrentProcessingYear("2016");
		publicationService.setEmailService(emailService);
		publicationService.setFileGeneratorFactory(fileGeneratorFactory);
		publicationService.setLookupMapper(lookupMapper);
		publicationService.setContextProvider(contextProvider);
		publicationService.setNotificationService(notificationService);
		publicationService.setVelocityEngine(velocityEngine);
		publicationService.setMessageSource(ntfMessageSource);
		publicationService.setCurrentProcessingYear(currentProcessingYear);
		publicationService.setPubDirectory(pubDirectory);

	}

	private ContextIdentifier mockCCIContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setBaseClassification("CCI");
		contextIdentifier.setContextId(0L);
		contextIdentifier.setFreezingStatus(FreezingStatus.TAB);
		return contextIdentifier;
	}

	private PublicationSnapShot mockCCIPublicationSnapShot() {
		PublicationSnapShot publicationSnapShot = new PublicationSnapShot();
		publicationSnapShot.setSnapShotId(0L);
		publicationSnapShot.setContextIdentifier(mockCCIContextIdentifier());
		return publicationSnapShot;
	}

	private List<CodeDescriptionPublication> mockCodeDescriptionPublications() {
		List<CodeDescriptionPublication> cdps = new ArrayList<CodeDescriptionPublication>();
		CodeDescriptionPublication cdp1 = new CodeDescriptionPublication();
		cdp1.setCode("A00");
		cdp1.setShortTitle("A00 short title");
		cdp1.setLongTitle("A00 long title");
		CodeDescriptionPublication cdp2 = new CodeDescriptionPublication();
		cdp2.setCode("A01");
		cdp2.setShortTitle("A01 short title");
		cdp2.setLongTitle("A01 long title");
		cdps.add(cdp1);
		cdps.add(cdp2);
		return cdps;
	}

	private ComponentAndAttributeElementModel mockComponentAndAttributeElementModel() {
		ComponentAndAttributeElementModel model = new ComponentAndAttributeElementModel();
		model.setCode("A00");
		model.setSection("01");
		model.setElementUUID("CCI:ConceptVersion:GroupComp:Z2__1");

		return model;
	}

	private List<ComponentAndAttributeElementModel> mockComponentAndAttributeElementModels() {
		List<ComponentAndAttributeElementModel> models = new ArrayList<ComponentAndAttributeElementModel>();

		ComponentAndAttributeElementModel model1 = new ComponentAndAttributeElementModel();
		model1.setCode("A00");
		model1.setSection("01");
		model1.setElementUUID("CCI:ConceptVersion:GroupComp:Z2__1");
		ComponentAndAttributeElementModel model2 = new ComponentAndAttributeElementModel();
		model2.setCode("A01");
		model2.setSection("01");
		model2.setElementUUID("CCI:ConceptVersion:GroupComp:Z2__1");
		models.add(model1);
		models.add(model2);
		return models;
	}

	private ContextAccess mockContextAccess() {

		return contextAccess;
	}

	private User mockCurrentUser() {
		User currentUser = new User();
		currentUser.setUserId(0L);
		return currentUser;
	}

	private FileGenerator mockFileGenerator() {
		return fileGenerator;
	}

	private ContextIdentifier mockICDContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setBaseClassification("ICD-10-CA");
		contextIdentifier.setContextId(0L);
		contextIdentifier.setFreezingStatus(FreezingStatus.TAB);
		return contextIdentifier;
	}

	private PublicationSnapShot mockICDPublicationSnapShot() {
		PublicationSnapShot publicationSnapShot = new PublicationSnapShot();
		publicationSnapShot.setSnapShotId(0L);
		publicationSnapShot.setContextIdentifier(mockICDContextIdentifier());
		return publicationSnapShot;
	}

	private PublicationRelease mockPublicationRelease() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(0L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.PRELIMINARY_INTERNAL_QA);
		publicationRelease.setCreatedDate(Calendar.getInstance().getTime());
		return publicationRelease;
	}

	private List<PublicationRelease> mockPublicationReleases() {
		List<PublicationRelease> allReleases = new ArrayList<PublicationRelease>();
		PublicationRelease publicationRelease1 = new PublicationRelease();
		publicationRelease1.setReleaseId(0L);
		PublicationRelease publicationRelease2 = new PublicationRelease();
		publicationRelease2.setReleaseId(1L);
		allReleases.add(publicationRelease1);
		allReleases.add(publicationRelease2);
		return allReleases;

	}

	private PublicationSnapShot mockPublicationSnapShot() {
		PublicationSnapShot publicationSnapShot = new PublicationSnapShot();

		publicationSnapShot.setSnapShotId(0L);
		publicationSnapShot.setCreatedDate(Calendar.getInstance().getTime());
		publicationSnapShot.setFileFormat(FileFormat.TAB);
		return publicationSnapShot;
	}

	private List<PublicationSnapShot> mockPublicationSnapShots() {

		List<PublicationSnapShot> allSnapShots = new ArrayList<PublicationSnapShot>();
		PublicationSnapShot publicationSnapShot1 = new PublicationSnapShot();
		publicationSnapShot1.setSnapShotId(0L);
		PublicationSnapShot publicationSnapShot2 = new PublicationSnapShot();
		publicationSnapShot1.setSnapShotId(1L);
		allSnapShots.add(publicationSnapShot1);
		allSnapShots.add(publicationSnapShot2);

		return allSnapShots;
	}

	@Test
	public void testAreBothClassificationFixedWidthFilesGenerated() {
		GenerateReleaseTablesCriteria releaseTablesModel = new GenerateReleaseTablesCriteria();
		releaseTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		when(publicationMapper.findLatestSuccessFixedWidthSnapShotByContextId(nullable(Long.class))).thenReturn(null);
		boolean generated = publicationService.areBothClassificationTabFilesGenerated(releaseTablesModel);
		assertTrue(!generated);

	}

	@Test
	public void testAreBothClassificationTabFilesGenerated() {
		GenerateReleaseTablesCriteria releaseTablesModel = new GenerateReleaseTablesCriteria();
		releaseTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		when(publicationMapper.findLatestSuccessTabSnapShotByContextId(nullable(Long.class))).thenReturn(null);
		boolean generated = publicationService.areBothClassificationTabFilesGenerated(releaseTablesModel);

		assertTrue(!generated);

	}

	@Test
	public void testCloseYear() {
		Long currentOpenYear = Long.valueOf(CIMSTestConstants.TEST_VERSION);
		User currentUser = mockCurrentUser();
		String versionCode = String.valueOf(currentOpenYear);
		when(contextProvider.findContext(nullable(ContextDefinition.class))).thenReturn(mockContextAccess());
		when(contextProvider.createContext(nullable(ContextIdentifier.class), nullable(Boolean.class))).thenReturn(mockContextAccess());
		when(contextAccess.getContextId()).thenReturn(mockICDContextIdentifier());
		doNothing().when(contextService).blockUnfreeze(nullable(Long.class));
		doNothing().when(contextAccess).closeContext();
		doNothing().when(changeRequestService).publishAllChangeRequestsForYear(versionCode, currentUser);
		doNothing().when(notificationService).removeAllNotificationsForYear(versionCode);

		publicationService.closeYear(currentOpenYear, currentUser);

		verify(contextService, times(2)).blockUnfreeze(nullable(Long.class));

	}

	@Test
	public void testCreatePublicationRelease() {
		PublicationRelease publicationRelease = mockPublicationRelease();
		when(publicationMapper.insertPublicationRelease(nullable(PublicationRelease.class))).thenReturn(1);
		publicationService.createPublicationRelease(publicationRelease);
		verify(publicationMapper, times(1)).insertPublicationRelease(nullable(PublicationRelease.class));
	}

	@Test
	public void testCreatePublicationSnapShot() {
		PublicationSnapShot publicationSnapShot = mockPublicationSnapShot();
		when(publicationMapper.insertPublicationSnapShot(nullable(PublicationSnapShot.class))).thenReturn(1);
		publicationService.createPublicationSnapShot(publicationSnapShot);
		verify(publicationMapper, times(1)).insertPublicationSnapShot(nullable(PublicationSnapShot.class));
	}

	@Test
	public void testFindAllLatestSnapShots() {

		// when(notificationMapper.findNotificationByPrimaryKey(nullable(Long.class))).thenReturn(mockNotificationDTO());
		when(publicationMapper.findAllLatestSnapShots()).thenReturn(mockPublicationSnapShots());
		List<PublicationSnapShot> allSnapShots = publicationService.findAllLatestSnapShots();
		assertTrue(allSnapShots.size() == 2);
	}

	@Test
	public void testFindAllReleases() {
		when(publicationMapper.findAllReleases()).thenReturn(mockPublicationReleases());
		List<PublicationRelease> allReleases = publicationService.findAllReleases();
		assertTrue(allReleases.size() == 2);
	}

	@Test
	public void testFindAllSnapShotsByContextId() {
		when(publicationMapper.findAllSnapShotsByContextId(nullable(Long.class))).thenReturn(mockPublicationSnapShots());
		List<PublicationSnapShot> allSnapShots = publicationService.findAllSnapShotsByContextId(0L);
		assertTrue(allSnapShots.size() == 2);
	}

	@Test
	public void testFindAllSuccessLatestSnapShots() {
		when(publicationMapper.findAllSuccessLatestSnapShots()).thenReturn(mockPublicationSnapShots());
		List<PublicationSnapShot> allSnapShots = publicationService.findAllSuccessLatestSnapShots();
		assertTrue(allSnapShots.size() == 2);
	}

	@Test
	public void testFindLatestHighestSuccessPublicationReleaseByFiscalYear() {
		when(publicationMapper.findAllSuccessDescentOrderPublicationReleasesByFiscalYear(nullable(String.class))).thenReturn(
				mockPublicationReleases());
		PublicationRelease publicationRelease = publicationService
				.findLatestHighestSuccessPublicationReleaseByFiscalYear(CIMSTestConstants.TEST_VERSION);
		assertTrue(publicationRelease.getReleaseId() == 0);
	}

	@Test
	public void testFindLatestPublicationReleaseByFiscalYear() {
		when(publicationMapper.findLatestPublicationReleaseByFiscalYear(nullable(String.class))).thenReturn(
				mockPublicationRelease());
		PublicationRelease publicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(CIMSTestConstants.TEST_VERSION);
		assertTrue(publicationRelease.getReleaseId() == 0);
	}

	@Test
	public void testFindLatestSnapShotByContextId() {
		when(publicationMapper.findLatestSnapShotByContextId(nullable(Long.class))).thenReturn(mockPublicationSnapShot());
		PublicationSnapShot publicationSnapShot = publicationService.findLatestSnapShotByContextId(0L);
		assertTrue(publicationSnapShot.getSnapShotId() == 0);
	}

	@Test
	public void testFindLatestSuccessPublicationReleaseByFiscalYear() {
		when(publicationMapper.findLatestSuccessPublicationReleaseByFiscalYear(nullable(String.class))).thenReturn(
				mockPublicationRelease());
		PublicationRelease publicationRelease = publicationService
				.findLatestSuccessPublicationReleaseByFiscalYear(CIMSTestConstants.TEST_VERSION);
		assertTrue(publicationRelease.getReleaseId() == 0);
	}

	@Test
	public void testFindNextVersionNumber() {
		String fiscalYear = CIMSTestConstants.TEST_VERSION;
		ReleaseType releaseType = ReleaseType.PRELIMINARY_INTERNAL_QA;
		PublicationSnapShot icdSnapShot = mockICDPublicationSnapShot();
		PublicationSnapShot cciSnapShot = mockPublicationSnapShot();
		when(publicationMapper.findLatestPublicationReleaseByFiscalYearAndReleaseType(fiscalYear, releaseType))
				.thenReturn(null);

		Integer nextVersionNumber = publicationService.findNextVersionNumber(fiscalYear, releaseType, icdSnapShot,
				cciSnapShot);
		assertTrue(nextVersionNumber.intValue() == 1);
	}

	@Test
	public void testFindPublicationReleaseAndReleaseMsgTmpById() {
		when(publicationMapper.findPublicationReleaseById(nullable(Long.class))).thenReturn(mockPublicationRelease());
		PublicationRelease publicationRelease = publicationService.findPublicationReleaseAndReleaseMsgTmpById(0L);
		assertTrue(publicationRelease.getReleaseId() == 0);
	}

	@Test
	public void testFindPublicationReleaseById() {
		when(publicationMapper.findPublicationReleaseById(nullable(Long.class))).thenReturn(mockPublicationRelease());
		PublicationRelease publicationRelease = publicationService.findPublicationReleaseById(0L);
		assertTrue(publicationRelease.getReleaseId() == 0);
	}

	@Test
	public void testFindReleaseZipFileName() {
		when(publicationMapper.findPublicationReleaseById(nullable(Long.class))).thenReturn(mockPublicationRelease());
		String fileName = publicationService.findReleaseZipFileName(0L);
		assertTrue(fileName != null);
	}

	@Test
	public void testFindSnapShotById() {
		when(publicationMapper.findSnapShotById(nullable(Long.class))).thenReturn(mockPublicationSnapShot());
		PublicationSnapShot publicationSnapShot = publicationService.findSnapShotById(0L);
		assertTrue(publicationSnapShot.getSnapShotId() == 0);
	}

	@Test
	public void testFindSnapShotZipFileName() {
		when(publicationMapper.findSnapShotById(nullable(Long.class))).thenReturn(mockPublicationSnapShot());
		String zipFileName = publicationService.findSnapShotZipFileName(0L);
		assertTrue(zipFileName != null);
	}

	@Test
	public void testFindUnusedComponentElements() {
		when(publicationMapper.findUnusedComponentElements(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockComponentAndAttributeElementModels());
		List<ComponentAndAttributeElementModel> unusedComponentElements = publicationService
				.findUnusedComponentElements(0L, 1L);
		assertTrue(unusedComponentElements.size() == 2);
	}

	@Test
	public void testFindUnusedGenericAttributes() {
		when(publicationMapper.findUnusedGenericAttributes(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockComponentAndAttributeElementModels());
		List<ComponentAndAttributeElementModel> unusedGenericAttributes = publicationService
				.findUnusedGenericAttributes(0L, 1L);
		assertTrue(unusedGenericAttributes.size() == 2);
	}

	@Test
	public void testFindUnusedReferenceValues() {
		when(publicationMapper.findUnusedReferenceValues(nullable(Long.class), nullable(Long.class))).thenReturn(
				mockComponentAndAttributeElementModels());
		List<ComponentAndAttributeElementModel> unusedReferenceValues = publicationService.findUnusedReferenceValues(
				0L, 1L);
		assertTrue(unusedReferenceValues.size() == 2);
	}

	@Test
	public void testGenerateClassificationTables() throws Exception {
		doNothing().when(notificationService).removeWrapupWorkNotifcation(nullable(GenerateReleaseTablesCriteria.class));
		doNothing().when(contextService).freezeTabularChanges(nullable(Long.class));
		doNothing().when(publicationMapper).updatePublicationSnapShot(nullable(PublicationSnapShot.class));
		when(publicationMapper.findSnapShotSeqNumber(nullable(Long.class))).thenReturn(0);
		when(publicationMapper.insertPublicationSnapShot(nullable(PublicationSnapShot.class))).thenReturn(1);
		// currentCCIOpenContextId, cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdSection,
		// cciClassIdBlock,
		// cciClassIdGroup, "ENG"
		when(
				publicationMapper.findCCIBlkDesc(nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class),
						nullable(Long.class), nullable(String.class))).thenReturn(mockCodeDescriptionPublications());

		when(publicationMapper.findCCIRubricDesc(nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCodeDescriptionPublications());

		when(publicationMapper.findCCICodeDesc(nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(Long.class), nullable(String.class)))
				.thenReturn(mockCodeDescriptionPublications());

		when(fileGeneratorFactory.createFileGenerator(nullable(String.class))).thenReturn(mockFileGenerator());

		// (currentICDOpenContextId, generateTablesModel, releaseId, isRelease, statisticsSummary, currentUser);
		doNothing().when(fileGenerator).generateAsciiFile(nullable(Long.class), nullable(GenerateReleaseTablesCriteria.class),
				nullable(String.class), nullable(Boolean.class), nullable(List.class), nullable(User.class));

		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		generateTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		generateTablesModel.setFileFormat(FileFormat.FIX);
		User currentUser = mockCurrentUser();
		String sessionId = "seesionId";
		publicationService.generateClassificationTables(generateTablesModel, currentUser, sessionId);
	}

	@Test
	public void testGetCurrentProcessingFile() {
		String currentProcessionFile = publicationService.getCurrentProcessingFile("sessionId");
		assertTrue(currentProcessionFile == null);
	}

	@Test
	public void testGetCurrentProcessingYear() {
		String currentProcessionYear = publicationService.getCurrentProcessingYear();
		assertTrue(currentProcessionYear != null);
	}

	@Test
	public void testIsGenerateFileProcessRunning() {
		when(publicationMapper.findLatestSnapShotByContextId(nullable(Long.class))).thenReturn(mockPublicationSnapShot());
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		generateTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		generateTablesModel.setFileFormat(FileFormat.FIX);
		boolean isRunning = publicationService.isGenerateFileProcessRunning(generateTablesModel);
		assertTrue(!isRunning);
	}

	@Test
	public void testNotifyUsersToWrapupWork() {
		doNothing().when(notificationService).postWrapupWorkNotifcationToContentDeveloperAndReviewer(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class));
		doNothing().when(notificationService).postWrapupWorkNotifcationToAdministrator(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class));
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		generateTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		generateTablesModel.setFileFormat(FileFormat.FIX);
		publicationService.notifyUsersToWrapupWork(generateTablesModel, mockCurrentUser());
		verify(notificationService, times(1)).postWrapupWorkNotifcationToContentDeveloperAndReviewer(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class));
		verify(notificationService, times(1)).postWrapupWorkNotifcationToAdministrator(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class));

	}

	// FIXME: PublicationServiceImpl uses static CimsFileUtils
	@Test
	@Ignore
	public void testReleaseClassificationTables() throws Exception {
		when(publicationMapper.synchronizeRelease()).thenReturn(1L);
		when(
				publicationMapper.findLatestPublicationReleaseByFiscalYearAndReleaseType(nullable(String.class),
						nullable(ReleaseType.class))).thenReturn(null);

		when(publicationMapper.findLatestSuccessFixedWidthSnapShotByContextId(nullable(Long.class))).thenReturn(
				mockICDPublicationSnapShot());
		when(publicationMapper.findSnapShotById(nullable(Long.class))).thenReturn(mockPublicationSnapShot());

		doNothing().when(emailService).sendReleaseTableNotificationEmail(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class));
		when(publicationMapper.insertPublicationRelease(nullable(PublicationRelease.class))).thenReturn(1);

		doNothing().when(publicationMapper).insertPublicationReleaseSnapShot(nullable(PublicationRelease.class));

		doNothing().when(notificationService).postPackageReleaseNotifcation(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class));
		doNothing().when(publicationMapper).updatePublicationRelease(nullable(PublicationRelease.class));

		GenerateReleaseTablesCriteria releaseTablesModel = new GenerateReleaseTablesCriteria();
		releaseTablesModel.setClassification(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH);
		releaseTablesModel.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		releaseTablesModel.setReleaseType("Preliminary_Internal_QA");

		publicationService.releaseClassificationTables(releaseTablesModel, mockCurrentUser(), "sessionId");
	}

	@Test
	public void testSendReleaseEmailNotification() {
		doNothing().when(publicationMapper).updatePublicationRelease(nullable(PublicationRelease.class));
		doNothing().when(emailService).emailReleaseNotification(nullable(PublicationRelease.class));
		PublicationRelease publicationRelease = mockPublicationRelease();
		publicationService.sendReleaseEmailNotification(publicationRelease);
		verify(emailService, times(1)).emailReleaseNotification(nullable(PublicationRelease.class));
	}

	@Test
	public void testUnfreezeTabularChanges() {
		doNothing().when(contextService).unfreezeTabularChanges(nullable(Long.class));
		publicationService.unfreezeTabularChanges(0L);
		verify(contextService, times(1)).unfreezeTabularChanges(nullable(Long.class));

	}

	@Test
	public void testUpdatePublicationRelease() {
		doNothing().when(publicationMapper).updatePublicationRelease(nullable(PublicationRelease.class));
		PublicationRelease publicationRelease = mockPublicationRelease();
		publicationService.updatePublicationRelease(publicationRelease);
		verify(publicationMapper, times(1)).updatePublicationRelease(nullable(PublicationRelease.class));
	}

	@Test
	public void testUpdatePublicationSnapShot() {
		PublicationSnapShot publicationSnapShot = mockPublicationSnapShot();
		doNothing().when(publicationMapper).updatePublicationSnapShot(nullable(PublicationSnapShot.class));
		publicationService.updatePublicationSnapShot(publicationSnapShot);
		verify(publicationMapper, times(1)).updatePublicationSnapShot(nullable(PublicationSnapShot.class));

	}

	@Test
	public void testUpdatePublicationSnapShotQANote() {
		PublicationSnapShot publicationSnapShot = mockPublicationSnapShot();
		doNothing().when(publicationMapper).updatePublicationSnapShotQANote(nullable(PublicationSnapShot.class));
		publicationService.updatePublicationSnapShotQANote(publicationSnapShot);
		verify(publicationMapper, times(1)).updatePublicationSnapShotQANote(nullable(PublicationSnapShot.class));
	}

}
