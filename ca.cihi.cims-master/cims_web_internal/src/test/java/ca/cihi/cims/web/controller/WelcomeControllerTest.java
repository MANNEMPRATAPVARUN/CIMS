package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl.Essence;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.web.bean.UserViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class WelcomeControllerTest {

	protected WelcomeController welcomeController;
	@Mock
	protected Model model;
	@Mock
	protected HttpSession session;
	@Mock
	AdminService adminService;
	@Mock
	Authentication authentication;

	@Mock
	protected HttpServletRequest request;

	@Mock
	HttpServletResponse response;

	@Before
	public void initializeMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		welcomeController = new WelcomeController();
		welcomeController.setAdminService(adminService);

		// Mockito.whens() for your authorization object
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(mockLdapUserDetails());
		SecurityContextHolder.setContext(securityContext);
		when(adminService.getUserByUserName(nullable(String.class))).thenReturn(mockUser());
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
		when(request.getAttribute("javax.servlet.error.exception")).thenReturn("HttpSessionRequiredException");
	}

	private LdapUserDetails mockLdapUserDetails() throws Exception {
		Essence essen = new org.springframework.security.ldap.userdetails.LdapUserDetailsImpl.Essence();
		essen.setUsername("rliang");
		essen.setDn("CIHI");
		LdapUserDetails user = essen.createUserDetails();
		user.getUsername();
		return user;
	}

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	private UserViewBean mockUserViewBean() {
		UserViewBean userViewBean = new UserViewBean();
		userViewBean.setActionType("change");
		userViewBean.setEmail("tester@cihi.ca");
		userViewBean.setFirstname("tester");
		userViewBean.setUser_id("1");
		return userViewBean;
	}

	@Test
	public void testAccessDenied() {
		String rtnView = welcomeController.accessDenied();
		String expectedView = WelcomeController.ACCESS_DENIED;
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testChange() {
		Model test_model = model;
		HttpSession test_session = session;
		UserViewBean viewBean = mockUserViewBean();
		String language = "ENG";
		welcomeController.change(test_model, test_session, viewBean, language);
		verify(adminService, times(1)).updateUser(nullable(User.class));
	}

	@Test
	public void testIndex() {

		String index = welcomeController.index(model, session);
		assertSame(index, "index");
	}

	@Test
	public void testInitMyPreferences() {
		Model test_model = model;
		HttpSession test_session = session;
		String rtnView = welcomeController.initMyPreferences(test_model, test_session);
		String expectedView = WelcomeController.MY_PREFERENCES_VIEW;
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testSessionTimeOut() {
		Model test_model = model;
		String rtnView = welcomeController.sessionTimeOut(test_model);
		String expectedView = WelcomeController.SESSION_TIME_OUT;
		assertEquals("Should get same view", rtnView, expectedView);
	}

	@Test
	public void testViewError() {
		String rtnView = welcomeController.viewError(request, response);
		assertEquals("Should get session Expired", rtnView, "sessionExpired");
	}
}
