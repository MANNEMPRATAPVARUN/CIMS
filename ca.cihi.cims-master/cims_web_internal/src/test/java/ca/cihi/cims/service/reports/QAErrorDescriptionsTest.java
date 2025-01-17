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
import ca.cihi.cims.web.bean.report.QAErrorReportViewBean;

public class QAErrorDescriptionsTest {

	@Mock
	ReportMapper reportMapper;

	private QAErrorReportViewBean bean;
	private ReportGenerator reportGenerator;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new QAErrorDescriptions();
		reportGenerator.setReportMapper(reportMapper);

		bean = new QAErrorReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setYear("2018");
		bean.setLanguage("ENG");
		bean.setLanguageDesc("English");
		bean.setOwnerUserName("Test Owner");
		bean.setDateFrom(null);
		bean.setDateTo(null);
		bean.setStatusFrom("Test From Status");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", bean.getClassification());
		params.put("year", bean.getYear());
		params.put("language", bean.getLanguage());
		params.put("owner", bean.getOwnerUserName());
		params.put("dateFrom", bean.getDateFrom());
		params.put("dateTo", bean.getDateTo());
		params.put("statusFrom", bean.getStatusFrom());

		when(reportMapper.findChangeRequestSendBacks(params)).thenReturn(mockChangeRequestSendBacks());

	}

	private List<ChangeRequestSendBack> mockChangeRequestSendBacks() {
		List<ChangeRequestSendBack> changeRequestSendBacks = new ArrayList<ChangeRequestSendBack>();
		ChangeRequestSendBack changeRequestSendBack1 = new ChangeRequestSendBack();
		changeRequestSendBack1.setChangeRequestId("1");
		changeRequestSendBack1.setClassification("ICD-10-CA");
		changeRequestSendBack1.setLanguage("English");
		changeRequestSendBack1.setOwner("Test Owner");
		changeRequestSendBack1.setFromStatus("Test From Status");
		changeRequestSendBack1.setSendBackReason("Test Send Back Reason 1");
		changeRequestSendBack1.setSendBackDate(new Date());
		changeRequestSendBacks.add(changeRequestSendBack1);

		ChangeRequestSendBack changeRequestSendBack2 = new ChangeRequestSendBack();
		changeRequestSendBack2.setChangeRequestId("2");
		changeRequestSendBack2.setClassification("ICD-10-CA");
		changeRequestSendBack2.setLanguage("English");
		changeRequestSendBack2.setOwner("Test Owner");
		changeRequestSendBack2.setFromStatus("Test From Status");
		changeRequestSendBack2.setSendBackReason("Test Send Back Reason 2");
		changeRequestSendBack2.setSendBackDate(new Date());
		changeRequestSendBacks.add(changeRequestSendBack2);

		return changeRequestSendBacks;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("2018", reportData.get("year"));
		assertEquals("English", reportData.get("language"));
		assertEquals("Test Owner", reportData.get("owner"));
		assertEquals("Test From Status", reportData.get("statusFrom"));
		assertEquals("", reportData.get("dateFrom"));
		assertEquals("", reportData.get("dateTo"));

		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(2, detailDataList.size());

		Map<String, Object> detailData1 = detailDataList.get(0);
		assertNotNull(detailData1);
		assertEquals("1", detailData1.get("changeRequestId"));
		assertEquals("ICD-10-CA", detailData1.get("classification"));
		assertEquals("English", detailData1.get("language"));
		assertEquals("Test Owner", detailData1.get("owner"));
		assertEquals(QAErrorDescriptions.formatDate(new Date()), detailData1.get("sendBackDate"));
		assertEquals("Test From Status", detailData1.get("fromStatus"));
		assertEquals("Test Send Back Reason 1", detailData1.get("sendBackReason"));

		Map<String, Object> detailData2 = detailDataList.get(1);
		assertNotNull(detailData2);
		assertEquals("2", detailData2.get("changeRequestId"));
		assertEquals("ICD-10-CA", detailData2.get("classification"));
		assertEquals("English", detailData2.get("language"));
		assertEquals("Test Owner", detailData2.get("owner"));
		assertEquals(QAErrorDescriptions.formatDate(new Date()), detailData2.get("sendBackDate"));
		assertEquals("Test From Status", detailData2.get("fromStatus"));
		assertEquals("Test Send Back Reason 2", detailData2.get("sendBackReason"));

	}

}
