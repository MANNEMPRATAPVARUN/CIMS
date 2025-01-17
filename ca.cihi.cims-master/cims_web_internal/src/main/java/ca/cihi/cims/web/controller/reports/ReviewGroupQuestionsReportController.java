package ca.cihi.cims.web.controller.reports;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.reports.ReportService;
import ca.cihi.cims.service.reports.TokenService;
import ca.cihi.cims.validator.ReviewGroupReportValidator;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguageDTO;
import ca.cihi.cims.model.Distribution;

@Controller
@RequestMapping(value="/reports/reviewGroup")
public class ReviewGroupQuestionsReportController {

	private static final Log LOGGER = LogFactory.getLog(ReviewGroupQuestionsReportController.class);

	public static final int MAX_AUTOCOMPLETE_SEARCH_RESULTS = 18;

	public static final String REVIEW_GROUP_OUTBOUND_QUESTIONS="/reports/reviewGroupOutboundQuestions";
	public static final String REVIEW_GROUP_COMPILED_RESPONSES="/reports/reviewGroupCompiledResponses";

	protected static final String REVIEW_GROUP_OUTBOUND_QUESTIONS_REPORT = "/reports/reviewGroupOutboundQuestionsReport";
	protected static final String REVIEW_GROUP_COMPILED_RESPONSES_REPORT = "/reports/reviewGroupCompiledResponsesReport";

	private LookupService lookupService;
	private AdminService adminService;

	public LookupService getLookupService() {
		return lookupService;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Autowired
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	private ReportService reportService;

	public ReportService getReportService() {
		return reportService;
	}

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	private ReviewGroupReportValidator validator;

	public ReviewGroupReportValidator getValidator() {
		return validator;
	}
	@Autowired
	public void setValidator(ReviewGroupReportValidator validator) {
		this.validator = validator;
	}

	private ViewService viewService;

	public ViewService getViewService() {
		return viewService;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	private TokenService tokenService;

	public TokenService getTokenService() {
		return tokenService;
	}

	@Autowired
	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}


	/*
	 * when user select classification from the drop down
	 */
	@RequestMapping(value = "/selectClassification", method = RequestMethod.GET)
	public @ResponseBody
	List<ContextIdentifier> getVersionForClassification(
			@RequestParam("baseClassification") final String baseClassification) {
		List<ContextIdentifier> contextIdentifiers =  lookupService.findNonClosedBaseContextIdentifiers(baseClassification);
		return contextIdentifiers;
	}

	@RequestMapping(value = "/selectRequestCategory", method = RequestMethod.GET)
	public @ResponseBody
	List<ContextIdentifier> getVersionForClassificationRequestCategory(
			@RequestParam("baseClassification") final String baseClassification,
			@RequestParam("requestCategory") final String requestCategory) {

		List<ContextIdentifier> contextIdentifiers = null;
        if ("Index".equals(requestCategory)) {
			contextIdentifiers = lookupService.findNonClosedBaseContextIdentifiersIndex(baseClassification);
		} else {
			contextIdentifiers = lookupService.findNonClosedBaseContextIdentifiers(baseClassification);
		}

		return contextIdentifiers;
	}

	@RequestMapping(value="/validate")
	public @ResponseBody ValidationResponse validate(ReviewGroupReportViewBean reportViewBean, final BindingResult bindResult, Model model){

		validator.validate(reportViewBean, bindResult);
		ValidationResponse res = new ValidationResponse();
		List<String> errors = validator.getErrorMessages();
		if(errors!=null&&errors.size()>0){
			res.setStatus("FAILED");
			res.setErrors(errors);
		}else{
			res.setToken(tokenService.generate());
			res.setStatus("SUCCESS");
		}
		return res;
	}

	@RequestMapping(value="/checkDownloadProgress")
	public @ResponseBody ValidationResponse checkDownloadProgress(@RequestParam(value="token") String token){

		ValidationResponse res = new ValidationResponse();

			res.setToken(tokenService.check(token));
			res.setStatus("SUCCESS");

		return res;
	}

	@RequestMapping(value="/generateOutboundQuestionsReport")
	public ModelAndView generateOutboundQuestionsReport(@Valid ReviewGroupReportViewBean reportViewBean, final BindingResult bindResult, Model model, @RequestParam(value="token") String token){
		if("Tabular".equals(reportViewBean.getRequestCategory())){
			reportViewBean.setReportType("ReviewGroupOutboundQuestionsTabular");
			Map<String, Object> reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
			return new ModelAndView("reviewGroupOutboundQuestionsTabularReportView", "reportData", reportData);
		}else{
			reportViewBean.setReportType("ReviewGroupOutboundQuestionsIndex");
			Map<String, Object> reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
			return new ModelAndView("reviewGroupOutboundQuestionsIndexReportView", "reportData", reportData);
		}
	}

	@RequestMapping(value="/generateCompiledResponsesReport")
	public ModelAndView generateCompiledResponsesReport(@Valid ReviewGroupReportViewBean reportViewBean, final BindingResult bindResult, Model model, @RequestParam(value="token") String token){
		if("Tabular".equals(reportViewBean.getRequestCategory())){
			reportViewBean.setReportType("ReviewGroupCompiledResponsesTabular");
			Map<String, Object> reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
			return new ModelAndView("reviewGroupCompiledResponsesTabularReportView", "reportData", reportData);
		}else{
			reportViewBean.setReportType("ReviewGroupCompiledResponsesIndex");
			Map<String, Object> reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
			return new ModelAndView("reviewGroupCompiledResponsesIndexReportView", "reportData", reportData);
		}
	}


	@RequestMapping(method=RequestMethod.GET, value="/outboundQuestions")
	public String reviewGroupQuestions(Model model){
		buildModelAttribute(model, null);
		return REVIEW_GROUP_OUTBOUND_QUESTIONS;
	}

	@RequestMapping(method=RequestMethod.GET, value="/compiledResponses")
	public String reviewGroupResponses(Model model){
		buildModelAttribute(model, null);
		return REVIEW_GROUP_COMPILED_RESPONSES;
	}


	private void buildModelAttribute(Model model, ReviewGroupReportViewBean reportViewBean){
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);

        populateLanguages(model);
        populateReviewGroupList(model);

		if(reportViewBean==null){
			reportViewBean = new ReviewGroupReportViewBean();
			model.addAttribute("reportViewBean", reportViewBean);
		}else{
			if("Index".equals(reportViewBean.getRequestCategory())){
				List<CodeDescription> allBookIndexes = viewService.getAllBookIndexesNoLang(reportViewBean.getClassification());
				model.addAttribute("allBookIndexes", allBookIndexes);
			}

		}
	}


	private void populateLanguages(Model model) {
		List<ChangeRequestLanguageDTO> languages = lookupService.findChangeRequestLanguages();
		model.addAttribute("languages", languages);
    }

	private void populateReviewGroupList(Model model) {
		List<Distribution> reviewGroupList = adminService.getReviewGroupList();
		model.addAttribute("reviewGroupList", reviewGroupList);
    }

	@RequestMapping(value="/searchPatternTopics", method=RequestMethod.GET)
	@ResponseBody public List<String> searchPatternTopics(@RequestParam("classification") final String classification,
			@RequestParam("term") final String searchString){

		List<String> results = lookupService.getSearchPatternTopics2(classification, searchString, MAX_AUTOCOMPLETE_SEARCH_RESULTS);

		return results;
	}

	//@RequestMapping(method=RequestMethod.GET, value="/generateOutboundQuestionsReportHtml")
	@RequestMapping(value="/generateOutboundQuestionsReportHtml")
	public String generateOutboundQuestionsReportHtml(@Valid ReviewGroupReportViewBean reportViewBean,
	                                                        final BindingResult bindResult,
	                                                        Model model,
	                                                        @RequestParam(value="token") String token){
		LOGGER.info("ReviewGroupQuestionsReportController.generateOutboundQuestionsReportHtml()> begin...");

		Map<String, Object> reportData = null;
		if("Tabular".equals(reportViewBean.getRequestCategory())){
			reportViewBean.setReportType("ReviewGroupOutboundQuestionsTabular");
			reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
		}else{
			reportViewBean.setReportType("ReviewGroupOutboundQuestionsIndex");
			reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
		}

        List<Map<String, Object>> detailDataList = (List<Map<String, Object>>)reportData.get("detail1");
        int resultSize = detailDataList.size();
        if (resultSize > 1000) {
			detailDataList.remove(1001);
		}

		model.addAttribute("reportData", reportData);
		//model.addAttribute("reportDetail", reportData.get("detail1"));
		model.addAttribute("reportDetail", detailDataList);
		model.addAttribute("resultSize", resultSize);

		return  REVIEW_GROUP_OUTBOUND_QUESTIONS_REPORT;
	}

	//@RequestMapping(method=RequestMethod.GET, value="/generateCompiledResponsesReportHtml")
	@RequestMapping(value="/generateCompiledResponsesReportHtml")
	public String generateCompiledResponsesReportHtml(@Valid ReviewGroupReportViewBean reportViewBean,
	                                                        final BindingResult bindResult,
	                                                        Model model,
	                                                        @RequestParam(value="token") String token){
		LOGGER.info("ReviewGroupQuestionsReportController.generateCompiledResponsesReportHtml()> begin...");
        Map<String, Object> reportData = null;
		if("Tabular".equals(reportViewBean.getRequestCategory())){
			reportViewBean.setReportType("ReviewGroupCompiledResponsesTabular");
			reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
		}else{
			reportViewBean.setReportType("ReviewGroupCompiledResponsesIndex");
			reportData = reportService.generateReportData(reportViewBean);
			tokenService.remove(token);
		}

        List<Map<String, Object>> detailDataList = (List<Map<String, Object>>)reportData.get("detail1");
        int resultSize = detailDataList.size();
        if (resultSize > 1000) {
			detailDataList.remove(1001);
		}

		model.addAttribute("reportData", reportData);
		//model.addAttribute("reportDetail", reportData.get("detail1"));
		model.addAttribute("reportDetail", detailDataList);
		model.addAttribute("resultSize", resultSize);

		return  REVIEW_GROUP_COMPILED_RESPONSES_REPORT;
	}

}
