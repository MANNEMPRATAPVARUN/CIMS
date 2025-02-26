package ca.cihi.cims.web.controller.reports;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.Language;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.validator.MissingValidationReportValidator;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Controller
@RequestMapping(value = "/reports/missingValidation")
public class MissingValidationReportController extends ReportBaseController {

	public static final String MISSING_VALIDATION_REPORT = "/reports/missingValidationReport";

	private ClassificationService classificationService;
	private MissingValidationReportValidator validator;

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {

		model.addAttribute("reportViewBean", reportViewBean);
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);
		Collection<ContextIdentifier> openedContextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(reportViewBean.getClassification());
		model.addAttribute("openedContextIdentifiers", openedContextIdentifiers);

	}

	public ClassificationService getClassificationService() {
		return classificationService;
	}

	@RequestMapping(value = "/selectVersionCode", method = RequestMethod.GET)
	@ResponseBody
	public List<TabularConceptValidationDadHoldingModel> getDataHoldings() {
		return classificationService.getDataHoldings(Language.ENGLISH);
	}

	public MissingValidationReportValidator getValidator() {
		return validator;
	}

	/*
	 * when user select classification from the drop down
	 */
	@RequestMapping(value = "/selectClassification", method = RequestMethod.GET)
	@ResponseBody
	public List<ContextIdentifier> getVersionForClassification(
			@RequestParam("baseClassification") final String baseClassification) {
		return lookupService.findNonClosedBaseContextIdentifiers(baseClassification);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String missingValidationReport(Model model) {
		ReportViewBean bean = new ReportViewBean();
		buildModelAttribute(model, bean);
		return MISSING_VALIDATION_REPORT;
	}

	@Autowired
	public void setClassificationService(ClassificationService classificationService) {
		this.classificationService = classificationService;
	}

	@Autowired
	public void setValidator(MissingValidationReportValidator validator) {
		this.validator = validator;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public ValidationResponse validate(ReportViewBean reportViewBean, final BindingResult bindResult) {

		validator.validate(reportViewBean, bindResult);
		ValidationResponse res = new ValidationResponse();
		List<String> errors = validator.getErrorMessages();
		if (!errors.isEmpty()) {
			res.setStatus("FAILED");
			res.setErrors(errors);
		} else {
			res.setToken(tokenService.generate());
			res.setStatus("SUCCESS");
		}
		return res;
	}
}
