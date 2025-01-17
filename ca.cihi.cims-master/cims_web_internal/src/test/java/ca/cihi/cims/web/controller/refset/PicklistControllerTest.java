package ca.cihi.cims.web.controller.refset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.model.refset.ClassificationCodeSearchRequest;
import ca.cihi.cims.service.refset.PicklistService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.validator.refset.ColumnValidator;
import ca.cihi.cims.validator.refset.PicklistValidator;
import ca.cihi.cims.web.bean.refset.AvailableColumnTypeResponse;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;
import ca.cihi.cims.web.bean.refset.PickListTableViewBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RecordViewBean;
import ca.cihi.cims.web.bean.refset.ValueViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class PicklistControllerTest {

	private MockMvc mockMvc;
	@Mock
	private RefsetService refsetService;
	@Mock
	private PicklistService picklistServie;

	@Autowired
	private PicklistValidator picklistValidator;
	@Autowired
	private ColumnValidator columnValidator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		PickListController controller = new PickListController();
		controller.setRefsetService(refsetService);
		controller.setPicklistService(picklistServie);
		controller.setPicklistValidator(picklistValidator);
		controller.setColumnValidator(columnValidator);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
	}

	@Test
	public void testListPickList() throws Exception {
		mockMvc.perform(get("/refset/picklist?contextId=1&elementId=1&elementVersionId=1", new Object[] {}))
				.andExpect(view().name(PickListController.PICKLIST_VIEW));
	}

	@Test
	public void testAddPickList() throws Exception {
		mockMvc.perform(get("/refset/picklist/add?contextId=1&elementId=1&elementVersionId=1", new Object[] {}))
				.andExpect(view().name(PickListController.PICKLIST_ADD_VIEW));
	}

	@Test
	public void testSavePickList() throws Exception {
		mockMvc.perform(
				post("/refset/picklist/add?contextId=1&elementId=1&elementVersionId=1&actionType=save&name={name}&code={code}&classificationStandard={classificationStandard}",
						new Object[] { "Test name", "TCODE", "ICD-10-CA" }))
				.andExpect(content().contentType("application/json"));
		PickListViewBean viewBean = new PickListViewBean();
		viewBean.setContextId(1l);
		viewBean.setElementId(1l);
		viewBean.setElementVersionId(1l);
		viewBean.setName("Test name");
		viewBean.setCode("TCODE");
		viewBean.setClassificationStandard("ICD-10-CA");
		verify(refsetService, times(1)).insertPickList(viewBean);
	}

	@Test
	public void testDeletePickList() throws Exception {
		mockMvc.perform(
				post("/refset/picklist/add?contextId=1&elementId=1&elementVersionId=1&actionType=drop&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 1l, 1l }))
				.andExpect(content().contentType("application/json"));
		PickListViewBean viewBean = new PickListViewBean();
		viewBean.setContextId(1l);
		viewBean.setElementId(1l);
		viewBean.setElementVersionId(1l);
		viewBean.setPicklistElementId(1l);
		viewBean.setPicklistElementVersionId(1l);
		verify(picklistServie, times(1)).deletePickList(viewBean);
	}

	@Test
	public void testSavePickListName() throws Exception {
		mockMvc.perform(
				post("/refset/picklist/savePicklistName?contextId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 1l, 1l }))
				.andExpect(content().contentType("application/json"));
		PickListViewBean viewBean = new PickListViewBean();
		viewBean.setContextId(1l);
		viewBean.setPicklistElementId(1l);
		viewBean.setPicklistElementVersionId(1l);
		verify(picklistServie, times(1)).savePicklist(viewBean);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEditPickList() throws Exception {
		mockMvc.perform(
				get("/refset/picklist/edit?contextId=1&elementId=1&elementVersionId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 1l, 1l }))
				.andExpect(view().name(PickListController.PICKLIST_EDIT_VIEW));
		PickListViewBean viewBean = new PickListViewBean();
		viewBean.setContextId(2L);
		viewBean.setElementId(1L);
		viewBean.setElementVersionId(1L);
		viewBean.setPicklistElementId(2L);
		viewBean.setPicklistElementVersionId(2L);
		when(picklistServie.generatePicklistTable(viewBean)).thenReturn(new PickListTableViewBean());
		mockMvc.perform(
				get("/refset/picklist/edit?contextId=2&elementId=1&elementVersionId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 2l, 2l }))
				.andExpect(view().name(PickListController.PICKLIST_EDIT_VIEW));

		PickListViewBean viewBeanEx = new PickListViewBean();
		viewBeanEx.setContextId(3L);
		viewBeanEx.setElementId(2L);
		viewBeanEx.setElementVersionId(2L);
		viewBeanEx.setPicklistElementId(2L);
		viewBeanEx.setPicklistElementVersionId(2L);
		when(picklistServie.generatePicklistTable(viewBeanEx)).thenThrow(RuntimeException.class);
		mockMvc.perform(
				get("/refset/picklist/edit?contextId=3&elementId=2&elementVersionId=2&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 2l, 2l }))
				.andExpect(view().name(PickListController.PICKLIST_EDIT_VIEW));
	}

	@Test
	public void testAvailableColumnTypes() throws Exception {
		PickListColumnBean beanPicklist = new PickListColumnBean();
		beanPicklist.setContextId(1L);
		beanPicklist.setPicklistElementId(1L);
		beanPicklist.setPicklistElementVersionId(1L);
		beanPicklist.setContainerElementId(1L);
		beanPicklist.setContainerElementVersionId(1L);
		beanPicklist.setContainerSublist(false);
		when(picklistServie.getAvailableColumnTypes(beanPicklist)).thenReturn(mockAvailableColumnTypes());
		mockMvc.perform(
				post("/refset/picklist/availableColumnTypes?contextId=1&picklistElementId=1&picklistElementVersionId=1&containerElementId=1&containerElementVersionId=1"
						+ "&containerSublist=false"))
				.andExpect(content().contentType("application/json"));

		verify(picklistServie, times(1)).getAvailableColumnTypes(beanPicklist);

		PickListColumnBean beanSublist = new PickListColumnBean();
		beanSublist.setContextId(1L);
		beanSublist.setPicklistElementId(1L);
		beanSublist.setPicklistElementVersionId(1L);
		beanSublist.setContainerElementId(2L);
		beanSublist.setContainerElementVersionId(2L);
		beanSublist.setContainerSublist(true);
		when(picklistServie.getAvailableColumnTypes(beanSublist)).thenReturn(mockAvailableColumnTypesSublist());
		mockMvc.perform(
				post("/refset/picklist/availableColumnTypes?contextId=1&picklistElementId=1&picklistElementVersionId=1&containerElementId=2&containerElementVersionId=2"
						+ "&containerSublist=true"))
				.andExpect(content().contentType("application/json"));

		verify(picklistServie, times(1)).getAvailableColumnTypes(beanSublist);
	}

	private AvailableColumnTypeResponse mockAvailableColumnTypesSublist() {
		AvailableColumnTypeResponse response = new AvailableColumnTypeResponse();
		response.setMultipleColumnSublistExists(true);
		return response;
	}

	private AvailableColumnTypeResponse mockAvailableColumnTypes() {
		AvailableColumnTypeResponse response = new AvailableColumnTypeResponse();
		return response;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testView() throws Exception {
		Mockito.when(refsetService.isRefreshAllowed(1L, 1L, 1L)).thenReturn(true);
		mockMvc.perform(
				get("/refset/picklist/view?contextId=1&elementId=1&elementVersionId=1&picklistElementId=1&picklistElementVersionId=1"))
				.andExpect(view().name(PickListController.PICKLIST_VIEW_VIEW));
		PickListViewBean viewBean = new PickListViewBean();
		viewBean.setContextId(2L);
		viewBean.setElementId(1L);
		viewBean.setElementVersionId(1L);
		viewBean.setPicklistElementId(2L);
		viewBean.setPicklistElementVersionId(2L);
		when(picklistServie.generatePicklistTable(viewBean)).thenReturn(new PickListTableViewBean());
		mockMvc.perform(
				get("/refset/picklist/view?contextId=2&elementId=1&elementVersionId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 2l, 2l }))
				.andExpect(view().name(PickListController.PICKLIST_VIEW_VIEW));
		verify(picklistServie, times(1)).generatePicklistTable(viewBean);

		PickListViewBean viewBeanEx = new PickListViewBean();
		viewBeanEx.setContextId(3L);
		viewBeanEx.setElementId(2L);
		viewBeanEx.setElementVersionId(2L);
		viewBeanEx.setPicklistElementId(2L);
		viewBeanEx.setPicklistElementVersionId(2L);
		when(picklistServie.generatePicklistTable(viewBeanEx)).thenThrow(RuntimeException.class);
		mockMvc.perform(
				get("/refset/picklist/view?contextId=3&elementId=2&elementVersionId=2&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}",
						new Object[] { 2l, 2l }))
				.andExpect(view().name(PickListController.PICKLIST_VIEW_VIEW));
		verify(picklistServie, times(2)).generatePicklistTable(viewBeanEx);
	}

	@Test
	public void testSaveColumn() throws Exception {
		mockMvc.perform(post(
				"/refset/picklist/savePicklistColumn?actionType=create&contextId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}"
						+ "&containerElementId={containerElementId}&containerElementVersionId={containerElementVersionId}&columnType={columnType}&revisedColumnName={revisedColumnName}",
				new Object[] { 1l, 1l, 1l, 1l, "ICD-10-CA Code", "Code" }))
				.andExpect(content().contentType("application/json"));
		PickListColumnBean bean = new PickListColumnBean();
		bean.setActionType("create");
		bean.setContextId(1L);
		bean.setPicklistElementId(1L);
		bean.setPicklistElementVersionId(1L);
		bean.setContainerElementId(1L);
		bean.setContainerElementVersionId(1L);
		bean.setColumnType("ICD-10-CA Code");
		bean.setRevisedColumnName("Code");
		bean.setColumnOrder(null);
		bean.setContainerSublist(false);
		verify(picklistServie, times(0)).addColumn(bean);

		mockMvc.perform(
				post("/refset/picklist/savePicklistColumn?actionType=create&contextId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}"
						+ "&containerElementId={containerElementId}&containerElementVersionId={containerElementVersionId}&columnType={columnType}&revisedColumnName={revisedColumnName}"
						+ "&columnOrder={columnOrder}", new Object[] { 1l, 1l, 1l, 1l, "ICD-10-CA Code", "Code", 1 }))
				.andExpect(content().contentType("application/json"));

		PickListColumnBean beanAddEx = new PickListColumnBean();
		beanAddEx.setActionType("create");
		beanAddEx.setContextId(1L);
		beanAddEx.setPicklistElementId(1L);
		beanAddEx.setPicklistElementVersionId(1L);
		beanAddEx.setContainerElementId(1L);
		beanAddEx.setContainerElementVersionId(1L);
		beanAddEx.setColumnType("ICD-10-CA Code");
		beanAddEx.setRevisedColumnName("Code");
		beanAddEx.setColumnOrder(1);
		beanAddEx.setContainerSublist(false);
		verify(picklistServie, times(1)).addColumn(beanAddEx);

		PickListColumnBean beanAdd = new PickListColumnBean();
		beanAdd.setActionType("create");
		beanAdd.setContextId(2L);
		beanAdd.setPicklistElementId(2L);
		beanAdd.setPicklistElementVersionId(2L);
		beanAdd.setContainerElementId(2L);
		beanAdd.setContainerElementVersionId(2L);
		beanAdd.setColumnType("ICD-10-CA Code");
		beanAdd.setRevisedColumnName("Code");
		beanAdd.setColumnOrder(1);
		beanAdd.setContainerSublist(false);
		when(picklistServie.addColumn(beanAdd)).thenReturn(new ElementIdentifier(3L, 3L));

		mockMvc.perform(
				post("/refset/picklist/savePicklistColumn?actionType=create&contextId=2&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}"
						+ "&containerElementId={containerElementId}&containerElementVersionId={containerElementVersionId}&columnType={columnType}&revisedColumnName={revisedColumnName}"
						+ "&columnOrder={columnOrder}", new Object[] { 2l, 2l, 2l, 2l, "ICD-10-CA Code", "Code", 1 }))
				.andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).addColumn(beanAdd);

		mockMvc.perform(post(
				"/refset/picklist/savePicklistColumn?actionType=save&contextId=1&picklistElementId={picklistElementId}&picklistElementVersionId={picklistElementVersionId}"
						+ "&containerElementId={containerElementId}&containerElementVersionId={containerElementVersionId}&columnType={columnType}&revisedColumnName={revisedColumnName}"
						+ "&columnOrder={columnOrder}&columnElementId=1&columnElementVersionId=1",
				new Object[] { 1l, 1l, 1l, 1l, "ICD-10-CA Code", "Code Modify", 1 }))
				.andExpect(content().contentType("application/json"));

		PickListColumnBean bean2 = new PickListColumnBean();
		bean2.setActionType("save");
		bean2.setContextId(1L);
		bean2.setPicklistElementId(1L);
		bean2.setPicklistElementVersionId(1L);
		bean2.setContainerElementId(1L);
		bean2.setContainerElementVersionId(1L);
		bean2.setColumnType("ICD-10-CA Code");
		bean2.setRevisedColumnName("Code Modify");
		bean2.setColumnOrder(1);
		bean2.setContainerSublist(false);
		bean2.setColumnElementId(1l);
		bean2.setColumnElementVersionId(1L);
		verify(picklistServie, times(1)).saveColumn(bean2);

		mockMvc.perform(
				post("/refset/picklist/savePicklistColumn?actionType=drop&contextId=1&columnElementId=1&columnElementVersionId=1"))
				.andExpect(content().contentType("application/json"));

		PickListColumnBean bean3 = new PickListColumnBean();
		bean3.setActionType("drop");
		bean3.setContextId(1L);
		bean3.setColumnElementId(1l);
		bean3.setColumnElementVersionId(1L);
		verify(picklistServie, times(1)).deleteColumn(bean3);
	}

	@Test
	public void testGetClassificationCodeSearchResult() throws Exception {
		mockMvc.perform(
				post("/refset/picklist/getClassificationCodeSearchResult?contextId=3103461&classification=ICD-10-CA&term=A0"))
				.andExpect(content().contentType("application/json"));
		ClassificationCodeSearchRequest request = new ClassificationCodeSearchRequest();
		request.setClassificationCode("ICD-10-CA");
		request.setContextId(3103461L);
		request.setMaxResults(20);
		request.setSearchConceptCode("A0");
		verify(refsetService, times(1)).getActiveClassificationByCode(request);
	}

	@Test
	public void testGetFreeSearchResult() throws Exception {
		mockMvc.perform(
				post("/refset/picklist/getFreeSearchResult?columnType=Desc-Common Term (ENG)&term=ICD-10-CA&conceptId=35&maxResults=10"))
				.andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).searchCommonTerm("ICD-10-CA", "Desc-Common Term (ENG)", 35L, 10);
	}

	@Test
	public void testAddRecord() throws Exception {
		RecordViewBean viewBean = new RecordViewBean();
		viewBean.setContextId(1L);
		viewBean.setContainerElementId(1L);
		viewBean.setContainerElementVersionId(1L);
		List<ValueViewBean> values = new ArrayList<>();
		ValueViewBean value = new ValueViewBean();
		value.setColumnElementId(1L);
		value.setColumnElementVersionId(1L);
		value.setIdValue(123L);
		value.setTextValue("Text");
		values.add(value);
		viewBean.setValues(values);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(viewBean);
		mockMvc.perform(post("/refset/picklist/addPicklistColumnValue")
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(),
						MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
				.content(requestJson)).andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).addRecord(viewBean);

		RecordViewBean viewBean1 = new RecordViewBean();
		viewBean1.setContextId(1L);
		viewBean1.setContainerSublist(true);
		viewBean1.setRecordElementId(1L);
		viewBean1.setRecordElementVersionId(1L);
		viewBean1.setContainerElementId(1L);
		viewBean1.setContainerElementVersionId(1L);
		List<ValueViewBean> values1 = new ArrayList<>();
		ValueViewBean value1 = new ValueViewBean();
		value1.setColumnElementId(1L);
		value1.setColumnElementVersionId(1L);
		value1.setIdValue(123L);
		value1.setTextValue("Text");
		values1.add(value1);
		viewBean1.setValues(values1);

		String requestJson1 = ow.writeValueAsString(viewBean1);

		mockMvc.perform(
				post("/refset/picklist/addPicklistColumnValue")
						.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(),
								MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
				.content(requestJson1)).andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).addRecord(viewBean1);
	}

	@Test
	public void testSaveRecord() throws Exception {
		RecordViewBean viewBean = new RecordViewBean();
		viewBean.setContextId(1L);
		viewBean.setRecordElementId(1L);
		viewBean.setRecordElementVersionId(1L);
		List<ValueViewBean> values = new ArrayList<>();
		ValueViewBean value = new ValueViewBean();
		value.setColumnElementId(1L);
		value.setColumnElementVersionId(1L);
		value.setIdValue(123L);
		value.setTextValue("Text Test");
		values.add(value);
		viewBean.setValues(values);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(viewBean);
		mockMvc.perform(post("/refset/picklist/savePicklistColumnValue")
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(),
						MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
				.content(requestJson)).andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).saveRecord(viewBean);

	}

	@Test
	public void testDeleteRecord() throws Exception {
		RecordViewBean viewBean = new RecordViewBean();
		viewBean.setContextId(1L);
		viewBean.setRecordElementId(1L);
		viewBean.setRecordElementVersionId(1L);

		mockMvc.perform(post("/refset/picklist/deletePicklistColumnValue?contextId=1&elementId=1"
				+ "&elementVersionId=1&containerSublist=false"))
				.andExpect(content().contentType("application/json"));
		verify(picklistServie, times(1)).deleteRecord(viewBean);
	}

	@Test
	public void testListRecords() throws Exception {
		mockMvc.perform(get("/refset/picklist/getPicklistColumnValue?contextId=1"
				+ "&containerElementId=1&containerElementVersionId=1&recordElementId=1&recordElementVersionId=1"
				+ "&containerSublist=false")).andExpect(content().contentType("application/json"));
		RecordViewBean viewBean = new RecordViewBean();
		viewBean.setContextId(1L);
		viewBean.setRecordElementId(1L);
		viewBean.setRecordElementVersionId(1L);
		viewBean.setContainerElementId(1L);
		viewBean.setContainerElementVersionId(1L);
		verify(picklistServie, times(1)).listRecords(viewBean);

		mockMvc.perform(get("/refset/picklist/getPicklistColumnValue?contextId=1"
				+ "&containerElementId=1&containerElementVersionId=1&recordElementId=1&recordElementVersionId=1"
				+ "&containerSublist=true")).andExpect(content().contentType("application/json"));
		RecordViewBean viewBean1 = new RecordViewBean();
		viewBean1.setContextId(1L);
		viewBean1.setRecordElementId(1L);
		viewBean1.setRecordElementVersionId(1L);
		viewBean1.setContainerElementId(1L);
		viewBean1.setContainerElementVersionId(1L);
		viewBean1.setContainerSublist(true);
		verify(picklistServie, times(1)).listRecords(viewBean1);
	}

}
