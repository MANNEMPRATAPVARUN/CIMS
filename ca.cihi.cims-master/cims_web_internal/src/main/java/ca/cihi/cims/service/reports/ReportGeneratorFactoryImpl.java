package ca.cihi.cims.service.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import ca.cihi.cims.CIMSException;

@Component
public class ReportGeneratorFactoryImpl implements ReportGeneratorFactory {

	private static Map<String, ReportGenerator> reportGenerators = new HashMap<String, ReportGenerator>();

	public ReportGeneratorFactoryImpl() {
		reportGenerators.put("ClassificationChangeTabular", new ClassificationChangeTabular());
		reportGenerators.put("ClassificationChangeIndex", new ClassificationChangeIndex());
		reportGenerators.put("MissingValidationCCI", new MissingValidationCCI());
		reportGenerators.put("MissingValidationICD-10-CA", new MissingValidationICD());
		reportGenerators.put("ICDModifiedValidations", new ICDModifiedValidations());
		reportGenerators.put("CCIModifiedValidations", new CCIModifiedValidations());
		reportGenerators.put("CCINewTableCodesWithCodingDirectives", new CCINewTableCodesWithCodingDirectives());
		reportGenerators.put("SendBackDetail", new SendBackDetail());
		reportGenerators.put("QASummaryMetrics", new QASummaryMetrics());
		reportGenerators.put("ICDModifiedValidCode", new ICDModifiedValidCode());

		reportGenerators.put("ReviewGroupOutboundQuestionsTabular", new ReviewGroupOutboundQuestionsTabular());
		reportGenerators.put("ReviewGroupOutboundQuestionsIndex", new ReviewGroupOutboundQuestionsIndex());
		reportGenerators.put("ReviewGroupCompiledResponsesTabular", new ReviewGroupCompiledResponsesTabular());
		reportGenerators.put("ReviewGroupCompiledResponsesIndex", new ReviewGroupCompiledResponsesIndex());
		reportGenerators.put("QAErrorDescriptions", new QAErrorDescriptions());

	}

	@Override
	public <T extends ReportGenerator> T createReportGenerator(String reportType) {
		@SuppressWarnings("unchecked")
		T reportGenerator = (T) reportGenerators.get(reportType);
		if (reportGenerator != null) {
			return reportGenerator;
		} else {
			throw new CIMSException("Report type is undefined: " + reportType);
		}
	}
}
