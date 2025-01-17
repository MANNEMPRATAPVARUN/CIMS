package ca.cihi.cims.web.controller.classification;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.model.tabular.TabularConceptXmlType;
import ca.cihi.cims.model.tabular.validation.TabularConceptCciValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationSetModel;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.NotificationService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.util.CimsConfiguration;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.tabular.TabularAddBean;
import ca.cihi.cims.web.bean.tabular.TabularXmlBean;
import ca.cihi.cims.web.controller.tabular.TabularAddController;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.controller.tabular.TabularConceptXmlController;
import ca.cihi.cims.web.controller.tabular.TabularDiagramShowController;
import ca.cihi.cims.web.controller.tabular.TabularQualifierAddController;
import ca.cihi.cims.web.controller.tabular.TabularReferenceLinksReportController;
import ca.cihi.cims.web.controller.tabular.TabularRemoveController;
import ca.cihi.cims.web.controller.tabular.TabularValidationController;
import ca.cihi.cims.web.controller.tabular.TabularValidationSetReportController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class TabularControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ViewService viewService;
	@Mock
	private CimsConfiguration config;
	@Mock
	private ClassificationService service;
	@Mock
	private ClassificationService context;
	@Mock
	private NotificationService notificationService;
	@Mock
	private ChangeRequestService changeRequestService;

	// --------------------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(config.isTracePerformanceEnabled()).thenReturn(true);
	}

	@Test
	@Ignore
	public void testAdd() throws Exception {
		TabularAddController controller = new TabularAddController();
		controller.setService(service);
		controller.setConfig(config);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		TabularConceptModel cciCode = new TabularConceptModel();
		cciCode.setType(TabularConceptType.CCI_CCICODE);
		cciCode.setElementId(100L);

		TabularConceptModel cciGroup = new TabularConceptModel();
		cciGroup.setType(TabularConceptType.CCI_GROUP);
		cciGroup.setElementId(200L);

		TabularConceptModel cciRubric = new TabularConceptModel();
		cciRubric.setType(TabularConceptType.CCI_RUBRIC);
		cciRubric.setElementId(300L);

		TabularConceptModel icdChapter = new TabularConceptModel();
		icdChapter.setType(TabularConceptType.ICD_CATEGORY);
		icdChapter.setElementId(400L);

		List<TabularConceptModel> list = Arrays.asList(cciCode, cciGroup, cciRubric, icdChapter);

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(any(User.class), eq(ChangeRequestCategory.T))).thenReturn(perm);
		when(service.getTabularConceptById(0)).thenThrow(new RootElementExeption("@"));

		CciTabular cciRubricTabular = Mockito.mock(CciTabular.class);
		when(cciRubricTabular.getGroupComponent()).thenReturn(Mockito.mock(CciGroupComponent.class));
		when(service.getTabularById(cciRubric.getElementId())).thenReturn(cciRubricTabular);

		for (TabularConceptModel model : list) {
			when(service.getTabularConceptById(model.getElementId())).thenReturn(model);
			when(service.getTabularConceptLightById(model.getElementId())).thenReturn(model);
		}

		// test normal concept
		for (final TabularConceptModel model : list) {
			mockMvc.perform(get("/tabulars/children/add?id={id}&type={type}", //
					model.getElementId(), model.getType())) //
					.andExpect(
							model().attribute(TabularBasicInfoEditController.ATTRIBUTE_BEAN, TabularAddBeanMatcher.getMatcher(model)));
							
			mockMvc.perform(post("/tabulars/children/add") //
					.param("id", model.getElementId() + "") //
					.param("type", model.getType() + "") //
					.param("cciGroup", "1") //
					.param("cciIntervention", "1") //
					.sessionAttr(WebConstants.CURRENT_USER, new User()))//
					.andExpect(
							model().attribute(TabularBasicInfoEditController.ATTRIBUTE_BEAN, 
									hasProperty("result", equalTo(BeanResult.SUCCESS))));
		}
	}

	@Test
	@Ignore
	public void testEdit() throws Exception {
		TabularBasicInfoEditController controller = new TabularBasicInfoEditController();
		controller.setService(service);
		controller.setChangeRequestService(changeRequestService);
		controller.setConfig(config);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final TabularConceptModel model = new TabularConceptModel();
		model.setElementId(100L);
		model.setType(TabularConceptType.CCI_CCICODE);

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(nullable(User.class), eq(ChangeRequestCategory.T))).thenReturn(perm);
		when(service.getTabularConceptById(0)).thenThrow(new RootElementExeption("@"));
		when(service.getTabularConceptById(model.getElementId())).thenReturn(model);
		when(changeRequestService.isIncomplete(nullable(Long.class))).thenReturn(false);

		// test root concept
		mockMvc.perform(get("/tabulars/basicInfo/edit?id={id}", 0))
			.andExpect(view().name(TabularBasicInfoEditController.VIEW_ROOT));

		// test normal concept
		mockMvc.perform(get("/tabulars/basicInfo/edit?id={id}", model.getElementId())).andExpect(
				model().attribute(TabularBasicInfoEditController.ATTRIBUTE_BEAN, 
	                allOf(
	                        hasProperty("model", equalTo(model)),
	                        hasProperty("codeEditable", equalTo(false)),
	                        hasProperty("codeVisible", equalTo(true)),
	                        hasProperty("englishEditable", equalTo(true)),
	                        hasProperty("frenchEditable", equalTo(true)),
	                        hasProperty("editable", equalTo(true)),
	                        hasProperty("saveVisible", equalTo(true)),
	                        hasProperty("removeVisible", equalTo(true))
	                )));

		mockMvc.perform(post("/tabulars/basicInfo/edit").param("id", model.getElementId() + "")).andExpect(
				model().attribute(TabularBasicInfoEditController.ATTRIBUTE_BEAN, 
	                allOf(
	                        hasProperty("model", equalTo(model)),
	                        hasProperty("result", equalTo(BeanResult.SUCCESS))
	                )));
	}

	@Test
	public void testQualifierAdd() throws Exception {
		TabularQualifierAddController controller = new TabularQualifierAddController();
		controller.setContext(context);
		controller.setNotificationService(notificationService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		mockMvc.perform(post("/tabulars/qualifier/add") //
				.param("comment", "comment") //
				.sessionAttr(WebConstants.CURRENT_USER, new User()) //
		).andExpect(content().string(""));
	}

	@Test
	@Ignore
	public void testReferenceLinksReport() throws Exception {
		TabularReferenceLinksReportController controller = new TabularReferenceLinksReportController();
		controller.setService(service);
		controller.setViewService(viewService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		mockMvc.perform(get("/tabulars/report/referenceLinks?code=XXXX") //
		).andExpect(content().string(""));
	}

	@Test
	public void testRemove() throws Exception {
		TabularRemoveController controller = new TabularRemoveController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		mockMvc.perform(get("/tabulars/remove?id=100&lockTimestamp=0") //
				.sessionAttr(WebConstants.CURRENT_USER, new User())).andExpect(
				content().string("{\"key\":null,\"value\":null}"));
	}

	@Test
	public void testShowDiagram() throws Exception {
		TabularDiagramShowController controller = new TabularDiagramShowController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(service.getTabularDiagramContent(100, Language.ENGLISH)).thenReturn(new byte[] {});
		mockMvc.perform(get("/tabulars/diagram/show?id=100&lang=ENGLISH"));
	}

	@Test
	@Ignore
	public void testValidation() throws Exception {
		TabularValidationController controller = new TabularValidationController();
		controller.setService(service);
		controller.setChangeRequestService(changeRequestService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(changeRequestService.isIncomplete(any(Long.class))).thenReturn(false);

		final List<TabularConceptValidationDadHoldingModel> dataHoldings = new ArrayList<TabularConceptValidationDadHoldingModel>();
		final TabularConceptValidationDadHoldingModel dadHolding = new TabularConceptValidationDadHoldingModel();
		dadHolding.setElementId(9999);
		dadHolding.setCode(FacilityType.CODE_DAD);
		dataHoldings.add(dadHolding);

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final TabularConceptCciValidationSetModel cciBlock = new TabularConceptCciValidationSetModel();
		{
			cciBlock.setElementId(100);
			final TabularConceptModel cciBlockConcept = new TabularConceptModel();
			cciBlockConcept.setElementId(cciBlock.getElementId());
			cciBlockConcept.setType(TabularConceptType.CCI_BLOCK);
			{
				when(service.getTabularConceptById(cciBlockConcept.getElementId(), true)).thenReturn(cciBlockConcept);
				when(service.getTabularValidationSet(cciBlockConcept.getElementId(), dadHolding.getElementId()))
						.thenReturn(cciBlock);
			}
		}
		final TabularConceptIcdValidationSetModel icdBlock = new TabularConceptIcdValidationSetModel();
		{
			icdBlock.setElementId(200);
			icdBlock.setDxTypeId(888L);
			final TabularConceptModel icdBlockConcept = new TabularConceptModel();
			icdBlockConcept.setElementId(icdBlock.getElementId());
			icdBlockConcept.setType(TabularConceptType.ICD_BLOCK);
			{
				when(service.getTabularConceptById(icdBlockConcept.getElementId(), true)).thenReturn(icdBlockConcept);
				when(service.getTabularValidationSet(icdBlockConcept.getElementId(), dadHolding.getElementId()))
						.thenReturn(icdBlock);
			}
		}

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getDataHoldings(Language.ENGLISH)).thenReturn(dataHoldings);
		when(service.getChangeRequestClassificationPermission(any(User.class), eq(ChangeRequestCategory.T)))
				.thenReturn(perm);

		List<TabularConceptValidationSetModel> models = Arrays.asList(cciBlock, icdBlock);
		// show
		for (final TabularConceptValidationSetModel model : models) {
			mockMvc.perform(get("/tabulars/validation/edit?id={id}", model.getElementId())//
					.sessionAttr(WebConstants.CURRENT_USER, new User()))
					//
					.andExpect(view().name(TabularValidationController.VIEW_EDIT))
					//
					.andExpect(
							model().attribute(TabularValidationController.ATTRIBUTE_BEAN,
					                allOf(
					                        hasProperty("model", equalTo(model)),
					                        hasProperty("dataHoldings", equalTo(dataHoldings))
					                )));
		}
		// remove
		mockMvc.perform(get("/tabulars/validation/remove?id={id}", cciBlock.getElementId())//
		).andExpect(model().attribute(TabularValidationController.ATTRIBUTE_BEAN, 
                allOf(
                        hasProperty("model", equalTo(null)),
                        hasProperty("result", equalTo(BeanResult.SUCCESS))
                )));
		// save
		mockMvc.perform(post("/tabulars/validation/edit") //
				.param("id", cciBlock.getElementId() + "") //
		).andExpect(model().attribute(TabularValidationController.ATTRIBUTE_BEAN, 
                allOf(
                        hasProperty("model", equalTo(cciBlock)),
                        hasProperty("result", equalTo(BeanResult.SUCCESS))
                )));
	}

	@Test
	@Ignore
	public void testValidationSetsReport() throws Exception {
		TabularValidationSetReportController controller = new TabularValidationSetReportController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		final TabularConceptModel model = new TabularConceptModel();
		model.setElementId(100L);
		model.setType(TabularConceptType.CCI_CCICODE);
		when(service.getTabularConceptById(model.getElementId(), false)).thenReturn(model);

		mockMvc.perform(get("/tabulars/report/validationSets?id=100") //
		).andExpect(view().name(TabularValidationSetReportController.VIEW));
	}

	@Test
	@Ignore
	public void testXml() throws Exception {
		TabularConceptXmlController controller = new TabularConceptXmlController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		long conceptId = 100L;
		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final TabularConceptXmlModel model = new TabularConceptXmlModel();
		model.setType(TabularConceptXmlType.NOTE);
		model.setElementId(conceptId);

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptNonInfoPermission(any(User.class))).thenReturn(perm);
		when(service.getTabularXml(model.getElementId(), model.getType())).thenReturn(model);

		// test root concept
		mockMvc.perform(get("/tabulars/xml/edit?id={id}&tab={tab}", //
				model.getElementId(), model.getType())//
				.sessionAttr(WebConstants.CURRENT_USER, new User())) //
				.andExpect(view().name(TabularConceptXmlController.VIEW_EDIT)) //
				.andExpect(model().attribute(TabularConceptXmlController.ATTRIBUTE_BEAN,
                        allOf(
                                hasProperty("model", equalTo(model)),
                                hasProperty("englishEditable", is(true)),
                                hasProperty("frenchEditable", is(true)),
                                hasProperty("englishVisible", is(true)),
                                hasProperty("frenchVisible", is(true))
                        )						
					));
		mockMvc.perform(post("/tabulars/xml/edit")//
				.param("id", model.getElementId() + "") //
				.param("tab", model.getType() + "") //
				.param("model.frenchXml", "french") //
				.param("model.englishXml", "english") //
		).andExpect(model().attribute(TabularConceptXmlController.ATTRIBUTE_BEAN, 
				TabularXmlBeanMatcher.getMatcher(model)));
	}
}


class TabularAddBeanMatcher extends TypeSafeMatcher<TabularAddBean> {

	final TabularConceptModel model;
	
	TabularAddBeanMatcher(TabularConceptModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(TabularAddBean bean) {
		assertEquals(model, bean.getModel().getParent());
		assertEquals(bean.getType().getClassification() == Classification.ICD,
				bean.isCodeEditable());
		assertEquals(false, bean.isEnglishEditable());
		assertEquals(false, bean.isFrenchEditable());
		// assertEquals(false, bean.isEditable());
		assertEquals(true, bean.isSaveVisible());
		assertEquals(false, bean.isRemoveVisible());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher matches");
    }

    public static Matcher<TabularAddBean> 
    	getMatcher(TabularConceptModel model) {
        return new TabularAddBeanMatcher(model);
    }
}


class TabularXmlBeanMatcher extends TypeSafeMatcher<TabularXmlBean> {

	final TabularConceptXmlModel model;
	
	TabularXmlBeanMatcher(TabularConceptXmlModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(TabularXmlBean bean) {
		assertEquals(model, bean.getModel());
		assertEquals("english", bean.getModel().getEnglishXml());
		assertEquals("french", bean.getModel().getFrenchXml());
		assertEquals(BeanResult.SUCCESS, bean.getResult());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher matches");
    }

    public static Matcher<TabularXmlBean> 
    	getMatcher(TabularConceptXmlModel model) {
        return new TabularXmlBeanMatcher(model);
    }
}
