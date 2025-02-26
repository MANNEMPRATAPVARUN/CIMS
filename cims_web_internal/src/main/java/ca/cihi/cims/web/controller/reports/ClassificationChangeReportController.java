package ca.cihi.cims.web.controller.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.validator.ClassificationChangeReportValidator;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Controller
@RequestMapping(value = "/reports/classificationChange")
public class ClassificationChangeReportController extends ReportBaseController {

	public static final String CLASSIFICATION_CHANGE_REPORT = "/reports/classificationChangeReport";

	private ClassificationChangeReportValidator validator;

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);
		model.addAttribute("reportViewBean", reportViewBean);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String classificationChange(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return CLASSIFICATION_CHANGE_REPORT;
	}

	public ClassificationChangeReportValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(ClassificationChangeReportValidator validator) {
		this.validator = validator;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public ValidationResponse validate(ReportViewBean reportViewBean, final BindingResult bindResult) {

		getValidator().validate(reportViewBean, bindResult);
		ValidationResponse res = new ValidationResponse();
		List<String> errors = getValidator().getErrorMessages();
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
