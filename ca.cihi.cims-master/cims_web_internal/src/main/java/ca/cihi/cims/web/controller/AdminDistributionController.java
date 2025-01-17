package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.validator.AdminValidator;
import ca.cihi.cims.web.bean.DistributionListViewBean;
import ca.cihi.cims.web.bean.KeyValueBean;

@Controller
@RequestMapping("/admin/distribution")
@SessionAttributes( { WebConstants.DISTRIBUTE_LIST_VIEW_BEAN })
public class AdminDistributionController {

	private static final Log LOGGER = LogFactory.getLog(AdminDistributionController.class);
	protected static final String LIST_VIEW = "/admin/listDistributions";
	protected static final String EDIT_VIEW = "/admin/editDistribution";

	private static final String ADD_DISTRIBUTION = "A";
	private static final String UPDATE_DISTRIBUTION = "E";

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminValidator adminValidator;
	
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
	
	// --------------------------------------------------------------------------------------

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(final Model model,
			@ModelAttribute(WebConstants.DISTRIBUTE_LIST_VIEW_BEAN) final DistributionListViewBean viewBean,
			final BindingResult result,
			@RequestParam(value = "distributionId", required = false) final String distributionId) {

		viewBean.reset();
		viewBean.setDistribution_id(Long.toString(adminService.getMaxDistributionId() + 1));
		viewBean.setActionType(ADD_DISTRIBUTION);
		return EDIT_VIEW;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(final Model model,
			@ModelAttribute(WebConstants.DISTRIBUTE_LIST_VIEW_BEAN) final DistributionListViewBean viewBean,
			final BindingResult result, @RequestParam(value = "distributionId") final String distributionId) {
		viewBean.setActionType(UPDATE_DISTRIBUTION);
		Distribution distribution = adminService.getDistributionById(Long.valueOf(distributionId));
		viewBean.fromDistribution(distribution);
		return EDIT_VIEW;

	}

	@SuppressWarnings("unchecked")
	private boolean hasRole(String role) {
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		boolean hasRole = false;
		for (GrantedAuthority authority : authorities) {
			hasRole = authority.getAuthority().equals(role);
			if (hasRole) {
				break;
			}
		}
		return hasRole;
	}

	/**
	 * Remove role based DL's depending on the users Role.
	 * 
	 * There is no key or indicator on which DL's are role based. Hard coding....... Role based DL's are ID 1,2,3,4,5,6
	 * 
	 * @param distList
	 * @return
	 */
	private List<Distribution> limitRoleBasedDLtoITAdministrator(List<Distribution> distList) {
		List<Distribution> distributionList = new ArrayList<Distribution>();
		IntRange intRange = new IntRange(1, 6);
		if (hasRole(SecurityRole.ROLE_IT_ADMINISTRATOR.getRole())) {
			distributionList = distList;
		} else {
			for (Distribution dist : distList) {
				int distID = dist.getDistributionlistid().intValue();
				if (intRange.containsInteger(distID)) {
					// Distribution ID is within role based DL range. Do not add to returned list
					LOGGER.debug("Skipping distribution ID [" + distID + "]");
				} else {
					distributionList.add(dist);
				}
			}
		}
		return distributionList;
	}

	private String listDistribution(HttpServletRequest request, ModelMap model) {
		List<Distribution> distributionList = adminService.getDistributionList();
		distributionList = limitRoleBasedDLtoITAdministrator(distributionList);
		Collections.sort(distributionList);

		model.addAttribute(WebConstants.DISTRIBUTE_LIST_VIEW_BEAN, new DistributionListViewBean());
		model.addAttribute("distributionBean", distributionList);

		return LIST_VIEW;
	}

	@ModelAttribute("reviewGroup")
	public Collection<KeyValueBean> populateReviewGroup() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		// TODO
		keyValues.add(new KeyValueBean("Y", "Yes"));
		keyValues.add(new KeyValueBean("N", "No"));
		return keyValues;
	}

	@ModelAttribute("status")
	public Collection<KeyValueBean> populateStatus() {
		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		keyValues.add(new KeyValueBean(Status.ACTIVE.getCode(), "Active"));
		keyValues.add(new KeyValueBean(Status.DISABLED.getCode(), "Disabled"));

		return keyValues;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String processAdd(HttpServletRequest request, final ModelMap model,
			@ModelAttribute(WebConstants.DISTRIBUTE_LIST_VIEW_BEAN) @Valid DistributionListViewBean viewBean,
			final BindingResult result, RedirectAttributes redirectAttributes) {
		viewBean.setDistribution_id(Long.toString(adminService.getMaxDistributionId() + 1));
		adminValidator.validate(viewBean, result, true);
		if (result.hasErrors()) {
			return EDIT_VIEW;
		}

		adminService.createDistribution(viewBean.toDistribution());

		model.clear();
		List<String> messageToDisplay = new ArrayList<String>();
		messageToDisplay.add(WebConstants.DISTRIBUTION_SAVE_SUCCESS);

		redirectAttributes.addFlashAttribute("messageToDisplay", messageToDisplay);
		return "redirect:/admin/distribution.htm";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String processEdit(HttpServletRequest request, final ModelMap model,
			@ModelAttribute(WebConstants.DISTRIBUTE_LIST_VIEW_BEAN) @Valid DistributionListViewBean viewBean,
			final BindingResult result, RedirectAttributes redirectAttributes) {
		// adminValidator.validate(viewBean, result, false);
		if (result.hasErrors()) {
			return EDIT_VIEW;
		}
		Distribution oldDistribution = adminService.getDistributionByCode(viewBean.getCode());
		String oldStatus = oldDistribution.getStatus();
		String newStatus = viewBean.getStatus();
		Distribution distribution = viewBean.toDistribution();
		if (!oldStatus.equalsIgnoreCase(newStatus) && newStatus.equalsIgnoreCase("D")) {
			if (adminService.isDistributionInUse(distribution.getDistributionlistid())) {
				result.rejectValue("status", WebConstants.ALREADY_IN_USE);
				return EDIT_VIEW;
			}
		}
		List<String> messageToDisplay = new ArrayList<String>();
		if (!oldStatus.equalsIgnoreCase(newStatus) && oldStatus.equalsIgnoreCase("D")) {
			messageToDisplay.add(WebConstants.DISTRIBUTION_SAVE_STATUS_SUCCESS);
		}
		adminService.updateDistribution(distribution);
		messageToDisplay.add(WebConstants.DISTRIBUTION_SAVE_SUCCESS);
		model.clear();
		redirectAttributes.addFlashAttribute("messageToDisplay", messageToDisplay);
		return "redirect:/admin/distribution.htm";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, ModelMap model) {
		return listDistribution(request, model);
	}

}