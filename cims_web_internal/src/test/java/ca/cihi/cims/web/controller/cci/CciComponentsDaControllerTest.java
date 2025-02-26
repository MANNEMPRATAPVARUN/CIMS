package ca.cihi.cims.web.controller.cci;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciAgentGroup;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;

@SuppressWarnings( { "static-access", "unchecked" })
public class CciComponentsDaControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CciAuxService auxService;
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
	private ClassService classService;
	@Mock
	private CciAttributeType attributeType;
	@Mock
	private CciReferenceAttribute refAttribute;

	@Mock
	private CciGenericAttribute genericAttribute;

	@Mock
	private CciAgentGroup agentGroup;
	@Mock
	private CciDeviceAgentComponent deviceAgentComponent;

	private final CciComponentsDaController controller = new CciComponentsDaController();

	// -----------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setAuxService(auxService);
		controller.setContextProvider(contextProvider);
		controller.setDtService(dtService);
		controller.setLookupService(lookupService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(contextProvider.findContext(any(ContextDefinition.class))).thenReturn(access);
		when(contextProvider.findContext(any(ContextIdentifier.class))).thenReturn(access);
		when(access.createChangeContext(nullable(Long.class))).thenReturn(access);
		when(refAttribute.getType()).thenReturn(attributeType);
		when(access.findOne(any(Ref.class), any(FindCriterion.class))).thenReturn(attributeType);
		when(access.createWrapper(eq(CciGenericAttribute.class), nullable(String.class), nullable(String.class))).thenReturn(
				genericAttribute);

		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(access.getContextId()).thenReturn(contextId);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), any(String.class))).thenReturn(
				contextId);

		BusinessKeyGenerator bkg = new BusinessKeyGenerator();
		bkg.setClassService(classService);
		bkg.registerInstance();
	}

	@Test
	public void testList() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		final List<CciComponentModel> values = new ArrayList<CciComponentModel>();
		values.add(new CciComponentModel());
		values.add(new CciComponentModel());
		when(
				auxService.getComponentsSQL(eq("CCI"), eq(comps.getVersionCode()), eq(comps.getSection()), eq(comps
						.getStatus()), eq("DeviceAgent"), eq("DeviceAgentToSectionCPV"))).thenReturn(values);
		mockMvc.perform(get("/daComponents") //
				.sessionAttr("cciComponentsForViewer", comps)) //
				.andExpect(model().attribute("components", values))//
				.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, values.size()));
	}

	@Test
	public void testListDiagram() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		when(access.load(eq(200L))).thenReturn(deviceAgentComponent);
		when(deviceAgentComponent.getCode()).thenReturn("code");
		when(deviceAgentComponent.getAgentGroup()).thenReturn(agentGroup);
		when(agentGroup.getStatus()).thenReturn("ACTIVE");
		when(agentGroup.getDescription(any(String.class))).thenReturn("description");
		when(agentGroup.getElementId()).thenReturn(999L);
		List<CciAgentGroup> list = Arrays.asList(agentGroup);
		when(access.findAll(eq(CciAgentGroup.class))).thenReturn(list.iterator());
		mockMvc.perform(get("/daComponents/diagram") // 
				.param("e", "200") //
				.sessionAttr("cciComponentsForViewer", comps)) //
				.andExpect(model().attribute("agentGroups", hasEntry(agentGroup.getElementId() + "", "description")))
				.andExpect(model().attribute("agentGroups", aMapWithSize(1)))
				.andExpect(model().attribute("groupCode", agentGroup.getElementId())) //
				.andExpect(model().attribute("code", deviceAgentComponent.getCode()));
	}

	@Test
	public void testUpdateDiagram() throws Exception {
		CciComponents comps = new CciComponents();
		comps.setStatus("SOME");
		comps.setSection("1");
		comps.setVersionCode("2015");
		when(access.load(eq(200L))).thenReturn(deviceAgentComponent);
		when(deviceAgentComponent.getCode()).thenReturn("code");
		when(deviceAgentComponent.getAgentGroup()).thenReturn(agentGroup);
		when(agentGroup.getStatus()).thenReturn("ACTIVE");
		when(agentGroup.getDescription(any(String.class))).thenReturn("description");
		when(agentGroup.getElementId()).thenReturn(999L);
		List<CciAgentGroup> list = Arrays.asList(agentGroup);
		when(access.findAll(eq(CciAgentGroup.class))).thenReturn(list.iterator());
		when(access.load(eq(agentGroup.getElementId()))).thenReturn(agentGroup);
		mockMvc
				.perform(post("/daComponents/diagram") // 
						.param("e", "200") //
						.param("ag", agentGroup.getElementId() + "") //
						.sessionAttr("cciComponentsForViewer", comps))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

}
