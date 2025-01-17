package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.service.PropertyService;
import ca.cihi.cims.service.SnomedService;
import ca.cihi.cims.web.bean.LoadingSnomedViewBean;

public class LoadingSnomedControllerTest {
	
	private LoadingSnomedController loadingSnomedController;
	
	@Mock
	private SnomedService snomedService;
	
	@Mock
	private PropertyService propertyService;
	@Mock
	private Model model;
	@Mock
	private MultipartFile conceptFile;	
	@Mock
	private MultipartFile descFile;
	@Mock
	private MultipartFile refsetLangFile;
	@Mock
	private MultipartFile relationshipFile;
	
	private LoadingSnomedViewBean viewBean;
	
	private ETLLog mockETLLog() {
		ETLLog log = new ETLLog();
		log.setMessage("This is test.");
		return log;
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		loadingSnomedController = new LoadingSnomedController();
		loadingSnomedController.setSnomedService(snomedService);
		loadingSnomedController.setPropertyService(propertyService);
		
		viewBean = new LoadingSnomedViewBean();
		initData();
	}
	
	@Test
	public void testSetupForm() throws Exception {
		ModelAndView mav = loadingSnomedController.setupForm();
		assertSame(mav.getViewName(), "/migration/snomed");
	}
	
	@Test
	public void testSnomedStatus() throws Exception {
		when(snomedService.getLatestETLLog(nullable(String.class))).thenReturn(mockETLLog());
		assertEquals("This is test.", loadingSnomedController.snomedStatus("testcode"));
	}
	
	@Test
	public void testLoadFile() throws Exception {
		when(snomedService.getLatestETLLog(nullable(String.class))).thenReturn(mockETLLog());
		when(propertyService.getSnomedFileDirectory()).thenReturn("testFileDir");
		doNothing().when(snomedService).truncateLogTable();
		doNothing().when(snomedService).insertLog(nullable(String.class), nullable(String.class));
		doNothing().when(snomedService).uploadFile(nullable(MultipartFile.class), nullable(String.class));
		doNothing().when(snomedService).loadAll(nullable(List.class), nullable(List.class), nullable(String.class), nullable(String.class), nullable(String.class));
		assertTrue(loadingSnomedController.loadFile(model, viewBean).indexOf("/snomedStatus.htm?sctVersion=")>=0);
	}
	
	@Test
	public void testHandleProcessInProgress() throws Exception {
		ModelAndView mav = loadingSnomedController.handleProcessInProgress(new Exception());
		assertSame(mav.getViewName(), "/migration/snomed");
	}

	private void initData(){
		viewBean.setConceptFile(conceptFile);
		viewBean.setDescFile(descFile);
		viewBean.setRefsetLangFile(refsetLangFile);
		viewBean.setRelationshipFile(relationshipFile);
		viewBean.setSctVersion("vcode_vdesc");
	}

}
