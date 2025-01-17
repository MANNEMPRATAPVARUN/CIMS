package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserType;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.validator.AdminValidator;
import ca.cihi.cims.web.bean.KeyValueBean;
import ca.cihi.cims.web.bean.UserViewBean;

@Controller
@RequestMapping("/admin/user")
@SessionAttributes({ WebConstants.USER_VIEW_BEAN })
public class AdminUserController {

	private static final Log LOGGER = LogFactory.getLog(AdminUserController.class);
	protected static final String LIST_VIEW = "/admin/listUsers";
	protected static final String EDIT_VIEW = "/admin/editUser";

	private static final String ADD_USER = "A";
	private static final String UPDATE_USER = "E";

	protected static final String LIST_RECIPIENTS = "/admin/listRecipients";

	@Autowired
	private AdminService adminService;

	@Autowired
	private AdminValidator adminValidator;

	@Autowired
	private DisplayTagUtilService dtService;

	protected static final String MODEL_KEY_PAGE_SIZE = "pageSize";
	protected static final String MODEL_KEY_RESULT_SIZE = "resultSize";
	
	public AdminService getAdminService ()
	{
		return adminService;
	}
	
	public void setAdminService (AdminService adminService)
	{
		this.adminService = adminService;
	}

	public AdminValidator getAdminValidator ()
	{
		return adminValidator;
	}
	
	public void setAdminValidator (AdminValidator adminValidator)
	{
		this.adminValidator = adminValidator;
	}
	
	public DisplayTagUtilService getDtService ()
	{
		return dtService;
	}
	
	public void setDtService (DisplayTagUtilService dtService)
	{
		this.dtService = dtService;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(final Model model, @ModelAttribute(WebConstants.USER_VIEW_BEAN) final UserViewBean viewBean,
			final BindingResult result, @RequestParam(value = "userId", required = false) final String userId) {

		viewBean.reset();
		viewBean.setUser_id(Long.toString(adminService.getMaxUserId() + 1));
		viewBean.setActionType(ADD_USER);
		return EDIT_VIEW;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(final Model model, @ModelAttribute(WebConstants.USER_VIEW_BEAN) final UserViewBean viewBean,
			final BindingResult result, @RequestParam(value = "userId") final String userId) {

		User user = adminService.getUserById(Long.valueOf(userId));
		viewBean.setActionType(UPDATE_USER);
		viewBean.fromUser(user);
		return EDIT_VIEW;
	}

	private String listUsers(HttpServletRequest request, ModelMap model) {

		List<User> users = adminService.getUsers();
		model.addAttribute(WebConstants.USER_VIEW_BEAN, new UserViewBean());
		model.addAttribute("userBeans", users);
		model.addAttribute(MODEL_KEY_PAGE_SIZE, 10);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, users.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "users"));

		return LIST_VIEW;
	}

	@ModelAttribute("languageCodes")
	public Collection<KeyValueBean> populateLanguage() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean(Language.ENGLISH.getCode(), "English"));
		keyValues.add(new KeyValueBean(Language.FRENCH.getCode(), "French"));

		return keyValues;
	}

	@ModelAttribute("userStatus")
	public Collection<KeyValueBean> populateStatus() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean(Status.ACTIVE.getCode(), "Active"));
		keyValues.add(new KeyValueBean(Status.DISABLED.getCode(), "Disabled"));

		return keyValues;
	}

	@ModelAttribute("userTypes")
	public Collection<KeyValueBean> populateTypes() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean(UserType.INTERNAL.getCode(), "Internal"));
		keyValues.add(new KeyValueBean(UserType.EXTERNAL.getCode(), "External"));

		return keyValues;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String processAdd(HttpServletRequest request, ModelMap model,
			@ModelAttribute(WebConstants.USER_VIEW_BEAN) @Valid UserViewBean viewBean, final BindingResult result) {

		if (ADD_USER.equals(viewBean.getActionType())) {
			viewBean.setUser_id(Long.toString(adminService.getMaxUserId() + 1));
			adminValidator.validate(viewBean, result, true);
			if (result.hasErrors()) {
				return EDIT_VIEW;
			}
			adminService.createUser(viewBean.toUser());
		}

		model.clear();

		List<User> users = adminService.getUsers();
		model.addAttribute(WebConstants.USER_VIEW_BEAN, new UserViewBean());
		model.addAttribute("userBeans", users);
		model.addAttribute(MODEL_KEY_PAGE_SIZE, 10);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, users.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "users"));

		return "redirect:/admin/user.htm";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String processEdit(HttpServletRequest request, ModelMap model,
			@ModelAttribute(WebConstants.USER_VIEW_BEAN) @Valid UserViewBean viewBean, final BindingResult result) {

		if (UPDATE_USER.equals(viewBean.getActionType())) {
			adminValidator.validate(viewBean, result, false);
			if (result.hasErrors()) {
				return EDIT_VIEW;
			}

			adminService.updateUser(viewBean.toUser());
		}

		model.clear();

		List<User> users = adminService.getUsers();
		model.addAttribute(WebConstants.USER_VIEW_BEAN, new UserViewBean());
		model.addAttribute("userBeans", users);
		model.addAttribute(MODEL_KEY_PAGE_SIZE, 10);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, users.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "users"));

		return "redirect:/admin/user.htm";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, ModelMap model) {
		return listUsers(request, model);
	}

}