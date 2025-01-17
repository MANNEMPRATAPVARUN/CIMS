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
import ca.cihi.cims.validator.ClassificationChangeReportValidator;

public class ClassificationChangeReportControllerTest {

	private MockMvc mockMvc;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	TokenService tokenService;

	@Mock
	ClassificationChangeReportValidator validator;

	@Before
	public void initMocks() {

		MockitoAnnotations.initMocks(this);
		ClassificationChangeReportController controller = new ClassificationChangeReportController();
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setTokenService(tokenService);
		controller.setValidator(validator);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
	}

	@Test
	public void testClassificationChange() throws Exception {
		mockMvc.perform(get("/reports/classificationChange", new Object[] {})).andExpect(
				view().name(ClassificationChangeReportController.CLASSIFICATION_CHANGE_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(
				get("/reports/classificationChange/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}",
						new Object[] { "ICD-10-CA", "Tabular", "A00", "D00" })).andExpect(
				content().contentType("application/json"));
		mockMvc.perform(
				get("/reports/classificationChange/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}",
						new Object[] { "ICD-10-CA", "Tabular", "900", "D00" })).andExpect(
				content().contentType("application/json"));
	}
}
