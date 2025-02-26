package ca.cihi.cims.web.rule.refset;

import javax.servlet.http.HttpServletRequest;

import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.bean.refset.RefsetLightBean;

/**
 *
 * @author lzhu
 *
 */
public class RefsetEditRule implements PermissionRule {

	private final static String ATTR_REFSET_SAVE_PERMISSION = "refsetSavePermission";
	private final static String ATTR_REFSET_ASSIGN_PERMISSION = "refsetAssignPermission";
	private final static String ATTR_REFSET_DROP_PERMISSION = "refsetDropPermission";
	private final static String ATTR_REFSET_CLOSE_PERMISSION = "refsetClosePermission";
	private final static String ATTR_REFSET_CREATE_PERMISSION = "refsetCreatePermission";

	@Override
	public void applyRule(HttpServletRequest request, RefsetLightBean refsetLightBean) {
		RefsetConfigDetailBean viewBean = (RefsetConfigDetailBean) refsetLightBean;
		if (!viewBean.getReadOnly() && (viewBean.getRefsetDeveloperRole() || viewBean.getAdminRole())) {
			request.setAttribute(ATTR_REFSET_SAVE_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
			request.setAttribute(ATTR_REFSET_DROP_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
		} else {
			request.setAttribute(ATTR_REFSET_SAVE_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
			request.setAttribute(ATTR_REFSET_DROP_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
		}

		if ((!viewBean.getReadOnly() && viewBean.getRefsetDeveloperRole()) || viewBean.getAdminRole()
				|| (viewBean.getCheckAssignee() && (viewBean.getRefsetDeveloperRole() || viewBean.getAdminRole())
						&& (viewBean.getVersionStatus().equals(ContextStatus.CLOSED.getStatus())))) {
			request.setAttribute(ATTR_REFSET_ASSIGN_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
		} else {
			request.setAttribute(ATTR_REFSET_ASSIGN_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
		}

		if (!viewBean.getReadOnly() && (viewBean.getRefsetDeveloperRole() || viewBean.getAdminRole())) {
			request.setAttribute(ATTR_REFSET_CLOSE_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
		} else {
			request.setAttribute(ATTR_REFSET_CLOSE_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
		}

		if (viewBean.getCheckAssignee() && (viewBean.getRefsetDeveloperRole() || viewBean.getAdminRole())
				&& (viewBean.getVersionStatus().equals(ContextStatus.CLOSED.getStatus()))
				&& (!viewBean.isOpenVersionExists() && viewBean.isLatestClosedVersion())) {
			request.setAttribute(ATTR_REFSET_CREATE_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
		} else {
			request.setAttribute(ATTR_REFSET_CREATE_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
		}
	}

}
