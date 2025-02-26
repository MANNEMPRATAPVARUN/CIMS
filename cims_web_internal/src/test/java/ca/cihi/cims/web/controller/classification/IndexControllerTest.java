package ca.cihi.cims.web.controller.classification;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexAddBean;
import ca.cihi.cims.web.controller.index.IndexAddController;
import ca.cihi.cims.web.controller.index.IndexBasicInfoEditController;
import ca.cihi.cims.web.controller.index.IndexRemoveController;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;

public class IndexControllerTest {

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
	@Ignore
	public void testAdd() throws Exception {
		IndexAddController controller = new IndexAddController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		final IndexModel model = new IndexModel();
		model.setType(IndexType.CCI_BOOK_INDEX);
		model.setElementId(100L);
		model.setDescription("description");

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(nullable(User.class), eq(ChangeRequestCategory.I))).thenReturn(perm);
		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn("");
		when(elementOperations.getIndexPath(0, 0L)).thenReturn("parent >");

		/*mockMvc.perform(get("/indexes/children/add?id={id}&type={type}", //
				model.getElementId(), model.getType())) //
				.andExpect(model().attribute(IndexAddController.ATTRIBUTE_BEAN, 
						IndexAddBeanMatcher.getMatcher(model)));*/
		mockMvc.perform(post("/indexes/children/add") //
				.param("id", model.getElementId() + "") //
				.param("type", model.getType().name()) //
				.param("model.description", "description") //
				.sessionAttr(WebConstants.CURRENT_USER, new User()))//
				.andExpect(model().attribute(IndexAddController.ATTRIBUTE_BEAN, 
						IndexAddBeanMatcher2.getMatcher(model)));
	}

	@Test
	@Ignore
	public void testEdit() throws Exception {
		IndexBasicInfoEditController controller = new IndexBasicInfoEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setChangeRequestService(changeRequestService);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;

		final IndexModel model = new IndexModel();
		model.setElementId(100L);
		model.setDescription("description");
		model.setParent(new IndexModel());
		model.getParent().setDescription("parent");

		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(any(User.class), eq(ChangeRequestCategory.I))).thenReturn(perm);
		when(service.getIndexById(1, Language.ENGLISH)).thenThrow(new UnsupportedElementExeption());
		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn(
				model.getParent().getDescription() + " > " + model.getDescription());
		when(service.isIndexEditableShallow(model)).thenReturn(true);
		when(service.isIndexStatusEditable(model)).thenReturn(true);
		when(service.isAddedInCurrentVersionYear(model)).thenReturn(true);
		when(service.isIndexDeletableShallow(model)).thenReturn(true);
		when(service.getNodeTitle(model.getElementId(), Language.ENGLISH.getCode())).thenReturn("title");
		when(changeRequestService.isIncomplete(any(Long.class))).thenReturn(false);

		// test unsupported
		//mockMvc.perform(get("/indexes/basicInfo/edit?id={id}", 1)).andExpect(
		//		view().name(TabularBasicInfoEditController.VIEW_UNSUPPORTED));
		// test normal
		mockMvc.perform(get("/indexes/basicInfo/edit?id={id}", model.getElementId())) //
				.andExpect(
						model().attribute(IndexBasicInfoEditController.ATTRIBUTE_BEAN, 
			                allOf(
			                        hasProperty("breadCrumbs", equalTo("parent >")),
			                        hasProperty("model", equalTo(model)),
			                        hasProperty("editable", equalTo(true)),
			                        hasProperty("saveVisible", equalTo(true)),
			                        hasProperty("removeVisible", equalTo(true))
			                )));

		mockMvc.perform(post("/indexes/basicInfo/edit")//
				.param("id", model.getElementId() + "") //
				.param("model.description", "description")) //
				.andExpect(
						model().attribute(IndexBasicInfoEditController.ATTRIBUTE_BEAN, 
			                allOf(
			                        hasProperty("model", equalTo(model)),
			                        hasProperty("nodeTitle", equalTo("title")),
			                        hasProperty("result", equalTo(BeanResult.SUCCESS))
			                )));
		// save fail
		Mockito.doThrow(new CIMSException("test"))
				.when(service)
				.saveIndex(any(OptimisticLock.class), any(ErrorBuilder.class), any(User.class), eq(model),
						eq(Language.ENGLISH));
		mockMvc.perform(post("/indexes/basicInfo/edit")//
				.param("id", model.getElementId() + "")) //
				.andExpect(
						model().attribute(IndexBasicInfoEditController.ATTRIBUTE_BEAN, 
				                allOf(
				                        hasProperty("model", equalTo(model)),
				                        hasProperty("errorMessage", equalTo("test")),
				                        hasProperty("result", equalTo(BeanResult.ERROR))
				                )));
	}

	@Test
	public void testRemove() throws Exception {
		IndexRemoveController controller = new IndexRemoveController();
		controller.setService(service);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		mockMvc.perform(get("/indexes/remove?id=100&lockTimestamp=0") //
				.sessionAttr(WebConstants.CURRENT_USER, new User())).andExpect(
				content().string("{\"key\":null,\"value\":null}"));
	}

}


class IndexAddBeanMatcher extends TypeSafeMatcher<IndexAddBean> {

	final IndexModel model;
	
	IndexAddBeanMatcher(IndexModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(IndexAddBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(true, bean.isSaveVisible());
		assertEquals(false, bean.isRemoveVisible());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher matches");
    }

    public static Matcher<IndexAddBean> 
    	getMatcher(IndexModel model) {
        return new IndexAddBeanMatcher(model);
    }
}


class IndexAddBeanMatcher2 extends TypeSafeMatcher<IndexAddBean> {

	final IndexModel model;
	
	IndexAddBeanMatcher2(IndexModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(IndexAddBean bean) {
		assertEquals(null, bean.getModel().getElementId());
		assertEquals("parent >", bean.getBreadCrumbs());
		assertEquals("description", bean.getModel().getDescription());
		assertEquals(BeanResult.SUCCESS, bean.getResult());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher matches");
    }

    public static Matcher<IndexAddBean> 
    	getMatcher(IndexModel model) {
        return new IndexAddBeanMatcher2(model);
    }
}