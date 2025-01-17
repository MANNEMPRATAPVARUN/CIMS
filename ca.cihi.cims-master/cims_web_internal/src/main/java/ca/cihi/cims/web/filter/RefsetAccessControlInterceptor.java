package ca.cihi.cims.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

public class RefsetAccessControlInterceptor extends HandlerInterceptorAdapter {
    /**
     * Reference to refset service.
     */
    @Autowired
    private RefsetService refsetService;

    /**
     * Not applicable refset access permission.
     */
    private final static String REFSET_ACCESS_PERMISSION_NA = "NA";

    /**
     * Writable refset access permission.
     */
    private final static String REFSET_ACCESS_PERMISSION_WRITE = "WRITE";

    /**
     * Read-only refset access permission.
     */
    private final static String REFSET_ACCESS_PERMISSION_READ = "READ";

    /**
     * Read-only refset access permission.
     */
    private final static String REFSET_LATEST_CLOSED = "latestClosed";

    /**
     * Request Attribute - 'refset permission'.
     */
    private final static String ATTR_REFSET_PERMISSION = "refsetPermission";

    /**
     * Request Attribute - 'refset export'.
     */
    private final static String ATTR_REFSET_EXPORT = "refsetExport";

    /**
     * Request Attribute - 'write permission for lastest closed refset version'.
     */
    private final static String ATTR_WRITE_FOR_LASTEST_CLOSED_VERSION = "writeForlastestClosedVersion";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String cId = request.getParameter("contextId");
        String eId = request.getParameter("elementId");
        String evId = request.getParameter("elementVersionId");

        if (StringUtils.isEmpty(cId) || StringUtils.isEmpty(eId) || StringUtils.isEmpty(evId)) {
            request.setAttribute(ATTR_REFSET_PERMISSION, REFSET_ACCESS_PERMISSION_NA);

            return true;
        }

        try {
            long contextId = Long.parseLong(cId);
            long elementId = Long.parseLong(eId);
            long elementVersionId = Long.parseLong(evId);

            /**
             * Retrieve Refset configuration for privilege check.
             */
            RefsetConfigDetailBean viewBean = new RefsetConfigDetailBean();
            viewBean.setContextId(contextId);
            viewBean.setElementId(elementId);
            viewBean.setElementVersionId(elementVersionId);

            refsetService.populateDataFromRefset(contextId, elementId, elementVersionId, viewBean);

            LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            refsetService.checkPermission(user, viewBean);

            boolean isAdmin = refsetService.isRoleAssigned(user, SecurityRole.ROLE_ADMINISTRATOR);
            boolean isRefsetDeveloper = refsetService.isRoleAssigned(user, SecurityRole.ROLE_REFSET_DEVELOPER);

            request.setAttribute(ATTR_REFSET_PERMISSION, !viewBean.getReadOnly() && (isAdmin || isRefsetDeveloper)
                    ? REFSET_ACCESS_PERMISSION_WRITE : REFSET_ACCESS_PERMISSION_READ);

            Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);

            request.setAttribute(REFSET_LATEST_CLOSED, refset.isLatestClosedVersion() ? "Y" : "N");
            request.setAttribute(ATTR_WRITE_FOR_LASTEST_CLOSED_VERSION,
                    viewBean.getCheckAssignee() && (isAdmin || isRefsetDeveloper) && refset.isLatestClosedVersion()
                            ? "Y" : "N");

            request.setAttribute(ATTR_REFSET_EXPORT, refset != null
                    ? RefsetStatus.ACTIVE.getStatus().equals(refset.getStatus()) || refset.isLatestClosedVersion() ? "Y"
                            : "N"
                    : "N");
        } catch (Exception e) {
            request.setAttribute(ATTR_REFSET_PERMISSION, REFSET_ACCESS_PERMISSION_NA);
            request.setAttribute(ATTR_REFSET_EXPORT, "N");
            request.setAttribute(REFSET_LATEST_CLOSED, "N");
            request.setAttribute(ATTR_WRITE_FOR_LASTEST_CLOSED_VERSION, "N");
        }

        return true;
    }
}
