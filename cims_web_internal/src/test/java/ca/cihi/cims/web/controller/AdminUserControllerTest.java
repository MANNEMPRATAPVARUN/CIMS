package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.cihi.cims.model.User;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.validator.AdminValidator;
import ca.cihi.cims.web.bean.UserViewBean;

public class AdminUserControllerTest 
{
	AdminUserController adminUserController;
	
	@Mock
	AdminService adminService;
	@Mock
	AdminValidator adminValidator;
	@Mock
	DisplayTagUtilService dtService;
	@Mock
	private ModelMap model;
	@Mock
	private HttpServletRequest request;
	@Mock
	private UserViewBean viewBean;
	@Mock
	private BindingResult result;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private User user;
	
	@Before
	public void setup ()
	{
		MockitoAnnotations.initMocks(this); 
		adminUserController = new AdminUserController();
		
		when(viewBean.getActionType()).thenReturn("E");
		when(viewBean.toUser()).thenReturn(user);
				
		when(adminService.getUsers()).thenReturn(new ArrayList<User>());
		doNothing().when(adminService).createUser((User)any());
		doNothing().when(adminService).updateUser((User)any());
		adminUserController.setAdminService(adminService);
		
		doNothing().when(adminValidator).validate((UserViewBean)any(), 
													(Errors)any(), 
													nullable(Boolean.class));
		adminUserController.setAdminValidator(adminValidator);
		
		when(dtService.addForPageLinks((HttpServletRequest)any(), nullable(String.class))).thenReturn(new HashMap<String,Object>());
		adminUserController.setDtService(dtService);
		
	}
	
	@Test
	public void testSetupForm ()
	{
		assertEquals("/admin/listUsers", adminUserController.setupForm(request, model));
	}
	
	@Test
	public void testProcessAdd ()
	{
		assertEquals("redirect:/admin/user.htm", adminUserController.processAdd(request, model, viewBean, result));
	}
	
	@Test
	public void testProcessEdit ()
	{
		assertEquals("redirect:/admin/user.htm", adminUserController.processEdit(request, model, viewBean, result));
	}
}
