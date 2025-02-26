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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
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
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.TransformCCIComponentService;
import ca.cihi.cims.util.DtdValidator;


@SuppressWarnings( { "static-access" })
public class CciComponentsGroupControllerTest {

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
	private CciGroupComponent cciGroupComponent;

	@Mock
	private TransformCCIComponentService transformService;

	private final CciComponentsGroupController controller = new CciComponentsGroupController();

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
						.getStatus()), eq("GroupComp"), eq("GroupCompToSectionCPV"))).thenReturn(values);
		mockMvc.perform(get("/groupComponents") //
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
		when(access.load(eq(200L))).thenReturn(cciGroupComponent);
		when(cciGroupComponent.getDefinitionTitle(eq(Language.ENGLISH.getCode()))).thenReturn("e");
		when(cciGroupComponent.getDefinitionTitle(eq(Language.FRENCH.getCode()))).thenReturn("f");
		mockMvc.perform(get("/groupComponents/diagram") // 
				.param("e", "200") //
				.sessionAttr("cciComponentsForViewer", comps)) //
				.andExpect(model().attribute("definitionEng", "e")) //
				.andExpect(model().attribute("definitionFra", "f"));
	}

	@Ignore
	@Test
	public void testUpdateDiagram() throws Exception {
		doNothing().when(transformService).transformCciComponent(nullable(String.class),nullable(String.class) , nullable(CciComponent.class), nullable(ContextAccess.class), nullable(Boolean.class));
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		when(access.load(eq(200L))).thenReturn(cciGroupComponent);
	
		mockMvc
				.perform(post("/groupComponents/diagram") // 
						.param("e", "200") //
						.param("de", "<e/>") //
						.param("df", "<f/>") //
						.sessionAttr("cciComponentsForViewer", comps))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
		verify(cciGroupComponent).setDefinitionTitle(Language.ENGLISH.getCode(), "<e/>");
		verify(cciGroupComponent).setDefinitionTitle(Language.FRENCH.getCode(), "<f/>");
	}

}
