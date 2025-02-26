package ca.cihi.cims.web.view;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelReportView extends AbstractExcelView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		Map<String, String> reportData = (Map<String, String>) model.get("reportData");
		// create a worksheet
		HSSFSheet sheet1 = workbook.createSheet("Report1");
		// freeze the first row
		sheet1.createFreezePane(0, 1);
		sheet1.setDefaultColumnWidth(30); 
		 // create style for header cells  
		//keep on eye on cell formatters: 
		//create one and use it several times instead of creating each time for cell, it is a huge memory consumption difference or large data.

		CellStyle style = workbook.createCellStyle();	       
		style.setFillForegroundColor(HSSFColor.BLUE.index);         
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font font = workbook.createFont();         
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         
		font.setColor(HSSFColor.WHITE.index);         
		style.setFont(font); 
		// create header row 
		HSSFRow header1 = sheet1.createRow(0);
		header1.createCell(0).setCellValue("Month");
		header1.getCell(0).setCellStyle(style); 
		header1.createCell(1).setCellValue("Cost");
		header1.getCell(1).setCellStyle(style); 
		
		// create data rows 
		int rowNum1 = 1;
		for (Map.Entry<String, String> entry : reportData.entrySet()) {
			// create the row data
			HSSFRow row1 = sheet1.createRow(rowNum1++);
			row1.createCell(0).setCellValue(entry.getKey());
			row1.createCell(1).setCellValue(entry.getValue());
		}
		
		// create a new Excel sheet 
		HSSFSheet sheet2 = workbook.createSheet("Report2");
		sheet2.setDefaultColumnWidth(100); 
		HSSFRow header2 = sheet2.createRow(0);
		header2.createCell(0).setCellValue("Month");
		header2.createCell(1).setCellValue("Cost");

		int rowNum2 = 1;
		for (Map.Entry<String, String> entry : reportData.entrySet()) {
			// create the row data
			HSSFRow row2 = sheet2.createRow(rowNum2++);
			row2.createCell(0).setCellValue(entry.getKey());
			row2.createCell(1).setCellValue(entry.getValue());
		}
	}
}