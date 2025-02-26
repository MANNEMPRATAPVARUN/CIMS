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
@RequestMapping(value = "/reports/cciNewTableCodes")
public class CCINewTableCodesWithCodingDirectivesReportController extends ReportBaseController {

	public static final String CCI_NEW_TABLE_CODES_REPORT = "/reports/cciNewTableCodesReport";

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {

		reportViewBean.setClassification("CCI");
		model.addAttribute("reportViewBean", reportViewBean);
		List<String> versionYears = lookupService.findOpenVersionYears(reportViewBean.getClassification());
		model.addAttribute("versionYears", versionYears);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String cciNewTableCodesReport(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return CCI_NEW_TABLE_CODES_REPORT;
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
