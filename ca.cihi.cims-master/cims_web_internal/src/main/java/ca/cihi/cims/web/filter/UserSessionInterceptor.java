package ca.cihi.cims.web.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.service.AdminService;

@Component
public class UserSessionInterceptor extends HandlerInterceptorAdapter {

	private static final String USER_NOT_SETUP = "userNotSetup.html";

	private static final String USER_ROLES_ERROR = "userRolesError.jsp";

	@Autowired
	private AdminService adminService;

	private static final Logger LOGGER = LogManager.getLogger(UserSessionInterceptor.class);

	// --------------------------------------------------------------------

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (null == session.getAttribute(WebConstants.CURRENT_USER)) {
			LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			String userName = user.getUsername();
			ca.cihi.cims.model.User currentUser = adminService.getUserByUserName(userName);
			if (currentUser == null) {
				response.sendRedirect(USER_NOT_SETUP);
				LOGGER.error("Invalid Login. Username: <" + userName + ">");
				return false;
			} else {
				Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) user.getAuthorities();
				Set<SecurityRole> roles = new HashSet<SecurityRole>(grantedAuthorities.size());
				for (GrantedAuthority grantedAuthority : grantedAuthorities) {
					String authority = grantedAuthority.getAuthority();
					roles.add(SecurityRole.fromString(authority));
				}
				currentUser.setRoles(roles);
			}
			session.setAttribute(WebConstants.CURRENT_USER, currentUser);
		}

		// check the Role assignment rules
		ca.cihi.cims.model.User currentUser = (ca.cihi.cims.model.User) session.getAttribute(WebConstants.CURRENT_USER);
		Set<SecurityRole> currentUserRoles = currentUser.getRoles();
		if ((currentUserRoles.size() > 1) && (// The Initiator role must not be combined with any other roles except
												// Reviewer role and Refset Developer role
		(currentUserRoles.contains(SecurityRole.ROLE_INITIATOR) && ((currentUserRoles.size() > 3)
				|| ((currentUserRoles.size() == 3) && !currentUserRoles.contains(SecurityRole.ROLE_REVIEWER)
						&& !currentUserRoles.contains(SecurityRole.ROLE_REFSET_DEVELOPER))))
				|| (// The Administrator role must not be combined with any other roles
				currentUserRoles.contains(SecurityRole.ROLE_ADMINISTRATOR)
						&& !currentUserRoles.contains(SecurityRole.ROLE_IT_ADMINISTRATOR))
				|| (// The English Content Developer and French Content Developer roles are mutually exclusive
				currentUserRoles.contains(SecurityRole.ROLE_ENG_CONTENT_DEVELOPER)
						&& currentUserRoles.contains(SecurityRole.ROLE_FRA_CONTENT_DEVELOPER)))) {
			response.sendRedirect(USER_ROLES_ERROR);
			return false;
		}
		return true;
	}
}
