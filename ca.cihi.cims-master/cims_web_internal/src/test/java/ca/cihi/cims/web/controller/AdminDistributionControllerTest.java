package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.validator.AdminValidator;
import ca.cihi.cims.web.bean.DistributionListViewBean; 


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class AdminDistributionControllerTest 
{
	private AdminDistributionController adminDistributionController;
	
	@Mock
	private ModelMap model;
	@Mock
	private HttpServletRequest request;
	@Mock
	private AdminService adminService;
	@Mock
	private AdminValidator adminValidator;
	@Mock
	private DistributionListViewBean viewBean;
	@Mock
	private BindingResult result;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private Distribution dist;
	
	@Before
	public void setup ()
	{
		MockitoAnnotations.initMocks(this); 
		adminDistributionController = new AdminDistributionController ();
		
		when(dist.getDistributionlistid()).thenReturn(new Long(0));
		when(dist.getStatus()).thenReturn("D");
		
		when(viewBean.getCode()).thenReturn("");
		when(viewBean.getStatus()).thenReturn("D");
		when(viewBean.toDistribution()).thenReturn(dist);
		
		when(adminService.getDistributionList()).thenReturn(new ArrayList<Distribution>());
		when(adminService.isDistributionInUse(nullable(Long.class))).thenReturn(new Boolean(false));
		when(adminService.getDistributionByCode(nullable(String.class))).thenReturn(dist);
		doNothing().when(adminService).createDistribution((Distribution)any());
		doNothing().when(adminService).updateDistribution((Distribution)any());
		
		adminDistributionController.setAdminService(adminService);
		
		doNothing().when(adminValidator).validate((DistributionListViewBean)any(),
										(Errors)any(),
										nullable(Boolean.class));
		adminDistributionController.setAdminValidator(adminValidator);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(new User(), null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
	}
	
	@Test
	public void testSetupForm ()
	{
		assertEquals("/admin/listDistributions", adminDistributionController.setupForm(request, model));
	}
	
	@Test
	public void testProcessAdd ()
	{
		assertEquals("redirect:/admin/distribution.htm", adminDistributionController.processAdd(request, model, viewBean, result, redirectAttributes));
	}
	
	@Test
	public void testProcessEdit ()
	{
		assertEquals("redirect:/admin/distribution.htm", adminDistributionController.processEdit(request, model, viewBean, result, redirectAttributes));
	}

}
