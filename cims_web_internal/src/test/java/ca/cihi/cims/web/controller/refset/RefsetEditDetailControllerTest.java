package ca.cihi.cims.web.controller.refset;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.HttpServletRequest;

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

import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.model.refset.ActionType;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.validator.refset.RefsetValidator;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.bean.refset.RefsetLightBean;
import ca.cihi.cims.web.rule.refset.RefsetEditRule;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class RefsetEditDetailControllerTest {

	private RefsetEditDetailController controller;

	private MockMvc mockMvc;
	@Mock
	private RefsetService refsetService;

	private RefsetValidator refsetValidator;

	private RefsetEditRule refsetEditRule;

	@Mock
	private Model model;

	private RefsetConfigDetailBean viewBean;

	private RefsetConfigDetailBean viewBean2;

	@Mock
	private BindingResult result;

	@Mock
	private HttpServletRequest request;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		refsetValidator = new RefsetValidator();
		controller = new RefsetEditDetailController();
		controller.setRefsetService(refsetService);
		controller.setRefsetValidator(refsetValidator);
		refsetEditRule = new RefsetEditRule();
		controller.setRefsetEditRule(refsetEditRule);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(mockLdapUserDetails(), null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		initData();
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
		mockMvc.perform(get("/refset/refsetEditDetail.htm?contextId=5945427&elementId=2767598&elementVersionId=5945428",
				new Object[] {})).andExpect(view().name(RefsetEditDetailController.REFSET_EDIT_VIEW));
		assertEquals("refsetEditDetail", controller.setupForm(1L, 1L, 1L, request).getViewName());
	}

	@Test
	public void doRefsetSave1() {
		when(refsetService.isAssigneeRevoked((RefsetLightBean) anyObject(), (String) anyObject()))
				.thenReturn(Boolean.FALSE);
		when(result.hasErrors()).thenReturn(Boolean.FALSE);
		viewBean.setActionType(ActionType.valueOf("SAVE"));
		assertEquals("SUCCESS", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetSave2() {
		when(refsetService.isAssigneeRevoked((RefsetLightBean) anyObject(), (String) anyObject()))
				.thenReturn(Boolean.TRUE);
		viewBean.setActionType(ActionType.valueOf("SAVE"));
		assertEquals("FAILED", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetSave3() {
		when(result.hasErrors()).thenReturn(Boolean.TRUE);
		viewBean2.setActionType(ActionType.valueOf("SAVE"));
		assertEquals("FAILED", controller.doRefset(model, viewBean2, result).getStatus());
	}

	@Test
	public void doRefsetSave4() throws DuplicateCodeNameException {
		when(result.hasErrors()).thenReturn(Boolean.FALSE).thenReturn(Boolean.TRUE);
		viewBean2.setActionType(ActionType.valueOf("SAVE"));
		doThrow(new DuplicateCodeNameException("refset name is duplicated")).when(refsetService)
				.updateRefset(viewBean2);
		assertEquals("FAILED", controller.doRefset(model, viewBean2, result).getStatus());
	}

	@Test
	public void doRefsetSave5() throws PropertyKeyNotFoundException, DuplicateCodeNameException {
		when(result.hasErrors()).thenReturn(Boolean.FALSE).thenReturn(Boolean.TRUE);
		viewBean2.setActionType(ActionType.valueOf("SAVE"));
		doThrow(new PropertyKeyNotFoundException("This is just for test")).when(refsetService).updateRefset(viewBean2);
		assertEquals("FAILED", controller.doRefset(model, viewBean2, result).getStatus());
	}

	/*
	 * @Test public void doRefsetSave6() throws RuntimeException, Exception{
	 * when(result.hasErrors()).thenReturn(Boolean.FALSE).thenReturn(Boolean.TRUE);
	 * viewBean2.setActionType(ActionType.valueOf("SAVE")); doThrow(new Exception("This is just for test"
	 * )).when(refsetService).updateRefset(viewBean2); assertEquals("FAILED", controller.doRefset(model, viewBean2,
	 * result).getStatus()); }
	 */

	@Test
	public void doRefsetAssign() {
		viewBean.setActionType(ActionType.valueOf("ASSIGN"));
		assertEquals("SUCCESS", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetDrop1() {
		when(refsetService.isAssigneeRevoked((RefsetLightBean) anyObject(), (String) anyObject()))
				.thenReturn(Boolean.FALSE);
		viewBean.setActionType(ActionType.valueOf("DROP"));
		assertEquals("SUCCESS", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetDrop2() {
		when(refsetService.isAssigneeRevoked((RefsetLightBean) anyObject(), (String) anyObject()))
				.thenReturn(Boolean.TRUE);
		viewBean.setActionType(ActionType.valueOf("DROP"));
		assertEquals("FAILED", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetClose1() {
		viewBean.setActionType(ActionType.valueOf("CLOSE"));
		assertEquals("FAILED", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetClose2() {
		viewBean.setActionType(ActionType.valueOf("CLOSE"));
		viewBean.setPickListContent(true);
		when(refsetService.picklistExists(viewBean)).thenReturn(Boolean.TRUE);
		assertEquals("SUCCESS", controller.doRefset(model, viewBean, result).getStatus());
	}

	@Test
	public void doRefsetClose3() {
		viewBean.setActionType(ActionType.valueOf("CLOSE"));
		when(refsetService.isAssigneeRevoked((RefsetLightBean) anyObject(), (String) anyObject()))
				.thenReturn(Boolean.TRUE);
		assertEquals("FAILED", controller.doRefset(model, viewBean, result).getStatus());
	}

	private void initData() {
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

		viewBean2 = new RefsetConfigDetailBean();
		viewBean2.setAdminRole(false);
		viewBean2.setAssignee("tyang");
		viewBean2.setCategoryId(null);
		viewBean2.setCategoryName("test");
		viewBean2.setCCIYear("2017");
		viewBean2.setContextId(5945427L);
		viewBean2.setDefinition("this is for testing only");
		viewBean2.setEffectiveYearFrom(2016);
		viewBean2.setEffectiveYearTo(2018);
		viewBean2.setElementId(2767598L);
		viewBean2.setElementVersionId(5945428L);
		viewBean2.setICD10CAYear("2018");
		viewBean2.setNewAssignee("lzhu");
		viewBean2.setNotes("will do it later");
		viewBean2.setReadOnly(false);
		viewBean2.setRefsetCode("first test");
		viewBean2.setRefsetNameENG("refset1eng");
		viewBean2.setRefsetNameFRE("refset1fra");
		viewBean2.setRefsetVersionName("1.0");
		viewBean2.setSCTVersionCode("IE20160331");
		viewBean2.setSCTVersionDesc("International Edition 20160331");
		viewBean2.setStatus("OPEN");
		viewBean2.setVersionCode("dwwwewewew1.0");
		viewBean2.setVersionName("versionstatus");
		viewBean2.setVersionStatus("versionStatus");

	}
}
