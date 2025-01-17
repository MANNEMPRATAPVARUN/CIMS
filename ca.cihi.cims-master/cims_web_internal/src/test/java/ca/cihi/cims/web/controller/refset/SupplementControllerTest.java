package ca.cihi.cims.web.controller.refset;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.service.refset.SupplementService;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class SupplementControllerTest {

	private SupplementController supplementController;

	@Mock
	private Model model;

	private Long contextId = 1l;

	private Long elementId = 2l;

	private Long elementVersionId = 3l;

	@Mock
	private Refset refset;
	@Mock
	private RefsetService refsetService;

	@Mock
	private SupplementService supplementService;

	@Mock
	protected BindingResult result;

	@Mock
	MultipartFile testFile;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		supplementController = new SupplementController();

		when(refsetService.getRefset(contextId, elementId, elementVersionId)).thenReturn(refset);
		when(refsetService.getSupplements(refset)).thenReturn(new ArrayList<Supplement>());

		supplementController.setRefsetService(refsetService);

		supplementController.setSupplementService(supplementService);
	}

	@Test
	public void testListSupplement() throws Exception {
		assertEquals("listSupplement",
				supplementController.listSupplement(model, contextId, elementId, elementVersionId));
	}

	@Test
	public void testAddSupplement() throws Exception {
		assertEquals("addSupplement",
				supplementController.addSupplement(model, contextId, elementId, elementVersionId));
	}

	// TODO:
	@Ignore
	@Test
	public void testSaveSupplement() throws Exception {
		when(result.hasErrors()).thenReturn(false);
		SupplementViewBean viewBean = new SupplementViewBean();
		viewBean.setActionType("save");
		viewBean.setFile(testFile);
		BindingResult test_result = result;

		supplementController.saveSupplement(viewBean, test_result);
		verify(refsetService, times(1)).insertSupplement(viewBean);
		verify(supplementService, times(0)).deleteSupplement(viewBean);

		SupplementViewBean viewBean1 = new SupplementViewBean();
		viewBean1.setActionType("drop");
		viewBean1.setFile(testFile);
		BindingResult test_result1 = result;

		supplementController.saveSupplement(viewBean1, test_result1);
		verify(supplementService, times(1)).deleteSupplement(viewBean1);
		verify(refsetService, times(0)).insertSupplement(viewBean1);
	}

	@Test
	public void testSaveSupplement1() throws Exception {
		when(result.hasErrors()).thenReturn(true);
		SupplementViewBean viewBean = new SupplementViewBean();
		viewBean.setActionType("save");
		viewBean.setFile(testFile);
		BindingResult test_result = result;

		supplementController.saveSupplement(viewBean, test_result);
		verify(refsetService, times(0)).insertSupplement(viewBean);
		verify(supplementService, times(0)).deleteSupplement(viewBean);

	}

}
