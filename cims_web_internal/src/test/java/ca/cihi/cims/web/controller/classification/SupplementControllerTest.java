package ca.cihi.cims.web.controller.classification;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.supplement.SupplementMatter;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.supplement.SupplementAddBean;
import ca.cihi.cims.web.bean.supplement.SupplementBasicInfoBean;
import ca.cihi.cims.web.bean.tabular.TabularBasicInfoBean;
import ca.cihi.cims.web.controller.supplement.SupplementAddController;
import ca.cihi.cims.web.controller.supplement.SupplementBasicInfoEditController;
import ca.cihi.cims.web.controller.supplement.SupplementRemoveController;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;

public class SupplementControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CimsConfiguration config;
	@Mock
	private ClassificationService service;
	@Mock
	protected ElementOperations elementOperations;
	@Mock
	private ChangeRequestService changeRequestService;

	// --------------------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(config.isTracePerformanceEnabled()).thenReturn(true);
	}

	@Test
	public void testAdd() throws Exception {
		SupplementAddController controller = new SupplementAddController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final SupplementModel model = new SupplementModel();
		model.setElementId(100L);
		model.setDescription("description");
		model.setParent(new SupplementModel());
		model.getParent().setDescription("parent");

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(nullable(User.class), eq(ChangeRequestCategory.S))).thenReturn(perm);
		when(service.getSupplementById(model.getElementId(), Language.ENGLISH)).thenReturn(model);

		mockMvc.perform(get("/supplements/children/add?id={id}", //
				model.getElementId())) //
				.andExpect(model().attribute(SupplementAddController.ATTRIBUTE_BEAN, SupplementAddBeanMatcher.getMatcher(model)));
						
		mockMvc.perform(post("/supplements/children/add") //
				.param("id", model.getElementId() + "") //
				.param("model.description", "description") //
				.param("model.matter", SupplementMatter.BACK.name()) //
				.sessionAttr(WebConstants.CURRENT_USER, new User()))//
				.andExpect(model().attribute(SupplementAddController.ATTRIBUTE_BEAN, SupplementAddBeanMatcher2.getMatcher()));
	}

	@Test
	@Ignore
	public void testEdit() throws Exception {
		SupplementBasicInfoEditController controller = new SupplementBasicInfoEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setChangeRequestService(changeRequestService);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final SupplementModel model = new SupplementModel();
		model.setElementId(100L);
		model.setDescription("description");
		model.setParent(new SupplementModel());
		model.getParent().setDescription("parent");

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(nullable(User.class), eq(ChangeRequestCategory.S))).thenReturn(perm);
		when(service.getSupplementById(0, Language.ENGLISH)).thenThrow(new RootElementExeption("@"));
		when(service.getSupplementById(1, Language.ENGLISH)).thenThrow(new UnsupportedElementExeption());
		when(service.getSupplementById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(service.getNodeTitle(model.getElementId(), Language.ENGLISH.getCode())).thenReturn("title");

		when(service.isSupplementEditableShallow(model)).thenReturn(true);
		when(service.isSupplementStatusEditable(model)).thenReturn(true);
		when(service.isAddedInCurrentVersionYear(model)).thenReturn(true);
		when(service.isSupplementDeletableShallow(model)).thenReturn(true);
		when(changeRequestService.isIncomplete(any(Long.class))).thenReturn(false);

		// test root
		mockMvc.perform(get("/supplements/basicInfo/edit?id={id}", 0))
			.andExpect(view().name(SupplementBasicInfoEditController.VIEW_ROOT));
		// test unsupported
		mockMvc.perform(get("/supplements/basicInfo/edit?id={id}", 1)).andExpect(
				view().name(TabularBasicInfoEditController.VIEW_UNSUPPORTED));
		// test normal
		mockMvc.perform(get("/supplements/basicInfo/edit?id={id}", model.getElementId())) //
		.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("breadCrumbs", equalTo("parent > "))))
		.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("model", equalTo(model))))
		.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("editable", equalTo(true))))
		.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("saveVisible", equalTo(true))))
		.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("removeVisible", equalTo(true))));

//		new ArgumentMatcher<Object>() {
//									@Override
//									public boolean matches(Object arg0) {
//										SupplementBasicInfoBean bean = (SupplementBasicInfoBean) arg0;
//										assertEquals("parent > ", bean.getBreadCrumbs());
//										assertEquals(model, bean.getModel());
//										assertEquals(true, bean.isEditable());
//										assertEquals(true, bean.isSaveVisible());
//										assertEquals(true, bean.isRemoveVisible());
//										return true;
//									}
//								}));
		// save ok
		mockMvc.perform(post("/supplements/basicInfo/edit")//
				.param("id", model.getElementId() + "") //
				.param("model.description", "description")) //
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("model", equalTo(model))))
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("nodeTitle", equalTo("title"))))
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("result", equalTo(BeanResult.SUCCESS))));
						
		// save fail
		Mockito.doThrow(new CIMSException("test"))
				.when(service)
				.saveSupplement(any(OptimisticLock.class), any(ErrorBuilder.class), any(User.class), eq(model),
						eq(Language.ENGLISH));
		mockMvc.perform(post("/supplements/basicInfo/edit")//
				.param("id", model.getElementId() + "")) //
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("model", equalTo(model))))
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("errorMessage", equalTo("test"))))
				.andExpect(model().attribute(SupplementBasicInfoEditController.ATTRIBUTE_BEAN, hasProperty("result", equalTo(BeanResult.ERROR))));

	}

	@Test
	public void testRemove() throws Exception {
		SupplementRemoveController controller = new SupplementRemoveController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		mockMvc.perform(get("/supplements/remove?id=100&lockTimestamp=0") //
				.sessionAttr(WebConstants.CURRENT_USER, new User())).andExpect(
				content().string("{\"key\":null,\"value\":null}"));
	}
}

class SupplementAddBeanMatcher extends TypeSafeMatcher<SupplementAddBean> {

	final SupplementModel model;
	
	SupplementAddBeanMatcher(SupplementModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(SupplementAddBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(true, bean.isSaveVisible());
		assertEquals(false, bean.isRemoveVisible());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("SupplementAddBeanMatcher matches");
    }

    public static Matcher<SupplementAddBean> 
    	getMatcher(SupplementModel model) {
        return new SupplementAddBeanMatcher(model);
    }
}

class SupplementAddBeanMatcher2 extends TypeSafeMatcher<SupplementAddBean> {

	@Override
    protected boolean matchesSafely(SupplementAddBean bean) {
		assertEquals(0, bean.getModel().getElementId());
		assertEquals("description", bean.getModel().getDescription());
		assertEquals(SupplementMatter.BACK, bean.getModel().getMatter());
		assertEquals(BeanResult.SUCCESS, bean.getResult());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("SupplementAddBeanMatcher matches");
    }

    public static Matcher<SupplementAddBean> 
    	getMatcher() {
        return new SupplementAddBeanMatcher2();
    }
}