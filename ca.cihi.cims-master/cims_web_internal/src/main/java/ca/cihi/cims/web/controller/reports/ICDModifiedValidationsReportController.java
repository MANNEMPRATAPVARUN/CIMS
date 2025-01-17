package ca.cihi.cims.web.controller.reports;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Controller
@RequestMapping(value = "/reports/icdModifiedValidations")
public class ICDModifiedValidationsReportController extends ReportBaseController {

	public static final String ICD_MODIFIED_VALIDATION_REPORT = "/reports/icdModifiedValidationsReport";

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {

		reportViewBean.setClassification("ICD-10-CA");
		model.addAttribute("reportViewBean", reportViewBean);
		Collection<ContextIdentifier> openedContextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(reportViewBean.getClassification());
		model.addAttribute("openedContextIdentifiers", openedContextIdentifiers);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String icdModifiedCodeValidationsReport(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return ICD_MODIFIED_VALIDATION_REPORT;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public ValidationResponse validate() {

		ValidationResponse res = new ValidationResponse();

		res.setToken(tokenService.generate());
		res.setStatus("SUCCESS");
		return res;
	}

}
