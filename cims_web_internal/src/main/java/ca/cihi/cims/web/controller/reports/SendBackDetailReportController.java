package ca.cihi.cims.web.controller.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.reports.ValidationResponse;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.web.bean.report.ReportViewBean;
import ca.cihi.cims.web.bean.search.Languages;

@Controller
@RequestMapping(value = "/reports/sendBackDetail")
public class SendBackDetailReportController extends ReportBaseController {

	public static final String SEND_BACK_DETAIL_REPORT = "/reports/sendBackDetailReport";

	@Autowired
	AdminService adminService;

	private void buildModelAttribute(Model model, ReportViewBean reportViewBean) {
		model.addAttribute("reportViewBean", reportViewBean);
		List<String> baseClassifications = lookupService.findBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);
		model.addAttribute("users", adminService.getUsersReport());
		model.addAttribute("statuses", ChangeRequestStatus.values());
		model.addAttribute("languages", Languages.values());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String sendBackDetailReport(Model model) {
		buildModelAttribute(model, new ReportViewBean());
		return SEND_BACK_DETAIL_REPORT;
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
