package ca.cihi.cims.web.controller.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.model.ClassificationViewerModel;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.web.bean.CodeSearchResultBean;
import ca.cihi.cims.web.bean.ConceptViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ClassficationViewerControllerTest {

	protected ClassificationViewerController controller;

	@Mock
	protected Model model;

	@Mock
	protected HttpServletRequest request;

	@Mock
	protected HttpSession session;

	@Mock
	HttpServletResponse response;

	@Mock
	ViewService viewService;

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private TransformQualifierlistService transformationServiceQualifierlist;

	// --------------------------------------------------------------------

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		controller = new ClassificationViewerController();
		controller.setViewService(viewService);
		controller.setContextProvider(contextProvider);
		controller.setLookupService(lookupService);
		controller.setTransformationServiceQualifierlist(transformationServiceQualifierlist);
		when(request.getParameter("conceptId")).thenReturn("41");
		when(request.getParameter("classification")).thenReturn("ICD-10-CA");
		when(request.getParameter("fiscalYear")).thenReturn("2012");
		when(request.getParameter("contextId")).thenReturn("1");
		when(request.getParameter("language")).thenReturn("ENG");
		when(request.getParameter("containerConceptId")).thenReturn("01");
		when(request.getParameter("term")).thenReturn("A00");
		when(request.getParameter("searchBy")).thenReturn("code");

		// when(viewService.getClassificationRootVersionId("ICD-10-CA", "2012")).thenReturn("34");
		// when(viewService.getTreeNodes("41", "ICD-10-CA", "2012", "ENG",
		// "01")).thenReturn(mockGetTreeNodeOrContentList());
		when(viewService.getTreeNodes("41", "ICD-10-CA", 1L, "ENG", "01")).thenReturn(mockGetTreeNodeOrContentList());

		// when(viewService.getContentList("41", "ICD-10-CA", "2012", "ENG",
		// null)).thenReturn(mockGetTreeNodeOrContentList());
		when(viewService.getSearchResults("ICD-10-CA", 1L, "ENG", "code", null, "A00", 18)).thenReturn(
				mockGetSearchResults());
		when(viewService.getSearchResults("ICD-10-CA", 1L, "ENG", "code", null, "A00", 18, true)).thenReturn(
				mockGetSearchResults());

		// when(viewService.getConceptShortPresentation("O08.0", "ICD-10-CA", "2012",
		// "ENG")).thenReturn("a name=\"\">O08.0</a>");
		when(viewService.getConceptShortPresentation("O08.0", "ICD-10-CA", 1L, "ENG")).thenReturn(
				"a name=\"\">O08.0</a>");
	}

	private List<SearchResultModel> mockGetSearchResults() {
		List<SearchResultModel> result = new ArrayList<SearchResultModel>();
		SearchResultModel model = new SearchResultModel();
		model.setConceptId("71");
		model.setConceptIdPath("/34/41/59/71");
		model.setConceptType("Category");
		model.setConceptCodeDesc("A00");
		model.setConceptCode("A00");
		model.setLongDescription("Cholera");
		result.add(model);
		return result;
	}

	private List<ContentViewerModel> mockGetTreeNodeOrContentList() {
		List<ContentViewerModel> result = new ArrayList<ContentViewerModel>();
		ContentViewerModel cvm = new ContentViewerModel();
		cvm.setConceptCode("A00-A09");
		cvm.setConceptLongDesc("Intestinal infectious diseases (A00-A09)");
		cvm.setConceptShortDesc("Intestinal infectious diseases");
		cvm.setConceptId("59");
		cvm.setUnitConceptId("1");
		cvm.setLeaf(false);
		cvm.setHasChildren("Y");
		result.add(cvm);
		return result;
	}

	@Test
	public void testAttributePopup() {
		ContextAccess contextAccess = contextProvider.findContext(ContextDefinition.forVersion("CCI", "2015"));

		String viewName = controller.attributePopup(model, request, "CCI", contextAccess.getContextId().getContextId(),
				"ENG", "S04");
		String expectedViewName = ClassificationViewerController.CODE_REFERENCE_REPORT_VIEW;
		assertEquals("Should get expected view", viewName, expectedViewName);
	}

	@Test
	public void testCciValidationPopup() {
		ContextAccess contextAccess = contextProvider.findContext(ContextDefinition.forVersion("CCI", "2015"));
		String viewName = controller.cciValidationPopup(model, request, "CCI",
				String.valueOf(contextAccess.getContextId().getContextId()), "ENG", "1.AE.85.^^");
		String expectedViewName = ClassificationViewerController.CCI_VALIDATION_REPORT_VIEW;
		assertEquals("Should get expected view", viewName, expectedViewName);
	}

	@Test
	public void testClassificationViewerForm() {
		ClassificationViewerModel viewerModel = new ClassificationViewerModel();
		ModelAndView mav = controller.classificationViewerForm(viewerModel, session, request);
		ModelAndView expectedMav = new ModelAndView();
		// expectedMav.addObject(ClassificationViewerController.CLASSIFICATION_VIEWER_MODEL_KEY, viewerModel);

		expectedMav.setViewName(ClassificationViewerController.CLASSIFICATION_VIEWER_VIEW);
		// assertEquals("Should get expected model", mav.getModel(), expectedMav.getModel());
		assertEquals("Should get expected view", mav.getViewName(), expectedMav.getViewName());

	}

	@Test
	public void testConceptDetailPopup() {
		String viewName = controller.conceptDetailPopup(model, request, "ICD-10-CA", 1L, "ENG", "O08.0");
		String expectedViewName = ClassificationViewerController.CONTENT_DETAIL_DIALOG_VIEW;
		assertEquals("Should get expected view", viewName, expectedViewName);
	}

	@Test
	public void testContentsForm() {
		String contentViewName = controller.contentsForm(model, request, "ICD-10-CA", 1L, "ENG", null, response);
		String expectedViewName = ClassificationViewerController.CONTENT_VIEW;
		assertEquals("Should get expected view", contentViewName, expectedViewName);
	}

	@Test
	public void testGetCodeSearchResult() {
		List<CodeSearchResultBean> results = controller.getCodeSearchResult(true, request, response);
		assertTrue("Should only return one object", results.size() == 1);
	}

	@Test
	public void testGetsAndSets() {
		assertTrue("viewService is not null", controller.getViewService() != null);
		assertTrue("contextProvider is not null", controller.getContextProvider() != null);
		assertTrue("transformationServiceQualifierlist is not null",
				controller.getTransformationServiceQualifierlist() != null);
	}

	@Test
	public void testGetTreeData() throws Throwable {
		List<ConceptViewBean> viewBeans = controller.getTreeData(request, response);
		assertTrue("Should only have one object", viewBeans.size() == 1);
	}

	@Test
	public void testIcdValidationPopup() {
		ContextAccess contextAccess = contextProvider.findContext(ContextDefinition.forVersion("CCI", "2015"));
		String viewName = controller.icdValidationPopup(model, request, "ICD-10-CA",
				String.valueOf(contextAccess.getContextId().getContextId()), "ENG", "A02");
		String expectedViewName = ClassificationViewerController.ICD_VALIDATION_REPORT_VIEW;
		assertEquals("Should get expected view", viewName, expectedViewName);
	}

	@Test
	public void testSelectClassificationForm() {
		ModelAndView mav = controller.selectClassificationForm(session, "ICD-10-CA");
		ModelAndView expectedMav = new ModelAndView();
		expectedMav.setViewName(ClassificationViewerController.SELECT_CLASSIFICATION_VIEW);
		assertEquals("Should get expected view", mav.getViewName(), expectedMav.getViewName());
	}

}
