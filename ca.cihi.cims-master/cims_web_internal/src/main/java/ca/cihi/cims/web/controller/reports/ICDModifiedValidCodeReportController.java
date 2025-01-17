package ca.cihi.cims.web.controller.reports;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Controller
@RequestMapping(value = "/reports/icdModifiedValidCode")
public class ICDModifiedValidCodeReportController extends ReportBaseController {

	public static final String ICD_MODIFIED_VALID_CODE = "/reports/icdModifiedValidCodeReport";

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {

		reportViewBean.setClassification("ICD-10-CA");
		model.addAttribute("reportViewBean", reportViewBean);
		List<String> versionYears = getLookupService().findVersionYears(reportViewBean.getClassification());
		versionYears.remove(0);
		model.addAttribute("versionYears", versionYears);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String icdModifiedCodeValidationsReport(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return ICD_MODIFIED_VALID_CODE;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public ValidationResponse validate() {

		ValidationResponse res = new ValidationResponse();

		res.setToken(getTokenService().generate());
		res.setStatus("SUCCESS");
		return res;
	}
}
