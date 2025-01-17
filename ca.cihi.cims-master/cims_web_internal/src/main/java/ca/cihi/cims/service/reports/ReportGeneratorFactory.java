package ca.cihi.cims.service.reports;

public interface ReportGeneratorFactory {
	public <T extends ReportGenerator> T createReportGenerator(String reportType);
}
