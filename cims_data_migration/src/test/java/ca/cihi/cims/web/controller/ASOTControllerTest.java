package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.dao.bean.AsotETLLog;
import ca.cihi.cims.service.ASOTService;

public class ASOTControllerTest {

	private ASOTController asotController;

	@Mock
	private ASOTService asotService;

	private AsotETLLog mockAsotETLLog() {
		AsotETLLog log = new AsotETLLog();
		log.setAsotETLLog("This is test.");
		return log;
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		asotController = new ASOTController();
		asotController.setAsotService(asotService);
	}

	@Test
	public void testAsotLanding() {
		ModelAndView mav = asotController.asotLanding();
		assertSame(mav.getViewName(), ASOTController.ASOT_VIEW);
	}

	@Test
	public void testAsotStatus() {
		when(asotService.getLatestETLLog("2015")).thenReturn(mockAsotETLLog());
		assertEquals("This is test.", asotController.asotStatus("2015"));
	}

	@Test
	public void testGenerateASOT() {
		asotService.generateASOT("2015", "tyang@cihi.ca");
		verify(asotService, times(1)).generateASOT("2015", "tyang@cihi.ca");
	}

}
