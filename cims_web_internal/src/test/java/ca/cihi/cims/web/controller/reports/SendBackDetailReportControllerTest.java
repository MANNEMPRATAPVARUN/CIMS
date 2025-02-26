package ca.cihi.cims.web.controller.reports;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SendBackDetailReportControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	SendBackDetailReportController controller;

	@Mock
	ReportService reportService;

	@Mock
	LookupService lookupService;

	@Mock
	TokenService tokenService;

	@Mock
	AdminService adminService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

	}

	@Test
	public void testSendBackDetailReport() throws Exception {
		mockMvc.perform(get("/reports/sendBackDetail", new Object[] {})).andExpect(
				view().name(SendBackDetailReportController.SEND_BACK_DETAIL_REPORT));
	}

	@Test
	public void testValidate() throws Exception {
		mockMvc.perform(
				get("/reports/sendBackDetail/validate?classification={classification}&year={year}", new Object[] {
						"ICD-10-CA", "2016" })).andExpect(content().contentType("application/json"));
	}
}
