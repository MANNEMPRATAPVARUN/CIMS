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
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.service.TransformIndexServiceImpl;
import ca.cihi.cims.transformation.IndexXmlGenerator;
import ca.cihi.cims.web.bean.KeyValueBean;

/**
 * @author wxing
 */
public class IndexDataTransformControllerTest {
	private static final Log LOGGER = LogFactory.getLog(IndexDataTransformControllerTest.class);

	private IndexDataTransformController controller;
	protected ContextAccess context;
	private static final String FISCAL_YEAR_2009 = "2009";
	private static final String FISCAL_YEAR_2010 = "2010";

	@Mock
	private Model model;

	@Mock
	private TransformIndexServiceImpl transformIndexService;

	@Mock
	private ContextProvider contextProvider;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new IndexDataTransformController();
		controller.setTransformIndexService(transformIndexService);
		controller.setContextProvider(contextProvider);
	}

	@Test
	public void testGetContextProvider() {
		Assert.assertTrue(contextProvider.equals(controller.getContextProvider()));
	}

	@Test
	public void testGetTransformationService() {
		Assert.assertTrue(transformIndexService.equals(controller.getTransformIndexService()));
	}

	@Test
	public void testPopulateClassification() {
		LOGGER.info("Enter DataMigrationControllerTest.testPopulateClassification()");

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
		assertSame(mav.getViewName(), "/migration/indexDataTransformation");
	}

	/**
	 * Test case for transformData.
	 */
	@Test
	public void testTransformData() {

		// Stub
		when(
				transformIndexService.checkRunStatus(FISCAL_YEAR_2009, Constants.CLASSIFICATION_ICD10CA,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG)).thenReturn(false);
		// RunStatus is false
		ModelAndView mav = controller.transformData(model, FISCAL_YEAR_2009, Constants.CLASSIFICATION_ICD10CA,
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG);
		assertSame(mav.getViewName(), "/migration/indexDataTransformation");

		// No index data
		when(
				transformIndexService.checkRunStatus(FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG)).thenReturn(true);
		when(
				contextProvider.findContext(ContextDefinition.forVersion(Constants.CLASSIFICATION_ICD10CA,
						FISCAL_YEAR_2010))).thenReturn(null);
		when(
				transformIndexService.getBookIndex(context, IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL,
						Index.LANGUAGE_ENG)).thenReturn(null);

		mav = controller.transformData(model, FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA,
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG);
		assertSame(mav.getViewName(), "/migration/indexDataTransformation");
	}

	/**
	 * Test case for viewTransformationReport
	 */
	@Test
	public void testViewTransformationReport() {
		ModelAndView mav = controller.viewTransformationReport(model, FISCAL_YEAR_2009, "ICD-10-CA",
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG);
		assertSame(mav.getViewName(), "redirect:/indexTransformation.htm");
	}

}
