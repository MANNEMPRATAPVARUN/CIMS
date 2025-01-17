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

public class CCINewTableCodesWithCodingDirectivesReportControllerTest {

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
		CCINewTableCodesWithCodingDirectivesReportController controller = new CCINewTableCodesWithCodingDirectivesReportController();
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setTokenService(tokenService);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

	}

	@Test
	public void testCciNewTableCodesReport() throws Exception {
		mockMvc.perform(get("/reports/cciNewTableCodes", new Object[] {})).andExpect(
				view().name(CCINewTableCodesWithCodingDirectivesReportController.CCI_NEW_TABLE_CODES_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(
				get("/reports/cciNewTableCodes/validate?priorYear={priorYear}&year={year}", new Object[] { "2015",
						"2016" })).andExpect(content().contentType("application/json"));
	}

}
