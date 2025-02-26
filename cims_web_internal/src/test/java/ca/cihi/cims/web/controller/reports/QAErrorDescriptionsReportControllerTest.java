package ca.cihi.cims.web.controller.reports;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;
import ca.cihi.cims.validator.ReviewGroupReportValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class QAErrorDescriptionsReportControllerTest {

	private MockMvc mockMvc;

    protected QAErrorDescriptionsReportController controller;

	@Mock
	protected Model model;

	@Mock
    Map<String, Object> reportData;

	@Mock
	protected BindingResult result;

	@Mock
	CIMSReportExcelView excelView;

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
		controller = new QAErrorDescriptionsReportController();
		controller.setExcelView(excelView);
		controller.setReportService(reportService);
		controller.setLookupService(lookupService);
		controller.setTokenService(tokenService);
		controller.setAdminService(adminService);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(lookupService.findBaseContextYears(nullable(String.class))).thenReturn(mockBaseContextYears());

	}

    private List<String> mockBaseContextYears() {
        List<String> list = new ArrayList<String>();
        list.add("2018");
        list.add("2019");
        list.add("2020");
        return list;
    }

	@Test
	public void testQAErrorDescriptionsReportController() throws Exception {
		mockMvc.perform(get("/reports/qaErrorDescriptions", new Object[] {})).andExpect(
				view().name(QAErrorDescriptionsReportController.QA_ERROR_DESCRIPTIONS_REPORT));
	}


	@Test
	public void testGetVersionForClassification() {
		String baseClassification = "CCI";
        List<String> years = controller.getVersionForClassification(baseClassification);
        assertEquals(3, years.size());
        String year1 = years.get(0);
        String year2 = years.get(1);
        String year3 = years.get(2);
        assertEquals("2018", year1);
        assertEquals("2019", year2);
        assertEquals("2020", year3);
        
	}	

}
