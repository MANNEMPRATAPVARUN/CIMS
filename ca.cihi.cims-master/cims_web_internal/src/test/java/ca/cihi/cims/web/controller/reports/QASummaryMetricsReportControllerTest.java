package ca.cihi.cims.web.controller.reports;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;

public class QASummaryMetricsReportControllerTest {

	private MockMvc mockMvc;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	TokenService tokenService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		QASummaryMetricsReportController controller = new QASummaryMetricsReportController();
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setTokenService(tokenService);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

	}

	@Test
	public void testQASummaryMetricsReport() throws Exception {
		mockMvc.perform(get("/reports/qaSummaryMetrics", new Object[] {})).andExpect(
				view().name(QASummaryMetricsReportController.QA_SUMMARY_METRICS_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(
				get("/reports/qaSummaryMetrics/validate?classification={classification}&year={year}", new Object[] {
						"ICD-10-CA", "2016" })).andExpect(content().contentType("application/json"));
	}
}
