package ca.cihi.cims.web.controller.reports;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;
import ca.cihi.cims.validator.ReviewGroupReportValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ReviewGroupQuestionsReportControllerTest {


	private MockMvc mockMvc;

	@Mock
	ViewService viewService;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	AdminService adminService;

	@Mock
	TokenService tokenService;

	@Mock
	ReviewGroupReportValidator validator;

	@Before
	public void initMocks(){

		MockitoAnnotations.initMocks(this);
		ReviewGroupQuestionsReportController controller=new ReviewGroupQuestionsReportController();

		controller.setViewService(viewService);
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setAdminService(adminService);
		controller.setTokenService(tokenService);
		controller.setValidator(validator);
		mockMvc=MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(lookupService.findNonClosedBaseContextIdentifiers((nullable(String.class)))).thenReturn(mockNonClosedBaseContextIdentifiers());

	}

	@Test
	public void testReviewGroup() throws Exception {
		mockMvc.perform(get("/reports/reviewGroup/outboundQuestions", new Object[]{})).andExpect(view().name(ReviewGroupQuestionsReportController.REVIEW_GROUP_OUTBOUND_QUESTIONS));
		mockMvc.perform(get("/reports/reviewGroup/compiledResponses", new Object[]{})).andExpect(view().name(ReviewGroupQuestionsReportController.REVIEW_GROUP_COMPILED_RESPONSES));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(get("/reports/reviewGroup/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}", new Object[]{"ICD-10-CA","Tabular","A00","Z99"})).andExpect(content().contentType("application/json"));
		mockMvc.perform(get("/reports/reviewGroup/validate?classification={classification}&requestCategory={requestCategory}&codeFrom={codeFrom}&codeTo={codeTo}", new Object[]{"CCI","Tabular","1.AA.55","8.ZZ.10"})).andExpect(content().contentType("application/json"));
	}

	@Test
	public void testGetVersionForClassification() {
		List<ContextIdentifier> list = lookupService.findNonClosedBaseContextIdentifiers(nullable(String.class));
		assertEquals(3, list.size());
	}

	
	private List<ContextIdentifier> mockNonClosedBaseContextIdentifiers() {
		List<ContextIdentifier> list = new ArrayList<ContextIdentifier>();
		ContextIdentifier contextIdentifier1 = new ContextIdentifier();
		ContextIdentifier contextIdentifier2 = new ContextIdentifier();
		ContextIdentifier contextIdentifier3 = new ContextIdentifier();
		list.add(contextIdentifier1);
		list.add(contextIdentifier2);
		list.add(contextIdentifier3);
		return list;
				
	}
	
}
