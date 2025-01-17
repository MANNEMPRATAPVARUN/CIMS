package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.folioclamlexport.HtmlOutputLogServiceImpl;
import ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceImpl;
import ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ExportFolioFileControllerTest {

	ExportFolioFileController exportFolioFileController;
	@Autowired
	private LookupService lookupService;
	@Mock
	private HtmlOutputServiceImpl htmlOutputService;

	@Mock
	private HtmlOutputLogServiceImpl htmlOutputLogService;

	private HtmlOutputLog currentLogStatusObj;

	private List<HtmlOutputLog> mockHtmlOutputLog() {
		List<HtmlOutputLog> logs = new ArrayList<>();
		HtmlOutputLog log1 = new HtmlOutputLog();
		log1.setStatusCode("G");
		log1.setHtmlOutputLogId(1L);

		HtmlOutputLog log2 = new HtmlOutputLog();
		log2.setHtmlOutputLogId(2L);
		log2.setStatusCode("G");

		logs.add(log1);
		logs.add(log2);

		return logs;
	}

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		exportFolioFileController = new ExportFolioFileController();
		exportFolioFileController.setLookupService(lookupService);
		exportFolioFileController.setHtmlOutputService(htmlOutputService);

		currentLogStatusObj = new HtmlOutputLog();
		currentLogStatusObj.setHtmlOutputLogId(1L);
		when(htmlOutputService.getCurrentLogStatusObj()).thenReturn(currentLogStatusObj);
		when(htmlOutputService.getDetailedLog(1L)).thenReturn(Arrays.asList("log 1"));
		when(htmlOutputService.getDetailedLog()).thenReturn(Arrays.asList("log 1", "log 2", "log 3"));
		when(htmlOutputService.getStatus()).thenReturn(HtmlOutputServiceStatus.DONE);
		when(htmlOutputService.getZipFileName()).thenReturn("/path/file.txt");

		when(htmlOutputService.getHtmlOutputLogService()).thenReturn(htmlOutputLogService);
		when(htmlOutputLogService.getHtmlOutputLogs()).thenReturn(mockHtmlOutputLog());

	}

	@Test
	public void testGetContextYears() {
		List<String> contextYears = exportFolioFileController.getContextYears("CCI");
		assertTrue(contextYears.size() >= 0);
	}

	@Test
	public void testGetDetailedLog() {
		List<String> logs = exportFolioFileController.getDetailedLog(1L);
		assertEquals(1, logs.size());
	}

	@Test
	public void testHasDetailedLog() {
		assertTrue(exportFolioFileController.hasDetailedLog(1L));
	}

	@Test
	public void testGetLastDetailedLog() {
		Map<Integer, String> latestLogs = exportFolioFileController.getLastDetailedLog(1);
		assertEquals("log 3", latestLogs.get(2));
	}

	@Test
	public void testShowDetailedLogPage() {
		Model model = new ExtendedModelMap();
		String page = exportFolioFileController.showDetailedLogPage(model, 1L, "CCI", "2018", "ENG");
		assertEquals(ExportFolioFileController.SHOW_EXPORT_FOLIO_DETAIL_LOG_PAGE, page);
	}

	@Test
	public void testViewAllStatuses() {
		Model model = new ExtendedModelMap();
		String page = exportFolioFileController.viewAllStatuses(model);
		assertEquals(ExportFolioFileController.EXPORT_FOLIO_STATUS_PAGE, page);
	}

	@Test
	public void testGetStatus() {
		assertEquals(HtmlOutputServiceStatus.DONE.getDescription(), exportFolioFileController.getStatus());
	}

	@Test
	public void testGetZipFileName() {
		assertEquals("/path/file.txt", exportFolioFileController.getZipFileName());
	}

	@Test
	public void testProcessGenerationRequest() {
		assertEquals("redirect:/admin/initExportFolioRequestPage.htm",
				exportFolioFileController.processGenerationRequest());
	}

	@Test
	public void testSetupForm() {
		ModelAndView ret = exportFolioFileController.setupForm(null, null);
		assertNotNull(ret);
		assertEquals(ExportFolioFileController.SHOW_EXPORT_FOLIO_REQUEST_PAGE, ret.getViewName());
	}

}