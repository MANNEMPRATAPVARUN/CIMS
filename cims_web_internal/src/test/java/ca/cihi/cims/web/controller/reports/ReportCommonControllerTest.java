package ca.cihi.cims.web.controller.reports;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ReportCommonControllerTest {

	private MockMvc mockMvc;

	@Mock
	ViewService viewService;

	@Mock
	TokenService tokenService;

	@Mock
	LookupService lookupService;

	@Mock
	ReportService reportService;

	@Autowired
	private CIMSReportExcelView excelView;

	@Before
	public void initMocks() {

		MockitoAnnotations.initMocks(this);
		ReportCommonController controller = new ReportCommonController();
		controller.setViewService(viewService);
		controller.setTokenService(tokenService);
		controller.setLookupService(lookupService);
		controller.setReportService(reportService);
		controller.setExcelView(excelView);
		when(viewService.getAllBookIndexesNoLang("ICD-10-CA")).thenReturn(new ArrayList<CodeDescription>());
		when(
				viewService.getIndexTermSearchResults("ICD-10-CA", 45678l, "Test",
						ReportCommonController.MAX_AUTOCOMPLETE_SEARCH_RESULTS)).thenReturn(
				new ArrayList<SearchResultModel>());
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
	}

	@Test
	public void testCheckDownloadProgress() throws Exception {
		mockMvc.perform(get("/reports/checkDownloadProgress?token={token}", new Object[] { "Test" })).andExpect(
				content().contentType("application/json"));
	}

	@Test
	public void testFindPriorYear() throws Exception {
		mockMvc.perform(
				get("/reports/findPriorYear?classification={classification}&currentYear={currentYear}", new Object[] {
						"Test", "2015" })).andExpect(content().contentType("application/json"));
	}

	@Ignore
	public void testGenerateReport() throws Exception {
		ModelAndView modelAndView = mockMvc
				.perform(
						get("/reports/generate?classification={classification}&year={year}&codeFrom={codeFrom}&codeTo={codeTo}&token={token}&reportType={reportType}",
								new Object[] { "ICD-10-CA", "2016", "A00", "D00", "Token", "MissingValidationICD-10-CA" }))
				.andReturn().getModelAndView();
		assertEquals("application/vnd.ms-excel", modelAndView.getView().getContentType());
	}

	@Test
	public void testGetVersionForClassification() throws Exception {
		mockMvc.perform(
				get("/reports/getBaseContextYears?baseClassification={baseClassification}", new Object[] { "CCI" }))
				.andExpect(content().contentType("application/json"));
	}

	@Test
	public void testGetVersionForClassificationAll() throws Exception {
		mockMvc.perform(
				get("/reports/getBaseContextYearsAll?baseClassification={baseClassification}", new Object[] { "CCI" }))
				.andExpect(content().contentType("application/json"));
	}

	@Test
	public void testListIndexBooks() throws Exception {
		mockMvc.perform(get("/reports/listIndexBooks?classification={classification}", new Object[] { "ICD-10-CA" }))
				.andExpect(content().contentType("application/json"));
	}

	@Test
	public void testSearchCodeValues() throws Exception {
		mockMvc.perform(
				get("/reports/searchCodeValues?classification={classification}&ccp_cid={ccp_cid}&term={term}",
						new Object[] { "ICD-10-CA", "45678", "Test" })).andExpect(
				content().contentType("application/json"));
	}

	@Test
	public void testSearchLeadTerms() throws Exception {
		mockMvc.perform(
				get("/reports/searchIndexBooks?classification={classification}&indexElementId={indexElementId}&term={term}",
						new Object[] { "ICD-10-CA", "45678", "Test" })).andExpect(
				content().contentType("application/json"));
	}
}
