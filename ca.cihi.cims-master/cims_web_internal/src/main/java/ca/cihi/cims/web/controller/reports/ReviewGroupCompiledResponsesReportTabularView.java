package ca.cihi.cims.web.controller.reports;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import ca.cihi.cims.service.reports.ReportService;


public class ReviewGroupCompiledResponsesReportTabularView extends CIMSBaseExcelView {

	public ReviewGroupCompiledResponsesReportTabularView(){
		super();
		setUrl(TEMPLATE_URL);
	}

	public static final String REPORT_DATA="reportData";

	private static final String TEMPLATE_URL = "classpath:/report/ReviewGroupCompiledResponsesTabularReportTemplate";

	private static final String SCHEMA_URL = "/report/ReviewGroupCompiledResponsesTabularReportSchema.xml";

	public static final String REPORT_FILE_NAME = "ReviewGroupCompiledResponsesTabularReport.xls";

	private ReportService reportService;

	public ReportService getReportService() {
		return reportService;
	}

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final InputStream schemaFile = this.getClass().getResourceAsStream(SCHEMA_URL);
		response.setHeader("Content-Disposition", "attachment; filename=\""+REPORT_FILE_NAME+"\"");
		@SuppressWarnings("unchecked")
		Map<String, Object> reportData = (Map<String, Object>)model.get(REPORT_DATA);
		reportService.generateReport(reportData, workbook, schemaFile);
	}

}
