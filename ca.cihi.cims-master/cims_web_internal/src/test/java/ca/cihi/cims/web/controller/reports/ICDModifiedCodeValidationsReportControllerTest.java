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

public class ICDModifiedCodeValidationsReportControllerTest {

	private MockMvc mockMvc;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	TokenService tokenService;

	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		ICDModifiedValidationsReportController controller = new ICDModifiedValidationsReportController();
		controller.setLookupService(lookupService);
		controller.setReportService(reportService);
		controller.setTokenService(tokenService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
	}

	@Test
	public void testIcdModifiedCodeValidationsReport() throws Exception {
		mockMvc.perform(get("/reports/icdModifiedValidations", new Object[] {})).andExpect(
				view().name(ICDModifiedValidationsReportController.ICD_MODIFIED_VALIDATION_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(get("/reports/icdModifiedValidations/validate", new Object[] {})).andExpect(
				content().contentType("application/json"));
	}
}
