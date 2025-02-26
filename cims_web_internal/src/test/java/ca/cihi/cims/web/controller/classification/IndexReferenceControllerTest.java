package ca.cihi.cims.web.controller.classification;

import static ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController.VIEW_UNSUPPORTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
import java.util.HashMap;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.Language;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.index.DrugDetailType;
import ca.cihi.cims.model.index.IndexCategoryReferenceModel;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexTermReferenceModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.model.index.NeoplasmDetailType;
import ca.cihi.cims.model.index.TabularReferenceModel;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.util.CimsConfiguration;
import ca.cihi.cims.web.bean.index.IndexIcd1An2AndCCICodeValueReferencesBean;
import ca.cihi.cims.web.bean.index.IndexIcd3An4CodeValueReferencesBean;
import ca.cihi.cims.web.bean.index.IndexTabularReferenceBean;
import ca.cihi.cims.web.bean.index.IndexTermReferencesBean;
import ca.cihi.cims.web.controller.index.IndexIcd1And2AndCCICodeValueReferencesEditController;
import ca.cihi.cims.web.controller.index.IndexIcd3And4CodeValueReferencesEditController;
import ca.cihi.cims.web.controller.index.IndexTermReferencesEditController;

@SuppressWarnings("static-access")
public class IndexReferenceControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CimsConfiguration config;
	@Mock
	private ClassificationService service;
	@Mock
	protected ElementOperations elementOperations;

	// --------------------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(config.isTracePerformanceEnabled()).thenReturn(true);

		ChangeRequestPermission perm = StandardChangeRequestPermission.WRITE_ALL;
		when(service.getChangeRequestLanguages()).thenReturn(Language.ALL);
		when(service.getConceptInfoPermission(nullable(User.class), eq(ChangeRequestCategory.I))).thenReturn(perm);
		when(service.getIndexById(1, Language.ENGLISH)).thenThrow(new UnsupportedElementExeption());
		when(elementOperations.getIndexPath(0, 0L)).thenReturn("parent >");
	}

	@Test
	@Ignore
	public void testIcd1And2AndCCICodeValue() throws Exception {
		IndexIcd1And2AndCCICodeValueReferencesEditController controller = new IndexIcd1And2AndCCICodeValueReferencesEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		long DUMMY_ELEMENT_ID = controller.DUMMY_ELEMENT_ID;
		final IndexModel model = new IndexModel();
		model.setType(IndexType.ICD_BOOK_INDEX);
		model.setElementId(100L);
		model.setSection(1);
		model.setDescription("description");

		IndexCategoryReferenceModel ref1 = new IndexCategoryReferenceModel();
		ref1.setPairedElementId(DUMMY_ELEMENT_ID + 1);
		ref1.setMainCustomDescription("main");
		ref1.setMainCode("M1");
		model.setCategoryReferences(new ArrayList<IndexCategoryReferenceModel>());
		model.getCategoryReferences().add(ref1);

		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn("");

		// test unsupported
		mockMvc.perform(get("/indexes/codereferences/editICD12CCI?id={id}", 1))
				.andExpect(view().name(VIEW_UNSUPPORTED));
		// normal
		mockMvc.perform(get("/indexes/codereferences/editICD12CCI?id={id}", //
				model.getElementId())) //
				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, IndexIcd1An2AndCCICodeValueReferencesBeanMatcher.getMatcher(model)));
						
		// save: ok
		mockMvc.perform(post("/indexes/codereferences/editICD12CCI") //
				.param("id", model.getElementId() + "") //
				.param("references[0].mainElementId", DUMMY_ELEMENT_ID + "") //
				.param("references[1].mainElementId", DUMMY_ELEMENT_ID + 1 + "") //
				.param("references[1].mainCustomDescription", "main new")) //
				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, IndexIcd1An2AndCCICodeValueReferencesBeanMatcher2.getMatcher()));
	}

	@Test
	@Ignore
	public void testIndexIcd3And4CodeValueReferencesEditController_Drugs() throws Exception {

		IndexIcd3And4CodeValueReferencesEditController controller = new IndexIcd3And4CodeValueReferencesEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		long DUMMY_ELEMENT_ID = controller.DUMMY_ELEMENT_ID;
		final IndexModel model = new IndexModel();
		model.setType(IndexType.ICD_BOOK_INDEX);
		model.setElementId(100L);
		model.setSection(3);
		model.setDescription("description");

		final TabularReferenceModel ref1 = new TabularReferenceModel();
		ref1.setElementId(DUMMY_ELEMENT_ID + 2);
		ref1.setCustomDescription(DrugDetailType.ACCIDENTAL.name());
		model.setDrugsDetails(new HashMap<DrugDetailType, TabularReferenceModel>());
		model.getDrugsDetails().put(DrugDetailType.ACCIDENTAL, ref1);
		final TabularConceptModel refModel1 = new TabularConceptModel();
		refModel1.setCode(ref1.getCustomDescription());

		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn("");
		when(service.getTabularConceptLightById(ref1.getElementId())).thenReturn(refModel1);

		// test unsupported
		mockMvc.perform(get("/indexes/codereferences/editICD34?id={id}", 1)).andExpect(view().name(VIEW_UNSUPPORTED));
		// normal

		mockMvc.perform(get("/indexes/codereferences/editICD34?id={id}", //
				model.getElementId())) //
				.andExpect(
						model().attribute(controller.ATTRIBUTE_BEAN, IndexIcd3An4CodeValueReferencesBeanMatcher.getMatcher(model, ref1, refModel1)));
		// save: ok
		mockMvc.perform(post("/indexes/codereferences/editICD34") //
				.param("id", model.getElementId() + "") //
				.param("references[" + DrugDetailType.ACCIDENTAL + "].elementId", ref1.getElementId() + "") //
				.param("references[" + DrugDetailType.ACCIDENTAL + "].code", "new code") //	
				.param("references[" + DrugDetailType.ACCIDENTAL + "].customDescription", "customDescription new")) //
				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, IndexIcd3An4CodeValueReferencesBeanMatcher2.getMatcher()));
	}

	@Test
	@Ignore
	public void testIndexIcd3And4CodeValueReferencesEditController_Neoplasm() throws Exception {
		IndexIcd3And4CodeValueReferencesEditController controller = new IndexIcd3And4CodeValueReferencesEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		long DUMMY_ELEMENT_ID = controller.DUMMY_ELEMENT_ID;
		final IndexModel model = new IndexModel();
		model.setType(IndexType.ICD_NEOPLASM_INDEX);
		model.setElementId(100L);
		model.setSection(4);
		model.setDescription("description");

		final TabularReferenceModel ref1 = new TabularReferenceModel();
		ref1.setElementId(DUMMY_ELEMENT_ID + 2);
		ref1.setCustomDescription(NeoplasmDetailType.BENIGN.name());
		model.setNeoplasmDetails(new HashMap<NeoplasmDetailType, TabularReferenceModel>());
		model.getNeoplasmDetails().put(NeoplasmDetailType.BENIGN, ref1);
		final TabularConceptModel refModel1 = new TabularConceptModel();
		refModel1.setCode(ref1.getCustomDescription());

		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn("");
		when(service.getTabularConceptLightById(ref1.getElementId())).thenReturn(refModel1);

		// test unsupported
		mockMvc.perform(get("/indexes/codereferences/editICD34?id={id}", 1)).andExpect(view().name(VIEW_UNSUPPORTED));
		// normal
		mockMvc.perform(get("/indexes/codereferences/editICD34?id={id}", //
				model.getElementId())) //
				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, IndexIcd3An4CodeValueReferencesBeanMatcher3.getMatcher(model, ref1, refModel1)));
		// save: ok
//		mockMvc.perform(post("/indexes/codereferences/editICD34") //
//				.param("id", model.getElementId() + "") //
//				.param("references[" + NeoplasmDetailType.BENIGN + "].elementId", ref1.getElementId() + "") //
//				.param("references[" + NeoplasmDetailType.BENIGN + "].code", "new code") //	
//				.param("references[" + NeoplasmDetailType.BENIGN + "].customDescription", "customDescription new")) //
//				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, new ArgumentMatcher<Object>() {
//					@Override
//					public boolean matches(Object arg0) {
//						IndexIcd3An4CodeValueReferencesBean bean = (IndexIcd3An4CodeValueReferencesBean) arg0;
//						IndexTabularReferenceBean refBean1 = bean.getReferences().get(NeoplasmDetailType.BENIGN.name());
//						assertEquals("customDescription new", refBean1.getCustomDescription());
//						return true;
//					}
//				}));
	}

	@Test
	@Ignore
	public void testIndexTermReferencesEditController() throws Exception {
		IndexTermReferencesEditController controller = new IndexTermReferencesEditController();
		controller.setConfig(config);
		controller.setService(service);
		controller.setElementOperations(elementOperations);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		long DUMMY_ELEMENT_ID = controller.DUMMY_ELEMENT_ID;
		final IndexModel model = new IndexModel();
		model.setType(IndexType.ICD_BOOK_INDEX);
		model.setElementId(100L);
		model.setSection(1);
		model.setDescription("description");

		final IndexTermReferenceModel ref1 = new IndexTermReferenceModel();
		ref1.setElementId(DUMMY_ELEMENT_ID + 2);
		ref1.setCustomDescription("M1");
		model.setIndexReferences(new ArrayList<IndexTermReferenceModel>());
		model.getIndexReferences().add(ref1);

		when(service.getIndexById(model.getElementId(), Language.ENGLISH)).thenReturn(model);
		when(elementOperations.getIndexPath(0, model.getElementId())).thenReturn("");
		when(elementOperations.getIndexPath(0L, ref1.getElementId())).thenReturn("parent > child1 > child2");

		// test unsupported
		mockMvc.perform(get("/indexes/termreferences/edit?id={id}", 1)).andExpect(view().name(VIEW_UNSUPPORTED));
		// normal
		mockMvc.perform(get("/indexes/termreferences/edit?id={id}", //
				model.getElementId())) //
				.andExpect(model().attribute(controller.ATTRIBUTE_BEAN, IndexTermReferencesBeanMatcher.getMatcher(model, ref1)));


		// normal
		mockMvc.perform(
				get("/indexes/termreferences/edit/breadCrumbs?id=" + model.getElementId() + "&ids=0,"
						+ model.getElementId())) //
				.andExpect(content().string("{\"0\":\"parent >\",\"100\":\"\"}"));
	}
}

class IndexIcd3An4CodeValueReferencesBeanMatcher extends TypeSafeMatcher<IndexIcd3An4CodeValueReferencesBean> {

	final IndexModel model;
	final TabularReferenceModel ref1;
	final TabularConceptModel refModel1;
	
	IndexIcd3An4CodeValueReferencesBeanMatcher(IndexModel model, TabularReferenceModel ref, TabularConceptModel refModel){
		this.model = model;
		this.ref1 = ref;
		this.refModel1 = refModel;
	}

	@Override
    protected boolean matchesSafely(IndexIcd3An4CodeValueReferencesBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(DrugDetailType.values().length, bean.getReferences().size());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd3An4CodeValueReferencesBean matches");
    }

    public static Matcher<IndexIcd3An4CodeValueReferencesBean> 
    	getMatcher(IndexModel model, TabularReferenceModel ref, TabularConceptModel refModel) {
        return new IndexIcd3An4CodeValueReferencesBeanMatcher(model, ref, refModel);
    }
}

class IndexIcd3An4CodeValueReferencesBeanMatcher2 extends TypeSafeMatcher<IndexIcd3An4CodeValueReferencesBean> {

	@Override
    protected boolean matchesSafely(IndexIcd3An4CodeValueReferencesBean bean) {
		IndexTabularReferenceBean refBean1 = bean.getReferences().get(DrugDetailType.ACCIDENTAL.name());
		assertEquals("customDescription new", refBean1.getCustomDescription());
		return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd3An4CodeValueReferencesBean 2 matches");
    }

    public static Matcher<IndexIcd3An4CodeValueReferencesBean> 
    	getMatcher() {
        return new IndexIcd3An4CodeValueReferencesBeanMatcher2();
    }
}


class IndexIcd3An4CodeValueReferencesBeanMatcher3 extends TypeSafeMatcher<IndexIcd3An4CodeValueReferencesBean> {

	final IndexModel model;
	final TabularReferenceModel ref1;
	final TabularConceptModel refModel1;
	
	IndexIcd3An4CodeValueReferencesBeanMatcher3(IndexModel model, TabularReferenceModel ref, TabularConceptModel refModel){
		this.model = model;
		this.ref1 = ref;
		this.refModel1 = refModel;
	}

	@Override
    protected boolean matchesSafely(IndexIcd3An4CodeValueReferencesBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(NeoplasmDetailType.values().length, bean.getReferences().size());
		IndexTabularReferenceBean refBean1 = bean.getReferences().get(NeoplasmDetailType.BENIGN.name());
		assertEquals(ref1.getElementId(), refBean1.getElementId());
		assertEquals(refModel1.getCode(), refBean1.getCode());
		assertEquals(ref1.getCustomDescription(), refBean1.getCustomDescription());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd3An4CodeValueReferencesBeanMatcher3 matches");
    }

    public static Matcher<IndexIcd3An4CodeValueReferencesBean> 
    	getMatcher(IndexModel model, TabularReferenceModel ref, TabularConceptModel refModel) {
        return new IndexIcd3An4CodeValueReferencesBeanMatcher(model, ref, refModel);
    }
}

class IndexTermReferencesBeanMatcher extends TypeSafeMatcher<IndexTermReferencesBean> {

	final IndexModel model;
	final IndexTermReferenceModel ref1;
	
	IndexTermReferencesBeanMatcher(IndexModel model, IndexTermReferenceModel ref){
		this.model = model;
		this.ref1 = ref;
	}

	@Override
    protected boolean matchesSafely(IndexTermReferencesBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(model.getIndexReferences().size() + 1, bean.getReferences().size());
		assertEquals(ref1.getElementId(), bean.getReferences().get(1).getElementId());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexTermReferencesBeanMatcher matches");
    }

    public static Matcher<IndexTermReferencesBean> 
    	getMatcher(IndexModel model, IndexTermReferenceModel ref) {
        return new IndexTermReferencesBeanMatcher(model, ref);
    }
}


class IndexTermReferencesBeanMatcher2 extends TypeSafeMatcher<IndexTermReferencesBean> {

	final IndexModel model;
	
	IndexTermReferencesBeanMatcher2(IndexModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(IndexTermReferencesBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals("customDescription new", bean.getReferences().get(1).getCustomDescription());
		assertEquals("child2", bean.getReferences().get(1).getBreadCrumbs());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexTermReferencesBeanMatcher2 matches");
    }

    public static Matcher<IndexTermReferencesBean> 
    	getMatcher(IndexModel model) {
        return new IndexTermReferencesBeanMatcher2(model);
    }
}


class IndexIcd1An2AndCCICodeValueReferencesBeanMatcher extends TypeSafeMatcher<IndexIcd1An2AndCCICodeValueReferencesBean> {

	final IndexModel model;
	
	IndexIcd1An2AndCCICodeValueReferencesBeanMatcher(IndexModel model){
		this.model = model;
	}

	@Override
    protected boolean matchesSafely(IndexIcd1An2AndCCICodeValueReferencesBean bean) {
		assertEquals(model.getElementId(), bean.getModel().getElementId());
		assertEquals(model.getCategoryReferences().size() + 1, bean.getReferences().size());
		assertEquals(model.getCategoryReferences().get(0).getMainElementId(), bean.getReferences().get(
				1).getMainElementId());
		return true;
	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher matches");
    }

    public static Matcher<IndexIcd1An2AndCCICodeValueReferencesBean> 
    	getMatcher(IndexModel model) {
        return new IndexIcd1An2AndCCICodeValueReferencesBeanMatcher(model);
    }
}


class IndexIcd1An2AndCCICodeValueReferencesBeanMatcher2 extends TypeSafeMatcher<IndexIcd1An2AndCCICodeValueReferencesBean> {

	@Override
    protected boolean matchesSafely(IndexIcd1An2AndCCICodeValueReferencesBean bean) {
		assertEquals("main new", bean.getReferences().get(1).getMainCustomDescription());
		return true;	}

    @Override
    public void describeTo(Description description) {
        description.appendText("IndexIcd1An2AndCCICodeValueReferencesBeanMatcher2 matches");
    }

    public static Matcher<IndexIcd1An2AndCCICodeValueReferencesBean> 
    	getMatcher() {
        return new IndexIcd1An2AndCCICodeValueReferencesBeanMatcher2();
    }
}
