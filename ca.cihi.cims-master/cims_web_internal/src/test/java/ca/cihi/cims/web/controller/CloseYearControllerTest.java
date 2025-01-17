package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;
import ca.cihi.cims.validator.PublicationValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CloseYearControllerTest {
	CloseYearController closeYearController;
	@Autowired
	LookupService lookupService;
	@Mock
	PublicationService publicationService;
	@Mock
	PublicationValidator publicationValidator;
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

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		closeYearController = new CloseYearController();
		closeYearController.setLookupService(lookupService);
		closeYearController.setPublicationService(publicationService);
		closeYearController.setPublicationValidator(publicationValidator);
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
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

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	@Test
	public void testCloseYear() {
		HttpSession test_session = session;
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		BindingResult test_result = result;
		Model test_model = model;
		closeYearController.closeYear(test_model, generateReleaseTablesCriteria, test_result, test_session);
		verify(publicationService, times(1)).closeYear(nullable(Long.class), nullable(User.class));
	}

	@Test
	public void testShowCloseYearPage() {
		Model test_model = model;
		HttpSession test_session = session;
		String rtnView = closeYearController.showCloseYearPage(test_model, test_session);
		String expectedView = CloseYearController.CLOSE_YEAR;
		assertEquals("Should get same view", rtnView, expectedView);
	}
}
