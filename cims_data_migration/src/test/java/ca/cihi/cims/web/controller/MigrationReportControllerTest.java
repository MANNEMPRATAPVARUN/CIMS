package ca.cihi.cims.web.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.service.MigrationService;
import ca.cihi.cims.web.bean.LogMessage;

/**
 * @author wxing
 */
public class MigrationReportControllerTest {
	private MigrationReportController controller;
	protected ApplicationContext context;

	private static final String VERSION = "2009";

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private Model model;

	@Mock
	private MigrationService migrationService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new MigrationReportController();
		controller.setMigrationService(migrationService);
	}

	/**
	 * Test case for back
	 */
	@Test
	public void testBack() {
		ModelAndView mav = controller.back(model);
		assertSame(mav.getViewName(), "redirect:/dataMigration.htm");
	}

	@Test
	public void testGetMigrationService() {
		Assert.assertTrue(migrationService.equals(controller.getMigrationService()));
	}

	/**
	 * Test case for setupForm
	 */
	@Test
	public void testSetupForm() {

		// Stub
		when(migrationService.getLogMessage(Constants.CLASSIFICATION_ICD10CA, VERSION)).thenReturn(
				new ArrayList<LogMessage>());
		when(migrationService.migrateData(VERSION, Constants.CLASSIFICATION_ICD10CA)).thenReturn(
				new ArrayList<LogMessage>());

		when(request.getSession()).thenReturn(session);

		ModelAndView mav = controller.setupForm(Constants.CLASSIFICATION_ICD10CA, VERSION, "", request, session);
		assertSame(mav.getViewName(), "/migration/migrationReport");

		mav = controller.setupForm(Constants.CLASSIFICATION_ICD10CA, VERSION, "view", request, session);
		assertSame(mav.getViewName(), "/migration/migrationReport");

	}

}
