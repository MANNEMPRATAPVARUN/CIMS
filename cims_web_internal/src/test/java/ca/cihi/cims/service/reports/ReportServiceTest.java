package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.web.bean.report.ReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ReportServiceTest {
	@Autowired
	private ReportService reportService;

	@Test(expected = Exception.class)
	public void testGenerateReport() throws Exception {
		final Resource templateFile = new ClassPathResource("/report/ClassificationChangeTabularReportTemplate.xls",
				this.getClass());

		final Resource schemaFile = new ClassPathResource("/report/ClassificationChangeTabularReportSchema.xml",
				this.getClass());
		HSSFWorkbook workBook = new HSSFWorkbook(templateFile.getInputStream());

		Map<String, Object> reportData = new HashMap<String, Object>();

		reportData.put("classification", "ICD-10-CA");
		reportData.put("valueFrom", "A00");
		reportData.put("requestCategory", "Tabular");
		reportData.put("valueTo", "B00");

		Map<String, Object> detailHeader = new HashMap<String, Object>();
		detailHeader.put("year1", 2015);
		detailHeader.put("year2", 2016);
		detailHeader.put("year3", 2017);
		detailHeader.put("year4", 2018);

		reportData.put("detailHeader", detailHeader);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> detailData1 = new HashMap<String, Object>();
		detailData1.put("codeValue", "A00");
		detailData1.put("year1", "CR-00785");
		detailData1.put("year2", "CR-00786");
		detailData1.put("year3", "CR-00777");
		detailData1.put("year4", "CR-00888");
		detailDataList.add(detailData1);

		Map<String, Object> detailData2 = new HashMap<String, Object>();
		detailData2.put("codeValue", "A01");
		detailData2.put("year1", "CR-00046");
		detailData2.put("year3", "CR-00733");
		detailDataList.add(detailData2);

		Map<String, Object> detailData3 = new HashMap<String, Object>();
		detailData3.put("codeValue", "A04");
		detailData3.put("year2", "CR-00026\nCR-00014");
		detailData3.put("year4", "CR-00006");
		detailDataList.add(detailData3);

		reportService.generateReport(reportData, workBook, schemaFile.getInputStream());

		HSSFSheet sheet = workBook.getSheet("CIMS Report");
		HSSFRow row = sheet.getRow(3);
		HSSFCell cell = row.getCell(2);
		assertEquals("ICD-10-CA", cell.getStringCellValue());

		row = sheet.getRow(10);
		cell = row.getCell(2);
		assertEquals(2015, (long) cell.getNumericCellValue());

		row = sheet.getRow(12);
		cell = row.getCell(1);
		assertEquals("A01", cell.getStringCellValue());

		row = sheet.getRow(13);
		cell = row.getCell(1);
		assertEquals("A04", cell.getStringCellValue());
		cell = row.getCell(3);
		assertEquals("CR-00026\nCR-00014", cell.getStringCellValue());

		final Resource templateFile1 = new ClassPathResource("/report/ICDModifiedValidCodeTemplate.xls",
				this.getClass());

		final Resource schemaFile1 = new ClassPathResource("/report/ICDModifiedValidCodeSchema.xml", this.getClass());
		HSSFWorkbook workBook1 = new HSSFWorkbook(templateFile1.getInputStream());

		Map<String, Object> reportData1 = new HashMap<String, Object>();

		reportData1.put("currentYear", "2016");
		reportData1.put("codeCount", 2);

		List<Map<String, Object>> detailDataList1 = new ArrayList<Map<String, Object>>();
		reportData1.put("detail1", detailDataList1);

		Map<String, Object> detailData11 = new HashMap<String, Object>();
		detailData11.put("codeValue", "A00.9");
		detailData11.put("currentFlag", "Y");
		detailData11.put("priorFlag", "N");

		detailDataList1.add(detailData11);

		Map<String, Object> detailData12 = new HashMap<String, Object>();
		detailData12.put("codeValue", "C06.1");
		detailData12.put("currentFlag", "N");
		detailData12.put("priorFlag", "Y");

		detailDataList1.add(detailData12);

		reportService.generateReport(reportData1, workBook1, schemaFile1.getInputStream());
		HSSFSheet sheet1 = workBook1.getSheet("CIMS Report");
		HSSFRow row1 = sheet1.getRow(3);
		HSSFCell cell1 = row1.getCell(2);
		assertEquals("2016", cell1.getStringCellValue());

		HSSFRow row2 = sheet1.getRow(4);
		HSSFCell cell2 = row2.getCell(2);
		assertEquals(2, (long) cell2.getNumericCellValue());

		final Resource templateFile2 = new ClassPathResource("/report/ICDModifiedValidCodeTemplate1.xls",
				this.getClass());

		final Resource schemaFile2 = new ClassPathResource("/report/ICDModifiedValidCodeSchema1.xml", this.getClass());

		HSSFWorkbook workBook2 = new HSSFWorkbook(templateFile2.getInputStream());

		Map<String, Object> reportData2 = new HashMap<String, Object>();

		reportData2.put("currentYear", "2016");
		reportData2.put("codeCount", 2);

		List<Map<String, Object>> detailDataList2 = new ArrayList<Map<String, Object>>();
		reportData2.put("detail1", detailDataList2);

		reportService.generateReport(reportData2, workBook2, schemaFile2.getInputStream());
	}

	@Test
	public void testGenerateReportData() {
		ReportViewBean reportViewBean = new ReportViewBean();
		reportViewBean.setReportType("MissingValidationICD-10-CA");
		reportViewBean.setCodeFrom("A00");
		reportViewBean.setCodeTo("B00");
		reportViewBean.setDataHolding("DAD");
		reportViewBean.setDataHoldingCode("1");
		reportViewBean.setYear("2016");
		reportViewBean.setClassification("ICD-10-CA");

		Map<String, Object> reportData = reportService.generateReportData(reportViewBean);

		assertNotNull(reportData);
		assertEquals(reportData.get("classification"), "ICD-10-CA");

	}
}
