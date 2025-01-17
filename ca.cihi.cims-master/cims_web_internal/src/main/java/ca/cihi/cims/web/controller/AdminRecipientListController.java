package ca.cihi.cims.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.web.bean.RecipientListViewBean;

@Controller
@RequestMapping("/admin/recipientList")
@SessionAttributes({ WebConstants.RECIPIENT_LIST_VIEW_BEAN })
public class AdminRecipientListController {
	private static final Log LOGGER = LogFactory.getLog(AdminRecipientListController.class);
	protected static final String LIST_VIEW = "/admin/listRecipients";

	private static final String ADD = "add";
	private static final String REMOVE = "remove";

	@Autowired
	private AdminService adminService;

	@RequestMapping(params = { "distributionId", "user", "type" })
	public String manage(final Model model,
			@ModelAttribute(WebConstants.RECIPIENT_LIST_VIEW_BEAN) final RecipientListViewBean viewBean,
			final BindingResult result, @RequestParam("type") final String type,
			@RequestParam("distributionId") final String distributionId, @RequestParam("user") final String user) {

		LOGGER.debug("< update DomainValue");

		if (ADD.equals(type)) {
			adminService.addUserToRecipients(Long.valueOf(user), (Long.valueOf(distributionId)));
		} else if (REMOVE.equals(type)) {
			adminService.removeUserFromRecipients(Long.valueOf(user), (Long.valueOf(distributionId)));

		}
		List<User> recipients = adminService.getRecipientsByDistributionId(Long.valueOf(distributionId));
		List<User> users = adminService.getIdleUsersByDistributionId(Long.valueOf(distributionId));
		viewBean.setRecipients(recipients);
		viewBean.setUsers(users);
		return LIST_VIEW;
	}

	@RequestMapping(params = "distributionId", method = RequestMethod.GET)
	public String setupForm(final Model model, @RequestParam("distributionId") final String distributionId) {

		LOGGER.debug("< setupForm Recipient List");
		Distribution dist = adminService.getDistributionById(Long.valueOf(distributionId));
		final RecipientListViewBean viewBean = new RecipientListViewBean();
		model.addAttribute(WebConstants.RECIPIENT_LIST_VIEW_BEAN, viewBean);
		List<User> recipients = adminService.getRecipientsByDistributionId(Long.valueOf(distributionId));

		viewBean.setDistribution_id(distributionId);
		viewBean.setDistribution_code(dist.getCode());
		viewBean.setDistribution_name(dist.getName());
		viewBean.setDistribution_description(dist.getDescription());

		viewBean.setRecipients(recipients);
		LOGGER.debug("> setupForm Recipient List");
		List<User> users = adminService.getIdleUsersByDistributionId(Long.valueOf(distributionId));
		viewBean.setUsers(users);
		LOGGER.debug("> setupForm User List");
		return LIST_VIEW;
	}
}
