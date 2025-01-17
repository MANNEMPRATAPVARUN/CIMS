package ca.cihi.cims.web.controller.cci;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.hg.HGContextAccess;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.model.ContextStatus;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

public class CciComponentsControllerTest {
	private CciComponentsController cciComponentsController;

	@Mock
	private ElementOperations elementOperations;
	@Mock
	private NonContextOperations nonContextOperations;
	@Mock
	private CommonElementOperations commonOperations;
	@Mock
	private ContextOperations operations;
	@Mock
	private ContextProvider contextProvider;
	@Mock
	private CciAuxService auxService;
	@Mock
	private DisplayTagUtilService dtService;
	@Mock
	private BindingResult result;
	@Mock
	private ModelMap model;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpSession session;
	@Mock
	private CciComponents viewerModel;
	@Mock
	private HGContextAccess baseContext;
	@Mock
	private ContextAccess context;
	@Mock
	private CciComponentModel cm;
	@Mock
	private CciTabular cciTabular;
	@Mock
	private ContextIdentifier contextId;
	@Mock
	private ClassService classService;
	@Mock
	private CciComponent cciComponent;

	// -----------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		cciComponentsController = new CciComponentsController();
		cciComponentsController.setOperations(operations);

		// mock elementoperations so that loading element does nothing
		when(elementOperations.loadElement((ContextIdentifier) any(), nullable(Long.class)))
				.thenReturn(new ConceptVersion());
		cciComponentsController.setElementOperations(elementOperations);

		// mock to return false for has concept been published
		when(operations.hasConceptBeenPublished(nullable(Long.class))).thenReturn(false);
		cciComponentsController.setOperations(operations);

		cciComponentsController.setCommonOperations(commonOperations);
		cciComponentsController.setNonContextOperations(nonContextOperations);

		// mock viewermodel
		when(viewerModel.getVersionCode()).thenReturn("1");
		when(viewerModel.getSection()).thenReturn("1");

		// mock ccicomponentmodel
		when(cm.getCode()).thenReturn("1");
		when(cm.getComponentModelType()).thenReturn("group");

		// mock session to return mocked viewer model
		when(session.getAttribute("cciComponentsForViewer")).thenReturn(viewerModel);

		when(auxService.getCCISections(nullable(String.class), nullable(String.class))).thenReturn(new HashMap<String, String>());
		cciComponentsController.setAuxService(auxService);

		when(baseContext.createChangeContext(null)).thenReturn(context);
		when(contextId.getBaseClassification()).thenReturn("CCI");
		when(context.getContextId()).thenReturn(contextId);
		when(context.realizeChangeContext(nullable(Boolean.class))).thenReturn(new HashMap<ElementVersion, ElementVersion>());
		doNothing().when(context).persist();

		Set<ContextIdentifier> contexts = new HashSet<ContextIdentifier>();
		ContextIdentifier contextIdentifier = new ContextIdentifier();
		contextIdentifier.setContextStatus(ContextStatus.OPEN.name());
		contexts.add(contextIdentifier);
		when(contextProvider.findBaseClassificationVersionYearVersionCodes(nullable(String.class))).thenReturn(contexts);
		when(contextProvider.findContext((ContextDefinition) any())).thenReturn(baseContext);
		cciComponentsController.setContextProvider(contextProvider);

	}

	@Test
	public void testLoadMain() {
		assertEquals("cciComponents", cciComponentsController.loadViewerMain(request, session, model));
	}

	@Test
	public void testProcessComponentRemoval() {
		doNothing().when(nonContextOperations).remove((ContextIdentifier) any(), nullable(Long.class));
		when(
				commonOperations.isConceptEligibleForRemoval((ContextIdentifier) any(),
						(ConceptVersion) any())).thenReturn(true);
		// should be no errors
		assertEquals(cciComponentsController.processComponentRemoval(session, 1, "1").getStatus(),
				ValidationResponse.Status.SUCCESS.toString());

	}

	/*
	 * @Test
	 * 
	 * @Ignore // creating new cci component can't be mocked...leave for now public void testSaveNewComponent () {
	 * when(classService.getCachedTableName(nullable(String.class), nullable(String.class))).thenReturn("t");
	 * 
	 * List<CciTabular> dummy = new ArrayList<CciTabular>(); when(cciTabular.getCode()).thenReturn("1");
	 * dummy.add(cciTabular);
	 * 
	 * when(cm.getComponentModelType()).thenReturn("group"); when(cm.getCode()).thenReturn("1");
	 * 
	 * when(context.find((Ref<CciTabular>)any(), (FindCriterion)any(),
	 * (FindCriterion)any())).thenReturn(dummy.iterator()); // should be no errors
	 * assertEquals(cciComponentsController.saveNewComponent(session, model, cm, result, "en", "fr").getStatus(),
	 * ValidationResponse.Status.SUCCESS); }
	 */

	@Test
	public void testUpdateComponent() throws UnsupportedEncodingException {
		when(cciComponent.getCode()).thenReturn("1");
		when(context.load(nullable(Long.class))).thenReturn(cciComponent);

		when(
				auxService.getComponents(nullable(String.class), nullable(String.class), nullable(String.class), nullable(String.class), (Class<?>) any(),
						nullable(String.class))).thenReturn(new ArrayList<CciComponentModel>());
		when(auxService.getComponentReferences(nullable(String.class), nullable(String.class), nullable(Long.class))).thenReturn(
				new ArrayList<CciComponentRefLink>());
		assertEquals(cciComponentsController.updateComponent(session, cm, result, model).getStatus(),
				ValidationResponse.Status.SUCCESS.toString());
	}
}
