package ca.cihi.cims.web.controller.reports;

import java.util.ArrayList;
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

import ca.cihi.cims.model.UserProfile;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguageDTO;
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.validator.ReviewGroupReportValidator;
import ca.cihi.cims.web.bean.report.QAErrorReportViewBean;

@Controller
@RequestMapping(value = "/reports/qaErrorDescriptions")
public class QAErrorDescriptionsReportController extends ReportBaseController {

	private static final Log LOGGER = LogFactory.getLog(QAErrorDescriptionsReportController.class);
	public static final int MAX_AUTOCOMPLETE_SEARCH_RESULTS = 18;
	public static final String QA_ERROR_DESCRIPTIONS_REPORT = "/reports/qaErrorDescriptionsReport";

	private CIMSReportExcelView excelView;
	private AdminService adminService;
	private ReviewGroupReportValidator validator;

	private ViewService viewService;

	private void buildModelAttribute(Model model, QAErrorReportViewBean reportViewBean) {
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);

		populateLanguages(model);
		populateOwners(model);
		populateFromStatuses(model);

		if (reportViewBean == null) {
			reportViewBean = new QAErrorReportViewBean();
			model.addAttribute("reportViewBean", reportViewBean);
		}
	}

	@RequestMapping(value = "/checkDownloadProgress")
	public @ResponseBody
	ValidationResponse checkDownloadProgress(@RequestParam(value = "token") String token) {

		ValidationResponse res = new ValidationResponse();

		res.setToken(tokenService.check(token));
		res.setStatus("SUCCESS");

		return res;
	}

	@RequestMapping(value = "/generateQAErrorDescriptionsReport")
	public ModelAndView generateQAErrorDescriptionsReport(@Valid QAErrorReportViewBean reportViewBean,
			final BindingResult bindResult, Model model, @RequestParam(value = "token") String token) {

		Map<String, Object> reportData = getReportService().generateReportData(reportViewBean);
		tokenService.remove(token);
		excelView.prepareData(reportViewBean.getReportType());
		return new ModelAndView(excelView, "reportData", reportData);
	}

	public AdminService getAdminService() {
		return adminService;
	}

	public CIMSReportExcelView getExcelView() {
		return excelView;
	}

	public ReviewGroupReportValidator getValidator() {
		return validator;
	}

	/*
	 * when user select classification from the drop down
	 */
	@RequestMapping(value = "/getBaseContextYears", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getVersionForClassification(@RequestParam("baseClassification") final String baseClassification) {
		LOGGER.info("QAErrorDescriptionsReportController.getVersionForClassification()> lookupService.findBaseContextYears for baseClassification="
				+ baseClassification);
		List<String> years = lookupService.findBaseContextYears(baseClassification);
		LOGGER.info("QAErrorDescriptionsReportController.getVersionForClassification()>years.size()=" + years.size());
		return years;
	}

	public ViewService getViewService() {
		return viewService;
	}

	private void populateFromStatuses(Model model) {
		List<String> fromStatuses = new ArrayList<String>();
		fromStatuses.add("Valid");
		fromStatuses.add("Accepted");
		fromStatuses.add("Translation Done");
		fromStatuses.add("Validation Done");
		fromStatuses.add("Realized");
		fromStatuses.add("Iterative QA Done");

		model.addAttribute("fromStatuses", fromStatuses);
	}

	private void populateLanguages(Model model) {
		List<ChangeRequestLanguageDTO> languages = lookupService.findChangeRequestLanguages();
		model.addAttribute("languages", languages);
	}

	private void populateOwners(Model model) {
		List<UserProfile> userProfiles = lookupService.findUserProfiles();
		model.addAttribute("owners", userProfiles);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String qaErrorDescriptions(Model model) {
		buildModelAttribute(model, null);
		return QA_ERROR_DESCRIPTIONS_REPORT;
	}

	@Autowired
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@Autowired
	public void setExcelView(CIMSReportExcelView excelView) {
		this.excelView = excelView;
	}

	@Autowired
	public void setValidator(ReviewGroupReportValidator validator) {
		this.validator = validator;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	@RequestMapping(value = "/validate")
	public @ResponseBody
	ValidationResponse validate(QAErrorReportViewBean reportViewBean, final BindingResult bindResult, Model model) {

		validator.validate(reportViewBean, bindResult);
		ValidationResponse res = new ValidationResponse();
		List<String> errors = validator.getErrorMessages();
		if (errors != null && errors.size() > 0) {
			res.setStatus("FAILED");
			res.setErrors(errors);
		} else {
			res.setToken(tokenService.generate());
			res.setStatus("SUCCESS");
		}
		return res;
	}

}
