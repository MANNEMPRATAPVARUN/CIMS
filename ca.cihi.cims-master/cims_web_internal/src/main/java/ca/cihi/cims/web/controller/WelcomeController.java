package ca.cihi.cims.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.web.bean.UserViewBean;

@Controller
public class WelcomeController {

	private static final Log LOGGER = LogFactory.getLog(WelcomeController.class);
	protected static final String INDEX_VIEW = "index";
	protected static final String MY_PREFERENCES_VIEW = "/profile/myPreferences";
	protected static final String ACCESS_DENIED = "accessDenied";
	// private static final String USER_NOT_SETUP= "userNotSetup";
	protected static final String SESSION_TIME_OUT = "sessionTimeOut";

	// public static final String CURRENT_USER= "currentUser";
	@Autowired
	private AdminService adminService;

	// -------------------------------------------------------------------------

	@RequestMapping(value = "accessDenied", method = RequestMethod.GET)
	public String accessDenied() {
		LOGGER.debug("accessDenied");
		return ACCESS_DENIED;
	}

	@RequestMapping("blank")
	public String blank() {
		return "blank";
	}

	@RequestMapping(value = "/profile/myPreferences", params = "language", method = RequestMethod.GET)
	public String change(final Model model, final HttpSession session,
			@ModelAttribute(WebConstants.USER_VIEW_BEAN) final UserViewBean viewBean,
			@RequestParam("language") final String language) {

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		currentUser.setLanguagepreference(language);
		currentUser.setCreatedDate(new Date());
		// change in user_profile table
		adminService.updateUser(currentUser);
		// change in session
		session.setAttribute(WebConstants.CURRENT_USER, currentUser);
		viewBean.setLanguagepreference(currentUser.getLanguagepreference());

		// seriously..this is so dumb.
		// hmmmmmmmmmmmmmmmm..why is it written like this
		viewBean.setUsername(currentUser.getUsername());
		viewBean.setDepartment(currentUser.getDepartment());
		viewBean.setEmail(currentUser.getEmail());
		viewBean.setFirstname(currentUser.getFirstname());
		viewBean.setLastname(currentUser.getLastname());
		viewBean.setStatus(currentUser.getStatus());
		viewBean.setTitle(currentUser.getTitle());

		model.addAttribute(WebConstants.USER_VIEW_BEAN, viewBean);
		return MY_PREFERENCES_VIEW;
	}

	@RequestMapping(value = "/test.htm", method = RequestMethod.GET)
	public ModelAndView getData() throws Exception {

		ModelAndView modelView = new ModelAndView("test");
		return modelView;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(final Model model, final HttpSession session) {
		LOGGER.debug("index");
		return INDEX_VIEW;
	}

	@RequestMapping(value = "/profile/myPreferences", method = RequestMethod.GET)
	public String initMyPreferences(final Model model, final HttpSession session) {
		final UserViewBean viewBean = new UserViewBean();
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		viewBean.setLanguagepreference(currentUser.getLanguagepreference());

		viewBean.setUsername(currentUser.getUsername());
		viewBean.setDepartment(currentUser.getDepartment());
		viewBean.setEmail(currentUser.getEmail());
		viewBean.setFirstname(currentUser.getFirstname());
		viewBean.setLastname(currentUser.getLastname());
		viewBean.setStatus(currentUser.getStatus());
		viewBean.setTitle(currentUser.getTitle());

		model.addAttribute(WebConstants.USER_VIEW_BEAN, viewBean);
		return MY_PREFERENCES_VIEW;
	}

	@RequestMapping(value = "sessionTimeOut", method = RequestMethod.GET)
	public String sessionTimeOut(Model model) {
		LOGGER.debug("sessionTimeOut");
		return SESSION_TIME_OUT; // errrrrrrr...this loops
		// still a bit off, fix later
		// // blah
		// model.addAttribute("sessionTimedOutMessage", "Your session has timed out. Returned to home page.");
		// return "/myHome";
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@RequestMapping("/error.htm")
	public String viewError(final HttpServletRequest request, final HttpServletResponse response) {
		if (request.getAttribute("javax.servlet.error.exception") != null) {
			String error = request.getAttribute("javax.servlet.error.exception").toString();
			LOGGER.error(error);
			if (error.contains("HttpSessionRequiredException")) {
				return "sessionExpired";
			}
		}
		return "error";
	}

}