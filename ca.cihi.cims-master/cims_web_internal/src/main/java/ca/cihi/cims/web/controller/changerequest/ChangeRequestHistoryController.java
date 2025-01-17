package ca.cihi.cims.web.controller.changerequest;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestHistory;
import ca.cihi.cims.service.ChangeRequestHistoryService;
import ca.cihi.cims.service.ChangeRequestService;

@Controller
public class ChangeRequestHistoryController {
	protected static final String VIEW_CHANGEREQUESTHISTORY = "changerequesthistory";

	@Autowired
	private ChangeRequestHistoryService changeRequestHistoryService;

	@Autowired
	private ChangeRequestService changeRequestService;

	@RequestMapping("/changeRequestHistory.htm")
	public String findChangeRequestHistory(final Model model, @RequestParam("changeRequestId") Long changeRequestId,
			HttpSession session) {
		ChangeRequestDTO changeRequestDTO = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		List<ChangeRequestHistory> changeRequestHistorys = changeRequestHistoryService
				.findChangeRequestHistoryByChangeRequestId(changeRequestId);
		model.addAttribute("activeTab", "changeRequestModificationHistory");
		model.addAttribute("changeRequestDTO", changeRequestDTO);
		model.addAttribute("changeRequestHistorys", changeRequestHistorys);

		return VIEW_CHANGEREQUESTHISTORY;
	}

	public void setChangeRequestHistoryService(ChangeRequestHistoryService changeRequestHistoryService) {
		this.changeRequestHistoryService = changeRequestHistoryService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}
}
