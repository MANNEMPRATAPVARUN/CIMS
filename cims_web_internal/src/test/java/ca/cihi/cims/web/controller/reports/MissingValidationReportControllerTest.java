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

import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;
import ca.cihi.cims.validator.MissingValidationReportValidator;

public class MissingValidationReportControllerTest {

	private MockMvc mockMvc;

	@Mock
	MissingValidationReportValidator validator;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	TokenService tokenService;

	@Mock
	ClassificationService classificationService;

	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		MissingValidationReportController controller = new MissingValidationReportController();
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setTokenService(tokenService);
		controller.setValidator(validator);
		controller.setClassificationService(classificationService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

	}

	@Test
	public void testGetDataHoldings() throws Exception {
		mockMvc.perform(get("/reports/missingValidation/selectVersionCode", new Object[] {})).andExpect(
				content().contentType("application/json"));
	}

	@Test
	public void testGetVersionForClassification() throws Exception {
		mockMvc.perform(
				get("/reports/missingValidation/selectClassification?baseClassification={baseClassification}",
						new Object[] { "ICD-10-CA" })).andExpect(
				content().contentType("application/json"));
	}

	@Test
	public void testMissingValidationReport() throws Exception {
		mockMvc.perform(get("/reports/missingValidation", new Object[] {})).andExpect(
				view().name(MissingValidationReportController.MISSING_VALIDATION_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(
				get("/reports/missingValidation/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}",
						new Object[] { "ICD-10-CA", "Tabular", "A00", "D00" })).andExpect(
				content().contentType("application/json"));
		mockMvc.perform(
				get("/reports/missingValidation/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}",
						new Object[] { "ICD-10-CA", "Tabular", "WrongCode", "D00" })).andExpect(
				content().contentType("application/json"));
	}
}
