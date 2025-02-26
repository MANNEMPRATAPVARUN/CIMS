package ca.cihi.cims.web.controller.refset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.validation.BindingResult;

import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.RefsetStatusViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class RefsetStatusControllerTest {
	private RefsetStatusController rsController;

	@Mock
	private RefsetService mockRefsetService;
	@Mock
	private RefsetVersion mockRefsetVersion;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpSession session;
	@Mock
	private ModelMap model;
	@Mock
	private RefsetStatusViewBean refsetStastusViewBean;
	@Mock
	private BindingResult result;

	List<RefsetVersion> refsetVersionList = new ArrayList<>();

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		rsController = new RefsetStatusController();

		rsController.setRefsetService(mockRefsetService);
		refsetVersionList.add(mockRefsetVersion);

		when(mockRefsetVersion.getRefsetStatus()).thenReturn(RefsetStatus.ACTIVE);
		when(mockRefsetService.getAllRefsets(null)).thenReturn(refsetVersionList);

	}

	@Test
	public void testSetupForm() {
		assertEquals("/refset/refsetStatus", rsController.setupForm(request, session, model));
	}

	@Test
	public void testUpdateStatusFilter() {
		assertEquals("/refset/refsetStatus", rsController.updateStatusFilter(refsetStastusViewBean, result));
	}

}
