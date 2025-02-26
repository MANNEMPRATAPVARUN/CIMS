package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformSupplementService;
import ca.cihi.cims.web.bean.KeyValueBean;

/**
 * @author wxing
 */
public class SupplementDataTransformControllerTest {
	private static final Log LOGGER = LogFactory.getLog(SupplementDataTransformControllerTest.class);

	private SupplementDataTransformController controller;
	protected ApplicationContext context;
	private static final String FISCAL_YEAR_2009 = "2009";
	private static final String FISCAL_YEAR_2010 = "2010";

	@Mock
	private Model model;

	@Mock
	private TransformSupplementService transformService;

	@Mock
	private BaseTransformationService baseTransformService;

	@Mock
	private ContextProvider contextProvider;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new SupplementDataTransformController();
		controller.setTransformSupplementService(transformService);
		controller.setContextProvider(contextProvider);
		controller.setBaseTransformService(baseTransformService);
	}

	@Test
	public void testGetBaseTransformationService() {
		Assert.assertTrue(baseTransformService.equals(controller.getBaseTransformService()));
	}

	@Test
	public void testGetContextProvider() {
		Assert.assertTrue(contextProvider.equals(controller.getContextProvider()));
	}

	@Test
	public void testGetTransformationService() {
		Assert.assertTrue(transformService.equals(controller.getTransformSupplementService()));
	}

	@Test
	public void testPopulateClassification() {
		LOGGER.info("Enter TabularDataTransformControllerTest.testPopulateClassification()");

		List<String> classifications = new ArrayList<String>();
		classifications.add("ICD-10-CA");
		classifications.add("CCI");
		classifications.add("ICD11");

		when(contextProvider.findBaseClassifications()).thenReturn(classifications);

		Collection<KeyValueBean> results = controller.populateClassification();
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());

		Assert.assertTrue(results.size() == 3);
	}

	/**
	 * Test case for view setup form
	 */
	@Test
	public void testSetupForm() {
		ModelAndView mav = controller.setupForm(model);
		assertSame(mav.getViewName(), "/migration/supplementDataTransformation");
	}

	/**
	 * Test case for transformData.
	 */
	@Test
	public void testTransformData() {

		// Stub
		when(transformService.checkRunStatus(FISCAL_YEAR_2009, Constants.CLASSIFICATION_ICD10CA)).thenReturn(false);
		// RunStatus is false
		ModelAndView mav = controller.transformData(model, FISCAL_YEAR_2009, Constants.CLASSIFICATION_ICD10CA);
		assertSame(mav.getViewName(), "/migration/supplementDataTransformation");

		// No tabular data
		when(transformService.checkRunStatus(FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA)).thenReturn(true);
		when(
				contextProvider.findContext(ContextDefinition.forVersion(Constants.CLASSIFICATION_ICD10CA,
						FISCAL_YEAR_2010))).thenReturn(null);
		when(transformService.getAllSupplements(null)).thenReturn(null);
		mav = controller.transformData(model, FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA);
		assertSame(mav.getViewName(), "/migration/supplementDataTransformation");

	}

	/**
	 * Test case for viewTransformationReport
	 */
	@Test
	public void testViewTransformationReport() {
		ModelAndView mav = controller.viewTransformationReport(model, FISCAL_YEAR_2009, "ICD-10-CA");
		assertSame(mav.getViewName(), "redirect:/supplementTransformation.htm");
	}

}
