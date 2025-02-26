package ca.cihi.cims.web.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.service.AuxTableService;
import ca.cihi.cims.util.PropertyManager;
import ca.cihi.cims.web.bean.AuxiliaryViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class AdminAuxiliaryControllerTest {

	private MockMvc mockMvc;

	@Mock
	private AuxTableService auxService;
	@Mock
	private PropertyManager propertyManager;

	private final AuxiliaryViewBean auxiliaryViewBean = new AuxiliaryViewBean();
	private final AdminAuxiliaryController controller = new AdminAuxiliaryController();

	// ---------------------------------------------

	private List<String> mockList() {
		List<String> list = new ArrayList<String>();
		list.add("CHANGETYPE");
		list.add("CHANGENATURE");
		list.add("REQUESTOR");
		list.add("REFSETCATEGORY");
		return list;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setAuxService(auxService);
		controller.setPropertyManager(propertyManager);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(propertyManager.getMessage(ArgumentMatchers.anyString())).thenAnswer(AdditionalAnswers.returnsFirstArg());
		when(auxService.getChangeRequestTableCodes()).thenReturn(mockList());
	}

	@Test
	public void testChange() throws Exception {
		// update: invalid English update
		auxiliaryViewBean.setAuxTableValueId(2L);
		mockMvc.perform(
				get("/admin/auxiliary?action=change&auxId={1}&status={2}&auxEngDesc={3}",
						auxiliaryViewBean.getAuxTableValueId(), "D", StringUtils.repeat("E", 300))//
						.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(
						content()
								.string("{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.aux.english.lable size must be between 1 and 50\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.user.english.meaning size must be less then or equal to 255\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"}]}"));
		// update: success
		mockMvc.perform(
				get("/admin/auxiliary?action=change&auxId={1}&status={2}&auxEngLable={3}",
						auxiliaryViewBean.getAuxTableValueId(), "D", "english label")//
						.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean)).andExpect(
				content().string(""));
		// update classification: invalid French
		auxiliaryViewBean.setClassification(true);
		mockMvc.perform(
				get("/admin/auxiliary?action=change&auxId={1}&status={2}&auxEngDesc={3}&auxFraDesc={4}",
						auxiliaryViewBean.getAuxTableValueId(), "D", //
						StringUtils.repeat("E", 300), StringUtils.repeat("F", 300))//
						.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(
						content()
								.string("{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.aux.english.lable size must be between 1 and 50\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.user.english.meaning size must be less then or equal to 255\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.aux.french.lable size must be between 1 and 50\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.user.french.meaning size must be less then or equal to 255\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"}]}"));
		// update classification: success
		mockMvc.perform(
				get("/admin/auxiliary?action=change&auxId={1}&status={2}&auxEngLable={3}&auxFraLable={4}",
						auxiliaryViewBean.getAuxTableValueId(), "D", "english label", "french label")//
						.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean)).andExpect(
				content().string(""));

	}

	@Test
	public void testChangeInsert() throws Exception {
		// insert: failure
		auxiliaryViewBean.setClassification(false);
		mockMvc.perform(
				get("/admin/auxiliary?action=change&status={2}&auxEngDesc={3}", "D", StringUtils.repeat("E", 300))//
						.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(
						content()
								.string("{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.aux.value.code size must be between 1 and 3\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.aux.english.lable size must be between 1 and 50\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"},{\"codes\":[\"auxiliaryViewBean\",\"\"],\"arguments\":null,\"defaultMessage\":\"admin.user.english.meaning size must be less then or equal to 255\",\"objectName\":\"auxiliaryViewBean\",\"code\":\"\"}]}"));
		// insert:success
		mockMvc.perform(get("/admin/auxiliary?action=change&status={2}&auxEngLable={3}&auxValueCode={5}", //
				"D", "english label", "COD")//
				.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean)).andExpect(
				content().string(
						"{\"value\":\"0\",\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Test
	public void testDelete() throws Exception {
		ArgumentMatcher<AuxTableValue> matcher2 = new ArgumentMatcher<AuxTableValue>() {
			@Override
			public boolean matches(AuxTableValue arg0) {
				return ObjectUtils.equals(((AuxTableValue) arg0).getAuxTableValueId(), 2L);
			}
		};
		ArgumentMatcher<AuxTableValue> matcher3 = new ArgumentMatcher<AuxTableValue>() {
			@Override
			public boolean matches(AuxTableValue arg0) {
				return ObjectUtils.equals(((AuxTableValue) arg0).getAuxTableValueId(), 3L);
			}
		};
		Mockito.doThrow(AlreadyInUseException.class).when(auxService).deleteAux(argThat(matcher2));
		Mockito.doThrow(new CIMSException("test")).when(auxService).deleteAux(argThat(matcher3));

		// delete: normal
		mockMvc.perform(get("/admin/auxiliary?action=delete&auxId=1")//
				.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean)) //
				.andExpect(content().string(""));
		// delete: already in use
		auxiliaryViewBean.setAuxTableValueId(2L);
		mockMvc.perform(get("/admin/auxiliary?action=delete&auxId={id}", auxiliaryViewBean.getAuxTableValueId())//
				.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(
						content()
								.string("{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"Unable to delete since it is already associated\",\"objectName\":\"error\",\"field\":\"error\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
		// delete: some exception
		auxiliaryViewBean.setAuxTableValueId(3L);
		mockMvc.perform(get("/admin/auxiliary?action=delete&auxId={id}", auxiliaryViewBean.getAuxTableValueId())//
				.sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				//
				.andExpect(
						content()
								.string("{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"test\",\"objectName\":\"error\",\"field\":\"error\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
	}

	@Test
	public void testSearch() throws Exception {
		// search: Change Request Auxiliary Tables
		mockMvc.perform(get("/admin/auxiliary")) //
				.andExpect(model().attribute(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean));
		// search: Classifications Auxiliary Tables
		mockMvc.perform(get("/admin/auxiliary?classsification=true")) //
				.andExpect(model().attribute(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean));

		// search change request
		final List<AuxTableValue> auxChangeRequestTableValues = new ArrayList<AuxTableValue>();
		when(auxService.getChangeRequestTableIdByCode("100")).thenReturn(100L);
		when(auxService.getChangeRequestTableValues("100")).thenReturn(auxChangeRequestTableValues);

		auxiliaryViewBean.setAuxCode("100");
		mockMvc.perform(post("/admin/auxiliary").sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(model().attribute(WebConstants.AUXILIARY_VIEW_BEAN, hasProperty("auxTableId", equalTo(100L))));

		// search classification
		final List<AuxTableValue> auxClassificationTableValues = new ArrayList<AuxTableValue>();
		when(auxService.getClassificationTableIdByCode("200")).thenReturn(200L);
		when(auxService.getClassificationTableValues("200", 9999)).thenReturn(auxClassificationTableValues);

		auxiliaryViewBean.setYear(9999);
		auxiliaryViewBean.setAuxCode("200");
		auxiliaryViewBean.setClassification(true);
		mockMvc.perform(post("/admin/auxiliary").sessionAttr(WebConstants.AUXILIARY_VIEW_BEAN, auxiliaryViewBean))
				.andExpect(model().attribute(WebConstants.AUXILIARY_VIEW_BEAN,                                         
						allOf(
	                        hasProperty("auxTableId", equalTo(200L)),
	                        hasProperty("auxTableValues", equalTo(auxClassificationTableValues)
	                        )
                )));
	}

}