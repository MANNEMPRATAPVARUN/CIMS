package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.model.User;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.NotificationService;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/qualifier/add")
public class TabularQualifierAddController {

	public static final String VIEW = "/classification/tabular/addQualifier";

	@Autowired
	private ClassificationService context;
	@Autowired
	private NotificationService notificationService;

	// ------------------------------------------------------------------

	@ResponseBody
	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user, @RequestParam("comment") String comment) {
		try {
			notificationService.postNewComponentRequestNotifcation(context.getCurrentChangeRequestId(), comment, user
					.getUserId());
			return "";
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	public void setContext(ClassificationService context) {
		this.context = context;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@RequestMapping(method = GET)
	public String show() {
		return VIEW;
	}

}
