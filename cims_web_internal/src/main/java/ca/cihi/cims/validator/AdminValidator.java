package ca.cihi.cims.validator;

import static ca.cihi.cims.WebConstants.ALREADY_EXISTS;
import static ca.cihi.cims.WebConstants.ALREADY_IN_USE;
import static ca.cihi.cims.WebConstants.DISTRIBUTION_ALREADY_EXISTS;

import org.springframework.validation.Errors;

import ca.cihi.cims.model.Status;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.web.bean.DistributionListViewBean;
import ca.cihi.cims.web.bean.UserViewBean;

public class AdminValidator {

	public static final String USER_NAME = "username";
	public static final String DISTRIBUTION_LIST_CODE = "code";

	private AdminService adminService;

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	public void validate(final DistributionListViewBean viewBean, final Errors errors, final boolean isNew) {
		if (!errors.hasErrors() && isNew) {
			if (adminService.getDistributionByCode(viewBean.getCode()) != null) {
				errors.rejectValue(DISTRIBUTION_LIST_CODE, DISTRIBUTION_ALREADY_EXISTS);
			}
		}
	}

	public void validate(final UserViewBean viewBean, final Errors errors, final boolean isNew) {

		if (!errors.hasErrors() && isNew) {
			if (adminService.getUserByUserName(viewBean.getUsername().toLowerCase()) != null) {
				errors.rejectValue(USER_NAME, ALREADY_EXISTS);
			}
		}

		if (viewBean.getStatus().equalsIgnoreCase(Status.DISABLED.getCode())) {
			if (adminService.getDistributionListUserCountByUserId(Long.valueOf(viewBean.getUser_id())) != 0) {
				errors.rejectValue(USER_NAME, ALREADY_IN_USE);
			}
		}
	}

}
