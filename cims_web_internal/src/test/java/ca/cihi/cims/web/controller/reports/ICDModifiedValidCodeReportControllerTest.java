package ca.cihi.cims.web.controller.reports;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;

public class ICDModifiedValidCodeReportControllerTest {

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
		ICDModifiedValidCodeReportController controller = new ICDModifiedValidCodeReportController();
		controller.setLookupService(lookupService);
		controller.setReportService(reportService);
		controller.setTokenService(tokenService);
		when(lookupService.findVersionYears("ICD-10-CA")).thenReturn(mockVersionYears());
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
	}

	private List<String> mockVersionYears() {
		List<String> versionYears = new ArrayList<String>();
		versionYears.add("2015");
		versionYears.add("2016");
		return versionYears;
	}

	@Test
	public void testIcdModifiedCodeValidationsReport() throws Exception {
		mockMvc.perform(get("/reports/icdModifiedValidCode", new Object[] {})).andExpect(
				view().name(ICDModifiedValidCodeReportController.ICD_MODIFIED_VALID_CODE));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(get("/reports/icdModifiedValidCode/validate", new Object[] {})).andExpect(
				content().contentType("application/json"));
	}
}
