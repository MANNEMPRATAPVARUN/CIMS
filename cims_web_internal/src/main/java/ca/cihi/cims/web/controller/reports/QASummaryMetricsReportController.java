package ca.cihi.cims.web.controller.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.web.bean.report.ReportViewBean;
import ca.cihi.cims.web.bean.search.Languages;

@Controller
@RequestMapping(value = "/reports/qaSummaryMetrics")
public class QASummaryMetricsReportController extends ReportBaseController {
	public static final String QA_SUMMARY_METRICS_REPORT = "/reports/qaSummaryMetricsReport";

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {

		model.addAttribute("reportViewBean", reportViewBean);
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);
		model.addAttribute("languages", Languages.values());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String qaSummaryMetricsReport(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return QA_SUMMARY_METRICS_REPORT;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public ValidationResponse validate(ReportViewBean reportViewBean, final BindingResult bindResult, Model model) {

		Date dateFrom = reportViewBean.getFromDate();
		Date dateTo = reportViewBean.getToDate();
		ValidationResponse res = new ValidationResponse();
		List<String> errorMessages = new ArrayList<String>();
		if (dateTo != null && dateFrom != null && dateTo.before(dateFrom)) {
			errorMessages.add("To date is before from date, please verify.");
		}
		if (errorMessages != null && errorMessages.size() > 0) {
			res.setStatus("FAILED");
			res.setErrors(errorMessages);
		} else {

			res.setToken(tokenService.generate());
			res.setStatus("SUCCESS");
		}
		return res;
	}
}
