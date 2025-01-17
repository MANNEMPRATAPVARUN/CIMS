package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.service.TransformIndexServiceImpl;
import ca.cihi.cims.transformation.IndexXmlGenerator;

/**
 * @author wxing
 */
public class IndexTransformReportControllerTest {
	private IndexTransformReportController controller;

	@Mock
	private Model model;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private TransformIndexServiceImpl transformIndexService;

	private static final String FISCAL_2009 = "2009";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new IndexTransformReportController();
		controller.setTransformIndexService(transformIndexService);
	}

	/**
	 * Test case for back
	 */
	@Test
	public void testBack() {
		ModelAndView mav = controller.back(Constants.CLASSIFICATION_ICD10CA, FISCAL_2009,
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG);
		assertSame(mav.getViewName(), "redirect:/indexDataTransformation.htm");
	}

	@Test
	public void testGetTranformationService() {
		Assert.assertTrue(transformIndexService.equals(controller.getTransformIndexService()));
	}

	/**
	 * Test case for view setup form
	 */
	@Test
	public void testSetupForm() {

		// Stub
		when(
				transformIndexService.getAllErrors(FISCAL_2009, Constants.CLASSIFICATION_ICD10CA,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG)).thenReturn(
				new ArrayList<TransformationError>());
		when(request.getSession()).thenReturn(session);

		ModelAndView mav = controller.setupForm(model, Constants.CLASSIFICATION_ICD10CA, FISCAL_2009, " ",
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG, request, session);
		assertSame(mav.getViewName(), "/migration/indexTransformationReport");
	}

}
