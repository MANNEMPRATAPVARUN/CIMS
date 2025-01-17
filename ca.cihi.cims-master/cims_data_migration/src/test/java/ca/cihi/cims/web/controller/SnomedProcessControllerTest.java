package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.service.SnomedService;

public class SnomedProcessControllerTest {
	
	private SnomedProcessController snomedProcessController;
	@Mock
	private SnomedService snomedService;
	@Mock
	private HttpServletRequest request;
	
	private ETLLog mockETLLog() {
		ETLLog log = new ETLLog();
		log.setMessage("This is test.");
		return log;
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		snomedProcessController = new SnomedProcessController();
		snomedProcessController.setSnomedService(snomedService);
	}
	
	@Test
	public void testSetupForm() throws Exception {
		ModelAndView mav = snomedProcessController.setupForm(request);
		assertSame(mav.getViewName(), "/migration/snomedStatus");
	}
	
	@Test
	public void testSnomedStatus() throws Exception {
		when(snomedService.getLatestETLLog(nullable(String.class))).thenReturn(mockETLLog());
		assertEquals("This is test.", snomedProcessController.snomedStatus("testcode"));
	}

	@Test
	public void testHandleProcessInProgress() throws Exception {
		ModelAndView mav = snomedProcessController.handleProcessInProgress(new Exception());
		assertSame(mav.getViewName(), "/migration/snomedStatus");
	}
	
}
