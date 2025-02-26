package ca.cihi.cims.service.reports;

import java.io.InputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import ca.cihi.cims.web.bean.report.ReportViewBean;

public interface ReportService {

	void generateReport(final Map<String, Object> reportData, Workbook workbook, InputStream schemaFile)  throws Exception;

	Map<String, Object> generateReportData(
			ReportViewBean viewBean);
}
