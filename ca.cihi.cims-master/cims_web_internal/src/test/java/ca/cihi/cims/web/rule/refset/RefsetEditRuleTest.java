package ca.cihi.cims.web.rule.refset;

import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class RefsetEditRuleTest {

	private RefsetEditRule refsetEditRule;
	
	private MockMvc mockMvc;
	

	private RefsetConfigDetailBean viewBean;
	
	@Mock
    private HttpServletRequest request;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		refsetEditRule = new RefsetEditRule();
	}
	
	@Test
	public void testApplyRule1(){
		initData1();
		refsetEditRule.applyRule(request, viewBean);
		assertNull(request.getAttribute("any"));
	}
	
	private void initData1(){
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
	}
	
	private void initData2(){
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
	}
	
}