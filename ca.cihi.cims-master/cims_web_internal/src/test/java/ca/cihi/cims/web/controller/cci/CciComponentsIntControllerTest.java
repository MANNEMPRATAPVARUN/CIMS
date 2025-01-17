package ca.cihi.cims.web.controller.cci;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.TransformCCIComponentService;
import ca.cihi.cims.util.DtdValidator;

@SuppressWarnings( { "static-access" })
public class CciComponentsIntControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CciAuxService auxService;
	@Mock
	private DtdValidator dtdValidator;
	@Mock
	private DisplayTagUtilService dtService;
	@Mock
	private LookupService lookupService;
	@Mock
	private ContextProvider contextProvider;
	@Mock
	protected MessageSource messageSource;

	@Mock
	private ContextAccess access;
	@Mock
	private CciInterventionComponent interventionComponent;

	@Mock
	private TransformCCIComponentService transformService;
	
	
	private final CciComponentsIntController controller = new CciComponentsIntController();

	// -----------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setAuxService(auxService);
		controller.setContextProvider(contextProvider);
		controller.setDtService(dtService);
		controller.setDtdValidator(dtdValidator);
		controller.setLookupService(lookupService);
		controller.setTransformService(transformService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(contextProvider.findContext(nullable(ContextDefinition.class))).thenReturn(access);
		when(contextProvider.findContext(nullable(ContextIdentifier.class))).thenReturn(access);
		when(access.createChangeContext(nullable(Long.class))).thenReturn(access);
	}

	@Test
	public void testNewDefinition() throws Exception {
		mockMvc.perform(get("/intComponents/newDefinition")) // 
				.andExpect(view().name(controller.LIST_COMPONENTS_INTERVENTION_DEFINITION));
	}

	@Test
	public void testRead() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		final List<CciComponentModel> values = new ArrayList<CciComponentModel>();
		values.add(new CciComponentModel());
		values.add(new CciComponentModel());
		when(
				auxService.getComponentsSQL(eq("CCI"), eq(comps.getVersionCode()), eq(comps.getSection()), eq(comps
						.getStatus()), eq("Intervention"), eq("InterventionToSectionCPV"))).thenReturn(values);
		mockMvc.perform(get("/intComponents") //
				.sessionAttr("cciComponentsForViewer", comps)) //
				.andExpect(model().attribute("components", values))//
				.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, values.size()));
	}

	@Test
	public void testReadDiagram() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		when(access.load(eq(200L))).thenReturn(interventionComponent);
		when(interventionComponent.getDefinitionTitle(eq(Language.ENGLISH.getCode()))).thenReturn("e");
		when(interventionComponent.getDefinitionTitle(eq(Language.FRENCH.getCode()))).thenReturn("f");
		mockMvc.perform(get("/intComponents/diagram") // 
				.param("e", "200") //
				.sessionAttr("cciComponentsForViewer", comps)) //
				.andExpect(model().attribute("definitionEng", "e")) //
				.andExpect(model().attribute("definitionFra", "f"));
	}

	@Test
	public void testUpdateDiagram() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		when(access.load(eq(200L))).thenReturn(interventionComponent);
		doNothing().when(transformService).transformCciComponent(nullable(String.class), nullable(String.class), nullable(CciComponent.class), nullable(Long.class), nullable(ContextAccess.class), nullable(Boolean.class)); 
		mockMvc
				.perform(post("/intComponents/diagram") // 
						.param("e", "200") //
						.param("de", "<e/>") //
						.param("df", "<f/>") //
						.sessionAttr("cciComponentsForViewer", comps))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
		verify(interventionComponent).setDefinitionTitle(Language.ENGLISH.getCode(), "<e/>");
		verify(interventionComponent).setDefinitionTitle(Language.FRENCH.getCode(), "<f/>");
	}

	@Test
	public void testValidateDefinitionXML() throws Exception {
		mockMvc.perform(post("/intComponents/validateDefinitionXML") // 
				.param("de", "<e/>") //
				.param("df", "<f/>")) //
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":[]}"));
	}

}
