package ca.cihi.cims.web.controller.reports;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.util.CimsUtils;

@Component
public class CIMSReportExcelView extends CIMSBaseExcelView {

	private static final Object REPORT_DATA = "reportData";
	private static Map<String, Map<String, String>> reportViewMap = new HashMap<String, Map<String, String>>();
	private String reportFileName;
	private ReportService reportService;

	private String schemaUrl;

	public CIMSReportExcelView() {
		super();
		this.setBeanName("cimsReportExcelView");
		Map<String, String> classificationChangeTabular = new HashMap<String, String>();
		classificationChangeTabular.put("schemaUrl", "/report/ClassificationChangeTabularReportSchema.xml");
		classificationChangeTabular.put("reportFileName", "ClassificationChangeTabularReport");
		classificationChangeTabular.put("templateUrl", "classpath:/report/ClassificationChangeTabularReportTemplate");
		reportViewMap.put("ClassificationChangeTabular", classificationChangeTabular);
		Map<String, String> classificationChangeIndex = new HashMap<String, String>();
		classificationChangeIndex.put("schemaUrl", "/report/ClassificationChangeIndexReportSchema.xml");
		classificationChangeIndex.put("reportFileName", "ClassificationChangeIndexReport");
		classificationChangeIndex.put("templateUrl", "classpath:/report/ClassificationChangeIndexReportTemplate");
		reportViewMap.put("ClassificationChangeIndex", classificationChangeIndex);
		Map<String, String> cciModifiedValidations = new HashMap<String, String>();
		cciModifiedValidations.put("schemaUrl", "/report/CCIModifiedValidationsSchema.xml");
		cciModifiedValidations.put("reportFileName", "CCIModifiedValidations");
		cciModifiedValidations.put("templateUrl", "classpath:/report/CCIModifiedValidationsTemplate");
		reportViewMap.put("CCIModifiedValidations", cciModifiedValidations);
		Map<String, String> icdModifiedValidations = new HashMap<String, String>();
		icdModifiedValidations.put("schemaUrl", "/report/ICDModifiedValidationsSchema.xml");
		icdModifiedValidations.put("reportFileName", "ICDModifiedValidations");
		icdModifiedValidations.put("templateUrl", "classpath:/report/ICDModifiedValidationsTemplate");
		reportViewMap.put("ICDModifiedValidations", icdModifiedValidations);
		Map<String, String> missingValidationCCI = new HashMap<String, String>();
		missingValidationCCI.put("schemaUrl", "/report/MissingValidationReportSchema.xml");
		missingValidationCCI.put("reportFileName", "MissingValidationReport");
		missingValidationCCI.put("templateUrl", "classpath:/report/MissingValidationReportTemplate");
		reportViewMap.put("MissingValidationCCI", missingValidationCCI);
		Map<String, String> missingValidationICD10CA = new HashMap<String, String>();
		missingValidationICD10CA.put("schemaUrl", "/report/MissingValidationReportSchema.xml");
		missingValidationICD10CA.put("reportFileName", "MissingValidationReport");
		missingValidationICD10CA.put("templateUrl", "classpath:/report/MissingValidationReportTemplate");
		reportViewMap.put("MissingValidationICD-10-CA", missingValidationICD10CA);

		Map<String, String> cciNewTableCodes = new HashMap<String, String>();
		cciNewTableCodes.put("schemaUrl", "/report/CCINewTableCodesWithCodingDirectivesSchema.xml");
		cciNewTableCodes.put("reportFileName", "CCINewTableCodesWithCodingDirectivesReport");
		cciNewTableCodes.put("templateUrl", "classpath:/report/CCINewTableCodesWithCodingDirectivesTemplate");
		reportViewMap.put("CCINewTableCodesWithCodingDirectives", cciNewTableCodes);

		Map<String, String> sendBackDetail = new HashMap<String, String>();
		sendBackDetail.put("schemaUrl", "/report/SendBackDetailSchema.xml");
		sendBackDetail.put("reportFileName", "SendBackDetailReport");
		sendBackDetail.put("templateUrl", "classpath:/report/SendBackDetailTemplate");
		reportViewMap.put("SendBackDetail", sendBackDetail);

		Map<String, String> qaSummaryMetrics = new HashMap<String, String>();
		qaSummaryMetrics.put("schemaUrl", "/report/QASummaryMetricsSchema.xml");
		qaSummaryMetrics.put("reportFileName", "QASummaryMetricsReport");
		qaSummaryMetrics.put("templateUrl", "classpath:/report/QASummaryMetricsTemplate");
		reportViewMap.put("QASummaryMetrics", qaSummaryMetrics);

		Map<String, String> icdModifiedValidCode = new HashMap<String, String>();
		icdModifiedValidCode.put("schemaUrl", "/report/ICDModifiedValidCodeSchema.xml");
		icdModifiedValidCode.put("reportFileName", "ICDModifiedValidCodeReport");
		icdModifiedValidCode.put("templateUrl", "classpath:/report/ICDModifiedValidCodeTemplate");
		reportViewMap.put("ICDModifiedValidCode", icdModifiedValidCode);

		Map<String, String> qaErrorDescriptions = new HashMap<String, String>();
		qaErrorDescriptions.put("schemaUrl", "/report/QAErrorDescriptionsReportSchema.xml");
		// qaErrorDescriptions.put("reportFileName", "QAErrorDescriptionsReport");
		qaErrorDescriptions.put("reportFileName", "SendBackDetailReport");
		qaErrorDescriptions.put("templateUrl", "classpath:/report/QAErrorDescriptionsReportTemplate");
		reportViewMap.put("QAErrorDescriptions", qaErrorDescriptions);
		
	}

	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		final InputStream schemaFile = this.getClass().getResourceAsStream(getSchemaUrl());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + getReportFileName() + "\"");
		@SuppressWarnings("unchecked")
		Map<String, Object> reportData = (Map<String, Object>) model.get(REPORT_DATA);
		reportService.generateReport(reportData, workbook, schemaFile);
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}

	public void prepareData(String reportType) {
		Map<String, String> viewData = reportViewMap.get(reportType);
		setSchemaUrl(viewData.get("schemaUrl"));
		setReportFileName(viewData.get("reportFileName") + CimsUtils.getDateStr(new Date()) + ".xls");
		setUrl(viewData.get("templateUrl"));
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	
}
