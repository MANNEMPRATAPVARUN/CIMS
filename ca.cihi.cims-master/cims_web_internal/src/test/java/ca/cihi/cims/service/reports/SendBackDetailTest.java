package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.model.reports.ChangeRequestSendBack;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class SendBackDetailTest {

	@Mock
	ReportMapper reportMapper;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<ChangeRequestSendBack> mockResult() {
		List<ChangeRequestSendBack> results = new ArrayList<ChangeRequestSendBack>();
		ChangeRequestSendBack sendBack = new ChangeRequestSendBack();
		sendBack.setChangeRequestId("12");
		sendBack.setClassification("CCI");
		sendBack.setFromStatus("Valid");
		sendBack.setLanguage("English");
		sendBack.setOwner("tyang");
		sendBack.setSendBackDate(new Date());
		sendBack.setSendBackReason("Test");
		results.add(sendBack);
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new SendBackDetail();
		reportGenerator.setReportMapper(reportMapper);

		bean = new ReportViewBean();
		bean.setClassification("CCI");
		bean.setYear("2016");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", bean.getClassification());
		params.put("year", bean.getYear());
		params.put("languageCode", null);
		params.put("owner", null);
		params.put("fromStatus", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		when(reportMapper.findChangeRequestStatusChangeHistories(params)).thenReturn(mockResult());

	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("CCI", reportData.get("classification"));
		assertEquals("2016", reportData.get("year"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());

		Map<String, Object> data = detailDataList.get(0);
		assertNotNull(data);
		String changeRequestId = (String) data.get("changeRequestId");
		assertEquals("12", changeRequestId);
	}

}
