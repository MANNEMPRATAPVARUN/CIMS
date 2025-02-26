package ca.cihi.cims.web.controller.reports;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;

public class ReportBaseController {

	protected LookupService lookupService;

	protected ReportService reportService;
	
	protected TokenService tokenService; 
	
	public LookupService getLookupService() {
		return lookupService;
	}
	
	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public ReportService getReportService() {
		return reportService;
	}

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public TokenService getTokenService() {
		return tokenService;
	}

	@Autowired
	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}
}
