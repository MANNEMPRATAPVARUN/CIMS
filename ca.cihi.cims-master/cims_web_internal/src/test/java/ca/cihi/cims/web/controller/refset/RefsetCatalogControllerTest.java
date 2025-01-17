package ca.cihi.cims.web.controller.refset;

import static org.junit.Assert.*;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.RefsetCatalogViewBean;
import static org.mockito.Mockito.when; 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class RefsetCatalogControllerTest {
	
	private RefsetCatalogController refsetCatalogController;
	
	@Mock
	private ModelMap model;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpSession session;
	@Mock
	private RefsetService refsetService;
	@Mock
	private RefsetCatalogViewBean viewBean;
	
	@Before
	public void setup () throws Exception
	{
		MockitoAnnotations.initMocks(this); 
		refsetCatalogController = new RefsetCatalogController();
		
		when(refsetService.getCategoryList()).thenReturn(new ArrayList<AuxTableValue>());
		when(refsetService.getRefsetVersions()).thenReturn(new ArrayList<RefsetVersion>());
		when(viewBean.getRefsetCategory()).thenReturn("Test");
		
		refsetCatalogController.setRefsetService(refsetService);
		refsetCatalogController.setViewBean(viewBean);
	}
	
	@Test
	public void testSetupForm () throws Exception
	{
		assertEquals("/refset/refsetCatalog", refsetCatalogController.setUpForm(request, session, model));
	}
	
	@Test
	public void testDisplayForm () throws Exception
	{
		assertEquals("/refset/refsetCatalog", refsetCatalogController.displayForm(request, session, model, viewBean));
	}

}
