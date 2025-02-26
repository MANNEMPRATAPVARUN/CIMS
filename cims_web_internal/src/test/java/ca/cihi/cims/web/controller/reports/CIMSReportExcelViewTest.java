package ca.cihi.cims.web.controller.reports;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import ca.cihi.cims.service.reports.ReportServiceImpl;
import ca.cihi.cims.util.CimsUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml"})
@WebAppConfiguration
public class CIMSReportExcelViewTest {

	@Autowired
	WebApplicationContext wac; // cached

	@Autowired
	MockServletContext servletContext; // cached

	@Autowired
	MockHttpServletRequest request;

	@Autowired
	MockHttpServletResponse response;

	@Test
	public void testBulidDocument() throws Exception {
		CIMSReportExcelView excelView = new CIMSReportExcelView();
		excelView.prepareData("CCIModifiedValidations");
		excelView.setApplicationContext(wac);
		excelView.setReportService(new ReportServiceImpl());

		Map<String, Object> dataMap = new HashMap<String, Object>();

		Map<String, Object> reportData = new HashMap<String, Object>();
		reportData.put("classification", "CCI");
		reportData.put("currentYear", "2016");
		reportData.put("priorYear", "2015");
		reportData.put("countCount", "1");

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> detailData1 = new HashMap<String, Object>();
		detailData1.put("codeValue", "A11");
		detailDataList.add(detailData1);

		dataMap.put("reportData", reportData);

		excelView.render(dataMap, request, response);

		POIFSFileSystem poiFs = new POIFSFileSystem(new ByteArrayInputStream(response.getContentAsByteArray()));
		HSSFWorkbook wb = new HSSFWorkbook(poiFs);
		assertEquals("CIMS Report", wb.getSheetName(0));

		assertEquals("attachment; filename=\"" + excelView.getReportFileName() + "\"",
				response.getHeader("Content-Disposition"));
	}

	@Test
	public void testPrepareData() {
		CIMSReportExcelView excelView = new CIMSReportExcelView();
		excelView.prepareData("CCIModifiedValidations");
		assertEquals("CCIModifiedValidations" + CimsUtils.getDateStr(new Date()) + ".xls",
				excelView.getReportFileName());
		assertEquals("/report/CCIModifiedValidationsSchema.xml", excelView.getSchemaUrl());
	}
}
