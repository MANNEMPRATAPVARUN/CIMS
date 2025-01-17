package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformCCIComponentService;

public class CciComponentTransformReportControllerTest {

	private CciComponentTransformReportController controller;

	@Mock
	private Model model;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private TransformCCIComponentService transformService;

	@Mock
	private BaseTransformationService baseTransformService;

	private static final String FISCAL_2009 = "2009";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new CciComponentTransformReportController();
		controller.setTransformService(transformService);
		controller.setBaseTransformService(baseTransformService);
	}

	/**
	 * Test case for back
	 */
	@Test
	public void testBack() {
		ModelAndView mav = controller.back(Constants.CLASSIFICATION_ICD10CA, FISCAL_2009);
		assertSame(mav.getViewName(), "redirect:/cciComponentDataTransformation.htm");
	}

	@Test
	public void testGetBaseTransformationService() {
		assertTrue(baseTransformService.equals(controller.getBaseTransformService()));
	}

	@Test
	public void testGetTranformationService() {
		assertTrue(transformService.equals(controller.getTransformService()));
	}

	/**
	 * Test case for view setup form
	 */
	@Test
	public void testSetupForm() {

		// Stub
		when(baseTransformService.getAllErrors(FISCAL_2009, Constants.CLASSIFICATION_ICD10CA)).thenReturn(
				new ArrayList<TransformationError>());
		when(request.getSession()).thenReturn(session);

		ModelAndView mav = controller.setupForm(model, Constants.CLASSIFICATION_ICD10CA, FISCAL_2009, "", request,
				session);
		assertSame(mav.getViewName(), "/migration/cciComponentTransformationReport");
	}

}
