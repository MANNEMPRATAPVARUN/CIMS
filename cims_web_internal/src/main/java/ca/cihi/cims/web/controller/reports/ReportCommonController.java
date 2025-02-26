package ca.cihi.cims.web.controller.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;
import ca.cihi.cims.web.bean.CodeSearchResultBean;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Controller
public class ReportCommonController {

	/**
	 * How many search results should we display in the autocomplete flyout list?
	 */
	public static final int MAX_AUTOCOMPLETE_SEARCH_RESULTS = 18;
	private ViewService viewService;
	private TokenService tokenService;
	private LookupService lookupService;
	private ReportService reportService;

	private CIMSReportExcelView excelView;

	@RequestMapping(value = "/reports/checkDownloadProgress")
	@ResponseBody
	public ValidationResponse checkDownloadProgress(@RequestParam(value = "token") String token) {

		ValidationResponse res = new ValidationResponse();

		res.setToken(getTokenService().check(token));
		res.setStatus("SUCCESS");

		return res;
	}

	@RequestMapping(value = "/reports/generate")
	public ModelAndView generateReport(@Valid ReportViewBean reportViewBean, final BindingResult bindResult,
			Model model, @RequestParam(value = "token") String token) {
		Map<String, Object> reportData = getReportService().generateReportData(reportViewBean);
		tokenService.remove(token);
		excelView.prepareData(reportViewBean.getReportType());
		return new ModelAndView(excelView, "reportData", reportData);
	}

	public CIMSReportExcelView getExcelView() {
		return excelView;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public TokenService getTokenService() {
		return tokenService;
	}

	@RequestMapping(value = "/reports/getBaseContextYears", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getVersionForClassification(@RequestParam("baseClassification") final String baseClassification) {
		List<String> years = getLookupService().findBaseContextYears(baseClassification);
		return years;
	}

	@RequestMapping(value = "/reports/getBaseContextYearsAll", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getVersionForClassificationAll(
			@RequestParam("baseClassification") final String baseClassification) {
		List<String> years = lookupService.findBaseContextYearsReport(baseClassification);
		return years;
	}

	public ViewService getViewService() {
		return viewService;
	}

	@RequestMapping(value = "/reports/listIndexBooks", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<CodeDescription> listIndexBooks(@RequestParam("classification") final String classification) {

		return viewService.getAllBookIndexesNoLang(classification);
	}

	@RequestMapping(value = "/reports/searchCodeValues", method = RequestMethod.GET)
	@ResponseBody
	public List<CodeSearchResultBean> searchCodeValues(@RequestParam("classification") final String classification,
			@RequestParam("term") final String searchString, @RequestParam("ccp_cid") final Long contextId) {
		List<CodeSearchResultBean> results = new ArrayList<CodeSearchResultBean>();
		for (SearchResultModel result : viewService.getSearchResultsByCodeCat1(classification, searchString,
				MAX_AUTOCOMPLETE_SEARCH_RESULTS, contextId)) {
			results.add(new CodeSearchResultBean(result.getLongDescription(), result.getConceptCode(), result
					.getConceptId()));
		}
		return results;
	}

	@RequestMapping(value = "/reports/searchIndexBooks", method = RequestMethod.GET)
	@ResponseBody
	public List<CodeSearchResultBean> searchLeadTerms(@RequestParam("classification") final String classification,
			@RequestParam("indexElementId") final Long indexElementId, @RequestParam("term") final String searchString) {
		List<CodeSearchResultBean> results = new ArrayList<CodeSearchResultBean>();
		for (SearchResultModel result : getViewService().getIndexTermSearchResults(classification, indexElementId,
				searchString, MAX_AUTOCOMPLETE_SEARCH_RESULTS)) {
			results.add(new CodeSearchResultBean(result.getLongDescription(), result.getConceptCode(), result
					.getConceptId()));
		}
		return results;
	}

	@RequestMapping(value = "/reports/findPriorYear", method = RequestMethod.GET)
	@ResponseBody
	public List<ContextIdentifier> selectCurrentYear(@RequestParam("currentYear") final String currentYear,
			@RequestParam("classification") final String classification) {
		return getLookupService().findPriorBaseContextIdentifiers(classification, currentYear);
	}

	@Autowired
	public void setExcelView(CIMSReportExcelView excelView) {
		this.excelView = excelView;
	}

	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	@Autowired
	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}
}
