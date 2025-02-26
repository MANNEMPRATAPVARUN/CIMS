package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.model.reports.QASummaryMetricsModel;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class QASummaryMetricsTest {

	@Mock
	ReportMapper reportMapper;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<QASummaryMetricsModel> mockResult() {
		List<QASummaryMetricsModel> results = new ArrayList<QASummaryMetricsModel>();
		QASummaryMetricsModel summary = new QASummaryMetricsModel();
		summary.setChangeRequestId(1l);
		summary.setValid(1);
		summary.setAccepted(0);
		summary.setTranslationDone(0);
		summary.setQaDone(0);
		summary.setRealized(0);

		results.add(summary);

		QASummaryMetricsModel summary1 = new QASummaryMetricsModel();
		summary1.setChangeRequestId(1l);
		summary1.setValid(0);
		summary1.setAccepted(1);
		summary1.setTranslationDone(0);
		summary1.setQaDone(0);
		summary1.setRealized(0);

		results.add(summary1);

		QASummaryMetricsModel summary2 = new QASummaryMetricsModel();
		summary2.setChangeRequestId(2l);
		summary2.setValid(0);
		summary2.setAccepted(1);
		summary2.setTranslationDone(0);
		summary2.setQaDone(0);
		summary2.setRealized(0);

		results.add(summary2);

		QASummaryMetricsModel summary3 = new QASummaryMetricsModel();
		summary3.setChangeRequestId(2l);
		summary3.setValid(0);
		summary3.setAccepted(1);
		summary3.setTranslationDone(0);
		summary3.setQaDone(0);
		summary3.setRealized(0);

		results.add(summary3);

		QASummaryMetricsModel summary4 = new QASummaryMetricsModel();
		summary4.setChangeRequestId(2l);
		summary4.setValid(0);
		summary4.setAccepted(0);
		summary4.setTranslationDone(1);
		summary4.setQaDone(0);
		summary4.setRealized(0);

		results.add(summary4);

		QASummaryMetricsModel summary5 = new QASummaryMetricsModel();
		summary5.setChangeRequestId(3l);
		summary5.setValid(0);
		summary5.setAccepted(0);
		summary5.setTranslationDone(0);
		summary5.setQaDone(1);
		summary5.setRealized(0);

		results.add(summary5);

		QASummaryMetricsModel summary6 = new QASummaryMetricsModel();
		summary6.setChangeRequestId(3l);
		summary6.setValid(0);
		summary6.setAccepted(0);
		summary6.setTranslationDone(0);
		summary6.setQaDone(0);
		summary6.setRealized(1);

		results.add(summary6);
		return results;
	}

	private Integer mockResult1() {
		return 10;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new QASummaryMetrics();
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
		when(reportMapper.findQASummaryMetrics(params)).thenReturn(mockResult());
		when(reportMapper.findTotalChangeRequests(params)).thenReturn(mockResult1());

	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("CCI", reportData.get("classification"));
		assertEquals("2016", reportData.get("year"));

		assertEquals(10, reportData.get("totalCount"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());

		Map<String, Object> data = detailDataList.get(0);
		assertNotNull(data);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList1 = (List<Map<String, Object>>) reportData.get("detail2");
		assertEquals(1, detailDataList1.size());

		Map<String, Object> data1 = detailDataList1.get(0);
		assertNotNull(data1);
	}

}
