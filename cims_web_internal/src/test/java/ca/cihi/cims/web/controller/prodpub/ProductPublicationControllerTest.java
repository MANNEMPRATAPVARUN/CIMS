package ca.cihi.cims.web.controller.prodpub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
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

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.EmailService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;
import ca.cihi.cims.validator.PublicationValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ProductPublicationControllerTest {

	ProductPublicationController productPublicationController;
	@Autowired
	private LookupService lookupService;
	@Mock
	private PublicationService publicationService;
	@Mock
	private EmailService emailService;
	@Mock
	private PublicationValidator publicationValidator;
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
	static Date today = Calendar.getInstance().getTime();

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		productPublicationController = new ProductPublicationController();
		productPublicationController.setEmailService(emailService);
		productPublicationController.setLookupService(lookupService);
		productPublicationController.setPublicationService(publicationService);
		productPublicationController.setPublicationValidator(publicationValidator);
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
		when(publicationService.findAllSnapShotsByContextId(nullable(Long.class))).thenReturn(mockPublicationSnapShots());
		when(publicationService.findAllReleases()).thenReturn(mockPublicationReleases());
	}

	private ContextIdentifier mockContextIdentifierCCI() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2018", "CCI", 1l, "ACTIVE", new Date(),
				Boolean.TRUE, 1l, null);
		return contextIdentifier;
	}

	private ContextIdentifier mockContextIdentifierICD() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2018", "ICD-10-CA", 1l, "ACTIVE", new Date(),
				Boolean.TRUE, 1l, null);
		return contextIdentifier;
	}

	private GenerateReleaseTablesCriteria mockGenerateReleaseTablesCriteria() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setClassification("CCI");
		generateReleaseTablesCriteria.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		generateReleaseTablesCriteria.setFileFormat(FileFormat.FIX);
		generateReleaseTablesCriteria
				.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY_INTERNAL_QA);
		return generateReleaseTablesCriteria;

	}

	private PublicationRelease mockPublicationReleaseO() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(1L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.OFFICIAL);
		publicationRelease.setCreatedDate(today);
		publicationRelease.setPublicationSnapShots(mockPublicationSnapShots());
		return publicationRelease;
	}

	private PublicationRelease mockPublicationReleaseOIQA() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(1L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.OFFICIAL_INTERNAL_QA);
		publicationRelease.setCreatedDate(today);
		publicationRelease.setPublicationSnapShots(mockPublicationSnapShots());
		return publicationRelease;
	}

	private PublicationRelease mockPublicationReleaseP() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(1L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.PRELIMINARY);
		publicationRelease.setCreatedDate(today);
		publicationRelease.setPublicationSnapShots(mockPublicationSnapShots());
		return publicationRelease;
	}

	private PublicationRelease mockPublicationReleasePIQA() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(1L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.PRELIMINARY_INTERNAL_QA);
		publicationRelease.setCreatedDate(today);
		publicationRelease.setPublicationSnapShots(mockPublicationSnapShots());
		return publicationRelease;
	}

	private List<PublicationRelease> mockPublicationReleases() {
		List<PublicationRelease> publicationReleases = new ArrayList<PublicationRelease>();
		publicationReleases.add(mockPublicationReleasePIQA());
		publicationReleases.add(mockPublicationReleaseP());
		publicationReleases.add(mockPublicationReleaseOIQA());
		publicationReleases.add(mockPublicationReleaseO());
		return publicationReleases;
	}

	private PublicationSnapShot mockPublicationSnapShotCCI() {
		PublicationSnapShot publicationSnapShot = new PublicationSnapShot();
		publicationSnapShot.setContextIdentifier(mockContextIdentifierCCI());
		publicationSnapShot.setSnapShotId(1L);
		publicationSnapShot.setCreatedDate(today);
		return publicationSnapShot;
	}

	private PublicationSnapShot mockPublicationSnapShotICD() {
		PublicationSnapShot publicationSnapShot = new PublicationSnapShot();
		publicationSnapShot.setContextIdentifier(mockContextIdentifierICD());
		publicationSnapShot.setSnapShotId(2L);
		publicationSnapShot.setCreatedDate(today);
		return publicationSnapShot;
	}

	private List<PublicationSnapShot> mockPublicationSnapShots() {
		List<PublicationSnapShot> publicationSnapShots = new ArrayList<PublicationSnapShot>();
		publicationSnapShots.add(mockPublicationSnapShotCCI());
		publicationSnapShots.add(mockPublicationSnapShotICD());

		return publicationSnapShots;
	}

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	@Test
	public void testGenerateClassificationTables() throws Exception {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;
		productPublicationController.generateClassificationTables(test_session, generateTablesCriteria, test_result,
				test_model);
		verify(publicationService, times(1)).generateClassificationTables(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class), nullable(String.class));
	}

	//@Test(expected = RuntimeException.class)
	@Ignore
	public void testGenerateClassificationTablesWithException() throws Exception {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;
		doThrow(new Exception()).when(publicationService).generateClassificationTables(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class), nullable(String.class));
		productPublicationController.generateClassificationTables(test_session, generateTablesCriteria, test_result,
				test_model);
		verify(emailService, times(1)).sendGenerateTableFailedEmail(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class), nullable(Exception.class));

	}

	@Test
	public void testGetGeneratingTablesState() throws Exception {
		HttpServletResponse test_response = response;
		HttpSession test_session = session;
		String classification = "CCI";
		productPublicationController.getGeneratingTablesState(test_response, test_session, classification);
		verify(publicationService, times(1)).getCurrentProcessingFile(nullable(String.class));
	}

	@Test
	public void testInitBinder() {
		final WebDataBinder test_binder = binder;
		productPublicationController.initBinder(test_binder);
	}

	@Test
	public void testNotifyUsersToWrapupWork() {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;
		productPublicationController.notifyUsersToWrapupWork(test_session, generateTablesCriteria, test_result,
				test_model);
		verify(publicationService, times(1)).notifyUsersToWrapupWork(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class));
	}

	@Test
	public void testPopupProcessNotes() {
		Model test_model = model;
		Long contextId = 1L;
		HttpSession test_session = session;
		String rtnView = productPublicationController.popupProcessNotes(test_model, contextId, test_session);
		String expectedView = ProductPublicationController.PROCESS_NOTES;
		verify(publicationService, times(1)).findAllSnapShotsByContextId(nullable(Long.class));
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testPopupQAResults() {
		Model test_model = model;
		Long contextId = 1L;
		HttpSession test_session = session;
		String rtnView = productPublicationController.popupQAResults(test_model, contextId, test_session);
		String expectedView = ProductPublicationController.QA_RESULTS;
		verify(publicationService, times(1)).findAllSnapShotsByContextId(nullable(Long.class));
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testPopupReleaseDetails() {
		Model test_model = model;
		Long releaseId = 1L;
		HttpSession test_session = session;
		productPublicationController.popupReleaseDetails(test_model, releaseId, test_session);
		verify(publicationService, times(1)).findPublicationReleaseById(releaseId);
	}

	@Test
	public void testPopupReleaseEmail() {
		Model test_model = model;
		Long releaseId = 1L;
		HttpSession test_session = session;
		productPublicationController.popupReleaseEmail(test_model, releaseId, test_session);
		verify(publicationService, times(1)).findPublicationReleaseAndReleaseMsgTmpById(releaseId);
	}

	@Test
	public void testPopupUnusedComponentAttributesReport() {
		Model test_model = model;
		HttpSession test_session = session;
		productPublicationController.popupUnusedComponentAttributesReport(test_model, test_session);
		verify(publicationService, times(1)).findUnusedComponentElements(nullable(Long.class), nullable(Long.class));
		verify(publicationService, times(1)).findUnusedGenericAttributes(nullable(Long.class), nullable(Long.class));
		verify(publicationService, times(1)).findUnusedReferenceValues(nullable(Long.class), nullable(Long.class));
	}

	@Ignore
	public void testReleaseClassificationTables() throws Exception {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;

		when(publicationService.areBothClassificationFixedWidthFilesGenerated(generateReleaseTablesCriteria))
				.thenReturn(true);
		productPublicationController.releaseClassificationTables(test_session, generateReleaseTablesCriteria,
				test_result, test_model);
		verify(publicationService, times(1)).releaseClassificationTables(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class), nullable(String.class));
	}

	@Ignore
	public void testReleaseClassificationTablesWithException() throws Exception {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;

		when(publicationService.areBothClassificationFixedWidthFilesGenerated(generateReleaseTablesCriteria))
				.thenReturn(true);
		doThrow(new Exception()).when(publicationService).releaseClassificationTables(
				nullable(GenerateReleaseTablesCriteria.class), nullable(User.class), nullable(String.class));
		productPublicationController.releaseClassificationTables(test_session, generateReleaseTablesCriteria,
				test_result, test_model);
		verify(publicationService, times(1)).releaseClassificationTables(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class), nullable(String.class));
		verify(emailService, times(1)).sendReleaseTableFailedEmail(nullable(GenerateReleaseTablesCriteria.class),
				nullable(User.class), nullable(Exception.class));
	}

	@Test
	public void testSendReleaseEmailNotification() {
		HttpSession test_session = session;
		PublicationRelease publicationRelease = mockPublicationReleasePIQA();
		BindingResult test_result = result;
		Model test_model = model;
		productPublicationController.sendReleaseEmailNotification(test_session, publicationRelease, test_result,
				test_model);
		verify(publicationService, times(1)).sendReleaseEmailNotification(publicationRelease);

	}

	@Test
	public void testShowGenerateClassificationTablesPage() {
		Model test_model = model;
		HttpSession test_session = session;
		String rtnView = productPublicationController.showGenerateClassificationTablesPage(test_model, test_session);
		String expectedView = ProductPublicationController.GENERATE_TABLES;
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testShowReleaseHistoryPage() {
		Model test_model = model;
		HttpSession test_session = session;
		productPublicationController.showReleaseHistoryPage(test_model, test_session);
		verify(publicationService, times(1)).findAllReleases();
	}

	@Test
	public void testShowReviewTables() {
		Model test_model = model;
		HttpSession test_session = session;
		String rtnView = productPublicationController.showReviewTables(test_model, test_session);
		verify(publicationService, times(1)).findAllSuccessLatestSnapShots();
	}

	@Test
	public void testShowUnfreezeSystem() {
		Model test_model = model;
		HttpSession test_session = session;
		String rtnView = productPublicationController.showUnfreezeSystem(test_model, test_session);
		String expectedView = ProductPublicationController.UNFREEZE_SYSTEM;
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testUnfreezeCCI() {
		Model test_model = model;
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		HttpSession test_session = session;
		productPublicationController.unfreezeCCI(test_model, generateReleaseTablesCriteria, test_result, test_session);
		verify(publicationService, times(1)).unfreezeTabularChanges(nullable(Long.class));
	}

	@Test
	public void testUnfreezeICD() {
		Model test_model = model;
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		HttpSession test_session = session;
		productPublicationController.unfreezeICD(test_model, generateReleaseTablesCriteria, test_result, test_session);
		verify(publicationService, times(1)).unfreezeTabularChanges(nullable(Long.class));
	}

	@Ignore
	public void testUpdateQAResults() {
		Model test_model = model;
		PublicationSnapShot publicationSnapShot = mockPublicationSnapShotCCI();
		HttpSession test_session = session;
		productPublicationController.updateQAResults(test_model, publicationSnapShot, test_session);
		verify(publicationService, times(1)).updatePublicationSnapShotQANote(publicationSnapShot);
	}
}
