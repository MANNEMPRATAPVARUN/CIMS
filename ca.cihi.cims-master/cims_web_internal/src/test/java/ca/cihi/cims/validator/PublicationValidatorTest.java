package ca.cihi.cims.validator;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class PublicationValidatorTest {
	PublicationValidator publicationValidator;
	@Mock
	ChangeRequestService changeRequestService;
	@Mock
	PublicationService publicationService;
	@Autowired
	LookupService lookupService;
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
		publicationValidator = new PublicationValidator();
		publicationValidator.setChangeRequestService(changeRequestService);
		publicationValidator.setLookupService(lookupService);
		publicationValidator.setPublicationService(publicationService);
	}

	private ContextIdentifier mockContextIdentifierCCI() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, CIMSTestConstants.TEST_VERSION, "CCI", 1l,
				"ACTIVE", new Date(), Boolean.TRUE, 1l, null);
		return contextIdentifier;
	}

	private ContextIdentifier mockContextIdentifierICD() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, CIMSTestConstants.TEST_VERSION, "ICD-10-CA",
				1l, "ACTIVE", new Date(), Boolean.TRUE, 1l, null);
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

	@Test
	public void testSupports() {
		boolean support = publicationValidator.supports(GenerateReleaseTablesCriteria.class);
		assertTrue(support);
	}

	@Test
	public void testValidateCloseYearBtn() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		publicationValidator.validateCloseYearBtn(generateReleaseTablesCriteria, test_result);
		verify(changeRequestService, times(2)).findOpenChangeRequestsByClassificationAndVersionYear(nullable(String.class),
				nullable(Long.class));
	}

	/*
	 * it is CCI
	 */
	@Test
	public void testValidateGenerateTablesBtn() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		publicationValidator.validateGenerateTablesBtn(generateReleaseTablesCriteria, test_result);
		verify(changeRequestService, times(1)).findOpenTabularChangeRequestsByClassificationAndVersionYear(nullable(String.class),
				nullable(Long.class));
		verify(publicationService, times(1)).findUnusedComponentElements(nullable(Long.class), nullable(Long.class));
		verify(publicationService, times(1)).findUnusedGenericAttributes(nullable(Long.class), nullable(Long.class));
		verify(publicationService, times(1)).findUnusedReferenceValues(nullable(Long.class), nullable(Long.class));
	}

	@Test
	public void testValidateReleaseBtn() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		publicationValidator.validateReleaseBtn(generateReleaseTablesCriteria, test_result);
		verify(publicationService, times(2)).findLatestSnapShotByContextId(nullable(Long.class));
	}

	@Test
	public void testValidateUnfreezeCCIBtn() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		publicationValidator.validateUnfreezeCCIBtn(generateReleaseTablesCriteria, test_result);
		verify(publicationService, times(1)).findLatestSnapShotByContextId(nullable(Long.class));
		verify(publicationService, times(1)).findLatestPublicationReleaseByFiscalYear(nullable(String.class));

	}

	@Test
	public void testValidateUnfreezeICDBtn() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		publicationValidator.validateUnfreezeICDBtn(generateReleaseTablesCriteria, test_result);
		verify(publicationService, times(1)).findLatestSnapShotByContextId(nullable(Long.class));
		verify(publicationService, times(1)).findLatestPublicationReleaseByFiscalYear(nullable(String.class));
	}
}
