package ca.cihi.cims.web.controller.refset;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl.Essence;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.validator.refset.RefsetValidator;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class RefsetConfigDetailControllerTest {

	private RefsetConfigDetailController controller;
	
	private MockMvc mockMvc;
	@Mock
	private RefsetService refsetService;
	
	private RefsetValidator refsetValidator;

	@Mock
	private Model model;
	
	private RefsetConfigDetailBean viewBean;
	
	@Mock
	private BindingResult result;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		refsetValidator = new RefsetValidator();
		controller = new RefsetConfigDetailController();
		controller.setRefsetService(refsetService);
		controller.setRefsetValidator(refsetValidator);		
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
		initData();
		Authentication authentication = new UsernamePasswordAuthenticationToken(mockLdapUserDetails(), null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	private LdapUserDetails mockLdapUserDetails() throws Exception {
		Essence essen = new org.springframework.security.ldap.userdetails.LdapUserDetailsImpl.Essence();
		essen.setUsername("lzhu");
		essen.setDn("CIHI");
		LdapUserDetails user = essen.createUserDetails();
		user.getUsername();
		return user;
	}

	@Test
	public void testSetupForm() throws Exception {
		mockMvc.perform(get("/refset/refsetConfigDetail.htm", new Object[] {}))
				.andExpect(view().name(RefsetConfigDetailController.REFSET_CONFIG_VIEW));
		assertEquals("refsetConfigDetail", controller.setupForm().getViewName());
	}

	@Test
	public void testCreateRefset() throws Exception {
		assertEquals("SUCCESS", controller.createRefset(model, viewBean, result).getStatus());
	}

	private void initData(){
		viewBean = new RefsetConfigDetailBean();
		viewBean.setAdminRole(false);
		viewBean.setAssignee("tyang");
		viewBean.setCategoryId(new Long(1));
		viewBean.setCategoryName("test");
		viewBean.setCCIYear("2017");
		viewBean.setContextId(5945427L);
		viewBean.setDefinition("this is for testing only");
		viewBean.setEffectiveYearFrom(2016);
		viewBean.setEffectiveYearTo(2018);
		viewBean.setElementId(2767598L);
		viewBean.setElementVersionId(5945428L);
		viewBean.setICD10CAYear("2018");
		viewBean.setNewAssignee("lzhu");
		viewBean.setNotes("will do it later");
		viewBean.setReadOnly(false);
		viewBean.setRefsetCode("first test");
		viewBean.setRefsetNameENG("refset1eng");
		viewBean.setRefsetNameFRE("refset1fra");
		viewBean.setRefsetVersionName("1.0");
		viewBean.setSCTVersionCode("IE20160331");
		viewBean.setSCTVersionDesc("International Edition 20160331");
		viewBean.setStatus("OPEN");
		viewBean.setVersionCode("dwwwewewew1.0");
		viewBean.setVersionName("versionstatus");
		viewBean.setVersionStatus("versionStatus");
		viewBean.setICD10CAContextInfo("1_2018");
		viewBean.setCCIContextInfo("2_2017");
		
	}
}
